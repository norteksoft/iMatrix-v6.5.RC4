package org.jwebap.ui.controler;

import org.jwebap.cfg.model.DispatcherDef;
import org.jwebap.core.AbstractContext;

/**
 * 默认的分发器上下文实现
 * 
 * @author leadyu(yu-lead@163.com)
 * @since Jwebap 0.5
 * @date  2007-12-26
 */
public class DispatcherContextImpl extends AbstractContext implements DispatcherContext{
	
	DispatcherDef dipatcherDef=null;
	
	DispatcherContextImpl(DispatcherDef dipatcherDef){
		this.dipatcherDef=dipatcherDef;
	}
	
	public String getDispatcherPath() {
		Mapper mapping=dipatcherDef.getMapper();
		
		if(mapping!=null){
			return mapping.getMappingPath();
		}
		
		return null;
	}


}
