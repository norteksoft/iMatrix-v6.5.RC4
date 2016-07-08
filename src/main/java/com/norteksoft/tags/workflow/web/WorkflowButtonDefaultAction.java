package com.norteksoft.tags.workflow.web;


import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.jbpm.api.ProcessEngine;
import org.jbpm.api.activity.ActivityExecution;
import org.springframework.beans.factory.annotation.Autowired;

import com.norteksoft.acs.base.web.struts2.CRUDActionSupport;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.entity.WorkflowInstance;
import com.norteksoft.product.api.entity.WorkflowTask;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.JsonParser;
import com.norteksoft.product.util.ParameterUtils;
import com.norteksoft.product.util.PropUtils;
import com.norteksoft.product.util.ThreadParameters;
import com.norteksoft.product.util.freemarker.TagUtil;
import com.norteksoft.product.web.struts2.Struts2Utils;
import com.norteksoft.task.base.enumeration.TaskProcessingResult;
import com.norteksoft.wf.base.enumeration.CommonStrings;
import com.norteksoft.wf.base.enumeration.CompleteTaskTipType;
import com.norteksoft.wf.base.utils.WorkflowUtil;
import com.norteksoft.wf.engine.core.GetBackCommand;

public class WorkflowButtonDefaultAction  extends CRUDActionSupport{
	private static final long serialVersionUID = 1L;
	private Long taskId;
	private String userIds;
	private String taskIds;
	private Long userId;
	private String transitionName;
	private String taches;
	private ProcessEngine processEngine;
	
	@Override
	public String delete() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	/*
	 * 实现加签
	 */
	public String addSigner() {
		try{
			setThreadInfo(taskId);
			ApiFactory.getTaskService().addSigner(taskId,Arrays.asList(userIds.split(",")));
			renderText("加签成功！");
		}catch (Exception e) {
			renderText(e.getMessage());
		}
		return null;
	}
	/**
	 * 将当前用户的相关信息设置到线程中
	 * @param taskId
	 */
	private void setThreadInfo(Long taskId){
		WorkflowTask task = ApiFactory.getTaskService().getTask(taskId);
		ThreadParameters parameters = new ThreadParameters();
		parameters.setCompanyId(task.getCompanyId());
		parameters.setUserId(task.getTransactorId());
		parameters.setLoginName(task.getTransactor());
		parameters.setUserName(task.getTransactorName());
		parameters.setSubCompanyId(task.getSubCompanyId());
		ParameterUtils.setParameters(parameters);
	}
	
	/**
	 * 选择减签人员
	 * @return
	 * @throws Exception 
	 */
	public String cutsign() throws Exception{
		setThreadInfo(taskId);
		renderText(JsonParser.object2Json(WorkflowUtil.generateRemoveSingerTree(taskId)));
		return null;
	}
	
	/**
	 * 获得下一环节的任务名称
	 * @return
	 * @throws Exception 
	 */
	public String getNextTaskName() throws Exception{
		WorkflowTask task=ApiFactory.getTaskService().getTask(taskId);
		String nextTaskNames=task.getNextTasks();
		if(StringUtils.isEmpty(nextTaskNames)){
			nextTaskNames="";
		}
		renderText(nextTaskNames);
		return null;
	}
	
	/**
	 * 退回到上一环节
	 * 上一环节办理人指定时，没有选择办理人，而是把办理人页面直接关掉，所以要退回到上一环节
	 * @return
	 * @throws Exception 
	 */
	public String goBackPreviousTask() throws Exception{
		WorkflowTask task=ApiFactory.getTaskService().getTask(taskId);
		WorkflowInstance workflow=ApiFactory.getInstanceService().getInstance(task.getProcessInstanceId());
		//将jbpm退回到task.getName()环节
		processEngine.execute(new GetBackCommand(workflow.getProcessInstanceId(), task.getName()));
		//如果退回到的环节办理人为"上一环节办理人指定"或“条件筛选/选择具体办理人/人工选择”，则设置jbpmTask的办理人为previousTask的办理人
		ActivityExecution execution = (ActivityExecution) processEngine.getExecutionService().findExecutionById(task.getExecutionId());
		org.jbpm.api.task.Task jbpmTask = processEngine.getTaskService().createTaskQuery()
		.processInstanceId(task.getProcessInstanceId())
		.activityName(execution.getActivityName()).uniqueResult();   
		if(jbpmTask!=null){
				processEngine.getTaskService().assignTask(jbpmTask.getId(),task.getTransactor());
    	}
		task.setActive(0);
		ApiFactory.getTaskService().saveTask(task);
		renderText("ok");
		return null;
	}
	
	/**
	 * 减签
	 */
	public String removeSigner() {
		List<Long> lists = new ArrayList<Long>();
		String[] taskIdStrs = taskIds.split(",");
		for(String idStr:taskIdStrs){
			lists.add(Long.parseLong(idStr));
		}
		setThreadInfo(taskId);
		ApiFactory.getTaskService().removeSigners(lists);
		renderText("减签成功！");
		return null;
	}
	/**
	 * 指派
	 */
	public String assign() {
		setThreadInfo(taskId);
		ApiFactory.getTaskService().assign(taskId,userId);
		renderText("指派成功！");
		return null;
	}
	/**
	 * 抄送
	 */
	public String copyTask() {
		setThreadInfo(taskId);
		List<String> userIdStrs=new ArrayList<String>();
		if("all_user".equals(userIds)){
			List<Long> userids=ApiFactory.getAcsService().getUserIdsByCompanyWithoutAdmin();
			userIdStrs.addAll(Arrays.asList(userids.toString().replace("[", "").replace("]", "").replace(" ", "")));
		}else{
			userIdStrs=Arrays.asList(userIds.split(","));
		}
		ApiFactory.getTaskService().createCopyTasks(taskId, userIdStrs, null, null);
		renderText("已抄送");
		return null;
	}
	/**
	 * 完成选择办理人
	 */
	public String assignTransactor() {
		setThreadInfo(taskId);
		List<String> userIdStrs=new ArrayList<String>();
		if("all_user".equals(userIds)){
			List<Long> userids=ApiFactory.getAcsService().getUserIdsByCompanyWithoutAdmin();
			userIdStrs.addAll(Arrays.asList(userids.toString().replace("[", "").replace("]", "").replace(" ", "")));
		}else{
			userIdStrs=Arrays.asList(userIds.split(","));
		}
		CompleteTaskTipType result = ApiFactory.getTaskService().completeInteractiveWorkflowTask(taskId, userIdStrs,null);
		if(result==CompleteTaskTipType.OK){
			renderText("已指定办理人");
		}else{
			renderText("指定办理人失败");
		}
		return null;
	}
	/**
	 * 选择环节
	 */
	public String canChoiceTaches() {
		setThreadInfo(taskId);
		CompleteTaskTipType completeTaskTipType=null;
		try{
			completeTaskTipType =  ApiFactory.getTaskService().completeWorkflowTask( taskId, TaskProcessingResult.READED);
			if(completeTaskTipType==CompleteTaskTipType.TACHE_CHOICE_URL){
				renderText(completeTaskTipType.getCanChoiceTaches().toString().replace("{", "").replace("}", ""));
			}
		}catch(RuntimeException de){
			de.printStackTrace();
		}
		return null;
	}
	/**
	 * 显示选择环节页面
	 */
	public String showActivity() throws Exception{
		setThreadInfo(taskId);
		List<String[]> tacheList = new ArrayList<String[]>(); 
		if(taches!=null){
			String[] tache = taches.split(",");
			for (String a : tache) {
				String[] s = new String[]{a.split("=")[0].trim(),a.split("=")[1].trim()}; 
				tacheList.add(s);
 			}
		}
		String language = ContextUtils.getCurrentLanguage();
		String resourceCtx=PropUtils.getProp("host.resources");
		String ctx=PropUtils.getProp("host.app");
		Map<String, Object> root=new HashMap<String, Object>();
		root.put("tacheList", tacheList);
		root.put("resourcesCtx", resourceCtx);
		root.put("taskId", taskId);
		root.put("theme", ContextUtils.getTheme());
		root.put("ctx", ctx);
		//国际化
		root.put("selectNextTache", Struts2Utils.getText("selectNextTache",language));
		root.put("ftlSubmit", Struts2Utils.getText("ftlSubmit",language));
		root.put("nextTache", Struts2Utils.getText("nextTache",language));
		String html = TagUtil.getContent(root, "workflow/select-taches.ftl");
		//将信息内容输出到JSP页面
		HttpServletResponse response = Struts2Utils.getResponse();
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer = response.getWriter();
		writer.print(html);
		
		return null;
	}
	/**
	 * 完成选择环节
	 */
	public String selectActivity() {
		setThreadInfo(taskId);
		CompleteTaskTipType completeTaskTipType=null;
		try{
			completeTaskTipType =  ApiFactory.getTaskService().selectActivity(taskId, transitionName);
			if(completeTaskTipType==CompleteTaskTipType.OK){
				renderText("已选择环节");
			}
		}catch(RuntimeException de){
			de.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 取回
	 */
	public String retrieveTask() {
		String msg=ApiFactory.getTaskService().retrieve(taskId);
		renderText(msg);
		return null;
	}
	
	/**
	 * 领取任务
	 */
	public String drawTask() {
		renderText(ApiFactory.getTaskService().drawTask(taskId));
		return null;
	}
	
	/**
	 * 放弃领取任务
	 */
	public String abandonReceive() {
		renderText(ApiFactory.getTaskService().abandonReceive(taskId));
		return null;
	}
	

	@Override
	public String list() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void prepareModel() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String save() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getModel() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	public void setUserIds(String userIds) {
		this.userIds = userIds;
	}

	public void setTaskIds(String taskIds) {
		this.taskIds = taskIds;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public void setTransitionName(String transitionName) {
		this.transitionName = transitionName;
	}
	public void setTaches(String taches) {
		this.taches = taches;
	}
	public ProcessEngine getProcessEngine() {
		return processEngine;
	}
	@Autowired
	public void setProcessEngine(ProcessEngine processEngine) {
		this.processEngine = processEngine;
	}

}
