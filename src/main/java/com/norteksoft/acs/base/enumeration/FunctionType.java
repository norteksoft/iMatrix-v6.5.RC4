package com.norteksoft.acs.base.enumeration;

public enum FunctionType {
	PUBLIC("function.functionType.public"), // 公用
	DEFAULT("function.functionType.default"), // 匿名
	PRIVATE("function.functionType.private"); // 专用
	public String code;
	FunctionType(String code){
		this.code=code;
	}
	public String getCode() {
		return code;
	}
}
