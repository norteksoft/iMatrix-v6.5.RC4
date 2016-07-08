package org.jwebap.ui.template;

import java.io.IOException;


public interface TemplateFactory {
	/**
	 * 通过名称获取模板. 使用默认编码加载
	 * 
	 * @param name
	 *            模板名称
	 * @return 模板
	 * @throws TemplateException
	 *             模板解析出错时抛出
	 */
	public Template getTemplate(String name) throws IOException,
			TemplateException;

	/**
	 * 通过名称获取模板. 并指定加载编码
	 * 
	 * @param name
	 *            模板名称
	 * @param encoding
	 *            模板编码
	 * @return 模板
	 * @throws TemplateException
	 *             模板解析出错时抛出
	 */
	public Template getTemplate(String name, String encoding)
			throws IOException, TemplateException;
}
