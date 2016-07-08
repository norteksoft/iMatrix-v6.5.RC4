package com.norteksoft.acs.service.organization;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.acs.base.orm.hibernate.SimpleHibernateTemplate;
import com.norteksoft.acs.base.utils.log.LogUtilDao;
import com.norteksoft.acs.entity.authorization.Role;
import com.norteksoft.acs.entity.authorization.RoleWorkgroup;
import com.norteksoft.acs.entity.organization.User;
import com.norteksoft.acs.entity.organization.UserInfo;
import com.norteksoft.acs.entity.organization.Workgroup;
import com.norteksoft.acs.entity.organization.WorkgroupUser;
import com.norteksoft.acs.service.AcsUtils;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;

@SuppressWarnings("deprecation")
@Service
@Transactional
public class WorkGroupManager {
    
	private SimpleHibernateTemplate<Workgroup, Long> workGroupDao;
	private SimpleHibernateTemplate<User,Long> userDao;
	private SimpleHibernateTemplate<UserInfo,Long> userInfoDao;
	private SimpleHibernateTemplate<WorkgroupUser,Long> workGroupToUserDao;
	private SimpleHibernateTemplate<Role,Long> roleDao;
	private SimpleHibernateTemplate<RoleWorkgroup,Long> role_wDao;
	private LogUtilDao logUtilDao;
	private static String hql = "from Workgroup w where w.company.id=? and w.deleted=? order by w.weight desc";
	private static String DELETED = "deleted";
	private static String COMPANYID = "companyId";
	private static String WORKGROUPID = "workgroup.id";

	@Autowired
	private AcsUtils acsUtils;

	public Long getSystemIdByCode(String code) {
	   return acsUtils.getSystemsByCode(code).getId();
	}
	
	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		workGroupDao = new SimpleHibernateTemplate<Workgroup, Long>(sessionFactory, Workgroup.class);
		userDao = new SimpleHibernateTemplate<User, Long>(sessionFactory, User.class);
		userInfoDao = new SimpleHibernateTemplate<UserInfo, Long>(sessionFactory, UserInfo.class);
		workGroupToUserDao = new SimpleHibernateTemplate<WorkgroupUser, Long>(sessionFactory, WorkgroupUser.class);
		roleDao = new SimpleHibernateTemplate<Role, Long>(sessionFactory, Role.class);
		role_wDao = new SimpleHibernateTemplate<RoleWorkgroup, Long>(sessionFactory, RoleWorkgroup.class);
		logUtilDao = new LogUtilDao(sessionFactory);
	}   

	
	/**
	 * 验证工作组编号唯一性
	 * liudongxia
	 */
	public boolean checkWorkCode(String workGroupCode){
		String hql = "FROM Workgroup d WHERE d.code=? AND d.company.id=? AND d.deleted=0";
		Object obj = workGroupDao.findUnique(hql, workGroupCode,ContextUtils.getCompanyId());
		if(obj == null){
			return false;
		}
		return true;
	}
	
	public LogUtilDao getLogUtilDao() {
		return logUtilDao;
	}

	public void setLogUtilDao(LogUtilDao logUtilDao) {
		this.logUtilDao = logUtilDao;
	}


	private Long companyId;
	
	public Long getCompanyId() {
		if(companyId == null){
			return ContextUtils.getCompanyId();
		}else 
			return companyId;
	}
    
	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
	
	/**
  	 * 查询所有工作组信息
  	 */
	@Transactional(readOnly = true)
	public List<Workgroup> getAllWorkGroup(){
		return workGroupDao.findByCriteria(Restrictions.eq("company.id", getCompanyId()),Restrictions.eq(DELETED,false));
	}
	
	/**
	 * 获取单条工作组信息
	 */
	@Transactional(readOnly = true)
	public Workgroup getWorkGroup(Long id) {
		return workGroupDao.get(id);
	}
	
	   
    /**
     * 公司添加工作组(保存公司与工作组的关系)
     */
	public List<Workgroup> saveWorkGroup(List<Long> workGroupIds){
		return workGroupDao.findByCriteria(Restrictions.in("id", workGroupIds));
	}
	
	public Page<User> workGroupToUsers(Page<User> userPage){
	
	return userDao.findAll(userPage);
    }
   
	/**
	 * 
	 * 查询工作组己添加的用户
	 */
   public List<Long> getUserIds(Long workGroupId) throws Exception {
	List<Long> userIds = new ArrayList<Long>();
	List<WorkgroupUser> workGroupToUsers = workGroupToUserDao.findByCriteria(Restrictions.eq(WORKGROUPID, workGroupId),
		                                        	                           Restrictions.eq("companyId", ContextUtils.getCompanyId()),
                                                                               Restrictions.eq(DELETED,false)
	                                                                            );
	for (WorkgroupUser workGroupToUser : workGroupToUsers) {
		userIds.add(workGroupToUser.getUser().getUserInfo().getId());
	}
	return userIds;
  }
          

	/**
	 * 分页查询所有工作组信息
	 */
	@Transactional(readOnly = true)
	public Page<Workgroup> getAllWorkGroup(Page<Workgroup> page) {
		workGroupDao.findPage(page, hql, getCompanyId(), false);
		return page;

	}
	
	/**
	 * 根据分支机构id分页查询该分支机构下的所有工作组信息
	 */
	@Transactional(readOnly = true)
	public Page<Workgroup> getAllWorkGroupByBranchesId(Page<Workgroup> page,Long branchesId) {
		StringBuilder hql=new StringBuilder();
		hql.append("from Workgroup w where w.company.id=? and w.deleted=? ");
		if(branchesId==null){
			hql.append(" and w.subCompanyId is null ");
			hql.append(" order by w.weight desc");
			workGroupDao.findPage(page, hql.toString(), ContextUtils.getCompanyId(), false);
		}else{
			hql.append(" and w.subCompanyId=? ");
			hql.append(" order by w.weight desc");
			workGroupDao.findPage(page, hql.toString(),ContextUtils.getCompanyId(), false,branchesId);
		}
		return page;
		
	}
	
	/**
	  * 保存工作组信息
	  */	
	public void saveWorkGroup(Workgroup workGroup){
	    
		workGroupDao.save(workGroup);	
	}
	
	/**
	 * 删除工作组信息
	 */
	public void deleteWorkGroup(Long id) {
		Workgroup workGroup = workGroupDao.get(id);
		workGroup.setDeleted(true);
		workGroupDao.save(workGroup);
	}		
	
	  public List<Role> getRole(Long workGroupId){
		  List<Role> roleIds = new ArrayList<Role>();
		  List<RoleWorkgroup> role_WorkGroups = role_wDao.findByCriteria(Restrictions.eq(WORKGROUPID, workGroupId),
				                                                          Restrictions.eq("companyId", getCompanyId()),
				                                                          Restrictions.eq(DELETED, false));
		  for (RoleWorkgroup role_WorkGroup : role_WorkGroups) {
			  roleIds.add(role_WorkGroup.getRole());
		}
		  return roleIds;
	  }
	  
	  
	  public List<Workgroup> queryWorkGroupByCompany(Long companyId){
		  return workGroupDao.findList(
				  "from Workgroup w where w.company.id=? and w.deleted=? order by w.weight desc", 
				  companyId, false);
	  }
	  
	  public List<Workgroup> queryWorkGroupByBranches(Long branchesId){
		  StringBuilder hql=new StringBuilder();
		  hql.append("from Workgroup w where w.company.id=? and w.deleted=? ");
		  if(branchesId==null){
			  hql.append(" and w.subCompanyId is null ");
			  hql.append(" order by w.weight desc");
			  return workGroupDao.findList(hql.toString(),ContextUtils.getCompanyId(), false);
		  }else{
			  hql.append(" and w.subCompanyId=? ");
			  hql.append(" order by w.weight desc");
			  return workGroupDao.findList(hql.toString(),ContextUtils.getCompanyId(), false,branchesId);
		  }
	  }

	@SuppressWarnings("unchecked")
	public List<Workgroup> getWorkGroupsByUser(Long companyId, String loginName){
		StringBuilder hql = new StringBuilder();
		hql.append("select wg from Workgroup wg join wg.workgroupUsers wgu join wgu.user u ");
		hql.append("where u.companyId=? and u.loginName=? and u.deleted=? and wgu.deleted=? and wg.deleted=?");
		return workGroupDao.find(hql.toString(), companyId, loginName, false, false, false);
	}
	  
	public SimpleHibernateTemplate<Workgroup, Long> getWorkGroupDao() {
		return workGroupDao;
	}

	public SimpleHibernateTemplate<Role, Long> getRoleDao() {
		return roleDao;
	}

	public SimpleHibernateTemplate<RoleWorkgroup, Long> getRole_wDao() {
		return role_wDao;
	}

	public SimpleHibernateTemplate<com.norteksoft.acs.entity.organization.User, Long> getUserDao() {
		return userDao;
	}

	public SimpleHibernateTemplate<com.norteksoft.acs.entity.organization.WorkgroupUser, Long> getWorkGroupToUserDao() {
		return workGroupToUserDao;
	}


	public void setUserInfoDao(
			SimpleHibernateTemplate<com.norteksoft.acs.entity.organization.UserInfo, Long> userInfoDao) {
		this.userInfoDao = userInfoDao;
	}


	public SimpleHibernateTemplate<com.norteksoft.acs.entity.organization.UserInfo, Long> getUserInfoDao() {
		return userInfoDao;
	}
	 /**
	 * 保存工作组添加用户
	 * userIds:当是全公司时其值为[0],否则是人员id集合
	 * @return
	 * @throws Exception
	 */
	public String workgroupAddUser(Long workGroupId, List<Long> userIds, int isAdd) {
		if(userIds==null){
			return "";
		}
		String addUserNames = "";
		Workgroup workgroup = workGroupDao.get(workGroupId);
		/**
		 * 添加人员
		 */
		if (isAdd == 0) {
			for (Long userId : userIds) {
				if(userId.equals(0L)){//全公司时
					List<com.norteksoft.product.api.entity.User> users = ApiFactory.getAcsService().getAllUsersByCompany(ContextUtils.getCompanyId());
					for(com.norteksoft.product.api.entity.User u:users){
						addUserNames+=u.getName()+",";
						workgroupAddSingleUser(u.getId(),workgroup);
					}
				}else{
					addUserNames+=userDao.get(userId).getName()+",";
					workgroupAddSingleUser(userId,workgroup);
				}
			}
		}
		/**
		 *移除人员
		 */
		String removeUserNames = "";
		if (isAdd == 1) {
			List<User> uif = userDao.findByCriteria(Restrictions.in(
					"id", userIds));
			List<Long> ids = new ArrayList<Long>();
			for (User user : uif) {
				ids.add(user.getId());
			}
			List<WorkgroupUser> list = workGroupToUserDao.findByCriteria(
					Restrictions.in("user.id", ids), Restrictions.eq(
							WORKGROUPID, workGroupId), Restrictions.eq(
									COMPANYID, getCompanyId()), Restrictions.eq(
									DELETED, false));

			for (WorkgroupUser workgroupUser : list) {
				 removeUserNames+=workgroupUser.getUser().getName()+",";
				 workgroupUser.setDeleted(true);
				 workGroupToUserDao.save(workgroupUser);
			}
		}
       if(isAdd == 0){
    	   return  addUserNames.substring(0, addUserNames.length()-1);
       }else if(isAdd == 1){
    	   return  removeUserNames.substring(0, removeUserNames.length()-1);
       }
       return "";
	}
	
	private void workgroupAddSingleUser(Long userId,Workgroup workgroup){
		WorkgroupUser workgroupUser;
		User user = null;
		List<WorkgroupUser> wu=getWorkgroupUserByuserId(userId,workgroup.getId());
		if(wu.size()==0){
			workgroupUser = new WorkgroupUser();
			user = userDao.get(userId);
			workgroupUser.setUser(user);
			workgroupUser.setWorkgroup(workgroup);
			workgroupUser.setCompanyId(getCompanyId());
			workGroupToUserDao.save(workgroupUser);
		}else{
			WorkgroupUser w=wu.get(0);
			w.setDeleted(false);
			workGroupToUserDao.save(w);
		}
	}
	
	/**
	 *根据userId得到WorkgroupUser
	 */
	@SuppressWarnings("unchecked")
	public List<WorkgroupUser> getWorkgroupUserByuserId(Long userId,Long workgroupId){
		String hql="from WorkgroupUser d where d.user.id=? and d.workgroup.id=?";
		return workGroupToUserDao.find(hql, userId,workgroupId);
	}

	/**
	 * 获得工作组编码为默认编码的所有工作组
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Workgroup> getDefaultCodeWorkGroups() {
		String hql="from Workgroup w where w.company.id=? and w.deleted=? and w.code like 'workgroup-%'";
		return workGroupDao.find(hql, ContextUtils.getCompanyId(),false);
	}

	/**
	 * 通过编号获得工作组
	 * @param workGroupCode
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Workgroup> getWorkgroupsByCode(String workGroupCode) {
		String hql="from Workgroup w where w.company.id=? and w.deleted=? and w.code=? ";
		return workGroupDao.find(hql, ContextUtils.getCompanyId(),false,workGroupCode);
	}

	/**
	 * 通过名称和分支机构id获得工作组
	 * @param branchesId
	 * @param workGroupName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Workgroup> getWorkgroupsByName(Long branchesId,String workGroupName) {
		StringBuilder hql=new StringBuilder();
		hql.append("from Workgroup w where w.company.id=? and w.deleted=? and w.name=? ");
		if(branchesId==null){
			hql.append(" and w.subCompanyId is null ");
			return workGroupDao.find(hql.toString(), ContextUtils.getCompanyId(),false,workGroupName);
		}else{
			hql.append(" and w.subCompanyId=? ");
			return workGroupDao.find(hql.toString(), ContextUtils.getCompanyId(),false,workGroupName,branchesId);
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<Workgroup> getWorkgroupsByBranch(Long branchId) {
		StringBuilder hql=new StringBuilder();
		hql.append("from Workgroup w where w.company.id=? and w.deleted=? ");
		hql.append("and w.subCompanyId=? ");
			return workGroupDao.find(hql.toString(), ContextUtils.getCompanyId(),false,branchId);
	}

	public void cleanWorkGroup(Long id) {
		Workgroup workGroup=getWorkGroup(id);
		Set<RoleWorkgroup> roleWorkgroups = workGroup.getRoleWorkgroups();
		if(!roleWorkgroups.isEmpty()){
			for(RoleWorkgroup roleWorkgroup:roleWorkgroups){
				role_wDao.delete(roleWorkgroup);
			}
		}
		Set<WorkgroupUser> workgroupUsers = workGroup.getWorkgroupUsers();
		if(!roleWorkgroups.isEmpty()){
			for(WorkgroupUser workgroupUser:workgroupUsers){
				workGroupToUserDao.delete(workgroupUser);
			}
		}
	}
}