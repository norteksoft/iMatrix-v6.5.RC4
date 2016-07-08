package com.norteksoft.mms.form.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import com.norteksoft.mms.form.enumeration.QueryType;
import com.norteksoft.product.orm.IdEntityNoExtendField;

/**
 * 查询条件
 * @author admin
 *
 */
@Entity
@Table(name="MMS_SEARCH_CONDITION")
public class SearchCondition extends IdEntityNoExtendField  implements Serializable{
	private static final long serialVersionUID = 1L;
	@Column(length=50)
	private String name;//条件名称
	private Long userId;//保存条件的人员id
	@Column(length=50)
	private String listCode;//列表编号
	@Enumerated(EnumType.STRING)
	private QueryType queryType;//条件类型：FIXED:固定查询，CUSTOM：高级查询
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getListCode() {
		return listCode;
	}
	public void setListCode(String listCode) {
		this.listCode = listCode;
	}
	public QueryType getQueryType() {
		return queryType;
	}
	public void setQueryType(QueryType queryType) {
		this.queryType = queryType;
	}
}
