package com.norteksoft.product.api;

/**
 * 数据授权api
 * @author Administrator
 *
 */
public interface DataPermissionService{
	
	/**
	 * 查看权限
	 * @param entity
	 * @return
	 */
	public boolean viewPermission(Object entity);
	
	/**
	 * 修改权限
	 * @param entity
	 * @return
	 */
	public boolean updatePermission(Object entity);
	
	/**
	 * 删除权限
	 * @param entity
	 * @return
	 */
	public boolean deletePermission(Object entity);
	
	/**
	 * 根据实体添加查询条件
	 * @param entity
	 * @return
	 */
	public void addPermissionCondition(String hql, Object entity);
	
	/**
	 * 清除数据权限
	 */
	public void clearDataPermission();
}
