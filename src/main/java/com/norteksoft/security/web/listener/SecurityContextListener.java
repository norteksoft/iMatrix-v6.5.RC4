package com.norteksoft.security.web.listener;


import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.norteksoft.bs.options.entity.TimedTask;
import com.norteksoft.bs.options.entity.Timer;
import com.norteksoft.bs.options.enumeration.ApplyType;
import com.norteksoft.bs.options.enumeration.TimingType;
import com.norteksoft.product.enumeration.DataState;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.Scheduler;
import com.norteksoft.product.util.WebContextUtils;
import com.norteksoft.security.service.CheckLicenseManager;

/**
 * 通过读取web.xml中的系统编号(systemCode)参数，
 * 预先加载该系统的所有资源信息，供权限系统使用。
 * 
 * @author xiaoj
 */
@SuppressWarnings("deprecation")
public class SecurityContextListener implements ServletContextListener{
	private String systemCode;
	
	public void contextDestroyed(ServletContextEvent event) { }

	public void contextInitialized(ServletContextEvent event) {
		ServletContext context = event.getServletContext();
		systemCode = context.getInitParameter("systemCode");
		checkLicense(context);
	}


	private Object getBeanFromApplicationContext(ServletContext servletContext, String beanName) {
    	ApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
    	WebContextUtils.setContext(applicationContext);
    	ContextUtils.setContext(applicationContext);
    	Object object = applicationContext.getBean(beanName);
    	if(object == null){
    		StringBuilder builder = new StringBuilder();
    		builder.append("无法实例化Bean[").append(beanName).append("],系统启动失败");
    		throw new RuntimeException(builder.toString());
    	}
    	return object;
    }

    private void checkLicense(ServletContext context) {
    	Object obj = getBeanFromApplicationContext(context, "checkLicenseManager");
		((CheckLicenseManager)obj).checkLicense();
		if(StringUtils.equals(systemCode, "imatrix")){
			//放到定时中
			TimedTask jobInfo = new TimedTask();
			jobInfo.setDataState(DataState.ENABLE);
			jobInfo.setUrl("/rest/license/checkLicense");
			jobInfo.setSystemCode("acs");
			jobInfo.setApplyType(ApplyType.RESTFUL_APPLY);
			
			Timer timer = new Timer();
			timer.setTimingType(TimingType.everyDate);
			timer.setCorn("00:00");
			timer.setJobInfo(jobInfo);
			timer.setId(0L);
			Scheduler.addJob(timer);
		}
	}
}
