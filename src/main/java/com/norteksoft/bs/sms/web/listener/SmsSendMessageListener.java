package com.norteksoft.bs.sms.web.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.norteksoft.product.util.PropUtils;
import com.norteksoft.utilSecret.license.License;


/**
 * 短信平台监听器
 * @author lenove1
 *
 */
public class SmsSendMessageListener implements ServletContextListener {
	private Log log = LogFactory.getLog(getClass());
	/**
	 * 发送短信监听器 
	 */
	public void contextInitialized(ServletContextEvent event) {
		License.hasValidateSecurity();
		try {
			new Thread(new SmsSendMessageThread()).start();
		} catch (Exception e) {
			log.error("sms:启动线程错误:"+PropUtils.getExceptionInfo(e));
		}
	}

	public void contextDestroyed(ServletContextEvent arg0) {
		
	}

}
