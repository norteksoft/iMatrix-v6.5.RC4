package org.jwebap.ui.template;

import java.io.Writer;
import java.util.Locale;
import java.util.TimeZone;

public interface ContextFactory {
	
	/**
	 * 创建新的模板上下文
	 * 
	 * @param out
	 *            输出接口
	 * @return 新的模板上下文
	 */
	public Context createContext(Writer out);
	/**
	 * 创建新的模板上下文 通过Locale查找相应的TimeZone(以地区国家首都为准)
	 * 
	 * @param out
	 *            输出接口
	 * @param locale
	 *            国际化区域
	 * @return 新的模板上下文
	 */
	public Context createContext(Writer out,Locale locale);

	/**
	 * 创建新的模板上下文
	 * 
	 * @param out
	 *            输出接口
	 * @param locale
	 *            国际化区域
	 * @param timeZone
	 *            时区
	 * @return 新的模板上下文
	 */
	public Context createContext(Writer out,Locale locale, TimeZone timeZone);

}
