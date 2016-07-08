package com.norteksoft.product.web.struts2;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;

import com.norteksoft.product.util.ParameterUtils;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

public class MethodIterceptor extends 	AbstractInterceptor{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String intercept(ActionInvocation invocation) throws Exception {
		String actionName = invocation.getAction().toString();
		actionName = actionName.substring(0,actionName.indexOf("@"));
		String uriName = null;
		HttpServletRequest request = (HttpServletRequest)invocation.getInvocationContext().get(ServletActionContext.HTTP_REQUEST);
		if(request!=null){
			String uri = request.getRequestURI();
			if(uri.indexOf("/")>=0){
				String[] uris = uri.split("/");
				if(uris.length>=2)uriName = "/"+uris[uris.length-2]+"/"+uris[uris.length-1];
			}
		}
		if(StringUtils.isNotEmpty(uriName)){
			ParameterUtils.setClassMethodName(uriName);
		}
		return invocation.invoke();
	}

}
