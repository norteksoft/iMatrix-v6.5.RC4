package org.jwebap.ui.template;

import java.io.Writer;
import java.util.Map;

/**
 * 模版引擎上下文
 * 
 * @author leadyu(yu-lead@163.com)
 * @since Jwebap 0.5
 * @date  2007-11-26
 */
public interface Context {
	public void clear();
	
	/**
	 * 获取当前上下文的输出端
	 * 
	 * @return 输出端
	 */
	public abstract Writer getOut();
	
	/**
	 * 获取环境变量
	 * 
	 * @param key
	 * @return
	 */
	public Object get(String key);
	
	/**
	 * 获取所有参数
	 * @return
	 */
	public Map getAll();
	
	/**
	 * 绑定环境变量
	 * 
	 * @return
	 */
	public void put(String key,Object value);
	
	/**
	 * 绑定所有环境变量
	 * 
	 * @return
	 */
	public void putAll(Map values);
}
