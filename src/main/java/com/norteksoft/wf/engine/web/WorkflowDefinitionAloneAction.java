package com.norteksoft.wf.engine.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;

import com.norteksoft.mms.form.entity.FormView;
import com.norteksoft.mms.form.service.FormViewManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.ParameterUtils;
import com.norteksoft.product.util.PropUtils;
import com.norteksoft.product.util.SystemUrls;
import com.norteksoft.product.util.ThreadParameters;
import com.norteksoft.product.web.struts2.CrudActionSupport;
import com.norteksoft.product.web.struts2.Struts2Utils;
import com.norteksoft.wf.engine.core.DefinitionXmlParse;
import com.norteksoft.wf.engine.entity.WorkflowDefinition;
import com.norteksoft.wf.engine.service.WorkflowDefinitionManager;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

@Namespace("/engine")
@ParentPackage("default")
@Results( { @Result(name = CrudActionSupport.RELOAD, location = "workflow-definition-alone?wfdId=${wfdId}&wfdFile=${wfdFile}", type = "redirectAction")})
public class WorkflowDefinitionAloneAction extends CrudActionSupport<WorkflowDefinition>{
	private static final long serialVersionUID = 1L;
	
	private Long templateId;
	private Long wfdId;
	private WorkflowDefinition workflowDefinition;
	private List<FormView> forms;
	private String defCreator;
	private String defCreatorName;
	private String currentorLoginName;
	private String currentorName;
	private Long defSystemId;
	private Long defCompanyId;
	private Long type = 0l;//流程类型id
	private String xmlFile;
	private String saveUrl;
	private String backUrl;
	private String flag;
	private FormViewManager formViewManager;//?
	private String tacheCode;//环节编号
	private String tacheName;//环节名称
	private String pageType;//页面类型：historyClick表示“流转历史页面”，viewClick表示“查看页面”
	
	@Autowired
	private WorkflowDefinitionManager workflowDefinitionManager;
	
	@Override
	public String delete() throws Exception {
		return null;
	}

	@Override
	public String input() throws Exception {
		return null;
	}

	@Override
	public String list() throws Exception {
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

	@Override
	public String save() throws Exception {
		return null;
	}

	public void prepareAloneInput() throws Exception{
		prepareModel();
	}
	public String aloneInput() throws Exception {
		if(wfdId == null){
			createDefinition();
		}else{
			update();
		}
		return INPUT;
	}
	private void createDefinition() {
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
	}
	
	
	private void update(){
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
	}
//	@Action("workflow-definition-view")
//	public String view() throws Exception{
//		ApiFactory.getBussinessLogService().log("流程定义", 
//				"查看流程定义", 
//				ContextUtils.getSystemId("wf"));
//		workflowDefinition = workflowDefinitionManager.getWfDefinition(wfdId);
//		FormView form=formViewManager.getCurrentFormViewByCodeAndVersion(workflowDefinition.getFormCode(), workflowDefinition.getFromVersion());
//		if(form==null){
//			return "viewFaild";
//		}else{
//			formHtml = form.getHtml();
//			wfDefinitionId = workflowDefinitionManager.getWfDefinition(wfdId).getProcessId();
//			return "workflow-definition-view";
//		}
//	}
	private void getFormInfo(){
		forms = formViewManager.getFormViewsByCompany();
		FormView temp = new FormView();
		temp.setName("请选择表单");
		forms.add(0, temp);
	}
	
	public String addTacheClickByProcessExtendProperty() throws Exception {
		ThreadParameters parameters = new ThreadParameters(defCompanyId);
		ParameterUtils.setParameters(parameters);
		workflowDefinition = workflowDefinitionManager.getWfDefinition(wfdId);
		Map<String,String> processExtendFields = DefinitionXmlParse.getProcessExtendFields(workflowDefinition.getProcessId(),wfdId);
		StringBuilder extendFields = new StringBuilder();
		for(Entry<String,String> field:processExtendFields.entrySet()){
			if(StringUtils.isNotEmpty(extendFields.toString()))
				extendFields.append(",");
			if(field.getKey().toString().indexOf(pageType)==0){
				extendFields.append("\"");
				extendFields.append(field.getKey().replace(pageType+"_", ""));
				extendFields.append("\":\"");
				extendFields.append(field.getValue());
				extendFields.append("\"");
			}
		}
		Map<String,String> filterTachePopupPage = DefinitionXmlParse.getFilterTachePopupPageSet(workflowDefinition.getProcessId(),wfdId);
		String result="";
		String extendProperty = "{"+extendFields.toString()+"}";
		String url=filterTachePopupPage.get(DefinitionXmlParse.FILTER_TACHE_POPUP_PAGE);
		url=packagingUrl(url,workflowDefinition.getSystemId());
		if("http".equals(filterTachePopupPage.get(DefinitionXmlParse.SET_TYPE))){
			result=getHttpConnection(workflowDefinition,url,extendProperty);
		}else{
			result=getRestfulRequest(workflowDefinition,url,extendProperty);
		}
		if(StringUtils.isNotEmpty(result)){
			result = packagingUrl(result,workflowDefinition.getSystemId());
		}else{
			result = "";
		}
		this.renderText(result);
		return null;
	}
	
	private String packagingUrl(String url,Long systemId){
		if(StringUtils.isEmpty(url))return "";
		String tempUrl=url;
		if(!tempUrl.contains("http://")){
			String systemCode=ApiFactory.getAcsService().getSystemById(systemId).getCode();
			String resultUrl=SystemUrls.getSystemUrl(systemCode);
			if(PropUtils.isBasicSystem(resultUrl)){
				resultUrl = SystemUrls.getSystemPageUrl("imatrix");
			}
			if(tempUrl.substring(0,1).equals("/")){
				tempUrl=resultUrl+tempUrl;
			}else{
				tempUrl=resultUrl+"/"+tempUrl;
			}
		}
		return tempUrl;
	}
	
	private String getHttpConnection(WorkflowDefinition wfDefinition,String url,String extendProperty){
		String result="";
		HttpPost httppost = new HttpPost(url);
		HttpClient httpclient = new DefaultHttpClient();
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();  
		formparams.add(new BasicNameValuePair("companyId", defCompanyId.toString()));  
		formparams.add(new BasicNameValuePair("processCode", wfDefinition.getCode()));  
		formparams.add(new BasicNameValuePair("processName", wfDefinition.getName()));  
		formparams.add(new BasicNameValuePair("processId", wfDefinition.getId().toString()));  
		formparams.add(new BasicNameValuePair("tacheCode", tacheCode));  
		formparams.add(new BasicNameValuePair("tacheName", tacheName));  
		formparams.add(new BasicNameValuePair("extendProperty", extendProperty));  
		UrlEncodedFormEntity uefEntity;
		try {
			uefEntity = new UrlEncodedFormEntity(formparams, "UTF-8");  
			httppost.setEntity(uefEntity);  
			result=httpclient.execute(httppost, responseHandler);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		httpclient.getConnectionManager().shutdown();
		return result;
	}
	
	private String getRestfulRequest(WorkflowDefinition wfDefinition,String url,String extendProperty){
		String result="";
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		String param = "companyId="+defCompanyId+"&processCode="+wfDefinition.getCode();
		param +="&processName="+wfDefinition.getName()+"&tacheCode="+tacheCode;
		param +="&tacheName="+tacheName+"&extendProperty="+extendProperty+"&processId="+wfDefinition.getId();
		WebResource service = client.resource(url);
		ClientResponse cr = service
		.entity(param,"text/html;charset=UTF-8")
		.accept("text/html;charset=UTF-8")
		.post(ClientResponse.class);
		if(cr != null) result=cr.getEntity(String.class);
		return result;
	}
	
	public WorkflowDefinition getModel() {
		return null;
	}

	public Long getTemplateId() {
		return templateId;
	}

	public void setTemplateId(Long templateId) {
		this.templateId = templateId;
	}

	public Long getWfdId() {
		return wfdId;
	}

	public void setWfdId(Long wfdId) {
		this.wfdId = wfdId;
	}

	public List<FormView> getForms() {
		return forms;
	}

	public void setForms(List<FormView> forms) {
		this.forms = forms;
	}

	public String getCurrentorLoginName() {
		return currentorLoginName;
	}

	public void setCurrentorLoginName(String currentorLoginName) {
		this.currentorLoginName = currentorLoginName;
	}

	public String getCurrentorName() {
		return currentorName;
	}

	public void setCurrentorName(String currentorName) {
		this.currentorName = currentorName;
	}

	public String getDefCreator() {
		return defCreator;
	}

	public void setDefCreator(String defCreator) {
		this.defCreator = defCreator;
	}

	public String getDefCreatorName() {
		return defCreatorName;
	}

	public void setDefCreatorName(String defCreatorName) {
		this.defCreatorName = defCreatorName;
	}

	public Long getDefSystemId() {
		return defSystemId;
	}

	public void setDefSystemId(Long defSystemId) {
		this.defSystemId = defSystemId;
	}

	public Long getDefCompanyId() {
		return defCompanyId;
	}

	public void setDefCompanyId(Long defCompanyId) {
		this.defCompanyId = defCompanyId;
	}

	public String getSaveUrl() {
		return saveUrl;
	}

	public void setSaveUrl(String saveUrl) {
		this.saveUrl = saveUrl;
	}

	public String getBackUrl() {
		return backUrl;
	}

	public void setBackUrl(String backUrl) {
		this.backUrl = backUrl;
	}

	public Long getType() {
		return type;
	}

	public void setType(Long type) {
		this.type = type;
	}

	public String getXmlFile() {
		return xmlFile;
	}

	public void setXmlFile(String xmlFile) {
		this.xmlFile = xmlFile;
	}

	public String getTacheName() {
		return tacheName;
	}

	public void setTacheName(String tacheName) {
		this.tacheName = tacheName;
	}

	public String getTacheCode() {
		return tacheCode;
	}

	public void setTacheCode(String tacheCode) {
		this.tacheCode = tacheCode;
	}

	public String getPageType() {
		return pageType;
	}

	public void setPageType(String pageType) {
		this.pageType = pageType;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	} 
}
