package com.norteksoft.bs.sms.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.bs.sms.dao.SmsAuthoritySettingDao;
import com.norteksoft.bs.sms.dao.SmsGatewaySettingDao;
import com.norteksoft.bs.sms.entity.SmsAuthoritySetting;
import com.norteksoft.bs.sms.entity.SmsGatewaySetting;


/**
 * 短信平台/
 * @author c
 */
@Repository
@Transactional
public class SmsManager  {
	@Autowired
	private SmsGatewaySettingDao smsGatewaySettingDao;
	@Autowired
	private SmsAuthoritySettingDao smsAuthoritySettingDao;
	@Autowired
	private SmsTemplateSettingManager smsTemplateSettingManager;
	@Autowired
	private SmsWaitTosendManager smsWaitTosendManager;
	/**
	 * @param receiver 收信人
	 * @param interCode 接口编号
	 * @param args 短信内容或模版参数(根据接口中是否存在模版来判断)
	 * 
	 * 将短信增加至待发送列表， 根据接口中是否有模板编号，确定是否使用模板
	 * 		1、不使用模版，自定义内容，需要参数：(收信人，接口编号,短信内容)
	 * 		2、使用模版，需要参数：(收信人，接口编号，模版参数),
	 */
	public String sendMessage(String receiver,String interCode,String...args) {
		//取出所有启用的,可以发送短信的网关
		List<SmsGatewaySetting> settings = smsGatewaySettingDao.getAllSmsGatewaySetting();
		if(settings != null && settings.size() > 0){
			SmsAuthoritySetting setting = smsAuthoritySettingDao.getSmsAuthoritySettingByCode(interCode);
			if(setting == null ) return "接口编号不存在";
			String content = smsTemplateSettingManager.parseArgsToContent(setting,args);
			//生成待发送列表
			smsWaitTosendManager.createWaitToSendByReceiver(receiver, content,interCode);
			//判断是否需要报警
			smsWaitTosendManager.validateTowarn(settings);
			return "已添加到待发送列表";
		}else {
			return "没有可用的网关";
		}
	}

}
