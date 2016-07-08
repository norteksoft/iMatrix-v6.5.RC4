package com.norteksoft.acs.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.entity.BusinessSystem;
import com.norteksoft.product.util.PropUtils;
import com.norteksoft.product.util.SystemUrls;

public class CustomSubSystemEntranceRedirectFilter implements Filter {
	
	public void destroy() { }

	public void doFilter(ServletRequest req, ServletResponse rep,
			FilterChain chan) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) rep;
		
		String url=request.getRequestURI();
		if(url.endsWith("/")){
			url=url.substring(0,url.lastIndexOf("/"));
		}
		String systemCode=url.substring(url.lastIndexOf("/")+1);
		boolean isSubSystem = false;
		String redirectUrl=PropUtils.getProp("customRedirectUrl.properties",systemCode);
		//重定向到平台提供的action中，在该action中获得第一个有权限的菜单，并再次重定向到该有权限的菜单中
		String resultUrl = SystemUrls.getSystemPageUrl("imatrix")+"/portal/index/redirect-into-subSystem.htm?code="+systemCode;
		if(StringUtils.isEmpty(redirectUrl)){//表示属性文件中没有配置该系统的访问路径，则判断是否是子系统，并在菜单管理中获得第一个有权限的菜单
			BusinessSystem system = ApiFactory.getAcsService().getSystemByCode(systemCode);
			if(system!=null&&StringUtils.isNotEmpty(system.getParentCode())){//表示是子系统
				isSubSystem = true;
			}
		}else{//如果属性文件中配置了子系统的访问路径，则将该配置的路径传给平台，由平台判断是否有该路径的权限，再决定是否可以重定向到该地址
			isSubSystem = true;
			resultUrl = resultUrl + "&url="+redirectUrl;
		}
		if(isSubSystem){
			response.sendRedirect(resultUrl);
		}else{
			chan.doFilter(req, rep);
		}
	}

	public void init(FilterConfig arg0) throws ServletException { }
}
