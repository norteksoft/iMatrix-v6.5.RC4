package com.norteksoft.mms.form.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.norteksoft.product.orm.IdEntityNoExtendField;

/**
 * 查询条件值
 * @author admin
 *
 */
@Entity
@Table(name="MMS_SEARCH_CONDITION_VALUE")
public class SearchConditionValue extends IdEntityNoExtendField  implements Serializable{
	private static final long serialVersionUID = 1L;
	@Column(length=50)
	private String fieldName;//字段名
	@Column(length=50)
	private String dbFieldName;//数据库表中的字段名
	@Column(length=20)
	private String fieldOperator;//比较符号
	@Column(length=25)
	private String conditionValue;//条件值
	@Column(length=25)
	private String conditionName;//条件值对应的名称
	@Column(length=5)
	private String logicOperator;//条件连接类型
	@Column(length=10)
	private String dataType;//字段数据类型
	@Column(length=10)
	private String leftBracket;//左括号
	@Column(length=10)
	private String rightBracket;//右括号
	private Integer displayIndex;//显示顺序
	private Long searchConditionId;//查询条件id
	@Column(length=150)
	private String enumName;//当dataType值为枚举类型时，该值有用
	@Column(length=10)
	private String position;//当dataType值为时间或日前类型时，该值有用，值为：first(开始时间)或second(结束时间)
	
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public String getDbFieldName() {
		return dbFieldName;
	}
	public void setDbFieldName(String dbFieldName) {
		this.dbFieldName = dbFieldName;
	}
	public String getFieldOperator() {
		return fieldOperator;
	}
	public void setFieldOperator(String fieldOperator) {
		this.fieldOperator = fieldOperator;
	}
	public String getConditionValue() {
		return conditionValue;
	}
	public void setConditionValue(String conditionValue) {
		this.conditionValue = conditionValue;
	}
	public String getConditionName() {
		return conditionName;
	}
	public void setConditionName(String conditionName) {
		this.conditionName = conditionName;
	}
	public String getLogicOperator() {
		return logicOperator;
	}
	public void setLogicOperator(String logicOperator) {
		this.logicOperator = logicOperator;
	}
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public String getLeftBracket() {
		return leftBracket;
	}
	public void setLeftBracket(String leftBracket) {
		this.leftBracket = leftBracket;
	}
	public String getRightBracket() {
		return rightBracket;
	}
	public void setRightBracket(String rightBracket) {
		this.rightBracket = rightBracket;
	}
	public Integer getDisplayIndex() {
		return displayIndex;
	}
	public void setDisplayIndex(Integer displayIndex) {
		this.displayIndex = displayIndex;
	}
	public Long getSearchConditionId() {
		return searchConditionId;
	}
	public void setSearchConditionId(Long searchConditionId) {
		this.searchConditionId = searchConditionId;
	}
	public String getEnumName() {
		return enumName;
	}
	public void setEnumName(String enumName) {
		this.enumName = enumName;
	}
	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}
}
