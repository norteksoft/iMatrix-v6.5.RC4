package org.jwebap.ui.controler;

import javax.servlet.http.HttpServletRequest;

/**
 * 抽象Dispatcher
 * 
 * @author leadyu(yu-lead@163.com)
 * @since Jwebap 0.5
 * @date 2008-1-10
 */
public abstract class AbstractDispatcher implements Dispatcher {

	protected DispatcherContext context = null;

	public void ini(DispatcherContext context) {
		this.context = context;
	}

	public DispatcherContext getContext() {
		return context;
	}

	protected String getSubPath(HttpServletRequest request) {
		String path = request.getPathInfo();
		String dispatcherPath = context.getDispatcherPath();
		if (path == null) {
			return null;
		}
		path = path.substring(dispatcherPath.length());
		return path.startsWith("/")?path:"/"+path;
	}

}
