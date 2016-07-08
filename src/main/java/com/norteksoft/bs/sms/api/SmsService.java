package com.norteksoft.bs.sms.api;

import java.util.Map;

public interface SmsService {
	/**
	 * 
	 * @param settings 网关配置
	 * @param receiver 收信人
	 * @param content	短信内容
	 * @return
	 */
	public String sendMessage(Map<String, String> settings,String receiver,String content);
	
}
