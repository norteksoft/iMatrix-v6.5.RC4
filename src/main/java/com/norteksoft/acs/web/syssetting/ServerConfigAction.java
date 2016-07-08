package com.norteksoft.acs.web.syssetting;

import java.io.IOException;
import java.io.OutputStream;

import javax.naming.ldap.LdapContext;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.StrutsStatics;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Required;

import com.norteksoft.acs.base.utils.Ldaper;
import com.norteksoft.acs.base.web.struts2.CRUDActionSupport;
import com.norteksoft.acs.entity.sysSetting.ServerConfig;
import com.norteksoft.acs.service.syssetting.ServerConfigManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.entity.Company;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.web.struts2.Struts2Utils;
import com.opensymphony.xwork2.ActionContext;

@Namespace("/syssetting")
@ParentPackage("default")
@Results( { @Result(name = CRUDActionSupport.RELOAD, location = "/syssetting/server-config.action", type = "redirect") })
public class ServerConfigAction extends CRUDActionSupport<ServerConfig> {

	private static final long serialVersionUID = 4622265559442003480L;

	private ServerConfig serverConfig;

	private Long id;
	
	public ServerConfigManager serverConfigManager;
	
	private Boolean ldapInvocation;
	
	private Boolean rtxInvocation;
	
	private Boolean synInv;//是否启用imtrix同步集成设置
	
	private String type="ldap";//表示是ldap、imatrix、rtx、other
	
	private String oldInvocationType;//已经设置为启用单点登录的类型
	
	private String companyCode;//imatrix集成中需要同步组织结构的公司
	
	private String synType;//ldap集成时，同步组织机构时同步的类型：更新所有用户信息（synAllUser）、只更新增加的用户信息（onlySynAdd）、更新用户信息（synSelectUserInfo）
	private String synLdapUserInfo;//ldap集成时，同步组织机构时同步的用户信息：姓名(userName)、尊称(hornoric)、正职部门(mainDept)、...
	
	private static final String SUCCESS_MESSAGE_LEFT = "<font class=\"onSuccess\"><nobr>";
	private static final String ERROR_MESSAGE_LEFT = "<font class=\"onError\"><nobr>";
	private static final String MESSAGE_RIGHT = "</nobr></font>";

	@Override
	public String delete() throws Exception {
		return null;
	}

	/**
	 * 查看具体设置
	 */
	@Override
	@Action("server-config")
	public String list() throws Exception {
		serverConfig = serverConfigManager.getServerConfigByCompanyId(ContextUtils.getCompanyId());
		if(serverConfig!=null){
			id = serverConfig.getId();
		}else{
			serverConfig  = new ServerConfig();
			serverConfig.setRtxInvocation(false);
			serverConfig.setLdapInvocation(false);
			serverConfigManager.save(serverConfig);
		}
		ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("acs.loginSet"), 
				ApiFactory.getBussinessLogService().getI18nLogInfo("acs.viewLoginConfigure"),ContextUtils.getSystemId("acs"));
		return "server-config";
	}

	@Override
	protected void prepareModel() throws Exception {
		if(id==null){
			serverConfig  = new ServerConfig();
		}else{
			serverConfig = serverConfigManager.getServerConfig(id);
		}
	}

	/**
	 * 保存设置
	 */
	@Override
	@Action("server-config-save")
	public String save() throws Exception {
		serverConfig.setCompanyId(ContextUtils.getCompanyId());
		serverConfigManager.save(serverConfig);
		if(serverConfig.getLdapInvocation()){
			LdapContext context = Ldaper.getConnectionFromPool();
			if(context==null){
				serverConfig.setLdapInvocation(false);
				serverConfig.setRtxInvocation(false);
				serverConfigManager.save(serverConfig);
				addActionMessage(ERROR_MESSAGE_LEFT+Struts2Utils.getText("ldap.connect.error")+MESSAGE_RIGHT);
			}else{
				try {
					context.close();
				} catch (Exception e) { }
				addActionMessage(SUCCESS_MESSAGE_LEFT+Struts2Utils.getText("ldap.connect.success")+MESSAGE_RIGHT);
			}
		}
		ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("acs.loginSet"), 
				ApiFactory.getBussinessLogService().getI18nLogInfo("acs.loginWay"),ContextUtils.getSystemId("acs"));
		return list();
		
	}
	
	public void prepareSaveIntegration() throws Exception{
		prepareModel();
	}
	/**
	 * 保存集成参数设置
	 */
	@Action("integration-save")
	public String saveIntegration() throws Exception {
		//重新设置启用的单点登录
		serverConfigManager.resetLoginInvocation(oldInvocationType);
		serverConfigManager.save(serverConfig);
		addActionMessage(SUCCESS_MESSAGE_LEFT+Struts2Utils.getText("common.saved")+MESSAGE_RIGHT);
		if(type.equals("ldap")){
			return "security-set-ldapIntegration";
		}else if(type.equals("iMatrix")){
			return "security-set-iMatrixIntegration";
		}else if(type.equals("rtx")){
			return "security-set-rtxIntegration";
		}else if(type.equals("other")){
			return "security-set-otherIntegration";
		}
		return "security-set-ldapIntegration";
	}
	public void prepareIntegrationSetPage() throws Exception{
		getServerConfigByCompany();
	}
	@Action("security-set-integration")
	public String integrationSetPage() throws Exception{
		if(type.equals("ldap")){
			return "security-set-ldapIntegration";
		}else if(type.equals("iMatrix")){
			return "security-set-iMatrixIntegration";
		}else if(type.equals("rtx")){
			return "security-set-rtxIntegration";
		}else if(type.equals("other")){
			return "security-set-otherIntegration";
		}
		return "security-set-ldapIntegration";
	}
	
	public void prepareSysOrg() throws Exception{
		prepareModel();
	}
	/**
	 * 同步组织结构
	 */
	@Action("integration-sysOrg")
	public String sysOrg() throws Exception {
		if(type.equals("ldap")){
			String result =serverConfigManager.sysOrgLdap(serverConfig,synType,synLdapUserInfo);
			if(result.equals("fail")){
				addActionMessage(ERROR_MESSAGE_LEFT+Struts2Utils.getText("ldap.connect.error")+MESSAGE_RIGHT);//连接失败!
			}else{
				addActionMessage(SUCCESS_MESSAGE_LEFT+result+MESSAGE_RIGHT);
			}
			return "security-synLdapUsers-setting";
		}else if(type.equals("iMatrix")){
			String result =serverConfigManager.sysOrgImatrix(serverConfig);
			if(result.equals("success")){
				addActionMessage(SUCCESS_MESSAGE_LEFT+Struts2Utils.getText("acs.ldap.synchronized.success")+MESSAGE_RIGHT);//同步成功!
			}else{
				addActionMessage(ERROR_MESSAGE_LEFT+result+MESSAGE_RIGHT);
			}
			return "security-set-iMatrixIntegration";
		}
		return "security-set-ldapIntegration";
	}
	
	public void prepareShowSynUserInfo() throws Exception {
		prepareModel();
	}
	/**
	 * 可以同步的用户信息列表
	 */
	@Action("integration-showSynUserInfo")
	public String showSynUserInfo() throws Exception {
		return "security-synLdapUsers-setting";
	}
	/**
	 * 是否开启单点登录验证
	 */
	@Action("validate-loginInvocation")
	public String loginInvocation() throws Exception {
		if(type.equals("ldap")){
			if(serverConfigManager.isRtxInvocation()){
				this.renderText("rtx");
				return null;
			}else if(serverConfigManager.isOtherInvocation()){
				this.renderText("other");
				return null;
			}
		}else if(type.equals("rtx")){
			if(serverConfigManager.isLdapInvocation()){
				this.renderText("ldap");
				return null;
			}else if(serverConfigManager.isOtherInvocation()){
				this.renderText("other");
				return null;
			}
		}else if(type.equals("other")){
			if(serverConfigManager.isLdapInvocation()){
				this.renderText("ldap");
				return null;
			}else if(serverConfigManager.isRtxInvocation()){
				this.renderText("rtx");
				return null;
			}
		}
		this.renderText("success");//表示可以设置为rtx单点登录
		return null;
	}
	
	/**
	 * 远程访问时获取组织结构信息
	 * @return
	 * @throws IOException
	 */
	@Action("user-findOrg")
	public String findOrg() throws IOException{
		boolean allowable = true;
		String msg="";
		Company company= ApiFactory.getAcsService().getCompanyByCode(companyCode);
		if(company!=null){
			ServerConfig serverConfig = serverConfigManager.getServerConfigByCompanyId(company.getId());
			if(serverConfig!=null){
				allowable = serverConfig.getSysOrgAllowable();
			}
			if(allowable){
				msg=serverConfigManager.findUserInfos(company.getId());
			}else{
				msg="unallowSysOrg";//companyCode+"公司的组织结构不允许被同步!"
			}
		}else{
			msg="comanyNotFound";//companyCode+"公司不存在!"
		}
		HttpServletResponse hsr=(HttpServletResponse)ActionContext.getContext().get(StrutsStatics.HTTP_RESPONSE);
		hsr.setCharacterEncoding("utf-8");
		OutputStream os=hsr.getOutputStream();
		os.write(msg.getBytes("utf-8"));
		return null;
	}
	
	private void getServerConfigByCompany(){
		serverConfig = serverConfigManager.getServerConfigByCompanyId(ContextUtils.getCompanyId());
		if(serverConfig==null){
			serverConfig  = new ServerConfig();
		}
	}


	public ServerConfig getModel() {

		return serverConfig;
	}

	
	
	@Required
	public void setServerConfigManager(ServerConfigManager serverConfigManager) {
		this.serverConfigManager = serverConfigManager;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Boolean getLdapInvocation() {
		return ldapInvocation;
	}

	public Boolean getRtxInvocation() {
		return rtxInvocation;
	}

	public ServerConfig getServerConfig() {
		return serverConfig;
	}

	public void setServerConfig(ServerConfig serverConfig) {
		this.serverConfig = serverConfig;
	}
	
	public Boolean getSynInv() {
		return synInv;
	}

	public void setSynInv(Boolean synInv) {
		this.synInv = synInv;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setOldInvocationType(String oldInvocationType) {
		this.oldInvocationType = oldInvocationType;
	}

	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}

	public void setSynType(String synType) {
		this.synType = synType;
	}

	public void setSynLdapUserInfo(String synLdapUserInfo) {
		this.synLdapUserInfo = synLdapUserInfo;
	}

	
}
