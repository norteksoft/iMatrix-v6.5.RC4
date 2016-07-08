package com.norteksoft.acs.service.authorization;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.acs.base.enumeration.BranchDataType;
import com.norteksoft.acs.base.orm.hibernate.SimpleHibernateTemplate;
import com.norteksoft.acs.entity.authorization.Function;
import com.norteksoft.acs.entity.authorization.FunctionGroup;
import com.norteksoft.acs.entity.authorization.Role;
import com.norteksoft.acs.entity.authorization.RoleFunction;
import com.norteksoft.acs.entity.authorization.RoleUser;
import com.norteksoft.acs.service.AcsUtils;
import com.norteksoft.mms.module.service.MenuManager;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;

@SuppressWarnings("deprecation")
@Service
@Transactional
public class StandardRoleManager {
	private SimpleHibernateTemplate<Role, Long> roleDao;
	private SimpleHibernateTemplate<RoleUser, Long> roleUserDao;
	private SimpleHibernateTemplate<FunctionGroup, Long> functionGroupDao;
	private SimpleHibernateTemplate<RoleFunction, Long> roleFunctionDao;
	private SimpleHibernateTemplate<Function, Long> functionDao;
	private Long companyId;
	@Autowired
	private AcsUtils acsUtils;
	public Long getCompanyId() {
		if (companyId == null) {
			return ContextUtils.getCompanyId();
		} else
			return companyId;
	}
	
	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		roleUserDao=new SimpleHibernateTemplate<RoleUser, Long>(sessionFactory, RoleUser.class);
		roleDao = new SimpleHibernateTemplate<Role, Long>(sessionFactory, Role.class);
		functionGroupDao = new SimpleHibernateTemplate<FunctionGroup, Long>(sessionFactory, FunctionGroup.class);
		roleFunctionDao = new SimpleHibernateTemplate<RoleFunction, Long>(sessionFactory,RoleFunction.class);
		functionDao = new SimpleHibernateTemplate<Function, Long>(sessionFactory,Function.class);
	}
	
	@Transactional(readOnly = true)
	public Role getStandardRole(Long id){
		return roleDao.get(id);
	}
	
	public Role getStandarRoleByCode(String code, Long systemId){
		return (Role) roleDao.findUnique("from Role sr where sr.code=? and sr.businessSystem.id=? and sr.deleted=?", code, systemId,false);
	}
	/**
	 * 在权限系统中添加的角色带有公司id
	 * @param code
	 * @param systemId
	 * @param companyId
	 * @return
	 */
	public Role getStandarRoleByCode(String code, Long systemId,Long companyId){
		return (Role) roleDao.findUnique("from Role sr where sr.code=? and sr.businessSystem.id=? and sr.deleted=? and sr.companyId=?", code, systemId,false,companyId);
	}
	
	public void deleteStandardRole(Long id){
		Role role = roleDao.get(id);
		role.setDeleted(true);
		roleDao.save(role);
	}

	@Transactional(readOnly = true)
	public List<Role> getAllStandardRole(Long sysId){
		String hql = "from Role sr where sr.businessSystem.id=? and sr.deleted=? and (sr.companyId is null or sr.companyId=?) order by sr.weight desc";
		return roleDao.find( hql,sysId,false,ContextUtils.getCompanyId());
	}
	@Transactional(readOnly = true)
	public List<Role> getAllStandardRoleByCompany(Long sysId,Long companyId){
		String hql = "from Role sr where sr.businessSystem.id=? and sr.deleted=? and sr.companyId=null";
		if(companyId!=null){
			hql = "from Role sr where sr.businessSystem.id=? and sr.deleted=? and (sr.companyId!=null and sr.companyId=?)";
			return roleDao.find(hql, sysId,false,companyId);
		}else{
			return roleDao.find(hql, sysId,false);
		}
	}

	@Transactional(readOnly = true)
	public Page<Role> getAllStandardRole(Page<Role> page, Long sysId){
		String hql = "from Role sr where sr.businessSystem.id=? and sr.deleted=? order by sr.weight desc ,sr.id desc";
		return roleDao.find(page, hql,sysId,false);
	}
	
	public void saveStandardRole(Role role){
		roleDao.save(role);
	}
	
	/**
	 * 角色添加功能 
	 */
	public Page<FunctionGroup> listFunctions(Page<FunctionGroup> functionpage,Long sysId){
		return functionGroupDao.findByCriteria(
				functionpage, Restrictions.eq("businessSystem.id", sysId), Restrictions.eq("deleted", false));
	}
	
	/**
	 * 角色移除功能 
	 */
	public Page<FunctionGroup> canRemoveFunctions(Page<FunctionGroup> functionpage, Long sysId, Long roleId){
		String hql = "select distinct fung from FunctionGroup fung " +
				     "join fung.functions fun join fun.roleFunctions r_f " +
				     "where r_f.role.id=? and fun.deleted=? " +
				     "and r_f.deleted=? and fung. deleted=? and fung.businessSystem.id=?";
		return functionGroupDao.find(functionpage, hql, roleId, false, false, false, sysId);
	}
	
	public List<Long> getFunctionIds(Long roleId,Long sysId) {
		List<Long> FunctionIds = new ArrayList<Long>();
		List<RoleFunction> role_Functions = roleFunctionDao.findByCriteria(
				Restrictions.eq("role.id", roleId), Restrictions.eq("deleted", false));
		for (RoleFunction role_Function : role_Functions) {
			if(role_Function.getFunction()!=null){
				FunctionIds.add(role_Function.getFunction().getId());
			}
		}
		return FunctionIds;
	}

	public void roleAddFunction(Long roleId,List<Long> functionIds,Integer isAdd){
		Role role = roleDao.get(roleId);
		if(isAdd==0){
			RoleFunction role_f = null;
			for (Long funId : functionIds) {
				role_f = new RoleFunction();
				role_f.setRole(role);
				role_f.setFunction(functionDao.get(funId));
				role_f.setCompanyId(getCompanyId());
				roleFunctionDao.save(role_f);
			}
		}
		if(isAdd==1){
			List<RoleFunction> funList = roleFunctionDao.findByCriteria(
					Restrictions.in("function.id", functionIds), Restrictions.eq("role.id", roleId), Restrictions.eq("deleted", false));
			for (RoleFunction role_Function : funList) {
				role_Function.setDeleted(true);
				roleFunctionDao.save(role_Function);
			}
		}
	}
	/**
	 * 复制角色，即复制角色和资源之间的关系
	 * @param sourceRoleId 源角色id
	 * @param roleIds 需要具有和源角色相同权限的角色id集合
	 */
	public void copyRoleAndFunction(List<Long> sourceRoleIds,List<Long> roleIds){
		Set<Long> functionIds = getFunctionIdsByRoles(sourceRoleIds);//获得源角色所具有的资源列表
		for(Long roleId:roleIds){
			for(Long funId:functionIds){
				boolean roleHasFun = roleHasFunction(roleId, funId);
				if(!roleHasFun){//如果角色roleId没有资源funId的权限，则需要添加，否则不需要添加
					Role role = getStandardRole(roleId);
					RoleFunction role_f = null;
					role_f = new RoleFunction();
					role_f.setRole(role);
					role_f.setFunction(functionDao.get(funId));
					role_f.setCompanyId(getCompanyId());
					roleFunctionDao.save(role_f);
				}
			}
		}
	}
	
	public Long getSystemId(){
		return ContextUtils.getSystemId();
	}
	
	public Set<Role> getAllRolesByUser(Long userId, Long companyId){
		return acsUtils.getRolesByUserIncludeConsigner(companyId, userId);
	}
	
	/**
	 * 根据角色集合查询所有角色能访问的资源
	 * @param roles
	 * @return
	 */
	public Set<Function> getFunctionsByRoles(Collection<Role> roles){
		Set<Function> functions = new HashSet<Function>();
		for(Role role : roles){
			functions.addAll(getFunctionsByRole(role));
		}
		return functions;
	}
	
	/**
	 * 根据角色查询所有角色能访问的资源
	 * @param role
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Function> getFunctionsByRole(Role role){
		return getFunctionsByRole(role.getId());
	}
	/**
	 * 根据角色id查询所有角色能访问的资源
	 * @param roleId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Function> getFunctionsByRole(Long roleId){
		StringBuilder hql = new StringBuilder();
		hql.append("select f from Function f join f.roleFunctions rf join rf.role r where r.id=? and r.deleted=? and rf.deleted=? and f.deleted=? ");
		return functionDao.find(hql.toString(), roleId, false, false, false);
	}
	/**
	 * 根据角色id查询所有角色能访问的资源id
	 * @param roleId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Set<Long> getFunctionIdsByRoles(List<Long> roleIds){
		Set<Long> result = new HashSet<Long>();
		if(roleIds.size()>0){
			Object[] objs = new Object[3+roleIds.size()];
			StringBuilder hql = new StringBuilder();
			hql.append("select f.id from Function f join f.roleFunctions rf join rf.role r where (");
			int i=0;
			for(Long roleid:roleIds){
				hql.append("r.id=?");
				objs[i++]=roleid;
				if(i<roleIds.size()){
					hql.append(" or ");
				}
			}
			objs[i++]=false;
			objs[i++]=false;
			objs[i++]=false;
			hql.append(") and r.deleted=? and rf.deleted=? and f.deleted=? ");
			List<Long> funIds =  functionDao.find(hql.toString(), objs);
			result.addAll(funIds);
		}
		return result;
	}
	
	
	public RoleFunction getRoleFunction(String roleCode,String funPath,String code){
		List<RoleFunction> roleFuncs=functionDao.find("from RoleFunction rf where (rf.role!=null and rf.role.code=?) and (rf.function!=null and rf.function.path=? and rf.function.code=?) and rf.deleted=?",roleCode,funPath,code,false );
		if(roleFuncs.size()>0)return roleFuncs.get(0);
		return null;
	}
	public boolean roleHasFunction(Long roleId,Long functionId){
		long countRf=functionDao.findLong("select count(rf) from RoleFunction rf where (rf.role!=null and rf.role.id=?) and (rf.function!=null and rf.function.id=?) and rf.deleted=?",roleId,functionId,false );
		if(countRf>0)return true;
		return false;
	}
	
	public void saveRoleFunction(RoleFunction roleFun){
		roleFunctionDao.save(roleFun);
	}


	@SuppressWarnings("unchecked")
	public List<Role> getRolesBySystemId(Long bsId) {
		StringBuilder hql = new StringBuilder();
		hql.append("select r from Role r join r.businessSystem bs  where bs.id=? order by r.weight desc");
		return functionDao.find(hql.toString(), bsId);
	}
	@SuppressWarnings("unchecked")
	public List<String> getRoleCodesBySystemId(Long bsId) {
		StringBuilder hql = new StringBuilder();
		hql.append("select r.name from Role r join r.businessSystem bs  where bs.id=? order by r.weight desc");
		return functionDao.find(hql.toString(), bsId);
	}
	@SuppressWarnings("unchecked")
	public List<Long> getRoleIdsBySystemId() {
		StringBuilder hql = new StringBuilder();
		hql.append("select r.id from Role r  order by r.weight desc");
		return functionDao.find(hql.toString());
	}
	/**
	 * 判断角色下有没有角色和所有用户对应的中间表数据
	 * @param roleId
	 * @return
	 */
	public boolean hasAllUserByRole(Long roleId){
		Long count=roleUserDao.findLong("select count(ru.id) from RoleUser ru where ru.deleted=? and ru.role.deleted=? and (ru.companyId=? or ru.companyId is null ) and ru.role.id=? and ru.allUser=?",false,false,ContextUtils.getCompanyId(),roleId,"ALL_USER");
		return count>0?true:false;
	}
	/**
	 * 判断角色下有没有角色和所有部门对应的中间表数据
	 * @param roleId
	 * @return
	 */
	public boolean hasAllDepartmentByRole(Long roleId){
		Long count=roleUserDao.findLong("select count(ru.id) from RoleDepartment ru where ru.deleted=? and ru.role.deleted=? and (ru.companyId=? or ru.companyId is null ) and ru.role.id=? and ru.allDept=?",false,false,ContextUtils.getCompanyId(),roleId,"ALL_DEPARTMENT");
		return count>0?true:false;
	}
	/**
	 * 判断角色下有没有角色和所有工作组对应的中间表数据
	 * @param roleId
	 * @return
	 */
	public boolean hasAllWorkgroupByRole(Long roleId){
		Long count=roleUserDao.findLong("select count(ru.id) from RoleWorkgroup ru where ru.deleted=? and ru.role.deleted=? and (ru.companyId=? or ru.companyId is null ) and ru.role.id=? and ru.allGroup=?",false,false,ContextUtils.getCompanyId(),roleId,"ALL_WORKGROUP");
		return count>0?true:false;
	}
	/**
	 * 删除数据库表中deleted字段为true的数据和只存在中间表中的垃圾数据
	 */
	public void deleteJunkData(){
		roleFunctionDao.executeSqlUpdate("delete from ACS_ROLE_FUNCTION where FK_FUNCTION_ID in (select id from ACS_FUNCTION where deleted=?) or FK_ROLE_ID in (select id from ACS_ROLE where deleted=?) or FK_FUNCTION_ID not in (select id from ACS_FUNCTION) or FK_ROLE_ID not in (select id from ACS_ROLE)",true,true);
		roleFunctionDao.executeSqlUpdate("update MMS_MENU set function_id=null where function_id in (select id from ACS_FUNCTION where deleted=?) or function_id not in (select id from ACS_FUNCTION)", true);
		functionDao.executeSqlUpdate("delete from ACS_FUNCTION where deleted=?",true);
		roleUserDao.executeSqlUpdate("delete from ACS_ROLE_USER where FK_ROLE_ID in (select id from ACS_ROLE where deleted=?) or FK_ROLE_ID not in (select id from ACS_ROLE)",true);
		roleUserDao.executeSqlUpdate("delete from ACS_ROLE_DEPARTMENT where FK_ROLE_ID in (select id from ACS_ROLE where deleted=?) or FK_ROLE_ID not in (select id from ACS_ROLE)",true);
		roleUserDao.executeSqlUpdate("delete from ACS_ROLE_WORKGROUP where FK_ROLE_ID in (select id from ACS_ROLE where deleted=?) or FK_ROLE_ID not in (select id from ACS_ROLE)",true);
		roleUserDao.executeSqlUpdate("delete from PORTAL_WIDGET_ROLE where role_id in (select id from ACS_ROLE where deleted=?) or role_id not in (select id from ACS_ROLE)",true);
		roleUserDao.executeSqlUpdate("delete from ACS_BRANCH_AUTHORITY where branch_data_type=? and (data_id in (select id from ACS_ROLE where deleted=?) or data_id not in (select id from ACS_ROLE))",BranchDataType.ROLE,true);
		roleDao.executeSqlUpdate("delete from ACS_ROLE where deleted=?",true);
		roleDao.executeSqlUpdate("update PORTAL_WIDGET_PARAMETER set option_group_id=null,option_group_name=null where option_group_id not in (select id from BS_OPTION_GROUP)");
	}
	/**
	 * 根据资源id删除资源以及与资源关联表中的数据
	 * @param functionIds
	 */
	public void deleteFunctionByIds(StringBuilder functionIds) {
		roleFunctionDao.executeSqlUpdate("delete from ACS_ROLE_FUNCTION where FK_FUNCTION_ID in ("+functionIds.toString()+")");
		roleFunctionDao.executeSqlUpdate("update MMS_MENU set function_id=null where function_id in ("+functionIds.toString()+")");
		functionDao.executeSqlUpdate("delete from ACS_FUNCTION where id in ("+functionIds.toString()+")");
	}
	/**
	 * 根据角色id删除角色以及与角色关联表中的数据
	 * @param roleIds
	 */
	public void deleteRoleByIds(StringBuilder roleIds) {
		roleFunctionDao.executeSqlUpdate("delete from ACS_ROLE_FUNCTION where FK_ROLE_ID in ("+roleIds.toString()+")");
		roleUserDao.executeSqlUpdate("delete from ACS_ROLE_USER where FK_ROLE_ID in ("+roleIds.toString()+")");
		roleUserDao.executeSqlUpdate("delete from ACS_ROLE_DEPARTMENT where FK_ROLE_ID in ("+roleIds.toString()+")");
		roleUserDao.executeSqlUpdate("delete from ACS_ROLE_WORKGROUP where FK_ROLE_ID in ("+roleIds.toString()+")");
		roleUserDao.executeSqlUpdate("delete from PORTAL_WIDGET_ROLE where role_id in ("+roleIds.toString()+")");
		roleUserDao.executeSqlUpdate("delete from ACS_BRANCH_AUTHORITY where branch_data_type=? and data_id in ("+roleIds.toString()+")",BranchDataType.ROLE);
		roleDao.executeSqlUpdate("delete from ACS_ROLE where id in ("+roleIds.toString()+")");
	}
}
