package com.norteksoft.product.orm;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.GenericGenerator;

import com.norteksoft.product.web.struts2.MetaData;

/**
 * 统一定义id的entity基类.
 */
@SuppressWarnings("serial")
@MappedSuperclass
public class IdEntity implements Serializable {

	@Id
	@GenericGenerator(name = "imatrixEntityIdGenerator", strategy = "native")
	@GeneratedValue(generator = "imatrixEntityIdGenerator")
	private Long id;
	@MetaData(describe="公司id")
	private Long companyId;
	@MetaData(describe="创建人登录名")
	@Column(length=25)
	private String creator; // 创建者登录名
	@MetaData(describe="创建人姓名")
	@Column(length=100)
	private String creatorName; // 创建者姓名
	@MetaData(describe="创建时间")
	private Date createdTime; // 创建时间
	@MetaData(describe="修改人登录名")
	@Column(length=25)
	private String modifier; // 修改者登录名
	@MetaData(describe="修改人姓名")
	@Column(length=25)
	private String modifierName; // 修改者姓名
	@MetaData(describe="修改时间")
	private Date modifiedTime; // 修改时间
	@MetaData(describe="创建人部门id")
	private Long departmentId;//创建人部门id
	@MetaData(describe="创建人id")
	private Long creatorId;//创建者id
	@MetaData(describe="创建人子公司id")
	private Long subCompanyId;//子公司id
	@Embedded
	private EntityExtendField entityExtendField;

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

	public EntityExtendField getEntityExtendField() {
		return entityExtendField;
	}

	public void setEntityExtendField(EntityExtendField entityExtendField) {
		this.entityExtendField = entityExtendField;
	}
	

}
