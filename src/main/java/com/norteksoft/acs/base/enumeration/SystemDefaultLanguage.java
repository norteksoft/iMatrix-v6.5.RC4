package com.norteksoft.acs.base.enumeration;
/**
 * 默认语言
 * @author nortek
 *
 */
public enum SystemDefaultLanguage {
	/**
	 * 中文
	 */
	CHINESE("system.default.language.chinese"),
	/**
	 * 英文
	 */
	ENGLISH("system.default.language.english");
	
	public String code;
	SystemDefaultLanguage(String code){
		this.code=code;
	}
	public Short getIndex(){
		return (short)(this.ordinal()+1);
	}
	public String getCode(){
		return this.code;
	}
}
