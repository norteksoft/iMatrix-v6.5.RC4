package com.norteksoft.acs.web.authorization;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;

import com.norteksoft.acs.base.enumeration.BranchDataType;
import com.norteksoft.acs.base.web.struts2.CRUDActionSupport;
import com.norteksoft.acs.entity.authorization.BranchAuthority;
import com.norteksoft.acs.entity.authorization.BusinessSystem;
import com.norteksoft.acs.entity.authorization.Role;
import com.norteksoft.acs.entity.organization.Department;
import com.norteksoft.acs.entity.organization.User;
import com.norteksoft.acs.service.authorization.BranchAuthorityManager;
import com.norteksoft.acs.service.authorization.BusinessSystemManager;
import com.norteksoft.acs.service.authorization.RoleManager;
import com.norteksoft.acs.service.authorization.StandardRoleManager;
import com.norteksoft.acs.service.organization.DepartmentManager;
import com.norteksoft.acs.service.organization.UserManager;
import com.norteksoft.mms.module.entity.Menu;
import com.norteksoft.mms.module.service.MenuManager;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.JsonParser;
import com.norteksoft.product.util.tree.ZTreeNode;
import com.norteksoft.product.web.struts2.Struts2Utils;

/**
 * 分支机构授权管理
 * 
 */
@ParentPackage("default")
@Results( { @Result(name = CRUDActionSupport.RELOAD, location = "branch-authority", type="redirectAction") })
public class BranchAuthorityAction extends CRUDActionSupport<BranchAuthority>{
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private BranchAuthority branchAuthority;
	private List<User> users=new ArrayList<User>();
	private List<Role> roles=new ArrayList<Role>();
	private Long branchesId;//分支机构id
	private boolean selectPageFlag=false;
	private String roleIds;//角色id
	private String userIds;//人员id
	private String manageBranchesIds="";//被管理的分支机构id
	
	@Autowired
	private BranchAuthorityManager branchAuthorityManager;
	@Autowired
	private UserManager userManager;
	@Autowired
	private RoleManager roleManager;
	@Autowired
	private DepartmentManager departmentManager;
	@Autowired
	private BusinessSystemManager businessSystemManager;
	@Autowired
	private StandardRoleManager standardRoleManager;
	@Autowired
	private MenuManager menuManager;

	@Override
	@Action("branch-authority-clearAway")
	public String delete() throws Exception {
		int deleteNum=0;//删除角色的个数
		int noDeleteNum=0;//未删除角色的个数
		int deleteManagerNum=0;//删除管理员的个数
		if(StringUtils.isNotEmpty(roleIds)&&branchesId!=null){
			User user=userManager.getUserById(ContextUtils.getUserId());
			if(roleManager.hasSecurityAdminRole(user)){
				for(String roleId:roleIds.split(",")){
					deleteNum++;
					branchAuthorityManager.deleteRoleByBranchesId(branchesId,Long.valueOf(roleId));
				}
			}else if(roleManager.hasBranchAdminRole(user)){
				List<BranchAuthority> branchesList=branchAuthorityManager.getBranchByUser(ContextUtils.getUserId());
				Set<Role> roleSet=new HashSet<Role>();
				for(BranchAuthority branches:branchesList){
					List<BranchAuthority> roleList=branchAuthorityManager.getRolesByBranch(branches.getBranchesId());
					for(BranchAuthority role:roleList){
						Role r=roleManager.getRole(role.getDataId());
						roleSet.add(r);
					}
					packagingRole(branches.getBranchesId(),roleSet);
					//增加在角色管理中角色的所属分支机构为他的子分支机构的角色
					List<Department> subBranches=new ArrayList<Department>();
					getSubBranches(branches.getBranchesId(),subBranches);
					for(Department d:subBranches){
						packagingRole(d.getId(),roleSet);
					}
				}
				for(String roleId:roleIds.split(",")){
					for(Role role:roleSet){
						if(Long.valueOf(roleId).equals(role.getId())){
							deleteNum++;
							branchAuthorityManager.deleteRoleByBranchesId(branchesId,Long.valueOf(roleId));
						}
					}
				}
				noDeleteNum=roleIds.split(",").length-deleteNum;
			}
		}
		if(StringUtils.isNotEmpty(userIds)&&branchesId!=null){
			for(String userId:userIds.split(",")){
				List<BranchAuthority> branchesList=branchAuthorityManager.getBranchByUser(Long.valueOf(userId));
				if(branchesList.size()==1){
					Role role=roleManager.getRoleByCode("acsBranchAdmin");
					List<Long> uList=new ArrayList<Long>();
					uList.add(Long.valueOf(userId));
					roleManager.removeUDWFromRoel(role.getId(), uList, null, null);
				}
				deleteManagerNum++;
				branchAuthorityManager.deleteUserByBranchesId(branchesId,Long.valueOf(userId));
			}
		}
		this.renderText(Struts2Utils.getText("removeRoleInBranchInfo",new String[]{deleteManagerNum+"",deleteNum+"",noDeleteNum+""}));
		return null;
	}

	/**
	 * 分支机构授权管理列表
	 */
	@Override
	@Action("branch-authority")
	public String list() throws Exception {
		List<BranchAuthority> userList=branchAuthorityManager.getUsersByBranch(branchesId);
		for(BranchAuthority branch:userList){
			User u=userManager.getUserById(branch.getDataId());
			if(u!=null&&!u.isDeleted()){
				users.add(u);
			}
		}
		List<BranchAuthority> roleList=branchAuthorityManager.getRolesByBranch(branchesId);
		for(BranchAuthority branch:roleList){
			Role r=roleManager.getRole(branch.getDataId());
			if(r != null&&!r.isDeleted()){
				roles.add(r);
			}
		}
		User user=userManager.getUserById(ContextUtils.getUserId());
		if(!roleManager.hasSecurityAdminRole(user)&&roleManager.hasBranchAdminRole(user)){
			List<BranchAuthority> branchesList=branchAuthorityManager.getBranchByUser(ContextUtils.getUserId());
			for(BranchAuthority branches:branchesList){
				if(StringUtils.isNotEmpty(manageBranchesIds)){
					manageBranchesIds+=",";
				}
				manageBranchesIds+=branches.getBranchesId();
			}
		}
		return "branch-authority";
	}

	@Override
	protected void prepareModel() throws Exception {
		if(id==null){
			branchAuthority=new BranchAuthority();
		}else{
			branchAuthority=branchAuthorityManager.getBranchAuthority(id);
		}
	}

	@Override
	public String save() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public BranchAuthority getModel() {
		return branchAuthority;
	}
	
	/**
	 * 生成分支机构JSON树
	 */
	@Action("branches-tree")
	public String branchesTree(){
		List<ZTreeNode> treeNodes = new ArrayList<ZTreeNode>();
		String result ="";
		User user=userManager.getUserById(ContextUtils.getUserId());
		if(roleManager.hasSecurityAdminRole(user)){
			ZTreeNode root = new ZTreeNode("COMPANY_" + ContextUtils.getCompanyId(),"0",ContextUtils.getCompanyName(), "true", "false", "", "", "root", "");
			getSubBrancheTreeNodes(null,treeNodes);
			treeNodes.add(root);
		}else if(roleManager.hasBranchAdminRole(user)){
			List<BranchAuthority> branches=branchAuthorityManager.getBranchByUser(ContextUtils.getUserId());
			for(BranchAuthority branch:branches){
				Department d=departmentManager.getDepartment(branch.getBranchesId());
				String nodeId="BRANCHES_" + d.getId();
				ZTreeNode root = new ZTreeNode(nodeId,"0",d.getName(), "true", "false", "", "", "root", "");
				getSubBrancheTreeNodes(d.getId(),treeNodes);
				treeNodes.add(root);
			}
		}
		result = JsonParser.object2Json(treeNodes);
		renderText(result);
		return null;
	}
	
	private void getSubBrancheTreeNodes(Long departmentId,List<ZTreeNode> treeNodes) {
		addAllDeptTreeNode(departmentId,treeNodes);
	}
	/**
	 * 工作组管理左侧树中，获得所有需要加载的部门及分支
	 * @param departmentId
	 * @return List<List<Department>> 外层List表示以分支为准确定集合的个数，里层的 List<Department>存储的依次为当前分支、该分支对应的父部门、父父部门、...，即该分支对应的组织结构层次集合
	 */
	private List<List<Department>> getTreeNodeDept(Long departmentId){
		List<List<Department>> result = new ArrayList<List<Department>>();
		List<Department> branches = new ArrayList<Department>();
		if(departmentId==null){
			branches = departmentManager.getAllBranches();
		}else{
			branches = departmentManager.getSubBranchs(departmentId);
		}
		for(int i=0;i<branches.size();i++){
			List<Department> depts = new ArrayList<Department>();
			Department branch = branches.get(i);
			depts.add(branch);
			addParentNodeDept(branch,depts,departmentId);
			result.add(depts);
		}
		return result;
	}
	/**
	 * 工作组管理左侧树中，递归存储分支对应的组织结构层次集合
	 * @param dept
	 * @param depts
	 * @param departmentId
	 */
	private void addParentNodeDept(Department dept,List<Department> depts,Long departmentId){
		Department parentDept = dept.getParent();
		if(parentDept!=null){
			if(departmentId!=null&&parentDept.getId().equals(departmentId)){//表示是分支管理员时，树的顶层节点为该用户有权限的分支，并不是公司节点
				return;
			}
			depts.add(parentDept);
			addParentNodeDept(parentDept,depts,departmentId);
		}
	}
	
	/**
	 *分支授权管理左侧树中，拼接左侧树节点
	 * @param departmentId
	 * @param treeNodes
	 */
	private void addAllDeptTreeNode(Long departmentId,List<ZTreeNode> treeNodes){
		List<List<Department>> result =  getTreeNodeDept(departmentId);
		Set<Long> deptIds = new HashSet<Long>();
		for(int i=0;i<result.size();i++){
			List<Department> depts = result.get(i);
			if(depts.size()<0)continue;
			for(int j=depts.size()-1;j>=0;j--){//倒序取部门，因为最后一个元素是顶层节点
				Department dept = depts.get(j);
				if(!deptIds.contains(dept.getId())){
					deptIds.add(dept.getId());
					if(dept.getBranch()){
						String parentId=getParentNodeId(dept);
						String nodeId="BRANCHES_" + dept.getId();
						ZTreeNode root = new ZTreeNode(nodeId,parentId,dept.getName(), "true", "false", "", "", "root", "");
						treeNodes.add(root);
					}else{
						String parentId=getParentNodeId(dept);
						String nodeId="DEPARTMENT_" + dept.getId();
						ZTreeNode root = new ZTreeNode(nodeId,parentId,StringUtils.isNotEmpty(dept.getShortTitle())?dept.getShortTitle():dept.getName(), "true", "false", "", "", "department", "");
						treeNodes.add(root);
					}
				}
			}
		}
	}
	
	private String getParentNodeId(Department dept){
		Department parentDept = dept.getParent();
		if(parentDept==null){
			return "COMPANY_" + ContextUtils.getCompanyId();
		}else{
			if(parentDept.getBranch()){
				return "BRANCHES_" + parentDept.getId();
			}else{
				return "DEPARTMENT_" + parentDept.getId();
			}
		}
	}
	
	/**
	 * 添加角色
	 * @return
	 */
	@Action("branch-authority-addRole")
	public String addRole() {
		List<ZTreeNode> treeNodes = new ArrayList<ZTreeNode>();
		ZTreeNode node=null;
		if(!selectPageFlag){
			StringBuilder tree = new StringBuilder("[ ");
			User user=userManager.getUserById(ContextUtils.getUserId());
			if(roleManager.hasSecurityAdminRole(user)){
				List<Menu> menuList = menuManager.getAllEnabledStandardRootMenus();
				for(Menu menu : menuList){
					if(!"acs".equals(menu.getCode())){
						//tree.append(JsTreeUtils.generateJsTreeNodeNew("BUSINESSSYSTEM", "closed", bs.getName(), getRolesNodes(bs,treeNodes), ""));
						//tree.append(",");
						node = new ZTreeNode(menu.getSystemId().toString(),"0",menuManager.getNameToi18n(menu.getName()), "false", "false", "", "", "folder", "");
							treeNodes.add(node);
							getRolesNodes(menu,treeNodes);
					}
				}
			}else if(roleManager.hasBranchAdminRole(user)){
				List<BranchAuthority> branches=branchAuthorityManager.getBranchByUser(ContextUtils.getUserId());
				Set<Role> roleSet=new HashSet<Role>();
				for(BranchAuthority b:branches){
					List<BranchAuthority> roles=branchAuthorityManager.getRolesByBranch(b.getBranchesId());
					for(BranchAuthority ba:roles){
						Role r=roleManager.getRole(ba.getDataId());
						roleSet.add(r);
					}
					packagingRole(b.getBranchesId(),roleSet);
					//增加在角色管理中角色的所属分支机构为他的子分支机构的角色
					List<Department> subBranches=new ArrayList<Department>();
					getSubBranches(b.getBranchesId(),subBranches);
					for(Department d:subBranches){
						packagingRole(d.getId(),roleSet);
					}
				}
				Set<BusinessSystem> businessSystemSet=new HashSet<BusinessSystem>();
				for(Role r:roleSet){
					businessSystemSet.add(r.getBusinessSystem());
				}
				BusinessSystem[] sysArray=businessSystemSet.toArray(new BusinessSystem[businessSystemSet.size()]);
				for(int i=1;i<sysArray.length;i++){
					for(int j=0;j<sysArray.length-i;j++){
						if(sysArray[j].getId()>sysArray[j+1].getId()){
							BusinessSystem temp=sysArray[j];
							sysArray[j]=sysArray[j+1];
							sysArray[j+1]=temp;
						}
					}
				}
				for(BusinessSystem bs:sysArray){
					Menu menu = menuManager.getMenuByCode(bs.getCode());
					node = new ZTreeNode(bs.getId().toString(),"0",menuManager.getNameToi18n(menu.getName()), "false", "false", "", "", "folder", "");
					treeNodes.add(node);
					getRolesNodes(bs,roleSet,treeNodes);
				}
			}
			renderText(JsonParser.object2Json(treeNodes));
			return null;
		}
		return "branch-authority-addRole";
	}
	
	private void getRolesNodes(BusinessSystem bs,Set<Role> roleSet,List<ZTreeNode> treeNodes){
		List<Role> roleList = new ArrayList<Role>();
		ZTreeNode node=null;
		for(Role r : roleSet){
			if(r.isDeleted()) continue;
			if(r.getCompanyId()!=null && !r.getCompanyId().equals(ContextUtils.getCompanyId())) continue;
			if(bs.equals(r.getBusinessSystem())){
				roleList.add(r);
			}
		}
		Role[] roleArray=roleList.toArray(new Role[roleList.size()]);
		for(int i=1;i<roleArray.length;i++){
			for(int j=0;j<roleArray.length-i;j++){
				if(roleArray[j].getId()>roleArray[j+1].getId()){
					Role temp=roleArray[j];
					roleArray[j]=roleArray[j+1];
					roleArray[j+1]=temp;
				}
			}
		}
		for(Role r:roleArray){
			node = new ZTreeNode("ROLE_"+r.getId().toString(),bs.getId().toString(), r.getName()+"("+r.getSubCompanyName()+")", "false", "false", "", "", "folder", "");
			treeNodes.add(node);
		}
	}
	
	private void packagingRole(Long branchesId,Set<Role> roleSet){
		List<Role> roleList=roleManager.getRoleByBranches(branchesId);
		for(Role r:roleList){
			roleSet.add(r);
		}
	}
	
	/**
	 * 根据分支机构id获得此分支机构的所有子分支机构
	 * @param branchesId
	 * @param subBranches
	 */
	private void getSubBranches(Long departmentId, List<Department> subBranches) {
		List<Department> subDeptments=departmentManager.getSubDeptments(departmentId);
		for(Department d:subDeptments){
			if(d.getBranch()){
				subBranches.add(d);
			}
			getSubBranches(d.getId(), subBranches);
		}
	}

	private void getRolesNodes(Menu menu,List<ZTreeNode> treeNodes){
		List<Role> roles = standardRoleManager.getRolesBySystemId(menu.getSystemId());
		ZTreeNode node=null;
		for(Role r : roles){
			if(r.isDeleted()) continue;
			if(r.getCompanyId()!=null && !r.getCompanyId().equals(ContextUtils.getCompanyId())) continue;
			if(!r.getName().equals("普通用户")){
				node = new ZTreeNode("ROLE_"+r.getId().toString(),menu.getSystemId().toString(), r.getName()+"("+r.getSubCompanyName()+")", "false", "false", "", "", "folder", "");
				treeNodes.add(node);
			}
		}
	}
	
	/**
	 * 添加管理员
	 * @return
	 */
	@Action("branch-authority-addManager")
	public String addManager() {
		if("ALLCOMPANYID".equals(userIds)){
			this.renderText("ALLCOMPANYID");
		}else{
//			User user=userManager.getUserById(ContextUtils.getUserId());
			List<Long> userIdList=new ArrayList<Long>();
//			if(roleManager.hasSecurityAdminRole(user)){
				String[] ids=userIds.split(",");
				for(String str:ids){
					userIdList.add(Long.valueOf(str));
				}
//			}else if(roleManager.hasBranchAdminRole(user)){
//				userIdList=ApiFactory.getAcsService().getTreeUserIds(userIds);
//			}
			String result=validateAddManager(userIdList,branchesId);
			if(StringUtils.isNotEmpty(result)){
				this.renderText(result);
			}else{
				for(Long userId:userIdList){
					branchAuthority=branchAuthorityManager.getBranchAuthorityUser(branchesId,userId);
					if(branchAuthority == null){//表示此分支机构下没有该人员，那就保存，否则不保存
						branchAuthority=new BranchAuthority();
						branchAuthority.setBranchesId(branchesId);
						branchAuthority.setDataId(userId);
						branchAuthority.setBranchDataType(BranchDataType.USER);
						branchAuthority.setCompanyId(ContextUtils.getCompanyId());
						branchAuthorityManager.saveBranchAuthority(branchAuthority);
						Role role=roleManager.getRoleByCode("acsBranchAdmin");
						List<Long> uList=new ArrayList<Long>();
						uList.add(userId);
						roleManager.roleAddUsers(role, uList, new ArrayList<Long>(), new ArrayList<Long>(), null);
					}
				}	
				this.renderText("ok");
			}
		}
		return null;
	}
	private String validateAddManager(List<Long> userIdList,Long branchId){
		List<Department> subBranches=new ArrayList<Department>();
		getSubBranches(branchId, subBranches);
		String result="";
		for(Department dept:subBranches){
			for(Long userId:userIdList){
				BranchAuthority ba=branchAuthorityManager.getBranchAuthorityUser(dept.getId(), userId);
				if(ba!=null){//此用户已经是此分支机构下的管理员
					User u=userManager.getUserById(userId);
					result=u.getName();
					break;
				}
			}
			if(StringUtils.isNotEmpty(result)){
				result="用户 "+result+" 已经是 "+dept.getName()+" 下的管理员";
				break;
			}
		}
		if(StringUtils.isEmpty(result)){
			Department dept=departmentManager.getDepartment(branchId);
			while(dept.getParent()!=null){
				dept=dept.getParent();
				if(dept.getBranch()){
					for(Long userId:userIdList){
						BranchAuthority ba=branchAuthorityManager.getBranchAuthorityUser(dept.getId(), userId);
						if(ba!=null){//此用户已经是此分支机构下的管理员
							User u=userManager.getUserById(userId);
							result=u.getName();
							break;
						}
					}
				}
				if(StringUtils.isNotEmpty(result)){
					result="用户 "+result+" 已经是 "+dept.getName()+" 下的管理员";
					break;
				}
			}
		}
		return result;
	}
	
	/**
	 * 验证添加移除权限
	 * @return
	 */
	@Action("branch-authority-validateAuthority")
	public String validateAuthority() {
		User user=userManager.getUserById(ContextUtils.getUserId());
		if(roleManager.hasSecurityAdminRole(user)){
			this.renderText("ok");
		}else if(roleManager.hasBranchAdminRole(user)){
			branchAuthority=branchAuthorityManager.getBranchAuthorityUser(branchesId,ContextUtils.getUserId());
			if(branchAuthority != null){
				this.renderText("no");
			}else{
				this.renderText("ok");
			}
		}
		return null;
	}
	
	/**
	 * 保存角色
	 * @return
	 */
	@Action("branch-authority-saveRole")
	public String saveRole(){
		String[] ids=roleIds.split(",");
		for(String str:ids){
			branchAuthority=branchAuthorityManager.getBranchAuthority(branchesId,Long.valueOf(str));
			if(branchAuthority == null){//表示此分支机构下没有该角色，那就保存，否则不保存
				branchAuthority=new BranchAuthority();
				branchAuthority.setBranchesId(branchesId);
				branchAuthority.setDataId(Long.valueOf(str));
				branchAuthority.setBranchDataType(BranchDataType.ROLE);
				branchAuthority.setCompanyId(ContextUtils.getCompanyId());
				branchAuthorityManager.saveBranchAuthority(branchAuthority);
			}
		}
		this.renderText("ok");
		return null;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	public Long getBranchesId() {
		return branchesId;
	}

	public void setBranchesId(Long branchesId) {
		this.branchesId = branchesId;
	}

	public boolean isSelectPageFlag() {
		return selectPageFlag;
	}

	public void setSelectPageFlag(boolean selectPageFlag) {
		this.selectPageFlag = selectPageFlag;
	}

	public String getRoleIds() {
		return roleIds;
	}

	public void setRoleIds(String roleIds) {
		this.roleIds = roleIds;
	}

	public String getUserIds() {
		return userIds;
	}

	public void setUserIds(String userIds) {
		this.userIds = userIds;
	}

	public String getManageBranchesIds() {
		return manageBranchesIds;
	}
	
}
