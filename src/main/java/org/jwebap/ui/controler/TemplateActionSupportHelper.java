package org.jwebap.ui.controler;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jwebap.ui.template.Context;

/**
 * 模版处理Action帮助者
 * 
 * 能够从request获取参数以及属性，初始化到Context中，方便处理，同时使模板Action接口更加清晰
 * 
 * @author leadyu(yu-lead@163.com)
 * @since Jwebap 0.5
 * @date 2007-12-18
 */
public abstract class TemplateActionSupportHelper extends TemplateActionSupport {

	private static final Log log = LogFactory.getLog(TemplateActionSupportHelper.class);
	
	/**
	 * 基于模版处理的Action都实现该方法
	 * 
	 * @param request
	 * @param response
	 * @param context
	 * @throws Exception
	 */
	public void process(HttpServletRequest request,
			HttpServletResponse response, Context context) throws Exception {
		putParameters(request, response, context);
		process(context);
	}

	/**
	 * 初始化上下文参数
	 * 
	 * @param request
	 * @param response
	 * @param context
	 */
	private void putParameters(HttpServletRequest request,
			HttpServletResponse response, Context context) {
		
		Map params = request.getParameterMap();
		Iterator paramsKeyIt=params.keySet().iterator();
		while (paramsKeyIt.hasNext()) {
			String paramName = (String) paramsKeyIt.next();
			String value = request.getParameter(paramName);
			try {
				context.put(paramName, value);
			} catch (Exception e) {
				//这里让我很苦恼，CommonTemplate 对于参数有命名规范检查，但是命名规范是否应该语言层面去处理？
				log.warn("param '"+paramName+"' can not initial into Context. error:"+e+" "+e.getMessage());
			}
		}
		
		Enumeration attributeNames = request.getAttributeNames();
		while (attributeNames.hasMoreElements()) {
			String attributeName = (String) attributeNames.nextElement();
			Object value = request.getAttribute(attributeName);
			try {
				context.put(attributeName, value);
			} catch (Exception e) {
				//这里让我很苦恼，CommonTemplate 对于参数有命名规范检查，但是命名规范是否应该语言层面去处理？
				log.warn("attribute '"+attributeName+"' can not initial into Context. error:"+e+" "+e.getMessage());
			}
		}

	}

	/**
	 * 基于模版处理的Action实现该方法
	 * 
	 * @param request
	 * @param response
	 * @param context
	 * @throws Exception
	 */
	public abstract void process(Context context) throws Exception;

}
