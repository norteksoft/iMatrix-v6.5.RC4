package com.norteksoft.mms.module.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.acs.entity.authorization.BusinessSystem;
import com.norteksoft.acs.service.AcsUtils;
import com.norteksoft.acs.service.authorization.BusinessSystemManager;
import com.norteksoft.acs.service.authorization.FunctionManager;
import com.norteksoft.bs.options.enumeration.InternationType;
import com.norteksoft.mms.form.entity.DataTable;
import com.norteksoft.mms.form.entity.FormView;
import com.norteksoft.mms.form.enumeration.MenuType;
import com.norteksoft.mms.form.service.DataTableManager;
import com.norteksoft.mms.form.service.FormViewManager;
import com.norteksoft.mms.module.dao.MenuDao;
import com.norteksoft.mms.module.dao.ModulePageDao;
import com.norteksoft.mms.module.entity.Menu;
import com.norteksoft.mms.module.entity.ModulePage;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.enumeration.DataState;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.JsonParser;
import com.norteksoft.product.util.PropUtils;
import com.norteksoft.product.util.SystemUrls;
import com.norteksoft.product.util.TicketUtil;
import com.norteksoft.product.util.tree.ZTreeNode;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

@Service
@Transactional(readOnly=true)
public class MenuManager {
	private static final String MEDIA_TYPE = "text/html;charset=UTF-8";
	private MenuDao menuDao;
	private ModulePageDao modulePageDao;
	private AcsUtils acsUtils;
	@Autowired
	private DataTableManager dataTableManager;
	@Autowired
	private FormViewManager formViewManager;
	@Autowired
	private BusinessSystemManager businessSystemManager;
	@Autowired
	private FunctionManager functionManager;
	@Autowired
	public void setMenuDao(MenuDao menuDao) {
		this.menuDao = menuDao;
	}
	@Autowired
	public void setModulePageDao(ModulePageDao modulePageDao) {
		this.modulePageDao = modulePageDao;
	}
	@Autowired
	public void setAcsUtils(AcsUtils acsUtils) {
		this.acsUtils = acsUtils;
	}
	/**
	 * 保存菜单
	 */
	@Transactional(readOnly=false)
	public void saveMenu(Menu menu){
		if(menu.getId()==null){
			Menu parent = menu.getParent();
			if(menu.getSystemId()==null){
				if(parent!=null){
					//二级菜单系统id
					menu.setSystemId(parent.getSystemId());
					menu.setCurrentSystemId(parent.getSystemId());
				}else{
					//自定义一级菜单
					menu.setType(MenuType.CUSTOM);
					Long systemId = ContextUtils.getSystemId("mms");
					menu.setSystemId(systemId);
					menu.setCurrentSystemId(systemId);
					if(!(StringUtils.isNotBlank(menu.getUrl())&&menu.getUrl().startsWith("http:"))){
						String sysUrl=getSysUrl(ContextUtils.getSystemId("mms"));
						menu.setUrl(sysUrl+"/common/list.htm");
					}
				}
				//自定义非一级菜单的url的设置
				if(parent!=null && !MenuType.PLACEHOLDER.equals(menu.getType())){
					if("#this".equals(menu.getUrl())||StringUtils.isEmpty(menu.getUrl())){
						//标准或自定义系统中的子菜单没设url则为“定义菜单”
						menu.setType(MenuType.CUSTOM);
						//三级和三级以下菜单
						if(parent.getParent()!=null){
							menu.setUrl("/mms/common/list.htm");
						}else{//二级菜单
							menu.setUrl("/mms/common/list.htm");
						}
					}else{
						//子菜单设置了url
						if(parent.getType()==MenuType.STANDARD){
							//标准系统已有子菜单设置为“标准菜单”
							menu.setType(MenuType.STANDARD);
						}else{
							menu.setType(MenuType.CUSTOM);
							if(!menu.getExternalable()){//是否是外部系统，不是外部系统时，则表示是自定义菜单，则需要重新设置url
								//自定义系统中子菜单无论是否设置了url，永远为“自定义菜单”
								menu.setUrl("/mms/common/list.htm");
							}
						}
					}
				}
			}
			menu.setCompanyId(ContextUtils.getCompanyId());
			menu.setCreatedTime(new Date());
			if(parent!=null){
				menu.setLayer(parent.getLayer()+1);
			}
		}
		if(!menu.getLayer().equals(1)&&!MenuType.PLACEHOLDER.equals(menu.getType())){//如果不是一级菜单
			if("/mms/common/list.htm".equals(menu.getUrl())||"/mms/common/list.htm?".equals(menu.getUrl())){
				//当不是一级菜单时，修改了路径为/mms/common/list.htm或/mms/common/list.htm?时，该菜单的类型改为自定义
				menu.setType(MenuType.CUSTOM);
			}else{
				//当不是一级菜单时，修改了路径不为/mms/common/list.htm或/mms/common/list.htm?时，该菜单的类型改为标准
				menu.setType(MenuType.STANDARD);
			}
		}
		menuDao.saveMenu(menu);
	}
	
	/**
	 * 获取菜单
	 */
	public Menu getMenu(Long menuId){
		return menuDao.getMenu(menuId);
	}

	/**
	 * 得到公司所有的一级菜单(只包含订单中选中的所有系统对应的菜单)
	 */
	public List<Menu> getRootMenuByCompany() {
		List<Long> systemIds = businessSystemManager.getSubsciberSystemId();
		return menuDao.getRootMenuByCompany(systemIds);
	}
	/**
	 * 得到公司所有的一级菜单
	 */
	public List<Menu> getAllRootMenuByCompany() {
		return menuDao.getAllRootMenuByCompany();
	}
	
	/**
	 * 得到公司所有启用的一级菜单(在订单中选中的系统)
	 */
	public List<Menu> getEnabledRootMenuByCompany() {
		List<Long> systemIds = businessSystemManager.getSubsciberSystemId();
		return menuDao.getEnabledRootMenuByCompany(systemIds);
	}
	/**
	 * 删除菜单
	 */
	@Transactional(readOnly=false)
	public String deleteMenu(Menu menu) {
		List<DataTable> tables=dataTableManager.getAllDataTablesByMenu(menu.getId());
		if(tables.size()>0){
			return "该菜单已被使用,无法删除";
		}
		List<FormView> formviews=formViewManager.getFormViewsByMenu(menu.getId());
		if(formviews.size()>0){
			return "该菜单已被使用,无法删除";
		}
		menuDao.delete(menu);
		return "success";
	}
	public Menu getRootMenu(Long menuId){
		Menu menu=menuDao.get(menuId);
		return getRootMenu(menu);
	}
	
	private Menu getRootMenu(Menu menu){
		Integer layer = menu.getLayer();
		Menu rootMenu = menu;
		for(int i=layer;i>=1;i--){
			Menu parent = rootMenu.getParent();
			if(parent==null)break;
			rootMenu = parent;
		}
		return rootMenu;
	}
	
	public List<Menu> getEnableMenuByLayer(Integer layer,Long parentId) {
		return menuDao.getEnableMenuByLayer(layer,parentId);
	}
	
	public Menu getDefaultModulePageBySystem(String code, Long companyId) {
		List<Menu> menus=menuDao.getDefaultMenuByLayer(1, code, companyId);
		Menu firstMenu =null;
		if(menus.size()>0){
			firstMenu = menus.get(0);
		}
		Menu secondMenu = (firstMenu==null?null:firstMenu.getFirstChildren());
		return secondMenu==null?null:secondMenu.getFirstChildren();
	}
	/**
	 * 获得最底层菜单
	 * @param systemId
	 * @param companyId
	 * @return
	 */
	@Transactional(readOnly=true)
	public Menu getLastMenu(Menu menu,Menu firstMenu,List<Long> systemIds) {
		if(menu==null)return null;
		BusinessSystem system = businessSystemManager.getBusiness(menu.getSystemId());
		List<Menu> children=menuDao.getChildrenEnabledMenus(menu.getId(),systemIds);
		if(children.size()<=0)return menu;//表示没有二级菜单
		for(Menu m:children){
			if(shouldGetSystem(firstMenu, system)){//是否需要重新获得系统
				system = businessSystemManager.getBusiness(m.getSystemId());
			}
			List<Menu> childrens=menuDao.getChildrenEnabledMenus(m.getId(),systemIds);
			if(isHasAuth(m,firstMenu,system)&&childrens.size()<=0){
				return m;
			}
			Menu authMenu = getChildLastMenu(childrens,firstMenu,system,systemIds);
			if(authMenu!=null)return authMenu;
		}
		return null;
	}
	/**
	 * 获得最底层菜单
	 * @param systemId
	 * @param companyId
	 * @return
	 */
	@Transactional(readOnly=true)
	public Menu getHasAuthLastMenu(Menu menu,Menu firstMenu,List<Long> systemIds) {
		if(menu==null)return null;
		BusinessSystem system = businessSystemManager.getBusiness(menu.getSystemId());
		List<Menu> children=menuDao.getChildrenEnabledMenus(menu.getId(),systemIds);
		if(children.size()<=0)return menu;//表示没有二级菜单
		for(Menu m:children){
			if(shouldGetSystem(firstMenu, system)){//是否需要重新获得系统
				system = businessSystemManager.getBusiness(m.getSystemId());
			}
			if(isHasAuth(m,firstMenu,system)){
				return m;
			}
			List<Menu> childrens=menuDao.getChildrenEnabledMenus(m.getId(),systemIds);
			Menu authMenu = getHasAuthChildLastMenu(childrens,firstMenu,system,systemIds);
			if(authMenu!=null)return authMenu;
		}
		return null;
	}
	
	/**
	 * 判断是否是自定义系统
	 * @param menu
	 * @return true表示是自定义系统，false表示不是自定义系统
	 */
	public boolean isCustomSystem(Menu menu){
		if(menu.getUrl().indexOf("mms/common/list.htm")>=0){
			return true;
		}
		return false;
	}
	/**
	 * 菜单是否有权限
	 * @param menu
	 * @return
	 */
	public boolean isHasAuth(Menu menu,Menu firstMenu,BusinessSystem business){
		if(menu.getExternalable()!=null&&menu.getExternalable())return true;//表示当是外来系统，例如百度等时，不做权限控制直接显示
		if(isCustomSystem(menu)){//当是第1种情况时
			if(ContextUtils.isAuthority("/common/list.htm", "mms")){//表示有自定义系统的权限
				return true;
			}
		}else{
			if(ContextUtils.isAuthority(getMenuUrlWithoutParam(menu,firstMenu,business), business)){
				return true;
			}
		}
		return false;
	}
	
	
	
	private Menu getChildLastMenu(List<Menu> children,Menu firstMenu,BusinessSystem system,List<Long> systemIds){
		for(Menu m:children){
			if(shouldGetSystem(firstMenu, system)){//是否需要重新获得系统
				system = businessSystemManager.getBusiness(m.getSystemId());
			}
			List<Menu> childrens=menuDao.getChildrenEnabledMenus(m.getId(),systemIds);
			if(isHasAuth(m,firstMenu,system)&&childrens.size()<=0){
				return m;
			}
			Menu menu =  getChildLastMenu(childrens,firstMenu,system,systemIds);
			if(menu!=null){
				if(isHasAuth(menu,firstMenu,system)){
					return menu;
				}
			}
		}
		return null;
	}
	private Menu getHasAuthChildLastMenu(List<Menu> children,Menu firstMenu,BusinessSystem system,List<Long> systemIds){
		for(Menu m:children){
			if(shouldGetSystem(firstMenu, system)){//是否需要重新获得系统
				system = businessSystemManager.getBusiness(m.getSystemId());
			}
			if(isHasAuth(m,firstMenu,system)){
				return m;
			}
			List<Menu> childrens=menuDao.getChildrenEnabledMenus(m.getId(),systemIds);
			Menu menu =  getHasAuthChildLastMenu(childrens,firstMenu,system,systemIds);
			if(menu!=null){
				if(isHasAuth(menu,firstMenu,system)){
					return menu;
				}
			}
		}
		return null;
	}
	
	public ModulePage getDefaultModulePageByMenu(Long menuId) {
		List<Long> systemIds = getSubsciberSystemIds();
		Menu menu=menuDao.get(menuId);
		ModulePage defaultPage=null;
		ModulePage page=modulePageDao.getDefaultDisplayPageByMenuId(menuId);
		if(page==null){
			List<ModulePage> pages=modulePageDao.getEnableModulePagesByMenuId(menuId);
			if(pages.size()>0)defaultPage=pages.get(0);
			if(defaultPage==null){
				List<Menu> menus=menuDao.getChildrenEnabledMenus(menu.getId(),systemIds);
				menu= (menus.size()<=0?null:menus.get(0));
				if(menu!=null){
					defaultPage=getDefaultModulePageByMenu(menu.getId());
					if(defaultPage!=null){
						return defaultPage;
					}
				}
			}
		}else{
			defaultPage=page;
		}
		return defaultPage;
	}
	
	/**
	 * 获得菜单menuId中层级为layer的默认选中的菜单
	 */
	public Menu getDefaultSelectMenuByLayer(Integer layer,Long menuId){
		Menu menu=getMenu(menuId);
		if(menu.getLayer()>=layer){//当前菜单的层级大于等于结果需要的层级时
			return getDefaultSelectMenuByUpLayer(layer,menu);
		}else{
			return getDefaultSelectMenuByLowerLayer(layer,menu);
		}
	}
	
	private Menu getDefaultSelectMenuByUpLayer(Integer layer,Menu menu){
		Integer menulayer = menu.getLayer();
		Menu resultMenu = menu;
		for(int i=menulayer;i>layer;i--){
			resultMenu = resultMenu.getParent();
		}
		return resultMenu;
	}
	private Menu getDefaultSelectMenuByLowerLayer(Integer layer,Menu menu){
		Integer menulayer = menu.getLayer();//2 
		Menu resultMenu = menu;
		for(int i=menulayer;i<layer;i++){
			List<Menu> children = resultMenu.getChildren();
			if(children.size()>0)resultMenu = children.get(0);
		}
		return resultMenu;
	}
	
	
	/**
	 * 获得选中的菜单列表
	 */
	public List<Menu> getSelectMenus(Long menuId,Menu firstMenu){
		Menu menu=getMenu(menuId);
		List<Menu> result = new ArrayList<Menu>();
		result.add(clone(menu));
		//获得当前菜单对应的父节点、父父节点...信息
		getDefaultSelectParentMenuInfo(menu,result);
		//获得当前菜单对应的孩子、孙子...信息
		getDefaultSelectChildrenMenuInfo(menu,result,firstMenu);
		sortMenuByLayer(result);
		return result;
	}
	/**
	 * 按层级升序排序
	 * @param result
	 */
	private void sortMenuByLayer(List<Menu> result){
		Collections.sort(result, new Comparator<Menu>() {
			public int compare(Menu menu1, Menu menu2) {
				if(menu1.getLayer().equals(menu2.getLayer())){
					return 0;
				}
				if(menu1.getLayer()>menu2.getLayer()){
					return 1;
				}else{
					return -1;
				}
				
			}
		});
	}
	
	private void getDefaultSelectParentMenuInfo(Menu menu,List<Menu> result){
		Menu parent = menu.getParent();
		while(parent!=null){
			result.add(clone(parent));
			parent = parent.getParent();
		}
	}
	private void getDefaultSelectChildrenMenuInfo(Menu menu,List<Menu> result,Menu firstMenu){
		Menu resultMenu = menu;
		BusinessSystem system = businessSystemManager.getBusiness(menu.getSystemId());
		List<Menu> children = resultMenu.getChildren();
		while(children.size()>0){//获得当前菜单的默认选中的孩子、子孩子...的集合
			boolean isHasAuth = false;
			for(Menu m:children){
				if(shouldGetSystem(m, system)){
					system = businessSystemManager.getBusiness(m.getSystemId());
				}
				if(isHasAuth(m,firstMenu,system)){
					resultMenu = m;
					result.add(clone(resultMenu));
					children = resultMenu.getChildren();
					isHasAuth= true;
				}
			}
			if(!isHasAuth)break;
		}
	}
	
	//得到子菜单及其所有父菜单
	public void getMenuParents(List<Menu> menus,Menu menu){
		if(menu!=null){
			menus.add(menu);
			getMenuParents(menus,menu.getParent());
		}
	}
	
	public List<Menu> getChildrenEnabledMenus(Long menuId,List<Long> systemIds){
		return menuDao.getChildrenEnabledMenus(menuId,systemIds);
	}
	
	/**
	 * 根据父菜单id获得草稿或启用状态的子菜单
	 * @param menuId
	 * @return
	 */
	public List<Menu> getChildrenDraftOrEnabledMenus(Long menuId){
		return menuDao.getChildrenDraftOrEnabledMenus(menuId);
	}
	/**
	 * 获得启用的标准菜单一级菜单集合
	 * @return
	 */
	public List<Menu> getEnabledStandardRootMenuByCompany() {
		List<Long> systemIds = businessSystemManager.getSubsciberSystemId();
		return menuDao.getEnabledStandardRootMenuByCompany(systemIds);
	}
	
	/**
	 * 获得启用的自定义菜单一级菜单集合
	 * @return
	 */
	public List<Menu> getEnabledCustomRootMenuByCompany() {
		return menuDao.getEnabledCustomRootMenuByCompany();
	}
	
	public String getSysUrl(Long systemId){
		Menu menu=menuDao.getSysMenu(systemId);
		if(menu!=null){
			String url=menu.getUrl();
			if(url.lastIndexOf("/")==url.length()-1){
				return url.substring(0,url.length()-1);
			}else{
				return url;
			}
		}
		return "";
	}
	
	/**
	 * 初始化一级菜单
	 */
	@Transactional(readOnly=false)
	public void initAllMenus(){
		List<BusinessSystem> bses=acsUtils.getAllBusiness(ContextUtils.getCompanyId());
		Set<BusinessSystem> parentBs = getPlateSystemNotInSubsciber(bses);
		for(BusinessSystem sys:parentBs){
				bses.add(sys);
		}
		for(BusinessSystem bs:bses){
			Menu mn = getMenuByCode(bs.getCode());
			if(mn==null){
				mn=new Menu();
				mn.setType(MenuType.STANDARD);
				mn.setCompanyId(ContextUtils.getCompanyId());
				mn.setSystemId(bs.getId());
				mn.setCurrentSystemId(bs.getId());
				mn.setLayer(1);
				mn.setCode(bs.getCode());
				mn.setName(bs.getName());
				mn.setEnableState(DataState.ENABLE);
				mn.setUrl(bs.getPath());
				menuDao.save(mn);
			}
		}
	}
	
	
	
	/**
	 * 根据系统id获得一级菜单
	 * @return
	 */
	public Menu getDefaultMenuByLayer(String code){
		List<Menu> menus=menuDao.getDefaultMenuByLayer(1, code, ContextUtils.getCompanyId());
		if(menus.size()>0)return menus.get(0);
		return null;
	}
	/**
	 * 获得所有标准菜单
	 * @return
	 */
	public List<Menu> getAllMenus(){
		return menuDao.getAllMenus();
	}
	/*
	 * 获得所有不在订单中的父系统
	 */
	private Set<BusinessSystem> getPlateSystemNotInSubsciber(List<BusinessSystem> bses){
		Set<String> parentCode = new HashSet<String>();
		Set<BusinessSystem> parentBs = new HashSet<BusinessSystem>();//获得所有父系统
		if(bses!=null){
			for(BusinessSystem sys:bses){
				if(StringUtils.isNotEmpty(sys.getParentCode())){
					if(parentCode.contains(sys.getParentCode())){//当集合中已经存在该父系统时，则进入下次循环，为了提高性能
						continue;
					}
					parentCode.add(sys.getParentCode());
					BusinessSystem system = businessSystemManager.getSystemBySystemCode(sys.getParentCode());
					parentBs.add(system);
				}
			}
		}
		return parentBs;
	}
	/**
	 * 获得订单中选中系统中所有的菜单(标准和自定义)
	 * @return
	 */
	public List<Menu> getMenus(){
		List<Long> systemIds = getSubsciberSystemIds();
		return menuDao.getMenus(systemIds);
	}
	/**
	 * 获得订单中选中的系统id的集合
	 * @return
	 */
	public List<Long> getSubsciberSystemIds(){
		List<Long> systemIds = businessSystemManager.getSubsciberSystemId();
		List<BusinessSystem> bses=acsUtils.getAllBusiness(ContextUtils.getCompanyId());
		Set<BusinessSystem> parentBs = getPlateSystemNotInSubsciber(bses);
		for(BusinessSystem sys:parentBs){
			systemIds.add(sys.getId());
		}
		return systemIds;
	}
	
	/**
	 * 获得订单中选中系统中所有已启用的菜单
	 * @return
	 */
	public List<Menu> getEnabledMenus(){
		List<Long> systemIds = getSubsciberSystemIds();
		return menuDao.getEnabledMenus(systemIds);
	}
	
	public Menu getMenuByCode(String code){
		return menuDao.getMenuByCode(code);
	}
	public Menu getMenuByCodeWithoutCompany(String code){
		return menuDao.getMenuByCodeWithoutCompany(code);
	}
	public Menu getMenuByCode(String code,Long companyId){
		return menuDao.getMenuByCode(code,companyId);
	}
	public Menu getUnCompanyMenuByCode(String code){
		return menuDao.getUnCompanyMenuByCode(code);
	}
	
	public List<Menu> getMenuBySystem(String systemIds,Long companyId){
		return menuDao.getMenuBySystem(systemIds,companyId);
	}
	/**
	 * 获得该系统中第一个叶子菜单
	 * @param systemId
	 * @return
	 */
	public Menu getLeafMenuBySystem(Long systemId){
		return menuDao.getLeafMenuBySystem(systemId);
	}
	public Menu getParentMenu(String code) {
		Menu menu = menuDao.getMenuByCode(code);
		return menu.getParent();
	}
	public List<Menu> getChildren(String code) {
		Menu menu = menuDao.getMenuByCode(code);
		return menu.getChildren();
	}
	
	/**
	 *  根据menuId获得自定义系统为草稿或启用状态的一级菜单
	 * @param menuId
	 * @return
	 */
	public Menu getCustomRootMenuById(Long menuId) {
		return menuDao.getCustomRootMenuById(menuId);
	}
	/**
	 * 根据编号获得一级菜单
	 * @param code
	 * @return
	 */
	public Menu getRootMenuByCode(String code) {
		return menuDao.getRootMenuByCode(code, ContextUtils.getCompanyId());
	}
	/**
	 * 根据编号获得启用的一级菜单
	 * @param code
	 * @return
	 */
	public Menu getEnableRootMenuByCode(String code) {
		return menuDao.getEnableRootMenuByCode(code, ContextUtils.getCompanyId());
	}
	/**
	 * 根据id删除菜单
	 * @param id
	 */
	public void deleteMenu(Long id) {
		menuDao.delete(id);
	}
	
	public Menu getDefaultMenuByLayer(Integer layer, String code, MenuType menuType) {
		return menuDao.getDefaultMenuByLayer(layer, code, menuType);
	}
	
	public Menu getMenu(String menuCode, Integer menuLayer,
			String parentMenuCode, MenuType menuType) {
		return menuDao.getMenu(menuCode,menuLayer,parentMenuCode,menuType);
	}
	/**
	 * 获得菜单menu是否有权限，有则返回当前菜单，没有权限则递归孩子节点中有权限的菜单，并返回
	 * @param menu
	 * @param systemCode 系统编码，当主子系统时该参数必须的
	 * @return
	 */
	public Menu getAuthMenu(Menu menu,BusinessSystem business,Menu firstMenu,List<Long> systemIds){
		if(menu==null)return null;
		if(shouldGetSystem(menu,business)){//是否需要重新获得系统
			business = businessSystemManager.getBusiness(menu.getSystemId());
		}
		boolean isAuth = isHasAuth(menu,firstMenu,business);
		if(isAuth)return menu;
		List<Menu> children = menuDao.getChildrenEnabledMenus(menu.getId(),systemIds);
		return getAuthChildMenu(children,firstMenu,business,systemIds);
	}
	
	public String getMenuUrlWithoutParam(Menu menu,Menu firstMenu,BusinessSystem business){
		String url = menu.getUrl();
		url = functionManager.getPathWithParam(url);
		String functionPath = url;
		String param = null;//获得参数
		if(business!=null){
			if(isFirstMenu(menu,firstMenu)){//表示是一级菜单
				//当是标准系统且该系统下没有二级菜单时，且地址为全路径，例如：http://192.168.1.51:8085/imatrix/portal/index/index.htm?aa=1
				String sysPath =business.getPath();
				if(sysPath.length()>0&&sysPath.lastIndexOf("/")==sysPath.length()-1){//去掉系统路径后的斜线/
					sysPath = sysPath.substring(0,sysPath.length()-1);
				}
				if(menu.getUrl().contains(sysPath)){//sales中的系统地址（http://192.168.1.51:8085/imatrix/portal）要和menu的地址前半部分一样（http://192.168.1.51:8085/imatrix/portal/index/index.htm）中配置的一样
					String path = menu.getUrl().substring(sysPath.length(),menu.getUrl().length());//得到 /index/index.htm?aa=1&bb=3
					functionPath = functionManager.getPathWithParam(path);//获得系统里维护的资源路径
					if(functionPath.contains("?")){//如果系统里维护的资源为/index/index.htm?aa=1
						if(path.contains("&")){//包含多个参数
							param = path.substring(path.indexOf("&")+1);//得到 bb=3
						}
					}else{//如果系统里维护的资源为/index/index.htm
						if(path.contains("?")){//路径中包含?，则表示有参数
							param = path.substring(path.indexOf("?")+1);//得到 aa=1&bb=3
						}
					}
				}
			}
		}
		//设置路径和参数
		menu.setMenuFunctionPath(functionPath);
		menu.setMenuParam(param);
		return functionPath;
	}
	
	/**
	 * 当平台系统或使用了平台的系统，且有孩子节点的一级菜单或非一级菜单是否有权限
	 * @param menu
	 * @param firstMenu
	 * @param authUrl
	 * @return
	 */
	public String getHasAuthMenuUrl(Menu menu,Menu firstMenu,Menu authMenu){
		String authUrl = authMenu.getUrl();
		if(authMenu.getExternalable()!=null&&authMenu.getExternalable()){
			return authUrl;
		}
		//获得被拖动过的菜单的原属系统编码或没有被拖动过的菜单的系统编码
		String systemCode = getSystemCode(authMenu,firstMenu);
		if(isFirstMenu(menu,firstMenu)){//表示是一级菜单
			String functionPath = menu.getMenuFunctionPath()==null?"":menu.getMenuFunctionPath();
			String param = menu.getMenuParam()==null?"":menu.getMenuParam();
			String depModule = PropUtils.getProp("project.model");
			if(StringUtils.isNotEmpty(functionPath)){//说明是全路径,如：http://192.168.1.51:8085/imatrix/portal/index/index.htm
				if(!"developing.model".equals(depModule)){//产品模式下，直接返回当前一级菜单配置的地址
					return authUrl;
				}else{
					String systemUrl = SystemUrls.getSystemPageUrl(systemCode);
					if(StringUtils.isNotEmpty(param)){
						if(functionPath.contains("?")){
							return systemUrl+functionPath+"&"+param;
						}else{
							return systemUrl+functionPath+"?"+param;
						}
					}else{
						return systemUrl+functionPath;
					}
				}
			}else{//说明不是全路径，如：http://192.168.1.51:8085/imatrix/mms
				String systemUrl = SystemUrls.getSystemPageUrl(systemCode);
				if(StringUtils.isNotEmpty(param)){
					if(functionPath.contains("?")){
						return systemUrl+authUrl+"&"+param;
					}else{
						return systemUrl+authUrl+"?"+param;
					}
				}else{
					return systemUrl+authUrl;
				}
			}
		}else{
			return SystemUrls.getSystemPageUrl(systemCode)+authUrl;
		}
	}
	
	/**
	 * 获得被拖动过的菜单的原属系统编码
	 * @param menu
	 * @return
	 */
	
	private String getSystemCode(Menu menu,Menu firstMenu){
		String systemCode = null;
		if(isCustomSystem(menu))systemCode="imatrix";
		if(StringUtils.isEmpty(systemCode))systemCode = getOriginalSystemCode(menu);
		if(StringUtils.isEmpty(systemCode)){
			systemCode = firstMenu.getCode();
		}
		return systemCode;
	}
	
	/**
	 * 获得被拖动过的菜单的原属系统编码
	 * @param menu
	 * @return
	 */
	
	private String getOriginalSystemCode(Menu menu){
		String systemCode = null;
		if(isDrag(menu)){//拖动过
			//获得被拖动过的菜单的原属系统
			BusinessSystem originalSystem = businessSystemManager.getBusiness(menu.getSystemId());
			if(originalSystem!=null)systemCode = originalSystem.getCode();
		}
		return systemCode;
	}
	
	/**
	 * 是否拖动过，true表示被拖动了
	 * @param menu
	 * @return true表示被拖动了
	 */
	private boolean isDrag(Menu menu){
		if(!menu.getSystemId().equals(menu.getCurrentSystemId())) return true;
		return false;
	}
	
	/**
	 * 设置一级菜单的url，处理参数
	 * @param menu
	 */
	public String getHasAuthFirstMenuUrl(Menu menu,Menu firstMenu,Long lastMenuId){
		String menuUrl = getMenuUrl(menu,firstMenu,lastMenuId);
		if(!menuUrl.contains("_r=1")){//不包含刷新样式的格式参数则需要添加
			//刷新样式用到
			if(menuUrl.contains("?")){
				menuUrl = menuUrl+"&_r=1";
			}else{
				menuUrl = menuUrl+"?_r=1";
			}
		}
		return menuUrl;
	}
	
	public String getMenuUrl(Menu menu,Menu firstMenu,Long lastMenuId){
		String menuUrl = menu.getMenuUrl();
		if(StringUtils.isEmpty(menuUrl)){
			menuUrl = getHasAuthMenuUrl(menu,firstMenu,menu);
		}
		if((menu.getExternalable()==null||!menu.getExternalable())){//表示不是外部系统时
			menuUrl = getMenuUrlByMenu(menuUrl,lastMenuId);
		}
		return menuUrl;
	}
	
	/**
	 * 处理菜单id参数
	 * @param menuUrl
	 * @param lastMenuId
	 * @return
	 */
	private String getMenuUrlByMenu(String menuUrl,Long lastMenuId){
		if(menuUrl.indexOf("menuId=")>=0){//包含menuId参数
			if(menuUrl.indexOf("?menuId=")>=0){
				menuUrl = menuUrl.substring(0,menuUrl.indexOf("?menuId="));
			}else if(menuUrl.indexOf("&menuId=")>=0){
				menuUrl = menuUrl.substring(0,menuUrl.indexOf("&menuId="));
			}
		}
		if(lastMenuId!=null){
			if(menuUrl.indexOf("?")>=0){
				menuUrl = menuUrl+"&menuId="+lastMenuId;
			}else{
				menuUrl = menuUrl+"?menuId="+lastMenuId;
			}
		}
		return menuUrl;
	}
	
	
	
	 /**
	 * 是否是一级菜单
	 * @param menu
	 * @param firstMenu
	 * @return
	 */
	public boolean isFirstMenu(Menu menu,Menu firstMenu){
		return menu.getId().equals(firstMenu.getId());
	}
	
	private Menu getAuthChildMenu(List<Menu> children,Menu firstMenu,BusinessSystem business,List<Long> systemIds){
		for(Menu m:children){
			if(shouldGetSystem(m,business)){//是否需要重新获得系统
				business = businessSystemManager.getBusiness(m.getSystemId());
			}
			boolean isAuth = isHasAuth(m,firstMenu,business);
			if(isAuth)return m;
			//如果没有权限则递归查询孩子节点是否有权限
			List<Menu> childrens = menuDao.getChildrenEnabledMenus(m.getId(),systemIds);
			Menu authMenu = getAuthChildMenu(childrens,firstMenu,business,systemIds);
			if(authMenu!=null)return authMenu;
		}
		return null;
	}
	/**
	 * 是否需要重新获得系统
	 * 为了提高性能
	 * @param menu
	 * @return true表示需要重新获得系统
	 */
	private boolean shouldGetSystem(Menu menu,BusinessSystem business){
		if(!business.getId().equals(menu.getSystemId())) return true;
		return false;
	}
	/**
	 * 由于菜单的menu和parent配置使得在使用JsonParser.object2Json(resultMenus)时会出现死循环，所以使用该方法重新设置菜单属性
	 * @param menu
	 * @return
	 */
	public Menu clone(Menu menu){
		Menu tempMenu = new Menu();
		tempMenu.setId(menu.getId());
		tempMenu.setUrl(menu.getUrl());
		tempMenu.setMenuUrl(menu.getMenuUrl());
		tempMenu.setOpenWay(menu.getOpenWay());
		tempMenu.setEvent(menu.getEvent());
		tempMenu.setLayer(menu.getLayer());
		tempMenu.setCode(menu.getCode());
		tempMenu.setType(menu.getType());
		tempMenu.setImageUrl(menu.getImageUrl());
		tempMenu.setName(menu.getName());
		tempMenu.setLastMenuId(menu.getLastMenuId());
		tempMenu.setSubCompanyId(menu.getSubCompanyId());
		tempMenu.setSystemId(menu.getSystemId());
		tempMenu.setCurrentSystemId(menu.getCurrentSystemId());
		tempMenu.setFunctionId(menu.getFunctionId());
		tempMenu.setMenuFunctionPath(menu.getMenuFunctionPath());
		tempMenu.setMenuParam(menu.getMenuParam());
		tempMenu.setExternalable(menu.getExternalable());
		tempMenu.setIframable(menu.getIframable());
		tempMenu.setUrlParamDynamic(menu.getUrlParamDynamic());
		return tempMenu;
	}

	/**
	 * 获得三级菜单下有5级菜单及孩子的菜单的菜单集合
	 * @param currentMenuId
	 * @return
	 */
	public List<Menu> getThirdMenusChildren(Long currentThirdMenuId,List<Long> systemIds){
		List<Menu> result = new ArrayList<Menu>();
		Menu currentMenu = getMenu(currentThirdMenuId);
		//获得当前菜单的子菜单集合
		getMenuChildrens(result,currentMenu,systemIds);
		return result;
	}
	
	
	private void getMenuChildrens(List<Menu> result,Menu currentMenu,List<Long> systemIds){
		Map<String,Boolean> shouldCreateTree = new HashMap<String,Boolean>();
		if(currentMenu!=null&&currentMenu.getEnableState()==DataState.ENABLE){
			Long menuId = currentMenu.getId();
			List<Menu> childrens = menuDao.getChildrenEnabledMenus(menuId,systemIds);
			result.addAll(childrens);
			getMenuChildren(childrens,result,shouldCreateTree,systemIds);
		}
		boolean canCreateTree = shouldCreateTree.get("shouldCreateTree")==null?false:shouldCreateTree.get("shouldCreateTree");
		if(!canCreateTree)result.clear();
	}
	
	private void getMenuChildren(List<Menu> childrens,List<Menu> result,Map<String,Boolean> shouldCreateTree,List<Long> systemIds){
		for(Menu menu:childrens){
			if(menu.getLayer()>4){//当前三级菜单下有5级及更多子菜单
				shouldCreateTree.put("shouldCreateTree", true);
			}
			List<Menu>  mychildrens = menuDao.getChildrenEnabledMenus(menu.getId(),systemIds);
			result.addAll(mychildrens);
			getMenuChildren(mychildrens,result,shouldCreateTree,systemIds);
		}
	}
	@Transactional
	public void moveNodes(String msgs, String targetId, String moveType) {
		String[] ids=msgs.split(";");
		Integer len=ids.length;
		Long id=null;
		String menuIds="";
		Menu menu=null;
		Menu target=null;
		target=menuDao.get(Long.parseLong(targetId));
		for(int i=0;i<ids.length;i++){
			String[] msg=ids[i].split(",");
			id=Long.parseLong(msg[0]);
			menuIds=menuIds+id+(i==ids.length-1?"":",");
			menu=menuDao.get(id);
			if(moveType.equals("inner")){
				menu.setParent(target);
				menu.setLayer(target.getLayer()+1);
				menu.setDisplayOrder(getMaxDisplayOrderByPid(target.getId())+3);
			}else if(moveType.equals("prev")){
				menu.setParent(target.getParent());
				menu.setLayer(target.getLayer());
				menu.setDisplayOrder(target.getDisplayOrder()+i);
			}else if(moveType.equals("next")){
				menu.setParent(target.getParent());
				menu.setLayer(target.getLayer());
				menu.setDisplayOrder(target.getDisplayOrder()+i+1);
			}
			menu.setCurrentSystemId(target.getCurrentSystemId());
			setChildLayer(menu);
		}
		sort(moveType,target,len,menuIds);
		
	}
	private void setChildLayer(Menu parent) {
		List<Menu> temp=new ArrayList<Menu>();
		temp=menuDao.find("from Menu m where m.parent.id=? order by m.displayOrder,m.id",parent.getId());
		for(Menu menu:temp){
			menu.setLayer(parent.getLayer()+1);
			menu.setCurrentSystemId(parent.getCurrentSystemId());
			setChildLayer(menu);
		}
	}
	//对受拖拽影响的节点重新排序
	private void sort(String moveType,Menu target,Integer len,String ids) {
		Menu parent=target.getParent();
		if(moveType.equals("prev")){
			if(parent==null){
				menuDao.createQuery("update Menu m set m.displayOrder=(m.displayOrder+"+(len+3)+") where m.displayOrder>=? and m.parent is null and m.id not in ("+ids+")", target.getDisplayOrder()).executeUpdate();
			}else{
				menuDao.createQuery("update Menu m set m.displayOrder=(m.displayOrder+"+(len+3)+") where m.displayOrder>=? and m.parent.id=? and m.id not in ("+ids+")", target.getDisplayOrder(),parent.getId()).executeUpdate();
			}
		}else if(moveType.equals("next")){
			if(parent==null){
				menuDao.createQuery("update Menu m set m.displayOrder=(m.displayOrder+"+(len+3)+") where m.displayOrder>? and m.parent is null and m.id not in ("+ids+")", target.getDisplayOrder()).executeUpdate();
			}else{
				menuDao.createQuery("update Menu m set m.displayOrder=(m.displayOrder+"+(len+3)+") where m.displayOrder>? and m.parent.id=? and m.id not in ("+ids+")", target.getDisplayOrder(),parent.getId()).executeUpdate();
			}
		}
	}
	//获取子菜单最大序号
	private Integer getMaxDisplayOrderByPid(Long id) {
		return menuDao.findUnique("select max(m.displayOrder) from Menu m where m.parent.id=?",id);
	}
	//获取菜单下所有的菜单
	public void getMenuByPid(List<Menu> menus,Long currentId) {
		List<Menu> temp=new ArrayList<Menu>();
		temp=menuDao.find("from Menu m where m.parent.id=? order by m.displayOrder,m.id",currentId);
		for(Menu menu:temp){
			getMenuByPid(menus,menu.getId());
		}
		menus.addAll(temp);
	}
	
	/**
	  * 验证菜单编码是否存在
	  * @param code
	  * @return 存在返回true,反之
	  */
	 public boolean isMenuCodeExist(String code,Long menuId){
		 Menu menu=menuDao.getMenuByCode(code);
		 if(menu==null){
			 return false;
		 }else{
			 if(menuId==null)return true;
			 if(menu.getId().equals(menuId)){
				 return false;
			 }else{
				 return true;
			 }
		 }
	 }
	 
	 /**
		 * 获得有权限的菜单集合
		 * @param menus
		 * @return
		 */
		public List<ZTreeNode> getHasAuthTreeMenus(List<Menu> treeMenus,Menu firstMenu,List<Long> systemIds)throws Exception{
			List<ZTreeNode> ztreeNodes=new ArrayList<ZTreeNode>();
			ZTreeNode zNode=null;
			BusinessSystem business = businessSystemManager.getSystemBySystemCode(firstMenu.getCode());
			for(Menu menu:treeMenus){
				boolean isAuth = shouldShowMenu(menu,firstMenu,business,systemIds);
				if(isAuth){//表示有权限
					Menu lastMenu = getLastMenu(menu,firstMenu,systemIds);
					menu.setLastMenuId(lastMenu.getId());
					Menu parent = menu.getParent();
					Long parentId = parent==null?0:parent.getId();
					Menu cloneMenu = clone(menu);
					setMenuNameToi18n(cloneMenu);//菜单名称国际化
					zNode = new ZTreeNode(menu.getId().toString(), parentId.toString(), cloneMenu.getName() , "true", "false", "", "", "folder", "");
					String menuUrl = null;
					if(isCustomSystem(menu)){//如果是自定义系统
						menuUrl = getCustomSystemMenu(menu,null);
					}else{//表示不是自定义系统
						menuUrl = getHasAuthMenuUrl(cloneMenu,firstMenu,menu);
					}
					setMenuUrlParam(menuUrl,cloneMenu);
					zNode.setData("["+JsonParser.object2Json(cloneMenu)+"]");
					ztreeNodes.add(zNode);
				}
			}
			return ztreeNodes;
		}
		
		/**
		 * 
		 * @param menu 一级菜单
		 * @return true:表示有权限，false表示没有权限
		 */
		public boolean shouldShowMenu(Menu menu,Menu firstMenu,BusinessSystem business,List<Long> systemIds) throws Exception{//是否是使用了平台的系统：底层的系统和使用了底层的系统（例如：OA，url为/common/list.htm的自定义系统）均属于使用了平台的系统
			if(menu.getExternalable()!=null&&menu.getExternalable()){
				menu.setMenuUrl(menu.getUrl());
				//为url添加ticket参数
				addTicketToMenuUrl(menu);
				return true;
			}
			if(StringUtils.isEmpty(menu.getUrl())){
				// 一级菜单的url为空，菜单有事件则不做权限判断
				return true;
			}
			if(business!=null){//当是自定义的系统时，business为空
				if(isFirstMenuNoChildren(menu,business,firstMenu,systemIds)){//是标准的一级菜单，但是一级菜单下没有任何子菜单
					menu.setMenuUrl(menu.getUrl());
					return true;
				}else{
					Menu authMenu = getAuthMenu(menu,business,firstMenu,systemIds);
					String authUrl = authMenu==null?null:authMenu.getUrl();
					if(StringUtils.isEmpty(authUrl)){//表示没有权限
						return false;
					}else{//表示有权限
						String menuUrl = getHasAuthMenuUrl(menu,firstMenu,authMenu);
						menu.setMenuUrl(menuUrl);
						return true;
					}
				}
			}else{//自定义系统时
				//自定义系统分为两种情况，
				//1  平台级别的自定义系统，
				//2  例如百度等系统，即系统的url不为/common/list.htm的自定义系统
				//对于第1种情况，可以判断系统的权限，第2种则无法做判断，则默认为有权限显示在一级菜单中
				if(isCustomSystem(menu)){//当是第1种情况时
					if(ContextUtils.isAuthority("/common/list.htm", "mms")){//表示有自定义系统的权限
						String menuUrl = getCustomSystemMenu(menu,firstMenu);
						menu.setMenuUrl(menuUrl);
						return true;
					}
				}else{//第2种则无法做判断，则默认为有权限显示在一级菜单中
					return true;
				}
			}
			return false;
		}
		
		private boolean isFirstMenuNoChildren(Menu menu,BusinessSystem business,Menu firstMenu,List<Long> systemIds){
			if(menu.getExternalable()!=null&&menu.getExternalable())return false;//表示当是外来系统，例如百度等时，不做权限控制直接显示
			if(isCustomSystem(menu)){//当是自定义系统情况时
				return false;
			}else{
				if(isFirstMenuNoFullUrl(menu,firstMenu,business)){
					List<Menu> children = menuDao.getChildrenMenus(menu.getId(),systemIds);
					if(!(children!=null && children.size()>0)){
						return true;
					}
				}
			}
			return false;
		}
		
		private boolean isFirstMenuNoFullUrl(Menu menu,Menu firstMenu,BusinessSystem business){
			boolean result=false;
			if(business!=null){
				if(isFirstMenu(menu,firstMenu)){//表示是一级菜单
					String sysPath =business.getPath();
					if(sysPath.length()>0&&sysPath.lastIndexOf("/")==sysPath.length()-1){//去掉系统路径后的斜线/
						sysPath = sysPath.substring(0,sysPath.length()-1);
					}
					if(menu.getUrl().equals(sysPath)){
						result=true;
					}
				}
			}
			return result;
		}
		

		/**
		 * 获得自定义系统中的菜单地址
		 * @return
		 */
		private String getCustomSystemMenu(Menu menu,Menu firstMenu){
			if(firstMenu!=null&&isFirstMenu(menu,firstMenu)){
				String menuUrl = menu.getUrl();
				String[] menuinfos = menu.getUrl().split("/mms");
				String functionPath = "";
				if(menuinfos.length>=2)functionPath = menuinfos[1];//获得/common/list.htm
				menuUrl = SystemUrls.getSystemPageUrl("mms")+functionPath;//http://.../mms/common/list.htm
				return menuUrl;
			}else{
				return SystemUrls.getSystemPageUrl("imatrix")+menu.getUrl();
			}
		}
		
		/**
		 * 获得有权限的菜单集合
		 * @param menus
		 * @return
		 */
		public List<Menu> getHasAuthMenus(List<Menu> menus,Menu firstMenu,List<Long> systemIds)throws Exception{
			BusinessSystem business = businessSystemManager.getSystemBySystemCode(firstMenu.getCode());
			List<Menu> resultMenus=new ArrayList<Menu>();
			for(Menu menu:menus){
				boolean isAuth = shouldShowMenu(menu,firstMenu,business,systemIds);
				if(isAuth){//表示有权限
					Menu cloneMenu = clone(menu);
					Menu lastMenu = getLastMenu(cloneMenu,firstMenu,systemIds);
					cloneMenu.setLastMenuId(lastMenu.getId());
					setHasAuthMenuUrl(cloneMenu,firstMenu);
					resultMenus.add(cloneMenu);
				}
			}
			return resultMenus;
		}
		/**
		 * 设置菜单的url，处理参数
		 * @param menu
		 */
		private void setHasAuthMenuUrl(Menu menu,Menu firstMenu){
			String menuUrl = getMenuUrl(menu,firstMenu,menu.getLastMenuId());
			menu.setMenuUrl(menuUrl);
			setMenuUrlParam(menuUrl,menu);
			
		}
		
		private void setMenuUrlParam(String menuUrl,Menu menu){
			try{
				if(StringUtils.isNotEmpty(menu.getUrlParamDynamic())){
					com.norteksoft.product.api.entity.BusinessSystem system = ApiFactory.getAcsService().getSystemById(menu.getSystemId());
					if(system!=null){
						menuUrl = getDynamicUrlParam(menu.getUrlParamDynamic(),system.getCode(),menuUrl,menu.getName());
						if(StringUtils.isNotEmpty(menuUrl))menu.setMenuUrl(menuUrl);
					}
				}
			}catch (Exception e) {//当设置的动态获取url参数的路径不正确时，则url不添加任何参数
				menu.setMenuUrl(menuUrl);
			}
		}
		
		public String getAuthorizeUrl(String code, String systemCode) {
			String resultUrl = "";
			if(StringUtils.isEmpty(code))return resultUrl;
			String[] codes = code.split(",");
			FunctionManager functionManager=(FunctionManager)ContextUtils.getBean("functionManager");
			if(StringUtils.isEmpty(systemCode)){
				systemCode = ContextUtils.getSystemCode();
			}
			for(String mycode:codes){
				if(ContextUtils.isAuthority(mycode)){
					String path = functionManager.getFunctionPathByCode(mycode, systemCode);
					resultUrl = SystemUrls.getSystemUrl(systemCode)+ path;
					break;
				}
			}
			return resultUrl;
		}
		 
		 private String getDynamicUrlParam(String url,String systemCode,String menuUrl,String menuName){
				ClientConfig config = new DefaultClientConfig();
				Client client = Client.create(config);
				String resultUrl = SystemUrls.getSystemUrl(systemCode);
				if(PropUtils.isBasicSystem(resultUrl)){
					resultUrl = SystemUrls.getSystemPageUrl("imatrix");
				}
				resultUrl = resultUrl + url;
				WebResource service = client.resource(resultUrl);
				ClientResponse cr = service
				.entity("menuUrl="+menuUrl+"&menuName="+menuName, MEDIA_TYPE)
				.accept(MEDIA_TYPE)
				.post(ClientResponse.class);
				if(cr != null&&cr.getStatus()==201) return cr.getEntity(String.class);
				return null;
			}
		 
		 /**
		 * 一级菜单国际化
		 * 1、格式：${key}
		 * @param resultMenus
		 * @return
		 */
		public List<Menu> toi18nMenu(List<Menu> resultMenus) {
			if(resultMenus != null && resultMenus.size() > 0){
				for (Menu menu : resultMenus) {
					setMenuNameToi18n(menu);
				}
			}
			return resultMenus;
		}
		
		public void setMenuNameToi18n(Menu menu){
			menu.setName(getNameToi18n(menu.getName()));
		}
		
		public  String getNameToi18n(String name){
			if(name==null)return name;
			if(name.contains("${")&&name.contains("}")){
				name = name.substring(name.indexOf("${")+2,name.indexOf("}"));
				name = getI18nString(name);
				if(StringUtils.isNotEmpty(name)){
					return name;
				}
			}
			return name;
		}
		
		private static String getI18nString(String code){
			return ApiFactory.getSettingService().getInternationOptionValue(code, ContextUtils.getCurrentLanguage(), InternationType.MENU_RESOURCE.toString());
		}
		
		/**
		 * 获得有权限的一级菜单
		 * @return
		 */
		public List<Menu> getHasAuthFirstMenus(List<Long> systemIds)throws Exception{
			List<Menu> menus=getEnabledRootMenuByCompany();
			List<Menu> resultMenus=new ArrayList<Menu>();
			for(Menu menu:menus){
				BusinessSystem business = businessSystemManager.getSystemBySystemCode(menu.getCode());
				boolean isAuth = shouldShowMenu(menu,menu,business,systemIds);
				if(isAuth){//表示有权限
					Menu tempMenu =clone(menu);
					Menu lastMenu = getLastMenu(menu,menu,systemIds);
					if(lastMenu!=null)tempMenu.setLastMenuId(lastMenu.getId());
					setHasAuthFirstMenuUrl(tempMenu,menu);
					resultMenus.add(tempMenu);
				}
			}
			return resultMenus;
		}
		
		/**
		 * 为菜单的url添加ticket参数。只有是外部系统，且设置为是单点登录的菜单才需要添加该参数。
		 * @param menu
		 * @throws Exception
		 */
		private void addTicketToMenuUrl(Menu menu)throws Exception{
			if(menu.getAutoLoginable()!=null&&menu.getAutoLoginable()){
				String url = menu.getMenuUrl();
				String ticket = TicketUtil.getTicket(PropUtils.getProp("host.sso"), ContextUtils.getLoginName(), ContextUtils.getPassword());
				if(StringUtils.isNotEmpty(ticket)){
					if(url.indexOf("?")>=0){
						url = url + "&ticket="+ticket;
					}else{
						url = url +"?ticket="+ticket;
					}
					menu.setMenuUrl(url);
				}
			}
		}
		
		/**
		 * 设置一级菜单的url，处理参数
		 * @param menu
		 */
		private void setHasAuthFirstMenuUrl(Menu menu,Menu firstMenu){
			String menuUrl =  getHasAuthFirstMenuUrl(menu,firstMenu,menu.getLastMenuId());
			menu.setMenuUrl(menuUrl);
		}
		
		//设置模板中二级菜单需要的参数
		public List<Menu> setParamsForSecondMenu(Map<String, Object> root,Menu firstMenu,List<Menu> secMenus,List<Long> systemIds) throws Exception{
			List<Menu> resultMenus=new ArrayList<Menu>();
			if(firstMenu!=null){
				if(secMenus!=null&&secMenus.size()>0){
					//获得有权限的二级菜单集合
					resultMenus=getHasAuthSecMenus(firstMenu,secMenus,systemIds);
					
					//菜单国际化
					resultMenus = toi18nMenu(resultMenus); 
					root.put("secMenus", resultMenus);
					root.put("showSecMenu", "true");
				}else{
					root.put("showSecMenu", "false");
				}
			}else{
				root.put("showSecMenu", "false");
			}
			return resultMenus;
		}
		
		/**
		 * 获得有权限的二级菜单集合
		 * @param firstMenu
		 * @return
		 */
		private List<Menu> getHasAuthSecMenus(Menu firstMenu,List<Menu> secMenus,List<Long> systemIds)throws Exception{
			return getHasAuthMenus(secMenus,firstMenu,systemIds);
		}
		/**
		 * 菜单标签设置左侧菜单
		 * @param root
		 * @param selectMenus
		 * @param firstMenu
		 * @param systemIds
		 * @throws Exception
		 */
		public void setLeftMenu(Map<String, Object> root,List<Menu> selectMenus,Menu firstMenu,List<Long> systemIds)throws Exception{
			//设置模板中左侧菜单需要的参数
			Long secondMenuId = 0L;
			Long thirdMenuId = 0L;
			if(selectMenus.size()>=2){//因为selectMenus是按层级排序的
				secondMenuId = selectMenus.get(1).getId();
			}
			if(selectMenus.size()>=3){//因为selectMenus是按层级排序的
				thirdMenuId = selectMenus.get(2).getId();
			}
			root.put("thirdMenuId",thirdMenuId);
			List<Menu> thirdMenus=getEnableMenuByLayer(3,secondMenuId);
			
			List<Menu> treeMenus = getThirdMenusChildren(thirdMenuId,systemIds);
			List<Menu> fourMenus=new ArrayList<Menu>();
			if(treeMenus.isEmpty()){//表示不需要拼ztree树才需要查找四级菜单集合
				fourMenus = getEnableMenuByLayer(4,thirdMenuId);
			}
			setParamsForLeftMenu(root,firstMenu,thirdMenus,fourMenus,systemIds);
			//将四级及其孩子拼接为Ztree树
			getZtreeFourMenusAndChildren(treeMenus,root,firstMenu,systemIds);
			
			root.put("isAccordion", getThirdMenuAccordionSign(root));
		}
		
		//设置模板中左侧菜单需要的参数
		private void setParamsForLeftMenu(Map<String, Object> root,Menu firstMenu,List<Menu> threeMenus,List<Menu> fourMenus,List<Long> systemIds) throws Exception{
			if(threeMenus==null){
				root.put("showLeftMenu", "false");
			}else{
				List<Menu> resultMenus = getHasAuthThreeMenus(firstMenu,root,threeMenus,systemIds);
				//菜单国际化
				resultMenus = toi18nMenu(resultMenus); 
				root.put("thirdMenus",resultMenus );
			}
			if(fourMenus!=null){//四级菜单集合存在，且不需要拼接ztree树
				List<Menu> resultMenus = getHasAuthFourMenus(firstMenu,fourMenus,systemIds);
				//菜单国际化
				resultMenus = toi18nMenu(resultMenus); 
				root.put("fourMenus",resultMenus );
			}
		}
		
		/**
		 * 获得有权限的三级菜单集合
		 * @param firstMenu
		 * @return
		 */
		private List<Menu> getHasAuthThreeMenus(Menu firstMenu,Map<String, Object> root,List<Menu> threeMenus,List<Long> systemIds)throws Exception{
			if(threeMenus!=null&&threeMenus.size()>0){
				root.put("showLeftMenu", "true");
			}else{
				root.put("showLeftMenu", "false");
			}
			return getHasAuthMenus(threeMenus,firstMenu,systemIds);
		}
		/**
		 * 获得有权限的四级菜单集合
		 * @param firstMenu
		 * @return
		 */
		private List<Menu> getHasAuthFourMenus(Menu firstMenu,List<Menu> fourMenus,List<Long> systemIds)throws Exception{
			return getHasAuthMenus(fourMenus,firstMenu,systemIds);
		}
		
		/**
		 * 将四级及其孩子拼接为Ztree树
		 */
		private void getZtreeFourMenusAndChildren(List<Menu> treeMenus,Map<String, Object> root,Menu firstMenu,List<Long> systemIds)throws Exception{
			List<ZTreeNode> ztreeNodes=getHasAuthTreeMenus(treeMenus,firstMenu,systemIds);
			root.put("showZtree", ztreeNodes.isEmpty()?"false":"true");
			if(!treeMenus.isEmpty()){
				root.put("fourMenuTreeDatas", JsonParser.object2Json(ztreeNodes));
			}else{
				root.put("fourMenuTreeDatas", "\"\"");
			}
		}
		
		private String getThirdMenuAccordionSign(Map<String, Object> root){
			String isAccordion="false";
			String showLeftMenuObj=root.get("showLeftMenu").toString();
			if("true".equals(showLeftMenuObj)){
				Object hasAuthThirdMenus=root.get("thirdMenus");
				if(hasAuthThirdMenus!=null){
					for(Menu menu:(List<Menu>)hasAuthThirdMenus){
						List<Menu> fourMenus = getEnableMenuByLayer(4,menu.getId());
						if(fourMenus!=null && fourMenus.size()>0){
							isAccordion="true";
							break;
						}
					}
				}
			}
			return isAccordion;
		}
		/*
		 * 获得所有启用的级别为一级、类型为标准类型的菜单
		 */
		public List<Menu> getEnabledRootMenuBySystems(List<Long> ids ) {
			return menuDao.getEnabledStandardRootMenus(ids);
		}
		/*
		 * 获得所有启用的级别为一级、类型为标准类型的菜单
		 */
		public List<Menu> getAllEnabledStandardRootMenus() {
			return menuDao.getAllEnabledStandardRootMenus();
		}
}
