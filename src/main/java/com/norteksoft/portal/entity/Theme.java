package com.norteksoft.portal.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import com.norteksoft.product.enumeration.DataState;
import com.norteksoft.product.orm.IdEntityNoExtendField;

@Entity
@Table(name="PORTAL_THEME")
public class Theme extends IdEntityNoExtendField{
	private static final long serialVersionUID = 1L;
	@Column(length=50)
	private String code;//编码
	@Column(length=50)
	private String name;//名称
	@Enumerated(EnumType.STRING)
	private DataState dataState=DataState.DRAFT;//状态
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public DataState getDataState() {
		return dataState;
	}
	public void setDataState(DataState dataState) {
		this.dataState = dataState;
	}
}
