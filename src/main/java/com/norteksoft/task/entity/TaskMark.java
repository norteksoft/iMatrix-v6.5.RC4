package com.norteksoft.task.entity;


public enum TaskMark {
	RED("red","task.button.red"),
	BLUE("blue","task.button.blue"),
	YELLOW("yellow","task.button.yellow"),
	GREEN("green","task.button.green"),
	ORANGE("orange","task.button.orange"),
	PURPLE("purple","task.button.purple"),
	CANCEL("white","task.button.white");
	
	private String name;
	private String i18nName;
	
	TaskMark(String name,String i18nName){
		this.name = name;
		this.i18nName=i18nName;
	}
	
	public int getCode(){
		return this.ordinal();
	}
	
	public String getName(){
		return name;
	}
	public String getI18nName() {
		return i18nName;
	}
	
	public static TaskMark valueOf(int ordinal){
		for(TaskMark mark:TaskMark.values()){
			if(mark.getCode()==ordinal) return mark;
		}
		return CANCEL;
	}
}
