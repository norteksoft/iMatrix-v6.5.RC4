package com.norteksoft.task.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.norteksoft.mms.form.jdbc.JdbcSupport;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.orm.hibernate.HibernateDao;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.task.base.enumeration.TaskProcessingMode;
import com.norteksoft.task.base.enumeration.TaskState;
import com.norteksoft.task.base.enumeration.TaskProcessingResult;
import com.norteksoft.task.entity.WorkflowTask;
import com.norteksoft.wf.base.enumeration.ProcessState;

@Repository
public class WorkflowTaskDao extends HibernateDao<WorkflowTask, Long>{
	private JdbcSupport jdbcDao;
	@Autowired
	public void setJdbcDao(JdbcSupport jdbcDao) {
		this.jdbcDao = jdbcDao;
	}
	
	public WorkflowTask getTask(Long taskId){
		return findUniqueNoCompanyCondition("from WorkflowTask t where t.id=?", taskId);
	}
	
	public List<WorkflowTask> getWorkflowTasks(String instanceId, String taskName) {
		return find("from WorkflowTask t where t.processInstanceId = ? and t.name = ? and t.paused=?", instanceId, taskName,false);
	}

	public WorkflowTask getFirstTaskByInstance(Long companyId, String instanceId, String transactor,Long userId) {
		return findUnique("from WorkflowTask t where t.companyId = ? and t.processInstanceId = ? and ((t.transactor = ? and t.transactorId is null) or (t.transactorId is not null and t.transactorId=?))", 
				companyId, instanceId, transactor,userId);
	}
	
	public void deleteTaskByProcessId(String processInstanceId, Long companyId) {
		//由于WorkflowTask与Task实体是继承关系，在db2数据库中使用hibernate的如下语句无法删除成功，报参数无效，且hibernate打印出的语句为insert into session.HT_WORKFLOW_TASK select ....，
		//并不是删除语句，找不到具体原因，所以改为现在这种写法，为了兼容db2数据库
		//this.createQuery("delete  from WorkflowTask t where  t.processInstanceId = ? and t.companyId = ? ", processInstanceId,companyId).executeUpdate();
		//mysql、oracle、sqlserver数据库使用如上写法都没有问题。
		String sql = "select id  from workflow_task  where id in (select pt.id from product_task pt where pt.company_id = "+companyId+") and process_instance_id = '"+processInstanceId+"'";
		List ids = jdbcDao.excutionSql(sql);
		String idStr = changeIdListToStr(ids);
		if(StringUtils.isNotEmpty(idStr)){
			sql = "delete  from workflow_task  where id in ("+idStr+")";
			jdbcDao.updateTable(sql);
			sql = "delete from product_task   where id  in ("+idStr+")";
			jdbcDao.updateTable(sql);
		}
	}
	
	private String changeIdListToStr(Collection ids){
		if(ids.size()<=0)return null;
		String idStr = ids.toString().toUpperCase();//db2时[{id=111},{id=2}]，mysql时[{ID=111},{ID=2}]
		return idStr.replace("[", "").replace("{ID=", "").replace("}", "").replace("]", "");
	}
	/**
	 * 根据环节名称获得任务
	 * @param companyId
	 * @param instanceId
	 * @param names
	 * @return
	 */
	public List<Long> getTaskIdsByName(Long companyId, String instanceId, String[] names){
		for(String name : names){
			return find("select t.id from WorkflowTask t where t.companyId=? and t.processInstanceId=? and t.name=?", 
					companyId, instanceId,StringUtils.trim(name));
		}
		return new ArrayList<Long>();
	}
	/**
	 * 根据环节名称删除任务
	 * @param companyId
	 * @param instanceId
	 * @param names
	 */
	public void deleteTasksByName(Long companyId, String instanceId, String[] names){
		//由于WorkflowTask与Task实体是继承关系，在db2数据库中使用hibernate的如下语句无法删除成功，报参数无效，且hibernate打印出的语句为insert into session.HT_WORKFLOW_TASK select ....，
		//并不是删除语句，找不到具体原因，所以改为现在这种写法，为了兼容db2数据库
		//this.createQuery(...).executeUpdate();
		//mysql、oracle、sqlserver数据库使用如上写法都没有问题。
		for(String name : names){
			String sql = "select id  from workflow_task  where id in (select pt.id from product_task pt where pt.company_id = "+companyId+" and pt.name = '"+StringUtils.trim(name)+"') and process_instance_id = '"+instanceId+"'";
			List ids = jdbcDao.excutionSql(sql);
			String idStr = changeIdListToStr(ids);
			if(StringUtils.isNotEmpty(idStr)){
				sql = "delete  from workflow_task  where id in ("+idStr+")";
				jdbcDao.updateTable(sql);
				sql = "delete from product_task   where id  in ("+idStr+")";
				jdbcDao.updateTable(sql);
			}
		}
	}
	
	
	public List<WorkflowTask> getTasksByName(Long companyId, String instanceId, String name){
		return  this.find("from WorkflowTask t where t.companyId = ? and t.processInstanceId = ?  and t.name=? and t.effective=? and t.paused=?", 
				companyId, instanceId,name,true,false);
	}
	
	public List<WorkflowTask> getNotCompleteTasksByName(Long companyId, String instanceId, String name){
		return  this.find("from WorkflowTask t where t.companyId = ? and t.processInstanceId = ?  and t.name=? and t.effective=? and (t.active<>? and t.active<>? and t.active<>? and t.active<>?)", 
				companyId, instanceId,name,true,TaskState.COMPLETED.getIndex(), TaskState.CANCELLED.getIndex(),TaskState.ASSIGNED.getIndex(),TaskState.HAS_DRAW_OTHER.getIndex());
	}

	public List<WorkflowTask> getNoAssignTasksByName(Long companyId, String instanceId, String taskName,Integer groupNum) {
		return this.find("from WorkflowTask t where t.companyId = ? and t.processInstanceId = ? and t.name = ? and t.active <> ? and  t.effective=? and t.groupNum=?", 
				companyId, instanceId, taskName, TaskState.ASSIGNED.getIndex(), true,groupNum);
	}
	
	public Page<WorkflowTask> getDelegateTasks(Long companyId, String loginName,Long userId, Page<WorkflowTask> page){
		Page<WorkflowTask> result = new Page<WorkflowTask>(page.getPageSize(), true);
		result.setPageNo(page.getPageNo());
		//result.setOrder(page.getOrder());
		//result.setOrderBy(page.getOrderBy());
		this.findPage(result,"from WorkflowTask t where t.companyId=? and  ((t.trustor=? and t.trustorId is null) or (t.trustorId is not null and t.trustorId=?)) and t.paused=?",companyId, loginName,userId,false);
		page = new Page<WorkflowTask>();
		page.setResult(result.getResult());
		//page.setOrder(result.getOrder());
		//page.setOrderBy(result.getOrderBy());
		page.setPageNo(result.getPageNo());
		page.setPageSize(result.getPageSize());
		page.setTotalCount(result.getTotalCount());
		return page;
	}
	
	public Integer getDelegateTasksNum(Long companyId, String loginName,Long userId){
		Object o = createQuery("select count(t) from WorkflowTask t where t.companyId=? and ((t.trustor=? and t.trustorId is null) or (t.trustorId is not null and t.trustorId=?))  and t.paused=?", companyId, loginName,userId,false).uniqueResult();
		return Integer.valueOf(o.toString());
	}
	
	public Integer getDelegateTasksNum(Long companyId, String loginName,Long userId, Boolean isCompleted){
		String hql = "select count(t) from WorkflowTask t where t.companyId=? and t.effective = true and ((t.trustor=? and t.trustorId is null) or (t.trustorId is not null and t.trustorId=?)) and (t.active=? or t.active=?  or t.active=?  or t.active=?) and t.paused=?";
		Object o = 0;
		if(isCompleted){
			o = createQuery(hql, companyId, loginName,userId, TaskState.COMPLETED.getIndex(), TaskState.CANCELLED.getIndex(),TaskState.ASSIGNED.getIndex(),TaskState.HAS_DRAW_OTHER.getIndex(),false).uniqueResult();
		}else{
			hql = "select count(t) from WorkflowTask t where t.companyId=? and t.effective = true and ((t.trustor=? and t.trustorId is null) or (t.trustorId is not null and t.trustorId=?)) and (t.active=? or t.active=? or t.active=? or t.active=?) and t.paused=? ";
			o = createQuery(hql, companyId, loginName,userId, TaskState.WAIT_TRANSACT.getIndex(), TaskState.WAIT_DESIGNATE_TRANSACTOR.getIndex(),TaskState.DRAW_WAIT.getIndex(),TaskState.WAIT_CHOICE_TACHE.getIndex(),false).uniqueResult();
		}
		return Integer.valueOf(o.toString());
	}
	
	public Integer getTrusteeTasksNum(Long companyId, String loginName,Long userId, Boolean isCompleted){
		String hql = "select count(t) from WorkflowTask t where t.companyId=?  and t.effective = true and ((t.transactor = ? and t.transactorId is null) or (t.transactorId is not null and t.transactorId=?)) and (t.active=? or t.active=?  or t.active=?  or t.active=?) and t.paused=? and t.trustor is not null";
		Object o = 0;
		if(isCompleted){
			o = createQuery(hql, companyId, loginName,userId, TaskState.COMPLETED.getIndex(), TaskState.CANCELLED.getIndex(),TaskState.ASSIGNED.getIndex(),TaskState.HAS_DRAW_OTHER.getIndex(),false).uniqueResult();
		}else{
			hql = "select count(t) from WorkflowTask t where t.companyId=?  and t.effective = true and ((t.transactor = ? and t.transactorId is null) or (t.transactorId is not null and t.transactorId=?)) and (t.active=? or t.active=? or t.active=? or t.active=?) and t.paused=? and t.trustor is not null";
			o = createQuery(hql, companyId, loginName,userId, TaskState.WAIT_TRANSACT.getIndex(), TaskState.WAIT_DESIGNATE_TRANSACTOR.getIndex(),TaskState.DRAW_WAIT.getIndex(),TaskState.WAIT_CHOICE_TACHE.getIndex(),false).uniqueResult();
		}
		return Integer.valueOf(o.toString());
	}
	
	public Page<WorkflowTask> getDelegateTasks(Long companyId, String loginName,Long userId, Page<WorkflowTask> page, boolean isEnd){
		Page<WorkflowTask> result = new Page<WorkflowTask>(page.getPageSize(), true);
		result.setPageNo(page.getPageNo());
		//result.setOrder(page.getOrder());
		//result.setOrderBy(page.getOrderBy());
		String hql = "from WorkflowTask t where t.companyId=? and ((t.trustor=? and t.trustorId is null) or (t.trustorId is not null and t.trustorId=?))  and t.effective = true and (t.active=? or t.active=? or t.active=?  or t.active=?) and t.paused=?  order by t.createdTime desc";
		if(isEnd){
			this.findPage(result,hql,companyId, loginName,userId, TaskState.COMPLETED.getIndex(), TaskState.CANCELLED.getIndex(),TaskState.ASSIGNED.getIndex(),TaskState.HAS_DRAW_OTHER.getIndex(),false);			
		}else{
			hql = "from WorkflowTask t where t.companyId=? and ((t.trustor=? and t.trustorId is null) or (t.trustorId is not null and t.trustorId=?))  and t.effective = true and (t.active=? or t.active=? or t.active=? or t.active=?) and t.paused=?  order by t.createdTime desc";
			this.findPage(result,hql,companyId, loginName,userId, TaskState.WAIT_TRANSACT.getIndex(), TaskState.WAIT_DESIGNATE_TRANSACTOR.getIndex(),TaskState.DRAW_WAIT.getIndex(),TaskState.WAIT_CHOICE_TACHE.getIndex(),false);
		}
		page = new Page<WorkflowTask>();
		page.setResult(result.getResult());
		//page.setOrder(result.getOrder());
		//page.setOrderBy(result.getOrderBy());
		page.setPageNo(result.getPageNo());
		page.setPageSize(result.getPageSize());
		page.setTotalCount(result.getTotalCount());
		return page;
	}
	
	public Page<WorkflowTask> getTaskAsTrustee(Long companyId, String loginName,Long userId, Page<WorkflowTask> page, boolean isEnd){
		Page<WorkflowTask> result = new Page<WorkflowTask>(page.getPageSize(), true);
		result.setPageNo(page.getPageNo());
		String hql = "from WorkflowTask t where t.companyId=? and ((t.transactor = ? and t.transactorId is null) or (t.transactorId is not null and t.transactorId=?)) and t.visible = true and t.effective = true and (t.active=? or t.active=? or t.active=?  or t.active=?) and t.paused=? and t.trustor is not null  order by t.createdTime desc";
		if(isEnd){
			this.findPage(result,hql,companyId, loginName,userId, TaskState.COMPLETED.getIndex(), TaskState.CANCELLED.getIndex(),TaskState.ASSIGNED.getIndex(),TaskState.HAS_DRAW_OTHER.getIndex(),false);			
		}else{
			hql = "from WorkflowTask t where t.companyId=? and  ((t.transactor = ? and t.transactorId is null) or (t.transactorId is not null and t.transactorId=?))  and t.visible = true and t.effective = true and (t.active=? or t.active=? or t.active=? or t.active=?) and t.paused=? and t.trustor is not null  order by t.createdTime desc";
			this.findPage(result,hql,companyId, loginName,userId, TaskState.WAIT_TRANSACT.getIndex(), TaskState.WAIT_DESIGNATE_TRANSACTOR.getIndex(),TaskState.DRAW_WAIT.getIndex(),TaskState.WAIT_CHOICE_TACHE.getIndex(),false);
		}
		page = new Page<WorkflowTask>();
		page.setResult(result.getResult());
		page.setPageNo(result.getPageNo());
		page.setPageSize(result.getPageSize());
		page.setTotalCount(result.getTotalCount());
		return page;
	}

	public List<WorkflowTask> getAllTasksByInstance(Long companyId, String instanceId){
		return this.find("from WorkflowTask t where t.companyId = ? and t.processInstanceId = ? and t.paused=?", 
				companyId, instanceId,false);
	}
	
	/**
	 * 活动该流程实例当前任务,当前任务为待领取或待办理且该任务不是分发给的任务也不是特事特办任务，且任务是有效的
	 * @param instanceId 实例id
	 * @param companyId 公司id
	 * @return 任务列表
	 */
	public List<WorkflowTask> getActivityTasks(String instanceId,Long companyId) {
		if(companyId==null){
			return this.find("from WorkflowTask t where t.processInstanceId = ?  and ( t.active=0 or t.active=4 or t.active=6 or t.active=1) and t.distributable=? and t.effective=?  and t.paused=? order by t.specialTask DESC", 
					 instanceId,false,true,false);
		}else{
			return this.find("from WorkflowTask t where t.companyId = ? and t.processInstanceId = ?  and ( t.active=0 or t.active=4 or t.active=6 or t.active=1) and t.distributable=? and t.effective=?  and t.paused=? order by t.specialTask DESC", 
					companyId, instanceId,false,true,false);
		}
	}
	/**
	 * 活动该流程实例当前任务,当前任务为待领取或待办理且该任务不是分发给的任务也不是特事特办任务，且任务是有效的（不包括当前子流程任务，即任务办理人姓名不为空就是非子流程任务）
	 * @param instanceId 实例id
	 * @param companyId 公司id
	 * @return 任务列表
	 */
	public List<WorkflowTask> getActivityTasksWithoutSubProcessTasks(String instanceId,Long companyId) {
		if(companyId==null){
			return this.find("from WorkflowTask t where t.processInstanceId = ?  and ( t.active=0 or t.active=4 or t.active=6 or t.active=1) and t.distributable=? and t.effective=?  and t.paused=? and t.transactorName is not null  order by t.specialTask DESC", 
					instanceId,false,true,false);
		}else{
			return this.find("from WorkflowTask t where t.companyId = ? and t.processInstanceId = ?  and ( t.active=0 or t.active=4 or t.active=6 or t.active=1) and t.distributable=? and t.effective=?  and t.paused=? and t.transactorName is not null  order by t.specialTask DESC", 
					companyId, instanceId,false,true,false);
		}
	}
	/**
	 * 活动该流程实例当前子流程任务。即所有当前任务中办理人姓名为空的任务就是当前子流程任务
	 * @param instanceId 实例id
	 * @param companyId 公司id
	 * @return 任务列表
	 */
	public List<WorkflowTask> getActivitySubProcessTasks(String instanceId,Long companyId) {
		if(companyId==null){
			return this.find("from WorkflowTask t where t.processInstanceId = ?  and ( t.active=0 or t.active=4 or t.active=6 or t.active=1) and t.distributable=? and t.effective=?  and t.paused=? and t.transactorName is null order by t.specialTask DESC", 
					instanceId,false,true,false);
		}else{
			return this.find("from WorkflowTask t where t.companyId = ? and t.processInstanceId = ?  and ( t.active=0 or t.active=4 or t.active=6 or t.active=1) and t.distributable=? and t.effective=?  and t.paused=? and t.transactorName is null order by t.specialTask DESC", 
					companyId, instanceId,false,true,false);
		}
	}
	/**
	 * 获得该流程实例当前任务的办理人id集合,当前任务为待领取或待办理且该任务不是分发给的任务也不是特事特办任务，且任务是有效的
	 * @param instanceId 实例id
	 * @param companyId 公司id
	 * @return 任务列表
	 */
	public List<Long> getActivityTaskTransactorIds(String instanceId,Long companyId) {
		return this.find("select t.transactorId from WorkflowTask t where t.processInstanceId = ?  and ( t.active=0 or t.active=4 or t.active=6 or t.active=1) and t.distributable=? and t.effective=?  and t.paused=? and t.transactorId is not null", 
				instanceId,false,true,false);
	}
	/**
	 * 活动该流程实例当前任务列表中需要根据实例的催办设置来催办的任务,当前任务为待领取或待办理且该任务不是分发给的任务也不是特事特办任务，且任务是有效的且催办方式 为空(催办方式 为空说明是历史数据)
	 * @param instanceId 实例id
	 * @param companyId 公司id
	 * @return 任务列表
	 */
	public List<WorkflowTask> getActivityReminderTasksByInstance(String instanceId,Long companyId) {
		if(companyId==null){
			return this.find("from WorkflowTask t where t.processInstanceId = ?  and ( t.active=? or t.active=? or t.active=?) and t.distributable=? and t.effective=?  and t.paused=? and t.reminderStyle is  null and t.transferTaskId is  null and t.processingMode!=? order by t.specialTask DESC", 
					instanceId,TaskState.WAIT_DESIGNATE_TRANSACTOR.getIndex(),TaskState.WAIT_TRANSACT.getIndex(),TaskState.WAIT_CHOICE_TACHE.getIndex(),false,true,false,TaskProcessingMode.TYPE_READ);
		}else{
			return this.find("from WorkflowTask t where t.companyId = ? and t.processInstanceId = ?  and ( t.active=? or t.active=? or t.active=?) and t.distributable=? and t.effective=?  and t.paused=? and t.reminderStyle is null  and t.transferTaskId is null and t.processingMode!=? order by t.specialTask DESC", 
					companyId, instanceId,TaskState.WAIT_DESIGNATE_TRANSACTOR.getIndex(),TaskState.WAIT_TRANSACT.getIndex(),TaskState.WAIT_CHOICE_TACHE.getIndex(),false,true,false,TaskProcessingMode.TYPE_READ);
		}
	}
	
	/**
	 * 加签或者减签或移交任务时候取活动任务
	 * @param instanceId
	 * @param companyId
	 * @return
	 */
	public List<WorkflowTask> getActivitySignTasks(String instanceId,Long companyId) {
		if(companyId==null){
			return this.find("from WorkflowTask t where t.processInstanceId = ?  and ( t.active=0 or t.active=4 or t.active=6 or t.active=1) and t.distributable=? and t.effective=?  and t.paused=? and t.processingMode!=? order by t.specialTask DESC", 
					 instanceId,false,true,false,TaskProcessingMode.TYPE_READ);
		}else{
			return this.find("from WorkflowTask t where t.companyId = ? and t.processInstanceId = ?  and ( t.active=0 or t.active=4 or t.active=6 or t.active=1) and t.distributable=? and t.effective=?  and t.paused=? and t.processingMode!=? order by t.specialTask DESC", 
					companyId, instanceId,false,true,false,TaskProcessingMode.TYPE_READ);
		}
	}
	
	/**
	 * 查询办理人的当前任务
	 * @param instanceId
	 * @param companyId
	 * @return
	 */
	public WorkflowTask getMyTask(String instanceId,Long companyId,String loginName) {
		List<WorkflowTask> tasks=this.find("from WorkflowTask t where t.companyId = ? and t.processInstanceId = ? and t.transactor = ? and (t.active=0 or t.active=1 or t.active=4 or t.active=6) and t.paused=?", 
				companyId, instanceId,loginName,false);
		if(tasks.size()>0){
			return tasks.get(0);
		}
		return null;
	}
	/**
	 * 查询办理人的当前任务
	 * @param instanceId
	 * @param companyId
	 * @return
	 */
	public WorkflowTask getMyTask(String instanceId,Long companyId,Long userId) {
		List<WorkflowTask> tasks=this.find("from WorkflowTask t where t.companyId = ? and t.processInstanceId = ? and (t.transactorId is not null and t.transactorId=? ) and (t.active=0 or t.active=1 or t.active=4 or t.active=6) and t.paused=?", 
				companyId, instanceId,userId,false);
		if(tasks.size()>0){
			return tasks.get(0);
		}
		return null;
	}
	
	public List<String> getCountersignByProcessInstanceId(String processInstanceId,TaskProcessingMode processingMode){
		return find( "select distinct t.name from WorkflowTask t where t.processInstanceId=? and t.processingMode=? and t.paused=? ", processInstanceId,processingMode,false);
	}
	
	/**
	 * 自定义流程中取会签环节名称
	 * @param processInstanceId
	 * @param processingMode
	 * @return
	 */
	public List<String> getSignByProcessInstanceId(String processInstanceId,TaskProcessingMode processingMode){
		return find( "select t.name from WorkflowTask t where t.companyId = ? and t.processInstanceId=? and t.processingMode=? and t.paused=? group by t.name ",ContextUtils.getCompanyId(), processInstanceId,processingMode,false);
	}
	
	public List<WorkflowTask> getCountersignByProcessInstanceIdResult(String processInstanceId,String taskName,TaskProcessingResult result){
		return find( "from WorkflowTask t where t.processInstanceId=?  and t.name=? and t.taskProcessingResult=?  and t.paused=? ", processInstanceId,taskName,result,false);
	}
	/**
	 * 获得审批任务组数
	 * @param processInstanceId
	 * @param taskName
	 * @param result
	 * @return
	 */
	public List<Integer> getGroupNumByTaskName(String processInstanceId,String taskName){
		return find( "select t.groupNum from WorkflowTask t where t.processInstanceId=?  and t.name=?   and t.paused=? and t.companyId = ? group by t.groupNum", processInstanceId,taskName,false,ContextUtils.getCompanyId());
	}
	/**
	 * 流程有效的办理人
	 * @param companyId
	 * @param instanceId
	 * @return
	 */
	public List<String> getParticipantsTransactor(Long companyId,
			String instanceId) {
		return find("select t.transactor from WorkflowTask t where t.companyId = ? and t.processInstanceId = ? and t.active=? and t.effective=? and t.paused=?", 
				companyId, instanceId, TaskState.COMPLETED.getIndex(), true,false);
	}
	/**
	 * 流程有效的办理人
	 * @param companyId
	 * @param instanceId
	 * @return
	 */
	public List<Long> getParticipantsTransactorId(Long companyId,
			String instanceId) {
		return find("select t.transactorId from WorkflowTask t where t.companyId = ? and t.processInstanceId = ? and t.active=? and t.effective=? and t.paused=?", 
				companyId, instanceId, TaskState.COMPLETED.getIndex(), true,false);
	}
	
	public List<WorkflowTask> getCountersigns(String instanceId,String taskName){
		return find( "from WorkflowTask t where t.processInstanceId=?  and t.name=? and t.active=0  and t.paused=?", instanceId,taskName,false);
	}
	
	public List<WorkflowTask> getCountersigns(Long taskId,String instanceId,String taskName){
		return find( "from WorkflowTask t where t.processInstanceId=?  and t.name=? and t.active=0 and t.id!=? and t.paused=?", instanceId,taskName,taskId,false);
	}
	
	public List<String> getCountersignsHandler(String instanceId,String taskName,Integer activie){
		return find( " select t.transactor from WorkflowTask t where t.processInstanceId=?  and t.name=? and t.active=? and t.paused=?", instanceId,taskName,activie,false);
	}
	
	public void deleteCountersignHandler(String instanceId,String taskName,Collection<String> users){
		//由于WorkflowTask与Task实体是继承关系，在db2数据库中使用hibernate的如下语句无法删除成功，报参数无效，且hibernate打印出的语句为insert into session.HT_WORKFLOW_TASK select ....，
		//并不是删除语句，找不到具体原因，所以改为现在这种写法，为了兼容db2数据库
		//this.batchExecute(...);
		//mysql、oracle、sqlserver数据库使用如上写法都没有问题。
		StringBuilder transactorInfo = new StringBuilder();
		String sql = "select distinct(wt.id)  from workflow_task wt,product_task pt  where wt.id=pt.id and pt.name = '"+taskName
				+ "' and wt.process_instance_id = '"+instanceId+"' and pt.paused=0 and wt.processing_mode!='"+TaskProcessingMode.TYPE_READ.toString()+"'";
		if(users.size()>0){
			sql =sql+" and (";
		}
		for(String user:users){
			transactorInfo.append("(pt.transactor='"+user+"' or wt.trustor='"+user+"') or ");
		}
		if(transactorInfo.indexOf("or")>=0)sql +=transactorInfo.toString().substring(0,transactorInfo.lastIndexOf("or"));
		if(users.size()>0){
			sql +=")";
		}
		List ids = jdbcDao.excutionSql(sql);
		String idStr = changeIdListToStr(ids);
		if(StringUtils.isNotEmpty(idStr)){
			sql = "delete  from workflow_task  where id in ("+idStr+")";
			jdbcDao.updateTable(sql);
			sql = "delete from product_task   where id  in ("+idStr+")";
			jdbcDao.updateTable(sql);
		}
	}
	public void deleteSignHandler(String instanceId,String taskName,Collection<Long> userIds){
		//由于WorkflowTask与Task实体是继承关系，在db2数据库中使用hibernate的如下语句无法删除成功，报参数无效，且hibernate打印出的语句为insert into session.HT_WORKFLOW_TASK select ....，
		//并不是删除语句，找不到具体原因，所以改为现在这种写法，为了兼容db2数据库
		//this.batchExecute(...);
		//mysql、oracle、sqlserver数据库使用如上写法都没有问题。
		StringBuilder transactorInfo = new StringBuilder();
		String sql = "select distinct(wt.id)  from workflow_task wt,product_task pt  where wt.id=pt.id and pt.name = '"+taskName
				+ "' and wt.process_instance_id = '"+instanceId+"' and pt.paused=0 and wt.processing_mode!='"+TaskProcessingMode.TYPE_READ.toString()+"'";
		if(userIds.size()>0){
			sql =sql+" and (";
		}
		for(Long userid:userIds){
			transactorInfo.append("(pt.transactor_id="+userid+" or t.trustor_id="+userid+") or ");
		}
		if(transactorInfo.indexOf("or")>=0){
			sql +=transactorInfo.toString().substring(0,transactorInfo.lastIndexOf("or"));
		}
		if(userIds.size()>0){
			sql +=")";
		}
		List ids = jdbcDao.excutionSql(sql);
		String idStr = changeIdListToStr(ids);
		if(StringUtils.isNotEmpty(idStr)){
			sql = "delete  from workflow_task  where id in ("+idStr+")";
			jdbcDao.updateTable(sql);
			sql = "delete from product_task   where id  in ("+idStr+")";
			jdbcDao.updateTable(sql);
		}
	}
	
	public void deleteSignHandlerByTaskId(Collection<Long> taskIds){
		//由于WorkflowTask与Task实体是继承关系，在db2数据库中使用hibernate的如下语句无法删除成功，报参数无效，且hibernate打印出的语句为insert into session.HT_WORKFLOW_TASK select ....，
		//并不是删除语句，找不到具体原因，所以改为现在这种写法，为了兼容db2数据库
		//this.batchExecute(...);
		//mysql、oracle、sqlserver数据库使用如上写法都没有问题。
		String idStr = changeIdListToStr(taskIds);
		if(StringUtils.isNotEmpty(idStr)){
			String sql = "delete  from workflow_task  where id in ("+idStr+")";
			jdbcDao.updateTable(sql);
			sql = "delete from product_task   where id  in ("+idStr+")";
			jdbcDao.updateTable(sql);
		}
	}

	public List<String> getHandledTransactors(String workflowId) {
		String hql = "select t.transactor from WorkflowTask t where t.processInstanceId=? and t.active=? and t.effective=?  and t.paused=?";
		return find(hql, workflowId,TaskState.COMPLETED.getIndex(),true,false);
	}
	public List<Long> getHandledTransactorIds(String workflowId) {
		String hql = "select t.transactorId from WorkflowTask t where t.processInstanceId=? and t.active=? and t.effective=?  and t.paused=?";
		return find(hql, workflowId,TaskState.COMPLETED.getIndex(),true,false);
	}
	//获得流程所有办理人
	public List<String> getAllHandleTransactors(String workflowId) {
		String hql = "select t.transactor from WorkflowTask t where t.processInstanceId=? and t.effective=? and t.paused=?";
		return find(hql, workflowId,true,false);
	}
	//获得流程所有办理人
	public List<Long> getAllHandleTransactorIds(String workflowId) {
		String hql = "select t.transactorId from WorkflowTask t where t.processInstanceId=? and t.effective=? and t.paused=?";
		return find(hql, workflowId,true,false);
	}

	/**
	 * 获得需要催办的任务列表
	 * @return
	 */
	public List<WorkflowTask> getNeedReminderTasks() {
		String hql = "from Task t where (t.active=? or t.active=? or t.active=?) and t.reminderStyle is not null and t.transactor is not null  and t.paused=? and t.transferTaskId is null and t.processingMode!=?";
		return find(hql, TaskState.WAIT_DESIGNATE_TRANSACTOR.getIndex(),TaskState.WAIT_TRANSACT.getIndex(),TaskState.WAIT_CHOICE_TACHE.getIndex(),false,TaskProcessingMode.TYPE_READ);
	}

	public List<WorkflowTask> getCompletedTasks(String workflowId,
			Long companyId) {
		 String hql = "from WorkflowTask t where t.processInstanceId=? and t.companyId=? and t.active=?  and t.paused=? order by t.id";
		return find(hql, workflowId,companyId,TaskState.COMPLETED.getIndex(),false);
	}

	public List<WorkflowTask> getNeedReminderTasks(String loginName,Long userId,
			Long companyId) {
		String hql = "from Task t where (t.active=? or t.active=? or t.active=?) and t.duedate<>0 and t.reminderStyle is not null and ((t.transactor = ? and t.transactorId is null) or (t.transactorId is not null and t.transactorId=?)) and t.companyId=?  and t.paused=?";
		return find(hql, TaskState.WAIT_DESIGNATE_TRANSACTOR.getIndex(),TaskState.WAIT_TRANSACT.getIndex(),TaskState.WAIT_CHOICE_TACHE.getIndex(),loginName,userId,companyId,false);
	}

	public List<WorkflowTask> getTasksOrderByWdfName(String definitionName,
			String loginName) {
		Assert.notNull(ContextUtils.getCompanyId(),"查询流程定义中某个办理人的任务时，公司id不能为null");
		String hql = " from Task t where t.companyId = ? and t.transactor = ? and t.visible = true and (t.active=? or t.active=? or t.active=?  or t.active=?) and t.groupName=?  and t.paused=? order by t.createdTime desc";
		return find(hql,ContextUtils.getCompanyId(), loginName,TaskState.DRAW_WAIT.getIndex(), TaskState.WAIT_TRANSACT.getIndex(), TaskState.WAIT_DESIGNATE_TRANSACTOR.getIndex(),TaskState.WAIT_CHOICE_TACHE.getIndex(),definitionName,false);
	}
	public List<WorkflowTask> getTasksOrderByWdfName(String definitionName,
			Long userId) {
		Assert.notNull(ContextUtils.getCompanyId(),"查询流程定义中某个办理人的任务时，公司id不能为null");
		String hql = " from Task t where t.companyId = ? and (t.transactorId is not null and t.transactorId=?) and t.visible = true and (t.active=? or t.active=? or t.active=?  or t.active=?) and t.groupName=?  and t.paused=? order by t.createdTime desc";
		return find(hql,ContextUtils.getCompanyId(), userId,TaskState.DRAW_WAIT.getIndex(), TaskState.WAIT_TRANSACT.getIndex(), TaskState.WAIT_DESIGNATE_TRANSACTOR.getIndex(),TaskState.WAIT_CHOICE_TACHE.getIndex(),definitionName,false);
	}
	public List<WorkflowTask> getCompletedTasksByTaskName(String workflowId,
			Long companyId,String taskName) {
		 String hql = "from WorkflowTask t where t.processInstanceId=? and t.companyId=? and t.active=? and t.name=?  and t.paused=? order by t.id";
		return find(hql, workflowId,companyId,TaskState.COMPLETED.getIndex(),taskName,false);
	}
	public Integer getNotCompleteTasksNumByTransactor(Long companyId, String loginName){
		return Integer.parseInt(createQuery(
				"select count(t) from WorkflowTask t where t.companyId = ? and t.visible=true and  t.transactor = ? and (t.active=? or t.active=? or t.active=?  or t.active=?)  and t.paused=?", 
				companyId, loginName, TaskState.DRAW_WAIT.getIndex(), TaskState.WAIT_TRANSACT.getIndex(), TaskState.WAIT_DESIGNATE_TRANSACTOR.getIndex(),TaskState.WAIT_CHOICE_TACHE.getIndex(),false).uniqueResult().toString());
	}
	public Integer getNotCompleteTasksNumByTransactor(Long companyId, Long userId){
		return Integer.parseInt(createQuery(
				"select count(t) from WorkflowTask t where t.companyId = ? and t.visible=true and  (t.transactorId is not null and t.transactorId=?) and (t.active=? or t.active=? or t.active=?  or t.active=?)  and t.paused=?", 
				companyId, userId, TaskState.DRAW_WAIT.getIndex(), TaskState.WAIT_TRANSACT.getIndex(), TaskState.WAIT_DESIGNATE_TRANSACTOR.getIndex(),TaskState.WAIT_CHOICE_TACHE.getIndex(),false).uniqueResult().toString());
	}
	/**
	 * 查找公司中所有的超期任务
	 * @param companyId
	 * @return
	 */
	public List<WorkflowTask> getOverdueTasks(Long companyId) {
		 String hql = "from WorkflowTask t where t.companyId=? and (t.active=? or t.active=? or t.active=?  or t.active=?) and t.lastReminderTime is not null  and t.paused=?  order by t.createdTime desc";
		return find(hql, companyId,TaskState.DRAW_WAIT.getIndex(), TaskState.WAIT_TRANSACT.getIndex(), TaskState.WAIT_DESIGNATE_TRANSACTOR.getIndex(),TaskState.WAIT_CHOICE_TACHE.getIndex(),false);
	}
	
	/**
	 * 查找当前办理人所有的超期任务的总数
	 * @param companyId
	 * @param transactorName
	 * @return
	 */
	public Integer getOverdueTasksNumByTransactor(Long companyId,String transactorName,Long transactorId) {
		return Integer.parseInt(createQuery(
				"select count(t) from WorkflowTask t where t.companyId=? and (t.active=? or t.active=? or t.active=?  or t.active=?) and ((t.transactor = ? and t.transactorId is null) or (t.transactorId is not null and t.transactorId=?)) and t.lastReminderTime is not null  and t.paused=?", 
				companyId, TaskState.DRAW_WAIT.getIndex(), TaskState.WAIT_TRANSACT.getIndex(), TaskState.WAIT_DESIGNATE_TRANSACTOR.getIndex(),TaskState.WAIT_CHOICE_TACHE.getIndex(),transactorName,transactorId,false).uniqueResult().toString());
	}
	/**
	 * 查找公司中所有的超期任务,包括已完成的任务
	 * @param companyId
	 * @return
	 */
	public List<WorkflowTask> getTotalOverdueTasks(Long companyId) {
		 String hql = "from WorkflowTask t where t.companyId=? and  t.lastReminderTime is not null  and t.paused=? order by t.createdTime desc";
		return find(hql, companyId,false);
	}
	/**
	 * 查找当前办理人所有的超期任务的总数,包括已完成的任务
	 * @param companyId
	 * @param transactorName
	 * @return
	 */
	public Integer getTotalOverdueTasksNumByTransactor(Long companyId,String transactorName) {
		return Integer.parseInt(createQuery(
				"select count(t) from WorkflowTask t where t.companyId=? and t.transactor = ? and t.lastReminderTime is not null  and t.paused=?", 
				companyId, transactorName,false).uniqueResult().toString());
	}
	/**
	 * 查找当前办理人所有的超期任务的总数,包括已完成的任务
	 * @param companyId
	 * @param transactorId
	 * @return
	 */
	public Integer getTotalOverdueTasksNumByTransactorId(Long companyId,Long transactorId) {
		return Integer.parseInt(createQuery(
				"select count(t) from WorkflowTask t where t.companyId=? and (t.transactorId is not null and t.transactorId=?) and t.lastReminderTime is not null  and t.paused=?", 
				companyId, transactorId,false).uniqueResult().toString());
	}
	
	/**
	 * 获得“他人已领取”状态的任务
	 * @param companyId   公司id
	 * @param instanceId  流程实例id
	 * @param name        任务名称
	 * @return
	 */
	public List<WorkflowTask> getHasDrawOtherTasks(Long companyId, String instanceId, String name ) {
		 String hql = "from WorkflowTask t where t.processInstanceId=? and t.companyId=? and t.name=? and t.active=?  and t.paused=?  order by t.id";
		return find(hql, instanceId,companyId,name,TaskState.HAS_DRAW_OTHER.getIndex(),false);
	}
	
	/**
	 * 分页查询用户所有未完成任务
	 * @param page
	 */
	public void getAllTasksByUser(Long companyId, String loginName, Page<WorkflowTask> page){
			String hql="from WorkflowTask t where t.companyId = ? and t.transactor = ?  and t.visible = true and (t.active=? or t.active=? or t.active=?  or t.active=?)  and t.paused=? order by t.createdTime desc";
			this.searchPageByHql(page, hql.toString(),companyId, loginName,TaskState.DRAW_WAIT.getIndex(), TaskState.WAIT_TRANSACT.getIndex(), TaskState.WAIT_DESIGNATE_TRANSACTOR.getIndex(),TaskState.WAIT_CHOICE_TACHE.getIndex(),false);
	}
	/**
	 * 分页查询用户所有未完成任务
	 * @param page
	 */
	public void getAllTasksByUser(Long companyId, Long userId, Page<WorkflowTask> page){
		String hql="from WorkflowTask t where t.companyId = ? and (t.transactorId is not null and t.transactorId=?) and t.visible = true and (t.active=? or t.active=? or t.active=?  or t.active=?)  and t.paused=? order by t.createdTime desc";
		this.searchPageByHql(page, hql.toString(),companyId, userId,TaskState.DRAW_WAIT.getIndex(), TaskState.WAIT_TRANSACT.getIndex(), TaskState.WAIT_DESIGNATE_TRANSACTOR.getIndex(),TaskState.WAIT_CHOICE_TACHE.getIndex(),false);
	}
	
	
	/**
	 * 分页查询用户已完成任务
	 * @param companyId
	 * @param loginName
	 * @param page
	 */
	public void getCompletedTasksByUser(Long companyId, String loginName,Long userId, Page<WorkflowTask> page) {
		String hql="from WorkflowTask t where t.companyId = ? and ((t.transactor = ? and t.transactorId is null) or (t.transactorId is not null and t.transactorId=?)) and t.visible = true and (t.active=? or t.active=? or t.active=? or t.active=?)  and t.paused=? order by t.transactDate desc";
		this.searchPageByHql(page, hql.toString(),companyId, loginName,userId, TaskState.COMPLETED.getIndex(), TaskState.CANCELLED.getIndex(),TaskState.ASSIGNED.getIndex(),TaskState.HAS_DRAW_OTHER.getIndex(),false);
	}
	
	
	/**
	 * 分页查询用户已完成任务
	 * @param companyId
	 * @param loginName
	 * @param page
	 */
	public void getReadTasksByUser(Long companyId, String loginName,Long userId, Page<WorkflowTask> page) {
		findPage(page, "from WorkflowTask t where t.companyId = ? and ((t.transactor = ? and t.transactorId is null) or (t.transactorId is not null and t.transactorId=?)) and t.visible = true and (t.active=? or t.active=?) and t.read=true  and t.paused=?", 
				companyId, loginName,userId, TaskState.WAIT_TRANSACT.getIndex(), TaskState.WAIT_DESIGNATE_TRANSACTOR.getIndex(),false);
	}
	
	/**
	 * 获得所有办理人除当前任务名称的办理人
	 * @param task
	 * @return
	 */
	public List<String> getTransactorsExceptTask(WorkflowTask task){
		String hql="select distinct t.transactor from WorkflowTask t where t.name!=? and t.companyId=? and t.processInstanceId=? and t.active=?  and t.paused=?";
		return this.find(hql, task.getName(),task.getCompanyId(),task.getProcessInstanceId(),TaskState.COMPLETED.getIndex(),false);
	}
	/**
	 * 获得所有办理人除当前任务名称的办理人
	 * @param task
	 * @return
	 */
	public List<Long> getTransactorIdsExceptTask(WorkflowTask task){
		String hql="select distinct t.transactorId from WorkflowTask t where t.name!=? and t.companyId=? and t.processInstanceId=? and t.active=?  and t.paused=?";
		return this.find(hql, task.getName(),task.getCompanyId(),task.getProcessInstanceId(),TaskState.COMPLETED.getIndex(),false);
	}
	
	public List<String> getTransactorsByTask(String name,String transactor,Long transactorId,String processId){
		String hql="select distinct t.transactor from WorkflowTask t where t.name=? and ((t.transactor = ? and t.transactorId is null) or (t.transactorId is not null and t.transactorId=?)) and t.processInstanceId=? and t.active=?  and t.paused=?";
		return this.find(hql, name,transactor,transactorId,processId,TaskState.WAIT_TRANSACT.getIndex(),false);
	}
	/**
	 * 根据“任务组”查询任务列表
	 * @param companyId
	 * @param instanceId
	 * @param taskName
	 * @return
	 */
	public List<WorkflowTask> getTaskOrderByGroupNum(Long companyId,String instanceId,String taskName){
		String hql="from WorkflowTask t where t.name=? and t.companyId=? and t.processInstanceId=? and t.groupNum!=null  and t.paused=? order by t.groupNum desc";
		return this.find(hql,taskName,companyId,instanceId,false);
	}
	
	/**
	 * 活动该流程实例当前任务,当前任务为待领取或待办理且该任务不是分发给的任务也不是特事特办任务，且任务是有效的
	 * @param instanceId 实例id
	 * @param companyId 公司id
	 * @return 任务列表
	 */
	public List<WorkflowTask> getActivityTasksByName(String instanceId,Long companyId,String taskName) {
		if(companyId==null){
			return this.find("from WorkflowTask t where t.processInstanceId = ?  and ( t.active=0 or t.active=4 or t.active=6) and t.distributable=? and t.effective=? and t.name=?  and t.paused=? order by t.specialTask DESC", 
					 instanceId,false,true,taskName,false);
		}else{
			return this.find("from WorkflowTask t where t.companyId = ? and t.processInstanceId = ?  and ( t.active=0 or t.active=4 or t.active=6) and t.distributable=? and t.effective=? and t.name=?  and t.paused=? order by t.specialTask DESC", 
					companyId, instanceId,false,true,taskName,false);
		}
	}
	
	/**
	 * 活动该流程实例当前任务的所有办理人,当前任务不是委托任务
	 * @param instanceId 实例id
	 * @param companyId 公司id
	 * @return 办理人列表
	 */
	public List<String[]> getActivityTaskTransactors(String instanceId,Long companyId) {
		if(companyId==null){
			return this.find("select t.transactor,t.transactorName,t.name from WorkflowTask t where t.processInstanceId = ?  and ( t.active=0 or t.active=4 or t.active=6) and t.distributable=? and t.effective=? and t.trustor=null  and t.paused=? and t.processingMode!=?  order by t.specialTask DESC", 
					 instanceId,false,true,false,TaskProcessingMode.TYPE_READ);
		}else{
			return this.find("select t.transactor,t.transactorName,t.name from WorkflowTask t where t.companyId = ? and t.processInstanceId = ?  and ( t.active=0 or t.active=4 or t.active=6) and t.distributable=? and t.effective=? and t.trustor=null  and t.paused=? and t.processingMode!=?  order by t.specialTask DESC", 
					companyId, instanceId,false,true,false,TaskProcessingMode.TYPE_READ);
		}
	}
	/**
	 * 活动该流程实例当前任务的所有办理人,当前任务不是委托任务
	 * @param instanceId 实例id
	 * @param companyId 公司id
	 * @return 办理人列表
	 */
	public List<WorkflowTask> getActivityTaskByInstance(String instanceId,Long companyId) {
		if(companyId==null){
			return this.find("from WorkflowTask t where t.processInstanceId = ?  and ( t.active=0 or t.active=4 or t.active=6) and t.distributable=? and t.effective=? and t.trustor is null  and t.paused=? and t.processingMode!=?  order by t.specialTask DESC", 
					instanceId,false,true,false,TaskProcessingMode.TYPE_READ);
		}else{
			return this.find("from WorkflowTask t where t.companyId = ? and t.processInstanceId = ?  and ( t.active=0 or t.active=4 or t.active=6) and t.distributable=? and t.effective=? and t.trustor is null  and t.paused=? and t.processingMode!=?  order by t.specialTask DESC", 
					companyId, instanceId,false,true,false,TaskProcessingMode.TYPE_READ);
		}
	}
	
	/**
	 * 活动该流程实例当前任务的所有委托人,当前任务是委托任务
	 * @param instanceId 实例id
	 * @param companyId 公司id
	 * @return 办理人列表
	 */
	public List<String> getActivityTaskPrincipals(String instanceId,Long companyId) {
		if(companyId==null){
			return this.find("select t.trustor from WorkflowTask t where t.processInstanceId = ?  and ( t.active=0 or t.active=4 or t.active=6) and t.distributable=? and t.effective=? and t.trustor!=null  and t.paused=?  and t.processingMode!=?  order by t.specialTask DESC", 
					 instanceId,false,true,false,TaskProcessingMode.TYPE_READ);
		}else{
			return this.find("select t.trustor from WorkflowTask t where t.companyId = ? and t.processInstanceId = ?  and ( t.active=0 or t.active=4 or t.active=6) and t.distributable=? and t.effective=? and t.trustor!=null  and t.paused=?  and t.processingMode!=?    order by t.specialTask DESC", 
					companyId, instanceId,false,true,false,TaskProcessingMode.TYPE_READ);
		}
	}
	/**
	 * 活动该流程实例当前任务的所有委托人,当前任务是委托任务
	 * @param instanceId 实例id
	 * @param companyId 公司id
	 * @return 办理人列表
	 */
	public List<Long> getActivityTaskPrincipalIds(String instanceId,Long companyId) {
		if(companyId==null){
			return this.find("select t.trustorId from WorkflowTask t where t.processInstanceId = ?  and ( t.active=0 or t.active=4 or t.active=6) and t.distributable=? and t.effective=? and t.trustor is not null  and t.paused=?  and t.processingMode!=?  order by t.specialTask DESC", 
					instanceId,false,true,false,TaskProcessingMode.TYPE_READ);
		}else{
			return this.find("select t.trustorId from WorkflowTask t where t.companyId = ? and t.processInstanceId = ?  and ( t.active=0 or t.active=4 or t.active=6) and t.distributable=? and t.effective=? and t.trustor is not null  and t.paused=?  and t.processingMode!=?    order by t.specialTask DESC", 
					companyId, instanceId,false,true,false,TaskProcessingMode.TYPE_READ);
		}
	}
	/**
	 * 活动该流程实例当前任务的所有委托人,当前任务是委托任务
	 * @param instanceId 实例id
	 * @param companyId 公司id
	 * @return 办理人列表[loginName,name]
	 */
	public List<String[]> getActivityTaskPrincipalsDetail(String instanceId,Long companyId) {
		if(companyId==null){
			return this.find("select t.trustor,t.trustorName from WorkflowTask t where t.processInstanceId = ?  and ( t.active=0 or t.active=4 or t.active=6) and t.distributable=? and t.effective=? and t.trustor is not null  and t.paused=?  and t.processingMode!=?  order by t.specialTask DESC", 
					instanceId,false,true,false,TaskProcessingMode.TYPE_READ);
		}else{
			return this.find("select t.trustor,t.trustorName from WorkflowTask t where t.companyId = ? and t.processInstanceId = ?  and ( t.active=0 or t.active=4 or t.active=6) and t.distributable=? and t.effective=? and t.trustor is not null  and t.paused=?  and t.processingMode!=?    order by t.specialTask DESC", 
					companyId, instanceId,false,true,false,TaskProcessingMode.TYPE_READ);
		}
	}
	
	/**
	 * 活动该流程实例当前任务的所有委托人,当前任务是委托任务
	 * @param instanceId 实例id
	 * @param companyId 公司id
	 * @return 
	 */
	public List<WorkflowTask> getActivityPrincipalTask(String instanceId,Long companyId) {
		if(companyId==null){
			return this.find("from WorkflowTask t where t.processInstanceId = ?  and ( t.active=0 or t.active=4 or t.active=6) and t.distributable=? and t.effective=? and t.trustor is not null  and t.paused=?  and t.processingMode!=?  order by t.specialTask DESC", 
					instanceId,false,true,false,TaskProcessingMode.TYPE_READ);
		}else{
			return this.find("from WorkflowTask t where t.companyId = ? and t.processInstanceId = ?  and ( t.active=0 or t.active=4 or t.active=6) and t.distributable=? and t.effective=? and t.trustor is not null  and t.paused=?  and t.processingMode!=?    order by t.specialTask DESC", 
					companyId, instanceId,false,true,false,TaskProcessingMode.TYPE_READ);
		}
	}
	
	public List<String> getCompletedTaskNames(String workflowId,
			Long companyId) {
		 String hql = "select t.name from WorkflowTask t where t.processInstanceId=? and t.companyId=? and t.active=?  and t.paused=? order by t.id";
		return find(hql, workflowId,companyId,TaskState.COMPLETED.getIndex(),false);
	}
	/**
	 * 获得实例中暂停的任务
	 * @param workflowId
	 * @param companyId
	 * @return
	 */
	public List<WorkflowTask> getPauseTasksByInstance(String workflowId,
			Long companyId){
		 String hql = "from WorkflowTask t where t.processInstanceId=? and t.companyId=? and t.paused=?";
			return find(hql, workflowId,companyId,true);
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
		StringBuilder hql = new StringBuilder("select wt ");
		hql.append(" from ").append("  WorkflowTask wt,WorkflowInstance wi  ").append("where wi.processInstanceId=wt.processInstanceId and wi.processInstanceId=wt.executionId and wi.processState<>? and wt.companyId = ? and ( wt.active=0 or wt.active=4 ) and wt.distributable=? and wt.effective=? ");
		List<Object> objs = new ArrayList<Object>();
		objs.add(ProcessState.UNSUBMIT);
		objs.add(ContextUtils.getCompanyId());
		objs.add(false);
		objs.add(true);
		if(wfdId!=null && wfdId.intValue() != 0){
			hql.append("and wi.workflowDefinitionId=? ");
			objs.add(wfdId);
		}
		if(typeId!=null && typeId.intValue() != 0){
			hql.append("and wi.typeId = ? ");
			objs.add(typeId);
		}
		if(StringUtils.isNotEmpty(defCode)){
			hql.append("and wi.processCode=? ");
			objs.add(defCode);
		}
		hql.append(" order by wt.transactDate desc ");
		this.searchPageByHql(tasks, hql.toString(),objs.toArray());
	}
	
	/**
	 * 根据实例集合活动未办理的任务
	 * @param instanceIds 实例id的集合
	 * @param taskName 任务名称
	 * @param recieveUser 委托的受托人登录名
	 * @param consignor 委托的委托人登录名
	 * @return
	 */
	public List<WorkflowTask> getTasksByInstance(List<String> instanceIds,String taskName,String recieveUser,Long recieveId,String consignor,Long consignorId, Long companyId){
		StringBuilder hql=new StringBuilder("from WorkflowTask t where t.companyId = ? and ( t.active=0 or t.active=4 or t.active=1) and t.distributable=? and t.effective=? and t.paused=? and ((t.transactor = ? and t.transactorId is null) or (t.transactorId is not null and t.transactorId=?)) and ((t.trustor = ? and t.trustorId is null) or (t.trustorId is not null and t.trustorId=?)) ");
		Object[] objs=new Object[8+instanceIds.size()] ;
		if(StringUtils.isNotEmpty(taskName)&&!"0".equals(taskName)){
			objs=new Object[9+instanceIds.size()];
		}
		int i=0;
		objs[0]=companyId;
		objs[1]=false;
		objs[2]=true;
		objs[3]=false;
		objs[4]=recieveUser;
		objs[5]=recieveId;
		objs[6]=consignor;
		objs[7]=consignorId;
		i=8;
		if(StringUtils.isNotEmpty(taskName)&&!"0".equals(taskName)){
			hql.append("and t.name=? ");
			objs[8]=taskName;
			i=9;
		}
		if(instanceIds.size()>0){
			hql.append("and (");
		}
		int j=0;
		for(String instanceId:instanceIds){
			hql.append(" t.processInstanceId=? ");
			if(j<instanceIds.size()-1)hql.append("or ");
			objs[i++]=instanceId;
			j++;
		}
		if(instanceIds.size()>0){
			hql.append(")");
		}
		return this.find(hql.toString(), objs);
	}
	
	
	public List<String> getActiveTaskNameWithoutSpecial(String instanceId){
			return this.find("select distinct(t.name) from WorkflowTask t where t.processInstanceId = ?  and ( t.active=0 or t.active=4 or t.active=6 or t.active=1) and t.distributable=? and t.effective=?  and t.paused=? and t.specialTask=?", 
					 instanceId,false,true,false,false);
	}
	/**
	 * 根据用户获得自己所有已完成的流程名称
	 * @param companyId
	 * @param loginName
	 * @return
	 */
	public List<Object[]> getAllCompleteTaskGroupNames(Long companyId,String loginName,Long userId){
		String hql="select t.groupName,count(t.groupName) from WorkflowTask t where t.companyId = ? and ((t.transactor = ? and t.transactorId is null) or (t.transactorId is not null and t.transactorId=?)) and t.visible = true and t.active=? and t.paused=? and t.groupName!=null group by t.groupName";
		return find(hql, companyId, loginName,userId,TaskState.COMPLETED.getIndex(),false);
	}
	/**
	 * 根据用户获得自己所有已完成的流程名称
	 * @param companyId
	 * @param loginName
	 * @return
	 */
	public List<Object[]> getAllCancelTaskGroupNames(Long companyId,String loginName,Long userId){
		String hql="select t.groupName,count(t.groupName) from WorkflowTask t where t.companyId = ? and ((t.transactor = ? and t.transactorId is null) or (t.transactorId is not null and t.transactorId=?)) and t.visible = true and (t.active=? or t.active=?) and t.paused=? and t.groupName!=null group by t.groupName";
		return find(hql, companyId, loginName,userId,TaskState.CANCELLED.getIndex(),TaskState.ASSIGNED.getIndex(),false);
	}
	
	/**
	 * 根据用户获得自己所有流程名称
	 * @param companyId
	 * @param loginName
	 * @return
	 */
	public List<Object[]> getAllActiveTaskGroupNames(Long companyId,String loginName,Long userId){
		String hql="select t.groupName,count(t.groupName) from WorkflowTask t where  t.companyId = ? and ((t.transactor = ? and t.transactorId is null) or (t.transactorId is not null and t.transactorId=?)) and t.visible = true and (t.active=? or t.active=? or t.active=? or t.active=?) and t.paused=? and t.groupName!=null group by t.groupName";
		return find(hql, companyId, loginName,userId,TaskState.DRAW_WAIT.getIndex(), TaskState.WAIT_TRANSACT.getIndex(), TaskState.WAIT_DESIGNATE_TRANSACTOR.getIndex(),TaskState.WAIT_CHOICE_TACHE.getIndex(),false);
	}
	/**
	 * 根据用户获得自己所有已完成的流程名称
	 * @param companyId
	 * @param loginName
	 * @return
	 */
	public List<Object[]> getAllCompleteTaskCustomTypes(Long companyId,String loginName,Long userId){
		String hql="select t.customType,count(t.customType) from WorkflowTask t where t.companyId = ? and ((t.transactor = ? and t.transactorId is null) or (t.transactorId is not null and t.transactorId=?)) and t.visible = true and t.active=? and t.paused=? and t.customType!=null group by t.customType";
		return find(hql, companyId, loginName,userId,TaskState.COMPLETED.getIndex(),false);
	}
	/**
	 * 根据用户获得自己所有已完成的流程名称
	 * @param companyId
	 * @param loginName
	 * @return
	 */
	public List<Object[]> getAllCancelTaskCustomTypes(Long companyId,String loginName,Long userId){
		String hql="select t.customType,count(t.customType) from WorkflowTask t where t.companyId = ? and ((t.transactor = ? and t.transactorId is null) or (t.transactorId is not null and t.transactorId=?)) and t.visible = true and (t.active=? or t.active=?) and t.paused=? and t.customType!=null group by t.customType";
		return find(hql, companyId, loginName,userId, TaskState.CANCELLED.getIndex(),TaskState.ASSIGNED.getIndex(),false);
	}
	
	/**
	 * 根据用户获得自己所有流程名称
	 * @param companyId
	 * @param loginName
	 * @return
	 */
	public List<Object[]> getAllActiveTaskCustomTypes(Long companyId,String loginName,Long userId){
		String hql="select t.customType,count(t.customType) from WorkflowTask t where  t.companyId = ? and ((t.transactor = ? and t.transactorId is null) or (t.transactorId is not null and t.transactorId=?)) and t.visible = true and (t.active=? or t.active=? or t.active=? or t.active=?) and t.paused=? and t.customType!=null group by t.customType";
		return find(hql, companyId, loginName,userId,TaskState.DRAW_WAIT.getIndex(), TaskState.WAIT_TRANSACT.getIndex(), TaskState.WAIT_DESIGNATE_TRANSACTOR.getIndex(),TaskState.WAIT_CHOICE_TACHE.getIndex(),false);
	}
	/**
	 * 退回功能中需要得到环节名称taskName的最新完成的任务
	 * @param workflowId
	 * @param companyId
	 * @param taskName
	 * @return
	 */
	public WorkflowTask getLastCompletedTaskByTaskName(String workflowId,
			Long companyId,String taskName) {
		 String hql = "from WorkflowTask t where t.processInstanceId=? and t.companyId=? and t.active=? and t.name=?  and t.paused=?  and t.specialTask=? order by t.id desc ";
		List<WorkflowTask> tasks= find(hql, workflowId,companyId,TaskState.COMPLETED.getIndex(),taskName,false,false);
		if(tasks.size()>0) return tasks.get(0);
		return null;
	}
	
	/**
	 * 查询当前环节其它的待办理的任务集合，除当前传过来的任务
	 * @param workflowId 实例id
	 * @param taskId 任务id
	 * @param taskName 任务名称
	 * @return 任务列表
	 */
	public List<WorkflowTask> getActivityTasksByNameWithout(String workflowId,Long taskId,String taskName) {
		return this.find("from WorkflowTask t where  t.processInstanceId = ?  and ( t.active=0 or t.active=4 or t.active=6 or t.active=1) and t.distributable=? and t.effective=?  and t.paused=? and t.id<>? and t.name=? order by t.createdTime DESC", 
				workflowId,false,true,false,taskId,taskName);
	}
	/**
	 * 根据办理人查找待办理的委托任务
	 * @param workflowId
	 * @param transactor
	 * @return
	 */
	public List<WorkflowTask> getActivityTrustorTasksByTransactor(String workflowId,String transactor,Long userId,Long taskId) {
		return this.find("from WorkflowTask t where  t.processInstanceId = ?  and t.active=? and t.visible=? and ((t.transactor = ? and t.transactorId is null) or (t.transactorId is not null and t.transactorId=?)) and t.trustor is not null and t.id<>? and t.distributable=? and t.effective=?  and t.paused=? and t.specialTask=? order by t.createdTime DESC", 
				workflowId,TaskState.WAIT_TRANSACT.getIndex(),false,transactor,userId,taskId,false,true,false,false);
	}
	public List<WorkflowTask> getTaskByCode(String code){
		return find("from WorkflowTask t where t.code=?", code);
	}
	
	/**
	 * 根据流程实例id获取task列表
	 * @param companyId
	 * @param instanceId
	 * @return
	 */
	public List<WorkflowTask> getTasksByInstanceId( String instanceId){
		return this.find("from WorkflowTask t where t.companyId = ? and t.processInstanceId = ? ", 
				ContextUtils.getCompanyId(), instanceId);
	}
	/**
	 * 根据办理人查询当前实例中的任务
	 * @param transactor 办理人登录名
	 * @param workflowId 实例id
	 * @return 
	 */
	public List<WorkflowTask> getTaskByTransactor(String transactor,String workflowId){
		return this.find("from WorkflowTask t where  t.processInstanceId = ?  and t.visible=? and (t.transactor = ?  or t.trustor = ? ) and (t.active<>? and  t.active<>? and  t.active<>?)  and t.distributable=? and t.effective=?  and t.paused=? and t.specialTask=? order by t.createdTime DESC", 
				workflowId,false,transactor,transactor,TaskState.CANCELLED.getIndex(),TaskState.ASSIGNED.getIndex(),TaskState.HAS_DRAW_OTHER.getIndex(),false,true,false,false);
	}
	/**
	 * 根据办理人查询当前实例中的任务
	 * @param userId 办理人id
	 * @param workflowId 实例id
	 * @return 
	 */
	public List<WorkflowTask> getTaskByTransactor(Long userId,String workflowId){
		return this.find("from WorkflowTask t where  t.processInstanceId = ?  and t.visible=? and ((t.transactorId is not null and t.transactorId=?) or (t.trustorId is not null and t.trustorId=?) ) and (t.active<>? and  t.active<>? and  t.active<>?)  and t.distributable=? and t.effective=?  and t.paused=? and t.specialTask=? order by t.createdTime DESC", 
				workflowId,false,userId,userId,TaskState.CANCELLED.getIndex(),TaskState.ASSIGNED.getIndex(),TaskState.HAS_DRAW_OTHER.getIndex(),false,true,false,false);
	}
	
	public List<String> getAllTaskTransactors(String workflowId){
		return this.find("select t.transactor from WorkflowTask t where  t.processInstanceId = ?   and t.distributable=? and t.effective=?  and t.paused=? and t.specialTask=?  order by t.createdTime DESC", 
				workflowId,false,true,false,false);
		
	}
	public List<String> getAllTaskTrustors(String workflowId){
		return this.find("select t.trustor from WorkflowTask t where  t.processInstanceId = ? and t.distributable=? and t.effective=?  and t.paused=? and t.specialTask=? and t.trustor is not null order by t.createdTime DESC", 
				workflowId,false,true,false,false);
		
	}
	
	/**
	 * 查询当前环节的办理人
	 * @param workflowId 实例id
	 * @param taskName 任务名称
	 * @return 办理人列表
	 */
	public List<String> getTransactorsByName(String workflowId,String taskName) {
		return this.find("select t.transactor from WorkflowTask t where  t.processInstanceId = ?  and t.distributable=? and t.effective=?  and t.paused=? and t.name=?", 
				workflowId,false,true,false,taskName);
	}
	/**
	 * 查询当前环节的委托人登录名
	 * @param workflowId 实例id
	 * @param taskName 任务名称
	 * @return 委托人列表
	 */
	public List<String> getTrustorsByName(String workflowId,String taskName) {
		return this.find("select t.trustor from WorkflowTask t where  t.processInstanceId = ?  and t.distributable=? and t.effective=?  and t.paused=? and t.name=? and t.trustor is not null", 
				workflowId,false,true,false,taskName);
	}
	
	
	public List<Long> getAllTaskTransactorIds(String workflowId){
		return this.find("select t.transactorId from WorkflowTask t where  t.processInstanceId = ?   and t.distributable=? and t.effective=?  and t.paused=? and t.specialTask=? and t.transactorId is not null  order by t.createdTime DESC", 
				workflowId,false,true,false,false);
		
	}
	public List<Long> getAllTaskTrustorIds(String workflowId){
		return this.find("select t.trustorId from WorkflowTask t where  t.processInstanceId = ? and t.distributable=? and t.effective=?  and t.paused=? and t.specialTask=? and t.trustorId is not null order by t.createdTime DESC", 
				workflowId,false,true,false,false);
		
	}
	
	/**
	 * 查询当前环节的办理人
	 * @param workflowId 实例id
	 * @param taskName 任务名称
	 * @return 办理人列表
	 */
	public List<Long> getTransactorIdsByName(String workflowId,String taskName) {
		return this.find("select t.transactorId from WorkflowTask t where  t.processInstanceId = ?  and t.distributable=? and t.effective=?  and t.paused=? and t.name=? and t.transactorId is not null", 
				workflowId,false,true,false,taskName);
	}
	/**
	 * 查询当前环节的委托人登录名
	 * @param workflowId 实例id
	 * @param taskName 任务名称
	 * @return 委托人列表
	 */
	public List<Long> getTrustorIdsByName(String workflowId,String taskName) {
		return this.find("select t.trustorId from WorkflowTask t where  t.processInstanceId = ?  and t.distributable=? and t.effective=?  and t.paused=? and t.name=? and t.trustorId is not null", 
				workflowId,false,true,false,taskName);
	}
	
	public void deleteTaskByIds(List<Long> ids){
		if(ids.size()>0){
			//由于WorkflowTask与Task实体是继承关系，在db2数据库中使用hibernate的如下语句无法删除成功，报参数无效，且hibernate打印出的语句为insert into session.HT_WORKFLOW_TASK select ....，
			//并不是删除语句，找不到具体原因，所以改为现在这种写法，为了兼容db2数据库
			//this.batchExecute(...);
			//mysql、oracle、sqlserver数据库使用如上写法都没有问题。
			String idStr = changeIdListToStr(ids);
			if(StringUtils.isNotEmpty(idStr)){
				String sql = "delete  from workflow_task  where id in ("+idStr+")";
				jdbcDao.updateTable(sql);
				sql = "delete from product_task   where id  in ("+idStr+")";
				jdbcDao.updateTable(sql);
			}
		}
	}
	
	/**
	 * 查询所有超期任务
	 */
	public void getOverdueTasks(Page<WorkflowTask> page) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		this.searchPageByHql(page,"from WorkflowTask t where   t.distributable=? and t.effective=?  and t.paused=? and t.lastTransactTime is not null and ((t.transactDate is not null and t.lastTransactTime < t.transactDate and (t.active=? or t.active=?)) or (t.transactDate is null and t.lastTransactTime < ? and (t.active=? or t.active=? or t.active=? or t.active=?)) )  and t.companyId=? order by t.lastTransactTime desc", 
				false,true,false,TaskState.COMPLETED.getIndex(),TaskState.CANCELLED.getIndex(),cal.getTime(),TaskState.WAIT_TRANSACT.getIndex(),TaskState.WAIT_DESIGNATE_TRANSACTOR.getIndex(),TaskState.WAIT_CHOICE_TACHE.getIndex(),TaskState.DRAW_WAIT.getIndex(),ContextUtils.getCompanyId());
	}
	/**
	 * 查询办理人的所有超期任务
	 */
	public void getOverdueTaskDetails(Page<WorkflowTask> page,Long transactorId,String transactor,String transactorName,Date lastTransactTimeStart,Date lastTransactTimeEnd) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		StringBuilder hql = new StringBuilder("from WorkflowTask t where   t.distributable=? and t.effective=?  and t.paused=? and t.lastTransactTime is not null and ((t.transactDate is not null and t.lastTransactTime < t.transactDate and (t.active=? or t.active=?)) or (t.transactDate is null and t.lastTransactTime < ? and (t.active=? or t.active=? or t.active=? or t.active=?)) )  and ((t.transactorId is not null and t.transactorId=?) or (t.transactorId is null and t.transactor=?)) and t.companyId=? ");
		int num = getObjectNum(transactorName, lastTransactTimeStart, lastTransactTimeEnd);
		Object[] objs = new Object[13+num];
		objs[0] = false;
		objs[1] = true;
		objs[2] = false;
		objs[3]= TaskState.COMPLETED.getIndex();
		objs[4]= TaskState.CANCELLED.getIndex();
		objs[5] = cal.getTime();
		objs[6]=TaskState.WAIT_TRANSACT.getIndex();
		objs[7] = TaskState.WAIT_DESIGNATE_TRANSACTOR.getIndex();
		objs[8] = TaskState.WAIT_CHOICE_TACHE.getIndex();
		objs[9] = TaskState.DRAW_WAIT.getIndex();
		objs[10] = transactorId;
		objs[11] = transactor;
		objs[12] = ContextUtils.getCompanyId();
		int i=13;
		searchParma(hql,objs,transactorName,lastTransactTimeStart,lastTransactTimeEnd,i,false);
		hql.append(" order by t.lastTransactTime desc");
		this.searchPageByHql(page,hql.toString(), objs);
	}
	/**
	 * 查询所有超期任务的办理人、委托人、办理人个数
	 */
	public void getOverdueTaskTransactors(Page<Object> page,String transactorName,Date lastTransactTimeStart,Date lastTransactTimeEnd) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		StringBuilder hql = new StringBuilder("select pt.transactor_name,count(pt.transactor_id) as countNum,pt.transactor_id,pt.transactor from " +
				"product_task pt, workflow_task t  where  pt.id=t.id and pt.paused=? and pt.last_transact_time is not null and ((pt.transact_date is not null and pt.last_transact_time < pt.transact_date and (pt.active=? or pt.active=?)) or (pt.transact_date is null and pt.last_transact_time < ? and (pt.active=? or pt.active=? or pt.active=? or pt.active=?)) )  and pt.company_id=?  and t.distributable=? and t.effective=? ");
		int num = getObjectNum(transactorName, lastTransactTimeStart, lastTransactTimeEnd);
		Object[] objs = new Object[11+num];
		objs[0] = false;
		objs[1]= TaskState.COMPLETED.getIndex();
		objs[2]= TaskState.CANCELLED.getIndex();
		objs[3] = cal.getTime();
		objs[4]=TaskState.WAIT_TRANSACT.getIndex();
		objs[5] = TaskState.WAIT_DESIGNATE_TRANSACTOR.getIndex();
		objs[6] = TaskState.WAIT_CHOICE_TACHE.getIndex();
		objs[7] = TaskState.DRAW_WAIT.getIndex();
		objs[8] = ContextUtils.getCompanyId();
		objs[9] = false;
		objs[10] = true;
		int i=11;
		searchParma(hql,objs,transactorName,lastTransactTimeStart,lastTransactTimeEnd,i,true);
		hql.append(" group by pt.transactor_name,pt.transactor_id,pt.transactor order by pt.transactor");
		this.searchPageBySql(page,hql.toString(), objs);
	}
	private void searchParma(StringBuilder hql,Object[] objs,String transactorName,Date lastTransactTimeStart,Date lastTransactTimeEnd,int i,boolean sqlable){
		if(StringUtils.isNotEmpty(transactorName)){
			if(sqlable)hql.append(" and pt.transactor_name like ?");
			else hql.append(" and t.transactorName like ?");
			objs[i] = "%"+transactorName+"%";
			i++;
		}
		if(lastTransactTimeStart!=null){
			if(sqlable)hql.append(" and pt.last_transact_time >= ?");
			else hql.append(" and t.lastTransactTime >=?");
			objs[i] = lastTransactTimeStart;
			i++;
		}
		if(lastTransactTimeEnd!=null){
			if(sqlable)hql.append(" and pt.last_transact_time <= ?");
			else hql.append(" and t.lastTransactTime <=?");
			objs[i] = lastTransactTimeEnd;
			i++;
		}
	}
	private int getObjectNum(String transactorName,Date lastTransactTimeStart,Date lastTransactTimeEnd){
		int num = 0;
		if(StringUtils.isEmpty(transactorName)&&lastTransactTimeStart==null&&lastTransactTimeEnd==null){
			return 0;
		}
		if(StringUtils.isNotEmpty(transactorName)&&lastTransactTimeStart==null&&lastTransactTimeEnd==null){
			return 1;
		}
		if(StringUtils.isEmpty(transactorName)&&lastTransactTimeStart!=null&&lastTransactTimeEnd==null){
			return 1;
		}
		if(StringUtils.isEmpty(transactorName)&&lastTransactTimeStart==null&&lastTransactTimeEnd!=null){
			return 1;
		}
		if(StringUtils.isNotEmpty(transactorName)&&lastTransactTimeStart!=null&&lastTransactTimeEnd==null){
			return 2;
		}
		if(StringUtils.isNotEmpty(transactorName)&&lastTransactTimeStart==null&&lastTransactTimeEnd!=null){
			return 2;
		}
		if(StringUtils.isEmpty(transactorName)&&lastTransactTimeStart!=null&&lastTransactTimeEnd!=null){
			return 2;
		}
		if(StringUtils.isNotEmpty(transactorName)&&lastTransactTimeStart!=null&&lastTransactTimeEnd!=null){
			return 3;
		}
		return num;
	}
	
	/**
	 * 根据taskId集合获得办理人信息集合
	 * @return 办理人列表[transactor,transactorName,trustor,trustorName,trustorId,transactorId]
	 */
	public List<Object[]> getTransactorsByTaskIds(List<Long> taskIds) {
		Object[] obj = new Object[taskIds.size()];
		String hql = "";
		for(int i=0;i<taskIds.size();i++){
			obj[i] = taskIds.get(i);
			hql = hql+" t.id=? ";
			if(i<taskIds.size()-1)hql = hql+" or";
		}
		hql = hql+")";
		return this.find("select t.transactor,t.transactorName,t.trustor,t.trustorName,t.trustorId,t.transactorId from WorkflowTask t where  ("+hql, 
				obj);
	}
	/**
	 * 根据taskId获得移交任务列表
	 * @param taskId
	 * @param page
	 */
	public void getTransferTasksByTaskId(Long taskId,Page<WorkflowTask> page){
		this.searchPageByHql(page,"from WorkflowTask t where  t.transferTaskId=? and  t.distributable=? and t.effective=?   and t.companyId=? order by t.lastTransactTime desc", 
				taskId,false,true,ContextUtils.getCompanyId());
	}
	/**
	 * 获得办理人所有移交的任务个数
	 * @param transactorId
	 * @return
	 */
	public Integer getTransferTaskNumByTransactorId(Long transactorId,String transactorName,Date lastTransactTimeStart,Date lastTransactTimeEnd){
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		StringBuilder hql = new StringBuilder("select count(t.id) from WorkflowTask t where  t.transferabled=? and t.transactorId=? and t.paused=? and t.lastTransactTime is not null and ((t.transactDate is not null and t.lastTransactTime < t.transactDate and (t.active=? or t.active=?)) or (t.transactDate is null and t.lastTransactTime < ? and (t.active=? or t.active=? or t.active=? or t.active=?)) )  and t.companyId=?  and t.distributable=? and t.effective=?");
		int num = getObjectNum(transactorName, lastTransactTimeStart, lastTransactTimeEnd);
		Object[] objs = new Object[13+num];
		objs[0] = true;
		objs[1]= transactorId;
		objs[2] = false;
		objs[3]= TaskState.COMPLETED.getIndex();
		objs[4]= TaskState.CANCELLED.getIndex();
		objs[5] = cal.getTime();
		objs[6] = TaskState.WAIT_TRANSACT.getIndex();
		objs[7] = TaskState.WAIT_DESIGNATE_TRANSACTOR.getIndex();
		objs[8] = TaskState.WAIT_CHOICE_TACHE.getIndex();
		objs[9] = TaskState.DRAW_WAIT.getIndex();
		objs[10] = ContextUtils.getCompanyId();
		objs[11] = false;
		objs[12] = true;
		int i=13;
		searchParma(hql,objs,transactorName,lastTransactTimeStart,lastTransactTimeEnd,i,false);
		
		Object transferTaskNum  = createQuery(hql.toString(),objs ).uniqueResult();
		if(transferTaskNum!=null)return Integer.valueOf(transferTaskNum.toString());
		return 0;
	}
	/**
	 * 获得办理人所有移交的任务
	 * @param transactorId
	 * @return
	 */
	public void getTransferTaskDetails(Page<WorkflowTask> page,
			Long transactorId,String transactorName,Date lastTransactTimeStart,Date lastTransactTimeEnd){
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		StringBuilder hql = new StringBuilder("from WorkflowTask t where  t.transferabled=? and t.transactorId=? and t.paused=? and t.lastTransactTime is not null and ((t.transactDate is not null and t.lastTransactTime < t.transactDate and (t.active=? or t.active=?)) or (t.transactDate is null and t.lastTransactTime < ? and (t.active=? or t.active=? or t.active=? or t.active=?)) )  and t.companyId=?  and t.distributable=? and t.effective=?");
		int num = getObjectNum(transactorName, lastTransactTimeStart, lastTransactTimeEnd);
		Object[] objs = new Object[13+num];
		objs[0] = true;
		objs[1]= transactorId;
		objs[2] = false;
		objs[3]= TaskState.COMPLETED.getIndex();
		objs[4]= TaskState.CANCELLED.getIndex();
		objs[5] = cal.getTime();
		objs[6] = TaskState.WAIT_TRANSACT.getIndex();
		objs[7] = TaskState.WAIT_DESIGNATE_TRANSACTOR.getIndex();
		objs[8] = TaskState.WAIT_CHOICE_TACHE.getIndex();
		objs[9] = TaskState.DRAW_WAIT.getIndex();
		objs[10] = ContextUtils.getCompanyId();
		objs[11] = false;
		objs[12] = true;
		int i=13;
		searchParma(hql,objs,transactorName,lastTransactTimeStart,lastTransactTimeEnd,i,false);
		this.searchPageByHql(page,hql.toString(),objs );
	}
	
	/**
	 * 获得办理人移交任务的数量
	 * @param transactorId
	 * @param isCompleted（true:已完成，false：办理中）
	 * @return
	 */
	public Integer getTransferTasksNum(Long transactorId, Boolean isCompleted){
		String hql = "select count(t) from WorkflowTask t where t.companyId=? and t.effective = true and (t.active=? or t.active=? or t.active=? or t.active=?) and t.paused=? and t.transferId=? ";
		Object o = 0;
		if(isCompleted){
			o = createQuery(hql, ContextUtils.getCompanyId(), TaskState.COMPLETED.getIndex(), TaskState.CANCELLED.getIndex(),TaskState.ASSIGNED.getIndex(),TaskState.HAS_DRAW_OTHER.getIndex(),false,transactorId).uniqueResult();
		}else{
			hql = "select count(t) from WorkflowTask t where t.companyId=? and t.effective = true and (t.active=? or t.active=? or t.active=? or t.active=?) and t.paused=? and t.transferId=? ";
			o = createQuery(hql, ContextUtils.getCompanyId(),TaskState.WAIT_TRANSACT.getIndex(), TaskState.WAIT_DESIGNATE_TRANSACTOR.getIndex(),TaskState.DRAW_WAIT.getIndex(),TaskState.WAIT_CHOICE_TACHE.getIndex(),false,transactorId).uniqueResult();
		}
		return Integer.valueOf(o.toString());
	}

	/**
	 * 获得移交任务
	 * @param page
	 * @param transactorId
	 * @param isCompleted(true:已完成，false：办理中)
	 */
	public void getTransferTasks(Page<WorkflowTask> page,Long transactorId,boolean isCompleted) {
		String hql = "from WorkflowTask t where t.companyId=? and t.effective = true and (t.active=? or t.active=? or t.active=? or t.active=?) and t.paused=? and t.transferId=? order by t.createdTime desc";
		if(isCompleted){
			this.searchPageByHql(page,hql,ContextUtils.getCompanyId(), TaskState.COMPLETED.getIndex(), TaskState.CANCELLED.getIndex(),TaskState.ASSIGNED.getIndex(),TaskState.HAS_DRAW_OTHER.getIndex(),false,transactorId);			
		}else{
			this.searchPageByHql(page,hql,ContextUtils.getCompanyId(), TaskState.WAIT_TRANSACT.getIndex(), TaskState.WAIT_DESIGNATE_TRANSACTOR.getIndex(),TaskState.DRAW_WAIT.getIndex(),TaskState.WAIT_CHOICE_TACHE.getIndex(),false,transactorId);
		}
	}
	
	public void completeOtherTranferTask(Long transferTaskId,Long taskId){
		//由于WorkflowTask与Task实体是继承关系，在db2数据库中使用hibernate的如下语句无法删除成功，报参数无效，且hibernate打印出的语句为insert into session.HT_WORKFLOW_TASK select ....，
		//并不是修改语句，找不到具体原因，所以改为现在这种写法，为了兼容db2数据库
		//this.createQuery(...).executeUpdate();
		//mysql、oracle、sqlserver数据库使用如上写法都没有问题。
		String sql = "update product_task  set active="+TaskState.CANCELLED.getIndex()+" where id in"
				+ " (select t.id from workflow_task t where t.transfer_task_id="+transferTaskId+")"
						+ " and (active="+TaskState.WAIT_TRANSACT.getIndex()+" or active="+TaskState.WAIT_DESIGNATE_TRANSACTOR.getIndex()+" or active="+TaskState.DRAW_WAIT.getIndex()+" or active="+TaskState.WAIT_CHOICE_TACHE.getIndex()+") ";
		jdbcDao.updateTable(sql);
	}
	public List<String> selectOtherTranferTaskTransactorName(Long transferTaskId,Long taskId){
		return find("select t.transactorName from WorkflowTask t where t.transferTaskId=? and t.id!=? and (t.active=? or t.active=? or t.active=? or t.active=?)",transferTaskId,taskId,TaskState.WAIT_TRANSACT.getIndex(), TaskState.WAIT_DESIGNATE_TRANSACTOR.getIndex(),TaskState.DRAW_WAIT.getIndex(),TaskState.WAIT_CHOICE_TACHE.getIndex());
	}
	public List<Long> selectOtherTranferTaskTransactorId(Long transferTaskId,Long taskId){
		return find("select t.transactorId from WorkflowTask t where t.transferTaskId=? and t.id!=? and (t.active=? or t.active=? or t.active=? or t.active=?)",transferTaskId,taskId,TaskState.WAIT_TRANSACT.getIndex(), TaskState.WAIT_DESIGNATE_TRANSACTOR.getIndex(),TaskState.DRAW_WAIT.getIndex(),TaskState.WAIT_CHOICE_TACHE.getIndex());
	}
	public List<Long> selectOtherTranferTaskIds(Long transferTaskId,Long taskId){
		return find("select t.id from WorkflowTask t where t.transferTaskId=? and t.id!=? and (t.active=? or t.active=? or t.active=? or t.active=?)",transferTaskId,taskId,TaskState.WAIT_TRANSACT.getIndex(), TaskState.WAIT_DESIGNATE_TRANSACTOR.getIndex(),TaskState.DRAW_WAIT.getIndex(),TaskState.WAIT_CHOICE_TACHE.getIndex());
	}
	//2014-5-22
	public Integer getAcceptTasksNum( Boolean isCompleted){
		Object num = 0;
		if(isCompleted){
			String hql = "select count(t) from WorkflowTask t where t.companyId=? and t.effective = ? and (t.active=? or t.active=? or t.active=? or t.active=?) and t.paused=? and t.transactorId=? and t.transferTaskId is not null ";
			num=createQuery(hql,ContextUtils.getCompanyId(), true,TaskState.COMPLETED.getIndex(), TaskState.CANCELLED.getIndex(),TaskState.ASSIGNED.getIndex(),TaskState.HAS_DRAW_OTHER.getIndex(),false,ContextUtils.getUserId()).uniqueResult();
		}else{
			String hql = "select count(t) from WorkflowTask t where t.companyId=? and t.effective = ? and (t.active=? or t.active=? or t.active=? or t.active=?) and t.paused=? and t.transactorId=? and t.transferTaskId is not null  ";
			num=createQuery(hql,ContextUtils.getCompanyId(), true,TaskState.WAIT_TRANSACT.getIndex(), TaskState.WAIT_DESIGNATE_TRANSACTOR.getIndex(),TaskState.DRAW_WAIT.getIndex(),TaskState.WAIT_CHOICE_TACHE.getIndex(),false,ContextUtils.getUserId()).uniqueResult();
		}
		if(num!=null)return Integer.parseInt(num.toString());
		return 0;
	}
	//2014-5-22
	public void getTaskAsAccept( Page<WorkflowTask> page, boolean isCompleted){
		if(isCompleted){
			String hql = "from WorkflowTask t where t.companyId=? and t.effective = ? and (t.active=? or t.active=? or t.active=? or t.active=?) and t.paused=? and t.transactorId=? and t.transferTaskId is not null order by t.createdTime desc";
			this.searchPageByHql(page,hql,ContextUtils.getCompanyId(), true,TaskState.COMPLETED.getIndex(), TaskState.CANCELLED.getIndex(),TaskState.ASSIGNED.getIndex(),TaskState.HAS_DRAW_OTHER.getIndex(),false,ContextUtils.getUserId());			
		}else{
			String hql = "from WorkflowTask t where t.companyId=? and t.effective = ? and (t.active=? or t.active=? or t.active=? or t.active=?) and t.paused=? and t.transactorId=? and t.transferTaskId is not null  order by t.createdTime desc";
			this.searchPageByHql(page,hql,ContextUtils.getCompanyId(), true,TaskState.WAIT_TRANSACT.getIndex(), TaskState.WAIT_DESIGNATE_TRANSACTOR.getIndex(),TaskState.DRAW_WAIT.getIndex(),TaskState.WAIT_CHOICE_TACHE.getIndex(),false,ContextUtils.getUserId());
		}
	}
	
	/**
	 * 通过公司id和关闭标识获得系统中所有已完成的任务
	 * @param companyId
	 * @return
	 */
	public List<WorkflowTask> getCompleteTasks(Long companyId,String closeSign) {
		if(StringUtils.isNotEmpty(closeSign)){
			String hql="from WorkflowTask t where t.companyId = ? and t.visible = true and (t.active=? or t.active=? or t.active=? or t.active=?) and t.paused=? and t.closeSign=? order by t.transactDate desc";
			return this.find(hql,companyId,TaskState.COMPLETED.getIndex(),TaskState.CANCELLED.getIndex(),TaskState.ASSIGNED.getIndex(),TaskState.HAS_DRAW_OTHER.getIndex(),false,closeSign);
		}else{
			String hql="from WorkflowTask t where t.companyId = ? and t.visible = true and (t.active=? or t.active=? or t.active=? or t.active=?) and t.paused=? and (t.closeSign is null or t.closeSign=?) order by t.transactDate desc";
			return this.find(hql,companyId,TaskState.COMPLETED.getIndex(),TaskState.CANCELLED.getIndex(),TaskState.ASSIGNED.getIndex(),TaskState.HAS_DRAW_OTHER.getIndex(),false,"");
		}
	}

	/**
	 * 通过公司id和推送标识获得系统中所有当前未办理的任务
	 * @param companyId
	 * @return
	 */
	public List<WorkflowTask> getDidNotDealWithTasks(Long companyId,String pushSign) {
		if(StringUtils.isNotEmpty(pushSign)){
			String hql="from WorkflowTask t where t.companyId = ? and t.visible = true and (t.active=? or t.active=? or t.active=?  or t.active=?) and t.paused=? and t.pushSign=? order by t.createdTime desc";
			return this.find(hql,companyId,TaskState.DRAW_WAIT.getIndex(), TaskState.WAIT_TRANSACT.getIndex(), TaskState.WAIT_DESIGNATE_TRANSACTOR.getIndex(),TaskState.WAIT_CHOICE_TACHE.getIndex(),false,pushSign);
		}else{
			String hql="from WorkflowTask t where t.companyId = ? and t.visible = true and (t.active=? or t.active=? or t.active=?  or t.active=?) and t.paused=? and (t.pushSign is null or t.pushSign=?) order by t.createdTime desc";
			return this.find(hql,companyId,TaskState.DRAW_WAIT.getIndex(), TaskState.WAIT_TRANSACT.getIndex(), TaskState.WAIT_DESIGNATE_TRANSACTOR.getIndex(),TaskState.WAIT_CHOICE_TACHE.getIndex(),false,"");
		}
	}
}
