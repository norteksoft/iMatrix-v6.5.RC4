package com.norteksoft.bs.options.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.norteksoft.product.orm.IdEntityNoExtendField;
import com.norteksoft.product.web.struts2.Struts2Utils;

/**
 * 数据源配置
 * @author ldx
 *
 */
@Entity
@Table(name="BS_DATASOURCE_SETTING")
public class DatasourceSetting extends IdEntityNoExtendField implements Serializable {
	private static final long serialVersionUID = 1L;
	@Column(length=30)
	private String code;
	@Column(length=100)
	private String driveName="oracle.jdbc.driver.OracleDriver";//驱动
	@Column(length=100)
	private String dataBaseUrl="${interfaceManager.datasourceSetting.dataBaseUrl}";//数据库地址(jdbc:oracle:thin:@ip地址:1521:数据库)
	@Column(length=30)
	private String userName;//数据库用户名
	@Column(length=40)
	private String dataBasePassword;//数据库密码,MD5加密
	@Column(length=255)
	private String testSql;//测试sql
	@Column(length=200)
	private String remark;//说明
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
}
