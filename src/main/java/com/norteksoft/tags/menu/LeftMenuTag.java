package com.norteksoft.tags.menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.norteksoft.acs.service.AcsUtils;
import com.norteksoft.acs.service.security.SecurityResourceCache;
import com.norteksoft.mms.module.entity.Menu;
import com.norteksoft.mms.module.service.MenuManager;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.JsonParser;
import com.norteksoft.product.util.PropUtils;
import com.norteksoft.product.util.SystemUrls;
import com.norteksoft.product.util.freemarker.TagUtil;

public class LeftMenuTag extends TagSupport {
	private static final long serialVersionUID = 1L;
	private Log log = LogFactory.getLog(LeftMenuTag.class);
	private MenuManager menuManager;
	private Long thirdMenuId;
	private Long fourMenuId;
	private Long menuId;
	private String selectMenuInfo="";//json格式的字符串，[{菜单实体属性},{菜单实体},...]
	private List<Menu> selectMenus = new ArrayList<Menu>();//选中的菜单列表
	private List<Long> systemIds = new ArrayList<Long>();//订单中选中的系统id的集合
	private Menu firstMenu;
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
				selectMenuInfo = JsonParser.object2Json(selectMenus);
			}
			 JspWriter out=pageContext.getOut(); 
			 out.print(readScriptTemplate());
		} catch (Exception e) {
			log.error(e);
			throw new JspException(e);
		}
		return Tag.EVAL_PAGE;
	}
	
	@Override
	public int doEndTag() throws JspException {
		return Tag.EVAL_PAGE;
	}
	
	//读取脚本模板
	private String readScriptTemplate() throws Exception{
		systemIds = menuManager.getSubsciberSystemIds();
		String imatrixUrl=SystemUrls.getSystemPageUrl("imatrix");
		Map<String, Object> root=new HashMap<String, Object>();
		menuManager.setLeftMenu(root,selectMenus,firstMenu,systemIds);
		root.put("companyId", ContextUtils.getCompanyId());
		String resourcesCtx = PropUtils.getProp("host.resources");
		root.put("resourcesCtx",resourcesCtx==null?"":resourcesCtx );
		root.put("imatrixUrl", imatrixUrl);
		root.put("selectMenuInfo",selectMenuInfo );
		root.put("menuId", menuId);
		root.put("iframeName",PropUtils.getMenuIframeName() );
		String result = TagUtil.getContent(root, "menu/leftMenuTag.ftl");
		return result;
	}
	
	public void setThirdMenuId(Long thirdMenuId) {
		this.thirdMenuId = thirdMenuId;
	}
	public Long getThirdMenuId() {
		return thirdMenuId;
	}
	public void setFourMenuId(Long fourMenuId) {
		this.fourMenuId = fourMenuId;
	}
	public Long getFourMenuId() {
		return fourMenuId;
	}
}
