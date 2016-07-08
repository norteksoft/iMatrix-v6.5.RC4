package com.norteksoft.wf.engine.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.norteksoft.product.orm.IdEntityNoExtendField;
import com.norteksoft.task.base.enumeration.TaskProcessingMode;

/**
 * 附件文件描述信息
 * @author wurong
 *
 */
@Entity
@Table(name="WF_ATTACHMENT")
public class WorkflowAttachment extends IdEntityNoExtendField implements Serializable{

	private static final long serialVersionUID = 1L;
	@Column(length=50)
	private String workflowId;//流程实例ID
	@Column(length=25)
	private String transactor;  // 办理人
	
//	private Date time;//上传附件时间
	
	@Enumerated(EnumType.STRING)
	private TaskProcessingMode taskMode;//环节的办理模式
	@Column(length=50)
	private String taskName; //上传环节的环节名字
	
	private Float fileSize;  //附件大小
	@Column(length=50)
	private String departmentName;//部门名
	@Column(length=10)
	private String fileType;//文件类型
	@Column(length=255)
	private String filePath;//文件路径
	@Column(length=50)
	private String customField;//自定义类别
	@Transient
	private Boolean deleteSetting;
	
	public String getDepartmentName() {
		return departmentName;
	}
	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public Long getTaskId() {
		return taskId;
	}
	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	private Long taskId;
	
	private String fileName;

	public TaskProcessingMode getTaskMode() {
		return taskMode;
	}

	public void setTaskMode(TaskProcessingMode taskMode) {
		this.taskMode = taskMode;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getWorkflowId() {
		return workflowId;
	}

	public void setWorkflowId(String workflowId) {
		this.workflowId = workflowId;
	}


	public Float getFileSize() {
		return fileSize;
	}

	public void setFileSize(Float fileSize) {
		this.fileSize = fileSize;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getTransactor() {
		return transactor;
	}
	public void setTransactor(String transactor) {
		this.transactor = transactor;
	}
	public String getCustomField() {
		return customField;
	}
	public void setCustomField(String customField) {
		this.customField = customField;
	}
	public Boolean getDeleteSetting() {
		return deleteSetting;
	}
	public void setDeleteSetting(Boolean deleteSetting) {
		this.deleteSetting = deleteSetting;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
}
