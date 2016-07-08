package com.norteksoft.acs.web.authorization;

import java.io.File;
import java.io.FileInputStream;
import java.net.URLEncoder;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.norteksoft.acs.base.utils.ExportRoleQuery;
import com.norteksoft.acs.base.web.struts2.CRUDActionSupport;
import com.norteksoft.acs.entity.authorization.BranchAuthority;
import com.norteksoft.acs.entity.authorization.BusinessSystem;
import com.norteksoft.acs.entity.authorization.Function;
import com.norteksoft.acs.entity.authorization.FunctionGroup;
import com.norteksoft.acs.entity.authorization.FunctionVo;
import com.norteksoft.acs.entity.authorization.Role;
import com.norteksoft.acs.entity.organization.Department;
import com.norteksoft.acs.entity.organization.User;
import com.norteksoft.acs.entity.organization.Workgroup;
import com.norteksoft.acs.service.AcsUtils;
import com.norteksoft.acs.service.authorization.BranchAuthorityManager;
import com.norteksoft.acs.service.authorization.BusinessSystemManager;
import com.norteksoft.acs.service.authorization.RoleManager;
import com.norteksoft.acs.service.authorization.StandardRoleManager;
import com.norteksoft.acs.service.organization.CompanyManager;
import com.norteksoft.acs.service.organization.DepartmentManager;
import com.norteksoft.acs.service.organization.UserManager;
import com.norteksoft.acs.service.organization.WorkGroupManager;
import com.norteksoft.acs.service.syssetting.SecuritySetManager;
import com.norteksoft.acs.web.eunms.AddOrRomoveState;
import com.norteksoft.mms.module.entity.Menu;
import com.norteksoft.mms.module.service.MenuManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.JsonParser;
import com.norteksoft.product.util.PageUtils;
import com.norteksoft.product.util.tree.ZTreeNode;
import com.norteksoft.product.web.struts2.Struts2Utils;

@SuppressWarnings("deprecation")
@ParentPackage("default")
@Results( { 
	@Result(name = CRUDActionSupport.RELOAD, location = "role?businessSystemId=${businessSystemId}", type="redirectAction") 
	,@Result(name="RELOAD_CUSTOM_ROLE", location="custom-role?businessSystemId=${businessSystemId}", type="redirectAction")
	,@Result(name="RELOAD_STANDARD_ROLE", location="standard-role-authoritys?businessSystemId=${businessSystemId}&roleId=${roleId}", type="redirectAction")
})
public class RoleAction extends CRUDActionSupport<Role> {
	private static final long serialVersionUID = -5473169092158238538L;
	private static String ACS_SYSTEM_ADMIN="acsSystemAdmin";//系统管理员角色编码
	private static String ACS_SECURITY_ADMIN="acsSecurityAdmin";//安全管理员角色编码
	private static String ACS_AUDIT_ADMIN="acsAuditAdmin";//审计管理员角色编码
	private Page<Role> page = new Page<Role>(0, true);
	private Page<FunctionGroup> functionpage = new Page<FunctionGroup>(20, true);
	private Page<Workgroup> workGroupPage = new Page<Workgroup>(20, true);
	private Page<Department> departmentPage = new Page<Department>(20, true);
	private RoleManager roleManager;
	private BusinessSystemManager businessSystemManager;
	private SecuritySetManager securitySetManager;
	private List<Role> roles;
	private Role entity;
	private Long id;
	private Long paternId;
	private Long roleId;
	private Long businessSystemId;
	private DepartmentManager departmentManager;
	private List<Long> userIds;
	private List<User> allUsers;
	private List<Long> departmentsIds;
	private List<Long> functionIds = new ArrayList<Long>();
	private List<Long> checkedFunctionIds;
	private List<Long> checkedWorkGroupIds;
	private List<Long> workGroupIds;
	private Integer isAddOrRomove;
	private String departmentTree;
	private String usersTree;
	private String currentId;
	private Long roleGroupId;
	private String systemTree;
	private String workgroupTree;
	private CompanyManager companyManager;
	private WorkGroupManager workGroupManager;
	private String queryType;
	private String queryName;
	private String queryIds;
	private String queryTitle;
	private List<BusinessSystem> systems;
	private List<List<Role>> allRoles;
	private Map<User, List<List<Role>>> userRoles;
	private UserManager userManager;
	private String isHave;
	private List<Long> ids;
	private List<Long> roleIds;
	private String allInfos;
	private Boolean isAdminRole=false;//是否是管理员角色
	private List<Department> branches=new ArrayList<Department>();
	private String adminSign;//管理员标识：securityAdmin（安全管理员）、branchAdmin（分支机构管理员）
	private String companyName;//公司名称
	private String roleName;//角色名称
	private String roleCode;//角色编号
	private Long branchesId;//分支机构id
	private String moduleType;//模块类型：值为“role”表示角色管理，值为“log”表示系统日志
	private String manageBranchesIds="";//被管理的分支机构id
	private Map<User,List<Role>> userRoleMap=new HashMap<User,List<Role>>();
	private Map<Department,List<Role>> departmentRoleMap=new HashMap<Department,List<Role>>();
	private Map<Workgroup,List<Role>> workgroupRoleMap=new HashMap<Workgroup,List<Role>>();
	private Boolean containBranches;//集团公司中是否含有分支机构：true含有分支机构，false不含有分支机构
	private String viewType;//查看类型：user表示用户，department表示部门，workgroup表示工作组
	private String detailTitle="";
	private String addUserIds;
	private String addDepartmentIds;
	private String addWorkgroupIds;
	private String fids;//资源ids字符串
	private List<Function> functions=new ArrayList<Function>();
	private String exportQueryIds;
	private String buttonType;
	private File file;
	private String fileName;
	private List<FunctionVo> fvs=new ArrayList<FunctionVo>();
	private String functionCode;
	private String functionName;
	private String functionPath;
	
	public String getFunctionCode() {
		return functionCode;
	}
	public void setFunctionCode(String functionCode) {
		this.functionCode = functionCode;
	}
	public String getFunctionName() {
		return functionName;
	}
	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}
	public String getFunctionPath() {
		return functionPath;
	}
	public void setFunctionPath(String functionPath) {
		this.functionPath = functionPath;
	}
	public List<FunctionVo> getFvs() {
		return fvs;
	}
	public void setFvs(List<FunctionVo> fvs) {
		this.fvs = fvs;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileFileName(String fileName) {
		this.fileName = fileName;
	}
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
	public String getButtonType() {
		return buttonType;
	}
	public void setButtonType(String buttonType) {
		this.buttonType = buttonType;
	}
	private String roleIdStrs;//roleId集合，以逗号隔开
	private String selectRoleIds;//角色管理中的复制权限功能，在角色树中选择的角色id集合，以逗号隔开
	private Boolean hasSecurityAdmin=true;//是否具有安全管理员
	
	public String getFids() {
		return fids;
	}
	public void setFids(String fids) {
		this.fids = fids;
	}
	@Autowired
	private AcsUtils acsUtils;
	@Autowired
	private BranchAuthorityManager branchAuthorityManager;
	@Autowired
	private StandardRoleManager standardRoleManager;
	@Autowired
	private MenuManager menuManager;
	
	private static final String SUCCESS_MESSAGE_LEFT = "<font class=\"onSuccess\"><nobr>";
	private static final String MESSAGE_RIGHT = "</nobr></font>";
	private static final String ERROR_MESSAGE_LEFT = "<font class=\"onError\"><nobr>";
	protected void addErrorMessage(String message){
		this.addActionMessage(ERROR_MESSAGE_LEFT+message+MESSAGE_RIGHT);
	}
	protected void addSuccessMessage(String message){
		this.addActionMessage(SUCCESS_MESSAGE_LEFT+message+MESSAGE_RIGHT);
	}

	@Required
	public void setDepartmentManager(DepartmentManager departmentManager) {
		this.departmentManager = departmentManager;
	}
	
	@Required
	public void setUserManager(UserManager userManager) {
		this.userManager = userManager;
	}

	@Required
	public void setSecuritySetManager(SecuritySetManager securitySetManager) {
		this.securitySetManager = securitySetManager;
	}
  
	public void prepareRemoveUsers() throws Exception {
    	entity = roleManager.getRole(roleId);
    	isAdminRole=hasAdminRole(entity);
	}
	/**
	 * 
	 * @return
	 */
	@Action("role-hasAll")
	public String hasAll(){
		renderText(roleManager.hasAll(buttonType,roleId));
		return null;
	}
	private boolean hasAdminRole(Role role){
		if(ACS_SYSTEM_ADMIN.equals(role.getCode())||ACS_AUDIT_ADMIN.equals(role.getCode())||ACS_SECURITY_ADMIN.equals(role.getCode())){
			return true;
		}
		return false;
	}
	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	@Action("role-exportRole")
	public String exportRole() throws Exception{
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request=ServletActionContext.getRequest();
		String fileName1="权限信息.xls";
		response.reset();
		response.setContentType("application/x-download");
		String agent = request.getHeader("User-Agent");
		boolean isMSIE = (agent != null && agent.indexOf("MSIE") != -1);
		if (isMSIE) {
		    fileName1 = URLEncoder.encode(fileName1, "UTF-8");
		} else {
		    fileName1 = new String(fileName1.getBytes("UTF-8"), "ISO-8859-1");
		}
		response.setHeader("Content-Disposition", "attachment; filename=" + fileName1);
		HSSFWorkbook wb=roleManager.getWorkBook(businessSystemId);
		wb.write(response.getOutputStream());
		return null;
	}
	@Action("role-importRole")
	public String importRole() throws Exception{
		String result = "";
		Workbook workBook = null;
		if(fileName.endsWith(".xls")){
			workBook = new HSSFWorkbook(new FileInputStream(file));
			result=roleManager.importRole(workBook);
		}else if(fileName.endsWith(".xlsx")){
			workBook = new XSSFWorkbook(new FileInputStream(file));
			result=roleManager.importRole(workBook);
		}else{
			result = Struts2Utils.getText("importRoleFileTypeError");
		}
		renderText(result);
		return null;
	}
	@Action("role-accredit")
	public String accredit() throws Exception{
		String result = "";
		Workbook workBook = null;
		if(fileName.endsWith(".xls")){
			workBook = new HSSFWorkbook(new FileInputStream(file));
			result=roleManager.accredit(workBook);
		}else if(fileName.endsWith(".xlsx")){
			workBook = new XSSFWorkbook(new FileInputStream(file));
			result=roleManager.accredit(workBook);
		}else{
			result = Struts2Utils.getText("importRoleFileTypeError");
		}
		renderText(result);
		return null;
	}
	//导入角色功能关系的页面
	@Action("role-showImportRole")
	public String showImportRole(){
		return "role-showImportRole";
	}
	//导入角色用户关系的页面
	@Action("role-showAccredit")
	public String showAccredit(){
		return "role-showAccredit";
	}

	/**
	 * 给角色移除用户列表
	 */
	@Action("role-removeUsers")
	public String removeUsers() throws Exception{
		Role role = roleManager.getRole(roleId);
		businessSystemId = role.getBusinessSystem().getId();
		int deleteUserNum=0;//移除用户的个数
		int deleteDepartNum=0;//移除部门的个数
		int deleteWorkgroupNum=0;//移除工作组的个数
		int noDeleteUserNum=0;//未移除用户的个数
		int noDeleteDepartNum=0;//未移除部门的个数
		int noDeleteWorkgroupNum=0;//未移除工作组的个数
		if(roleManager.hasSecurityAdminRole(ContextUtils.getUserId())){
			roleManager.removeUDWFromRoel(roleId, userIds, departmentsIds, workGroupIds);
			if(userIds!=null&&userIds.size()>0){
				deleteUserNum=userIds.size();
			}
			if(departmentsIds!=null&&departmentsIds.size()>0){
				deleteDepartNum=departmentsIds.size();
			}
			if(workGroupIds!=null&&workGroupIds.size()>0){
				deleteWorkgroupNum=workGroupIds.size();
			}
		}else{
			List<BranchAuthority> branchList=branchAuthorityManager.getBranchByUser(ContextUtils.getUserId());
			for(BranchAuthority branch:branchList){
				manageBranchesIds+=branch.getBranchesId();
				manageBranchesIds+=getSubBranches(branch.getBranchesId());
				manageBranchesIds+=",";
			}
			List<Long> uIds=new ArrayList<Long>();
			List<Long> dIds=new ArrayList<Long>();
			List<Long> wIds=new ArrayList<Long>();
			if(StringUtils.isNotEmpty(manageBranchesIds)){
				if(userIds!=null && userIds.size()>0){
					for(Long uId:userIds){
						if(uId==-1){
							continue;
						}
						User u=userManager.getUserById(uId);
						if(u.getSubCompanyId()!=null&&manageBranchesIds.contains(u.getSubCompanyId()+",")){
							uIds.add(uId);
							deleteUserNum++;
						}else{
							noDeleteUserNum++;
						}
					}
				}
				if(departmentsIds!=null && departmentsIds.size()>0){
					for(Long dId:departmentsIds){
						if(dId==-1){
							continue;
						}
						Department d=departmentManager.getDepartment(dId);
						if(d.getSubCompanyId()!=null&&manageBranchesIds.contains(d.getSubCompanyId()+",")){
							dIds.add(dId);
							deleteDepartNum++;
						}else{
							noDeleteDepartNum++;
						}
					}
				}
				if(workGroupIds!=null && workGroupIds.size()>0){
					for(Long wId:workGroupIds){
						if(wId==-1){
							continue;
						}
						Workgroup w=workGroupManager.getWorkGroup(wId);
						if(w.getSubCompanyId()!=null&&manageBranchesIds.contains(w.getSubCompanyId()+",")){
							wIds.add(wId);
							deleteWorkgroupNum++;
						}else{
							noDeleteWorkgroupNum++;
						}
					}
				}
			}
			roleManager.removeUDWFromRoel(roleId, uIds, dIds, wIds);
		}
		if("acsSystemAdmin".equals(role.getCode())||"acsSecurityAdmin".equals(role.getCode())||"acsAuditAdmin".equals(role.getCode())){
			addSuccessMessage(Struts2Utils.getText("authorization.remove")+" "+deleteUserNum+" "+Struts2Utils.getText("authorization.user.not.remove")+" "+noDeleteUserNum+" "+Struts2Utils.getText("authorization.user.period"));
		}else{
			addSuccessMessage(Struts2Utils.getText("authorization.remove")+" "+deleteUserNum+" "+Struts2Utils.getText("authorization.user.not.remove")+" "+noDeleteUserNum+" "+Struts2Utils.getText("authorization.user.remove")+" "+deleteDepartNum+" "+Struts2Utils.getText("authorization.department.not.remove")+" "+noDeleteDepartNum+" "+Struts2Utils.getText("authorization.department.remove")+" "+deleteWorkgroupNum+" "+Struts2Utils.getText("authorization.workgroup.not.remove")+" "+noDeleteWorkgroupNum+" "+Struts2Utils.getText("authorization.workgroup.period"));
		}
		return "RELOAD_STANDARD_ROLE";
	}
	
	public String removeFromRole(){
		roleManager.removeUDWFromRoel(roleId, userIds, departmentsIds, workGroupIds);
		return "RELOAD_STANDARD_ROLE";
	}
	
	/**
	 * 角色添加用户时的用户节点 
	 */
	public String getUserNodes(Long deptId) throws Exception{
		StringBuilder nodes = new StringBuilder();
		
		List<User> users = userManager.getUsersByDeptId(deptId);
		
		List<Department> subDepts = departmentManager.getSubDeptments(deptId);
		for(Department subDept : subDepts){
			nodes.append(generateJsTreeNode("DEPARTMENT," + subDept.getId(), "closed", subDept.getName(), ""));
			nodes.append(",");
		}
		List<Long> checkedUsers = roleManager.getCheckedUserByRole(roleId);
		if(isAddOrRomove == 0){
			for(User user : users){
				if(checkedUsers.contains(user.getId())) continue;
				nodes.append(generateJsTreeNode("USER," + user.getId(), "", user.getName(), "")).append(",");
			}
		}else if(isAddOrRomove == 1){
			for(User user : users){
				if(checkedUsers.contains(user.getId()))
					nodes.append(generateJsTreeNode("USER," + user.getId(), "", user.getName(), "")).append(",");
			}
		}
		if(nodes.lastIndexOf(",") != -1 && nodes.lastIndexOf(",") == nodes.length()-1){
			nodes.replace(nodes.length()-1, nodes.length(), "");
		}
		return nodes.toString();
	}
	
	/**
	 * 没有部门的用户的树节点
	 * @param companyId
	 * @return
	 */
	public String getNoDepartmentUserNodes(Long companyId){
		StringBuilder nodes = new StringBuilder();
		List<com.norteksoft.product.api.entity.User> users = ApiFactory.getAcsService().getUsersNotInDepartment(companyId);
		List<Long> checkedUsers = roleManager.getCheckedUserByRole(roleId);
		if(isAddOrRomove == 0){
			for(com.norteksoft.product.api.entity.User user : users){
				if(checkedUsers.contains(user.getId())) continue;
				nodes.append(generateJsTreeNode("USER," + user.getId(), "", user.getLoginName(), "")).append(",");
			}
		}else if(isAddOrRomove == 1){
			for(com.norteksoft.product.api.entity.User user : users){
				if(!checkedUsers.contains(user.getId())) continue;
				nodes.append(generateJsTreeNode("USER," + user.getId(), "", user.getLoginName(), "")).append(",");
			}
		}
		if(nodes.lastIndexOf(",") != -1 && nodes.lastIndexOf(",") == nodes.length()-1){
			nodes.replace(nodes.length()-1, nodes.length(), "");
		}
		return nodes.toString();
	}
	
	/**
	 * 给角色添加用户
	 * @return
	 * @throws Exception
	 */
	@Action("role-addUsersToRole")
	public String addUsersToRole() throws Exception{
		entity = roleManager.getRole(roleId);
		businessSystemId = entity.getBusinessSystem().getId();
		if(addUserIds!=null&&addUserIds.equals("ALL_USER")){
			roleManager.addRoleAllUser(roleId,addUserIds);
			addSuccessMessage(Struts2Utils.getText("common.saved"));
			return "RELOAD_STANDARD_ROLE";
		}
		if(addDepartmentIds!=null&&addDepartmentIds.equals("ALL_DEPARTMENT")){
			roleManager.addRoleAllDepartment(roleId,addDepartmentIds);
			addSuccessMessage(Struts2Utils.getText("common.saved"));
			return "RELOAD_STANDARD_ROLE";
		}
		if(addWorkgroupIds!=null&&addWorkgroupIds.equals("ALL_WORKGROUP")){
			roleManager.addRoleAllWorkGroup(roleId,addWorkgroupIds);
			addSuccessMessage(Struts2Utils.getText("common.saved"));
			return "RELOAD_STANDARD_ROLE";
		}
		addSuccessMessage(roleManager.addUDWFromRoel(entity,stringToList(addUserIds),stringToList(addDepartmentIds),stringToList(addWorkgroupIds),allInfos==null?"":allInfos));
		return "RELOAD_STANDARD_ROLE";
	}
	
	/**
	 *  生成树的一个NODE
	 * @param id        NODE的id
	 * @param state     NODE的状态   open || closed || ""
	 * @param data      NODE的显示数据
	 * @param children  NODE的子NODE 
	 * @return
	 */
	protected String generateJsTreeNode(String id, String state, String data, String children){
		StringBuilder node = new StringBuilder();
		node.append("{ attributes: { id : \"").append(id).append("\" }");
		if(state != null && !"".equals(state.trim())){
			node.append(",state : \"").append(state).append("\"");
		}
		node.append(", data: \"").append(data).append("\" ");
		if(children != null && !"".equals(children.trim())){
			node.append(", children : [").append(children).append("]");
		}
		node.append("}");
		return node.toString();
	}
	
	
	 public String forward(Object obj){
//		Object target = null;
//		if(obj instanceof HibernateProxy){
//	        HibernateProxy proxy = (HibernateProxy)obj;
//	        target = proxy.getHibernateLazyInitializer().getImplementation();
//	    }
		return "RELOAD_STANDARD_ROLE";
	}
	 
	@Override
	@Action("role-delete")
	public String delete() throws Exception {
		String logSign="";//该字段只是为了标识日志信息：角色名称
		User user=userManager.getUserById(ContextUtils.getUserId());
		int deleteNum=0;//删除角色的个数
		int noDeleteNum=0;//未删除角色的个数
		if(roleManager.hasSecurityAdminRole(user)){
			for(Long rId : roleIds){
				Role r=roleManager.getRole(rId);
				if(StringUtils.isNotEmpty(logSign)){
					logSign+=",";
				}
				logSign+=r.getName();
				branchAuthorityManager.deleteRoleByBranchesId(rId);
				roleManager.clean(rId);
				roleManager.deleteRole(rId);
			}
		}else if(roleManager.hasBranchAdminRole(user)){
			for(Long rId:roleIds){
				if(validateDelete(rId)){
					Role r=roleManager.getRole(rId);
					if(StringUtils.isNotEmpty(logSign)){
						logSign+=",";
					}
					logSign+=r.getName();
					
					deleteNum++;
					branchAuthorityManager.deleteRoleByBranchesId(rId);
					roleManager.clean(rId);
					roleManager.deleteRole(rId);
				}
			}
			noDeleteNum=roleIds.size()-deleteNum;
		}
		
		if(StringUtils.isNotEmpty(logSign))
			ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("acs.roleManager"), 
					ApiFactory.getBussinessLogService().getI18nLogInfo("acs.deleteRole")+logSign,ContextUtils.getSystemId("acs"));
		
		addSuccessMessage(Struts2Utils.getText("common.delete")+" "+(roleIds.size()-noDeleteNum)+" "+Struts2Utils.getText("role.role.not.delete")+" "+noDeleteNum+" "+Struts2Utils.getText("role.role.period"));
		return list();
	}
	
	private boolean validateDelete(Long rId){
		boolean sign=false;
		List<BranchAuthority> branchAuthoritys=branchAuthorityManager.getBranchByUser(ContextUtils.getUserId());
		if(branchAuthoritys!=null && branchAuthoritys.size()>0){
			Set<Department> branchSet=new HashSet<Department>();
			for(BranchAuthority branchAuthority:branchAuthoritys){
				Department department=departmentManager.getDepartment(branchAuthority.getBranchesId());
				packagingSubBranches(department,branchSet);
			}
			for(Department d:branchSet){
				Role r=roleManager.getRole(rId);
				if(d.getId().equals(r.getSubCompanyId())){
					sign=true;
					break;
				}
			}
		}
		return sign;
	}

	@Override
	public String list() throws Exception {
		User user=userManager.getUserById(ContextUtils.getUserId());
		hasSecurityAdmin = roleManager.hasSecurityAdminRole(user);
		if(page.getPageSize()>1){
			if(hasSecurityAdmin){
				if(businessSystemId!=null)page = roleManager.getAllRoles(page, businessSystemId);
			}else if(roleManager.hasBranchAdminRole(user)){
				//所管理的分支机构
				List<BranchAuthority> branchAuthoritys=branchAuthorityManager.getBranchByUser(ContextUtils.getUserId());
				Set<Long> branchesSet=new HashSet<Long>();
				for(BranchAuthority ba:branchAuthoritys){
					branchesSet.add(ba.getBranchesId());
					getSubBranches(ba.getBranchesId(),branchesSet);
				}
				if(businessSystemId!=null)page = roleManager.getRoles(page, businessSystemId,branchesSet);
			}
			this.renderText(PageUtils.pageToJson(page));
			return null;
		}
		return "role";
	}
	
	private void getSubBranches(Long departmentId, Set<Long> branchesSet) {
		List<Department> subDeptments=departmentManager.getSubBranchs(departmentId);
		for(Department d:subDeptments){
			if(d.getBranch()){
				branchesSet.add(d.getId());
			}
		}
	}
	/**
	 * 新建
	 */
	@Action("role-input")
	public String input() throws Exception {
		User user=userManager.getUserById(ContextUtils.getUserId());
		if(roleManager.hasSecurityAdminRole(user)){
			adminSign="securityAdmin";
			companyName=ContextUtils.getCompanyName();
			BusinessSystem businessSystem = businessSystemManager.getBusiness(businessSystemId);
			if(businessSystem!=null&&!"acs".equals(businessSystem.getCode())){
				branches=departmentManager.getAllBranches();
			}
		}else if(roleManager.hasBranchAdminRole(user)){
			adminSign="branchAdmin";
			List<BranchAuthority> branchAuthoritys=branchAuthorityManager.getBranchByUser(ContextUtils.getUserId());
			if(branchAuthoritys!=null && branchAuthoritys.size()>0){
				Set<Department> branchSet=new HashSet<Department>();
				for(BranchAuthority branchAuthority:branchAuthoritys){
					Department department=departmentManager.getDepartment(branchAuthority.getBranchesId());
					packagingSubBranches(department,branchSet);
				}
				branches.addAll(branchSet);
			}
		}
		if(entity.getId()==null){
			entity.setCode(createRoleCode());
		}
		return "role-input";
	}
	
	private String createRoleCode(){
		long num=0;
		List<Role> roles=roleManager.getDefaultCodeRoles();
		if(roles != null && roles.size()>0){
			for(Role r:roles){
				String codeNum=r.getCode().replace("role-", "");
				if(codeNum.matches("^-?\\d+$")&&Long.valueOf(codeNum)>num){
					num=Long.valueOf(codeNum);
				}
			}
		}else{
			return "role-1";
		}
		return "role-"+(num+1);
	}
	
	/**
	 * 封装该分支机构以及该分支机构下的所有子分支机构
	 */
	private void packagingSubBranches(Department department,Set<Department> branchSet) {
		if(department.getBranch()){
			branchSet.add(department);
		}
		List<Department> departments=departmentManager.getSubDeptments(department.getId());
		if(departments!=null && departments.size()>0){
			for(Department d:departments){
				packagingSubBranches(d,branchSet);
			}
		}
	}
	@Override
	protected void prepareModel() throws Exception {
		if (id != null) {
			entity = roleManager.getRole(id);
		} else {
			entity = new Role();
			if(businessSystemId != null){
				BusinessSystem businessSystem = businessSystemManager.getBusiness(businessSystemId);
				entity.setBusinessSystem(businessSystem);
			}
			//控制在acs中建角色时，保存公司id
			entity.setCompanyId(ContextUtils.getCompanyId());
		}
	}

	@Override
	@Action("role-save")
	public String save() throws Exception {
		boolean logSign=true;//该字段只是为了标识日志信息：true表示新建角色、false表示修改角色
		if(id!=null)logSign=false;
		if(entity.getId()==null){//只有在权限系统中新建角色时才需加公司id
			entity.setCompanyId(ContextUtils.getCompanyId());
		}
		if(entity.getWeight()==null){
			entity.setWeight(0);
		}
		roleManager.saveRole(entity);
		this.setBusinessSystemId(entity.getBusinessSystem().getId());
		addSuccessMessage(getText("common.saved"));
		
		if(logSign){
			ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("acs.roleManager"), 
					ApiFactory.getBussinessLogService().getI18nLogInfo("acs.createRole")+entity.getName(),ContextUtils.getSystemId("acs"));
		}else{
			ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("acs.roleManager"), 
					ApiFactory.getBussinessLogService().getI18nLogInfo("acs.updateRole")+entity.getName(),ContextUtils.getSystemId("acs"));
		}
		return input();
	}
	
	/**
	 * 验证角色名称在同一系统同一分支机构唯一
	 * 验证角色编号在集团唯一
	 * @return
	 * @throws Exception
	 */
	@Action("role-validateNameOnly")
	public String validateNameOnly() throws Exception{
		List<Role> roleList=roleManager.getRoles(roleCode);
		List<Role> roles=roleManager.getRoles(businessSystemId,branchesId,roleName);
		boolean codeRepeat=validateOnly(roleList,id);
		boolean nameRepeat=validateOnly(roles,id);
		if(codeRepeat){
			if(nameRepeat){
				this.renderText("codeNameRepeat");//表示角色编号和角色名称都不唯一
			}else{
				this.renderText("codeRepeat");//表示角色编号不唯一
			}
		}else{
			if(nameRepeat){
				this.renderText("nameRepeat");//表示角色名称都不唯一
			}else{
				this.renderText("ok");//表示角色编号和角色名称都唯一
			}
		}
		return null;
	}
	
	private boolean validateOnly(List<Role> roles,Long id){
		boolean repeat=false;
		if(id==null){
			if(roles !=null && roles.size()>0){
				repeat=true;
			}
		}else{
			for(Role r:roles){
				if(!id.equals(r.getId())){
					repeat=true;
					break;
				}
			}
		}
		return repeat;
	}
	
	/**
	 * 验证是否有权限修改或删除角色
	 */
	@Action("role-validateRole")
	public String validateRole() throws Exception {
		String sign="no";
		User user=userManager.getUserById(ContextUtils.getUserId());
		if(roleManager.hasSecurityAdminRole(user)){
			sign="ok";
		}else if(roleManager.hasBranchAdminRole(user)){
			if(validateDelete(id)){
				sign="ok";
			}
		}
		this.renderText(sign);
		return null;
	}
	
	/**
	 * 获得当前系统的角色树，不包含当前选中的角色
	 * @return
	 */
	@Action("role-roleTree")
	public String roleTree(){
		List<ZTreeNode> treeNodes = new ArrayList<ZTreeNode>();
		String result ="";
		BusinessSystem system = businessSystemManager.getBusiness(businessSystemId);
		List<Role> roles = new ArrayList<Role>();
		String[] roleids = roleIdStrs.split(",");
		List<Long> roleIds = new ArrayList<Long>();
		for(String roleid:roleids){
			roleIds.add(Long.parseLong(StringUtils.trim(roleid)));
		}
		roles = roleManager.getAllRolesExceptRole(businessSystemId,roleIds);
		ZTreeNode root = new ZTreeNode("system~~system","0",system.getName(), "true", "false", "", "", "folder", "");
		treeNodes.add(root);
		for(Role role:roles){
			//拼接不是三员和分支管理员的角色
			if(!role.getCode().equals("acsBranchAdmin")&&!role.getCode().equals("acsSystemAdmin")&&!role.getCode().equals("acsSecurityAdmin")&&!role.getCode().equals("acsAuditAdmin")){
				if (!role.getName().equals("普通用户")) {
					root = new ZTreeNode("role~~"+role.getId(),"system~~system",role.getName(), "false", "false", "", "", "folder", "");
					treeNodes.add(root);
				}
			}
		}
		result = JsonParser.object2Json(treeNodes);
		renderText(result);
		return null;
	}
	@Action("role-copyRoleAndFunction")
	public String copyRoleAndFunction() throws Exception {
		List<Long> sourceRoleIds = getIdByStr(roleIdStrs);
		
		List<Long> roleIds = getIdByStr(selectRoleIds);;
		
		standardRoleManager.copyRoleAndFunction(sourceRoleIds, roleIds);
		return null;
	}
	
	private List<Long> getIdByStr(String roleIdStrs){
		List<Long> roleIds = new ArrayList<Long>();
		String[] sourceRoleids = roleIdStrs.split(",");
		for(String roleid:sourceRoleids){
			if(!"system".equals(roleid)){//角色管理中复制权限中的角色树，选中根节点时会有“system”，不应包含该系统根节点
				roleIds.add(Long.parseLong(StringUtils.trim(roleid)));
			}
		}
		return roleIds;
	}
	
	/*
	 * 生成系统JSON树
	 */
	@Action("role-systemTree")
	public String systemTree(){
		List<ZTreeNode> treeNodes = new ArrayList<ZTreeNode>();
		String result ="";
		List<Menu> menus = new ArrayList<Menu>();
		User user=userManager.getUserById(ContextUtils.getUserId());
		if(roleManager.hasSecurityAdminRole(user)&&"role".equals(moduleType)){
			menus = menuManager.getAllEnabledStandardRootMenus();
		}else if((roleManager.hasAdminRole(user.getId()))){
			if(roleManager.hasBranchAdminRole(user)&&"role".equals(moduleType)){
				menus = packagingSystemTree();
			}else{
				menus = menuManager.getAllEnabledStandardRootMenus();
			}
		}else if(roleManager.hasBranchAdminRole(user)){
			menus = packagingSystemTree();
		}
		for(Menu menu : menus){
			ZTreeNode root = new ZTreeNode("BUSINESSSYSTEM_"+menu.getSystemId(),"0",menuManager.getNameToi18n(menu.getName()), "false", "false", "", "", "folder", "");
			treeNodes.add(root);
		}
		if(menus.size() > 0){
			if(businessSystemId == null){
				businessSystemId = menus.get(0).getSystemId();
			}
		}
		result = JsonParser.object2Json(treeNodes);
		renderText(result);
		return null;
	}
	
	private List<Menu> packagingSystemTree(){
		Set<Long> businessSystemSet=new HashSet<Long>();
		//所管理的分支机构
		List<BranchAuthority> branches=branchAuthorityManager.getBranchByUser(ContextUtils.getUserId());
		for(BranchAuthority b:branches){
			//所管理的分支机构-所拥有的角色
			List<BranchAuthority> roles=branchAuthorityManager.getRolesByBranch(b.getBranchesId());
			for(BranchAuthority ba:roles){
				Role r=roleManager.getRole(ba.getDataId());
				businessSystemSet.add(r.getBusinessSystem().getId());
			}
			//所属分支机构为“所管理的分支机构”的角色
			packagingBusinessSystem(b.getBranchesId(),businessSystemSet);
			Set<Long> branchesSet=new HashSet<Long>();
			//所管理的分支机构的子分支机构
			getSubBranches(b.getBranchesId(), branchesSet);
			for(Long branchesId:branchesSet){
				//所属分支机构为“所管理的分支机构的子分支机构”的角色
				packagingBusinessSystem(branchesId,businessSystemSet);
			}
		}
		List<Long> idsList = new ArrayList<Long>();
		idsList.addAll(businessSystemSet);
		return  menuManager.getEnabledRootMenuBySystems(idsList);
	}
	
    private void packagingBusinessSystem(Long branchesId,Set<Long> businessSystemSet) {
    	List<Role> roleList=roleManager.getRoleByBranches(branchesId);
		for(Role r:roleList){
			businessSystemSet.add(r.getBusinessSystem().getId());
		}
	}
    /**
     * 角色添加资源
     * @return
     * @throws Exception
     */
    @Action("role-roleToFunctionList")
	public String roleToFunctionList()throws Exception{
    	isAddOrRomove=AddOrRomoveState.ADD.code;
    	return "role-function-list";
    }
    /**
     * 角色移除资源
     * @return
     * @throws Exception
     */
    @Action("role-roleRomoveFunctionList")
    public String roleRomoveFunctionList()throws Exception{
    	isAddOrRomove=AddOrRomoveState.ROMOVE.code;
    	return "role-function-list";
    }
    /**
     * 保存角色与资源的关系
     * @return
     * @throws Exception
     */
    @Action("role-roleAddFunction")
    public String roleAddFunction()throws Exception{
    	Role role = roleManager.getRole(roleId);
    	this.setBusinessSystemId(role.getBusinessSystem().getId());
    	roleManager.roleAddFunction(roleId, fids,isAddOrRomove);
    	return null;
    }
    
    private String getSubBranches(Long departmentId) {
    	String manageSubBranchesIds="";
		List<Department> subDeptments=departmentManager.getSubDeptments(departmentId);
		for(Department d:subDeptments){
			if(d.getBranch()){
				manageSubBranchesIds+=","+d.getId();
			}
			manageSubBranchesIds+=getSubBranches(d.getId());
		}
		return manageSubBranchesIds;
	}
   
	public Role getModel() {
		return entity;
	}

	public Page<Role> getPage() {
		return page;
	}

	public void setPage(Page<Role> page) {
		this.page = page;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPaternId() {
		return paternId;
	}

	public void setPaternId(Long paternId) {
		this.paternId = paternId;
	}
	
	public Long getBusinessSystemId() {
		return businessSystemId;
	}

	public void setBusinessSystemId(Long businessSystemId) {
		this.businessSystemId = businessSystemId;
	}

	@Required
	public void setRoleManager(RoleManager roleManager) {
		this.roleManager = roleManager;
	}

	@Required
	public void setBusinessSystemManager(BusinessSystemManager businessSystemManager) {
		this.businessSystemManager = businessSystemManager;
	}

	public Page<Department> getDepartmentPage() {
		return departmentPage;
	}

	public void setDepartmentPage(Page<Department> departmentPage) {
		this.departmentPage = departmentPage;
	}

	public List<Long> getUserIds() {
		return userIds;
	}

	public void setUserIds(List<Long> userIds) {
		this.userIds = userIds;
	}

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public List<User> getAllUsers() {
		return allUsers;
	}

	public void setAllUsers(List<User> allUsers) {
		this.allUsers = allUsers;
	}

	public List<Long> getDepartmentsIds() {
		return departmentsIds;
	}

	public void setDepartmentsIds(List<Long> departmentsIds) {
		this.departmentsIds = departmentsIds;
	}
	
	public List<Long> getFunctionIds() {
		return functionIds;
	}

	public void setFunctionIds(List<Long> functionIds) {
		this.functionIds = functionIds;
	}
	
	public Page<FunctionGroup> getFunctionpage() {
		return functionpage;
	}

	public void setFunctionpage(Page<FunctionGroup> functionpage) {
		this.functionpage = functionpage;
	}
	
	public List<Long> getCheckedFunctionIds() {
		return checkedFunctionIds;
	}

	public void setCheckedFunctionIds(List<Long> checkedFunctionIds) {
		this.checkedFunctionIds = checkedFunctionIds;
	}

	public Page<Workgroup> getWorkGroupPage() {
		return workGroupPage;
	}

	public void setWorkGroupPage(Page<Workgroup> workGroupPage) {
		this.workGroupPage = workGroupPage;
	}

	public List<Long> getCheckedWorkGroupIds() {
		return checkedWorkGroupIds;
	}

	public void setCheckedWorkGroupIds(List<Long> checkedWorkGroupIds) {
		this.checkedWorkGroupIds = checkedWorkGroupIds;
	}

	public List<Long> getWorkGroupIds() {
		return workGroupIds;
	}

	public void setWorkGroupIds(List<Long> workGroupIds) {
		this.workGroupIds = workGroupIds;
	}

	public Long getRoleGroupId() {
		return roleGroupId;
	}

	public void setRoleGroupId(Long roleGroupId) {
		this.roleGroupId = roleGroupId;
	}
	
	public Integer getIsAddOrRomove() {
		return isAddOrRomove;
	}

	public void setIsAddOrRomove(Integer isAddOrRomove) {
		this.isAddOrRomove = isAddOrRomove;
	}

	public String getDepartmentTree() {
		return departmentTree;
	}

	public void setDepartmentTree(String departmentTree) {
		this.departmentTree = departmentTree;
	}

	public String getUsersTree() {
		return usersTree;
	}

	public void setUsersTree(String usersTree) {
		this.usersTree = usersTree;
	}

	public String getCurrentId() {
		return currentId;
	}

	public void setCurrentId(String currentId) {
		this.currentId = currentId;
	}

	public String getSystemTree() {
		return systemTree;
	}

	public void setSystemTree(String systemTree) {
		this.systemTree = systemTree;
	}

	public String getWorkgroupTree() {
		return workgroupTree;
	}

	public void setWorkgroupTree(String workgroupTree) {
		this.workgroupTree = workgroupTree;
	}

	@Required
	public void setCompanyManager(CompanyManager companyManager) {
		this.companyManager = companyManager;
	}

	@Required
	public void setWorkGroupManager(WorkGroupManager workGroupManager) {
		this.workGroupManager = workGroupManager;
	}

	public String getQueryType() {
		return queryType;
	}

	public void setQueryType(String queryType) {
		this.queryType = queryType;
	}

	public String getQueryName() {
		return queryName;
	}

	public void setQueryName(String queryName) {
		this.queryName = queryName;
	}
	/**
	 * 查询资源所拥有的角色
	 * @return
	 */
	@Action("role-queryFunctionRole")
	public String queryFunctionRole(){
		fvs=roleManager.queryFunctionRole(functionPath,functionCode,functionName);
		return "role-queryFunctionRole";
	}
	/**
	 * 权限查询
	 * @return
	 */
	@Action("role-query")
	public String query(){
		if(!roleManager.hasSecurityAdminRole(ContextUtils.getUserId())&&roleManager.hasBranchAdminRole(ContextUtils.getUserId())){
			List<BranchAuthority> branchesList=branchAuthorityManager.getBranchByUser(ContextUtils.getUserId());
			for(BranchAuthority branches:branchesList){
				if(StringUtils.isNotEmpty(manageBranchesIds)){
					manageBranchesIds+=",";
				}
				manageBranchesIds+=branches.getBranchesId();
			}
		}
		if(queryType == null || "".equals(queryType)){
//			queryTitle="选择用户";
			queryTitle= Struts2Utils.getText("menu.selectUser");
			return "role-query-page"; 
		}
		if("roleByFunction".equals(queryType)){
			return "role-queryFunctionRole";
		}
		String sign="";
		if(StringUtils.isNotEmpty(queryIds)){
			containBranches=departmentManager.containBranches();
			if("ROLE_USER".equals(queryType)){
				if("ALLCOMPANYID".equals(queryIds)){
					List<User> userList=userManager.getUsersByCompanyId(ContextUtils.getCompanyId());
					for(User u:userList){
						List<Role> roleList=roleManager.getRolesByUserIdNew(u.getId());
						setNameInternation(roleList);//通过menu菜单国际化
						userRoleMap.put(u, roleList);
					}
					ApiFactory.getBussinessLogService().log(
							ApiFactory.getBussinessLogService().getI18nLogInfo("acs.authorityQuery"), 
							ApiFactory.getBussinessLogService().getI18nLogInfo("acs.searchAllUserAuthority"),ContextUtils.getSystemId("acs"));
				}else{
					for(String userId:queryIds.split(",")){
						User u=userManager.getUserById(Long.valueOf(userId));
						if(u!=null){
							List<Role> roleList=new ArrayList<Role>();
							roleList.addAll(acsUtils.getRolesByUserIncludeConsigner(ContextUtils.getCompanyId(), u.getId()));
							setNameInternation(roleList);//通过menu菜单国际化
							userRoleMap.put(u, roleList);
							if(StringUtils.isNotEmpty(sign)){
								sign+=",";
							}
							sign+=u.getName();
						}
					}
					if(StringUtils.isNotEmpty(sign)){
						List<String> messages = new ArrayList<String>();//国际化
						messages.add(sign);
						ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("acs.authorityQuery"), 
								ApiFactory.getBussinessLogService().getI18nLogInfo("acs.searchUserAuthority",messages),ContextUtils.getSystemId("acs"));
					}
				}
				
			}else if("ROLE_DEPARTMENT".equals(queryType)){
				if("ALLDEPARTMENTID".equals(queryIds)){
					List<Department> deptList=departmentManager.getAllDepartment();
					for(Department d:deptList){
						List<Role> roleList=roleManager.getRolesByDepartmentId(d.getId());
						setNameInternation(roleList);//通过menu菜单国际化
						departmentRoleMap.put(d, roleList);
					}
					if(StringUtils.isNotEmpty(sign)){
						ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("acs.authorityQuery"), 
								ApiFactory.getBussinessLogService().getI18nLogInfo("acs.searchAllDepartAuthority"),ContextUtils.getSystemId("acs"));
					}
				}else{
					for(String departmentId:queryIds.split(",")){
						Department d=departmentManager.getDepartment(Long.valueOf(departmentId));
						if(d!=null){
							List<Role> roleList=roleManager.getRolesByDepartmentId(d.getId());
							setNameInternation(roleList);//通过menu菜单国际化
							departmentRoleMap.put(d, roleList);
							if(StringUtils.isNotEmpty(sign)){
								sign+=",";
							}
							sign+=d.getName();
						}
					}
					
					if(StringUtils.isNotEmpty(sign)){
						ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("acs.authorityQuery"), 
								"查询"+sign+"部门权限",ContextUtils.getSystemId("acs"));
					}
				}
				
			}else if("ROLE_WORKGROUP".equals(queryType)){
				if("ALLWORKGROUPID".equals(queryIds)){
					List<Workgroup> workgroupList=workGroupManager.getAllWorkGroup();
					for(Workgroup w:workgroupList){
						List<Role> roleList=roleManager.getRolesByWorkgroupId(w.getId());
						setNameInternation(roleList);//通过menu菜单国际化
						workgroupRoleMap.put(w, roleList);
					}
				}else{
					for(String workgroupId:queryIds.split(",")){
						Workgroup w=workGroupManager.getWorkGroup(Long.valueOf(workgroupId));
						if(w!=null){
							List<Role> roleList=roleManager.getRolesByWorkgroupId(w.getId());
							setNameInternation(roleList);//通过menu菜单国际化
							workgroupRoleMap.put(w, roleList);
							if(StringUtils.isNotEmpty(sign)){
								sign+=",";
							}
							sign+=w.getName();
						}
					}
					
					if(StringUtils.isNotEmpty(sign)){
						List<String> messages = new ArrayList<String>();//国际化
						messages.add(sign);
						ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("acs.authorityQuery"), 
								ApiFactory.getBussinessLogService().getI18nLogInfo("acs.searchDepartAuthority",messages),ContextUtils.getSystemId("acs"));
					}
				}
			}
		}
		return "role-query-page";
	}
	private void setNameInternation(List<Role> roleList){
		for(Role role:roleList){
			Menu menu = menuManager.getMenuByCode(role.getBusinessSystem().getCode());
			String name = menuManager.getNameToi18n(menu.getName());
			role.getBusinessSystem().setNameil8(name);
		}
	}
	
	/**
	 * 查看权限
	 * @return
	 */
	@Action("role-detail")
	public String detail(){
		entity=roleManager.getRole(roleId);
		containBranches=departmentManager.containBranches();
		if("user".equals(viewType)){
			User u=userManager.getUserById(id);
			detailTitle+=Struts2Utils.getText("user.user")+"："+u.getName();
			if(containBranches)detailTitle+="("+u.getSubCompanyName()+")";
			detailTitle+="&nbsp;&nbsp;&nbsp;&nbsp;"+Struts2Utils.getText("authorization.role")+"："+entity.getName()+"("+entity.getBusinessSystem().getName();
			if(containBranches)detailTitle+="/"+entity.getSubCompanyName();
			detailTitle+=")";
		}else if("department".equals(viewType)){
			Department d=departmentManager.getDepartment(id);
			detailTitle+=Struts2Utils.getText("permission.item.type.department")+"："+d.getName();
			if(containBranches&&!d.getBranch())detailTitle+="("+d.getSubCompanyName()+")";
			detailTitle+="&nbsp;&nbsp;&nbsp;&nbsp;"+Struts2Utils.getText("authorization.role")+"："+entity.getName()+"("+entity.getBusinessSystem().getName();
			if(containBranches)detailTitle+="/"+entity.getSubCompanyName();
			detailTitle+=")";
		}else{
			Workgroup w=workGroupManager.getWorkGroup(id);
			detailTitle+=Struts2Utils.getText("permission.item.type.workgroup")+"："+w.getName();
			if(containBranches)detailTitle+="("+w.getSubCompanyName()+")";
			detailTitle+="&nbsp;&nbsp;&nbsp;&nbsp;"+Struts2Utils.getText("authorization.role")+"："+entity.getName()+"("+entity.getBusinessSystem().getName();
			if(containBranches)detailTitle+="/"+entity.getSubCompanyName();
			detailTitle+=")";
		}
		functions=roleManager.getFunctions(roleId);
		return "role-detail";
	}
	/**
	 * 导出
	 * @return
	 */
	@Action("role-exportRoleQuery")
	public String exportRoleQuery() throws Exception{
		String fileName="";
		List<Long> queryIdList=new ArrayList<Long>();
		if("ROLE_USER".equals(queryType)){
			fileName="用户权限";
			if(roleManager.hasSecurityAdminRole(ContextUtils.getUserId())){
				queryIdList=stringToList(exportQueryIds);
			}else{
				queryIdList=ApiFactory.getAcsService().getTreeUserIds(exportQueryIds);
			}
		}else if("ROLE_DEPARTMENT".equals(queryType)){
			fileName="部门权限";
			if(roleManager.hasSecurityAdminRole(ContextUtils.getUserId())){
				queryIdList=stringToList(exportQueryIds);
			}else{
				queryIdList=ApiFactory.getAcsService().getTreeDepartmentIds(exportQueryIds, true);
			}
		}else{
			fileName="工作组权限";
			if(roleManager.hasSecurityAdminRole(ContextUtils.getUserId())){
				queryIdList=stringToList(exportQueryIds);
			}else{
				queryIdList=ApiFactory.getAcsService().getTreeWorkgroupIds(exportQueryIds);
			}
		}
		HttpServletResponse response = ServletActionContext.getResponse();
		response.reset();
		response.setContentType("application/x-download");
		response.addHeader("Content-Disposition", "attachment;filename="+URLEncoder.encode(fileName+".xls","UTF-8"));
		ExportRoleQuery.exportRoleQuery(response.getOutputStream(), queryIdList, queryType);
		ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("acs.authorizationManagement"), 
				ApiFactory.getBussinessLogService().getI18nLogInfo("acs.exportRole"),ContextUtils.getSystemId("acs"));
		return null;
	}
	

	private List<Long> stringToList(String queryIds) {
		List<Long> queryList=new ArrayList<Long>();
		if(StringUtils.isNotEmpty(queryIds)){
			for(String str:queryIds.split(",")){
				queryList.add(Long.valueOf(str));
			}
		}
		return queryList;
	}
	public List<BusinessSystem> getSystems() {
		return systems;
	}

	public void setSystems(List<BusinessSystem> systems) {
		this.systems = systems;
	}

	public List<List<Role>> getAllRoles() {
		return allRoles;
	}

	public void setAllRoles(List<List<Role>> allRoles) {
		this.allRoles = allRoles;
	}

	public Map<User, List<List<Role>>> getUserRoles() {
		return userRoles;
	}

	public void setUserRoles(Map<User, List<List<Role>>> userRoles) {
		this.userRoles = userRoles;
	}

	public String getAllInfos() {
		return allInfos;
	}

	public void setAllInfos(String allInfos) {
		this.allInfos = allInfos;
	}

	public String getIsHave() {
		return isHave;
	}

	public void setIsHave(String isHave) {
		this.isHave = isHave;
	}

	public void setIds(List<Long> ids) {
		this.ids = ids;
	}

	public String getQueryTitle() {
		return queryTitle;
	}

	public void setQueryTitle(String queryTitle) {
		this.queryTitle = queryTitle;
	}

	public void setRoleIds(List<Long> roleIds) {
		this.roleIds = roleIds;
	}
	public Boolean getIsAdminRole() {
		return isAdminRole;
	}
	public void setIsAdminRole(Boolean isAdminRole) {
		this.isAdminRole = isAdminRole;
	}
	public List<Department> getBranches() {
		return branches;
	}
	public void setBranches(List<Department> branches) {
		this.branches = branches;
	}
	public String getAdminSign() {
		return adminSign;
	}
	public String getCompanyName() {
		return companyName;
	}
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	public Long getBranchesId() {
		return branchesId;
	}
	public void setBranchesId(Long branchesId) {
		this.branchesId = branchesId;
	}
	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}
	public void setModuleType(String moduleType) {
		this.moduleType = moduleType;
	}
	public String getManageBranchesIds() {
		return manageBranchesIds;
	}
	public String getQueryIds() {
		return queryIds;
	}
	public void setQueryIds(String queryIds) {
		this.queryIds = queryIds;
	}
	public Map<User, List<Role>> getUserRoleMap() {
		return userRoleMap;
	}
	public Map<Department, List<Role>> getDepartmentRoleMap() {
		return departmentRoleMap;
	}
	public Map<Workgroup, List<Role>> getWorkgroupRoleMap() {
		return workgroupRoleMap;
	}
	public Boolean getContainBranches() {
		return containBranches;
	}
	public String getViewType() {
		return viewType;
	}
	public void setViewType(String viewType) {
		this.viewType = viewType;
	}
	public String getDetailTitle() {
		return detailTitle;
	}
	public void setAddUserIds(String addUserIds) {
		this.addUserIds = addUserIds;
	}
	public void setAddDepartmentIds(String addDepartmentIds) {
		this.addDepartmentIds = addDepartmentIds;
	}
	public void setAddWorkgroupIds(String addWorkgroupIds) {
		this.addWorkgroupIds = addWorkgroupIds;
	}
	public List<Function> getFunctions() {
		return functions;
	}
	public void setFunctions(List<Function> functions) {
		this.functions = functions;
	}
	public String getExportQueryIds() {
		return exportQueryIds;
	}
	public void setExportQueryIds(String exportQueryIds) {
		this.exportQueryIds = exportQueryIds;
	}
	public Boolean getHasSecurityAdmin() {
		return hasSecurityAdmin;
	}
	public void setRoleIdStrs(String roleIdStrs) {
		this.roleIdStrs = roleIdStrs;
	}
	public void setSelectRoleIds(String selectRoleIds) {
		this.selectRoleIds = selectRoleIds;
	}
}
