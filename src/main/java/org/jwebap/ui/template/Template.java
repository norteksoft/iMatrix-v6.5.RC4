package org.jwebap.ui.template;


/**
 * 模版
 * @author leadyu(yu-lead@163.com)
 * @since Jwebap 0.5
 * @date  2007-11-23
 */
public interface Template {
	
	/**
	 * 绑定上下文
	 * 
	 * @param context
	 * @throws MergeException
	 */
	public void merge(Context context)throws MergeException;
}
