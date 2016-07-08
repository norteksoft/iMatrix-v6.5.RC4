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

import com.norteksoft.acs.entity.authorization.BusinessSystem;
import com.norteksoft.acs.service.authorization.BusinessSystemManager;
import com.norteksoft.acs.service.security.SecurityResourceCache;
import com.norteksoft.mms.module.entity.Menu;
import com.norteksoft.mms.module.service.MenuManager;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.PropUtils;
import com.norteksoft.product.util.SystemUrls;
import com.norteksoft.product.util.freemarker.TagUtil;

public class SecondMenuTag extends TagSupport {
	private static final long serialVersionUID = 1L;
	private Log log = LogFactory.getLog(SecondMenuTag.class);
	private MenuManager menuManager;
	private Long menuId;
	private Long secondMenuId;
	private String code;
	private BusinessSystemManager businessSystemManager;
	private List<Long> systemIds = new ArrayList<Long>();//订单中选中的系统id的集合
	@Override
	public int doStartTag() throws JspException {
		try {
			menuManager=(MenuManager)ContextUtils.getBean("menuManager");
			String systemCode=ContextUtils.getSystemCode();
			String currentCode = StringUtils.isEmpty(code)?systemCode:code;
			String lastMenuIdStr=pageContext.getRequest().getParameter("menuId");
			if(lastMenuIdStr == null){
				if(pageContext.getRequest().getAttribute("menuId")!=null){
					lastMenuIdStr =  pageContext.getRequest().getAttribute("menuId").toString();
				}else{
					Menu lastMenu=menuManager.getDefaultMenuByLayer(currentCode);
					if(lastMenu!=null){
						lastMenuIdStr =  lastMenu.getId().toString();
					}
				}
			}
			if(lastMenuIdStr!=null){
				Menu secondMenu=menuManager.getDefaultSelectMenuByLayer(2,Long.parseLong(lastMenuIdStr));
				if(secondMenu!=null)secondMenuId=secondMenu.getId();
				menuId=Long.parseLong(lastMenuIdStr);
			}
			 JspWriter out=pageContext.getOut(); 
			 out.print(readScriptTemplate(currentCode));
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
	private String readScriptTemplate(String currentCode) throws Exception{
		systemIds = menuManager.getSubsciberSystemIds();
		Map<String, Object> root=new HashMap<String, Object>();
		Menu firstMenu=menuManager.getRootMenu(menuId);
		List<Menu> secMenus=menuManager.getEnableMenuByLayer(2,firstMenu.getId());
		
		List<Menu> resultMenus = menuManager.setParamsForSecondMenu(root,firstMenu,secMenus,systemIds);
		if(secondMenuId!=null){
			root.put("secondMenuId", secondMenuId);
		}else{
			if(resultMenus.size()>0){
				root.put("secondMenuId", resultMenus.get(0).getId());
			}
		}
		root.put("iframeName",PropUtils.getMenuIframeName() );
		String result = TagUtil.getContent(root, "menu/secondMenuTag.ftl");
		return result;
	}
	public Long getMenuId() {
		return menuId;
	}
	public void setMenuId(Long menuId) {
		this.menuId = menuId;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
}
