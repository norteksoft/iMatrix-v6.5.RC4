package com.norteksoft.acs.web.organization;

import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;

import com.norteksoft.acs.base.web.struts2.CRUDActionSupport;
import com.norteksoft.portal.entity.UserCurrentLanguage;
import com.norteksoft.portal.service.UserCurrentLanguageManager;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.web.struts2.Struts2Utils;
import com.opensymphony.xwork2.ActionContext;

@Namespace("/organization")
@ParentPackage("default")
@Results( { @Result(name = CRUDActionSupport.RELOAD, location = "company", type = "redirectAction")})
public class LanguageAction extends CRUDActionSupport<Object> {
	private static final long serialVersionUID = 1L;
	@Autowired
	private UserCurrentLanguageManager userCurrentLanguageManager;
	private String lanague;
	
	public String getLanague() {
		return lanague;
	}

	public void setLanague(String lanague) {
		this.lanague = lanague;
	}

	@Action("organization-change-lanague")
	public String changeLanague() throws Exception {
		if(StringUtils.isNotEmpty(url)){
			if(url.endsWith("#")) url = url.replace("#", "");
			if(!url.contains("_r=1")){
				if(url.contains("?")){
					//交换_r和第二个参数的位置，否则会报脚本错误，暂时没有找到原因
					if(url.contains("&")){
						String resultUrl = url.substring(0,url.indexOf("&"));
						resultUrl = resultUrl + "&_r=1";
						url = resultUrl+url.substring(url.indexOf("&")+1);
					}else{
						url = url + "&_r=1";
					}
				}else{
					url = url + "?_r=1";
				}
			}
		}
		
		//String callback=Struts2Utils.getParameter("callback");//跨域请求需用此参数
		Locale currentLocale = Locale.getDefault();
		// 1、根据页面请求，创建不同的Locale对象
		if ("en_US".equals(lanague.trim())) {
			currentLocale = new Locale("en", "US");
		} else if ("zh_CN".equals(lanague.trim())) {
			currentLocale = new Locale("zh", "CN");
		}
		//保存/更新用户设置的语言
		setUserCurrentLanguage(currentLocale);
		/*
		 * 2、设置Action中的Locale 前台页面的Locale和后台session中的Locale范围是不一样的
		 * a)只改页面Locale当前页面信息会改变但提交后Locale又会改回到默认的
		 * b)改变了后台Locale，当前线程中的页面Locale并不会改变，但会随下一次提交
		 * Action一同改变，所以可能要刷新页面两次，第一次只变后台Locale，第二次 前台和后台同时改变
		 * 为避免上述情况，需要前台和后台的Locale一起改变
		 */
		ActionContext.getContext().setLocale(currentLocale);
		ServletActionContext.getRequest().getSession().setAttribute("WW_TRANS_I18N_LOCALE", currentLocale);
		Struts2Utils.getResponse().sendRedirect(url);
		//this.renderText(callback+"({data:'"+lanague+"'})");
		return null;
	}
	/**
	 * 保存/更新用户设置的语言
	 * @param currentLocale
	 */
	private void setUserCurrentLanguage(Locale currentLocale) {
		UserCurrentLanguage userLanguage = userCurrentLanguageManager
				.getUserLanguageByUid(ContextUtils.getUserId(), ContextUtils.getCompanyId());
		if (null == userLanguage) {
			userLanguage = new UserCurrentLanguage();
			userLanguage.setUserId(ContextUtils.getUserId());
		}
		userLanguage.setCurrentLanguage(currentLocale.toString());
		userCurrentLanguageManager.save(userLanguage);
	}

	public String delete() throws Exception {
		return null;
	}

	@Override
	public String list() throws Exception {
		return null;
	}

	@Override
	protected void prepareModel() throws Exception {

	}

	@Override
	public String save() throws Exception {
		return null;
	}

	public Object getModel() {
		return null;
	}
	private String url;
	public void setUrl(String url) {
		this.url = url;
	}
	public String getUrl() {
		return url;
	}
}
