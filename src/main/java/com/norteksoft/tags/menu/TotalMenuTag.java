package com.norteksoft.tags.menu;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;

import com.norteksoft.mms.module.entity.Menu;
import com.norteksoft.mms.module.service.MenuManager;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.JsonParser;
import com.norteksoft.product.util.PropUtils;
import com.norteksoft.product.util.SystemUrls;
import com.norteksoft.product.util.freemarker.TagUtil;
import com.norteksoft.product.web.struts2.Struts2Utils;
/**
 * 一级菜单和二级菜单合并标签
 * @author ldx
 *
 */
public class TotalMenuTag extends TagSupport {
	private static final long serialVersionUID = 1L;
	private Log log = LogFactory.getLog(TotalMenuTag.class);
	private MenuManager menuManager;
	private Long menuId;
	private Integer showNum=1;
	private String code;//系统code
	private boolean themeChagable = true;//一级菜单是否显示换肤
	private boolean existable = true;//一级菜单是否显示退出
	private String selectMenuInfo="";//json格式的字符串，[{菜单实体属性},{菜单实体},...]
	private List<Menu> selectMenus = new ArrayList<Menu>();//选中的菜单列表
	private List<Long> systemIds = new ArrayList<Long>();//订单中选中的系统id的集合
	private String changeType="totalZone";//zone:表示刷新区域|totalZone:表示刷新整个页面 defaultForm mainZone 
	private boolean dateVisible = true;//一级菜单是否显示日期
	private boolean passwordVisible = true;//一级菜单是否显示修改密码
	private boolean languageVisible = true;//一级菜单是否显示语言切换：中英文版
	private boolean helloVisible = true;//一级菜单是否显示问候语，您好
	@Override
	public int doStartTag() throws JspException {
		try {
			menuManager=(MenuManager)ContextUtils.getBean("menuManager");
			String lastMenuIdStr=pageContext.getRequest().getParameter("menuId");
			if(lastMenuIdStr!=null){
				menuId=Long.parseLong(lastMenuIdStr);
			}else{
				if(pageContext.getRequest().getAttribute("menuId")!=null){
					menuId =  Long.parseLong(pageContext.getRequest().getAttribute("menuId").toString());
				}else{
					String url=(String)pageContext.getRequest().getAttribute("struts.request_uri");
					String[] urls=url.split("/");
					//底层系统应用地址
					String systemCode=ContextUtils.getSystemCode();
					String code=urls[1];
					if(urls.length>=3){
						String tempCode = urls[2];
						Menu tempMenu=menuManager.getRootMenuByCode(tempCode);
						if(tempMenu !=null){
							code=tempCode;
						}
					}
					Menu lastMenu=menuManager.getDefaultMenuByLayer(StringUtils.isEmpty(code)?systemCode:code);
					if(lastMenu!=null){
						menuId =  lastMenu.getId();
					}
				}
			}
			if(menuId!=null){
				firstMenu = menuManager.getRootMenu(menuId);
				selectMenus = menuManager.getSelectMenus(menuId,firstMenu);
				firstMenu = menuManager.clone(firstMenu);//因为selectMenus是按层级排序的,所以第一个元素就是一级菜单
				selectMenuInfo = JsonParser.object2Json(selectMenus);
			}
			 JspWriter out=pageContext.getOut(); 
			 String result = readScriptTemplate();
			 out.print(result);
		} catch (Exception e) {
			log.error(e);
			throw new JspException(e);
		}
		return Tag.EVAL_PAGE;
	}
	
	private Menu firstMenu = null;
	//读取脚本模板
	private String readScriptTemplate() throws Exception{
		String webapp=((HttpServletRequest)pageContext.getRequest()).getContextPath();
		systemIds = menuManager.getSubsciberSystemIds();
		menuManager.initAllMenus();
		// 获得有权限的一级菜单集合
		List<Menu> resultMenus=menuManager.getHasAuthFirstMenus(systemIds);
		Map<String, Object> root=new HashMap<String, Object>();
		
		//交换一级菜单中显示的和更多中的菜单（开始） 
		int lastIndexOf=resultMenus.lastIndexOf(firstMenu);
		if(lastIndexOf>=showNum){
			Menu temp=menuManager.clone(resultMenus.get(showNum-1));
			firstMenu.setLastMenuId(menuId);
			
			String menuUrl = menuManager.getHasAuthFirstMenuUrl(temp,firstMenu,temp.getLastMenuId());
			temp.setMenuUrl(menuUrl);
			Menu menu = menuManager.getMenu(menuId);
			menuUrl = menuManager.getHasAuthFirstMenuUrl(menu,firstMenu,menuId);
			firstMenu.setMenuUrl(menuUrl);
			
			resultMenus.set(showNum-1, firstMenu);
			resultMenus.set(lastIndexOf, temp);
		}
		
		//一级菜单国际化
		resultMenus = menuManager.toi18nMenu(resultMenus); 
			
		//交换一级菜单中显示的和更多中的菜单（ 结束）
		String imatrixUrl=SystemUrls.getSystemPageUrl("imatrix");
		root.put("changeType", changeType);
		root.put("showNum", showNum);
		root.put("menuId", menuId);
		root.put("moreSystem",  Struts2Utils.getText("more.menu"));
		root.put("menus", resultMenus);
		root.put("firstMenus", JsonParser.object2Json(resultMenus));
		root.put("menuSize", resultMenus.size());
		root.put("companyId", ContextUtils.getCompanyId());
		root.put("ctx", webapp);
		root.put("imatrixUrl", imatrixUrl);
		root.put("honorificTitle", ContextUtils.getHonorificTitle());
		root.put("themeChagable", themeChagable);
		root.put("existable", existable);
		String versionType = PropUtils.getProp("product.version.type");
		root.put("versionType",versionType==null?"":versionType );
		root.put("selectMenuInfo",selectMenuInfo );
		String resourcesCtx = PropUtils.getProp("host.resources");
		root.put("resourcesCtx",resourcesCtx==null?"":resourcesCtx );
		root.put("iframeName",PropUtils.getMenuIframeName() );
		//门户国际化字段
		root.put("hello",Struts2Utils.getText("hello") );//修改密码
		root.put("changePassword",Struts2Utils.getText("changePassword") );//修改密码
		root.put("changeSkin",Struts2Utils.getText("changeSkin") );//换肤
		root.put("exit",Struts2Utils.getText("exit") );//退出
		root.put("imatrixCtx","imatrixCtx" );//test
		root.put("dateVisible", dateVisible);
		root.put("helloVisible", helloVisible);
		root.put("passwordVisible", passwordVisible);
		root.put("languageVisible", languageVisible);
		
		Locale currentLocale  = (Locale) ServletActionContext.getRequest().getSession().getAttribute("WW_TRANS_I18N_LOCALE");
		if(currentLocale == null  ){
			currentLocale = Locale.getDefault();
			root.put("lanague",currentLocale.toString() );//test
		}else {
			if("zh_CN".equals(currentLocale.toString())){
				root.put("lanague","zh_CN" );//test
			}else {
				root.put("lanague","en" );//test
			}
		}
		root.put("currentTime", getCurrentTime((String)root.get("lanague")));
		
		//设置模板中二级菜单需要的参数
		Long firstMenuId = 0L;
		if(firstMenu!=null){
			firstMenuId = firstMenu.getId();
		}
		
		List<Menu> secMenus=menuManager.getEnableMenuByLayer(2,firstMenuId);
		
		menuManager.setParamsForSecondMenu(root,firstMenu,secMenus,systemIds);
		
		//设置模板中左侧菜单需要的参数
		menuManager.setLeftMenu(root,selectMenus,firstMenu,systemIds);
		String result = TagUtil.getContent(root, "menu/totalMenuTag.ftl");
		return result;
	}
	
	private String getCurrentTime(String flag){
		SimpleDateFormat fmt = null;
		if("en".equals(flag)){
			fmt = new SimpleDateFormat("yyyy-MM-dd");
		}else{
			fmt = new SimpleDateFormat("yyyy年MM月dd日");
		}
		Calendar cal = Calendar.getInstance();
		int day = cal.get(Calendar.DAY_OF_WEEK);
		String weekDay = null;
		switch(day){
			case Calendar.MONDAY: weekDay=Struts2Utils.getText("Monday");break;
			case Calendar.TUESDAY: weekDay=Struts2Utils.getText("Tuesday");break;
			case Calendar.WEDNESDAY: weekDay=Struts2Utils.getText("Wednesday");break;
			case Calendar.THURSDAY: weekDay=Struts2Utils.getText("Thursday");break;
			case Calendar.FRIDAY: weekDay=Struts2Utils.getText("Friday");break;
			case Calendar.SATURDAY: weekDay=Struts2Utils.getText("Saturday");break;
			case Calendar.SUNDAY: weekDay=Struts2Utils.getText("Sunday");break;
		}
		return fmt.format(cal.getTime())+"  "+weekDay;
	}
	
	@Override
	public int doEndTag() throws JspException {
		return Tag.EVAL_PAGE;
	}

	public Integer getShowNum() {
		return showNum;
	}

	public void setShowNum(Integer showNum) {
		this.showNum = showNum;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setThemeChagable(boolean themeChagable) {
		this.themeChagable = themeChagable;
	}

	public void setExistable(boolean existable) {
		this.existable = existable;
	}
	public void setChangeType(String changeType) {
		this.changeType = changeType;
	}

	public void setDateVisible(boolean dateVisible) {
		this.dateVisible = dateVisible;
	}

	public void setPasswordVisible(boolean passwordVisible) {
		this.passwordVisible = passwordVisible;
	}

	public void setLanguageVisible(boolean languageVisible) {
		this.languageVisible = languageVisible;
	}

	public void setHelloVisible(boolean helloVisible) {
		this.helloVisible = helloVisible;
	}
}
