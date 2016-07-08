package org.jwebap.cfg.persist.betwixt;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.betwixt.io.BeanReader;
import org.jwebap.cfg.exception.BeanParseException;
import org.jwebap.cfg.persist.InputSource;
import org.jwebap.util.Assert;

/**
 * 基于commons-betwixt的BeanReader实现
 * 
 * @author leadyu(yu-lead@163.com)
 * @since Jwebap 0.5
 * @date 2008-4-14
 */
public class BetwixtReader implements org.jwebap.cfg.persist.BeanReader {

	/**
	 * 要解析的文件
	 */
	private InputSource _input = null;

	private Class _clazz = null;

	private String _header = null;

	/**
	 * betwixt的beanReader
	 */
	private BeanReader _reader = null;

	public BetwixtReader(String header, Class clazz, InputSource input) {
		Assert.assertNotNull(input, "null input.");
		_reader = new BeanReader();
		_clazz = clazz;
		_header = header;
		_input = input;
	}

	
	/**
	 * 解析对象
	 * @throws BeanParseException 
	 */
	public Object parse() throws BeanParseException {
		Object bean=null;
		InputStream in = null;
		try {
			_reader.registerBeanClass(_header, _clazz);

			in = _input.getInputStream();
			bean = _reader.parse(in);

			return bean;

		} catch (Exception e) {
			throw new BeanParseException("bean parse error:" + _input, e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {}
			}
		}

	}

}
