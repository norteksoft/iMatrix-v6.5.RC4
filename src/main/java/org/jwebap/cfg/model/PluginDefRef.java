package org.jwebap.cfg.model;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.json.JSONString;
import org.jwebap.cfg.exception.PluginDefLinkedException;
import org.jwebap.cfg.exception.PluginDefNotFoundException;
import org.jwebap.cfg.exception.PluginDefParseException;
import org.jwebap.cfg.persist.BeanFactory;
import org.jwebap.cfg.persist.BeanReader;
import org.jwebap.cfg.persist.InputSource;
import org.jwebap.util.Assert;

/**
 * plugin配置引用
 * 
 * plugin配置的引用,只包含plugin配置文件的地址,当组件需要访问到plugin配置时加载
 * 
 * @author leadyu(yu-lead@163.com)
 * @since Jwebap 0.6
 * @date 2008-4-6
 */
public class PluginDefRef implements JSONString{
	
	public static final String ABSOLUT_PATH_PREFIX="${ABSOLUTE_PATH}";
	
	public static final String PLUGIN_DEF="META-INF/plugin.xml";
	
	/**
	 * plugin名称
	 */
	private String _name = null;
	/**
	 * plugin的jar包路径
	 */
	private String _ref = null;

	/**
	 * 内部配置定义
	 */
	private PluginDef _pluginDef=null;
	
	
	public void setPluginDef(PluginDef internalDef) {
		_pluginDef = internalDef;
	}
	
	/**
	 * 返回插件引用对应的插件配置
	 * @param internalDef
	 * @throws PluginDefParseException 
	 * @throws PluginDefNotFoundException 
	 */
	private PluginDef getPluginDef() throws PluginDefLinkedException {
		if(_pluginDef!=null){
			return _pluginDef;
		}else{
			try {
				loadPluginDef();
			} catch (Exception e) {
				throw new PluginDefLinkedException("plugin "+_name+" linked fail.",e);
			}
			return _pluginDef;
		}
	}
	
	/**
	 * 增加Component定义
	 * 
	 * 对应jwebap.xml的Component定义，其优先级高于plugin的Component定义
	 * @param name
	 * @param component
	 * @throws PluginDefLinkedException 
	 */
	public void addComponentDef(ComponentDef component) throws PluginDefLinkedException {
		getPluginDef().addComponentDef(component);
	}

	public ComponentDef getComponentDef(String name) throws PluginDefLinkedException  {
		return getPluginDef().getComponentDef(name);
	}
	
	public Collection getComponentDefs()  throws PluginDefLinkedException {
		return getPluginDef().getComponentDefs();
	}

	public void addDispatcherDef(DispatcherDef dispatcher) throws PluginDefLinkedException  {
		getPluginDef().addDispatcherDef(dispatcher);
	}

	public DispatcherDef getDispatcherDef(String name)  throws PluginDefLinkedException {
		return getPluginDef().getDispatcherDef(name);
	}
	
	public Collection getDispatcherDefs() throws PluginDefLinkedException  {
		return getPluginDef().getDispatcherDefs();
	}
	
	public void addActionDef(ActionDef action)  throws PluginDefLinkedException {
		getPluginDef().addActionDef(action);
	}
	
	public ActionDef getActionDef(String name)  throws PluginDefLinkedException {
		return getPluginDef().getActionDef(name);
	}

	public String getName() {
		return _name;
	}

	public void setName(String name) {
		_name = name;
	}
	
	/**
	 * 加载plugin配置，采用延时加载的策略，当第一次使用插件配置时加载
	 * @throws PluginDefNotFoundException
	 * @throws PluginDefParseException 
	 */
	private synchronized void loadPluginDef()throws PluginDefNotFoundException, PluginDefParseException{
		// TODO 延时加载插件配置
		BeanFactory factory= new BeanFactory();
		Assert.assertNotNull(_ref,"plugin "+_name+" deployPath is null.");
		InputSource input=null;
		try {
			URL url = loadPluginDefFile();
			input=new InputSource(url);
		} catch (IOException e) {
			throw new PluginDefNotFoundException("plugin def not exists: "
					+ _ref,e);
		}

		try {
			BeanReader reader = factory.createReader("plugin", PluginDef.class,input);
			setPluginDef((PluginDef) reader.parse());
		} catch (Exception e) {
			throw new PluginDefParseException("plugin def parse fail :"+ e.getMessage(), e);
		}
	}

	/**
	 * 加载pluginRef对应plugin配置文件
	 * @return
	 * @throws IOException 
	 */
	private URL loadPluginDefFile() throws IOException{
		URL file=null;
		if(getRef()==null){
			throw new IOException("plugin def path is null:"+getName());
		}else if(getRef().toUpperCase().startsWith(ABSOLUT_PATH_PREFIX)){
			file=loadRelativeDefFile(getRef());
		}else{
			file=loadAbsoluteDefFile(getRef());
		}
		
		return file;
	}
	
	/**
	 * 加载采用相对路径配置的plugin.xml
	 * @param path
	 * @return
	 */
	private URL loadRelativeDefFile(String path)throws IOException{
		ClassLoader loader =this.getClass().getClassLoader();
		Enumeration urls=loader.getResources(PLUGIN_DEF);
		URL url=null;
		while(urls.hasMoreElements()){
			url=(URL)urls.nextElement();
			String pluginUrl=url.getFile();
			int index=pluginUrl.indexOf(PLUGIN_DEF);
			if(index==-1){
				continue;
			}
			pluginUrl=pluginUrl.substring(0,index);
			String pluginPath=path.substring(ABSOLUT_PATH_PREFIX.length());
			//找到plugin
			if(pluginUrl.indexOf(pluginPath)>-1){
				break;
			}
		}
		return url;
	}
	
	/**
	 * 加载采用绝对路径配置的plugin.xml
	 * @param path
	 * @return
	 */
	private URL loadAbsoluteDefFile(String path)throws IOException{
		URL url=null;
		if(path.endsWith(".jar")){
			url=new URL(path+"!"+PLUGIN_DEF);
		}else if(path.endsWith("/")){
			url=new URL(path+PLUGIN_DEF);
		}else{
			url=new URL(path+"/"+PLUGIN_DEF);
		}
		return url;
	}
	
	public String getRef() {
		return _ref;
	}

	public void setRef(String ref) {
		_ref = ref;
		_pluginDef=null;
	}
	
	/**
	 * 转换成Json对象
	 * @return
	 */
	public String toJSONString() {
		Map map = new HashMap();
		map.put("name", _name);
		map.put("path", _ref);
		return new JSONObject(map).toString();
	}
}
