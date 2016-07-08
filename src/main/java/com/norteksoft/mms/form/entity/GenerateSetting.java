package com.norteksoft.mms.form.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.norteksoft.mms.form.enumeration.TemplateEnum;
import com.norteksoft.product.orm.IdEntityNoExtendField;

/**
 * 代码生成设置
 * @author Administrator
 *
 */
@Entity
@Table(name="MMS_GENERATE_SETTING")
public class GenerateSetting extends IdEntityNoExtendField  implements Serializable{
	private static final long serialVersionUID = 1L;
	private Boolean entitative=true;//是否生成实体
	private Boolean flowable=false;//是否走流程
	private Long tableId;//对应的数据表
	@Column(length=50)
	private String workflowCode;//对应的流程编码
	@Enumerated(EnumType.STRING)
	private TemplateEnum templateEnum;
	@Transient
	private String menuName;//列表对应的菜单
	public Boolean getEntitative() {
		return entitative;
	}
	public void setEntitative(Boolean entitative) {
		this.entitative = entitative;
	}
	public Boolean getFlowable() {
		return flowable;
	}
	public void setFlowable(Boolean flowable) {
		this.flowable = flowable;
	}
	public String getMenuName() {
		return menuName;
	}
	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}
	public Long getTableId() {
		return tableId;
	}
	public void setTableId(Long tableId) {
		this.tableId = tableId;
	}
	public String getWorkflowCode() {
		return workflowCode;
	}
	public void setWorkflowCode(String workflowCode) {
		this.workflowCode = workflowCode;
	}
	public TemplateEnum getTemplateEnum() {
		return templateEnum;
	}
	public void setTemplateEnum(TemplateEnum templateEnum) {
		this.templateEnum = templateEnum;
	}
	
}
