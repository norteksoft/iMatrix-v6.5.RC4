package com.norteksoft.product.util;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Map.Entry;


import com.danga.MemCached.MemCachedClient;

/**
 * MemCached 工具类
 * @author xiao j
 */
public class MemCachedUtils {
	public static final String DEFAULT_CHARSET = "UTF-8";
	public static final int MAX_SIZE=1024 * 1024;//memcached 的 item size 最大是1M
	public static final String COMPRESSED_FLAG="-$$$$-";
	private MemCachedUtils(){}
	
	public static MemCachedClient getMemCachedClient(){
		return (MemCachedClient) ContextUtils.getBean("memcachedClient");
	}
	
	/**
	 * 向缓存中添加信息
	 * @param key
	 * @param value
	 * @return 是否添加成功
	 */
	public static boolean add(String key, Object value){
		//只对字符串做压缩处理，其他类型不处理
		if(value instanceof String ){
			String original=(String)value;
			try {
				if(original.getBytes(DEFAULT_CHARSET).length>=MAX_SIZE){
					return getMemCachedClient().set(key+COMPRESSED_FLAG, ZipUtils.gzip(original));
				}
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}
		}
		return getMemCachedClient().set(key, value);
	}
	
	/**
	 * 批量添加信息
	 * @param map
	 */
	public static void add(Map<String, Object> map){
		for(Entry<String, Object> keyValue : map.entrySet()){
			add(keyValue.getKey(), keyValue.getValue());
		}
	}
	
	/**
	 * 从缓存中获取信息
	 * @param key
	 * @return
	 * @throws Exception 
	 */
	public static Object get(String key){
		MemCachedClient client = getMemCachedClient();
		Object obj =  client.get(key);
		//取不到值时尝试找压缩过的key，带特殊标记
		if(obj==null){
			obj=client.get(key+COMPRESSED_FLAG);
			obj=ZipUtils.ungzip((String)obj);
		}
		return obj;
	}
	/**
	 * 删除缓存信息
	 * @param key
	 * @return
	 */
	public static boolean delete(String key){
		boolean result=false;
		result=getMemCachedClient().delete(key);
		if(!result){
			result=getMemCachedClient().delete(key+COMPRESSED_FLAG);
		}
		return result;
	}
}
