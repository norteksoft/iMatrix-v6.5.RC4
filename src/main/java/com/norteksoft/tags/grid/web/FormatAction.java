package com.norteksoft.tags.grid.web;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;

import com.norteksoft.acs.base.web.struts2.CRUDActionSupport;
import com.norteksoft.mms.module.entity.Menu;
import com.norteksoft.mms.module.service.MenuManager;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.JsonParser;
import com.norteksoft.product.util.ParameterUtils;
import com.norteksoft.product.util.PropUtils;
import com.norteksoft.product.util.ThreadParameters;
import com.norteksoft.product.util.tree.ZTreeNode;
import com.norteksoft.product.web.struts2.Struts2Utils;

@Namespace("/grid")
@ParentPackage("default")
public class FormatAction extends CRUDActionSupport{
	private static final long serialVersionUID = 1L;
	private String currentInputId;
	private String thirdMenuCode;//三级菜单编码
	private Long companyId;//公司id
	/**
	 * 弹出格式设置
	 * @return
	 * @throws Exception
	 */
	public String format() throws Exception {
		String resourceCtx=PropUtils.getProp("host.resources");
		HttpServletRequest request=Struts2Utils.getRequest();
		request.setAttribute("resourceCtx",resourceCtx);
		String language = ContextUtils.getCurrentLanguage();
		request.setAttribute("confirmButton",Struts2Utils.getText("do.query",language));
		request.setAttribute("emptyButton",Struts2Utils.getText("portal.empty",language));
		request.setAttribute("formatClassification",Struts2Utils.getText("format.classification",language));
		request.setAttribute("formatNumericalValue",Struts2Utils.getText("format.numerical.value",language));
		request.setAttribute("formatCurrency",Struts2Utils.getText("format.currency",language));
		request.setAttribute("orderDate",Struts2Utils.getText("order.date",language));
		request.setAttribute("formatTime",Struts2Utils.getText("format.time",language));
		request.setAttribute("formatPercentage",Struts2Utils.getText("format.percentage",language));
		request.setAttribute("formatCustom",Struts2Utils.getText("format.custom",language));
		request.setAttribute("formatDecimalDigits",Struts2Utils.getText("format.decimalDigits",language));
		request.setAttribute("formatUsingThousandSeparator",Struts2Utils.getText("format.usingThousandSeparator",language));
		request.setAttribute("formatCurrencySymbol",Struts2Utils.getText("format.currencySymbol",language));
		request.setAttribute("formatType",Struts2Utils.getText("format.type",language));
		request.setAttribute("formatFormat",Struts2Utils.getText("format.format",language));
		request.setAttribute("formatNonFormat",Struts2Utils.getText("format.nonFormat",language));
		
		return "success";
	}
	//切换三级菜单时拼接其子菜单
	public String refreshFourMenuTree() throws Exception {
		ThreadParameters parameters = new ThreadParameters(companyId);
		ParameterUtils.setParameters(parameters);
		MenuManager menuManager = (MenuManager)ContextUtils.getBean("menuManager");
		List<Long> systemIds = menuManager.getSubsciberSystemIds();
		Menu thirdMenu = menuManager.getMenuByCode(thirdMenuCode);
		Menu firstMenu = menuManager.getMenuByCode(ContextUtils.getSystemCode());
		List<Menu> treeMenus = new ArrayList<Menu>();
		List<Menu> fourMenus=new ArrayList<Menu>();
		List<Menu> hasAuthFourMenus=new ArrayList<Menu>();
		Long thirdMenuId = null;
		if(thirdMenu!=null){
			thirdMenuId = thirdMenu.getId();
			treeMenus = menuManager.getThirdMenusChildren(thirdMenuId,systemIds);
			if(treeMenus.isEmpty()){//表示不需要拼ztree树才需要查找四级菜单集合
				fourMenus = menuManager.getEnableMenuByLayer(4,thirdMenuId);
				hasAuthFourMenus = menuManager.getHasAuthMenus(fourMenus,firstMenu,systemIds);
			}
		}
		List<ZTreeNode> ztreeNodes = menuManager.getHasAuthTreeMenus(treeMenus,firstMenu,systemIds);
		if(ztreeNodes.size()>0){
			this.renderText("true:"+JsonParser.object2Json(ztreeNodes));//表示以树的形式显示4级及5级等等菜单
		}else{
			String iframeName = PropUtils.getMenuIframeName();
			StringBuilder fourMenuInfos = new StringBuilder();
			if(hasAuthFourMenus.size()>0){
				for(Menu menu:hasAuthFourMenus){
					menuManager.setMenuNameToi18n(menu);//菜单名称国际化
					if(StringUtils.isEmpty(menu.getEvent())){
						fourMenuInfos.append("<div class=\"four-menu\" menuInfo=\""+menu.getLayer()+"_"+menu.getCode()+"\">");
						fourMenuInfos.append("<a href=\""+menu.getMenuUrl()+"\" ");
						if(menu.getExternalable()){
							fourMenuInfos.append("target=\"_blank\"");
						}else if(menu.getIframable()){
							fourMenuInfos.append("target=\""+iframeName+"\"")
							.append(" onclick=\"__setSelectClass('4','"+menu.getCode()+"');\"");
						}
						fourMenuInfos.append(">"+getInternation(menu.getName())+"</a>");
						fourMenuInfos.append("</div>");
					}else{
						fourMenuInfos.append("<div class=\"four-menu\" menuInfo=\""+menu.getLayer()+"_"+menu.getCode()+"\">");
						fourMenuInfos.append("<a href=\"#this\" onclick=\""+menu.getEvent()+"('"+menu.getMenuUrl()+"','"+menu.getName()+"')\">"+getInternation(menu.getName())+"</a>");
						fourMenuInfos.append("</div>");
					}
					
				}
			}else{
				fourMenuInfos.append("<div class=\"demo\" id=\""+thirdMenuCode+"_content\" style=\"margin-top: 10px;\"></div>");
			}
			this.renderText("false:"+fourMenuInfos.toString());//表示不以树的形式显示4级及5级等等菜单
		}
		return null;
	}
	
	public String getCurrentInputId() {
		return currentInputId;
	} 
	public String getInternation(String code){
		MenuManager menuManager = (MenuManager)ContextUtils.getBean("menuManager");
		 return menuManager.getNameToi18n(code);
	 }

	public void setCurrentInputId(String currentInputId) {
		this.currentInputId = currentInputId;
	}

	@Override
	public String delete() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String list() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void prepareModel() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String save() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getModel() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getThirdMenuCode() {
		return thirdMenuCode;
	}

	public void setThirdMenuCode(String thirdMenuCode) {
		this.thirdMenuCode = thirdMenuCode;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

}
