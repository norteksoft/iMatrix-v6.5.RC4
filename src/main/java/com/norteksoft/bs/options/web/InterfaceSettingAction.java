package com.norteksoft.bs.options.web;


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
import com.norteksoft.bs.options.entity.DatasourceSetting;
import com.norteksoft.bs.options.entity.InterfaceSetting;
import com.norteksoft.bs.options.service.DatasourceSettingManager;
import com.norteksoft.bs.options.service.InterfaceSettingManager;
import com.norteksoft.mms.module.entity.Menu;
import com.norteksoft.mms.module.service.MenuManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.JsonParser;
import com.norteksoft.product.util.PageUtils;
import com.norteksoft.product.util.tree.ZTreeNode;
import com.norteksoft.product.web.struts2.CrudActionSupport;
import com.norteksoft.product.web.struts2.Struts2Utils;

/**
 * 数据源配置
 * @author Administrator
 *
 */
@Namespace("/options")
@ParentPackage("default")
@Results( { @Result(name = CrudActionSupport.RELOAD, location = "import-definition", type = "redirectAction")})
public class InterfaceSettingAction extends CrudActionSupport<InterfaceSetting> {
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private InterfaceSetting interfaceSetting;
	private Page<InterfaceSetting> page=new Page<InterfaceSetting>(0,true);
	private String ids;
	private String interfaceCode;
	private static final String SUCCESS_MESSAGE_LEFT = "<font class=\"onSuccess\"><nobr>";
	private static final String MESSAGE_RIGHT = "</nobr></font>";
	private static final String ERROR_MESSAGE_LEFT = "<font class=\"onError\"><nobr>";
	
	private List<DatasourceSetting> datasources;
	
	protected void addErrorMessage(String message){
		this.addActionMessage(ERROR_MESSAGE_LEFT+message+MESSAGE_RIGHT);
	}
	protected void addSuccessMessage(String message){
		this.addActionMessage(SUCCESS_MESSAGE_LEFT+message+MESSAGE_RIGHT);
	}
	
	@Autowired
	private InterfaceSettingManager interfaceSettingManager;
	@Autowired
	private DatasourceSettingManager datasourceSettingManager;
	@Autowired
	private BusinessSystemManager businessSystemManager;
	@Autowired
	private MenuManager menuManager;
	
	@Override
	@Action("interface-setting-delete")
	public String delete() throws Exception {
		interfaceSettingManager.delete(ids);
		this.addSuccessMessage(Struts2Utils.getText("basicSetting.deleteSuccess"));
		ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("bs.interfaceManagement"), ApiFactory.getBussinessLogService().getI18nLogInfo("bs.deleteCommonInterface"), ContextUtils.getSystemId("bs"));
		return "interface-list-data";
	}

	@Override
	@Action("interface-setting-input")
	public String input() throws Exception {
		ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("bs.interfaceManagement"), ApiFactory.getBussinessLogService().getI18nLogInfo("bs.commonInterfacePage"), ContextUtils.getSystemId("bs"));
		return "interface-input";
	}
	public void prepareView() throws Exception {
		prepareModel();
		DatasourceSetting ds = datasourceSettingManager.getDatasourceSetting(interfaceSetting.getDataSourceId());
		if(ds!=null){
			interfaceSetting.setDatasourceCode(ds.getCode());
		}
	}
	@Action("interface-setting-view")
	public String view() throws Exception {
		ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("bs.interfaceManagement"), ApiFactory.getBussinessLogService().getI18nLogInfo("bs.viewCommonInterface"), ContextUtils.getSystemId("bs"));
		return "interface-view";
	}

	@Override
	@Action("interface-setting-list-data")
	public String list() throws Exception {
		if(page.getPageSize()>1){
			ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("bs.interfaceManagement"), ApiFactory.getBussinessLogService().getI18nLogInfo("bs.commonInterfaceList"), ContextUtils.getSystemId("bs"));
			interfaceSettingManager.getInterfaceSettingPage(page);
			this.renderText(PageUtils.pageToJson(page));
			return null;
		}
		return "interface-list-data";
	}
	@Override
	protected void prepareModel() throws Exception {
		if(id==null){
			interfaceSetting=new InterfaceSetting();
			interfaceSetting.setCreatorName(ContextUtils.getUserName());
		}else{
			interfaceSetting=interfaceSettingManager.getInterfaceSetting(id);
		}
		datasources = datasourceSettingManager.getAllDatasourceSettings();
	}
	/**
	 * 验证数据源编码是否存在
	 * @return
	 * @throws Exception
	 */
	@Action("interface-setting-validate-code")
	public String validateCode() throws Exception{
		boolean isExist=interfaceSettingManager.isDatasourceExist(interfaceCode,id);
		if(isExist){//存在
			this.renderText("true");
		}else{
			this.renderText("false");
		}
		return null;
	}

	@Override
	@Action("interface-setting-save")
	public String save() throws Exception {
		interfaceSettingManager.saveInterfaceSetting(interfaceSetting);
		this.addSuccessMessage(Struts2Utils.getText("form.save.success"));
		ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("bs.interfaceManagement"), ApiFactory.getBussinessLogService().getI18nLogInfo("bs.saveCommonInterface"), ContextUtils.getSystemId("bs"));
		return "interface-input";
	}
	/**
	 * 启用/禁用普通接口
	 * @return
	 * @throws Exception
	 */
	@Action("interface-setting-deploy")
	public String changeInterfaceState() throws Exception {
		this.renderText(interfaceSettingManager.changeInterfaceState(ids));
		ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("bs.interfaceManagement"), ApiFactory.getBussinessLogService().getI18nLogInfo("bs.controlCommonInterface"), ContextUtils.getSystemId("bs"));
		return null;
	}
	/**
	 * 左侧的三级菜单定时和接口管理下的树
	 * @return
	 */
	@Action("interface-setting-tree")
	public String createInterfaceTree()throws Exception {
//		List<BusinessSystem> businessSystems= businessSystemManager.getAllBusiness();
		List<ZTreeNode> treeNodes = new ArrayList<ZTreeNode>();
		ZTreeNode root = new ZTreeNode("_interface","0", Struts2Utils.getText("interfaceManager.ordinaryInterface"), "false", "false", "", "", "folder", "");
		treeNodes.add(root);
		root = new ZTreeNode("_job_time","0", Struts2Utils.getText("interfaceManager.timerInterface"), "true", "false", "", "", "folder", "");
		treeNodes.add(root);
		
//		for(BusinessSystem system :businessSystems){
//			root = new ZTreeNode(system.getId().toString(),"_job_time", menuManager.getNameToi18n(system.getName()), "true", "false", "", "", "folder", "");
//			treeNodes.add(root);
//		}
		List<Menu> menus = menuManager.getAllEnabledStandardRootMenus();
		for(Menu menu :menus){
			root = new ZTreeNode(menu.getSystemId().toString(),"_job_time", menuManager.getNameToi18n(menu.getName()), "true", "false", "", "", "folder", "");
			treeNodes.add(root);
		}
		renderText(JsonParser.object2Json(treeNodes));
		return null;
	}

	public InterfaceSetting getModel() {
		return interfaceSetting;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public InterfaceSetting getInterfaceSetting() {
		return interfaceSetting;
	}

	public void setInterfaceSetting(InterfaceSetting interfaceSetting) {
		this.interfaceSetting = interfaceSetting;
	}

	public Page<InterfaceSetting> getPage() {
		return page;
	}

	public void setPage(Page<InterfaceSetting> page) {
		this.page = page;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

	public void setInterfaceCode(String interfaceCode) {
		this.interfaceCode = interfaceCode;
	}
	public List<DatasourceSetting> getDatasources() {
		return datasources;
	}
	
}
