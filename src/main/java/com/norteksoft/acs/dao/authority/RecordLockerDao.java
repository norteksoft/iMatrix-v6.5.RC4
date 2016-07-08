package com.norteksoft.acs.dao.authority;

import java.util.Date;

import org.springframework.stereotype.Repository;

import com.norteksoft.acs.entity.authority.RecordLocker;
import com.norteksoft.product.orm.hibernate.HibernateDao;

@Repository
public class RecordLockerDao extends HibernateDao<RecordLocker, Long>{
	
	/**
	 * 获取正在编辑数据的人员名称
	 * @param entityName
	 * @param entityId
	 * @return
	 */
	public RecordLocker getEditor(String entityName, Long entityId) {
		String hql = "from RecordLocker t where t.entityName=? and t. entityId=? ";
		return this.findUnique(hql,entityName,entityId);
	}

	/**
	 * 释放被锁的数据
	 * @param userIds
	 */
	public void releaseLockedDataBy(String editorLoginName,String editorCompanyCode) {
		StringBuilder hql=new StringBuilder("delete from RecordLocker t where t.editorLoginName=? and t.editorCompanyCode=?");
		this.batchExecute(hql.toString(), editorLoginName,editorCompanyCode);
	}

}
