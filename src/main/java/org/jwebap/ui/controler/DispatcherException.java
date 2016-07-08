package org.jwebap.ui.controler;

/**
 * 分发器内部错误
 * 
 * @author leadyu(yu-lead@163.com)
 * @since Jwebap 0.5
 * @date 2007-11-23
 */
public class DispatcherException extends RuntimeException {

	static final long serialVersionUID = -1L;
	
	public DispatcherException(String message) {
		super(message);
	}

	public DispatcherException(String message, Throwable cause) {
		super(message, cause);
	}

}
