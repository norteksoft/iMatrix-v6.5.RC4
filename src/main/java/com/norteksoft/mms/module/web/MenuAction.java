package com.norteksoft.mms.module.web;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;

import com.norteksoft.mms.base.data.DataHandle;
import com.norteksoft.mms.form.enumeration.MenuType;
import com.norteksoft.mms.module.entity.Menu;
import com.norteksoft.mms.module.service.MenuManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.enumeration.DataState;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.JsonParser;
import com.norteksoft.product.util.SystemUrls;
import com.norteksoft.product.util.tree.ZTreeNode;
import com.norteksoft.product.web.struts2.CrudActionSupport;
import com.norteksoft.product.web.struts2.Struts2Utils;
@Namespace("/module")
@ParentPackage("default")
@Results( { @Result(name = CrudActionSupport.RELOAD, location = "http://www.baidu.com", type = "redirectAction")})

public class MenuAction extends CrudActionSupport<Menu>{
	
	private static final long serialVersionUID = 1L;
	
	private Page<Menu> page = new Page<Menu>(Page.EACH_PAGE_TWENTY,true);
	
	private Long menuId;
	
	private Long parentMenuId;
	
	private Menu menu;
	
	private MenuManager menuManager;
	
	private DataHandle dataHandle;
	
	private Long choseSystemId ;
	
	private String isCreateSystem;
	
	private String parentMenuName;
	
	private String importType;
	private File file;
	private String fileName;
	private String msgs;
	private String targetId;
	private String moveType;
	private String currentId;
	private String menuCode;
	private Boolean placeholderSign=false;//占位符标识,true是占位符,false不是占位符
	private Boolean selectPlaceholderSign=true;//选择占位符标识,true可以选择占位符,false不可以选择占位符

	public String getCurrentId() {
		return currentId;
	}

	public void setCurrentId(String currentId) {
		this.currentId = currentId;
	}

	public String getMsgs() {
		return msgs;
	}

	public void setMsgs(String msgs) {
		this.msgs = msgs;
	}

	public String getTargetId() {
		return targetId;
	}

	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}

	public Menu getMenu() {
		return menu;
	}

	public void setMenu(Menu menu) {
		this.menu = menu;
	}

	public String getMoveType() {
		return moveType;
	}

	public void setMoveType(String moveType) {
		this.moveType = moveType;
	}

	public MenuManager getMenuManager() {
		return menuManager;
	}

	@Autowired
	public void setMenuManager(MenuManager menuManager) {
		this.menuManager = menuManager;
	}

	@Autowired
	public void setDataHandle(DataHandle dataHandle) {
		this.dataHandle = dataHandle;
	}
	
	public void prepareDelete() throws Exception {
		this.prepareModel();
	}
	@Override
	@Action("menu-delete")
	public String delete() throws Exception {
		ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("mms.menuManagement"), 
				ApiFactory.getBussinessLogService().getI18nLogInfo("mms.menuDelete"), 
				ContextUtils.getSystemId("mms"));
		if(menu.getLayer()==1&&menu.getEnableState().equals(DataState.ENABLE)){
			this.renderText("false");
		}else{
			this.renderText(menuManager.deleteMenu(menu));
		}
		return null;
	}
	@Override
	@Action("menu-input")
	public String input() throws Exception {
		ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("mms.menuManagement"), 
				ApiFactory.getBussinessLogService().getI18nLogInfo("mms.menuForm"), 
				ContextUtils.getSystemId("mms"));
		if(!(menuId==null || menuId.intValue()==0)){
			if(MenuType.PLACEHOLDER.equals(menu.getType()))placeholderSign=true;
		}
		if(menu.getEnableState().equals(DataState.ENABLE)){
			if(menu!=null&&menu.getId()==null){
				if(placeholderSign){
					menu.setCode("placeholder_"+UUID.randomUUID());
				}else{
					menu.setCode("menu_code_"+UUID.randomUUID());
				}
			}
			this.renderText("false");
		}else{
			if(menu!=null&&menu.getId()==null){
				if(placeholderSign){
					menu.setCode("placeholder_"+UUID.randomUUID());
				}else{
					menu.setCode("menu_code_"+UUID.randomUUID());
				}
			}
			this.renderText("success");
			parentMenuName = menu.getParent()==null?"":getInternation(menu.getParent().getName());
			return "menu-input";
		}
		return null;
	}
	@Override
	@Action("menu")
	public String list() throws Exception {
		ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("mms.menuManagement"), 
				ApiFactory.getBussinessLogService().getI18nLogInfo("mms.showMenu"), 
				ContextUtils.getSystemId("mms"));
		return "menu";
	}
	
	@Override
	@Action("menu-save")
	public String save() throws Exception {
		ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("mms.menuManagement"), 
				ApiFactory.getBussinessLogService().getI18nLogInfo("mms.saveMenu"), 
				ContextUtils.getSystemId("mms"));
		String msg = uniqueMenu();
		if(msg.equals("true")){
			uploadIcon();
			if(choseSystemId!=null){
				menu.setSystemId(choseSystemId);
				menu.setCurrentSystemId(choseSystemId);
				menu.setType(MenuType.STANDARD);
			}
			if(placeholderSign){
				menu.setType(MenuType.PLACEHOLDER);
			}
//			else{
//				menu.setType(null);//修改菜单后，该值会被设为null，导致一级菜单无法显示。
//			}
			if(menu.getEnableState().equals(DataState.ENABLE)){
				List<Menu> mList = new ArrayList<Menu>();
			
				menuManager.getMenuParents(mList,menu);
				String data = "";
				for(Menu m : mList){
					m.setEnableState(DataState.ENABLE);
					setMenuEvent(m);
					menuManager.saveMenu(m);
					data += m.getId()+"="+getInternation(m.getName())+"("+this.getText(m.getEnableState().code)+")"+",";
				}
				this.renderText("enable:"+data.substring(0,data.length()-1)+":"+menu.getId()+"-"+getInternation(menu.getName())+"("+this.getText(menu.getEnableState().code)+")");
			}else{
				setMenuEvent(menu);
			    menuManager.saveMenu(menu);
			    this.renderText(menu.getId()+":"+getInternation(menu.getName())+"("+this.getText(menu.getEnableState().code)+")");
			}
			
		}else{
			this.renderText("msg:" + msg);
		}
		return null;
	}
	
	//给menu事件属性添加参数menuId
	private void setMenuEvent(Menu m) {
		if(m.getEvent()!=null && m.getEvent()!=""){
			String [] s = m.getEvent().split("'");
			if(s.length>1){
				String event = "";
				if(s[1].indexOf("menuId")<0){
					if(s[1].indexOf("?")>0){
						s[1]=s[1]+"&menuId="+m.getId();
					}else{
						s[1]=s[1]+"?menuId="+m.getId();
					}
					for (String str : s) {
						event+=str+"'";
					}
					m.setEvent(event.substring(0, event.length()-1));
				}
			}
		}
	}

	private void uploadIcon() throws IOException{
		// 上传菜单标签
		if(StringUtils.isNotEmpty(fileName)){
			String localName = getLocalPath();
			if(StringUtils.isNotEmpty(menu.getIconName())){
				FileUtils.deleteQuietly(new File(localName+menu.getImageUrl()));
			}
			String[] fs = fileName.split("\\.");
			String iconName = (new Date()).getTime()+"."+fs[fs.length-1];
			FileUtils.copyFile(file, new File(localName+iconName));
			menu.setIconName(fileName);
			menu.setImageUrl(iconName);
		}
	}
	
	private String getLocalPath() {
		String localPath = ServletActionContext.getServletContext().getRealPath("/");
		return localPath+"/icons/";
	}
	
	/**
	 * 验证当前菜单的编号和名称是否唯一，如果唯一返回"true"，否则返回消息；
	 */
	private String uniqueMenu(){
		List<Menu> menus = null;
		if(menu.getParent()==null){
			menus = menuManager.getAllRootMenuByCompany();
		}else{
			menus = menu.getParent().getChildren();
		}
		for(Menu m: menus){
			if(!m.getId().equals(menu.getId())){
				if(choseSystemId!=null &&m.getSystemId().equals(choseSystemId)){
					return this.getText("menu.unique.validate.tip.system.created");
				}
				if(getInternation(m.getName()).equals(getInternation(menu.getName()))&&m.getCode().equals(menu.getCode())){
					return this.getText("menu.unique.validate.tip.name.code.used");
				}
				if(getInternation(m.getName()).equals(getInternation(menu.getName()))){
					return this.getText("menu.unique.validate.tip.name.used");
				}
				if(m.getCode().equals(menu.getCode())){
					return this.getText("menu.unique.validate.tip.code.used");
				}
			}
		}
		return "true";
	}
	
	@Action("menu-tree")
	public String list2() throws Exception {
		return SUCCESS;
	}
	
	/**
	 * 表单菜单树
	 * @return
	 * @throws Exception 
	 */
	@Action("menu-menuTree")
	public String menuTree() throws Exception{
		Long id=null;
		List<ZTreeNode> ztreeNodes=new ArrayList<ZTreeNode>();
		List<Menu> menus=new ArrayList<Menu>();
		if(!currentId.matches("^[1-9]\\d*")){
			id=0L;
		}else{
			id=Long.parseLong(currentId);
		}
		if(id.equals(0L)){
			menus = menuManager.getMenus();
		}else{
			menuManager.getMenuByPid(menus,id);
		}
		ZTreeNode zNode=null;
		for(Menu menu :menus){
			Long pid=menu.getParent()==null?null:menu.getParent().getId();
			String name="";
			name=getInternation(menu.getName())+"("+Struts2Utils.getText(menu.getEnableState().getCode())+")";

			if(menu.getChildren()==null||menu.getChildren().isEmpty()){
				zNode=new ZTreeNode(menu.getId().toString(), pid==null?"0":pid.toString(),name,menu.getCode(),"false","menu");
			}else{
				zNode=new ZTreeNode(menu.getId().toString(), pid==null?"0":pid.toString(),name,menu.getCode(),"true","menu");
			}
			if(isSystemMenu(menu)){
				zNode.setIconSkin("system");
				zNode.setType("system");
			}
			ztreeNodes.add(zNode);
		}
		renderText(JsonParser.object2Json(ztreeNodes));
		return null;
	}
	@Action("menu-moveNodes")
	public String moveNodes(){
		menuManager.moveNodes(msgs,targetId,moveType);
		return null;
	}
	private boolean isSystemMenu(Menu menu) {
		if(menu.getParent()==null){
			return true;
		}
		return false;
	}
	public void prepareEnable() throws Exception {
		this.prepareModel();
	}
	/**
	 * 启用菜单
	 */
	@Action("menu-enable")
	public String enable() throws Exception{
		List<Menu> mList = new ArrayList<Menu>();
		menuManager.getMenuParents(mList,menu);
		String data = "";
		for(Menu m : mList){
			m.setEnableState(DataState.ENABLE);
			menuManager.saveMenu(m);
			data += m.getId()+"="+getInternation(m.getName())+"("+this.getText(m.getEnableState().code)+")"+",";
		}
		ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("mms.menuManagement"), 
				ApiFactory.getBussinessLogService().getI18nLogInfo("mms.startMenu"), 
				ContextUtils.getSystemId("mms"));
		this.renderText(data.substring(0,data.length()-1));
		return null;
	}
	
	public void prepareDisableMenu() throws Exception {
		this.prepareModel();
	}
	/**
	 * 禁用用菜单
	 */
	@Action("menu-disableMenu")
	public String disableMenu() throws Exception{
		menu.setEnableState(DataState.DISABLE);
		menuManager.saveMenu(menu);
		ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("mms.menuManagement"), 
				ApiFactory.getBussinessLogService().getI18nLogInfo("mms.undisableMenu"), 
				ContextUtils.getSystemId("mms"));
		this.renderText(getInternation(menu.getName())+"("+this.getText(menu.getEnableState().code)+")");
		return null;
	}
		
	/**
	 * 导出菜单
	 * @return
	 * @throws Exception
	 */
	@Action("export-menu")
	public String exportMenu() throws Exception{
		HttpServletResponse response = ServletActionContext.getResponse();
		response.reset();
		response.setContentType("application/x-download");
		response.addHeader("Content-Disposition", "attachment;filename="+URLEncoder.encode("menu-info.xls","UTF-8"));
		dataHandle.exportMenu(response.getOutputStream());
		ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("mms.menuManagement"), 
				ApiFactory.getBussinessLogService().getI18nLogInfo("mms.exportMenu"), 
				ContextUtils.getSystemId("mms"));

		return null;
	}
	@Action("show-import-menu")
	public String showImportMenu() throws Exception{
		return "show-import-menu";
	}
	/**
	 * 导入菜单
	 * @return
	 * @throws Exception
	 */
	@Action("import-menu")
	public String importMenu() throws Exception{
		if(fileName==null || !fileName.endsWith(".xls")){
			this.addActionMessage(Struts2Utils.getText("menuManager.excelValidate"));
			return "show-import-menu";
		}
		boolean success = true;
		try {
			ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("mms.menuManagement"), 
					ApiFactory.getBussinessLogService().getI18nLogInfo("mms.importMenu"), 
					ContextUtils.getSystemId("mms"));
			dataHandle.importMenu(true,file,null);
			dataHandle.importMenu(false,file,null);
		} catch (Exception e) {
			success = false;
		}
		if(success){
			this.addActionMessage(Struts2Utils.getText("menuManager.importSuccess"));
		}else{
			this.addActionMessage(Struts2Utils.getText("menuManager.importFail"));
		}
		return "show-import-menu";
	}
	@Action("update-url-cache")
	public String updateUrlCache() throws Exception{
		SystemUrls.updateUrls();
		ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("mms.menuManagement"), 
				ApiFactory.getBussinessLogService().getI18nLogInfo("mms.updateUrlCache"), 
				ContextUtils.getSystemId("mms"));
		return null;
	}
	
	/**
	 * 导出自定义系统
	 * @return
	 * @throws Exception
	 */
	@Action("export-custom-system")
	public String exportCustomSystem() throws Exception{
		HttpServletResponse response = Struts2Utils.getResponse();
		response.reset();
		response.setCharacterEncoding("utf-8");
		response.setContentType("application/x-download");
		response.addHeader("Content-Disposition", "attachment;filename="+URLEncoder.encode("custom-system.zip","utf-8"));
		dataHandle.exportCustemSystem(response.getOutputStream(),menuId);
		
		ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("mms.menuManagement"), 
				ApiFactory.getBussinessLogService().getI18nLogInfo("mms.exportCustomSystem"), 
				ContextUtils.getSystemId("mms"));
		return null;
	}
	
	/**
	 * 验证是否是草稿或启用状态的自定义一级菜单
	 * @return
	 * @throws Exception
	 */
	@Action("validate-menu")
	public String validateMenu() throws Exception{
		menu=menuManager.getCustomRootMenuById(menuId);
		if(menu != null){
			this.renderText("ok");
		}else{
			this.renderText("no");
		}
		return null;
	}
	/**
	 * 验证菜单编码是否唯一
	 * @return
	 * @throws Exception
	 */
	@Action("validate-menu-code")
	public String validateMenuCode() throws Exception{
		boolean isExist=menuManager.isMenuCodeExist(menuCode,menuId);
		if(isExist){//存在
			this.renderText("true");
		}else{
			this.renderText("false");
		}
		return null;
	}
	
	/**
	 * 打开导入自定义系统页面
	 * @return
	 * @throws Exception
	 */
	@Action("show-import-custom-system")
	public String showImportCustomSystem() throws Exception{
		
		return "show-import-custom-system";
	}
	
	/**
	 * 导入自定义系统
	 * @return
	 * @throws Exception
	 */
	@Action("import-custom-system")
	public String importCustomSystem() throws Exception{
		dataHandle.importCustomSystem();
		importType="ok";
		return "show-import-custom-system";
	}
	
	/**
	 * 验证导入自定义系统
	 * @return
	 * @throws Exception
	 */
	@Action("validate-import-custom-system")
	public String validateImportCustomSystem() throws Exception{
		String result="";
		if(fileName==null || !fileName.endsWith(".zip")){
			result=Struts2Utils.getText("menuManager.zipInfo");
		}else{
			result=dataHandle.validateImportCustomSystem(file);
		}
		if(StringUtils.isEmpty(result)){
			result="ok";
		}
		this.renderText(result);
		return null;
	}
	
	@Override
	protected void prepareModel() throws Exception {
		if(menuId==null || menuId.intValue()==0){
			menu = new Menu();
		}else{
			menu = menuManager.getMenu(menuId);
		}
		if(parentMenuId!=null && parentMenuId.intValue()!=0){
			menu.setParent(menuManager.getMenu(parentMenuId));
		}
	}	
	 public String getInternation(String code){
		 return menuManager.getNameToi18n(code);
	 }	
	public Menu getModel() {
		return menu;
	}
	public Long getMenuId() {
		return menuId;
	}
	public void setMenuId(Long menuId) {
		this.menuId = menuId;
	}
	public Page<Menu> getPage() {
		return page;
	}
	public void setParentMenuId(Long parentMenuId) {
		this.parentMenuId = parentMenuId;
	}
	public Long getChoseSystemId() {
		return choseSystemId;
	}

	public void setChoseSystemId(Long choseSystemId) {
		this.choseSystemId = choseSystemId;
	}

	public String getIsCreateSystem() {
		return isCreateSystem;
	}

	public void setIsCreateSystem(String isCreateSystem) {
		this.isCreateSystem = isCreateSystem;
	}

	public String getParentMenuName() {
		return parentMenuName;
	}

	public void setParentMenuName(String parentMenuName) {
		this.parentMenuName = parentMenuName;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public void setFileFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getImportType() {
		return importType;
	}

	public void setImportType(String importType) {
		this.importType = importType;
	}

	public String getMenuCode() {
		return menuCode;
	}

	public void setMenuCode(String menuCode) {
		this.menuCode = menuCode;
	}
	
	public Boolean getPlaceholderSign() {
		return placeholderSign;
	}

	public void setPlaceholderSign(Boolean placeholderSign) {
		this.placeholderSign = placeholderSign;
	}

	public Boolean getSelectPlaceholderSign() {
		return selectPlaceholderSign;
	}

	public void setSelectPlaceholderSign(Boolean selectPlaceholderSign) {
		this.selectPlaceholderSign = selectPlaceholderSign;
	}

}
