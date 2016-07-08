package com.norteksoft.wf.engine.entity;

public class AllDeptsModelViewFlex {
	//部门名称
	private String name;
	private String hasSubDepartment;
	//部门是否有用户
	private String isHasUsersInDept;
	//是否时分支机构
	private String branch="false";
	//部门编号
	private String code;
	//是否有分支
	private String hasbranch;
	//子公司名称
	private String subCompanyName;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getHasSubDepartment() {
		return hasSubDepartment;
	}
	public void setHasSubDepartment(String hasSubDepartment) {
		this.hasSubDepartment = hasSubDepartment;
	}
	public String getIsHasUsersInDept() {
		return isHasUsersInDept;
	}
	public void setIsHasUsersInDept(String isHasUsersInDept) {
		this.isHasUsersInDept = isHasUsersInDept;
	}
	
	public String getBranch() {
		return branch;
	}
	public void setBranch(String branch) {
		this.branch = branch;
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
