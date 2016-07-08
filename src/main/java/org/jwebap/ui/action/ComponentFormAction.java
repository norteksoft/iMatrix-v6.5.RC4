package org.jwebap.ui.action;

import java.util.Collection;

import org.jwebap.cfg.model.ComponentDef;
import org.jwebap.cfg.model.JwebapDef;
import org.jwebap.cfg.persist.PersistManager;
import org.jwebap.core.RuntimeContext;
import org.jwebap.startup.Startup;
import org.jwebap.ui.controler.TemplateActionSupportHelper;
import org.jwebap.ui.template.Context;

/**
 * component表单界面action
 * @author leadyu(yu-lead@163.com)
 * @since Jwebap 0.6
 * @date  2008-11-28
 */
public class ComponentFormAction  extends TemplateActionSupportHelper {

	/**
	 * 展现component参数
	 */
	public void process(Context context) throws Exception {
		String componentName=(String)context.get("componentName");
		
		RuntimeContext runtimeContext=Startup.getRuntimeContext();
		PersistManager jwebapDefManager=runtimeContext.getJwebapDefManager();
		JwebapDef def=jwebapDefManager.get();
		ComponentDef componentDef=null;
		if(componentName!=null){
			componentDef=def.getComponentDef(componentName);
		}
		
		if(componentDef!=null){
			context.put("name", componentDef.getName());
			context.put("class", componentDef.getType());
			Collection params=componentDef.getProperties();
			context.put("params",params);
		}
		
		
	}
}



