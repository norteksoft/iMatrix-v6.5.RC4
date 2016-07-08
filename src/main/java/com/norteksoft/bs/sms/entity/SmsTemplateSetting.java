package com.norteksoft.bs.sms.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.norteksoft.product.orm.IdEntityNoExtendField;

/**
 * 短信模版设置
 * @author Administrator
 *
 */
@Entity
@Table(name = "BS_SMS_TEMPLATE_SETTING")
public class SmsTemplateSetting extends IdEntityNoExtendField implements Serializable{
	
	private static final long serialVersionUID = 1L;
		
	@Column(length=50)
	private String templateCode;//模版编号
	@Column(length=250)
	private String templateName;//模版内容
	
	
	public String getTemplateCode() {
		return templateCode;
	}
	public void setTemplateCode(String templateCode) {
		this.templateCode = templateCode;
	}
	public String getTemplateName() {
		return templateName;
	}
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}
	
	
	
	
	
	
}