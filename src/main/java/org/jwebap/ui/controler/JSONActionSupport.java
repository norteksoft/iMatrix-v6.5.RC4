package org.jwebap.ui.controler;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

/**
 * Json对象处理Action支持类
 * 
 * 如果Action只需要生成Json对象，那么可以继承该类实现Action JSONActionSupport会把生成的Json对象文本(@see
 * http://www.json.org/) 输出到http回应中
 * 
 * @author leadyu(yu-lead@163.com)
 * @since Jwebap 0.5
 * @date 2008-1-29
 */
public abstract class JSONActionSupport extends Action {

	public void process(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		JSONObject json =processJson(request,response);
		
		if (json != null) {
			String cb = request.getParameter("callback");
			if (cb != null) { 
			    response.setContentType("text/javascript; charset=utf-8");
			} else {
			    response.setContentType("application/x-json; charset=utf-8");
			}

			PrintWriter out = response.getWriter();
			out.print(json.toString());
			
		}

	}

	public abstract JSONObject processJson(HttpServletRequest request,
			HttpServletResponse response) throws Exception;

}
