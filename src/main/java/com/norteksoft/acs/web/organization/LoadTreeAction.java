package com.norteksoft.acs.web.organization;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.norteksoft.acs.base.enumeration.TreeType;
import com.norteksoft.acs.base.web.struts2.CRUDActionSupport;
import com.norteksoft.acs.entity.authorization.BranchAuthority;
import com.norteksoft.acs.entity.organization.Company;
import com.norteksoft.acs.entity.organization.Department;
import com.norteksoft.acs.entity.organization.User;
import com.norteksoft.acs.entity.organization.Workgroup;
import com.norteksoft.acs.service.authorization.BranchAuthorityManager;
import com.norteksoft.acs.service.authorization.RoleManager;
import com.norteksoft.acs.service.organization.CompanyManager;
import com.norteksoft.acs.service.organization.DepartmentManager;
import com.norteksoft.acs.service.organization.UserManager;
import com.norteksoft.acs.service.organization.WorkGroupManager;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.JsonParser;
import com.norteksoft.product.util.TreeUtils;
import com.norteksoft.product.util.tree.ZTreeNode;
import com.norteksoft.tags.tree.DepartmentDisplayType;


@ParentPackage("default")
public class LoadTreeAction extends CRUDActionSupport<Company> {
	private static final long serialVersionUID = 1L;
	private CompanyManager companyManager;
	private DepartmentManager departmentManager;
	private WorkGroupManager workGroupManager;
	private RoleManager roleManager;
	private String currentId;
	private String treeNodeId;//树节点id
	private String treeType;
	private boolean systemAdminable = false;//是否是系统管理员
	
	//"-"--->"|#"
	private static String SPLIT_ONE="|*";
	//"="--->"=="
	private static String SPLIT_TWO="==";
	//"~"--->"*#"
	private static String SPLIT_THREE="**";
	
	@Autowired
	private BranchAuthorityManager branchAuthorityManager;
	@Autowired
	private UserManager userManager;
	
	/**
	 * 工作组管理左侧树
	 * @return
	 */
	@Action("load-tree-loadWorkgroupTree")
	public String loadWorkgroupTree(){
		List<ZTreeNode> treeNodes = new ArrayList<ZTreeNode>();
		String result ="";
		User user=userManager.getUserById(ContextUtils.getUserId());
		if(roleManager.hasSystemAdminRole(user)){//当前用户是系统管理员
			ZTreeNode root = new ZTreeNode("COMPANY-" + ContextUtils.getCompanyId(),"0",ContextUtils.getCompanyName(), "true", "false", "", "", "root", "");
			treeNodes.add(root);
			getSubBranches(null,treeNodes,"COMPANY-" + ContextUtils.getCompanyId());
		}else if(roleManager.hasBranchAdminRole(user)){//当前用户是分支管理员
			StringBuilder nodes = new StringBuilder();
			List<BranchAuthority> branches=branchAuthorityManager.getBranchByUser(ContextUtils.getUserId());
			for(BranchAuthority branch:branches){
				if(StringUtils.isNotEmpty(nodes.toString())){
					nodes.append(",");
				}
				Department d=departmentManager.getDepartment(branch.getBranchesId());
				String nodeId="BRANCHES-" + d.getId();
				ZTreeNode root = new ZTreeNode(nodeId,"0",d.getName(), "true", "false", "", "", "root", "");
				treeNodes.add(root);
				getSubBranches(d.getId(),treeNodes,nodeId);
			}
		}
		result = JsonParser.object2Json(treeNodes);
		renderText(result);
		return null;
	}
	
	private void getSubBranches(Long departmentId,List<ZTreeNode> treeNodes,String parentId) {
		if(departmentId==null){
			getWorkGroupNodes(null,treeNodes,parentId);
		}else{
			Department d=departmentManager.getDepartment(departmentId);
			if(d.getBranch()){
				getWorkGroupNodes(departmentId,treeNodes,parentId);
			}
		}
		addGroupTreeNode(departmentId,treeNodes);
	}
	/**
	 * 工作组管理左侧树中，获得所有需要加载的部门及分支
	 * @param departmentId
	 * @return List<List<Department>> 外层List表示以分支为准确定集合的个数，里层的 List<Department>存储的依次为当前分支、该分支对应的父部门、父父部门、...，即该分支对应的组织结构层次集合
	 */
	private List<List<Department>> getGroupTreeNodeDept(Long departmentId){
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
	 * 工作组管理左侧树中，拼接左侧树节点
	 * @param departmentId
	 * @param treeNodes
	 */
	private void addGroupTreeNode(Long departmentId,List<ZTreeNode> treeNodes){
		List<List<Department>> result =  getGroupTreeNodeDept(departmentId);
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
						String nodeId="BRANCHES-" + dept.getId();
						ZTreeNode root = new ZTreeNode(nodeId,parentId,dept.getName(), "true", "false", "", "", "root", "");
						treeNodes.add(root);
						getWorkGroupNodes(dept.getId(),treeNodes,nodeId);
					}else{
						String parentId=getParentNodeId(dept);
						String nodeId="DEPARTMENT-" + dept.getId();
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
			return "COMPANY-" + ContextUtils.getCompanyId();
		}else{
			if(parentDept.getBranch()){
				return "BRANCHES-" + parentDept.getId();
			}else{
				return "DEPARTMENT-" + parentDept.getId();
			}
		}
	}

	public void getWorkGroupNodes(Long branchesId,List<ZTreeNode> treeNodes,String parentId){
		List<Workgroup> workGroups = workGroupManager.queryWorkGroupByBranches(branchesId);
		for(Workgroup wg: workGroups){
			if(wg.isDeleted()) continue;
			ZTreeNode root = new ZTreeNode("USERSBYWORKGROUP-"+wg.getId().toString(),parentId,wg.getName(), "false", "false", "", "", "workgroup", "");
			treeNodes.add(root);
		}
	}
	
	/**
	 * 用户管理或部门管理左侧树生成，以公司为根节点的树
	 * @return
	 */
	@Action("load-tree-loadDepartmentTree")
	public String loadDepartmentTree(){
		List<ZTreeNode> treeNodes = new ArrayList<ZTreeNode>();
		String result ="";
		if(currentId == null || currentId.trim().length() <= 0) return null;
		//初始化时显示公司根节点和工作站根节点
		Company company = companyManager.getCompany(ContextUtils.getCompanyId());
		boolean hasBranch = roleManager.hasBranchAdminRole(ContextUtils.getUserId());
		if("INITIALIZED".equals(currentId)){//部门管理左侧树
			boolean systemAdminable = roleManager.hasSystemAdminRole(ContextUtils.getUserId());
			if(systemAdminable){//如果是系统管理员且不是分支管理员
//				sb.append(JsTreeUtils.generateJsTreeNodeNew("DEPARTMENTS-" + company.getId(), "open", company.getName(), getDepartmentNodes(company.getId()), "company"));
				getDepartmentNodes(company.getId(),treeNodes,"DEPARTMENTS-" + company.getId());
			}else if(hasBranch){//如果不是系统管理员且是分支管理员
				generateBranchTree(treeNodes);
			}
		}else if("INITIALIZED_USERS".equals(currentId)){//用户管理左侧树
			boolean adminable = roleManager.hasAdminRole(ContextUtils.getUserId());
			if(adminable&&!hasBranch){//如果是管理员且不是分支管理员
				generateAdminTree(company,"true",treeNodes);
			}else if(adminable&&hasBranch){//如果是管理员且是分支管理员
				generateAdminTree(company,"false",treeNodes);
				generateBranchUserTree(treeNodes);
			}else if(!adminable&&hasBranch){//如果不是管理员且是分支管理员
				generateBranchUserTree(treeNodes);
			}
		}
		result = JsonParser.object2Json(treeNodes);
		renderText(result);
		return null;
	}
	/**
	 * 用户管理左侧树完整的组织结构树
	 * @param company
	 * @param state
	 * @param treeNodes
	 */
	private void generateAdminTree(Company company,String state,List<ZTreeNode> treeNodes){
		if(treeNodeId.equals("INITIALIZED_USERS")){//初始化部门树
			String nodeId="DEPARTMENTS-" + company.getId();
			ZTreeNode root = new ZTreeNode(nodeId,"0",company.getName(), state, "false", "", "", "root", "");
			List<ZTreeNode> children=new ArrayList<ZTreeNode>();
			getDepartmentNodesUser(company,children);
			ZTreeNode nodepartmentUser = new ZTreeNode("NODEPARTMENT_USER-"+ company.getId(),nodeId,getText("user.noDepartment"), "false", "false", "", "", "department", "");
			children.add(nodepartmentUser);
			ZTreeNode deletedUser = new ZTreeNode("DELETED_USER-" + company.getId(),nodeId,getText("common.userDelete"), "false", "false", "", "", "department", "");
			children.add(deletedUser);
			root.setChildren(children);
			treeNodes.add(root);
		}else if(isBranchOrDeptNodeForAdminTree()){//"USERSBYBRANCH-":"USERSBYDEPARTMENT-",表示是部门节点或分支节点
			addNodeInDepartmentForUser(treeNodes,null);
		}
	}
	/**
	 * 用户管理左侧树完整的组织结构树中是否是部门节点或分支节点
	 * @return
	 */
	private boolean isBranchOrDeptNodeForAdminTree(){
		//USERSBYBRANCH-deptId或USERSBYDEPARTMENT-deptId
		String[] arr = treeNodeId.split("-");
		return arr.length==2&&(treeNodeId.indexOf("USERSBYBRANCH-")>=0 ||treeNodeId.indexOf("USERSBYDEPARTMENT-")>=0);
	}
	
	/**
	 * 用户管理左侧树生成公司的子公司及部门的树
	 * @param companyId
	 */
	public void getDepartmentNodesUser(Company company,List<ZTreeNode> treeNodes){
		for(Company comp : company.getChildren()){
			ZTreeNode root = new ZTreeNode("DEPARTMENTS-"+comp.getId().toString(),"",comp.getName(), "false", "false", "", "", "root", "");
			treeNodes.add(root);
		}
		addAllRootDepartmentNode(treeNodes);
	}
	
	/**
	 *	用户管理 左侧树添加父部门节点（即根部门节点）
	 * @param treeNodes
	 */
	private void addAllRootDepartmentNode(List<ZTreeNode> treeNodes){
		List<Department> rootDepts = departmentManager.getDepartments();
		addDepartmentNode(rootDepts,treeNodes,"",null);
	}
	
	/**
	 * 用户管理左侧树添加部门节点
	 * @param depts
	 * @param treeNodes
	 * @param parentNodeId
	 * @param partBranchTreeNodeId 部门管理中当是分支树时，部门节点和分支节点id会多BRANCH-，例如：id为USERSBYBRANCH-BRANCH-deptId
	 */
	private void addDepartmentNode(List<Department> depts,List<ZTreeNode> treeNodes,String parentNodeId,String partBranchTreeNodeId){
		for(Department dept:depts){
			//部门树节点
			String partNodeId = dept.getBranch()?"USERSBYBRANCH-":"USERSBYDEPARTMENT-";
			if(StringUtils.isNotEmpty(partBranchTreeNodeId)){
				partNodeId = partNodeId + partBranchTreeNodeId;
			}
			String nodeId=partNodeId+dept.getId();
			ZTreeNode root = new ZTreeNode(nodeId,parentNodeId,dept.getName(), "false", isParentDeptNode(dept)+"", "", "", dept.getBranch()?"root":"department", "");
			treeNodes.add(root);
		}
	}
	/**
	 * 用户管理左侧树添加子部门节点、无部门节点和已删除节点
	 * @param parentDept
	 */
	private void addNodeInDepartment(Department parentDept,List<ZTreeNode> treeNodes,String partNodeId){
		List<Department> departments = departmentManager.getSubDepartmentList(parentDept.getId());
		addDepartmentNode(departments,treeNodes,treeNodeId,partNodeId);
		//无部门节点和已删除节点
		//判断是否是分支机构，如果是则拼无部门节点和已删除节点
		if(parentDept.getBranch()){
			ZTreeNode root1 = new ZTreeNode("BRANCH_NODEPARTMENT_USER-"+ parentDept.getId(),"",getText("user.noDepartment"), "false", "false", "", "", "department", "");
			treeNodes.add(root1);
			ZTreeNode root2 = new ZTreeNode("BRANCH_DELETED_USER-" + parentDept.getId(),"",getText("common.userDelete"), "false", "false", "", "", "department", "");
			treeNodes.add(root2);
		}
	}
	/**
	 * 是否有子部门
	 * @param parentDeptId
	 * @return true表示有
	 */
	private boolean isHasSubDept(Long parentDeptId){
		Long count = departmentManager.getSubDepartmentCount(parentDeptId);
		if(count>0)return true;
		return false;
	}
	/**
	 * 用户管理左侧树中，判断部门节点是否是父节点
	 * @param dept
	 * @return true表示有
	 */
	private boolean isParentDeptNode(Department dept){
		if(dept.getBranch())return true;//分支时永远作为父节点，因为
		Long count = departmentManager.getSubDepartmentCount(dept.getId());
		if(count>0)return true;
		return false;
	}
	/**
	 * 用户管理左侧树中加载有权限的分支树
	 * @param treeNodes
	 */
	
	private void generateBranchUserTree(List<ZTreeNode> treeNodes){
		List<BranchAuthority> branchAuthoritys = branchAuthorityManager.getBranchByUser(ContextUtils.getUserId());
		if(treeNodeId.equals("INITIALIZED_USERS")){//初始化部门树
			int i=0;
			for(BranchAuthority b : branchAuthoritys ){
				Department d = departmentManager.getDepartment(b.getBranchesId());
				if(i==0){
					addFirstAuthBranchNodes(d, treeNodes);
				}else{
					addOtherAuthBranchNodes(d, treeNodes);
				}
				i++;
			}
		}else if(isBranchOrDeptNodeForBranchTree()){//USERSBYBRANCH-BRANCH-deptId或USERSBYDEPARTMENT-BRANCH-deptId,表示是部门节点或分支节点
			addNodeInDepartmentForUser(treeNodes,"BRANCH-");
		}
	}
	
	/**
	 * 用户管理左侧树有权限的分支树中是否是部门节点或分支节点
	 * @return
	 */
	private boolean isBranchOrDeptNodeForBranchTree(){
		//USERSBYBRANCH-BRANCH-deptId或USERSBYDEPARTMENT-BRANCH-deptId
		String[] arr = treeNodeId.split("-");
		return arr.length==3&&(treeNodeId.indexOf("USERSBYBRANCH-")>=0 ||treeNodeId.indexOf("USERSBYDEPARTMENT-")>=0);
	}
	
	/**
	 * 用户管理左侧树添加第一个有权限的分支树
	 * @param dept
	 * @param treeNodes
	 */
	private void addFirstAuthBranchNodes(Department dept,List<ZTreeNode> treeNodes){
		//部门树节点
		String nodeId=(dept.getBranch()?"USERSBYBRANCH-BRANCH-":"USERSBYDEPARTMENT-BRANCH-")+dept.getId();
		ZTreeNode root = new ZTreeNode(nodeId,"",dept.getName(), "true", isParentDeptNode(dept)+"", "", "", dept.getBranch()?"root":"department", "");
		List<ZTreeNode> children=new ArrayList<ZTreeNode>();
		//添加子部门节点
		List<Department> departments = departmentManager.getSubDepartmentList(dept.getId());
		addDepartmentNode(departments, children, nodeId,"BRANCH-");
		//无部门节点和已删除节点
		//判断是否是分支机构，如果是则拼无部门节点和已删除节点
	    if(dept.getBranch()){
	    	ZTreeNode root1 = new ZTreeNode("BRANCH_NODEPARTMENT_USER-"+ dept.getId(),"",getText("user.noDepartment"), "false", "false", "", "", "department", "");
	    	children.add(root1);
			ZTreeNode root2 = new ZTreeNode("BRANCH_DELETED_USER-" + dept.getId(),"",getText("common.userDelete"), "false", "false", "", "", "department", "");
			children.add(root2);
	    }
	    root.setChildren(children);
	    treeNodes.add(root);
	}
	/**
	 * 用户管理左侧树添加非第一个有权限的分支树
	 */
	private void addOtherAuthBranchNodes(Department dept,List<ZTreeNode> treeNodes){
		//部门树节点
		String nodeId=(dept.getBranch()?"USERSBYBRANCH-BRANCH-":"USERSBYDEPARTMENT-BRANCH-")+dept.getId();
		ZTreeNode root = new ZTreeNode(nodeId,"",dept.getName(), "false", isParentDeptNode(dept)+"", "", "", dept.getBranch()?"root":"department", "");
		treeNodes.add(root);
	}
	
	/**
	 * 用户管理左侧树选中部门或分支节点后，动态加载该节点的子节点
	 */
	private void addNodeInDepartmentForUser(List<ZTreeNode> treeNodes,String partNodeId){
		Long deptId = Long.parseLong(treeNodeId.substring(treeNodeId.lastIndexOf("-")+1));
		Department dept = departmentManager.getDepartment(deptId);
		addNodeInDepartment(dept,treeNodes,partNodeId);
	}
	/**
	 * 部门管理左侧树有权限的分支树的生成
	 * @param treeNodes
	 */
	private void generateBranchTree(List<ZTreeNode> treeNodes){
		if(treeNodeId.equals("INITIALIZED")){//初始化部门树
			List<BranchAuthority> branchAuthoritys = branchAuthorityManager.getBranchByUser(ContextUtils.getUserId());
			int i=0;
			for(BranchAuthority b : branchAuthoritys ){
				Department d = departmentManager.getDepartment(b.getBranchesId());
				if(i==0){
					addFirstAuthBranchNodesForDept(d, treeNodes);
				}else{
					addOtherAuthBranchNodesForDept(d, treeNodes);	
				}
				i++;
			}
		}else if(treeNodeId.indexOf("USERSBYBRANCH-")>=0 || treeNodeId.indexOf("USERSBYDEPARTMENT-")>=0  ){//表示是部门节点或分支节点
			addNodeInDepartmentForDept(treeNodes);
		}
	}
	
	/**
	 * 部门管理左侧树完整组织机构生成公司的子公司及部门的树
	 * @param companyId
	 */
	public void getDepartmentNodes(Long companyId,List<ZTreeNode> treeNodes,String parentId){
		if(treeNodeId.equals("INITIALIZED")){//初始化部门树
			Company company = companyManager.getCompany(companyId);
			ZTreeNode root = new ZTreeNode("DEPARTMENTS-" + company.getId(),"0",company.getName(), "true", "false", "", "", "department", "");
			treeNodes.add(root);
			for(Company comp : company.getChildren()){
				root = new ZTreeNode("DEPARTMENTS-"+comp.getId().toString(),parentId,comp.getName(), "false", "false", "", "", "root", "");
				treeNodes.add(root);
			}
			addAllRootDeptNodes(treeNodes,parentId);
		}else if(treeNodeId.indexOf("USERSBYBRANCH-")>=0 || treeNodeId.indexOf("USERSBYDEPARTMENT-")>=0  ){//表示是部门节点或分支节点
			addNodeInDepartmentForDept(treeNodes);
		}
	}
	
	/**
	 * 部门管理左侧树添加第一个有权限的分支树
	 * @param dept
	 * @param treeNodes
	 */
	private void addFirstAuthBranchNodesForDept(Department dept,List<ZTreeNode> treeNodes){
		//部门树节点
		String nodeId = (dept.getBranch()?"USERSBYBRANCH-":"USERSBYDEPARTMENT-")+dept.getId()+"="+dept.getSubCompanyId();
		ZTreeNode root = new ZTreeNode(nodeId,"0",dept.getName(), "true", isHasSubDept(dept.getId())+"", "", "", dept.getBranch()?"root":"department", "");
		List<ZTreeNode> children=new ArrayList<ZTreeNode>();
		//添加子部门节点
		List<Department> departments = departmentManager.getSubDepartmentList(dept.getId());
		addDepartmentsNodes(departments,children,nodeId);
	    root.setChildren(children);
	    treeNodes.add(root);
	}
	/**
	 * 部门管理左侧树添加非第一个有权限的分支树
	 */
	private void addOtherAuthBranchNodesForDept(Department dept,List<ZTreeNode> treeNodes){
		//部门树节点
		String nodeId = (dept.getBranch()?"USERSBYBRANCH-":"USERSBYDEPARTMENT-")+dept.getId()+"="+dept.getSubCompanyId();
		ZTreeNode root = new ZTreeNode(nodeId,"0",dept.getName(), "false", isHasSubDept(dept.getId())+"", "", "", dept.getBranch()?"root":"department", "");
		treeNodes.add(root);
	}
	
	/**
	 * 部门管理左侧树添加所有根部门节点（即无父部门的部门节点）
	 */
	private void addAllRootDeptNodes(List<ZTreeNode> treeNodes,String parentId){
		List<Department> rootDepts = departmentManager.getDepartments();
		addDepartmentsNodes(rootDepts,treeNodes,parentId);
	}
	/**
	 * 部门管理左侧树添加部门节点或分支节点
	 */
	private void addDepartmentsNodes(List<Department> depts,List<ZTreeNode> treeNodes,String parentId){
		for(Department dept:depts){
			addDeptNodeForDept(dept,treeNodes,parentId);
		}
	}
	/**
	 * 部门管理左侧树单个部门节点的添加
	 * @param dept
	 * @param treeNodes
	 * @param parentId
	 */
	private void addDeptNodeForDept(Department dept,List<ZTreeNode> treeNodes,String parentId){
		ZTreeNode root = new ZTreeNode((dept.getBranch()?"USERSBYBRANCH-":"USERSBYDEPARTMENT-")+dept.getId()+"="+dept.getSubCompanyId(),parentId,dept.getName(), "false", isHasSubDept(dept.getId())+"", "", "",  dept.getBranch()?"root":"department", "");
		treeNodes.add(root);
	}
	
	/**
	 * 部门管理左侧树选中部门或分支节点后，动态加载该节点的子节点
	 */
	private void addNodeInDepartmentForDept(List<ZTreeNode> treeNodes){
		Long deptId = Long.parseLong(treeNodeId.substring(treeNodeId.indexOf("-")+1,treeNodeId.indexOf("=")));
		List<Department> departments = departmentManager.getSubDepartmentList(deptId);
		addDepartmentsNodes(departments,treeNodes,treeNodeId);
	}
	
	/**
	 * 用户管理中选择正职部门和兼职部门的部门树
	 * @return
	 */
	@Action("load-tree-loadDepartment")
	public String loadDepartment(){
		boolean hasBranch = roleManager.hasBranchAdminRole(ContextUtils.getUserId());
		boolean adminable = roleManager.hasSystemAdminRole(ContextUtils.getUserId());
		List<ZTreeNode> treeNodes = new ArrayList<ZTreeNode>();
		
		if(currentId == null || currentId.trim().length() <= 0) return null;
		//初始化时显示公司根节点和工作站根节点
		Company company = companyManager.getCompany(ContextUtils.getCompanyId());
		if(adminable){//如果是系统管理员且不是分支管理员
			loadDepartmentGenerateAdminTree(company,treeNodes);
		}else if(hasBranch){//如果不是管理员且是分支管理员
			loadDepartmentGenerateBranchTree(treeNodes);
		}
		this.renderText(JsonParser.object2Json(treeNodes));
		return null;
	}
	/**
	 * 如果是系统管理员且不是分支管理员，加载完整组织结构树
	 * @param company
	 * @param treeNodes
	 */
	private void loadDepartmentGenerateAdminTree(Company company,List<ZTreeNode> treeNodes){
		List<ZTreeNode> children = new ArrayList<ZTreeNode>();
		if("INITIALIZED".equals(currentId)){
			ZTreeNode root = new ZTreeNode("DEPARTMENTS"+SPLIT_ONE+ company.getId()+SPLIT_TWO+company.getName(),"0",company.getName(), "true", "false", "", "", "root", "");
			getDepartmentNodes2(company.getId(),children);
			root.setChildren(children);
			treeNodes.add(root);
		}else if(currentId.indexOf("USERSBYBRANCH"+SPLIT_ONE)>=0 || currentId.indexOf("USERSBYDEPARTMENT"+SPLIT_ONE)>=0){
			Long deptId = Long.parseLong(currentId.substring(currentId.indexOf(SPLIT_ONE)+2,currentId.indexOf(SPLIT_TWO)));
			addSubDeptsForChooseDept(deptId,treeNodes);
		}
	}
	/**
	 * 如果不是管理员且是分支管理员，加载有权限的分支结构树
	 * @param treeNodes
	 */
	private void loadDepartmentGenerateBranchTree(List<ZTreeNode> treeNodes){
		if("INITIALIZED".equals(currentId)){
			List<BranchAuthority> branchAuthoritys = branchAuthorityManager.getBranchByUser(ContextUtils.getUserId());
			int i=0;
			for(BranchAuthority b : branchAuthoritys ){
				Department d = departmentManager.getDepartment(b.getBranchesId());
				if(i==0){
					addFirstAuthBrachDeptsForChooseDept(d,treeNodes);
				}else{
					addOtherAuthBrachDeptsForChooseDept(d,treeNodes);
				}
				i++;
			}
		}else if(currentId.indexOf("USERSBYBRANCH"+SPLIT_ONE)>=0 || currentId.indexOf("USERSBYDEPARTMENT"+SPLIT_ONE)>=0){
			Long deptId = Long.parseLong(currentId.substring(currentId.indexOf(SPLIT_ONE)+2,currentId.indexOf(SPLIT_TWO)));
			addSubDeptsForChooseDept(deptId,treeNodes);
		}
		
	}
	
	private void getDepartmentNodes2(Long companyId,List<ZTreeNode> treeNodes){
		Company company = companyManager.getCompany(companyId);
		for(Company comp : company.getChildren()){
			ZTreeNode root = new ZTreeNode("DEPARTMENTS"+SPLIT_ONE+comp.getId().toString()+SPLIT_TWO+comp.getName(),"",comp.getName(), "false", "false", "", "", "department", "");
			treeNodes.add(root);
		}
		addAllRootDeptsForChooseDept(treeNodes);
	}
	/**
	 * 用户管理中选择正职部门和兼职部门的部门树时，加载所有根部门，即没有父部门的部门
	 */
	private void addAllRootDeptsForChooseDept(List<ZTreeNode> treeNodes){
		List<Department> rootDepts = departmentManager.getDepartments();
		addDeptsForChooseDept(rootDepts,treeNodes);
	}
	/**
	 * 用户管理中选择正职部门和兼职部门的部门树时，加载多个部门节点
	 */
	private void addDeptsForChooseDept(List<Department> depts,List<ZTreeNode> treeNodes){
		for(Department dept:depts){
			addDeptNodeForChooseDept(dept,treeNodes);
		}
	}
	/**
	 * 用户管理中选择正职部门和兼职部门的部门树时，加载单个部门节点
	 * @param dept
	 * @param treeNodes
	 */
	private void addDeptNodeForChooseDept(Department dept,List<ZTreeNode> treeNodes){
		String nodeId = (dept.getBranch()?"USERSBYBRANCH":"USERSBYDEPARTMENT")+SPLIT_ONE+dept.getId()+SPLIT_TWO+dept.getName()+SPLIT_THREE+dept.getSubCompanyId();
		ZTreeNode node = new ZTreeNode(nodeId,"",dept.getName(), "false", isHasSubDept(dept.getId())+"", "", "", dept.getBranch()?"branch":"department", "");
		treeNodes.add(node);
	}
	
	/**
	 * 用户管理中选择正职部门和兼职部门的部门树时，加载子部门节点
	 * @param parentDeptId
	 * @param treeNodes
	 */
	private void addSubDeptsForChooseDept(Long parentDeptId,List<ZTreeNode> treeNodes){
		List<Department> depts = departmentManager.getSubDepartmentList(parentDeptId);
		addDeptsForChooseDept(depts,treeNodes);
	}
	/**
	 * 用户管理中选择正职部门和兼职部门的部门树时，加载第一个有权限的分支树
	 */
	private void addFirstAuthBrachDeptsForChooseDept(Department dept,List<ZTreeNode> treeNodes){
		//部门树节点
		String nodeId = (dept.getBranch()?"USERSBYBRANCH":"USERSBYDEPARTMENT")+SPLIT_ONE+dept.getId()+SPLIT_TWO+dept.getName()+SPLIT_THREE+dept.getSubCompanyId();
		ZTreeNode root = new ZTreeNode(nodeId,"",dept.getName(), "true", isHasSubDept(dept.getId())+"", "", "", dept.getBranch()?"branch":"department", "");
		//加载子部门节点
		List<ZTreeNode> children = new ArrayList<ZTreeNode>();
		addSubDeptsForChooseDept(dept.getId(),children);
		root.setChildren(children);
		treeNodes.add(root);
	}
	/**
	 * 用户管理中选择正职部门和兼职部门的部门树时，加载非第一个有权限的分支树
	 */
	private void addOtherAuthBrachDeptsForChooseDept(Department dept,List<ZTreeNode> treeNodes){
		addDeptNodeForChooseDept(dept,treeNodes);
	}
	
	@Required
	public void setDepartmentManager(DepartmentManager departmentManager) {
		this.departmentManager = departmentManager;
	}
	
	//公司人员树
	public String createManCompanyTree() throws Exception {
		renderText(TreeUtils.getCreateManCompanyTree(ContextUtils.getCompanyId(), ContextUtils.getCompanyName(), currentId,false,DepartmentDisplayType.NAME,false,""));
		return null;
	}
	
	//部门工作组人员树
	public String createManDepartmentGroupTree(){
		renderText(TreeUtils.getCreateManDepartmentGroupTree(ContextUtils.getCompanyId(),  currentId,false,DepartmentDisplayType.NAME,false,""));
		return null;
	}
	
	
	
	//部门人员树
	public String createManDepartmentTree(){
		renderText(TreeUtils.getCreateManDepartmentTree(ContextUtils.getCompanyId(),  currentId,false,DepartmentDisplayType.NAME,false,""));
		return null;
	}
	//工作组人员树
	public String createManGroupTree(){
		renderText(TreeUtils.getCreateManGroupTree(ContextUtils.getCompanyId(),  currentId,false,""));
		return null;
		
	}
	//部门树
	public String createDepartmentTree(){
		renderText(TreeUtils.getCreateDepartmentTree(ContextUtils.getCompanyId(),  currentId,DepartmentDisplayType.NAME,""));
		return null;
	}
	//工作组树
	public String createGroupTree(){
		renderText(TreeUtils.getCreateGroupTree(ContextUtils.getCompanyId(),  currentId,""));
		return null;
	}	
	
	
	//标签树
	public String getTree(){
	
		 switch(TreeType.valueOf(treeType)) {
	       case COMPANY:
	    	   renderText(TreeUtils.getCreateManCompanyTree(ContextUtils.getCompanyId(), ContextUtils.getCompanyName(), currentId,false,DepartmentDisplayType.NAME,false,""));
	    	   break;
	       case MAN_DEPARTMENT_GROUP_TREE:
	    	   renderText(TreeUtils.getCreateManDepartmentGroupTree(ContextUtils.getCompanyId(),  currentId,false,DepartmentDisplayType.NAME,false,""));
	          break;
	       case MAN_DEPARTMENT_TREE:
	    	   renderText(TreeUtils.getCreateManDepartmentTree(ContextUtils.getCompanyId(),  currentId,false,DepartmentDisplayType.NAME,false,""));
	          break;
	       case MAN_GROUP_TREE:
	    	   renderText(TreeUtils.getCreateManGroupTree(ContextUtils.getCompanyId(),  currentId,false,""));
	           break;
	       case DEPARTMENT_TREE:
	    	   renderText(TreeUtils.getCreateDepartmentTree(ContextUtils.getCompanyId(),  currentId,DepartmentDisplayType.NAME,""));
	         break;
	       case GROUP_TREE:
	    	   renderText(TreeUtils.getCreateGroupTree(ContextUtils.getCompanyId(),  currentId,""));
	          break;
	       
	       default:  return renderText(TreeUtils.getCreateManCompanyTree(ContextUtils.getCompanyId(), ContextUtils.getCompanyName(), currentId,false,DepartmentDisplayType.NAME,false,""));
	       }
		
		return null;
	}
	
	
	
	
	
	
	
	
	
	
	

	public String getCurrentId() {
		return currentId;
	}

	public void setCurrentId(String currentId) {
		this.currentId = currentId;
	}

	@Required
	public void setCompanyManager(CompanyManager companyManager) {
		this.companyManager = companyManager;
	}
  
	@Required
	public void setWorkGroupManager(WorkGroupManager workGroupManager) {
		this.workGroupManager = workGroupManager;
	}
	
	@Required
    public void setRoleManager(RoleManager roleManager) {
		this.roleManager = roleManager;
	}

	// 继承自父类的方法=======================================================================
	@Override
	public String delete() throws Exception {
		return null;
	}
	
	@Override
	public String list() throws Exception {
		return null;
	}
	
	@Override
	protected void prepareModel() throws Exception {
		
	}
	
	@Override
	public String save() throws Exception {
		return null;
	}
	
	public Company getModel() {
		return null;
	}

	public String getTreeType() {
		return treeType;
	}

	public void setTreeType(String treeType) {
		this.treeType = treeType;
	}

	public boolean isSystemAdminable() {
		return systemAdminable;
	}
	
	public void setTreeNodeId(String treeNodeId) {
		this.treeNodeId = treeNodeId;
	}
}
