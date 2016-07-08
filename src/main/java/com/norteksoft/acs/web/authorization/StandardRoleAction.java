package com.norteksoft.acs.web.authorization;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.norteksoft.acs.base.utils.ExportRole;
import com.norteksoft.acs.base.web.struts2.CRUDActionSupport;
import com.norteksoft.acs.entity.authorization.BranchAuthority;
import com.norteksoft.acs.entity.authorization.BusinessSystem;
import com.norteksoft.acs.entity.authorization.Function;
import com.norteksoft.acs.entity.authorization.Role;
import com.norteksoft.acs.entity.organization.Company;
import com.norteksoft.acs.entity.organization.Department;
import com.norteksoft.acs.entity.organization.User;
import com.norteksoft.acs.entity.organization.Workgroup;
import com.norteksoft.acs.service.authorization.BranchAuthorityManager;
import com.norteksoft.acs.service.authorization.BusinessSystemManager;
import com.norteksoft.acs.service.authorization.RoleManager;
import com.norteksoft.acs.service.authorization.StandardRoleManager;
import com.norteksoft.acs.service.organization.CompanyManager;
import com.norteksoft.acs.service.organization.DepartmentManager;
import com.norteksoft.acs.service.organization.UserManager;
import com.norteksoft.mms.module.entity.Menu;
import com.norteksoft.mms.module.service.MenuManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.JsonParser;
import com.norteksoft.product.util.tree.ZTreeNode;
import com.norteksoft.product.web.struts2.Struts2Utils;

/**
 * 标准角色Action
 * @author Administrator
 */
@ParentPackage("default")
@Results( { @Result(name = CRUDActionSupport.RELOAD, location = "standard-role?businessSystemId=${businessSystemId}", type="redirectAction") })
public class StandardRoleAction extends CRUDActionSupport<Role> {
	private static final long serialVersionUID = 1L;
	private static String ACS_SYSTEM_ADMIN="acsSystemAdmin";//系统管理员角色编码
	private static String ACS_SECURITY_ADMIN="acsSecurityAdmin";//安全管理员角色编码
	private static String ACS_AUDIT_ADMIN="acsAuditAdmin";//审计管理员角色编码
	
	private Page<Role> page = new Page<Role>(20, true);
	private Role entity;
	private Long businessSystemId;
	@Autowired
	private StandardRoleManager standardRoleManager;
	private Long id;
	private Long roleId;
	private BusinessSystemManager businessSystemManager;
	private String systemTree;
	private List<User> users=new ArrayList<User>();
	private List<Department> departments=new ArrayList<Department>();
	private List<Workgroup> workgroups=new ArrayList<Workgroup>();
	private RoleManager manager;
	private DepartmentManager departmentManager;
	private Boolean isAdminRole=false;//是否是管理员角色
	private Boolean isDefaultRole=false;//是否是管理员角色
	private List<String> defaultAdmin;//是否是系统默认管理员
	private Boolean containBranches;//集团公司中是否含有分支机构：true含有分支机构，false不含有分支机构
	private String manageBranchesIds="";//被管理的分支机构id
	private Integer isAddOrRomove;
	List<Function> functions=new ArrayList<Function>();
	
	@Autowired
	private CompanyManager companyManager;
	@Autowired
	private RoleManager roleManager;
	@Autowired
	private BranchAuthorityManager branchAuthorityManager;
	@Autowired
	private UserManager userManager;
	@Autowired
	private MenuManager menuManager;
	/**
	 * 删除标准角色
	 */
	@Override
	public String delete() throws Exception {
		standardRoleManager.deleteStandardRole(id);
		return RELOAD;
	}
	
	/**
	 * 导出标准角色
	 */
	@Action("standard-role-exportRole")
	public String exportRole() throws Exception{
		HttpServletResponse response = ServletActionContext.getResponse();
		response.reset();
		response.setContentType("application/x-download");
		response.addHeader("Content-Disposition", "attachment;filename="+URLEncoder.encode("角色信息.xls","UTF-8"));
//		List<BusinessSystem> businessSystems = new ArrayList<BusinessSystem>();
		List<Menu> menus = new ArrayList<Menu>();
		boolean isBranchAdmin=false;//false表示安全管理员；true表示分支机构管理员
		if(roleManager.hasSecurityAdminRole(ContextUtils.getUserId())){
//			businessSystems = businessSystemManager.getAllBusiness();
			menus = menuManager.getAllEnabledStandardRootMenus();
		}else{
			menus = packagingSystemTree();
			isBranchAdmin=true;
		}
		ExportRole.exportRole(response.getOutputStream(), menus, isBranchAdmin);
		ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("acs.authorizationManagement"), 
				ApiFactory.getBussinessLogService().getI18nLogInfo("acs.exportRole"),ContextUtils.getSystemId("acs"));
		return null;
	}

	/**
	 * 分页显示标准角色
	 */
	@Override
	public String list() throws Exception {
		List<BusinessSystem> businessSystems = businessSystemManager.getAllBusiness();
		if(businessSystemId == null && businessSystems.size() > 0){
			businessSystemId = businessSystems.get(0).getId();
		}
		BusinessSystem bs=businessSystemManager.getBusiness(businessSystemId);
		Set<Role> roles = bs.getRoles();
		for(Role r : roles){
			if(r.isDeleted()) continue;
			//if("acsSystemAdmin".equals(r.getRoleCode())||"acsSecurityAdmin".equals(r.getRoleCode())|| //三个管理员不能重新分配用户
					//"acsAuditAdmin".equals(r.getRoleCode())) continue;
			if(roleId == null){
				roleId = r.getId();
				break;
			}else{
				break;
			}
		}
		ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("acs.authorizationManagement"), 
				ApiFactory.getBussinessLogService().getI18nLogInfo("acs.viewRoleAuthorityList"),ContextUtils.getSystemId("acs"));
		return SUCCESS;
	}
	@Action("standard-role-data")
	public String data(){
		return "standard-role-data";
	}
	
	/**
	 * 查看权限
	 * @return
	 */
	@Action("standard-role-viewAuthority")
	public String viewAuthority(){
		entity=roleManager.getRole(roleId);
		functions=roleManager.getFunctions(roleId);
		return "standard-role-viewAuthority";
	}
	/*
	 * 生成系统JSON树
	 */
	@Action("standard-role-tree")
	public String tree()throws Exception {
		String currentId = Struts2Utils.getParameter("currentId");
		if(currentId!=null&&currentId.startsWith("BUSINESSSYSTEM_")){
			this.renderText("[]");
			return null;
		}
		List<ZTreeNode> treeNodes = new ArrayList<ZTreeNode>();
		String result ="";
		boolean containBranch=departmentManager.containBranches();
		List<Menu> menus = new ArrayList<Menu>();
		if(roleManager.hasSecurityAdminRole(ContextUtils.getUserId())){
			menus = menuManager.getAllEnabledStandardRootMenus();
			if(businessSystemId == null && menus.size() > 0){
				businessSystemId = menus.get(0).getSystemId();
			}
			for(Menu menu : menus){
				List<Role> roles = standardRoleManager.getRolesBySystemId(menu.getSystemId());
				String nodeId="BUSINESSSYSTEM_" + menu.getSystemId();
				if(menu.getSystemId().equals(businessSystemId)){
					ZTreeNode root = new ZTreeNode(nodeId,"0", menuManager.getNameToi18n(menu.getName()), "true", "false", "", "", "folder", "");
					treeNodes.add(root);
					getRolesNodes(roles, true,containBranch,treeNodes,nodeId);
				}else{
					ZTreeNode root = new ZTreeNode(nodeId,"0",menuManager.getNameToi18n(menu.getName()), "false", "false", "", "", "folder", "");
					treeNodes.add(root);
					getRolesNodes(roles, false,containBranch,treeNodes,nodeId);
				}
			}
		}else if(roleManager.hasBranchAdminRole(ContextUtils.getUserId())){
			menus = packagingSystemTree();
			//所管理的分支机构
			List<BranchAuthority> branchAuthoritys=branchAuthorityManager.getBranchByUser(ContextUtils.getUserId());
			Set<Long> branchesSet=new HashSet<Long>();
			for(BranchAuthority ba:branchAuthoritys){
				branchesSet.add(ba.getBranchesId());
				getSubBranches(ba.getBranchesId(),branchesSet);
			}
			for(Menu menu : menus){
				List<Role> roles=roleManager.getRoleList(menu.getSystemId(),branchesSet);
				String nodeId="BUSINESSSYSTEM_" + menu.getSystemId();
				ZTreeNode root = new ZTreeNode(nodeId,"0",menuManager.getNameToi18n(menu.getName()), "false", "false", "", "", "folder", "");
				treeNodes.add(root);
				getRolesNodes(roles, false,containBranch,treeNodes,nodeId);
			}
		}
		
		if(roleId != null){
			users = manager.getCheckedUsersByRole(roleId);
			departments = departmentManager.getDepartmentsInRole(roleId);
			workgroups = manager.getCheckedWorkgroupByRole(roleId);
		}
		result = JsonParser.object2Json(treeNodes);
		renderText(result);
		return null;
	}
	
	private void getRolesNodes(List<Role> roles, boolean isOpen,boolean containBranch,List<ZTreeNode> treeNodes,String parentId){
		boolean isNull = true;
		for(Role r : roles){
			if(r.isDeleted()) continue;
			if(r.getCompanyId()!=null && !r.getCompanyId().equals(ContextUtils.getCompanyId())) continue;
			if(isNull && isOpen){
				if(roleId == null) roleId = r.getId();
				isNull = false;
			}
			if(!"acsBranchAdmin".equals(r.getCode())){
				if(containBranch){
					ZTreeNode root = new ZTreeNode("ROLE_"+r.getId().toString(),parentId,r.getName()+"("+(r.getSubCompanyId()==null?ContextUtils.getCompanyName():r.getSubCompanyName())+")", "false", "false", "", "", "folder", "");
					treeNodes.add(root);
				}else{
					ZTreeNode root = new ZTreeNode("ROLE_"+r.getId().toString(),parentId,r.getName(), "false", "false", "", "", "folder", "");
					treeNodes.add(root);
				}
			}
		}
	}
	
	private List<Menu> packagingSystemTree(){
		Set<Long> businessSystemSet=new HashSet<Long>();
		//所管理的分支机构
		List<BranchAuthority> branches=branchAuthorityManager.getBranchByUser(ContextUtils.getUserId());
		for(BranchAuthority b:branches){
			//所管理的分支机构-所拥有的角色和所属分支机构为“所管理的分支机构”的角色
			packagingBusinessSystem(b.getBranchesId(),businessSystemSet);
			
			Set<Long> branchesSet=new HashSet<Long>();
			//所管理的分支机构的子分支机构
			getSubBranches(b.getBranchesId(), branchesSet);
			for(Long branchesId:branchesSet){
				//所管理分支机构的子分支机构-所拥有的角色和所属分支机构为“所管理的分支机构的子分支机构”的角色
				packagingBusinessSystem(branchesId,businessSystemSet);
			}
		}
		List<Long> idsList = new ArrayList<Long>();
		idsList.addAll(businessSystemSet);
		return menuManager.getEnabledRootMenuBySystems(idsList);
	}
	
	private void packagingBusinessSystem(Long branchesId,Set<Long> businessSystemSet) {
		//分支机构-所拥有的角色
		List<BranchAuthority> roles=branchAuthorityManager.getRolesByBranch(branchesId);
		for(BranchAuthority ba:roles){
			Role r=roleManager.getRole(ba.getDataId());
			businessSystemSet.add(r.getBusinessSystem().getId());
		}
		//所属分支机构为“此分支机构”的角色
    	List<Role> roleList=roleManager.getRoleByBranches(branchesId);
		for(Role r:roleList){
			businessSystemSet.add(r.getBusinessSystem().getId());
		}
	}
	
	private void getSubBranches(Long departmentId, Set<Long> branchesSet) {
		List<Department> subDeptments=departmentManager.getSubDeptments(departmentId);
		for(Department d:subDeptments){
			if(d.getBranch()){
				branchesSet.add(d.getId());
			}
			getSubBranches(d.getId(), branchesSet);
		}
	}

	/**
	 * 角色分配用户/部门/工作组
	 * @return
	 */
	@Action("standard-role-authoritys")
	public String authoritys(){
		containBranches=departmentManager.containBranches();
		if(!roleManager.hasSecurityAdminRole(ContextUtils.getUserId())&&roleManager.hasBranchAdminRole(ContextUtils.getUserId())){
			List<BranchAuthority> branchesList=branchAuthorityManager.getBranchByUser(ContextUtils.getUserId());
			for(BranchAuthority branches:branchesList){
				if(StringUtils.isNotEmpty(manageBranchesIds)){
					manageBranchesIds+=",";
				}
				manageBranchesIds+=branches.getBranchesId();
			}
		}
		if(id != null){
			if(roleManager.hasAllUserRole(id)){
				User all_user=new User();
				all_user.setName(Struts2Utils.getText("authorization.allUsers"));
				all_user.setId(-1l);
				users.add(all_user);
			}
			users.addAll(manager.getCheckedUsersByRoleOld(id));
			if(roleManager.hasAllDeptRole(id)){
				Department all_dept=new Department();
				all_dept.setName(Struts2Utils.getText("authorization.allDepartment"));
				all_dept.setId(-1l);
				departments.add(all_dept);
			}
			departments.addAll(departmentManager.getDepartmentsInRole(id));
			if(roleManager.hasAllGroupRole(id)){
				Workgroup all_group=new Workgroup();
				all_group.setId(-1l);
				all_group.setName(Struts2Utils.getText("authorization.allGroups"));
				workgroups.add(all_group);
			}
			workgroups.addAll(manager.getCheckedWorkgroupByRole(id));
			Role role=standardRoleManager.getStandardRole(id);
			isAdminRole=hasAdminRole(role);
			isDefaultRole=isDefaultRole(role);
			defaultAdmin = getSystemDefaultAdmin(role);
		}else if(roleId != null){
			if(roleManager.hasAllUserRole(roleId)){
				User all_user=new User();
				all_user.setName(Struts2Utils.getText("authorization.allUsers"));
				all_user.setId(-1l);
				users.add(all_user);
			}
			users.addAll(manager.getCheckedUsersByRoleOld(roleId));
			if(roleManager.hasAllDeptRole(roleId)){
				Department all_dept=new Department();
				all_dept.setName(Struts2Utils.getText("authorization.allDepartment"));
				all_dept.setId(-1l);
				departments.add(all_dept);
			}
			departments.addAll(departmentManager.getDepartmentsInRole(roleId));
			if(roleManager.hasAllGroupRole(roleId)){
				Workgroup all_group=new Workgroup();
				all_group.setName(Struts2Utils.getText("authorization.allGroups"));
				all_group.setId(-1l);
				workgroups.add(all_group);
			}
			workgroups.addAll(manager.getCheckedWorkgroupByRole(roleId));
			Role role=standardRoleManager.getStandardRole(roleId);
			isAdminRole=hasAdminRole(role);
			isDefaultRole=isDefaultRole(role);
			defaultAdmin = getSystemDefaultAdmin(role);
		}
		return "standard-role-data";
	}
	
	
	private Boolean isDefaultRole(Role role) {
		String sysCode=role.getBusinessSystem().getCode();
		if(role.getCode().equals(sysCode+"_Everyone")){
			return true;
		}
		return false;
	}

	private List<String> getSystemDefaultAdmin(Role role) {
		List<String> result = new ArrayList<String>();
		Company company = companyManager.getCompany(ContextUtils.getCompanyId());
		if("acsSystemAdmin".equals(role.getCode())){
			String systemAdmin = company.getCode()+".systemAdmin";
			result.add(systemAdmin);
		}
		if("acsSecurityAdmin".equals(role.getCode())){
			String securityAdmin = company.getCode()+".securityAdmin";
			result.add(securityAdmin);
		}
		if("acsAuditAdmin".equals(role.getCode())){
			String auditAdmin = company.getCode()+".auditAdmin";
			result.add(auditAdmin);
		}
		return result;
	}

	private boolean hasAdminRole(Role role){
		if(ACS_SYSTEM_ADMIN.equals(role.getCode())||ACS_AUDIT_ADMIN.equals(role.getCode())||ACS_SECURITY_ADMIN.equals(role.getCode())){
			return true;
		}
		return false;
	}
	
	@Override
	protected void prepareModel() throws Exception {
		if(id != null){
			entity = standardRoleManager.getStandardRole(id);
		}else{
			entity = new Role();
		}
	}

	@Override
	public String input() throws Exception {
		return INPUT;
	}

	@Override
	public String save() throws Exception {
		return RELOAD;
	}

	public Role getModel() {
		return entity;
	}

	public Long getBusinessSystemId() {
		return businessSystemId;
	}

	public void setBusinessSystemId(Long businessSystemId) {
		this.businessSystemId = businessSystemId;
	}

	public Page<Role> getPage() {
		return page;
	}

	public void setPage(Page<Role> page) {
		this.page = page;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	@Required
	public void setDepartmentManager(DepartmentManager departmentManager) {
		this.departmentManager = departmentManager;
	}

	@Required
	public void setRoleManager(RoleManager manager) {
		this.manager = manager;
	}

	@Required
	public void setBusinessSystemManager(BusinessSystemManager businessSystemManager) {
		this.businessSystemManager = businessSystemManager;
	}

	public String getSystemTree() {
		return systemTree;
	}

	public void setSystemTree(String systemTree) {
		this.systemTree = systemTree;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public List<Department> getDepartments() {
		return departments;
	}

	public void setDepartments(List<Department> departments) {
		this.departments = departments;
	}

	public List<Workgroup> getWorkgroups() {
		return workgroups;
	}

	public void setWorkgroups(List<Workgroup> workgroups) {
		this.workgroups = workgroups;
	}

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public Boolean getIsAdminRole() {
		return isAdminRole;
	}

	public void setIsAdminRole(Boolean isAdminRole) {
		this.isAdminRole = isAdminRole;
	}

	public List<String> getDefaultAdmin() {
		return defaultAdmin;
	}

	public void setDefaultAdmin(List<String> defaultAdmin) {
		this.defaultAdmin = defaultAdmin;
	}
	
	public Boolean getContainBranches() {
		return containBranches;
	}
	public Role getEntity() {
		return entity;
	}
	public void setEntity(Role entity) {
		this.entity = entity;
	}
	public String getManageBranchesIds() {
		return manageBranchesIds;
	}
	public Integer getIsAddOrRomove() {
		return isAddOrRomove;
	}
	public void setIsAddOrRomove(Integer isAddOrRomove) {
		this.isAddOrRomove = isAddOrRomove;
	}
	public List<Function> getFunctions() {
		return functions;
	}
	public void setFunctions(List<Function> functions) {
		this.functions = functions;
	}

	public Boolean getIsDefaultRole() {
		return isDefaultRole;
	}

	public void setIsDefaultRole(Boolean isDefaultRole) {
		this.isDefaultRole = isDefaultRole;
	}
	
}
