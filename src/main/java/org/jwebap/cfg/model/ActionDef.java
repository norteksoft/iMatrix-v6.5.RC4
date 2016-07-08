package org.jwebap.cfg.model;

/**
 * Action配置定义
 * 
 * 当component属性不为空时,action实例将绑定ComponentContext
 * 
 * @author leadyu(yu-lead@163.com)
 * @since Jwebap 0.5
 * @date 2007-12-14
 */
public class ActionDef {
	
	/**
	 * 对应的Component名称，为空的话ActionContext不可获得Component上下文的
	 */
	private String component = null;

	/**
	 * Action的映射path
	 */
	private String path = null;

	/**
	 * Action 对应的类名
	 */
	private String type = null;

	/**
	 * Action对应的模版
	 */
	private String template = null;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getComponent() {
		return component;
	}

	public void setComponent(String component) {
		this.component = component;
	}

	/**
	 * 判断action定义是否相等，actionName相等则相等，如果actionName为空则不相等
	 */
	public boolean equals(Object obj){
		
		if(!(obj instanceof ActionDef)){
			return false;
		}
		ActionDef def=(ActionDef)obj;
		
		if(this.getPath()==null){
			return false;
		}else if(this.getPath().equals(def.getPath())){
			return true;
		}

		return false;
	}
	
	/**
	 * 改写super.hashCode
	 */
	public int hashCode(){
		if(this.getPath()==null){
			return super.hashCode();
		}else {
			return this.getPath().hashCode();
		}
	}

}
