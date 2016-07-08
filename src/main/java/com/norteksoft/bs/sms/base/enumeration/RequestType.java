package com.norteksoft.bs.sms.base.enumeration;

public enum RequestType {
	/**
	 * http请求
	 */
	HTTP("request.type.http"),
	/**
	 * webservice请求
	 */
	WEBSERVICE("request.type.webservice"),
	/**
	 * restful请求
	 */
	RESTFUL("request.type.restful");
	
	public String code;
	
	RequestType(String code){
		this.code=code;
	}
	
	public int getIndex(){
		return this.ordinal();
	}
	public String getCode() {
		return code;
	}
}
