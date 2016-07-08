package com.norteksoft.product.api;

import java.util.Collection;

import com.norteksoft.product.api.entity.Message;

public interface PortalService {
	/**
	 * 增加消息
	 * @param systemCode 系统code
	 * @param name 发件人名子
	 * @param loginName 发件人登陆名
	 * @param receiverLoginName 收件人登陆名
	 * @param type 类型
	 * @param info 内容
	 * @param url 弹窗的链接
	 * @throws Exception 
	 */
	@Deprecated
	public Long addMessage(String systemCode,String name,String loginName,String receiverLoginName,String type,String info,String url) throws Exception;
	/**
	 * 增加消息
	 * @param systemCode 系统code
	 * @param name 发件人名子
	 * @param user 发件人用户id
	 * @param receiverId 收件人用户id
	 * @param type 类型
	 * @param info 内容
	 * @param url 弹窗的链接
	 * @throws Exception 
	 */
	@Deprecated
	public Long addMessage(String systemCode,String name,Long userId,Long receiverId,String type,String info,String url) throws Exception;
	/**
	 * 增加消息
	 * @param systemCode 系统code
	 * @param name 发件人名子
	 * @param loginName 发件人登陆名
	 * @param receiverLoginName 收件人登陆名
	 * @param type 类型
	 * @param info 内容
	 * @param url 弹窗的链接
	 * @param automatic  true为自动稳藏，false为手动处理
	 * @param uniquely  唯一标识
	 * @throws Exception 
	 */
	public Long addMessage(String systemCode,String name,String loginName,String receiverLoginName,String type,String info,String url,boolean automatic,String uniquely) throws Exception;
	/**
	 * 增加消息
	 * @param systemCode 系统code
	 * @param name 发件人名子
	 * @param user 发件人用户id
	 * @param receiverId 收件人用户id
	 * @param type 类型
	 * @param info 内容
	 * @param url 弹窗的链接
	 * @param automatic  true为自动稳藏，false为手动处理
	 * @param uniquely  唯一标识
	 * @throws Exception 
	 */
	public Long addMessage(String systemCode,String name,Long userId,Long receiverId,String type,String info,String url,boolean automatic,String uniquely) throws Exception;
	/**
	 * 增加消息
	 * @param systemCode 系统code
	 * @param name 发件人名子
	 * @param loginName 发件人登陆名
	 * @param receiverLoginName 收件人登陆名
	 * @param type 类型
	 * @param info 内容
	 * @param url 弹窗的链接
	 * @param taskId 任务id
	 * @param instanceId 实例id
	 * @throws Exception 
	 */
	@Deprecated
	public Long addMessage(String systemCode,String name,String loginName,String receiverLoginName,String type,String info,String url,Long taskId,String instanceId) throws Exception;
	/**
	 * 增加消息
	 * @param systemCode 系统code
	 * @param name 发件人名子
	 * @param user 发件人用户id
	 * @param receiverId 收件人用户id
	 * @param type 类型
	 * @param info 内容
	 * @param url 弹窗的链接
	 * @param taskId 任务id
	 * @param instanceId 实例id
	 * @throws Exception
	 */
	@Deprecated
	public Long addMessage(String systemCode,String sender,Long senderId,Long receiverId,String category,
			String content, String url,Long taskId,String instanceId) throws Exception;
	/**
	 * 更新消息的url字段的值
	 * @param messageId
	 * @param url
	 */
	public void updateMessageUrl(Long messageId,String url);
	/**
	 * 设置该流程实例对应的所有消息的状态为已读
	 * @param instanceId
	 */
	@Deprecated
	public void setMessageReadedByInstanceId(String instanceId);
	/**
	 * 设置该任务对应的消息的状态为已读
	 * @param taskId
	 * @param visible:false已读，true:未读
	 */
	@Deprecated
	public void setMessageReadedByTaskId(Long taskId,Boolean visible);
	/**
	 * 设置任务对应的消息的状态为已读
	 * @param taskIds
	 */
	@Deprecated
	public void setMessageReadedByTaskIds(Collection<Long> taskIds);
	
	public Message getMessageById(Long messageId);
	
	/**
	 * 设置消息显示状态
	 * @param uniquely  唯一标识
	 * @param display  显示状态
	 * @throws OnlyUniquelyException
	 */
	public void setMessageDisplayState(String uniquely,boolean display)throws OnlyUniquelyException;
	
	/**
	 * 关闭消息
	 * @param uniquely  唯一标识
	 * @throws OnlyUniquelyException
	 */
	public void closeMessage(String uniquely)throws OnlyUniquelyException;
	/**
	 * 获得用户的语言
	 * @param userId
	 * @return
	 */
	public String getUserLanguageById(Long userId);
}
