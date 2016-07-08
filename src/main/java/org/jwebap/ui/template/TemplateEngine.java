package org.jwebap.ui.template;

import java.io.IOException;

/**
 * 模版引擎
 * 
 * @author leadyu(yu-lead@163.com)
 * @since Jwebap 0.5
 * @date 2007-11-23
 */
public interface TemplateEngine extends TemplateFactory, ContextFactory {

	/**
	 * 绑定模版
	 * 
	 * @param templateName
	 *            模版名称
	 * @param context
	 *            上下文
	 * @throws MergeException
	 */
	public abstract void mergeTemplate(String templateName, Context context)
			throws MergeException,IOException;

	/**
	 * 绑定模版
	 * 
	 * @param templateName
	 *            模版名称
	 * @param encoding
	 *            模版文件编码
	 * @param context
	 *            上下文
	 * @throws MergeException
	 */
	public abstract void mergeTemplate(String templateName, String encoding,
			Context context) throws MergeException,IOException;

	/**
	 * 绑定模版
	 * 
	 * @param template
	 *            模版
	 * @param context
	 *            上下文
	 * @throws MergeException
	 */
	public abstract void mergeTemplate(Template template, Context context)
			throws MergeException;

}
