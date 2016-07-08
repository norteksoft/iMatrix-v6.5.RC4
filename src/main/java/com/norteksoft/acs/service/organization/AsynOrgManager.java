package com.norteksoft.acs.service.organization;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.acs.base.enumeration.BranchDataType;
import com.norteksoft.acs.base.orm.hibernate.SimpleHibernateTemplate;
import com.norteksoft.acs.entity.authorization.BranchAuthority;
import com.norteksoft.acs.entity.authorization.Role;
import com.norteksoft.acs.entity.authorization.RoleDepartment;
import com.norteksoft.acs.entity.authorization.RoleFunction;
import com.norteksoft.acs.entity.authorization.RoleUser;
import com.norteksoft.acs.entity.authorization.RoleWorkgroup;
import com.norteksoft.acs.entity.organization.Company;
import com.norteksoft.acs.entity.organization.Department;
import com.norteksoft.acs.entity.organization.DepartmentUser;
import com.norteksoft.acs.entity.organization.User;
import com.norteksoft.acs.entity.organization.UserInfo;
import com.norteksoft.acs.entity.organization.Workgroup;
import com.norteksoft.acs.entity.organization.WorkgroupUser;
import com.norteksoft.acs.service.AcsUtils;
import com.norteksoft.acs.service.authorization.BranchAuthorityManager;
import com.norteksoft.acs.service.authorization.RoleManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.ParameterUtils;
import com.norteksoft.product.util.ThreadParameters;

import java.util.Arrays;
import java.util.Collections;
/**
 * 同步组织机构使用的service
 *
 */

@Service
@Transactional
public class AsynOrgManager {
	private SimpleHibernateTemplate<Company, Long> companyDao;
	private SimpleHibernateTemplate<User, Long> userDao;
	private SimpleHibernateTemplate<UserInfo, Long> userInfoDao;
	private SimpleHibernateTemplate<DepartmentUser, Long> departmentToUserDao;
	private SimpleHibernateTemplate<WorkgroupUser, Long> workGroupToUserDao;
	private SimpleHibernateTemplate<Department, Long> departmentDao;
	private SimpleHibernateTemplate<RoleDepartment, Long> roleDepartmentDao;
	private SimpleHibernateTemplate<BranchAuthority, Long> branchAuthorityDao;
	private SimpleHibernateTemplate<RoleFunction, Long> roleFunctionDao;
	private SimpleHibernateTemplate<RoleUser, Long> roleUserDao;
	private SimpleHibernateTemplate<RoleWorkgroup, Long> roleWorkgroupDao;



	@Autowired
	private AcsUtils acsUtils;
	@Autowired
	private UserInfoManager userInfoManager;
	@Autowired
	private UserManager userManager;
	@Autowired
	private DepartmentManager departmentManager;
	@Autowired
	private WorkGroupManager workGroupManager;
	@Autowired
	private RoleManager roleManager;
	@Autowired
	private BranchAuthorityManager branchAuthorityManager;
	public Long getSystemIdByCode(String code) {
	   return acsUtils.getSystemsByCode(code).getId();
	}
	
	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory){
		userDao = new SimpleHibernateTemplate<User, Long>(sessionFactory, User.class);
		workGroupToUserDao = new SimpleHibernateTemplate<WorkgroupUser, Long>(sessionFactory, WorkgroupUser.class);
		departmentToUserDao = new SimpleHibernateTemplate<DepartmentUser, Long>(sessionFactory, DepartmentUser.class);
		departmentDao = new SimpleHibernateTemplate<Department, Long>(sessionFactory, Department.class);
		userInfoDao = new SimpleHibernateTemplate<UserInfo, Long>(sessionFactory, UserInfo.class);
		companyDao=new SimpleHibernateTemplate<Company,Long>(sessionFactory,Company.class);
		branchAuthorityDao=new SimpleHibernateTemplate<BranchAuthority,Long>(sessionFactory,BranchAuthority.class);
		roleDepartmentDao = new SimpleHibernateTemplate<RoleDepartment, Long>(
				sessionFactory, RoleDepartment.class);
		roleFunctionDao= new SimpleHibernateTemplate<RoleFunction, Long>(
				sessionFactory, RoleFunction.class);
		roleUserDao= new SimpleHibernateTemplate<RoleUser, Long>(
				sessionFactory, RoleUser.class);
		roleWorkgroupDao=new SimpleHibernateTemplate<RoleWorkgroup, Long>(
				sessionFactory, RoleWorkgroup.class);
	}
	@Transactional
	public Response saveUserForWebService(String path,String loginName, String name,
			String password) {
		if(StringUtils.isEmpty(path)){
			return Response.status(500).entity("path必填!").build();
		}
		if(StringUtils.isEmpty(loginName)){
			return Response.status(500).entity("登陆名必填!").build();
		}
		if(StringUtils.isEmpty(name)){
			return Response.status(500).entity("用户名必填!").build();
		}
		if(StringUtils.isEmpty(password)){
			return Response.status(500).entity("密码必填!").build();
		}
		Object o = parsePath(path);
		if(o!=null){
			if(o instanceof Company){
				Company company=(Company)o;
				return saveUserToCompany(company,loginName,name,password,null);
			}else if(o instanceof Department){
				Department department=(Department)o;
				if(department.getBranch()){
					return saveUserToBranch(department,loginName,name,password,null);
				}else{
					if(department.getSubCompanyId()==null){
						return saveUserToCompany(department.getCompany(),loginName,name,password,department);
					}else{
						return saveUserToBranch(departmentDao.get(department.getSubCompanyId()),loginName,name,password,department);
					}
				}
				
			}
		}else{
			return Response.status(500).entity("path错误!").build();
		}
		return Response.status(200).entity("ok").build();
	}

	private Response saveUserToBranch(Department branch, String loginName,
			String name, String password, Department object) {
		List<User> us=userDao.findList("select user from User user join user.userInfos ui where ui.companyId=? and  ui.deleted=? and user.deleted=? and user.subCompanyId=? and user.loginName=?", branch.getCompany().getId(),true,true,branch.getId(),loginName);
		if(us!=null&&us.size()>0){
			return Response.status(500).entity("已存在登录名是相同的已删除用户!").build();
		}
		List<User> users=userDao.findList("from User u where u.deleted=? and u.companyId=? and u.subCompanyId=? and u.loginName=?", false,branch.getCompany().getId(),branch.getId(),loginName);
		User user=null;
		if(users!=null&&users.size()>0){
			user=users.get(0);
			departmentToUserDao.createQuery("delete from DepartmentUser du where du.user.id=?",user.getId()).executeUpdate();
			if(object!=null){
				user.setMainDepartmentId(object.getId());
				DepartmentUser du=new DepartmentUser();
				du.setUser(user);
				du.setDepartment(object);
				du.setSubCompanyId(user.getSubCompanyId());
				du.setCompanyId(branch.getCompany().getId());
				departmentToUserDao.save(du);
			}else{
				DepartmentUser du=new DepartmentUser();
				du.setUser(user);
				du.setDepartment(branch);
				du.setCompanyId(branch.getCompany().getId());
				du.setSubCompanyId(branch.getId());
				departmentToUserDao.save(du);
			}
			user.setName(name);
			user.setPassword(password);
			userDao.save(user);
		}else{
			ThreadParameters parameters = new ThreadParameters(branch.getCompany().getId());
			ParameterUtils.setParameters(parameters);
			UserInfo userInfo=null;
			//新建用户
			user=new User();
			user.setLoginName(loginName);
			user.setName(name);
			user.setPassword(password);
			user.setCompanyId(branch.getCompany().getId());
			user.setSubCompanyId(branch.getId());
			user.setSubCompanyName(branch.getName());
			userManager.saveUser(user);
			if(object!=null){
				user.setMainDepartmentId(object.getId());
				DepartmentUser du=new DepartmentUser();
				du.setUser(user);
				du.setDepartment(object);
				du.setCompanyId(branch.getCompany().getId());
				du.setSubCompanyId(branch.getId());
				departmentToUserDao.save(du);
			}else{
				DepartmentUser du=new DepartmentUser();
				du.setUser(user);
				du.setDepartment(branch);
				du.setCompanyId(branch.getCompany().getId());
				du.setSubCompanyId(branch.getId());
				departmentToUserDao.save(du);
			}
			//给用户添加基本的权限
			userInfoManager.giveNewUserPortalCommonRole(user);
			userInfo=new UserInfo();
			userInfo.setUser(user);
			userInfo.setPasswordUpdatedTime(new Date());
			userInfoManager.add(userInfo);
		}
		return Response.status(200).entity("ok").build();
	}
	private Response saveUserToCompany(Company company, String loginName,String name,String password,Department department) {
		User user=null;
		List<User> us=userDao.findList("select user from User user join user.userInfos ui where ui.companyId=? and  ui.deleted=? and user.deleted=? and user.subCompanyId is null and user.loginName=?", company.getId(),true,true,loginName);
		if(us!=null&&us.size()>0){
			return Response.status(500).entity("已存在登录名是相同的已删除用户!").build();
		}
		List<User> users = userDao.findList("from User u where u.companyId=? and u.deleted=? and u.subCompanyId is null and loginName=?",company.getId(),false,loginName);
		if(users!=null&&users.size()>0){
			user=users.get(0);
			departmentToUserDao.createQuery("delete from DepartmentUser du where du.user.id=?",user.getId()).executeUpdate();
			if(department!=null){
				user.setMainDepartmentId(department.getId());
				DepartmentUser du=new DepartmentUser();
				du.setUser(user);
				du.setDepartment(department);
				du.setSubCompanyId(department.getSubCompanyId());
				du.setCompanyId(company.getId());
				departmentToUserDao.save(du);
			}
			user.setName(name);
			user.setPassword(password);
			userDao.save(user);
		}else{
			ThreadParameters parameters = new ThreadParameters(company.getId());
			ParameterUtils.setParameters(parameters);
			UserInfo userInfo=null;
			//新建用户
			user=new User();
			user.setLoginName(loginName);
			user.setName(name);
			user.setPassword(password);
			user.setCompanyId(company.getId());
			user.setSubCompanyId(null);
			user.setSubCompanyName(company.getName());
			userManager.saveUser(user);
			if(department!=null){
				user.setMainDepartmentId(department.getId());
				DepartmentUser du=new DepartmentUser();
				du.setUser(user);
				du.setDepartment(department);
				du.setSubCompanyId(department.getSubCompanyId());
				du.setCompanyId(company.getId());
				departmentToUserDao.save(du);
			}
			//给用户添加基本的权限
			userInfoManager.giveNewUserPortalCommonRole(user);
			userInfo=new UserInfo();
			userInfo.setUser(user);
			userInfo.setPasswordUpdatedTime(new Date());
			userInfoManager.add(userInfo);
		}
		return Response.status(200).entity("ok").build();
	}

	@Transactional
	public Response saveDepartmentForWebService(String path,Boolean branchFlag, String name,
			String code) {
		if(StringUtils.isEmpty(path)){
			return Response.status(500).entity("path必填!").build();
		}
		if(StringUtils.isEmpty(name)){
			return Response.status(500).entity("部门名称必填!").build();
		}
		Object o = parsePath(path);
		if(o!=null){
			return addDepartment(o,branchFlag,name,code);
		}else{
			return Response.status(500).entity("path错误!").build();
		}
	}
	private Response addDepartment(Object o, Boolean branchFlag, String name,
			String code) {
		Department department=null;
		if(o instanceof Company){
			Company company=(Company)o;
			Long count = departmentDao.findLong("select count(*) from Department d where d.deleted=? and d.company.id=? and d.subCompanyId is null and d.name=? and d.parent.id is null",false, company.getId(),name);
			if(count>0){
				return Response.status(500).entity("该部门已存在!").build();
			}
			ThreadParameters parameters = new ThreadParameters(company.getId());
			ParameterUtils.setParameters(parameters);
			department = new Department();
			department.setCode(departmentManager.createDepartmentCode());
			department.setCompany(company);
			department.setName(name);
			department.setBranch(branchFlag==null?false:branchFlag);
			
		}else if(o instanceof Department){
			Department dept=(Department)o;
			Long count = departmentDao.findLong("select count(*) from Department d where d.deleted=? and d.company.id=? and d.parent.id=? and d.name=?",false,dept.getCompany().getId(),dept.getId(),name);
			if(count>0){
				return Response.status(500).entity("该部门已存在!").build();
			}
			ThreadParameters parameters = new ThreadParameters(dept.getCompany().getId());
			ParameterUtils.setParameters(parameters);
			department = new Department();
			department.setCode(departmentManager.createDepartmentCode());
			department.setCompany(dept.getCompany());
			department.setName(name);
			department.setParent(dept);
			department.setSubCompanyId(dept.getSubCompanyId());
			department.setSubCompanyName(dept.getSubCompanyName());
			department.setBranch(branchFlag==null?false:branchFlag);
		}
		departmentDao.save(department);
		return Response.status(200).entity("ok").build();
	}

	@Transactional
	public Response deleteDepartmentForWebService(String path,String code) {
		Object o = parsePath(path);
		if(o!=null){
			if(o instanceof Company){
				return Response.status(500).entity("路径错误!").build();
			}else if(o instanceof Department){
				Department department=(Department)o;
				ThreadParameters parameters = new ThreadParameters(department.getCompany().getId());
				ParameterUtils.setParameters(parameters);
				if(department.getBranch()){
					if(validateBranchDelete(department.getId())){
						departmentDao.delete(department);
						return Response.status(200).entity("ok").build();
					}else{
						return Response.status(500).entity("请先删除分支机构下的部门,人员,工作组,分支机构,分支机构授权管理,角色!").build();
					}
				}else{
					if(validateDepartmentDelete(department.getId())){
						List<User> users=userManager.getUsersByDeptId(department.getId());
						for(User user:users){
							departmentToUserDao.createQuery("delete from DepartmentUser du where du.user.id=? and du.department.id=?",user.getId(),department.getId()).executeUpdate();
							List<DepartmentUser> departmentToUser = departmentToUserDao.findList("from DepartmentUser du where  du.deleted=? and du.user.id=?",false,user.getId());
							if(departmentToUser==null||departmentToUser.size()==0){
								if(department.getSubCompanyId()!=null){
									DepartmentUser du=new DepartmentUser();
									du.setUser(user);
									du.setDepartment(departmentDao.get(user.getSubCompanyId()));
									du.setCompanyId(user.getCompanyId());
									du.setSubCompanyId(user.getSubCompanyId());
									departmentToUserDao.save(du);
								}
							}
							if(user.getMainDepartmentId().equals(department.getId())){
								user.setMailboxDeploy(null);
							}
						}
						departmentDao.delete(department);
						return Response.status(200).entity("ok").build();
					}else{
						return Response.status(500).entity("请先删除子部门或分支机构!").build();
					}
				}
			}else{
				return Response.status(500).entity("未知错误!").build();
			}
		}else{
			return Response.status(500).entity("路径错误!").build();
		}
	}
	@Transactional
	public Response deleteUserForWebService(String path,String loginName) {
		if(StringUtils.isEmpty(path)){
			return Response.status(500).entity("path必填!").build();
		}
		if(StringUtils.isEmpty(loginName)){
			return Response.status(500).entity("登陆名必填!").build();
		}
		Object o = parsePath(path);
		if(o!=null){
			return deleteUser(o,loginName);
		}else{
			return Response.status(500).entity("path错误!").build();
		}
	}
	private Response deleteUser(Object o,String loginName) {
		List<User> users=null;
		if(o instanceof Company){
			Company company=(Company)o;
			users=userDao.findList("from User u where u.companyId=? and u.deleted=? and u.subCompanyId is null and u.loginName=?",company.getId(),false,loginName);
			if(users!=null&&users.size()>0){
				User user=users.get(0);
				userInfoManager.clearUser(user.getId());
//				clearUser(user.getId());
				user.setDeleted(true);
				return Response.status(200).entity("ok").build();
			}else{
				return Response.status(500).entity("用户不存在!").build();
			}
		}else if(o instanceof Department){
			Department department=(Department)o;
			
			if(department.getSubCompanyId()==null){
				users=userDao.findList("from User u where u.companyId=? and u.deleted=? and u.subCompanyId is null and u.loginName=?",department.getCompany().getId(),false,loginName);
			}else{
				users=userDao.findList("from User u where u.companyId=? and u.deleted=? and u.companyId=? and u.subCompanyId=? and u.loginName=?",department.getCompany().getId(),false,department.getCompany().getId(),department.getSubCompanyId(),loginName);
			}
			if(users!=null&&users.size()>0){
				User user=users.get(0);
				userInfoManager.clearUser(user.getId());
//				clearUser(user.getId());
				user.setDeleted(true);
				return Response.status(200).entity("ok").build();
			}else{
				return Response.status(500).entity("用户不存在!").build();
			}
			
		}else{
			return Response.status(500).entity("用户不存在!").build();
		}
		
	}
	/**
	 * 解析路径     --测试公司\部门1\分支机构1-3\部门1
	 * @param path
	 * @return
	 */
	private Object parsePath(String path) {
		if(StringUtils.isEmpty(path)){
			return null;
		}
		String[] names=path.split("\\\\");
		List<Company> companys=companyDao.findList("from Company c where c.deleted=? and c.name=?", false,names[0]);
		for(Company company:companys){
			if(1==names.length){
				return company;
			}
			Object o=findDepartment(company,null,names,1);
			if(o!=null){
				return o;
			}
		}
		return null;
	}
	private Object parsePath(String[] strs){
		StringBuilder sb=new StringBuilder(ContextUtils.getCompanyName());
		for(String str:strs){
			sb.append("\\"+str.split("_")[0]);
		}
		return parsePath(sb.toString());
	}

	private Object findDepartment(Company company,Department dept,String[] names, int i) {
		Object obj=null;
		if(i>names.length-1){  
			return null;
		}
		StringBuilder hql=new StringBuilder("from Department d where d.deleted=? and d.name=? and d.company.id=? ");
		if(dept!=null){
			hql.append("and d.parent.id="+dept.getId().toString());
		}else{
			hql.append("and d.parent is null");
		}
		List<Department> departments=departmentDao.findList(hql.toString(),false,names[i],company.getId());
		for(Department department:departments){
			if(i==names.length-1){
				obj=department;
			}else{
				obj=findDepartment(company,department,names,++i);
			}
		}
		return obj;
	}

//	private void clearUser(Long id) {
//		User user=userManager.getUserById(id);
//		UserInfo userInfo = user.getUserInfo();
//		userInfo.setDeleted(true);
//		departmentToUserDao.createQuery("delete from DepartmentUser du where du.user.id=?",id).executeUpdate();
//		workGroupToUserDao.createQuery("delete from WorkgroupUser du where du.user.id=?",id).executeUpdate();
//		userInfo.setDeleted(true);
//		userInfoDao.save(userInfo);
//		userDao.save(user);
//	}
	/**
	 * webService版
	 * @param departmentId2
	 * @param company
	 */

	private boolean validateDepartmentDelete(Long deptId){
		List<Department> subDepartments=departmentManager.getSubDeptments(deptId);
		List<Workgroup> workgroups=workGroupManager.getWorkgroupsByBranch(deptId);
		List<BranchAuthority> branchAuthoritys=branchAuthorityManager.getBranchAuthorityByBranch(deptId);
		List<Role> roles=roleManager.getRoleByBranches(deptId);
		if((subDepartments!=null && subDepartments.size()>0) || (workgroups!=null && workgroups.size()>0) || (branchAuthoritys!=null&&branchAuthoritys.size()>0) || (roles!=null&&roles.size()>0)){
			return false;
		}else{
			return true;
		}
	}
	private boolean validateBranchDelete(Long deptId){
		List<Department> subDepartments=departmentManager.getSubDeptments(deptId);
		List<User> users=userManager.getUsersBySubCompany(deptId);
		List<Workgroup> workgroups=workGroupManager.getWorkgroupsByBranch(deptId);
		List<BranchAuthority> branchAuthoritys=branchAuthorityManager.getBranchAuthorityByBranch(deptId);
		List<Role> roles=roleManager.getRoleByBranches(deptId);
		if((subDepartments!=null && subDepartments.size()>0) || (users!=null&& users.size()>0) || (workgroups!=null && workgroups.size()>0) || (branchAuthoritys!=null&&branchAuthoritys.size()>0) || (roles!=null&&roles.size()>0)){
			return false;
		}else{
			return true;
		}
	}
	
	/**
	 * 同步组织结构
	 * @param remoteUserSet
	 * @param remoteDeptSet
	 * @param localUserSet
	 * @param localDeptSet
	 */
	@Transactional
	public String synOrg(Set<String> remoteUserSet, Set<String> remoteDeptSet,
			Set<String> localUserSet, Set<String> localDeptSet) {
		try{
			Set<String> tempUserAdd=new HashSet<String>();
			Set<String> tempUserRemove=new HashSet<String>();
			Set<String> tempDeptAdd=new HashSet<String>();
			Set<String> tempDeptRemove=new HashSet<String>();
			List<User> users=new ArrayList<User>();
			for(String str:localUserSet){//获取要删除的用户
				if(!remoteUserSet.contains(str)){
					tempUserRemove.add(str);
				}
			}
			for(String str:remoteUserSet){//获取要添加的用户
				if(!localUserSet.contains(str)){
					tempUserAdd.add(str);
				}
			}
			for(String str:localDeptSet){//获取要删除的部门
				if(!remoteDeptSet.contains(str)){
					tempDeptRemove.add(str);
				}
			}
			for(String str:remoteDeptSet){//获取要添加的部门
				if(!localDeptSet.contains(str)){
					tempDeptAdd.add(str);
				}
			}
			users=constructUser(tempUserRemove);//切断用户与部门的关系并将其设置为已删除
			deleteDept(tempDeptRemove);//删除要删除的部门
			addDept(tempDeptAdd);//添加部门
			addUser(tempUserAdd,users);//添加用户
			return "success";
		}catch (Exception e) {
			return e.getMessage();
		}
		
		
	}
	/**
	 * 删除用户与部门的关系、把用户设置为delete状态
	 * @param tempUserRemove
	 * @return
	 */
	private List<User> constructUser(Set<String> tempUserRemove) {
		List<User> users=new ArrayList<User>();
		for(String userStr:tempUserRemove){
			User user=parseUser(userStr);
			userInfoManager.clearUser(user);
			users.add(user);
		}
		return users;
	}
	/**
	 * 添加用户
	 * @param tempUserAdd
	 * @param users
	 */
	private void addUser(Set<String> tempUserAdd,List<User> users) {
		//开发部_-\liudongxia#刘冬霞#ldx@n.com#false#1#202cb962ac59075b964b07152d234b70#_1;
		//\liudongxia#刘冬霞#ldx@n.com#false#1#202cb962ac59075b964b07152d234b70#_3;表示集团下的已删除用户，_3表示已删除用户
		//分支1\liudongxia#刘冬霞#ldx@n.com#false#1#202cb962ac59075b964b07152d234b70#_3;表示分支1下的已删除用户
		for(String userStr:tempUserAdd){
			boolean isAdd=false;
			String branchName="";
			String loginName="";
			String loginNameStr="";
			String[] userinfos = userStr.substring((userStr.lastIndexOf("\\")==-1?0:userStr.lastIndexOf("\\")+1),userStr.lastIndexOf("_")).split("#");
			String username=userinfos[1];
			String email=userinfos[2];
			String sex=userinfos[3];
			String weight=userinfos[4];
			String password=userinfos[5];
			Department dept=null;
			if(userStr.endsWith("#_3")){//已删除用户
				branchName = userStr.substring(0,userStr.lastIndexOf("\\"));
			}else{
				loginNameStr=userStr.substring(userStr.lastIndexOf("\\")+1);
				String str2=userStr.substring(0,(userStr.lastIndexOf("_+")==-1?0:userStr.lastIndexOf("_+")));
				if(!str2.equals("")){
					branchName=str2.substring(str2.lastIndexOf("\\")==-1?0:str2.lastIndexOf("\\")+1);
				}
				String[] strs=userStr.split("\\\\");
				if(strs.length>1){
					dept=parseDept((String[])Arrays.copyOfRange(strs, 0, strs.length-1));
				}
			}
			loginName=userinfos[0];
			for(User user:users){//到那些结构不匹配的用户里面去找登陆名和分支机构与之相同的用户 ,如果有回复该用户并重设该用户结构。
				if(loginName.equals(user.getLoginName())&&((user.getSubCompanyId()!=null&&user.getSubCompanyName().equals(branchName))||(user.getSubCompanyId()==null&&branchName.equals("")))){
					user.setSubCompanyName(branchName.equals("")?ContextUtils.getCompanyName():branchName);
					if(userStr.endsWith("#_3")){//已删除用户
						UserInfo userInfo = user.getUserInfo();
						user.setDeleted(true);
						userInfo.setDeleted(true);
						userManager.saveUser(user);
						userInfoManager.save(userInfo);
					}else{
						user.setDeleted(false);
						UserInfo userInfo = user.getUserInfo();
						userInfo.setDeleted(false);
						userInfoManager.save(userInfo);
						if(dept!=null){
							if(loginNameStr.split("#_")[1].equals("1")){
								user.setMainDepartmentId(dept.getId());
							}
						}
						user.setName(username);
						user.setEmail(email);
						user.setSex(Boolean.valueOf(sex));
						user.setWeight(Integer.valueOf(weight));
						user.setPassword(password);
						userManager.saveUser(user);
						if(dept!=null){
							DepartmentUser du=new DepartmentUser();
							du.setUser(user);
							du.setDepartment(dept);
							du.setSubCompanyId(dept.getSubCompanyId());
							du.setCompanyId(dept.getCompany().getId());
							departmentToUserDao.save(du);
						}
					}
					isAdd=true;
					break;
				}
			}
			if(!isAdd){
				User user=null;
				List<User> us=userDao.findList("from User u where u.companyId=? and u.loginName=? and u.subCompanyName=?", ContextUtils.getCompanyId(),loginName,branchName.equals("")?ContextUtils.getCompanyName():branchName);
				if(us!=null&&us.size()>0){
					user=us.get(0);
				}else{
					user=new User();
				}
				if(userStr.endsWith("#_3")){//已删除用户
					user.setDeleted(true);
				}else{
					user.setDeleted(false);
				}
				user.setLoginName(loginName);
				user.setName(username);
				user.setEmail(email);
				user.setSex(Boolean.valueOf(sex));
				user.setWeight(Integer.valueOf(weight));
				user.setPassword(password);
				user.setCompanyId(ContextUtils.getCompanyId());
				if(userStr.indexOf("\\")<0){//表示集团下的无部门人员
					user.setSubCompanyId(null);
					user.setSubCompanyName(ContextUtils.getCompanyName());
					userManager.saveUser(user);
				}else if(userStr.endsWith("#_3")){//表示已删除用户
					if(StringUtils.isEmpty(branchName)){//表示是集团下的人
						user.setSubCompanyId(null);
						user.setSubCompanyName(ContextUtils.getCompanyName());
					}else{
						com.norteksoft.product.api.entity.Department branch = ApiFactory.getAcsService().getBranchByName(branchName);
						if(branch!=null){//分支存在，分支不可能不存在
							user.setSubCompanyId(branch.getId());
							user.setSubCompanyName(branch.getName());
							userManager.saveUser(user);
						}
					}
				}else{
					if(loginNameStr.split("#_")[1].equals("1")){
						user.setMainDepartmentId(dept.getId());
					}
					user.setSubCompanyId(dept.getSubCompanyId());
					user.setSubCompanyName(dept.getSubCompanyName());
					List<DepartmentUser> dus=departmentToUserDao.findList("from DepartmentUser du where du.user.id=? and du.department.id=? and du.deleted=?", user.getId(),dept.getId(),false);
					if(dus==null||dus.size()==0){
						DepartmentUser du=new DepartmentUser();
						userManager.saveUser(user);
						du.setUser(user);
						du.setDepartment(dept);
						du.setSubCompanyId(dept.getSubCompanyId());
						du.setCompanyId(dept.getCompany().getId());
						departmentToUserDao.save(du);
					}
				}
				userInfoManager.giveNewUserPortalCommonRole(user);
				UserInfo userInfo=null;
				List<UserInfo> userInfos=userInfoDao.findList("from UserInfo ui where ui.user.id=?", user.getId());
				if(userInfos==null||userInfos.size()==0){
					userInfo=new UserInfo();
				}else{
					userInfo=userInfos.get(0);
				}
				if(user.isDeleted()){
					userInfo.setDeleted(true);
				}else{
					userInfo.setDeleted(false);
				}
				userInfo.setUser(user);
				userInfo.setPasswordUpdatedTime(new Date());
				userInfoManager.add(userInfo);
			}
		}
	}
	/**
	 * 添加部门
	 * @param tempDeptAdd
	 */
	private void addDept(Set<String> tempDeptAdd) {
		List<String[]> deptArr=new ArrayList<String[]>();
		for(String str1:tempDeptAdd){
			deptArr.add(str1.split("\\\\"));
		}
		Collections.sort(deptArr, new Comparator<String[]>() {//排序要添加的部门,上层部门先添加
			public int compare(String[] o1, String[] o2) {
				if(o1.length>o2.length){
					return 1;
				}else{
					return -1;
				}
			}
		});
		for(String[] strArr:deptArr){
			Department pDept=null;
			String deptName=strArr[strArr.length-1].split("_")[0];
			if(strArr.length>1){
				pDept=parseDept((String[])Arrays.copyOfRange(strArr, 0, strArr.length-1));
			}
			Department newDepartment=new Department();
			newDepartment.setCompany(companyDao.get(ContextUtils.getCompanyId()));
			newDepartment.setName(deptName);
			newDepartment.setShortTitle(deptName);
			newDepartment.setCode(departmentManager.createDepartmentCodeForBranch());
			newDepartment.setBranch(strArr[strArr.length-1].split("_")[1].equals("+")?true:false);
			newDepartment.setWeight(1);
			newDepartment.setParent(pDept);
			departmentDao.save(newDepartment);
			if(pDept==null){
				newDepartment.setSubCompanyId(null);
				newDepartment.setSubCompanyName(ContextUtils.getCompanyName());
			}else if(newDepartment.getBranch()){
				newDepartment.setSubCompanyId(newDepartment.getId());
			}else{
				newDepartment.setSubCompanyId(pDept.getSubCompanyId());
			}
		}
	}
	/**
	 * 删除部门
	 * @param tempDeptRemove
	 */
	private void deleteDept(Set<String> tempDeptRemove) {
		List<String[]> deptArr=new ArrayList<String[]>();
		for(String str1:tempDeptRemove){
			deptArr.add(str1.split("\\\\"));
		}
		Collections.sort(deptArr, new Comparator<String[]>() {//对部门信息进行排序先删除最低层的部门
			public int compare(String[] o1, String[] o2) {
				if(o1.length>o2.length){
					return -1;
				}else{
					return 1;
				}
			}
		});
		for(String[] deptStr:deptArr){
			Department d=parseDept(deptStr);
			cleanDept(d);
			departmentDao.delete(d);//应为排过序,暂时不用考虑外键问题
		}
	}
	/**
	 * 删除部门前清除关系
	 * @param d
	 */
	private void cleanDept(Department d) {
		departmentToUserDao.createQuery("delete from DepartmentUser du where du.department.id=?",d.getId()).executeUpdate();//切断用户与部门之间的关系
		roleDepartmentDao.createQuery("delete from RoleDepartment du where du.department.id=?",d.getId()).executeUpdate();
		if(d.getBranch()){
			branchAuthorityDao.createQuery("delete from BranchAuthority du where du.branchesId=?", d.getId()).executeUpdate();
			List<Role> roles=roleManager.getRoleDao().findList("from Role r where r.subCompanyId=?", d.getId());
			if(roles!=null){
				for(Role role:roles){
					//删除分支机构管理对应的角色的中间表
					branchAuthorityDao.createQuery("delete from BranchAuthority ba where ba.branchDataType=? and ba.dataId=?", BranchDataType.ROLE,role.getId()).executeUpdate();
					//删除角色与资源中间表
					roleFunctionDao.createQuery("delete from RoleFunction rf where rf.role.id=?",role.getId()).executeUpdate();
					//删除角色用户中间表
					roleDepartmentDao.createQuery("delete from RoleDepartment ru where ru.role.id=?", role.getId()).executeUpdate();
					//删除角色用户中间表
					roleWorkgroupDao.createQuery("delete from RoleWorkgroup ru where ru.role.id=?", role.getId()).executeUpdate();
					//删除角色用户中间表
					roleUserDao.createQuery("delete from RoleUser ru where ru.role.id=?", role.getId()).executeUpdate();
					roleManager.getRoleDao().delete(role);
				}
			}
		}
		
	}
	/**
	 * 根据字符串数组信息,去得到部门。
	 * @param deptStr
	 * @return
	 */
	private Department parseDept(String[] deptStr) {
		return (Department)parsePath(deptStr);
	}
	/**
	 * 根据用户字符串信息去找到用户。
	 * @param userStr
	 * @return
	 */
	private  User parseUser(String userStr) {
		String str2=userStr.substring(0,(userStr.lastIndexOf("_+")==-1?0:userStr.lastIndexOf("_+")));
		String branchName="";
		if(!str2.equals("")){
			branchName=str2.substring(str2.lastIndexOf("\\")==-1?0:str2.lastIndexOf("\\")+1);
		}
		String loginName=userStr.substring((userStr.lastIndexOf("\\")==-1?0:userStr.lastIndexOf("\\")+1),userStr.lastIndexOf("_")).split("#")[0];
		return userDao.findList("from User u where u.companyId=? and u.loginName=? and u.subCompanyName=?", ContextUtils.getCompanyId(),loginName,branchName.equals("")?ContextUtils.getCompanyName():branchName).get(0);
	}

	public List<User> getDeletedUser(){
		return userDao.findList("from User u where u.companyId=? and u.deleted=?", ContextUtils.getCompanyId(),true);
	}


				
}