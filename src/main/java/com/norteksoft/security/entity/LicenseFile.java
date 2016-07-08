package com.norteksoft.security.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

/**
 * License管理
 * @author nortek
 *
 */
@Entity
@Table(name = "ACS_LICENSE_FILE")
public class LicenseFile implements Serializable{
	private static final long serialVersionUID = 1L;
	@Id
	@GenericGenerator(name = "imatrixEntityIdGenerator", strategy = "native")
	@GeneratedValue(generator = "imatrixEntityIdGenerator")
	private Long id;
	@Transient
	private String companyName;//租户名称
	@Transient
	private String formerName;//曾用名
	@Transient
	private Integer enrollment;//注册人数
	@Transient
	private Date startTime;//生效日期
	@Transient
	private Date endTime;//失效日期
	@Transient
	private String licenseVersion;//版本类型
	@Transient
	private String exceptionMessage;//异常信息
	@Lob
    @Column(columnDefinition="LONGTEXT", nullable=true)
	private String content;//License文件内容
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getLicenseVersion() {
		return licenseVersion;
	}
	public void setLicenseVersion(String licenseVersion) {
		this.licenseVersion = licenseVersion;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getFormerName() {
		return formerName;
	}
	public void setFormerName(String formerName) {
		this.formerName = formerName;
	}
	public Integer getEnrollment() {
		return enrollment;
	}
	public void setEnrollment(Integer enrollment) {
		this.enrollment = enrollment;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public String getExceptionMessage() {
		return exceptionMessage;
	}
	public void setExceptionMessage(String exceptionMessage) {
		this.exceptionMessage = exceptionMessage;
	}
	
}
