package com.norteksoft.acs.entity.authority;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.norteksoft.product.orm.IdEntityNoExtendField;
/**
 * 被锁数据表
 * @author admin
 *
 */
@Entity
@Table(name="ACS_RECORD_LOCKER")
public class RecordLocker extends IdEntityNoExtendField implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String entityName;//被编辑实体的名称
	private Long entityId;//被编辑数据的id
	private String editorName;//正在编辑数据人员的名称
	private Date editTime;//开始编辑数据的时间
	private String editorLoginName;//正在编辑数据人员的登录名
	private String editorCompanyCode;//正在编辑数据人员的公司编码
	
	public String getEntityName() {
		return entityName;
	}
	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}
	public Long getEntityId() {
		return entityId;
	}
	public void setEntityId(Long entityId) {
		this.entityId = entityId;
	}
	public String getEditorName() {
		return editorName;
	}
	public void setEditorName(String editorName) {
		this.editorName = editorName;
	}
	public Date getEditTime() {
		return editTime;
	}
	public void setEditTime(Date editTime) {
		this.editTime = editTime;
	}
	public String getEditorLoginName() {
		return editorLoginName;
	}
	public void setEditorLoginName(String editorLoginName) {
		this.editorLoginName = editorLoginName;
	}
	public String getEditorCompanyCode() {
		return editorCompanyCode;
	}
	public void setEditorCompanyCode(String editorCompanyCode) {
		this.editorCompanyCode = editorCompanyCode;
	}
}
