package org.jwebap.ui.template;

/**
 * 模版执行错误
 * 
 * @author leadyu(yu-lead@163.com)
 * @since Jwebap 0.5
 * @date 2007-11-23
 */
public class MergeException extends RuntimeException {

	static final long serialVersionUID = -19811222419830928L;
	
	public MergeException(String message) {
		super(message);
	}

	public MergeException(String message, Throwable cause) {
		super(message, cause);
	}

}
