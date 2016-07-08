package com.norteksoft.acs.web.syssetting;


import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Required;

import com.norteksoft.acs.base.enumeration.LoginFailSetType;
import com.norteksoft.acs.base.enumeration.SystemDefaultLanguage;
import com.norteksoft.acs.base.web.struts2.CRUDActionSupport;
import com.norteksoft.acs.entity.sysSetting.SecuritySetting;
import com.norteksoft.acs.service.syssetting.SecuritySetManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.util.ContextUtils;
/**
 * 
 * @author chenchenhu
 *
 */
@Namespace("/syssetting")
@ParentPackage("default")
@Results( { @Result(name = CRUDActionSupport.RELOAD, location = "/syssetting/security-set.action", type = "redirect") })
public class SecuritySetAction extends CRUDActionSupport<SecuritySetting> {

	private static final long serialVersionUID = 4622265559442003480L;

	private SecuritySetManager securitySetManager;

	public String name;

	private SecuritySetting entity;

	private Long id;
	
	private String passWordLenth;
	
	private String [] prems;
	
	private String passRule;
	
	private String mse;
	private String failType="VALIDATE_CODE";
	private Integer lockTime;
	
	private String logRemainTime;//审计信息保留时间
	private String logRemainTimeRemark;//审计信息保留时间备注
	private String resetPassword;//用户第一次登录系统是否重新设置密码：yes重新设置密码,no不重新设置密码,默认不用重新设置密码
	private SystemDefaultLanguage systemDefaultLanguage;//系统默认语言

	@Override
	public String delete() throws Exception {

		return null;
	}
	
	@Action("list")
	public String toList() throws Exception{
		return SUCCESS;
	}

	@Override
	@Action("security-set")
	public String list() throws Exception {
		List<SecuritySetting>  list = securitySetManager.getSecuritySetList();
		logRemainTimeRemark="";
		prems = new String[11];
		prems[0]="3";//登陆次数默认设为3次
		for (SecuritySetting obj : list) {
			if(obj.getName().equals("login-security")){
				prems[0]=obj.getValue();
				prems[2]=obj.getRemarks();
				failType=obj.getFailSetType().toString();
				lockTime=obj.getLockedTime();
			}
			if(obj.getName().equals("loginTimeouts")){
				prems[3]=obj.getValue();
				prems[4]=obj.getRemarks();
			}
			if(obj.getName().equals("password-over-notice")){
				prems[5]=obj.getValue();
				prems[6]=obj.getRemarks();
			}
			if(obj.getName().equals("admin-password-overdue")){
				prems[7]=obj.getValue();
				prems[8]=obj.getRemarks();
			}
			if(obj.getName().equals("user-password-overdue")){
				prems[9]=obj.getValue();
				prems[10]=obj.getRemarks();
			}
			if(obj.getName().equals("password-complexity")){
				passRule=obj.getValue();
				passWordLenth = securitySetManager.getPassWordLength(obj);
			}
			if(obj.getName().equals("log-set")){
				logRemainTime=obj.getValue();
				logRemainTimeRemark=obj.getRemarks();
			}
			if(obj.getName().equals("reset-password")){
				resetPassword=obj.getValue();
			}
			if(obj.getName().equals("system-default-language")){
				systemDefaultLanguage=SystemDefaultLanguage.valueOf(obj.getValue());
			}
		}
		ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("acs.parameterConfigure"), 
				ApiFactory.getBussinessLogService().getI18nLogInfo("acs.viewParameterConfigure"),ContextUtils.getSystemId("acs"));
		return "security-set";
	}

	@Override
	protected void prepareModel() throws Exception {
		//entity = securitySetManager.getSecuritySetByName(name,null);
	}

	/**
	 * 参数设置保存
	 */
	@Override
	@Action("security-set-save")
	public String save() throws Exception {
		List<SecuritySetting> entitys = new ArrayList<SecuritySetting>();
		List<SecuritySetting> deleteEntitys = new ArrayList<SecuritySetting>();
		if(StringUtils.isNotEmpty(prems[0])||StringUtils.isNotEmpty(failType)||lockTime!=null){
			entity = securitySetManager.getSecuritySetByName("login-security",null);
			entity.setValue(prems[0]);
			entity.setRemarks(prems[1]);
			if("VALIDATE_CODE".equals(failType)){
				entity.setFailSetType(LoginFailSetType.VALIDATE_CODE);
			}else if("LOCK_USER".equals(failType)){
				entity.setFailSetType(LoginFailSetType.LOCK_USER);
				entity.setLockedTime(lockTime);
			}
			entitys.add(entity);
		}
			
	    if(StringUtils.isNotEmpty(prems[2])){
			entity = securitySetManager.getSecuritySetByName("loginTimeouts",null);
			entity.setValue(prems[2]);
			entity.setRemarks(prems[3]);
			entitys.add(entity);
	    }else {
	    	entity = securitySetManager.getSecuritySetByName("loginTimeouts",null);
	    	deleteEntitys.add(entity);
		}
	    
	    if(StringUtils.isNotEmpty(prems[4])){
			entity = securitySetManager.getSecuritySetByName("password-over-notice",null);
			entity.setValue(prems[4]);
			entity.setRemarks(prems[5]);
			entitys.add(entity);
	    }else {
	    	entity = securitySetManager.getSecuritySetByName("password-over-notice",null);
	    	deleteEntitys.add(entity);
		}
	    
	    if(StringUtils.isNotEmpty(prems[6])){
			entity = securitySetManager.getSecuritySetByName("admin-password-overdue",null);
			entity.setValue(prems[6]);
			entity.setRemarks(prems[7]);
			entitys.add(entity);
	    }else {
	    	entity = securitySetManager.getSecuritySetByName("admin-password-overdue",null);
	    	deleteEntitys.add(entity);
		}
		
	    if(StringUtils.isNotEmpty(prems[8])){
			entity = securitySetManager.getSecuritySetByName("user-password-overdue",null);
			entity.setValue(prems[8]);
			entity.setRemarks(prems[9]);
			entitys.add(entity);
	    }else {
	    	entity = securitySetManager.getSecuritySetByName("user-password-overdue",null);
	    	deleteEntitys.add(entity);
		}
		
	    if(StringUtils.isNotEmpty(passRule)){
			entity = securitySetManager.getSecuritySetByName("password-complexity",null);
			entity.setValue(passRule);
			entitys.add(entity);
	    }
	    if(StringUtils.isNotEmpty(logRemainTime)){
	    	entity = securitySetManager.getSecuritySetByName("log-set",null);
	    	entity.setValue(logRemainTime);
	    	entity.setRemarks(logRemainTimeRemark);
	    	entitys.add(entity);
	    }else {
	    	entity = securitySetManager.getSecuritySetByName("log-set",null);
	    	deleteEntitys.add(entity);
		}
	    if("yes".equals(resetPassword)){
	    	entity = securitySetManager.getSecuritySetByName("reset-password",null);
	    	entity.setValue(resetPassword);
	    	entitys.add(entity);
	    }else{
	    	entity = securitySetManager.getSecuritySetByName("reset-password",null);
	    	entity.setValue("no");
	    	entitys.add(entity);
	    }
	    if(systemDefaultLanguage!=null){
	    	entity = securitySetManager.getSecuritySetByName("system-default-language",null);
			entity.setValue(systemDefaultLanguage.toString());
			entitys.add(entity);
	    }
		securitySetManager.save(entitys);
		securitySetManager.delete(deleteEntitys);
		
		mse ="ok";
		ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("acs.parameterConfigure"), 
				ApiFactory.getBussinessLogService().getI18nLogInfo("acs.submitParameteConfigure"),ContextUtils.getSystemId("acs"));
		return list();
	}

	public void prepareModifyLoginTimeouts() throws Exception {
		prepareModel();
	}

	public SecuritySetting getModel() {

		return entity;
	}

	@Required
	public void setSecuritySetManager(SecuritySetManager securitySetManager) {
		this.securitySetManager = securitySetManager;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassWordLenth() {
		return passWordLenth;
	}

	public void setPassWordLenth(String passWordLenth) {
		this.passWordLenth = passWordLenth;
	}

	public String [] getPrems() {
		return prems;
	}

	public void setPrems(String [] prems) {
		this.prems = prems;
	}

	public String  getPassRule() {
		return passRule;
	}

	public void setPassRule(String  passRule) {
		this.passRule = passRule;
	}

	public String getMse() {
		return mse;
	}

	public void setMse(String mse) {
		this.mse = mse;
	}

	public String getFailType() {
		return failType;
	}

	public void setFailType(String failType) {
		this.failType = failType;
	}

	public Integer getLockTime() {
		return lockTime;
	}

	public void setLockTime(Integer lockTime) {
		this.lockTime = lockTime;
	}

	public String getLogRemainTime() {
		return logRemainTime;
	}

	public void setLogRemainTime(String logRemainTime) {
		this.logRemainTime = logRemainTime;
	}

	public String getLogRemainTimeRemark() {
		return logRemainTimeRemark;
	}

	public void setLogRemainTimeRemark(String logRemainTimeRemark) {
		this.logRemainTimeRemark = logRemainTimeRemark;
	}

	public String getResetPassword() {
		return resetPassword;
	}

	public void setResetPassword(String resetPassword) {
		this.resetPassword = resetPassword;
	}

	public SystemDefaultLanguage getSystemDefaultLanguage() {
		return systemDefaultLanguage;
	}

	public void setSystemDefaultLanguage(SystemDefaultLanguage systemDefaultLanguage) {
		this.systemDefaultLanguage = systemDefaultLanguage;
	}

}
