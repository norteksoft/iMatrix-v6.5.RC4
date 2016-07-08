package com.norteksoft.acs.web.filter;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.security.context.SecurityContextImpl;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.norteksoft.acs.entity.security.User;
import com.norteksoft.portal.service.UserCurrentLanguageManager;

public class SetUserLanagueFilter implements Filter {
	private static final String DEFAULT_LANGUAGE = "zh_CN";
	private UserCurrentLanguageManager userCurrentLanguageManager;

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		SecurityContextImpl context = (SecurityContextImpl) req.getSession().getAttribute("SPRING_SECURITY_CONTEXT");
		String currentLanguage = "";
		if (context != null) {
			User user = (User) context.getAuthentication().getPrincipal();
			currentLanguage = userCurrentLanguageManager.getUserLanguageByUserId(user.getUserId(), user.getCompanyId());
		}
		if (StringUtils.isEmpty(currentLanguage)) {
			currentLanguage = DEFAULT_LANGUAGE;
		}
		Locale currentLocale = Locale.getDefault();
		if ("en_US".equals(currentLanguage)) {
			currentLocale = new Locale("en", "US");
		} else if ("zh_CN".equals(currentLanguage)) {
			currentLocale = new Locale("zh", "CN");
		}
		req.getSession().setAttribute("WW_TRANS_I18N_LOCALE", currentLocale);
		chain.doFilter(request, response);
	}

	public void destroy() {
	}

	public void init(FilterConfig filterConfig) throws ServletException {
		ServletContext context = filterConfig.getServletContext();
		ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(context);
		userCurrentLanguageManager = (UserCurrentLanguageManager) ctx.getBean("userCurrentLanguageManager");

	}

	public UserCurrentLanguageManager getUserCurrentLanguageManager() {
		return userCurrentLanguageManager;
	}

	public void setUserCurrentLanguageManager(UserCurrentLanguageManager userCurrentLanguageManager) {
		this.userCurrentLanguageManager = userCurrentLanguageManager;
	}
	
	

}