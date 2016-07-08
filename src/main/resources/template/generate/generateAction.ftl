package ${packageName};
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.PageUtils;
import com.norteksoft.product.web.struts2.Struts2Utils;
import com.norteksoft.product.api.ApiFactory;
import org.apache.commons.lang.StringUtils;
<#if containWorkflow?if_exists>
import edu.emory.mathcs.backport.java.util.Arrays;
import com.norteksoft.product.web.wf.WorkflowActionSupport;
import java.util.ArrayList;
import java.util.List;
import com.norteksoft.wf.base.enumeration.CompleteTaskTipType;
import com.norteksoft.product.util.ContextUtils;
<#else>
import com.norteksoft.product.web.struts2.CrudActionSupport;
import com.norteksoft.product.util.JsonParser;
</#if>
<#list imports?if_exists as item>
import ${item};
</#list>

@Namespace("/${namespace}")
@ParentPackage("default")
<#if containWorkflow?if_exists>
public class ${entityName}Action  extends WorkflowActionSupport<${entityName}> {
<#else>
public class ${entityName}Action  extends CrudActionSupport<${entityName}> {
</#if>

	private static final long serialVersionUID = 1L;
	private String fieldPermission;
	private Long id;//实体id
	private String ids;
	private ${entityName} ${entityAttribute};
	private Page<${entityName}> page;
<#if containWorkflow?if_exists>
	private String addSignPerson;//加签人员
	private String removeTaskIds;//减签的任务
	private String copyPerson;//抄送人员
	private List<String[]> handerList = new ArrayList<String[]>();//减签环节办理人list
	private String assignee; //指派人
	private String submitResult;//任务提交结果
	private Boolean saveTaskFlag=false;//任务保存标志
	private String transactorIdStr;//下一环节办理人id字符串
	private String opinion;//意见框中的意见
	private String autoFillOpinionInfo;//办理前自动填写意见控件
</#if>
	private static final String SUCCESS_MESSAGE_LEFT = "<font class=\"onSuccess\"><nobr>";
	private static final String MESSAGE_RIGHT = "</nobr></font>";
	private static final String ERROR_MESSAGE_LEFT = "<font class=\"onError\"><nobr>";
	
	@Autowired
	private ${entityName}Manager ${entityAttribute}Manager;
	
	protected void addErrorMessage(String message){
		this.addActionMessage(ERROR_MESSAGE_LEFT+message+MESSAGE_RIGHT);
	}
	protected void addSuccessMessage(String message){
		this.addActionMessage(SUCCESS_MESSAGE_LEFT+message+MESSAGE_RIGHT);
	}
	
<#if containWorkflow?if_exists>

	public void prepareTask() throws Exception {
		prepareModel();
	}

	/**
	 * 办理任务页面
	 * @return
	 */
	@Action("${entityAttribute}-task")
	public String task() throws Exception {
		getRight(taskId,"${workflowCode?if_exists}");
		//办理前自动填写域设值
		if(taskId!=null){
			autoFillOpinionInfo = ApiFactory.getFormService().fillEntityByTask(${entityAttribute}, taskId);
		}
		<#if logFlag?if_exists=="true">
		ApiFactory.getBussinessLogService().log("${entityAlias}流程处理", 
				"进入办理任务页面");
		</#if>
		return SUCCESS;
	}
	
	/**
	 * 抄送
	 * @return
	 */
	@Action("${entityAttribute}-copyTask")
	public String copyTasks(){
	List<String> userIdStrs=new ArrayList<String>();
		if("all_user".equals(copyPerson)){
			List<Long> userids=ApiFactory.getAcsService().getAllUserIdsWithoutAdminByCompany();
			userIdStrs.addAll(Arrays.asList((userids.toString().replace("[", "").replace("]", "").replace(" ", "")).split(",")));
		}else{
			userIdStrs=Arrays.asList(copyPerson.split(","));
		}
		${entityAttribute}Manager.createCopyTasks(taskId, userIdStrs, null, null);
		<#if logFlag?if_exists=="true">
		ApiFactory.getBussinessLogService().log("${entityAlias}流程处理", 
				"抄送任务");
		</#if>		
		renderText("已抄送");
		return null;
	}
	
	/**
	 * 退回
	 * @return
	 */
	@Action("${entityAttribute}-goback")
	public String goback(){
		String msg=${entityAttribute}Manager.goback(taskId);
		task=${entityAttribute}Manager.getWorkflowTask(taskId);
		${entityAttribute}=${entityAttribute}Manager.get${entityName}ByTaskId(taskId);
		<#if logFlag?if_exists=="true">
		ApiFactory.getBussinessLogService().log("${entityAlias}流程处理", 
				"退回任务");
		</#if>
		renderText(msg);
		return null;
	}
	
	/**
	 * 放弃领取任务
	 */
	@Override
	@Action("${entityAttribute}-abandonReceive")
	public String abandonReceive() {
		${entityAttribute}Manager.abandonReceive(taskId);
		task=${entityAttribute}Manager.getWorkflowTask(taskId);
		<#if logFlag?if_exists=="true">
		ApiFactory.getBussinessLogService().log("${entityAlias}流程处理", 
				"放弃领取任务");
		</#if>
		return "${entityAttribute}-task";
	}
	
	/**
	 * 加签
	 */
	@Override
	@Action("${entityAttribute}-addSigner")
	public String addSigner() {
		String[] strs = addSignPerson.split(",");
		List<String> lists = new ArrayList<String>();
		if("all_user".equals(addSignPerson)){
			List<Long> userIds=ApiFactory.getAcsService().getUserIdsByCompanyWithoutAdmin();
			for(Long userId:userIds){
				lists.add(String.valueOf(userId));
			}
		}else{
			lists.addAll(java.util.Arrays.asList(strs));
		}
		${entityAttribute}Manager.addSigner(taskId, lists);
		<#if logFlag?if_exists=="true">
		ApiFactory.getBussinessLogService().log("${entityAlias}流程处理", 
				"任务加签");
		</#if>
		renderText("加签成功！");
		return null;
	}
	
	/**
	 * 完成交互任务：用于选人、选环节、填意见
	 */
	@Override
	@Action("${entityAttribute}-completeInteractiveTask")
	public String completeInteractiveTask() {
		<#if logFlag?if_exists=="true">
		ApiFactory.getBussinessLogService().log("${entityAlias}流程处理", 
				"完成交互任务");
		</#if>
		CompleteTaskTipType completeTaskTipType=null;
		List<String> lists = new ArrayList<String>();
		try{
			if(StringUtils.isNotEmpty(transactorIdStr)){//上一环节办理人指定
				lists.add(transactorIdStr);
				completeTaskTipType = ${entityAttribute}Manager.distributeTask(taskId,lists,"");
			}else{
				if(StringUtils.isNotEmpty(opinion)){//意见框
					completeTaskTipType = ${entityAttribute}Manager.distributeTask(taskId,lists,opinion);
				}else{
					completeTaskTipType = ${entityAttribute}Manager.distributeTask(taskId,lists,"");
				}
			}
		}catch(RuntimeException de){
			de.printStackTrace();
		}
		${entityAttribute} = ${entityAttribute}Manager.get${entityName}ByTaskId(taskId);
		this.renderText(${entityAttribute}Manager.getCompleteTaskTipType(completeTaskTipType,${entityAttribute}));
		return null;
	}
	
	/**
	 * 完成任务
	 */
	@Override
	@Action("${entityAttribute}-completeTask")
	public String completeTask() {
		CompleteTaskTipType completeTaskTipType=null;
		try{
			completeTaskTipType =  ${entityAttribute}Manager.completeTask(${entityAttribute}, taskId, taskTransact);
		}catch(RuntimeException e){
			e.printStackTrace();
		}
		<#if logFlag?if_exists=="true">
		ApiFactory.getBussinessLogService().log("${entityAlias}流程处理", 
				"完成任务");
		</#if>
		renderText(${entityAttribute}Manager.getCompleteTaskTipType(completeTaskTipType,${entityAttribute}));
		return null;
	}
	
	/**
	 * 绑定完成任务
	 */
	
	public void prepareCompleteTask() throws Exception{
		prepareModel();
	}
	/**
	 * 领取任务
	 */
	@Override
	@Action("${entityAttribute}-drawTask")
	public String drawTask() {
		${entityAttribute}Manager.drawTask(taskId);
		task=${entityAttribute}Manager.getWorkflowTask(taskId);
		${entityAttribute} = ${entityAttribute}Manager.get${entityName}ByTaskId(taskId);
		getRight(taskId,"${workflowCode?if_exists}");
		<#if logFlag?if_exists=="true">
		ApiFactory.getBussinessLogService().log("${entityAlias}流程处理", 
				"领取任务");
		</#if>
		return "${entityAttribute}-task";
	}
	
	/**
	 * 填写意见
	 */
	@Override
	@Action("${entityAttribute}-fillOpinion")
	public String fillOpinion() {
		// TODO Auto-generated method stub
		<#if logFlag?if_exists=="true">
		ApiFactory.getBussinessLogService().log("${entityAlias}流程处理", 
				"填写意见");
		</#if>
		return null;
	}
	
	/**
	 * 流程监控中应急处理功能
	 */
	@Override
	@Action("${entityAttribute}-processEmergency")
	public String processEmergency() {
		// TODO Auto-generated method stub
		<#if logFlag?if_exists=="true">
		ApiFactory.getBussinessLogService().log("${entityAlias}流程处理", 
				"进入应急处理页面");
		</#if>
		return null;
	}
	
	/**
	 * 减签
	 * @return
	 */
	@Override
	@Action("${entityAttribute}-removeSigner")
	public String removeSigner() {
		List<Long> lists = new ArrayList<Long>();
		String[] taskIdStrs = removeTaskIds.split(",");
		for(String idStr:taskIdStrs){
			lists.add(Long.parseLong(idStr));
		}
		${entityAttribute}Manager.removeSigners(lists);
		<#if logFlag?if_exists=="true">
		ApiFactory.getBussinessLogService().log("${entityAlias}流程处理", 
				"进入任务减签页面");
		</#if>
		renderText("减签成功！");
		return null;
	}
	
	/**
	 * 选择减签人员
	 * @return
	 * @throws Exception 
	 */
	@Action("${entityAttribute}-cutsignTree")
	public String cutsignTree() throws Exception{
		prepareModel();
		<#if logFlag?if_exists=="true">
		ApiFactory.getBussinessLogService().log("${entityAlias}流程处理", 
				"任务减签");
		</#if>
		renderText(${entityAttribute}Manager.getTaskHander(taskId));
		return null;
	}
	
	/**
	 * 指派
	 * @return
	 */
	@Action("${entityAttribute}-assign")
	public String assign(){
		${entityAttribute}Manager.assign(taskId, assignee);
		<#if logFlag?if_exists=="true">
		ApiFactory.getBussinessLogService().log("${entityAlias}流程处理", 
				"任务指派");
		</#if>
		renderText("指派完成");
		return null;
	}
	
	/**
	 * 取回任务
	 */
	@Override
	@Action("${entityAttribute}-retrieveTask")
	public String retrieveTask() {
		String msg=${entityAttribute}Manager.retrieve(taskId);
		task=${entityAttribute}Manager.getWorkflowTask(taskId);
		${entityAttribute}=${entityAttribute}Manager.get${entityName}ByTaskId(taskId);
		<#if logFlag?if_exists=="true">
		ApiFactory.getBussinessLogService().log("${entityAlias}流程处理", 
				"任务取回");
		</#if>
		renderText(msg);
		return null;
	}
	
	/**
	 * 显示流转历史
	 */
	@Override
	@Action("${entityAttribute}-showHistory")
	public String showHistory() {
		<#if logFlag?if_exists=="true">
		ApiFactory.getBussinessLogService().log("${entityAlias}流程处理", 
				"显示流转历史");
		</#if>
		return "${entityAttribute}-history";
	}
	
	/**
	 * 绑定流转历史
	 */
	public void prepareShowHistory() throws Exception {
		prepareModel();
	}
	
	/**
	 * 绑定提交流程
	 */
	public void prepareSubmitProcess() throws Exception {
		prepareModel();
	}
	
	/**
	 * 启动并提交流程
	 */
	@Override
	@Action("${entityAttribute}-submitProcess")
	public String submitProcess() {
		CompleteTaskTipType completeTaskTipType=null;
		try{
			completeTaskTipType =  ${entityAttribute}Manager.submitProcess(${entityAttribute},"发起","${workflowCode?if_exists}");
		}catch(RuntimeException de){
			de.printStackTrace();
		}
		submitResult=${entityAttribute}Manager.getCompleteTaskTipType(completeTaskTipType,${entityAttribute});
		if(taskId==null)taskId = ${entityAttribute}.getWorkflowInfo().getFirstTaskId();
		getRight(taskId,"${workflowCode?if_exists}");
		addSuccessMessage("提交成功");
		<#if logFlag?if_exists=="true">
		ApiFactory.getBussinessLogService().log("${entityAlias}流程处理", 
				"启动并提交流程${workflowCode?if_exists}");
		</#if>
		return "${entityAttribute}-input";
	}
	
	/**
	 * 删除
	 */
	@Override
	@Action("${entityAttribute}-delete")
	public String delete() throws Exception {
		addSuccessMessage(${entityAttribute}Manager.delete${entityName}(ids));
		<#if logFlag?if_exists=="true">
		ApiFactory.getBussinessLogService().log("删除${entityAlias}", 
				"删除${entityAlias}");
		</#if>
		return "${entityAttribute}-list";
	}
	
	/**
	 * 新建页面
	 */
	@Override
	@Action("${entityAttribute}-input")
	public String input() throws Exception {
		getRight(taskId,"${workflowCode?if_exists}");
		<#if logFlag?if_exists=="true">
		ApiFactory.getBussinessLogService().log("新建${entityAlias}", 
				"新建${entityAlias}");
		</#if>
		return SUCCESS;
	}
	
	/**
	 * 绑定查看页面
	 */
	public void prepareView() throws Exception {
		prepareModel();
	}
	
	/**
	 * 查看页面
	 */
	@Action("${entityAttribute}-view")
	public String view() throws Exception {
	fieldPermission=ApiFactory.getFormService().getFieldPermission(false);
	<#if logFlag?if_exists=="true">
	ApiFactory.getBussinessLogService().log("查看${entityAlias}", 
				"查看${entityAlias}");
	</#if>
		return SUCCESS;
	}
	
	/**
	 * 列表页面
	 */
	@Override
	@Action("${entityAttribute}-list")
	public String list() throws Exception {
		return SUCCESS;
	}
	/**
	 * 可编辑列表页面
	 */
	@Action("${entityAttribute}-listEditable")
	public String listEditable() throws Exception {
		return SUCCESS;
	}
	
	/**
	 * 列表数据
	 */
	@Action("${entityAttribute}-listDatas")
	public String getListDatas() throws Exception {
		<#if logFlag?if_exists=="true">
		ApiFactory.getBussinessLogService().log("${entityAlias}列表", 
				"显示列表数据");
		</#if>
		page = ${entityAttribute}Manager.search(page);
		renderText(PageUtils.pageToJson(page));
		return null;
	}

	@Override
	protected void prepareModel() throws Exception {
		if(taskId!=null){
	    	${entityAttribute} = ${entityAttribute}Manager.get${entityName}ByTaskId(taskId);
	    	task = ${entityAttribute}Manager.getWorkflowTask(taskId);
	    	autoFillOpinionInfo = ApiFactory.getFormService().fillEntityByTask(${entityAttribute}, taskId);
	    }else if(id!=null){
	    	${entityAttribute}=${entityAttribute}Manager.get${entityName}(id);
			if(task==null) taskId = ${entityAttribute}.getWorkflowInfo().getFirstTaskId();
			if(taskId==null){
				task = ${entityAttribute}Manager.getActiveTaskByTransactorId(${entityAttribute},ContextUtils.getUserId());
				if(task!=null)taskId = task.getId();
			}
	    }else if(id==null){
			${entityAttribute}=new ${entityName}();
			autoFillOpinionInfo = ApiFactory.getFormService().fillEntityByDefinition(${entityAttribute}, "${workflowCode?if_exists}");
		}
	}
	
	/**
	 * 保存
	 */
	@Override
	@Action("${entityAttribute}-save")
	public String save() throws Exception {
		getRight(taskId,"${workflowCode?if_exists}");
		<#if logFlag?if_exists=="true">
		ApiFactory.getBussinessLogService().log("保存${entityAlias}", 
				"保存${entityAlias}");
		</#if>
		if(saveTaskFlag){
			${entityAttribute}Manager.save${entityName}(${entityAttribute},taskId);
			addSuccessMessage("保存成功");
			return "${entityAttribute}-task";
		}else{
			${entityAttribute}Manager.saveInstance("${workflowCode?if_exists}",${entityAttribute});
			if(${entityAttribute}.getWorkflowInfo()!=null&&taskId==null)taskId = ${entityAttribute}.getWorkflowInfo().getFirstTaskId();
			addSuccessMessage("保存成功");
			return "${entityAttribute}-input";
		}
	}
	
	/**
	 * 获取权限
	 */
	public void getRight(Long taskId,String defCode) {
		if(taskId==null){
			fieldPermission = ${entityAttribute}Manager.getFieldPermission(defCode);//禁止或必填字段
			taskPermission = ${entityAttribute}Manager.getActivityPermission(defCode);
		}else{
			fieldPermission = ${entityAttribute}Manager.getFieldPermissionByTaskId(taskId);//禁止或必填字段
			taskPermission = ${entityAttribute}Manager.getActivityPermission(taskId);
		}
	}

	@Override
	public ${entityName} getModel() {
		return ${entityAttribute};
	}
<#else>
	/**
	 * 删除
	 */
	@Override
	@Action("${entityAttribute}-delete")
	public String delete() throws Exception {
		<#if logFlag?if_exists=="true">
		ApiFactory.getBussinessLogService().log("删除${entityAlias}", 
				"删除${entityAlias}");
		</#if>
		addActionMessage("<font class=\"onSuccess\"><nobr>"+${entityAttribute}Manager.delete${entityName}(ids)+"</nobr></font>");
		return "${entityAttribute}-list";
	}

	/**
	 * 新建页面
	 */
	@Override
	@Action("${entityAttribute}-input")
	public String input() throws Exception {
		<#if logFlag?if_exists=="true">
		ApiFactory.getBussinessLogService().log("${entityAlias}表单", 
				"进入表单${entityAlias}页面");
		</#if>
		return SUCCESS;
	}

	/**
	 * 列表页面
	 */
	@Override
	@Action("${entityAttribute}-list")
	public String list() throws Exception {
		return SUCCESS;
	}
	
	/**
	 * 列表数据
	 */
	@Action("${entityAttribute}-listDatas")
	public String getListDatas() throws Exception {
		<#if logFlag?if_exists=="true">
		ApiFactory.getBussinessLogService().log("${entityAlias}列表", 
				"查看${entityAlias}列表数据");
		</#if>
		page = ${entityAttribute}Manager.search(page);
		renderText(PageUtils.pageToJson(page));
		return null;
	}
	/**
	 * 绑定查看页面
	 */
	public void prepareView() throws Exception {
		prepareModel();
	}
	
	/**
	 * 查看页面
	 */
	@Action("${entityAttribute}-view")
	public String view() throws Exception {
	fieldPermission=ApiFactory.getFormService().getFieldPermission(false);
	<#if logFlag?if_exists=="true">
	ApiFactory.getBussinessLogService().log("查看${entityAlias}", 
				"查看${entityAlias}");
	</#if>
		return SUCCESS;
	}
	@Override
	protected void prepareModel() throws Exception {
		if(id==null){
			${entityAttribute}=new ${entityName}();
		}else{
			${entityAttribute}=${entityAttribute}Manager.get${entityName}(id);
		}
	}

	/**
	 * 保存
	 */
	@Override
	@Action("${entityAttribute}-save")
	public String save() throws Exception {
		<#if logFlag?if_exists=="true">
		ApiFactory.getBussinessLogService().log("保存${entityAlias}", 
				"保存${entityAlias}");
		</#if>
		${entityAttribute}Manager.save${entityName}(${entityAttribute});
		addSuccessMessage("保存成功");
		return "${entityAttribute}-input";
	}

	@Override
	public ${entityName} getModel() {
		return ${entityAttribute};
	}
	
	public void prepareEditSave() throws Exception{
		prepareModel();
	}
	
	/**
	 * 编辑-保存
	 */
	@Action("${entityAttribute}-editSave")
	public String editSave() throws Exception {
		${entityAttribute}Manager.save${entityName}(${entityAttribute});
			this.renderText(JsonParser.getRowValue(${entityAttribute}));
		return null;
	}
	
	/**
	 * 编辑-删除
	 */
	@Action("${entityAttribute}-editDelete")
	public String editDelete() throws Exception {
		ids=Struts2Utils.getParameter("deleteIds");
		String[] deleteIds=ids.split(",");
		for(String deleteId:deleteIds){
			${entityAttribute}Manager.delete${entityName}(Long.valueOf(deleteId));
		}
		return null;
	}
</#if>

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

	public ${entityName} get${entityName}() {
		return ${entityAttribute};
	}

	public void set${entityName}(${entityName} ${entityAttribute}) {
		this.${entityAttribute} = ${entityAttribute};
	}

	public Page<${entityName}> getPage() {
		return page;
	}

	public void setPage(Page<${entityName}> page) {
		this.page = page;
	}
	public String getFieldPermission() {
		return fieldPermission;
	}
	public void setFieldPermission(String fieldPermission) {
		this.fieldPermission = fieldPermission;
	}
<#if containWorkflow?if_exists>
	public String getAddSignPerson() {
		return addSignPerson;
	}

	public void setAddSignPerson(String addSignPerson) {
		this.addSignPerson = addSignPerson;
	}
	
	public String getRemoveTaskIds() {
		return removeTaskIds;
	}

	public void setRemoveTaskIds(String removeTaskIds) {
		this.removeTaskIds = removeTaskIds;
	}
	public String getCopyPerson() {
		return copyPerson;
	}
	public void setCopyPerson(String copyPerson) {
		this.copyPerson = copyPerson;
	}
		public List<String[]> getHanderList() {
		return handerList;
	}

	public void setHanderList(List<String[]> handerList) {
		this.handerList = handerList;
	}
	public String getAssignee() {
		return assignee;
	}

	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}
	public String getSubmitResult() {
		return submitResult;
	}
	public void setSaveTaskFlag(Boolean saveTaskFlag) {
		this.saveTaskFlag = saveTaskFlag;
	}
	public void setTransactorIdStr(String transactorIdStr) {
		this.transactorIdStr = transactorIdStr;
	}
	public void setOpinion(String opinion) {
		this.opinion = opinion;
	}
	public String getAutoFillOpinionInfo() {
		return autoFillOpinionInfo;
	}

</#if>
}

