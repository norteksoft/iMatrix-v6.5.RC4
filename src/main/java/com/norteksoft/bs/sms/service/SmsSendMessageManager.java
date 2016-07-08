package com.norteksoft.bs.sms.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.bs.sms.api.SmsService;
import com.norteksoft.bs.sms.base.enumeration.RequestType;
import com.norteksoft.bs.sms.base.utils.SmsUtil;
import com.norteksoft.bs.sms.dao.SmsAuthoritySettingDao;
import com.norteksoft.bs.sms.dao.SmsGatewaySettingDao;
import com.norteksoft.bs.sms.dao.SmsWaitTosendDao;
import com.norteksoft.bs.sms.entity.SmsAuthoritySetting;
import com.norteksoft.bs.sms.entity.SmsGatewaySetting;
import com.norteksoft.bs.sms.entity.SmsWaitTosend;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.PropUtils;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;


/**
 * 短信平台
 * @author c
 */
@Repository
@Transactional
public class SmsSendMessageManager {
	private Log log = LogFactory.getLog(getClass());
	private static final String MEDIA_TYPE = "text/html;charset=UTF-8";
	
	@Autowired
	private SmsAuthoritySettingDao smsAuthoritySettingDao;
	@Autowired
	private SmsLogManager smsLogManager;
	@Autowired
	private SmsGatewaySettingDao smsGatewaySettingDao;
	@Autowired
	private SmsWaitTosendDao smsWaitTosendDao;
	
	/**
	 *  定时调用该方法去发送
	 * @throws Exception
	 */
	public void send() throws Exception {
		//取出所有启用的,可以发送短信的网关
		List<SmsGatewaySetting> settings = smsGatewaySettingDao.getAllSmsGatewaySetting();
		if(settings != null && settings.size() > 0){
			SmsGatewaySetting smsGatewaySetting = settings.get(0);//选中一个网关发送所有的信息
			List<SmsWaitTosend> waitTosends = smsWaitTosendDao.getAllDatas(smsGatewaySetting);//所有的短信
			sendMessage(smsGatewaySetting, waitTosends);
		}
	}
	/**
	 * 发送
	 * @param smsGatewaySetting 网关
	 * @param waitTosends 待发送列表
	 * @throws Exception
	 */
	private void sendMessage(SmsGatewaySetting smsGatewaySetting,List<SmsWaitTosend> waitTosends) throws Exception{
		for (SmsWaitTosend smsWaitTosend : waitTosends) {
			SmsService sendMsgImpl = getServiceImpl(smsGatewaySetting);
			if(null == sendMsgImpl){
				ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("bs.messagePlatform"), 
						ApiFactory.getBussinessLogService().getI18nLogInfo("bs.cmsSendNoImplement"),ContextUtils.getSystemId("bs"));
			}else {
				String config = smsGatewaySetting.getConfiguration();
				Map<String, String> resultMap = SmsUtil.stringToMap(config);
				//发送
				String flag = sendMsgImpl.sendMessage(resultMap,smsWaitTosend.getReceiver(),smsWaitTosend.getContent());
				//发送成功
				if("success".equals(flag)){
					this.sendSuccessBack(smsWaitTosend,smsGatewaySetting);
				}else {//发送失败
					smsWaitTosend.setSendTime(smsWaitTosend.getSendTime()+1);
					smsWaitTosendDao.save(smsWaitTosend);
					ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("bs.messagePlatform"),
							ApiFactory.getBussinessLogService().getI18nLogInfo("bs.sendFail"),ContextUtils.getSystemId("bs"));
				}
			}
		}
	}
	
	
	/**
	 * 短信成功发送后
	 * @param smsWaitTosend
	 * @param smsGatewaySetting
	 * @throws Exception
	 */
	private void sendSuccessBack(SmsWaitTosend smsWaitTosend, SmsGatewaySetting smsGatewaySetting) throws Exception {
		SmsAuthoritySetting setting = 
			smsAuthoritySettingDao.getSmsAuthoritySettingByCode(smsWaitTosend.getInterCode());//获得接口
		//回调,成功返回ok
		String backResult = "";
		if(null != setting){
			backResult = this.accessCallback(setting);
		}else {
			 backResult = "noinfo";
		}
		//增加日志
		smsLogManager.createLog(smsGatewaySetting, smsWaitTosend,setting,backResult);//通用短信发送，接口编号为空
		//删除待发送
		smsWaitTosendDao.delete(smsWaitTosend);
	}
	/**
	 * 获得不同的实现类
	 * @param smsGatewaySetting
	 * @return
	 */
	private SmsService getServiceImpl(SmsGatewaySetting smsGatewaySetting){
		SmsService sendMsgImpl = null;
		if("smsCat".equals(smsGatewaySetting.getGatewayType())){
			sendMsgImpl = (SmsService)ContextUtils.getBean("sendMsgByCatImpl");
		}else if ("smsWeimi".equals(smsGatewaySetting.getGatewayType())) {
			sendMsgImpl = (SmsService)ContextUtils.getBean("sendMsgByWeimiImpl");
		}else {
			//自定义的发送
			sendMsgImpl = (SmsService)ContextUtils.getBean(smsGatewaySetting.getImplClassName());
		}
		return sendMsgImpl;
	} 
	
	/**
	 * 回调
	 * @param interCode
	 */
	private String accessCallback(SmsAuthoritySetting setting) throws Exception {
		String backUrl = setting.getBackUrl();//回调Url
		if(StringUtils.isNotEmpty(backUrl)){
			//有回调
			String result = null;
			if(setting.getRequestType().equals(RequestType.HTTP)){//http
				result = httpClientConnection(backUrl);
			}else if (setting.getRequestType().equals(RequestType.WEBSERVICE)) {//webservice
				result = webserviceConnection(backUrl);
			}else if (setting.getRequestType().equals(RequestType.RESTFUL)) {//restful
				result = restfulConnection(backUrl);
			}
			//如果访问成功，返回值为 ok
			return result;
		}else {
			return "noinfo";
		}
	}
	
	/**
	 * httpClient连接方式，访问回调
	 */
	private String httpClientConnection(String url) {
		HttpGet httpget = new HttpGet(url);
		HttpClient httpclient = new DefaultHttpClient();
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		String responseBody = "";
		try {
			responseBody = httpclient.execute(httpget, responseHandler);
		} catch (ClientProtocolException e) {
			log.error("sms:httpClient连接方式:"+PropUtils.getExceptionInfo(e));
		} catch (IOException e) {
			log.error("sms:httpClient连接方式:"+PropUtils.getExceptionInfo(e));
		}
		httpclient.getConnectionManager().shutdown();
		List<String> messages = new ArrayList<String>();//国际化
		messages.add(url);
		messages.add(responseBody);
		ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("bs.messagePlatform"), 
				ApiFactory.getBussinessLogService().getI18nLogInfo("bs.httpCallBack",messages),ContextUtils.getSystemId("bs"));
	    return responseBody;//接收html
	}
	
	/**
	 * webservice连接方式，访问回调
	 */
	private String webserviceConnection(String url) throws Exception{
		JaxWsDynamicClientFactory dynamicClient = JaxWsDynamicClientFactory.newInstance();
		int index = url.lastIndexOf("?");
		org.apache.cxf.endpoint.Client client = dynamicClient.createClient(url.substring(0,index)); 
		Object[] password = client.invoke(url.substring(index + 1));   //访问cgcsl并获得返回值
		return password[0].toString();
	}
	/**
	 * restful连接方式，访问回调
	 */
	private String restfulConnection(String url) {
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		WebResource service = client.resource(url);
		ClientResponse cr = null;
		try {
			String param = "companyId="+1L;
			cr = service.entity(param,MEDIA_TYPE).accept(MEDIA_TYPE).post(ClientResponse.class);
		} catch (UniformInterfaceException e) {
			log.error("sms:restful连接方式:"+PropUtils.getExceptionInfo(e));
		} catch (ClientHandlerException e) {
			log.error("sms:restful连接方式:"+PropUtils.getExceptionInfo(e));
		}
		List<String> messages = new ArrayList<String>();//国际化
		messages.add(url);
		messages.add(cr.getEntity(String.class));
		ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("bs.messagePlatform"), 
				ApiFactory.getBussinessLogService().getI18nLogInfo("bs.messagePlatform",messages),ContextUtils.getSystemId("bs"));
		return cr.getEntity(String.class);
	}
	
}
