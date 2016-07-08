package com.norteksoft.wf.engine.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import com.norteksoft.product.orm.IdEntityNoExtendField;
import com.norteksoft.task.base.enumeration.TaskProcessingMode;

/**
 * 审批意见
 */
@Entity
@Table(name = "WF_OPINION")
public class Opinion extends IdEntityNoExtendField implements Serializable {
	private static final long serialVersionUID = 1L;
	@Column(length=25)
	private String transactor;  // 办理人
	@Column(length=25)
	private String transactorName;  // 办理人姓名
	private Long transactorId;  // 办理人
	
	@Column(length=1000)
	private String opinion;     // 办理意见
	
	@Enumerated(EnumType.STRING)
	private TaskProcessingMode taskMode = TaskProcessingMode.TYPE_EDIT;//上传环节的办理模式
	@Column(length=50)
	private String taskName; //上传环节的环节名字
	@Column(length=50)
	private String workflowId; //流程实例id

	private Long taskId;
	@Column(length=50)
	private String departmentName;//部门名
	@Column(length=50)
	private String fileType;//文件类型
	@Column(length=50)
	private String customField;//自定义类别
	@Column(length=50)
	private String taskCode;//环节编码
	
	private Boolean delegateFlag=false;//委托标识，该意见是否是委托任务环节生成的意见
	@Column(length=50)
	private String controlId;//意见控件id
	@Column(length=25)
	private String opinionSign;//在表单意见控件和意见标签中被用做“签名”
	@Column(length=25)
	private String opinionDate;//在表单意见控件和意见标签中被用做“日期”
	private String conclusion;//在表单意见控件和意见标签中被用做“结论”

	public Long getTaskId() {
		return taskId;
	}
	
	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}
	public String getWorkflowId() {
		return workflowId;
	}

	public void setWorkflowId(String workflowId) {
		this.workflowId = workflowId;
	}
	
	public String getOpinion() {
		return opinion;
	}

	public void setOpinion(String opinion) {
		this.opinion = opinion;
	}

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

	public String getTransactor() {
		return transactor;
	}

	public void setTransactor(String transactor) {
		this.transactor = transactor;
	}

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

	public String getCustomField() {
		return customField;
	}

	public void setCustomField(String customField) {
		this.customField = customField;
	}

	public String getTaskCode() {
		return taskCode;
	}

	public void setTaskCode(String taskCode) {
		this.taskCode = taskCode;
	}

	public Boolean getDelegateFlag() {
		return delegateFlag;
	}

	public void setDelegateFlag(Boolean delegateFlag) {
		this.delegateFlag = delegateFlag;
	}

	public Long getTransactorId() {
		return transactorId;
	}

	public void setTransactorId(Long transactorId) {
		this.transactorId = transactorId;
	}

	public String getTransactorName() {
		return transactorName;
	}

	public void setTransactorName(String transactorName) {
		this.transactorName = transactorName;
	}

	public String getControlId() {
		return controlId;
	}

	public void setControlId(String controlId) {
		this.controlId = controlId;
	}

	public String getOpinionSign() {
		return opinionSign;
	}

	public void setOpinionSign(String opinionSign) {
		this.opinionSign = opinionSign;
	}

	public String getOpinionDate() {
		return opinionDate;
	}

	public void setOpinionDate(String opinionDate) {
		this.opinionDate = opinionDate;
	}

	public String getConclusion() {
		return conclusion;
	}

	public void setConclusion(String conclusion) {
		this.conclusion = conclusion;
	}

}
