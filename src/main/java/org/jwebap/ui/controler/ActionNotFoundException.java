package org.jwebap.ui.controler;

/**
 * Action未找到错误
 * 
 * @author leadyu(yu-lead@163.com)
 * @since Jwebap 0.5
 * @date 2007-11-23
 */
public class ActionNotFoundException extends Exception {

	static final long serialVersionUID = -1L;
	
	public ActionNotFoundException(String message) {
		super(message);
	}

	public ActionNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

}
