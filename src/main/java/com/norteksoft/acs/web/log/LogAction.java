package com.norteksoft.acs.web.log;

import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.norteksoft.acs.base.web.struts2.CRUDActionSupport;
import com.norteksoft.acs.entity.authorization.BusinessSystem;
import com.norteksoft.acs.entity.log.Log;
import com.norteksoft.acs.entity.log.LoginLog;
import com.norteksoft.acs.service.authorization.BusinessSystemManager;
import com.norteksoft.acs.service.log.LogManager;
import com.norteksoft.acs.service.organization.DepartmentManager;
import com.norteksoft.mms.base.MmsUtil;
import com.norteksoft.mms.base.utils.view.ExportData;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.ExcelExportEnum;
import com.norteksoft.product.util.ExcelExporter;
import com.norteksoft.product.util.PageUtils;

@ParentPackage("default")
@Results( { @Result(name = "reload", location = "log", type="redirectAction") })
public class LogAction extends CRUDActionSupport<Log>{

	private static final long serialVersionUID = -6636275446940878497L;
	private LogManager logManager;
	private BusinessSystemManager businessSystemManager;
	private Page<Log> page = new Page<Log>(0,true);
	private Page<LoginLog> userLoginPage = new Page<LoginLog>(0,true);
	private Log entity;
	private Long id;
	private String name;
	private Long businessSystemId;
	private Long searchsysId;
	private Long companyId;
	private Long sysId;
	private LoginLog loginUserLog;
	private String systemTree;
	private String loginLogIds;
	private String syIds;
	private String dsysId;
	private String deleteAll;
	private String deleteAllSysLog;
	private Boolean containBranches;//集团公司中是否含有分支机构：true含有分支机构，false不含有分支机构
	
	private static final String SUCCESS_MESSAGE_LEFT = "<font class=\"onSuccess\"><nobr>";
	private static final String MESSAGE_RIGHT = "</nobr></font>";
	private static final String ERROR_MESSAGE_LEFT = "<font class=\"onError\"><nobr>";
	protected void addErrorMessage(String message){
		this.addActionMessage(ERROR_MESSAGE_LEFT+message+MESSAGE_RIGHT);
	}
	protected void addSuccessMessage(String message){
		this.addActionMessage(SUCCESS_MESSAGE_LEFT+message+MESSAGE_RIGHT);
	}
	@Autowired
	private MmsUtil mmsUtil;
	@Autowired
	private DepartmentManager departmentManager;
	
	@Override
	public String delete() throws Exception {
		logManager.deleteLog(id);
		return RELOAD;
	}

	/**
	 *  查询所有日志
	 */
	@Override
	public String list() throws Exception {
		List<BusinessSystem> businessSystems = businessSystemManager.getAllBusiness();
		if(businessSystems.size() > 0){
			if(sysId == null){
				sysId = businessSystems.get(0).getId();
				if(dsysId!=null&&!dsysId.equals("")){
					sysId=Long.parseLong(dsysId);	
				}
			}
		}
		businessSystemId=sysId;
		return SUCCESS;
	}

	@Action("log-data")
	public String listData() throws Exception{
		containBranches=departmentManager.containBranches();
		if(page.getPageSize() > 1){
			page = logManager.getAllLog(page,sysId);
			ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("acs.systemLog"), 
					ApiFactory.getBussinessLogService().getI18nLogInfo("acs.viewSystemLogList"),ContextUtils.getSystemId("acs"));
			renderText(PageUtils.pageToJson(page));
			return null;
		}
		return "log-list";
	}
	
	/**
	 * 日志导出
	 */
	@Action("log-export")
	public String export() throws Exception{
		Page<Log> dynamicPage = new Page<Log>(100000);
		dynamicPage=logManager.search(dynamicPage,sysId);
		ExportData exportData=null;
		try {
			if(departmentManager.containBranches()){
				exportData=ApiFactory.getMmsService().getExportData(dynamicPage, "ACS_LOGS_SUB_COMPANY");
				this.renderText(ExcelExporter.export(exportData,"ACS_LOGS_SUB_COMPANY",ExcelExportEnum.EXCEL2007));
			}else{
				exportData=ApiFactory.getMmsService().getExportData(dynamicPage, "ACS_LOGS");
				this.renderText(ExcelExporter.export(exportData,"ACS_LOGS",ExcelExportEnum.EXCEL2007));
			}
		} catch (Exception e) {
			LOG.error("导出错误", e);
			renderText("导出错误");
		}
		ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("acs.systemLog"), 
				ApiFactory.getBussinessLogService().getI18nLogInfo("acs.exportSystemLog"),ContextUtils.getSystemId("acs"));
		return null;
	}

	@Override
	protected void prepareModel() throws Exception {
		if(id != null){
			entity = logManager.getLog(id);
		}else{
			entity = new Log();
		}
	}
	

	
	/**
	 * 删除系统日志
	 */
	@Action("log-deleteSysLoginLog")
	public String deleteSysLoginLog() throws Exception {
		String s="";
		String result="";
		if("yes".equals(deleteAllSysLog)){
			s=syIds;
			result=logManager.deleteAllSysLog(syIds);
		}else{
			String ss=syIds.substring(0,syIds.indexOf("="));
			s=syIds.substring(syIds.indexOf("=")+1,syIds.length());
			result=logManager.deleteSysLogs(ss);
		}
		dsysId=s;
		if(page.getPageSize() > 1){
			page = logManager.getAllLog(page,sysId);
			renderText(PageUtils.pageToJson(page));
			return null;
		}
		ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("acs.systemLog"), 
				ApiFactory.getBussinessLogService().getI18nLogInfo("acs.deleteSystemLog"),ContextUtils.getSystemId("acs"));
		this.renderText(result);
		return null;
	}

	/**
	 * 保存方法,是不允许用户自己插入日志记录的
	 */
	@Override
	public String save() throws Exception {
		logManager.saveLog(entity);
		return RELOAD;
	}

	@Override
	public String input() throws Exception {
		return INPUT;
	}
	
	
	/**
	 * 查看登陆日志
	 * @return
	 */
	@Action("log-lookUserLoginLog")
	public String lookUserLoginLog()throws Exception{
		containBranches=departmentManager.containBranches();
		if(userLoginPage.getPageSize() >1){
			userLoginPage = logManager.getloginUserLogAllByCompanyId(userLoginPage, getCompanyId());
			this.renderText(PageUtils.pageToJson(userLoginPage));
			ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("acs.systemLog"), 
					ApiFactory.getBussinessLogService().getI18nLogInfo("acs.viewLoginLog"),ContextUtils.getSystemId("acs"));
			return null;
		}
		return "log-user-login";
	}
	
	/**
	 * 导出登陆日志
	 */
	@Action("log-exportLoginLog")
	public String exportLoginLog() throws Exception{
		Page<LoginLog> dynamicPage = new Page<LoginLog>(100000);
		dynamicPage=logManager.searchLoginLog(dynamicPage);
		ExportData exportData=null;
		containBranches=departmentManager.containBranches();
		try {
			if(departmentManager.containBranches()){
				exportData=ApiFactory.getMmsService().getExportData(dynamicPage, "ACS_LOG_LOGIN_SUB_COMPANY");
				this.renderText(ExcelExporter.export(exportData,"ACS_LOG_LOGIN_SUB_COMPANY",ExcelExportEnum.EXCEL2007));
			}else{
				exportData=ApiFactory.getMmsService().getExportData(dynamicPage, "ACS_LOG_LOGIN");
				this.renderText(ExcelExporter.export(exportData,"ACS_LOG_LOGIN",ExcelExportEnum.EXCEL2007));
			}
		} catch (Exception e) {
			LOG.error("导出错误", e);
			renderText("导出错误");
		}
		ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("acs.systemLog"), 
				ApiFactory.getBussinessLogService().getI18nLogInfo("acs.exportLoginLog"),ContextUtils.getSystemId("acs"));
		return null;
	}
	
	
	/**
	 * 删除登陆日志
	 * @return
	 */
	@Action("log-deleteUserLoginLog")
	public String deleteUserLoginLog()throws Exception{
		String result="";
		if("yes".equals(deleteAll)){
			result=logManager.deleteAllLoginUserLog();
		}else{
			result=logManager.deleteloginUserLogAllByCompanyId(loginLogIds, getCompanyId());	
		}
		ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("acs.systemLog"), 
				ApiFactory.getBussinessLogService().getI18nLogInfo("acs.deleteLoginLog"),ContextUtils.getSystemId("acs"));
		this.renderText(result);
		return null;
	}
	
	
	/**
	 * 搜索登陆日志
	 * @return
	 */
	public String searchUserLoginLog()throws Exception{
		userLoginPage = logManager.getListByLoginUserLog(userLoginPage, loginUserLog, getCompanyId());
		return "user-login";
	}
	
	public Page<Log> getPage() {
		return page;
	}

	public void setPage(Page<Log> page) {
		this.page = page;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Required
	public void setLogManager(LogManager logManager) {
		this.logManager = logManager;
	}

	public BusinessSystemManager getBusinessSystemManager() {
		return businessSystemManager;
	}
	@Required
	public void setBusinessSystemManager(BusinessSystemManager businessSystemManager) {
		this.businessSystemManager = businessSystemManager;
	}


	public Log getModel() {
		return entity;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getCompanyId() {
		if(companyId==null){
			companyId=ContextUtils.getCompanyId();
		}
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public Long getSysId() {
		return sysId;
	}

	public void setSysId(Long sysId) {
		this.sysId = sysId;
	}

	public Page<LoginLog> getUserLoginPage() {
		return userLoginPage;
	}

	public void setUserLoginPage(Page<LoginLog> userLoginPage) {
		this.userLoginPage = userLoginPage;
	}

	public LoginLog getLoginUserLog() {
		return loginUserLog;
	}

	public void setLoginUserLog(LoginLog loginUserLog) {
		this.loginUserLog = loginUserLog;
	}

	public String getSystemTree() {
		return systemTree;
	}

	public void setSystemTree(String systemTree) {
		this.systemTree = systemTree;
	}

	public Long getBusinessSystemId() {
		return businessSystemId;
	}

	public void setBusinessSystemId(Long businessSystemId) {
		this.businessSystemId = businessSystemId;
	}

	public Long getSearchsysId() {
		return searchsysId;
	}

	public void setSearchsysId(Long searchsysId) {
		this.searchsysId = searchsysId;
	}

	public String getLoginLogIds() {
		return loginLogIds;
	}

	public void setLoginLogIds(String loginLogIds) {
		this.loginLogIds = loginLogIds;
	}

	public String getSyIds() {
		return syIds;
	}

	public void setSyIds(String syIds) {
		this.syIds = syIds;
	}

	public String getDsysId() {
		return dsysId;
	}

	public void setDsysId(String dsysId) {
		this.dsysId = dsysId;
	}

	public String getDeleteAll() {
		return deleteAll;
	}

	public void setDeleteAll(String deleteAll) {
		this.deleteAll = deleteAll;
	}

	public String getDeleteAllSysLog() {
		return deleteAllSysLog;
	}

	public void setDeleteAllSysLog(String deleteAllSysLog) {
		this.deleteAllSysLog = deleteAllSysLog;
	}
	
	public Boolean getContainBranches() {
		return containBranches;
	}
}
