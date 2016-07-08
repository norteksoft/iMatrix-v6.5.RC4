package com.norteksoft.portal.dao;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.norteksoft.portal.entity.Message;
import com.norteksoft.product.orm.hibernate.HibernateDao;

@Repository
public class MessageInfoDao extends HibernateDao<Message, Long> {
	public void updateMessageReadedByInstanceId(String instanceId){
		if(StringUtils.isNotEmpty(instanceId))
		  this.createQuery("update Message  set visible=? where uniquely like ? and visible=?", false,"%"+instanceId,true).executeUpdate();
	}
	public void updateMessageReadedByTaskId(Long taskId,Boolean visible){
		if(taskId!=null)
		  this.createQuery("update Message  set visible=? where uniquely like ?  and visible=?", visible,"task-"+taskId+"%",!visible).executeUpdate();
	}
	public void updateMessageReadedByTaskIds(Collection<Long> taskIds){
		if(taskIds!=null&&taskIds.size()>0){
			Object[] objs = new Object[2+taskIds.size()];
			StringBuilder sb = new StringBuilder("update Message  set visible=? where visible=? and (");
			objs[0]=false;
			objs[1]=true;
			int i=2;
			int j=0;
			for(Long taskId:taskIds){
				sb.append(" uniquely like ? ");
				objs[i]="task-"+taskId+"%";
				if(j<taskIds.size()-1)sb.append(" or ");
				j++;
				i++;
			}
			sb.append(")");
			this.createQuery(sb.toString(), objs).executeUpdate();
		}
	}
	
	public void updateMessageUrlById(Long messageId,String url){
		if(messageId!=null)
		  this.createQuery("update Message  set url=? where id=?", url,messageId).executeUpdate();
	}
	public void closeMessage(String uniquely) {
		this.createQuery("update Message set visible=? where uniquely=?", false,uniquely);
	}
	public void setMessageDisplayState(String uniquely, boolean display) {
		this.createQuery("update Message set visible=? where uniquely=?", display,uniquely);
	}
}
