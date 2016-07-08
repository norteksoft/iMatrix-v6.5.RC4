package com.norteksoft.mms.form.web;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;

import com.norteksoft.mms.base.data.DataHandle;
import com.norteksoft.mms.form.entity.DataTable;
import com.norteksoft.mms.form.entity.GroupHeader;
import com.norteksoft.mms.form.entity.ListView;
import com.norteksoft.mms.form.entity.TableColumn;
import com.norteksoft.mms.form.service.GroupHeaderManager;
import com.norteksoft.mms.form.service.ImportListViewManager;
import com.norteksoft.mms.form.service.ListViewManager;
import com.norteksoft.mms.form.service.TableColumnManager;
import com.norteksoft.mms.module.entity.Menu;
import com.norteksoft.mms.module.service.MenuManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.PageUtils;
import com.norteksoft.product.web.struts2.CrudActionSupport;
import com.norteksoft.product.web.struts2.Struts2Utils;
@Namespace("/form")
@ParentPackage("default")
@Results( { @Result(name = CrudActionSupport.RELOAD, location = "list-view", type = "redirectAction")})
public class ListViewAction extends CrudActionSupport<ListView> {
	private static final long serialVersionUID = 1L;
	
	private static final String SUCCESS_MESSAGE_LEFT = "<font class=\"onSuccess\"><nobr>";
	private static final String MESSAGE_RIGHT = "</nobr></font>";
	private static final String ERROR_MESSAGE_LEFT = "<font class=\"onError\"><nobr>";
	protected void addErrorMessage(String message){
		this.addActionMessage(ERROR_MESSAGE_LEFT+message+MESSAGE_RIGHT);
	}
	protected void addSuccessMessage(String message){
		this.addActionMessage(SUCCESS_MESSAGE_LEFT+message+MESSAGE_RIGHT);
	}
	
	private Page<ListView> page=new Page<ListView>(0,true);
	
	private Long menuId;
	private ListView view;
	private Long viewId;
	private List<DataTable> dataTables;
	private DataTable dataTable;
	private String viewIds;
	private String myCode;
	private String soleCode;
	private Long propertyId;
	private String currentInputId;//格式设置中input的id
	
	private File file;
	private String fileName;
	
	private ListViewManager viewManager;
	private MenuManager menuManager;
	@Autowired
	private ImportListViewManager importListViewManager;
	@Autowired
	private TableColumnManager tableColumnManager;
	@Autowired
	private GroupHeaderManager groupHeaderManager;
	private DataHandle dataHandle;
	private List<TableColumn> tableColumns;
	private boolean deleteEnable = false;
	private Integer frozenColumnAmount;
	private String haveGroupHeader;//yes:表示该列表有表头组合，no:表示该列表没有表头组合
	private String dataTableName;//数据表名称
	
	@Autowired
	public void setMenuManager(MenuManager menuManager) {
		this.menuManager = menuManager;
	}
	@Autowired
	public void setViewManager(ListViewManager viewManager) {
		this.viewManager = viewManager;
	}
	@Autowired
	public void setDataHandle(DataHandle dataHandle) {
		this.dataHandle = dataHandle;
	}
	@Override
	@Action("list-view-delete")
	public String delete() throws Exception {
		String msg=null;
		if(deleteEnable){
			msg=viewManager.deleteEnable(viewIds);
		}else{
			msg=viewManager.delete(viewIds);
		}
		if(msg!=null){
			addSuccessMessage(msg);
		}
		ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("mms.formManager"), 
				ApiFactory.getBussinessLogService().getI18nLogInfo("mms.listviewDelete"), 
				ContextUtils.getSystemId("mms"));
		return list();
	}

	@Override
	@Action("list-view-input")
	public String input() throws Exception {
		tableColumns=tableColumnManager.getTableColumnByDataTableId(view.getDataTable().getId());
		viewId=view.getId();
		frozenColumnAmount=view.getFrozenColumn()==null?0:view.getFrozenColumn();
		
		List<GroupHeader> groupHeaders=groupHeaderManager.getGroupHeadersByViewId(view.getId());
		if(groupHeaders!=null && groupHeaders.size()>0){
			haveGroupHeader="yes";
		}else{
			haveGroupHeader="no";
		}
		
		ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("mms.formManager"), 
				ApiFactory.getBussinessLogService().getI18nLogInfo("mms.listViewForm"), 
				ContextUtils.getSystemId("mms"));
		return "list-view-input";
	}

	@Override
	@Action("list-view-list-data")
	public String list() throws Exception {
		List<Menu> menus = menuManager.getEnabledRootMenuByCompany();
		if(menuId==null&&menus.size()>0){
			menuId = menus.get(0).getId();
		}
		if(menuId!=null){
			if(page.getPageSize()>1){
				if(StringUtils.isNotEmpty(dataTableName)){
					viewManager.getListViewPageByMenu(page, menuId,dataTableName);
				}else{
					viewManager.getListViewPageByMenu(page, menuId);
				}
				ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("mms.formManager"), 
						ApiFactory.getBussinessLogService().getI18nLogInfo("mms.listView"), 
						ContextUtils.getSystemId("mms"));
				this.renderText(PageUtils.pageToJson(page));
				return null;
			}
		}
		return "list-view";
	}

	@Override
	protected void prepareModel() throws Exception {
		if(viewId==null){
			view=new ListView();
		}else{
			view=viewManager.getView(viewId);
		}
	}

	@Override
	@Action("list-view-save")
	public String save() throws Exception {
		viewManager.saveView(view, menuId);
		ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("mms.formManager"), 
				ApiFactory.getBussinessLogService().getI18nLogInfo("mms.saveListView"), 
				ContextUtils.getSystemId("mms"));
		addSuccessMessage(Struts2Utils.getText("form.save.success"));
		return input();
	}
	
	@Action("list-view-delete-property")
	public String deleteJqGridProperty() throws Exception {
		viewManager.deleteJqGridProperty(propertyId);
		ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("mms.formManager"), 
				ApiFactory.getBussinessLogService().getI18nLogInfo("mms.saveCustomProperty"), 
				ContextUtils.getSystemId("mms"));
		String callback=Struts2Utils.getParameter("callback");
		this.renderText(callback+"({msg:'"+Struts2Utils.getText("form.delete.success")+"'})");
		return null;
	}
	
	/**
	 * 格式设置
	 * @return
	 * @throws Exception
	 */
	@Action("list-view-format-setting")
	public String formatSetting() throws Exception {
		return SUCCESS;
	}
	@Action("list-view-copy")
	public String copy() throws Exception{
		view = viewManager.getView(viewId);
		return "copy";
	}
	
	public void prepareSavecopy() throws Exception{
		view = new ListView();
	}
	@Action("list-view-savecopy")
	public String savecopy() throws Exception{
		viewManager.savecopy(viewId, view);
		return null;
	}
	@Action("list-view-validateListFormCode")
	public String validateListFormCode() throws Exception{
		this.renderText(viewManager.isListCodeExist(soleCode,menuId).toString());
		return null;
	}
	@Action("list-view-validateCode")	
	public String validateCode() throws Exception{
		this.renderText(viewManager.isCodeExist(myCode,viewId).toString());
		return null;
	}
	@Action("list-view-defaultDisplaySet")
	public String defaultDisplaySet() throws Exception {
		ListView listView=viewManager.getView(viewId);
		Long tbId=null;
		if(listView!=null&&listView.getDataTable()!=null){
			tbId=listView.getDataTable().getId();
		}
		String before=listView.getDefaultListView()?Struts2Utils.getText("common.yes"):Struts2Utils.getText("common.no");
		if(viewManager.defaultDisplaySet(viewId,tbId)){
			listView=viewManager.getView(viewId);
			String end=listView.getDefaultListView()?Struts2Utils.getText("common.yes"):Struts2Utils.getText("common.no");
			addSuccessMessage(Struts2Utils.getText("pageManager.setDefault")+before+"-->"+end);
		}else{
			addErrorMessage(Struts2Utils.getText("pageManager.dataTableErrorInfo"));
		}
		ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("mms.formManager"), 
				ApiFactory.getBussinessLogService().getI18nLogInfo("mms.setDefaultList"), 
				ContextUtils.getSystemId("mms"));
		return list();
	}
	/**
	 * 远程编辑列表字段信息
	 * @return
	 * @throws Exception
	 */
	@Action("edit-view")
	public String editView() throws Exception {
		view=viewManager.getListViewByCode(myCode);
		return "edit-view";
	}
	
	/**
	 * 导出列表及字段信息
	 * @return
	 * @throws Exception
	 */
	@Action("export-list-view")
	public String exportListView() throws Exception{
		HttpServletResponse response = ServletActionContext.getResponse();
		response.reset();
		response.setContentType("application/x-download");
		Menu menu=menuManager.getMenu(menuId);
		String name="list-view";
		if(menu!=null)name=name+"-"+menu.getCode();
		response.addHeader("Content-Disposition", "attachment;filename="+URLEncoder.encode(name+".xls","UTF-8"));
		dataHandle.exportListView(response.getOutputStream(),viewIds,menuId);
		ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("mms.formManager"), 
				ApiFactory.getBussinessLogService().getI18nLogInfo("mms.exportList"), 
				ContextUtils.getSystemId("mms"));
		return null;
	}
	@Action("show-import-list-view")
	public String showImportListView() throws Exception{
		return "show-import-list-view";
	}
	/**
	 * 导入数据表及字段信息
	 * @return
	 * @throws Exception
	 */
	@Action("import-list-view")
	public String importListView() throws Exception{
		String result = "";
		try {
			result = ApiFactory.getDataImporterService().importData(file, fileName,importListViewManager);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		renderText(result);
		return null;
	}

	public ListView getModel() {
		return view;
	}

	public Page<ListView> getPage() {
		return page;
	}

	public void setPage(Page<ListView> page) {
		this.page = page;
	}
	public List<DataTable> getDataTables() {
		return dataTables;
	}
	public DataTable getDataTable() {
		return dataTable;
	}
	public void setViewIds(String viewIds) {
		this.viewIds = viewIds;
	}
	public Long getViewId() {
		return viewId;
	}
	public void setViewId(Long viewId) {
		this.viewId = viewId;
	}
	public void setMyCode(String myCode) {
		this.myCode = myCode;
	}
	public Long getMenuId() {
		return menuId;
	}
	public void setMenuId(Long menuId) {
		this.menuId = menuId;
	}
	public ListView getView() {
		return view;
	}
	public void setView(ListView view) {
		this.view = view;
	}
	public String getSoleCode() {
		return soleCode;
	}
	public void setSoleCode(String soleCode) {
		this.soleCode = soleCode;
	}
	public Long getPropertyId() {
		return propertyId;
	}
	public void setPropertyId(Long propertyId) {
		this.propertyId = propertyId;
	}
	public String getCurrentInputId() {
		return currentInputId;
	}
	public void setCurrentInputId(String currentInputId) {
		this.currentInputId = currentInputId;
	}
	public void setFile(File file) {
		this.file = file;
	}
	public void setFileFileName(String fileName) {
		this.fileName = fileName;
	}
	public List<TableColumn> getTableColumns() {
		return tableColumns;
	}
	public void setDeleteEnable(boolean deleteEnable) {
		this.deleteEnable = deleteEnable;
	}
	public Integer getFrozenColumnAmount() {
		return frozenColumnAmount;
	}
	public String getHaveGroupHeader() {
		return haveGroupHeader;
	}
	public String getDataTableName() {
		return dataTableName;
	}
	public void setDataTableName(String dataTableName) {
		this.dataTableName = dataTableName;
	}

}
