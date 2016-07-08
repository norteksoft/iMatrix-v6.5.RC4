package com.norteksoft.acs.dao.authority;

import java.io.Serializable;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.norteksoft.acs.base.enumeration.LogicOperator;
import com.norteksoft.acs.base.enumeration.PermissionAuthorize;
import com.norteksoft.acs.base.utils.PermissionUtils;
import com.norteksoft.acs.entity.authority.Permission;
import com.norteksoft.acs.entity.authority.PermissionInfo;
import com.norteksoft.mms.form.dao.DataTableDao;
import com.norteksoft.mms.form.entity.DataTable;
import com.norteksoft.product.orm.hibernate.HibernateDao;
import com.norteksoft.product.util.ParameterUtils;
import com.norteksoft.product.util.PermissionThreadParameters;

@Repository
public class PermissionValidator<T, PK extends Serializable> extends HibernateDao<T, PK>{
	@Autowired
	private DataTableDao dataTableDao;
	@Autowired
	private PermissionDao permissionDao;
	
	/**
	 * 查询权限
	 * @param className
	 * @param hql
	 * @param values
	 */
	public void addPermissionCondition(String hql, T entity){
		DataTable table = getDataTableByEntity(entity);
		List<Permission> ps = permissionDao.getPermissionsByDataTableId(table.getId());
		PermissionInfo  permissionInfo = PermissionUtils.getDataRuleByPermission(ps,PermissionAuthorize.SEARCH);
		PermissionThreadParameters parameters=new PermissionThreadParameters();
		if(permissionInfo.isNoPermission()){//表示没有授权，可以查看所有数据
			parameters.setHql(null);
			parameters.setParameters(null);
		}else if(!permissionInfo.isHasPermission()){//授权了，没有权限
			parameters.setHql(PermissionUtils.NO_PERMISSION);
			parameters.setParameters(null);
		}else{
			ConditionResult cr = PermissionUtils.getPermissionHqlPamateters(hql, permissionInfo);
			parameters.setHql(cr.getHql());
			parameters.setParameters(cr.getPrameters());
		}
		ParameterUtils.setPermissionParameters(parameters);
	}
	
	/**
	 * 查看权限
	 * @param entity
	 */
	public boolean viewPermission(T entity){
		try {
			PermissionInfo permissionInfo = getAuthorityDataRule(PermissionAuthorize.SEARCH,entity);
			if(permissionInfo.isNoPermission()){//表示没有授权
				return true;
			}
			if(!permissionInfo.isHasPermission()){//表示有授权但没有权限
				return false;
			}
			return PermissionUtils.entityPermission(entity, permissionInfo,LogicOperator.OR);
		} catch (Exception e) {
			logger.error("Get update permission error. ", e);
		}
		return false;
	}
	
	/**
	 * 修改权限
	 * @param entity
	 */
	public boolean updatePermission(T entity){
		try {
			PermissionInfo permissionInfo = getAuthorityDataRule(PermissionAuthorize.UPDATE,entity);
			if(permissionInfo.isNoPermission()){//表示没有授权
				return true;
			}
			if(!permissionInfo.isHasPermission()){//表示有授权但没有权限
				return false;
			}
			return PermissionUtils.entityPermission(entity,permissionInfo,LogicOperator.OR);
		} catch (Exception e) {
			logger.error("Get update permission error. ", e);
		}
		return false;
	}
	
	/**
	 * 删除权限
	 * @param entity
	 */
	public boolean deletePermission(T entity){
		try {
			PermissionInfo permissionInfo = getAuthorityDataRule(PermissionAuthorize.DELETE,entity);
			if(permissionInfo.isNoPermission()){//表示没有授权
				return true;
			}
			if(!permissionInfo.isHasPermission()){//表示有授权但没有权限
				return false;
			}
			return PermissionUtils.entityPermission(entity, permissionInfo,LogicOperator.OR);
		} catch (Exception e) {
			logger.error("Get update permission error. ", e);
		}
		return false;
	}
	
	/**
	 * 根据操作查询有权限的规则
	 * @param authority 保存,修改,删除
	 * @return
	 */
	protected PermissionInfo getAuthorityDataRule(PermissionAuthorize authority,T entity){
		DataTable table = getDataTableByEntity(entity);
		List<Permission> ps = permissionDao.getPermissionsByDataTableId(table.getId());
		return  PermissionUtils.getDataRuleByPermission(ps,authority);
	}
	
	public void clearDataPermission() {
		PermissionThreadParameters parameters=new PermissionThreadParameters();
		parameters.setClearDataPermission(true);
		parameters.setHql(null);
		parameters.setParameters(null);
		ParameterUtils.setPermissionParameters(parameters);
	}
	
	private DataTable getDataTableByEntity(T entity){
		String className = entity.getClass().getName();
		if(className.contains("_")){
			className =className.substring(0,className.indexOf("_"));
		}
		return dataTableDao.getDataTableByEntity(className);
	}
	
	public static class ConditionResult{
		private String hql;
		private Object[] prameters;
		public String getHql() {
			return hql;
		}
		public void setHql(String hql) {
			this.hql = hql;
		}
		public Object[] getPrameters() {
			return prameters;
		}
		public void setPrameters(Object[] prameters) {
			this.prameters = prameters;
		}
	}
}
