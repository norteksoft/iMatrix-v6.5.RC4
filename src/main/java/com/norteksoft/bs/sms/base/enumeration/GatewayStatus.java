package com.norteksoft.bs.sms.base.enumeration;

public enum GatewayStatus {
	/**
	 * 启用
	 */
	ENABLE("gateway.status.enable"),
	/**
	 * 禁用
	 */
	DISABLE("gateway.status.disable"),
	/**
	 * 草稿
	 */
	DRAFT("gateway.status.draft");
	public String code;
	GatewayStatus(String code){
		this.code=code;
	}
	public int getIndex(){
		return this.ordinal();
	}
	public String getCode() {
		return code;
	}
}
