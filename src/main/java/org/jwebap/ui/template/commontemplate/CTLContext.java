package org.jwebap.ui.template.commontemplate;

import java.io.Writer;
import java.util.Map;

import org.commontemplate.core.Context;
import org.jwebap.util.Assert;

/**
 * Common Template Context Adapter
 * 
 * @author leadyu(yu-lead@163.com)
 * @since Jwebap 0.5
 * @date  2007-11-26
 */
public class CTLContext implements org.jwebap.ui.template.Context{

	private Context _context=null;
	
	public CTLContext(Context context){
		_context=context;
	}
	
	private void assertContext(){
		Assert.assertNotNull(_context,"CommonTemplate上下文为空!");
	}
	
	public void clear() {
		assertContext();
		_context.clear();
	}

	public Writer getOut() {
		assertContext();
		return _context.getOut();
	}

	public Object get(String key) {
		assertContext();
		Map vars = _context.getDefinedVariables();
		if(vars!=null){
			return vars.get(key);
		}else{
			return null;
		}
	}

	public void put(String key, Object value) {
		assertContext();
		_context.defineVariable(key,value);
	}
	
	/**
	 * 返回代理对象
	 * Common Template包内私有
	 * 
	 * @return
	 */
	protected Context getDelegate(){
		return _context;
	}

	public Map getAll() {
		assertContext();
		return _context.getDefinedVariables();
	}

	public void putAll(Map values) {
		assertContext();
		_context.defineAllVariables(values);
	}

}
