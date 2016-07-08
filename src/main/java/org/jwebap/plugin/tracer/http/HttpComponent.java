package org.jwebap.plugin.tracer.http;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jwebap.core.Component;
import org.jwebap.core.ComponentContext;
import org.jwebap.core.TraceLiftcycleManager;
import org.jwebap.plugin.tracer.DBA;
import org.jwebap.plugin.tracer.FrequencyAnalyser;
import org.jwebap.plugin.tracer.TimeFilterAnalyser;

/**
 * http请求监控组件
 * @author leadyu
 * @since Jwebap 0.5
 * @date  2007-8-14
 */
public class HttpComponent implements Component{
	private static final Log log = LogFactory.getLog(HttpComponent.class);

	public static final Object HTTP_TRACE_TYPE=new Object();
	
	private TimeFilterAnalyser timeAnalyser=null;
	private FrequencyAnalyser  frequencyAnalyser=null;
	private ComponentContext componentContext=null;
	
	
	public void startup(ComponentContext context) {
		DBA.getParmeter();
		componentContext=context;
		TraceLiftcycleManager container=componentContext.getContainer();
		
		timeAnalyser=new TimeFilterAnalyser();
		frequencyAnalyser=new FrequencyAnalyser();
		//zzl  
		String maxSize=DBA.trace_max_size_http;
		String activeTime=DBA.trace_filter_active_time_http;
		
		try{
			timeAnalyser.setMaxTraceSize(Integer.parseInt(maxSize));
		}catch(Exception e){timeAnalyser.setMaxTraceSize(1000);}
		try{
			timeAnalyser.setTracefilterActivetime(Integer.parseInt(activeTime));
		}catch(Exception e){timeAnalyser.setTracefilterActivetime(-1);}
	
		
		container.registerAnalyser(HTTP_TRACE_TYPE,timeAnalyser);
		container.registerAnalyser(HTTP_TRACE_TYPE,frequencyAnalyser);
		DetectFilter.setContainer(container);
		log.info("httpcomponent startup.");
		// TODO inject trace
		//do noting,because it's not use bytecode enhance here.
	}

	public void destory() {
		timeAnalyser.clear();
		frequencyAnalyser.clear();
		TraceLiftcycleManager container=componentContext.getContainer();
		
		container.unregisterAnalyser(HTTP_TRACE_TYPE,timeAnalyser);
		container.unregisterAnalyser(HTTP_TRACE_TYPE,frequencyAnalyser);
		
		timeAnalyser=null;
		frequencyAnalyser=null;
	}

	public void clear() {
		timeAnalyser.clear();
		frequencyAnalyser.clear();		
	}

	public FrequencyAnalyser getFrequencyAnalyser() {
		return frequencyAnalyser;
	}

	public void setFrequencyAnalyser(FrequencyAnalyser frequencyAnalyser) {
		this.frequencyAnalyser = frequencyAnalyser;
	}

	public TimeFilterAnalyser getTimeAnalyser() {
		return timeAnalyser;
	}

	public void setTimeAnalyser(TimeFilterAnalyser timeAnalyser) {
		this.timeAnalyser = timeAnalyser;
	}

	public ComponentContext getComponentContext() {
		return componentContext;
	}

}
