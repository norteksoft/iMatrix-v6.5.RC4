package org.jwebap.plugin.tracer.method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jwebap.core.Component;
import org.jwebap.core.ComponentContext;
import org.jwebap.core.TraceLiftcycleManager;
import org.jwebap.plugin.tracer.DBA;
import org.jwebap.plugin.tracer.FrequencyAnalyser;
import org.jwebap.plugin.tracer.TimeFilterAnalyser;
import org.jwebap.toolkit.bytecode.ClassEnhancer;
import org.jwebap.toolkit.bytecode.PackageEnhancer;

/**
 * @className MethodComponent:类监控组件
 * 
 * 可以对类，对包进行监控
 * 
 * @link TraceMethodHandle
 * @author leadyu
 * @since Jwebap 0.5
 * @date  2007-8-14
 */
public class MethodComponent implements Component{
	private static final Log log = LogFactory.getLog(MethodComponent.class);
	private ComponentContext componentContext=null;
	
	public static final Object METHOD_TRACE_TYPE=new Object();
	
	protected TimeFilterAnalyser timeAnalyser=null;
	protected FrequencyAnalyser  frequencyAnalyser=null;
	
	
	public void startup(ComponentContext context) {
		DBA.getParmeter();
		componentContext=context;
		TraceLiftcycleManager container=componentContext.getContainer();

		timeAnalyser=new TimeFilterAnalyser();
		frequencyAnalyser=new FrequencyAnalyser();
		//zzl  
		String maxSize=DBA.trace_max_size_meth;
		String activeTime=DBA.trace_filter_active_time_meth;
		
		try{
			timeAnalyser.setMaxTraceSize(Integer.parseInt(maxSize));
		}catch(Exception e){timeAnalyser.setMaxTraceSize(1000);}
		try{
			timeAnalyser.setTracefilterActivetime(Integer.parseInt(activeTime));
		}catch(Exception e){timeAnalyser.setTracefilterActivetime(-1);}
	
		container.registerAnalyser(METHOD_TRACE_TYPE,timeAnalyser);
		container.registerAnalyser(METHOD_TRACE_TYPE,frequencyAnalyser);
		String clazzs = context.getProperty("detect-clazzs");
		if(DBA.detect_clazzs!=null)clazzs=DBA.detect_clazzs;
		String[] classNames=getClassNames(clazzs);
		//inject trace
		injectClass(classNames);
		log.info("methodcomponent startup.");
	}
	
	private String[] getClassNames(String clazzs)
    {
        
		if (clazzs == null || "".equals(clazzs)) {
			return new String[0];
		}
        clazzs=clazzs.replaceAll("\n","");
        clazzs=clazzs.replaceAll("\r","");
        clazzs=clazzs.replaceAll("\t","");
        clazzs=clazzs.replaceAll(" ","");
        try
        {
            return clazzs.split(";");
        }catch(Exception e)
        {
            return new String[0];
        }
    }
	
	private void injectClass(String[] resourceNames) {	
		ClassEnhancer enhancer = new ClassEnhancer();
		PackageEnhancer pEnhancer = new PackageEnhancer();
		TraceLiftcycleManager container=getComponentContext().getContainer();
		
		for (int i = 0; i < resourceNames.length; i++) {

			String resourceName = resourceNames[i];
			try {
				if(resourceName.endsWith("*")){
					//inject package
					pEnhancer.createPackage(resourceName,new TraceMethodHandle(container));
				}else{
					//inject class.
					enhancer.createClass(resourceName,new TraceMethodHandle(container));
				}
			} catch (Throwable e) {
				e.printStackTrace();
				log.warn("对类/包：" + resourceName + "的监听部署失败，不过这并不影响系统的运行。");

			}

		}
	}

	public void destory() {
		timeAnalyser.clear();
		frequencyAnalyser.clear();
		TraceLiftcycleManager container=getComponentContext().getContainer();
		
		container.unregisterAnalyser(METHOD_TRACE_TYPE,timeAnalyser);
		container.unregisterAnalyser(METHOD_TRACE_TYPE,frequencyAnalyser);
		
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
