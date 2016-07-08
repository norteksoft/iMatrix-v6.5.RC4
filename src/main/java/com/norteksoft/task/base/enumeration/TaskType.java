package com.norteksoft.task.base.enumeration;

public enum TaskType {
	DEFAULT_TYPE("task.menu.defaultCategory"),
	WORKFLOW_NAME("task.menu.processName"),
	CUSTOM_TYPE("task.menu.customCategories");
	
	private String name;
	
	TaskType(String name){
		this.name = name;
	}
	
	public String getCode(){
		return this.toString();
	}
	
	public String getName(){
		return name;
	}
}
