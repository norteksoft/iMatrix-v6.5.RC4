package com.norteksoft.bs.options.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.norteksoft.product.enumeration.DataState;
import com.norteksoft.product.orm.IdEntityNoExtendField;

/**
 * 普通接口配置
 * @author ldx
 *
 */
@Entity
@Table(name="BS_INTERFACE_SETTING")
public class InterfaceSetting extends IdEntityNoExtendField implements Serializable {
	private static final long serialVersionUID = 1L;
	@Column(length=30)
	private String code;
	@Column(length=100)
	private String name;//名称
	private Long dataSourceId;//数据源id
	@Transient
	private String datasourceCode;
	@Column(length=200)
	private String remark;//说明
	private DataState dataState=DataState.DRAFT;//状态
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
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getDataSourceId() {
		return dataSourceId;
	}
	public void setDataSourceId(Long dataSourceId) {
		this.dataSourceId = dataSourceId;
	}
	public String getDatasourceCode() {
		return datasourceCode;
	}
	public void setDatasourceCode(String datasourceCode) {
		this.datasourceCode = datasourceCode;
	}
	public DataState getDataState() {
		return dataState;
	}
	public void setDataState(DataState dataState) {
		this.dataState = dataState;
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
}
