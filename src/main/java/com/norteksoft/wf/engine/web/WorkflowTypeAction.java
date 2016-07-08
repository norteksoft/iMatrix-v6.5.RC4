package com.norteksoft.wf.engine.web;

import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;

import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.PageUtils;
import com.norteksoft.product.web.struts2.CrudActionSupport;
import com.norteksoft.product.web.struts2.Struts2Utils;
import com.norteksoft.wf.engine.entity.WorkflowType;
import com.norteksoft.wf.engine.service.WorkflowTypeManager;

@Namespace("/engine")
@ParentPackage("default")
@Results( { @Result(name = CrudActionSupport.RELOAD, location = "workflow-type", type = "redirectAction")})
public class WorkflowTypeAction extends CrudActionSupport<WorkflowType>{
	
	private static final long serialVersionUID = 1L;
	
	private WorkflowTypeManager workflowTypeManager;
	private Long id;
	private String code;
	private WorkflowType basicType;
	private Page<WorkflowType> page = new Page<WorkflowType>(0,true);
	private String name;
	private List<Long> typeIds;
	private Boolean approveSystem;
	
	private static final String SUCCESS_MESSAGE_LEFT = "<font class=\"onSuccess\"><nobr>";
	private static final String ERROR_MESSAGE_LEFT = "<font class=\"onError\"><nobr>";
	private static final String MESSAGE_RIGHT = "</nobr></font>";
	@Autowired	
	public void setWorkflowTypeManager(WorkflowTypeManager workflowTypeManager) {
		this.workflowTypeManager = workflowTypeManager;
	}
	@Override
	@Action("workflow-type-delete")
	public String delete() throws Exception {
		for(int i=0;i<typeIds.size();i++){
			workflowTypeManager.deleteWorkflowType(typeIds.get(i));
		}
		workflowTypeManager.getWorkflowTypePage(page);
		ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("wf.wfType"), 
				ApiFactory.getBussinessLogService().getI18nLogInfo("wf.deleteWorkflowType"), 
				ContextUtils.getSystemId("wf"));
		this.addActionMessage(Struts2Utils.getText("form.delete.success"));
		return list();
	}

	@Override
	@Action("workflow-type-input")
	public String input() throws Exception {
		return "workflow-type-input";
	}

	@Override
	public String list() throws Exception {
		if(page.getPageSize()>1){
			workflowTypeManager.getWorkflowTypePage(page);
			ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("wf.wfType"), 
					ApiFactory.getBussinessLogService().getI18nLogInfo("wf.wflist"), 
					ContextUtils.getSystemId("wf"));
			this.renderText(PageUtils.pageToJson(page));
			return null;
		}
		return "workflow-type";
	}

	@Override
	protected void prepareModel() throws Exception {
		if(id==null){
			basicType = new WorkflowType();
		}else{
			basicType = workflowTypeManager.getWorkflowType(id);
		}
	}

	@Override
	@Action("workflow-type-save")
	public String save() throws Exception {
		if(uniqueValidate(basicType.getName())){
			if(approveSystem==null){
				basicType.setApproveSystem(false);
			}
			workflowTypeManager.saveWorkflowType(basicType);
			ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("wf.wfType"), 
					ApiFactory.getBussinessLogService().getI18nLogInfo("wf.saveWorkflowType"), 
					ContextUtils.getSystemId("wf"));
			this.addSuccessMessage(Struts2Utils.getText("form.save.success"));
		}else{
			this.addErrorMessage(Struts2Utils.getText("wf.dictionary.codeRepetition"));
		}
		return input();
	}
	@Action("workflow-type-validateCode")
	public String validateCode(){
		if(id!=null){
			renderText("ok");
			return null;
		}
		if(workflowTypeManager.validateCode(code)){
			renderText("ok");
		}else{
			renderText("");
		}
		return null;
	}
	private boolean uniqueValidate(String name){
		List<WorkflowType> lists = workflowTypeManager.getWorkflowTypes(name);
		return lists==null || lists.isEmpty()||lists.size()==1&&lists.get(0).equals(basicType);
	}

	public WorkflowType getModel() {
		return basicType;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Page<WorkflowType> getPage() {
		return page;
	}

	public void setTypeIds(List<Long> typeIds) {
		this.typeIds = typeIds;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}
	
	private void addErrorMessage(String message){
		this.addActionMessage(ERROR_MESSAGE_LEFT+message+MESSAGE_RIGHT);
	}
	
	private void addSuccessMessage(String message){
		this.addActionMessage(SUCCESS_MESSAGE_LEFT+message+MESSAGE_RIGHT);
	}
	public Boolean getApproveSystem() {
		return approveSystem;
	}
	public void setApproveSystem(Boolean approveSystem) {
		this.approveSystem = approveSystem;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}

}
