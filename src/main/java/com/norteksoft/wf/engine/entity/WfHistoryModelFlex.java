package com.norteksoft.wf.engine.entity;

/**
 * 流程示例历史记录。flex用的model
 */
public class WfHistoryModelFlex {
	
	private String taskName;
	private String transactor;
	private String transactionResult;
	private String transactorOpinion;
	private String specialTask;
	
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	public String getTransactor() {
		return transactor;
	}
	public void setTransactor(String transactor) {
		this.transactor = transactor;
	}
	public String getTransactionResult() {
		return transactionResult;
	}
	public void setTransactionResult(String transactionResult) {
		this.transactionResult = transactionResult;
	}
	public String getTransactorOpinion() {
		return transactorOpinion;
	}
	public void setTransactorOpinion(String transactorOpinion) {
		this.transactorOpinion = transactorOpinion;
	}
	public String getSpecialTask() {
		return specialTask;
	}
	public void setSpecialTask(String specialTask) {
		this.specialTask = specialTask;
	}
	
	
}
