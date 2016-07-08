package com.norteksoft.bs.sms.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.bs.sms.dao.SmsLogDao;
import com.norteksoft.bs.sms.entity.SmsAuthoritySetting;
import com.norteksoft.bs.sms.entity.SmsGatewaySetting;
import com.norteksoft.bs.sms.entity.SmsLog;
import com.norteksoft.bs.sms.entity.SmsWaitTosend;
import com.norteksoft.product.orm.Page;


/**
 * 短信平台
 * @author lenove1
 *
 */
@Repository
@Transactional
public class SmsLogManager {

	@Autowired
	private SmsLogDao smsLogDao;

	public SmsLog getSmsLogById(Long id) {
		return smsLogDao.get(id);
	}
	/**
	 * 短信平台/短信日志/列表
	 * @return
	 */
	public void getAllSmsLog(Page<SmsLog> page, String logType) {
		smsLogDao.getAllSmsLog(page,logType);
	}
	/**
	 * 创建发送日志
	 * @param smsGatewaySetting 
	 * @param setting 
	 * @param phoneTo
	 * @param content
	 */
	public void createLog(SmsGatewaySetting smsGatewaySetting,SmsWaitTosend smsWaitTosend, 
								SmsAuthoritySetting setting,String backResult) {
		SmsLog log = new SmsLog();
		log.setSenderOrReceiver(smsWaitTosend.getReceiver());
		log.setSendOrReceiveDate(new Date());
		log.setContent(smsWaitTosend.getContent());
		if(null != setting){
			log.setBackUrl(setting.getBackUrl());//回调url
		}
		log.setSendTime(log.getSendTime()+1);
		log.setLogType("send");
		if(null != backResult && backResult.equals("ok")){
			log.setBackSuccess("成功");
		}else if(backResult.equals("noinfo")){
			log.setBackSuccess("无");
			log.setBackUrl("无");//回调url
		}else {
			log.setBackSuccess("失败");
		}
		log.setGatewayCode(smsGatewaySetting.getGatewayCode());//网关编号
		smsLogDao.save(log);
	}
}
