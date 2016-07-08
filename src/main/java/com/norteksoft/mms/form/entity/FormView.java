package com.norteksoft.mms.form.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.norteksoft.product.enumeration.DataState;
import com.norteksoft.product.orm.IdEntityNoExtendField;


/**
 * 表单视图
 * @author wurong
 */
@Entity
@Table(name="MMS_FORM_VIEW")
public class FormView extends IdEntityNoExtendField  implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	@Lob
    @Column(columnDefinition="LONGTEXT", nullable=true)
	private String html;
	
	private Integer version = 0;//如果版本号为0，则保存的时候需要重新生成版本号；否则，保持原来的版本
	
	@Enumerated(EnumType.STRING)
	private DataState formState;//表单的状态
	@Column(length=50)
	private String code;//编码
	@Column(length=50)
	private String name;//名称
	@ManyToOne
	@JoinColumn(name="FK_DATA_TABLE_ID")
	private DataTable dataTable;//数据表
	
	@Column(length=200)
	private String remark;//备注

	private Boolean standard=false;//是否是标准的视图
	
	@Column(name="FK_MENU_ID")
	private Long menuId;//菜单列表
	
	private Boolean deleted=false;//是否已删除
	
	/**
	 * 用于页面显示， 得到的是标准的html代码
	 */
	public String getHtml() {
		String formIdStr = this.getId()==null?"${formId}":this.getId().toString();
		return html==null?"":html.replace("&lt;", "<").replace("&gt;", ">").replace("${formId}", formIdStr);
	}

	/**
	 * 用于编辑器编辑，得到的是转义后的代码
	 */
	public String getHtmlCode(){
		return this.html;
	}
	/**
	 * 保证存在数据库里的html代码都是转义后的代码
	 * @param html
	 */
	public void setHtml(String html) {
		this.html = html==null?null:html.replace("<", "&lt;").replace(">", "&gt;");
	}
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}

	public DataState getFormState() {
		return formState;
	}

	public void setFormState(DataState formState) {
		this.formState = formState;
	}
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public DataTable getDataTable() {
		return dataTable;
	}

	public void setDataTable(DataTable dataTable) {
		this.dataTable = dataTable;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Boolean getStandard() {
		return standard;
	}

	public void setStandard(Boolean standard) {
		this.standard = standard;
	}

	public Long getMenuId() {
		return menuId;
	}

	public void setMenuId(Long menuId) {
		this.menuId = menuId;
	}

	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	/**
	 * 返回该表单是否为标准表单
	 */
	public Boolean isStandardForm(){
		return this.getStandard();
	}
	
	@Override
	public String toString() {
		if(getDataTable()==null){
			return "FormView["+this.getCode()+";"+this.getName()+"]";
		}else{
			return "FormView["+this.getCode()+";"+this.getName()+";"+this.getDataTable().getAlias()+";"+this.getDataTable().getName()+"]";
		}
	}
	
	@Override
	public FormView clone(){
		try {
			return (FormView) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			throw new RuntimeException("FormView clone failure");
		}
	}

}
