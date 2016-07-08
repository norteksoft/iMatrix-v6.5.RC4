package com.norteksoft.bs.sms.web;


import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;

import com.norteksoft.bs.sms.entity.SmsLog;
import com.norteksoft.bs.sms.service.SmsLogManager;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.PageUtils;
import com.norteksoft.product.web.struts2.CrudActionSupport;
/**
 *短信平台/短信日志/发送日志以及接收日志
 * @author lenove1
 *
 */
@Namespace("/sms")
@ParentPackage("default")
@Results( { @Result(name = CrudActionSupport.RELOAD, location = "sms-log", type = "redirectAction") })
public class SmsLogAction extends CrudActionSupport<SmsLog> {

	private static final long serialVersionUID = 1L;
	private SmsLog smsLog;
	private Long id;
	private Page<SmsLog> page = new Page<SmsLog>(0,true);
	
	private String logType;
	
	@Autowired
	private SmsLogManager smsLogManager;
	
	/**
	 * 短信平台/短信日志/列表
	 * @return
	 */
	@Override
	@Action("sms-log")
	public String list() throws Exception {
		if(page.getPageSize()>1){
			smsLogManager.getAllSmsLog(page,logType);
			this.renderText(PageUtils.pageToJson(page));
			return null;
		}
		return "sms-log";
	}
	@Override
	public String delete() throws Exception {
		return null;
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
		if(id == null ){
			smsLog = smsLogManager.getSmsLogById(id);
		}else {
			smsLog = new SmsLog();
		}
	}
	
	
	public SmsLog getModel() {
		return smsLog;
	}
	public SmsLog getSmsLog() {
		return smsLog;
	}
	public void setSmsLog(SmsLog smsLog) {
		this.smsLog = smsLog;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getLogType() {
		return logType;
	}
	public void setLogType(String logType) {
		this.logType = logType;
	}
	public Page<SmsLog> getPage() {
		return page;
	}
	public void setPage(Page<SmsLog> page) {
		this.page = page;
	}


 
	

}
