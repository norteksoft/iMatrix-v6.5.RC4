package com.norteksoft.bs.options.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.norteksoft.bs.options.enumeration.ApplyType;
import com.norteksoft.product.enumeration.DataState;
import com.norteksoft.product.orm.IdEntityNoExtendField;
/**
 * 定时任务
 * @author Administrator
 *
 */
@Entity
@Table(name="BS_TIMED_TASK")
public class TimedTask extends IdEntityNoExtendField implements Serializable {

	private static final long serialVersionUID = 1L;
	private Long systemId;//系统ID
	@Column(length=50)
	private String systemCode;//系统code
	@Column(length=50)
	private String code;//定时编号
	@Column(length=150)
	private String url; // 运行URL
	@Column(length=200)
	private String description;//备注
	@Column(length=25)
	private String runAsUser; // 运行身份当前用户登陆名
	
	private Long runAsUserId; // 运行身份当前用户id
	@Column(length=25)
	private String runAsUserName; // 运行身份当前用户名
	
	private Integer timeout = 30; // 单位(秒)
	
	private DataState dataState=DataState.DRAFT;//状态
	private ApplyType applyType;
	@Column(length=500)
	private String emails;//邮件提醒人的邮箱集合，多个以逗号隔开
	@Column(length=500)
	private String rtxAccounts;//rtx提醒人的rtx账号，多个以逗号隔开
	@Column(length=300)
	private String phoneReminderNums;//短信提醒人的手机号集合，多个以逗号隔开
	@Column(length=300)
	private String officeHelperReminderIds;//办公助手提醒人的用户id集合，多个以逗号隔开
	@Column(length=500)
	private String officeHelperReminderNames;//办公助手提醒人的用户姓名集合，多个以逗号隔开

	private Long dataSourceId;//数据源id
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getRunAsUser() {
		return runAsUser;
	}

	public void setRunAsUser(String runAsUser) {
		this.runAsUser = runAsUser;
	}

	public Integer getTimeout() {
		return timeout;
	}

	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSystemCode() {
		return systemCode;
	}

	public void setSystemCode(String systemCode) {
		this.systemCode = systemCode;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Long getSystemId() {
		return systemId;
	}

	public void setSystemId(Long systemId) {
		this.systemId = systemId;
	}

	public DataState getDataState() {
		return dataState;
	}

	public void setDataState(DataState dataState) {
		this.dataState = dataState;
	}

	public String getRunAsUserName() {
		return runAsUserName;
	}

	public void setRunAsUserName(String runAsUserName) {
		this.runAsUserName = runAsUserName;
	}
	
	@Override
	public String toString() {
		return "id:"+this.getId()+"；系统编码："+this.systemCode+"；定时任务地址："+this.url;
	}

	public ApplyType getApplyType() {
		return applyType;
	}

	public void setApplyType(ApplyType applyType) {
		this.applyType = applyType;
	}

	public Long getRunAsUserId() {
		return runAsUserId;
	}

	public void setRunAsUserId(Long runAsUserId) {
		this.runAsUserId = runAsUserId;
	}

	public String getEmails() {
		return emails;
	}

	public void setEmails(String emails) {
		this.emails = emails;
	}

	public String getRtxAccounts() {
		return rtxAccounts;
	}

	public void setRtxAccounts(String rtxAccounts) {
		this.rtxAccounts = rtxAccounts;
	}

	public String getPhoneReminderNums() {
		return phoneReminderNums;
	}

	public void setPhoneReminderNums(String phoneReminderNums) {
		this.phoneReminderNums = phoneReminderNums;
	}

	public String getOfficeHelperReminderIds() {
		return officeHelperReminderIds;
	}

	public void setOfficeHelperReminderIds(String officeHelperReminderIds) {
		this.officeHelperReminderIds = officeHelperReminderIds;
	}

	public String getOfficeHelperReminderNames() {
		return officeHelperReminderNames;
	}

	public void setOfficeHelperReminderNames(String officeHelperReminderNames) {
		this.officeHelperReminderNames = officeHelperReminderNames;
	}

	public Long getDataSourceId() {
		return dataSourceId;
	}

	public void setDataSourceId(Long dataSourceId) {
		this.dataSourceId = dataSourceId;
	}

}
