package org.jwebap.ui.template.commontemplate;

import java.io.IOException;
import java.io.Writer;
import java.util.Locale;
import java.util.TimeZone;

import org.commontemplate.core.Factory;
import org.commontemplate.engine.Engine;
import org.commontemplate.standard.ConfigurationSettings;
import org.commontemplate.tools.PropertiesConfigurationLoader;
import org.jwebap.ui.template.Context;
import org.jwebap.ui.template.MergeException;
import org.jwebap.ui.template.Template;
import org.jwebap.ui.template.TemplateEngine;
import org.jwebap.ui.template.TemplateException;

/**
 * Common Template引擎
 * 
 * @author leadyu(yu-lead@163.com)
 * @since Jwebap 0.5
 * @date 2007-11-23
 * @see www.commontemplate.org
 */
public class CTLTemplateEngine implements TemplateEngine {

	Factory _factory = null;

	public CTLTemplateEngine() {
		initialize();
	}

	private void initialize() {
		try {
			ConfigurationSettings config = PropertiesConfigurationLoader
					.loadStandardConfiguration();
			_factory = new Engine(config);
		} catch (Throwable e) {
			throw new TemplateException("Common Template引擎加载失败!", e);
		}

	}

	public void mergeTemplate(String templateName, Context context)
			throws MergeException, IOException {
		Template template = getTemplate(templateName);
		mergeTemplate(template, context);

	}

	public void mergeTemplate(String templateName, String encoding,
			Context context) throws MergeException, IOException {
		Template template = getTemplate(templateName, encoding);
		mergeTemplate(template, context);
	}

	public void mergeTemplate(Template template, Context context)
			throws MergeException {
		template.merge(context);
	}

	public Template getTemplate(String name) throws IOException,
			TemplateException {
		org.commontemplate.core.Template delegate = null;

		delegate = _factory.getTemplate(name);

		return new CTLTemplate(delegate);
	}

	public Template getTemplate(String name, String encoding)
			throws IOException, TemplateException {
		org.commontemplate.core.Template delegate = null;

		delegate = _factory.getTemplate(name, encoding);

		return new CTLTemplate(delegate);
	}

	public Context createContext(Writer out) {
		org.commontemplate.core.Context delegate = null;

		delegate = _factory.createContext(out);

		return new CTLContext(delegate);
	}

	public Context createContext(Writer out, Locale locale) {
		org.commontemplate.core.Context delegate = null;

		delegate = _factory.createContext(out, locale);

		return new CTLContext(delegate);
	}

	public Context createContext(Writer out, Locale locale, TimeZone timeZone) {
		org.commontemplate.core.Context delegate = null;

		delegate = _factory.createContext(out, locale, timeZone);

		return new CTLContext(delegate);
	}

}
