package org.jwebap.core;

import org.jwebap.util.ParameterMap;
import org.jwebap.util.ParameterStorage;

/**
 * 抽象上下文
 * 
 * @author leadyu(yu-lead@163.com)
 * @since Jwebap 0.5
 * @date  2007-12-8
 */
public abstract class AbstractContext extends ParameterStorage implements Context{
	
	private Context _parent=null;
	
	public AbstractContext(){
	}
	
	public AbstractContext(Context parent){
		_parent=parent;
	}
	
	public AbstractContext(Context parent,ParameterMap map){	
		super(map);
		_parent=parent;
	}
	
	
	public Context getParent(){
		return _parent;
	}
	


}
