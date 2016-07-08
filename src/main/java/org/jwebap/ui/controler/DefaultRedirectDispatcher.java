package org.jwebap.ui.controler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 默认路径分发器，当jwebap请求路径为:/,请求默认分发/console/
 * 
 * @author leadyu(yu-lead@163.com)
 * @since Jwebap 0.5
 * @date 2008-1-14
 */
public class DefaultRedirectDispatcher extends AbstractDispatcher {

	public void dispatch(HttpServletRequest request,
			HttpServletResponse response, DispatcherChain chain)
			throws Exception {
		String path = getSubPath(request);
		
		
		String query  = "?" + request.getQueryString();
		
		if(path==null){
			response.sendRedirect(request.getRequestURI()+"/console/" + query);
		}else if ("".equals(path) || "/".equals(path)) {
			response.sendRedirect("console/" + query);
		}

		chain.doChain(request, response);

	}

}
