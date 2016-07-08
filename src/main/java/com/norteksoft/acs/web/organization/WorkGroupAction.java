package com.norteksoft.acs.web.organization;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.norteksoft.acs.base.web.struts2.CRUDActionSupport;
import com.norteksoft.acs.entity.authorization.BranchAuthority;
import com.norteksoft.acs.entity.authorization.BusinessSystem;
import com.norteksoft.acs.entity.authorization.Role;
import com.norteksoft.acs.entity.organization.Company;
import com.norteksoft.acs.entity.organization.User;
import com.norteksoft.acs.entity.organization.Workgroup;
import com.norteksoft.acs.service.authorization.BranchAuthorityManager;
import com.norteksoft.acs.service.authorization.RoleManager;
import com.norteksoft.acs.service.organization.CompanyManager;
import com.norteksoft.acs.service.organization.DepartmentManager;
import com.norteksoft.acs.service.organization.UserInfoManager;
import com.norteksoft.acs.service.organization.UserManager;
import com.norteksoft.acs.service.organization.WorkGroupManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.PageUtils;

/**
 * author 李洪超 
 * version 创建时间：2009-3-11 上午09:51:10 
 * 工作组管理Action
 */
@Namespace("/organization")
@ParentPackage("default")
@Results( { @Result(name = CRUDActionSupport.RELOAD, location = "work-group", type = "redirectAction") })
public class WorkGroupAction extends CRUDActionSupport<Workgroup> {

	private static final long serialVersionUID = 4814560124772644966L;
	private WorkGroupManager workGroupManager;
	private CompanyManager companyManager;
	private UserInfoManager userInfoManager;
	private Page<Workgroup> page = new Page<Workgroup>(0, true);// 每页5项，自动查询计算总页数.
	private Page<User> userPage = new Page<User>(0, true);// 每页5项，自动查询计算总页数.
	private Page<Role> rolePage = new Page<Role>(20, true);// 每页5项，自动查询计算总页数.
	private Workgroup workGroup;
	private Long id;
	private Long companyId;
	private Long workGroupId;
	private Long parentId;
	private List<Workgroup> allWorkGroup;
	private String workGroupName;
	private String workGroupCode;
	private List<Long> userIds;
	private List<Long> checkedRoleIds;
	private List<Long> roleIds;
	private Integer isAddOrRomove;
	private List<BusinessSystem> systems;
	private List<Role> roleList;
	private String ids;
	private String prems1;
	private String ides;
	private String wfType;
	private String comeFrom;
	private Long branchesId;//分支机构id
	private String manageBranchesIds="";//被管理的分支机构id
	private Boolean containBranches;//集团公司中是否含有分支机构：true含有分支机构，false不含有分支机构
	
	@Autowired
	private BranchAuthorityManager branchAuthorityManager;
	@Autowired
	private UserManager userManager;
	@Autowired
	private RoleManager roleManager;
	@Autowired
	private DepartmentManager departmentManager;
	
	private static final String SUCCESS_MESSAGE_LEFT = "<font class=\"onSuccess\"><nobr>";
	private static final String MESSAGE_RIGHT = "</nobr></font>";
	private static final String ERROR_MESSAGE_LEFT = "<font class=\"onError\"><nobr>";
	protected void addErrorMessage(String message){
		this.addActionMessage(ERROR_MESSAGE_LEFT+message+MESSAGE_RIGHT);
	}
	protected void addSuccessMessage(String message){
		this.addActionMessage(SUCCESS_MESSAGE_LEFT+message+MESSAGE_RIGHT);
	}
	
	/**
	 * 唯一验证。工作组编号和名称
	 * liudongxia
	 */
	@Action("work-group-checkWorkCode")
	public String checkWorkCode() throws Exception{
		List<Workgroup> workgroupCodeList=workGroupManager.getWorkgroupsByCode(workGroupCode);
		List<Workgroup> workgroupNameList=workGroupManager.getWorkgroupsByName(branchesId,workGroupName);
		boolean codeRepeat=validateOnly(workgroupCodeList,id);
		boolean nameRepeat=validateOnly(workgroupNameList,id);
		if(codeRepeat){
			if(nameRepeat){
				this.renderText("codeNameRepeat");//表示工作组编号和工作组名称都不唯一
			}else{
				this.renderText("codeRepeat");//表示工作组编号不唯一
			}
		}else{
			if(nameRepeat){
				this.renderText("nameRepeat");//表示工作组名称不唯一
			}else{
				this.renderText("ok");//表示工作组编号和工作组名称都唯一
			}
		}
		return null;
	}
	
	private boolean validateOnly(List<Workgroup> workgroupList,Long id){
		boolean repeat=false;
		if(id==null){
			if(workgroupList !=null && workgroupList.size()>0){
				repeat=true;
			}
		}else{
			for(Workgroup w:workgroupList){
				if(!id.equals(w.getId())){
					repeat=true;
					break;
				}
			}
		}
		return repeat;
	}
	
	public void prepareInputWorkGroup() throws Exception {
		prepareModel();
	}
	
	/**
	 * 新建工作组
	 */
	@Action("work-group-inputWorkGroup")
	public String inputWorkGroup() throws Exception{
		companyId = companyManager.getCompanyId();
		if(workGroup.getId()==null){
			workGroup.setCode(createWorkGroupCode());
			workGroup.setSubCompanyId(branchesId);
		}
		return "work-group-input";
	}
	
	private String createWorkGroupCode(){
		int num=0;
		List<Workgroup> Workgroups=workGroupManager.getDefaultCodeWorkGroups();
		if(Workgroups != null && Workgroups.size()>0){
			for(Workgroup w:Workgroups){
				String codeNum=w.getCode().replace("workgroup-", "");
				if(codeNum.matches("^-?\\d+$")&&Integer.valueOf(codeNum)>num){
					num=Integer.valueOf(codeNum);
				}
			}
		}else{
			return "workgroup-1";
		}
		return "workgroup-"+(num+1);
	}
	
	/**
	 * 保存新建工作组
	 */
	
	public void prepareSaveWorkGroup() throws Exception {
		prepareModel();
	}
	
	/**
	 * 保存新建工作组信息
	 */
	@Action("work-group-saveWorkGroup")
	public String saveWorkGroup() throws Exception{
		boolean logSign=true;//该字段只是为了标识日志信息：true表示新建工作组、false表示修改工作组
		if(id==null){
			Company company = companyManager.getCompany(companyId); 
			workGroup.setCompany(company);
			workGroupManager.saveWorkGroup(workGroup);
		}else{
			workGroupManager.saveWorkGroup(workGroup);
			logSign=false;
		}
		List<String> messages = new ArrayList<String>();
		messages.add(workGroup.getName());
		if(logSign){
			ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("acs.workGroupManagement"), 
					ApiFactory.getBussinessLogService().getI18nLogInfo("acs.createWorkgroup",messages),ContextUtils.getSystemId("acs"));
		}else{
			ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("acs.workGroupManagement"), 
					ApiFactory.getBussinessLogService().getI18nLogInfo("acs.updateWorkgroup",messages),ContextUtils.getSystemId("acs"));
		}
		addSuccessMessage("保存成功");
		return null;
	}
	
	@Override
	@Action("work-group-delete")
	public String delete() throws Exception {
		String logSign="";//该字段只是为了标识日志信息：工作组名称
		String[] str=ides.split(",");
		for(String sid:str){
			workGroup = workGroupManager.getWorkGroup(Long.valueOf(sid));
			workGroupManager.cleanWorkGroup(workGroup.getId());
			if(StringUtils.isNotEmpty(logSign)){
				logSign+=",";
			}
			logSign+=workGroup.getName();
			workGroupManager.deleteWorkGroup(Long.valueOf(sid));
		}
		List<String> messages = new ArrayList<String>();
		messages.add(logSign);
		ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("acs.workGroupManagement"), 
				ApiFactory.getBussinessLogService().getI18nLogInfo("acs.workGroupManagement",messages),ContextUtils.getSystemId("acs"));
		this.renderText("ok");
     	return null;
	}

	@Override
	@Action("work-group")
	public String list() throws Exception {
		if(page.getPageSize() > 1){
			page = workGroupManager.getAllWorkGroupByBranchesId(page,branchesId);
//			ApiFactory.getBussinessLogService().log("工作组管理", 
//					"查看工作组列表",ContextUtils.getSystemId("acs"));
			this.renderText(PageUtils.pageToJson(page));
			return null;
			
		}
		return "work-group"; 
	}

	/**
	 * 按条件查询
	 * 
	 * @return
	 */
	public void prepareSearch() throws Exception {
		prepareModel();
	}

	public void prepareSaveUser() throws Exception {
		prepareModel();
	}
    
    /**
	 * 保存工作组添加用户
	 * @return
	 * @throws Exception
	 */
    @Action("work-group-workgroupAddUser")
    public String workgroupAddUser()throws Exception{
    	if(StringUtils.isNotEmpty(ids)){
    		userIds=new ArrayList<Long>();
    		User user=userManager.getUserById(ContextUtils.getUserId());
    		if("ALLCOMPANYID".equals(ids)){//全公司
				userIds.add(0l);
			}else{
				if(roleManager.hasSystemAdminRole(user)){
	    			for(String str:ids.split(",")){
	        			userIds.add(Long.valueOf(str));
	        		}
	    		}else if(roleManager.hasBranchAdminRole(user)){
	    			userIds=ApiFactory.getAcsService().getTreeUserIds(ids);
	    		}
			}
    		
    		String addUsers = workGroupManager.workgroupAddUser(workGroupId, userIds, 0);
    		List<String> messages = new ArrayList<String>();//国际化
    		messages.add(addUsers);
    		if(StringUtils.isNotEmpty(addUsers))
    			ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("acs.workGroupManagement"), 
    					ApiFactory.getBussinessLogService().getI18nLogInfo("acs.groupAddPerson",messages),ContextUtils.getSystemId("acs"));
    	}
    	this.renderText("ok");
    	return null;
    }
    /**
     * 工作组用户列表
     * @return
     * @throws Exception
     */
    @Action("work-group-getUserByWorkGroup")
    public String getUserByWorkGroup() throws Exception{
    	containBranches=departmentManager.containBranches();
		if(userPage.getPageSize() <= 1){
			User user=userManager.getUserById(ContextUtils.getUserId());
			if(!roleManager.hasSystemAdminRole(user)&&roleManager.hasBranchAdminRole(user)){
				List<BranchAuthority> branchesList=branchAuthorityManager.getBranchByUser(ContextUtils.getUserId());
				for(BranchAuthority branches:branchesList){
					if(StringUtils.isNotEmpty(manageBranchesIds)){
						manageBranchesIds+=",";
					}
					manageBranchesIds+=branches.getBranchesId();
				}
			}
			return "work-group-users"; 
		}else{
			if(workGroupId != null){
				userPage = userInfoManager.queryUsersByWorkGroup(userPage, workGroupId);
			}
			renderHtml(PageUtils.pageToJson(userPage));
			return null;
		}
	}
    
    /**
     * 工作组去除用户(小写字母g)
     * @return
     * @throws Exception
     */
    @Action("work-group-removeWorkgroupToUsers")
    public String removeWorkgroupToUsers() throws Exception{
    	String removeUsers = workGroupManager.workgroupAddUser(workGroupId, userIds, 1);
    	if(StringUtils.isNotEmpty(removeUsers)){
	    	List<String> messages = new ArrayList<String>();//国际化
			messages.add(removeUsers);
	    	ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("acs.workGroupManagement"), 
	    			ApiFactory.getBussinessLogService().getI18nLogInfo("acs.removeWorkgroupUser",messages),ContextUtils.getSystemId("acs"));
    	}
    	return getUserByWorkGroup();
    }

	@Override
	public String input() throws Exception {
		ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("acs.workGroupManagement"), 
				ApiFactory.getBussinessLogService().getI18nLogInfo("acs.updateWorkgroup1"),ContextUtils.getSystemId("acs"));
		return INPUT;
	}

	@Override
	protected void prepareModel() throws Exception {
		if (id != null) {
			workGroup = workGroupManager.getWorkGroup(id);
		} else {
			workGroup = new Workgroup();
		}
	}

	@Override
	public String save() throws Exception {
		workGroupManager.saveWorkGroup(workGroup);
		addActionMessage("保存工作组成功");
		ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("acs.workGroupManagement"), 
				ApiFactory.getBussinessLogService().getI18nLogInfo("acs.saveWorkgroupInfo"),ContextUtils.getSystemId("acs"));
		return RELOAD;
	}

	@Required
	public void setCompanyManager(CompanyManager companyManager) {
		this.companyManager = companyManager;
	}

	public Long getCompanyId() {
		return ContextUtils.getCompanyId();
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public Page<User> getUserPage() {
		return userPage;
	}

	public void setUserPage(Page<User> userPage) {
		this.userPage = userPage;
	}

	public List<Long> getUserIds() {
		return userIds;
	}

	public void setUserIds(List<Long> userIds) {
		this.userIds = userIds;
	}

	public Long getWorkGroupId() {
		return workGroupId;
	}

	public void setWorkGroupId(Long workGroupId) {
		this.workGroupId = workGroupId;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public UserInfoManager getUserInfoManager() {
		return userInfoManager;
	}

	public void setUserInfoManager(UserInfoManager userInfoManager) {
		this.userInfoManager = userInfoManager;
	}

	public void setWorkGroup(Workgroup workGroup) {
		this.workGroup = workGroup;
	}

	public String getWorkGroupName() {
		return workGroupName;
	}

	public void setWorkGroupName(String workGroupName) {
		this.workGroupName = workGroupName;
	}

	public String getWorkGroupCode() {
		return workGroupCode;
	}

	public void setWorkGroupCode(String workGroupCode) {
		this.workGroupCode = workGroupCode;
	}
	
	public Workgroup getModel() {

		return workGroup;
	}

	public Page<Workgroup> getPage() {
		return page;
	}

	public void setPage(Page<Workgroup> page) {
		this.page = page;
	}

	public String temp() throws Exception {
		return SUCCESS;
	}

	@Required
	public void setWorkGroupManager(WorkGroupManager workGroupManager) {
		this.workGroupManager = workGroupManager;
	}

	public List<Workgroup> getAllWorkGroup() {
		return allWorkGroup;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Page<Role> getRolePage() {
		return rolePage;
	}

	public void setRolePage(Page<Role> rolePage) {
		this.rolePage = rolePage;
	}

	public List<Long> getCheckedRoleIds() {
		return checkedRoleIds;
	}

	public void setCheckedRoleIds(List<Long> checkedRoleIds) {
		this.checkedRoleIds = checkedRoleIds;
	}

	public List<Long> getRoleIds() {
		return roleIds;
	}

	public void setRoleIds(List<Long> roleIds) {
		this.roleIds = roleIds;
	}

	public Integer getIsAddOrRomove() {
		return isAddOrRomove;
	}

	public void setIsAddOrRomove(Integer isAddOrRomove) {
		this.isAddOrRomove = isAddOrRomove;
	}

	public List<BusinessSystem> getSystems() {
		return systems;
	}

	public void setSystems(List<BusinessSystem> systems) {
		this.systems = systems;
	}

	public List<Role> getRoleList() {
		return roleList;
	}

	public void setRoleList(List<Role> roleList) {
		this.roleList = roleList;
	}

	public String getPrems1() {
		return prems1;
	}

	public void setPrems1(String prems1) {
		this.prems1 = prems1;
	}

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

	public String getIdes() {
		return ides;
	}

	public void setIdes(String ides) {
		this.ides = ides;
	}

	public String getWfType() {
		return wfType;
	}

	public void setWfType(String wfType) {
		this.wfType = wfType;
	}

	public String getComeFrom() {
		return comeFrom;
	}

	public void setComeFrom(String comeFrom) {
		this.comeFrom = comeFrom;
	}

	public Long getBranchesId() {
		return branchesId;
	}

	public void setBranchesId(Long branchesId) {
		this.branchesId = branchesId;
	}
	
	public String getManageBranchesIds() {
		return manageBranchesIds;
	}
	public Boolean getContainBranches() {
		return containBranches;
	}
}
