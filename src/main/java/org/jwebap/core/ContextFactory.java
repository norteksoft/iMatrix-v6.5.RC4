package org.jwebap.core;

import org.jwebap.cfg.model.ComponentDef;
import org.jwebap.cfg.model.JwebapDef;
import org.jwebap.cfg.persist.PersistManager;

/**
 * 上下文工厂
 * 
 * @author leadyu
 * @since Jwebap 0.5
 * @date Aug 7, 2007
 */
public class ContextFactory {

	/**
	 * 创建组件上下文，它的父上下文为RuntimeContext
	 * 
	 * @param container
	 * @param parent
	 * @param def
	 * @return
	 */
	public ComponentContext createComponentContext(
			TraceLiftcycleManager container, Context parent,
			ComponentDef def) {
		return new StandardComponentContext(container, parent, def);
	}

	/**
	 * 创建RuntimeContext,一个RuntimeContext代表一个jwebap实例
	 * 
	 * @param container
	 * @return
	 */
	public RuntimeContext createRuntimeContext(JwebapDef config,PersistManager defManager) {
		return new RuntimeContext(config,new TraceContainer(), defManager,this);
	}

}
