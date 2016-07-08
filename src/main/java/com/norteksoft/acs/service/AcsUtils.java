package com.norteksoft.acs.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.acs.base.orm.hibernate.SimpleHibernateTemplate;
import com.norteksoft.acs.entity.authorization.BusinessSystem;
import com.norteksoft.acs.entity.authorization.Function;
import com.norteksoft.acs.entity.authorization.Role;
import com.norteksoft.acs.entity.organization.Company;
import com.norteksoft.acs.entity.organization.Department;
import com.norteksoft.acs.entity.organization.User;
import com.norteksoft.acs.entity.organization.Workgroup;
import com.norteksoft.acs.service.authorization.BusinessSystemManager;
import com.norteksoft.acs.service.authorization.StandardRoleManager;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.Md5;

/**
 * 权限API
 * 
 * @author xiao
 *
 * 2010-9-26
 */
@Service
@Transactional
public class AcsUtils {

	private SimpleHibernateTemplate<User, Long> userDao;
	private SimpleHibernateTemplate<Department, Long> departmentDao;
	private SimpleHibernateTemplate<Workgroup, Long> workGroupDao;
	private SimpleHibernateTemplate<Role, Long> roleDao;
	private SimpleHibernateTemplate<Company, Long> companyDao;
	private StandardRoleManager standardRoleManager;
	private BusinessSystemManager businessSystemManager;
	
	private static SimpleHibernateTemplate<BusinessSystem, Long> businessSystemDao;
	
	
	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		userDao = new SimpleHibernateTemplate<User, Long>(sessionFactory,User.class);
		departmentDao = new SimpleHibernateTemplate<Department, Long>(sessionFactory, Department.class);
		workGroupDao = new SimpleHibernateTemplate<Workgroup, Long>(sessionFactory, Workgroup.class);
		roleDao = new SimpleHibernateTemplate<Role, Long>(sessionFactory,Role.class);
		companyDao = new SimpleHibernateTemplate<Company, Long>(sessionFactory,Company.class);
		businessSystemDao = new SimpleHibernateTemplate<BusinessSystem, Long>(sessionFactory,BusinessSystem.class);
	}
	
	@Autowired
	public void setStandardRoleManager(StandardRoleManager standardRoleManager) {
		this.standardRoleManager = standardRoleManager;
	}
	
	@Autowired
	public void setBusinessSystemManager(BusinessSystemManager businessSystemManager) {
		this.businessSystemManager = businessSystemManager;
	}

	/**
	 * 根据公司ID查询所有顶级部门
	 * @param companyId
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<Department> getDepartments(Long companyId) {
		return departmentDao.findList(
				"FROM Department d WHERE d.company.id=? AND d.deleted=? and d.parent.id is null ORDER BY d.weight desc", 
				companyId, false);
	}
	
	@SuppressWarnings("unchecked")
	public Department getManDepartment(String loginName, Long companyId){
		List<Department> depts = departmentDao.find("select d from Department d,User u where d.id=u.mainDepartmentId and u.companyId=? and u.loginName=? and u.deleted=false", companyId, loginName);
		if(depts.size() == 1){
			return depts.get(0);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public Department getManDepartment(Long userId, Long companyId){
		List<Department> depts = departmentDao.find("select d from Department d,User u where d.id=u.mainDepartmentId and u.companyId=? and u.id=?", companyId, userId);
		if(depts.size() == 1){
			return depts.get(0);
		}
		return null;
	}
	
	/**
	 * 根据公司ID查询所有工作组
	 * @param companyId
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<Workgroup> getWorkGroups(Long companyId) {
		return workGroupDao.findList(
				"from Workgroup wg where wg.company.id=? and wg.deleted=? ORDER BY wg.weight desc"
				,companyId, false);
	}
	
	/**
	 * 根据部门ID查询该部门所有的用户
	 * @param departmentId
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<User> getUsersByDepartmentId(Long companyId, Long departmentId) {
		return userDao.findList(
				"select u from User u join u.departmentUsers du join du.department d " +
				"where d.company.id=? and d.id=? and d.deleted=? and du.deleted=? and u.deleted=? ORDER BY u.weight desc", 
				companyId, departmentId, false, false, false);
	}
	
	/**
	 * 根据工作组ID查询该组下所有的用户
	 * @param companyId
	 * @param workGroupId
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<User> getUsersByWorkGroupId(Long companyId, Long workGroupId) {
		return userDao.findList(
				"select u from User u join u.workgroupUsers wu join wu.workgroup wg " +
				"where wg.company.id=? and wg.id=? and wg.deleted=? and wu.deleted=? and u.deleted=? ORDER BY u.weight desc", 
				companyId, workGroupId, false, false, false);
	}
	
	/**
	 * 根据父部门id查询该父部门下所有子部门
	 * @param paternDepartmentId
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<Department> getSubDepartmentList(Long paternDepartmentId) {
		return departmentDao.findList(
				"FROM Department d WHERE d.parent.id=? AND d.deleted=?  ORDER BY d.weight desc", 
				paternDepartmentId, false);
	}
	
	/**
	 * 根据用户Id得到用户
	 * @param id
	 * @return
	 */
	@Transactional(readOnly = true)
	public User getUserById(Long id) {
		if (id == null) return null;
		return userDao.get(id);
	}
	
	/**
	 * 根据用户Id得到用户
	 * @param id
	 * @return
	 */
	@Transactional(readOnly = true)
	public User getUserByLoginName(String loginName) {
		if (loginName == null) return null;
		List<User> users = userDao.find("select user from User user where user.deleted=false and user.loginName=?", loginName);
		if(users.size()>0){
			return users.get(0);
		}
		return null;
	}
	
	/**
	 * 根据用户Id得到用户
	 * @param id
	 * @return
	 */
	@Transactional(readOnly = true)
	public User getUserByLikeLoginName(String loginName,Long companyId) {
		if (loginName == null) return null;
		List<User> users=userDao.find("select user from User user where user.deleted=false and user.loginName like ? and user.companyId=? ", "%"+loginName+"%",companyId);
		if(users.size()>0)return users.get(0);
		return null;
	}
	
	/**
	 * 根据用户Id得到公司Id
	 * @param userId
	 * @return
	 */
	@Transactional(readOnly = true)
	public Long getCompanyIdByUserId(Long userId) {
		if (userId == null) return null;
		User user=getUserById(userId);
		if(user==null)return null;
		return user.getCompanyId();
	}
	
	/**
	 * 根据用户Id得到公司Id
	 * @param userId
	 * @return
	 */
	@Transactional(readOnly = true)
	public Long getCompanyIdLoginName(String loginName) {
		if (loginName == null) return null;
		User user=getUserByLoginName(loginName);
		if(user==null)return null;
		return user.getCompanyId();
	}
	
	/**
	 * 根据登录名查询用户
	 * @param companyId
	 * @param loginName
	 * @return
	 */
	@Transactional(readOnly = true)
	public User getUser(Long companyId, String loginName){
		List<User> users = userDao.findList("from User u where u.companyId=? and u.loginName=? and u.deleted=? ", companyId, loginName, false);
		User user = null;
		if(users.size() == 1){
			user = users.get(0);
		}
		return user;
	}
	
	/**
	 * 获取不属于任何部门的用户
	 * @param companyId
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<User> getUsersNotInDepartment(Long companyId){
		if(companyId == null) return null;
		StringBuilder sqlString = new StringBuilder();
		sqlString.append("SELECT ACS_USER.* FROM ACS_USER LEFT OUTER JOIN ");
		sqlString.append("(SELECT * FROM ACS_DEPARTMENT_USER WHERE ACS_DEPARTMENT_USER.DELETED = 0)");
		sqlString.append(" DEPT_USER ON ACS_USER.ID = DEPT_USER.FK_USER_ID ");
		sqlString.append("WHERE ACS_USER.DELETED=0 AND ACS_USER.FK_COMPANY_ID = ? ");
		sqlString.append("AND DEPT_USER.ID IS NULL ORDER BY ACS_USER.WEIGHING DESC");
		return userDao.findByJdbc(sqlString.toString(), companyId);
	}
	
	/**
	 * 通过部门ID获取部门实体
	 * @param workGroupId
	 * @return
	 */
	@Transactional(readOnly = true)
	public Department getDepartmentById(Long departmentId){
		if(departmentId == null) return null;
		return departmentDao.get(departmentId);
	}
	
	/**
	 * 通过部门名称获取部门实体
	 * @param name
	 * @param companyId
	 * @return
	 */
	@Transactional(readOnly = true)
	public Department getDepartmentByName(String name, Long companyId){
		List<Department> depts = departmentDao.findList("from Department d where d.company.id=? and d.name=? and d.deleted=?", companyId, name, false);
		Department dept = null;
		if(depts.size() == 1){
			dept = depts.get(0);
		}
		return dept;
	}
	
	/**
	 * 根据用户ID查询用户所在的部门
	 * @param companyId
	 * @param userId
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<Department> getDepartmentsByUser(Long companyId, Long userId){
		StringBuilder hql = new StringBuilder();
		hql.append("select d from Department d join d.departmentUsers du join du.user u ");
		hql.append("where u.companyId=? and u.id=? and u.deleted=? and du.deleted=? and d.deleted=?  ORDER BY d.weight desc");
		return departmentDao.findList(hql.toString(), companyId, userId, false, false, false);
	}
	
	/**
	 * 根据用户ID查询用户所在的工作组
	 * @param companyId
	 * @param userId
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<Workgroup> getWorkGroupByUser(Long companyId, Long userId){
		StringBuilder hql = new StringBuilder();
		hql.append("select wg from Workgroup wg join wg.workgroupUsers wgu join wgu.user u ");
		hql.append("where u.companyId=? and u.id=? and u.deleted=? and wgu.deleted=? and wg.deleted=? order by wg.weight desc");
		return workGroupDao.findList(hql.toString(), companyId, userId, false, false, false);
	}
	
	/**
	 * 根据用户登录名查询用户所在的部门
	 * @param companyId
	 * @param loginName
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<Department> getDepartmentsByUser(Long companyId, String loginName){
		StringBuilder hql = new StringBuilder();
		hql.append("select d from Department d join d.departmentUsers du join du.user u ");
		hql.append("where u.companyId=? and u.loginName=? and u.deleted=? and du.deleted=? and d.deleted=?");
		return departmentDao.findList(hql.toString(), companyId, loginName, false, false, false);
	}

	/**
	 * 通过角色编号查询所有的用户(不包含委托的权限)
	 * @param systemId
	 * @param companyId
	 * @param roleCode
	 * @return
	 */
	@Transactional(readOnly = true)
	public Set<User> getUsersByRole(Long systemId, Long companyId, String roleCode){
		Set<User> result = new LinkedHashSet<User>();
		List<Role> roles=roleDao.findList("from Role r where r.deleted=? and r.businessSystem.id=? and r.code=? and (r.companyId=? or r.companyId is null)",false,systemId,roleCode,companyId);
		
		if(roles!=null&&roles.size()>0){
			Role role=roles.get(0);
			if(standardRoleManager.hasAllUserByRole(role.getId())){
				List<User> allUser=getUsersByCompany(companyId);
				result.addAll(allUser);
				return result;
			}
			if(standardRoleManager.hasAllDepartmentByRole(role.getId())){
				result.addAll(findUserByAllDepartment());
			}
			if(standardRoleManager.hasAllWorkgroupByRole(role.getId())){
				result.addAll(findUserByAllWorkgroup());
			}
		
		//users role
		StringBuilder usersByRole = new StringBuilder();
		usersByRole.append("select u from User u join u.roleUsers ru join ru.role r ");
		usersByRole.append("where r.code = ? and u.companyId=? and r.deleted=false and ru.consigner is null and ");
		usersByRole.append("ru.deleted=false and u.deleted=false order by u.weight desc");
		List<User> roleUsers = userDao.findList(usersByRole.toString(), roleCode, companyId);
		//users department role
		StringBuilder usersByDeptRoleHql = new StringBuilder();
		usersByDeptRoleHql.append("select u from User u join u.departmentUsers du join du.department d ");
		usersByDeptRoleHql.append("join d.roleDepartments rd join rd.role r ");
		usersByDeptRoleHql.append("where r.code = ? and d.company.id=? and r.deleted=false and ");
		usersByDeptRoleHql.append("rd.deleted=false and d.deleted=false and du.deleted=false and u.deleted=false order by u.weight desc");
		List<User> roleDeptUsers = userDao.findList(usersByDeptRoleHql.toString(), roleCode, companyId);
		//users branches role
		StringBuilder usersByBranchesRoleHql = new StringBuilder();
		usersByBranchesRoleHql.append("select u from User u where u.subCompanyId is not null and u.subCompanyId in (select d.id from Department d join d.roleDepartments rd join rd.role r where r.code = ? and d.company.id=? and d.branch=true and r.deleted=false and rd.deleted=false and d.deleted=false) and u.deleted=false order by u.weight desc");
		List<User> roleBranchesUsers = userDao.findList(usersByBranchesRoleHql.toString(), roleCode, companyId);
		//users work-group role
		StringBuilder usersByWgRoleHql = new StringBuilder();
		usersByWgRoleHql.append("select u from User u join u.workgroupUsers wgu join wgu.workgroup wg ");
		usersByWgRoleHql.append("join wg.roleWorkgroups rwg join rwg.role r join r.businessSystem rbs ");
		usersByWgRoleHql.append("where rbs.id=? and r.code = ? and wg.company.id=? and rbs.deleted=false and r.deleted=false and ");
		usersByWgRoleHql.append("rwg.deleted=false and wg.deleted=false and wgu.deleted=false and u.deleted=false order by u.weight desc");
		List<User> roleWgUsers = userDao.findList(usersByWgRoleHql.toString(), systemId, roleCode, companyId);
		
		result.addAll(roleUsers);
		result.addAll(roleDeptUsers);
		result.addAll(roleBranchesUsers);
		result.addAll(roleWgUsers);
		return result;
		}else{
			return result;
		}
	}
	/**
	 * 判断用户是否在部门内
	 * @param userId
	 * @return
	 */
	public boolean departmentHasUser(Long companyId,Long userId){
		Long count=userDao.findLong("select count(*) FROM User u join u.departmentUsers du join du.department d  WHERE u.companyId=? AND u.deleted=? AND du.deleted=?  AND d.deleted=? and ((du.department.id<>du.subCompanyId) or (du.subCompanyId is null)) and u.id=?",companyId,false,false,false,userId);
		return count>0?true:false;
	}
	/**
	 * 判断用户是否在工作组内
	 * @param userId
	 * @return
	 */
	public boolean workgroupHasUser(Long companyId,Long userId){
		Long count = userDao.findLong("select count(*) from User u join u.workgroupUsers wgu where u.deleted=? and wgu.deleted=? and u.companyId=? and u.id=?", false,false,companyId,userId);
		return count>0?true:false;
	}
	/**
	 * 获取在部门内的所有用户
	 * @return
	 */
	public List<User> findUserByAllDepartment(){
		return userDao.findList("select distinct u FROM User u join u.departmentUsers du join du.department d  WHERE u.companyId=? AND u.deleted=? AND du.deleted=?  AND d.deleted=? and ((du.department.id<>du.subCompanyId) or (du.subCompanyId is null))",ContextUtils.getCompanyId(),false,false,false);
	}
	/**
	 * 获取在工作组内的所有用户
	 * @return
	 */
	public List<User> findUserByAllWorkgroup(){
		return userDao.findList("select distinct u from User u join u.workgroupUsers wgu where u.deleted=? and wgu.deleted=? and u.companyId=?", false,false,ContextUtils.getCompanyId());
	}
	/**
	 * 获取含有和所有用户关系的角色
	 * @return
	 */
	public List<Role> getRolesWhereHasAllUser(Long companyId){
		return roleDao.findList("select distinct r from Role r join r.roleUsers ru where ru.deleted=? and r.deleted=? and ru.allUser=? and (ru.companyId is null or ru.companyId=?)",false,false,"ALL_USER",companyId);
	}
	
	/**
	 * 获取含有和所有部门关系的角色
	 * @return
	 */
	public List<Role> getRolesWhereHasAllDept(Long companyId){
		return roleDao.findList("select distinct r from Role r join r.roleDepartments rd where rd.deleted=? and r.deleted=? and rd.allDept=? and (rd.companyId is null or rd.companyId=?)", false,false,"ALL_DEPARTMENT",companyId);
	}
	/**
	 * 获取含有和所有工作组关系的角色
	 * @return
	 */
	public List<Role> getRolesWhereHasAllGroup(Long companyId){
		return roleDao.findList("select distinct r from Role r join r.roleWorkgroups rw where rw.deleted=? and r.deleted=? and rw.allGroup=? and (rw.companyId is null or rw.companyId=?)", false,false,"ALL_WORKGROUP",companyId);
	}
	/**
	 * 获取部门拥有的所有角色
	 * @param departmentId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Role> getRolesByDepartmentId(Long departmentId) {
		StringBuilder hql=new StringBuilder();
		Set<Role> roles=new HashSet<Role>();
		List<Role> rolesList=new ArrayList<Role>();
		hql.append("select distinct role from Role role join role.roleDepartments rd join rd.department dept where dept.id = ? and role.deleted = false and rd.deleted = false and dept.deleted = false");
		roles.addAll(roleDao.find(hql.toString(),departmentId));
		roles.addAll(getRolesWhereHasAllDept(ContextUtils.getCompanyId()));
		rolesList.addAll(roles);
		return rolesList;
	}
	/**
	 * 获取工作组拥有的所有角色
	 * @param departmentId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Role> getRolesByWorkgroupId(Long workgroupId) {
		StringBuilder hql=new StringBuilder();
		Set<Role> roles=new HashSet<Role>();
		List<Role> rolesList=new ArrayList<Role>();
		hql.append("select distinct role from Role role join role.roleWorkgroups rw join rw.workgroup w where w.id = ? and role.deleted = false and rw.deleted = false and w.deleted = false");
		roles.addAll(roleDao.find(hql.toString(),workgroupId));
		roles.addAll(getRolesWhereHasAllGroup(ContextUtils.getCompanyId()));
		rolesList.addAll(roles);
		return rolesList;
	}
	/**
	 * 通过角色编号查询所有的用户（包含委托的权限）
	 * @param systemId
	 * @param companyId
	 * @param roleCode
	 * @return
	 */
	@Transactional(readOnly = true)
	public Set<User> getUsersByRoleIncludeTrustedRole(Long systemId, Long companyId, String roleCode){
		return getUsersByRole(systemId,companyId,roleCode);
	}
	/**
	 * 根据用户ID查询  该用户拥有的所有角色(含委托角色)
	 * @param companyId
	 * @param userId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Set<Role> getRolesByUserIncludeConsigner(Long companyId, Long userId){
		StringBuilder rolesByUserHql = new StringBuilder();
		rolesByUserHql.append("select r from User u join u.roleUsers ru join ru.role r ");
		rolesByUserHql.append("where u.deleted=? and ru.deleted=? and r.deleted=? and u.id=? and u.companyId=? and (r.companyId is null or r.companyId=?)");
		List<Role> userRoles = roleDao.find(rolesByUserHql.toString(), false, false, false, userId, companyId, companyId);
		
		StringBuilder rolesByDepartmentHql = new StringBuilder();
		rolesByDepartmentHql.append("select r from User u join u.departmentUsers du join du.department d join d.roleDepartments rd join rd.role r ");
		rolesByDepartmentHql.append("where u.deleted=? and du.deleted=? and d.deleted=? and rd.deleted=? and r.deleted=?  and u.id=? and u.companyId=? and (r.companyId is null or r.companyId=?)");
		List<Role> departmentRoles = roleDao.find(rolesByDepartmentHql.toString(), false, false, false,false, false, userId, companyId, companyId);
		
		StringBuilder rolesByBranchesHql = new StringBuilder();
		rolesByBranchesHql.append("select r from RoleDepartment du join du.role r join du.department d where r.deleted=? and du.deleted=? and d.deleted=? and d.branch=? and d.company.id=? and d.id is (select u.subCompanyId from User u where u.id=? and u.deleted=? and u.subCompanyId is not null)");
		List<Role> branchesRoles = roleDao.find(rolesByBranchesHql.toString(), false, false, false,true,companyId,userId, false);
		
		StringBuilder rolesByWorkgroupHql = new StringBuilder();
		rolesByWorkgroupHql.append("select r from User u join u.workgroupUsers wu join wu.workgroup w join w.roleWorkgroups rw join rw.role r ");
		rolesByWorkgroupHql.append("where u.deleted=? and wu.deleted=? and w.deleted=? and rw.deleted=? and r.deleted=?   and u.id=? and u.companyId=? and (r.companyId is null or r.companyId=?)");
		List<Role> workgroupRoles = roleDao.find(rolesByWorkgroupHql.toString(), false, false, false,false, false, userId, companyId, companyId);
		Set<Role> roles = new HashSet<Role>();
		//添加所有含有all_user关系的角色
		roles.addAll(getRolesWhereHasAllUser(companyId));
		if(departmentHasUser(companyId, userId)){//如果用户在部门中
			roles.addAll(getRolesWhereHasAllDept(companyId));//添加所有含有all_dept关系的角色
		}
		if(workgroupHasUser(companyId, userId)){//如果用户在工作组中
			roles.addAll(getRolesWhereHasAllGroup(companyId));//添加所有含有all_group关系的角色
		}
		roles.addAll(userRoles);
		roles.addAll(departmentRoles);
		roles.addAll(branchesRoles);
		roles.addAll(workgroupRoles);
		return roles;
	}
	/**
	 * 根据用户ID查询  该用户拥有的所有角色(不含委托角色)
	 * @param companyId
	 * @param userId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Set<Role> getRolesByUser(Long companyId, Long userId){
		StringBuilder rolesByUserHql = new StringBuilder();
		Set<Role> roles = new HashSet<Role>();
		rolesByUserHql.append("select r from User u join u.roleUsers ru join ru.role r ");
		rolesByUserHql.append("where u.deleted=? and ru.deleted=? and ru.consigner is null and r.deleted=? and u.id=? and u.companyId=? and (r.companyId is null or r.companyId=?)");
		List<Role> userRoles = roleDao.find(rolesByUserHql.toString(), false, false, false, userId, companyId, companyId);
		
		StringBuilder rolesByDepartmentHql = new StringBuilder();
		rolesByDepartmentHql.append("select r from User u join u.departmentUsers du join du.department d join d.roleDepartments rd join rd.role r ");
		rolesByDepartmentHql.append("where u.deleted=? and du.deleted=? and d.deleted=? and rd.deleted=? and r.deleted=?  and u.id=? and u.companyId=? and (r.companyId is null or r.companyId=?)");
		List<Role> departmentRoles = roleDao.find(rolesByDepartmentHql.toString(), false, false, false,false, false, userId, companyId, companyId);
		
		StringBuilder rolesByBranchesHql = new StringBuilder();
		rolesByBranchesHql.append("select r from RoleDepartment du join du.role r join du.department d where r.deleted=? and du.deleted=? and d.deleted=? and d.branch=? and d.company.id=? and d.id is (select u.subCompanyId from User u where u.id=? and u.deleted=? and u.subCompanyId is not null)");
		List<Role> branchesRoles = roleDao.find(rolesByBranchesHql.toString(), false, false, false,true,companyId,userId, false);
		
		StringBuilder rolesByWorkgroupHql = new StringBuilder();
		rolesByWorkgroupHql.append("select r from User u join u.workgroupUsers wu join wu.workgroup w join w.roleWorkgroups rw join rw.role r ");
		rolesByWorkgroupHql.append("where u.deleted=? and wu.deleted=? and w.deleted=? and rw.deleted=? and r.deleted=?   and u.id=? and u.companyId=? and (r.companyId is null or r.companyId=?)");
		List<Role> workgroupRoles = roleDao.find(rolesByWorkgroupHql.toString(), false, false, false,false, false, userId, companyId, companyId);
		
		//添加所有含有all_user关系的角色
		roles.addAll(getRolesWhereHasAllUser(companyId));
		if(departmentHasUser(companyId,userId)){//如果用户在部门中
			roles.addAll(getRolesWhereHasAllDept(companyId));//添加所有含有all_dept关系的角色
		}
		if(workgroupHasUser(companyId,userId)){//如果用户在工作组中
			roles.addAll(getRolesWhereHasAllGroup(companyId));//添加所有含有all_group关系的角色
		}
		roles.addAll(userRoles);
		roles.addAll(departmentRoles);
		roles.addAll(branchesRoles);
		roles.addAll(workgroupRoles);
		return roles;
	}
	/**
	 * 通过url的key查询用户是否具有该权限
	 * @param urlKey
	 * @param userId
	 * @param companyId
	 * @return
	 */
	@Transactional(readOnly = true)
	public boolean isAuthority(String urlKey, Long userId, Long companyId){
		Set<Role> userRoles = getRolesByUserIncludeConsigner(companyId,userId);
		Set<Function> functions =  standardRoleManager.getFunctionsByRoles(userRoles);
		boolean result = false;
		for(Function function : functions){
			if(urlKey.equals(function.getCode())){
				result = true;
				break;
			}
		}
		return result;
	}
	/**
	 * 获取租户内所有人
	 * @param companyId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<User> getUsersByCompany(Long companyId){
		return userDao.find("FROM User u WHERE u.companyId=? AND u.deleted=? ORDER BY u.weight DESC", companyId,false);
	}
	
	
	/**
	 * 查询所有的系统并排序
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<BusinessSystem> getSystems(){
		return businessSystemDao.find("from BusinessSystem bs where bs.deleted=? order by id", false);
	}
	/**
	 * 查询所有的系统并排序
	 * @return
	 */
	@Transactional(readOnly = true)
	public BusinessSystem getSystemsByCode(String systemCode){
		return businessSystemManager.getSystemBySystemCode(systemCode);
	}
	@Transactional(readOnly = true)
	public Workgroup getWorkGroup(Long workGroupId){
		if(workGroupId == null) 
			return null;
		return workGroupDao.get(workGroupId);
	}
	
	/**
	 * 根据工作组编号查询工作组
	 * @return
	 */
	@Transactional(readOnly = true)
	public Workgroup getWorkGroupByCode(String code, Long companyId){
		if(code == null)  return null;
		List<Workgroup> groups =  workGroupDao.findList("from Workgroup w where w.company.id=? and w.code=? and w.deleted=? ", 
				companyId, code, false);
		if(groups.size() == 1) return groups.get(0);
		return null;
	}
	
	/**
	 * 查询所有业务系统信息
	 */
	public List<BusinessSystem> getAllBusiness(Long companyId){
		return businessSystemManager.getAllBusiness(companyId);
	}
	/**
	 * 验证当前用户是否存在且密码是否正确
	 * @param loginName
	 * @param password
	 * @return
	 */
	public boolean validateUserAccess(String loginName,String password){
		User user=getUserByLoginName(loginName);
		if(user==null)return false;
		String userPassword=user.getPassword();
		if(userPassword.length()<32){
			userPassword=Md5.toMessageDigest(userPassword);
		}
		if(userPassword==null&&password==null)return true;
		if(userPassword!=null&&userPassword.equals(password))return true;
		return false;
	}
	/**
	 * 返回加密后的密码(Md5)
	 * @param loginName
	 * @param password
	 * @return
	 */
	public String validateUserAccess(String password){
		return Md5.toMessageDigest(password);
	}
	
	public User getUserByCardNo(String cardNo){
		if (cardNo == null) return null;
		List<User> users=userDao.find("select user from User user where user.deleted=false and user.cardNo=?", cardNo);
		if(users==null||users.size()<=0)return null;
		return (User) users.get(0);
	}
	
	/**
	 * 通过部门名称获取部门实体
	 * @param name
	 * @param companyId
	 * @return
	 */
	@Transactional(readOnly = true)
	public Department getDepartmentByCode(String code, Long companyId){
		List<Department> depts = departmentDao.findList("from Department d where d.company.id=? and d.code=? and d.deleted=?", companyId, code, false);
		Department dept = null;
		if(depts.size() == 1){
			dept = depts.get(0);
		}
		return dept;
	}
	/**
	 * 获得所有公司
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<Company> getAllCompanys(){
		return companyDao.findList("from Company c where c.deleted=?", false);
	}

	/**
	 * 通过公司code获取公司Id
	 * @param companyCode
	 * @return Long
	 */
	@Transactional(readOnly = true)
	public  Long getCompanyIdByCompanycode(String companyCode) {
		if (companyCode == null || companyCode.trim().length() <= 0)
			return null;
		Object obj = companyDao.findUnique(
						"from Company company where company.code=? and company.deleted=?",
						companyCode, false);
		if (obj instanceof Company) {
			return ((Company) obj).getId();
		}
		return null;
	}
	/**
	 * 通过工作组名称获取工作组
	 * @param name
	 * @param companyId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public  Workgroup getWorkGroupByName(String name, Long companyId){
		List<Workgroup> workGroups = workGroupDao.find("from Workgroup wg where wg.company.id=? and wg.name=? ", companyId, name);
		if(workGroups.size() == 1){
			return workGroups.get(0);
		}
		return null;
	}
	/**
	 * 根据邮件地址查询用户信息
	 * @param companyId
	 * @param loginName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public User getUser(String email){
		List<User> list=userDao.find("from User u where u.email=? and u.deleted=? ",email, false);
		if(list!=null&&!list.isEmpty()){
			return list.get(0);
		}
		return null;
	}
	@SuppressWarnings("unchecked")
	public List<User> getUserByName(Long companyId,String trueName){
		return userDao.find("from User u where u.companyId=? and u.name=? and u.deleted=? ",companyId,trueName, false);
	}
	/**
	 * 根据角色编码获取角色
	 * @param companyId
	 * @param roleCode
	 * @return
	 */
	public Role getRoleBycode(Long companyId,String roleCode){
		List<Role> roles=roleDao.findList("from Role r where r.deleted=? and (r.companyId is null or r.companyId=? ) and r.code=?",false,companyId,roleCode);
		if(roles!=null&&roles.size()>0){
			return roles.get(0);
		}
		return null;
	}
	/**
	 * 获得平台系统
	 * @return
	 */
	public List<BusinessSystem> getParentSystem(){
		return businessSystemManager.getParentSystem();
	}

	public Role getRoleByName(Long systemId, String roleName,Long companyId) {
		List<Role> roles=roleDao.findList("from Role r where r.deleted=? and r.businessSystem.id=? and r.companyId=? and r.name=?", false,systemId,companyId,roleName);
		if(roles!=null&&roles.size()>0){
			return roles.get(0);
		}
		return null;
	}
	public boolean isBasicSystem(String systemCode){
		if(StringUtils.isEmpty(systemCode)){
			return false;
		}
		if(systemCode.equals("acs")||systemCode.equals("wf")||systemCode.equals("task")||systemCode.equals("portal")||systemCode.equals("mms")||
				systemCode.equals("bs")||systemCode.equals("imatrix")||systemCode.equals("mm")){
			return true;
		}
		return false;
		
	}
	public boolean isBasicSystem(BusinessSystem businessSystem){
		if(businessSystem==null){
			return false;
		}
		return isBasicSystem(businessSystem.getCode());
	}

	
}
