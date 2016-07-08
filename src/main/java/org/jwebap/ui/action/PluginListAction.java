package org.jwebap.ui.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.jwebap.cfg.model.JwebapDef;
import org.jwebap.cfg.persist.PersistManager;
import org.jwebap.core.RuntimeContext;
import org.jwebap.startup.Startup;
import org.jwebap.ui.controler.JSONActionSupport;

/**
 * 返回当前部署的plugin数据
 * @author leadyu(yu-lead@163.com)
 * @since Jwebap 0.6
 * @date  2008-11-25
 */
public class PluginListAction  extends JSONActionSupport {

	/**
	 * 返回所有部署的plugin
	 */
	public JSONObject processJson(HttpServletRequest request, HttpServletResponse response) throws Exception {
		RuntimeContext context=Startup.getRuntimeContext();
		PersistManager jwebapDefManager=context.getJwebapDefManager();
		JwebapDef def=jwebapDefManager.get();
		
		JSONObject json = new JSONObject();
		json.put("plugins", def.getPluginDefs());
		return json;
		
	}
}
