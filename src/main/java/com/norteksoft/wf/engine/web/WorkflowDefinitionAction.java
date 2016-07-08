package com.norteksoft.wf.engine.web;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.norteksoft.acs.entity.authorization.BusinessSystem;
import com.norteksoft.acs.service.authorization.BusinessSystemManager;
import com.norteksoft.mms.base.DynamicColumnValues;
import com.norteksoft.mms.base.ExportDynamicColumnValues;
import com.norteksoft.mms.base.utils.view.DynamicColumnDefinition;
import com.norteksoft.mms.base.utils.view.ExportData;
import com.norteksoft.mms.form.entity.FormView;
import com.norteksoft.mms.form.entity.ListColumn;
import com.norteksoft.mms.form.entity.ListView;
import com.norteksoft.mms.form.entity.TableColumn;
import com.norteksoft.mms.form.enumeration.DataType;
import com.norteksoft.mms.form.service.FormViewManager;
import com.norteksoft.mms.form.service.ListViewManager;
import com.norteksoft.mms.module.entity.Menu;
import com.norteksoft.mms.module.service.MenuManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.ExcelExportEnum;
import com.norteksoft.product.util.ExcelExporter;
import com.norteksoft.product.util.PageUtils;
import com.norteksoft.product.util.SearchUtils;
import com.norteksoft.product.util.SystemUrls;
import com.norteksoft.product.web.struts2.CrudActionSupport;
import com.norteksoft.product.web.struts2.Struts2Utils;
import com.norteksoft.task.entity.HistoryWorkflowTask;
import com.norteksoft.task.entity.WorkflowTask;
import com.norteksoft.wf.base.enumeration.CommonStrings;
import com.norteksoft.wf.base.enumeration.ProcessType;
import com.norteksoft.wf.engine.entity.HistoryWorkflowInstance;
import com.norteksoft.wf.engine.entity.WorkflowDefinition;
import com.norteksoft.wf.engine.entity.WorkflowDefinitionTemplate;
import com.norteksoft.wf.engine.entity.WorkflowInstance;
import com.norteksoft.wf.engine.entity.WorkflowType;
import com.norteksoft.wf.engine.service.HistoryWorkflowInstanceManager;
import com.norteksoft.wf.engine.service.TaskService;
import com.norteksoft.wf.engine.service.WorkflowDefinitionManager;
import com.norteksoft.wf.engine.service.WorkflowInstanceManager;
import com.norteksoft.wf.engine.service.WorkflowTypeManager;

@Namespace("/engine")
@ParentPackage("default")
@Results( { @Result(name = CrudActionSupport.RELOAD, location = "workflow-definition?wfdId=${wfdId}&wfdFile=${wfdFile}", type = "redirectAction")})
public class WorkflowDefinitionAction extends CrudActionSupport<WorkflowDefinition>{
	
	private static final long serialVersionUID = 1L;
	private Long wfdId;
	private String workflowId;//流程实例ID
	private Set<Long> workflowIds;
	private Set<WorkflowInstance> workflowInstances;
	private Set<HistoryWorkflowInstance> historyworkflowInstances;
	private WorkflowInstance workflowInstance;
	private WorkflowDefinitionManager workflowDefinitionManager;
	private WorkflowInstanceManager workflowInstanceManager;
	private WorkflowTypeManager workflowTypeManager;
	private FormViewManager formViewManager;
	private ListViewManager listViewManager;
	private Page<WorkflowDefinition> wfdPage = new Page<WorkflowDefinition>(0, true);
	private Page<WorkflowTask> tasks=new Page<WorkflowTask>(0, true);
	private Page<Object> taskInfos=new Page<Object>(0, true);
	private Page<HistoryWorkflowTask> historyTasks= new Page<HistoryWorkflowTask>(0, true);
	private Page<Object> wiPage = new Page<Object>(0,true);
	private String xmlFile;
	private Long defCompanyId;
	private String defCreator;
	private String defCreatorName;
	private String currentorLoginName;
	private String currentorName;
	private List<WorkflowType> typeList;
	private String searchCdn;
	private Long type = 0l;//流程类型id
	private Long sysId = 0l ;//系统id
	private List<String> titleList;
	private WorkflowDefinition workflowDefinition;
	private List<ListColumn> displayField = new ArrayList<ListColumn>();
	private List<Menu> menus = new ArrayList<Menu>();
	private String tree;
	private String firstTreeId;
	private String wfDefinitionId;
	private String formHtml;
	private List<WorkflowDefinitionTemplate> templates;
	private Long templateId;
	private Long defSystemId;
	private String option;
	private String formType;
	private String processId;// 流程定义的id
	private Long formId;
	private String fieldPermission; //字段的编辑权限
	private List<Long> wfdIds;
	private List<String> operates;
	private List<String> searchValues;
	private String vertionType="ENABLE";
	
	private List<FormView> forms;
	
	private String formCode;
	private Integer version;
	private List<WorkflowDefinition> definitions;
	private String definitionCode;
	private List<String> enNames;
	private List<String> chNames;
	private List<String> dataTypes;
	private String position;
	private Long instanceId;//流程实例的记录id
	private String url;
	private String operationName;//流程监控中做的什么操作:查看流程实例（view）/应急处理(urgenDone)
	private String transactorName;//批量移除任务页面传来的办理人登录名
	private List<Long> taskIds;//需批量移除的任务id
	private List<BusinessSystem> systems;//所有系统
	
	private Long transactorId;//办理人id
	private String transactor;//办理人登录名
	private String parentSearchParameters;//超期人统计中的查询条件
	private Long taskId;
	
	private static final String SUCCESS_MESSAGE_LEFT = "<font class=\"onSuccess\"><nobr>";
	private static final String MESSAGE_RIGHT = "</nobr></font>";
	
	@Autowired
	private BusinessSystemManager businessSystemManager;
	@Autowired
	private MenuManager menuManager;
	
	@Autowired
	private HistoryWorkflowInstanceManager historyWorkflowInstanceManager;
	@Autowired
	private TaskService taskService;
	private String systemCode;
	private Map<String, List<WorkflowTask>> chooseTasks;
	
	private List<com.norteksoft.product.api.entity.Department> branches;//分支机构列表
	private boolean hasBranch;//是否存在分支机构
	private String subCompanyCode;//流程所属分支机构编码
	
	private String lastTransactTimeStart;//最后办理时间开始
	private String lastTransactTimeEnd;//最后办理时间结束
	
	private List<DynamicColumnDefinition> dynamicColumn=new ArrayList<DynamicColumnDefinition>();
	/**
	 * 模版列表
	 * @return
	 * @throws Exception
	 */
	@Action("workflow-definition-selectTemplate")
	public String template() throws Exception {
		typeList = workflowTypeManager.getAllWorkflowType();
		if(type.equals(0l)){
			if(typeList!=null && typeList.size()>0){
				templates = workflowDefinitionManager.getWorkflowDefinitionTemplates(typeList.get(0).getId());
			}
		}else{
			templates = workflowDefinitionManager.getWorkflowDefinitionTemplates(type);
		}
		return "workflow-definition-template";
	}
	@Action("workflow-definition-templateList")
	public String templateList() throws Exception {
		templates = workflowDefinitionManager.getWorkflowDefinitionTemplates(type);
		return "workflow-definition-template";
	}
	
	public String getActiveDefinition() throws Exception{
		workflowDefinitionManager.getActiveDefinition(wfdPage);
		return "";
	}
	
	/**
	 * 流程启用与禁用
	 * @return
	 * @throws Exception
	 */
	@Action("workflow-definition-deploy")
	public String deploy() throws Exception{
		ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("acs.workflowDefinition"), 
				ApiFactory.getBussinessLogService().getI18nLogInfo("wf.workflowDefinitionOpeation"), 
				ContextUtils.getSystemId("wf"));
		this.renderText(SUCCESS_MESSAGE_LEFT+workflowDefinitionManager.deployProcess(wfdId)+MESSAGE_RIGHT);
		return null;
	}
	
	
	public String getFirstTreeId() {
		return firstTreeId;
	}
	
	public String getTree() {
		return tree;
	}
	
	public String getWfDefinitionId() {
		return wfDefinitionId;
	}
	
	public void setFormHtml(String formHtml) {
		this.formHtml = formHtml;
	}
	
	public String getFormHtml() {
		return formHtml;
	}
	
	@Action("workflow-definition-view")
	public String view() throws Exception{
		ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("acs.workflowDefinition"), 
				ApiFactory.getBussinessLogService().getI18nLogInfo("wf.viewWDefinition"), 
				ContextUtils.getSystemId("wf"));
		workflowDefinition = workflowDefinitionManager.getWfDefinition(wfdId);
		FormView form=formViewManager.getCurrentFormViewByCodeAndVersion(workflowDefinition.getFormCode(), workflowDefinition.getFromVersion());
		if(form==null){
			return "viewFaild";
		}else{
			formHtml = form.getHtml();
			wfDefinitionId = workflowDefinitionManager.getWfDefinition(wfdId).getProcessId();
			return "workflow-definition-view";
		}
	}
	
	/**
	 * 删除还没有部署的流程定义
	 */
	@Override
	@Action("workflow-definition-delete")
	public String delete() throws Exception {
		if(wfdIds!=null){
			int num = workflowDefinitionManager.deleteWfDefinitions(wfdIds);
			ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("acs.workflowDefinition"), 
					ApiFactory.getBussinessLogService().getI18nLogInfo("wf.deleteWorkflowDefinition"), 
					ContextUtils.getSystemId("wf"));
			this.renderText(num+Struts2Utils.getText("wf.engine.delegate.deleted")+"；"+(wfdIds.size()-num)+Struts2Utils.getText("wf.test.areEnabledOr"));
		}
		return null;
	}
	
	/**
	 * 管理员删除流程实例
	 */
	@Action("workflow-definition-deleteWorkflow")
	public String deleteWorkflow() throws Exception {
		ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("acs.workflowMonitor"), 
				ApiFactory.getBussinessLogService().getI18nLogInfo("wf.deleteWorkflowInstance"), 
				ContextUtils.getSystemId("wf"));
		this.renderText(workflowInstanceManager.deleteWorkflowInstances(workflowInstances));
		return null;
	}
	
	public void prepareDeleteWorkflowHistory() throws Exception{
		if(workflowIds!=null&&!workflowIds.isEmpty()){
			historyworkflowInstances = historyWorkflowInstanceManager.getHistoryWorkflowInstances(workflowIds);
		}
	}
	
	/**
	 * 管理员删除历史流程实例
	 */
	@Action("workflow-definition-deleteWorkflowHistory")
	public String deleteWorkflowHistory() throws Exception {
		ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("acs.workflowMonitor"), 
				ApiFactory.getBussinessLogService().getI18nLogInfo("wf.deleteWorkflowInstance"),
				ContextUtils.getSystemId("wf"));
		this.renderText(workflowInstanceManager.deleteWorkflowInstancesHistory(historyworkflowInstances));
		return null;
	}

	@Override
	@Action("workflow-definition-input")
	public String input() throws Exception {
		if(templateId!=null&& WorkflowDefinitionTemplate.CUSTOM_PROCESS_TEMPLATE.equals(workflowDefinitionManager.getWorkflowDefinitionTemplate(templateId).getTemplateType())){
			getFormInfo();
			return "customProcess";
		}else{
			boolean hasBranch = ContextUtils.hasBranch();
			if(hasBranch){
				String subCompanyCode = ContextUtils.getSubCompanyCode();
				if(StringUtils.isEmpty(subCompanyCode)){
					currentorLoginName = ContextUtils.getLoginName()+"["+ContextUtils.getCompanyCode()+"]";
					currentorName = ContextUtils.getUserName()+"/"+ContextUtils.getCompanyName();
				}else{
					currentorLoginName = ContextUtils.getLoginName()+"["+subCompanyCode+"]";
					currentorName = ContextUtils.getUserName()+"/"+ContextUtils.getSubCompanyName();
				}
			}else{
				currentorLoginName = ContextUtils.getLoginName();
				currentorName = ContextUtils.getUserName();
			}
			if(workflowDefinition.getId()==null){
				defCreator = currentorLoginName;
				defCreatorName = currentorName;
			}else{
				defCreator = workflowDefinition.getCreator()+"["+ContextUtils.getCompanyCode()+"]";
				defCreatorName = workflowDefinition.getCreatorName();
			}
			defSystemId = ContextUtils.getSystemId();
			defCompanyId = ContextUtils.getCompanyId();
			return INPUT;
		}
	}
	
	private void getFormInfo(){
		forms = formViewManager.getFormViewsByCompany();
		FormView temp = new FormView();
		temp.setName("请选择表单");
		forms.add(0, temp);
	}
	public void prepareUpdate() throws Exception{
		prepareModel();
	}
	@Action("workflow-definition-update")
	public String update() throws Exception {
		if(ProcessType.CUSTOM_PROCESS.equals(workflowDefinition.getProcessType())){
			type = workflowDefinitionManager.getWfDefinition(wfdId).getTypeId();
			getFormInfo();
			return "customProcess";
		}else{
			if(wfdId==null&&(type==null || type.intValue() == 0)){
				type=0l;
			}else{//修改流程定义时
				type = workflowDefinitionManager.getWfDefinition(wfdId).getTypeId();
			}
			boolean hasBranch = ContextUtils.hasBranch();
			if(hasBranch){
				String subCompanyCode = ContextUtils.getSubCompanyCode();
				if(StringUtils.isEmpty(subCompanyCode)){
					currentorLoginName = ContextUtils.getLoginName()+"["+ContextUtils.getCompanyCode()+"]";
					currentorName = ContextUtils.getUserName()+"/"+ContextUtils.getCompanyName();
				}else{
					currentorLoginName = ContextUtils.getLoginName()+"["+subCompanyCode+"]";
					currentorName = ContextUtils.getUserName()+"/"+ContextUtils.getSubCompanyName();
				}
			}else{
				currentorLoginName = ContextUtils.getLoginName();
				currentorName = ContextUtils.getUserName();
			}
			if(workflowDefinition.getId()==null){
				defCreator = currentorLoginName;
				defCreatorName = currentorName;
			}else{
				defCreator = workflowDefinition.getCreator()+"["+ContextUtils.getCompanyCode()+"]";
				defCreatorName = workflowDefinition.getCreatorName();
			}
			defCompanyId = ContextUtils.getCompanyId();
			xmlFile=workflowDefinitionManager.getXmlByDefinitionId(wfdId, defCompanyId);
			defSystemId = ContextUtils.getSystemId();
			return "workflow-definition-update";
		}
	}

	@Override
	public String save() throws Exception {
		workflowDefinitionManager.saveWorkflowDefinition(wfdId, ContextUtils.getCompanyId(), xmlFile,type,ContextUtils.getSystemId());
		return RELOAD;
	}
	public String saveCustomProcess() throws Exception {
		FormView form = formViewManager.getFormView(formId);
		workflowDefinition.setFormName(form.getName());
		workflowDefinition.setFromVersion(form.getVersion());
		workflowDefinition.setVersion(workflowDefinitionManager.generateWorkflowDefinitionVersion(workflowDefinition.getName()));
		workflowDefinitionManager.saveWorkflowDefinition(workflowDefinition);
		wfdId = workflowDefinition.getId();
		getFormInfo();
		return "customProcess";
	}
	
	public void prepareSaveCustomProcess() throws Exception{
		if(wfdId==null){
			createWorkflowDefinition();
		}else{
			workflowDefinition = workflowDefinitionManager.getWfDefinition(wfdId);
		}
	}
	private WorkflowDefinition createWorkflowDefinition(){
		workflowDefinition = new WorkflowDefinition();
		workflowDefinition.setProcessType(ProcessType.CUSTOM_PROCESS);
		workflowDefinition.setSystemId(ContextUtils.getSystemId());
		workflowDefinition.setCompanyId(ContextUtils.getCompanyId());
		workflowDefinition.setCreator(ContextUtils.getLoginName());
		workflowDefinition.setCreatedTime(new Date());
		workflowDefinition.setTypeId(type);
		return workflowDefinition;
	}

	@Override
	public String list() throws Exception {
		return SUCCESS;
	}
	@Action("workflow-definition-data")
	public String data(){
		if(wfdPage.getPageSize()>1){
			ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("acs.workflowDefinition"), 
					ApiFactory.getBussinessLogService().getI18nLogInfo("wf.workflowDefList"), 
					ContextUtils.getSystemId("wf"));
			typeList = workflowTypeManager.getAllWorkflowType();
			if(type==null || type.intValue() == 0||sysId==null||sysId.intValue()==0){
				workflowDefinitionManager.getWfDefinitions(wfdPage,vertionType,ContextUtils.getLoginName(),ContextUtils.getUserId());
				this.renderText(PageUtils.pageToJson(wfdPage));
				return null;
			}else if(type!=null&&type.intValue() != -1){
				workflowDefinitionManager.getWfDefinitions(wfdPage,type,vertionType,ContextUtils.getLoginName(),ContextUtils.getUserId());
				this.renderText(PageUtils.pageToJson(wfdPage));
				return null;
			}else if(sysId!=null&&sysId.intValue() != -1){
				workflowDefinitionManager.getWfDefinitionsBySystemId(wfdPage,sysId,vertionType,ContextUtils.getLoginName(),ContextUtils.getUserId());
				this.renderText(PageUtils.pageToJson(wfdPage));
				return null;
			}
		}
		return "workflow-definition-data";
	}
	/**
	 * 取消流程
	 */
	@Action("workflow-definition-endWorkflow")
	public String endWorkflow() throws Exception {
		String msg = workflowInstanceManager.endWorkflowInstance(workflowIds);
		ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("acs.workflowDefinition"), 
				ApiFactory.getBussinessLogService().getI18nLogInfo("wf.cancelWorkflow"), 
				ContextUtils.getSystemId("wf"));
		this.renderText(msg);
		return null;
	}
	
	/**
	 * 强制结束流程
	 */
	@Action("workflow-definition-compelEndWorkflow")
	public String compelEndWorkflow() throws Exception {
		String msg = workflowInstanceManager.compelEndWorkflowInstance(workflowIds);
		ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("acs.workflowDefinition"), 
				ApiFactory.getBussinessLogService().getI18nLogInfo("	wf.forceEndWorkflow"), 
				ContextUtils.getSystemId("wf"));
		this.renderText(msg);
		return null;
	}
	

	/**
	 * 流程监控
	 */
	@Action("workflow-definition-monitor")
	public String monitor() throws Exception {
		if(wiPage.getPageSize() > 1){
			workflowDefinition = workflowDefinitionManager.getWfDefinition(wfdId);
			
			workflowDefinitionManager.monitor(wiPage,workflowDefinition);
			ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("acs.workflowDefinition-Monitor"), 
					ApiFactory.getBussinessLogService().getI18nLogInfo("wf.workflowInstanceList"), 
					ContextUtils.getSystemId("wf"));
			renderText(PageUtils.pageToJson(wiPage));
			return null;
		}
		return "monitor";
	}
	
	/**
	 * 流程监控
	 */
	@Action("workflow-definition-monitorHistory")
	public String monitorHistory() throws Exception {
		if(wiPage.getPageSize() > 1){
			workflowDefinition = workflowDefinitionManager.getWfDefinition(wfdId);
			
			workflowDefinitionManager.monitorHistory(wiPage,workflowDefinition);
			ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("acs.workflowDefinition-Monitor"), 
					ApiFactory.getBussinessLogService().getI18nLogInfo("wf.historyWfList"), 
					ContextUtils.getSystemId("wf"));
			renderText(PageUtils.pageToJson(wiPage));
			return null;
		}
		return "workflow-definition-monitor-history";
	}
	
	/**
	 * 流程监控管理
	 */
	@Action("workflow-definition-monitorDefintion")
	public String monitorDefintion() throws Exception {
		if(wiPage.getPageSize()>1){
			workflowDefinitionManager.monitorDefinition(wiPage,type,definitionCode);
			ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("acs.workflowMonitorModule"), 
					ApiFactory.getBussinessLogService().getI18nLogInfo("wf.workflowInstanceList"), 
					ContextUtils.getSystemId("wf"));
			this.renderText(PageUtils.pageToJson(wiPage));
			return null;
		}
		return "workflow-definition-monitorStandardManager";
	}
	/**
	 * 历史流程监控管理
	 */
	@Action("workflow-definition-monitorDefintionHistory")
	public String monitorDefintionHistory() throws Exception {
		if(wiPage.getPageSize()>1){
			workflowDefinitionManager.monitorDefinitionHistory(wiPage,type,definitionCode);
			ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("acs.workflowMonitorModule"), 
					ApiFactory.getBussinessLogService().getI18nLogInfo("wf.workflowInstanceList"), 
					ContextUtils.getSystemId("wf"));
			this.renderText(PageUtils.pageToJson(wiPage));
			return null;
		}
		return "workflow-definition-monitorStandardManagerHistory";
	}
	
	/**
	 * 查询流程实例
	 */
	@Action("workflow-definition-search")
	public String search() throws Exception {
		workflowDefinition = workflowDefinitionManager.getWfDefinition(wfdId);
		List<WorkflowInstance> wiList = workflowInstanceManager.getAllWorkflowInstances(wfdId,workflowDefinition.getSystemId());//流程定义所有的实例
		List<WorkflowInstance> wiEndList = workflowInstanceManager.getAllEndWorkflowInstances(wfdId,workflowDefinition.getSystemId());//流程定义所有结束的实例
		if(wiList!=null)workflowDefinition.setInstanceCount(wiList.size());
		if(wiEndList!=null)workflowDefinition.setEndCount(wiEndList.size());
		FormView form = formViewManager.getCurrentFormViewByCodeAndVersion(workflowDefinition.getFormCode(), workflowDefinition.getFromVersion());
		ListView listView=listViewManager.getDefaultDisplay(form.getDataTable().getId());
		if(listView!=null){
			for(ListColumn column:listView.getColumns()){
				if(column.getVisible()){
					displayField.add(column);
				}
			}
		}
		workflowDefinitionManager.searchMonitor(wiPage,workflowDefinition,getSearchManagerFields());
		ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("acs.workflowMonitor"), 
				ApiFactory.getBussinessLogService().getI18nLogInfo("	wf.searchWfInstance"), 
				ContextUtils.getSystemId("wf"));
//		if(form.isStandardForm()){
//			return "monitorStandardForm";
//		}else{
			return "workflow-definition-monitor";
//		}
	}
	
//	public String searchManager() throws Exception{
//		workflowDefinitionManager.searchManagerMonitor(wiPage,type,definitionCode,getSearchManagerFields());
//		return "monitorStandardManager";
//	}
	
	private List<ListColumn> getSearchManagerFields(){
		if(enNames==null)return null;
		List<ListColumn> fields = new ArrayList<ListColumn>();
		ListColumn field = null;
		for(int i=0;i<enNames.size();i++){
			field =new ListColumn();
			TableColumn tb=new TableColumn();
			field.setTableColumn(tb);
			field.getTableColumn().setName(enNames.get(i));
			field.getTableColumn().setAlias(chNames.get(i));
			field.getTableColumn().setDataType(DataType.valueOf(dataTypes.get(i).toUpperCase()));
			field.getTableColumn().setOperate(operates.get(i));
			field.getTableColumn().setSearchValue(searchValues.get(i));
			fields.add(field);
		}
		return fields;
	}
	/**
	 * 根据系统编码获得系统url
	 * @return
	 * @throws Exception
	 */
	@Action("obtain-system-url")
	public String obtainSystemUrl() throws Exception{
		BusinessSystem system=businessSystemManager.getSystemBySystemCode(systemCode);
		if(system==null||StringUtils.isEmpty(system.getPath())){
			this.renderText("");
		}else{
			this.renderText(system.getPath());
		}
		return null;
	}
	/**
	 * 流程监控/查看表单和应急处理入口
	 * @return
	 * @throws Exception
	 */
	@Action("monitor-view")
	public String monitorView() throws Exception{
		WorkflowInstance instance=workflowInstanceManager.getWorkflowInstance(instanceId);
		if(instance!=null){
			if("view".equals(operationName)){
				url=instance.getFormUrl();
			}else if("urgenDone".equals(operationName)){
				url=instance.getEmergencyUrl();
			}
			if(StringUtils.isNotEmpty(url)){
				if(url.indexOf("?")!=-1){
					url = url+instance.getDataId()+"&instanceId="+instance.getProcessInstanceId();
				}else{
					url = url + "?id="+instance.getDataId()+"&instanceId="+instance.getProcessInstanceId();
				}
				if(!url.startsWith("http")){
					int index = url.indexOf("/");
					String code = url.substring(0, index);
					String systemUrl=SystemUrls.getSystemUrl(code);
					if(StringUtils.isNotEmpty(systemUrl))
						url = systemUrl + url.substring(index, url.length());
				}
				url = url+"&_r=1";
			}
		}
		ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("acs.workflowMonitor"), 
				ApiFactory.getBussinessLogService().getI18nLogInfo("wf.viewFormOrUrgent"), 
				ContextUtils.getSystemId("wf"));
		return "monitor-view";
	}
	
	/**
	 * 流程监控/查看表单和应急处理入口(历史实例)
	 * @return
	 * @throws Exception
	 */
	@Action("monitor-view-history")
	public String monitorHistoryView() throws Exception{
		HistoryWorkflowInstance instance = historyWorkflowInstanceManager.getHistoryWorkflowInstance(instanceId);
		if(instance!=null){
			if("view".equals(operationName)){
				url=instance.getFormUrl();
			}else if("urgenDone".equals(operationName)){
				url=instance.getEmergencyUrl();
			}
			if(StringUtils.isNotEmpty(url)){
				if(url.indexOf("?")!=-1){
					url = url+instance.getDataId()+"&instanceId="+instance.getProcessInstanceId();
				}else{
					url = url + "?id="+instance.getDataId()+"&instanceId="+instance.getProcessInstanceId();
				}
				if(!url.startsWith("http")){
					int index = url.indexOf("/");
					String code = url.substring(0, index);
					String systemUrl=SystemUrls.getSystemUrl(code);
					if(StringUtils.isNotEmpty(systemUrl))
						url = systemUrl + url.substring(index, url.length());
				}
				url = url+"&_r=1";
			}
		}
		ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("acs.workflowMonitor"), 
				ApiFactory.getBussinessLogService().getI18nLogInfo("wf.viewFormOrUrgent"), 
				ContextUtils.getSystemId("wf"));
		return "monitor-view";
	}
	/**
	 * 流程定义/流程监控/暂停流程实例
	 * @return
	 * @throws Exception
	 */
	@Action("workflow-definition-pauseWorkflows")
	public String pauseWorkflows() throws Exception{
		String msg =  workflowInstanceManager.pauseWorkflowInstance(workflowIds);
		ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("acs.workflowDefinition-Monitor"), 
				ApiFactory.getBussinessLogService().getI18nLogInfo("wf.pauseWf"), 
				ContextUtils.getSystemId("wf"));
		this.renderText(msg);
		return null;
	}
	
	/**
	 * 流程监控/暂停流程实例
	 */
	@Action("workflow-definition-pauseWorkflowDef")
	public String pauseWorkflowDef() throws Exception {
		String msg = workflowInstanceManager.pauseWorkflowInstance(workflowIds);
		ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("acs.workflowMonitorModule"), 
				ApiFactory.getBussinessLogService().getI18nLogInfo("wf.pauseWf"), 
				ContextUtils.getSystemId("wf"));
		this.renderText(msg);
		return null;
	}
	
	/**
	 * 流程定义/流程监控/继续流程
	 * @return
	 * @throws Exception
	 */
	@Action("workflow-definition-continueWorkflows")
	public String continueWorkflows() throws Exception{
		String msg = workflowInstanceManager.continueWorkflowInstance(workflowIds);
		ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("acs.workflowDefinition-Monitor"), 
				ApiFactory.getBussinessLogService().getI18nLogInfo("wf.continueWf"), 
				ContextUtils.getSystemId("wf"));
		this.renderText(msg);
		return null;
	}
	
	/**
	 * 流程监控/继续流程
	 */
	@Action("workflow-definition-continueWorkflowDef")
	public String continueWorkflowDef() throws Exception {
		String msg = workflowInstanceManager.continueWorkflowInstance(workflowIds);
		ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("acs.workflowMonitorModule"), 
				ApiFactory.getBussinessLogService().getI18nLogInfo("wf.continueWf"), 
				ContextUtils.getSystemId("wf"));
		this.renderText(msg);
		return null;
	}
	
	/**
	 * 流程监控/根据办理人姓名查询任务
	 * @return
	 * @throws Exception
	 */
	@Action("workflow-definition-searchTasks")
	public String searchTasks() throws Exception{
		return "workflow-definition-tasks";
	}
	/**
	 * 流程监控/根据办理人姓名查询任务
	 * @return
	 * @throws Exception
	 */
	@Action("workflow-definition-searchTaskDatas")
	public String searchTaskDatas() throws Exception{
		if(tasks.getPageSize()>1 && !SearchUtils.getQueryParameter().isEmpty()){
			taskService.getActivityTasksByTransactorName(tasks,type,definitionCode,wfdId);
			ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("acs.workflowMonitor"), 
					ApiFactory.getBussinessLogService().getI18nLogInfo("wf.searchTask"), 
					ContextUtils.getSystemId("wf"));
			this.renderText(PageUtils.pageToJson(tasks));
			return null;
		}
		return "workflow-definition-tasks";
	}
	
	/**
	 * 流程监控/批量移除任务
	 * @return
	 * @throws Exception
	 */
	@Action("workflow-definition-delTasksBatch")
	public String delTasksBatch() throws Exception{
		chooseTasks = taskService.deleteTasks(taskIds);
		if(chooseTasks.isEmpty()){
			ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("acs.workflowMonitor"), 
					ApiFactory.getBussinessLogService().getI18nLogInfo("wf.batchRemoveTask"), 
					ContextUtils.getSystemId("wf"));
			addActionSuccessMessage(Struts2Utils.getText("wf.text.successfullyRemoved")+taskIds.size()+Struts2Utils.getText("wf.text.ge"));
			return searchTaskDatas();
		}
		return "workflow-definition-choose-task";
	}
	
	public Map<String, List<WorkflowTask>> getChooseTasks() {
		return chooseTasks;
	}
	
	public void prepareDeleteWorkflow() throws Exception{
		if(workflowIds!=null&&!workflowIds.isEmpty()){
			workflowInstances = workflowInstanceManager.getWorkflowInstances(workflowIds);
		}
	}
	
	public void prepareDeleteConfirm() throws Exception{
		
	}
	
	

	
	public void prepareBasicInput() throws Exception {
		prepareModel();
		
	}
	@Action("workflow-definition-basic-input")
	public String basicInput() throws Exception {
		defBasicInfo();
		return "workflow-definition-basic-input";
	}
	
	private void defBasicInfo(){
		typeList = workflowTypeManager.getAllWorkflowType();
		systems=businessSystemManager.getAllSystems();
		List<Menu> oldmenus = menuManager.getAllEnabledStandardRootMenus();
		for (int i = 0; i < oldmenus.size(); i++) {
			Menu menuNew = new Menu();
			menuNew.setId(oldmenus.get(i).getId());
			menuNew.setName(menuManager.getNameToi18n( oldmenus.get(i).getName()));//国际化
			menuNew.setSystemId(oldmenus.get(i).getSystemId());
			menus.add(menuNew);
		}
		branches = ApiFactory.getAcsService().getBranchs();
		hasBranch = branches.size()>0?true:false;
		if(workflowDefinition.getId()!=null){
			Long subCompanyId = workflowDefinition.getSubCompanyId();
			if(subCompanyId!=null){
				com.norteksoft.product.api.entity.Department branchDept = ApiFactory.getAcsService().getDepartmentById(subCompanyId);
				if(branchDept!=null)subCompanyCode = branchDept.getCode();
			}else{
				subCompanyCode = ContextUtils.getCompanyCode();
			}
		}
	}
	
	public void prepareSaveBasic() throws Exception {
		prepareBasicInput();
	}
	@Action("workflow-definition-save-basic")
	public String saveBasic() throws Exception {
		workflowDefinitionManager.saveWfBasic(workflowDefinition,subCompanyCode);
		defBasicInfo();
		addActionSuccessMessage(Struts2Utils.getText("form.save.success"));
		return "workflow-definition-basic-input";
	}
	
	
	/********************************************超期任务监控**************************************************/
	/**
	 * 超期任务监控
	 * @return
	 */
	@Action("workflow-definition-monitor-task-list")
	public String monitorOverTaskList()  throws Exception{
		if("taskActive".equals(position)||"taskHistory".equals(position)){//超期任务/当前任务
			DynamicColumnDefinition dynamicColumnDefinition=new DynamicColumnDefinition(Struts2Utils.getText("wf.table.theExtendedTime"),"overdueTime");
			dynamicColumnDefinition.setType(DataType.DOUBLE);
			dynamicColumnDefinition.setColWidth(250+"");;
			dynamicColumnDefinition.setExportable(true);
			
			dynamicColumn.add(dynamicColumnDefinition);
		}else if("taskUserActive".equals(position)||"taskUserHistory".equals(position)){//超期人监控
			DynamicColumnDefinition dynamicColumnDefinition=new DynamicColumnDefinition(Struts2Utils.getText("transfertask"),"transferTaskNum");
			dynamicColumnDefinition.setColWidth("200");
			dynamicColumnDefinition.setType(DataType.INTEGER);
			dynamicColumnDefinition.setExportable(true);
			if("taskUserActive".equals(position)){
				dynamicColumnDefinition.setFormat("formatTransferTaskNum");
			}else{
				dynamicColumnDefinition.setFormat("formatTransferHistoryTaskNum");
			}
			
			dynamicColumn.add(dynamicColumnDefinition);
		}
		return "workflow-definition-monitorTask";
	}
	private SimpleDateFormat transactDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	/**
	 * 超期任务监控
	 * @return
	 */
	@Action("workflow-definition-monitor-task-list-data")
	public String monitorOverTaskData()  throws Exception{
		ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("acs.workflowDefinition"), 
				ApiFactory.getBussinessLogService().getI18nLogInfo("wf.overdueTaskMonitor"), 
				ContextUtils.getSystemId("wf"));
		if("taskActive".equals(position)){//超期任务/当前任务
			taskService.getOverdueTasks(tasks);
			this.renderText(PageUtils.dynamicPageToJson(tasks,new DynamicColumnValues(){
				public void addValuesTo(List<Map<String, Object>> result) {
					setOverdueTime(result);
				}
			}));
		}else if("taskHistory".equals(position)){//超期任务/归档任务
			taskService.getOverdueHistoryTasks(historyTasks);
			this.renderText(PageUtils.dynamicPageToJson(historyTasks,new DynamicColumnValues(){
				public void addValuesTo(List<Map<String, Object>> result) {
					setOverdueTime(result);
				}
			}));
		}else if("taskUserActive".equals(position)){//超期人/当前任务;超期人/归档任务
			taskService.getOverdueTaskTransactors(taskInfos,transactorName,lastTransactTimeStart,lastTransactTimeEnd);
			this.renderText(PageUtils.dynamicPageToJson(taskInfos,new DynamicColumnValues(){
				public void addValuesTo(List<Map<String, Object>> result) {
					setTaskNum(result,"taskUserActive");
				}
			}));
		}else if("taskUserHistory".equals(position)){//超期人/当前任务;超期人/归档任务
			taskService.getOverdueHistoryTaskTransactors(taskInfos,transactorName,lastTransactTimeStart,lastTransactTimeEnd);
			this.renderText(PageUtils.dynamicPageToJson(taskInfos,new DynamicColumnValues(){
				public void addValuesTo(List<Map<String, Object>> result) {
					setTaskNum(result,"taskUserHistory");
				}
			}));
		}
		return null;
	}
	
	/**
	 * 超期任务导出
	 * @return
	 * @throws Exception
	 */
	@Action("workflow-definition-monitor-task-export")
	public String export() throws Exception {
		ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("acs.workflowDefinition"), 
				ApiFactory.getBussinessLogService().getI18nLogInfo("wf.exportOverdueTask"), ContextUtils.getSystemId("wf"));
		Page<WorkflowTask> tasks = new Page<WorkflowTask>(100000);
		Page<HistoryWorkflowTask> historyTasks = new Page<HistoryWorkflowTask>(100000);
		Page<Object> taskInfos = new Page<Object>(100000);
		if("taskActive".equals(position)){//超期任务/当前任务
			taskService.getOverdueTasks(tasks);
			ExportData exportData=ApiFactory.getMmsService().getDynamicColumnExportData(tasks,new ExportDynamicColumnValues(){
				public void addValuesTo(List<List<Object>> result) {
					setExportOverdueTime(result);
				}
			});
			//导出的方法调用exportData
			this.renderText(ExcelExporter.export(exportData,Struts2Utils.getText("wf.monitoring.extendedTask"),ExcelExportEnum.EXCEL2003));
		}else if("taskHistory".equals(position)){//超期任务/归档任务
			taskService.getOverdueHistoryTasks(historyTasks);
			ExportData exportData=ApiFactory.getMmsService().getDynamicColumnExportData(historyTasks,new ExportDynamicColumnValues(){
				public void addValuesTo(List<List<Object>> result) {
					setExportOverdueTime(result);
				}
			});
			//导出的方法调用exportData
			this.renderText(ExcelExporter.export(exportData,Struts2Utils.getText("wf.monitoring.archivingExtended"),ExcelExportEnum.EXCEL2003));
		}else if("taskUserActive".equals(position)){//超期人/当前任务;超期人/归档任务
			taskService.getOverdueTaskTransactors(taskInfos,transactorName,lastTransactTimeStart,lastTransactTimeEnd);
			ExportData exportData=ApiFactory.getMmsService().getDynamicColumnExportData(taskInfos,new ExportDynamicColumnValues(){
				public void addValuesTo(List<List<Object>> result) {
					setExportTaskNum(result,"taskUserActive");
				}
			});
			
			this.renderText(ExcelExporter.export(exportData,Struts2Utils.getText("wf.monitoring.theStatistical")));
		}else if("taskUserHistory".equals(position)){//超期人/当前任务;超期人/归档任务
			taskService.getOverdueHistoryTaskTransactors(taskInfos,transactorName,lastTransactTimeStart,lastTransactTimeEnd);
			ExportData exportData=ApiFactory.getMmsService().getDynamicColumnExportData(taskInfos,new ExportDynamicColumnValues(){
				public void addValuesTo(List<List<Object>> result) {
					setExportTaskNum(result,"taskUserHistory");
				}
			});
			this.renderText(ExcelExporter.export(exportData,Struts2Utils.getText("wf.monitoring.extendedPeople")));
		}
		
		return null;
	}
	
	/**
	 * 超期人监控/详情
	 * @return
	 */
	@Action("workflow-definition-monitor-task-list-user")
	public String monitorOverTaskUser()  throws Exception{
		if("taskUserActive".equals(position)||"taskUserHistory".equals(position)){//超期任务/当前任务
			DynamicColumnDefinition dynamicColumnDefinition=new DynamicColumnDefinition(Struts2Utils.getText("wf.table.theExtendedTime"),"overdueTime");
			dynamicColumnDefinition.setType(DataType.INTEGER);
			dynamicColumnDefinition.setColWidth(250+"");;
			
			dynamicColumn.add(dynamicColumnDefinition);
		}
		return "workflow-definition-monitorTaskDetail";
	}
	/**
	 * 超期人监控/详情
	 * @return
	 */
	@Action("workflow-definition-monitor-task-list-user-data")
	public String monitorOverTaskUserData()  throws Exception{
		if("taskUserHistory".equals(position)){
			taskService.getOverdueHistoryTaskDetails(historyTasks, transactorId, transactor,transactorName,lastTransactTimeStart,lastTransactTimeEnd);
			this.renderText(PageUtils.dynamicPageToJson(historyTasks,new DynamicColumnValues(){
				public void addValuesTo(List<Map<String, Object>> result) {
					setOverdueTime(result);
				}
			}));
		}else{//超期任务/当前任务
			taskService.getOverdueTaskDetails(tasks, transactorId, transactor,transactorName,lastTransactTimeStart,lastTransactTimeEnd);
			this.renderText(PageUtils.dynamicPageToJson(tasks,new DynamicColumnValues(){
				public void addValuesTo(List<Map<String, Object>> result) {
					setOverdueTime(result);
				}
			}));
		}
		return null;
	}
	
	
	private void setOverdueTime(List<Map<String, Object>> result){
		long a = System.currentTimeMillis();
		for(Map<String, Object> map:result){
			Date transactDate = null;
			try {
				Object dateStr = map.get("transactDate");
				if(dateStr!=null&&!"&nbsp;".equals(dateStr.toString())){
					transactDate =transactDateFormat.parse(dateStr.toString());;//办理时间
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			Date lastTransactTime = null;
			try {
				Object dateStr = map.get("lastTransactTime");
				if(dateStr!=null&&!"&nbsp;".equals(dateStr.toString())){
					lastTransactTime =transactDateFormat.parse(dateStr.toString());;//最后办理时限
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			Object branchIdStr = map.get("subCompanyId");
			Long branchId = null;
			if(branchIdStr!=null&&branchIdStr.toString().matches(CommonStrings.NUMBER_REG)){
				branchId = Long.parseLong(branchIdStr.toString());
			}
			map.put("overdueTime",getRowOverdueTime(branchId,transactDate,lastTransactTime));
		}
		long b = System.currentTimeMillis();
		System.out.println("************************"+(b-a));
	}
	private void setExportOverdueTime(List<List<Object>> result){
		for(List<Object> rowData:result){
			Date transactDate = null;
			Date lastTransactTime = null;
			Long branchId = null;
			Long taskId = Long.parseLong(rowData.get(0).toString());
			if("taskActive".equals(position)){
				WorkflowTask task = taskService.getTask(taskId);
				transactDate = task.getTransactDate();
				lastTransactTime = task.getLastTransactTime();
				branchId = task.getSubCompanyId();
			}else{
				HistoryWorkflowTask task = taskService.getHistoryTask(taskId);
				transactDate = task.getTransactDate();
				lastTransactTime = task.getLastTransactTime();
				branchId = task.getSubCompanyId();
			}
			rowData.add(getRowOverdueTime(branchId,transactDate,lastTransactTime));
		}
	}
	
	private void setTaskNum(List<Map<String, Object>> result,String position){
		for(Map<String, Object> map:result){
			Long transactorId = null;
			Object transactorIdStr = map.get("transactorId");
			if(transactorIdStr!=null&&transactorIdStr.toString().matches(CommonStrings.NUMBER_REG)){//如果是数字
				transactorId = Long.parseLong(transactorIdStr.toString());
			}
			if("taskUserActive".equals(position)){
				Integer transferTaskNum = taskService.getTransferTaskNumByTransactorId(transactorId,transactorName,lastTransactTimeStart,lastTransactTimeEnd);
				map.put("transferTaskNum",transferTaskNum);
			}else{
				Integer transferTaskNum = taskService.getTransferHistoryTaskNumByTransactorId(transactorId,transactorName,lastTransactTimeStart,lastTransactTimeEnd);
				map.put("transferTaskNum",transferTaskNum);
			}
		}
	}
	private void setExportTaskNum(List<List<Object>> result,String position){
		for(List<Object> rowData:result){
			Long transactorId = null;
			Object transactorIdStr = rowData.get(3);
			if(transactorIdStr!=null&&transactorIdStr.toString().matches(CommonStrings.NUMBER_REG)){//如果是数字
				transactorId = Long.parseLong(transactorIdStr.toString());
			}
			Integer transferTaskNum =0 ;
			if("taskUserActive".equals(position)){
				transferTaskNum = taskService.getTransferTaskNumByTransactorId(transactorId,transactorName,lastTransactTimeStart,lastTransactTimeEnd);
			}else{
				transferTaskNum = taskService.getTransferHistoryTaskNumByTransactorId(transactorId,transactorName,lastTransactTimeStart,lastTransactTimeEnd);
			}
			rowData.add(transferTaskNum);
		}
	}
	
	private double getRowOverdueTime(Long branchId,Date transactDate,Date lastTransactTime){
		long workTimes = CommonStrings.WORKTIMES;
		long overTime = 0;
		if(transactDate==null){//说明任务还没有被办理
			Date currentDate = taskService.getDateWithoutSecond(new Date());//当前时间，去掉秒和毫秒，例如：2014-10-10 10:10
			lastTransactTime = taskService.getDateWithoutSecond(lastTransactTime);//最后办理时间，去掉秒和毫秒，例如：2014-10-10-10 10:10
			Map<String,List<Date>> holidaySettings = ApiFactory.getSettingService().getHolidaySettingDays(lastTransactTime, currentDate, branchId);
			overTime = taskService.getOverTimes(currentDate,lastTransactTime,holidaySettings.get("workDate"));
		}else{
			transactDate = taskService.getDateWithoutSecond(transactDate);//任务办理时间，去掉秒和毫秒，例如：2014-0=10-10 10:10
			lastTransactTime = taskService.getDateWithoutSecond(lastTransactTime);//最后办理时间，去掉秒和毫秒，例如：2014-0=10-10 10:10
			Map<String,List<Date>> holidaySettings = ApiFactory.getSettingService().getHolidaySettingDays(lastTransactTime, transactDate, branchId);
			overTime =  taskService.getOverTimes(transactDate,lastTransactTime,holidaySettings.get("workDate"));
		}
		if(overTime>workTimes){
			return Math.ceil(Double.parseDouble(overTime+"")/Double.parseDouble(workTimes+""));
		}else{//如果超期不到一天则按1天算
			return 1;
		}
	}
	
	/**
	 * 超期任务监控/移交任务列表
	 * @return
	 */
	@Action("workflow-definition-monitor-task-transfer")
	public String monitorTransferTasks()  throws Exception{
		return "workflow-definition-monitorTransferTask";
	}
	/**
	 * 超期任务监控/移交任务列表
	 * @return
	 */
	@Action("workflow-definition-monitor-task-transfer-list-data")
	public String monitorTransferTasksData()  throws Exception{
		if("taskHistory".equals(position)){
			taskService.getTransferHistoryTasksByTaskId(taskId, historyTasks);
			this.renderText(PageUtils.pageToJson(historyTasks));
		}else{
			taskService.getTransferTasksByTaskId(taskId, tasks);
			this.renderText(PageUtils.pageToJson(tasks));
		}
		return null;
	}
	
	/**
	 * 超期人监控/移交任务数事件
	 * @return
	 */
	@Action("workflow-definition-monitor-transfer-task-list-user")
	public String monitorTransferTaskUser()  throws Exception{
		return "workflow-definition-monitorTransferTaskDetail";
	}
	/**
	 * 超期人监控/移交任务数事件
	 * @return
	 */
	@Action("workflow-definition-monitor-transfer-task-list-user-data")
	public String monitorTransferTaskUserData()  throws Exception{
		if("taskUserHistory".equals(position)){
			taskService.getTransferHistoryTaskDetails(historyTasks, transactorId,transactorName,lastTransactTimeStart,lastTransactTimeEnd);
			this.renderText(PageUtils.pageToJson(historyTasks));
		}else{//超期任务/当前任务
			taskService.getTransferTaskDetails(tasks, transactorId,transactorName,lastTransactTimeStart,lastTransactTimeEnd);
			this.renderText(PageUtils.pageToJson(tasks));
		}
		return null;
	}
	
	@Override
	protected void prepareModel() throws Exception {
		if(wfdId==null){
			workflowDefinition = new WorkflowDefinition();
		}else{
			workflowDefinition = workflowDefinitionManager.getWfDefinition(wfdId);
		}
	}

	public WorkflowDefinition getModel() {
		return workflowDefinition;
	}

	public Long getWfdId() {
		return wfdId;
	}

	public void setWfdId(Long wfdId) {
		this.wfdId = wfdId;
	}
	
	@Required
	public void setWorkflowDefinitionManager(
			WorkflowDefinitionManager workflowDefinitionManager) {
		this.workflowDefinitionManager = workflowDefinitionManager;
	}

	@Autowired
	public void setWorkflowInstanceManager(
			WorkflowInstanceManager workflowInstanceManager) {
		this.workflowInstanceManager = workflowInstanceManager;
	}
	
	@Required
	public void setWorkflowTypeManager(WorkflowTypeManager workflowTypeManager) {
		this.workflowTypeManager = workflowTypeManager;
	}
	
	@Required
	public void setFormViewManager(FormViewManager formManager) {
		this.formViewManager = formManager;
	}
	@Autowired
	public void setListViewManager(ListViewManager listViewManager) {
		this.listViewManager = listViewManager;
	}

	public Page<Object> getWiPage() {
		return wiPage;
	}
	
	public void setWiPage(Page<Object> wiPage) {
		this.wiPage = wiPage;
	}

	public Page<WorkflowDefinition> getWfdPage() {
		return wfdPage;
	}

	public void setWfdPage(Page<WorkflowDefinition> wfdPage) {
		this.wfdPage = wfdPage;
	}

	public String getXmlFile() {
		return xmlFile;
	}

	public void setXmlFile(String xmlFile) {
		this.xmlFile = xmlFile;
	}

	public Long getCompanyId() {
		return ContextUtils.getCompanyId();
	}
	
	public Long getDefCompanyId() {
		return defCompanyId;
	}

	public String getDefCreator() {
		return defCreator;
	}
	
	public String getDefCreatorName() {
		return defCreatorName;
	}


	public String getSearchCdn() {
		return searchCdn;
	}

	public void setSearchCdn(String searchCdn) {
		this.searchCdn = searchCdn;
	}

	public List<WorkflowType> getTypeList() {
		return typeList;
	}

	public Long getType() {
		return type;
	}

	public void setType(Long type) {
		this.type = type;
	}

	public List<String> getTitleList() {
		return titleList;
	}

	public List<ListColumn> getDisplayField() {
		return displayField;
	}

	public WorkflowDefinition getWorkflowDefinition() {
		return workflowDefinition;
	}

	public List<WorkflowDefinitionTemplate> getTemplates() {
		return templates;
	}

	public Long getTemplateId() {
		return templateId;
	}

	public void setTemplateId(Long templateId) {
		this.templateId = templateId;
	}

	public Long getDefSystemId() {
		return defSystemId;
	}

	public void setDefSystemId(Long defSystemId) {
		this.defSystemId = defSystemId;
	}

	public String getOption() {
		return option;
	}

	public void setOption(String option) {
		this.option = option;
	}

	public String getFormType() {
		return formType;
	}

	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public Long getFormId() {
		return formId;
	}

	public void setFormId(Long formId) {
		this.formId = formId;
	}

	public String getFieldPermission() {
		return fieldPermission;
	}

	public void setFieldPermission(String fieldPermission) {
		this.fieldPermission = fieldPermission;
	}

	public String getWorkflowId() {
		return workflowId;
	}

	public void setWorkflowId(String workflowId) {
		this.workflowId = workflowId;
	}

	public WorkflowInstance getWorkflowInstance() {
		return workflowInstance;
	}

	public void setWorkflowInstance(WorkflowInstance workflowInstance) {
		this.workflowInstance = workflowInstance;
	}

	public Collection<Long> getWorkflowIds() {
		return workflowIds;
	}

	public void setWorkflowIds(Set<Long> workflowIds) {
		this.workflowIds = workflowIds;
	}

	public void setWorkflowInstances(Set<WorkflowInstance> workflowInstances) {
		this.workflowInstances = workflowInstances;
	}

	public void setWfdIds(List<Long> wfdIds) {
		this.wfdIds = wfdIds;
	}

	public void setOperates(List<String> operates) {
		this.operates = operates;
	}

	public void setSearchValues(List<String> searchValues) {
		this.searchValues = searchValues;
	}

	public List<FormView> getForms() {
		return forms;
	}
	private void addActionSuccessMessage(String message){
		this.addActionMessage(SUCCESS_MESSAGE_LEFT+message+MESSAGE_RIGHT);
	}

	public String getFormCode() {
		return formCode;
	}
	public void setFormCode(String formCode) {
		this.formCode = formCode;
	}

	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}

	public List<WorkflowDefinition> getDefinitions() {
		return definitions;
	}

	public Long getSysId() {
		return sysId;
	}

	public void setSysId(Long sysId) {
		this.sysId = sysId;
	}

	public String getVertionType() {
		return vertionType;
	}

	public void setVertionType(String vertionType) {
		this.vertionType = vertionType;
	}
	public void setDefinitionCode(String definitionCode) {
		this.definitionCode = definitionCode;
	}
	
	public String getDefinitionCode() {
		return definitionCode;
	}

	public List<String> getEnNames() {
		return enNames;
	}

	public void setEnNames(List<String> enNames) {
		this.enNames = enNames;
	}

	public List<String> getChNames() {
		return chNames;
	}

	public void setChNames(List<String> chNames) {
		this.chNames = chNames;
	}

	public List<String> getDataTypes() {
		return dataTypes;
	}

	public void setDataTypes(List<String> dataTypes) {
		this.dataTypes = dataTypes;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public void setSystemCode(String systemCode) {
		this.systemCode = systemCode;
	}
	public String getUrl() {
		return url;
	}
	public void setInstanceId(Long instanceId) {
		this.instanceId = instanceId;
	}

	public void setTransactorName(String transactorName) {
		this.transactorName = transactorName;
	}
	public Page<WorkflowTask> getTasks() {
		return tasks;
	}

	public void setTaskIds(List<Long> taskIds) {
		this.taskIds = taskIds;
	}

	public String getOperationName() {
		return operationName;
	}

	public void setOperationName(String operationName) {
		this.operationName = operationName;
	}
	public List<BusinessSystem> getSystems() {
		return systems;
	}

	public List<com.norteksoft.product.api.entity.Department> getBranches() {
		return branches;
	}

	public boolean isHasBranch() {
		return hasBranch;
	}
	public String getCompanyName() {
		return ContextUtils.getCompanyName();
	}
	public String getSubCompanyCode() {
		return subCompanyCode;
	}
	public void setSubCompanyCode(String subCompanyCode) {
		this.subCompanyCode = subCompanyCode;
	}
	public String getCompanyCode() {
		return ContextUtils.getCompanyCode();
	}

	public String getCurrentorLoginName() {
		return currentorLoginName;
	}

	public String getCurrentorName() {
		return currentorName;
	}

	public Page<HistoryWorkflowTask> getHistoryTasks() {
		return historyTasks;
	}

	public List<DynamicColumnDefinition> getDynamicColumn() {
		return dynamicColumn;
	}
	public Page<Object> getTaskInfos() {
		return taskInfos;
	}

	public void setTransactorId(Long transactorId) {
		this.transactorId = transactorId;
	}

	public void setTransactor(String transactor) {
		this.transactor = transactor;
	}

	public Long getTransactorId() {
		return transactorId;
	}

	public String getTransactor() {
		return transactor;
	}

	public static void main(String[] args) {
		long a = 57600000;
		long b = 32400000;
		System.out.println("ceil--"+Math.ceil(Double.parseDouble(a+"")/Double.parseDouble(b+"")));
		System.out.println("round--"+Math.round(a/b));;
		System.out.println("floor--"+Math.floor(a/b));;
		
		
	}

	public void setParentSearchParameters(String parentSearchParameters) {
		this.parentSearchParameters = parentSearchParameters;
	}

	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	public String getLastTransactTimeStart() {
		return lastTransactTimeStart;
	}

	public void setLastTransactTimeStart(String lastTransactTimeStart) {
		this.lastTransactTimeStart = lastTransactTimeStart;
	}

	public String getLastTransactTimeEnd() {
		return lastTransactTimeEnd;
	}

	public void setLastTransactTimeEnd(String lastTransactTimeEnd) {
		this.lastTransactTimeEnd = lastTransactTimeEnd;
	}

	public String getTransactorName() {
		return transactorName;
	}
	public void setMenus(List<Menu> menus) {
		this.menus = menus;
	}
	public List<Menu> getMenus() {
		return menus;
	}
}
