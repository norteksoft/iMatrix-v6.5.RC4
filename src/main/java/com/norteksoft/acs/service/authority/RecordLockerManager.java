package com.norteksoft.acs.service.authority;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.acs.dao.authority.RecordLockerDao;
import com.norteksoft.acs.entity.authority.RecordLocker;
import com.norteksoft.acs.service.organization.DepartmentManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.entity.Company;
import com.norteksoft.product.api.entity.Department;
import com.norteksoft.product.api.entity.User;
import com.norteksoft.product.util.ContextUtils;

@Service
@Transactional
public class RecordLockerManager {
	@Autowired
	private RecordLockerDao recordLockerDao;
	@Autowired
	private DepartmentManager departmentManager;
	
	/**
	 * 获取正在编辑数据的人员名称
	 * @param entityName
	 * @param entityId
	 * @return
	 */
	public String getEditor(String entityName, Long entityId) {
		String editorName="no";
		if(entityId!=null && StringUtils.isNotEmpty(entityName)){
			String[] userInformation=packagingCompanyCodeAndUserName();
			RecordLocker recordLocker=recordLockerDao.getEditor(entityName, entityId);
			if(recordLocker!=null){
				if(!(ContextUtils.getLoginName().equals(recordLocker.getEditorLoginName())&&recordLocker.getEditorCompanyCode().equals(userInformation[0]))){
					editorName=recordLocker.getEditorName();
				}
			}else{
				recordLocker=new RecordLocker();
				recordLocker.setEntityName(entityName);
				recordLocker.setEntityId(entityId);
				recordLocker.setEditTime(new Date());
				recordLocker.setEditorLoginName(ContextUtils.getLoginName());
				recordLocker.setEditorCompanyCode(userInformation[0]);
				recordLocker.setEditorName(userInformation[1]);
				recordLockerDao.save(recordLocker);
			}
		}
		return editorName;
	}
	
	private String[] packagingCompanyCodeAndUserName(){
		boolean containBranch=departmentManager.containBranches();
		String userName=ContextUtils.getUserName();
		String companyCode=ContextUtils.getCompanyCode();
		String companyName=ContextUtils.getCompanyName();
		if(containBranch){
			if(StringUtils.isNotEmpty(ContextUtils.getSubCompanyCode())){
				companyCode=ContextUtils.getSubCompanyCode();
				if(StringUtils.isNotEmpty(ContextUtils.getSubCompanyShortTitle())){
					companyName=ContextUtils.getSubCompanyShortTitle();
				}else{
					companyName=ContextUtils.getSubCompanyName();
				}
			}
			userName+="("+companyName+")";
		}
		String[] result={companyCode,userName};
		return result;
	}

	/**
	 * 释放被锁的数据
	 * @param userIds
	 */
	public void releaseLockedDataBy(List<Long> userIds) {
		boolean containBranch=departmentManager.containBranches();
		for(Long userId:userIds){
			User user=ApiFactory.getAcsService().getUserById(userId);
			String companyCode="";
			if(containBranch){
				if(user.getSubCompanyId()!=null){
					Department department=ApiFactory.getAcsService().getDepartmentById(user.getSubCompanyId());
					companyCode=department.getCode();
				}else{
					Company company=ApiFactory.getAcsService().getCompanyById(user.getCompanyId());
					companyCode=company.getCode();
				}
			}else{
				Company company=ApiFactory.getAcsService().getCompanyById(user.getCompanyId());
				companyCode=company.getCode();
			}
			recordLockerDao.releaseLockedDataBy(user.getLoginName(),companyCode);
		}
	}
	
	/**
	 * 定时释放大于等于30分钟被锁的数据
	 */
	public void  timingReleaseLockedData(){
		List<RecordLocker> recordLockers=recordLockerDao.getAll();
		Date currentDate=new Date();
		for(RecordLocker recordLocker:recordLockers){
			long minute=(currentDate.getTime()-recordLocker.getEditTime().getTime())/1000/60;
			if(minute>=30){
				recordLockerDao.delete(recordLocker);
			}
		}
	}
	
}
