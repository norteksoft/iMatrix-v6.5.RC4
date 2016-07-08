package com.norteksoft.bs.sms.web;


import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;

import com.norteksoft.bs.sms.entity.SmsWaitTosend;
import com.norteksoft.bs.sms.service.SmsWaitTosendManager;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.PageUtils;
import com.norteksoft.product.web.struts2.CrudActionSupport;
import com.norteksoft.product.web.struts2.Struts2Utils;
/**
 * 短信平台/待发送列表
 * @author lenove1
 *
 */
@Namespace("/sms")
@ParentPackage("default")
@Results( { @Result(name = CrudActionSupport.RELOAD, location = "sms-wait-tosend", type = "redirectAction") })
public class SmsWaitToSendAction extends CrudActionSupport<SmsWaitTosend> {

	private static final long serialVersionUID = 1L;
	private Long id;
	private String ids;
	private SmsWaitTosend smsWaitTosend;
	private Page<SmsWaitTosend> page = new Page<SmsWaitTosend>(0,true);
	private static final String SUCCESS_MESSAGE_LEFT = "<font class=\"onSuccess\"><nobr>";
	private static final String MESSAGE_RIGHT = "</nobr></font>";
	private static final String ERROR_MESSAGE_LEFT = "<font class=\"onError\"><nobr>";
	protected void addErrorMessage(String message) {
		this.addActionMessage(ERROR_MESSAGE_LEFT + message + MESSAGE_RIGHT);
	}
	protected void addSuccessMessage(String message){
		this.addActionMessage(SUCCESS_MESSAGE_LEFT+message+MESSAGE_RIGHT);
	}
	@Autowired
	private SmsWaitTosendManager smsWaitTosendManager;
	
	/**
	 * 短信平台/待发送列表/列表
	 * @return
	 */
	@Override
	@Action("sms-wait-tosend")
	public String list() throws Exception {
		if(page.getPageSize() > 1){
			smsWaitTosendManager.getAllSmsWaitTosend(page);
			this.renderText(PageUtils.pageToJson(page));
			return null;
		}
		return "sms-wait-tosend";
	}
	/**
	 * 短信平台/待发送列表/删除
	 * @return
	 * @throws Exception
	 */
	@Override
	@Action("sms-wait-tosend-delete")
	public String delete() throws Exception {
		smsWaitTosendManager.delete(ids);
		this.addSuccessMessage(Struts2Utils.getText("basicSetting.deleteSuccess"));
		return "sms-wait-tosend";
	}

	@Override
	public String input() throws Exception {
		return null;
	}

	@Override
	public String save() throws Exception {
		return null;
	}
	@Override
	protected void prepareModel() throws Exception {
		if(id == null){
			smsWaitTosend = smsWaitTosendManager.getSmsWaitTosendById(id);
		}else {
			smsWaitTosend = new SmsWaitTosend();
			
		}
	}
	public SmsWaitTosend getModel() {
		return null;
	}
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public SmsWaitTosend getSmsWaitTosend() {
		return smsWaitTosend;
	}
	public void setSmsWaitTosend(SmsWaitTosend smsWaitTosend) {
		this.smsWaitTosend = smsWaitTosend;
	}
	public Page<SmsWaitTosend> getPage() {
		return page;
	}
	public void setPage(Page<SmsWaitTosend> page) {
		this.page = page;
	}
	public String getIds() {
		return ids;
	}
	public void setIds(String ids) {
		this.ids = ids;
	}
 
	

}
