package org.jwebap.cfg.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * plugin配置定义
 * 
 * plugin的配置存放在jar包的Meta-INF目录的plugin.xml中,包含*componentDef,*dispatcherDef,*actionDef
 * 
 * @author leadyu(yu-lead@163.com)
 * @since Jwebap 0.5
 * @date 2008-4-6
 */
public class PluginDef {
	/**
	 * 分发器定义
	 */
	private List dispatcherDefs = null;
	
	/**
	 * plug-in组件定义
	 */
	private List componentDefs = null;

	private List actionDefs = null;

	public PluginDef() {
		dispatcherDefs = new ArrayList();
		componentDefs = new ArrayList();
		actionDefs = new ArrayList();
	}

	public void addComponentDef(ComponentDef component) {
		componentDefs.add(component);
	}
	
	/**
	 * 根据名称返回组件定义
	 * @param name
	 * @return
	 */
	public ComponentDef getComponentDef(String name){
		for(int i=0;i<componentDefs.size();i++){
			ComponentDef def=(ComponentDef)componentDefs.get(i);
			if(def.getName()!=null && def.getName().equals(name)){
				return def;
			}
		}
		return null;
	}
	
	public Collection getComponentDefs() {
		return componentDefs;
	}

	public void addDispatcherDef(DispatcherDef dispatcher) {
		dispatcherDefs.add(dispatcher);
	}

	/**
	 * 根据名称返回分发器定义
	 * @param name
	 * @return
	 */
	public DispatcherDef getDispatcherDef(String name){
		for(int i=0;i<dispatcherDefs.size();i++){
			DispatcherDef def=(DispatcherDef)dispatcherDefs.get(i);
			if(def.getName()!=null && def.getName().equals(name)){
				return def;
			}
		}
		return null;
	}
	
	public Collection getDispatcherDefs() {
		return dispatcherDefs;
	}

	public void addActionDef(ActionDef action) {
		actionDefs.add(action);
	}

	public Collection getActionDefs() {
		return actionDefs;
	}

	/**
	 * 根据名称返回action定义
	 * @param name
	 * @return
	 */
	public ActionDef getActionDef(String path){
		for(int i=0;i<actionDefs.size();i++){
			ActionDef def=(ActionDef)actionDefs.get(i);
			if(def.getPath()!=null && def.getPath().equals(path)){
				return def;
			}
		}
		return null;
	}
	
}
