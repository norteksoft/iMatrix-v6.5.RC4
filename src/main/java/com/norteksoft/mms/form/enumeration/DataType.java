package com.norteksoft.mms.form.enumeration;

/**
 * 表字段的类型
 */
public enum DataType {
	TEXT("formManager.text"),
	DATE("formManager.date"),
	TIME("formManager.time"),
	INTEGER("formManager.integer"),
	LONG("formManager.long"),
	DOUBLE("formManager.double"),
	FLOAT("formManager.float"),
	BOOLEAN("formManager.boolean"),
	CLOB("formManager.longText"),
	BLOB("formManager.blob"),
	COLLECTION("formManager.collection"),
	ENUM("formManager.enum"),
	REFERENCE("formManager.reference"),
	@Deprecated
	AMOUNT("formManager.amount"),
	@Deprecated
	NUMBER("formManager.number")
	;
	public String code;
	DataType(String code){
		this.code=code;
	}
	public Short getIndex(){
		return (short)(this.ordinal()+1);
	}
	public String getCode(){
		return this.code;
	}
	/**
	 * 返回枚举的名称
	 * @return
	 */
	public String getEnumName(){
		return this.toString();
	}
}
