package com.norteksoft.product.api.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 数据源配置
 * @author ldx
 *
 */
public class DatasourceSetting implements Serializable {
	private static final long serialVersionUID = 1L;
	private String code;
	private String driveName;//驱动
	private String dataBaseUrl;//数据库地址
	private String userName;//数据库用户名
	private String dataBasePassword;//数据库密码,MD5加密
	private String testSql;//测试sql
	private String remark;//说明
	private Long id;
	private Long companyId;
	private String creator; // 创建者登录名
	private String creatorName; // 创建者姓名
	private Date createdTime; // 创建时间
	private String modifier; // 修改者登录名
	private String modifierName; // 修改者姓名
	private Date modifiedTime; // 修改时间
	private Long departmentId;//创建人部门id
	private Long creatorId;//创建者id
	private Long subCompanyId;//子公司id
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getDriveName() {
		return driveName;
	}
	public void setDriveName(String driveName) {
		this.driveName = driveName;
	}
	public String getDataBaseUrl() {
		return dataBaseUrl;
	}
	public void setDataBaseUrl(String dataBaseUrl) {
		this.dataBaseUrl = dataBaseUrl;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getDataBasePassword() {
		return dataBasePassword;
	}
	public void setDataBasePassword(String dataBasePassword) {
		this.dataBasePassword = dataBasePassword;
	}
	public String getTestSql() {
		return testSql;
	}
	public void setTestSql(String testSql) {
		this.testSql = testSql;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getCompanyId() {
		return companyId;
	}
	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public String getCreatorName() {
		return creatorName;
	}
	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}
	public Date getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}
	public String getModifier() {
		return modifier;
	}
	public void setModifier(String modifier) {
		this.modifier = modifier;
	}
	public String getModifierName() {
		return modifierName;
	}
	public void setModifierName(String modifierName) {
		this.modifierName = modifierName;
	}
	public Date getModifiedTime() {
		return modifiedTime;
	}
	public void setModifiedTime(Date modifiedTime) {
		this.modifiedTime = modifiedTime;
	}
	public Long getDepartmentId() {
		return departmentId;
	}
	public void setDepartmentId(Long departmentId) {
		this.departmentId = departmentId;
	}
	public Long getCreatorId() {
		return creatorId;
	}
	public void setCreatorId(Long creatorId) {
		this.creatorId = creatorId;
	}
	public Long getSubCompanyId() {
		return subCompanyId;
	}
	public void setSubCompanyId(Long subCompanyId) {
		this.subCompanyId = subCompanyId;
	}
}
