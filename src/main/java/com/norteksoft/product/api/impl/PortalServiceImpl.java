package com.norteksoft.product.api.impl;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.acs.entity.organization.User;
import com.norteksoft.portal.base.enumeration.MessageType;
import com.norteksoft.portal.service.MessageInfoManager;
import com.norteksoft.portal.service.UserCurrentLanguageManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.OnlyUniquelyException;
import com.norteksoft.product.api.PortalService;
import com.norteksoft.product.api.entity.Message;
import com.norteksoft.product.api.utils.BeanUtil;
import com.norteksoft.product.util.ContextUtils;

@Service
@Transactional
public class PortalServiceImpl implements PortalService {

	@Autowired
	private MessageInfoManager messageManager;
	@Autowired
	private UserCurrentLanguageManager userCurrentLanguageManager;
	
	/**
	 * 增加消息
	 * @param systemCode 系统code
	 * @param sender 发件人名字
	 * @param senderLoginName 发件人登陆名
	 * @param receiverLoginName 收件人登陆名
	 * @param category 类别
	 * @param content 内容
	 * @param url 弹窗的链接
	 */
	@Deprecated
	public Long addMessage(String systemCode,String sender,String senderLoginName,String receiverLoginName,String category,
			String content, String url) throws Exception{
		return messageManager.saveMessage(systemCode,sender, senderLoginName,receiverLoginName,category, content, url,MessageType.SYSTEM_MESSAGE,true,null);
	}
	/**
	 * 增加消息
	 * @param systemCode 系统code
	 * @param sender 发件人名字
	 * @param senderId 发件人id
	 * @param receiverId 收件人id
	 * @param category 类别
	 * @param content 内容
	 * @param url 弹窗的链接
	 */
	@Deprecated
	public Long addMessage(String systemCode,String sender,Long senderId,Long receiverId,String category,
			String content, String url)
			throws Exception {
		return messageManager.saveMessage(systemCode,sender, senderId,receiverId,category, content, url,MessageType.SYSTEM_MESSAGE,true,null);
	}
	/**
	 * 增加消息
	 * @param systemCode 系统code
	 * @param sender 发件人名字
	 * @param senderLoginName 发件人登陆名
	 * @param receiverLoginName 收件人登陆名
	 * @param category 类别
	 * @param content 内容
	 * @param url 弹窗的链接
	 * @param automatic  true为自动稳藏，false为手动处理
	 * @param uniquely  唯一标识
	 */
	public Long addMessage(String systemCode,String sender,String senderLoginName,String receiverLoginName,String category,
			String content, String url,boolean automatic,String uniquely) throws Exception{
		return messageManager.saveMessage(systemCode,sender, senderLoginName,receiverLoginName,category, content, url,MessageType.SYSTEM_MESSAGE,automatic,uniquely);
	}
	/**
	 * 增加消息
	 * @param systemCode 系统code
	 * @param sender 发件人名字
	 * @param senderId 发件人id
	 * @param receiverId 收件人id
	 * @param category 类别
	 * @param content 内容
	 * @param url 弹窗的链接
	 * @param automatic  true为自动稳藏，false为手动处理
	 * @param uniquely  唯一标识
	 */
	public Long addMessage(String systemCode,String sender,Long senderId,Long receiverId,String category,
			String content, String url,boolean automatic,String uniquely)throws Exception {
		return messageManager.saveMessage(systemCode,sender, senderId,receiverId,category, content, url,MessageType.SYSTEM_MESSAGE,automatic,uniquely);
	}
	@Deprecated
	public Long addMessage(String systemCode,String sender,Long senderId,Long receiverId,String category,
			String content, String url,Long taskId,String instanceId) throws Exception {
		return messageManager.saveMessage(systemCode,sender, senderId,receiverId,category, content, url,MessageType.SYSTEM_MESSAGE,true,"task-"+taskId+"-"+instanceId);
		
	}
	public void setMessageReadedByInstanceId(String instanceId) {
		messageManager.updateMessageReadedByInstanceId(instanceId);
		
	}
	public void setMessageReadedByTaskId(Long taskId,Boolean visible) {
		messageManager.updateMessageReadedByTaskId(taskId,visible);
		
	}
	@Deprecated
	public Long addMessage(String systemCode,String sender,String senderLoginName,String receiverLoginName,String category,
			String content, String url,Long taskId,String instanceId)  throws Exception {
		return messageManager.saveMessage(systemCode,sender, senderLoginName,receiverLoginName,category, content, url,MessageType.SYSTEM_MESSAGE,true,"task-"+taskId+"-"+instanceId);
		
	}
	public void setMessageReadedByTaskIds(Collection<Long> taskIds) {
		messageManager.updateMessageReadedByTaskIds(taskIds);
		
	}
	public void updateMessageUrl(Long messageId, String url) {
		messageManager.updateMessageUrlById(messageId, url);
		
	}
	public Message getMessageById(Long messageId) {
		com.norteksoft.portal.entity.Message message = messageManager.getMessage(messageId);
		return BeanUtil.turnToModelMessage(message);
	}
	
	/**
	 * 设置消息显示状态
	 * @param uniquely  唯一标识
	 * @param display  显示状态
	 * @throws OnlyUniqelyException
	 */
	public void setMessageDisplayState(String uniquely,boolean display)throws OnlyUniquelyException{
		messageManager.setMessageDisplayState(uniquely, display);
	}
	
	
	/**
	 * 关闭消息
	 * @param uniquely  唯一标识
	 * @throws OnlyUniqelyException
	 */
	public void closeMessage(String uniquely)throws OnlyUniquelyException{
		messageManager.closeMessage(uniquely);
	}
	
	public String getUserLanguageById(Long userId) {
		Long companyId = ContextUtils.getCompanyId();
		if(companyId==null){
			com.norteksoft.product.api.entity.User user = ApiFactory.getAcsService().getUserById(userId);
			if(user!=null)companyId = user.getCompanyId();
		}
		if(companyId!=null)
			return userCurrentLanguageManager.getUserLanguageByUserId(userId,companyId);
		return "zh_CN";
	}

}
