package org.jwebap.startup;

import java.net.URL;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jwebap.util.Assert;


/**
 * Jwebap在Web应用启动类
 * 
 * @author leadyu
 * @since Jwebap 0.5
 * @date  2007-8-16
 */
public class JwebapListener implements ServletContextListener {
	
	private static final Log log = LogFactory.getLog(JwebapListener.class);
	
	public static final String CONFIG_PARAM_NAME="jwebap-config";
	public static String system_code;
	
	public void contextInitialized(ServletContextEvent contextEnvent) {
		//zzl  
		system_code = contextEnvent.getServletContext().getInitParameter("systemCode");
		ServletContext servletContext=contextEnvent.getServletContext();
		String configPath = servletContext.getInitParameter(CONFIG_PARAM_NAME);

		try {
			Assert.assertNotNull(configPath,"please make sure your application cantains context-param '"+CONFIG_PARAM_NAME+"' in web.xml .");
			String path=servletContext.getRealPath(configPath);
			if(path!=null){
				Startup.startup(path);
			}else{
				URL url = servletContext.getResource(configPath);
				Assert.assertNotNull(url,"jwebap config not found : context-param '"+CONFIG_PARAM_NAME+"'= "+configPath);
				Startup.startup(url);
			}
		} catch (Throwable e) {
			log.warn(e.getMessage());
			e.printStackTrace();
		}
		
	}

	public void contextDestroyed(ServletContextEvent arg0) {
		
	}

}
