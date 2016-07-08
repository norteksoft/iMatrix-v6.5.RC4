package org.jwebap.ui.action;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.jwebap.cfg.model.JwebapDef;
import org.jwebap.cfg.model.PluginDefRef;
import org.jwebap.cfg.persist.PersistManager;
import org.jwebap.core.RuntimeContext;
import org.jwebap.startup.Startup;
import org.jwebap.ui.controler.JSONActionSupport;

/**
 * 返回当前plugin的components
 * @author leadyu(yu-lead@163.com)
 * @since Jwebap 0.6
 * @date  2008-11-25
 */
public class ComponentListAction  extends JSONActionSupport {

	/**
	 * 返回当前plugin的components
	 */
	public JSONObject processJson(HttpServletRequest request, HttpServletResponse response) throws Exception {
		RuntimeContext context=Startup.getRuntimeContext();
		PersistManager jwebapDefManager=context.getJwebapDefManager();
		JwebapDef def=jwebapDefManager.get();
		PluginDefRef plugin=null;
		String pluginName=request.getParameter("pluginName");
		if(pluginName==null){
			pluginName=(String)request.getAttribute("pluginName");
		}
		plugin=def.getPluginDef(pluginName);
		
		JSONObject json = new JSONObject();
		if(plugin!=null){
			json.put("components", plugin.getComponentDefs());
		}else{
			json.put("components", new ArrayList());
		}
		return json;
		
	}
}
