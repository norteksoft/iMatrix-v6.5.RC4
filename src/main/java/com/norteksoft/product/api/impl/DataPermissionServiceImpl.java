package com.norteksoft.product.api.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.acs.dao.authority.PermissionExtendBaseDao;
import com.norteksoft.product.api.DataPermissionService;

@Service
@Transactional
public class DataPermissionServiceImpl implements DataPermissionService {
	@Autowired
	private PermissionExtendBaseDao permissionExtendBaseDao;
	
	public boolean deletePermission(Object entity) {
		return permissionExtendBaseDao.deletePermission(entity);
	}

	public boolean updatePermission(Object entity) {
		return permissionExtendBaseDao.updatePermission(entity);
	}

	public boolean viewPermission(Object entity) {
		return permissionExtendBaseDao.viewPermission(entity);
	}

	public void addPermissionCondition(String hql, Object entity) {
		permissionExtendBaseDao.addPermissionCondition(hql,entity);
	}
	
	public void clearDataPermission() {
		permissionExtendBaseDao.clearDataPermission();
	}
}
