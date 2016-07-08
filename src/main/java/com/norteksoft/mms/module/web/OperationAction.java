package com.norteksoft.mms.module.web;

import java.util.ArrayList;
import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;

import com.norteksoft.acs.entity.authorization.BusinessSystem;
import com.norteksoft.acs.service.authorization.BusinessSystemManager;
import com.norteksoft.mms.module.entity.Menu;
import com.norteksoft.mms.module.entity.Operation;
import com.norteksoft.mms.module.service.MenuManager;
import com.norteksoft.mms.module.service.OperationManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.JsTreeUtils;
import com.norteksoft.product.util.JsonParser;
import com.norteksoft.product.util.PageUtils;
import com.norteksoft.product.util.tree.ZTreeNode;
import com.norteksoft.product.web.struts2.CrudActionSupport;
import com.norteksoft.product.web.struts2.Struts2Utils;
/**
 * 功能管理
 * @author liudongxia
 *
 */
@Namespace("/module")
@ParentPackage("default")
@Results( { @Result(name = CrudActionSupport.RELOAD, location = "operation", type = "redirectAction")})
public class OperationAction extends CrudActionSupport<Operation> {
	private static final long serialVersionUID = 1L;
	private Page<Operation> pages=new Page<Operation>(0,true);
	private Page<Operation> operationChildren=new Page<Operation>(0,true);
	private Operation operation;
	private Long operationId;
	private Long id;//主子表表格控件用到
	private String ids;
	private String operationCode;//编码
	private Long systemId;//系统id
	
	@Autowired
	private OperationManager operationManager;
	@Autowired
	private BusinessSystemManager businessSystemManager;
	@Autowired
	private MenuManager menuManager;
	private static final String SUCCESS_MESSAGE_LEFT = "<font class=\"onSuccess\"><nobr>";
	private static final String ERROR_MESSAGE_LEFT = "<font class=\"onError\"><nobr>";
	private static final String MESSAGE_RIGHT = "</nobr></font>";
	private void addErrorMessage(String message){
		this.addActionMessage(ERROR_MESSAGE_LEFT+message+MESSAGE_RIGHT);
	}
	
	private void addSuccessMessage(String message){
		this.addActionMessage(SUCCESS_MESSAGE_LEFT+message+MESSAGE_RIGHT);
	}
	@Override
	@Action("operation-delete")
	public String delete() throws Exception {
		operationManager.deleteOperations(ids);
		addSuccessMessage(Struts2Utils.getText("form.delete.success"));//删除成功
		ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("mms.functionManagement"), ApiFactory.getBussinessLogService().getI18nLogInfo("mms.deleteFunction"),ContextUtils.getSystemId("mms"));
		return "operation";
	}

	@Override
	@Action("operation-input")
	public String input() throws Exception {
		ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("mms.functionManagement"), ApiFactory.getBussinessLogService().getI18nLogInfo("mms.functionManagerMenu"),ContextUtils.getSystemId("mms"));
		return "operation-input";
	}

	@Override
	public String list() throws Exception {
		List<BusinessSystem> businessSystems= businessSystemManager.getAllSystems();
		if(businessSystems.size()>0){
			if(systemId==null)systemId=businessSystems.get(0).getId();
		}
		operationManager.dealwithOperation(systemId);
		if(pages.getPageSize()>1){
			operationManager.getOperations(pages,systemId);
			this.renderText(PageUtils.pageToJson(pages));
			return null;
		}
		ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("mms.functionManagement"), ApiFactory.getBussinessLogService().getI18nLogInfo("mms.ViewFunctionList"),ContextUtils.getSystemId("mms"));
		return SUCCESS;
	}

	/**
	 * 子功能列表
	 * @return
	 */
	@Action("operation-chiledList")
	public String chiledList() throws Exception {
		if(operationChildren.getPageSize()>1){
			if(id!=null){
				operationManager.getOperationChildren(operationChildren,id);
				this.renderText(PageUtils.pageToJson(operationChildren));
			}
		}
		return null;
	}
	
	/**
	 * 删除子功能
	 * @return
	 * @throws Exception
	 */
	@Action("delete-child-operation")
	public String deleteInternationOption() throws Exception {
		operationManager.deleteOperation(id);
		String callback=Struts2Utils.getParameter("callback");
		ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("mms.functionManagement"), 
				ApiFactory.getBussinessLogService().getI18nLogInfo("mms.deleteChildFunction"), 
				ContextUtils.getSystemId("mms"));
		this.renderText(callback+"({msg:'"+Struts2Utils.getText("form.delete.success")+"'})");
		return null;
	}
	
	@Override
	@Action("operation-save")
	public String save() throws Exception {
		operationManager.saveOperation(operation);
		operationId=operation.getId();
		addSuccessMessage(Struts2Utils.getText("form.save.success"));
		ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("mms.functionManagement"), ApiFactory.getBussinessLogService().getI18nLogInfo("mms.saveFunctionManager"),ContextUtils.getSystemId("mms"));
		return "operation-input";
	}
	
	/**
	 * 得到所有系统树
	 * @return
	 * @throws Exception
	 */
	@Action("operation-system-tree")
	public String systemTree() throws Exception {
		List<ZTreeNode> systemNodes = new ArrayList<ZTreeNode>();
//		List<BusinessSystem> businessSystems= businessSystemManager.getAllBusiness();
//		for(BusinessSystem system :businessSystems){
//			ZTreeNode node = new ZTreeNode(system.getId().toString(),"0",menuManager.getNameToi18n(system.getName()), "true", "false", "", "", "folder", "");
//			systemNodes.add(node);
//		}
		List<Menu> menus = menuManager.getAllEnabledStandardRootMenus();
		for(Menu menu :menus){
			ZTreeNode node = new ZTreeNode(menu.getSystemId().toString(),"0",menuManager.getNameToi18n(menu.getName()), "true", "false", "", "", "folder", "");
			systemNodes.add(node);
		}
		renderText(JsonParser.object2Json(systemNodes));
		return null;
	}
	/**
	 * 验证编码是否存在
	 * @return
	 * @throws Exception
	 */
	@Action("operation-check-code")
	public String checkCode() throws Exception {
		this.renderText(operationManager.isOperationExist(operationCode,operationId,systemId)+"");
		return null;
	}
	
	@Override
	protected void prepareModel() throws Exception {
		if(operationId==null){
			operation=new Operation();
		}else{
			operation=operationManager.getOperation(operationId);
		}
	}

	public Operation getModel() {
		return operation;
	}

	public Page<Operation> getPages() {
		return pages;
	}

	public void setOperationId(Long operationId) {
		this.operationId = operationId;
	}

	public Long getOperationId() {
		return operationId;
	}
	
	public Page<Operation> getOperationChildren() {
		return operationChildren;
	}

	public Operation getOperation() {
		return operation;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

	public void setOperationCode(String operationCode) {
		this.operationCode = operationCode;
	}

	public Long getSystemId() {
		return systemId;
	}

	public void setSystemId(Long systemId) {
		this.systemId = systemId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	

}
