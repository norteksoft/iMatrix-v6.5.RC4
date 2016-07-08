package org.jwebap.cfg.persist.betwixt;

import org.jwebap.cfg.exception.BeanParseException;

/**
 * 解析对象源错误，当解析源读取发生错误时抛出
 * 
 * @author leadyu(yu-lead@163.com)
 * @since Jwebap 0.6
 * @date  2008-10-19
 */
public class BeanInputException extends BeanParseException {

	static final long serialVersionUID = -19811222L;
	
	public BeanInputException(String message) {
		super(message);
	}

	public BeanInputException(String message, Throwable cause) {
		super(message, cause);
	}

}
