package org.jwebap.cfg.model;

import java.util.HashMap;

import org.jwebap.ui.controler.Dispatcher;
import org.jwebap.ui.controler.Mapper;
import org.jwebap.util.ParameterStorage;

/**
 * 视图分发器配置定义
 * 
 * @author leadyu(yu-lead@163.com)
 * @since Jwebap 0.5
 * @date 2008-4-6
 */
public class DispatcherDef extends ParameterStorage {

	/**
	 * Component的实现类，类似org.jwebap.http.HttpComponent
	 */
	private String type = null;

	/**
	 * 分发器定义的名称
	 */
	private String name = null;

	/**
	 * 分发器对应的mapping,其相对于JwebapServlet配置的mapping.比如Servlet配置的
	 * mapping=/detect而分发器配置的mapping=/sql/view,那么对于URL=/detect/sql/view
	 * 的请求，就会通过该分发器进行分发.
	 */
	private Mapper mapper = null;

	private String mapping = null;

	private Dispatcher dispatcher;

	public Dispatcher getDispatcher() {
		return dispatcher;
	}

	public void setDispatcher(Dispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}

	public Mapper getMapper() {
		return mapper;
	}

	public String getMapping() {
		return mapping;
	}

	public void setMapping(String mapping) {
		if(mapping==null){
			mapping="";
		}
		this.mapper = new Mapper(mapping);
		this.mapping = mapping;
	}

	public DispatcherDef() {
		super(new HashMap());
	}

	public String getType() {
		return type;
	}

	public void setType(String className) {
		this.type = className;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * 判断dispatcher定义是否相等，dispatcherName相等则相等，如果dispatcherName为空则不相等
	 */
	public boolean equals(Object obj){
		
		if(!(obj instanceof DispatcherDef)){
			return false;
		}
		DispatcherDef def=(DispatcherDef)obj;
		
		if(this.getName()==null){
			return false;
		}else if(this.getName().equals(def.getName())){
			return true;
		}

		return false;
	}
	
	/**
	 * 改写super.hashCode
	 */
	public int hashCode(){
		if(this.getName()==null){
			return super.hashCode();
		}else {
			return this.getName().hashCode();
		}
	}
	
}
