package com.norteksoft.tags.workflow;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.SystemUrls;
import com.norteksoft.product.util.freemarker.TagUtil;
import com.norteksoft.product.web.struts2.Struts2Utils;
import com.norteksoft.wf.engine.entity.WorkflowDefinition;
import com.norteksoft.wf.engine.service.WorkflowDefinitionManager;

public class WorkflowButtonGroupTag extends TagSupport {
	private static final long serialVersionUID = 1L;
	private Log log = LogFactory.getLog(WorkflowButtonGroupTag.class); 
	
	private Long taskId;
	
	private String formCode;
	
	private Integer version;
	
	private WorkflowDefinitionManager workflowDefinitionManager;
	
	private String hiddenButton;
	
	private String webRoot;
	private String name = "workflowButtonGroup";
	
	private String submitForm;//保存、提交、同意....等按钮提交时的form的id
	
	private Boolean showOtherButton=true;
	@Override
	public int doStartTag() throws JspException{
		workflowDefinitionManager = (WorkflowDefinitionManager)ContextUtils.getBean("workflowDefinitionManager");
		if(taskId==null){
			taskId = 0l;
		}
		if(taskId != null && taskId != 0l){
			com.norteksoft.product.api.entity.WorkflowTask task = ApiFactory.getTaskService().getTask(taskId);
			com.norteksoft.product.api.entity.WorkflowInstance workflow = ApiFactory.getInstanceService().getInstance(task.getProcessInstanceId());
			Long definitionId = workflow.getWorkflowDefinitionId();
			WorkflowDefinition definition = workflowDefinitionManager.getWfDefinition(definitionId);
			formCode = definition.getFormCode();
			version = definition.getFromVersion();
		}
		try {
			 JspWriter out=pageContext.getOut(); 
			 out.print(readTemplet());
		} catch (Exception e) {
			log.error(e);
			throw new JspException(e);
		}
		return Tag.EVAL_PAGE;
	 }
	
	
	private String readTemplet() throws Exception {
		webRoot = ((HttpServletRequest)this.pageContext.getRequest()).getContextPath();
		com.norteksoft.product.api.entity.WorkflowTask task = null;
		com.norteksoft.product.api.entity.WorkflowInstance workflow = null;
		boolean isFirstTask=false;
		if(taskId!=null&&taskId!=0l){
			task = ApiFactory.getTaskService().getTask(taskId);
			workflow = ApiFactory.getInstanceService().getInstance(task.getProcessInstanceId());
			if(taskId.equals(workflow.getFirstTaskId())){
				isFirstTask=true;
			}
		}
		//将按钮国际化
		if(task!=null)setButtonNameToi18n(task);
		String webRoot = SystemUrls.getSystemPageUrl(ContextUtils.getSystemCode());
		Map<String, Object> root = new HashMap<String, Object>();
		root.put("formCode", formCode);
		root.put("version", version);
		root.put("ctx", webRoot);
		root.put("taskId", taskId);
		root.put("task", task);
		root.put("workflow", workflow);
		root.put("workflowButtonGroupName", name);
		root.put("isFirstTask", isFirstTask);
		root.put("companyId", ContextUtils.getCompanyId().toString());
		root.put("submitForm", submitForm==null?"":submitForm);
		root.put("showOtherButton", showOtherButton==null?true:showOtherButton);
		root.put("hiddenButton", StringUtils.isEmpty(hiddenButton)?"":hiddenButton);
		root.put("webRoot", webRoot);
		String result =TagUtil.getContent(root, "workflow/workflowButtonGroup.ftl");
		return result;
	}
	private void setButtonNameToi18n(com.norteksoft.product.api.entity.WorkflowTask task){
		task.setSubmitButton(getButtonNameForI18n(task.getSubmitButton()));
		task.setAddSignerButton(getButtonNameForI18n(task.getAddSignerButton()));
		task.setRemoveSignerButton(getButtonNameForI18n(task.getRemoveSignerButton()));
		task.setAgreeButton(getButtonNameForI18n(task.getAgreeButton()));
		task.setDisagreeButton(getButtonNameForI18n(task.getDisagreeButton()));
		task.setSignForButton(getButtonNameForI18n(task.getSignForButton()));
		task.setApproveButton(getButtonNameForI18n(task.getApproveButton()));
		task.setOpposeButton(getButtonNameForI18n(task.getOpposeButton()));
		task.setAbstainButton(getButtonNameForI18n(task.getAbstainButton()));
		task.setAssignButton(getButtonNameForI18n(task.getAssignButton()));
		task.setSaveButton(getButtonNameForI18n(task.getSaveButton()));
		task.setAppointButton(getButtonNameForI18n(task.getAppointButton()));
		task.setCopyButton(getButtonNameForI18n(task.getCopyButton()));
		task.setDrawButton(getButtonNameForI18n(task.getDrawButton()));
		task.setReadButton(Struts2Utils.getText("read.name"));
		task.setBackButton(getButtonNameForI18n(task.getBackButton()));
		task.setAbandonButton(getButtonNameForI18n(task.getAbandonButton()));
	}
	
	private String getButtonNameForI18n(String name){
		if(StringUtils.isEmpty(name))return name;
		if(name.contains("${")&&name.contains("}")){
			name = name.substring(name.indexOf("${")+2,name.indexOf("}"));
			return Struts2Utils.getText(name);
		}
		return name;
	}
	@Override
	public int doEndTag() throws JspException {
		return Tag.EVAL_PAGE;
	}
	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public void setFormCode(String formCode) {
		this.formCode = formCode;
	}
	public void setShowOtherButton(Boolean showOtherButton) {
		this.showOtherButton = showOtherButton;
	}
	public void setHiddenButton(String hiddenButton) {
		this.hiddenButton = hiddenButton;
	}


	public void setSubmitForm(String submitForm) {
		this.submitForm = submitForm;
	}
}
