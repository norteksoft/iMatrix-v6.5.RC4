package com.norteksoft.acs.entity.authority;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 解析数据授权api用到
 * @author Administrator
 *
 */
public class PermissionInfo implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private boolean hasPermission;//当前用户是否在设置的人员范围内
	private List<DataRuleResult> rules = new ArrayList<DataRuleResult>();
	private Long userId;
	private Long directSuperiorId;
	private List<Long> departmentIds = new ArrayList<Long>();//当前用户所在的部门id的集合
	private boolean noPermission=false;//是否是没有授权,没有授权时,不受权限控制
	private List<String> systemCodes;//所有系统编码集合
	private String dataTableName;//数据表名称（在数据分类的高级设置中“字段名”是表单字段，并且“显示的条件值”是填写的“spring bean的名称”时用到）
	private String fieldName;//字段名（在数据分类的高级设置中“字段名”是表单字段，并且“显示的条件值”是填写的“spring bean的名称”时用到）
	public PermissionInfo(){
		super();
	}
	public PermissionInfo(boolean hasPermission, List<DataRuleResult> rules,Long userId, Long directSuperiorId, List<Long> departmentIds,boolean noPermission) {
		super();
		this.hasPermission = hasPermission;
		this.rules = rules;
		this.userId = userId;
		this.directSuperiorId = directSuperiorId;
		this.departmentIds = departmentIds;
		this.noPermission = noPermission;
	}
	public PermissionInfo(Long userId, Long directSuperiorId, List<Long> departmentIds,List<DataRuleResult> rules){
		super();
		this.userId = userId;
		this.directSuperiorId = directSuperiorId;
		this.departmentIds = departmentIds;
		this.rules = rules;
	}
	public boolean isHasPermission() {
		return hasPermission;
	}
	public void setHasPermission(boolean hasPermission) {
		this.hasPermission = hasPermission;
	}
	public List<DataRuleResult> getRules() {
		return rules;
	}
	public void setRules(List<DataRuleResult> rules) {
		this.rules = rules;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Long getDirectSuperiorId() {
		return directSuperiorId;
	}
	public void setDirectSuperiorId(Long directSuperiorId) {
		this.directSuperiorId = directSuperiorId;
	}
	public List<Long> getDepartmentIds() {
		return departmentIds;
	}
	public void setDepartmentIds(List<Long> departmentIds) {
		this.departmentIds = departmentIds;
	}
	public boolean isNoPermission() {
		return noPermission;
	}
	public void setNoPermission(boolean noPermission) {
		this.noPermission = noPermission;
	}
	public List<String> getSystemCodes() {
		return systemCodes;
	}
	public void setSystemCodes(List<String> systemCodes) {
		this.systemCodes = systemCodes;
	}
	public String getDataTableName() {
		return dataTableName;
	}
	public void setDataTableName(String dataTableName) {
		this.dataTableName = dataTableName;
	}
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
}
