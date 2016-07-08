package org.jwebap.ui.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jwebap.cfg.model.JwebapDef;
import org.jwebap.cfg.model.PluginDefRef;
import org.jwebap.cfg.persist.PersistManager;
import org.jwebap.core.RuntimeContext;
import org.jwebap.startup.Startup;
import org.jwebap.ui.controler.Action;

/**
 * plugin新增action
 * @author leadyu(yu-lead@163.com)
 * @since Jwebap 0.6
 * @date  2008-11-28
 */
public class PluginAddAction  extends Action {

	/**
	 * 新增plugin并且保存jwebap.xml
	 */
	public void process(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String pluginName=request.getParameter("pluginName");
		String path=request.getParameter("path");
		
		if(pluginName==null || pluginName.trim().equals("")){
			showErr("pluginName is empty.",request,response);
			return;
		}
		
		if(path==null || path.trim().equals("")){
			showErr("path is empty.",request,response);
			return;
		}
		
		request.setAttribute("pluginName", pluginName);
		request.setAttribute("path", path);
		
		PluginDefRef plugin=new PluginDefRef();
		plugin.setName(pluginName);
		plugin.setRef(path);
		
		RuntimeContext context=Startup.getRuntimeContext();
		PersistManager jwebapDefManager=context.getJwebapDefManager();
		//重新获取一个新的jwebap配置定义，而不去使用jwebap运行实例中的配置对象，保持对配置的修改不影响运行环境
		JwebapDef def=jwebapDefManager.get();
		def.addPluginDef(plugin);
		
		jwebapDefManager.save(def);
		
		redirect(request,response);
	}

	/**
	 * 转向
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	private void redirect(HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.sendRedirect("../plugins");
	}
	
	/**
	 * 显示错误信息
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	private void showErr(String errMsg,HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setAttribute("errMsg", errMsg);
		request.getRequestDispatcher("new").forward(request, response);
		
	}
}
