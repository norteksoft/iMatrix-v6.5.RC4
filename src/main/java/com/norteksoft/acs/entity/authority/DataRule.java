package com.norteksoft.acs.entity.authority;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import com.norteksoft.acs.base.enumeration.DataRange;
import com.norteksoft.product.orm.IdEntityNoExtendField;
/**
 * 数据规则
 * @author Administrator
 *
 */
@Entity
@Table(name="ACS_DATA_RULE")
public class DataRule  extends IdEntityNoExtendField implements Serializable{
	
	private static final long serialVersionUID = 1L;
	@Column(length=50)
	private String code;
	@Column(length=50)
	private String name;
	private Long dataTableId;
	@Column(length=50)
	private String dataTableName;
	@Column(length=200)
	private String remark;
	private Long systemId;
	private Long menuId;//与菜单关联
	
	@OneToMany(cascade=CascadeType.ALL,mappedBy="dataRule")
	@OrderBy("displayIndex asc")
	private List<Condition> conditions;
	
	private DataRange dataRange=DataRange.MYSELF;//数据范围：本人、本部门、所有数据
	private Boolean deparmentInheritable=true;//子部门是否继承该权限
	private Boolean fastable=false;//是否是快速授权自动创建的数据规则,默认不是
	private Boolean simplable=true;//是否是简易设置,true表示是简易设置
	@Column(length=60)
	private String configurableField;//可配置的字段（数据范围选择“本人”或“本部门”时用到）
	
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
	public Long getDataTableId() {
		return dataTableId;
	}
	public void setDataTableId(Long dataTableId) {
		this.dataTableId = dataTableId;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public List<Condition> getConditions() {
		return conditions;
	}
	public void setConditions(List<Condition> conditions) {
		this.conditions = conditions;
	}
	public Long getSystemId() {
		return systemId;
	}
	public void setSystemId(Long systemId) {
		this.systemId = systemId;
	}
	public String getDataTableName() {
		return dataTableName;
	}
	public void setDataTableName(String dataTableName) {
		this.dataTableName = dataTableName;
	}
	public Long getMenuId() {
		return menuId;
	}
	public void setMenuId(Long menuId) {
		this.menuId = menuId;
	}
	public DataRange getDataRange() {
		return dataRange;
	}
	public void setDataRange(DataRange dataRange) {
		this.dataRange = dataRange;
	}
	public Boolean getDeparmentInheritable() {
		return deparmentInheritable;
	}
	public void setDeparmentInheritable(Boolean deparmentInheritable) {
		this.deparmentInheritable = deparmentInheritable;
	}
	public Boolean getFastable() {
		return fastable;
	}
	public void setFastable(Boolean fastable) {
		this.fastable = fastable;
	}
	public Boolean getSimplable() {
		return simplable;
	}
	public void setSimplable(Boolean simplable) {
		this.simplable = simplable;
	}
	public String getConfigurableField() {
		return configurableField;
	}
	public void setConfigurableField(String configurableField) {
		this.configurableField = configurableField;
	}
}
