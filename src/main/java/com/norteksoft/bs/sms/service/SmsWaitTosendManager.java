package com.norteksoft.bs.sms.service;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.acs.entity.organization.User;
import com.norteksoft.acs.service.AcsUtils;
import com.norteksoft.bs.sms.base.utils.SmsUtil;
import com.norteksoft.bs.sms.dao.SmsWaitTosendDao;
import com.norteksoft.bs.sms.entity.SmsGatewaySetting;
import com.norteksoft.bs.sms.entity.SmsWaitTosend;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.PropUtils;


/**
 *  短信平台/待发送列表
 * @author c
 *
 */
@Repository
@Transactional
public class SmsWaitTosendManager {
	private Log log = LogFactory.getLog(getClass());
	@Autowired
	private SmsWaitTosendDao smsWaitTosendDao;
	@Autowired
	private AcsUtils acsUtils;
	
	public SmsWaitTosend getSmsWaitTosendById(Long id) {
		return smsWaitTosendDao.get(id);
	}

	public void getAllSmsWaitTosend(Page<SmsWaitTosend> page) {
		smsWaitTosendDao.getAllSmsWaitTosend(page);		
	}
	/**
	 * 删除
	 * @param ids
	 */
	public void delete(String ids) {
		for (String id : ids.split(",")) {
			smsWaitTosendDao.delete(Long.valueOf(id));
		}
	}
	/**
	 * 创建待发送列表,G根据用户据id以及短信内容
	 * @param receiver 收信人，格式在前台已解析，都是133********;122********;100********...
	 * @param content
	 */
	public void createWaitToSend(String ids,String receiver,String content) {
		if(StringUtils.isNotEmpty(ids)){
			String[] idArr = ids.split(",");
			for (String id : idArr) {
				User user = acsUtils.getUserById(Long.valueOf(id));
				String tel = user.getUserInfo().getTelephone() == null ?
						"":user.getUserInfo().getTelephone()  ;
				receiver = receiver.replace(user.getName(), tel);//将用户名替换为电话号
			}
		}
		createWaitToSendByReceiver(receiver, content,"");
	}
	
	/**
	 * 创建待发送列表,根据接收电话据id以及短信内容
	 * @param receiver
	 * @param content
	 */
	public void createWaitToSendByReceiver(String receiver,String content,String interCode) {
		SmsWaitTosend send = new SmsWaitTosend();
		send.setReceiver(receiver);//收信人
		send.setContent(content);//内容
		send.setSendTime(0);//发送次数
		send.setInterCode(interCode);//接口编号
		smsWaitTosendDao.save(send);
	}

	/**
	 * 判断是否需要报警
	 * @param settings
	 */
	public void validateTowarn(List<SmsGatewaySetting> settings) {
		int maxCount = settings.get(0).getMaxcountTowarn()==null?0:settings.get(0).getMaxcountTowarn();
		long count = smsWaitTosendDao.getCountAllDatas(settings.get(0));
		if(count > Long.valueOf(maxCount)){//大于报警需最大短信数
			//发邮件提醒
			try {
				SmsUtil.cgcslCancelOrderSendMail("网关编号：" + settings.get(0).getGatewayCode() +
					"网关名称：" + settings.get(0).getGatewayName() + "未发送短信数量:" + count + ",已达到最大值，请查看原因.");
			} catch (Exception e) {
				log.error("sms:邮件发送失败:"+PropUtils.getExceptionInfo(e));
			}
		}
	}
	
	
	
}
