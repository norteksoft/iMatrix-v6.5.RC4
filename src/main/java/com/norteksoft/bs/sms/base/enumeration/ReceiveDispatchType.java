package com.norteksoft.bs.sms.base.enumeration;

public enum ReceiveDispatchType {
	/**
	 * 发送
	 */
	SEND("messagePlatform.send"),
	/**
	 * 接收
	 */
	RECEIVE("messagePlatform.receive");
	public String code;
	ReceiveDispatchType(String code){
		this.code=code;
	}
	public int getIndex(){
		return this.ordinal();
	}
	public String getCode() {
		return code;
	}
}
