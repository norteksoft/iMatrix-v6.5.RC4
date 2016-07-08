package com.norteksoft.wf.base.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.acs.entity.organization.Company;
import com.norteksoft.acs.service.AcsUtils;
import com.norteksoft.bs.options.enumeration.InternationType;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.util.AsyncMailUtils;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.ParameterUtils;
import com.norteksoft.product.util.PropUtils;
import com.norteksoft.product.util.SystemUrls;
import com.norteksoft.product.util.ThreadParameters;
import com.norteksoft.product.web.struts2.Struts2Utils;
import com.norteksoft.task.entity.WorkflowTask;
import com.norteksoft.wf.base.enumeration.CommonStrings;
import com.norteksoft.wf.base.enumeration.TrustRecordState;
import com.norteksoft.wf.engine.core.DefinitionXmlParse;
import com.norteksoft.wf.engine.entity.TrustRecord;
import com.norteksoft.wf.engine.service.DataDictionaryManager;
import com.norteksoft.wf.engine.service.DelegateMainManager;
import com.norteksoft.wf.engine.service.TaskService;
import com.norteksoft.wf.engine.service.WorkflowInstanceManager;

@Service
@Transactional(readOnly=false)
public class TimerUtils {

	private Log log = LogFactory.getLog(DataDictionaryManager.class);
	private DelegateMainManager delegateMainManager;
	private WorkflowInstanceManager workflowInstanceManager;
	private TaskService taskService;
	private AcsUtils acsUtils;
	private static final long  DAY_MILLI_SECOND = 24*60*60*1000;
	private static final long  HOUR_MILLI_SECOND = 60*60*1000;
	@Autowired
	public void setTaskService(TaskService taskService) {
		this.taskService = taskService;
	}
	
	@Autowired
	public void setDelegateMainManager(DelegateMainManager delegateMainManager) {
		this.delegateMainManager = delegateMainManager;
	}
	
	@Autowired
	public void setWorkflowInstanceManager(
			WorkflowInstanceManager workflowInstanceManager) {
		this.workflowInstanceManager = workflowInstanceManager;
	}
	@Autowired
	public void setAcsUtils(AcsUtils acsUtils) {
		this.acsUtils = acsUtils;
	}
	
	@Transactional(readOnly=false)
	public void run() throws Exception{
		List<Company> companys=acsUtils.getAllCompanys();
		for(Company company:companys){
			ThreadParameters parameters=new ThreadParameters(company.getId());
			ParameterUtils.setParameters(parameters);
			com.norteksoft.product.api.entity.User systemAdmin = ApiFactory.getAcsService().getSystemAdmin();
			parameters=new ThreadParameters(company.getId());
			parameters.setUserName("系统");
			parameters.setLoginName(systemAdmin.getLoginName());
			parameters.setUserId(systemAdmin.getId());
			ParameterUtils.setParameters(parameters);

			//委托
			delegateMain();
			
			//催办
			List<WorkflowTask> result=new ArrayList<WorkflowTask>();
			result.addAll(workflowInstanceManager.getNeedReminderTasksByInstance());
			result.addAll(taskService.getNeedReminderTasks());
			reminder(result);
			
		}
		deleteExportTempFile();
		
		//清空同步处理时的实例map,见TaskService中的completeInteractiveWorkflowTask方法
		TaskService.instanceIds.clear();
	}
	
	/**
	 * 删除导出的临时文件
	 */
	public void deleteExportTempFile(){
		String path =  PropUtils.getProp("excel.export.file.path");
		if(StringUtils.isNotEmpty(path)){
			File file = new File(path);
			if(file.isDirectory()){
				String[] tempList=file.list();
				File temp=null;
				for(String t:tempList){
					temp=new File(path+t);
					if(temp.isFile()){
						temp.delete();
					}
				}
			}
		}
	}
	@Transactional(readOnly=false)
	public void delegateMain(){
		try {
			//权限委托
			List<TrustRecord> delegateMains = delegateMainManager.getDelegateMainsOnAssign();
			for(TrustRecord dm:delegateMains){
				com.norteksoft.product.api.entity.User trustee = null;
				com.norteksoft.product.api.entity.User trustor=null;
				if(dm.getTrusteeId()==null){
					trustee=ApiFactory.getAcsService().getUserByLoginName(dm.getTrustee());//受托人
				}else{
					trustee=ApiFactory.getAcsService().getUserById(dm.getTrusteeId());//受托人
				}
				if(dm.getTrustorId()==null){
					trustor=ApiFactory.getAcsService().getUserByLoginName(dm.getTrustor());//委托人
				}else{
					trustor=ApiFactory.getAcsService().getUserById(dm.getTrustorId());//委托人
				}
				
				if(needEfficient(dm)){
					ApiFactory.getAcsService().assignTrustedRole(trustor.getId(), dm.getRoleIds().split(","), trustee.getId());
					dm.setState(TrustRecordState.EFFICIENT);
					delegateMainManager.saveDelegateMain(dm);
				}
				if(needEnd(dm)){
					ApiFactory.getAcsService().deleteTrustedRole(trustor.getId(), dm.getRoleIds().split(","),trustee.getId() );
					dm.setState(TrustRecordState.END);
					delegateMainManager.saveDelegateMain(dm);
				}
			}
			
			//流程委托
			List<TrustRecord> workflowDelegateMains = delegateMainManager.getAllStartWorkflowDelegateMain();
			for(TrustRecord wfdm : workflowDelegateMains){
				if(needEfficient(wfdm)){
					wfdm.setState(TrustRecordState.EFFICIENT);
					delegateMainManager.saveDelegateMain(wfdm);
				}
				if(needEnd(wfdm)){
					wfdm.setState(TrustRecordState.END);
					delegateMainManager.saveDelegateMain(wfdm);
					//委托结束时取回任务
					taskService.recieveDelegateTask(wfdm);
				}
			}
		} catch (Exception e) {
			log.error("定时委托异常："+e.getMessage());
		}
	}
	
	/*
	 * 判断是需要结束委托
	 * 当委托处于生效状态，并且当前日期大于或等于截至日期时就需要结束
	 */
	public boolean needEnd(TrustRecord dm){
		return (dm.getState()==TrustRecordState.EFFICIENT || dm.getState()==TrustRecordState.STARTED)
				&&dm.getEndTime().compareTo(new Date(System.currentTimeMillis()))<=0;
	}
	
	/*
	 * 判断是需要生效
	 * 当委托处于启用状态，并且当前日期在生效日期和截至日期之间时就需要生效
	 */
	public boolean needEfficient(TrustRecord dm){
		return dm.getState()==TrustRecordState.STARTED
				&&dm.getBeginTime().compareTo(new Date(System.currentTimeMillis()))<=0
				&&dm.getEndTime().compareTo(new Date(System.currentTimeMillis()))>=0;
	}
	
	
	
	
	public void reminder(List<WorkflowTask> tasks){
		try {
			for(WorkflowTask task : tasks){
				if(neetReminder(task)){
					if(task.getReminderLimitTimes()!=0&&task.getReminderLimitTimes().equals(task.getAlreadyReminderTimes())){
							informSettingUser(task);
					}
					if(task.getReminderLimitTimes()==0||task.getReminderLimitTimes()>task.getAlreadyReminderTimes()){
							if(StringUtils.isNotEmpty(task.getReminderStyle())){
								reminder(task);
							}
							task.setLastReminderTime(new Date(System.currentTimeMillis()));
							task.setAlreadyReminderTimes(task.getAlreadyReminderTimes()+1);
					} 
				}
			}
			taskService.saveTasks(tasks);
		} catch (Exception e) {
			log.error("定时催办异常："+e.getMessage());
		}
	}
	/*
	 * 催办超出次数限制，通知相关人员
	 */
	public void informSettingUser(WorkflowTask task) throws Exception{
		com.norteksoft.wf.engine.entity.WorkflowInstance instance = workflowInstanceManager.getWorkflowInstance(task.getProcessInstanceId());
		List<String> remindUsers = getRemindUsers(task,instance);
		String reminderNoticeUser = remindUsers.get(0);
		String reminderTransferUser = remindUsers.get(1);
		//移交任务
		transferTask(task,reminderTransferUser,instance);
		if(StringUtils.isNotEmpty(task.getReminderNoticeStyle())&&StringUtils.isNotEmpty(reminderNoticeUser)){
			//去除重复
			Set<String> transactorIds = new HashSet<String>();
			transactorIds.addAll(Arrays.asList(reminderNoticeUser.split(",")));
			
			//通知
			informUser(task,transactorIds);
		}
	}
	
	private void informUser(WorkflowTask task,Set<String> transactorIds) throws Exception{
		String[] reminderNoticeStyle = task.getReminderNoticeStyle().split(",");
		for(String style:reminderNoticeStyle){
			if(style.equalsIgnoreCase(CommonStrings.EMAIL_STYLE)){
				emailInform(task,transactorIds);
			}else if(style.equalsIgnoreCase(CommonStrings.RTX_STYLE)){
				RtxInform(task,transactorIds);
			}else if(style.equalsIgnoreCase(CommonStrings.SMS_STYLE)){
				smsInform(task,transactorIds);
			}else if(style.equalsIgnoreCase(CommonStrings.SWING_STYLE)){
				swingInform(task,transactorIds);
			}
		}
	}
	
	private void transferTask(WorkflowTask task,String reminderTransferUser,com.norteksoft.wf.engine.entity.WorkflowInstance instance) throws Exception{
		if(StringUtils.isNotEmpty(reminderTransferUser)){
			//去除重复
			Set<String> transactorIds = new HashSet<String>();
			transactorIds.addAll(Arrays.asList(reminderTransferUser.split(",")));
			List<String> userIds = new ArrayList<String>();
			userIds.addAll(transactorIds);
			
    		taskService.generateTransferTasks(instance, userIds, task);
		}
	}
	
	
	public void RtxInform(WorkflowTask task,Set<String> transactorIds) {
		List<String> messages = new ArrayList<String>();
		messages.add(task.getGroupName());
		messages.add(task.getName());
		messages.add(task.getTitle());
		messages.add(task.getTransactorName());
		for(String userId : transactorIds){
			com.norteksoft.product.api.entity.User user = ApiFactory.getAcsService().getUserById(Long.parseLong(userId));
			if(user!=null){
				String language = ApiFactory.getPortalService().getUserLanguageById(Long.parseLong(userId));
				String msg = getInterationValue("inform.message.info", language,messages);
				rtx.RtxMsgSender.sendNotify(user.getLoginName(), getInterationValue("inform.message.info.type", language,null), "1", msg , "",task.getCompanyId());
			}
		}
	}
	public void smsInform(WorkflowTask task,Set<String> transactorIds) {
		// TODO Auto-generated method stub
	}
	
	public void emailInform(WorkflowTask task,Set<String> transactorIds) {
		Set<String> informUserEmails = new HashSet<String>();
		com.norteksoft.product.api.entity.User temp ;
		for(String userId : transactorIds){
			temp =  ApiFactory.getAcsService().getUserById(Long.parseLong(userId));
			if(temp!=null)informUserEmails.add(temp.getEmail());
		}
		String msg = new StringBuilder("流程:"+task.getGroupName()).append(",环节:").append(task.getName()).append(",标题:").append(task.getTitle()).append("的办理人").append(task.getTransactorName()).append( "被催办次数已经超过设置上限，请您核实情况。").toString();
		AsyncMailUtils.sendMail(informUserEmails,"催办超期提醒", msg);
	}
	public void swingInform(WorkflowTask task,Set<String> transactorIds) throws Exception {
		List<String> messages = new ArrayList<String>();
		messages.add(task.getGroupName());
		messages.add(task.getName());
		messages.add(task.getTitle());
		messages.add(task.getTransactorName());
		for(String userId : transactorIds){
			//${}消息的国际化显示时需要的
			String language = ApiFactory.getPortalService().getUserLanguageById(Long.parseLong(userId));
			String msg = getInterationValue("inform.message.info", language,messages);
			com.norteksoft.product.api.entity.User user = ApiFactory.getAcsService().getUserById(Long.parseLong(userId));
			if(user!=null){
				Long messageId = ApiFactory.getPortalService().addMessage("task", "系统管理员", ContextUtils.getUserId(), user.getId(),getInterationValue("inform.message.info.type", language,null), msg, "/task/workflow-notification.htm?notificationType=remind&id="+task.getId(),true,"task--"+task.getProcessInstanceId());
				ApiFactory.getPortalService().updateMessageUrl(messageId, "/task/workflow-notification.htm?notificationType=remind&messageId="+messageId);
			}
		}
		
	}
	
	private String getInterationValue(String code,String language,List<String> messages){
		return ApiFactory.getSettingService().getInternationOptionValue(code, language, InternationType.WORKFLOW_RESOURCE.toString(),messages);
	}
	
	public void reminder(WorkflowTask task) throws Exception{
		String[] reminderStyles = task.getReminderStyle().split(",");
		for(String style:reminderStyles){
			if(StringUtils.trim(style).equalsIgnoreCase(CommonStrings.EMAIL_STYLE)){
				emailReminder(task);
			}else if(StringUtils.trim(style).equalsIgnoreCase(CommonStrings.RTX_STYLE)){
				rtxReminder(task);
			}else if(StringUtils.trim(style).equalsIgnoreCase(CommonStrings.SMS_STYLE)){
				smsReminder(task);
			}else if(StringUtils.trim(style).equalsIgnoreCase(CommonStrings.SWING_STYLE)){
				swingReminder(task);
			}
		}
	}
	
	public void emailReminder(WorkflowTask task){
		com.norteksoft.product.api.entity.User user = null;
		if(task.getTransactorId()==null){
			user = ApiFactory.getAcsService().getUserByLoginName(task.getTransactor());
		}else{
			user = ApiFactory.getAcsService().getUserById(task.getTransactorId());
		}
		List<String> messages = new ArrayList<String>();
		messages.add("("+new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis()))+")");
		messages.add(task.getTitle());
		messages.add(getTaskDay(task)+"");
		String language = ApiFactory.getPortalService().getUserLanguageById(user.getId());
		String msg = getInterationValue("reminder.message.info", language,messages);
		AsyncMailUtils.sendMail(user.getEmail(),getInterationValue("reminder.message.info.type", language,null), msg);
	}
	
	public void rtxReminder(WorkflowTask task){
		String url = SystemUrls.getSystemUrl(StringUtils.substring(task.getUrl(), 0,task.getUrl().indexOf('/')))
					+StringUtils.substring(task.getUrl(), task.getUrl().indexOf('/'));
		if(url.contains("?")){
			url=url+task.getId();
		}else{
			url=url+"?taskId="+task.getId();
		}
		com.norteksoft.product.api.entity.User user = null;
		if(task.getTransactorId()==null){
			user = ApiFactory.getAcsService().getUserByLoginName(task.getTransactor());
		}else{
			user = ApiFactory.getAcsService().getUserById(task.getTransactorId());
		}
		List<String> messages = new ArrayList<String>();
		messages.add("("+new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis()))+")");
		messages.add(task.getTitle());
		messages.add(getTaskDay(task)+"");
		String language = ApiFactory.getPortalService().getUserLanguageById(user.getId());
		String msg = getInterationValue("reminder.message.info", language,messages);
		rtx.RtxMsgSender.sendNotify(task.getTransactor(), getInterationValue("reminder.message.info.type", language,null), "1", msg , url,user.getCompanyId());
	}
	public void swingReminder(WorkflowTask task) throws Exception{
		if(StringUtils.isNotEmpty(task.getTransactor())){
			String language = ApiFactory.getPortalService().getUserLanguageById(task.getTransactorId());
			List<String> messages = new ArrayList<String>();
			messages.add("("+new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis()))+")");
			messages.add(task.getTitle());
			messages.add(getTaskDay(task)+"");
			String msg = getInterationValue("reminder.message.info", language,messages);
			if(task.getTransactorId()==null){
				ApiFactory.getPortalService().addMessage("task", "系统管理员", ContextUtils.getLoginName(), task.getTransactor(),getInterationValue("reminder.message.info.type", language,null), msg, "/task/message-task.htm?id="+task.getId(),true,"task-"+task.getId()+"-"+task.getProcessInstanceId());
			}else{
				ApiFactory.getPortalService().addMessage("task", "系统管理员", ContextUtils.getUserId(), task.getTransactorId(),getInterationValue("reminder.message.info.type", language,null), msg, "/task/message-task.htm?id="+task.getId(),true,"task-"+task.getId()+"-"+task.getProcessInstanceId());
				
			}
		}
	}
	public void smsReminder(WorkflowTask task){
		//TODO
	}
	
	public boolean neetReminder(WorkflowTask task){
		long repeatMill = task.getRepeat()*DAY_MILLI_SECOND;//如果时间间隔粒度为天
		if(DefinitionXmlParse.REMIND_TIME_WAY_HOUR.equals(task.getReminderTimeWay())){//如果时间间隔粒度为小时
			repeatMill = task.getRepeat()*HOUR_MILLI_SECOND;
		}
		return (task.getLastReminderTime()== null && ((System.currentTimeMillis()-task.getCreatedTime().getTime())>task.getDuedate()*DAY_MILLI_SECOND) && (!isHolidayDate(task.getSubCompanyId()))) ||
		(task.getLastReminderTime()!= null && ((System.currentTimeMillis()-task.getLastReminderTime().getTime())>repeatMill) && (!isHolidayDate(task.getSubCompanyId())));
	}
	
	//添加节假日不催办的限制条件
	public boolean isHolidayDate(Long subCompanyId){
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		Date tomorrow = cal.getTime();
		return ApiFactory.getSettingService().isHolidayDay(tomorrow, subCompanyId);
	}
	/**
	 * 获得任务已经生成几天
	 * @param task
	 * @return
	 */
	private int getTaskDay(WorkflowTask task){
		Map<String,List<Date>> holidayWork = ApiFactory.getSettingService().getHolidaySettingDays(task.getCreatedTime(), new Date(), task.getSubCompanyId());
		long workTimes = CommonStrings.WORKTIMES;
		List<Date> workDates = holidayWork.get("workDate");//获得工作日集合
		Date currentDate = taskService.getDateWithoutSecond(new Date());//当前时间，去掉秒和毫秒，例如：2014-10-10 10:10
		if(StringUtils.isNotEmpty(task.getReminderStyle())&&task.getLastTransactTime()==null){//兼容历史数据
			taskService.setLastTransactTime(task);
		}
		long overTime = taskService.getOverTimes(currentDate,task.getLastTransactTime(),workDates);
		if(overTime>workTimes){
			return (int)(Math.ceil(Double.parseDouble(overTime+"")/Double.parseDouble(workTimes+"")));
		}else{//如果超期不到一天则按1天算
			return 1;
		}
	}
	
	private List<String> getRemindUsers(WorkflowTask task,com.norteksoft.wf.engine.entity.WorkflowInstance instance){
		Map<String,String > reminderSetting = DefinitionXmlParse.getReminderSetting(instance.getProcessDefinitionId(), task.getName());
		if(StringUtils.isEmpty(reminderSetting.get(DefinitionXmlParse.REMIND_STYLE))){//如果环节没有设置催办，继承了流程的催办设置，则获得催办相关信息
			reminderSetting = DefinitionXmlParse.getReminderSetting(instance.getProcessDefinitionId());
		}
		String noticeUserCondition = reminderSetting.get(DefinitionXmlParse.REMIND_NOTICE_USER_CONDITION);
		String transferUserCondition = reminderSetting.get(DefinitionXmlParse.REMIND_TRANSFER_USER_CONDITION);
		Boolean reminderContainNoticeUser = false;
		String reminderContainNotice = reminderSetting.get(DefinitionXmlParse.REMIND_CONTAIN_NOTICE_USER);
		if("true".equals(reminderContainNotice)){
			reminderContainNoticeUser = true;
		}
		Boolean reminderContainTransferUser = false;
		String reminderContainTransfer = reminderSetting.get(DefinitionXmlParse.REMIND_CONTAIN_TRANSFER_USER);
		if("true".equals(reminderContainTransfer)){
			reminderContainTransferUser = true;
		}
		String noticeUserConditions = noticeUserCondition;
		if(StringUtils.isNotEmpty(noticeUserCondition)){
			if(reminderContainTransferUser&&StringUtils.isNotEmpty(transferUserCondition)){
				noticeUserConditions = "("+noticeUserCondition+") condition.operator.or ("+transferUserCondition+")";
			}
		}else{
			if(reminderContainTransferUser){
				noticeUserConditions = transferUserCondition;
			}
		}
		String transferUserConditions = transferUserCondition;
		if(StringUtils.isNotEmpty(transferUserCondition)){
			if(reminderContainNoticeUser&&StringUtils.isNotEmpty(noticeUserCondition)){
				transferUserConditions = "("+transferUserCondition+") condition.operator.or ("+noticeUserCondition+")";
			}
		}else{
			if(reminderContainNoticeUser){
				transferUserConditions = noticeUserCondition;
			}
		}
		String reminderNoticeUser ="";
		String reminderTransferUser ="";
		if(StringUtils.isNotEmpty(noticeUserConditions)){
			reminderNoticeUser = taskService.parseUserCondition( task,noticeUserConditions,instance);
		}
		if(StringUtils.isNotEmpty(transferUserConditions)){
			reminderTransferUser = taskService.parseUserCondition( task,transferUserConditions,instance);
		}
		List<String> result = new ArrayList<String>();
		result.add(reminderNoticeUser);
		result.add(reminderTransferUser);
		return result;
	}
}
