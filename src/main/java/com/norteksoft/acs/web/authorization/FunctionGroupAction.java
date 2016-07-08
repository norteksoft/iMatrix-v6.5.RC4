package com.norteksoft.acs.web.authorization;

import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.norteksoft.acs.base.web.struts2.CRUDActionSupport;
import com.norteksoft.acs.entity.authorization.BusinessSystem;
import com.norteksoft.acs.entity.authorization.Function;
import com.norteksoft.acs.entity.authorization.FunctionGroup;
import com.norteksoft.acs.service.authorization.BusinessSystemManager;
import com.norteksoft.acs.service.authorization.FunctionGroupManager;
import com.norteksoft.acs.service.authorization.FunctionManager;
import com.norteksoft.acs.service.authorization.RoleManager;
import com.norteksoft.product.orm.Page;

/**
 *  author 李洪超 version 
 *  创建时间：2009-3-11 上午09:51:10
 *  部门管理Action
 */
@ParentPackage("default")
@Results( { @Result(name = CRUDActionSupport.RELOAD, location = "function-group?systemId=${systemId}", type = "redirectAction") })
public class FunctionGroupAction extends CRUDActionSupport<FunctionGroup> {

	private static final long serialVersionUID = 4814560124772644966L;
	private FunctionGroupManager functionGroupManager;
	private BusinessSystemManager businessSystemManager;
	private FunctionManager functionManager;
	private RoleManager roleManager;
	private Page<FunctionGroup> page = new Page<FunctionGroup>(20, true);
	private Page<Function> pageFunction = new Page<Function>(20, true);
	private FunctionGroup functionGroup;
	private Long id;
	private List<FunctionGroup> allFunctionGroup;
	private String function_Name;
	private String function_Id;
	private Long paternId;
	private List<Long> functionIds;
	private Long systemId;
	private Integer isAddOrRomove;
	private String systemTree;
	private String nodeId;
	private Long roleId;

	/**
	 * 角色添加移除资源树
	 * @return
	 * @throws Exception
	 */
	@Action("function-group-loadFunctionTree")
	public String loadFunctionTree() throws Exception{
		BusinessSystem system = roleManager.getRole(roleId).getBusinessSystem();
		this.renderText(roleManager.createTree(system,roleId,isAddOrRomove));
		return null;
	}
	@Override
	public String delete() throws Exception {
		return RELOAD;
	}
	@Override
	public String list() throws Exception {
		return SUCCESS;
	}
	
	@Override
	public String input() throws Exception {
		return INPUT;
	}

	@Override
	protected void prepareModel() throws Exception {
		if (id != null) {
			functionGroup = functionGroupManager.getFunctionGroup(id);
		} else {
			functionGroup = new FunctionGroup();
			if(systemId != null){
				BusinessSystem businessSystem = businessSystemManager.getBusiness(systemId);
				functionGroup.setBusinessSystem(businessSystem);
			}
		}
	}

	@Override
	public String save() throws Exception {
		return RELOAD;
	}

	public FunctionGroup getModel() {

		return functionGroup;
	}

	public Page<FunctionGroup> getPage() {
		return page;
	}

	public void setPage(Page<FunctionGroup> page) {
		this.page = page;
	}

	@Required
	public void setFunctionGroupManager(
			FunctionGroupManager functionGroupManager) {
		this.functionGroupManager = functionGroupManager;
	}
	
	@Autowired
	public void setRoleManager(RoleManager roleManager) {
		this.roleManager = roleManager;
	}

	public List<FunctionGroup> getAllFunGroup() {
		return allFunctionGroup;
	}

	public void setId(Long id) {
		this.id = id;
	}


	public Page<Function> getPageFunction() {
		return pageFunction;
	}

	public void setPageFunction(Page<Function> pageFunction) {
		this.pageFunction = pageFunction;
	}
	
	public FunctionManager getFunctionManager() {
		return functionManager;
	}

	@Required
	public void setBusinessSystemManager(BusinessSystemManager businessSystemManager) {
		this.businessSystemManager = businessSystemManager;
	}
	
	@Required
	public void setFunctionManager(FunctionManager functionManager) {
		this.functionManager = functionManager;
	}
	
	public Long getPaternId() {
		return paternId;
	}

	public void setPaternId(Long paternId) {
		this.paternId = paternId;
	}
	
	public List<Long> getFunctionIds() {
		return functionIds;
	}

	public void setFunctionIds(List<Long> functionIds) {
		this.functionIds = functionIds;
	}
	
	public Long getSystemId() {
		return systemId;
	}

	public void setSystemId(Long systemId) {
		this.systemId = systemId;
	}

	public String getFuncGroupsBySystem(){
		if(systemId != null){
			page = functionGroupManager.getFuncGroupsBySystem(page, systemId);
		}
		return SUCCESS;
	}

	public Integer getIsAddOrRomove() {
		return isAddOrRomove;
	}

	public void setIsAddOrRomove(Integer isAddOrRomove) {
		this.isAddOrRomove = isAddOrRomove;
	}

	public String getFunction_Name() {
		return function_Name;
	}

	public void setFunction_Name(String function_Name) {
		this.function_Name = function_Name;
	}

	public String getFunction_Id() {
		return function_Id;
	}

	public void setFunction_Id(String function_Id) {
		this.function_Id = function_Id;
	}

	public void setFunctionGroup(FunctionGroup functionGroup) {
		this.functionGroup = functionGroup;
	}

	public FunctionGroup getFunctionGroup() {
		return functionGroup;
	}

	public String getSystemTree() {
		return systemTree;
	}

	public void setSystemTree(String systemTree) {
		this.systemTree = systemTree;
	}

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}
	
}
