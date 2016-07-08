package org.jwebap.ui.controler;

/**
 * 模版视图内部错误
 * 
 * @author leadyu(yu-lead@163.com)
 * @since Jwebap 0.5
 * @date 2007-11-23
 */
public class ActionException extends RuntimeException {

	static final long serialVersionUID = -1L;
	
	public ActionException(String message) {
		super(message);
	}

	public ActionException(String message, Throwable cause) {
		super(message, cause);
	}

}
