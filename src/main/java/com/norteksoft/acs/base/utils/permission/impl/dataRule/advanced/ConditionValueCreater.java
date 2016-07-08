package com.norteksoft.acs.base.utils.permission.impl.dataRule.advanced;
/**
 * 动态计算值
 * @author nortek
 *
 */
public interface ConditionValueCreater {
	/**
	 * 获取条件值，返回值有两种形式：1、只有一个字符串，2、以逗号分隔的多个字符串
	 * @param tableName
	 * @param fieldName
	 * @return
	 */
	public String getValues(String tableName,String fieldName);
}
