package org.jwebap.ui.controler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 组件视图处理Action
 * 
 * @author leadyu(yu-lead@163.com)
 * @since Jwebap 0.5
 * @date  2007-12-14
 */
public abstract class Action {
	
	/**
	 * Action 对应的上下文
	 */
	private ActionContext actionContext=null;
	
	public void setActionContext(ActionContext context){
		actionContext=context;
	}
	
	public ActionContext getActionContext(){
		return actionContext;
	}
	
	/**
	 * 视图处理,你可以通过getActionContext()获得当前Action上下文，包括当前组件的上下文
	 * 
	 * @param context
	 * @throws Exception
	 */
	public abstract void process(HttpServletRequest request, HttpServletResponse response)throws Exception;

}
