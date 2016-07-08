package com.norteksoft.acs.base.enumeration;
/**
 * 锁定状态
 * @author nortek
 *
 */
public enum LockedState {
	/**
	 * 正常
	 */
	NORMAL("license.locked.state.normal"),
	/**
	 * 锁定
	 */
	LOCKED("license.locked.state.locked");
	
	public String code;
	
	LockedState(String code){
		this.code=code;
	}
	public Short getIndex(){
		return (short)(this.ordinal()+1);
	}
	public String getCode(){
		return this.code;
	}
}
