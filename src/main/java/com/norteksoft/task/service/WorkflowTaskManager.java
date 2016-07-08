package com.norteksoft.task.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.api.ProcessEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import rtx.RtxMsgSender;

import com.norteksoft.acs.service.AcsUtils;
import com.norteksoft.acs.service.authorization.AcsApiManager;
import com.norteksoft.acs.service.organization.UserManager;
import com.norteksoft.mms.form.entity.FormView;
import com.norteksoft.mms.form.service.FormViewManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.entity.User;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.AsyncMailUtils;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.PropUtils;
import com.norteksoft.product.util.SystemUrls;
import com.norteksoft.product.web.struts2.Struts2Utils;
import com.norteksoft.task.base.enumeration.TaskCategory;
import com.norteksoft.task.base.enumeration.TaskProcessingMode;
import com.norteksoft.task.base.enumeration.TaskProcessingResult;
import com.norteksoft.task.base.enumeration.TaskState;
import com.norteksoft.task.dao.HistoryWorkflowTaskDao;
import com.norteksoft.task.dao.TaskDao;
import com.norteksoft.task.dao.WorkflowTaskDao;
import com.norteksoft.task.entity.HistoryWorkflowTask;
import com.norteksoft.task.entity.Task;
import com.norteksoft.task.entity.WorkflowTask;
import com.norteksoft.task.webservice.WorkflowTaskService;
import com.norteksoft.wf.base.enumeration.InstanceHistoryType;
import com.norteksoft.wf.base.utils.WebUtil;
import com.norteksoft.wf.engine.core.DefinitionXmlParse;
import com.norteksoft.wf.engine.core.impl.UserParseCalculator;
import com.norteksoft.wf.engine.entity.InstanceHistory;
import com.norteksoft.wf.engine.entity.WorkflowDefinition;
import com.norteksoft.wf.engine.entity.WorkflowInstance;
import com.norteksoft.wf.engine.entity.WorkflowType;
import com.norteksoft.wf.engine.service.DelegateMainManager;
import com.norteksoft.wf.engine.service.InstanceHistoryManager;
import com.norteksoft.wf.engine.service.WorkflowDefinitionManager;
import com.norteksoft.wf.engine.service.WorkflowInstanceManager;
import com.norteksoft.wf.engine.service.WorkflowTypeManager;

//@WebService(endpointInterface = "com.norteksoft.task.webservice.WorkflowTaskService")
@Service
@Transactional
public class WorkflowTaskManager implements WorkflowTaskService{
	private Log log = LogFactory.getLog(WorkflowTaskManager.class);
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	//流转历史常量
	private static final String COMMA = ", ";
	private static final String DELTA_START = "[ ";
	private static final String DELTA_END = " ]";
	private static final String DELEGATE_INF = "${workflow.history.delegate.info}";
	private static final String ASSIGN_INF = "${workflow.history.assign.info}";
	private static final String ASSIGN_CURRENT_INF = "${workflow.history.assign.current.info}";
	private WorkflowTaskDao workflowTaskDao;
	private TaskDao taskDao;
	private UserManager userManager;
	private ProcessEngine processEngine;
	private DelegateMainManager delegateManager;
	private WorkflowDefinitionManager workflowDefinitionManager;
	private InstanceHistoryManager instanceHistoryManager;
	private HistoryWorkflowTaskDao historyWorkflowTaskDao;
	private FormViewManager formViewManager;
	
	@Autowired
	public void setFormViewManager(FormViewManager formManager) {
		this.formViewManager = formManager;
	}
	
	@Autowired
	public void setInstanceHistoryManager(
			InstanceHistoryManager instanceHistoryManager) {
		this.instanceHistoryManager = instanceHistoryManager;
	}
	@Autowired
	private WorkflowInstanceManager workflowInstanceManager;
	@Autowired
	private WorkflowTypeManager workflowTypeManager;
	@Autowired
	public void setUserManager(UserManager userManager) {
		this.userManager = userManager;
	}
	@Autowired
	public void setWorkflowTaskDao(WorkflowTaskDao workflowTaskDao) {
		this.workflowTaskDao = workflowTaskDao;
	}
	@Autowired
	public void setTaskDao(TaskDao taskDao) {
		this.taskDao = taskDao;
	}
	@Autowired
	public void setProcessEngine(ProcessEngine processEngine) {
		this.processEngine = processEngine;
	}
	@Autowired
	public void setDelegateManager(DelegateMainManager delegateManager) {
		this.delegateManager = delegateManager;
	}
	@Autowired
	public void setWorkflowDefinitionManager(
			WorkflowDefinitionManager workflowDefinitionManager) {
		this.workflowDefinitionManager = workflowDefinitionManager;
	}
	@Autowired
	public void setHistoryWorkflowTaskDao(
			HistoryWorkflowTaskDao historyWorkflowTaskDao) {
		this.historyWorkflowTaskDao = historyWorkflowTaskDao;
	}
	public Page<WorkflowTask> getDelegateTasks(
			Long companyId, String loginName,Long userId, Page<WorkflowTask> page){
		return workflowTaskDao.getDelegateTasks(companyId, loginName,userId, page);
	}
	
	public Page<WorkflowTask> getDelegateTasksByActive(
			Long companyId, String loginName,Long userId, Page<WorkflowTask> page, boolean isEnd){
		return workflowTaskDao.getDelegateTasks(companyId, loginName,userId, page, isEnd);
	}
	
	public Page<WorkflowTask> getTaskAsTrustee(
			Long companyId, String loginName,Long userId, Page<WorkflowTask> page, boolean isEnd){
		return workflowTaskDao.getTaskAsTrustee(companyId, loginName,userId, page, isEnd);
	}

	public List<WorkflowTask> getAllTasksByInstance(Long companyId, String instanceId){
		return workflowTaskDao.getAllTasksByInstance(companyId, instanceId);
	}
	
	public Integer getDelegateTasksNum(Long companyId, String loginName,Long userId){
		return workflowTaskDao.getDelegateTasksNum(companyId, loginName,userId);
	}
	
	public Integer getDelegateTasksNumByActive(Long companyId, String loginName,Long userId, Boolean isCompleted){
		if(isCompleted){
			Integer currentNum =  workflowTaskDao.getDelegateTasksNum(companyId, loginName,userId, isCompleted);
			Integer histNum = historyWorkflowTaskDao.getDelegateTasksNum(companyId, loginName,userId);
			if(currentNum == null)currentNum = 0;
			if(histNum == null)histNum = 0;
			return currentNum+histNum;
		}else{
			return  workflowTaskDao.getDelegateTasksNum(companyId, loginName,userId, isCompleted);
		}
	}
	
	public Integer getTrusteeTasksNum(Long companyId, String loginName,Long userId, Boolean isCompleted){
		if(isCompleted){
			Integer currentNum = workflowTaskDao.getTrusteeTasksNum(companyId, loginName,userId, isCompleted);
			Integer histNum = historyWorkflowTaskDao.getTrusteeTasksNum(companyId, loginName,userId);
			if(currentNum == null)currentNum = 0;
			if(histNum == null)histNum = 0;
			return currentNum+histNum;
		}else{
			return  workflowTaskDao.getTrusteeTasksNum(companyId, loginName,userId, isCompleted);
		}
		
	}
	
	/**
     * 流程被手动结束时，强制结束流程实例的当前任务
     */
	@Transactional(readOnly=false)
    public void endTasks(String instanceId,Long companyId){
    	log.debug("*** endTasks 方法开始");
		log.debug(new StringBuilder("*** Received parameter:[")
				.append("instanceId:").append(instanceId)
				.append(", companyId:").append(companyId)
				.append("]").toString());
    	
    	List<WorkflowTask> tasks = getActivityTasks(instanceId,companyId);
    	for(WorkflowTask task:tasks){
    		task.setActive(TaskState.CANCELLED.getIndex());
    		saveTask(task);
    	}
    	
    	log.debug("*** endTasks 方法结束");
    }
	/**
	 * 流程被强制结束时，强制结束流程实例的当前任务
	 */
	@Transactional(readOnly=false)
	public void compelEndTasks(String instanceId,Long companyId){
		log.debug("*** endTasks 方法开始");
		log.debug(new StringBuilder("*** Received parameter:[")
		.append("instanceId:").append(instanceId)
		.append(", companyId:").append(companyId)
		.append("]").toString());
		
		List<WorkflowTask> tasks = getActivityTasks(instanceId,companyId);
		for(WorkflowTask task:tasks){
			task.setActive(TaskState.COMPLETED.getIndex());
			saveTask(task);
		}
		
		log.debug("*** endTasks 方法结束");
	}
    
    /**
     * 活动该流程实例的当前任务
     */
    public List<WorkflowTask> getActivityTasks(String instanceId,Long companyId){
    	log.debug("*** getActivityTasks 方法开始");
		log.debug(new StringBuilder("*** Received parameter:[")
				.append("instanceId:").append(instanceId)
				.append(", companyId:").append(companyId)
				.append("]").toString());
		
    	List<WorkflowTask> tasks = workflowTaskDao.getActivityTasks(instanceId,companyId);
    	
    	log.debug("*** getActivityTasks 方法结束");
    	return tasks;
    }
    /**
	 * 活动该流程实例当前任务,当前任务为待领取或待办理且该任务不是分发给的任务也不是特事特办任务，且任务是有效的（不包括当前子流程任务，即任务办理人姓名不为空就是非子流程任务）
	 * @param instanceId 实例id
	 * @param companyId 公司id
	 * @return 任务列表
	 */
	public List<WorkflowTask> getActivityTasksWithoutSubProcessTasks(String instanceId,Long companyId) {
		return workflowTaskDao.getActivityTasksWithoutSubProcessTasks(instanceId, companyId);
	}
	
    
    /**
	 * 活动该流程实例当前子流程任务。即所有当前任务中办理人姓名为空的任务就是当前子流程任务
	 * @param instanceId 实例id
	 * @param companyId 公司id
	 * @return 任务列表
	 */
	public List<WorkflowTask> getActivitySubProcessTasks(String instanceId,Long companyId) {
		return workflowTaskDao.getActivitySubProcessTasks(instanceId, companyId);
	}
    
    public List<Long> getActivityTaskTransactorIds(String instanceId,Long companyId) {
    	return workflowTaskDao.getActivityTaskTransactorIds(instanceId, companyId);
    }
    /**
     * 查询该流程实例的当前任务中需要根据流程实例的催办设置来催办的任务列表
     */
    public List<WorkflowTask> getActivityReminderTasksByInstance(String instanceId,Long companyId){
    	log.debug("*** getActivityReminderTasksByInstance 方法开始");
    	log.debug(new StringBuilder("*** Received parameter:[")
    	.append("instanceId:").append(instanceId)
    	.append(", companyId:").append(companyId)
    	.append("]").toString());
    	
    	List<WorkflowTask> tasks = workflowTaskDao.getActivityReminderTasksByInstance(instanceId,companyId);
    	
    	log.debug("*** getActivityReminderTasksByInstance 方法结束");
    	return tasks;
    }
    
    /**
     * 活动该流程实例的当前任务
     */
    public List<WorkflowTask> getActivitySignTasks(String instanceId,Long companyId){
    	log.debug("*** getActivityTasks 方法开始");
		log.debug(new StringBuilder("*** Received parameter:[")
				.append("instanceId:").append(instanceId)
				.append(", companyId:").append(companyId)
				.append("]").toString());
		
    	List<WorkflowTask> tasks = workflowTaskDao.getActivitySignTasks(instanceId,companyId);
    	
    	log.debug("*** getActivityTasks 方法结束");
    	return tasks;
    }
    
    /**
     * 查询办理人的当前任务
     * @param instanceId
     * @param companyId
     * @param loginName
     * @return
     */
    public WorkflowTask getMyTask(String instanceId,Long companyId,String loginName){
    	log.debug("*** getMyTask 方法开始");
		log.debug(new StringBuilder("*** Received parameter:[")
				.append("instanceId:").append(instanceId)
				.append(", companyId:").append(companyId)
				.append(", loginName:").append(loginName)
				.append("]").toString());
		
    	WorkflowTask task = workflowTaskDao.getMyTask(instanceId, companyId, loginName);
    	
    	log.debug("*** getMyTask 方法结束");
    	return task;
    }
    /**
     * 查询办理人的当前任务
     * @param instanceId
     * @param companyId
     * @param loginName
     * @return
     */
    public WorkflowTask getMyTask(String instanceId,Long companyId,Long userId){
    	log.debug("*** getMyTask 方法开始");
    	log.debug(new StringBuilder("*** Received parameter:[")
    	.append("instanceId:").append(instanceId)
    	.append(", companyId:").append(companyId)
    	.append(", userId:").append(userId)
    	.append("]").toString());
    	
    	WorkflowTask task = workflowTaskDao.getMyTask(instanceId, companyId, userId);
    	
    	log.debug("*** getMyTask 方法结束");
    	return task;
    }
    
	/**
	 * 删除该流程实例中的所有任务。
	 * @param processInstanceId
	 * @param companyId
	 */
    @Transactional(readOnly=false)
	public void deleteTaskByProcessId(String processInstanceId,Long companyId) {
		workflowTaskDao.deleteTaskByProcessId(processInstanceId,companyId);
	}
	
	/**
	 * 保存工作流任务
	 * @param wfTask
	 */
    @Transactional(readOnly=false)
	public void saveTask(WorkflowTask wfTask){
		workflowTaskDao.save(wfTask);
		if(wfTask.getSendingMessage() && ApiFactory.getAcsService().isRtxEnable()) {
			String url=null;
			if(StringUtils.isEmpty(wfTask.getUrl()))return;
			if(wfTask.getUrl().contains("?")){
				url=SystemUrls.getSystemUrl(StringUtils.substringBefore(wfTask.getUrl(), "/"))+StringUtils.substring(wfTask.getUrl(), wfTask.getUrl().indexOf('/'))+wfTask.getId();
			}else{
				url=SystemUrls.getSystemUrl(StringUtils.substringBefore(wfTask.getUrl(), "/"))+StringUtils.substring(wfTask.getUrl(), wfTask.getUrl().indexOf('/'))+"?taskId="+wfTask.getId();
			}
			RtxMsgSender.sendNotify(wfTask.getTransactor(),
					"新任务-"+wfTask.getName(), 
					"1", 
					"你有一个新任务："+wfTask.getName(),
					url,getCompanyId());
		}
	}
	
	public Long getCompanyId(){
		return ContextUtils.getCompanyId();
	}
	
	/**
	 * 领取任务
	 * @param taskId
	 */
	@Transactional(readOnly=false)
	public String receive(String taskIds){
		String[] taskIdStr = taskIds.split(",");
		int successNum=0;
		int failNum=0;
		for(int i=0;i<taskIdStr.length;i++){
	    	log.debug("*** receive 方法开始");
			log.debug(new StringBuilder("*** Received parameter:[")
			.append("taskId:").append(taskIdStr[i])
			.append("]").toString());
	    	Long taskId = Long.parseLong(taskIdStr[i]);
			WorkflowTask task = workflowTaskDao.get(taskId);
			if(task.getActive().equals(TaskState.DRAW_WAIT.getIndex())){
				List<WorkflowTask> tasks = workflowTaskDao.getNotCompleteTasksByName(getCompanyId(), task.getProcessInstanceId(), task.getName());
				for(WorkflowTask tsk : tasks){
					if(taskId.equals(tsk.getId())){
						successNum++;
						task.setDrawTask(true);
						tsk.setActive(TaskState.WAIT_TRANSACT.getIndex());//待办理
					}else{
						tsk.setActive(TaskState.HAS_DRAW_OTHER.getIndex());//已领取
					}
				}
			}
		}
		failNum=taskIdStr.length-successNum;
		log.debug("*** receive 方法结束");
		return Struts2Utils.getText("taskReceiveMessage", new String[]{successNum+"",failNum+""});
	}
	/**
	 * 放弃领取的任务
	 * @return
	 */
	@Transactional(readOnly=false)
	public String abandonReceive(Long taskId){
		log.debug("*** abandonReceiveTask 方法开始");
		log.debug(new StringBuilder("*** Received parameter:[")
		.append("taskId:").append(taskId)
		.append("]").toString());
		List<Long> transactorIds = new ArrayList<Long>();
		List<Long> trustorIds = new ArrayList<Long>();
		WorkflowTask task = workflowTaskDao.get(taskId);
		if(task.getDrawTask()){
			task.setDrawTask(false);
			task.setActive(TaskState.DRAW_WAIT.getIndex());
			if(StringUtils.isNotEmpty(task.getTrustor())){//是否是委托任务
				trustorIds.add(task.getTrustorId());
			}else{
				transactorIds.add(task.getTransactorId());
			}
		}
		String msg = "";
		List<WorkflowTask> tasks = workflowTaskDao.getHasDrawOtherTasks(getCompanyId(), task.getProcessInstanceId(), task.getName());
		for(WorkflowTask tsk : tasks){
			if(tsk.getAssignable()){//是否是指派任务，是指派任务时原封还原
				tsk.setActive(TaskState.DRAW_WAIT.getIndex());
			}else{
				if(StringUtils.isNotEmpty(tsk.getTrustor())){//是否是委托任务,是委托任务
					if(!trustorIds.contains(tsk.getTrustorId())){//过滤重复的用户
						trustorIds.add(tsk.getTrustorId());
						tsk.setActive(TaskState.DRAW_WAIT.getIndex());
					}
				}else{//不是委托任务
					if(!transactorIds.contains(tsk.getTransactorId())){//过滤重复的用户
						transactorIds.add(tsk.getTransactorId());
						tsk.setActive(TaskState.DRAW_WAIT.getIndex());
					}
				}
			}
			//设置该任务对应的消息的状态为未读
			ApiFactory.getPortalService().setMessageReadedByTaskId(tsk.getId(),true);
		}
		msg = "task.abandon.receive.success";
		log.debug("*** abandonReceiveTask 方法结束");
		return msg;
	}
	
	@Transactional(readOnly=false)
	public void assigns(String taskIds,Long transactorId){
		if(StringUtils.isNotEmpty(taskIds)){
			User transactorUser = ApiFactory.getAcsService().getUserById(transactorId);
			if(transactorUser!=null){
				String[] taskIdStrs = taskIds.split(",");
				for(String idStr:taskIdStrs){
					assignTask(Long.parseLong(idStr), transactorUser);
					
					//指派任务的业务补偿
					assignTransactorSet(String.valueOf(transactorId), Long.parseLong(idStr));
				}
			}
		}
	}
	
	/**
	 * 指派任务给指定的人员
	 * @param taskId
	 * @param transactor
	 */
	@Deprecated
	@Transactional(readOnly=false)
	public void assign(Long taskId, String transactor){
		log.debug("*** assign 方法开始");
		log.debug(new StringBuilder("*** Received parameter:[")
		.append("taskId:").append(taskId)
		.append(", transactor:").append(transactor)
		.append("]").toString());
		
		//<s:if test="task.processingMode != '编辑式' && task.processingMode != '交办式' && task.active!=2" >
		User transactorUser = ApiFactory.getAcsService().getUserByLoginName(transactor);
		if(transactorUser!=null){
			assignTask(taskId,transactorUser);
		}
		log.debug("*** assign 方法结束");
	}
    /**
     * 指派任务给指定的人员
     * @param taskId
     * @param transactor
     */
	@Transactional(readOnly=false)
    public void assign(Long taskId, Long transactorId){
    	log.debug("*** assign 方法开始");
		log.debug(new StringBuilder("*** Received parameter:[")
		.append("taskId:").append(taskId)
		.append(", transactorId:").append(transactorId)
		.append("]").toString());
    	
		User transactorUser = ApiFactory.getAcsService().getUserById(transactorId);
    	//<s:if test="task.processingMode != '编辑式' && task.processingMode != '交办式' && task.active!=2" >
		assignTask(taskId,transactorUser);
    	
    	log.debug("*** assign 方法结束");
    }
	 public void assignTask(Long taskId, User transactorUser){
		WorkflowTask task = workflowTaskDao.get(taskId);
		//设置任务对应的消息为已读
		ApiFactory.getPortalService().setMessageReadedByTaskId(taskId,false);
		//被指派人的新任务
		WorkflowTask targetTask = task.clone();
		//设置指派人任务已完成
		task.setTaskProcessingResult(null);
		task.setActive(TaskState.ASSIGNED.getIndex());
		try {
			SimpleDateFormat time_format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			task.setTransactDate(time_format.parse(time_format.format(new Date())));
		} catch (Exception e) {
			e.printStackTrace();
		}
		task.setNextTasks("assign to:" + transactorUser.getId());
		//新任务办理人
		targetTask.setId(null);
		targetTask.setAssignable(true);
		//任务委托设置
		WorkflowInstance wi = workflowInstanceManager.getWorkflowInstance(task.getProcessInstanceId());
		String processId= "";
		if(wi!=null) processId= wi.getProcessDefinitionId();
		User delegateUser = delegateManager.getDelegateUser(
				task.getCompanyId(), null,transactorUser.getId(), processId, task.getName());
		
		WorkflowDefinition wfDef = workflowDefinitionManager.getWorkflowDefinitionByProcessId(processId);
		//流程类型
		WorkflowType type=null;
		if(wfDef!=null){//修改了流程名称或类型后需要重新赋值
			targetTask.setGroupName(wfDef.getName());
			targetTask.setCustomType(wfDef.getCustomType());
			//流程类型
			type=workflowTypeManager.getWorkflowType(wfDef.getTypeId());
			if(type!=null)targetTask.setCategory(type.getName());
		}
		
		if(delegateUser != null){
			targetTask.setTrustor(transactorUser.getLoginName());
			targetTask.setTrustorId(transactorUser.getId());
			targetTask.setTrustorName(transactorUser.getName());
			targetTask.setTransactor(delegateUser.getLoginName());
			targetTask.setTransactorId(delegateUser.getId());
			targetTask.setTransactorName(delegateUser.getName());
			targetTask.setSubCompanyId(transactorUser.getSubCompanyId());
			targetTask.setSubCompanyName(transactorUser.getSubCompanyName());
		}else{
			targetTask.setTransactor(transactorUser.getLoginName());
			targetTask.setTransactorId(transactorUser.getId());
			targetTask.setTransactorName(transactorUser.getName());
			targetTask.setSubCompanyId(transactorUser.getSubCompanyId());
			targetTask.setSubCompanyName(transactorUser.getSubCompanyName());
		}
		targetTask.setRead(false);
		//在移交的任务上再指派时要变成普通的指派任务
		targetTask.setTransferTaskId(null);
		targetTask.setTransferName(null);
		targetTask.setTransferId(null);
		
		Map<String,String > reminderSetting = DefinitionXmlParse.getReminderSetting(processId, targetTask.getName());
		//设置催办信息
		setTaskRemindInfo(targetTask,reminderSetting,wi);
		List<WorkflowTask> tasks = new ArrayList<WorkflowTask>();
		tasks.add(task);
		tasks.add(targetTask);
		saveTasks(tasks);
		
		//发送消息
		sendMessage(targetTask,type,"指派");
		//发送邮件
		sendMail(task,processId);
		//生成流转历史
		generateAssignHistory(task,transactorUser,targetTask,delegateUser);
	}
	 
	 public void setTaskRemindInfo(WorkflowTask task,Map<String,String > reminderSetting,WorkflowInstance wi){
		 clearRemindInfo(task);//清空task的催办信息，再重新设置
		 if(StringUtils.isNotEmpty(reminderSetting.get(DefinitionXmlParse.REMIND_STYLE))||StringUtils.isNotEmpty(wi.getReminderStyle())){//环节中设置的催办方式不为空或流程属性设置的催办方式不为空，才生成催办信息
				if(StringUtils.isNotEmpty(reminderSetting.get(DefinitionXmlParse.REMIND_STYLE))){
					task.setReminderStyle(reminderSetting.get(DefinitionXmlParse.REMIND_STYLE));
				}else{
					task.setReminderStyle(wi.getReminderStyle());
				}
				if(StringUtils.isNotEmpty(reminderSetting.get(DefinitionXmlParse.REMIND_REPEAT))){
					task.setRepeat(Long.valueOf(reminderSetting.get(DefinitionXmlParse.REMIND_REPEAT)));
				}else{
					task.setRepeat(wi.getRepeat());
				}
				if(StringUtils.isNotEmpty(reminderSetting.get(DefinitionXmlParse.REMIND_TIME_WAY))){
					task.setReminderTimeWay(reminderSetting.get(DefinitionXmlParse.REMIND_TIME_WAY));
				}else{
					task.setReminderTimeWay(wi.getReminderTimeWay());
				}
				if(StringUtils.isNotEmpty(reminderSetting.get(DefinitionXmlParse.REMIND_DUEDATE))){
					task.setDuedate(Long.valueOf(reminderSetting.get(DefinitionXmlParse.REMIND_DUEDATE)));
				}else{
					task.setDuedate(wi.getDuedate());
				}
				task.setAlreadyReminderTimes(0);
				task.setLastReminderTime(null);
				//设置任务最后办理时间
				setLastTransactTime(task);
				if(StringUtils.isNotEmpty(reminderSetting.get(DefinitionXmlParse.REMIND_TIME))){
					task.setReminderLimitTimes(Integer.valueOf(reminderSetting.get(DefinitionXmlParse.REMIND_TIME)));
				}else{
					task.setReminderLimitTimes(wi.getReminderLimitTimes());
				}
				if(StringUtils.isNotEmpty(reminderSetting.get(DefinitionXmlParse.REMIND_NOTICE_TYPE))){
					task.setReminderNoticeStyle(reminderSetting.get(DefinitionXmlParse.REMIND_NOTICE_TYPE));
				}else{
					task.setReminderNoticeStyle(wi.getReminderNoticeStyle());
				}
			}
	 }
	 
	 public void clearRemindInfo(WorkflowTask targetTask){
		targetTask.setAlreadyReminderTimes(0);
		targetTask.setReminderStyle(null);
		targetTask.setRepeat(0l);
		targetTask.setReminderTimeWay("day");
		targetTask.setDuedate(0l);
		targetTask.setLastReminderTime(null);
		targetTask.setReminderLimitTimes(0);
		targetTask.setReminderNoticeStyle(null);
		targetTask.setLastTransactTime(null);
	 }
	 
	 public String parseUserCondition(WorkflowTask task,String userCondition,WorkflowInstance instance){
			Set<Long> set = new HashSet<Long>();
			StringBuilder builder = new StringBuilder();
			WorkflowDefinition definition = workflowDefinitionManager.getWfDefinition(instance.getWorkflowDefinitionId());
			FormView form = formViewManager.getFormView(instance.getFormId());
			UserParseCalculator upc = new UserParseCalculator();
			upc.setDataId(instance.getDataId());
			upc.setFormView(form);
			upc.setDocumentCreator(instance.getCreator());
			upc.setDocumentCreatorId(instance.getCreatorId());
			if(task!=null){
				upc.setCurrentTransactor(task.getTransactor());
				upc.setCurrentTransactorId(task.getTransactorId());
			}
			upc.setProcessAdmin(definition.getAdminLoginName());
			upc.setProcessAdminId(definition.getAdminId());
			set.addAll(upc.getUsers(userCondition,instance.getSystemId(),instance.getCompanyId()));
			for(Long uid:set){
				builder.append(uid).append(",");
			}
			return  StringUtils.removeEnd(builder.toString(), ",");
		}
	 
	 /**
	 * 设置任务最后办理时间
	 * @param task
	 */
	public void setLastTransactTime(WorkflowTask task){
		long duedateTime = 0l;//将办理时限换算成毫秒数
		long worktimes = ApiFactory.getSettingService().getWorkTimes(task.getSubCompanyId());//每天的工作时间换算成的毫秒数
		String[] workTimeSettting = ApiFactory.getSettingService().getWorkTimeSetting(task.getSubCompanyId());
		String startTime = workTimeSettting[0];
		String endTime = workTimeSettting[1];
		Date startDate = ApiFactory.getSettingService().getWorkDate(task.getCreatedTime(),startTime);
		Date endDate =  ApiFactory.getSettingService().getWorkDate(task.getCreatedTime(),endTime);
		duedateTime = task.getDuedate()*worktimes;//将办理时限换算成毫秒数
		boolean isHoliday = ApiFactory.getSettingService().isHolidayDay(task.getCreatedTime(),task.getSubCompanyId());
		if(isHoliday){//任务是否是在节假日生成的，true表示是节假日
			setLastTransactTimeWhenHolidayAndEnd(task,task.getCreatedTime(),worktimes,duedateTime,false,endTime);
		}else{//生成任务是在工作日生成的：分成3种情况，1 开始上班前 2 上班中 3 下班后
			//将任务生成时间的秒、毫秒清除
			Calendar cal = Calendar.getInstance();
			cal.setTime(task.getCreatedTime());
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			Date createTime = cal.getTime();//日期 时:分：00:00，例如：2014-12-10 10:20
			if(createTime.getTime()<startDate.getTime()){//上班前生成的
				cal = Calendar.getInstance();
				cal.setTime(createTime);
				cal.add(Calendar.DAY_OF_YEAR, -1);//上班前生成的和昨天下班后生成的处理一样
				Date date = cal.getTime();
				setLastTransactTimeWhenHolidayAndEnd(task,date,worktimes,duedateTime,true,endTime);
			}else if(createTime.getTime()>=startDate.getTime()&&createTime.getTime()<=endDate.getTime()){//上班中
				if(createTime.getTime()==endDate.getTime()){//表示是下班时生成的,临界点
					setLastTransactTimeWhenHolidayAndEnd(task,createTime,worktimes,duedateTime,false,endTime);
				}else{//表示是上班时的临界点 和 上班和下班时间之间生成的任务
					setLastTransactTimeWhenWorkDate(task,createTime,worktimes,duedateTime,endDate,startTime,endTime);
				}
			}else if(createTime.getTime()>endDate.getTime()){//下班后
				setLastTransactTimeWhenHolidayAndEnd(task,createTime,worktimes,duedateTime,false,endTime);
			}
		}
		
	}
	/**
	 * 设置最后办理时间，当任务是节假日生成的 或 任务是下班后生成的 或 任务是下班时生成的处理 或 任务是上班和下班临界点生成的
	 * @param task
	 * @param creatTime
	 * @param worktimes
	 * @param duedateTime
	 * @param workDateFlag
	 * @param endWorkTime 工作结束时间。 例如：18:00
	 */
	private void setLastTransactTimeWhenHolidayAndEnd(WorkflowTask task,Date creatTime,long worktimes,long duedateTime,boolean workDateFlag,String endWorkTime){
		Calendar cal = Calendar.getInstance();
		cal.setTime(creatTime);
		long i=0;
		while(true){
			cal.add(Calendar.DAY_OF_YEAR, 1);
			Date date = cal.getTime();
			if(workDateFlag||!ApiFactory.getSettingService().isHolidayDay(date,task.getSubCompanyId())){//工作日标识为true或判断当前时间是否是工作日
				i=i+worktimes;
				if(i==duedateTime){//最后办理时间
					task.setLastTransactTime(ApiFactory.getSettingService().getWorkDate(date, endWorkTime));//设置最后办理时间
					break;
				}
			}
		}
	}
	/**
	 * 设置最后办理时间，当任务是工作日生成的处理
	 * @param task
	 * @param createTime
	 * @param worktimes
	 * @param duedateTime
	 * @param workEndTime
	 * @param startWorkTime 工作开始时间。例如：09:00
	 * @param endWorkTime 工作结束时间。例如：18:00
	 */
	private void setLastTransactTimeWhenWorkDate(WorkflowTask task,Date createTime,long worktimes,long duedateTime,Date workEndTime,String startWorkTime,String endWorkTime){
		long createTimeToEndTime = workEndTime.getTime()-createTime.getTime();//任务生成时间和当天工作时间结束之间的毫秒差值
		if(createTimeToEndTime==duedateTime){//生成任务时间为工作时间的开始时间，例如：工作时间为09:00~18:00,而生成任务时间正好是09:00,则最后办理时间为今天的18:00
			task.setLastTransactTime(ApiFactory.getSettingService().getWorkDate(createTime, endWorkTime));//设置最后办理时间
			return;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(createTime);
		long i=createTimeToEndTime;
		while(true){
			cal.add(Calendar.DAY_OF_YEAR, 1);
			Date date = cal.getTime();
			if(!ApiFactory.getSettingService().isHolidayDay(date,task.getSubCompanyId())){//判断当前时间date是否是工作日
				i=i+worktimes;//任务时间差+每天工作时间豪秒值
				if(i==duedateTime){//生成任务时间为工作时间的开始时间，例如：工作时间为09:00~18:00,而生成任务时间正好是09:00,则最后办理时间为今天的18:00
					task.setLastTransactTime(ApiFactory.getSettingService().getWorkDate(date, endWorkTime));//设置最后办理时间
					break;
				}else if(i<duedateTime){//小于时间办理时限，例如办理时限为2天，i的值可能为1.5天，此时会走该分支
					continue;
				}else{//大于时间办理时限，例如办理时限为2天，i的值可能为2.5天，此时会走该分支
					//最后办理时间肯定是在date这一天
					long beforeDateTime = i-worktimes;//获得今天之前的所有毫秒数
					long transactTimeMill=ApiFactory.getSettingService().getWorkDate(date, startWorkTime).getTime()+(duedateTime-beforeDateTime);//获得办理时间毫秒值
					task.setLastTransactTime(new Date(transactTimeMill));
					
					break;
				}
			}
		}
	}
	
	public void assignTransactorSet(String transactors,Long taskId ){
		WorkflowTask task = workflowTaskDao.get(taskId);
		WorkflowInstance wi = workflowInstanceManager.getWorkflowInstance(task.getProcessInstanceId());
		assignTransactorSet(wi,transactors,taskId);
	}
	
	/**
	 * 指派任务的业务补偿
	 * @param instance
	 * @param form
	 */
	public void assignTransactorSet(WorkflowInstance instance,String transactors,Long taskId){
		Map<String,String> assignTransactorSet = DefinitionXmlParse.getAssignTransactorSet(instance.getProcessDefinitionId());
		String setType=assignTransactorSet.get(DefinitionXmlParse.SET_TYPE);
		String assignTransactorSetUrl=assignTransactorSet.get(DefinitionXmlParse.ASSIGN_TRANSACTOR);
		if(StringUtils.isNotEmpty(assignTransactorSetUrl)){
			String systemCode=WebUtil.getSystemCodeByDef(instance.getProcessDefinitionId());
			if(setType.equals("http")){
				WebUtil.getHttpConnectionTransactor(assignTransactorSetUrl,ContextUtils.getCompanyId(),instance.getDataId(),systemCode,transactors,taskId);
			}else if(setType.equals("RESTful")){
				WebUtil.restfulTransactor(assignTransactorSetUrl,ContextUtils.getCompanyId(),instance.getDataId(),systemCode,transactors,taskId);
			}
		}
	}
	
	/**
	 *生成流转历史
	 * @param task
	 * @param transactor
	 */
	private void generateAssignHistory(WorkflowTask task, User transactorUser,WorkflowTask delegateTask,User delegateUser) {
		StringBuilder historyMessage = new StringBuilder();
		String inforType = "";
		AcsApiManager acsApiManager = (AcsApiManager)ContextUtils.getBean("acsApiManager");
		boolean hasBranch = acsApiManager.hasBranch(ContextUtils.getCompanyId());
		User currentUser = ApiFactory.getAcsService().getUserById(ContextUtils.getUserId());
		String userName = currentUser.getName();
		if(hasBranch){
			userName = userName+"("+currentUser.getSubCompanyName()+")";
		}
		
		String transactorUserName = transactorUser.getName();
		if(hasBranch){
			transactorUserName = transactorUserName+"("+transactorUser.getSubCompanyName()+")";
		}
		if(delegateUser!=null){//生成指派和委托的流转历史
			User trustorUser = ApiFactory.getAcsService().getUserById(delegateTask.getTrustorId());
			inforType = "delegateAndAssgin";
			String trustorUserName = trustorUser.getName();
			if(hasBranch){
				trustorUserName = trustorUserName+"("+trustorUser.getSubCompanyName()+")";
			}
			
			String delegateTransactor = delegateTask.getTransactorName();
			if(hasBranch){
				delegateTransactor = delegateTransactor+"("+delegateUser.getSubCompanyName()+")";
			}
			
			historyMessage.append(trustorUserName)
			.append(DELEGATE_INF)
			.append(delegateTransactor).append("。\n")
			.append(dateFormat.format(new Date()))
			.append(COMMA).append(userName).append(ASSIGN_CURRENT_INF)
			.append(transactorUserName).append(DELTA_START)
			.append(task.getName()).append(DELTA_END).append("\n");
		}else{//生成指派流转历史
			historyMessage.append(dateFormat.format(new Date()))
			.append(COMMA).append(userName).append(ASSIGN_INF)
			.append(transactorUserName).append(DELTA_START)
			.append(task.getName()).append(DELTA_END).append("\n");
		}
		
		InstanceHistory history = new InstanceHistory(task.getCompanyId(), task.getProcessInstanceId(), InstanceHistory.TYPE_TASK, historyMessage.toString(), task.getName());
		history.setEffective(false);
		history.setCreatedTime(new Date());
		history.setExecutionId(task.getProcessInstanceId());
		
		if("delegateAndAssgin".equals(inforType)){//委托+指派
			history.setHistoryType(InstanceHistoryType.HISTORY_DELEGATE_AND_ASSIGN);
		}else{//指派
			history.setHistoryType(InstanceHistoryType.HISTORY_ASSIGN);
		}
		
		
        instanceHistoryManager.saveHistory(history);
		
	}
	/**
	 * 消息提醒
	 * @param task
	 * @param type
	 */
	private void sendMessage(WorkflowTask task,WorkflowType type,String customType){
		if(StringUtils.isNotEmpty(task.getTransactor())){//子流程时Transactor为null
			try {
				if(StringUtils.isNotEmpty(customType)){
					ApiFactory.getPortalService().addMessage("task", ContextUtils.getUserName(), ContextUtils.getUserId(), task.getTransactorId(),type==null?customType+":待办任务":customType+":"+type.getName(), task.getTitle(), "/task/message-task.htm?id="+task.getId(),true,"task-"+task.getId()+"-"+task.getProcessInstanceId());
				}else{
					ApiFactory.getPortalService().addMessage("task", ContextUtils.getUserName(), ContextUtils.getUserId(), task.getTransactorId(),type==null?"待办任务":type.getName(), task.getTitle(), "/task/message-task.htm?id="+task.getId(),true,"task-"+task.getId()+"-"+task.getProcessInstanceId());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void sendMail(WorkflowTask task,String processId){
		
		 if(StringUtils.isNotEmpty(task.getTransactor())){//子流程时Transactor为null
			 try{
			        boolean isMailNotice=DefinitionXmlParse.isMailNotice(processId, task.getName());
			        if(isMailNotice){
			            String mailContent=PropUtils.getProp("mail.properties", "task.notice.content");
			            if(StringUtils.isNotEmpty(mailContent)){
			                mailContent=mailContent.replace("${url}", getTaskUrl(task));
			            }
		            	com.norteksoft.product.api.entity.User user=ApiFactory.getAcsService().getUserByLoginName(task.getTransactor());
		            	if(user==null){
		            		throw new RuntimeException("用户不存在："+task.getTransactor());
		            	}else if(StringUtils.isEmpty(user.getEmail())){
		            		throw new RuntimeException("用户邮件地址没有输入："+task.getTransactor());
		            	}else{
		            		AsyncMailUtils.sendMail(user.getEmail(), task.getTitle(),mailContent );
		            	}
			        }
			    }catch (Exception e) {
			        e.printStackTrace();
		            log.error(PropUtils.getExceptionInfo(e));
		        }
		 }
	}
	
	/**
	 * 批量保存Task
	 * @param tasks
	 */
	@Transactional(readOnly=false)
	public void saveTasks(List<WorkflowTask> tasks){
		for(WorkflowTask task : tasks){
			saveTask(task);
		}
	}
	

	public List<String> getParticipantsTransactor(Long companyId,
			String instanceId) {
		return workflowTaskDao.getParticipantsTransactor(companyId, instanceId);
	}
	public List<Long> getParticipantsTransactorId(Long companyId,
			String instanceId) {
		return workflowTaskDao.getParticipantsTransactorId(companyId, instanceId);
	}
	
	/**
	 * 查询task
	 * @param id
	 * @return
	 */
	public WorkflowTask getTask(Long id){
		return workflowTaskDao.getTask(id);
	}
	
	/**
	 * 删除Task
	 * @param task
	 */
	@Transactional(readOnly=false)
	public void deleteTask(WorkflowTask task){
		workflowTaskDao.delete(task);
	}
	
	/**
	 * 根据环节名称获得任务
	 * @param companyId
	 * @param instanceId
	 * @param names
	 * @return
	 */
	public List<Long> getTaskIdsByName(Long companyId, String instanceId, String[] taskNames){
		log.debug("*** getTasksByName 方法");
		log.debug(new StringBuilder("*** Received parameter:[")
				.append("companyId:").append(companyId)
				.append(", instanceId:").append(instanceId)
				.append(", taskName:").append(taskNames)
				.append("]").toString());
		return workflowTaskDao.getTaskIdsByName(companyId, instanceId, taskNames);
	}
	
	/**
	 * 根据名称删除Task
	 * @param companyId
	 * @param instanceId
	 * @param taskName
	 */
	@Transactional(readOnly=false)
	public void deleteTasksByName(Long companyId, String instanceId, String[] taskName){
    	log.debug("*** deleteTasksByName 方法开始");
		log.debug(new StringBuilder("*** Received parameter:[")
				.append("companyId:").append(companyId)
				.append(", instanceId:").append(instanceId)
				.append(", taskName:").append(taskName)
				.append("]").toString());
		
		workflowTaskDao.deleteTasksByName(companyId, instanceId, taskName);
		
    	log.debug("*** deleteTasksByName 方法结束");
	}
	
	/**
	 * 查询流程实例的第一个任务
	 * @param companyId
	 * @param instanceId
	 * @param transactor
	 * @return
	 */
	public WorkflowTask getFirstTaskByInstance(Long companyId, String instanceId, String transactor,Long userId) {
		return workflowTaskDao.getFirstTaskByInstance(companyId, instanceId, transactor,userId);
	}
	/**
	 * 根据流程名字和实例id查询workflowTask
	 * @param instanceId
	 * @param taskName
	 * @return
	 */
	public List<WorkflowTask> getWorkflowTasks(String instanceId, String taskName) {
		return workflowTaskDao.getWorkflowTasks(instanceId, taskName);
	}
	
	/**
	 * 查询流程实例所有已经生成的任务
	 * @param companyId
	 * @param instanceId
	 * @return
	 */
	public List<String> getTaskNamesByInstance(Long companyId, String instanceId){
    	log.debug("*** getTaskNamesByInstance 方法开始");
		log.debug(new StringBuilder("*** Received parameter:[")
		.append("companyId:").append(companyId)
		.append(", instanceId:").append(instanceId)
		.append("]").toString());
		
		List<WorkflowTask> tasks = workflowTaskDao.find("from WorkflowTask wft where wft.companyId=? and wft.processInstanceId=? and wft.specialTask=? and (wft.active=? or wft.active=? or wft.active=? or wft.active=?)", 
				companyId, instanceId, false,TaskState.WAIT_TRANSACT.getIndex(), TaskState.WAIT_DESIGNATE_TRANSACTOR.getIndex(),TaskState.DRAW_WAIT.getIndex(),TaskState.WAIT_CHOICE_TACHE.getIndex());
		List<String> result = new ArrayList<String>();
		for(WorkflowTask task : tasks){
			if(!result.contains(task.getName())){
				result.add(task.getName());
			}
		}
		
		log.debug("*** getTaskNamesByInstance 方法结束");
		return result;
	}
	
	/**
	 * 查询所有任务
	 * @return
	 */
	public List<Task> getAllTasks(){
		return taskDao.getAll();
	}
	
	public List<WorkflowTask> getTasksByActivity(Long companyId, String executionId, String taskName){
		return taskDao.find("from WorkflowTask wft where wft.companyId=? and wft.executionId=? and wft.name=? and wft.active = 0 and wft.distributable=0", 
				companyId, executionId, taskName);
	}
	
	
	/**
	 * 查询用户所有未完成任务(不是分页)
	 * @param page
	 */
	public List<WorkflowTask> getAllTasksByUser(Long companyId, String loginName){
		return taskDao.find("from Task t where t.companyId = ? and t.transactor = ?  and t.visible = true and (t.active=? or t.active=? or t.active=?  or t.active=?) order by t.groupName,t.createdTime desc", 
				companyId, loginName,TaskState.DRAW_WAIT.getIndex(), TaskState.WAIT_TRANSACT.getIndex(), TaskState.WAIT_DESIGNATE_TRANSACTOR.getIndex(),TaskState.WAIT_CHOICE_TACHE.getIndex());
	}
	/**
	 * 查询用户所有未完成任务(不是分页)
	 * @param page
	 */
	public List<WorkflowTask> getAllTasksByUser(Long companyId, Long userId){
		return taskDao.find("from Task t where t.companyId = ? and  (t.transactorId is not null and t.transactorId=?) and t.visible = true and (t.active=? or t.active=? or t.active=?  or t.active=?) order by t.groupName,t.createdTime desc", 
				companyId, userId,TaskState.DRAW_WAIT.getIndex(), TaskState.WAIT_TRANSACT.getIndex(), TaskState.WAIT_DESIGNATE_TRANSACTOR.getIndex(),TaskState.WAIT_CHOICE_TACHE.getIndex());
	}
	
	/**
	 * 分页查询用户已完成任务
	 * @param companyId
	 * @param loginName
	 * @param page
	 */
	public void getReadTasksByUser(Long companyId, String loginName,Long userId, Page<WorkflowTask> page) {
		workflowTaskDao.getReadTasksByUser(companyId,loginName,userId,page);
	}
	
	/**
	 * 分页查询用户已完成任务
	 * @param companyId
	 * @param loginName
	 * @param page
	 */
	public void getCompletedTasksByUser(Long companyId, String loginName,Long userId, Page<WorkflowTask> page) {
		workflowTaskDao.getCompletedTasksByUser(companyId, loginName,userId, page);
	}
	
	
	/**
	 * 根据任务名称查询任务
	 * @param instanceId
	 * @param name
	 * @return
	 */
	public List<WorkflowTask> getTasksByName(Long companyId, String instanceId, String name){
		return workflowTaskDao.getTasksByName(companyId, instanceId, name);
	}


	/**
	 * 根据任务名称查询任务,不含指派
	 * @param instanceId
	 * @param name
	 * @return
	 */
	public List<WorkflowTask> getNoAssignTasksByName(Long companyId, String instanceId, String taskName,Integer groupNum) {
		return workflowTaskDao.getNoAssignTasksByName(companyId, instanceId, taskName,groupNum);
	}
	
	/**
	 * 根据办理人查询办理人未完成任务
	 * @param companyId
	 * @param stransactor
	 * @return
	 */
	public List<Task> getTasksByTransactor(Long companyId, String stransactor){
		return null;
	}
	
	/**
	 * 返回对应办理模式的所有环节
	 * @param processInstanceId
	 * @param processingMode
	 * @return
	 */
	public List<String> getCountersignByProcessInstanceId(String processInstanceId,TaskProcessingMode processingMode){
		return workflowTaskDao.getCountersignByProcessInstanceId(processInstanceId, processingMode);
	}
	
	/**
	 * 自定义流程中取会签环节名称
	 */
	public List<String> getSignByProcessInstanceId(String processInstanceId,TaskProcessingMode processingMode){
		return workflowTaskDao.getSignByProcessInstanceId(processInstanceId, processingMode);
	}
	/**
	 * 根据办理结果查询环节
	 */
	public List<WorkflowTask> getCountersignByProcessInstanceIdResult(String processInstanceId,String taskName,TaskProcessingResult result){
		return workflowTaskDao.getCountersignByProcessInstanceIdResult(processInstanceId, taskName, result);
	}
	/**
	 * 获得审批任务组数
	 * @param processInstanceId
	 * @param taskName
	 * @param result
	 * @return
	 */
	public List<Integer> getGroupNumByTaskName(String processInstanceId,String taskName){
		return workflowTaskDao.getGroupNumByTaskName(processInstanceId, taskName);
	}
	
	@Transactional(readOnly=false)
	public void deleteWorkflowTask(List<Long> ids) {
		workflowTaskDao.deleteTaskByIds(ids);
	}
	public List<WorkflowTask> getCountersigns(Long id) {
		WorkflowTask wt = workflowTaskDao.get(id);
		return workflowTaskDao.getCountersigns(wt.getProcessInstanceId(), wt.getName());
	}
	
	public List<WorkflowTask> getProcessCountersigns(Long id) {
		WorkflowTask wt = workflowTaskDao.get(id);
		return workflowTaskDao.getCountersigns(id,wt.getProcessInstanceId(), wt.getName());
	}
	
	public List<String> getCountersignsHandler(Long id,Integer handlingState){
		WorkflowTask wt = workflowTaskDao.get(id);
		if(wt==null){throw new RuntimeException("获得会签环节的会签办理人时任务不能为null");}
		return workflowTaskDao.getCountersignsHandler(wt.getProcessInstanceId(), wt.getName(),handlingState);
	}
	@Transactional(readOnly=false)
	public void deleteCountersignHandler(Long taskId, Collection<String> users) {
		WorkflowTask wt = workflowTaskDao.get(taskId);
		if(wt==null){throw new RuntimeException("减签时任务不能为null");}
		workflowTaskDao.deleteCountersignHandler(wt.getProcessInstanceId(), wt.getName(),users);
	}
	@Transactional(readOnly=false)
	public void deleteSignHandler(Long taskId, Collection<Long> userIds) {
		WorkflowTask wt = workflowTaskDao.get(taskId);
		if(wt==null){throw new RuntimeException("减签时任务不能为null");}
		workflowTaskDao.deleteSignHandler(wt.getProcessInstanceId(), wt.getName(),userIds);
	}
	@Transactional(readOnly=false)
	public void deleteSignHandlerByTaskId(Collection<Long> taskIds) {
		workflowTaskDao.deleteSignHandlerByTaskId( taskIds);
	}
    
	public Set<String> getHandledTransactors(String workflowId) {
		Set<String> result = new HashSet<String>();
		List<String> transactors = workflowTaskDao.getHandledTransactors(workflowId);
		if(transactors.size()<=0){
			transactors = historyWorkflowTaskDao.getHandledTransactors(workflowId);
		}
		result.addAll(transactors);
		return result;
	}
	
	public Set<Long> getHandledTransactorIds(String workflowId){
		Set<Long> result = new HashSet<Long>();
		List<Long> transactors = workflowTaskDao.getHandledTransactorIds(workflowId);
		if(transactors.size()<=0){
			transactors = historyWorkflowTaskDao.getHandledTransactorIds(workflowId);
		}
		result.addAll(transactors);
		return result;
	}
	
	public Set<String> getAllHandleTransactors(String workflowId) {
		Set<String> result = new HashSet<String>();
		List<String> transactors = workflowTaskDao.getAllHandleTransactors(workflowId);
		if(transactors.size()<=0){
			transactors = historyWorkflowTaskDao.getAllHandleTransactors(workflowId);
		}
		result.addAll(transactors);
		return result;
	}
	public Set<Long> getAllHandleTransactorIds(String workflowId) {
		Set<Long> result = new HashSet<Long>();
		List<Long> transactors = workflowTaskDao.getAllHandleTransactorIds(workflowId);
		if(transactors.size()<=0){
			transactors = historyWorkflowTaskDao.getAllHandleTransactorIds(workflowId);
		}
		result.addAll(transactors);
		return result;
	}
	
	/**
	 * 得到所有需要催办的task
	 */
	public List<WorkflowTask> getNeedReminderTasks(){
		return workflowTaskDao.getNeedReminderTasks();
	}
	

	/**
	 * 获得 已完成的任务
	 */
	public List<WorkflowTask> getCompletedTasks(String workflowId,
			Long companyId) {
		return workflowTaskDao.getCompletedTasks( workflowId,
				 companyId);
	}
	
	/**
	 * 查询任务
	 * @param tasks
	 * @param names
	 * @param values
	 */
	public void searchTask(Page<Task> tasks, List<String> names, List<String> values, String finish){
		if(Boolean.valueOf(finish)){
			taskDao.findFinishTaskForPage(tasks, names, values);
		}else{
			taskDao.findUNFinishTaskForPage(tasks, names, values);
		}
	}
	public List<WorkflowTask> getNeedReminderTasks(String loginName,Long userId,
			Long companyId) {
		return workflowTaskDao.getNeedReminderTasks(loginName,userId,companyId);
	}
	public List<WorkflowTask> getTasksOrderByWdfName(String definitionName,
			String loginName) {
		return workflowTaskDao.getTasksOrderByWdfName(definitionName, loginName);
	}
	public List<WorkflowTask> getTasksOrderByWdfName(String definitionName,
			Long userId) {
		return workflowTaskDao.getTasksOrderByWdfName(definitionName, userId);
	}
	public List<WorkflowTask> getCompletedTasksByTaskName(String workflowId,
			Long companyId,String taskName){
		return workflowTaskDao.getCompletedTasksByTaskName(workflowId,companyId,taskName);
	}
	/**
	 * 根据当前用户查询未完成任务总数
	 * @param companyId 公司id
	 * @param loginName 当前用户登录名
	 * @return 未完成任务总数
	 */
	public Integer getTasksNumByTransactor(Long companyId, String loginName){
		return workflowTaskDao.getNotCompleteTasksNumByTransactor(companyId, loginName);
	}
	/**
	 * 根据当前用户查询未完成任务总数
	 * @param companyId 公司id
	 * @param loginName 当前用户登录名
	 * @return 未完成任务总数
	 */
	public Integer getTasksNumByTransactor(Long companyId, Long userId){
		return workflowTaskDao.getNotCompleteTasksNumByTransactor(companyId, userId);
	}
	
	/**
	 * 查找公司中所有的超期任务
	 * @param companyId
	 * @return
	 */
	public List<WorkflowTask> getOverdueTasks(Long companyId) {
		return workflowTaskDao.getOverdueTasks(companyId);
	}
	
	/**
	 * 查找当前办理人所有的超期任务的总数
	 * @param companyId
	 * @param transactorName
	 * @return
	 */
	public Map<String,Integer> getOverdueTasksNumByTransactor(Long companyId) {
		List<WorkflowTask> list=workflowTaskDao.getOverdueTasks(companyId);
		Map<String,Integer> map=new HashMap<String,Integer>();
		for(WorkflowTask task:list){
			map.put(task.getTransactor(), workflowTaskDao.getOverdueTasksNumByTransactor(companyId, task.getTransactor(),task.getTransactorId()));
		}
		return map;
	}
	
	/**
	 * 查找公司中所有的超期任务,包括已完成的任务
	 * @param companyId
	 * @return
	 */
	public List<WorkflowTask> getTotalOverdueTasks(Long companyId) {
		return workflowTaskDao.getTotalOverdueTasks(companyId);
	}
	/**
	 * 查找当前办理人所有的超期任务的总数,包括已完成的任务
	 * @param companyId
	 * @param transactorName
	 * @return
	 */
	public Map<String,Integer> getTotalOverdueTasksNumByTransactor(Long companyId) {
		List<WorkflowTask> list=workflowTaskDao.getTotalOverdueTasks(companyId);
		Map<String,Integer> map=new HashMap<String,Integer>();
		for(WorkflowTask task:list){
			map.put(task.getTransactor(), workflowTaskDao.getTotalOverdueTasksNumByTransactor(companyId, task.getTransactor()));
		}
		List<HistoryWorkflowTask> histlist=historyWorkflowTaskDao.getTotalOverdueTasks(companyId);
		for(HistoryWorkflowTask task:histlist){
			Integer taskNum = map.get(task.getTransactor());
			if(taskNum==null||taskNum.equals(0)){
				map.put(task.getTransactor(), historyWorkflowTaskDao.getTotalOverdueTasksNumByTransactor(companyId, task.getTransactor()));
			}else{
				map.put(task.getTransactor(), taskNum+historyWorkflowTaskDao.getTotalOverdueTasksNumByTransactor(companyId, task.getTransactor()));
			}
		}
		return map;
	}
	/**
	 * 查找当前办理人所有的超期任务的总数,包括已完成的任务
	 * @param companyId
	 * @param transactorName
	 * @return Map<Long,Integer>:key 办理人id，value该办理人对应的过期任务
	 */
	public Map<Long,Integer> getTotalOverdueTasksNumByTransactorId(Long companyId) {
		List<WorkflowTask> list=workflowTaskDao.getTotalOverdueTasks(companyId);
		Map<Long,Integer> map=new HashMap<Long,Integer>();
		for(WorkflowTask task:list){
			map.put(task.getTransactorId(), workflowTaskDao.getTotalOverdueTasksNumByTransactorId(companyId, task.getTransactorId()));
		}
		List<HistoryWorkflowTask> histlist=historyWorkflowTaskDao.getTotalOverdueTasks(companyId);
		for(HistoryWorkflowTask task:histlist){
			Integer taskNum = map.get(task.getTransactor());
			if(taskNum==null||taskNum.equals(0)){
				map.put(task.getTransactorId(), historyWorkflowTaskDao.getTotalOverdueTasksNumByTransactorId(companyId, task.getTransactorId()));
			}else{
				map.put(task.getTransactorId(), taskNum+historyWorkflowTaskDao.getTotalOverdueTasksNumByTransactorId(companyId, task.getTransactorId()));
			}
		}
		return map;
	}
	
	public List<String> getTransactorsExceptTask(Long taskId) {
		if(taskId==null)return null;
		WorkflowTask task=getTask(taskId);
		return workflowTaskDao.getTransactorsExceptTask(task);
	}
	public List<Long> getTransactorIdsExceptTask(Long taskId) {
		if(taskId==null)return null;
		WorkflowTask task=getTask(taskId);
		return workflowTaskDao.getTransactorIdsExceptTask(task);
	}
	
	/**
	 * 根据“任务组”查询任务列表
	 * @param companyId
	 * @param instanceId
	 * @param taskName
	 * @return
	 */
	public List<WorkflowTask> getTaskOrderByGroupNum(Long companyId,String instanceId,String taskName){
		return workflowTaskDao.getTaskOrderByGroupNum(companyId,instanceId,taskName);
	}
	
	public List<WorkflowTask> getActivityTasksByName(String instanceId,Long companyId,String taskName) {
		return workflowTaskDao.getActivityTasksByName(instanceId, companyId, taskName);
	}
	
	public List<String[]> getActivityTaskTransactors(String instanceId,Long companyId) {
		return workflowTaskDao.getActivityTaskTransactors(instanceId,companyId);
	}
	public List<WorkflowTask> getActivityTaskByInstance(String instanceId,Long companyId) {
		return workflowTaskDao.getActivityTaskByInstance(instanceId,companyId);
	}
	
	public List<String> getActivityTaskPrincipals(String instanceId,Long companyId) {
		return workflowTaskDao.getActivityTaskPrincipals(instanceId,companyId);
	}
	
	public List<Long> getActivityTaskPrincipalIds(String instanceId,Long companyId) {
		return workflowTaskDao.getActivityTaskPrincipalIds(instanceId,companyId);
	}
	public List<String[]> getActivityTaskPrincipalsDetail(String instanceId,Long companyId) {
		return workflowTaskDao.getActivityTaskPrincipalsDetail(instanceId,companyId);
	}
	public List<WorkflowTask> getActivityPrincipalTask(String instanceId,Long companyId) {
		return workflowTaskDao.getActivityPrincipalTask(instanceId,companyId);
	}
	public List<String> getCompletedTaskNames(String workflowId,
			Long companyId) {
		return workflowTaskDao.getCompletedTaskNames(workflowId, companyId);
	}
	public String receive(Long taskId) {
		log.debug("*** receive 方法开始");
		log.debug(new StringBuilder("*** Received parameter:[")
		.append("taskId:").append(taskId)
		.append("]").toString());
    	if(taskId==null){log.debug("领取任务时，任务id不能为null");throw new RuntimeException("领取任务时，任务id不能为null");}
		WorkflowTask task = workflowTaskDao.get(taskId);
		if(task==null){log.debug("领取任务时，任务不能为null");throw new RuntimeException("领取任务时，任务不能为null");}
		String msg = "task.not.need.receive";
		if(task.getActive().equals(TaskState.DRAW_WAIT.getIndex())){
			List<WorkflowTask> tasks = workflowTaskDao.getNotCompleteTasksByName(getCompanyId(), task.getProcessInstanceId(), task.getName());
			for(WorkflowTask tsk : tasks){
				if(taskId.equals(tsk.getId())){
					task.setDrawTask(true);
					tsk.setActive(TaskState.WAIT_TRANSACT.getIndex());
				}else{
					//设置任务对应的消息为已读
					ApiFactory.getPortalService().setMessageReadedByTaskId(tsk.getId(),false);
					tsk.setActive(TaskState.HAS_DRAW_OTHER.getIndex());
				}
			}
			msg = "task.receive.success";
		}
		
    	log.debug("*** receive 方法结束");
		return msg;
	}
	/**
     * 流程被暂停时，强制暂停的当前任务
     */
	@Transactional(readOnly=false)
    public void pauseTasks(String instanceId,Long companyId){
    	log.debug("*** pauseTasks 方法开始");
		log.debug(new StringBuilder("*** Received parameter:[")
				.append("instanceId:").append(instanceId)
				.append(", companyId:").append(companyId)
				.append("]").toString());
    	
    	List<WorkflowTask> tasks = getActivityTasks(instanceId,companyId);
    	for(WorkflowTask task:tasks){
    		task.setPaused(true);
    		saveTask(task);
    	}
    	
    	log.debug("*** pauseTasks 方法结束");
    }
	
	/**
     * 流程被暂停时，强制暂停的当前任务
     */
	@Transactional(readOnly=false)
    public void continueTasks(String instanceId,Long companyId){
    	log.debug("*** continueTasks 方法开始");
		log.debug(new StringBuilder("*** Received parameter:[")
				.append("instanceId:").append(instanceId)
				.append(", companyId:").append(companyId)
				.append("]").toString());
    	
    	List<WorkflowTask> tasks = workflowTaskDao.getPauseTasksByInstance(instanceId,companyId);
    	for(WorkflowTask task:tasks){
    		task.setPaused(false);
    		saveTask(task);
    		String processId= processEngine.getExecutionService().findProcessInstanceById(task.getProcessInstanceId()).getProcessDefinitionId();
    		WorkflowDefinition wfDef = workflowDefinitionManager.getWorkflowDefinitionByProcessId(processId);
    		//流程类型
    		WorkflowType type=null;
    		if(wfDef!=null){
    			//流程类型
    			type=workflowTypeManager.getWorkflowType(wfDef.getTypeId());
    		}
    		//生成消息提醒
    		if(task.getTransactor()!=null){
    			sendMessage(task, type,"");
    		}
    		//*******************邮件通知*************************
    		sendMail(task,processId);
    	}
    	
    	log.debug("*** continueTasks 方法结束");
    }
	/**
	 * 批量移除任务中根据办理人查询当前任务列表
	 * @param tasks
	 * @param transactorName
	 * @param typeId
	 * @param defCode
	 * @param wfdId
	 */
	
	public void getActivityTasksByTransactorName(Page<WorkflowTask> tasks,Long typeId, String defCode,Long wfdId){
		workflowTaskDao.getActivityTasksByTransactorName(tasks,  typeId, defCode, wfdId);
	}
	
	public void getAllTasksByUser(Long companyId, String loginName,
			Page<WorkflowTask> page) {
		workflowTaskDao.getAllTasksByUser(companyId, loginName, page);
		
	}
	public void getAllTasksByUser(Long companyId, Long userId,
			Page<WorkflowTask> page) {
		workflowTaskDao.getAllTasksByUser(companyId, userId, page);
		
	}
	
	public List<WorkflowTask> getTasksByInstance(List<String> instanceIds,String taskName,String recieveUser,Long recieveId,String consignor,Long consignorId,Long companyId){
		return workflowTaskDao.getTasksByInstance(instanceIds, taskName,recieveUser,recieveId,consignor,consignorId,companyId);
	}
	
	public List<String> getActiveTaskNameWithoutSpecial(String instanceId){
		return workflowTaskDao.getActiveTaskNameWithoutSpecial(instanceId);
	}
	
	/**
	 * 获得所有流程名称
	 * @param isComplete
	 * @return
	 */
	public List<Object[]> getGroupNames(String taskCategory){
		if(TaskCategory.COMPLETE.equals(taskCategory)){
			return workflowTaskDao.getAllCompleteTaskGroupNames(ContextUtils.getCompanyId(), ContextUtils.getLoginName(),ContextUtils.getUserId());
		}else if(TaskCategory.CANCEL.equals(taskCategory)){
			return workflowTaskDao.getAllCancelTaskGroupNames(ContextUtils.getCompanyId(), ContextUtils.getLoginName(),ContextUtils.getUserId());
		}else{
			return workflowTaskDao.getAllActiveTaskGroupNames(ContextUtils.getCompanyId(), ContextUtils.getLoginName(),ContextUtils.getUserId());
		}
	}
	/**
	 * 获得所有流程自定义类别
	 * @param isComplete
	 * @return
	 */
	public List<Object[]> getCustomTypes(String taskCategory){
		if(TaskCategory.COMPLETE.equals(taskCategory)){
			return workflowTaskDao.getAllCompleteTaskCustomTypes(ContextUtils.getCompanyId(), ContextUtils.getLoginName(),ContextUtils.getUserId());
		}else if(TaskCategory.CANCEL.equals(taskCategory)){
			return workflowTaskDao.getAllCancelTaskCustomTypes(ContextUtils.getCompanyId(), ContextUtils.getLoginName(),ContextUtils.getUserId());
		}else{
			return workflowTaskDao.getAllActiveTaskCustomTypes(ContextUtils.getCompanyId(), ContextUtils.getLoginName(),ContextUtils.getUserId());
		}
	}
	
	/**
	 * 分页查询用户所有未完成任务
	 * @param page
	 */
	public void getAllTasksByGroupName(Long companyId, String loginName,Long userId, Page<Task> page,String typeName){
		taskDao.getAllTasksByUserType(companyId, loginName,userId, page,typeName);
	}
	/**
	 * 获得办理任务页面
	 */
	@Transactional(readOnly=true)
	public String getTaskUrl(Task task){
		return getTaskUrl(task.getUrl(),task.getId());
	}
	@Transactional(readOnly=true)
	public String getTaskUrl(String taskUrl,Long taskId ){
		String url=taskUrl;
		if(!taskUrl.contains("http://")&&taskUrl.contains("?")){
			url=SystemUrls.getSystemUrl(StringUtils.substringBefore(taskUrl, "/"))+StringUtils.substring(taskUrl, taskUrl.indexOf('/'))+taskId;
		}else if(!taskUrl.contains("http://")){
			url=SystemUrls.getSystemUrl(StringUtils.substringBefore(taskUrl, "/"))+StringUtils.substring(taskUrl, taskUrl.indexOf('/'))+"?taskId="+taskId;
		}
		//重新加载页面样式
		if(!url.contains("_r=1")){
			if(url.contains("?")){
				url=url+"&_r=1";
			}else{
				url=url+"?_r=1";
			}
		}
		return url;
	}
	
	public WorkflowTask getLastCompletedTaskByTaskName(String workflowId,
			Long companyId,String taskName){
		return workflowTaskDao.getLastCompletedTaskByTaskName(workflowId,companyId,taskName);
	}
	public List<WorkflowTask> getActivityTasksByNameWithout(String workflowId,
			Long taskId, String taskName) {
		return workflowTaskDao.getActivityTasksByNameWithout(workflowId, taskId, taskName);
	}
	/**
	 * 根据办理人查找待办理的委托任务
	 * @param workflowId
	 * @param transactor
	 * @return
	 */
	public List<WorkflowTask> getActivityTrustorTasksByTransactor(String workflowId,String transactor,Long userId,Long taskId) {
		return workflowTaskDao.getActivityTrustorTasksByTransactor(workflowId, transactor,userId,taskId);
	}
	
	public List<String> getTransactorsByTask(String name,String transactor,Long userId,String processId){
		return workflowTaskDao.getTransactorsByTask(name,transactor,userId,processId);
	}
	/**
	 * 根据办理人查询当前实例中的任务
	 * @param transactor 办理人登录名
	 * @param workflowId 实例id
	 * @return 
	 */
	public List<WorkflowTask> getTaskByTransactor(String transactor,String workflowId){
		return workflowTaskDao.getTaskByTransactor(transactor, workflowId);
	}
	/**
	 * 根据办理人查询当前实例中的任务
	 * @param userId 办理人id
	 * @param workflowId 实例id
	 * @return 
	 */
	public List<WorkflowTask> getTaskByTransactor(Long userId,String workflowId){
		return workflowTaskDao.getTaskByTransactor(userId, workflowId);
	}
	
	public List<HistoryWorkflowTask> getHistoryTaskByTransactor(String transactor,String workflowId){
		return historyWorkflowTaskDao.getTaskByTransactor(transactor, workflowId);
	}
	public List<HistoryWorkflowTask> getHistoryTaskByTransactor(Long userId,String workflowId){
		return historyWorkflowTaskDao.getTaskByTransactor(userId, workflowId);
	}
	
	public Set<String> getAllTaskTransactors(String workflowId){
		Set<String> result = new HashSet<String>();
		result.addAll(workflowTaskDao.getAllTaskTransactors(workflowId)); 
		result.addAll(historyWorkflowTaskDao.getAllTaskTransactors(workflowId)); 
		return result;
	}
	public Set<String> getAllTaskTrustors(String workflowId){
		Set<String> result = new HashSet<String>();
		result.addAll(workflowTaskDao.getAllTaskTrustors(workflowId)); 
		result.addAll(historyWorkflowTaskDao.getAllTaskTrustors(workflowId)); 
		return result;
	}
	
	/**
	 * 查询当前环节的办理人和委托人集合
	 * @param workflowId 实例id
	 * @param taskName 任务名称
	 * @return 办理人列表
	 */
	public Set<String> getTransactorsByName(String workflowId,String taskName) {
		List<String> transactors =  workflowTaskDao.getTransactorsByName(workflowId, taskName);
		List<String> trustors =  workflowTaskDao.getTrustorsByName(workflowId, taskName);
		List<String> HistTransactors =  historyWorkflowTaskDao.getTransactorsByName(workflowId, taskName);
		List<String> HistTrustors =  historyWorkflowTaskDao.getTrustorsByName(workflowId, taskName);
		Set<String> result = new HashSet<String>();
		result.addAll(transactors);
		result.addAll(trustors);
		result.addAll(HistTransactors);
		result.addAll(HistTrustors);
		return result;
	}
	
	public Set<Long> getAllTaskTransactorIds(String workflowId){
		Set<Long> result = new HashSet<Long>();
		result.addAll(workflowTaskDao.getAllTaskTransactorIds(workflowId)); 
		result.addAll(historyWorkflowTaskDao.getAllTaskTransactorIds(workflowId)); 
		return result;
	}
	public Set<Long> getAllTaskTrustorIds(String workflowId){
		Set<Long> result = new HashSet<Long>();
		result.addAll(workflowTaskDao.getAllTaskTrustorIds(workflowId)); 
		result.addAll(historyWorkflowTaskDao.getAllTaskTrustorIds(workflowId)); 
		return result;
	}
	
	/**
	 * 查询当前环节的办理人和委托人集合
	 * @param workflowId 实例id
	 * @param taskName 任务名称
	 * @return 办理人列表
	 */
	public Set<Long> getTransactorIdsByName(String workflowId,String taskName) {
		List<Long> transactors =  workflowTaskDao.getTransactorIdsByName(workflowId, taskName);
		List<Long> trustors =  workflowTaskDao.getTrustorIdsByName(workflowId, taskName);
		List<Long> HistTransactors =  historyWorkflowTaskDao.getTransactorIdsByName(workflowId, taskName);
		List<Long> HistTrustors =  historyWorkflowTaskDao.getTrustorIdsByName(workflowId, taskName);
		Set<Long> result = new HashSet<Long>();
		result.addAll(transactors);
		result.addAll(trustors);
		result.addAll(HistTransactors);
		result.addAll(HistTrustors);
		return result;
	}
	
	/**
	 * 查询所有超期任务
	 */
	public void getOverdueTasks(Page<WorkflowTask> page) {
		workflowTaskDao.getOverdueTasks(page);
	}
	/**
	 * 查询所有历史超期任务
	 */
	public void getOverdueHistoryTasks(Page<HistoryWorkflowTask> page) {
		historyWorkflowTaskDao.getOverdueTasks(page);
	}
	/**
	 * 查询所有超期任务的办理人、委托人、办理人个数
	 */
	public void getOverdueTaskTransactors(Page<Object> page,String transactorName,String lastTransactTimeStart,String lastTransactTimeEnd) {
		Date dateStart = null; 
		Date dateEnd = null; 
		SimpleDateFormat transactDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		try {
			if(StringUtils.isNotEmpty(lastTransactTimeStart)){
				dateStart = transactDateFormat.parse(lastTransactTimeStart);
			}
			if(StringUtils.isNotEmpty(lastTransactTimeEnd)){
				dateEnd = transactDateFormat.parse(lastTransactTimeEnd);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		workflowTaskDao.getOverdueTaskTransactors(page,transactorName,dateStart,dateEnd);
	}
	
	public void getOverdueTaskDetails(Page<WorkflowTask> page,Long transactorId,String transactor,String transactorName,String lastTransactTimeStart,String lastTransactTimeEnd){
		Map<String,Object> result = getParam(lastTransactTimeStart, lastTransactTimeEnd);
		workflowTaskDao.getOverdueTaskDetails(page, transactorId, transactor,transactorName,(Date)result.get("lastTransactTimeStart"),(Date)result.get("lastTransactTimeEnd"));
	}
	
	/**
	 * 查询所有超期任务的办理人、委托人、办理人个数
	 */
	public void getOverdueHistoryTaskTransactors(Page<Object> page,String transactorName,String lastTransactTimeStart,String lastTransactTimeEnd) {
		Map<String,Object> result = getParam(lastTransactTimeStart, lastTransactTimeEnd);
		historyWorkflowTaskDao.getOverdueTaskTransactors(page,transactorName,(Date)result.get("lastTransactTimeStart"),(Date)result.get("lastTransactTimeEnd"));
	}
	
	public void getOverdueHistoryTaskDetails(Page<HistoryWorkflowTask> page,Long transactorId,String transactor,String transactorName,String lastTransactTimeStart,String lastTransactTimeEnd){
		Map<String,Object> result = getParam(lastTransactTimeStart, lastTransactTimeEnd);
		historyWorkflowTaskDao.getOverdueTaskDetails(page, transactorId, transactor,transactorName,(Date)result.get("lastTransactTimeStart"),(Date)result.get("lastTransactTimeEnd"));
	}
	/**
	 * 根据taskId集合获得办理人信息集合
	 * @return 办理人列表[transactor,transactorName,trustor,trustorName]
	 */
	public List<Object[]> getTransactorsByTaskIds(List<Long> taskIds) {
		return workflowTaskDao.getTransactorsByTaskIds(taskIds);
	}
	public void getTransferTasksByTaskId(Long taskId,Page<WorkflowTask> page){
		workflowTaskDao.getTransferTasksByTaskId(taskId, page);
	}
	
	public void getTransferHistoryTasksByTaskId(Long taskId,Page<HistoryWorkflowTask> page){
		historyWorkflowTaskDao.getTransferTasksByTaskId(taskId, page);
	}

	//2014-5-22
	public Integer getAcceptTasksNum(Boolean isCompleted) {
		
		if(isCompleted){
			Integer currentNum = workflowTaskDao.getAcceptTasksNum(isCompleted);
			Integer histNum = historyWorkflowTaskDao.getAcceptTasksNum();
			if(currentNum == null)currentNum = 0;
			if(histNum == null)histNum = 0;
			return currentNum+histNum;
		}else{
			return  workflowTaskDao.getAcceptTasksNum(isCompleted);
		}
	}
	//2014-5-22
	public void getTaskAsAccept(Page<WorkflowTask> page, boolean isEnd) {
		workflowTaskDao.getTaskAsAccept( page, isEnd);
	}
	//2014-5-22
	public void getHistoryTaskAsAccept(Page<HistoryWorkflowTask> page, boolean isEnd) {
		historyWorkflowTaskDao.getTaskAsAccept( page, isEnd);
	}
	
	/**
	 * 获得办理人所有移交的任务个数
	 * @param transactorId
	 * @return
	 */
	public Integer getTransferTaskNumByTransactorId(Long transactorId,String transactorName,String lastTransactTimeStart,String lastTransactTimeEnd){
		Map<String,Object> result = getParam(lastTransactTimeStart, lastTransactTimeEnd);
		return workflowTaskDao.getTransferTaskNumByTransactorId(transactorId,transactorName,(Date)result.get("lastTransactTimeStart"),(Date)result.get("lastTransactTimeEnd"));
	}
	public Integer getTransferHistoryTaskNumByTransactorId(Long transactorId,String transactorName,String lastTransactTimeStart,String lastTransactTimeEnd){
		Map<String,Object> result = getParam(lastTransactTimeStart, lastTransactTimeEnd);
		return historyWorkflowTaskDao.getTransferTaskNumByTransactorId(transactorId,transactorName,(Date)result.get("lastTransactTimeStart"),(Date)result.get("lastTransactTimeEnd"));
	}
	
	private Map<String,Object> getParam(String lastTransactTimeStart,String lastTransactTimeEnd){
		Map<String,Object> result = new HashMap<String, Object>();
		Date dateStart = null; 
		Date dateEnd = null; 
		SimpleDateFormat transactDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		try {
			if(StringUtils.isNotEmpty(lastTransactTimeStart)){
				dateStart = transactDateFormat.parse(lastTransactTimeStart);
			}
			if(StringUtils.isNotEmpty(lastTransactTimeEnd)){
				dateEnd = transactDateFormat.parse(lastTransactTimeEnd);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		result.put("lastTransactTimeStart", dateStart);
		result.put("lastTransactTimeEnd", dateEnd);
		return result;
	}
	public void getTransferHistoryTaskDetails(Page<HistoryWorkflowTask> page,
			Long transactorId,String transactorName,String lastTransactTimeStart,String lastTransactTimeEnd) {
		Map<String,Object> result = getParam(lastTransactTimeStart, lastTransactTimeEnd);
		historyWorkflowTaskDao.getTransferTaskDetails(page, transactorId,transactorName,(Date)result.get("lastTransactTimeStart"),(Date)result.get("lastTransactTimeEnd"));
	}
	public void getTransferTaskDetails(Page<WorkflowTask> page,
			Long transactorId,String transactorName,String lastTransactTimeStart,String lastTransactTimeEnd) {
		Map<String,Object> result = getParam(lastTransactTimeStart, lastTransactTimeEnd);
		workflowTaskDao.getTransferTaskDetails(page, transactorId,transactorName,(Date)result.get("lastTransactTimeStart"),(Date)result.get("lastTransactTimeEnd"));
	}
	public void completeOtherTranferTask(Long transferTaskId,Long taskId){
		workflowTaskDao.completeOtherTranferTask(transferTaskId,taskId);
	}
	
	public Integer getTransferTaskNumByActive(Long transactorId, Boolean isCompleted){
		if(isCompleted){
			Integer currentNum =  workflowTaskDao.getTransferTasksNum(transactorId, isCompleted);
			Integer histNum =  historyWorkflowTaskDao.getTransferTasksNum(transactorId);
			if(currentNum == null)currentNum = 0;
			if(histNum == null)histNum = 0;
			return currentNum+histNum;
		}else{
			return  workflowTaskDao.getTransferTasksNum(transactorId,isCompleted);
		}
	}
	
	/**
	 * 获得移交任务
	 * @param page
	 * @param transactorId
	 * @param isCompleted(true:已完成，false：办理中)
	 */
	public void getTransferTasksByActive(Page<WorkflowTask> page,Long transactorId, boolean isCompleted){
		workflowTaskDao.getTransferTasks(page,transactorId,isCompleted);
	}
	
	public List<String> selectOtherTranferTaskTransactorName(Long transferTaskId,Long taskId){
		return workflowTaskDao.selectOtherTranferTaskTransactorName(transferTaskId, taskId);
	}
	
	public List<Long> selectOtherTranferTaskTransactorId(Long transferTaskId,Long taskId){
		return workflowTaskDao.selectOtherTranferTaskTransactorId(transferTaskId, taskId);
	}
	
	public List<Long> selectOtherTranferTaskIds(Long transferTaskId,Long taskId){
		return workflowTaskDao.selectOtherTranferTaskIds(transferTaskId, taskId);
	}
	/**
	 * 通过公司id和关闭标识获得系统中所有已完成的任务
	 * @param companyId
	 * @return
	 */
	public List<WorkflowTask> getCompleteTasks(Long companyId,String closeSign) {
		return workflowTaskDao.getCompleteTasks(companyId,closeSign);
	}
	
	/**
	 * 通过公司id和推送标识获得系统中所有当前未办理的任务
	 * @param companyId
	 * @return
	 */
	public List<WorkflowTask> getDidNotDealWithTasks(Long companyId,String pushSign) {
		return workflowTaskDao.getDidNotDealWithTasks(companyId,pushSign);
	}
	
}
