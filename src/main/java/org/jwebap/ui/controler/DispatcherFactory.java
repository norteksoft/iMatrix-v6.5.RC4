package org.jwebap.ui.controler;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.jwebap.cfg.model.DispatcherDef;
import org.jwebap.cfg.model.JwebapDef;
import org.jwebap.util.BeanUtil;

/**
 * 分发器工厂
 * 
 * @author leadyu(yu-lead@163.com)
 * @since Jwebap 0.5
 * @date Dec 2, 2007
 */
public class DispatcherFactory {

	private JwebapDef _config;

	public DispatcherFactory(JwebapDef config) {
		_config = config;
	}

	/**
	 * 创建分发器处理链
	 * @param request
	 * @return
	 */
	public DispatcherChain createDispatcherChain(HttpServletRequest request) {
		DispatcherChainImpl chain = new DispatcherChainImpl();

		Collection dispatchers = _config.getDispatcherDefs();
		Iterator dispatcherIt=dispatchers.iterator();
		
		while(dispatcherIt.hasNext()) {
			DispatcherDef dispatcherDef = (DispatcherDef) dispatcherIt.next();

			if (matchDispatcher(request, dispatcherDef)) {
				Dispatcher dispatcher = dispatcherDef.getDispatcher();

				if (dispatcher == null) {
					try {
						dispatcher = (Dispatcher) BeanUtil
								.newInstance(dispatcherDef.getType());
						dispatcher
								.ini(new DispatcherContextImpl(dispatcherDef));
						dispatcherDef.setDispatcher(dispatcher);
					} catch (Exception e) {
						throw new DispatcherException("dispatcher "
								+ dispatcherDef.getType() + " 初始化错误.", e);
					}
				}
				chain.addDispatcher(dispatcher);

			}

		}
		return chain;
	}

	/**
	 * 返回当前请求是否匹配分发器
	 * @param request
	 * @param dispatcherDef
	 * @return
	 */
	private boolean matchDispatcher(HttpServletRequest request,
			DispatcherDef dispatcherDef) {
		String path = request.getPathInfo();
		if(path==null)path="";
		Mapper mapping = dispatcherDef.getMapper();

		String dispatcherPath = mapping.getMappingPath();

		return path.startsWith(dispatcherPath);
	}
}
