package com.norteksoft.wf.engine.web;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.springframework.beans.factory.annotation.Autowired;

import com.norteksoft.acs.api.AcsApi;
import com.norteksoft.mms.form.entity.FormControl;
import com.norteksoft.mms.form.entity.FormView;
import com.norteksoft.mms.form.service.FormViewManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.entity.Option;
import com.norteksoft.product.api.entity.WorkflowInstance;
import com.norteksoft.product.api.utils.BeanUtil;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.JsonParser;
import com.norteksoft.product.util.ParameterUtils;
import com.norteksoft.product.util.ThreadParameters;
import com.norteksoft.product.web.struts2.CrudActionSupport;
import com.norteksoft.product.web.struts2.Struts2Utils;
import com.norteksoft.wf.base.utils.Dom4jUtils;
import com.norteksoft.wf.engine.entity.AllBranchModelViewFlex;
import com.norteksoft.wf.engine.entity.AllBusinessModelViewFlex;
import com.norteksoft.wf.engine.entity.AllDeptsModelViewFlex;
import com.norteksoft.wf.engine.entity.AllRolesModelViewFlex;
import com.norteksoft.wf.engine.entity.CurrentTasksModelFlex;
import com.norteksoft.wf.engine.entity.DocumentTemplate;
import com.norteksoft.wf.engine.entity.SubDeptsModelViewFlex;
import com.norteksoft.wf.engine.entity.UsersNotInDeptModelViewFlex;
import com.norteksoft.wf.engine.entity.WfHistoryModelFlex;
import com.norteksoft.wf.engine.entity.WorkflowDefinition;
import com.norteksoft.wf.engine.entity.WorkflowType;
import com.norteksoft.wf.engine.entity.WorkgroupsModelViewFlex;
import com.norteksoft.wf.engine.service.DocumentTemplateFileManager;
import com.norteksoft.wf.engine.service.InstanceHistoryManager;
import com.norteksoft.wf.engine.service.WorkflowDefinitionManager;
import com.norteksoft.wf.engine.service.WorkflowEditorManager;
import com.norteksoft.wf.engine.service.WorkflowInstanceManager;
import com.norteksoft.wf.engine.service.WorkflowTypeManager;
@Namespace("/engine")
@ParentPackage("default")
@Results( { @Result(name = CrudActionSupport.RELOAD, location = "workflow-editor?wfdId=${wfdId}&wfdFile=${wfdFile}", type = "redirectAction")})
public class WorkflowEditorAction extends CrudActionSupport<WorkflowDefinition>{
	private static final long serialVersionUID = 1L;
	private Long wfdId;//流程定义id
	private WorkflowDefinition workflowDefinition;
	private String currentNodeId;//树当前节点id
	private String systemCode;//系统编码
	private Long systemId;//系统id
	private String formCode;//表单编码
	private String formVersion;//版本号
	private String code;//选项组编号
	private String wfTypeId;//
	private Long typeId;
	private String typeCode;//类别（人员：user,部门：department，工作组：workgroup）
	private String optionGroupCode;
	private Long companyId;//树公司id
	private String xmlCode;
	private String xmlContent;//流程定义xml
	private Long processTypeId;//流程类型id
	private String processTypeCode;//流程类型编码
	private String linkflag;//催办标示（流程属性或环节属性）
	private Integer version;
	private String option;
	private String parentDeptCode;
	private String deptCode;
	private String branchCode;
	private String saveUrl = "";
	private Long templateId;//模板ID
	private String instanceId;
	private String parentWorkflowId;
	private String tacheName;//环节名称
	private String processId;
	private String processInstanceId;//流程实例id
	private String xml;//xml字符串
	
	@Autowired
	private WorkflowDefinitionManager workflowDefinitionManager;
	@Autowired
	private WorkflowEditorManager workflowEditorManager;
	@Autowired
	private FormViewManager formViewManager;
	@Autowired
	private WorkflowTypeManager workflowTypeManager;
	@Autowired
	private AcsApi acsApi;
	@Autowired
	private DocumentTemplateFileManager documentTemplateFileManager;
	@Autowired
	private InstanceHistoryManager instanceHistoryManager;
	@Autowired
	private WorkflowInstanceManager workflowInstanceManager;
	private Long wfCompanyId;//流程属性公司ID
	private File file;
	private String fileName;
/***************************新版流程编辑器-开始**********************************************/
	/**
	 * 流程属性/权限控制/指定人员树
	 * @return
	 * @throws Exception
	 */
	@Action("wfeditor-designee-tree-data-js")
	public String wfeditorDesigneeTreeDataJs()  throws Exception{
		ThreadParameters threadParameters =new ThreadParameters(companyId);
		ParameterUtils.setParameters(threadParameters);
		String designeeTree=workflowEditorManager.createDesigneeTree(systemCode,currentNodeId);
		String callback=Struts2Utils.getParameter("jsoncallback");
		this.renderText(callback+"("+designeeTree+")");
		return null;
	}
	/**
	 * 流程属性/基本属性/流程管理员
	 * @return
	 * @throws Exception
	 */
	@Action("wfeditor-user-tree-data-js")
	public String wfeditorUserTreeDataJs()  throws Exception{
		ThreadParameters threadParameters =new ThreadParameters(companyId);
		ParameterUtils.setParameters(threadParameters);
		String designeeTree=workflowEditorManager.createUserTree(systemCode,currentNodeId);
		String callback=Struts2Utils.getParameter("jsoncallback");
		this.renderText(callback+"("+designeeTree+")");
		return null;
	}
	/**
	 * 流程属性/催办设置/通知人员选择
	 * @return
	 * @throws Exception
	 */
	@Action("wfeditor-userstandardvalue-tree-data-js")
	public String wfeditorUserStandardvalueTreeDataJs()  throws Exception{
		ThreadParameters threadParameters =new ThreadParameters(companyId);
		ParameterUtils.setParameters(threadParameters);
		String designeeTree="";
		if(typeCode!=null&&typeCode.equals("user")){
			designeeTree=workflowEditorManager.createUserStandardvalueTree(systemCode,currentNodeId,linkflag);
		}else if(typeCode!=null&&typeCode.equals("role")){
			designeeTree=workflowEditorManager.createRoleStandardvalueTree(systemCode,currentNodeId);
		}else if(typeCode!=null&&typeCode.equals("department")){
			designeeTree=workflowEditorManager.createDeptStandardvalueTree(systemCode,currentNodeId,linkflag);
		}else if(typeCode!=null&&typeCode.equals("workgroup")){
			designeeTree=workflowEditorManager.createWorkGroupStandardvalueTree(systemCode,currentNodeId,linkflag);
		}
		String callback=Struts2Utils.getParameter("jsoncallback");
		this.renderText(callback+"("+designeeTree+")");
		return null;
	}
	
	/**
	 * 环节属性/办理人设置/按条件筛选
	 * @return
	 * @throws Exception
	 */
	@Action("wfeditor-user-standardvalue-formfield-tree-js")
	public String userStandardvalueFormFieldTreeDataJs()  throws Exception{
		ThreadParameters threadParameters =new ThreadParameters(companyId);
		ParameterUtils.setParameters(threadParameters);
		String designeeTree="";
		if(StringUtils.isNotEmpty(formCode)&&StringUtils.isNotEmpty(formVersion)){
			List<FormControl> lists = formViewManager.getControlsByCodeAndVersion(companyId,formCode,Integer.valueOf(formVersion));
			if(typeCode!=null&&typeCode.equals("user")){//人员
				designeeTree=workflowEditorManager.createUserStandardvalueAndFormFieldTree(systemCode,currentNodeId,lists);
			}else if(typeCode!=null&&typeCode.equals("role")){//角色
				designeeTree=workflowEditorManager.createRoleAndFormFieldTree(systemCode,currentNodeId,lists);
			}else if(typeCode!=null&&typeCode.equals("department")){//部门
				designeeTree=workflowEditorManager.createDeptStandardvalueAndFormFieldTree(systemCode,currentNodeId,lists);
			}else if(typeCode!=null&&typeCode.equals("workgroup")){//工作组
				designeeTree=workflowEditorManager.createWorkGroupStandardvalueAndFormFieldTree(systemCode,currentNodeId,lists);
			}
		}
		String callback=Struts2Utils.getParameter("jsoncallback");
		this.renderText(callback+"("+designeeTree+")");
		return null;
	}
	
	/**
	 * 获得所有的表单字段
	 * @return
	 * @throws Exception
	 */
	@Action("wfeditor-allFormFieldUrl-js")
	public String allFormFieldUrlJs()  throws Exception{
		if(StringUtils.isNotEmpty(formCode) && StringUtils.isNotEmpty(formVersion)){
			FormView formView = formViewManager.getFormViewByCodeAndVersion(wfCompanyId,formCode, Integer.valueOf(formVersion));
			String callback=Struts2Utils.getParameter("jsoncallback");
			if(formView!=null){
				List<FormControl> lists = formViewManager.getControlsByCodeAndVersion(wfCompanyId,formCode,Integer.valueOf(formVersion));
				String formstandard=workflowEditorManager.allFormJosn(lists);
				formstandard=StringUtils.removeEnd(formstandard, ",");
				this.renderText(callback+"(["+formstandard+"])");
			}else{
				this.renderText(callback+"(\"\")");//表示没有此表单
			}
		}
		return null;
	}
	/**
	 * 获得所有的标准字段
	 * @return
	 * @throws Exception
	 */
	@Action("wfeditor-allStandardFieldUrl-js")
	public String allStandardFieldUrlJs()  throws Exception{
		String formstandard=workflowEditorManager.allStandardFieldJosn();
		String callback=Struts2Utils.getParameter("jsoncallback");
		this.renderText(callback+"(["+formstandard+"])");
		return null;
	}
	/**
	 * 获得所有的表单字段和标准字段
	 * @return
	 * @throws Exception
	 */
	@Action("wfeditor-allFormAndStandardUrl-js")
	public String  allFormFieldAndStandardUrlJs()  throws Exception{
		if(StringUtils.isNotEmpty(formCode) && StringUtils.isNotEmpty(formVersion)){
			List<FormControl> lists = formViewManager.getControlsByCodeAndVersion(wfCompanyId,formCode,Integer.valueOf(formVersion));
			String formstandard=workflowEditorManager.allFormAndStandardJosn(lists);
			String callback=Struts2Utils.getParameter("jsoncallback");
			this.renderText(callback+"(["+formstandard+"])");
		}
		return null;
	}
	/**环节属性-权限控制
	 * 获得所有的表单字段树和组织结构树和标准值树
	 * @return
	 * @throws Exception
	 */
	@Action("wfeditor-allOrganizationAndStandardUrl-js")
	public String allOrganizationAndStandardUrlJs()  throws Exception{
		ThreadParameters threadParameters =new ThreadParameters(companyId);
		ParameterUtils.setParameters(threadParameters);
		String designeeTree="";
		List<FormControl> lists = formViewManager.getControlsByCodeAndVersion(companyId,formCode,Integer.valueOf(formVersion));
		if(typeCode!=null&&(typeCode.equals("文档创建人姓名")||typeCode.equals("创建人直属上级姓名"))){
			designeeTree=workflowEditorManager.createUserFormTree(systemCode,currentNodeId,lists);
		}else if(typeCode!=null&&(typeCode.equals("文档创建人角色")||typeCode.equals("创建人直属上级角色")||typeCode.equals("当前办理人角色")||typeCode.equals("当前办理人直属上级角色"))){
			designeeTree=workflowEditorManager.createRoleFormTree(systemCode,currentNodeId,lists);
		}else if(typeCode!=null&&(typeCode.equals("文档创建人部门")||typeCode.equals("创建人上级部门")||typeCode.equals("创建人直属上级部门"))){
			designeeTree=workflowEditorManager.createDeptFormTree(systemCode,currentNodeId,lists);
		}else if(typeCode!=null&&typeCode.equals("创建人顶级部门")){
			designeeTree=workflowEditorManager.createUpstageDeptFormTree(systemCode,currentNodeId,lists);
		}else if(typeCode!=null&&(typeCode.equals("文档创建人工作组")||typeCode.equals("创建人直属上级工作组"))){
			designeeTree=workflowEditorManager.createdocumentCreatorWorkGroupStandardvalueTree(systemCode,currentNodeId,lists);
		}else if(typeCode!=null&&(typeCode.equals("当前办理人姓名")||typeCode.equals("当前办理人直属上级姓名"))){
			designeeTree=workflowEditorManager.createcurrentTransactorUserFormTree(systemCode,currentNodeId,lists);
		}else if(typeCode!=null&&(typeCode.equals("当前办理人部门")||typeCode.equals("当前办理人上级部门")||typeCode.equals("当前办理人直属上级部门"))){
			designeeTree=workflowEditorManager.createcurrentTransactorDeptFormTree(systemCode,currentNodeId,lists);
		}else if(typeCode!=null&&typeCode.equals("当前办理人顶级部门")){
			designeeTree=workflowEditorManager.createcurrentTransactorUpstageDepartmentFormTree(systemCode,currentNodeId,lists);
		}else if(typeCode!=null&&(typeCode.equals("当前办理人工作组")||typeCode.equals("当前办理人直属上级工作组"))){
			designeeTree=workflowEditorManager.createcurrentTransactorWorkGroupStandardvalueTree(systemCode,currentNodeId,lists);
		}
		String callback=Struts2Utils.getParameter("jsoncallback");
		this.renderText(callback+"("+designeeTree+")");
		return null;
	}
	/**
	 * 流向属性-流向流过的条件-获得所有的表单字段和标准字段
	 * @return
	 * @throws Exception
	 */
	@Action("wfeditor-transitionFormAndStandardUrl-js")
	public String  transitionFormFieldAndStandardUrlJs()  throws Exception{
		if(StringUtils.isNotEmpty(formCode) && StringUtils.isNotEmpty(formVersion)){
			List<FormControl> lists = formViewManager.getControlsByCodeAndVersion(wfCompanyId,formCode,Integer.valueOf(formVersion));
			String formstandard=workflowEditorManager.transitionFormAndStandardJosn(lists);
			String callback=Struts2Utils.getParameter("jsoncallback");
			this.renderText(callback+"(["+formstandard+"])");
		}
		return null;
	}
	/**流向属性-流向流过的条件
	 * 获得所有的表单字段树和组织结构树和标准值树
	 * @return
	 * @throws Exception
	 */
	@Action("wfeditor-transitionOrganizationAndStandardUrl-js")
	public String transitionOrganizationAndStandardUrlJs()  throws Exception{
		ThreadParameters threadParameters =new ThreadParameters(companyId);
		ParameterUtils.setParameters(threadParameters);
		String designeeTree="";
		List<FormControl> lists = formViewManager.getControlsByCodeAndVersion(companyId,formCode,Integer.valueOf(formVersion));
		if(typeCode!=null&&(typeCode.equals("文档创建人姓名")||typeCode.equals("创建人直属上级姓名")||typeCode.equals("当前办理人姓名"))){
			designeeTree=workflowEditorManager.createUserFormTree(systemCode,currentNodeId,lists);
		}else if(typeCode!=null&&(typeCode.equals("文档创建人角色")||typeCode.equals("创建人直属上级角色")||typeCode.equals("当前办理人角色"))){
			designeeTree=workflowEditorManager.createRoleFormTree(systemCode,currentNodeId,lists);
		}else if(typeCode!=null&&(typeCode.equals("当前办理人直属上级角色")||typeCode.equals("上一环节办理人角色")||typeCode.equals("上一环节办理人直属上级角色"))){
			designeeTree=workflowEditorManager.createRoleFormTree(systemCode,currentNodeId,lists);
		}else if(typeCode!=null&&(typeCode.equals("文档创建人部门")||typeCode.equals("创建人上级部门")||typeCode.equals("创建人直属上级部门"))){
			designeeTree=workflowEditorManager.createDeptFormTree(systemCode,currentNodeId,lists);
		}else if(typeCode!=null&&typeCode.equals("创建人顶级部门")){
			designeeTree=workflowEditorManager.createUpstageDeptFormTree(systemCode,currentNodeId,lists);
		}else if(typeCode!=null&&(typeCode.equals("文档创建人工作组")||typeCode.equals("创建人直属上级工作组")||typeCode.equals("当前办理人工作组"))){
			designeeTree=workflowEditorManager.createdocumentCreatorWorkGroupStandardvalueTree(systemCode,currentNodeId,lists);
		}else if(typeCode!=null&&(typeCode.equals("当前办理人直属上级工作组")||typeCode.equals("上一环节办理人工作组")||typeCode.equals("上一环节办理人直属上级工作组"))){
			designeeTree=workflowEditorManager.createdocumentCreatorWorkGroupStandardvalueTree(systemCode,currentNodeId,lists);
		}else if(typeCode!=null&&(typeCode.equals("当前办理人上级部门")||typeCode.equals("当前办理人直属上级部门"))){
			designeeTree=workflowEditorManager.createcurrentTransactorDeptFormTree(systemCode,currentNodeId,lists);
		}else if(typeCode!=null&&typeCode.equals("当前办理人顶级部门")||typeCode.equals("上一环节办理人顶级部门")){
			designeeTree=workflowEditorManager.createcurrentTransactorUpstageDepartmentFormTree(systemCode,currentNodeId,lists);
		}else if(typeCode!=null&&(typeCode.equals("当前办理人直属上级姓名")||typeCode.equals("上一环节办理人姓名")||typeCode.equals("上一环节办理人直属上级姓名"))){
			designeeTree=workflowEditorManager.createUserFormTree(systemCode,currentNodeId,lists);
		}else if(typeCode!=null&&(typeCode.equals("当前办理人部门")||typeCode.equals("上一环节办理人部门"))){
			designeeTree=workflowEditorManager.createpreviousTransactorDepartmentFormTree(systemCode,currentNodeId,lists);
		}else if(typeCode!=null&&(typeCode.equals("上一环节办理人上级部门")||typeCode.equals("上一环节办理人直属上级部门"))){
			designeeTree=workflowEditorManager.createcurrentTransactorDeptFormTree(systemCode,currentNodeId,lists);
		}
		String callback=Struts2Utils.getParameter("jsoncallback");
		this.renderText(callback+"("+designeeTree+")");
		return null;
	}
	
	/**
	 * 子流程
	 * @return
	 * @throws Exception
	 */
	@Action("wfeditor-workflow-childwf-js")
	public String childwfJs()  throws Exception{
		List<WorkflowDefinition> wfds=workflowDefinitionManager.getActiveDefinition(companyId,systemCode); 
		String data=JsonParser.object2Json(wfds);
		String callback=Struts2Utils.getParameter("jsoncallback");
		
		this.renderText(callback+"("+data+")");
		return null;
	}
	/**
	 * 流程属性/流程类型
	 * @return
	 * @throws Exception
	 */
	@Action("wfeditor-workflow-wftype-js")
	public String html5wfTypeJs()  throws Exception{
		String callback=Struts2Utils.getParameter("jsoncallback");
		ThreadParameters threadParameters =new ThreadParameters(wfCompanyId);
		ParameterUtils.setParameters(threadParameters);
		
		String data=JsonParser.object2Json(workflowTypeManager.getAllWorkflowType(),"yyyy-MM-dd HH:mm");
		this.renderText(callback+"("+data+")");
		//this.renderText(JsonParser.object2Json(workflowDefinition,"yyyy-MM-dd HH:mm"));
		return null;
	}
	/**
	 * 流程属性/流程对应表单
	 * @return
	 * @throws Exception
	 */
	@Action("wfeditor-workflow-wfformview-js")
	public String html5wfformViewJs()  throws Exception{
		String callback=Struts2Utils.getParameter("jsoncallback");
		ThreadParameters threadParameters =new ThreadParameters(companyId);
		ParameterUtils.setParameters(threadParameters);
		String designeeTree=workflowEditorManager.createFormTree();
		this.renderText(callback+"("+designeeTree+")");
		return null;
	}
	/**
	 * 流程属性/流程所在系统
	 * @return
	 * @throws Exception
	 */
	@Action("wfeditor-workflow-system-js")
	public String html5wfsystemJs()  throws Exception{
		String callback=Struts2Utils.getParameter("jsoncallback");
		ThreadParameters threadParameters =new ThreadParameters(wfCompanyId);
		ParameterUtils.setParameters(threadParameters);
		
		String data=JsonParser.object2Json(AcsApi.getAllBusiness(ContextUtils.getCompanyId()));
		this.renderText(callback+"("+data+")");
		return null;
	}
	/**
	 * 流程属性/流程所属分支
	 * @return
	 * @throws Exception
	 */
	@Action("wfeditor-workflow-branch-js")
	public String html5wfbranchJs()  throws Exception{
		String callback=Struts2Utils.getParameter("jsoncallback");
		ThreadParameters threadParameters =new ThreadParameters(wfCompanyId);
		ParameterUtils.setParameters(threadParameters);
		
		String data=JsonParser.object2Json(AcsApi.getAllBranch(ContextUtils.getCompanyId()));
		this.renderText(callback+"("+data+")");
		return null;
	}
	/**
	 * 流程-html5另存为
	 * 
	 */
	@Action("wfeditor-workflow-saveAs")
	public String workFlowSaveAs()  throws Exception{
		if (xmlContent != null) {
			downloadXML("myXml.xml", xmlContent);
		}
        return null;
	}
	/**
	 * 下载文档
	 * @param fileName
	 * @param content
	 * @throws IOException 
	 */
	public static void downloadXML(String fileName,String xmlContent) throws IOException{
		HttpServletResponse response = ServletActionContext.getResponse();
		response.reset();
		response.setContentType("application/x-download");
		OutputStream out=null;
		try {
			byte[] byname=fileName.getBytes("gbk");
			fileName=new String(byname,"8859_1");
			response.setCharacterEncoding("UTF-8");
			response.addHeader("Content-Disposition", "attachment;filename=\""+fileName+"\"");
			response.getOutputStream().write(xmlContent.getBytes("UTF-8"));  
			out=response.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			out.close();
		}
	}
	/**
	 * 根据选项组编号获得选项组
	 * @return
	 * @throws Exception
	 */
	@Action("wfeditor-workflow-getOptionGroupByCodeUrl-js")
	public String getOptionGroupByCodeUrlJs()  throws Exception{
		String callback=Struts2Utils.getParameter("jsoncallback");
		ThreadParameters threadParameters =new ThreadParameters(wfCompanyId);
		ParameterUtils.setParameters(threadParameters);
		List<Option> ops = ApiFactory.getSettingService().getOptionsByGroupCode(code);
		String data = workflowEditorManager.parseOptionGroups(ops);;
		this.renderText(callback+"(["+data+"])");
		return null;
	}
	/**
	 * 创建流程定义,用于兼容老版本流程图
	 * @return
	 * @throws Exception
	 */
	@Action("wfeditor-createWfDefinitionById-js")
	public String createWfDefinitionByIdJs()  throws Exception{
		Long workflowDefinitionId = workflowDefinitionManager.createWfDefinitionById(companyId, xmlContent, processTypeId, systemId);
		this.renderText(workflowDefinitionId.toString());
		return null;
	}
	/**
	 * 创建流程定义
	 * @return
	 * @throws Exception
	 */
	@Action("wfeditor-createWfDefinition-js")
	public String createWfDefinitionJs()  throws Exception{
		Long workflowDefinitionId = 0L;
		try {
			workflowDefinitionId = workflowDefinitionManager.createWfDefinition(companyId, xmlContent, processTypeCode, systemCode,"");
		} catch (Exception e) {
			this.renderText("error:" + e.getMessage() );
			return null;
		}
		this.renderText(workflowDefinitionId.toString());
		return null;
	}
	/**
	 * 修改流程定义,用于兼容老版本流程图
	 * @return
	 * @throws Exception
	 */
	@Action("wfeditor-updateWfDefinitionById-js")
	public String updateWfDefinitionByIdJs()  throws Exception{
		Long workflowDefinitionId = workflowEditorManager.updateWfDefinitionById(wfdId, companyId, xmlContent, processTypeId, systemId);
		this.renderText(workflowDefinitionId.toString());
		return null;
	}
	/**
	 * 修改流程定义
	 * @return
	 * @throws Exception
	 */
	@Action("wfeditor-updateWfDefinition-js")
	public String updateWfDefinitionJs()  throws Exception{
		Long workflowDefinitionId = workflowEditorManager.updateWfDefinition(wfdId, companyId, xmlContent, processTypeCode, systemCode,"");
		this.renderText(workflowDefinitionId.toString());
		return null;
	}
	/**
	 * 通过流程定义id和公司id获得流程定义xml文件和流程定义状态
	 * @return
	 * @throws Exception
	 */
	@Action("wfeditor-getXmlByDefinitionId-js")
	public String getXmlByDefinitionIdJs()  throws Exception{
		String callback=Struts2Utils.getParameter("jsoncallback");
		this.renderText(callback+"("+workflowEditorManager.getXmlAndWfStateJson(wfdId, companyId)+")");
		return null;
	}
	/**
	 * 根据流程实例ID查询历史环节和当前环节
	 * @return
	 * @throws Exception
	 */
	@Action("wfeditor-getHistoryCurrentTacheByInstanceId-js")
	public String getHistoryByInstanceIdJs()  throws Exception{
		String callback=Struts2Utils.getParameter("jsoncallback");
		this.renderText(callback+"("+workflowEditorManager.getHistoryCurrentTacheByInstanceId(companyId,processInstanceId)+")");
		return null;
	}
	/**
	 * 根据父流程的workflowId和环节名获得它的子流程实例
	 * @return
	 * @throws Exception
	 */
	@Action("wfeditor-getSubProcessInstanceByTaskName-js")
	public String getSubProcessInstanceByTaskNameJs()  throws Exception{
		String callback=Struts2Utils.getParameter("jsoncallback");
		this.renderText(callback+"("+workflowEditorManager.getSubProcessInstanceByTaskName(processInstanceId,tacheName)+")");
		return null;
	}
	/**
	 * 根据流程实例ID查询流程定义文件
	 * @return
	 * @throws Exception
	 */
	@Action("wfeditor-getXmlByInstanceId-js")
	public String getXmlByInstanceIdJs()  throws Exception{
		String callback=Struts2Utils.getParameter("jsoncallback");
		this.renderText(callback+"("+workflowEditorManager.getXmlByInstanceId(processInstanceId,companyId)+")");
		return null;
	}
	/**
	 * 正文权限设置/加载正文模版
	 * @return
	 * @throws Exception
	 */
	@Action("wfeditor-workflow-loadTemplate-js")
	public String loadTemplateJs()  throws Exception{
		String callback=Struts2Utils.getParameter("jsoncallback");
		ThreadParameters threadParameters =new ThreadParameters(wfCompanyId);
		ParameterUtils.setParameters(threadParameters);
		long typeid=0;
		if(StringUtils.isNotEmpty(processTypeCode)){
			typeid=workflowTypeManager.getWorkflowType(processTypeCode).getId();
		}
		List<DocumentTemplate> templates = documentTemplateFileManager.getTemplateByType(typeid, Long.valueOf(wfCompanyId) );
		String data = JsonParser.object2Json(templates);
		
		this.renderText(callback+"("+data+")");
		return null;
	}
	/**
	 * 获得当前用户信息
	 * @return
	 * @throws Exception
	 */
	@Action("wfeditor-getCurrentUserInformation-js")
	public String getCurrentUserInformationJs()  throws Exception{
		String callback=Struts2Utils.getParameter("jsoncallback");
		String currentUserBranchCode = "";
		String currentUserBranchName = "";
		if(StringUtils.isEmpty(ContextUtils.getSubCompanyCode())){
			currentUserBranchCode = ContextUtils.getCompanyCode();
			currentUserBranchName = ContextUtils.getCompanyName();
		}else{
			currentUserBranchCode = ContextUtils.getSubCompanyCode();
			if(StringUtils.isEmpty(ContextUtils.getSubCompanyShortTitle())){
				currentUserBranchName = ContextUtils.getSubCompanyName();
			}else{
				currentUserBranchName = ContextUtils.getSubCompanyShortTitle();
			}
		}
		StringBuilder data = new StringBuilder();
		data.append("'companyId':");
		data.append(ContextUtils.getCompanyId());
		data.append(",");
		data.append("'currentUserLoginName':'");
		data.append(ContextUtils.getLoginName());
		data.append("',");
		data.append("'currentUserName':'");
		data.append(ContextUtils.getUserName());
		data.append("',");
		data.append("'currentUserBranchCode':'");
		data.append(currentUserBranchCode);
		data.append("',");
		data.append("'currentUserBranchName':'");
		data.append(currentUserBranchName);
		data.append("'");
		this.renderText(callback+"({"+data.toString()+"})");
		return null;
	}
	/**
	 * 新建或修改页面
	 */
	@Override
	@Action("workflow-editor-input")
	public String input() throws Exception {
		if(templateId!=null){
			xml = workflowDefinitionManager.getTemplateXml(templateId);
		}
		return SUCCESS;
	}
	
	/**
	 * 打开xml文件
	 */
	@Action("wfeditor-openXmlFile-js")
	public String openXmlFileJs() throws Exception {
	    if (file.length()>0) {
	        this.renderText(workflowEditorManager.parseXml(file));
        } else {
            this.renderText("上传附件内容不能为空");
        }
		return null;
	}
	/**
	 * 解析xml文件
	 */
	@Action("wfeditor-parseXml-js")
	public String parseXmlJs() throws Exception {
		this.renderText(workflowEditorManager.parseXml(xmlContent));
		return null;
	}
	/**
	 * 验证表单编号是否存在
	 */
	@Action("wfeditor-validateFormCodeIsExist")
	public String validateFormCodeIsExist() throws Exception {
		FormView formView = formViewManager.getFormViewByCodeAndVersion(companyId,formCode,Integer.valueOf(formVersion));
		if(formView == null){
			this.renderText("false");
		}else{
			this.renderText("true");
		}
		return null;
	}
/***************************新版流程编辑器-结束**********************************************/
	
	/**
	 * 流程属性/权限控制/指定人员树
	 * @return
	 * @throws Exception
	 */
	@Action("wfeditor-designee-tree-data")
	public String wfeditorDesigneeTreeData()  throws Exception{
		String designeeTree=workflowEditorManager.createDesigneeTree(systemCode,currentNodeId);
		String callback=Struts2Utils.getParameter("callback");
		this.renderText(callback+"({\"data\":"+designeeTree+"})");
		return null;
	}
	/**
	 * 流程属性/基本属性/流程管理员
	 * @return
	 * @throws Exception
	 */
	@Action("wfeditor-user-tree-data")
	public String wfeditorUserTreeData()  throws Exception{
		String designeeTree=workflowEditorManager.createUserTree(systemCode,currentNodeId);
		String callback=Struts2Utils.getParameter("callback");
		this.renderText(callback+"({\"data\":"+designeeTree+"})");
		return null;
	}
	/**
	 * 流程属性/催办设置/通知人员选择
	 * @return
	 * @throws Exception
	 */
	@Action("wfeditor-userstandardvalue-tree-data")
	public String wfeditorUserStandardvalueTreeData()  throws Exception{
		String designeeTree="";
		if(typeCode!=null&&typeCode.equals("user")){
			designeeTree=workflowEditorManager.createUserStandardvalueTree(systemCode,currentNodeId,linkflag);
		}else if(typeCode!=null&&typeCode.equals("role")){
			designeeTree=workflowEditorManager.createRoleStandardvalueTree(systemCode,currentNodeId);
		}else if(typeCode!=null&&typeCode.equals("department")){
			designeeTree=workflowEditorManager.createDeptStandardvalueTree(systemCode,currentNodeId,linkflag);
		}else if(typeCode!=null&&typeCode.equals("workgroup")){
			designeeTree=workflowEditorManager.createWorkGroupStandardvalueTree(systemCode,currentNodeId,linkflag);
		}
		String callback=Struts2Utils.getParameter("callback");
		this.renderText(callback+"({\"data\":"+designeeTree+"})");
		return null;
	}
	
	/**
	 * 环节属性/办理人设置/按条件筛选
	 * @return
	 * @throws Exception
	 */
	@Action("wfeditor-user-standardvalue-formfield-tree")
	public String userStandardvalueFormFieldTreeData()  throws Exception{
		String designeeTree="";
		if(StringUtils.isNotEmpty(formCode)&&StringUtils.isNotEmpty(formVersion)){
			List<FormControl> lists = formViewManager.getControlsByCodeAndVersion(companyId,formCode,Integer.valueOf(formVersion));
			if(typeCode!=null&&typeCode.equals("user")){//人员
				designeeTree=workflowEditorManager.createUserStandardvalueAndFormFieldTree(systemCode,currentNodeId,lists);
			}else if(typeCode!=null&&typeCode.equals("role")){//角色
				designeeTree=workflowEditorManager.createRoleAndFormFieldTree(systemCode,currentNodeId,lists);
			}else if(typeCode!=null&&typeCode.equals("department")){//部门
				designeeTree=workflowEditorManager.createDeptStandardvalueAndFormFieldTree(systemCode,currentNodeId,lists);
			}else if(typeCode!=null&&typeCode.equals("workgroup")){//工作组
				designeeTree=workflowEditorManager.createWorkGroupStandardvalueAndFormFieldTree(systemCode,currentNodeId,lists);
			}
		}
		String callback=Struts2Utils.getParameter("callback");
		this.renderText(callback+"({\"data\":"+designeeTree+"})");
		return null;
	}
	
	/**
	 * 获得所有的表单字段
	 * @return
	 * @throws Exception
	 */
	@Action("wfeditor-allFormFieldUrl")
	public String allFormFieldUrl()  throws Exception{
		if(StringUtils.isNotEmpty(formCode) && StringUtils.isNotEmpty(formVersion)){
			List<FormControl> lists = formViewManager.getControlsByCodeAndVersion(wfCompanyId,formCode,Integer.valueOf(formVersion));
			String formstandard=workflowEditorManager.allFormJosn(lists);
			formstandard=StringUtils.removeEnd(formstandard, ",");
			String callback=Struts2Utils.getParameter("callback");
			this.renderText(callback+"({\"data\":["+formstandard+"]})");
		}
		return null;
	}
	/**
	 * 获得所有的标准字段
	 * @return
	 * @throws Exception
	 */
	@Action("wfeditor-allStandardFieldUrl")
	public String allStandardFieldUrl()  throws Exception{
		String formstandard=workflowEditorManager.allStandardFieldJosn();
		String callback=Struts2Utils.getParameter("callback");
		this.renderText(callback+"({\"data\":["+formstandard+"]})");
		return null;
	}
	/**
	 * 获得所有的表单字段和标准字段
	 * @return
	 * @throws Exception
	 */
	@Action("wfeditor-allFormAndStandardUrl")
	public String  allFormFieldAndStandardUrl()  throws Exception{
		if(StringUtils.isNotEmpty(formCode) && StringUtils.isNotEmpty(formVersion)){
			List<FormControl> lists = formViewManager.getControlsByCodeAndVersion(wfCompanyId,formCode,Integer.valueOf(formVersion));
			String formstandard=workflowEditorManager.allFormAndStandardJosn(lists);
			String callback=Struts2Utils.getParameter("callback");
			this.renderText(callback+"({\"data\":["+formstandard+"]})");
		}
		return null;
	}
	/**环节属性-权限控制
	 * 获得所有的表单字段树和组织结构树和标准值树
	 * @return
	 * @throws Exception
	 */
	@Action("wfeditor-allOrganizationAndStandardUrl")
	public String allOrganizationAndStandardUrl()  throws Exception{
		String designeeTree="";
		List<FormControl> lists = formViewManager.getControlsByCodeAndVersion(companyId,formCode,Integer.valueOf(formVersion));
		if(typeCode!=null&&(typeCode.equals("文档创建人姓名")||typeCode.equals("创建人直属上级姓名"))){
			designeeTree=workflowEditorManager.createUserFormTree(systemCode,currentNodeId,lists);
		}else if(typeCode!=null&&(typeCode.equals("文档创建人角色")||typeCode.equals("创建人直属上级角色")||typeCode.equals("当前办理人角色")||typeCode.equals("当前办理人直属上级角色"))){
			designeeTree=workflowEditorManager.createRoleFormTree(systemCode,currentNodeId,lists);
		}else if(typeCode!=null&&(typeCode.equals("文档创建人部门")||typeCode.equals("创建人上级部门")||typeCode.equals("创建人直属上级部门"))){
			designeeTree=workflowEditorManager.createDeptFormTree(systemCode,currentNodeId,lists);
		}else if(typeCode!=null&&typeCode.equals("创建人顶级部门")){
			designeeTree=workflowEditorManager.createUpstageDeptFormTree(systemCode,currentNodeId,lists);
		}else if(typeCode!=null&&(typeCode.equals("文档创建人工作组")||typeCode.equals("创建人直属上级工作组"))){
			designeeTree=workflowEditorManager.createdocumentCreatorWorkGroupStandardvalueTree(systemCode,currentNodeId,lists);
		}else if(typeCode!=null&&(typeCode.equals("当前办理人姓名")||typeCode.equals("当前办理人直属上级姓名"))){
			designeeTree=workflowEditorManager.createcurrentTransactorUserFormTree(systemCode,currentNodeId,lists);
		}else if(typeCode!=null&&(typeCode.equals("当前办理人部门")||typeCode.equals("当前办理人上级部门")||typeCode.equals("当前办理人直属上级部门"))){
			designeeTree=workflowEditorManager.createcurrentTransactorDeptFormTree(systemCode,currentNodeId,lists);
		}else if(typeCode!=null&&typeCode.equals("当前办理人顶级部门")){
			designeeTree=workflowEditorManager.createcurrentTransactorUpstageDepartmentFormTree(systemCode,currentNodeId,lists);
		}else if(typeCode!=null&&(typeCode.equals("当前办理人工作组")||typeCode.equals("当前办理人直属上级工作组"))){
			designeeTree=workflowEditorManager.createcurrentTransactorWorkGroupStandardvalueTree(systemCode,currentNodeId,lists);
		}
		String callback=Struts2Utils.getParameter("callback");
		this.renderText(callback+"({\"data\":"+designeeTree+"})");
		return null;
	}
	/**
	 * 流向属性-流向流过的条件-获得所有的表单字段和标准字段
	 * @return
	 * @throws Exception
	 */
	@Action("wfeditor-transitionFormAndStandardUrl")
	public String  transitionFormFieldAndStandardUrl()  throws Exception{
		if(StringUtils.isNotEmpty(formCode) && StringUtils.isNotEmpty(formVersion)){
			List<FormControl> lists = formViewManager.getControlsByCodeAndVersion(wfCompanyId,formCode,Integer.valueOf(formVersion));
			String formstandard=workflowEditorManager.transitionFormAndStandardJosn(lists);
			String callback=Struts2Utils.getParameter("callback");
			this.renderText(callback+"({\"data\":["+formstandard+"]})");
		}
		return null;
	}
	/**流向属性-流向流过的条件
	 * 获得所有的表单字段树和组织结构树和标准值树
	 * @return
	 * @throws Exception
	 */
	@Action("wfeditor-transitionOrganizationAndStandardUrl")
	public String transitionOrganizationAndStandardUrl()  throws Exception{
		String designeeTree="";
		List<FormControl> lists = formViewManager.getControlsByCodeAndVersion(companyId,formCode,Integer.valueOf(formVersion));
		if(typeCode!=null&&(typeCode.equals("文档创建人姓名")||typeCode.equals("创建人直属上级姓名")||typeCode.equals("当前办理人姓名"))){
			designeeTree=workflowEditorManager.createUserFormTree(systemCode,currentNodeId,lists);
		}else if(typeCode!=null&&(typeCode.equals("文档创建人角色")||typeCode.equals("创建人直属上级角色")||typeCode.equals("当前办理人角色"))){
			designeeTree=workflowEditorManager.createRoleFormTree(systemCode,currentNodeId,lists);
		}else if(typeCode!=null&&(typeCode.equals("当前办理人直属上级角色")||typeCode.equals("上一环节办理人角色")||typeCode.equals("上一环节办理人直属上级角色"))){
			designeeTree=workflowEditorManager.createRoleFormTree(systemCode,currentNodeId,lists);
		}else if(typeCode!=null&&(typeCode.equals("文档创建人部门")||typeCode.equals("创建人上级部门")||typeCode.equals("创建人直属上级部门"))){
			designeeTree=workflowEditorManager.createDeptFormTree(systemCode,currentNodeId,lists);
		}else if(typeCode!=null&&typeCode.equals("创建人顶级部门")){
			designeeTree=workflowEditorManager.createUpstageDeptFormTree(systemCode,currentNodeId,lists);
		}else if(typeCode!=null&&(typeCode.equals("文档创建人工作组")||typeCode.equals("创建人直属上级工作组")||typeCode.equals("当前办理人工作组"))){
			designeeTree=workflowEditorManager.createdocumentCreatorWorkGroupStandardvalueTree(systemCode,currentNodeId,lists);
		}else if(typeCode!=null&&(typeCode.equals("当前办理人直属上级工作组")||typeCode.equals("上一环节办理人工作组")||typeCode.equals("上一环节办理人直属上级工作组"))){
			designeeTree=workflowEditorManager.createdocumentCreatorWorkGroupStandardvalueTree(systemCode,currentNodeId,lists);
		}else if(typeCode!=null&&(typeCode.equals("当前办理人上级部门")||typeCode.equals("当前办理人直属上级部门"))){
			designeeTree=workflowEditorManager.createcurrentTransactorDeptFormTree(systemCode,currentNodeId,lists);
		}else if(typeCode!=null&&typeCode.equals("当前办理人顶级部门")||typeCode.equals("上一环节办理人顶级部门")){
			designeeTree=workflowEditorManager.createcurrentTransactorUpstageDepartmentFormTree(systemCode,currentNodeId,lists);
		}else if(typeCode!=null&&(typeCode.equals("当前办理人直属上级姓名")||typeCode.equals("上一环节办理人姓名")||typeCode.equals("上一环节办理人直属上级姓名"))){
			designeeTree=workflowEditorManager.createUserFormTree(systemCode,currentNodeId,lists);
		}else if(typeCode!=null&&(typeCode.equals("当前办理人部门")||typeCode.equals("上一环节办理人部门"))){
			designeeTree=workflowEditorManager.createpreviousTransactorDepartmentFormTree(systemCode,currentNodeId,lists);
		}else if(typeCode!=null&&(typeCode.equals("上一环节办理人上级部门")||typeCode.equals("上一环节办理人直属上级部门"))){
			designeeTree=workflowEditorManager.createcurrentTransactorDeptFormTree(systemCode,currentNodeId,lists);
		}
		String callback=Struts2Utils.getParameter("callback");
		this.renderText(callback+"({\"data\":"+designeeTree+"})");
		return null;
	}
	
	/**
	 * 子流程
	 * @return
	 * @throws Exception
	 */
	@Action("wfeditor-workflow-childwf")
	public String childwf()  throws Exception{
		List<WorkflowDefinition> wfds=workflowDefinitionManager.getActiveDefinition(companyId,systemCode); 
		String data=JsonParser.object2Json(wfds);
		String callback=Struts2Utils.getParameter("callback");
		
		this.renderText(callback+"({\"data\":"+data+"})");
		return null;
	}
	/**
	 * 流程属性/基本属性
	 * @return
	 * @throws Exception
	 */
	@Action("wfeditor-workflow-attribute")
	public String html5Attribute()  throws Exception{
		String callback=Struts2Utils.getParameter("callback");
		String data=workflowEditorManager.workflowDefinitionJosn(workflowDefinition);
		this.renderText(callback+"({\"data\":"+data+"})");
		return null;
	}
	
	public void prepareHtml5Attribute()throws Exception{
		prepareModel();
	}
	/**
	 * 流程属性/流程类型
	 * @return
	 * @throws Exception
	 */
	@Action("wfeditor-workflow-wftype")
	public String html5wfType()  throws Exception{
		String callback=Struts2Utils.getParameter("callback");
		ThreadParameters threadParameters =new ThreadParameters(wfCompanyId);
		ParameterUtils.setParameters(threadParameters);
		
		String data=JsonParser.object2Json(workflowTypeManager.getAllWorkflowType(),"yyyy-MM-dd HH:mm");
		this.renderText(callback+"({\"data\":"+data+"})");
		//this.renderText(JsonParser.object2Json(workflowDefinition,"yyyy-MM-dd HH:mm"));
		return null;
	}
	/**
	 * 流程属性/流程对应表单
	 * @return
	 * @throws Exception
	 */
	@Action("wfeditor-workflow-wfformview")
	public String html5wfformView()  throws Exception{
		String callback=Struts2Utils.getParameter("callback");
		ThreadParameters threadParameters =new ThreadParameters(companyId);
		ParameterUtils.setParameters(threadParameters);
		String designeeTree=workflowEditorManager.createFormTree();
		this.renderText(callback+"({\"data\":"+designeeTree+"})");
		return null;
	}
	/**
	 * 流程属性/流程所在系统
	 * @return
	 * @throws Exception
	 */
	@Action("wfeditor-workflow-system")
	public String html5wfsystem()  throws Exception{
		String callback=Struts2Utils.getParameter("callback");
		ThreadParameters threadParameters =new ThreadParameters(wfCompanyId);
		ParameterUtils.setParameters(threadParameters);
		
		String data=JsonParser.object2Json(AcsApi.getAllBusiness(ContextUtils.getCompanyId()));
		this.renderText(callback+"({\"data\":"+data+"})");
		return null;
	}
	/**
	 * 流程属性/流程所属分支
	 * @return
	 * @throws Exception
	 */
	@Action("wfeditor-workflow-branch")
	public String html5wfbranch()  throws Exception{
		String callback=Struts2Utils.getParameter("callback");
		ThreadParameters threadParameters =new ThreadParameters(wfCompanyId);
		ParameterUtils.setParameters(threadParameters);
		
		String data=JsonParser.object2Json(AcsApi.getAllBranch(ContextUtils.getCompanyId()));
		this.renderText(callback+"({\"data\":"+data+"})");
		return null;
	}
	
	public static String format(String str) throws Exception {
		//str = str.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "<xml version=\"1.0\" encoding=\"UTF-8\">");
        SAXReader reader = new SAXReader();
        // System.out.println(reader);
        // 注释：创建一个串的字符输入流
        StringReader in = new StringReader(str);
        Document doc = reader.read(in);
        // System.out.println(doc.getRootElement());
        // 注释：创建输出格式
        OutputFormat formater = OutputFormat.createPrettyPrint();
        //formater=OutputFormat.createCompactFormat();
        // 注释：设置xml的输出编码
        formater.setEncoding("utf-8");
        // 注释：创建输出(目标)
        StringWriter out = new StringWriter();
        // 注释：创建输出流
        XMLWriter writer = new XMLWriter(out, formater);
        // 注释：输出格式化的串到目标中，执行后。格式化后的串保存在out中。
        writer.write(doc);
        writer.close();
        // 注释：返回我们格式化后的结果
        return out.toString();
    }
	/**
	 * ajax格式化xml
	 * 
	 */
	@Action("wfeditor-workflow-xmlFormat")
	public String workFlowXmlFormat()  throws Exception{
		String callback=Struts2Utils.getParameter("callback");
		String xmlContentFormat = "";
		if (xmlContent != null) {
			xmlContentFormat = format(xmlContent);
		}
		//getJson页面接收data,如果data包含 "\n" 会报错,所以要把\n替换成特殊字符.
		String data = "{'name':'"+xmlContentFormat.replace("\n", "~~%%##")+"'}";
		this.renderText(callback+"({\"data\":"+data+"})");
		return null;
	}
	/**
	 * 下载文件
	 * @param fileName
	 * @param filePath	 * @throws IOException 
	 */
	public  void download(String fileName,File filePath) throws IOException{
		FileInputStream fileinput =new FileInputStream(filePath);
		BufferedInputStream bis = new BufferedInputStream(fileinput);
		HttpServletResponse response = ServletActionContext.getResponse();
		response.reset();
		response.setContentType("application/x-download");
		OutputStream out=null; byte[] byname;
		try {
			if ( ServletActionContext.getRequest().getHeader( "USER-AGENT" ).
					toLowerCase().indexOf("firefox") > 0 )  {
				byname=fileName.getBytes("GB2312");
			}else if( ServletActionContext.getRequest().getHeader( "USER-AGENT" ).
					toLowerCase().indexOf("safari") > -1 ){
				byname=fileName.getBytes("UTF-8");
			}else{
				byname=fileName.getBytes("GB2312");
			}
			fileName=new String(byname,"ISO-8859-1");
			response.addHeader("Content-Disposition", "attachment;filename="+fileName);
			out=response.getOutputStream();
			byte[] buffer = new byte[4096];
			int size = 0;
			while ((size = bis.read(buffer, 0, buffer.length)) != -1) {
				out.write(buffer, 0, size);
			}
		} catch (IOException e) {e.printStackTrace();
		}finally{
			out.close();bis.close();
		}
	}
	/**
	 * 根据选项组编号获得选项组
	 * @return
	 * @throws Exception
	 */
	@Action("wfeditor-workflow-getOptionGroupByCodeUrl")
	public String getOptionGroupByCodeUrl()  throws Exception{
		String callback=Struts2Utils.getParameter("callback");
		ThreadParameters threadParameters =new ThreadParameters(wfCompanyId);
		ParameterUtils.setParameters(threadParameters);
		List<com.norteksoft.product.api.entity.Option> ops = ApiFactory.getSettingService().getOptionsByGroupCode(code);
		String data = workflowEditorManager.parseOptionGroups(ops);;
		this.renderText(callback+"({\"data\":["+data+"]})");
		return null;
	}
	/**
	 * 创建流程定义,用于兼容老版本流程图
	 * @return
	 * @throws Exception
	 */
	@Action("wfeditor-createWfDefinitionById")
	public String createWfDefinitionById()  throws Exception{
		Long workflowDefinitionId = workflowDefinitionManager.createWfDefinitionById(companyId, xmlContent, processTypeId, systemId);
		this.renderText(workflowDefinitionId.toString());
		return null;
	}
	/**
	 * 创建流程定义
	 * @return
	 * @throws Exception
	 */
	@Action("wfeditor-createWfDefinition")
	public String createWfDefinition()  throws Exception{
		Long workflowDefinitionId = workflowDefinitionManager.createWfDefinition(companyId, xmlContent, processTypeCode, systemCode,saveUrl);
		this.renderText(workflowDefinitionId.toString());
		return null;
	}
	/**
	 * 修改流程定义,用于兼容老版本流程图
	 * @return
	 * @throws Exception
	 */
	@Action("wfeditor-updateWfDefinitionById")
	public String updateWfDefinitionById()  throws Exception{
		Long workflowDefinitionId = workflowEditorManager.updateWfDefinitionById(wfdId, companyId, xmlContent, processTypeId, systemId);
		this.renderText(workflowDefinitionId.toString());
		return null;
	}
	/**
	 * 修改流程定义
	 * @return
	 * @throws Exception
	 */
	@Action("wfeditor-updateWfDefinition")
	public String updateWfDefinition()  throws Exception{
		Long workflowDefinitionId = workflowEditorManager.updateWfDefinition(wfdId, companyId, xmlContent, processTypeCode, systemCode,saveUrl);
		this.renderText(workflowDefinitionId.toString());
		return null;
	}
	/**
	 * 通过流程定义id和公司id获得流程定义xml文件
	 * @return
	 * @throws Exception
	 */
	@Action("wfeditor-getXmlByDefinitionId")
	public String getXmlByDefinitionId()  throws Exception{
		String xml = workflowDefinitionManager.getXmlByDefinitionId(wfdId, companyId);
//		String callback=Struts2Utils.getParameter("callback");
//		String data = "{'xml':'"+xml.replace("\n", "~~%%##")+"'}";
//		this.renderText(callback+"({\"data\":"+data+"})");
		this.renderText(xml);
		return null;
	}
	/**
	 * 正文权限设置/加载正文模版
	 * @return
	 * @throws Exception
	 */
	@Action("wfeditor-workflow-loadTemplate")
	public String loadTemplate()  throws Exception{
		String callback=Struts2Utils.getParameter("callback");
		ThreadParameters threadParameters =new ThreadParameters(wfCompanyId);
		ParameterUtils.setParameters(threadParameters);
		
		List<DocumentTemplate> templates = documentTemplateFileManager.getTemplateByType(Long.valueOf(wfTypeId), Long.valueOf(wfCompanyId) );;
		String data = JsonParser.object2Json(templates);
		
		this.renderText(callback+"({\"data\":"+data+"})");
		return null;
	}
	/**
	 * 获得当前用户信息
	 * @return
	 * @throws Exception
	 */
	@Action("wfeditor-getCurrentUserInformation")
	public String getCurrentUserInformation()  throws Exception{
		String callback=Struts2Utils.getParameter("callback");
		String currentUserBranchCode = "";
		String currentUserBranchName = "";
		if(StringUtils.isEmpty(ContextUtils.getSubCompanyCode())){
			currentUserBranchCode = ContextUtils.getCompanyCode();
			currentUserBranchName = ContextUtils.getCompanyName();
		}else{
			currentUserBranchCode = ContextUtils.getSubCompanyCode();
			if(StringUtils.isEmpty(ContextUtils.getSubCompanyShortTitle())){
				currentUserBranchName = ContextUtils.getSubCompanyName();
			}else{
				currentUserBranchName = ContextUtils.getSubCompanyShortTitle();
			}
		}
		StringBuilder data = new StringBuilder();
		data.append("'companyId':");
		data.append(ContextUtils.getCompanyId());
		data.append(",");
		data.append("'currentUserLoginName':'");
		data.append(ContextUtils.getLoginName());
		data.append("',");
		data.append("'currentUserName':'");
		data.append(ContextUtils.getUserName());
		data.append("',");
		data.append("'currentUserBranchCode':'");
		data.append(currentUserBranchCode);
		data.append("',");
		data.append("'currentUserBranchName':'");
		data.append(currentUserBranchName);
		data.append("'");
		this.renderText(callback+"({\"data\":{"+data.toString()+"}})");
		return null;
	}
	
	/**
	 * 附件-保存
	 */
	@Action("wfeditor-openXmlFile")
	public String wfeditorUploadFileSave() throws Exception {
	    if (file.length()>0) {
	          BufferedReader reader = null;
	          String text="";
	          try {
	              reader = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"));
	              String tempString = null;
	              // 一次读入一行，直到读入null为文件结束
	              while ((tempString = reader.readLine()) != null) {
	            		  text+=tempString;
	              }
	              reader.close();
	          } catch (IOException e) {
	              e.printStackTrace();
	          } finally {
	              if (reader != null) {
	                  try {
	                      reader.close();
	                  } catch (IOException e1) {
	                  }
	              }
	          }
	        this.renderHtml(text);
        } else {
            this.renderText("上传附件内容不能为空");
        }
		return null;
	}
	
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
	
	/**
	 * @author 张洪达-flex
	 */
	@Action("wfeditor-workflow-getFormViewsByCompany")
	public String getFormViewsByCompany() throws Exception {
		List<FormView> list = formViewManager.getFormViewsByCompany(companyId);
		this.renderText("{\"data\":"+JsonParser.object2Json(BeanUtil.turnToModelFormViewList(list))+"}");
		return null;
	}
	/**
	 * 根据表单code和版本查询表单视图 -flex
	 * @param code
	 * @param version
	 */
	@Action("wfeditor-workflow-getFormViewByCodeAndVersion")
	public String getFormViewByCodeAndVersion() throws Exception {
		FormView oldFormView = formViewManager.getFormViewByCodeAndVersion(companyId, code, version);
		this.renderText("{\"data\":"+JsonParser.object2Json(BeanUtil.turnToModelFormView(oldFormView))+"}");
		return null;
	}
	/**
	 * 根据表单code和版本查询控件列表（按控件在html代码中出现先后排序）
	 * (flex有调用)
	 */
	@Action("wfeditor-workflow-getControlsByCodeAndVersion")
	public String getControlsByCodeAndVersion() throws Exception{
		List<FormControl> list = formViewManager.getControlsByCodeAndVersion(companyId,code, version);
		this.renderText("{\"data\":"+JsonParser.object2Json(list)+"}");
		return null;
	}
	/**
	 * 根据选项组编码查询选项
	 * (flex有调用)
	 */
	@Action("wfeditor-workflow-getOpinionConclusion")
	public String getOpinionConclusion() throws Exception{
		List<Option> optionList = formViewManager.getOpinionConclusion(optionGroupCode, companyId);
		this.renderText("{\"data\":"+JsonParser.object2Json(optionList)+"}");
		return null;
	}
	
	/**
	 * 查询companyId公司的所有workflow-flex
	 */
	@Action("wfeditor-workflow-getAllWorkflowType")
	public String getAllWorkflowType() throws Exception{
		List<WorkflowType> types=workflowTypeManager.getAllWorkflowType(companyId);
		this.renderText("{\"data\":"+JsonParser.object2Json(types)+"}");
		return null;
	}
	/**
	 * 查询companyId公司的所有workflow类型-flex
	 */
	@Action("wfeditor-workflow-getWorkflowTypeByCode")
	public String getWorkflowTypeByCode() throws Exception{
		WorkflowType workflowType = workflowTypeManager.getWorkflowTypeByCode(code,companyId);
		this.renderText("{\"data\":"+JsonParser.object2Json(workflowType)+"}");
		return null;
	}
	/**
	 * 查询companyId和类型typeId公司的所有workflow类型-flex
	 */
	@Action("wfeditor-workflow-getWorkflowTypeById")
	public String getWorkflowTypeById() throws Exception{
		WorkflowType workflowType = workflowTypeManager.getWorkflowTypeById(typeId,companyId);
		this.renderText("{\"data\":"+JsonParser.object2Json(workflowType)+"}");
		return null;
	}
	/**
	 * 查询公司所有的部门-flex
	 * @param companyId   公司ID
	 * @return List       [部门名称列表, 是否有子部门(true,false),是否有人员(true,false),是否是分支（true，false），部门编码]
	 */
	@Action("wfeditor-workflow-getAllDepts")
	public String getAllDepts() throws Exception{
		List<AllDeptsModelViewFlex> viewList = AcsApi.getAllDeptsToJson(companyId);
		this.renderText("{\"data\":"+JsonParser.object2Json(viewList)+"}");
		return null;
	}
	/**
	 * 查询公司所有的角色-flex调用
	 * @param companyId   公司ID
	 * @return List       角色名称列表
	 */
	@Action("wfeditor-workflow-getAllRoles")
	public String getAllRoles() throws Exception{
		List<AllRolesModelViewFlex> viewList = AcsApi.getAllRolesToJson(systemCode, companyId);
		this.renderText("{\"data\":"+JsonParser.object2Json(viewList)+"}");
		return null;
	}
	/**
	 * 查询总公司下的分支机构，不包括分支机构的-flex调用
	 * @param companyId
	 * @return
	 */
	@Action("wfeditor-workflow-getWorkgroups")
	public String getWorkgroups() throws Exception{
		List<WorkgroupsModelViewFlex> viewList = AcsApi.getWorkgroupsToJson(companyId);
		this.renderText("{\"data\":"+JsonParser.object2Json(viewList)+"}");
		return null;
	}
	/**
	 * 查询没有在任何部门的用户-flex调用
	 * @param companyId
	 * @return
	 */
	@Action("wfeditor-workflow-getUsersNotInDept")
	public String getUsersNotInDept() throws Exception{
		List<UsersNotInDeptModelViewFlex> viewList = AcsApi.getUsersNotInDeptToJson(companyId);
		this.renderText("{\"data\":"+JsonParser.object2Json(viewList)+"}");
		return null;
	}
	/**
	 * 根据部门名称查询该部门的所有子部门
	 * @param companyId
	 * @return
	 */
	@Action("wfeditor-workflow-getSubDeptsByParentDept")
	public String getSubDeptsByParentDept() throws Exception{
		List<SubDeptsModelViewFlex> viewList = AcsApi.getSubDeptsByParentDeptToJson(companyId,parentDeptCode);
		this.renderText("{\"data\":"+JsonParser.object2Json(viewList)+"}");
		return null;
	}
	/**
	 * 查询所有业务系统信息-flex调用
	 * @param companyId
	 * @return
	 */
	@Action("wfeditor-workflow-getAllBusiness")
	public String getAllBusiness() throws Exception{
		List<AllBusinessModelViewFlex> viewList = AcsApi.getAllBusinessToJsonFlex(companyId);
		this.renderText("{\"data\":"+JsonParser.object2Json(viewList)+"}");
		return null;
	}
	/**
	 * 查询所有分支机构-flex调用
	 * @param companyId
	 * @return
	 */
	@Action("wfeditor-workflow-getAllBranch")
	public String getAllBranch() throws Exception{
		List<AllBranchModelViewFlex> viewList = AcsApi.getAllBranchToJsonFlex(companyId);
		this.renderText("{\"data\":"+JsonParser.object2Json(viewList)+"}");
		return null;
	}
	/**
	 * 当前租户是否存在分支机构-flex调用
	 * @param companyId
	 * @return
	 */
	@Action("wfeditor-workflow-hasBranch")
	public String hasBranch() throws Exception{
		String hasBranch = AcsApi.hasBranch(companyId);
		this.renderText(hasBranch);
		return null;
	}
	/**
	 * 查询部门下所有的人员-flex调用
	 * @param companyId
	 * @return
	 */
	@Action("wfeditor-workflow-getUsersByDept")
	public String getUsersByDept() throws Exception{
		List<UsersNotInDeptModelViewFlex> viewList = AcsApi.getUsersByDeptToJson(companyId,deptCode);
		this.renderText("{\"data\":"+JsonParser.object2Json(viewList)+"}");
		return null;
	}
	/**
	 * 查询没有在任何部门的用户-flex调用
	 * @param companyId
	 * @return
	 */
	@Action("wfeditor-workflow-getBranchUsersNotInDept")
	public String getBranchUsersNotInDept() throws Exception{
		List<UsersNotInDeptModelViewFlex> viewList = AcsApi.getBranchUsersNotInDeptToJson(companyId,deptCode);
		this.renderText("{\"data\":"+JsonParser.object2Json(viewList)+"}");
		return null;
	}
	/**
	 * 查询该分支机构内的工作组-flex调用
	 * @param companyId
	 * @return
	 */
	@Action("wfeditor-workflow-getWorkgroupByBranchCode")
	public String getWorkgroupByBranchCode() throws Exception{
		List<WorkgroupsModelViewFlex> viewList = AcsApi.getWorkgroupByBranchCodeToJson(companyId,branchCode);
		this.renderText("{\"data\":"+JsonParser.object2Json(viewList)+"}");
		return null;
	}
	
	
	public WorkflowDefinition getModel() {
		return workflowDefinition;
	}
	
	
/************************将通信方式改为HttpService方式*******************************************/
	
	/**
	 * 超期人监控/移交任务数事件
	 * @return
	 */
	@Action("wfeditor-workflow-getWfDefinition")
	public String getWfDefinition() throws Exception{
		WorkflowDefinition workflowDefinition =  workflowDefinitionManager.getWfDefinition(wfdId);
		this.renderText("{\"data\":"+JsonParser.object2Json(workflowDefinition)+"}");
		return null;
	}
	/**
	 * 查询所有活动的流程(返回List)
	 * @return
	 */
	@Action("wfeditor-workflow-getActiveDefinition")
	public String getActiveDefinition() throws Exception{
		List<WorkflowDefinition> list =  workflowDefinitionManager.getActiveDefinition(companyId, systemCode);
		this.renderText("{\"data\":"+JsonParser.object2Json(list)+"}");
		return null;
	}
	/**
	 * 查询正文模板列表
	 * @return
	 */
	@Action("wfeditor-workflow-getTemplateByType")
	public String getTemplateByType() throws Exception{
		List<DocumentTemplate> templates = documentTemplateFileManager.getTemplateByType(Long.parseLong(wfTypeId), companyId);
		this.renderText("{\"data\":"+JsonParser.object2Json(templates)+"}");
		return null;
	}
	/**
	 * 查询模版的xml
	 * @return
	 */
	@Action("wfeditor-workflow-getTemplateXml")
	public String getTemplateXml() throws Exception{
		String xml = workflowDefinitionManager.getTemplateXml(templateId);
		this.renderText(xml);
		return null;
	}
	/**
	 * 根据流程实例ID查询历史记录
	 * @return
	 */
	@Action("wfeditor-workflow-getHistoryByInstanceId")
	public String getHistoryByInstanceId() throws Exception{
		List<WfHistoryModelFlex> list = instanceHistoryManager.getHistoryByInstanceIdFlex(companyId, instanceId);
		this.renderText("{\"data\":"+JsonParser.object2Json(list)+"}");
		return null;
	}
	/**
	 * 查询当前环节
	 * @return
	 */
	@Action("wfeditor-workflow-getCurrentTasks")
	public String getCurrentTasks() throws Exception{
		List<CurrentTasksModelFlex> list = instanceHistoryManager.getCurrentTasksFlex(companyId, instanceId);
		this.renderText("{\"data\":"+JsonParser.object2Json(list)+"}");
		return null;
	}
	/**
	 * 根据流程实例ID查询流程定义文件
	 * @return
	 */
	@Action("wfeditor-workflow-getXmlByInstanceId")
	public String getXmlByInstanceId() throws Exception{
		String document = workflowInstanceManager.getXmlByInstanceId(instanceId, companyId);
		this.renderText(document);
		return null;
	}
	/**
	 * 根据父流程的workflowId和环节名获得它的子流程实例
	 * @return
	 */
	@Action("wfeditor-workflow-getSubProcessInstanceByTaskName")
	public String getSubProcessInstanceByTaskName() throws Exception{
		List<WorkflowInstance> list = workflowInstanceManager.getSubProcessInstanceByTaskName(parentWorkflowId, tacheName);
		this.renderText("{\"data\":"+JsonParser.object2Json(list)+"}");
		return null;
	}
	/**
	 * 通过流程定义的Key查询WorkflowDefinition
	 * @return
	 */
	@Action("wfeditor-workflow-getWorkflowDefinitionByProcessId")
	public String getWorkflowDefinitionByProcessId() throws Exception{
		WorkflowDefinition workflowDefinition = workflowDefinitionManager.getWorkflowDefinitionByProcessId(processId);
		this.renderText("{\"data\":"+JsonParser.object2Json(workflowDefinition)+"}");
		return null;
	}
	/**
	 * 修改流程定义,且及时生效
	 * @return
	 */
	@Action("wfeditor-workflow-updateWfDefVersion")
	public String updateWfDefVersion() throws Exception{
		Document document = getDocument();
		String wfdIdStr = getParamXmlValue(document, "param/wfdId");
		Long wfdId = wfdIdStr == null ? null : Long.parseLong(wfdIdStr);
		String companyIdStr = getParamXmlValue(document, "param/companyId");
		Long companyId = companyIdStr == null ? null : Long.parseLong(companyIdStr);
		String xmlContent = getParamXmlValue(document, "param/xmlContent");
		String typeIdStr = getParamXmlValue(document, "param/typeId");
		Long typeId = typeIdStr == null ? null : Long.parseLong(typeIdStr);
		String systemIdStr = getParamXmlValue(document, "param/systemId");
		Long systemId = systemIdStr == null ? null : Long.parseLong(systemIdStr);
		Long id = workflowDefinitionManager.updateWfDefVersion(wfdId, companyId, xmlContent, typeId, systemId);
		this.renderText(id+"");
		return null;
	}
	/**
	 * 创建流程定义
	 * @return
	 * @throws Exception
	 */
	@Action("wfeditor-workflow-createWfDefinition")
	public String createWfDefinitionFlax()  throws Exception{
		Document document = getDocument();
		String companyIdStr = getParamXmlValue(document, "param/companyId");
		Long companyId = companyIdStr == null ? null : Long.parseLong(companyIdStr);
		String xmlContent = getParamXmlValue(document, "param/xmlContent");
		String processTypeCode = getParamXmlValue(document, "param/processTypeCode");
		String systemCode = getParamXmlValue(document, "param/systemCode");
		String saveUrl = getParamXmlValue(document, "param/saveUrl");
		Long workflowDefinitionId = workflowDefinitionManager.createWfDefinition(companyId, xmlContent, processTypeCode, systemCode, saveUrl);
		this.renderText(workflowDefinitionId.toString());
		return null;
	}
	//共用的方法
	private Document getDocument() throws IOException, UnsupportedEncodingException {
		InputStream rin = Struts2Utils.getRequest().getInputStream();
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		byte[] buffer = new byte[128];
		int len = 0;
		while ((len = rin.read(buffer)) >= 0) {
			bytes.write(buffer, 0, len);
		}
		String paramXml = bytes.toString("UTF-8");
		Document document = Dom4jUtils.getDocument("<param>" + paramXml + "</param>");
		return document;
	}
	//共用的方法
	@SuppressWarnings("unchecked")
	private String getParamXmlValue(Document document, String nodePath){
		List<Element> tableList = document.selectNodes(nodePath);
		Iterator<Element> it = tableList.iterator();
		while(it.hasNext()){//只会循环一次
			Element menuEle = it.next();
			return menuEle.getText();
		}
		return null;
	}
	/**
	 * 创建流程定义,用于兼容老版本流程图
	 * @return
	 * @throws Exception
	 */
	@Action("wfeditor-workflow-createWfDefinitionById")
	public String createWfDefinitionByIdFlax() throws Exception{
		Document document = getDocument();
		String companyIdStr = getParamXmlValue(document, "param/companyId");
		Long companyId = companyIdStr == null ? null : Long.parseLong(companyIdStr);
		String xmlContent = getParamXmlValue(document, "param/xmlContent");
		String processTypeIdStr = getParamXmlValue(document, "param/processTypeId");
		Long processTypeId = processTypeIdStr == null ? null : Long.parseLong(processTypeIdStr);
		String systemIdStr = getParamXmlValue(document, "param/systemId");
		Long systemId = systemIdStr == null ? null : Long.parseLong(systemIdStr);
		Long workflowDefinitionId = workflowDefinitionManager.createWfDefinitionById(companyId, xmlContent, processTypeId, systemId);
		this.renderText(workflowDefinitionId.toString());
		return null;
	}
	/**
	 * 修改流程定义
	 * @return
	 * @throws Exception
	 */
	@Action("wfeditor-workflow-updateWfDefinition")
	public String updateWfDefinitionFlax() throws Exception{
		Document document = getDocument();
		String wfdIdStr = getParamXmlValue(document, "param/wfdId");
		Long wfdId = wfdIdStr == null ? null : Long.parseLong(wfdIdStr);
		String companyIdStr = getParamXmlValue(document, "param/companyId");
		Long companyId = companyIdStr == null ? null : Long.parseLong(companyIdStr);
		String xmlContent = getParamXmlValue(document, "param/xmlContent");
		String processTypeCode = getParamXmlValue(document, "param/processTypeCode");
		String systemCode = getParamXmlValue(document, "param/systemCode");
		String saveUrl = getParamXmlValue(document, "param/saveUrl");
		Long workflowDefinitionId = workflowEditorManager.updateWfDefinition(wfdId, companyId, xmlContent, processTypeCode, systemCode,saveUrl);
		this.renderText(workflowDefinitionId.toString());
		return null;
	}
	/**
	 * 修改流程定义,用于兼容老版本流程图
	 * @return
	 * @throws Exception
	 */
	@Action("wfeditor-workflow-updateWfDefinitionById")
	public String updateWfDefinitionByIdFlax() throws Exception{
		Document document = getDocument();
		String wfdIdStr = getParamXmlValue(document, "param/wfdId");
		Long wfdId = wfdIdStr == null ? null : Long.parseLong(wfdIdStr);
		String companyIdStr = getParamXmlValue(document, "param/companyId");
		Long companyId = companyIdStr == null ? null : Long.parseLong(companyIdStr);
		String xmlContent = getParamXmlValue(document, "param/xmlContent");
		String processTypeIdStr = getParamXmlValue(document, "param/processTypeId");
		Long processTypeId = processTypeIdStr == null ? null : Long.parseLong(processTypeIdStr);
		String systemIdStr = getParamXmlValue(document, "param/systemId");
		Long systemId = systemIdStr == null ? null : Long.parseLong(systemIdStr);
		Long workflowDefinitionId = workflowEditorManager.updateWfDefinitionById(wfdId, companyId, xmlContent, processTypeId, systemId);
		this.renderText(workflowDefinitionId.toString());
		return null;
	}
	
/***************************************************************************************/
	public Long getWfdId() {
		return wfdId;
	}
	public void setWfdId(Long wfdId) {
		this.wfdId = wfdId;
	}
	public WorkflowDefinition getWorkflowDefinition() {
		return workflowDefinition;
	}
	public void setWorkflowDefinition(WorkflowDefinition workflowDefinition) {
		this.workflowDefinition = workflowDefinition;
	}
	public String getCurrentNodeId() {
		return currentNodeId;
	}
	public void setCurrentNodeId(String currentNodeId) {
		this.currentNodeId = currentNodeId;
	}
	public String getSystemCode() {
		return systemCode;
	}
	public void setSystemCode(String systemCode) {
		this.systemCode = systemCode;
	}
	public String getTypeCode() {
		return typeCode;
	}
	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}
	public Long getWfCompanyId() {
		return wfCompanyId;
	}
	public void setWfCompanyId(Long wfCompanyId) {
		this.wfCompanyId = wfCompanyId;
	}
	public Long getCompanyId() {
		return companyId;
	}
	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
	public String getXmlCode() {
		return xmlCode;
	}
	public void setXmlCode(String xmlCode) {
		this.xmlCode = xmlCode;
	}
	public String getXmlContent() {
		return xmlContent;
	}
	public void setXmlContent(String xmlContent) {
		this.xmlContent = xmlContent;
	}
	public String getFormCode() {
		return formCode;
	}
	public void setFormCode(String formCode) {
		this.formCode = formCode;
	}
	public String getFormVersion() {
		return formVersion;
	}
	public void setFormVersion(String formVersion) {
		this.formVersion = formVersion;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getProcessTypeCode() {
		return processTypeCode;
	}
	public void setProcessTypeCode(String processTypeCode) {
		this.processTypeCode = processTypeCode;
	}
	public Long getSystemId() {
		return systemId;
	}
	public void setSystemId(Long systemId) {
		this.systemId = systemId;
	}
	public Long getProcessTypeId() {
		return processTypeId;
	}
	public void setProcessTypeId(Long processTypeId) {
		this.processTypeId = processTypeId;
	}
	public String getWfTypeId() {
		return wfTypeId;
	}
	public void setWfTypeId(String wfTypeId) {
		this.wfTypeId = wfTypeId;
	}
	public String getLinkflag() {
		return linkflag;
	}
	public void setLinkflag(String linkflag) {
		this.linkflag = linkflag;
	}
	public String getOption() {
		return option;
	}
	public void setOption(String option) {
		this.option = option;
	}
	public FormViewManager getFormViewManager() {
		return formViewManager;
	}
	public void setFormViewManager(FormViewManager formViewManager) {
		this.formViewManager = formViewManager;
	}
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
	public void setFileFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
	public Long getTypeId() {
		return typeId;
	}
	public void setTypeId(Long typeId) {
		this.typeId = typeId;
	}
	public String getOptionGroupCode() {
		return optionGroupCode;
	}
	public void setOptionGroupCode(String optionGroupCode) {
		this.optionGroupCode = optionGroupCode;
	}
	public String getParentDeptCode() {
		return parentDeptCode;
	}
	public void setParentDeptCode(String parentDeptCode) {
		this.parentDeptCode = parentDeptCode;
	}
	public String getDeptCode() {
		return deptCode;
	}
	public void setDeptCode(String deptCode) {
		this.deptCode = deptCode;
	}
	public String getBranchCode() {
		return branchCode;
	}
	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}
	public String getSaveUrl() {
		return saveUrl;
	}
	public void setSaveUrl(String saveUrl) {
		this.saveUrl = saveUrl;
	}
	public Long getTemplateId() {
		return templateId;
	}
	public void setTemplateId(Long templateId) {
		this.templateId = templateId;
	}
	public String getInstanceId() {
		return instanceId;
	}
	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}
	public String getParentWorkflowId() {
		return parentWorkflowId;
	}
	public void setParentWorkflowId(String parentWorkflowId) {
		this.parentWorkflowId = parentWorkflowId;
	}
	public String getTacheName() {
		return tacheName;
	}
	public void setTacheName(String tacheName) {
		this.tacheName = tacheName;
	}
	public String getProcessId() {
		return processId;
	}
	public void setProcessId(String processId) {
		this.processId = processId;
	}
	public String getXml() {
		return xml;
	}
	public void setXml(String xml) {
		this.xml = xml;
	}
	public String getProcessInstanceId() {
		return processInstanceId;
	}
	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}
	
}
