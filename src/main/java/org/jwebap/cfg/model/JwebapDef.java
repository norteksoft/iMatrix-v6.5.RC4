package org.jwebap.cfg.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Jwebap配置
 * 
 * 包含了plugin的引用，同时，内含了一份虚拟的plugin配置，保存于jwebap.xml，其中的配置可以覆盖各plugin对应的配置。
 * 用于在界面部署时，保存各plugin的临时配置。
 * 
 * @author leadyu(yu-lead@163.com)
 * @since Jwebap 0.5
 * @date 2008-4-6
 */
public class JwebapDef implements Cloneable{

	/**
	 * 内部配置定义
	 */
	private PluginDef _internalDef = null;

	/**
	 * plugin定义引用
	 * 
	 * @see PluginDefRef
	 */
	private List _plugins = null;


	public JwebapDef() {
		_internalDef = new PluginDef();
		_plugins=new ArrayList();
	}

	public JwebapDef(PluginDef internalDef,List plugins) {
		_internalDef = internalDef;
		_plugins=plugins;
	}

	public void addPluginDef(PluginDefRef plugin) {
		_plugins.add(plugin);
	}

	public PluginDefRef getPluginDef(String name) {
		for(int i=0;i<_plugins.size();i++){
			PluginDefRef ref=(PluginDefRef)_plugins.get(i);
			if(ref.getName()!=null && ref.getName().equals(name)){
				return ref;
			}
		}
		return null;
	}
	
	/**
	 * 移除插件
	 * @param name
	 * @return
	 */
	public PluginDefRef removePluginDef(String name) {
		for(int i=0;i<_plugins.size();i++){
			PluginDefRef ref=(PluginDefRef)_plugins.get(i);
			if(ref.getName()!=null && ref.getName().equals(name)){
				_plugins.remove(ref);
			}
		}
		return null;
	}
	
	public Collection getPluginDefs(){
		return _plugins;
	}
	
	/**
	 * 增加Component定义
	 * 
	 * 对应jwebap.xml的Component定义，其优先级高于plugin的Component定义
	 * 
	 * @param name
	 * @param component
	 */
	public void addComponentDef(ComponentDef component) {
		_internalDef.addComponentDef(component);
	}

	/**
	 * 获得Component定义
	 * 
	 * jwebap.xml定义的Component优先级高于plugin中的Component定义
	 * 
	 * @param name
	 * @return
	 */
	public ComponentDef getComponentDef(String name) {

		ComponentDef def = _internalDef.getComponentDef(name);
		if (def != null) {
			return def;
		}
		for (int i = 0; i < _plugins.size(); i++) {
			PluginDefRef pluginDef = (PluginDefRef)_plugins.get(i);
			if(pluginDef==null){
				continue;
			}
			def = pluginDef.getComponentDef(name);
			if (pluginDef != null)
				return def;

		}
		return def;
	}

	
	/**
	 * 获取component定义用以更新
	 * @param name
	 * @return
	 */
	public ComponentDef getComponentDefForUpdate(String name) {

		ComponentDef def = getComponentDef(name);
		ComponentDef internalComponentDef = _internalDef.getComponentDef(name);
		//component定义如果已被更新过，那么会从pluginDef中移到internalDef
		if (internalComponentDef == null) {
			addComponentDef(def);
		}
		return def;

	}
	
	/**
	 * 得到组件定义，配置在jwebap.xml
	 * @return
	 */
	public Collection getComponentDefs() {
		return _internalDef.getComponentDefs();
	}
	
	/**
	 * 获得所有组件定义
	 * @return
	 */
	public Collection getAllComponentDefs() {
		List components=new ArrayList();
		//JwebapDef中的组件定义优先级更高
		components.addAll(_internalDef.getComponentDefs());
		
		//获取所有插件
		Collection plugins=getPluginDefs();
		Iterator pluginIt=plugins.iterator();
		
		while(pluginIt.hasNext()){
			PluginDefRef ref =(PluginDefRef)pluginIt.next();
			Collection pluginCps=ref.getComponentDefs();
			Iterator pCpsIt=pluginCps.iterator();
			while(pCpsIt.hasNext()){
				ComponentDef def=(ComponentDef)pCpsIt.next();
				if(!components.contains(def)){
					components.add(def);
				}
			}
		}
		
		return components;
	}
	
	public void addDispatcherDef(DispatcherDef dispatcher) {
		_internalDef.addDispatcherDef(dispatcher);
	}

	/**
	 * 获得Dispatcher定义
	 * 
	 * jwebap.xml定义的Dispatcher优先级高于plugin中的Dispatcher定义
	 * 
	 * @param name
	 * @return
	 */
	public DispatcherDef getDispatcherDef(String name) {
		DispatcherDef def = _internalDef.getDispatcherDef(name);
		if (def != null) {
			return def;
		}
		for (int i = 0; i < _plugins.size(); i++) {
			PluginDefRef pluginDef = (PluginDefRef)_plugins.get(i);
			def = pluginDef.getDispatcherDef(name);
			if (pluginDef != null)
				return def;

		}
		return def;
	}

	/**
	 * 得到dispatcher定义，配置在jwebap.xml
	 * @return
	 */
	public Collection getDispatcherDefs() {
		return _internalDef.getDispatcherDefs();
	}
	
	/**
	 * 获得所有Dispatcher定义
	 * @return
	 */
	public Collection getAllDispatcherDefs() {
		List dispatchers=new ArrayList();
		//JwebapDef中的组件定义优先级更高
		dispatchers.addAll(_internalDef.getDispatcherDefs());
		
		//获取所有插件
		Collection plugins=getPluginDefs();
		Iterator pluginIt=plugins.iterator();
		
		while(pluginIt.hasNext()){
			PluginDefRef ref =(PluginDefRef)pluginIt.next();
			Collection pluginCps=ref.getDispatcherDefs();
			Iterator pCpsIt=pluginCps.iterator();
			while(pCpsIt.hasNext()){
				DispatcherDef def=(DispatcherDef)pCpsIt.next();
				if(!dispatchers.contains(def)){
					dispatchers.add(def);
				}
			}
		}
		
		return dispatchers;
	}
	
	public void addActionDef(ActionDef action) {
		_internalDef.addActionDef(action);
	}

	/**
	 * 获得Action定义
	 * 
	 * jwebap.xml定义的Action优先级高于plugin中的Action定义
	 * 
	 * @param name
	 * @return
	 */
	public ActionDef getActionDef(String name) {
		ActionDef def = _internalDef.getActionDef(name);
		if (def != null) {
			return def;
		}
		for (int i = 0; i < _plugins.size(); i++) {
			PluginDefRef pluginDef = (PluginDefRef)_plugins.get(i);
			def = pluginDef.getActionDef(name);
			if (pluginDef != null)
				return def;

		}
		return def;
	}
	
	/**
	 * 得到action定义，配置在jwebap.xml
	 * @return
	 */
	public Collection getActionDefs() {
		return _internalDef.getActionDefs();
	}
	
	public Object clone(){
		JwebapDef defCopy=new JwebapDef();
		
		return defCopy;
	}
}
