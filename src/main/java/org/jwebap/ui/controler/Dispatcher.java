package org.jwebap.ui.controler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Jwebap Web Console 请求分发器 处理自己能够处理的URL请求，不能处理的调用Dispatcher.doChain
 * 
 * @link DispatcherChain
 * @author leadyu(yu-lead@163.com)
 * @since Jwebap 0.5
 * @date Nov 30, 2007
 */
public interface Dispatcher {

	public void dispatch(HttpServletRequest request,
			HttpServletResponse response, DispatcherChain chain)
			throws Exception;
	
	public void ini(DispatcherContext context);
	
	public DispatcherContext getContext();

}
