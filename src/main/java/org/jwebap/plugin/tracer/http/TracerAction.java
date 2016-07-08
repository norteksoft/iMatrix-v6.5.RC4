package org.jwebap.plugin.tracer.http;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jwebap.core.Analyser;
import org.jwebap.core.ComponentContext;
import org.jwebap.plugin.tracer.TimeFilterAnalyser;
import org.jwebap.ui.controler.ActionContext;
import org.jwebap.ui.controler.TemplateActionSupport;
import org.jwebap.ui.template.Context;
import org.jwebap.util.Assert;

/**
 * http plugin界面Action
 * 
 * @author leadyu(yu-lead@163.com)
 * @since Jwebap 0.5
 * @date  2008-1-11
 */
public class TracerAction extends TemplateActionSupport{

	public void process(HttpServletRequest request, HttpServletResponse response, Context context) throws Exception {

		/**获得http请求轨迹分析器，以获得分析数据*/
		TimeFilterAnalyser analyser=getTimeFilterAnalyser();
		Assert.assertNotNull(analyser,"HttpComponent is not startup:time filter analyser is null.");
		
		
		//http请求超时的时间
		long time=analyser.getTracefilterActivetime();
		//http请求超时轨迹最大记录数
		int max=analyser.getMaxTraceSize();
		
		/**设置模版上下文*/
		context.put("overTime",new Long(time));
		context.put("maxTrace",new Integer(max));
	}


	/**
	 * 返回当前Action对应的HttpComponent的执行时间过滤轨迹分析器
	 * 
	 * @return 根据执行时间进行过滤的轨迹分析器
	 * @see Analyser
	 */
	private TimeFilterAnalyser getTimeFilterAnalyser() {

		ActionContext actionContext = this.getActionContext();

		Assert.assertNotNull(actionContext, "actionContext is null.");

		ComponentContext componentContext = actionContext.getComponentContext();

		Assert.assertNotNull(componentContext, "componentContext is null.");

		HttpComponent component = (HttpComponent) componentContext
				.getComponent();

		/** 获得http请求轨迹分析器，以获得分析数据 */
		TimeFilterAnalyser analyser = component.getTimeAnalyser();

		return analyser;
	}
}
