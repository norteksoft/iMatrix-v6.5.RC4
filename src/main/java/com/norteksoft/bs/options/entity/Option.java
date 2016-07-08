package com.norteksoft.bs.options.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.norteksoft.product.orm.IdEntityNoExtendField;

/**
 * 选项实体
 * @author hjc
 */
@Entity
@Table(name="BS_OPTION")
public class Option extends IdEntityNoExtendField implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ManyToOne(targetEntity=OptionGroup.class)
	@JoinColumn(name="FK_OPTION_GROUP_ID")
	private OptionGroup optionGroup; // 所属的选项组
	@Column(length=50)
	private String name; // 选项的名字 
	@Column(length=50)
	private String value; // 选项的值

	private Integer optionIndex = 100; // 选项出现的顺序
	
	private Boolean selected = false; // 是否默认被选中
	
	@Transient
	private String viewName; // 选项名字（国际化。前台显示用）
	
	public OptionGroup getOptionGroup() {
		return optionGroup;
	}

	public void setOptionGroup(OptionGroup optionGroup) {
		this.optionGroup = optionGroup;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Boolean getSelected() {
		return selected;
	}

	public void setSelected(Boolean selected) {
		this.selected = selected;
	}

	public Integer getOptionIndex() {
		return optionIndex;
	}

	public void setOptionIndex(Integer optionIndex) {
		this.optionIndex = optionIndex;
	}

	public String getViewName() {
		return viewName;
	}

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}
	
}
