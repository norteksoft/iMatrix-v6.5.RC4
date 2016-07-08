package com.norteksoft.bs.sms.web;


import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;

import com.norteksoft.acs.entity.authorization.BusinessSystem;
import com.norteksoft.acs.service.authorization.BusinessSystemManager;
import com.norteksoft.bs.sms.entity.SmsAuthoritySetting;
import com.norteksoft.bs.sms.entity.SmsTemplateSetting;
import com.norteksoft.bs.sms.service.SmsAuthoritySettingManager;
import com.norteksoft.bs.sms.service.SmsTemplateSettingManager;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.PageUtils;
import com.norteksoft.product.web.struts2.CrudActionSupport;
import com.norteksoft.product.web.struts2.Struts2Utils;
/**
 * 收发接口设置/短信平台的权限控制/控制哪些模块可以使用短信功能
 * @author c
 *
 */
@Namespace("/sms")
@ParentPackage("default")
@Results( { @Result(name = CrudActionSupport.RELOAD, location = "Sms-authority-setting", type = "redirectAction") })
public class SmsAuthoritySettingAction extends CrudActionSupport<SmsAuthoritySetting> {

	private static final long serialVersionUID = 1L;
	
	private Long id;
	private Long systemId;
	private String ids;
	private String requestType;
	private Page<SmsAuthoritySetting> page = new Page<SmsAuthoritySetting>(0,true);
	private SmsAuthoritySetting smsAuthoritySetting;
	private List<BusinessSystem> businessSystems;
	private List<SmsTemplateSetting> smsTemplateSettings;
	
	private static final String SUCCESS_MESSAGE_LEFT = "<font class=\"onSuccess\"><nobr>";
	private static final String MESSAGE_RIGHT = "</nobr></font>";
	private static final String ERROR_MESSAGE_LEFT = "<font class=\"onError\"><nobr>";
	protected void addErrorMessage(String message) {
		this.addActionMessage(ERROR_MESSAGE_LEFT + message + MESSAGE_RIGHT);
	}
	protected void addSuccessMessage(String message){
		this.addActionMessage(SUCCESS_MESSAGE_LEFT+message+MESSAGE_RIGHT);
	}
	@Autowired
	private SmsAuthoritySettingManager smsAuthoritySettingManager;
	@Autowired
	private SmsTemplateSettingManager smsTemplateSettingManager;
	@Autowired
	private BusinessSystemManager businessSystemManager;
	
	private String interCode;//接口编号
	
	
	
	/**
	 * 短信平台/收发接口设置/列表
	 * @return
	 */
	@Override
	@Action("sms-authority-setting")
	public String list() throws Exception {
		if(page.getPageSize()>1){
			smsAuthoritySettingManager.getAllSmsAuthoritySetting(page,systemId);
			this.renderText(PageUtils.pageToJson(page));
			return null;
		}else{
			businessSystems = businessSystemManager.getAllSystems();
			if(businessSystems.size()>0 && systemId == null)
				systemId = businessSystems.get(0).getId();
		}
		return "sms-authority-setting";
	}

	/**
	 * 短信平台/收发接口设置/新建修改
	 * @return
	 */
	@Override
	@Action("sms-authority-setting-input")
	public String input() throws Exception {
		return "sms-authority-setting-input";
	}
	/**
	 * 短信平台/收发接口设置/选择模版
	 * @return
	 */
	@Action("sms-authority-setting-selectTemplate")
	public String selectTemplate() throws Exception {
		smsTemplateSettings = smsTemplateSettingManager.getAllSmsTemplateSetting();
		return "sms-authority-setting-selectTemplate";
	}
	/**
	 * 短信平台/收发接口设置/保存
	 * @return
	 */
	@Override
	@Action("sms-authority-setting-save")
	public String save() throws Exception {
		smsAuthoritySettingManager.save(systemId,requestType,smsAuthoritySetting);
		this.addSuccessMessage(Struts2Utils.getText("form.save.success"));
		return "sms-authority-setting-input";
	}
	/**
	 * 短信平台/收发接口设置/验证编号唯一性
	 * @return
	 */
	@Action("sms-authority-setting-validateCode")
	public String validateCode() throws Exception {
		boolean flag = smsAuthoritySettingManager.validateCode(interCode,id);
		this.renderText(String.valueOf(flag));
		return null;
	}
	/**
	 * 短信平台/收发接口设置/删除
	 * @return
	 */
	@Override
	@Action("sms-authority-setting-delete")
	public String delete() throws Exception {
		String flag = smsAuthoritySettingManager.delete(StringUtils.removeEnd(ids, ","));
		if(StringUtils.isNotEmpty(flag)){
			this.addErrorMessage(flag);
		}else {
			this.addSuccessMessage(Struts2Utils.getText("basicSetting.deleteSuccess"));
		}
		return "sms-authority-setting";
	}
	/**
	 * 短信平台/收发接口设置/启用禁用
	 * @return
	 */
	@Action("sms-authority-setting-changeStatus")
	public String changeStatus() throws Exception {
		String result = smsAuthoritySettingManager.changeStatus(StringUtils.removeEnd(ids, ","));
		this.addSuccessMessage(result);
		return "sms-authority-setting";
	}

	@Override
	protected void prepareModel() throws Exception {
		if(null == id){
			smsAuthoritySetting = new SmsAuthoritySetting();
		}else {
			smsAuthoritySetting = smsAuthoritySettingManager.getSmsAuthoritySettingById(id);
		}
	}

	public SmsAuthoritySetting getModel() {
		return smsAuthoritySetting;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getIds() {
		return ids;
	}
	public void setIds(String ids) {
		this.ids = ids;
	}
	public Page<SmsAuthoritySetting> getPage() {
		return page;
	}
	public void setPage(Page<SmsAuthoritySetting> page) {
		this.page = page;
	}
	public SmsAuthoritySetting getSmsAuthoritySetting() {
		return smsAuthoritySetting;
	}
	public void setSmsAuthoritySetting(SmsAuthoritySetting smsAuthoritySetting) {
		this.smsAuthoritySetting = smsAuthoritySetting;
	}
	public String getInterCode() {
		return interCode;
	}
	public void setInterCode(String interCode) {
		this.interCode = interCode;
	}
	public Long getSystemId() {
		return systemId;
	}
	public void setSystemId(Long systemId) {
		this.systemId = systemId;
	}
	public List<BusinessSystem> getBusinessSystems() {
		return businessSystems;
	}
	public void setBusinessSystems(List<BusinessSystem> businessSystems) {
		this.businessSystems = businessSystems;
	}
	public String getRequestType() {
		return requestType;
	}
	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}
	public List<SmsTemplateSetting> getSmsTemplateSettings() {
		return smsTemplateSettings;
	}
	public void setSmsTemplateSettings(List<SmsTemplateSetting> smsTemplateSettings) {
		this.smsTemplateSettings = smsTemplateSettings;
	}
	
 
}
