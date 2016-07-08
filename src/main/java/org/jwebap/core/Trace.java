package org.jwebap.core;

import java.util.ArrayList;
import java.util.List;

/**
 * 轨迹基类
 * 
 * 在Jwebap中轨迹类是一个重要的核心模型。
 * 
 * <p>
 * 轨迹的定义
 * 
 * 轨迹用于封装对性能分析有用的信息（比如上下文的信息，线程堆栈的信息，执 行时间的信息等等）。
 * 
 * 在系统中，任何程序的执行都有可能留下轨迹（数据库的调用会留下SQL轨迹， 事务的轨迹，http请求会留下访问的轨迹，程序的方法执行也可以留下轨迹）。
 * 
 * 轨迹之间存在关系，比如数据库事务的轨迹和SQL的轨迹，http请求和类方法 执行的轨迹之间都有一定的关系。Jwebap中通过字节码注入，收集运行时的信
 * 息，封装成轨迹，供分析器(Analyser)进行统计。
 * </p>
 * 
 * @author leadyu(yu-lead@163.com)
 * @since Jwebap 0.5
 * @date 2007-04-11
 */
public class Trace{

	/**
	 * 创建时间
	 */
	private long createdTime;

	/**
	 * 不活动时间
	 */
	private long inActiveTime;

	/**
	 * 轨迹内容
	 */
	private String content;

	/**
	 * 父轨迹
	 */
	Trace parent;

	/**
	 * 子轨迹
	 */
	private List traces;

	public Trace() {
		traces = new ArrayList();
		init(parent);
	}

	public Trace(Trace parent) {
		traces = new ArrayList();
		init(parent);
	}

	private void init(Trace parent) {
		if (parent != null)
			parent.addChild(this);
		this.parent = parent;
		createdTime = System.currentTimeMillis();
	}

	/**
	 * 增加子轨迹
	 * 
	 * @param trace
	 */
	public synchronized void addChild(Trace trace) {
		this.traces.add(trace);
	}


	/**
	 * 得到子轨迹
	 * 
	 * 返回定义为数组有2个目的:
	 * 
	 * 1)返回复本，保证外在的操作不会侵入轨迹的内部运行
	 * 
	 * 2)返回类型为数组，更不易产生歧义，误以为得到的是轨迹内部的集合结构
	 * 
	 * @return
	 */
	public Trace[] getChildTraces() {
		Trace[] ts = new Trace[traces.size()];
		traces.toArray(ts);
		return ts;
	}

	/**
	 * 得到失效时间
	 * 
	 * @return
	 */
	public long getActiveTime() {
		return getInactiveTime() <= 0L ? System.currentTimeMillis()
				- getCreatedTime() : getInactiveTime() - getCreatedTime();
	}

	/**
	 * 清空子轨迹
	 */
	public synchronized void clearChildTrace() {
		if (traces != null)
			traces.clear();
	}

	/**
	 * 删除子轨迹
	 * 
	 * @param trace
	 */
	protected synchronized void removeChildTrace(Trace trace) {
		if (this.traces != null) {
			this.traces.remove(trace);
			trace.parent = null;
		}
	}

	/**
	 * 销毁轨迹
	 */
	public void destroy() {
		if (parent != null) {
			parent.removeChildTrace(this);
			parent = null;
		}
		Trace[] children = getChildTraces();
		clearChildTrace();
		for (int i = 0; i < children.length; i++) {
			Trace trace = children[i];
			trace.destroy();
		}

	}

	/**
	 * 不活动
	 */
	public void inActive() {
		if (!(inActiveTime > 0L))
			inActiveTime = System.currentTimeMillis();
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public long getInactiveTime() {
		return inActiveTime;
	}

	public long getCreatedTime() {
		return createdTime;
	}

}
