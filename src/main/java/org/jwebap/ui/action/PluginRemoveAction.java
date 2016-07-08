package org.jwebap.ui.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jwebap.cfg.model.JwebapDef;
import org.jwebap.cfg.persist.PersistManager;
import org.jwebap.core.RuntimeContext;
import org.jwebap.startup.Startup;
import org.jwebap.ui.controler.Action;

/**
 * plugin删除action
 * @author leadyu(yu-lead@163.com)
 * @since Jwebap 0.6
 * @date  2008-11-28
 */
public class PluginRemoveAction  extends Action {

	/**
	 * 删除plugin并且保存jwebap.xml
	 */
	public void process(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String pluginName=request.getParameter("pluginName");
		RuntimeContext context=Startup.getRuntimeContext();
		PersistManager jwebapDefManager=context.getJwebapDefManager();
		//重新获取一个新的jwebap配置定义，而不去使用jwebap运行实例中的配置对象，保持对配置的修改不影响运行环境
		JwebapDef def=jwebapDefManager.get();
		def.removePluginDef(pluginName);
		
		jwebapDefManager.save(def);
		
	}

}
