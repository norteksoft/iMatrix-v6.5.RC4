package com.norteksoft.wf.engine.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.acs.api.AcsApi;
import com.norteksoft.mms.form.entity.FormControl;
import com.norteksoft.mms.form.entity.FormView;
import com.norteksoft.mms.form.service.FormViewManager;
import com.norteksoft.mms.module.entity.Menu;
import com.norteksoft.mms.module.service.MenuManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.entity.Department;
import com.norteksoft.product.api.entity.Option;
import com.norteksoft.product.api.entity.WorkflowInstance;
import com.norteksoft.product.api.entity.Workgroup;
import com.norteksoft.product.enumeration.DataState;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.JsonParser;
import com.norteksoft.product.util.ZTreeUtils;
import com.norteksoft.product.util.tree.ZTreeNode;
import com.norteksoft.wf.engine.dao.WorkflowDefinitionDao;
import com.norteksoft.wf.engine.entity.WorkflowDefinition;
/**
 * 工作流编辑器
 * @author nortek
 *
 */
@Service
@Transactional
public class WorkflowEditorManager {
	//拼节点id时为了防止节点id重复,前面加的前缀
	private static String BRANCH_USER_="branchUser_";
	private static String BRANCH_USER="branchUser";
	private static String ALL_USER="allUsers_";
	private static String DEPARTMENT_USER_="departmentUser_";
	private static String DEPARTMENT_USER="departmentUser";
	private static String BRANCH_NOT_DEPARTMENT_="branchNotDepartment_";
	private static String USER_HAS_NOT_DEPARTMENT_="userHasNotDepartment_";
	private static String USER_="user_";
	private static String ALL_ROLE_="allRole_";//角色
	private static String ROLE_="role_";
	private static String WORKGROUP_="workgroup_";
	private static String HASWORKGROUPBRANCH_="hasWorkgroupBranch_";
	private static String DEPARTMENT_="department_";
	private static String BRANCH_="branch_";
	private static String STANDARD_="standard_";
	private static String FORMFIELD_="formfield_";
	private static String ALL_STANDARD_VALUE_="allStandardValue_";
	private static String ALL_FORMFIELD_="allFormField_";
	//节点类型
	private static String BRANCH="branch";//分支机构
	private static String DEPARTMENT="department";//部门
	private static String BRANCH_NOT_DEPARTMENT="branchNotDepartment";//分支机构下无部门节点
	private static String USER_HAS_NOT_DEPARTMENT="userHasNotDepartment";//公司下无部门节点
	private static String USER="user";//员工
	private static String ROLE="role";//角色
	private static String WORKGROUP="workgroup";//工作组
	private static String HASWORKGROUPBRANCH="hasWorkgroupBranch";
	private static String STANDARD="standard";//标准值
	private static String FORMFIELD="formfield";//表单字段
	
	@Autowired
	private FormViewManager formViewManager;
	@Autowired
	private MenuManager menuManager;
	@Autowired
	private WorkflowDefinitionManager workflowDefinitionManager;
	@Autowired
	private WorkflowDefinitionDao workflowDefinitionDao;
	@Autowired
	private InstanceHistoryManager instanceHistoryManager;
	@Autowired
	private WorkflowInstanceManager workflowInstanceManager;
	
	/**
	 * 创建表单树(流程属性/基本属性/对应表单)
	 * @param companyId
	 * @param systemCode
	 * @param currentNodeId
	 * @return
	 */
	public String createFormTree() {
		List<ZTreeNode> treeNodes = new ArrayList<ZTreeNode>();
		List<Menu> menus=menuManager.getEnabledRootMenuByCompany();
		int i=0;
		for(Menu menu:menus){
			List<FormView> formviews= formViewManager.getFormViewsByMenu(menu.getId());
			ZTreeNode zTreeNode = new ZTreeNode("MENU_"+menu.getId(),"0", menu.getName(),"false","true","MENU","");
			if(i==0)zTreeNode.setOpen("true");
			if(formviews!=null && formviews.size()>0)zTreeNode.setIsParent("true");
			treeNodes.add(zTreeNode);
			initialFormTree(treeNodes,formviews,"MENU_"+menu.getId());
			i++;
		}
		return JsonParser.object2Json(treeNodes);
	}
	/**
	 * 创建工作组树(环节属性/条件添加/条件设置[当前办理人工作组，表单字段和标准值])
	 * @param companyId
	 * @param systemCode
	 * @param currentNodeId
	 * @return
	 */
	public String createcurrentTransactorWorkGroupStandardvalueTree(String systemCode,String currentNodeId,List<FormControl> standardvalues) {
		List<ZTreeNode> treeNodes = new ArrayList<ZTreeNode>();
		if (currentNodeId.equals("0")) {
			packagingWorkgroupsTree(treeNodes);
			initialFormFiledTree(systemCode,treeNodes,standardvalues);//表单字段
			List<String[]> values=setcurrentTransactorWorkGroupStandardValue();//标准值
			initialStandardvalueTree(systemCode,treeNodes,values);
		}
		return JsonParser.object2Json(treeNodes);
	}
	/**
	 * 创建人员树(流程属性/催办设置/通知人员[文档创建人工作组和标准值])
	 * @param companyId
	 * @param systemCode
	 * @param currentNodeId
	 * @return
	 */
	public String createdocumentCreatorWorkGroupStandardvalueTree(String systemCode,String currentNodeId,List<FormControl> standardvalues) {
		List<ZTreeNode> treeNodes = new ArrayList<ZTreeNode>();
		if (currentNodeId.equals("0")) {
			packagingWorkgroupsTree(treeNodes);
			initialFormFiledTree(systemCode,treeNodes,standardvalues);//表单字段
		}
		return JsonParser.object2Json(treeNodes);
	}
	/**
	 * 创建人员树(环节属性/权限设置/添加条件[创建顶级部门和表单字段])
	 * @param companyId
	 * @param systemCode
	 * @param currentNodeId
	 * @return
	 */
	public String createUpstageDeptFormTree(String systemCode,String currentNodeId,List<FormControl> standardvalues) {
		String[] str = currentNodeId.split("_");
		List<ZTreeNode> treeNodes = new ArrayList<ZTreeNode>();
		if (currentNodeId.equals("0")) {
			initialDepartmentTree(currentNodeId,treeNodes);
			initialFormFiledTree(systemCode,treeNodes,standardvalues);//表单字段
		}else if(str[0].equals("department")||str[0].equals("branch")) {
			packagingSubDepartmentNodes(currentNodeId,treeNodes);
		}
		return JsonParser.object2Json(treeNodes);
	}
	/**
	 * 创建人员树(环节属性/权限设置/添加条件[部门和表单字段])
	 * @param companyId
	 * @param systemCode
	 * @param currentNodeId
	 * @return
	 */
	public String createDeptFormTree(String systemCode,String currentNodeId,List<FormControl> standardvalues) {
		String[] str = currentNodeId.split("_");
		List<ZTreeNode> treeNodes = new ArrayList<ZTreeNode>();
		if (currentNodeId.equals("0")) {
			initialDepartmentTree(currentNodeId,treeNodes);
			initialFormFiledTree(systemCode,treeNodes,standardvalues);//表单字段
			List<String[]> values=setdocumentCreatorDepartmentStandardValue();//标准值
			initialStandardvalueTree(systemCode,treeNodes,values);
		}else if(str[0].equals("department")||str[0].equals("branch")) {
			packagingSubDepartmentNodes(currentNodeId,treeNodes);
		}
		return JsonParser.object2Json(treeNodes);
	}
	/**
	 * 创建人员树(环节属性/权限设置/添加条件[当前办理人部门和表单字段])
	 * @param companyId
	 * @param systemCode
	 * @param currentNodeId
	 * @return
	 */
	public String createcurrentTransactorDeptFormTree(String systemCode,String currentNodeId,List<FormControl> standardvalues) {
		String[] str = currentNodeId.split("_");
		List<ZTreeNode> treeNodes = new ArrayList<ZTreeNode>();
		if (currentNodeId.equals("0")) {
			initialDepartmentTree(currentNodeId,treeNodes);
			initialFormFiledTree(systemCode,treeNodes,standardvalues);//表单字段
			List<String[]> values=setcurrentTransactorDepartmentStandardValue();//标准值
			initialStandardvalueTree(systemCode,treeNodes,values);
		}else if(str[0].equals("department")||str[0].equals("branch")) {
			packagingSubDepartmentNodes(currentNodeId,treeNodes);
		}
		return JsonParser.object2Json(treeNodes);
	}
	/**
	 * 创建人员树(环节属性/权限设置/添加条件[当前办理人顶级部门和表单字段])
	 * @param companyId
	 * @param systemCode
	 * @param currentNodeId
	 * @return
	 */
	public String createcurrentTransactorUpstageDepartmentFormTree(String systemCode,String currentNodeId,List<FormControl> standardvalues) {
		String[] str = currentNodeId.split("_");
		List<ZTreeNode> treeNodes = new ArrayList<ZTreeNode>();
		if (currentNodeId.equals("0")) {
			initialDepartmentTree(currentNodeId,treeNodes);
			initialFormFiledTree(systemCode,treeNodes,standardvalues);//表单字段
			List<String[]> values=setcurrentTransactorUpstageDepartmentStandardValue();//标准值
			initialStandardvalueTree(systemCode,treeNodes,values);
		}else if(str[0].equals("department")||str[0].equals("branch")) {
			packagingSubDepartmentNodes(currentNodeId,treeNodes);
		}
		return JsonParser.object2Json(treeNodes);
	}
	/**
	 * 创建人员树(环节属性/权限设置/添加条件[角色和表单字段])
	 * @param companyId
	 * @param systemCode
	 * @param currentNodeId
	 * @return
	 */
	public String createRoleFormTree(String systemCode,String currentNodeId,List<FormControl> standardvalues) {
		List<ZTreeNode> treeNodes = new ArrayList<ZTreeNode>();
		if (currentNodeId.equals("0")) {
			packagingRoleBySystemCode(systemCode,treeNodes);
			initialFormFiledTree(systemCode,treeNodes,standardvalues);
		}
		return JsonParser.object2Json(treeNodes);
	}
	/**
	 * 创建人员树(环节属性/权限设置/添加条件[人员和表单字段])
	 * @param companyId
	 * @param systemCode
	 * @param currentNodeId
	 * @return
	 */
	public String createcurrentTransactorUserFormTree(String systemCode,String currentNodeId,List<FormControl> standardvalues) {
		String[] str = currentNodeId.split("_");
		List<ZTreeNode> treeNodes = new ArrayList<ZTreeNode>();
		if (currentNodeId.equals("0")) {
			initialUserTree(currentNodeId,treeNodes);
			initialFormFiledTree(systemCode,treeNodes,standardvalues);//表单字段
			List<String[]> values=setcurrentTransactorNameStandardValue();//标准值
			initialStandardvalueTree(systemCode,treeNodes,values);
		}else if(str[0].equals("departmentUser")||str[0].equals("branchUser")) {
			packagingNodeInDepartment(treeNodes,currentNodeId);
		}else if(str[0].equals("userHasNotDepartment") || str[0].equals(BRANCH_NOT_DEPARTMENT)){
			packagingUserNode(treeNodes,currentNodeId);
		}
		return JsonParser.object2Json(treeNodes);
	}
	/**
	 * 创建人员树(环节属性/权限设置/添加条件[人员和表单字段])
	 * @param companyId
	 * @param systemCode
	 * @param currentNodeId
	 * @return
	 */
	public String createUserFormTree(String systemCode,String currentNodeId,List<FormControl> standardvalues) {
		String[] str = currentNodeId.split("_");
		List<ZTreeNode> treeNodes = new ArrayList<ZTreeNode>();
		if (currentNodeId.equals("0")) {
			initialUserTree(currentNodeId,treeNodes);
			initialFormFiledTree(systemCode,treeNodes,standardvalues);
		}else if(str[0].equals("departmentUser")||str[0].equals("branchUser")) {
			packagingNodeInDepartment(treeNodes,currentNodeId);
		}else if(str[0].equals("userHasNotDepartment") || str[0].equals(BRANCH_NOT_DEPARTMENT)){
			packagingUserNode(treeNodes,currentNodeId);
		}
		return JsonParser.object2Json(treeNodes);
	}
	
	
	/**
	 * 获得所有标准值字段(环节属性-自动填写字段-选择)
	 * @param workflowDefinition
	 * @return
	 */
	public String allStandardFieldJosn(){
		String standardfiled=pagingAutomaticWriteStandardFiled();
		return standardfiled;
	}
	  /**    
	    * 封装标准字段(环节属性-自动填写字段-选择)
	    * @return
	    */
	   public String pagingAutomaticWriteStandardFiled(){
		   StringBuilder sb = new StringBuilder();
				sb.append("{");
				sb.append("'name':'currentTime',");//字段
				sb.append("'value':'本环节办理时间',");//字段名
				sb.append("'type':'DATE',");//数据类型
				sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
				sb.append("'groupName':'标准字段'");
				sb.append("},");
				sb.append("{");
				sb.append("'name':'currentTransactorId',");//字段
				sb.append("'value':'本环节办理人id',");//字段名
				sb.append("'type':'LONG',");//数据类型
				sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
				sb.append("'groupName':'标准字段'");
				sb.append("},");
				sb.append("{");
				sb.append("'name':'currentTransactorMainDepartmentId',");//字段
				sb.append("'value':'当前办理人正职部门id',");//字段名
				sb.append("'type':'LONG',");//数据类型
				sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
				sb.append("'groupName':'标准字段'");
				sb.append("},");
				sb.append("{");
				sb.append("'name':'currentTransactorDirectSuperiorId',");//字段
				sb.append("'value':'当前办理人直属上级id',");//字段名
				sb.append("'type':'LONG',");//数据类型
				sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
				sb.append("'groupName':'标准字段'");
				sb.append("},");
				sb.append("{");
				sb.append("'name':'currentTransactorDirectSuperiorMainDepartmentId',");//字段
				sb.append("'value':'当前办理人直属上级正职部门id',");//字段名
				sb.append("'type':'LONG',");//数据类型
				sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
				sb.append("'groupName':'标准字段'");
				sb.append("},");
				sb.append("{");
				sb.append("'name':'currentTransactor',");//字段
				sb.append("'value':'本环节办理人登录名',");//字段名
				sb.append("'type':'TEXT',");//数据类型
				sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
				sb.append("'groupName':'标准字段'");
				sb.append("},");
				sb.append("{");
				sb.append("'name':'currentTransactorName',");//字段
				sb.append("'value':'当前办理人姓名',");//字段名
				sb.append("'type':'TEXT',");//数据类型
				sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
				sb.append("'groupName':'标准字段'");
				sb.append("},");
				sb.append("{");
				sb.append("'name':'currentTransactorDepartment',");//字段
				sb.append("'value':'当前办理人部门',");//字段名
				sb.append("'type':'TEXT',");//数据类型
				sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
				sb.append("'groupName':'标准字段'");
				sb.append("},");
				sb.append("{");
				sb.append("'name':'currentTransactorDepartmentId',");//字段
				sb.append("'value':'当前办理人部门id',");//字段名
				sb.append("'type':'TEXT',");//数据类型
				sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
				sb.append("'groupName':'标准字段'");
				sb.append("},");
				sb.append("{");
				sb.append("'name':'currentTransactorMainDepartment',");//字段
				sb.append("'value':'当前办理人正职部门',");//字段名
				sb.append("'type':'TEXT',");//数据类型
				sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
				sb.append("'groupName':'标准字段'");
				sb.append("},");
				sb.append("{");
				sb.append("'name':'currentTransactorSuperiorDepartmentId',");//字段
				sb.append("'value':'当前办理人上级部门id',");//字段名
				sb.append("'type':'LONG',");//数据类型
				sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
				sb.append("'groupName':'标准字段'");
				sb.append("},");
				sb.append("{");
				sb.append("'name':'currentTransactorSuperiorDepartment',");//字段
				sb.append("'value':'当前办理人上级部门',");//字段名
				sb.append("'type':'TEXT',");//数据类型
				sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
				sb.append("'groupName':'标准字段'");
				sb.append("},");
				sb.append("{");
				sb.append("'name':'currentTransactorUpstageDepartment',");//字段
				sb.append("'value':'当前办理人顶级部门',");//字段名
				sb.append("'type':'TEXT',");//数据类型
				sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
				sb.append("'groupName':'标准字段'");
				sb.append("},");
				sb.append("{");
				sb.append("'name':'currentTransactorUpstageDepartmentId',");//字段
				sb.append("'value':'当前办理人顶级部门id',");//字段名
				sb.append("'type':'TEXT',");//数据类型
				sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
				sb.append("'groupName':'标准字段'");
				sb.append("},");
				sb.append("{");
				sb.append("'name':'currentTransactorRole',");//字段
				sb.append("'value':'当前办理人角色',");//字段名
				sb.append("'type':'TEXT',");//数据类型
				sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
				sb.append("'groupName':'标准字段'");
				sb.append("},");
				sb.append("{");
				sb.append("'name':'currentTransactorRoleId',");//字段
				sb.append("'value':'当前办理人角色id',");//字段名
				sb.append("'type':'TEXT',");//数据类型
				sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
				sb.append("'groupName':'标准字段'");
				sb.append("},");
				sb.append("{");
				sb.append("'name':'currentTransactorWorkGroup',");//字段
				sb.append("'value':'当前办理人工作组',");//字段名
				sb.append("'type':'TEXT',");//数据类型
				sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
				sb.append("'groupName':'标准字段'");
				sb.append("},");
				sb.append("{");
				sb.append("'name':'currentTransactorWorkGroupId',");//字段
				sb.append("'value':'当前办理人工作组id',");//字段名
				sb.append("'type':'TEXT',");//数据类型
				sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
				sb.append("'groupName':'标准字段'");
				sb.append("},");
				sb.append("{");
				sb.append("'name':'currentTransactorDirectSuperior',");//字段
				sb.append("'value':'当前办理人直属上级登录名',");//字段名
				sb.append("'type':'TEXT',");//数据类型
				sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
				sb.append("'groupName':'标准字段'");
				sb.append("},");
//				sb.append("{");
//				sb.append("'name':'currentTransactorDirectSuperiorId',");//字段
//				sb.append("'value':'当前办理人直属上级id',");//字段名
//				sb.append("'type':'TEXT',");//数据类型
//				sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
//				sb.append("'groupName':'标准字段'");
//				sb.append("},");
				sb.append("{");
				sb.append("'name':'currentTransactorDirectSuperiorName',");//字段
				sb.append("'value':'当前办理人直属上级姓名',");//字段名
				sb.append("'type':'TEXT',");//数据类型
				sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
				sb.append("'groupName':'标准字段'");
				sb.append("},");
				sb.append("{");
				sb.append("'name':'currentTransactorDirectSuperiorDepartment',");//字段
				sb.append("'value':'当前办理人直属上级部门',");//字段名
				sb.append("'type':'TEXT',");//数据类型
				sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
				sb.append("'groupName':'标准字段'");
				sb.append("},");
				sb.append("{");
				sb.append("'name':'currentTransactorDirectSuperiorDepartmentId',");//字段
				sb.append("'value':'当前办理人直属上级部门id',");//字段名
				sb.append("'type':'TEXT',");//数据类型
				sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
				sb.append("'groupName':'标准字段'");
				sb.append("},");
				sb.append("{");
				sb.append("'name':'currentTransactorDirectSuperiorMainDepartment',");//字段
				sb.append("'value':'当前办理人直属上级正职部门',");//字段名
				sb.append("'type':'TEXT',");//数据类型
				sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
				sb.append("'groupName':'标准字段'");
				sb.append("},");
//				sb.append("{");
//				sb.append("'name':'currentTransactorDirectSuperiorMainDepartmentId',");//字段
//				sb.append("'value':'当前办理人直属上级正职部门id',");//字段名
//				sb.append("'type':'TEXT',");//数据类型
//				sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
//				sb.append("'groupName':'标准字段'");
//				sb.append("},");
				sb.append("{");
				sb.append("'name':'currentTransactorDirectSuperiorRole',");//字段
				sb.append("'value':'当前办理人直属上级角色',");//字段名
				sb.append("'type':'TEXT',");//数据类型
				sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
				sb.append("'groupName':'标准字段'");
				sb.append("},");
				sb.append("{");
				sb.append("'name':'currentTransactorDirectSuperiorRoleId',");//字段
				sb.append("'value':'当前办理人直属上级角色id',");//字段名
				sb.append("'type':'TEXT',");//数据类型
				sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
				sb.append("'groupName':'标准字段'");
				sb.append("},");
				sb.append("{");
				sb.append("'name':'currentTransactorDirectSuperiorWorkGroup',");//字段
				sb.append("'value':'当前办理人直属上级工作组',");//字段名
				sb.append("'type':'TEXT',");//数据类型
				sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
				sb.append("'groupName':'标准字段'");
				sb.append("},");
				sb.append("{");
				sb.append("'name':'currentTransactorDirectSuperiorWorkGroupId',");//字段
				sb.append("'value':'当前办理人直属上级工作组id',");//字段名
				sb.append("'type':'TEXT',");//数据类型
				sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
				sb.append("'groupName':'标准字段'");
				sb.append("},");
				sb.append("{");
				sb.append("'name':'currentOperation',");//字段
				sb.append("'value':'本环节执行的操作',");//字段名
				sb.append("'type':'TEXT',");//数据类型
				sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
				sb.append("'groupName':'标准字段'");
				sb.append("},");
		       
				sb.append("{");
				sb.append("'name':'previousTransactorId',");//字段
				sb.append("'value':'上一环节办理人id',");//字段名
				sb.append("'type':'INTEGER',");//数据类型
				sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
				sb.append("'groupName':'标准字段'");
				sb.append("},");
				sb.append("{");
				sb.append("'name':'previousTransactor',");//字段
				sb.append("'value':'上一环节办理人登录名',");//字段名
				sb.append("'type':'TEXT',");//数据类型
				sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
				sb.append("'groupName':'标准字段'");
				sb.append("},");
				sb.append("{");
				sb.append("'name':'previousTransactorName',");//字段
				sb.append("'value':'上一环节办理人姓名',");//字段名
				sb.append("'type':'TEXT',");//数据类型
				sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
				sb.append("'groupName':'标准字段'");
				sb.append("}");
				
		   return sb.toString();
	   }
	/**
    * 获得所有表单字段和标准值字段
    * @param workflowDefinition
    * @return
    */
   public String allFormAndStandardJosn(List<FormControl> lists){
	    if(lists==null){
		   lists= new ArrayList<FormControl>();
	    }
		StringBuilder sb = new StringBuilder();
		String formjson=allFormJosn(lists);
		sb.append(formjson);
		String standardfiled=pagingStandardFiled();
		sb.append(standardfiled);
		return sb.toString();
	}
   /**
    * 流向属性-流向流过的条件-获得所有表单字段和标准值字段
    * @param workflowDefinition
    * @return
    */
   public String transitionFormAndStandardJosn(List<FormControl> lists){
	   if(lists==null){
		   lists= new ArrayList<FormControl>();
	   }
	   StringBuilder sb = new StringBuilder();
	   String formjson=allFormJosn(lists);
	   sb.append(formjson);
	   String transitionstandardfiled=pagingTransitionStandardFiled();
	   sb.append(transitionstandardfiled);
	   
	   return sb.toString();
   }
   /**
	 * 创建部门树(流向属性/流向流过的条件/添加条件[上一环节办理人部门和表单字段，标准值])
	 * @param companyId
	 * @param systemCode
	 * @param currentNodeId
	 * @return
	 */
	public String createpreviousTransactorDepartmentFormTree(String systemCode,String currentNodeId,List<FormControl> standardvalues) {
		String[] str = currentNodeId.split("_");
		List<ZTreeNode> treeNodes = new ArrayList<ZTreeNode>();
		if (currentNodeId.equals("0")) {
			initialDepartmentTree(currentNodeId,treeNodes);
			initialFormFiledTree(systemCode,treeNodes,standardvalues);//表单字段
			List<String[]> values=setpreviousTransactorDepartmentStandardValue();//标准值
			initialStandardvalueTree(systemCode,treeNodes,values);
		}else if(str[0].equals("department")||str[0].equals("branch")) {
			packagingSubDepartmentNodes(currentNodeId,treeNodes);
		}
		return JsonParser.object2Json(treeNodes);
	}
   /**
    * 获得所有表单字段
    * @param workflowDefinition
    * @return
    */
   public String allFormJosn(List<FormControl> lists){
	   if(lists==null){
		   lists= new ArrayList<FormControl>();
	   }
	   StringBuilder sb = new StringBuilder();
	   for(FormControl fc:lists){
		   sb.append("{");
		   sb.append("'name':'"+fc.getName()+"',");//字段
		   sb.append("'value':'"+fc.getTitle()+"',");//字段名
		   sb.append("'type':'"+fc.getDataType()+"',");//数据类型
		   sb.append("'dataSrc':'"+fc.getDataSrc()+"',");//dataSrc意见~~结论选项组
		   sb.append("'isSelfField':'"+1+"',");//是否是自定义字段
		   sb.append("'controlId':'"+fc.getControlId()+"',");//字段表单控件的id
		   sb.append("'controlType':'"+fc.getControlType()+"',");//字段的表单控件类型
		   sb.append("'customType':'"+fc.getCustomType()+"',");//日期类型时的自定义类型
		   sb.append("'format':'"+fc.getFormat()+"',");//格式化
		   sb.append("'groupName':'表单字段'");
		   sb.append("},");
	   }
	   return sb.toString();
   }
   /**    
    * 封装标准字段
    * @return
    */
   public String pagingStandardFiled(){
	   StringBuilder sb = new StringBuilder();
			sb.append("{");
			sb.append("'name':'documentCreatorName',");//字段
			sb.append("'value':'文档创建人姓名',");//字段名
			sb.append("'type':'TEXT',");//数据类型
			sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
			sb.append("'groupName':'标准字段'");
			sb.append("},");
			sb.append("{");
			sb.append("'name':'documentCreatorRole',");//字段
			sb.append("'value':'文档创建人角色',");//字段名
			sb.append("'type':'TEXT',");//数据类型
			sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
			sb.append("'groupName':'标准字段'");
			sb.append("},");
			sb.append("{");
			sb.append("'name':'documentCreatorDepartment',");//字段
			sb.append("'value':'文档创建人部门',");//字段名
			sb.append("'type':'TEXT',");//数据类型
			sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
			sb.append("'groupName':'标准字段'");
			sb.append("},");
			sb.append("{");
			sb.append("'name':'superiorDepartment',");//字段
			sb.append("'value':'创建人上级部门',");//字段名
			sb.append("'type':'TEXT',");//数据类型
			sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
			sb.append("'groupName':'标准字段'");
			sb.append("},");
			sb.append("{");
			sb.append("'name':'documentCreatorUpstageDepartment',");//字段
			sb.append("'value':'创建人顶级部门',");//字段名
			sb.append("'type':'TEXT',");//数据类型
			sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
			sb.append("'groupName':'标准字段'");
			sb.append("},");
			sb.append("{");
			sb.append("'name':'documentCreatorWorkGroup',");//字段
			sb.append("'value':'文档创建人工作组',");//字段名
			sb.append("'type':'TEXT',");//数据类型
			sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
			sb.append("'groupName':'标准字段'");
			sb.append("},");
			sb.append("{");
			sb.append("'name':'documentCreatorDirectSuperiorName',");//字段
			sb.append("'value':'创建人直属上级姓名',");//字段名
			sb.append("'type':'TEXT',");//数据类型
			sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
			sb.append("'groupName':'标准字段'");
			sb.append("},");
			sb.append("{");
			sb.append("'name':'documentCreatorDirectSuperiorDepartment',");//字段
			sb.append("'value':'创建人直属上级部门',");//字段名
			sb.append("'type':'TEXT',");//数据类型
			sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
			sb.append("'groupName':'标准字段'");
			sb.append("},");
			sb.append("{");
			sb.append("'name':'documentCreatorDirectSuperiorRole',");//字段
			sb.append("'value':'创建人直属上级角色',");//字段名
			sb.append("'type':'TEXT',");//数据类型
			sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
			sb.append("'groupName':'标准字段'");
			sb.append("},");
			sb.append("{");
			sb.append("'name':'documentCreatorDirectSuperiorWorkGroup',");//字段
			sb.append("'value':'创建人直属上级工作组',");//字段名
			sb.append("'type':'TEXT',");//数据类型
			sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
			sb.append("'groupName':'标准字段'");
			sb.append("},");
			sb.append("{");
			sb.append("'name':'currentTransactorName',");//字段
			sb.append("'value':'当前办理人姓名',");//字段名
			sb.append("'type':'TEXT',");//数据类型
			sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
			sb.append("'groupName':'标准字段'");
			sb.append("},");
			sb.append("{");
			sb.append("'name':'currentTransactorRole',");//字段
			sb.append("'value':'当前办理人角色',");//字段名
			sb.append("'type':'TEXT',");//数据类型
			sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
			sb.append("'groupName':'标准字段'");
			sb.append("},");
			sb.append("{");
			sb.append("'name':'currentTransactorDepartment',");//字段
			sb.append("'value':'当前办理人部门',");//字段名
			sb.append("'type':'TEXT',");//数据类型
			sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
			sb.append("'groupName':'标准字段'");
			sb.append("},");
			sb.append("{");
			sb.append("'name':'currentTransactorSuperiorDepartment',");//字段
			sb.append("'value':'当前办理人上级部门',");//字段名
			sb.append("'type':'TEXT',");//数据类型
			sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
			sb.append("'groupName':'标准字段'");
			sb.append("},");
			sb.append("{");
			sb.append("'name':'currentTransactorUpstageDepartment',");//字段
			sb.append("'value':'当前办理人顶级部门',");//字段名
			sb.append("'type':'TEXT',");//数据类型
			sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
			sb.append("'groupName':'标准字段'");
			sb.append("},");
			sb.append("{");
			sb.append("'name':'currentTransactorWorkGroup',");//字段
			sb.append("'value':'当前办理人工作组',");//字段名
			sb.append("'type':'TEXT',");//数据类型
			sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
			sb.append("'groupName':'标准字段'");
			sb.append("},");
			sb.append("{");
			sb.append("'name':'currentTransactorDirectSuperiorName',");//字段
			sb.append("'value':'当前办理人直属上级姓名',");//字段名
			sb.append("'type':'TEXT',");//数据类型
			sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
			sb.append("'groupName':'标准字段'");
			sb.append("},");
			sb.append("{");
			sb.append("'name':'currentTransactorDirectSuperiorDepartment',");//字段
			sb.append("'value':'当前办理人直属上级部门',");//字段名
			sb.append("'type':'TEXT',");//数据类型
			sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
			sb.append("'groupName':'标准字段'");
			sb.append("},");
			sb.append("{");
			sb.append("'name':'currentTransactorDirectSuperiorRole',");//字段
			sb.append("'value':'当前办理人直属上级角色',");//字段名
			sb.append("'type':'TEXT',");//数据类型
			sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
			sb.append("'groupName':'标准字段'");
			sb.append("},");
			sb.append("{");
			sb.append("'name':'currentTransactorDirectSuperiorWorkGroup',");//字段
			sb.append("'value':'当前办理人直属上级工作组',");//字段名
			sb.append("'type':'TEXT',");//数据类型
			sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
			sb.append("'groupName':'标准字段'");
			sb.append("},");
			sb.append("{");
			sb.append("'name':'approvalResult',");//字段
			sb.append("'value':'审批结果',");//字段名
			sb.append("'type':'TEXT',");//数据类型
			sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
			sb.append("'groupName':'标准字段'");
			sb.append("}");
	       
	   return sb.toString();
   }
   
   /**    
    * 流向流过的条件-封装标准字段（全部分）
    * @return
    */
   public String pagingTransitionStandardFiled(){
	   StringBuilder sb = new StringBuilder();
		    sb.append("{");
			sb.append("'name':'documentCreatorName',");//字段
			sb.append("'value':'文档创建人姓名',");//字段名
			sb.append("'type':'TEXT',");//数据类型
			sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
			sb.append("'groupName':'标准字段'");
			sb.append("},");
			sb.append("{");
			sb.append("'name':'documentCreatorRole',");//字段
			sb.append("'value':'文档创建人角色',");//字段名
			sb.append("'type':'TEXT',");//数据类型
			sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
			sb.append("'groupName':'标准字段'");
			sb.append("},");
			sb.append("{");
			sb.append("'name':'documentCreatorDepartment',");//字段
			sb.append("'value':'文档创建人部门',");//字段名
			sb.append("'type':'TEXT',");//数据类型
			sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
			sb.append("'groupName':'标准字段'");
			sb.append("},");
			sb.append("{");
			sb.append("'name':'superiorDepartment',");//字段
			sb.append("'value':'创建人上级部门',");//字段名
			sb.append("'type':'TEXT',");//数据类型
			sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
			sb.append("'groupName':'标准字段'");
			sb.append("},");
			sb.append("{");
			sb.append("'name':'documentCreatorUpstageDepartment',");//字段
			sb.append("'value':'创建人顶级部门',");//字段名
			sb.append("'type':'TEXT',");//数据类型
			sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
			sb.append("'groupName':'标准字段'");
			sb.append("},");
			sb.append("{");
			sb.append("'name':'documentCreatorWorkGroup',");//字段
			sb.append("'value':'文档创建人工作组',");//字段名
			sb.append("'type':'TEXT',");//数据类型
			sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
			sb.append("'groupName':'标准字段'");
			sb.append("},");
			sb.append("{");
			sb.append("'name':'documentCreatorDirectSuperiorName',");//字段
			sb.append("'value':'创建人直属上级姓名',");//字段名
			sb.append("'type':'TEXT',");//数据类型
			sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
			sb.append("'groupName':'标准字段'");
			sb.append("},");
			sb.append("{");
			sb.append("'name':'documentCreatorDirectSuperiorDepartment',");//字段
			sb.append("'value':'创建人直属上级部门',");//字段名
			sb.append("'type':'TEXT',");//数据类型
			sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
			sb.append("'groupName':'标准字段'");
			sb.append("},");
			sb.append("{");
			sb.append("'name':'documentCreatorDirectSuperiorRole',");//字段
			sb.append("'value':'创建人直属上级角色',");//字段名
			sb.append("'type':'TEXT',");//数据类型
			sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
			sb.append("'groupName':'标准字段'");
			sb.append("},");
			sb.append("{");
			sb.append("'name':'documentCreatorDirectSuperiorWorkGroup',");//字段
			sb.append("'value':'创建人直属上级工作组',");//字段名
			sb.append("'type':'TEXT',");//数据类型
			sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
			sb.append("'groupName':'标准字段'");
			sb.append("},");
			sb.append("{");
			sb.append("'name':'currentTransactorName',");//字段
			sb.append("'value':'当前办理人姓名',");//字段名
			sb.append("'type':'TEXT',");//数据类型
			sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
			sb.append("'groupName':'标准字段'");
			sb.append("},");
			sb.append("{");
			sb.append("'name':'currentTransactorRole',");//字段
			sb.append("'value':'当前办理人角色',");//字段名
			sb.append("'type':'TEXT',");//数据类型
			sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
			sb.append("'groupName':'标准字段'");
			sb.append("},");
			sb.append("{");
			sb.append("'name':'currentTransactorDepartment',");//字段
			sb.append("'value':'当前办理人部门',");//字段名
			sb.append("'type':'TEXT',");//数据类型
			sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
			sb.append("'groupName':'标准字段'");
			sb.append("},");
			sb.append("{");
			sb.append("'name':'currentTransactorSuperiorDepartment',");//字段
			sb.append("'value':'当前办理人上级部门',");//字段名
			sb.append("'type':'TEXT',");//数据类型
			sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
			sb.append("'groupName':'标准字段'");
			sb.append("},");
			sb.append("{");
			sb.append("'name':'currentTransactorUpstageDepartment',");//字段
			sb.append("'value':'当前办理人顶级部门',");//字段名
			sb.append("'type':'TEXT',");//数据类型
			sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
			sb.append("'groupName':'标准字段'");
			sb.append("},");
			sb.append("{");
			sb.append("'name':'currentTransactorWorkGroup',");//字段
			sb.append("'value':'当前办理人工作组',");//字段名
			sb.append("'type':'TEXT',");//数据类型
			sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
			sb.append("'groupName':'标准字段'");
			sb.append("},");
			sb.append("{");
			sb.append("'name':'currentTransactorDirectSuperiorName',");//字段
			sb.append("'value':'当前办理人直属上级姓名',");//字段名
			sb.append("'type':'TEXT',");//数据类型
			sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
			sb.append("'groupName':'标准字段'");
			sb.append("},");
			sb.append("{");
			sb.append("'name':'currentTransactorDirectSuperiorDepartment',");//字段
			sb.append("'value':'当前办理人直属上级部门',");//字段名
			sb.append("'type':'TEXT',");//数据类型
			sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
			sb.append("'groupName':'标准字段'");
			sb.append("},");
			sb.append("{");
			sb.append("'name':'currentTransactorDirectSuperiorRole',");//字段
			sb.append("'value':'当前办理人直属上级角色',");//字段名
			sb.append("'type':'TEXT',");//数据类型
			sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
			sb.append("'groupName':'标准字段'");
			sb.append("},");
			sb.append("{");
			sb.append("'name':'currentTransactorDirectSuperiorWorkGroup',");//字段
			sb.append("'value':'当前办理人直属上级工作组',");//字段名
			sb.append("'type':'TEXT',");//数据类型
			sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
			sb.append("'groupName':'标准字段'");
			sb.append("}");
			sb.append(",{");
			sb.append("'name':'previousTransactorName',");//字段
			sb.append("'value':'上一环节办理人姓名',");//字段名
			sb.append("'type':'TEXT',");//数据类型
			sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
			sb.append("'groupName':'标准字段'");
			sb.append("},");
			sb.append("{");
			sb.append("'name':'previousTransactorRole',");//字段
			sb.append("'value':'上一环节办理人角色',");//字段名
			sb.append("'type':'TEXT',");//数据类型
			sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
			sb.append("'groupName':'标准字段'");
			sb.append("},");
			sb.append("{");
			sb.append("'name':'previousTransactorDepartment',");//字段
			sb.append("'value':'上一环节办理人部门',");//字段名
			sb.append("'type':'TEXT',");//数据类型
			sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
			sb.append("'groupName':'标准字段'");
			sb.append("},");
			sb.append("{");
			sb.append("'name':'previousTransactorWorkGroup',");//字段
			sb.append("'value':'上一环节办理人工作组',");//字段名
			sb.append("'type':'TEXT',");//数据类型
			sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
			sb.append("'groupName':'标准字段'");
			sb.append("},");
			sb.append("{");
			sb.append("'name':'previousTransactorSuperiorDepartment',");//字段
			sb.append("'value':'上一环节办理人上级部门',");//字段名
			sb.append("'type':'TEXT',");//数据类型
			sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
			sb.append("'groupName':'标准字段'");
			sb.append("},");
			sb.append("{");
			sb.append("'name':'previousTransactorUpstageDepartment',");//字段
			sb.append("'value':'上一环节办理人顶级部门',");//字段名
			sb.append("'type':'TEXT',");//数据类型
			sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
			sb.append("'groupName':'标准字段'");
			sb.append("},");
			sb.append("{");
			sb.append("'name':'previousTransactorDirectSuperiorName',");//字段
			sb.append("'value':'上一环节办理人直属上级姓名',");//字段名
			sb.append("'type':'TEXT',");//数据类型
			sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
			sb.append("'groupName':'标准字段'");
			sb.append("},");
			sb.append("{");
			sb.append("'name':'previousTransactorDirectSuperiorDepartment',");//字段
			sb.append("'value':'上一环节办理人直属上级部门',");//字段名
			sb.append("'type':'TEXT',");//数据类型
			sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
			sb.append("'groupName':'标准字段'");
			sb.append("},");
			sb.append("{");
			sb.append("'name':'previousTransactorDirectSuperiorRole',");//字段
			sb.append("'value':'上一环节办理人直属上级角色',");//字段名
			sb.append("'type':'TEXT',");//数据类型
			sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
			sb.append("'groupName':'标准字段'");
			sb.append("},");
			sb.append("{");
			sb.append("'name':'previousTransactorDirectSuperiorWorkGroup',");//字段
			sb.append("'value':'上一环节办理人直属上级工作组',");//字段名
			sb.append("'type':'TEXT',");//数据类型
			sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
			sb.append("'groupName':'标准字段'");
			sb.append("},");
			sb.append("{");
			sb.append("'name':'favorCount',");//字段
			sb.append("'value':'赞成票总数',");//字段名
			sb.append("'type':'NUMBER',");//数据类型
			sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
			sb.append("'groupName':'标准字段'");
			sb.append("},");
			sb.append("{");
			sb.append("'name':'againstCount',");//字段
			sb.append("'value':'反对票总数',");//字段名
			sb.append("'type':'NUMBER',");//数据类型
			sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
			sb.append("'groupName':'标准字段'");
			sb.append("},");
			sb.append("{");
			sb.append("'name':'abstentionCount',");//字段
			sb.append("'value':'弃权票总数',");//字段名
			sb.append("'type':'NUMBER',");//数据类型
			sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
			sb.append("'groupName':'标准字段'");
			sb.append("},");
			sb.append("{");
			sb.append("'name':'countersignatureAgreePercentage',");//字段
			sb.append("'value':'会签同意人员百分比',");//字段名
			sb.append("'type':'NUMBER',");//数据类型
			sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
			sb.append("'groupName':'标准字段'");
			sb.append("},");
			sb.append("{");
			sb.append("'name':'countersignatureDisagreePercentage',");//字段
			sb.append("'value':'会签不同意人员百分比',");//字段名
			sb.append("'type':'NUMBER',");//数据类型
			sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
			sb.append("'groupName':'标准字段'");
			sb.append("},");
			sb.append("{");
			sb.append("'name':'favorPercentage',");//字段
			sb.append("'value':'赞成票百分比',");//字段名
			sb.append("'type':'NUMBER',");//数据类型
			sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
			sb.append("'groupName':'标准字段'");
			sb.append("},");
			sb.append("{");
			sb.append("'name':'againstPercentage',");//字段
			sb.append("'value':'反对票百分比',");//字段名
			sb.append("'type':'NUMBER',");//数据类型
			sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
			sb.append("'groupName':'标准字段'");
			sb.append("},");
			sb.append("{");
			sb.append("'name':'abstentionPercentage',");//字段
			sb.append("'value':'弃权票百分比',");//字段名
			sb.append("'type':'NUMBER',");//数据类型
			sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
			sb.append("'groupName':'标准字段'");
			sb.append("},");
			sb.append("{");
			sb.append("'name':'countersignatureAgreeCount',");//字段
			sb.append("'value':'会签同意人员总数',");//字段名
			sb.append("'type':'NUMBER',");//数据类型
			sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
			sb.append("'groupName':'标准字段'");
			sb.append("},");
			sb.append("{");
			sb.append("'name':'countersignatureDisagreeCount',");//字段
			sb.append("'value':'会签不同意人员总数',");//字段名
			sb.append("'type':'NUMBER',");//数据类型
			sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
			sb.append("'groupName':'标准字段'");
			sb.append("},");
			sb.append("{");
			sb.append("'name':'approvalResult',");//字段
			sb.append("'value':'审批结果',");//字段名
			sb.append("'type':'TEXT',");//数据类型
			sb.append("'isSelfField':'"+0+"',");//是否是自定义字段
			sb.append("'groupName':'标准字段'");
			sb.append("}");
	       
	   return sb.toString();
   }
   
	/**
	 * 创建人员树(流程属性/催办设置/通知人员[角色])
	 * @param companyId
	 * @param systemCode
	 * @param currentNodeId
	 * @return
	 */
	public String createRoleStandardvalueTree(String systemCode,String currentNodeId) {
		List<ZTreeNode> treeNodes = new ArrayList<ZTreeNode>();
		if (currentNodeId.equals("0")) {
			packagingRoleBySystemCode(systemCode,treeNodes);
		}
		return JsonParser.object2Json(treeNodes);
	}
	/**
	 * 创建角色树(环节属性/办理人设置/按条件筛选[角色])
	 * @param companyId
	 * @param systemCode
	 * @param currentNodeId
	 * @return
	 */
	public String createRoleAndFormFieldTree(String systemCode,String currentNodeId,List<FormControl> values) {
		List<ZTreeNode> treeNodes = new ArrayList<ZTreeNode>();
		if (currentNodeId.equals("0")) {
			packagingRoleBySystemCode(systemCode,treeNodes);
			initialFormFiledTree(systemCode,treeNodes,values);
		}
		return JsonParser.object2Json(treeNodes);
	}
	/**
	 * 创建工作组树(环节属性/办理人设置/按条件筛选[工作组,标准值和表单字段])
	 * @param companyId
	 * @param systemCode
	 * @param currentNodeId
	 * @return
	 */
	public String createWorkGroupStandardvalueAndFormFieldTree(String systemCode,String currentNodeId,List<FormControl> values) {
		List<ZTreeNode> treeNodes = new ArrayList<ZTreeNode>();
		if (currentNodeId.equals("0")) {
			packagingWorkgroupsTree(treeNodes);
			initialFormFiledTree(systemCode,treeNodes,values);//表单字段
			List<String[]> standardvalues=setcurrentTransactorWorkGroupStandardValue();//标准值
			List<String[]> list=setTransactorworkgroupStandardValue();//标准值
			for(String[] s:list){
				standardvalues.add(s);
			}
			initialStandardvalueTree(systemCode,treeNodes,standardvalues);
		}
		return JsonParser.object2Json(treeNodes);
	}
	/**
	 * 创建人员树(流程属性/催办设置/通知人员[工作组和标准值])
	 * @param companyId
	 * @param systemCode
	 * @param currentNodeId
	 * @return
	 */
	public String createWorkGroupStandardvalueTree(String systemCode,String currentNodeId,String linkflag) {
		List<ZTreeNode> treeNodes = new ArrayList<ZTreeNode>();
		if (currentNodeId.equals("0")) {
			packagingWorkgroupsTree(treeNodes);
			List<String[]> standardvalues=setworkgroupStandardValue();
			if(linkflag.equals("task")){
				List<String[]> values=setcurrentTransactorWorkGroupStandardValue();
				standardvalues.add(values.get(0));
				standardvalues.add(values.get(1));
			}
			
			initialStandardvalueTree(systemCode,treeNodes,standardvalues);
		}
		return JsonParser.object2Json(treeNodes);
	}
	/**
	 * 创建部门树(环节属性/办理人设置/按条件筛选[部门,表单字段和标准值])
	 * @param companyId
	 * @param systemCode
	 * @param currentNodeId
	 * @return
	 */
	public String createDeptStandardvalueAndFormFieldTree(String systemCode,String currentNodeId,List<FormControl> values) {
		String[] str = currentNodeId.split("_");
		List<ZTreeNode> treeNodes = new ArrayList<ZTreeNode>();
		if (currentNodeId.equals("0")) {
			initialDepartmentTree(currentNodeId,treeNodes);
			initialFormFiledTree(systemCode,treeNodes,values);//表单字段
			List<String[]> standardvalues=setcurrentTransactorDepartmentStandardValue();//标准值
			List<String[]> list =transactorsetdeptStandardValue();//标准值
			for(String[] s:list){
				standardvalues.add(s);
			}
			initialStandardvalueTree(systemCode,treeNodes,standardvalues);
		}else if(str[0].equals("department")||str[0].equals("branch")) {
			packagingSubDepartmentNodes(currentNodeId,treeNodes);
		}
		return JsonParser.object2Json(treeNodes);
	}
	/**
	 * 创建人员树(流程属性/催办设置/通知人员[部门和标准值])
	 * @param companyId
	 * @param systemCode
	 * @param currentNodeId
	 * @return
	 */
	public String createDeptStandardvalueTree(String systemCode,String currentNodeId,String linkflag) {
		String[] str = currentNodeId.split("_");
		List<ZTreeNode> treeNodes = new ArrayList<ZTreeNode>();
		if (currentNodeId.equals("0")) {
			initialDepartmentTree(currentNodeId,treeNodes);
			List<String[]> standardvalues=setdeptStandardValue();
			if(linkflag.equals("task")){
				List<String[]> values=setcurrentTransactorDepartmentStandardValue();
				standardvalues.add(values.get(0));
				standardvalues.add(values.get(1));
				standardvalues.add(values.get(2));
				standardvalues.add(values.get(3));
				standardvalues.add(values.get(4));
			}
			initialStandardvalueTree(systemCode,treeNodes,standardvalues);
		}else if(str[0].equals("department")||str[0].equals("branch")) {
			packagingSubDepartmentNodes(currentNodeId,treeNodes);
		}
		return JsonParser.object2Json(treeNodes);
	}
	/**
	 * 创建人员树(流程属性/催办设置/通知人员[人员和标准值])
	 * @param companyId
	 * @param systemCode
	 * @param currentNodeId
	 * @return
	 */
	public String createUserStandardvalueTree(String systemCode,String currentNodeId,String linkflag) {
		String[] str = currentNodeId.split("_");
		List<ZTreeNode> treeNodes = new ArrayList<ZTreeNode>();
		if (currentNodeId.equals("0")) {
			initialUserTree(currentNodeId,treeNodes);
			List<String[]> standardvalues=setuserStandardValue();
			if(linkflag.equals("task")){
				List<String[]> values=setcurrentTransactorNameStandardValue();
				standardvalues.add(values.get(0));
				standardvalues.add(values.get(1));
			}
			initialStandardvalueTree(systemCode,treeNodes,standardvalues);
		}else if(str[0].equals("departmentUser")||str[0].equals("branchUser")) {
			packagingNodeInDepartment(treeNodes,currentNodeId);
		}else if(str[0].equals("userHasNotDepartment") || str[0].equals(BRANCH_NOT_DEPARTMENT)){
			packagingUserNode(treeNodes,currentNodeId);
		}
		return JsonParser.object2Json(treeNodes);
	}
	/**
	 * 创建人员树(环节属性/办理人设置/按条件筛选[人员,标准值和表单字段])
	 * @param companyId
	 * @param systemCode
	 * @param currentNodeId
	 * @return
	 */
	public String createUserStandardvalueAndFormFieldTree(String systemCode,String currentNodeId,List<FormControl> values) {
		String[] str = currentNodeId.split("_");
		List<ZTreeNode> treeNodes = new ArrayList<ZTreeNode>();
		if (currentNodeId.equals("0")) {
			initialUserTree(currentNodeId,treeNodes);
			initialFormFiledTree(systemCode,treeNodes,values);
			List<String[]> standardvalues=setcurrentTransactorNameStandardValue();
			initialStandardvalueTree(systemCode,treeNodes,standardvalues);
		}else if(str[0].equals("departmentUser")||str[0].equals("branchUser")) {
			packagingNodeInDepartment(treeNodes,currentNodeId);
		}else if(str[0].equals("userHasNotDepartment") || str[0].equals(BRANCH_NOT_DEPARTMENT)){
			packagingUserNode(treeNodes,currentNodeId);
		}
		return JsonParser.object2Json(treeNodes);
	}
	/**
	 *（环节和子流程）办理人设置 给标准值集合拼值（部门）
	 * @return
	 */
	public List<String[]> transactorsetdeptStandardValue(){
		String[] userstandardvalue_first= new String [2];
		userstandardvalue_first[0]="上一环节办理人部门";
		userstandardvalue_first[1]="previousTransactorDepartment";
		String[] userstandardvalue_second= new String [2];
		userstandardvalue_second[0]="上一环节办理人上级部门";
		userstandardvalue_second[1]="previousTransactorSuperiorDepartment";
		String[] userstandardvalue_third= new String [2];
		userstandardvalue_third[0]="上一环节办理人顶级部门";
		userstandardvalue_third[1]="previousTransactorUpstageDepartment";
		String[] userstandardvalue_four= new String [2];
		userstandardvalue_four[0]="上一环节办理人直属上级部门";
		userstandardvalue_four[1]="previousTransactorDirectSuperiorDepartment";
		
		List<String[]> standardvalues = new ArrayList<String[]>();
		standardvalues.add(userstandardvalue_first);
		standardvalues.add(userstandardvalue_second);
		standardvalues.add(userstandardvalue_third);
		standardvalues.add(userstandardvalue_four);
		return standardvalues;
	}
	/**
	 * 给标准值集合拼值（部门）
	 * @return
	 */
	public List<String[]> setdeptStandardValue(){
		String[] userstandardvalue_first= new String [2];
		userstandardvalue_first[0]="当前办理人部门";
		userstandardvalue_first[1]="currentTransactorDepartment";
		String[] userstandardvalue_second= new String [2];
		userstandardvalue_second[0]="当前办理人上级部门";
		userstandardvalue_second[1]="currentTransactorSuperiorDepartment";
		String[] userstandardvalue_third= new String [2];
		userstandardvalue_third[0]="当前办理人顶级部门";
		userstandardvalue_third[1]="currentTransactorUpstageDepartment";
		String[] userstandardvalue_four= new String [2];
		userstandardvalue_four[0]="当前办理人直属上级部门";
		userstandardvalue_four[1]="currentTransactorDirectSuperiorDepartment";
		
		List<String[]> standardvalues = new ArrayList<String[]>();
		standardvalues.add(userstandardvalue_first);
		standardvalues.add(userstandardvalue_second);
		standardvalues.add(userstandardvalue_third);
		standardvalues.add(userstandardvalue_four);
		return standardvalues;
	}
	/**
	 * 给标准值集合拼值(人员)
	 * @return
	 */
	public List<String[]> setuserStandardValue(){
		String[] userstandardvalue_first= new String [2];
		userstandardvalue_first[0]="当前办理人姓名";
		userstandardvalue_first[1]="currentTransactorName";
		String[] userstandardvalue_second= new String [2];
		userstandardvalue_second[0]="当前办理人直属上级姓名";
		userstandardvalue_second[1]="currentTransactorDirectSuperiorName";
		
		List<String[]> standardvalues = new ArrayList<String[]>();
		standardvalues.add(userstandardvalue_first);
		standardvalues.add(userstandardvalue_second);
		return standardvalues;
	}
	/**
	 * 给标准值集合拼值(工作组)
	 * @return
	 */
	public List<String[]> setworkgroupStandardValue(){
		String[] userstandardvalue_first= new String [2];
		userstandardvalue_first[0]="当前办理人工作组";
		userstandardvalue_first[1]="currentTransactorWorkGroup";
		String[] userstandardvalue_second= new String [2];
		userstandardvalue_second[0]="当前办理人直属上级工作组";
		userstandardvalue_second[1]="currentTransactorDirectSuperiorWorkGroup";
		
		List<String[]> standardvalues = new ArrayList<String[]>();
		standardvalues.add(userstandardvalue_first);
		standardvalues.add(userstandardvalue_second);
		return standardvalues;
	}
	/**
	 * 给标准值集合拼值(环节属性/按条件筛选/工作组)
	 * @return
	 */
	public List<String[]> setTransactorworkgroupStandardValue(){
		String[] userstandardvalue_first= new String [2];
		userstandardvalue_first[0]="上一环节办理人工作组";
		userstandardvalue_first[1]="previousTransactorWorkGroup";
		String[] userstandardvalue_second= new String [2];
		userstandardvalue_second[0]="上一环节办理人直属上级工作组";
		userstandardvalue_second[1]="previousTransactorDirectSuperiorWorkGroup";
		
		List<String[]> standardvalues = new ArrayList<String[]>();
		standardvalues.add(userstandardvalue_first);
		standardvalues.add(userstandardvalue_second);
		return standardvalues;
	}
	/**
	 * 给标准值集合拼值(当前办理人工作组)
	 * @return
	 */
	public List<String[]> setcurrentTransactorWorkGroupStandardValue(){
		String[] userstandardvalue_first= new String [2];
		userstandardvalue_first[0]="文档创建人工作组";
		userstandardvalue_first[1]="documentCreatorWorkGroup";
		String[] userstandardvalue_second= new String [2];
		userstandardvalue_second[0]="创建人直属上级工作组";
		userstandardvalue_second[1]="documentCreatorDirectSuperiorWorkGroup";
		
		List<String[]> standardvalues = new ArrayList<String[]>();
		standardvalues.add(userstandardvalue_first);
		standardvalues.add(userstandardvalue_second);
		return standardvalues;
	}
	/**
	 * 给标准值集合拼值（当前办理人部门）
	 * @return
	 */
	public List<String[]> setcurrentTransactorDepartmentStandardValue(){
		String[] userstandardvalue_first= new String [2];
		userstandardvalue_first[0]="文档创建人部门";
		userstandardvalue_first[1]="documentCreatorDepartment";
		String[] userstandardvalue_second= new String [2];
		userstandardvalue_second[0]="创建人上级部门";
		userstandardvalue_second[1]="superiorDepartment";
		String[] userstandardvalue_third= new String [2];
		userstandardvalue_third[0]="创建人顶级部门";
		userstandardvalue_third[1]="documentCreatorUpstageDepartment";
		String[] userstandardvalue_four= new String [2];
		userstandardvalue_four[0]="创建人直属上级部门";
		userstandardvalue_four[1]="documentCreatorDirectSuperiorDepartment";
		String[] userstandardvalue_five= new String [2];
		userstandardvalue_five[0]="顶级部门";
		userstandardvalue_five[1]="upstageDepartment";
		
		List<String[]> standardvalues = new ArrayList<String[]>();
		standardvalues.add(userstandardvalue_five);
		standardvalues.add(userstandardvalue_first);
		standardvalues.add(userstandardvalue_second);
		standardvalues.add(userstandardvalue_third);
		standardvalues.add(userstandardvalue_four);
		return standardvalues;
	}
	/**
	 * 给标准值集合拼值（当前办理人顶级部门）
	 * @return
	 */
	public List<String[]> setcurrentTransactorUpstageDepartmentStandardValue(){
		String[] userstandardvalue_first= new String [2];
		userstandardvalue_first[0]="文档创建人部门";
		userstandardvalue_first[1]="documentCreatorDepartment";
		String[] userstandardvalue_second= new String [2];
		userstandardvalue_second[0]="创建人上级部门";
		userstandardvalue_second[1]="superiorDepartment";
		String[] userstandardvalue_third= new String [2];
		userstandardvalue_third[0]="创建人顶级部门";
		userstandardvalue_third[1]="documentCreatorUpstageDepartment";
		String[] userstandardvalue_four= new String [2];
		userstandardvalue_four[0]="创建人直属上级部门";
		userstandardvalue_four[1]="documentCreatorDirectSuperiorDepartment";
		List<String[]> standardvalues = new ArrayList<String[]>();
		standardvalues.add(userstandardvalue_first);
		standardvalues.add(userstandardvalue_second);
		standardvalues.add(userstandardvalue_third);
		standardvalues.add(userstandardvalue_four);
		return standardvalues;
	}
	/**
	 * 给标准值集合拼值(当前办理人部门)
	 * @return
	 */
	public List<String[]> setdocumentCreatorDepartmentStandardValue(){
		String[] userstandardvalue_first= new String [2];
		userstandardvalue_first[0]="顶级部门";
		userstandardvalue_first[1]="upstageDepartment";
		
		List<String[]> standardvalues = new ArrayList<String[]>();
		standardvalues.add(userstandardvalue_first);
		return standardvalues;
	}
	/**
	 * 给标准值集合拼值(当前办理人姓名)
	 * @return
	 */
	public List<String[]> setcurrentTransactorNameStandardValue(){
		String[] userstandardvalue_first= new String [2];
		userstandardvalue_first[0]="文档创建人姓名";
		userstandardvalue_first[1]="documentCreatorName";
		String[] userstandardvalue_second= new String [2];
		userstandardvalue_second[0]="创建人直属上级姓名";
		userstandardvalue_second[1]="documentCreatorDirectSuperiorName";
		List<String[]> standardvalues = new ArrayList<String[]>();
		standardvalues.add(userstandardvalue_first);
		standardvalues.add(userstandardvalue_second);
		return standardvalues;
	}
	/**
	 * 给标准值集合拼值(上一环节办理人部门)
	 * @return
	 */
	public List<String[]> setpreviousTransactorDepartmentStandardValue(){
		String[] userstandardvalue_first= new String [2];
		userstandardvalue_first[0]="创建人上级部门";
		userstandardvalue_first[1]="superiorDepartment";
		String[] userstandardvalue_third= new String [2];
		userstandardvalue_third[0]="顶级部门";
		userstandardvalue_third[1]="upstageDepartment";
		
		List<String[]> standardvalues = new ArrayList<String[]>();
		standardvalues.add(userstandardvalue_first);
		standardvalues.add(userstandardvalue_third);
		return standardvalues;
	}
	/**
	 * 封装人员标准值
	 * @param systemCode
	 * @param treeNodes
	 */
	private void initialStandardvalueTree(String systemCode, List<ZTreeNode> treeNodes,List<String[]> standardvalues) {
		boolean nodeHasSubNode=true;
		
		String rootNodeId=ALL_STANDARD_VALUE_;
		String rootNodeName="标准值";
		rootNode(rootNodeId,rootNodeName,treeNodes,nodeHasSubNode,"false");
		//standard[0]:标准值名称,standard[1]:标准值编码
		for(String[] standard:standardvalues){
			StringBuilder data = new StringBuilder();
			packagingData(data,"name",standard[0]);
			packagingData(data,"code",standard[1]);
			ZTreeNode roleNode = new ZTreeNode(STANDARD_+standard[1], rootNodeId, standard[0],"false","false",STANDARD,"{"+data.toString()+"}");
			treeNodes.add(roleNode);
		}
	}
	/**
	 * 封装表单字段
	 * @param systemCode
	 * @param treeNodes
	 */
	private void initialFormFiledTree(String systemCode, List<ZTreeNode> treeNodes,List<FormControl> standardvalues) {
		boolean nodeHasSubNode=true;
		
		String rootNodeId=ALL_FORMFIELD_;
		String rootNodeName="表单字段";
		rootNode(rootNodeId,rootNodeName,treeNodes,nodeHasSubNode,"false");
		//standard.getTitle():字段名称,standard.getName():字段定义
		for(FormControl standard:standardvalues){
			//非特殊字段
			if(!(standard.getName().indexOf("~~opinion") >= 0 ||standard.getName().indexOf("~~sign") >= 0 ||standard.getName().indexOf("~~date") >= 0 ||standard.getName().indexOf("~~conclusion") >= 0 )){
				StringBuilder data = new StringBuilder();
				packagingData(data,"name",standard.getTitle());
				packagingData(data,"code",standard.getName());
				ZTreeNode roleNode = new ZTreeNode(FORMFIELD_+standard.getName(), rootNodeId, standard.getTitle(),"false","false",FORMFIELD,"{"+data.toString()+"}");
				treeNodes.add(roleNode);
			}
		}
	}
	/**
	 * 封装表单
	 * @param systemCode
	 * @param treeNodes
	 */
	private void initialFormTree(List<ZTreeNode> treeNodes,List<FormView> formViews,String parentId) {
		for(FormView formView:formViews){
			StringBuilder data = new StringBuilder();
			packagingData(data,"name",formView.getName());
			packagingData(data,"code",formView.getCode());
			packagingData(data,"version",formView.getVersion()+"");
			ZTreeNode roleNode = new ZTreeNode(FORMFIELD_+formView.getId(), parentId, formView.getName()+"("+formView.getVersion()+")","false","false",FORMFIELD,"{"+data.toString()+"}");
			treeNodes.add(roleNode);
		}
	}
	/**
	 * 创建人员树(流程属性/基本属性/流程管理员)
	 * @param companyId
	 * @param systemCode
	 * @param currentNodeId
	 * @return
	 */
	public String createUserTree(String systemCode,String currentNodeId) {
		String[] str = currentNodeId.split("_");
		List<ZTreeNode> treeNodes = new ArrayList<ZTreeNode>();
		if (currentNodeId.equals("0")) {
			initialUserTree(currentNodeId,treeNodes);
		}else if(str[0].equals("departmentUser")||str[0].equals("branchUser")) {
			packagingNodeInDepartment(treeNodes,currentNodeId);
		}else if(str[0].equals("userHasNotDepartment") || str[0].equals(BRANCH_NOT_DEPARTMENT)){
			packagingUserNode(treeNodes,currentNodeId);
		}
		return JsonParser.object2Json(treeNodes);
	}
	/**
	 * 创建指定人员树
	 * @param companyId
	 * @param systemCode
	 * @param currentNodeId
	 * @return
	 */
	public String createDesigneeTree(String systemCode,String currentNodeId) {
		String[] str = currentNodeId.split("_");
		List<ZTreeNode> treeNodes = new ArrayList<ZTreeNode>();
		if (currentNodeId.equals("0")) {
			initialUserTree(currentNodeId,treeNodes);
			initialDepartmentTree(currentNodeId,treeNodes);
			packagingWorkgroupsTree(treeNodes);
			packagingRoleBySystemCode(systemCode,treeNodes);
		}else if(str[0].equals("department")||str[0].equals("branch")) {
			packagingSubDepartmentNodes(currentNodeId,treeNodes);
		}else if(str[0].equals("departmentUser")||str[0].equals("branchUser")) {
			packagingNodeInDepartment(treeNodes,currentNodeId);
		}else if(str[0].equals("userHasNotDepartment") || str[0].equals(BRANCH_NOT_DEPARTMENT)){
			packagingUserNode(treeNodes,currentNodeId);
		}
		return JsonParser.object2Json(treeNodes);
	}
	/**
	 * 初始化人员树
	 */
	private void initialUserTree(String currentNodeId,List<ZTreeNode> treeNodes) {
		List<Department> departments=ApiFactory.getAcsService().getDepartments();
		boolean companyNodeHasSubNode=false;
	    if(departments.size()>0){
	    	companyNodeHasSubNode=true;
	    }
		String rootNodeId=ALL_USER+ContextUtils.getCompanyId();
		String rootNodeName="人员";
		//人员树根节点
		rootNode(rootNodeId,rootNodeName,treeNodes,companyNodeHasSubNode,"true");
		//封装集团下部门和分支机构节点和无部门人员节点
		packagingNodeInCompany(treeNodes,currentNodeId,departments);
	}
	/**
	 * 封装集团下部门，分支机构和无部门节点
	 */
	private void packagingNodeInCompany(List<ZTreeNode> treeNodes,String currentNodeId,List<Department> departments) {
		for(Department dept:departments){
			ZTreeNode zTreeNode=null;
			if(dept.getBranch()){
				zTreeNode = 
					new ZTreeNode(BRANCH_USER_+dept.getId().toString(),ALL_USER+ContextUtils.getCompanyId(), dept.getName(),"false","false",BRANCH_USER,"",BRANCH);
			}else{
				String nodeName=dept.getName();
				zTreeNode = 
					new ZTreeNode(DEPARTMENT_USER_+dept.getId().toString(),ALL_USER+ContextUtils.getCompanyId(), nodeName,"false","false",DEPARTMENT_USER,"",DEPARTMENT);
			}
			if(ZTreeUtils.departmentHasSubNode(dept)){
				zTreeNode.setIsParent("true");
			}else{
				zTreeNode.setIsParent("false");
			}
			treeNodes.add(zTreeNode);
		}
		//封装无部门人员节点
		packagingNotDepartmentUser(currentNodeId,treeNodes);
	}
	/**
	 * 封装无部门人员节点
	 * @param currentId
	 * @param id
	 * @param treeNodes
	 */
	private void packagingNotDepartmentUser(String currentNodeId,List<ZTreeNode> treeNodes) {
		ZTreeNode userHasNotDepartmentNode = null;
		Long count =null;
		if(currentNodeId.equals("0")){
			count=ApiFactory.getAcsService().getUsersWithoutDepartmentCount();
			userHasNotDepartmentNode = 
				new ZTreeNode(USER_HAS_NOT_DEPARTMENT_+ContextUtils.getCompanyId(), ALL_USER+ContextUtils.getCompanyId(), "无部门人员","false","false",USER_HAS_NOT_DEPARTMENT,"","department");
		}else{
			String branchId = currentNodeId.split("_")[1];
			count=ApiFactory.getAcsService().getUsersWithoutBranchCount(Long.valueOf(branchId));
			userHasNotDepartmentNode = 
				new ZTreeNode(BRANCH_NOT_DEPARTMENT_+branchId, currentNodeId, "无部门人员","false","fasle",BRANCH_NOT_DEPARTMENT,"","department");
		}
		if(count!=null&&count>0){
			userHasNotDepartmentNode.setIsParent("true");
			treeNodes.add(userHasNotDepartmentNode);
		}
	}
	/**
	 * 根节点
	 * @param nodeId
	 * @param nodeName
	 * @param treeNodes
	 * @param nodeHasSubNode
	 * @param open
	 */
	private void rootNode(String nodeId,String nodeName,List<ZTreeNode> treeNodes,boolean nodeHasSubNode,String open) {
		ZTreeNode zTreeNode = new ZTreeNode(nodeId,"0",nodeName,open,"true","root","","root");
		if(!nodeHasSubNode){
			zTreeNode.setIsParent("false");
		}
		treeNodes.add(zTreeNode);
	}
	/**
	 * 通过系统编码封装某个系统下的角色节点
	 * @param systemCode
	 * @param treeNodes
	 */
	private void packagingRoleBySystemCode(String systemCode, List<ZTreeNode> treeNodes) {
		List<String[]> roles = AcsApi.getAllRoles(systemCode, ContextUtils.getCompanyId());
		boolean nodeHasSubNode=false;
		if(roles!=null && roles.size()>0){
			nodeHasSubNode=true;
		}
		String rootNodeId=ALL_ROLE_+systemCode;
		String rootNodeName="角色";
		rootNode(rootNodeId,rootNodeName,treeNodes,nodeHasSubNode,"false");
		//role[0]:角色名,role[1]:角色编码,role[2]:是否含有分支机构（true:表示含有分支机构，false：表示不含有分支机构）,role[3]:系统名称,role[4]:分支机构名称
		for(String[] role:roles){
			StringBuilder data = new StringBuilder();
			packagingData(data,"name",role[0]);
			packagingData(data,"code",role[1]);
			packagingData(data,"hasBranch",role[2]);
			packagingData(data,"systemName",role[3]);
			packagingData(data,"branchName",role[4]);
			packagingData(data,"systemCode",systemCode);
			ZTreeNode roleNode = new ZTreeNode(ROLE_+role[1], rootNodeId, role[0],"false","false",ROLE,"{"+data.toString()+"}",DEPARTMENT);
			treeNodes.add(roleNode);
		}
	}
	/**
	 * 封装数据
	 * @param data
	 * @param key
	 * @param value
	 * @return
	 */
	private String packagingData(StringBuilder data,String key,String value){
		if(StringUtils.isNotEmpty(data.toString())){
			data.append(",");
		}
		data.append("\"");
		data.append(key);
		data.append("\":");
		data.append("\"");
		data.append(value);
		data.append("\"");
		return data.toString();
	}
	
	/**
	 * 获取部门或分支机构下的所有节点
	 * @param 
	 * @param 
	 * @return
	 */	
	private void packagingNodeInDepartment(List<ZTreeNode> treeNodes,String currentNodeId) {
		String[] str = currentNodeId.split("_");
		Long departmentId = Long.valueOf(str[1]);
		String departmentType = str[0];
		List<Department> departments=ApiFactory.getAcsService().getSubDepartmentList(departmentId);
		if(departmentType.equals("departmentUser")){
			packagingUserNode(treeNodes,currentNodeId);
		}
		ZTreeNode node=null;
		for(Department dept:departments){
			if(dept.getBranch()){
				node = 
					new ZTreeNode(BRANCH_USER_+dept.getId().toString(),currentNodeId, dept.getName(),"false","true",BRANCH_USER,"","branch");
			}else{
				String nodeName=dept.getName();
				node = 
					new ZTreeNode(DEPARTMENT_USER_+dept.getId().toString(),currentNodeId, nodeName,"false","false",DEPARTMENT_USER,"","department");
				if(ZTreeUtils.departmentHasSubNode(dept)){
					node.setIsParent("true");
				}else{
					node.setIsParent("false");
				}
			}
			treeNodes.add(node);
		}
		if(departmentType.equals("branchUser")){
			//封装无部门人员节点
			packagingNotDepartmentUser(currentNodeId,treeNodes);
		}
	}
	
	/**
	 * 封装人员节点
	 * @param treeNodes
	 * @param currentNodeId
	 */
	private void packagingUserNode(List<ZTreeNode> treeNodes,String currentNodeId) {
		String[] str = currentNodeId.split("_");
		Long id = Long.valueOf(str[1]);
		String departmentType = str[0];
		List<com.norteksoft.acs.entity.organization.User> users=null;
		if(departmentType.equals("userHasNotDepartment")){
			users=ApiFactory.getAcsService().getEntityUsersWithoutDepartment();
		}else if(departmentType.equals(BRANCH_NOT_DEPARTMENT)){
			users=ApiFactory.getAcsService().getEntityUsersWithoutBranch(id);
		}else if(departmentType.equals("departmentUser")){
			users=ApiFactory.getAcsService().getEntityUsersByDepartment(id);
		}
		for(com.norteksoft.acs.entity.organization.User user:users){
			StringBuilder data = new StringBuilder();
			packagingData(data,"name",user.getName());
			packagingData(data,"loginName",user.getLoginName());
			packagingData(data,"branchName",user.getSubCompanyName());
			packagingData(data,"branchCode",user.getSubCompanyCode());
			packagingData(data,"hasBranch",ContextUtils.hasBranch()?"true":"false");
			ZTreeNode zTreeNode=new ZTreeNode(USER_+user.getId(),currentNodeId, user.getName(),"false","false",USER,"{"+data.toString()+"}",USER);
			treeNodes.add(zTreeNode);
		}
	}
	
	/**
	 * 初始化部门节点
	 * @param currentId
	 * @param treeNodes
	 */
	public void initialDepartmentTree(String currentId,List<ZTreeNode> treeNodes) {
		List<Department> departments=ApiFactory.getAcsService().getDepartments();
		boolean companyNodeHasSubNode=false;
	    if(departments.size()>0){
	    	companyNodeHasSubNode=true;
	    }
		String rootNodeId="allDepartment_"+ContextUtils.getCompanyId();
		String rootNodeName="部门";
		//部门节点
		rootNode(rootNodeId,rootNodeName,treeNodes,companyNodeHasSubNode,"false");
		packagingDepartment(rootNodeId,treeNodes,departments);
	}
	
	/**
	 * 封装部门或分支机构节点
	 * @param pId
	 * @param treeNodes
	 * @param departments
	 */
	private void packagingDepartment(String pId,List<ZTreeNode> treeNodes, List<Department> departments) {
		for(Department d : departments){
			List<Department> subDepartments = ApiFactory.getAcsService().getSubDepartmentList(d.getId());//得到子部门 
			if((subDepartments != null && subDepartments.size() > 0)){
				packagingDepartmentNode(d,pId,treeNodes,"true");
			}else{
				packagingDepartmentNode(d,pId,treeNodes,"false");
			}
		}
	}
	
	/**
	 * 点击部门节点时加载部门
	 */
	public void packagingSubDepartmentNodes(String currentId,List<ZTreeNode> treeNodes) {
		String[] str = currentId.split("_");
		List<Department> departments=ApiFactory.getAcsService().getSubDepartmentList(Long.valueOf(str[1]));
		packagingDepartment(currentId,treeNodes,departments);
	}
	
	/**
	 * 封装部门节点
	 * 
	 */
	private void packagingDepartmentNode(Department d,String parentId ,List<ZTreeNode> treeNodes,String open) {
		StringBuilder data = new StringBuilder();
		packagingData(data,"name",d.getName());
		packagingData(data,"code",d.getCode());
		packagingData(data,"branchName",d.getSubCompanyName());
		packagingData(data,"branch",d.getBranch()?"true":"false");
		packagingData(data,"hasBranch",ContextUtils.hasBranch()?"true":"false");
		ZTreeNode zTreeNode = null;
		if(d.getBranch()){
			zTreeNode=new ZTreeNode(BRANCH_+d.getId(),parentId,d.getName(),"false",open,BRANCH,"{"+data.toString()+"}",BRANCH);
		}else{
			zTreeNode=new ZTreeNode(DEPARTMENT_+d.getId(),parentId,d.getName(),"false",open,DEPARTMENT,"{"+data.toString()+"}",DEPARTMENT);
		}
		treeNodes.add(zTreeNode);
	}
	
	/**
	 * 封装工作组树
	 * @param treeNodes
	 */
	private void packagingWorkgroupsTree(List<ZTreeNode> treeNodes) {
	    List<Workgroup> workGroups = ApiFactory.getAcsService().getAllWorkgroups();
	    boolean companyNodeHasSubNode=false;
	    if(workGroups.size()>0){
	    	companyNodeHasSubNode=true;
	    }
	    String rootNodeId="allWorkgroup_"+ContextUtils.getCompanyId();
	    String rootNodeName="工作组";
		//工作组节点
	    rootNode(rootNodeId,rootNodeName,treeNodes,companyNodeHasSubNode,"false");
	    //封装公司下工作组节点
	    List<Workgroup> workGroupsINCompany=ApiFactory.getAcsService().getWorkgroups();
	    for(int i=0;i<workGroupsINCompany.size();i++){
	    	packagingWorkgroupNode(workGroupsINCompany.get(i),rootNodeId,treeNodes);
	    }
	    //封装分支机构下的"工作组节点"
	    List<Department> depts=ApiFactory.getAcsService().getDepartmentIfHasWorkGroup();
	    packagingBranchWorkgroupNode(rootNodeId,treeNodes,depts);
	}
	
	/**
	 * 封装工作组节点
	 * 
	 */
	public void packagingWorkgroupNode(Workgroup workgroup, String parentNodeId,List<ZTreeNode> treeNodes) {
	    StringBuilder data = new StringBuilder();
		packagingData(data,"name",workgroup.getName());
		packagingData(data,"code",workgroup.getCode());
		packagingData(data,"branchName",workgroup.getSubCompanyName());
		packagingData(data,"hasBranch",ContextUtils.hasBranch()?"true":"false");
		ZTreeNode zTreeNode = new ZTreeNode(WORKGROUP_+workgroup.getId(), parentNodeId, workgroup.getName(),"false","false",WORKGROUP,"{"+data+"}",WORKGROUP);	
		treeNodes.add(zTreeNode);
	}
	
	/**
	 * 封装分支机构下的工作组节点
	 * @param parentNodeId
	 * @param treeNodes
	 * @param depts
	 */
	public void packagingBranchWorkgroupNode(String parentNodeId,List<ZTreeNode> treeNodes, List<Department> depts) {
		for(Department dept:depts){
			String deptNodeId=HASWORKGROUPBRANCH_+dept.getId();
			treeNodes.add(new ZTreeNode(deptNodeId,parentNodeId, dept.getName(),"true","true",HASWORKGROUPBRANCH,"",BRANCH));
			List<Workgroup> workGroups=ApiFactory.getAcsService().getWorkgroupsByBranchId(dept.getId());
			for(Workgroup w : workGroups){
				packagingWorkgroupNode(w,deptNodeId,treeNodes);
			}
		}
	}
	/**
	 * 根据选项组编号获得选项组
	 * @param ops
	 * @return
	 */
	public String parseOptionGroups(List<Option> ops) {
		StringBuilder sb = new StringBuilder();
		for (Option op : ops) {
			sb.append("{");
			sb.append("'name':'" + op.getName() + "',");// 字段
			sb.append("'value':'" + op.getValue()+"'" );// 字段名
			sb.append("},");
		}
		return StringUtils.removeEnd(sb.toString(), ",");
	}
	/**
     * 修改流程定义
     */
	public Long updateWfDefinition(Long wfdId, Long companyId, String xmlContent,String processTypeCode,String systemCode,String saveUrl){
		WorkflowDefinition wfd = workflowDefinitionDao.get(wfdId);
		if(wfd.getEnable()==DataState.DRAFT){
			workflowDefinitionManager.updateWfDefinition(wfdId, companyId, xmlContent, processTypeCode, systemCode,saveUrl);
		}else{
			workflowDefinitionManager.updateWfDefVersion(wfdId, companyId, xmlContent, wfd.getTypeId(), wfd.getSystemId());
		}
    	return wfdId;
    }
	/**
     * 修改流程定义,用于兼容老版本流程图
     */
	public Long updateWfDefinitionById(Long wfdId, Long companyId, String xmlContent,Long processTypeId,Long systemId){
		WorkflowDefinition wfd = workflowDefinitionDao.get(wfdId);
		if(wfd.getEnable()==DataState.DRAFT){
			workflowDefinitionManager.updateWfDefinitionById(wfdId, companyId, xmlContent, processTypeId, systemId);
		}else{
			workflowDefinitionManager.updateWfDefVersion(wfdId, companyId, xmlContent, processTypeId, systemId);
		}
    	return wfdId;
    }
	/**
	 * 根据xmlFile解析xml
	 * @param file
	 * @return
	 */
	public String parseXml(File file){
		String xml = "";
		try {
			SAXReader reader = new SAXReader();
			Document document = reader.read(file);
			Element root = document.getRootElement();  
			xml = parseXml(root);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return xml;
	}
	/**
	 * 根据xml字符串解析xml
	 * @param xml
	 * @return
	 */
	public String parseXml(String xml){
		String xmlJson = "";
		try {
			Document document = DocumentHelper.parseText(xml);
			Element root = document.getRootElement();
			xmlJson = parseXml(root);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return xmlJson;
	}
	/**
	 * 根据xml Element解析xml
	 * @param root
	 * @return
	 */
	private String parseXml(Element root){
		Map<String,Object> map = new LinkedHashMap<String, Object>();
		Map<String,Map<String,Object>> specialEndMap = new LinkedHashMap<String, Map<String,Object>>();
		Map<String,Integer> tacheIndexMap = new LinkedHashMap<String, Integer>();
		for(Iterator it=root.elementIterator();it.hasNext();){       
			Element element = (Element) it.next();
			String elementName = element.getName();
			if(!"on".equals(elementName)&& !"extend".equals(elementName)){
				Attribute attribute = element.attribute("name");
				if(attribute == null)break;
				String attributeName = attribute.getText();
				if("state".equals(elementName) && attributeName.indexOf("end-state*")==0){
					String to = element.element("transition").attribute("to").getText();
					Map<String,Object> tempMap = specialEndMap.get(to);
					if(tempMap!=null){
						tempMap.put(element.getName(), parseXmlElement(element,tempMap));
						Integer index = tacheIndexMap.get("specialEndIcon");
						if(index==null){
							map.put("specialEndIcon", tempMap);
							tacheIndexMap.put("specialEndIcon", 1);
						}else{
							map.put("specialEndIcon-"+index, tempMap);
							tacheIndexMap.put("specialEndIcon", index+1);
						}
					}else{
						tempMap = new LinkedHashMap<String, Object>();
						tempMap.put(element.getName(), parseXmlElement(element,new LinkedHashMap<String, Object>()));
						specialEndMap.put(to,tempMap);
					}
				}else if("end".equals(elementName) && attributeName.indexOf("special-end*")==0){
					Map<String,Object> tempMap = specialEndMap.get(attributeName);
					if(tempMap!=null){
						tempMap.put(element.getName(), parseXmlElement(element,new LinkedHashMap<String, Object>()));
						Integer index = tacheIndexMap.get("specialEndIcon");
						if(index==null){
							map.put("specialEndIcon", tempMap);
							tacheIndexMap.put("specialEndIcon", 1);
						}else{
							map.put("specialEndIcon-"+index, tempMap);
							tacheIndexMap.put("specialEndIcon", index+1);
						}
					}else{
						tempMap = new LinkedHashMap<String, Object>();
						tempMap.put(element.getName(), parseXmlElement(element,new LinkedHashMap<String, Object>()));
						specialEndMap.put(attributeName,tempMap);
					}
				}else{
					String type = getTacheType(element);
					if(StringUtils.isNotEmpty(type)){
						Map<String,Object> mapA = new LinkedHashMap<String, Object>();
						mapA.put(element.getName(), parseXmlElement(element,new LinkedHashMap<String, Object>()));
						Integer index = tacheIndexMap.get(type);
						if(index==null){
							map.put(type, mapA);
							tacheIndexMap.put(type, 1);
						}else{
							map.put(type+"-"+index, mapA);
							tacheIndexMap.put(type, index+1);
						}
					}
				}
				root.remove(element);
			}
		}
		//解析流程属性
		Map<String,Object> processPropertiesMapTemp = parseXmlElement(root,new LinkedHashMap<String, Object>());
		processPropertiesMapTemp.put("xmlns", "http://jbpm.org/4.0/jpdl");
		Map<String,Object> processPropertiesMap = new LinkedHashMap<String, Object>();
		processPropertiesMap.put(root.getName(), processPropertiesMapTemp);
		map.put("processProperties", processPropertiesMap);
		JSONObject obj = JSONObject.fromObject(map);
		return obj.toString();
	}
	/**
	 * 获得环节的类型
	 * @param element
	 * @return
	 */
	private String getTacheType(Element element){
		String elementName = element.getName();
		String res = "";
		if("task".equals(elementName)){//任务
			res="taskIcon";
		}else if("custom".equals(elementName)){//子流程
			res="subProcessIcon";
		}else if("decision".equals(elementName)){//条件判断
			res="decisionIcon";
		}else if("java".equals(elementName)||"sql".equals(elementName)||"hql".equals(elementName)||"script".equals(elementName)){//自定义环节(java类型、sql类型、hql类型、script类型)
			res="customTacheIcon";
		}else if("start".equals(elementName)){//开始
			res="startIcon";
		}else if("end".equals(elementName)){//结束
			res="endIcon";
		}else if("fork".equals(elementName)){//分支
			res="forkIcon";
		}else if("join".equals(elementName)){//汇聚
			res="joinIcon";
		}else{
			//人工判断，自动环节，抄送环节全是state
			if(element.element("extend")!=null){
				String tacheType = element.element("extend").element("tache-type").getText();
				if(StringUtils.isNotEmpty(tacheType)){
					if("choice-tache".equals(tacheType)){//人工判断
						res="artificialityDecisionIcon";
					}else if("auto-tache".equals(tacheType)){//自动环节
						res="automationTacheIcon";
					}else if("copy-tache".equals(tacheType)){//抄送环节
						res="copyTacheIcon";
					}
				}
			}
		}
		return res;
	}
	/**
	 * 解析xml的每个节点
	 * @param element
	 * @param map
	 * @return
	 */
	private Map<String,Object> parseXmlElement(Element element,Map<String,Object> map){
		Map<String,Integer[]> tempMap = new LinkedHashMap<String, Integer[]>();
		//解析所有的属性
		for(Iterator it=element.attributeIterator();it.hasNext();){
			Attribute attribute = (Attribute) it.next();
			if(attribute!=null){
				String attributeName = attribute.getName();
				if("class".equals(attributeName)){
					map.put("class1", attribute.getText());
				}else{
					map.put(attributeName, attribute.getText());
				}
			}
		}
		//取得对象中所有的子对象的个数，并存起来
		for(Iterator it=element.elementIterator();it.hasNext();){         
			Element subElement = (Element) it.next();         
			String name = subElement.getName();
			if(tempMap.get(name)==null){
				Integer[] arr = {1,0};
				tempMap.put(name, arr);
			}else{
				Integer[] arr = tempMap.get(name);
				arr[0] = arr[0]+1;
				tempMap.put(name, arr);
			}
		} 
		//遍历每个子对象
		for(Iterator it=element.elementIterator();it.hasNext();){         
			Element subElement = (Element) it.next();         
			String name = subElement.getName();
			String newName = name;
			Integer[] arr = tempMap.get(name);
			if(!(arr[0] == 1 && arr[1] == 0)){
				int index = arr[1]+1;
				newName = name +"-"+ index;
				arr[0] = arr[0]-1;
				arr[1] = index;
				tempMap.put(name, arr);
			}
			Map<String,Object> subMap = new LinkedHashMap<String, Object>();
			String text = subElement.getText();
			if(subElement.elements().size()==0 && StringUtils.isNotEmpty(text)){
				subMap.put("text", text);
			}
			map.put(newName, parseXmlElement(subElement,subMap));
		} 
		return map;
	}
	/**
	 * 通过流程定义id和公司id获得JSON形式的xml
	 * @param companyId
	 * @param wfdId
	 * @return
	 */
	public String getXmlAndWfStateJson(Long wfdId, Long companyId) {
		String xmlFile = workflowDefinitionManager.getXmlByDefinitionId(wfdId, companyId);
		WorkflowDefinition workflowDefinition = workflowDefinitionManager.getWfDefinitionByWfdId(wfdId);
		String xmlJson = parseXml(xmlFile);
		return "{\"xmlFile\":"+xmlJson+",\"wfState\":\""+workflowDefinition.getEnable()+"\"}";
	}
	/**
	 * 根据流程实例ID查询历史环节和当前环节
	 * @param processInstanceId
	 * @param companyId
	 * @return
	 */
	public String getHistoryCurrentTacheByInstanceId(Long companyId,String processInstanceId) {
		String[][] historyTaskInformations = instanceHistoryManager.getHistoryByInstanceId(companyId, processInstanceId);
		Map<String,Object> map = new LinkedHashMap<String, Object>();
		Map<String,Object> historyTacheMap = new LinkedHashMap<String, Object>();
		int index = 0;
		for(String[] information:historyTaskInformations){
			if(information[0].indexOf("transitionUI")==0)continue;
			Map<String,String> subMap = new LinkedHashMap<String, String>();
			subMap.put("tacheName", information[0]);//环节名称
			subMap.put("transactor", information[1]);//办理人
			subMap.put("transactionResult", information[2]);//办理结果
			subMap.put("transactorOpinion", information[3]);//办理意见
			subMap.put("isSpecialTask", information[4]);//是否特事特办任务
			historyTacheMap.put("tacheInformation"+index,subMap);
			index++;
		}
		map.put("historyTache", historyTacheMap);
		List<String[]> currentTasks = instanceHistoryManager.getCurrentTasks(companyId, processInstanceId);
		Map<String,Object> currentTacheMap = new LinkedHashMap<String, Object>();
		for(String[] information:currentTasks){
			Map<String,String> subMap = new LinkedHashMap<String, String>();
			subMap.put("tacheName", information[0]);//环节名称
			subMap.put("transactor", information[1]);//办理人
			subMap.put("isSpecialTask", information[2]);//是否特事特办任务
			currentTacheMap.put("tacheInformation"+index, subMap);
			index++;
		}
		map.put("currentTache", currentTacheMap);
		JSONObject obj = JSONObject.fromObject(map);
		return obj.toString();
	}
	/**
	 * 根据父流程的workflowId和环节名获得它的子流程实例
	 * @param companyId
	 * @param processInstanceId
	 * @return
	 */
	public String getSubProcessInstanceByTaskName(String parentWorkflowId,String tacheName) {
		List<WorkflowInstance> workflowInstanceList = workflowInstanceManager.getSubProcessInstanceByTaskName(parentWorkflowId, tacheName);
		Map<String,Object> map = new LinkedHashMap<String, Object>();
		int index = 0;
		for(WorkflowInstance workflowInstance:workflowInstanceList){
			Map<String,Object> tempMap = new LinkedHashMap<String, Object>();
			index++;
			String showName = workflowInstance.getCreator();
			if(StringUtils.isNotEmpty(workflowInstance.getCreatorName())){
				showName = workflowInstance.getCreatorName();
			}
			String subCompanyName = "";
			if(ContextUtils.hasBranch()){
				subCompanyName = "("+workflowInstance.getSubCompanyName()+")";
			}
			String content = index+" 创建人为"+showName+subCompanyName;
			tempMap.put("content",content);
			tempMap.put("workflowDefinitionId",workflowInstance.getWorkflowDefinitionId());
			tempMap.put("processInstanceId",workflowInstance.getProcessInstanceId());
			map.put(index+"",tempMap);
		}
		JSONObject obj = JSONObject.fromObject(map);
		return obj.toString();
	}
	
	/**
	 * 根据流程实例ID查询流程定义文件
	 * @param instanceId
	 * @param companyId
	 * @return
	 */
	public String getXmlByInstanceId(String processInstanceId,Long companyId) {
		String xml = workflowInstanceManager.getXmlByInstanceId(processInstanceId, companyId);
		if(StringUtils.isNotEmpty(xml)){
			return parseXml(xml);
		}else{
			return "";
		}
	}
	
	/**
	 * 流程属性/基本属性
	 * @param workflowDefinition
	 * @return
	 */
	public String workflowDefinitionJosn(WorkflowDefinition workflowDefinition){
		
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("'id':'"+workflowDefinition.getId()+"',");
		sb.append("'name':'"+workflowDefinition.getName()+"',");
		sb.append("'code':'"+workflowDefinition.getCode()+"',");
		sb.append("'adminName':'"+workflowDefinition.getAdminName()+"',");
		sb.append("'adminLoginName':'"+workflowDefinition.getAdminLoginName()+"',");
		sb.append("'formName':'"+workflowDefinition.getFormName()+"',");
		sb.append("'formCode':'"+workflowDefinition.getFormCode()+"',");
		sb.append("'fromVersion':'"+workflowDefinition.getFromVersion()+"',");
		sb.append("'version':'"+workflowDefinition.getVersion()+"',");
		sb.append("'processId':'"+workflowDefinition.getProcessId()+"',");
		sb.append("'typeId':'"+workflowDefinition.getTypeId()+"',");
		sb.append("'systemId':'"+workflowDefinition.getSystemId()+"',");
		sb.append("'processType':'"+workflowDefinition.getProcessType()+"',");
		sb.append("'customType':'"+workflowDefinition.getCustomType()+"',");
		sb.append("'adminId':'"+workflowDefinition.getAdminId()+"',");
		sb.append("'subCompanyName':'"+workflowDefinition.getSubCompanyName()+"',");
		sb.append("'creatorName':'"+workflowDefinition.getCreatorName()+"'");
		sb.append("}");
		return sb.toString();
	}
}
