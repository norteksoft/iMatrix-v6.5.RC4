package com.norteksoft.product.api.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.norteksoft.bs.sms.service.SmsManager;
import com.norteksoft.product.api.SmsSendService;

@Service
public class SmsSendServiceImpl implements SmsSendService{
	
	@Autowired
	private SmsManager smsManager;
	
	 /**
     * 短信平台/将短信添加至待发送列表
     * @param phoneTo 收信人
     * @param interCode 接口编号
     * @param args 短信内容或模板参数
     * @return
     */
	public String sendMessage(String phoneTo, String interCode, String... args) {
		return smsManager.sendMessage(phoneTo, interCode, args);
	}
}
