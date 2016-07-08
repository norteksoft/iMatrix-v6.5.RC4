package com.norteksoft.product.util;

public class PermissionThreadParameters {
	private String hql;
	private Object[] parameters;
	private Boolean clearDataPermission=false;
	
	public PermissionThreadParameters() {
		super();
	}
	
	public PermissionThreadParameters(String hql, Object[] parameters) {
		super();
		this.hql = hql;
		this.parameters = parameters;
	}
	
	public String getHql() {
		return hql;
	}
	public void setHql(String hql) {
		this.hql = hql;
	}
	public Object[] getParameters() {
		return parameters;
	}
	public void setParameters(Object[] parameters) {
		this.parameters = parameters;
	}
	public Boolean getClearDataPermission() {
		return clearDataPermission;
	}
	public void setClearDataPermission(Boolean clearDataPermission) {
		this.clearDataPermission = clearDataPermission;
	}
}
