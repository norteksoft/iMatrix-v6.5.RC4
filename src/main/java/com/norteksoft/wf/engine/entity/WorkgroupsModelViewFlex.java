package com.norteksoft.wf.engine.entity;

public class WorkgroupsModelViewFlex {
	/**
	 * 工作组名称
	 */
	private String name;
	/**
	 * 工作组编号
	 */
	private String code;
	/**
	 * 是否是分支
	 */
	private String hasbranch;
	/**
	 * 子公司名称
	 */
	private String subCompanyName;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getHasbranch() {
		return hasbranch;
	}
	public void setHasbranch(String hasbranch) {
		this.hasbranch = hasbranch;
	}
	public String getSubCompanyName() {
		return subCompanyName;
	}
	public void setSubCompanyName(String subCompanyName) {
		this.subCompanyName = subCompanyName;
	}
	
}
