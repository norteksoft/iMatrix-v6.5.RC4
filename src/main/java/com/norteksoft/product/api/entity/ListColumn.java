package com.norteksoft.product.api.entity;

import com.norteksoft.mms.form.enumeration.DataType;

public class ListColumn {
	private String headerName;//列头名
	private String columnName;//数据表字段名
	private DataType dataType;//字段类型
	private Boolean visible;//是否显示
	private String dbName;//数据库字段名
	public String getHeaderName() {
		return headerName;
	}
	public void setHeaderName(String headerName) {
		this.headerName = headerName;
	}
	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	public DataType getDataType() {
		return dataType;
	}
	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}
	public Boolean getVisible() {
		return visible;
	}
	public void setVisible(Boolean visible) {
		this.visible = visible;
	}
	public String getDbName() {
		return dbName;
	}
	public void setDbName(String dbName) {
		this.dbName = dbName;
	}
	
}
