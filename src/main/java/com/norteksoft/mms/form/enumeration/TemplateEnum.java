package com.norteksoft.mms.form.enumeration;
/**
 * 模版
 * @author admin
 *
 */
public enum TemplateEnum {
	/**
	 * 2列
	 */
	TWO_COLUMN("template.two.column"),
	/**
	 * 4列
	 */
	FOUR_COLUMN("template.four.column"),
	/**
	 * 6列
	 */
	SIX_COLUMN("template.six.column");
	
	public String code;
	TemplateEnum(String code){
		this.code=code;
	}
	public Short getIndex(){
		return (short)(this.ordinal()+1);
	}
	public String getCode(){
		return this.code;
	}
}
