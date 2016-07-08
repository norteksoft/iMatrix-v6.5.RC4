package com.norteksoft.mms.form.enumeration;

/**
 * 控件的类型
 * @author wurong
 *
 */
public enum ControlType {
	/**
	 * 文本框
	 */
	TEXT("edit.control.type.text"),
	/**
	 * 隐藏域
	 */
	HIDDEN("edit.control.type.hidden"),
	/**
	 * 密码框
	 */
	PASSWORD("edit.control.type.password"),
	/**
	 * 单选框
	 */
	RADIO("edit.control.type.radio"),
	/**
	 * 复选框
	 */
	CHECKBOX("edit.control.type.checkbox"),
	/**
	 * 下拉框
	 */
	SELECT("edit.control.type.select"),
	/**
	 * 按钮控件
	 */
	BUTTON("edit.control.type.button"),
	/**
	 * 文本域
	 */
	TEXTAREA("edit.control.type.textarea"),
	/**
	 * 日期时间控件
	 */
	TIME("edit.control.type.time"),
	/**
	 * 部门人员控件
	 */
	SELECT_MAN_DEPT("edit.control.type.departperson"),
	/**
	 * 计算控件
	 */
	CALCULATE_COMPONENT("edit.control.type.calculate"),
	/**
	 *下拉菜单控件
	 */
	PULLDOWNMENU("edit.control.type.downmenu"),
	/**
	 * 数据选择控件
	 */
	DATA_SELECTION("edit.control.type.dataSelection"),
	/**
	 * 数据获取控件
	 */
	DATA_ACQUISITION("edit.control.type.dataAcquisition"),
	/**
	 * 紧急程度设置控件
	 */
	URGENCY("edit.control.type.urgency"),
	/**
	 * 特事特办控件
	 */
	CREATE_SPECIAL_TASK("edit.control.type.specialTask"),
	/**
	 * 特事特办人员选择
	 */
	SPECIAL_TASK_TRANSACTOR("edit.control.type.specialTaskTransactor"),
	/**
	 * 自定义列表控件
	 */
	LIST_CONTROL("edit.control.type.customListControl"),
	/**
	 * 标准列表控件
	 */
	STANDARD_LIST_CONTROL("edit.control.type.standardList"),
	/**
	 * 标签控件
	 */
	LABEL("edit.control.type.label"),
	/**
	 * 签章控件
	 */
	SIGNATURE_CONTROL("edit.control.type.signature"),
	/**
	 * 附件上传控件
	 */
	ATTACH_UPLOAD("edit.control.type.attachUpload"),
	/**
	 * 图片上传控件
	 */
	IMAGE_UPLOAD("edit.control.type.imageUpload"),
	/**
	 * 图片控件
	 */
	IMAGE("edit.control.type.image"),
	/**
	 * js/css
	 */
	JAVASCRIPT_CSS("edit.control.type.js"),
	/**
	 * 宏控件
	 */
	MACRO("edit.control.type.macro"),
	/**
	 * 占位符控件
	 */
	PLACEHOLDER("edit.control.type.placeholder"),
	/**
	 * 占位符控件
	 */
	OPINION("edit.control.type.option")
	;
	public String code;
	ControlType(String code){
		this.code=code;
	}
	public int getIndex(){
		return this.ordinal();
	}
	public String getCode() {
		return code;
	}
	/**
	 * 返回枚举的名称
	 * @return
	 */
	public String getEnumName(){
		return this.toString();
	}

	
}
