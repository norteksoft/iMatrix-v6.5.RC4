package com.norteksoft.acs.service.authorization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.acs.base.enumeration.BranchDataType;
import com.norteksoft.acs.base.enumeration.FunctionType;
import com.norteksoft.acs.base.orm.hibernate.SimpleHibernateTemplate;
import com.norteksoft.acs.base.utils.log.LogUtilDao;
import com.norteksoft.acs.entity.authorization.BusinessSystem;
import com.norteksoft.acs.entity.authorization.Function;
import com.norteksoft.acs.entity.authorization.FunctionGroup;
import com.norteksoft.acs.entity.authorization.FunctionVo;
import com.norteksoft.acs.entity.authorization.Role;
import com.norteksoft.acs.entity.authorization.RoleDepartment;
import com.norteksoft.acs.entity.authorization.RoleFunction;
import com.norteksoft.acs.entity.authorization.RoleUser;
import com.norteksoft.acs.entity.authorization.RoleWorkgroup;
import com.norteksoft.acs.entity.organization.Company;
import com.norteksoft.acs.entity.organization.Department;
import com.norteksoft.acs.entity.organization.User;
import com.norteksoft.acs.entity.organization.Workgroup;
import com.norteksoft.acs.service.AcsUtils;
import com.norteksoft.acs.service.organization.CompanyManager;
import com.norteksoft.mms.module.entity.Menu;
import com.norteksoft.mms.module.service.MenuManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.JsonParser;
import com.norteksoft.product.util.PropUtils;
import com.norteksoft.product.util.tree.ZTreeNode;
import com.norteksoft.product.web.struts2.Struts2Utils;

/**
 * 角色管理
 */
@Service
@Transactional
public class RoleManager {
	private static String COMPANY_ID = "company.id";
	private static String DELETED = "deleted";
	private static String COMPANYID = "companyId"; 
	private static String ROLE_ID = "role.id";
	private static String ACS = "acs";
	private static String ACS_SYSTEM_ADMIN="acsSystemAdmin";//系统管理员角色编码
	private static String ACS_SECURITY_ADMIN="acsSecurityAdmin";//安全管理员角色编码
	private static String ACS_AUDIT_ADMIN="acsAuditAdmin";//审计管理员角色编码
	private static String ACS_BRANCH_ADMIN="acsBranchAdmin";//分支机构管理员角色编码
	private SimpleHibernateTemplate<Role, Long> roleDao;
	private SimpleHibernateTemplate<Function, Long> functionDao;
	private SimpleHibernateTemplate<RoleFunction, Long> role_fDao;
	private SimpleHibernateTemplate<RoleDepartment, Long> roleDepartmentDao;
	private SimpleHibernateTemplate<RoleUser, Long> roleUserDao;
	private SimpleHibernateTemplate<Workgroup, Long> workGroupDao;
	private SimpleHibernateTemplate<RoleWorkgroup, Long> role_wDao;// FunctionGroup
	private SimpleHibernateTemplate<FunctionGroup, Long> functionGroupDao;
	private SimpleHibernateTemplate<User, Long> userDao;
	private SimpleHibernateTemplate<Department, Long> departmentDao;
	private SimpleHibernateTemplate<Menu, Long> menuDao;
	private SimpleHibernateTemplate<BusinessSystem,Long> businessSystemDao;
	private SimpleHibernateTemplate<RoleFunction,Long> roleFunctionDao;
	private LogUtilDao logUtilDao;
	private Long companyId;
	@Autowired
	private AcsUtils acsUtils;
	@Autowired
	private CompanyManager companyManager;
	@Autowired
	private StandardRoleManager standardRoleManager;
	@Autowired
	private MenuManager menuManager;
	public Long getCompanyId() {
		if (companyId == null) {
			return ContextUtils.getCompanyId();
		} else
			return companyId;
	}
	public Long getSystemIdByCode(String code) {
			return acsUtils.getSystemsByCode(code).getId();
	}
	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		businessSystemDao = new SimpleHibernateTemplate<BusinessSystem,Long>(sessionFactory,BusinessSystem.class);
		menuDao = new SimpleHibernateTemplate<Menu, Long>(sessionFactory,Menu.class);
		roleDao = new SimpleHibernateTemplate<Role, Long>(sessionFactory,
				Role.class);
		functionDao = new SimpleHibernateTemplate<Function, Long>(
				sessionFactory, Function.class);
		role_fDao = new SimpleHibernateTemplate<RoleFunction, Long>(
				sessionFactory, RoleFunction.class);
		roleDepartmentDao = new SimpleHibernateTemplate<RoleDepartment, Long>(
				sessionFactory, RoleDepartment.class);
		roleUserDao = new SimpleHibernateTemplate<RoleUser, Long>(
				sessionFactory, RoleUser.class);
		workGroupDao = new SimpleHibernateTemplate<Workgroup, Long>(
				sessionFactory, Workgroup.class);
		role_wDao = new SimpleHibernateTemplate<RoleWorkgroup, Long>(
				sessionFactory, RoleWorkgroup.class);
		functionGroupDao = new SimpleHibernateTemplate<FunctionGroup, Long>(
				sessionFactory, FunctionGroup.class);
		userDao = new SimpleHibernateTemplate<User, Long>(sessionFactory,
				User.class);
		departmentDao = new SimpleHibernateTemplate<Department, Long>(sessionFactory,
				Department.class);
		logUtilDao = new LogUtilDao(sessionFactory);
		roleFunctionDao = new SimpleHibernateTemplate<RoleFunction,Long>(sessionFactory,RoleFunction.class);
	}

	public Role getRole(Long id) {
		return roleDao.get(id);
	}
	
	public Role getRole(Long systemId,String code) {
		List<Role> roles=roleDao.findList("from Role role where role.code = ? and role.deleted = ? and role.businessSystem.id=? ", code,false,systemId);
		if(roles!=null && roles.size()>0){
			return roles.get(0);
		}else{
			return null;
		}
	}
	
	public Role getRoleByCode(String code) {
		return (Role)roleDao.findUnique(
				"select role from Role role where role.code = ? and role.deleted = ?",
				code,false);
	}
	public Role getRoleByCodeNotUnique(String code) {
		List<Role> roles=roleDao.findList(
				"select role from Role role where role.code = ? and role.deleted = ?",
				code,false);
		if(roles!=null&&roles.size()>0){
			return roles.get(0);
		}
		return null;
	}
	
	public List<Role> getRolesByCodes(String... codes){
		if(codes == null || codes.length==0) return new ArrayList<Role>();
		Object[] prmts = new Object[codes.length+1];
		prmts[0] = Boolean.FALSE;
		System.arraycopy(codes, 0, prmts, 1, codes.length);
		StringBuilder hql = new StringBuilder("from Role role where role.deleted=?");
		for(int i=0;i<codes.length;i++){
			if(i == 0){
				hql.append(" and (role.code = ?");
			}else{
				hql.append(" or role.code = ?");
			}
			if(i == (codes.length-1)) hql.append(")");
		}
		return roleDao.findList(hql.toString(), prmts);
	}
	
	public void saveRoleUser(RoleUser roleUser){
		roleUserDao.save(roleUser);
	}

	public Page<Role> getAllRoles(Page<Role> page, Long businessSystemId) {
		BusinessSystem bs=businessSystemDao.get(businessSystemId);
		String code=bs.getCode()+"_Everyone";
		return roleDao.searchPageByHql(page,
			"select role from Role role join role.businessSystem bs where bs.id = ? and role.deleted=? and bs.deleted=? " +
			"and (role.companyId is null or role.companyId=?) and role.code<>? order by role.weight desc ",
			businessSystemId, false,false, ContextUtils.getCompanyId(),code);
	}
	/**
	 * 查询系统下的所有角色列表，不包含角色roleId
	 * @param businessSystemId
	 * @param roleId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Role> getAllRolesExceptRole(Long businessSystemId,List<Long> roleIds) {
		Object[] objs = new Object[4+roleIds.size()];
		StringBuilder sb = new StringBuilder();
		sb.append("select role from Role role join role.businessSystem bs where bs.id = ? and role.deleted=? and bs.deleted=? " +
				"and (role.companyId is null or role.companyId=?)");
		if(roleIds.size()>0){
			sb.append(" and (");
		}
		objs[0] = businessSystemId;
		objs[1] = false;
		objs[2] = false;
		objs[3] = ContextUtils.getCompanyId();
		
		int i=0;
		int j = 4;
		for(Long roleId:roleIds){
			sb.append(" role.id<>? ");
			objs[j++] = roleId;
			i++;
			if(i<roleIds.size()){
				sb.append(" and ");
			}
		}
		if(roleIds.size()>0){
			sb.append(" ) ");
		}
		sb.append(" order by role.weight desc");
		return roleDao.find(sb.toString(),objs);
	}
	
	public List<Role> getAllRoles() {
		return roleDao.findAll();
	}

	public void saveRole(Role role) {
		roleDao.save(role);
	}

	public void deleteRole(Long id) {
		Role role = roleDao.get(id);
		if("acsSystemAdmin".equals(role.getCode()) ||
				"acsSecurityAdmin".equals(role.getCode()) ||
				"acsAuditAdmin".equals(role.getCode())) return;
		roleDao.delete(role);
	}
	
	public void deleteRoles(List<Long> ids){
		String logSign="";//该字段只是为了标识日志信息：角色名称
		for(Long id : ids){
			Role role=getRole(id);
			if(StringUtils.isNotEmpty(logSign)){
				logSign+=",";
			}
			logSign+=role.getName();
			
			deleteRole(id);
		}
		if(StringUtils.isNotEmpty(logSign))
			logUtilDao.debugLog("角色管理", "删除角色：" + logSign ,getSystemIdByCode(ACS));
	}

	public Page<Function> roleToFunctionList(Page<Function> functionpage,
			Function entity) {
		if (entity != null) {
			String functionName = entity.getName();
			String functionId = entity.getCode();

			if (functionName != null && !"".equals(functionName)
					&& functionId != null && !"".equals(functionId)) {
				return functionDao.findByCriteria(functionpage, Restrictions
						.like("name", "%" + entity.getName()
								+ "%"), Restrictions.like("code", "%"
						+ entity.getCode() + "%"));
			}
			if (functionName != null && !"".equals(functionName)) {
				return functionDao.findByCriteria(functionpage, Restrictions
						.like("name", "%" + entity.getName() + "%"));
			}
			if (functionId != null && !"".equals(functionId)) {
				return functionDao
						.findByCriteria(functionpage, Restrictions.like(
								"code", "%" + entity.getCode() + "%"));
			}
		}
		return functionDao.findByCriteria(functionpage);
	}

	
	public Page<FunctionGroup> roleToFunctionList2(
			Page<FunctionGroup> functionpage, Function entity, Long sysId) {
		return functionGroupDao.findByCriteria(functionpage, Restrictions.eq(
				"businessSystem.id", sysId), Restrictions.eq(DELETED, false));
	}

	

	public Page<FunctionGroup> roleRomoveFunctionList2(
			Page<FunctionGroup> functionpage, Function entity, Long sysId,
			Long roleId) {
		String hql = "select distinct fung from FunctionGroup fung "
				+ "join fung.function fun join fun.roleFunctions r_f "
				+ "where r_f.role.id=? and r_f.companyId=? and fun.deleted=? "
				+ "and r_f.deleted=? and fung. deleted=? and fung.businessSystem.id=?";
		return functionGroupDao.find(functionpage, hql, roleId, getCompanyId(),
				false, false, false, sysId);
	}

	public List<Long> getFunctionIds(Long roleId, Long sysId) {
		List<Long> FunctionIds = new ArrayList<Long>();
		List<RoleFunction> role_Functions = role_fDao.findByCriteria(
				Restrictions.eq("role.id", roleId), Restrictions.eq("deleted", false));
		for (RoleFunction role_Function : role_Functions) {
			FunctionIds.add(role_Function.getFunction().getId());
		}
		return FunctionIds;
	}
	
	public List<Function> getFunctions(Long roleId) {
		List<Function> Functions = new ArrayList<Function>();
		List<RoleFunction> role_Functions = role_fDao.findByCriteria(
				Restrictions.eq("role.id", roleId), Restrictions.eq("deleted", false));
		for (RoleFunction role_Function : role_Functions) {
			Functions.add(role_Function.getFunction());
		}
		return Functions;
	}

	public void roleAddFunction(Long roleId,String fIds,Integer isAdd){
		Role role = roleDao.get(roleId);
		String[] function = fIds.split(",");
		if(isAdd.equals(0)){
			RoleFunction role_f = null;
			for (int i=0;i< function.length;i++) {
				role_f = new RoleFunction();
				role_f.setRole(role);
				role_f.setFunction(functionDao.get(Long.parseLong(function[i])));
				role_f.setCompanyId(getCompanyId());
				role_fDao.save(role_f);
			}
			logUtilDao.debugLog("角色管理", role.getName()+"添加资源" ,getSystemIdByCode(ACS));
		}else if(isAdd.equals(1)){
			Long[] fs=new Long[function.length];
			for(int i=0;i<function.length;i++){
				fs[i]=Long.parseLong(function[i]);
			}
			List<RoleFunction> funList = null;
			funList = role_fDao.findByCriteria( Restrictions.in("function.id", fs),Restrictions.eq("role.id", roleId), Restrictions.eq("deleted", false));
			for (RoleFunction role_Function : funList) {
				role_fDao.delete(role_Function);
			}
			logUtilDao.debugLog("角色管理", role.getName()+"删除资源" ,getSystemIdByCode(ACS));
		}
	}

	/**
	 * 角色中添加部门 0 为添加， 1 为移除
	 */
	public void addDepartmentsToRole(Long roleId, List<Long> departmentsIds,
			Integer isAdd) {
		Role role = roleDao.get(roleId);
		RoleDepartment roleDepartment = null;
		if (isAdd == 0) {
			Department department = null;
			for (Long id : departmentsIds) {
				department = new Department();
				department.setId(id);
				roleDepartment = new RoleDepartment();
				roleDepartment.setDepartment(department);
				roleDepartment.setRole(role);
				roleDepartment.setCompanyId(getCompanyId());
				roleDepartmentDao.save(roleDepartment);
			}
		} else if (isAdd == 1) {
			List<RoleDepartment> rds = roleDepartmentDao.findByCriteria(
					Restrictions.eq(ROLE_ID, roleId), Restrictions.in(
							"department.id", departmentsIds));
			for (RoleDepartment rd : rds) {
				rd.setDeleted(true);
				roleDepartmentDao.save(rd);
			}
		}
	}

	/**
	 * 角色中移除部门
	 */
	public void deleteDepartmentsFromRole(Long roleId, List<Long> departmentsIds) {
		List<RoleDepartment> roleDepartments = roleDepartmentDao
				.findByCriteria(Restrictions.eq(ROLE_ID, roleId),
						Restrictions.eq(COMPANYID, getCompanyId()));
		for (RoleDepartment rd : roleDepartments) {
			if (departmentsIds.contains(rd.getDepartment().getId())) {
				roleDepartmentDao.delete(rd.getId());
			}
		}
	}

	public List<Long> getCheckedDepartmentByRole(Long roleId) {
		List<RoleDepartment> roleDepartmentList = roleDepartmentDao
				.findByCriteria(Restrictions.eq(ROLE_ID, roleId),
						Restrictions.eq(COMPANYID, getCompanyId()),
						Restrictions.eq(DELETED, false));
		List<Long> checkDepartments = new ArrayList<Long>();
		for (RoleDepartment roleDepartment : roleDepartmentList) {
			checkDepartments.add(roleDepartment.getDepartment().getId());
		}
		return checkDepartments;
	}

	public void addUsersToRole(Long roleId, List<Long> userIds, Integer isAdd) {
		Set<Long> uIds = new HashSet<Long>();
		uIds.addAll(userIds);
		Role role = roleDao.get(roleId);
		RoleUser roleUser = null;
		if (isAdd == 0) {
			User user = null;
			for (Long id : uIds) {
				user = new User();
				user.setId(id);
				roleUser = new RoleUser();
				roleUser.setRole(role);
				roleUser.setUser(user);
				roleUser.setCompanyId(getCompanyId());
				roleUserDao.save(roleUser);
			}
		} else if (isAdd == 1) {
			List<RoleUser> roleUsers = roleUserDao.findByCriteria(Restrictions
					.in("user.id", uIds), Restrictions.eq(ROLE_ID, roleId),
					Restrictions.eq(COMPANYID, getCompanyId()));
			for (RoleUser ru : roleUsers) {
				ru.setDeleted(true);
				roleUserDao.save(ru);
			}
		}
	}

	/**
	 * 获取具有该角色的用户Id
	 */
	public List<Long> getCheckedUserByRole(Long roleId) {
		List<RoleUser> roleUserList = roleUserDao.findByCriteria(Restrictions
				.eq(ROLE_ID, roleId), Restrictions.eq(COMPANYID,
				getCompanyId()), Restrictions.eq(DELETED, false));
		List<Long> checkUsers = new ArrayList<Long>();
		for (RoleUser roleUser : roleUserList) {
			checkUsers.add(roleUser.getUser().getId());
		}
		return checkUsers;
	}
	
	/**
	 * 获取具有该角色的用户
	 */
	@SuppressWarnings("unchecked")
	public List<User> getCheckedUsersByRole(Long roleId) {
		if(standardRoleManager.hasAllUserByRole(roleId)){
			return acsUtils.getUsersByCompany(getCompanyId());
			
		}else{
			return userDao.find(
					"select distinct u from User u join u.roleUsers ru join ru.role r where u.deleted=? and ru.deleted=? and ru.consigner is null and r.deleted=? and r.id=? and u.companyId=?  order by u.weight desc", 
					false, false, false, roleId, getCompanyId());
		}
	}
	
	/**
	 * 获取具有该角色的用户(不包含通过角色和所有用户的关系得到的用户)
	 */
	@SuppressWarnings("unchecked")
	public List<User> getCheckedUsersByRoleOld(Long roleId) {
		return userDao.find(
				"select distinct u from User u join u.roleUsers ru join ru.role r where u.deleted=? and ru.deleted=? and ru.consigner is null and r.deleted=? and r.id=? and u.companyId=?  order by u.weight desc", 
				false, false, false, roleId, getCompanyId());
	}
	
	
	/**
	 * 获取具有该角色的工作组
	 */
	public List<Workgroup> getCheckedWorkgroupByRole(Long roleId) {
		List<RoleWorkgroup> roleWorkgroupList = role_wDao.findList("from RoleWorkgroup rw where rw.deleted=? and rw.companyId=? and rw.allGroup is null and rw.role.id=?",false,ContextUtils.getCompanyId(),roleId);
		List<Workgroup> checkUsers = new ArrayList<Workgroup>();
		for (RoleWorkgroup rw : roleWorkgroupList) {
			if(rw.getWorkgroup().isDeleted()) continue;
			checkUsers.add(rw.getWorkgroup());
		}
		return checkUsers;
	}

	public Page<Workgroup> roleToWorkGroupList(Page<Workgroup> page,
			Workgroup entity) {
		if (entity != null) {
			String workGroupCode = entity.getCode();
			String workGroupName = entity.getName();

			if (workGroupCode != null && !"".equals(workGroupCode)
					&& workGroupName != null && !"".equals(workGroupName)) {
				return workGroupDao.findByCriteria(page, Restrictions.like(
						"code", "%" + workGroupCode + "%"),
						Restrictions.like("name", "%" + workGroupName
								+ "%"), Restrictions.eq(DELETED, false),
						Restrictions.eq(COMPANY_ID, getCompanyId()));
			}
			if (workGroupCode != null && !"".equals(workGroupCode)) {
				return workGroupDao.findByCriteria(page, Restrictions.like(
						"code", "%" + workGroupCode + "%"),
						Restrictions.eq(DELETED, false), Restrictions.eq(
								COMPANY_ID, getCompanyId()));
			}
			if (workGroupName != null && !"".equals(workGroupName)) {
				return workGroupDao.findByCriteria(page, Restrictions.like(
						"name", "%" + workGroupName + "%"),
						Restrictions.eq(DELETED, false), Restrictions.eq(
								COMPANY_ID, getCompanyId()));
			}
		}
		return workGroupDao.findByCriteria(page, Restrictions.eq(DELETED,
				false), Restrictions.eq(COMPANY_ID, getCompanyId()));

	}

	public Page<Workgroup> roleRomoveWorkGroupList(Page<Workgroup> page,
			Workgroup entity, Long roleId) {
		String hql = "select work from WorkGroup work join work.roleWorkgroups r_w where r_w.role.id=? and r_w.companyId=? and work.deleted=? and r_w.deleted=? ";
		if (entity != null) {
			String workGroupCode = entity.getCode();
			String workGroupName = entity.getName();
			StringBuilder hqL = new StringBuilder(hql);
			if (workGroupCode != null && !"".equals(workGroupCode)
					&& workGroupName != null && !"".equals(workGroupName)) {
				hqL.append(" and work.code like ? ");
				hqL.append(" and work.name like ? ");
				return workGroupDao.find(page, hql, roleId, getCompanyId(),
						false, false, "%" + workGroupCode + "%", "%"
								+ workGroupName + "%");
			}
			if (workGroupCode != null && !"".equals(workGroupCode)) {
				hqL.append(" and work.code like ? ");
				return workGroupDao.find(page, hql, roleId, getCompanyId(),
						false, false, "%" + workGroupCode + "%");
			}
			if (workGroupName != null && !"".equals(workGroupName)) {
				hqL.append(" and work.name like ? ");
				return workGroupDao.find(page, hql, roleId, getCompanyId(),
						false, false, "%" + workGroupName + "%");
			}
		}
		return workGroupDao.find(page, hql, roleId, getCompanyId(), false, false);

	}

	public List<Long> getWorkGroupIds(Long roleId) {
		List<Long> workGroupIds = new ArrayList<Long>();
		List<RoleWorkgroup> role_WorkGroups = role_wDao.findByCriteria(
				Restrictions.eq(ROLE_ID, roleId), Restrictions.eq(DELETED,
						false), Restrictions.eq(COMPANYID, getCompanyId()));
		for (RoleWorkgroup role_WorkGroup : role_WorkGroups) {
			workGroupIds.add(role_WorkGroup.getWorkgroup().getId());
		}
		return workGroupIds;
	}

	public void roleAddWorkGroup(Long roleId, List<Long> workGroupIds,
			Integer isAdd) {
		Role role = getRole(roleId);
		if (isAdd == 0) {
			RoleWorkgroup role_WorkGroup;
			for (Long workId : workGroupIds) {
				role_WorkGroup = new RoleWorkgroup();
				role_WorkGroup.setRole(role);
				role_WorkGroup.setWorkgroup(workGroupDao.get(workId));
				role_WorkGroup.setCompanyId(getCompanyId());
				role_wDao.save(role_WorkGroup);
			}
		}
		if (isAdd == 1) {
			List<RoleWorkgroup> role_WorkGroups = role_wDao.findByCriteria(
					Restrictions.in("workgroup.id", workGroupIds), Restrictions
							.eq(ROLE_ID, roleId), Restrictions.eq(
							COMPANYID, getCompanyId()), Restrictions.eq(
							DELETED, false));
			for (RoleWorkgroup role_WorkGroup : role_WorkGroups) {
				role_WorkGroup.setDeleted(true);
				role_wDao.save(role_WorkGroup);
			}
		}

	}

	public Page<Role> getRolesByRoleGroup(Page<Role> page, Long roleGroupId) {
		return roleDao.findByCriteria(page, Restrictions.eq("roleGroup.id", roleGroupId));
	}

	/**
	 * 查询公司里面的所有的角色(含标准角色和自定义角色)
	 */
	public List<Role> getAllRolesByCompany() {
		List<Role> roles = roleDao.findAll();
		List<Role> customRoles = roleDao.findByCriteria(Restrictions.eq(COMPANY_ID, getCompanyId()));
		roles.addAll(customRoles);
		return roles;
	}

	public SimpleHibernateTemplate<Role, Long> getRoleDao() {
		return roleDao;
	}

	public SimpleHibernateTemplate<RoleFunction, Long> getRole_fDao() {
		return role_fDao;
	}

	public SimpleHibernateTemplate<RoleWorkgroup, Long> getRole_wDao() {
		return role_wDao;
	}

	public SimpleHibernateTemplate<Workgroup, Long> getWorkGroupDao() {
		return workGroupDao;
	}

	public SimpleHibernateTemplate<Function, Long> getFunctionDao() {
		return functionDao;
	}

	public SimpleHibernateTemplate<FunctionGroup, Long> getFunctionGroupDao() {
		return functionGroupDao;
	}
	
	/**
	 * 角色中移除用户、部门、工作组
	 * @param roleId
	 * @param uIds
	 * @param dIds
	 * @param wIds
	 */
	public void removeUDWFromRoel(Long roleId, List<Long> uIds, List<Long> dIds, List<Long> wIds){
		Role role=getRole(roleId);
		//移除用户
		boolean isFirst = true;
		String roleName = null;
		StringBuilder logMsg = null;
		if(uIds != null && uIds.size() > 0){
			List<RoleUser> roleUsers = roleUserDao.findByCriteria(Restrictions
					.in("user.id", uIds), Restrictions.eq(ROLE_ID, roleId),
					Restrictions.eq(COMPANYID, getCompanyId()),Restrictions.isNull("consigner"));
			logMsg = new StringBuilder();
			for (RoleUser ru : roleUsers) {
				if(ru.isDeleted())continue;
				ru.setDeleted(true);
				roleUserDao.save(ru);
				if(!isFirst){ logMsg.append(",");}
				if(isFirst){ roleName = ru.getRole().getName(); isFirst = false; }
				logMsg.append(ru.getUser().getName());
			}
			isFirst = true;
			if(logMsg.length() != 0)
				logUtilDao.debugLog("授权管理", role.getName()
					+ "移除用户：" + logMsg.toString() + "}",getSystemIdByCode(ACS));
		}
		//移除部门
		if(dIds != null && dIds.size() > 0){
			List<RoleDepartment> rds = roleDepartmentDao.findByCriteria(
					Restrictions.eq(ROLE_ID, roleId), Restrictions.in("department.id", dIds));
			logMsg = new StringBuilder();
			for (RoleDepartment rd : rds) {
				if(rd.isDeleted())continue;
				rd.setDeleted(true);
				roleDepartmentDao.save(rd);
				
				if(!isFirst){ logMsg.append(",");}
				if(isFirst){ roleName = rd.getRole().getName(); isFirst = false;}
				logMsg.append(rd.getDepartment().getName());
			}
			isFirst = true;
			if(logMsg.length() != 0)
				logUtilDao.debugLog("授权管理", role.getName()
					+ "移除部门：" + logMsg.toString() + "}",getSystemIdByCode(ACS));
		}
		//移除工作组
		if(wIds != null && wIds.size() > 0){
			List<RoleWorkgroup> role_WorkGroups = role_wDao.findByCriteria(
					Restrictions.in("workgroup.id", wIds), Restrictions
							.eq(ROLE_ID, roleId), Restrictions.eq(
							COMPANYID, getCompanyId()), Restrictions.eq(
							DELETED, false));
			logMsg = new StringBuilder();
			for (RoleWorkgroup role_WorkGroup : role_WorkGroups) {
				if(role_WorkGroup.isDeleted())continue;
				role_WorkGroup.setDeleted(true);
				role_wDao.save(role_WorkGroup);
				
				if(!isFirst){ logMsg.append(","); }
				if(isFirst){ roleName = role_WorkGroup.getRole().getName(); isFirst = false;}
				logMsg.append(role_WorkGroup.getWorkgroup().getName());
			}
			if(logMsg.length() != 0)
				logUtilDao.debugLog("授权管理", role.getName()
					+ "移除工作组：" + logMsg.toString() + "}",getSystemIdByCode(ACS));
		}
		removeAll(roleId,uIds,dIds,wIds);
	}

	private void removeAll(Long roleId, List<Long> uIds, List<Long> dIds,
			List<Long> wIds) {
		if(uIds!=null){
			for(Long uId:uIds){
				if(uId==-1){
					roleUserDao.createQuery("delete from RoleUser ru where ru.role.id=? and (ru.companyId=? or ru.companyId is null) and ru.allUser=?", roleId,ContextUtils.getCompanyId(),"ALL_USER").executeUpdate();
				}
			}
		}
		if(dIds!=null){
			for(Long dId:dIds){
				if(dId==-1){
					roleDepartmentDao.createQuery("delete from RoleDepartment rd where rd.role.id=? and (rd.companyId=? or rd.companyId is null) and rd.allDept=?", roleId,ContextUtils.getCompanyId(),"ALL_DEPARTMENT").executeUpdate();
				}
			}
		}
		if(wIds!=null){
			for(Long wId:wIds){
				if(wId==-1){
					roleUserDao.createQuery("delete from RoleWorkgroup rw where rw.role.id=? and (rw.companyId=? or rw.companyId is null) and rw.allGroup=?", roleId,ContextUtils.getCompanyId(),"ALL_WORKGROUP").executeUpdate();
				}
			}
		}
	}
	public String addUDWFromRoel(Role role, List<Long> userIds,
			List<Long> departmentsIds, List<Long> workGroupIds,String allInfos) {
		StringBuilder result=new StringBuilder();
		//===============  添加用户  ===================
		result.append(roleAddUsers(role, userIds, departmentsIds, workGroupIds, allInfos));
		
		//===============  添加部门  ===================
		roleAddDepartments(role, userIds, departmentsIds, workGroupIds, allInfos);
	
		//===============  添加工作组  ===================
		roleAddWorkgroups(role, userIds, departmentsIds, workGroupIds, allInfos);
		if(StringUtils.isEmpty(result.toString())){
			result.append(Struts2Utils.getText("common.saved"));
		}
		return result.toString();
	}
	
	public String roleAddUsers(Role role, List<Long> userIds,
			List<Long> departmentsIds, List<Long> workGroupIds,String allInfos){
		StringBuilder result=new StringBuilder();
		StringBuilder logMsg = new StringBuilder();
		StringBuilder logErrMsg = new StringBuilder();
		StringBuilder logIsDefaultAdminMsg = new StringBuilder();
		StringBuilder haveRoleMsg = new StringBuilder();
		boolean isFirst = true;
		RoleUser roleUser = null;
		User user = null;
		if("user_allDepartment".equals(allInfos) || "user_company".equals(allInfos) ||"user_usersNotIndept".equals(allInfos) ){
			List<User> users =null ;
			boolean noDeptUser = false;
			if("user_allDepartment".equals(allInfos) || "user_company".equals(allInfos)){
				String authoritySetting = PropUtils.getProp("applicationContent.properties", "authority.setting.alone");
//				if(StringUtils.isEmpty(authoritySetting)||authoritySetting.equals("true")){//设置了三员独立
//					users= queryAllUserWithoutAdmin();
//				}else{
					users= queryAllUser();
					
//				}
				
				logMsg.append("所有用户");
			}else{
				users = getUsersWithoutDepartment();
				noDeptUser = true;
			}
			for(User us:users){
				userIds.add(us.getId());
				if(noDeptUser){
					if(!isFirst) logMsg.append(",");
					logMsg.append(us.getName());
					isFirst = false;
				}
			}
		}
		boolean needMsg = (logMsg.length() == 0);
		for (Long id : userIds) {
			boolean flag=checkRoleUser(id,role.getId());
			User myuser=userDao.get(id);
			if(!flag){
				String authoritySetting = PropUtils.getProp("applicationContent.properties", "authority.setting.alone");
				boolean sign=false;
				if(StringUtils.isEmpty(authoritySetting)||authoritySetting.equals("true")){//设置了三员独立
					sign=!hasAdminRole(role,myuser);
				}else{
					sign=true;
				}
				if(sign){
					user = new User();
					user.setId(id);
					roleUser = new RoleUser();
					roleUser.setRole(role);
					roleUser.setUser(user);
					roleUser.setCompanyId(getCompanyId());
					roleUserDao.save(roleUser);
					
					if(needMsg){
						if(!isFirst){ logMsg.append(","); }
						logMsg.append(userDao.get(id).getName());
						isFirst = false;
					}
				}else{
					logErrMsg.append(myuser.getName()).append(",");
				}
			}else{
				haveRoleMsg.append(myuser.getName()).append(",");
			}
		}
		boolean logSign=true;//该字段只是为了标识日志信息
		if(StringUtils.isNotEmpty(logErrMsg.toString())){
			result.append(logErrMsg.substring(0, logErrMsg.lastIndexOf(","))).append(Struts2Utils.getText("roleAddUserAdminInfo"));
			logMsg.append(logErrMsg.substring(0, logErrMsg.lastIndexOf(","))).append(ApiFactory.getBussinessLogService().getI18nLogInfo("roleAddUserAdminInfo"));
			logSign=false;
		}
		if(StringUtils.isNotEmpty(haveRoleMsg.toString())){
			result.append(haveRoleMsg.substring(0, haveRoleMsg.lastIndexOf(","))).append(" ").append(Struts2Utils.getText("roleAddUserInfo", new String[]{role.getName()}));
			List<String> messages = new ArrayList<String>();
			messages.add(role.getName());
			logMsg.append(haveRoleMsg.substring(0, haveRoleMsg.lastIndexOf(","))).append(" ").append(ApiFactory.getBussinessLogService().getI18nLogInfo("roleAddUserInfo",messages));
			logSign=false;
		}
		if(logSign && logMsg.length() != 0)
			logUtilDao.debugLog("授权管理", role.getName()
				+ "添加用户：" + logMsg.toString() + "}",getSystemIdByCode(ACS));
		return result.toString();
	}
	
	private List<User> getUsersWithoutDepartment(){
		StringBuilder sqlString = new StringBuilder();
		sqlString.append("SELECT u.* FROM ACS_USERINFO ");
		sqlString.append("inner join ACS_USER u on ACS_USERINFO.FK_USER_ID=u.id ");
		sqlString.append("LEFT OUTER JOIN ");
		sqlString.append("(SELECT * FROM ACS_DEPARTMENT_USER WHERE ACS_DEPARTMENT_USER.DELETED = 0)");
		sqlString.append(" DEPT_USER ON ACS_USERINFO.FK_USER_ID = DEPT_USER.FK_USER_ID ");
		sqlString.append("WHERE ACS_USERINFO.DELETED=0 AND ACS_USERINFO.FK_COMPANY_ID = ? ");
		sqlString.append("AND DEPT_USER.ID IS NULL ORDER BY ACS_USERINFO.ID DESC ");
		
		return userDao.findByJdbc(sqlString.toString(), ContextUtils.getCompanyId());
	}

	private boolean hasAdminRole(Role role,User user){
		String authoritySetting = PropUtils.getProp("applicationContent.properties", "authority.setting.alone");
		if(StringUtils.isEmpty(authoritySetting)||authoritySetting.equals("true")){//设置了三员独立
			String userRoles=getRoleUsersIncludeTrustedRole(user);
			if(ACS_SYSTEM_ADMIN.equals(role.getCode())||ACS_AUDIT_ADMIN.equals(role.getCode())||ACS_SECURITY_ADMIN.equals(role.getCode())){
				//如果是系统管理员
				if(userRoles.contains(ACS_SYSTEM_ADMIN)){
					return true;
				}
				//如果是安全管理员
				if(userRoles.contains(ACS_SECURITY_ADMIN)){
					return true;
				}
				//如果是审计管理员
				if(userRoles.contains(ACS_AUDIT_ADMIN)){
					return true;
				}
			}
			return false;
		}else{
			return false;
		}
	}
	
	//判断用户是否具有系统管理员
	public boolean hasSystemAdminRole(Long userId){
		return  hasSystemAdminRole(userDao.get(userId));
	}
	//判断用户是否具有三员的角色
	public boolean hasAdminRole(Long userId){
		String userRoles=getRoleUsersIncludeTrustedRole(userDao.get(userId));
		//如果是系统管理员
		if(userRoles.contains(ACS_SYSTEM_ADMIN)){
			return true;
		}
		
		if(userRoles.contains(ACS_SECURITY_ADMIN)){
			return true;
		}
		
		if(userRoles.contains(ACS_AUDIT_ADMIN)){
			return true;
		}
		return false;
	}
	/*
	 * 获得用户的所有角色，包括委托的权限
	 */
	public String getRoleUsersIncludeTrustedRole(User user){
		if(user == null) return "";
		List<com.norteksoft.acs.entity.authorization.Role> roleList = new ArrayList<com.norteksoft.acs.entity.authorization.Role>();
		roleList.addAll(acsUtils.getRolesByUserIncludeConsigner(ContextUtils.getCompanyId(), user.getId()));
		//角色按权重排序
		sortRole(roleList);
		// 生成字符串形式
		StringBuilder roleStrings = new StringBuilder();
		for(com.norteksoft.acs.entity.authorization.Role role : roleList){
			roleStrings.append(role.getCode()).append(",");
		}
		// 去掉最后一个逗号
		if(roleStrings.lastIndexOf(",") != -1 && roleStrings.lastIndexOf(",") == roleStrings.length()-1){
			roleStrings.replace(roleStrings.length()-1, roleStrings.length(), "");
		}
		return roleStrings.toString();
	}
	
	
	//角色按权重排序
	private void sortRole(List<Role> roles){
		Collections.sort(roles, new Comparator<Role>() {
			public int compare(Role role1, Role role2) {
				if(role1.getWeight()==null&&role2.getWeight()!=null)return 1;
				if(role1.getWeight()!=null&&role2.getWeight()==null)return 0;
				if(role1.getWeight()==null&&role2.getWeight()==null)return 0;
				if(role1.getWeight()<role2.getWeight()){
					return 1;
				}
				return 0;
			}
		});
	}
    
	private boolean isNotDefaultAdmin(User user){
		Company company = companyManager.getCompany(user.getCompanyId());
		String systemAdmin = company.getCode()+".systemAdmin";
		String securityAdmin = company.getCode()+".securityAdmin";
		String auditAdmin = company.getCode()+".auditAdmin";
		if(user.getLoginName().equals(systemAdmin)
				||user.getLoginName().equals(securityAdmin)
				     ||user.getLoginName().equals(auditAdmin)){
			return false;
		}
		return true;
	}
	private void roleAddDepartments(Role role, List<Long> userIds,
			List<Long> departmentsIds, List<Long> workGroupIds,String allInfos){
		StringBuilder logMsg = new StringBuilder();
		boolean isFirst = true;
		RoleDepartment roleDepartment = null;
		Department department = null;
		if("department_allDepartment".equals(allInfos) || "department_company".equals(allInfos)){
			List<Department> depts = queryAllDepts();
			for(Department dept:depts){
				departmentsIds.add(dept.getId());
				if(!isFirst){ logMsg.append(","); }
				logMsg.append(dept.getName());
				isFirst = false;
			}
		}
		boolean needMsg = (logMsg.length() == 0);
		for (Long id : departmentsIds) {
			boolean flag=checkRoleDepartment(id,role.getId());
			if(!flag){
				department = new Department();
				department.setId(id);
				roleDepartment = new RoleDepartment();
				roleDepartment.setDepartment(department);
				roleDepartment.setRole(role);
				roleDepartment.setCompanyId(getCompanyId());
				roleDepartmentDao.save(roleDepartment);
				
				 if(needMsg){
					if(!isFirst){ logMsg.append(","); }
					logMsg.append(departmentDao.get(id).getName());
					isFirst = false;
				}
			}
		}
		if(logMsg.length() != 0)
			logUtilDao.debugLog("授权管理", role.getName()
				+ "添加部门：" + logMsg.toString() + "}",getSystemIdByCode(ACS));
	}
	
	private void roleAddWorkgroups(Role role, List<Long> userIds,
			List<Long> departmentsIds, List<Long> workGroupIds,String allInfos){
		StringBuilder logMsg = new StringBuilder();
		boolean isFirst = true;
		RoleWorkgroup role_WorkGroup = null;
		Workgroup wg = null;
		if("workGroup_allWorkGroup".equals(allInfos) || "workGroup_company".equals(allInfos)){
			List<Workgroup> workGroups = queryAllWorkGroups();
			for(Workgroup workGroup:workGroups){
				workGroupIds.add(workGroup.getId());
				if(isFirst){ logMsg.append(","); isFirst = false;}
				logMsg.append(workGroup.getName());
			}
		}
		boolean needMsg = (logMsg.length() == 0);
		for (Long workId : workGroupIds) {
			boolean flag=checkRoleWokGroup(workId,role.getId());
			if(!flag){
				wg = new Workgroup();
				wg.setId(workId);
				role_WorkGroup = new RoleWorkgroup();
				role_WorkGroup.setRole(role);
				role_WorkGroup.setWorkgroup(wg);
				role_WorkGroup.setCompanyId(getCompanyId());
				role_wDao.save(role_WorkGroup);
				
				if(needMsg){
					if(!isFirst){ logMsg.append(",");}
					logMsg.append(workGroupDao.get(workId).getName());
					isFirst = false;
				}
			}
		}
		if(logMsg.length() != 0)
			logUtilDao.debugLog("授权管理", role.getName()
				+ "添加工作组：" + logMsg.toString() + "}",getSystemIdByCode(ACS));
	}
	
	/**
	 * 将角色分配给其他人
	 * @param anthorId
	 * @param roleIds
	 * @param companyId
	 */
	
	public void assignRolesToSomeone(Long someoneId,String[] roleIds,Long companyId,Long sourceUserId){
		deleteAssignedAuthority(sourceUserId,someoneId,companyId);
		for(int i=0;i<roleIds.length;i++){
			if(!StringUtils.isEmpty(roleIds[i])){
				if(roleIds[i].endsWith("/")){
					roleIds[i] = roleIds[i].replace("/", "");
				}
				if((getRoleUserByRelation(someoneId, Long.parseLong(roleIds[i]), companyId))==null){
					RoleUser roleUser = new RoleUser();
					Role role = roleDao.get(Long.parseLong(roleIds[i]));
					User user = userDao.get(someoneId);
					roleUser.setRole(role);
					roleUser.setUser(user);
					roleUser.setCompanyId(companyId);
					roleUser.setConsigner(sourceUserId);
					roleUserDao.save(roleUser);
				}
			}
		}
	}
	/**
	 * 按条件获取角色用户表数据
	 * @param userId
	 * @param roleId
	 * @param companyId
	 */
	public RoleUser getRoleUserByRelation(Long userId,Long roleId,Long companyId){
		String hql = "FROM RoleUser ru WHERE ru.role.id=? AND ru.user.id=? AND ru.companyId=?";
		return (RoleUser)roleUserDao.findUnique(hql, roleId,userId,companyId);
	}
	/**
	 * 删除由别人分配的权限
	 * @param sourceId
	 * @param userId
	 * @param companyId
	 */
	@SuppressWarnings("unchecked")
	public void deleteAssignedAuthority(Long sourceId,Long userId,Long companyId){
		String hql = "FROM RoleUser ru WHERE ru.consigner=? AND ru.user.id=? AND ru.companyId=?";
		List<RoleUser> roleUsers = roleUserDao.find(hql, sourceId,userId,companyId);
		for(RoleUser ru:roleUsers){
			roleUserDao.delete(ru);
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<User> queryUserByTrueName(String name){
		 return userDao.find("select user from User user join user.userInfos ui where user.loginName = ? and user.deleted = false and dr=0 and user.companyId=?", name,getCompanyId());
	}
	
	@SuppressWarnings("unchecked")
	public List<User> queryAllUserWithoutAdmin(){
		 return userDao.find("from User user where user.companyId=? and user.deleted = false and (user.loginName not like ? and user.loginName not like ? and user.loginName not like ?)",getCompanyId(),"%.systemAdmin%","%.securityAdmin%","%.auditAdmin%");
	}
	
	@SuppressWarnings("unchecked")
	public List<User> queryAllUser(){
		return userDao.find("from User user where user.companyId=? and user.deleted = false ",getCompanyId());
	}
	
	@SuppressWarnings("unchecked")
	public List<Department> queryAllDepts(){
		 return departmentDao.find("FROM Department d WHERE d.company.id=? AND d.deleted=?", getCompanyId(), false);
	}
	
	public List<Workgroup> queryAllWorkGroups(){
		 return workGroupDao.findByCriteria(Restrictions.eq("company.id", getCompanyId()),Restrictions.eq(DELETED,false));
	}
	
	
	@SuppressWarnings("unchecked")
	public List<Role> queryRolesByUserName(Long userId, Long sysId){
		String hql = "select role from Role role join role.roleUsers ru join ru.user user where user.id = ? and role.businessSystem.id= ? " +
				"and role.deleted = false and ru.deleted = false and user.deleted = false";
		return roleDao.find(hql, userId, sysId);
		
	}
	
	@SuppressWarnings("unchecked")
	public List<Role> queryRolesByDepartmentName(String name, Long sysId){
		String hql = "select role from Role role join role.roleDepartments rd join rd.department dept where dept.name = ? and role.businessSystem.id= ? " +
				"and role.deleted = false and rd.deleted = false and dept.deleted = false and dept.company.id=?";
		return roleDao.find(hql, name, sysId,ContextUtils.getCompanyId());
	}
	
	@SuppressWarnings("unchecked")
	public List<Role> queryRolesByWorkgroupName(String name, Long sysId){
		String hql = "select role from Role role join role.roleWorkgroups rw join rw.workgroup wg where wg.name = ? and role.businessSystem.id= ? " +
				"and role.deleted = false and rw.deleted = false and wg.deleted = false and wg.company.id=?";
		return roleDao.find(hql, name, sysId,ContextUtils.getCompanyId());
	}
	
	/**
	 * 确定该用户是否已经具有该权限
	 * @param userId:用户id
	 * @param roleId:角色id
	 * @return true:已经有 false:没有该权限
	 */
	@SuppressWarnings("unchecked")
	public boolean checkRoleUser(Long userId, Long roleId){
		String hql = "from RoleUser ru where ru.user.id = ? and ru.role.id= ? and ru.companyId=? and ru.deleted = false";
		List<RoleUser> rus = roleUserDao.find(hql, userId, roleId,getCompanyId());
		if(rus.size()>0){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 确定该部门是否已经具有该权限
	 * @param deptId:部门id
	 * @param roleId:角色id
	 * @return true:已经有 false:没有该权限
	 */
	@SuppressWarnings("unchecked")
	public boolean checkRoleDepartment(Long deptId, Long roleId){
		String hql = "from RoleDepartment rd where rd.department.id = ? and rd.role.id= ? and rd.companyId=? and rd.deleted = false";
		List<RoleUser> rus = roleUserDao.find(hql, deptId, roleId,getCompanyId());
		if(rus.size()>0){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 确定该工作组是否已经具有该权限
	 * @param workGroupId:工作组id
	 * @param roleId:角色id
	 * @return true:已经有 false:没有该权限
	 */
	@SuppressWarnings("unchecked")
	public boolean checkRoleWokGroup(Long workGroupId, Long roleId){
		String hql = "from RoleWorkgroup rw where rw.workgroup.id = ? and rw.role.id= ? and rw.companyId=? and rw.deleted = false";
		List<RoleUser> rus = roleUserDao.find(hql, workGroupId, roleId,getCompanyId());
		if(rus.size()>0){
			return true;
		}else{
			return false;
		}
	}
	
	public boolean hasSystemAdminRole(User user){
		String userRoles=getRoleUsersIncludeTrustedRole(user);
		//如果是系统管理员
		if(userRoles.contains(ACS_SYSTEM_ADMIN)){
			return true;
		}
		return false;
	}
	public boolean hasSecurityAdminRole(User user){
		String userRoles=getRoleUsersIncludeTrustedRole(user);
		//如果是安全管理员
		if(userRoles.contains(ACS_SECURITY_ADMIN)){
			return true;
		}
		return false;
	}
	public boolean hasSecurityAdminRole(Long userId){
		User user = userDao.get(userId);
		return hasSecurityAdminRole(user);
	}
	public boolean hasBranchAdminRole(User user){
		String userRoles=getRoleUsersIncludeTrustedRole(user);
		//如果是分支机构管理员
		if(userRoles.contains(ACS_BRANCH_ADMIN)){
			return true;
		}
		return false;
	}
	public boolean hasBranchAdminRole(Long userId){
		User user = userDao.get(userId);
		return hasBranchAdminRole(user);
	}
	
	public boolean hasAuditAdminRole(User user) {
		String userRoles=getRoleUsersIncludeTrustedRole(user);
		//如果是审计管理员
		if(userRoles.contains(ACS_AUDIT_ADMIN)){
			return true;
		}
		return false;
	}
	
	public boolean hasAuditAdminRole(Long userId) {
		User user = userDao.get(userId);
		return hasAuditAdminRole(user);
	}

	
	/**
	 * 获得角色编码为默认编码的所有角色
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Role> getDefaultCodeRoles() {
		String hql = "from Role role where (role.companyId is null or role.companyId=?) and role.code like 'role-%' and role.deleted=? " ;
		return roleDao.find(hql, ContextUtils.getCompanyId(),false);
	}
	
	@SuppressWarnings("unchecked")
	public List<Role> getRoles(Long businessSystemId, Long subCompanyId,
			String roleName) {
		StringBuilder sql=new StringBuilder();
		sql.append("select role from Role role join role.businessSystem bs ");
		sql.append("where bs.id = ? and (role.companyId is null or role.companyId=?) ");
		sql.append("and role.name=? and role.deleted=?");
		if(subCompanyId==null){
			sql.append("and role.subCompanyId is null ");
			return roleDao.find(sql.toString(),businessSystemId, ContextUtils.getCompanyId(),roleName,false);
		}else{
			sql.append("and role.subCompanyId = ? ");
			return roleDao.find(sql.toString(),businessSystemId, ContextUtils.getCompanyId(),roleName,false,subCompanyId);
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<Role> getRoles(String code) {
		StringBuilder hql=new StringBuilder();
		hql.append("from Role role where (role.companyId is null or role.companyId=?) and role.code=? and role.deleted=? ");
		return roleDao.find(hql.toString(), ContextUtils.getCompanyId(),code,false);
	}
	
	@SuppressWarnings("unchecked")
	public List<Role> getRoleByBranches(Long branchesId) {
		StringBuilder hql=new StringBuilder();
		hql.append("from Role role where (role.companyId is null or role.companyId=?) ");
		hql.append(" and role.subCompanyId = ? and role.deleted=? ");
		return roleDao.find(hql.toString(), ContextUtils.getCompanyId(),branchesId,false);
	}
	/**
	 * 获得系统下的当前分支管理员所管理的角色列表
	 * @param page
	 * @param businessSystemId
	 * @param branchesSet
	 * @return
	 */
	public Page<Role> getRoles(Page<Role> page, Long businessSystemId,Set<Long> branchesSet) {
		StringBuilder sql=new StringBuilder();
		BusinessSystem bs=businessSystemDao.get(businessSystemId);
		String code=bs.getCode()+"_Everyone";
		sql.append("select distinct role from Role role join role.businessSystem bs   ");
		sql.append("where bs.id = ? and role.deleted=? and bs.deleted=? ");
		sql.append("and (role.companyId is null or role.companyId=?) ");
		sql.append("and (role.id in (");
		sql.append("select r.dataId from BranchAuthority r ");
		sql.append(" where r.companyId=? and r.branchDataType=? and  ");
		sql.append("(r.branchesId in (");
		sql.append("select b.branchesId from BranchAuthority b ");
		sql.append(" where b.companyId=? and b.branchDataType=? and b.dataId=?))) ");
		for(Long branchesId:branchesSet){
			sql.append("or role.subCompanyId =");
			sql.append(branchesId+" ");
		}
		sql.append(")  and role.code<>? ");
		sql.append("order by role.weight desc ");
		
		return roleDao.searchPageByHql(page,sql.toString(),businessSystemId, false,false,ContextUtils.getCompanyId(),ContextUtils.getCompanyId(),BranchDataType.ROLE,ContextUtils.getCompanyId(),BranchDataType.USER,ContextUtils.getUserId(),code);
	}
	@SuppressWarnings("unchecked")
	public List<Role> getRoles(Long businessSystemId,Set<Long> branchesSet) {
		StringBuilder sql=new StringBuilder();
		sql.append("select distinct role from Role role join role.businessSystem bs   ");
		sql.append("where bs.id = ? and role.deleted=? ");
		sql.append("and (role.companyId is null or role.companyId=?) ");
		sql.append("and (role.id in (");
		sql.append("select r.dataId from BranchAuthority r ");
		sql.append(" where r.companyId=? and r.branchDataType=? and  ");
		sql.append("(r.branchesId in (");
		sql.append("select b.branchesId from BranchAuthority b ");
		sql.append(" where b.companyId=? and b.branchDataType=? and b.dataId=?))) ");
		for(Long branchesId:branchesSet){
			sql.append("or role.subCompanyId =");
			sql.append(branchesId+" ");
		}
		sql.append(") ");
		sql.append("order by role.weight desc ");
		
		return roleDao.find(sql.toString(),businessSystemId, false,ContextUtils.getCompanyId(),ContextUtils.getCompanyId(),BranchDataType.ROLE,ContextUtils.getCompanyId(),BranchDataType.USER,ContextUtils.getUserId());
	}
	
	@SuppressWarnings("unchecked")
	public List<Role> getRoleList(Long businessSystemId,Set<Long> branchesSet) {
		int resultLength=8;
		if(branchesSet!=null){
			resultLength+=branchesSet.size();
		}
		Object[] result =new Object[resultLength];
		
		StringBuilder sql=new StringBuilder();
		sql.append("select distinct role from Role role join role.businessSystem bs   ");
		sql.append("where bs.id = ? and role.deleted=? ");
		sql.append("and (role.companyId is null or role.companyId=?) ");
		sql.append("and (role.id in (");
		sql.append("select r.dataId from BranchAuthority r ");
		sql.append(" where r.companyId=? and r.branchDataType=? and  ");
		sql.append("(r.branchesId in (");
		sql.append("select b.branchesId from BranchAuthority b ");
		sql.append(" where b.companyId=? and b.branchDataType=? and b.dataId=?))) ");
		for(Long branchesId:branchesSet){
			sql.append(" or (role.id in (select ba.dataId from BranchAuthority ba  where  ba.companyId=");
			sql.append(ContextUtils.getCompanyId());
			sql.append(" and ba.branchesId=");
			sql.append(branchesId);
			sql.append(" and ba.branchDataType=?");
			sql.append(")) ");
			
		}
		for(Long branchesId:branchesSet){
			sql.append("or role.subCompanyId =");
			sql.append(branchesId+" ");
		}
		sql.append(") ");
		sql.append("order by role.weight desc ");
		result[0]=businessSystemId;
		result[1]=false;
		result[2]=ContextUtils.getCompanyId();
		result[3]=ContextUtils.getCompanyId();
		result[4]=BranchDataType.ROLE;
		result[5]=ContextUtils.getCompanyId();
		result[6]=BranchDataType.USER;
		result[7]=ContextUtils.getUserId();
		for(int i=8;i<resultLength;i++){
			result[i]=BranchDataType.ROLE;
		}
		
//		return roleDao.find(sql.toString(),businessSystemId, false,ContextUtils.getCompanyId(),ContextUtils.getCompanyId(),BranchDataType.ROLE,ContextUtils.getCompanyId(),BranchDataType.USER,ContextUtils.getUserId());
		return roleDao.find(sql.toString(),result);
	}
	
	/**
	 * 根据用户id删除用户角色
	 * @param userId
	 */
	public void deleteRoleUserByUserId(Long userId) {
		roleUserDao.executeUpdate("delete RoleUser where user.id=? ", userId);
	}
	
	/**
	 * 根据用户id获得角色
	 * @param userId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Role> getRolesByUserId(Long userId) {
		StringBuilder hql=new StringBuilder();
		hql.append("select distinct role from Role role join role.roleUsers ru join ru.user user where user.id = ? and role.deleted = false and ru.deleted = false and user.deleted = false");
		return roleDao.find(hql.toString(),userId);
	}
	/**
	 * 获取用户角色包括 用户角色,含有所有用户对应关系的角色,用户所在部门角色,用户所在工作组的角色
	 * @param userId
	 * @return
	 */
	public List<Role> getRolesByUserIdNew(Long userId){
		List<Role> list=new ArrayList<Role>();
		list.addAll(acsUtils.getRolesByUserIncludeConsigner(ContextUtils.getCompanyId(), userId));
		return list;
	}
	
	/**
	 * 根据部门id获得角色
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
		acsUtils.getRolesWhereHasAllDept(ContextUtils.getCompanyId());
		Department d=departmentDao.get(departmentId);
		if(!d.getBranch()){
			roles.addAll(acsUtils.getRolesWhereHasAllDept(ContextUtils.getCompanyId()));
		}
		rolesList.addAll(roles);
		return rolesList;
	}
	
	/**
	 * 根据工作组id获得角色
	 * @param workgroupId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Role> getRolesByWorkgroupId(Long workgroupId) {
		StringBuilder hql=new StringBuilder();
		Set<Role> roles=new HashSet<Role>();
		List<Role> rolesList=new ArrayList<Role>();
		hql.append("select distinct role from Role role join role.roleWorkgroups rw join rw.workgroup w where w.id = ? and role.deleted = false and rw.deleted = false and w.deleted = false");
		roles.addAll(roleDao.find(hql.toString(),workgroupId));
		roles.addAll(acsUtils.getRolesWhereHasAllGroup(ContextUtils.getCompanyId()));
		rolesList.addAll(roles);
		return rolesList;
	}
	//删除角色时删除角色对应的中间表
	public void clean(Long rId) {
		Role role=roleDao.get(rId);
		Set<RoleUser> roleUsers=role.getRoleUsers();
		Set<RoleDepartment> roleDepartments =role.getRoleDepartments();
		Set<RoleWorkgroup> roleWorkgroups = role.getRoleWorkgroups();
		Set<RoleFunction> roleFunctions = role.getRoleFunctions();
		if(!roleUsers.isEmpty()){
			for(RoleUser ru:roleUsers){
				roleUserDao.delete(ru);
			}
		}
		if(!roleDepartments.isEmpty()){
			for(RoleDepartment rd:roleDepartments){
				roleDepartmentDao.delete(rd);
			}
		}
		if(!roleWorkgroups.isEmpty()){
			for(RoleWorkgroup rw:roleWorkgroups){
				role_wDao.delete(rw);
			}
		}
		if(!roleFunctions.isEmpty()){
			for(RoleFunction rf:roleFunctions){
				role_fDao.delete(rf);
			}
		}
	}
	public String createTree(BusinessSystem bs,Long roleId,Integer isAddOrRomove) {
		Role entity = roleDao.get(roleId);
		List<ZTreeNode> ztreeNodes=new ArrayList<ZTreeNode>();
		List<Function> functions=null;
		if(isAddOrRomove.equals(1)){
			functions=functionDao.findList("select distinct f from RoleFunction rf join rf.role r join rf.function f where r.id=? and f.businessSystem.id=? and rf.deleted=? and f.deleted=? and f.functionType=? order by f.ordinal,f.name",entity.getId(),bs.getId(),false,false,FunctionType.PRIVATE);
		}else{
			functions=functionDao.findList("from Function f where f.id not in (select distinct rf.function.id from RoleFunction rf where rf.role.id=? and rf.deleted=?) and f.businessSystem.id=? and f.deleted=? and f.functionType=? order by f.ordinal,f.name",entity.getId(),false,bs.getId(),false,FunctionType.PRIVATE);
		}
		//系统节点
		ZTreeNode business_=new ZTreeNode("business_"+bs.getId().toString(),"0",menuManager.getNameToi18n(bs.getName()),"false","false",bs.getPath(),"","system","system","false","false","false","true","false");
		ZTreeNode void_=new ZTreeNode("void_"+bs.getId().toString(),"0", Struts2Utils.getText("role.remove.resources.not.classified"),"false","false","root",bs.getPath(),"void","void","false","false","false","true","false");
		//递归拼树
		for(Function function:functions){
			setNodes(function,functions,ztreeNodes);
		}
		if(hasChildInBusiness(ztreeNodes,business_.getId())){
			business_.setIsParent("true");
			business_.setOpen("true");
		}
		if(hasChildInVoid(ztreeNodes,void_.getId())){
			void_.setIsParent("true");
			void_.setOpen("true");
		}
		ztreeNodes.add(business_);
		ztreeNodes.add(void_);
		return JsonParser.object2Json(ztreeNodes);
	}
	private boolean hasChildInVoid(List<ZTreeNode> ztreeNodes,String void_) {
		for(ZTreeNode zNode:ztreeNodes){
			if(zNode.getpId().equals(void_)){
				return true;
			}
		}
		return false;
	}

	private boolean hasChildInBusiness(List<ZTreeNode> ztreeNodes,String Business_) {
		for(ZTreeNode zNode:ztreeNodes){
			if(zNode.getpId().equals(Business_)){
				return true;
			}
		}
		return false;
	}

	//拼接已选资源节点
	private void setNodes(Function function,List<Function> functions,List<ZTreeNode> ztreeNodes) {
		Function parent=function.getPid()==null?null:functionDao.get(function.getPid());
		ZTreeNode node=null;
		if(parent!=null){
			if(!functions.contains(parent)){
				node=new ZTreeNode(parent.getId().toString(),parent.getPid()==null?(parent.getIsmenu()?"business_"+parent.getBusinessSystem().getId().toString():"void_"+parent.getBusinessSystem().getId().toString()):parent.getPid().toString(), parent.getName(),"true","true",parent.getPath(),"",getStringType(parent),getStringType(parent),"false","true","false","true","false");
				if(!ztreeNodes.contains(node)){
					ztreeNodes.add(node);
				}
				setNodes(parent,functions,ztreeNodes);
			}
		}
		node=new ZTreeNode(function.getId().toString(),parent==null?(function.getIsmenu()?"business_"+function.getBusinessSystem().getId().toString():"void_"+function.getBusinessSystem().getId().toString()):parent.getId().toString(), function.getName(),"true","false",function.getPath(),"",getStringType(function),getStringType(function),"false","false","false","true","false");
		if(hasChildInFunctions(function,functions)){
			node.setIsParent("true");
		}
		if(!ztreeNodes.contains(node)){
			ztreeNodes.add(node);
		}
	}
	//查找当前集合里有没有节点是给定节点的子节点
	private boolean hasChildInFunctions(Function function,List<Function> functions) {
		List<Function> fs=getFunctionsByPid(function.getId());
		for(Function f:fs){
			if(functions.contains(f)){
				return true;
			}else{
				return hasChildInFunctions(f,functions);
			}
		}
		return false;
	}
	//根据function菜单属性返回
	private String getStringType(Function function){
		if(function.getIsmenu()){
			return "menu";
		}else{
			return "function";
		}
	}
	public List<Function> getFunctionsByPid(Long id) {
		return functionDao.findList("from Function f where f.deleted=? and f.pid=? order by f.ordinal,f.name ", false,id);
	}
	private List<Menu> getMenusByPid(Menu menu,BusinessSystem system) {
		return menuDao.findList("from Menu m where m.parent.id=? and m.systemId=?", menu.getId(),system.getId());
	}
	@SuppressWarnings("unchecked")
	public boolean hasAllUserRole(Long id) {
		List objs=roleDao.find("select ru from Role r join r.roleUsers ru where ru.deleted=? and ru.allUser=? and r.id=? and (ru.companyId=? or ru.companyId is null)",false,"ALL_USER",id,ContextUtils.getCompanyId());
		return (objs==null?false:objs.size()>0);
	}
	@SuppressWarnings("unchecked")
	public boolean hasAllDeptRole(Long id) {
		List objs=roleDao.find("select rd from Role r join r.roleDepartments rd where rd.deleted=? and rd.allDept=? and r.id=? and (rd.companyId=? or rd.companyId is null)" ,false,"ALL_DEPARTMENT",id,ContextUtils.getCompanyId());
		return objs==null?false:objs.size()>0;
	}
	@SuppressWarnings("unchecked")
	public boolean hasAllGroupRole(Long id) {
		List objs=roleDao.find("select rw from Role r join r.roleWorkgroups rw where rw.deleted=? and rw.allGroup=? and r.id=? and (rw.companyId=? or rw.companyId is null)",false,"ALL_WORKGROUP",id,ContextUtils.getCompanyId());
		return objs==null?false:objs.size()>0;
	}
	public void addRoleAllUser(Long roleId, String addUserIds) {
		if(!hasAllUserRole(roleId)){
			RoleUser ru=new RoleUser();
			ru.setRole(roleDao.get(roleId));
			ru.setCompanyId(ContextUtils.getCompanyId());
			ru.setAllUser(addUserIds);
			roleUserDao.save(ru);
		}
	}
	public void addRoleAllDepartment(Long roleId, String addDepartmentIds) {
		if(!hasAllDeptRole(roleId)){
			RoleDepartment rd=new RoleDepartment();
			rd.setRole(roleDao.get(roleId));
			rd.setCompanyId(ContextUtils.getCompanyId());
			rd.setAllDept(addDepartmentIds);
			roleDepartmentDao.save(rd);
		}
	}
	public void addRoleAllWorkGroup(Long roleId, String addWorkgroupIds) {
		if(!hasAllGroupRole(roleId)){
			RoleWorkgroup rw=new RoleWorkgroup();
			rw.setRole(roleDao.get(roleId));
			rw.setCompanyId(ContextUtils.getCompanyId());
			rw.setAllGroup(addWorkgroupIds);
			role_wDao.save(rw);
		}
	}
	public String hasAll(String buttonType,Long roleId) {
		if(buttonType.equals("ADD_USER")){
			if(hasAllUserRole(roleId)){
				return Struts2Utils.getText("authorization.userExistTip");
			}
		}else if(buttonType.equals("ADD_DEPARTMENT")){
			if(hasAllDeptRole(roleId)){
				return Struts2Utils.getText("authorization.departmentExistTip");
			}
		}else if(buttonType.equals("ADD_WORKGROUP")){
			if(hasAllGroupRole(roleId)){
				return Struts2Utils.getText("authorization.workGroupExistTip");
			}
		} 
		return "";
	}
	@SuppressWarnings("unchecked")
	public HSSFWorkbook getWorkBook(Long businessSystemId) {
		BusinessSystem bs=businessSystemDao.get(businessSystemId);
		List<Object> functions=functionDao.find("select f.functionType,f.name,f.code,f.path,f.pid from Function f where f.deleted=? and f.businessSystem.id=?",false,businessSystemId);
		List<Object> roles=roleDao.find("select r.name,r.code from Role r where r.deleted=? and r.businessSystem.id=?", false,businessSystemId);
		String[] functionTitles={"资源类型","资源名称","资源编码","资源路径","所属模块"};
		String[] roleTitles={"角色名称","角色编号"};
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFCell cell=null;
    	HSSFSheet sheet=wb.createSheet(bs.getName()+".资源");
    	HSSFSheet sheet2=wb.createSheet(bs.getName()+".角色");
        HSSFFont boldFont = wb.createFont();
        boldFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        HSSFCellStyle boldStyle = wb.createCellStyle();
        boldStyle.setFont(boldFont);
        HSSFRow row = sheet.createRow(0);
        HSSFRow row2 = sheet2.createRow(0);
        for(int i=0;i<functionTitles.length;i++){
        	cell=row.createCell(i);
        	cell.setCellValue(functionTitles[i]);
        	cell.setCellStyle(boldStyle);
        }
        for(int i=0;i<roleTitles.length;i++){
        	cell=row2.createCell(i);
        	cell.setCellValue(roleTitles[i]);
        	cell.setCellStyle(boldStyle);
        }
        for(Object function:functions){
        	HSSFRow row_n=sheet.createRow(sheet.getLastRowNum()+1);
        	for(int i=0;i<functionTitles.length;i++){
            	cell=row_n.createCell(i);
            	if(functionTitles[i].equals("资源类型")){
            		if(((Object[])function)[i]==null){
            			cell.setCellValue("");
            		}else{
            			cell.setCellValue(Struts2Utils.getText(((FunctionType)((Object[])function)[0]).getCode()));
            		}
            		
            	}else{
            		cell.setCellValue((i==functionTitles.length-1?(((Object[])function)[i]==null?bs.getName():functionDao.get((Long)((Object[])function)[i]).getName()):(((Object[])function)[i]==null?"":((Object[])function)[i].toString())));
            	}
            }
        }
        for(Object role:roles){
        	HSSFRow row_n=sheet2.createRow(sheet2.getLastRowNum()+1);
        	 for(int i=0;i<roleTitles.length;i++){
             	cell=row_n.createCell(i);
             	cell.setCellValue(((Object[])role)[i].toString());
             }
        }
	    return wb;
	}
	/**
	 * 导入角色和资源关系
	 * @param workBook
	 * @return
	 */
	public String importRole(Workbook workBook) {
		Sheet sheet=workBook.getSheetAt(0);
		int firstRowNum = sheet.getFirstRowNum();
		int lastRowNum = sheet.getLastRowNum();
		List<String[]> lists = new ArrayList<String[]>();
		for(int i = firstRowNum+1; i <= lastRowNum; i++){
			List<String> columns = new ArrayList<String>();
			Row rowData = sheet.getRow(i);
			if(rowData == null) continue;
			for (int j = 0; j <= rowData.getLastCellNum()-1; j++) {
				Cell cell = rowData.getCell(j);
				if(cell==null){
					columns.add("");
				}else{
					columns.add(cell.toString());
				}
			}
			lists.add(toStringArray(columns.toArray()));
		}
		return importData(lists);
	}
	/**
	 * 导入用户与角色关系
	 * @param workBook
	 * @return
	 */
	public String accredit(Workbook workBook) {
		Sheet sheet=workBook.getSheetAt(0);
		int firstRowNum = sheet.getFirstRowNum();
		int lastRowNum = sheet.getLastRowNum();
		List<String[]> lists = new ArrayList<String[]>();
		for(int i = firstRowNum+1; i <= lastRowNum; i++){
			List<String> columns = new ArrayList<String>();
			Row rowData = sheet.getRow(i);
			if(rowData == null) continue;
			for (int j = 0; j <= rowData.getLastCellNum()-1; j++) {
				Cell cell = rowData.getCell(j);
				if(cell==null){
					columns.add("");
				}else{
					
					columns.add(cell.toString());
				}
			}
			lists.add(toStringArray(columns.toArray()));
		}
		return importRoleUserData(lists);
	}
	private String importRoleUserData(List<String[]> lists) {
		Integer secuess=0;
		Integer error=0;
		String msg="";
		for(int i=0;i<lists.size();i++){
			String result=insertRoleUserData(i+2,lists.get(i));
			if(result.equals("")){
				secuess++;
			}else{
				error++;
			}
			msg=msg+result;
		}
		return Struts2Utils.getText("importRoleMessage", new String[]{secuess+"",error+""})+"\r"+msg;
	}
	private String insertRoleUserData(int i, String[] strs) {
		User user=null;
		BusinessSystem bs=null;
		Role role=null;
		Department branch=null;
		if(strs.length<7){
			return Struts2Utils.getText("importRoleMessageTemplateRuleError", new String[]{i+""})+"\r";
		}
		String roleName=strs[0];
		String roleCode=strs[1];
		String systemCode=strs[2];
		String roleBranchName=strs[3];
		String userBranchName=strs[4];
		String loginName=strs[5];
		String userName=strs[6];
		user=getUser(loginName,userBranchName);
		if(user==null){
			return Struts2Utils.getText("importRoleMessageUserInfoError", new String[]{i+""})+"\r";
		}
		if(!roleBranchName.trim().equals("")){
			if(!roleBranchName.trim().equals(ContextUtils.getCompanyName())){
				branch=acsUtils.getDepartmentByName(roleBranchName, ContextUtils.getCompanyId());
				if(branch==null){
					return Struts2Utils.getText("importRoleMessageRoleBranchError", new String[]{i+""})+"\r";
				}
				if(systemCode.equals("acs")){
					return Struts2Utils.getText("importRoleMessageAcsRoleError", new String[]{i+""})+"\r";
				}
			}
		}
		if(roleName.equals("")||roleCode.equals("")){
			return Struts2Utils.getText("importRoleMessageRoleInfoError", new String[]{i+""})+"\r";
		}
		bs=acsUtils.getSystemsByCode(systemCode);
		if(bs==null){
			return Struts2Utils.getText("importRoleMessageSystemCodeError", new String[]{i+""})+"\r";
		}
		role=getRoleByCode2(roleCode);
		if(isAdminRole(roleCode)){
			return Struts2Utils.getText("importRoleMessageAdminRoleError", new String[]{i+""})+"\r";
		}
		if(role==null){
			if(!validataRoleName(branch,systemCode,roleName)){
				return Struts2Utils.getText("importRoleMessageRoleRepeatError", new String[]{i+""})+"\r";
			}
			role=new Role();
			role.setCode(roleCode);
			role.setBusinessSystem(bs);
			role.setName(roleName);
			role.setCompanyId(ContextUtils.getCompanyId());
			if(branch!=null){
				role.setSubCompanyId(branch.getId());
				role.setSubCompanyName(branch.getName());
			}else{
				role.setSubCompanyName(ContextUtils.getCompanyName());
			}
			if(role.getCode().equals(bs.getCode()+"_Everyone")){
				return Struts2Utils.getText("importRoleMessageRoleAnonymousError", new String[]{i+""})+"\r";
			}
			roleDao.save(role);
		}else{
			if(branch!=null&&branch.getBranch()){
				if(role.getSubCompanyId()==null||!role.getSubCompanyId().equals(branch.getId())){
					return Struts2Utils.getText("importRoleMessageRoleCodeRepeatError", new String[]{i+""})+"\r";
				}
			}else{
				if(role.getSubCompanyId()!=null){
					return Struts2Utils.getText("importRoleMessageRoleCodeRepeatError", new String[]{i+""})+"\r";
				}
			}
			if(!role.getBusinessSystem().getCode().equals(systemCode)){
				return Struts2Utils.getText("importRoleMessageRoleCodeRepeatError", new String[]{i+""})+"\r";
			}
			if(role.getCode().equals(bs.getCode()+"_Everyone")){
				return Struts2Utils.getText("importRoleMessageRoleAnonymousError", new String[]{i+""})+"\r";
			}
		}
		if(!existRoleUser(role,user)){
			RoleUser ru=new RoleUser();
			ru.setCompanyId(ContextUtils.getCompanyId());
			ru.setRole(role);
			ru.setUser(user);
			roleUserDao.save(ru);
		}
		return "";
	}

	private boolean isAdminRole(String roleCode) {
			if(roleCode.equals("acsSystemAdmin")){
				return true;
			}
			if(roleCode.equals("acsSecurityAdmin")){
				return true;
			}
			if(roleCode.equals("acsAuditAdmin")){
				return true;
			}
			if(roleCode.equals("acsBranchAdmin")){
				return true;
			}
			return false;
	}
	private String importData(List<String[]> lists) {
		Integer secuess=0;
		Integer error=0;
		String msg="";
		for(int i=0;i<lists.size();i++){
			String result=insertdata(i+2,lists.get(i));
			if(result.equals("")){
				secuess++;
			}else{
				error++;
			}
			msg=msg+result;
		}
		String[] messages = new String[2];
		messages[0] = secuess.toString();
		messages[1] = error.toString();
		return Struts2Utils.getText("importRoleMessage",messages)+"\r"+msg;
	}
	private String insertdata(int i,String[] strs) {
		Department dept=null;
		if(strs.length<7){
			return Struts2Utils.getText("importRoleFunMessageNoFunctionError",new String[]{i+""})+"\r";
		}
		String roleName=strs[0];
		String roleCode=strs[1];
		String branchName=strs[2];
		String systemCode=strs[3];
		String moduleName=strs[4];
		String functionName=strs[5];
		String functionCode=strs[6];
		if(functionCode.equals("IS_AUTHENTICATED_ANONYMOUSLY")){
			return Struts2Utils.getText("importRoleFunMessageAnonyFunctionError",new String[]{i+""})+"\r";
		}
		Function function=getFunctionByCode(functionCode);
		if(function==null){
			return Struts2Utils.getText("importRoleFunMessageNoFunctionError",new String[]{i+""})+"\r";
		}
		if(!function.getFunctionType().equals(FunctionType.PRIVATE)){
			if(FunctionType.PUBLIC.equals(function.getFunctionType())){
				return Struts2Utils.getText("importRoleFunMessagePublicFunctionError",new String[]{i+""})+"\r";
			}else if(FunctionType.DEFAULT.equals(function.getFunctionType())){
				return Struts2Utils.getText("importRoleFunMessageAnonyFunctionError",new String[]{i+""})+"\r";
			}
			
		}
		if(!function.getBusinessSystem().getCode().equals(systemCode)){
			return Struts2Utils.getText("importRoleFunMessageFunctionSystemError",new String[]{i+""})+"\r";
		}
		if(roleName.equals("")||roleCode.equals("")){
			return Struts2Utils.getText("importRoleFunMessageRoleError",new String[]{i+""})+"\r";
		}
		if(!branchName.trim().equals("")){
			if(!branchName.equals(ContextUtils.getCompanyName())){
				dept=acsUtils.getDepartmentByName(branchName, ContextUtils.getCompanyId());
				if(dept==null){
					return Struts2Utils.getText("importRoleMessageRoleBranchError",new String[]{i+""})+"\r";
				}
				if(systemCode.equals("acs")){
					return Struts2Utils.getText("importRoleMessageAcsRoleError",new String[]{i+""})+"\r";
				}
			}
			
		}
		Role role=getRoleByCode2(roleCode);
		if(role==null){
			if(!validataRoleName(dept,systemCode,roleName)){
				return Struts2Utils.getText("importRoleMessageRoleRepeatError",new String[]{i+""})+"\r";
			}
			role=new Role();
			role.setBusinessSystem(acsUtils.getSystemsByCode(systemCode));
			role.setCompanyId(ContextUtils.getCompanyId());
			role.setName(roleName);
			role.setCode(roleCode);
			if(dept!=null){
				role.setSubCompanyId(dept.getId());
				role.setSubCompanyName(dept.getName());
			}else{
				role.setSubCompanyName(ContextUtils.getCompanyName());
			}
			roleDao.save(role);
		}else{
			if(dept!=null&&dept.getBranch()){
				if(role.getSubCompanyId()==null||!role.getSubCompanyId().equals(dept.getId())){
					return Struts2Utils.getText("importRoleMessageRoleCodeRepeatError",new String[]{i+""})+"\r";
				}
			}else{
				if(role.getSubCompanyId()!=null){
					return Struts2Utils.getText("importRoleMessageRoleCodeRepeatError",new String[]{i+""})+"\r";
				}
			}
			if(!role.getBusinessSystem().getCode().equals(systemCode)){
				return Struts2Utils.getText("importRoleMessageRoleCodeRepeatError",new String[]{i+""})+"\r";
			}
		}
		if(!existRoleFunction(role,function)){
			RoleFunction rf=new RoleFunction();
			rf.setCompanyId(ContextUtils.getCompanyId());
			rf.setFunction(function);
			rf.setRole(role);
			roleFunctionDao.save(rf);
		}
		return "";
	}
	
	private boolean validataRoleName(Department branch,String systemCode, String roleName) {
		Long count=0l;
		if(branch!=null){
			count=roleDao.findLong("select count(*) from Role r where r.deleted=? and r.name=? and (r.companyId=? or r.companyId is null) and r.businessSystem.code=? and r.subCompanyId=?", false,roleName,ContextUtils.getCompanyId(),systemCode,branch.getId());
		}else{
			count=roleDao.findLong("select count(*) from Role r where r.deleted=? and r.name=? and (r.companyId=? or r.companyId is null) and r.businessSystem.code=? and r.subCompanyId is null", false,roleName,ContextUtils.getCompanyId(),systemCode);
		}
		if(count>0){
			return false;
		}
		return true;
	}
	private boolean existRoleFunction(Role role, Function function) {
		Long count=roleFunctionDao.findLong("select count(*) from RoleFunction fr where fr.deleted=? and fr.role.id=? and fr.function.id=? and fr.companyId=?", false,role.getId(),function.getId(),ContextUtils.getCompanyId());
		if(count>0){
			return true;
		}
		return false;
	}
	private boolean existRoleUser(Role role, User user) {
		Long count = roleUserDao.findLong("select count(*) from RoleUser ru where ru.deleted=? and ru.role.id=? and ru.user.id=? and ru.companyId=?", false,role.getId(),user.getId(),ContextUtils.getCompanyId());
		if(count>0){
			return true;
		}
		return false;
	}
	private User getUser(String loginName,String userBranchName){
		List<User> users=new ArrayList<User>();
		if(userBranchName.trim().equals("")||userBranchName.equals(ContextUtils.getCompanyName())){
			users=userDao.findList("from User u where u.deleted=? and u.loginName=? and u.subCompanyId is null and u.companyId=?", false,loginName,ContextUtils.getCompanyId());
		}else{
			users=userDao.findList("from User u where u.deleted=? and u.loginName=? and u.subCompanyName=? and u.companyId=?", false,loginName,userBranchName,ContextUtils.getCompanyId());
		}
		if(users!=null&&users.size()>0){
			return users.get(0);
		}
		return null;
	}
	private Role getRoleByCode2(String roleCode) {
		List<Role> roles=roleDao.findList("from Role r where r.deleted=? and r.code=? and (r.companyId=? or r.companyId is null)", false,roleCode,ContextUtils.getCompanyId());
		if(roles!=null&&roles.size()>0){
			return roles.get(0);
		}
		return null;
	}
	private Function getFunctionByCode(String functionCode) {
		List<Function> functions=functionDao.findList("from Function f where f.deleted=? and f.code=?",false,functionCode);
		if(functions!=null&&functions.size()>0){
			return functions.get(0);
		}
		return null;
	}
	private String[] toStringArray(Object[] array) {
		String[] str=new String[array.length];
		for(int i=0;i<str.length;i++){
			str[i]=(String)array[i];
		}
		return str;
	}
	public List<Role> getRolesBySystem(BusinessSystem businessSystem) {
		return roleDao.findList("from Role r where r.deleted=? and r.businessSystem.id=?",false,businessSystem.getId());
	}
	public List<FunctionVo> queryFunctionRole(String functionPath,
			String functionCode, String functionName) {
		List<Function> functions=new ArrayList<Function>();
		List<Role> roles=new ArrayList<Role>();
		List<FunctionVo> fvs=new ArrayList<FunctionVo>();
		FunctionVo fv=null;
		String roleMsg="";
		StringBuilder sb=new StringBuilder("from Function f where f.deleted=? ");
		if(StringUtils.isNotEmpty(functionPath)){
			sb.append("and f.path like '%"+functionPath.trim()+"%'");
		}
		if(StringUtils.isNotEmpty(functionCode)){
			sb.append("and f.code like '%"+functionCode.trim()+"%'");
		}
		if(StringUtils.isNotEmpty(functionName)){
			sb.append("and f.name like '%"+functionName.trim()+"%'");
		}
		functions=functionDao.findList(sb.toString(), false);
		for(Function f:functions){
			roles=roleFunctionDao.find("select r from RoleFunction rf join rf.role r join rf.function f where rf.deleted=? and r.deleted=? and f.deleted=? and f.id=?",false,false,false,f.getId());
			fv=new FunctionVo();
			fv.setCode(f.getCode());
			fv.setName(f.getName());
			fv.setPath(f.getPath());
			for(Role r:roles){
				roleMsg=roleMsg+(r.getName()+"("+r.getBusinessSystem().getName()+")</br>");
			}
			System.out.println(roleMsg);
			if(StringUtils.isNotEmpty(roleMsg)){
				roleMsg=roleMsg.substring(0, roleMsg.length()-1);
			}
			if(f.getFunctionType().equals(FunctionType.DEFAULT)){
				fv.setRoles("匿名资源");
			}else if(f.getFunctionType().equals(FunctionType.PUBLIC)){
				fv.setRoles(f.getBusinessSystem().getName()+":普通用户");
			}else{
				fv.setRoles(roleMsg.equals("")?"无":roleMsg);
			}
			roleMsg="";
			fvs.add(fv);
		}
		return fvs;
	}
}
