package com.norteksoft.tags.menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.norteksoft.mms.module.entity.Menu;
import com.norteksoft.mms.module.service.MenuManager;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.SystemUrls;
import com.norteksoft.product.util.freemarker.TagUtil;
import com.norteksoft.product.web.struts2.Struts2Utils;

public class MenuTag extends TagSupport {
	private static final long serialVersionUID = 1L;
	private Log log = LogFactory.getLog(MenuTag.class);
	private MenuManager menuManager;
	private String imgSrc;
	private Long menuId;
	private Integer showNum=1;
	private String code;//系统code
	private List<Long> systemIds = new ArrayList<Long>();//订单中选中的系统id的集合
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
						Menu tempMenu=menuManager.getMenuByCode(tempCode);
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
			 JspWriter out=pageContext.getOut(); 
			 out.print(readScriptTemplate());
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
		
		
		Long systemId=ContextUtils.getSystemId("portal");
		if(menuId!=null){
			Menu menu = menuManager.getRootMenu(menuId);
			firstMenu = menu.clone();
		}
		root.put("imgSrc", imgSrc==null?"":imgSrc);
		if(menuId!=null){
			root.put("firstMenuId", firstMenu.getId());
		}else{
			root.put("firstMenuId", 0l);
		}
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
		root.put("showNum", showNum);
		root.put("moreSystem", Struts2Utils.getText("more.menu"));
		root.put("menus", resultMenus);
		root.put("menuSize", resultMenus.size());
		root.put("systemId", systemId);
		root.put("ctx", webapp);
		root.put("imatrixUrl", imatrixUrl);
		String result = TagUtil.getContent(root, "menu/menuTag.ftl");
		return result;
	}
	
	@Override
	public int doEndTag() throws JspException {
		return Tag.EVAL_PAGE;
	}
	public void setImgSrc(String imgSrc) {
		this.imgSrc = imgSrc;
	}
	public String getImgSrc() {
		return imgSrc;
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
}
