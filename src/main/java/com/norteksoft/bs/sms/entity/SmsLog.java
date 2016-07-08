package com.norteksoft.bs.sms.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.norteksoft.product.orm.IdEntityNoExtendField;

/**
 * 短信平台/短信日志/发送日志以及接收日志
 * @author Administrator
 *
 */
@Entity
@Table(name = "BS_SMS_LOG")
public class SmsLog extends IdEntityNoExtendField implements Serializable{
	
	private static final long serialVersionUID = 1L;
		
	
	@Column(length=500)
	private String senderOrReceiver;//发信人/收信人
	private Date sendOrReceiveDate;//发信日期/收信日期
	@Column(length=500)
	private String content;//短信内容
	@Column(length=250)
	private String backUrl;//回调Url
	@Column(length=20)
	private String backSuccess;//回调是否成功
	private Integer sendTime = 0;//发送次数
	@Column(length=20)
	private String logType;//日志类型：接收日志，发送日志 取值：send receive
	@Column(length=30)
	private String gatewayCode;//网关编号
	
	
	public String getGatewayCode() {
		return gatewayCode;
	}
	public void setGatewayCode(String gatewayCode) {
		this.gatewayCode = gatewayCode;
	}
	public String getSenderOrReceiver() {
		return senderOrReceiver;
	}
	public void setSenderOrReceiver(String senderOrReceiver) {
		this.senderOrReceiver = senderOrReceiver;
	}
	public Date getSendOrReceiveDate() {
		return sendOrReceiveDate;
	}
	public void setSendOrReceiveDate(Date sendOrReceiveDate) {
		this.sendOrReceiveDate = sendOrReceiveDate;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getBackUrl() {
		return backUrl;
	}
	public void setBackUrl(String backUrl) {
		this.backUrl = backUrl;
	}
	public String getBackSuccess() {
		return backSuccess;
	}
	public void setBackSuccess(String backSuccess) {
		this.backSuccess = backSuccess;
	}
	public Integer getSendTime() {
		return sendTime;
	}
	public void setSendTime(Integer sendTime) {
		this.sendTime = sendTime;
	}
	public String getLogType() {
		return logType;
	}
	public void setLogType(String logType) {
		this.logType = logType;
	}

	
	
	
}