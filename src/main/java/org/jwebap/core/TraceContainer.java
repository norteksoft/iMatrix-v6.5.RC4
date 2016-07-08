package org.jwebap.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * 基于内存和硬盘钝化的轨迹收集器
 * @author leadyu
 * @since Jwebap 0.5
 * @date  2007-8-8
 */
public class TraceContainer implements TraceLiftcycleManager{
	
	protected Map analysers=null;
	
	
	private abstract class DoProcess{
		void doProcess(Object traceType, Trace trace){
			Collection as=(Collection)analysers.get(traceType);
			if(as==null){
				return;
			}
			Iterator it=as.iterator();
			while(it.hasNext()){
				Analyser analyser=(Analyser)it.next();
				doEvent(analyser,trace);
			}
		}
		abstract void doEvent(Analyser analyser,Trace trace);
	};
	
	
	public TraceContainer(){
		analysers=new HashMap();
	}
	
	public void activateTrace(Object traceType, Trace trace) {
		DoProcess process=new DoProcess(){
			void doEvent(Analyser analyser,Trace trace){
				//TODO 以后可采用异步触发形式
				analyser.activeProcess(trace);
			}		
		};
		process.doProcess(traceType,trace);	
	}

	public void inactivateTrace(Object traceType,Trace trace) {
		DoProcess process=new DoProcess(){
			void doEvent(Analyser analyser,Trace trace){
				//TODO 以后可采用异步触发形式
				analyser.inactiveProcess(trace);
			}		
		};
		process.doProcess(traceType,trace);	
		
		//TODO 钝化轨迹
		passivate(trace);
	}

	public void destoryTrace(Object traceType,Trace trace) {
		DoProcess process=new DoProcess(){
			void doEvent(Analyser analyser,Trace trace){
				//TODO 以后可采用异步触发形式
				analyser.destoryProcess(trace);
			}		
		};
		process.doProcess(traceType,trace);	
	}

	public void registerAnalyser(Object traceType,Analyser analyser) {
		if(analysers.get(traceType)==null){
			analysers.put(traceType,new ArrayList());
		}
		Collection as=(Collection)analysers.get(traceType);
		as.add(analyser);
	}

	public void unregisterAnalyser(Object traceType,Analyser analyser) {
		Collection as=(Collection)analysers.get(traceType);
		if(as!=null){
			as.remove(analyser);
		}
	}
	
	private void passivate(Trace trace){
		
	}

}
