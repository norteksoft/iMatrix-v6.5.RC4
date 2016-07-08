package org.jwebap.ui.controler;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 分发器处理链，通过多级分发器，可以细化分发器的职责，处理自己能处理的URL，
 * 同时也为对请求进行拦截 提供手段
 * 
 * @author leadyu(yu-lead@163.com)
 * @since Jwebap 0.5
 * @date Dec 2, 2007
 */
public class DispatcherChainImpl implements DispatcherChain {

	private List dispatchers = new ArrayList();
	int pos=0;
	
	/**
	 * 交由下级dispatcher处理
	 * @throws Exception 
	 */
	public void doChain(HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(pos<dispatchers.size()){
			Dispatcher dispatcher=(Dispatcher)dispatchers.get(pos++);
			dispatcher.dispatch(request, response, this);
		}
	}

	protected void addDispatcher(Dispatcher dispatcher) {
		dispatchers.add(dispatcher);
	}
}
