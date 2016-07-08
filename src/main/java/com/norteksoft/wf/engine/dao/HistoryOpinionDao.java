package com.norteksoft.wf.engine.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.norteksoft.product.orm.hibernate.HibernateDao;
import com.norteksoft.task.base.enumeration.TaskProcessingMode;
import com.norteksoft.wf.engine.entity.HistoryOpinion;
import com.norteksoft.wf.engine.entity.Opinion;

@Repository
public class HistoryOpinionDao extends HibernateDao<HistoryOpinion, Long>{
	public List<HistoryOpinion> getOpinions(Long taskId, Long companyId) {
		StringBuilder hql = new StringBuilder("from HistoryOpinion o where o.taskId=? and o.companyId=? and o.controlId is null order by o.createdTime");
		return find(hql.toString(), taskId,companyId);
	}	
	public List<HistoryOpinion> getOpinionsByInstanceId(String workflowId , Long companyId) {
		return find("from HistoryOpinion o where o.workflowId=? and o.companyId=? and o.controlId is null order by o.createdTime", workflowId,companyId);
	}
	
	public void deleteAllHistoryOpinionsByWorkflowInstanceId(String workflowId,Long companyId){
		createQuery("delete from HistoryOpinion o where  o.workflowId = ? and o.companyId = ? ", workflowId,companyId).executeUpdate();
	}
	
	public List<HistoryOpinion> getHistoryOpinionsBytaskId(Long taskId,
			String loginName) {
		return find("from HistoryOpinion o where o.taskId=? and o.creator=? and o.controlId is null order by o.createdTime", taskId,loginName);
	}
	
	public List<HistoryOpinion> getHistoryOpinionsByInstanceId(String workflowId,
			Long companyId) {
		return find("from HistoryOpinion o where o.workflowId=? and o.companyId=? and o.controlId is null order by o.createdTime", workflowId,companyId);
	}
	
	public List<HistoryOpinion> getHistroyOpinions(String workflowId, Long companyId,
			TaskProcessingMode taskMode) {
		StringBuilder hql = new StringBuilder("from HistoryOpinion o where o.workflowId=? and o.companyId=?  and o.taskMode=? and o.controlId is null  order by o.createdTime");
		return find(hql.toString(), workflowId,companyId,taskMode);
	}
	
	public List<HistoryOpinion> getHistoryOpinionsExceptTaskMode(
			String workflowId, Long companyId, TaskProcessingMode taskMode) {
		StringBuilder hql = new StringBuilder("from HistoryOpinion o where o.workflowId=? and o.companyId=?  and o.taskMode<>? and o.controlId is null  order by o.createdTime");
		return find(hql.toString(), workflowId,companyId,taskMode);
	}
	
	public List<HistoryOpinion> getHistoryOpinions(String workflowId,
			Long companyId, String taskName) {
		StringBuilder hql = new StringBuilder("from HistoryOpinion o where o.workflowId=? and o.companyId=?  and o.taskName=? and o.controlId is null  order by o.createdTime");
		return find(hql.toString(), workflowId,companyId,taskName);
	}
	
	public List<HistoryOpinion> getHistoryOpinions(String workflowId,
			Long companyId, List<String> taskNames) {
		StringBuilder hql = new StringBuilder("from HistoryOpinion o where o.workflowId=? and o.companyId=? and o.controlId is null");
		Object[] objs = new Object[taskNames.size()+2];
		if(taskNames.size()>0)hql.append(" and (");
		boolean isFirst=true;
		objs[0]=workflowId;
		objs[1]=companyId;
		for(int i=0;i<taskNames.size();i++){
			if(!isFirst) hql.append(" or ");
			hql.append(" o.taskName=?" );
			isFirst=false;
			objs[i+2]=taskNames.get(i);
		}
		if(taskNames.size()>0)hql.append(")");
		hql.append(" order by o.createdTime");
		return find(hql.toString(),objs);
	}
	
	public List<HistoryOpinion> getHistoryOpinionsExceptTaskName(
			String workflowId, Long companyId, List<String> taskNames) {
		StringBuilder hql = new StringBuilder("from HistoryOpinion o where o.workflowId=? and o.companyId=? and o.controlId is null ");
		Object[] objs = new Object[taskNames.size()+2];
		if(taskNames.size()>0)hql.append(" and (");
		boolean isFirst=true;
		objs[0]=workflowId;
		objs[1]=companyId;
		for(int i=0;i<taskNames.size();i++){
			if(!isFirst) hql.append(" and ");
			hql.append(" o.taskName!=?" );
			isFirst=false;
			objs[i+2]=taskNames.get(i);
		}
		if(taskNames.size()>0)hql.append(")");
		hql.append(" order by o.createdTime");
		return find(hql.toString(),objs);
	}
	
	public List<HistoryOpinion> getHistoryOpinionsByCustomField(
			String workflowId, String customField) {
		String hql = "from HistoryOpinion d where d.workflowId=? and d.customField=? and d.controlId is null order by d.createdTime desc";
		return this.find(hql, workflowId,customField);
	}
	
	public List<HistoryOpinion> getHistoryOpinionsExceptCustomField(
			String workflowId, String customField) {
		String hql = "from Opinion d where HistoryOpinion=? and (d.customField<>? or d.customField is null) and d.controlId is null order by d.createdTime desc";
		return this.find(hql, workflowId,customField);
	}
	
	public HistoryOpinion getHistoryOpinionsById(Long opinionId) {
		return findUnique("from HistoryOpinion o where o.id=?", opinionId);
	}
	
	public List<HistoryOpinion> getHistoryOpinionsByTacheCode(
			String workflowId, Long companyId, List<String> tacheCodes) {
		StringBuilder hql = new StringBuilder("from HistoryOpinion o where o.workflowId=? and o.companyId=? and o.controlId is null");
		Object[] objs = new Object[tacheCodes.size()+2];
		objs[0]=workflowId;
		objs[1]=companyId;
		if(tacheCodes.size()>0)hql.append(" and (");
		boolean isFirst=true;
		int i=2;
		for(String tacheCode:tacheCodes){
			if(!isFirst) hql.append(" or ");
			hql.append(" o.taskCode=?" );
			isFirst=false;
			objs[i++]=tacheCode;
		}
		if(tacheCodes.size()>0)hql.append(")");
		hql.append(" order by o.createdTime");
		return find(hql.toString(),objs);
	}
	public List<HistoryOpinion> getHistoryOpinionsByTaskName(String workflowId,
			Long companyId, List<String> taskNames) {
		StringBuilder hql = new StringBuilder("from HistoryOpinion o where o.workflowId=? and o.companyId=? and o.controlId is null");
		Object[] objs = new Object[taskNames.size()+2];
		objs[0]=workflowId;
		objs[1]=companyId;
		if(taskNames.size()>0)hql.append(" and (");
		boolean isFirst=true;
		int i=2;
		for(String tacheCode:taskNames){
			if(!isFirst) hql.append(" or ");
			hql.append(" o.taskName=?" );
			isFirst=false;
			objs[i++]=tacheCode;
		}
		if(taskNames.size()>0)hql.append(")");
		hql.append(" and o.taskCode=null order by o.createdTime");
		return find(hql.toString(),objs);
	}
	public HistoryOpinion getHistoryOpinion(String instanceId,String controlId, Long companyId) {
		List<HistoryOpinion> opinions = find("from HistoryOpinion o where o.companyId=? and o.workflowId=? and o.controlId=?", companyId,instanceId,controlId);
		if(opinions.size()>0)return opinions.get(0);
		return null;
	}
	
	/**
	 * 根据实例Id和意见控件id获得意见的结论
	 */
	public String getOpinionConclusion(String instanceId, String controlId,Long companyId) {
		List<String> conclusions = find("select o.conclusion from HistoryOpinion o where o.companyId=? and o.workflowId=? and o.controlId=?", companyId,instanceId,controlId);
		if(conclusions.size()>0)return conclusions.get(0);
		return null;
	}
}
