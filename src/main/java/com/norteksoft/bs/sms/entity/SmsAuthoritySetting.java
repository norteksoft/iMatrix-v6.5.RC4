package com.norteksoft.bs.sms.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import com.norteksoft.bs.sms.base.enumeration.GatewayStatus;
import com.norteksoft.bs.sms.base.enumeration.ReceiveDispatchType;
import com.norteksoft.bs.sms.base.enumeration.RequestType;
import com.norteksoft.product.orm.IdEntityNoExtendField;

/**
 * 收发接口设置/短信平台的权限控制/控制哪些模块可以使用短信功能
 * @author c
 *
 */
@Entity
@Table(name = "BS_SMS_AUTHORITY_SETTING")
public class SmsAuthoritySetting extends IdEntityNoExtendField implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private Long systemId;//系统ID
	@Column(length=20)
	private String systemCode	;	//系统编号
	@Column(length=20)
	private String interCode	;	//接口编号
	@Column(length=300)
	private String description	;	//描述
	@Column(length=50)
	private String templateCode	;	//模板编号
	@Enumerated(EnumType.STRING)
	private RequestType requestType	;	//请求类型 ：http/webservice/restful
	@Column(length=250)
	private String backUrl	;	//回调Url
	@Enumerated(EnumType.STRING)
	private GatewayStatus useStatus	;	//接口状态 ：启用/禁用/草稿
	@Enumerated(EnumType.STRING)
	private ReceiveDispatchType type	;	//收发类型
	
	
	public String getSystemCode() {
		return systemCode;
	}
	public void setSystemCode(String systemCode) {
		this.systemCode = systemCode;
	}
	public String getInterCode() {
		return interCode;
	}
	public void setInterCode(String interCode) {
		this.interCode = interCode;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getTemplateCode() {
		return templateCode;
	}
	public void setTemplateCode(String templateCode) {
		this.templateCode = templateCode;
	}
	public String getBackUrl() {
		return backUrl;
	}
	public void setBackUrl(String backUrl) {
		this.backUrl = backUrl;
	}
	public GatewayStatus getUseStatus() {
		return useStatus;
	}
	public void setUseStatus(GatewayStatus useStatus) {
		this.useStatus = useStatus;
	}
	public ReceiveDispatchType getType() {
		return type;
	}
	public void setType(ReceiveDispatchType type) {
		this.type = type;
	}
	public Long getSystemId() {
		return systemId;
	}
	public void setSystemId(Long systemId) {
		this.systemId = systemId;
	}
	public RequestType getRequestType() {
		return requestType;
	}
	public void setRequestType(RequestType requestType) {
		this.requestType = requestType;
	}

}