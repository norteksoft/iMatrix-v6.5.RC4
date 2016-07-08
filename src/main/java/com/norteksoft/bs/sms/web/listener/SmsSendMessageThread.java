package com.norteksoft.bs.sms.web.listener;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.norteksoft.acs.entity.organization.Company;
import com.norteksoft.acs.service.AcsUtils;
import com.norteksoft.bs.sms.service.SmsSendMessageManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.ParameterUtils;
import com.norteksoft.product.util.PropUtils;
import com.norteksoft.product.util.ThreadParameters;

/**
 * 短信平台
 * @author lenove1
 *
 */
public class SmsSendMessageThread implements Runnable {
	
	private Log log = LogFactory.getLog(getClass());
	   public void run() {  
	        while (true) {// 线程未中断执行循环  
	            try {  
	            	SmsSendMessageManager smsSendMessageManager = (SmsSendMessageManager) ContextUtils.getBean("smsSendMessageManager");
	                AcsUtils acsUtils = (AcsUtils) ContextUtils.getBean("acsUtils");
	            	List<Company> companys=acsUtils.getAllCompanys();
	    			for(Company company:companys){
	    				ThreadParameters parameters=new ThreadParameters(company.getId());
	    				ParameterUtils.setParameters(parameters);
	    				com.norteksoft.product.api.entity.User systemAdmin = ApiFactory.getAcsService().getSystemAdmin();
	    				parameters=new ThreadParameters(company.getId());
	    				parameters.setUserName("系统");
	    				parameters.setLoginName(systemAdmin.getLoginName());
	    				parameters.setUserId(systemAdmin.getId());
	    				ParameterUtils.setParameters(parameters);
	    				
	    				smsSendMessageManager.send();
	    				Thread.sleep(2000); //每隔2000ms执行一次  
	    			}
	                
	            } catch (InterruptedException e) {  
	            	log.error("sms:启动线程发送短信:"+PropUtils.getExceptionInfo(e));
	            } catch (Exception e) {
	            	log.error("sms:启动线程发送短信:"+PropUtils.getExceptionInfo(e));
				}  
	              
	        }  
	    }  
}
