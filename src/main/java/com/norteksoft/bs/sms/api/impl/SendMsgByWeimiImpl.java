package com.norteksoft.bs.sms.api.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.bs.sms.api.SmsService;
import com.norteksoft.bs.sms.base.utils.HttpClientHelper;
import com.norteksoft.bs.sms.base.utils.SmsUtil;
import com.norteksoft.bs.sms.entity.SmsGatewaySetting;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.PropUtils;


/**
 * 短信平台/短信猫方式
 * @author c
 */
@Repository
@Transactional
public class SendMsgByWeimiImpl  implements SmsService{
	private Log log = LogFactory.getLog(getClass());
	private static final String SENDSUCCESSMSG = "success";
	private static final String SENDFAILMSG = "error";
	private static final String WEIMI_SMSURL = "http://api.weimi.cc/2/sms/send.html";
	private static final String WEIMI_SEARCHURL = "http://api.weimi.cc/2/account/balance.html";
	
	
	/**
	 * 微米网方式发送短信
	 *	       短信内容。必须设置好短信签名，签名规范： <br>
	 * 		1、短信内容一定要带签名，签名放在短信内容的最前面；<br>
	 * 		2、签名格式：【***】，签名内容为三个汉字以上（包括三个）；<br>
	 * 		3、短信内容不允许双签名，即短信内容里只有一个“【】”
	 */
	public String sendMessage(Map<String, String> settings,String phoneTo,String content) {
		Map<String, String> para = new HashMap<String, String>();
		//目标手机号码，多个以","分隔，一次性调用最多100个号码，示例：139********,138********
		para.put("mob", phoneTo.replace(";" , ","));
		para.put("uid", settings.get("weimiId"));//微米账号的接口UID
		para.put("pas", settings.get("weimiPw"));//微米账号的接口密码
		para.put("type", "xml");//接口返回类型：json、xml、txt。默认值为txt
		para.put("con", "【"+settings.get("smsSign")+"】"+content);
		try {
			String result = HttpClientHelper.convertStreamToString(HttpClientHelper.post(WEIMI_SMSURL, para),"UTF-8");
			List<String> messages = new ArrayList<String>();
			ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("bs.messagePlatform"), ApiFactory.getBussinessLogService().getI18nLogInfo("bs.cmsCatSend1",messages),
					ContextUtils.getSystemId("bs"));
			return this.parseResult(result);
		} catch (Exception e) {
			log.error(PropUtils.getExceptionInfo(e));
			return SENDFAILMSG;
		}
	}


	/**
	 * 微米账号查询/
	 */
	public String searchAcount(SmsGatewaySetting gatewaySetting) {
		Map<String, String> para = new HashMap<String, String>();
		String config = gatewaySetting.getConfiguration();
		Map<String, String> resultMap = SmsUtil.stringToMap(config);
		para.put("uid", resultMap.get("weimiId"));//微米账号的接口UID
		para.put("pas", resultMap.get("weimiPw"));//微米账号的接口密码
		para.put("type", "xml");// 接口返回类型：json、xml、txt。默认值为txt
		try {
			return HttpClientHelper.convertStreamToString(
							HttpClientHelper.post(WEIMI_SEARCHURL,para), "UTF-8");
		} catch (Exception e) {
			log.debug(PropUtils.getExceptionInfo(e));
			return SENDFAILMSG;
		}
	}
	
	/**
	 * 解析访问微米网后的返回值
	 * @param result
	 * @return
	 * <?xml version="1.0" encoding="UTF-8"?><result><code>-2</code><msg>非法账号</msg></result>
	 * @throws DocumentException 
	 */
	@SuppressWarnings("unchecked")
	private String parseResult(String result) throws DocumentException {
		String resultMsg = null;
		Document document1 = DocumentHelper.parseText(result);
		List<Element> listtx = document1.selectNodes("//result" );
		if(null != listtx && listtx.size() > 0){
			Element ele = listtx.get(0);
			String code = ele.elementText("code");
			if("0".equals(code)) {
				resultMsg = SENDSUCCESSMSG;
			}else {
				resultMsg = code + "@@" + ele.elementText("msg");//0代表发送成功
			}
		}
		return resultMsg;
	}
	
	
}
