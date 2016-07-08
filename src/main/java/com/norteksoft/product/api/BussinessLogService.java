package com.norteksoft.product.api;

import java.util.List;

public interface BussinessLogService {
	
	/**
	 * 记录日志
	 * @param operationType 日志操作类型
	 * @param message       日志信息
	 */
	void log(String operationType, String message);

	/**
	 * 记录日志
	 * @param operationType  日志操作类型
	 * @param message        日志信息
	 * @param systemId       系统ID
	 */
	void log(String operationType, String message, Long systemId);
	
	/**
	 * 
	 * @param operator       日志操作人
	 * @param operationType  日志操作类型
	 * @param message        日志信息
	 */
	void log(String operator, String operationType, String message);

	/**
	 * 
	 * @param operatorId     日志操作人ID
	 * @param operationType  日志操作类型
	 * @param message        日志信息
	 */
	void log(Long operatorId, String operationType, String message);
	/**
	 * 获得国际化信息
	 * @param code 国际化编号
	 * @return
	 */
	String getI18nLogInfo(String code);
	/**
	 * 获得国际化信息
	 * @param code 国际化编号
	 * @param language 语言。zh_CN表示中文，en_US表示英文
	 * @return
	 */
	String getI18nLogInfo(String code,String language);
	/**
	 * 获得国际化信息
	 * @param code 国际化编号
	 * @param messages  替换的占位符的值，例如国际化值为：删除成功{0}个,删除失败{1}个，{0},{1}的值需要在该集合中传过去
	 * @return
	 */
	String getI18nLogInfo(String code,List<String> messages);
	/**
	 * 获得国际化信息
	 * @param code 国际化编号
	 * @param language 语言。zh_CN表示中文，en_US表示英文
	 * @param messages  替换的占位符的值，例如国际化值为：删除成功{0}个,删除失败{1}个，{0},{1}的值需要在该集合中传过去
	 * @return
	 */
	String getI18nLogInfo(String code,String language,List<String> messages);
}
