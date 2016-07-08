package org.jwebap.ui.controler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jwebap.util.FactorySetter;

/**
 * 标准Action请求分发器 根据Action的Mapping配置，分发请求给Action进行处理。所有的Action都继承于Action类
 * <p>
 * public abstract class Action {
 * 
 * public abstract void process(HttpServletRequest request, HttpServletResponse
 * response); }
 * </p>
 * 
 * 但是对于不同的处理需求，Action又分:模版视图处理Action,动态图片处理Action,等等,各种Action
 * 通过Support类进行支持，如TemplateActionSupport：
 * 
 * public abstract class TemplateActionSupport extends Action {
 * 
 * public void process(HttpServletRequest request, HttpServletResponse response)
 * throws Exception {
 * 
 * TemplateEngine engine = EngineFactory.getEngine(); Writer out =
 * response.getWriter(); Context context = getContext(); Template template =
 * getTempalte();
 * 
 * //处理模版 process(request,response,context);
 * 
 * //显示模版 template.merge(templateContext); }
 * 
 * //所有的模版处理Action都实现该方法 public abstract void process(HttpServletRequest
 * request, HttpServletResponse response, Context context) throws Exception;
 *  }
 * 
 * @author leadyu(yu-lead@163.com)
 * @since Jwebap 0.5
 * @date 2007-12-18
 * @see Action
 * @see TemplateActionSupport
 * @see PicActionSupport
 */
public class ActionDispatcher extends AbstractDispatcher {

	private ActionProcesser processer;

	public ActionDispatcher() {
		processer = new ActionProcesser(FactorySetter.getActionFactory());
	}

	

	public void dispatch(HttpServletRequest request,
			HttpServletResponse response, DispatcherChain chain)
			throws Exception {

		processer.process(getSubPath(request),request, response);

		chain.doChain(request, response);
	}

}
