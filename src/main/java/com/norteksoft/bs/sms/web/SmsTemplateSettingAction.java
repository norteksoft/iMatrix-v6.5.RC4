package com.norteksoft.bs.sms.web;


import org.apache.commons.lang.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;

import com.norteksoft.bs.sms.entity.SmsTemplateSetting;
import com.norteksoft.bs.sms.service.SmsTemplateSettingManager;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.PageUtils;
import com.norteksoft.product.web.struts2.CrudActionSupport;
import com.norteksoft.product.web.struts2.Struts2Utils;
/**
 * 短信模版设置
 * @author c
 *
 */
@Namespace("/sms")
@ParentPackage("default")
@Results( { @Result(name = CrudActionSupport.RELOAD, location = "sms-template-setting", type = "redirectAction") })
public class SmsTemplateSettingAction extends CrudActionSupport<SmsTemplateSetting> {

	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String ids;
	private Page<SmsTemplateSetting> page = new Page<SmsTemplateSetting>(0,true);
	private SmsTemplateSetting smsTemplateSetting;
	private String templateCode;//模板编号
	
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
	private SmsTemplateSettingManager smsTemplateSettingManager;
	
	
	
	/**
	 * 短信平台/短信模版设置/列表
	 * @return
	 */
	@Override
	@Action("sms-template-setting")
	public String list() throws Exception {
		if(page.getPageSize()>1){
			smsTemplateSettingManager.getAllSmsTemplateSetting(page);
			this.renderText(PageUtils.pageToJson(page));
			return null;
		}
		return "sms-template-setting";
	}

	/**
	 * 短信平台/短信模版设置/新建修改
	 * @return
	 */
	@Override
	@Action("sms-template-setting-input")
	public String input() throws Exception {
		return "sms-template-setting-input";
	}
	/**
	 * 短信平台/短信模版设置/保存
	 * @return
	 */
	@Override
	@Action("sms-template-setting-save")
	public String save() throws Exception {
		smsTemplateSettingManager.save(smsTemplateSetting);
		this.addSuccessMessage(Struts2Utils.getText("form.save.success"));
		return "sms-template-setting-input";
	}
	/**
	 * 短信平台/短信模版设置/验证编号唯一性
	 * @return
	 */
	@Action("sms-template-setting-validateCode")
	public String validateCode() throws Exception {
		boolean flag = smsTemplateSettingManager.validateCode(templateCode,id);
		this.renderText(String.valueOf(flag));
		return null;
	}
	/**
	 * 短信平台/短信模版设置/删除
	 * @return
	 */
	@Override
	@Action("sms-template-setting-delete")
	public String delete() throws Exception {
		smsTemplateSettingManager.delete(StringUtils.removeEnd(ids, ","));
		this.addSuccessMessage(Struts2Utils.getText("basicSetting.deleteSuccess"));
		return "sms-template-setting";
	}

	@Override
	protected void prepareModel() throws Exception {
		if(null == id){
			smsTemplateSetting = new SmsTemplateSetting();
		}else {
			smsTemplateSetting = smsTemplateSettingManager.getSmsTemplateSettingById(id);
		}
	}

	public SmsTemplateSetting getModel() {
		return smsTemplateSetting;
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
	public Page<SmsTemplateSetting> getPage() {
		return page;
	}
	public void setPage(Page<SmsTemplateSetting> page) {
		this.page = page;
	}
	public SmsTemplateSetting getSmsTemplateSetting() {
		return smsTemplateSetting;
	}
	public void setSmsTemplateSetting(SmsTemplateSetting SmsTemplateSetting) {
		this.smsTemplateSetting = SmsTemplateSetting;
	}

	public String getTemplateCode() {
		return templateCode;
	}

	public void setTemplateCode(String templateCode) {
		this.templateCode = templateCode;
	}
 
}
