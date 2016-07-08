package org.jwebap.ui.controler;


/**
 * Http请求映射对象 支持通配符'*',mapping表达式规则如下: 1)通配符'*'代表任意字符 2)'/'代表WebModule根目录
 * 
 * @author leadyu(yu-lead@163.com)
 * @since Jwebap 0.5
 * @date 2007-12-18
 */
public class Mapper {

	/**
	 * Mapping表达式,比如'view/*',该表达式是相对于JwebapServlet的Mapping路径而言
	 */
	private String urlPattern;

	public Mapper(String mapping) {
		assertPatternValidate(mapping);
		urlPattern = mapping;
	}

	private void assertPatternValidate(String pattern)
			throws IllegalArgumentException {
		if (pattern.indexOf(".") < 0) {
			if (pattern.indexOf("*") < 0) {
				return;
			}

			if (pattern.indexOf("/*") == pattern.length() - 2) {
				return;
			}
		}

		throw new IllegalArgumentException("invalid mapping:" + pattern);

	}

	/**
	 * 返回Mapper的根目录
	 * <p>
	 * 比如mapping=/view/*,那么mappingPath=/view
	 * </p>
	 * <p>
	 * 比如mapping=/view,那么mappingPath=/view
	 * </p>
	 * 
	 * @param request
	 * @param path
	 *            对于当前urlPattern的相对路径，'/'代表当前urlPattern的根路径
	 * @return
	 */
	public String getMappingPath() {
		String path = urlPattern;
		if (urlPattern.endsWith("/*")) {
			path = urlPattern.substring(0, urlPattern.length() - 2);
		}
		return path;

	}
}
