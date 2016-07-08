package com.norteksoft.bs.sms.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import com.norteksoft.product.orm.IdEntityNoExtendField;
import com.norteksoft.product.web.struts2.Struts2Utils;

/**
 * 待发送列表
 * @author Administrator
 *
 */
@Entity
@Table(name = "BS_SMS_WAIT_TOSEND")
public class SmsWaitTosend extends IdEntityNoExtendField implements Serializable{
	
	private static final long serialVersionUID = 1L;
		
	
	@Column(length=500)
	private String receiver;//收信人（注：${common.allPersonnel}表示“所有人员”）
	@Transient
	private String internationReceiver;//收信人(国际化使用，当收信人是“所有人员”时国际化，否则保持原样)
	@Column(length=500)
	private String content;//短信内容
	
	private Integer sendTime = 0;//发送次数
	
	@Column(length=20)
	private String interCode	;	//接口编号

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Integer getSendTime() {
		return sendTime;
	}

	public void setSendTime(Integer sendTime) {
		this.sendTime = sendTime;
	}

	public String getInterCode() {
		return interCode;
	}

	public void setInterCode(String interCode) {
		this.interCode = interCode;
	}

	public String getInternationReceiver() {
		String temp = receiver;
		if(StringUtils.isNotEmpty(receiver)&&receiver.equals("${common.allPersonnel}")){
			temp = Struts2Utils.getText("common.allPersonnel");
		}
		return temp;
	}

	public void setInternationReceiver(String internationReceiver) {
		this.internationReceiver = internationReceiver;
	}
	
	
	
	
}