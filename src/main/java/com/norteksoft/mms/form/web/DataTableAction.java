package com.norteksoft.mms.form.web;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLClassLoader;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;

import com.norteksoft.mms.base.autoTool.MyClassLoader;
import com.norteksoft.mms.base.autoTool.Util;
import com.norteksoft.mms.base.data.DataHandle;
import com.norteksoft.mms.form.entity.DataTable;
import com.norteksoft.mms.form.entity.GenerateSetting;
import com.norteksoft.mms.form.entity.TableColumn;
import com.norteksoft.mms.form.service.DataTableManager;
import com.norteksoft.mms.form.service.FormViewManager;
import com.norteksoft.mms.form.service.ImportDataTableManager;
import com.norteksoft.mms.form.service.SheetManager;
import com.norteksoft.mms.form.service.TableColumnManager;
import com.norteksoft.mms.module.entity.Menu;
import com.norteksoft.mms.module.service.MenuManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.entity.BusinessSystem;
import com.norteksoft.product.api.entity.Company;
import com.norteksoft.product.enumeration.DataState;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.JsonParser;
import com.norteksoft.product.util.PageUtils;
import com.norteksoft.product.util.ParameterUtils;
import com.norteksoft.product.util.ThreadParameters;
import com.norteksoft.product.util.ZipUtils;
import com.norteksoft.product.util.freemarker.TagUtil;
import com.norteksoft.product.util.freemarker.TemplateRender;
import com.norteksoft.product.util.tree.ZTreeNode;
import com.norteksoft.product.web.struts2.CrudActionSupport;
import com.norteksoft.product.web.struts2.Struts2Utils;

@Namespace("/form")
@ParentPackage("default")
@Results( { @Result(name = CrudActionSupport.RELOAD, location = "data-table", type = "redirectAction") })
public class DataTableAction extends CrudActionSupport<DataTable> {
	private static final long serialVersionUID = 1L;

	private DataTable dataTable;

	private Long tableId;
	
	private Long menuId;
	
	private MenuManager menuManager;

	private DataHandle dataHandle;
	
	private Page<DataTable> dataTables = new Page<DataTable>(0, true);

	private List<Long> tableIds;

	private DataTableManager dataTableManager;

	private TableColumnManager tableColumnManager;
	
	private SheetManager sheetManager;
	
	@Autowired
	private ImportDataTableManager importDataTableManager;

	private String tableName;

	private List<TableColumn> columns;

	private String states;

	private boolean canChange;
	private boolean deleteEnable = false;
	private String ids;
	
	private File file;
	private Long id;//数据表字段id
	private String fileName;
	private String showTreeType;//三级菜单显示树的类型
	
	private String systemCode;//swing代码生成中使用
	private String companyCode;//swing代码生成中使用
	private String filePath;//swing代码生成中使用
	private String logFlag="false";//swing代码生成中使用,action中是否生成日志
	private String inputShowType="refresh";//swing代码生成工具中配置的新建、修改按钮实现方式:弹出(popup)、刷新区域(refresh),默认为刷新区域
	private String menuChangeType="zone";//swing代码生成工具中配置的点击左侧菜单时，页面显示方式：刷新区域(zone)或、整个页面(totalZone)，默认为刷新区域
	
	private static final String SUCCESS_MESSAGE_LEFT = "<font class=\"onSuccess\"><nobr>";
	private static final String MESSAGE_RIGHT = "</nobr></font>";
	private static final String ERROR_MESSAGE_LEFT = "<font class=\"onError\"><nobr>";
	
	private Log log = LogFactory.getLog(DataTableAction.class);
	protected void addErrorMessage(String message){
		this.addActionMessage(ERROR_MESSAGE_LEFT+message+MESSAGE_RIGHT);
	}
	protected void addSuccessMessage(String message){
		this.addActionMessage(SUCCESS_MESSAGE_LEFT+message+MESSAGE_RIGHT);
	}
	
	@Autowired
	public void setMenuManager(MenuManager menuManager) {
		this.menuManager = menuManager;
	}
	@Autowired
	public void setDataHandle(DataHandle dataHandle) {
		this.dataHandle = dataHandle;
	}
	
	@Override
	@Action("data-table-list-data")
	public String list() throws Exception {
		List<Menu> menus = menuManager.getEnabledStandardRootMenuByCompany();
		if(menuId==null&&menus.size()>0){
			menuId = menus.get(0).getId();
		}
		if(menuId!=null){
			if(dataTables.getPageSize()>1){
				dataTableManager.getSystemAllDataTables(dataTables, menuId);
				ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("mms.dataTableManager"), 
						ApiFactory.getBussinessLogService().getI18nLogInfo("mms.dataTableList"), 
						ContextUtils.getSystemId("mms"));
				this.renderText(PageUtils.pageToJson(dataTables));
				return null;
			}
		}
		return "data-table";
	}
	
	@Action("data-table-defaultDataTableList")
	public String defaultDataTableList() throws Exception {
		List<Menu> menus = menuManager.getEnabledCustomRootMenuByCompany();
		if(menuId==null&&menus.size()>0){
			menuId = menus.get(0).getId();
		}
		if(menuId!=null){
			if(dataTables.getPageSize()>1){
				dataTableManager.getSystemDefaultDataTables(dataTables, menuId);
				ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("mms.dataTableManager"), 
						ApiFactory.getBussinessLogService().getI18nLogInfo("mms.dataTableList"), 
						ContextUtils.getSystemId("mms"));
				this.renderText(PageUtils.pageToJson(dataTables));
				return null;
			}
		}
		return "data-table-default";
	}

	@Override
	@Action("data-table-input")
	public String input() throws Exception {
		ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("mms.dataTableManager"), 
				ApiFactory.getBussinessLogService().getI18nLogInfo("mms.dataTableForm"), 
				ContextUtils.getSystemId("mms"));
		return "data-table-input";
	}
	
	@Action("data-table-viewCustom")
	public String viewCustom() throws Exception {
		dataTable = dataTableManager.getDataTable(tableId);
		return "data-table-view";
	}
	@Action("data-table-checkTableName")
	public String checkTableName() throws Exception {
		renderText(String.valueOf(dataTableManager.getDataTableByName(
				tableName, tableId)));
		return null;
	}

	/**
	 * 保存只存在改表结构，在草稿到启用时才是建表
	 */
	@Override
	@Action("data-table-save")
	public String save() throws Exception {
		dataTableManager.saveDataTable(dataTable);
		ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("mms.dataTableManager"), 
				ApiFactory.getBussinessLogService().getI18nLogInfo("mms.dataTableSave"), 
				ContextUtils.getSystemId("mms"));
		addSuccessMessage(Struts2Utils.getText("form.save.success"));
		return "data-table-input";
	}

	public void prepareDealWithTableColumn() throws Exception {
		dataTable = dataTableManager.getDataTable(tableId);
	}

	/**
	 * 字段设置页面
	 * 
	 * @return
	 * @throws Exception
	 */
	@Action("data-table-dealWithTableColumn")
	public String dealWithTableColumn() throws Exception {
		dataTable = dataTableManager.getDataTable(tableId);
		columns=tableColumnManager.getTableColumnByDataTableId(tableId);
		ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("mms.dataTableManager"), 
				ApiFactory.getBussinessLogService().getI18nLogInfo("mms.fieldSetPage"), 
				ContextUtils.getSystemId("mms"));
		return "data-table-columns";
	}
	/**
	 * 自定义数据表查看页面字段设置页面
	 * 
	 * @return
	 * @throws Exception
	 */
	@Action("data-table-viewCustomTableColumn")
	public String viewCustomTableColumn() throws Exception {
		dataTable = dataTableManager.getDataTable(tableId);
		columns=tableColumnManager.getTableColumnByDataTableId(tableId);
		ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("mms.dataTableManager"), 
				ApiFactory.getBussinessLogService().getI18nLogInfo("mms.fieldSetPage"), 
				ContextUtils.getSystemId("mms"));
		return "data-table-custom-columns";
	}

	/**
	 * 删除数据表信息--只能为草稿状态的
	 */
	@Override
	@Action("data-table-delete")
	public String delete() throws Exception {
		if(deleteEnable){
			deleteEnable();
		}else{
			deleteDraft();
		}
		ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("mms.dataTableManager"), 
				ApiFactory.getBussinessLogService().getI18nLogInfo("mms.dataTableDelete"), 
				ContextUtils.getSystemId("mms"));
		return list();
	}
	private void deleteEnable(){
		dataTableManager.deleteEnableDataTables(tableIds);
		addSuccessMessage(Struts2Utils.getText("form.delete.success"));
	}
	private void deleteDraft(){
		boolean canDelete = true;
		for (Long tableId : tableIds) {
			dataTable = dataTableManager.getDataTable(tableId);
			if (dataTable.getTableState() != DataState.DRAFT) {
				canDelete = false;
			}
		}
		if (canDelete) {
			dataTableManager.deleteDataTables(tableIds);
			addSuccessMessage(Struts2Utils.getText("form.delete.success"));
		} else {
			addErrorMessage(Struts2Utils.getText("formManager.deleteTableInfo"));
		}
	}
	
	/**
	 * 删除启用或草稿状态的自定义数据表
	 */
	@Action("data-table-deleteCustom")
	public String deleteCustom() throws Exception {
		if(deleteEnable){
			deleteEnable();
		}else{
			deleteDraft();
		}
		ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("mms.dataTableManager"), 
				ApiFactory.getBussinessLogService().getI18nLogInfo("mms.dataTableDelete"), 
				ContextUtils.getSystemId("mms"));
		return defaultDataTableList();
	}

	public void prepareSaveColumns() throws Exception {
		dataTable = dataTableManager.getDataTable(tableId);
	}

	/**
	 * 保存字段信息
	 * 
	 * @return
	 * @throws Exception
	 */
	@Action("data-table-saveColumns")
	public String saveColumns() throws Exception {
		tableColumnManager.saveTableColumns(dataTable);
		columns=tableColumnManager.getTableColumnByDataTableId(tableId);
		ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("mms.dataTableManager"), 
				ApiFactory.getBussinessLogService().getI18nLogInfo("mms.saveFieldInfo"), 
				ContextUtils.getSystemId("mms"));
		addSuccessMessage(Struts2Utils.getText("form.save.success"));
		return "data-table-columns";
	}

//	public void prepareChangeTableState() throws Exception {
//		dataTable = dataTableManager.getDataTable(tableId);
//	}

	/**
	 * 改变数据表的状态(草稿->启用;启用->禁用;禁用->启用)
	 * 
	 * @return
	 * @throws Exception
	 */
	@Action("data-table-changeTableState")
	public String changeTableState() throws Exception {
		addSuccessMessage(dataTableManager.changeTableState(tableIds,menuId));
		ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("mms.dataTableManager"), 
				ApiFactory.getBussinessLogService().getI18nLogInfo("mms.changeTableStatus"), 
				ContextUtils.getSystemId("mms"));
		log.debug("table info has saved");
		return list();
	}
	@Action("data-table-exportToExcel")
	public String exportToExcel() throws Exception{
		HttpServletResponse response = Struts2Utils.getResponse();
		response.reset();
		response.setContentType("application/x-download");
		response.addHeader("Content-Disposition", "attachment;filename="+URLEncoder.encode("字段信息.xls","UTF-8"));
		sheetManager.exportToExcel(response.getOutputStream());
		return null;
	}
	@Action("data-table-showImport")
	public String showImport() throws Exception{
		return "data-table-showImport";
	}
	@Action("data-table-importInto")
	public String importInto() throws Exception{
		dataTable = dataTableManager.getDataTable(tableId);
		columns=sheetManager.importIntoData(file,dataTable);
		addSuccessMessage(Struts2Utils.getText("menuManager.importSuccess"));
		return "data-table-import";
	}

	@Override
	protected void prepareModel() throws Exception {
		if (tableId != null) {
			dataTable = dataTableManager.getDataTable(tableId);
			menuId=dataTable.getMenuId();
		} else {
			dataTable = new DataTable();
		}
		if(menuId!=null && menuId.intValue()!=0){
			dataTable.setMenuId(menuId);
		}
	}
	/**
	 * 数据表管理的系统树
	 */
	@Action("data-table-dataTableStandardSysTree")
	public String dataTableStandardSysTree() throws Exception {
		ZTreeNode node = null;
		List<ZTreeNode> treeNodes = new ArrayList<ZTreeNode>();
		List<Menu> menus1 = menuManager.getEnabledStandardRootMenuByCompany();
		java.util.Collections.sort(menus1);
		node = new ZTreeNode("SYSTEM_STANDARD","0",Struts2Utils.getText("dataTableManager.standardTable"), "true", "false", "", "", "folder", "");
		treeNodes.add(node);
		node = new ZTreeNode("SYSTEM_CUSTOM","0",Struts2Utils.getText("dataTableManager.customTable"), "true", "false", "", "", "folder", "");
		treeNodes.add(node);
		for(Menu menu :menus1){
			node = new ZTreeNode(menu.getId().toString()+"_STANDARD","SYSTEM_STANDARD",getInternation(menu.getName()), "true", "false", "", "", "folder", "");
			treeNodes.add(node);
		}
		List<Menu> menus2 = menuManager.getEnabledCustomRootMenuByCompany();
		for(Menu menu :menus2){
			node = new ZTreeNode(menu.getId().toString()+"_CUSTOM","SYSTEM_CUSTOM",getInternation(menu.getName()), "true", "false", "", "", "folder", "");
			treeNodes.add(node);
		}
		renderText(JsonParser.object2Json(treeNodes));
		return null;
	}
	
	/**
	 * 数据表管理的系统树
	 */
	@Action("data-table-dataTableTree")
	public String dataTableTree() throws Exception {
		List<Menu> menus = menuManager.getEnabledRootMenuByCompany();
		ZTreeNode node = null;
		List<ZTreeNode> treeNodes = new ArrayList<ZTreeNode>();
		java.util.Collections.sort(menus);
		for(Menu menu :menus){
			node = new ZTreeNode(menu.getId().toString(),"0",getInternation(menu.getName()), "true", "false", "", "", "folder", "");
			treeNodes.add(node);
		}
		if(!menus.isEmpty()&&"formView".equals(showTreeType)){
			node = new ZTreeNode("deleted_form_view","0",Struts2Utils.getText("formManager.deleteForm"), "true", "false", "", "", "folder", "");
			treeNodes.add(node);
		}
		renderText(JsonParser.object2Json(treeNodes));
		return null;
	}
	/**
	 * 删除数据表字段
	 * @return
	 * @throws Exception
	 */
	@Action("delete-table-column")
	public String deleteTableColumn() throws Exception {
		tableColumnManager.deleteTableColumn(id);
		ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("mms.dataTableManager"), 
				ApiFactory.getBussinessLogService().getI18nLogInfo("mms.dataTableDelField"), 
				ContextUtils.getSystemId("mms"));
		String callback=Struts2Utils.getParameter("callback");
		this.renderText(callback+"({msg:'"+Struts2Utils.getText("form.delete.success")+"'})");//删除成功
		return null;
	}

	/**
	 * 导出数据表及字段信息
	 * @return
	 * @throws Exception
	 */
	@Action("export-data-table")
	public String exportDataTable() throws Exception{
		HttpServletResponse response = ServletActionContext.getResponse();
		response.reset();
		response.setContentType("application/x-download");
		Menu menu=menuManager.getMenu(menuId);
		String name="data-table";
		if(menu!=null)name=name+"-"+menu.getCode();
		response.addHeader("Content-Disposition", "attachment;filename="+URLEncoder.encode(name+".xls","UTF-8"));
		dataHandle.exportDataTable(response.getOutputStream(),tableIds,menuId);
		ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("mms.dataTableManager"), 
				ApiFactory.getBussinessLogService().getI18nLogInfo("mms.dataTableImport"), 
				ContextUtils.getSystemId("mms"));
		return null;
	}
	@Action("show-import-data-table")
	public String showImportDataTable() throws Exception{
		return "show-import-data-table";
	}
	/**
	 * 导入数据表及字段信息
	 * @return
	 * @throws Exception
	 */
	@Action("import-data-table")
	public String importDataTable() throws Exception{
		String result = "";
		try {
			result = ApiFactory.getDataImporterService().importData(file, fileName,importDataTableManager);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		renderText(result);
		return null;
	}
	
	@Action("data-table-selectParent")
	public String selectParent()throws Exception{
		if(dataTables.getPageSize()>1){
			dataTableManager.getSystemAllDataTables(dataTables, menuId,tableId);
			this.renderText(PageUtils.pageToJson(dataTables));
			return null;
		}
		return "data-table-selectParent";
	}
	
	@Action("generate-code")
	public String generateCode() throws Exception{
		try {
			HttpServletResponse response = Struts2Utils.getResponse();
			response.reset();
			response.setCharacterEncoding("utf-8");
			response.setContentType("application/x-download");
			response.addHeader("Content-Disposition", "attachment;filename="+URLEncoder.encode("generate-code.zip","utf-8"));
			OutputStream fileOut=response.getOutputStream();
			generateCodeZip(fileOut,TemplateRender.GENERATE_DIR);
			if(fileOut!=null)fileOut.close();
			FileUtils.deleteDirectory(new File(TemplateRender.GENERATE_DIR));//删除文件夹
		}catch (Exception e) {
			e.printStackTrace(); 
		}
		return null;
	}
	
	@Action("validate-generate-code")
	public String validateGenerateCode()throws Exception{
		String sign="";
		String[] tempIds=ids.split(",");
		for(String dataTableId:tempIds){
			DataTable dt=dataTableManager.getDataTable(Long.valueOf(dataTableId));
			if(DataState.DRAFT.equals(dt.getTableState())){
				sign+="数据表名称为 "+dt.getName()+" 的数据表是草稿状态\n";
			}else{
				List<TableColumn> tcs=tableColumnManager.getTableColumnByDataTableId(Long.valueOf(dataTableId));
				if(tcs==null||tcs.size()==0){
					sign+="数据表名称为 "+dt.getName()+" 中字段信息中没有字段\n";
				}
			}
		}
		if(StringUtils.isNotEmpty(sign)){
			this.renderText(sign);
		}else{
			this.renderText("ok");
		}
		return null;
	}
	
	private void generateCodeZip(OutputStream fileOut,String filePath) throws Exception{
		boolean mypopupable = false;
		if("popup".equals(inputShowType)){
			mypopupable = true;
		}
		List<DataTable> dataTableList=new ArrayList<DataTable>();//含有继承关系的数据表
		String[] tempIds=ids.split(",");
		for(String dataTableId:tempIds){
			boolean processFlag=false;//表示是否走流程
			GenerateSetting setting = dataTableManager.getGenerateSettingByTable(Long.valueOf(dataTableId));
			String workflowCode="";
			if(setting!=null){
				processFlag=setting.getFlowable();
				workflowCode=setting.getWorkflowCode();
			}
			dataTable=dataTableManager.getDataTable(Long.valueOf(dataTableId));
			if(StringUtils.isNotEmpty(dataTable.getParentName())){
				dataTableList.add(dataTable);
			}
			Map<String, Object> dataModel= new HashMap<String, Object>();
			if(setting==null||setting.getEntitative()){//如果对应数据表没有设置代码生成配置 ，或者生成代码配置里设置生成实体，则生成实体代码
				dataModel =dataTableManager.generateEntity(dataTable,processFlag,false);
				TagUtil.generateFile(dataModel, dataModel.get("filePath").toString(), dataModel.get("fileName").toString(), dataModel.get("templateName").toString());
			}
			dataModel = dataTableManager.generateDao(dataTable);
			TagUtil.generateFile(dataModel, dataModel.get("filePath").toString(), dataModel.get("fileName").toString(), dataModel.get("templateName").toString());
			dataModel = dataTableManager.generateService(dataTable,processFlag);
			TagUtil.generateFile(dataModel, dataModel.get("filePath").toString(), dataModel.get("fileName").toString(), dataModel.get("templateName").toString());
			dataModel=dataTableManager.generateAction(dataTable,processFlag,menuId,workflowCode,logFlag);
			TagUtil.generateFile(dataModel, dataModel.get("filePath").toString(), dataModel.get("fileName").toString(), dataModel.get("templateName").toString());
			dataModel = getHeaderModel();
			TagUtil.generateFile(dataModel, "menus/", "header.jsp", "generateHeader.ftl");
			dataModel=dataTableManager.generateList(dataTable,processFlag,menuId,workflowCode,mypopupable,menuChangeType);
			TagUtil.generateFile(dataModel, dataModel.get("filePath").toString(), dataModel.get("fileName").toString(), dataModel.get("templateName").toString());
			dataModel=dataTableManager.generateInput(dataTable,processFlag,menuId,workflowCode,mypopupable);
			TagUtil.generateFile(dataModel, dataModel.get("filePath").toString(), dataModel.get("fileName").toString(), dataModel.get("templateName").toString());
			dataModel=dataTableManager.generateEditableList(dataTable,processFlag);
			TagUtil.generateFile(dataModel, dataModel.get("filePath").toString(), dataModel.get("fileName").toString(), dataModel.get("templateName").toString());
			dataModel=dataTableManager.generateTask(dataTable,processFlag,mypopupable);
			if(processFlag){
				TagUtil.generateFile(dataModel, dataModel.get("filePath").toString(), dataModel.get("fileName").toString(), dataModel.get("templateName").toString());
				TagUtil.generateFile(dataModel, "jsp/"+dataModel.get("nameSpace").toString()+"/", dataModel.get("lowCaseEntityName").toString()+"-history.jsp", "generateHistory.ftl");
			}
			TagUtil.generateFile(dataModel, "jsp/"+dataModel.get("nameSpace").toString()+"/", dataModel.get("lowCaseEntityName").toString()+"-view.jsp", "generateView.ftl");
		}
		if(dataTableList.size()>0){
			for(DataTable dt:dataTableList){
				boolean processFlag=false;//表示是否走流程
				GenerateSetting setting = dataTableManager.getGenerateSettingByTable(dt.getParentId());
				if(setting!=null){
					processFlag=setting.getFlowable();
				}
				dataTable=dataTableManager.getDataTable(dt.getParentId());
				if(StringUtils.isNotEmpty(dataTable.getParentName())){
					dataTableList.add(dataTable);
				}
				Map<String, Object> dataModel= new HashMap<String, Object>();
				dataModel =dataTableManager.generateEntity(dataTable,processFlag,true);
				TagUtil.generateFile(dataModel, dataModel.get("filePath").toString(), dataModel.get("fileName").toString(), dataModel.get("templateName").toString());
			}
		}
		ZipUtils.zipFolder(filePath, fileOut);
		
	}
	
	private Map<String,Object> getHeaderModel(){
		Map<String,Object> root = new HashMap<String, Object>();
		root.put("menuChangeType", menuChangeType);
		return root;
	}
	/**
	 * swing代码生成工具获得系统列表
	 * @return
	 * @throws Exception
	 */
	@Action("generate-code-system")
	public String getSystemInfo() throws Exception{
		List<com.norteksoft.product.api.entity.BusinessSystem> systems = ApiFactory.getAcsService().getSystemsWithoutPlateSystem();
		this.renderText(JsonParser.object2Json(systems));
		return null;
	}
	/**
	 * swing代码生成工具获得数据表列表
	 * @return
	 * @throws Exception
	 */
	@Action("generate-code-dataTable")
	public String getDataTableInfo() throws Exception{
		Long companyId = getCompanyId();
		StringBuilder dataTables = new StringBuilder("[");
		if(companyId!=null){
			Menu menu = menuManager.getRootMenuByCode(systemCode);
			List<DataTable> list = dataTableManager.getEnabledStandardDataTableByMenuId(menu.getId());
			for(DataTable table:list){
				dataTables.append("{\"name\":\"")
				.append(table.getName())
				.append("\",\"id\":\"")
				.append(table.getId())
				.append("\",\"alias\":\"")
				.append(table.getAlias())
				.append("\"},");
			}
			if(dataTables.lastIndexOf(",")==dataTables.length()-1){//去掉最后一个斜线
				dataTables.replace(dataTables.length()-1, dataTables.length(), "");
			}
		}
		dataTables.append("]");
		this.renderText(dataTables.toString());
		return null;
	}
	
	private Long getCompanyId(){
		Long companyId = null;
		if(StringUtils.isEmpty(companyCode)){
			List<Company> list =ApiFactory.getAcsService().getAllCompanys();
			if(list.size()>0)companyId = list.get(0).getId();
		}else{
			Company company = ApiFactory.getAcsService().getCompanyByCode(companyCode);
			if(company!=null)companyId = company.getId();
		}
		if(companyId!=null){
			ThreadParameters parameters = new ThreadParameters(companyId);
			ParameterUtils.setParameters(parameters);
		}
		return companyId;
	}
	
	/**
	 * swing代码生成工具点击生成代码后将生成的代码放在imatrix服务器端
	 * @return
	 * @throws Exception
	 */
	@Action("generate-code-generateFile")
	public String generateCodeGenerateFile() throws Exception{
		String path=getGenerateDir();
		File folder = new File(path);
		if(!folder.exists()){
			folder.mkdir();
		}
		Long companyId = getCompanyId();
		if(companyId!=null){
			Menu menu = menuManager.getRootMenuByCode(systemCode);
			if(menu!=null){
				String fileName = path+UUID.randomUUID().toString()+".zip";
				menuId = menu.getId();
				File file = new File(fileName);
				OutputStream outStream = null;
				outStream = new FileOutputStream(file); 
				generateCodeZip(outStream,TemplateRender.GENERATE_DIR);
				if(outStream!=null)outStream.close();
				cleanTempFile(new File(TemplateRender.GENERATE_DIR));//删除文件夹
				this.renderText(fileName);
				return null;
			}
		}
		this.renderText("false");
		return null;
	}
	
	//清除暂时文件夹中的文件
	private static void cleanTempFile(File file) throws Exception{
		File[] oldFiles = file.listFiles();
		for(File oldFile:oldFiles){
			if(oldFile.isDirectory()){
				FileUtils.deleteDirectory(oldFile);
			}
		}
	}
	
	private String getGenerateDir(){
		String path=FormViewManager.class.getClassLoader().getResource("application.properties").getPath();
		path=path.substring(1, path.indexOf("WEB-INF/classes"))+TemplateRender.GENERATE_DIR;
		return path;
	}
	
	/**
	 * swing代码生成工具点击生成代码后将imatrix服务器端生成的代码拷贝到swing客户端所在的机器中
	 * @return
	 * @throws Exception
	 */
	@Action("generate-code-postFile")
	public String generateCodePostFile() throws Exception{
		if(StringUtils.isNotEmpty(filePath)){
			OutputStream fileOut=null;
			BufferedInputStream bis=null;
			try{
				bis=new BufferedInputStream(new FileInputStream(filePath));
				HttpServletResponse response = Struts2Utils.getResponse();
				response.setContentType("application/zip");
				fileOut=response.getOutputStream();
				byte[] content=new byte[4*1024];
				while(bis.read(content) != -1){ 
					fileOut.write(content);
				}
				
			}catch (Exception e) {
				e.printStackTrace();
			}finally{
				if(bis!=null)bis.close();
				if(fileOut!=null)fileOut.close();
			}
			File file = new File(filePath);
			file.delete();//删除文件夹
		}
		return null;
	}
	/**
	 * swing工具点击生成元数据时，验证是否创建了租户
	 * @return
	 * @throws Exception
	 */
	@Action("validate-company")
	public String validateCompany() throws Exception{
		Long companyId = getCompanyId();
		if(companyId==null){
			this.renderText("fail");//请先创建租户
		}else{
			this.renderText("ok");
		}
		return null;
	}
	/**
	 * swing工具点击生成元数据时，验证是否创建systemCode系统和菜单
	 * @return
	 * @throws Exception
	 */
	@Action("validate-system-menu")
	public String validateSystemMenu() throws Exception{
		if(StringUtils.isNotEmpty(systemCode)){
			BusinessSystem system = ApiFactory.getAcsService().getSystemByCode(systemCode);
			if(system==null){
				this.renderText("noSystem");//系统不存在
				return null;
			}else{
				getCompanyId();//将公司id放入线程中
				Menu menu = menuManager.getRootMenuByCode(systemCode);
				if(menu==null){
					this.renderText("noMenu");//菜单不存在
					return null;
				}else{
					this.renderText("ok");//系统和菜单都存在
					return null;
				}
			}
		}else{
			this.renderText("noSystemCode");//系统编码为空
			return null;
		}
	}
	/**
	 * swing工具点击生成元数据将WEB-INF路径下的所有文件传到iMatrix服务器端
	 * @return
	 * @throws Exception
	 */
	@Action("generate-basic-data-postFile")
	public String postWebFile() throws Exception{
		if(StringUtils.isNotEmpty(filePath)){
			Map<String,String> libClassPaths = Util.getLibAndClassPath(filePath);
			Util.classPath = libClassPaths.get("classes");
			Util.libPath = libClassPaths.get("lib");
			
			URLClassLoader classLoader = (URLClassLoader)Thread.currentThread().getContextClassLoader();
			
			MyClassLoader myClassLoader = new MyClassLoader( classLoader.getURLs(), classLoader ); 
			Util._loadClassInJars(myClassLoader);
			Long companyId = getCompanyId();//将公司id放入线程中
			Menu menu = menuManager.getRootMenuByCode(systemCode);
			menuId = menu.getId();
			//filePath:项目工程路径/web目录/WEB-INF
			//工具中的“系统编码”不为空 或 “系统编码”为空,则表示系统的编码由表名获得，表名规则：系统编码_....
			dataTableManager.loadDataTableClass(companyId,menuId,myClassLoader,filePath);
			dataTableManager.loadActionClass(filePath, systemCode, myClassLoader);
			this.renderText("ok");//生成成功
		}
		return null;
	}
	public DataTable getModel() {
		return dataTable;
	}
	 public String getInternation(String code){
		 return menuManager.getNameToi18n(code);
	 }
	@Autowired
	public void setDataTableManager(DataTableManager dataTableManager) {
		this.dataTableManager = dataTableManager;
	}

	@Autowired
	public void setTableColumnManager(TableColumnManager tableColumnManager) {
		this.tableColumnManager = tableColumnManager;
	}
	@Autowired
	public void setSheetManager(SheetManager sheetManager) {
		this.sheetManager = sheetManager;
	}

	public void setDataTables(Page<DataTable> dataTables) {
		this.dataTables = dataTables;
	}

	public Page<DataTable> getDataTables() {
		return dataTables;
	}

	public void setTableId(Long tableId) {
		this.tableId = tableId;
	}

	public Long getTableId() {
		return tableId;
	}

	public void setTableIds(List<Long> tableIds) {
		this.tableIds = tableIds;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public List<TableColumn> getColumns() {
		return columns;
	}

	public void setColumns(List<TableColumn> columns) {
		this.columns = columns;
	}

	public String getStates() {
		return states;
	}

	public void setStates(String states) {
		this.states = states;
	}

	public boolean isCanChange() {
		return canChange;
	}
	public void setCanChange(boolean canChange) {
		this.canChange = canChange;
	}
	public Long getMenuId() {
		return menuId;
	}
	public void setMenuId(Long menuId) {
		this.menuId = menuId;
	}
	public void setFile(File file) {
		this.file = file;
	}
	public DataTable getDataTable() {
		return dataTable;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public void setFileFileName(String fileName) {
		this.fileName = fileName;
	}
	public void setDeleteEnable(boolean deleteEnable) {
		this.deleteEnable = deleteEnable;
	}
	public String getIds() {
		return ids;
	}
	public void setIds(String ids) {
		this.ids = ids;
	}
	public String getShowTreeType() {
		return showTreeType;
	}
	public void setShowTreeType(String showTreeType) {
		this.showTreeType = showTreeType;
	}
	public void setSystemCode(String systemCode) {
		this.systemCode = systemCode;
	}
	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public void setLogFlag(String logFlag) {
		this.logFlag = logFlag;
	}
	public void setInputShowType(String inputShowType) {
		this.inputShowType = inputShowType;
	}
	public void setMenuChangeType(String menuChangeType) {
		this.menuChangeType = menuChangeType;
	}
}
