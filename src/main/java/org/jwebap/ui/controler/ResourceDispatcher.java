package org.jwebap.ui.controler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 静态文件请求的分发器，对于资源的部署有自己的要求：
 * <p>
 * 1)资源位于resources.*包以及子包内 2)以Dispatcher的SubPath为子包名获取文件
 * </p>
 * 比如请求路径:http://domain/detect/resources/js/a.js,Disaptcher分发的根路径:/resources/*
 * 那么,SubPath:/js/a.js,最终请求的资源名:resources/js/a.js
 * 
 * @author leadyu(yu-lead@163.com)
 * @since Jwebap 0.5
 * @date 2008-1-10
 */
public class ResourceDispatcher extends AbstractDispatcher {

	private ResourceProcesser processer;

	public ResourceDispatcher() {
		processer = new ResourceProcesser();
	}

	public void dispatch(HttpServletRequest request,
			HttpServletResponse response, DispatcherChain chain)
			throws Exception {
		String path = this.getSubPath(request);
		int doat = path.lastIndexOf(".");
		String mime = doat > -1 && doat < path.length() - 1 ? path
				.substring(doat + 1) : "";

		if (path.indexOf("/images/") > -1 || path.indexOf("/image/") > -1
				|| "|jpg|jpeg|bmp|gif|png|tng|".indexOf(mime) > -1) {
			response.setContentType("image/jpeg;");
		} else if ("css".equals(mime)) {
			response.setContentType("text/css;");
		} else if ("xml".equals(mime)) {
			response.setContentType("text/xml;");
		} else if ("".equals(mime) || "html".equals(mime) || "htm".equals(mime)) {
			response.setContentType("text/html;");
		} else {
			response.setContentType("text/" + mime + ";");
		}

		processer.process(path, request, response);
	}

}
