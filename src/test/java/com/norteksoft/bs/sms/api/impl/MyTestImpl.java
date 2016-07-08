package com.norteksoft.bs.sms.api.impl;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.bs.sms.api.SmsService;
import com.norteksoft.product.util.PropUtils;


/**
 * 短信平台/短信猫方式
 * @author c
 */

@Repository
@Transactional
public class MyTestImpl  implements SmsService{
	private static final String SENDSUCCESSMSG = "success";
	private static final String SENDFAILMSG = "error";
	private Log log = LogFactory.getLog(getClass());
	
	/**
	 * @param phoneTo 发送的目标手机号
	 * @param content 发送的内容
	 */
	public String sendMessage(Map<String, String> settings,String phoneTo,String content) {
		try {
			System.out.println("我要去发短信了。。。。。。");
			Thread.sleep(3000);
		} catch (Exception e) {
			log.error(PropUtils.getExceptionInfo(e));
			return SENDFAILMSG;
		}
		return SENDSUCCESSMSG;
	}
}

