package com.norteksoft.wf.engine.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.entity.User;
import com.norteksoft.product.api.utils.BeanUtil;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.task.dao.HistoryWorkflowTaskDao;
import com.norteksoft.task.dao.WorkflowTaskDao;
import com.norteksoft.task.entity.HistoryWorkflowTask;
import com.norteksoft.task.entity.WorkflowTask;
import com.norteksoft.wf.base.enumeration.ProcessState;
import com.norteksoft.wf.engine.dao.HistoryInstanceHistoryDao;
import com.norteksoft.wf.engine.dao.HistoryOpinionDao;
import com.norteksoft.wf.engine.dao.HistoryWorkflowInstanceDao;
import com.norteksoft.wf.engine.dao.InstanceHistoryDao;
import com.norteksoft.wf.engine.dao.OpinionDao;
import com.norteksoft.wf.engine.dao.WorkflowInstanceDao;
import com.norteksoft.wf.engine.entity.HistoryInstanceHistory;
import com.norteksoft.wf.engine.entity.HistoryOpinion;
import com.norteksoft.wf.engine.entity.HistoryWorkflowInstance;
import com.norteksoft.wf.engine.entity.InstanceHistory;
import com.norteksoft.wf.engine.entity.Opinion;
import com.norteksoft.wf.engine.entity.WorkflowInstance;

@Service
@Transactional
public class WorkflowFinishManager {
	@Autowired
	private WorkflowInstanceDao workflowInstanceDao;
	@Autowired
	private WorkflowTaskDao workflowTaskDao;
	@Autowired
	private OpinionDao opinionDao;
	@Autowired
	private InstanceHistoryDao instanceHistoryDao;
	@Autowired
	private HistoryWorkflowTaskDao historyWorkflowTaskDao;
	@Autowired
	private HistoryOpinionDao historyOpinionDao;
	@Autowired
	private HistoryInstanceHistoryDao historyInstanceHistoryDao;
	@Autowired
	private HistoryWorkflowInstanceDao historyWorkflowInstanceDao;
	
	//迁移实例、任务等时，每1千条迁一次
	public void  execute(){
		long a = System.currentTimeMillis();
		try {
			Page<WorkflowInstance> wiPages = new Page<WorkflowInstance>(1000,true);
			workflowInstanceDao.getEndAndCancelProcess(wiPages);
			List<WorkflowInstance> workflowInstances = wiPages.getResult();
			for (int i=0 ; i<workflowInstances.size();i++) {
				System.out.println("实例id"+i+":"+workflowInstances.get(i).getProcessInstanceId());
					WorkflowInstance instance  =  workflowInstances.get(i);
					String instanceId = instance.getProcessInstanceId();
					if(instance.getParentProcessId()!=null){//表示有父流程
						//取最外层的主流程
						Object[] parentInstanceInfo  = parentIntanceEnded(instance.getParentProcessId());
						if(parentInstanceInfo!=null){
							ProcessState parentState = (ProcessState)parentInstanceInfo[0];
							//当父流程未结束或未取消时，不迁移数据
							if(parentState!=ProcessState.END && parentState!=ProcessState.MANUAL_END ) continue;
						}
					}
					//获取此流程实例的所有任务，迁移任务
					List<WorkflowTask> tasks = workflowTaskDao.getTasksByInstanceId(instanceId);
					for (WorkflowTask task : tasks) {
						HistoryWorkflowTask historyTask = new HistoryWorkflowTask();
						BeanUtil.copy(historyTask,task);
						historyTask.setId(null);
						historyTask.setSourceTaskId(task.getId());
						historyWorkflowTaskDao.save(historyTask);
					}
					//删除当前任务
					workflowTaskDao.deleteTaskByProcessId(instanceId,instance.getCompanyId());
					//获取此流程实例的所有意见，迁移意见
					List<Opinion> opinions = opinionDao.getAllOpinionsByInstanceId(instanceId, ContextUtils.getCompanyId());
					for (Opinion opinion : opinions) {
						HistoryOpinion historyOpinion = new HistoryOpinion();
						BeanUtil.copy(historyOpinion,opinion);
						historyOpinion.setId(null);
						historyOpinionDao.save(historyOpinion);
					}
					opinionDao.deleteAllOpinionsByWorkflowInstanceId(instanceId, ContextUtils.getCompanyId());
					
					//获取此流程实例的所有流转历史，迁移流转历史
					List<InstanceHistory> instanceHistorys =  instanceHistoryDao.getInstanceHistoryByProcessId(instanceId);
					for (InstanceHistory instanceHistory : instanceHistorys) {
						HistoryInstanceHistory his = new HistoryInstanceHistory();
						BeanUtil.copy(his,instanceHistory);
						his.setId(null);
						historyInstanceHistoryDao.save(his);
					}
					instanceHistoryDao.deleteHistoryByworkflowId(instanceId, ContextUtils.getCompanyId());
					
					//迁移流程实例数据
					HistoryWorkflowInstance historyInstance = new HistoryWorkflowInstance();
					BeanUtil.copy(historyInstance,instance);
					if(instance.getCreatorId()==null){
						User u = ApiFactory.getAcsService().getUserByLoginName(instance.getCreator());
						if(u!=null)historyInstance.setCreatorId(u.getId());
					}
					historyInstance.setId(null);
					historyWorkflowInstanceDao.save(historyInstance);
					workflowInstanceDao.delete(instance.getId());
					//设置实例对应的对应的消息为已读:例如：未完成的抄送任务、实例通知等
					ApiFactory.getPortalService().setMessageReadedByInstanceId(instance.getProcessInstanceId());
					//已结束或已取消的流程实例(包括子流程)
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		long b = System.currentTimeMillis();
		System.out.println("----流程结束定时时间----"+(b-a));
	}

	//如果有子流程，获取最外层的流程
	private Object[] parentIntanceEnded(String parentProcessId) {
		Object[] parentInstanceInfo =	workflowInstanceDao.getParentInstanceInfoByJbpmInstanceId(parentProcessId,ContextUtils.getCompanyId());
		if(parentInstanceInfo!=null){
			parentProcessId = (String)parentInstanceInfo[1];
			parentIntanceEnded(parentProcessId);
		}else{
			parentInstanceInfo =	workflowInstanceDao.getInstanceInfoByJbpmInstanceId(parentProcessId,ContextUtils.getCompanyId());
			return parentInstanceInfo; 
		}
		return parentInstanceInfo; 
	}
}
