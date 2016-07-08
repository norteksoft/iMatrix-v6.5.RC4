package com.norteksoft.task.base.enumeration;

/**
 * 任务处理结果
 * 办理任务时执行的操作
 * @author wurong
 *
 */
public enum TaskProcessingResult {
	/**
	 * 同意
	 */
	APPROVE("approve", "transition.approval.result.agree", "transition.approval.result.agree"),
	/**
	 * 不同意
	 */
	REFUSE("refuse", "transition.approval.result.disagree", "transition.approval.result.disagree"),
	/**
	 * 赞成
	 */
	AGREEMENT("agreement", "赞成","agreement.name"),
	/**
	 * 反对
	 */
	OPPOSE("oppose", "反对","oppose.name"),
	/**
	 * 弃权
	 */
	KIKEN("kiken", "弃权", "kiken.name"),
	/**
	 * 签收
	 */
	SIGNOFF("signoff", "签收", "signoff.name"),
	/**
	 * 提交
	 */
	SUBMIT("submit", "提交", "submit.name"),
	/**
	 * 交办
	 */
	ASSIGN("assign", "交办", "assign.name"),
	/**
	 * 分发
	 */
	DISTRIBUTE("distribute", "分发", "wf.text.distribute"),
	
	/**
	 * 已阅
	 */
	READED("readed","已阅","wf.text.seen"),
	/**
	 * 指派
	 */
	ASSIGN_TASK("assign_task", "指派", "appoint.name");
	
    
    String key;
    String name;
    String i18nName;
    TaskProcessingResult(String key, String name,String i18nName){
        this.key = key;
        this.name = name;
        this.i18nName=i18nName;
    }

    /**
     * 该操作的key
     */
	@Override
    public String toString() {
        return this.key;
    }
	/**
	 * 该操作的名称
	 * @return 名称
	 */
	public String getName(){
		return name;
	}
	/**
	 * 该操作的名称的国际化key值
	 * @return 名称
	 */
	public String getI18nName(){
		return i18nName;
	}
	/**
	 * 该操作的名称
	 * @return 名称
	 */
	public String getKey(){
		return key;
	}
}
