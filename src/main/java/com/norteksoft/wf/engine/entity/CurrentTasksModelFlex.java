package com.norteksoft.wf.engine.entity;

/**
 * 查询当前环节。flex用的model
 */
public class CurrentTasksModelFlex {
	
	private String key;
	private String resultTransactors;
	private String specials;//
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getResultTransactors() {
		return resultTransactors;
	}
	public void setResultTransactors(String resultTransactors) {
		this.resultTransactors = resultTransactors;
	}
	public String getSpecials() {
		return specials;
	}
	public void setSpecials(String specials) {
		this.specials = specials;
	}

	
}
