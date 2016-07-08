package org.jwebap.plugin.tracer.jdbc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.jwebap.core.Analyser;
import org.jwebap.core.ComponentContext;
import org.jwebap.plugin.tracer.TimeFilterAnalyser;
import org.jwebap.plugin.tracer.util.TimeFilterViewHelper;
import org.jwebap.ui.controler.ActionContext;
import org.jwebap.ui.controler.JSONActionSupport;
import org.jwebap.util.Assert;

/**
 * method component tracer 界面异步请求超时轨迹数据的响应Action
 * 
 * @author leadyu(yu-lead@163.com)
 * @since Jwebap 0.5
 * @date 2008-1-11
 */
public class TraceDatasAction extends JSONActionSupport {

	public JSONObject processJson(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		/** 获得http请求轨迹分析器，以获得分析数据 */
		TimeFilterAnalyser analyser = getTimeFilterAnalyser();
		Assert.assertNotNull(analyser,
				"MethodComponent is not startup:time filter analyser is null.");

		TimeFilterViewHelper helper = new TimeFilterViewHelper(analyser);

		return helper.processJson(request, response);
	}


	/**
	 * 返回当前Action对应的DBComponent的执行时间过滤轨迹分析器
	 * 
	 * @return 根据执行时间进行过滤的轨迹分析器
	 * @see Analyser
	 */
	private TimeFilterAnalyser getTimeFilterAnalyser() {

		ActionContext actionContext = this.getActionContext();

		Assert.assertNotNull(actionContext, "actionContext is null.");

		ComponentContext componentContext = actionContext.getComponentContext();

		Assert.assertNotNull(componentContext, "componentContext is null.");

		JdbcComponent component = (JdbcComponent) componentContext
				.getComponent();

		/** 获得http请求轨迹分析器，以获得分析数据 */
		TimeFilterAnalyser analyser = component.getTimeAnalyser();

		return analyser;
	}
}