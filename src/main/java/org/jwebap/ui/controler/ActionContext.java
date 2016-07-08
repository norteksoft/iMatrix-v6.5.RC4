package org.jwebap.ui.controler;

import org.jwebap.cfg.model.ActionDef;
import org.jwebap.core.Component;
import org.jwebap.core.ComponentContext;
import org.jwebap.core.Context;

/**
 * Action上下文，可以获取当前Action对应的Component组件。
 * 对于组件视图，组件容器的管理是透明的，视图所需要的环境数据都是通过ActionContext获得。
 * 
 * @author leadyu(yu-lead@163.com)
 * @since Jwebap 0.5
 * @date  2007-12-14
 */
public interface ActionContext extends Context{

	
	/**
	 * 得到当前Action对应的Component上下文
	 * @return
	 */
	public Component getComponent();
	
	/**
	 * 得到Action对应的组件的上下文
	 * @return
	 */
	public ComponentContext getComponentContext();

	public ActionDef getActionDefinition();
}
