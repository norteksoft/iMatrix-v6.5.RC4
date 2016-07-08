package com.norteksoft.portal.web.index;


import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;

import com.norteksoft.acs.entity.organization.User;
import com.norteksoft.acs.service.AcsUtils;
import com.norteksoft.portal.base.enumeration.MessageType;
import com.norteksoft.portal.entity.BaseSetting;
import com.norteksoft.portal.entity.Message;
import com.norteksoft.portal.entity.Widget;
import com.norteksoft.portal.service.BaseSettingManager;
import com.norteksoft.portal.service.IndexManager;
import com.norteksoft.portal.service.MessageInfoManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.PageUtils;
import com.norteksoft.product.util.PropUtils;
import com.norteksoft.product.util.SystemUrls;
import com.norteksoft.product.util.freemarker.TagUtil;
import com.norteksoft.product.web.struts2.CrudActionSupport;
import com.norteksoft.product.web.struts2.Struts2Utils;
/**
 * 个人消息管理
 * @author zzl
 *
 */

@Namespace("/index")
@ParentPackage("default")
@Results({@Result(name=CrudActionSupport.RELOAD,location="my-message",type="redirectAction")})
public class MyMessageAction extends CrudActionSupport<Message>{
	private static final long serialVersionUID = 1L;

	private Long id;
	
	private String ids;
	
	private Page<Message> pages = new Page<Message>(0, true);
	
	private List<Message> messages= new ArrayList<Message>();

	private Message message;
	
	private String messageType="SYSTEM_MESSAGE";
	
	private String userNames;
	
	private String loginNames;
	
	private Boolean isOpen=false;//是否是open出来的页面
	
	private Boolean bl;
	
	private Long messageId;
	
	@Autowired
	private MessageInfoManager messageManager;
	
	@Autowired
	private AcsUtils acsUtils;
	
	@Autowired
	private BaseSettingManager baseSettingManager;
	@Autowired
	private IndexManager indexManager;
	
	/**
	 * 删除
	 */
	@Action("my-message-delete")
	@Override
	public String delete() throws Exception {
		int num = messageManager.deleteMessage(ids);
		addActionMessage("<font class=\"onSuccess\"><nobr>"+Struts2Utils.getText("message.delete.tip")+" "+num+" "+Struts2Utils.getText("message.tip")+"</nobr></font>");
		ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("portal.messageManagement"), 
				ApiFactory.getBussinessLogService().getI18nLogInfo("portal.deleteMesage"), ContextUtils.getSystemId("portal"));
		return "my-message";
	}
	
	/**
	 * 标识为读取状态
	 */
	@Action("my-message-stateAll")
	public String stateAll() throws Exception {
		int num = messageManager.setMessageState(ids,bl);
		addActionMessage("<font class=\"onSuccess\"><nobr>"+Struts2Utils.getText("message.success.tip")+" "+num+" "+Struts2Utils.getText("message.tip")+"</nobr></font>");
		ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("portal.messageManagement"),
				ApiFactory.getBussinessLogService().getI18nLogInfo("portal.signMessage"), ContextUtils.getSystemId("portal"));
		return "my-message";
	}
	
	/**
	 * 清空
	 * @return
	 * @throws Exception
	 */
	@Action("my-message-deleteAll")
	public String deleteAll() throws Exception {
		int num = 0;
		if(messageType.equals("SYSTEM_MESSAGE")){
			 num = messageManager.deleteMessage(ContextUtils.getLoginName() ,ContextUtils.getUserId() ,ContextUtils.getCompanyId(), MessageType.SYSTEM_MESSAGE);
		}else if(messageType.equals("ONLINE_MESSAGE")){
			 num = messageManager.deleteMessage(ContextUtils.getLoginName() ,ContextUtils.getUserId() ,ContextUtils.getCompanyId(), MessageType.ONLINE_MESSAGE);
		}
		addActionMessage("<font class=\"onSuccess\"><nobr>"+Struts2Utils.getText("message.delete.tip")+" "+num+" "+Struts2Utils.getText("message.tip")+"</nobr></font>");
		ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("portal.messageManagement"), 
				ApiFactory.getBussinessLogService().getI18nLogInfo("portal.clearMessage"), ContextUtils.getSystemId("portal"));
		return "my-message";
	}

	/**
	 * 新建页面
	 */
	@Action("my-message-input")
	@Override
	public String input() throws Exception {
		return "my-message-input";
	}

	/**
	 * 主入口
	 */
	@Override
	public String list() throws Exception {
		if(pages.getPageSize()>1){
			if(messageType.equals("SYSTEM_MESSAGE")){
				messageManager.getMessages(pages,ContextUtils.getLoginName() ,ContextUtils.getUserId() ,ContextUtils.getCompanyId(), MessageType.SYSTEM_MESSAGE);//系统
			}else if(messageType.equals("ONLINE_MESSAGE")){
				messageManager.getMessages(pages,ContextUtils.getLoginName() ,ContextUtils.getUserId() ,ContextUtils.getCompanyId(), MessageType.ONLINE_MESSAGE);//系统
			}
			this.renderText(PageUtils.pageToJson(pages));
			ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("portal.messageManagement"), 
					ApiFactory.getBussinessLogService().getI18nLogInfo("portal.viewMessageList"), ContextUtils.getSystemId("portal"));
			return null;
		}
		return SUCCESS;
	}

	@Override
	protected void prepareModel() throws Exception {
		if(id==null){
			message  = new Message();
		}else{
			message= messageManager.getMessage(id);
		}
		
	}

	/**
	 * 保存
	 */
	@Action("my-message-save")
	@Override
	public String save() throws Exception {
		message.setCreatedTime(new Date());
		if(StringUtils.isNotEmpty(loginNames)){ 
			if("ALLCOMPANYID".equals(loginNames)){
				List<User> users = acsUtils.getUsersByCompany(ContextUtils.getCompanyId());
				for(User user : users){
					messageManager.saveMessageToPortal("portal", ContextUtils.getUserName(),ContextUtils.getUserId(), user, Struts2Utils.getText("message.online.type"), message.getContent(), "/index/my-message-view.htm?isOpen=true&id=",MessageType.valueOf(messageType));
				}
			}else{
				if(ids!=null&&!ids.equals("")){
					messageManager.saveMessageToPortal("portal", ContextUtils.getUserName(),ContextUtils.getUserId(), ids, Struts2Utils.getText("message.online.type"), message.getContent(), "/index/my-message-view.htm?isOpen=true&id=",MessageType.valueOf(messageType));
				}
			}
		}
		ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("portal.messageManagement"),
				ApiFactory.getBussinessLogService().getI18nLogInfo("portal.saveMessage"), ContextUtils.getSystemId("portal"));
		this.renderText("ok-"+messageType);
		return null;
	}
	
	/**
	 * 取信息
	 * @return
	 */
	@Action("my-message-getInfor")
	public String getInfor()throws Exception{
		BaseSetting baseSetting = baseSettingManager.getBaseSettingByCreatorId();
		if(baseSetting==null || baseSetting.getShowRows()==null){
			baseSetting = new BaseSetting();
			baseSetting.setShowRows(15);
		}
		Page<Message> messagePage = new Page<Message>(baseSetting.getShowRows(), true);
		messagePage=messageManager.getMessages(messagePage,ContextUtils.getLoginName(),ContextUtils.getUserId() ,ContextUtils.getCompanyId(),true);
		messages=messagePage.getResult();
		String callback=Struts2Utils.getParameter("callback");
		if(messages!=null&&!messages.isEmpty()){
			StringBuffer bu = new StringBuffer();
			bu.append("<div style='font-size: 12px;padding:6px 6px  2px 6px;'>");
			bu.append("<table style='width: 100%;height:100%;'>");
			SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
			for (Message mess : messages) {
				String title="<td class='remassage-title' >";
				bu.append("<tr id='mess"+mess.getId()+"'>");
				if(mess.getMessageType()!=null&&mess.getMessageType().equals(MessageType.SYSTEM_MESSAGE)){//为系统消息
					title="<td class='remassage-title' >";
				}else if(mess.getMessageType()==null||mess.getMessageType().equals(MessageType.ONLINE_MESSAGE)){//为在线个人消息
					title="<td class='remassage-title-p' >";
				}
				   bu.append(title);
				   if(mess.getMessageType()!=null&&mess.getMessageType().equals(MessageType.SYSTEM_MESSAGE)&& !mess.getCategory().equals("系统消息")){//为系统消息
					   String url=mess.getUrl();
						if(StringUtils.isNotEmpty(url)){//是否有url
							String werRoot = SystemUrls.getSystemUrl(mess.getSystemCode());
							url=werRoot+url;
							if(url.indexOf("/message-task.htm")>=0){//如果是待办事宜任务
								Widget widget = indexManager.getWidgetByCode("task");//获得待办事宜小窗体id，用于更新小窗体
								if(widget!=null){
									bu.append("<a href='#' style='text-decoration:underline;color:black;' onclick='setMessageState("+mess.getId()+");taskMessageOpen(\\\""+url.trim()+"\\\","+widget.getId()+");'>");
								}else{
									bu.append("<a href='#' style='text-decoration:underline;color:black;' onclick='setMessageState("+mess.getId()+");messageOpen(\\\""+url.trim()+"\\\");'>");
								}
							}else{
								bu.append("<a href='#' style='text-decoration:underline;color:black;' onclick='setMessageState("+mess.getId()+");messageOpen(\\\""+url.trim()+"\\\");'>");
							}
						}else{
							bu.append("<a href='#' style='text-decoration:underline;color:black;' onclick='setMessageState("+mess.getId()+")'>");
						}
					  
					}else if(mess.getCategory().equals("系统消息")&&mess.getMessageType().equals(MessageType.SYSTEM_MESSAGE)){
						bu.append("<a href='#' style='text-decoration:underline;color:black;' onclick='openMessageInput("+mess.getId()+")'>");
					}else if(mess.getMessageType()==null||mess.getMessageType().equals(MessageType.ONLINE_MESSAGE)){//为在线个人消息
						 bu.append("<a href='#' style='text-decoration:underline;color:black;' onclick='openMessageInput("+mess.getId()+")'>");
					}
						   String str=mess.getContent();
							if(StringUtils.isEmpty(str)){
								str=Struts2Utils.getText("message.empty");
							}else if(StringUtils.isNotEmpty(str)&&str.length()>60){
								str=str.replace("\r", "\\n").replace("\n", "\\n").replace("\t", "\\n").replace("\r\n", "\\n").replace("\n", "\\n").replace("\"", "‘").replace("\\\\", "\\\\\\\\");
								str=StringUtils.substring(str, 0, 59)+"...";
							}else{
								str=str.replace("\r", "\\n").replace("\n", "\\n").replace("\t", "\\n").replace("\r\n", "\\n").replace("\n", "\\n").replace("\"", "‘").replace("\\\\", "\\\\\\\\");
								
							}
							bu.append(str);
						bu.append("</a>");
					bu.append("</td>");
					bu.append("<td class='remassage-name' >");
						bu.append(mess.getSender()+"<br>"+format.format(mess.getCreatedTime()));
					bu.append("</td>");
				bu.append("</tr>");
			}
			bu.append("</table>");
			bu.append("</div>");
			this.renderText(callback+"({msg:\""+bu.toString()+"\"})");
			return null;
		}else{
			this.renderText(callback+"({msg:\"error\"})");
			return null;
		}
	}
	
//	private String replaceMessageInfo(String content){
//		//将催办消息中固定的字符国际化显示
//		Pattern p=Pattern.compile("\\$\\{([\\w+\\.]*\\w+)\\}",Pattern.MULTILINE);
//		Matcher m=p.matcher(content);
//		while(m.find()){
//			content=m.replaceFirst(Struts2Utils.getText(m.group(1)));
//			m=p.matcher(content);
//		}
//		return content;
//	}
	
	public void prepareView() throws Exception{
		prepareModel();
	}
	
	/**
	 * 查看
	 * @return
	 * @throws Exception
	 */
	@Action("my-message-view")
	public String view()throws Exception{
		if(message.getAutomatic()==null||message.getAutomatic()){
			messageManager.setMessageState(message,false);
		}
		return "my-message-view";
	}

	public void prepareSetState() throws Exception{
		prepareModel();
	}
	
	/**
	 * 设置查看状态
	 * @return
	 * @throws Exception
	 */
	@Action("my-message-setState")
	public String setState()throws Exception{
		if(message.getAutomatic()==null||message.getAutomatic()){
			messageManager.setMessageState(message,false);
		}
		String callback=Struts2Utils.getParameter("callback");
		this.renderText(callback+"({msg:'ok'})");
		return null;
	}
	@Action("my-message-error")
	public String messageError()throws Exception{
		HttpServletResponse response = Struts2Utils.getResponse();
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer = response.getWriter();
		writer.print(readScriptTemplet());
		return null;
	}
	
	private String readScriptTemplet() throws Exception{
		String resourceCtx=PropUtils.getProp("host.resources");
		Map<String, Object> root=new HashMap<String, Object>();
		root.put("resourceCtx",resourceCtx);
		root.put("errorInfo",Struts2Utils.getParameter("errorInfo"));
		root.put("messageError",Struts2Utils.getText("messageError",ContextUtils.getCurrentLanguage()));
		String result =TagUtil.getContent(root, "message-error.ftl");
		return result;
	}
	
	
	public Message getModel() {
		return message;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public Page<Message> getPages() {
		return pages;
	}

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

	public String getLoginNames() {
		return loginNames;
	}

	public void setLoginNames(String loginNames) {
		this.loginNames = loginNames;
	}

	public String getUserNames() {
		return userNames;
	}

	public void setUserNames(String userNames) {
		this.userNames = userNames;
	}
	public List<Message> getMessages() {
		return messages;
	}

	public Boolean getIsOpen() {
		return isOpen;
	}

	public void setIsOpen(Boolean isOpen) {
		this.isOpen = isOpen;
	}

	public Boolean getBl() {
		return bl;
	}

	public void setBl(Boolean bl) {
		this.bl = bl;
	}

	public Long getMessageId() {
		return messageId;
	}

	public void setMessageId(Long messageId) {
		this.messageId = messageId;
	}

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}
	
	public static void main(String[] args) {
		Map<String, String> messages=new HashMap<String, String>();
		messages.put("eea.cee.cee.cee", "OK");
		messages.put("b.a2", "OK2");
		messages.put("c.f", "OK3");
		messages.put("d.d", "OK4");
		messages.put("dg.fg.a", "OK5");
		messages.put("tt", "OK-t");
		String content="dd${tt}(*dd${eea.cee.cee.cee}uuu${b.a2} 9999${c.f} ${d.d} ${dg.fg.a}";
		Pattern p=Pattern.compile("\\$\\{([\\w+\\.]*\\w+)\\}",Pattern.MULTILINE);
		//Pattern p=Pattern.compile("\\$\\{(\\w+\\.\\w+\\.\\w+\\.\\w+)\\}",Pattern.MULTILINE);
		Matcher m=p.matcher(content);
		while(m.find()){
			System.out.println(m.group()+":"+m.group(1));
			content=m.replaceFirst(messages.get(m.group(1)));
			m=p.matcher(content);
		}
		System.out.println(content);
		
	}

}
