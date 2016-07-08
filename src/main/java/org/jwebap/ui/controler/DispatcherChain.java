package org.jwebap.ui.controler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 分发器责任链,具有相同dispatch mapping的Dispatcher在同一个处理链中，
 * 各自处理自己能够处理的URL请求，不能处理的调用doChain
 * 
 * @author leadyu(yu-lead@163.com)
 * @since Jwebap 0.5
 * @date  Nov 30, 2007
 */
public interface DispatcherChain {
	
	public void doChain(HttpServletRequest request, HttpServletResponse response)throws Exception;

}
