package org.jwebap.ui.controler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Action请求处理器
 * 
 * @author leadyu(yu-lead@163.com)
 * @since Jwebap 0.5
 * @date 2007-12-14
 */
public class ActionProcesser {


	protected ActionFactory _factory;

	public ActionProcesser(ActionFactory factory) {
		_factory = factory;
	}

	public void process(String path,HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		if(path==null){
			throw new ActionException("action path "+path+" 没有找到.");
		}
		
		Action action = createAction(path);

		action.process(request, response);
	}

	protected Action createAction(String path)throws ActionNotFoundException{
		Action action = _factory.getAction(path);
		return action;
	}

}
