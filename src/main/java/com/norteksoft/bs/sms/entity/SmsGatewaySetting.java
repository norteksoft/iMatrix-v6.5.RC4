package com.norteksoft.bs.sms.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import com.norteksoft.bs.sms.base.enumeration.GatewayStatus;
import com.norteksoft.bs.sms.base.enumeration.SendReceiveStatus;
import com.norteksoft.product.orm.IdEntityNoExtendField;

/**
 * 短信网关设置
 * @author Administrator
 *
 */
@Entity
@Table(name = "BS_SMS_GATEWAY_SETTING")
public class SmsGatewaySetting extends IdEntityNoExtendField implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@Column(length=30)
	private String gatewayCode;//网关编号
	@Column(length=50)
	private String gatewayName;//网关名称
	@Enumerated(EnumType.STRING)
	private GatewayStatus gatewayStatus;//网关状态
	@Enumerated(EnumType.STRING)
	private SendReceiveStatus sendReceiveStatus;//收发设置

	private Integer maxTime;//最大发送次数
	@Column(length=500)
	private String configuration;//配置
	
	@Column(length=20)
	private String gatewayType;//网关类型
	@Column(length=200)
	private String implClassName;//实现类类名，自定义网关才有值
	
	private Integer maxcountTowarn = 0;//报警需最大短信条数：
	
	
	
	
	public Integer getMaxcountTowarn() {
		return maxcountTowarn;
	}
	public void setMaxcountTowarn(Integer maxcountTowarn) {
		this.maxcountTowarn = maxcountTowarn;
	}
	public String getGatewayCode() {
		return gatewayCode;
	}
	public void setGatewayCode(String gatewayCode) {
		this.gatewayCode = gatewayCode;
	}
	public String getGatewayName() {
		return gatewayName;
	}
	public void setGatewayName(String gatewayName) {
		this.gatewayName = gatewayName;
	}
	public String getConfiguration() {
		return configuration;
	}
	public void setConfiguration(String configuration) {
		this.configuration = configuration;
	}
	public Integer getMaxTime() {
		return maxTime;
	}
	public void setMaxTime(Integer maxTime) {
		this.maxTime = maxTime;
	}
	public SendReceiveStatus getSendReceiveStatus() {
		return sendReceiveStatus;
	}
	public void setSendReceiveStatus(SendReceiveStatus sendReceiveStatus) {
		this.sendReceiveStatus = sendReceiveStatus;
	}
	public GatewayStatus getGatewayStatus() {
		return gatewayStatus;
	}
	public void setGatewayStatus(GatewayStatus gatewayStatus) {
		this.gatewayStatus = gatewayStatus;
	}
	public String getGatewayType() {
		return gatewayType;
	}
	public void setGatewayType(String gatewayType) {
		this.gatewayType = gatewayType;
	}
	public String getImplClassName() {
		return implClassName;
	}
	public void setImplClassName(String implClassName) {
		this.implClassName = implClassName;
	}
	
}