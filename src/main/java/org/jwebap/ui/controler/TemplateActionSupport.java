package org.jwebap.ui.controler;

import java.io.Writer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jwebap.cfg.model.ActionDef;
import org.jwebap.ui.template.Context;
import org.jwebap.ui.template.EngineFactory;
import org.jwebap.ui.template.Template;
import org.jwebap.ui.template.TemplateEngine;

/**
 * 模版处理Action
 * 
 * @author leadyu(yu-lead@163.com)
 * @since Jwebap 0.5
 * @date 2007-12-18
 */
public abstract class TemplateActionSupport extends Action {

	public void process(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		TemplateEngine engine = EngineFactory
				.getEngine(EngineFactory.COMMON_TEMPLATE);

		Writer out = response.getWriter();

		Context templateContext = null;

		ActionDef config = getActionContext()
				.getActionDefinition();
		String templateName = config.getTemplate();
		Template template = null;
		if (templateName != null && !"".equals(templateName)) {
			template = engine.getTemplate(templateName);
			templateContext = engine.createContext(out);
		}
		/**
		 * 模版处理
		 * 
		 */
		process(request,response,templateContext);
		/**
		 * 模版展现
		 */
		if (template != null) {
			template.merge(templateContext);
		}
	}

	/**
	 * 基于模版处理的Action都实现该方法
	 * 
	 * @param request
	 * @param response
	 * @param context
	 * @throws Exception
	 */
	public abstract void process(HttpServletRequest request,
			HttpServletResponse response, Context context) throws Exception;

}
