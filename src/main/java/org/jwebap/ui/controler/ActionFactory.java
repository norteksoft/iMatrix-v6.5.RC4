package org.jwebap.ui.controler;

import java.util.HashMap;
import java.util.Map;

import org.jwebap.cfg.model.ActionDef;
import org.jwebap.cfg.model.ComponentDef;
import org.jwebap.cfg.model.JwebapDef;

/**
 * 视图Action工厂
 * 
 * @author leadyu(yu-lead@163.com)
 * @since Jwebap 0.5
 * @date Dec 16, 2007
 */
public class ActionFactory {

	private JwebapDef _config;

	private Map actions;

	public ActionFactory(JwebapDef config) {
		_config = config;
		actions = new HashMap();
	}

	public Action getAction(String path) throws ActionNotFoundException {

		Action action = (Action) actions.get(path);

		if (action != null) {
			return action;
		}

		ActionDef actionDef = _config.getActionDef(path);
		if (actionDef == null) {
			throw new ActionNotFoundException("Action is not found:" + path);
		}

		String clazz = actionDef.getType();
		try {
			action = (Action) newInstance(clazz);
		} catch (Exception e) {
			throw new ActionNotFoundException("Action class is not found:" + clazz, e);
		}

		String componentName = actionDef.getComponent();
		ComponentDef componentDef = null;
		if(componentName!=null && !"".equals(componentName)){
			componentDef = _config.getComponentDef(componentName);
		}

		if (componentDef != null && componentDef.getContext() != null) {
			action.setActionContext(new TemplateActionContext(componentDef
					.getContext(), actionDef));
		} else {
			action.setActionContext(new TemplateActionContext(actionDef));
		}
		actions.put(path, action);

		return action;
	}

	private Object newInstance(String className) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException {
		/*
		 * 由于Action类可能由第三方实现，部署时未必就放在和jwebap.jar同样层级的类路径里， 所以不能使用Class.forName
		 */
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		Class actionClz = loader.loadClass(className);
		Object o = actionClz.newInstance();

		return o;
	}
}
