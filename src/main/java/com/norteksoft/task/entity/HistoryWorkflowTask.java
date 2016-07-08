package com.norteksoft.task.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.norteksoft.product.orm.IdEntityNoExtendField;
import com.norteksoft.task.base.enumeration.TaskProcessingMode;
import com.norteksoft.task.base.enumeration.TaskProcessingResult;
import com.norteksoft.task.base.enumeration.TaskSource;
import com.norteksoft.task.base.enumeration.TaskState;


@Entity	
@Table(name = "HISTORY_WORKFLOW_TASK")
public class HistoryWorkflowTask extends IdEntityNoExtendField   implements Serializable{
	private static final long serialVersionUID = 1L;
	@Column(length=50)
	private String code;//环节编码
	private boolean effective = true; //任务是否有效，当环节被退回时，中间环节任务失效
	@Enumerated(EnumType.STRING)
	private TaskProcessingMode processingMode; //任务办理方式
	private boolean specialTask = false;//是否为特事特办任务， true为特事特办任务
	private Boolean distributable = false;//是不是分发的 ，true为分发的，分发的任务不会影响流程。办理人只需要查看。如果提交了任务就算完成
	@Enumerated(EnumType.STRING)
	private TaskProcessingResult taskProcessingResult;   //任务处理结果(办理意见  同意   不同意   放弃)
	@Column(length=50)
	private String processInstanceId;   //流程ID
	@Column(length=50)
	private String executionId;       	//
	@Column(length=25)
	private String trustor;         //委托人登录名
	private Long trustorId;         //委托人id
	@Column(length=25)
	private String trustorName;         //委托人名称
	@Column(length=50)
	private String nextTasks;         //后面环节
	//按钮重命名
	@Column(length=25)
	private String submitButton="提交";
	@Column(length=25)
	private String addSignerButton="加签";
	@Column(length=25)
	private String removeSignerButton="减签";
	@Column(length=25)
	private String agreeButton="同意";
	@Column(length=25)
	private String disagreeButton="不同意";
	@Column(length=25)
	private String signForButton="签收";
	@Column(length=25)
	private String approveButton="赞成";
	@Column(length=25)
	private String opposeButton="反对";
	@Column(length=25)
	private String abstainButton="弃权";
	@Column(length=25)
	private String assignButton="交办";
	@Column(length=25)
	private String saveButton="保存";
	@Column(length=25)
	private String appointButton="指派";
	@Column(length=25)
	private String copyButton="抄送";
	@Column(length=25)
	private String drawButton="领取";
	@Column(length=25)
	private String readButton="已阅";
	@Column(length=25)
	private String backButton="取回";
	@Column(length=25)
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
	@Column(length=200)
	private String remark; //扩展字段，bkyOA中用到
	private Integer groupNum;//第几次办理该环节
	private Boolean moreTransactor=false;//是否是多人办理环节
	private Boolean drawTask=false;//是否领取任务
	@Column(length=50)
	private String customType;//流程自定义类别
	private Boolean assignable=false;//是否是指派任务，任务委托监控中会用到 
	private TaskSource taskSource=TaskSource.NORMAL;//任务来源 
	@Transient
	private String expands;  //扩展
	private Long transactorId;  //办理人id
	@Column(length=25)
	private String transactor;  //办理人登录名
	@Column(length=25)
	private String transactorName;//办理人姓名
	private Date transactDate;  //办理日期
	@Column(length=255)
	private String title; //任务标题
	@Column(length=150)
	private String url;   //任务打开链接
	private Integer active = TaskState.WAIT_TRANSACT.getIndex();   //任务状态 : 0:等待处理  1:等待设置办理人  2:任务完成  3:被取消 4:待领取 5：已指派  6：待选择环节
	@Column(name="IS_READ")
 	private Boolean read = false;     //是否已阅
	private TaskMark taskMark = TaskMark.CANCEL;
	@Column(length=150)
	private String groupName; //任务组，显示任务列表时按组排列
	private Boolean visible = true; //任务的可见性
	private Date lastReminderTime;//上次催办时间
	@Column(length=64)
	private String reminderStyle;//催办方式
	private Long duedate = 0l;//开始催办时限
	private Date lastTransactTime;//任务的最后办理时间
	@Column(length=10)
	private String reminderTimeWay = "day";//催办间隔的时间粒度。其值可以为day（天）或hour（小时），默认为day（天）
	@Column(name="urge_interval")
	private Long repeat = 0l;//催办间隔时间
	private Integer reminderLimitTimes = 0;//催办次数上限  0表示一直催办
	private Integer alreadyReminderTimes = 0;//已催办次数
	@Column(length=50)
	private String reminderNoticeStyle;//催办次数达到上限后，通知相关人员的方式
	@Column(length=50)
	private String category;//任务类型，当是流程任务时其值为 流程类别(流程类型名称)，普通任务时其值自己任意取
	@Transient
	private Boolean sendingMessage = false;//是否发送RTX消息设置
	
	
	@Column(name="is_workflow_task")
	private Boolean workflowTask=true;//是否是工作流相关的任务
	private Boolean paused=false;//实例是否暂停,true是暂停，false是正常
	@Column(length=150)
	private String name;   //任务名
	private Integer displayOrder=0;//排序字段，xtsoa需要
	private Long sourceTaskId;//原任务id
	@Column(length=50)
	private String subCompanyName;//所属分支机构名称
	private Boolean transferabled=false;//是否是移交任务
	private Long transferTaskId;//移交任务id，例如张三移交给李四，则该字段的值为张三的任务id
	@Column(length=50)
	private String transferName;//移交人姓名
	private Long transferId;//移交人id
	@Column(length=50)
	private String pushSign;  //推送标识（扩展字段，实施部“任务与门户系统”集成中用到）
	@Column(length=50)
	private String closeSign;  //关闭标识（扩展字段，实施部“任务与门户系统”集成中用到）
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public boolean isEffective() {
		return effective;
	}
	public void setEffective(boolean effective) {
		this.effective = effective;
	}
	public TaskProcessingMode getProcessingMode() {
		return processingMode;
	}
	public void setProcessingMode(TaskProcessingMode processingMode) {
		this.processingMode = processingMode;
	}
	public boolean isSpecialTask() {
		return specialTask;
	}
	public void setSpecialTask(boolean specialTask) {
		this.specialTask = specialTask;
	}
	public Boolean getDistributable() {
		return distributable;
	}
	public void setDistributable(Boolean distributable) {
		this.distributable = distributable;
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
	public String getTrustorName() {
		return trustorName;
	}
	public void setTrustorName(String trustorName) {
		this.trustorName = trustorName;
	}
	public String getNextTasks() {
		return nextTasks;
	}
	public void setNextTasks(String nextTasks) {
		this.nextTasks = nextTasks;
	}
	public String getSubmitButton() {
		return submitButton;
	}
	public void setSubmitButton(String submitButton) {
		this.submitButton = submitButton;
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
	public TaskSource getTaskSource() {
		return taskSource;
	}
	public void setTaskSource(TaskSource taskSource) {
		this.taskSource = taskSource;
	}
	public String getExpands() {
		return expands;
	}
	public void setExpands(String expands) {
		this.expands = expands;
	}
	public String getTransactor() {
		return transactor;
	}
	public void setTransactor(String transactor) {
		this.transactor = transactor;
	}
	public String getTransactorName() {
		return transactorName;
	}
	public void setTransactorName(String transactorName) {
		this.transactorName = transactorName;
	}
	public Date getTransactDate() {
		return transactDate;
	}
	public void setTransactDate(Date transactDate) {
		this.transactDate = transactDate;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Integer getActive() {
		return active;
	}
	public void setActive(Integer active) {
		this.active = active;
	}
	public Boolean getRead() {
		return read;
	}
	public void setRead(Boolean read) {
		this.read = read;
	}
	public TaskMark getTaskMark() {
		return taskMark;
	}
	public void setTaskMark(TaskMark taskMark) {
		this.taskMark = taskMark;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public Boolean getVisible() {
		return visible;
	}
	public void setVisible(Boolean visible) {
		this.visible = visible;
	}
	public Date getLastReminderTime() {
		return lastReminderTime;
	}
	public void setLastReminderTime(Date lastReminderTime) {
		this.lastReminderTime = lastReminderTime;
	}
	public String getReminderStyle() {
		return reminderStyle;
	}
	public void setReminderStyle(String reminderStyle) {
		this.reminderStyle = reminderStyle;
	}
	public Long getDuedate() {
		return duedate;
	}
	public void setDuedate(Long duedate) {
		this.duedate = duedate;
	}
	public Long getRepeat() {
		return repeat;
	}
	public void setRepeat(Long repeat) {
		this.repeat = repeat;
	}
	public Integer getReminderLimitTimes() {
		return reminderLimitTimes;
	}
	public void setReminderLimitTimes(Integer reminderLimitTimes) {
		this.reminderLimitTimes = reminderLimitTimes;
	}
	public Integer getAlreadyReminderTimes() {
		return alreadyReminderTimes;
	}
	public void setAlreadyReminderTimes(Integer alreadyReminderTimes) {
		this.alreadyReminderTimes = alreadyReminderTimes;
	}
	public String getReminderNoticeStyle() {
		return reminderNoticeStyle;
	}
	public void setReminderNoticeStyle(String reminderNoticeStyle) {
		this.reminderNoticeStyle = reminderNoticeStyle;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public Boolean getSendingMessage() {
		return sendingMessage;
	}
	public void setSendingMessage(Boolean sendingMessage) {
		this.sendingMessage = sendingMessage;
	}
	public Boolean getWorkflowTask() {
		return workflowTask;
	}
	public void setWorkflowTask(Boolean workflowTask) {
		this.workflowTask = workflowTask;
	}
	public Boolean getPaused() {
		return paused;
	}
	public void setPaused(Boolean paused) {
		this.paused = paused;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getDisplayOrder() {
		return displayOrder;
	}
	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}
	public Long getSourceTaskId() {
		return sourceTaskId;
	}
	public void setSourceTaskId(Long sourceTaskId) {
		this.sourceTaskId = sourceTaskId;
	}
	public Long getTrustorId() {
		return trustorId;
	}
	public void setTrustorId(Long trustorId) {
		this.trustorId = trustorId;
	}
	public Long getTransactorId() {
		return transactorId;
	}
	public void setTransactorId(Long transactorId) {
		this.transactorId = transactorId;
	}
	public String getSubCompanyName() {
		return subCompanyName;
	}
	public void setSubCompanyName(String subCompanyName) {
		this.subCompanyName = subCompanyName;
	}
	public Date getLastTransactTime() {
		return lastTransactTime;
	}
	public void setLastTransactTime(Date lastTransactTime) {
		this.lastTransactTime = lastTransactTime;
	}
	public String getReminderTimeWay() {
		return reminderTimeWay;
	}
	public void setReminderTimeWay(String reminderTimeWay) {
		this.reminderTimeWay = reminderTimeWay;
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
	public void setCopyButton(String copyButton) {
		this.copyButton = copyButton;
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
	/**
	 * 查看当前任务是否被完成、被取消或者被指派(这些状态下的任务是不能够再对表单操作的)
	 * @return 如果满足一种状态，返回true，否则返回false
	 */
	public boolean isCompleted(){
		return TaskState.COMPLETED.getIndex().equals(this.getActive())||TaskState.CANCELLED.getIndex().equals(this.getActive())||TaskState.ASSIGNED.getIndex().equals(this.getActive());
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
