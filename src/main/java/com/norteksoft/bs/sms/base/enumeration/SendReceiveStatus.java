package com.norteksoft.bs.sms.base.enumeration;

public enum SendReceiveStatus {
	/**
	 * 仅发送
	 */
	ONLYSEND("only-send"), // 仅发送
	/**
	 * 仅接收
	 */
	OBLYRECEIVE("only-receive"),   // 仅接收
	/**
	 * 发送并接收
	 */
	SENDANDRECEIVE("send-and-receive");//发送并接收
	
	public String code;
	SendReceiveStatus(String code){
		this.code=code;
	}
	public int getIndex(){
		return this.ordinal();
	}
	public String getCode() {
		return code;
	}
}
