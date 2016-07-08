package com.norteksoft.wf.engine.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.norteksoft.product.orm.IdEntityNoExtendField;


@Entity
@Table(name="WF_DATA_DICTIONARY_TYPE")
public class DataDictionaryType extends IdEntityNoExtendField implements Serializable {
	private static final long serialVersionUID = 1L;
	@Column(length=50)
	private String no;      //类型编号  唯一
	@Column(length=50)
	private String name;      //类型名称
	
	private Long systemId; //系统ID
	@Column(length=255)
	private String typeIds;

	public String getNo() {
		return no;
	}

	public void setNo(String no) {
		this.no = no;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getSystemId() {
		return systemId;
	}

	public void setSystemId(Long systemId) {
		this.systemId = systemId;
	}

	public String getTypeIds() {
		return typeIds;
	}

	public void setTypeIds(String typeIds) {
		this.typeIds = typeIds;
	}

	@Override
	public String toString() {
		return "DataDictionaryType [companyId=" + this.getCompanyId() 
				+ ", name=" + name + ", systemId="
				+ systemId + "]";
	}
	
	

}
