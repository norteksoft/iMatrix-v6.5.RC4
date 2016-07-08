package com.norteksoft.bs.options.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.orm.IdEntityNoExtendField;

/**
 * 国际化选项
 * @author liudongxia
 *
 */
@Entity
@Table(name="BS_INTERNATION_OPTION")
public class InternationOption extends IdEntityNoExtendField{
	private static final long serialVersionUID = 1L;
	private Long category;//语言种类,选项组中定义的，记录的是选项的id
	@Column(length=25)
	private String categoryName;//语言种类名称,选项组中定义的，记录的是选项的名称
	@Column(length=150)
	private String value;//值
	@ManyToOne
	@JoinColumn(name="FK_INTERNATION_ID")
	private Internation internation;
	
	@Transient
	private String viewName;
	
	public Long getCategory() {
		return category;
	}
	public void setCategory(Long category) {
		this.category = category;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public Internation getInternation() {
		return internation;
	}
	public void setInternation(Internation internation) {
		this.internation = internation;
	}
	public String getViewName() {
		return ApiFactory.getSettingService().getText(this.getCategoryName());
	}
	public void setViewName(String viewName) {
		this.viewName = viewName;
	}
	
}
