package org.jwebap.core;


/**
 * 轨迹收集器，这个类的设计关乎整个组件的性能
 * @author leadyu
 * @since Jwebap 0.5
 * @date  2007-8-8
 */
public interface TraceLiftcycleManager {
	public void activateTrace(Object traceType,Trace trace);
	public void inactivateTrace(Object traceType,Trace trace);
	public void destoryTrace(Object traceType,Trace trace);
	public void registerAnalyser(Object traceType,Analyser analyser);
	public void unregisterAnalyser(Object traceType,Analyser analyser);

}
