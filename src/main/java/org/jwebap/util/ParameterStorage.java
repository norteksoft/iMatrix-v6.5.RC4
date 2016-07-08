package org.jwebap.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 参数集合实现类
 * 
 * @author leadyu(yu-lead@163.com)
 * @since Jwebap 0.6
 * @date  2008-10-6
 */
public class ParameterStorage implements ParameterMap{
	/**
	 *参数Map 
	 */
	private Map paramMap=new HashMap();
	
	public ParameterStorage(Map map){
		paramMap=map;
	}
	
	public ParameterStorage(ParameterMap map) {
		String[] names=map.propNames();
		for(int i=0;i<names.length;i++){
			putProperty(names[i],map.getProperty(names[i]));
		}
	}
	
	protected Map getParamMap() {
		return paramMap;
	}

	public ParameterStorage(){
		paramMap=new HashMap();
	}
	
	/**
	 * 返回参数值，找不到时返回空串
	 */
	public String getProperty(String key) {
		String value=(String)paramMap.get(key);
		return value==null?"":value;
	}

	/**
	 * 设置参数
	 */
	public void putProperty(String key, String value) {
		paramMap.put(key,value);
	}
	
	/**
	 * 返回所有参数名
	 */
	public String[] propNames(){
		String[] keys=new String[paramMap.size()];
		Set keySet=paramMap.keySet();
		keySet.toArray(keys);
		return keys;
	}

}