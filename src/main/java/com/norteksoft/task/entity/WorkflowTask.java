package com.norteksoft.task.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.entity.User;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.task.base.enumeration.TaskProcessingMode;
import com.norteksoft.task.base.enumeration.TaskProcessingResult;
import com.norteksoft.task.base.enumeration.TaskSource;

@Entity
@Table(name = "WORKFLOW_TASK")
//@XmlRootElement
//@XmlAccessorType(XmlAccessType.PROPERTY)
//@XmlType(name = "WorkflowTask")
public class WorkflowTask extends Task implements Cloneable{

	private static final long serialVersionUID = 1L;
	
	private String code;//环节编码
	
	private boolean effective = true; //任务是否有效，当环节被退回时，中间环节任务失效
	
	@Enumerated(EnumType.STRING)
	private TaskProcessingMode processingMode; //任务办理方式
	
	private boolean specialTask = false;//是否为特事特办任务， true为特事特办任务
	
	private Boolean distributable = false;//是不是分发的 ，true为分发的，分发的任务不会影响流程。办理人只需要查看。如果提交了任务就算完成
	
	@Enumerated(EnumType.STRING)
	private TaskProcessingResult taskProcessingResult;   //任务处理结果(办理意见  同意   不同意   放弃)
	
	private String processInstanceId;   //流程ID
	
	private String executionId;       	//
	
	private String trustor;         //委托人登录名
	private Long trustorId;         //委托人id
	private String trustorName;         //委托人名称
	
	private String nextTasks;         //后面环节
	//按钮重命名
	private String submitButton="提交";
	private String addSignerButton="加签";
	private String removeSignerButton="减签";
	private String agreeButton="同意";
	private String disagreeButton="不同意";
	private String signForButton="签收";
	private String approveButton="赞成";
	private String opposeButton="反对";
	private String abstainButton="弃权";
	private String assignButton="交办";
	
	private String saveButton="保存";
	private String appointButton="指派";
	private String copyButton="抄送";
	private String drawButton="领取";
	private String readButton="已阅";
	private String backButton="取回";
	private String abandonButton="放弃";
	private Boolean showButtonAddCounter=true;//是否显示‘加签’(办理方式：会签式)，默认是显示，true表示显示，false表示不显示
	private Boolean showButtonDelCounter=true;//是否显示‘减签’(办理方式：会签式)
	private Boolean showButtonKiken=true;//是否显示‘弃权’(办理方式：投票式)，默认是显示，true表示显示，false表示不显示
	private Boolean showButtonAppoint=true;//是否显示‘指派’，默认是显示，true表示显示，false表示不显示
	private Boolean showButtonCopy=true;//是否显示‘抄送’，默认是显示，true表示显示，false表示不显示
	private Boolean showButtonSave=true;//是否显示‘保存’，默认是显示，true表示显示，false表示不显示
	private Boolean showButtonDraw=true;//是否显示‘领取’，默认是显示，true表示显示，false表示不显示
	private Boolean showButtonGetBack=true;//是否显示‘取回’，默认是显示，true表示显示，false表示不显示
	private Boolean showButtonAbandon=true;//是否显示‘放弃’，默认是显示，true表示显示，false表示不显示
	private String remark; //扩展字段，bkyOA中用到
	private Integer groupNum;//第几次办理该环节
	
	
	private Boolean moreTransactor=false;//是否是多人办理环节
	private Boolean drawTask=false;//是否领取任务
	private String customType;//流程自定义类别
	private Boolean assignable=false;//是否是指派任务，任务委托监控中会用到 
	private TaskSource taskSource=TaskSource.NORMAL;//任务来源 
	
	private Boolean transferabled=false;//是否是移交任务
	private Long transferTaskId;//移交任务id，例如张三移交给李四，则该字段的值为张三的任务id
	@Column(length=50)
	private String transferName;//移交人姓名
	private Long transferId;//移交人id
	
	@Transient
	private String expands;  //扩展
	@Column(length=50)
	private String pushSign;  //推送标识（扩展字段，实施部“任务与门户系统”集成中用到）
	@Column(length=50)
	private String closeSign;  //关闭标识（扩展字段，实施部“任务与门户系统”集成中用到）
	
	public TaskProcessingMode getProcessingMode() {
		return processingMode;
	}

	public void setProcessingMode(TaskProcessingMode processingMode) {
		this.processingMode = processingMode;
	}

	public TaskProcessingResult getTaskProcessingResult() {
		return taskProcessingResult;
	}

	public void setTaskProcessingResult(TaskProcessingResult taskProcessingResult) {
		this.taskProcessingResult = taskProcessingResult;
	}

	public String getProcessInstanceId() {
		return processInstanceId;
	}

	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

	public String getExecutionId() {
		return executionId;
	}

	public void setExecutionId(String executionId) {
		this.executionId = executionId;
	}

	public String getTrustor() {
		return trustor;
	}

	public void setTrustor(String trustor) {
		this.trustor = trustor;
	}

	public String getNextTasks() {
		return nextTasks;
	}

	public void setNextTasks(String nextTasks) {
		this.nextTasks = nextTasks;
	}
	
	public Boolean getDistributable() {
		return distributable;
	}

	public void setDistributable(Boolean distributable) {
		this.distributable = distributable;
	}

	public boolean isEffective() {
		return effective;
	}

	public void setEffective(boolean effective) {
		this.effective = effective;
	}

	public boolean isSpecialTask() {
		return specialTask;
	}

	public void setSpecialTask(boolean specialTask) {
		this.specialTask = specialTask;
	}

	public String getExpands() {
		return expands;
	}

	public void setExpands(String expands) {
		this.expands = expands;
	}

	public String getSubmitButton() {
		return submitButton;
	}

	public void setSubmitButton(String submitButton) {
		this.submitButton = submitButton;
	}

	public String getAgreeButton() {
		return agreeButton;
	}

	public void setAgreeButton(String agreeButton) {
		this.agreeButton = agreeButton;
	}

	public String getDisagreeButton() {
		return disagreeButton;
	}

	public void setDisagreeButton(String disagreeButton) {
		this.disagreeButton = disagreeButton;
	}

	public String getSignForButton() {
		return signForButton;
	}

	public void setSignForButton(String signForButton) {
		this.signForButton = signForButton;
	}

	public String getApproveButton() {
		return approveButton;
	}

	public void setApproveButton(String approveButton) {
		this.approveButton = approveButton;
	}

	public String getOpposeButton() {
		return opposeButton;
	}

	public void setOpposeButton(String opposeButton) {
		this.opposeButton = opposeButton;
	}

	public String getAbstainButton() {
		return abstainButton;
	}

	public void setAbstainButton(String abstainButton) {
		this.abstainButton = abstainButton;
	}

	public String getAssignButton() {
		return assignButton;
	}

	public void setAssignButton(String assignButton) {
		this.assignButton = assignButton;
	}

	public String getAddSignerButton() {
		return addSignerButton;
	}

	public void setAddSignerButton(String addSignerButton) {
		this.addSignerButton = addSignerButton;
	}

	public String getRemoveSignerButton() {
		return removeSignerButton;
	}

	public void setRemoveSignerButton(String removeSignerButton) {
		this.removeSignerButton = removeSignerButton;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getGroupNum() {
		return groupNum;
	}

	public void setGroupNum(Integer groupNum) {
		this.groupNum = groupNum;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Boolean getMoreTransactor() {
		return moreTransactor;
	}

	public void setMoreTransactor(Boolean moreTransactor) {
		this.moreTransactor = moreTransactor;
	}

	public Boolean getDrawTask() {
		return drawTask;
	}

	public void setDrawTask(Boolean drawTask) {
		this.drawTask = drawTask;
	}

	public String getCustomType() {
		return customType;
	}

	public void setCustomType(String customType) {
		this.customType = customType;
	}


	public Boolean getAssignable() {
		return assignable;
	}

	public void setAssignable(Boolean assignable) {
		this.assignable = assignable;
	}

	public String getTrustorName() {
		return trustorName;
	}

	public void setTrustorName(String trustorName) {
		this.trustorName = trustorName;
	}

	public TaskSource getTaskSource() {
		return taskSource;
	}

	public void setTaskSource(TaskSource taskSource) {
		this.taskSource = taskSource;
	}

	public Long getTrustorId() {
		return trustorId;
	}

	public void setTrustorId(Long trustorId) {
		this.trustorId = trustorId;
	}

	public String getSaveButton() {
		return saveButton;
	}

	public void setSaveButton(String saveButton) {
		this.saveButton = saveButton;
	}

	public String getAppointButton() {
		return appointButton;
	}

	public void setAppointButton(String appointButton) {
		this.appointButton = appointButton;
	}

	public String getCopyButton() {
		return copyButton;
	}

	public String getDrawButton() {
		return drawButton;
	}

	public void setDrawButton(String drawButton) {
		this.drawButton = drawButton;
	}

	public String getReadButton() {
		return readButton;
	}

	public void setReadButton(String readButton) {
		this.readButton = readButton;
	}

	public String getBackButton() {
		return backButton;
	}

	public void setBackButton(String backButton) {
		this.backButton = backButton;
	}

	public void setCopyButton(String copyButton) {
		this.copyButton = copyButton;
	}

	public String getAbandonButton() {
		return abandonButton;
	}

	public void setAbandonButton(String abandonButton) {
		this.abandonButton = abandonButton;
	}

	public Boolean getShowButtonAddCounter() {
		return showButtonAddCounter;
	}

	public void setShowButtonAddCounter(Boolean showButtonAddCounter) {
		this.showButtonAddCounter = showButtonAddCounter;
	}

	public Boolean getShowButtonDelCounter() {
		return showButtonDelCounter;
	}

	public void setShowButtonDelCounter(Boolean showButtonDelCounter) {
		this.showButtonDelCounter = showButtonDelCounter;
	}

	public Boolean getShowButtonKiken() {
		return showButtonKiken;
	}

	public void setShowButtonKiken(Boolean showButtonKiken) {
		this.showButtonKiken = showButtonKiken;
	}

	public Boolean getShowButtonAppoint() {
		return showButtonAppoint;
	}

	public void setShowButtonAppoint(Boolean showButtonAppoint) {
		this.showButtonAppoint = showButtonAppoint;
	}

	public Boolean getShowButtonCopy() {
		return showButtonCopy;
	}

	public void setShowButtonCopy(Boolean showButtonCopy) {
		this.showButtonCopy = showButtonCopy;
	}

	public Boolean getShowButtonSave() {
		return showButtonSave;
	}

	public void setShowButtonSave(Boolean showButtonSave) {
		this.showButtonSave = showButtonSave;
	}

	public Boolean getShowButtonDraw() {
		return showButtonDraw;
	}

	public void setShowButtonDraw(Boolean showButtonDraw) {
		this.showButtonDraw = showButtonDraw;
	}

	public Boolean getShowButtonGetBack() {
		return showButtonGetBack;
	}

	public void setShowButtonGetBack(Boolean showButtonGetBack) {
		this.showButtonGetBack = showButtonGetBack;
	}

	public Boolean getShowButtonAbandon() {
		return showButtonAbandon;
	}

	public void setShowButtonAbandon(Boolean showButtonAbandon) {
		this.showButtonAbandon = showButtonAbandon;
	}

	@Override
	public WorkflowTask clone(){
		try {
			return (WorkflowTask) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			throw new RuntimeException("WorkflowTask clone failure");
		}
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public String getTrustorSubCompanyName() {
		User user = ApiFactory.getAcsService().getUserById(trustorId);
		if(user!=null){
			return user.getSubCompanyName();
		}
		return ContextUtils.getCompanyName();
	}

	public Boolean getTransferabled() {
		return transferabled;
	}

	public void setTransferabled(Boolean transferabled) {
		this.transferabled = transferabled;
	}

	public Long getTransferTaskId() {
		return transferTaskId;
	}

	public void setTransferTaskId(Long transferTaskId) {
		this.transferTaskId = transferTaskId;
	}

	public String getTransferName() {
		return transferName;
	}

	public void setTransferName(String transferName) {
		this.transferName = transferName;
	}

	public Long getTransferId() {
		return transferId;
	}

	public void setTransferId(Long transferId) {
		this.transferId = transferId;
	}

	public String getPushSign() {
		return pushSign;
	}

	public void setPushSign(String pushSign) {
		this.pushSign = pushSign;
	}

	public String getCloseSign() {
		return closeSign;
	}

	public void setCloseSign(String closeSign) {
		this.closeSign = closeSign;
	}
	
}
