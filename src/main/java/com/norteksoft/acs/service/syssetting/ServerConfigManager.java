package com.norteksoft.acs.service.syssetting;

import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.naming.ldap.LdapContext;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.acs.base.orm.hibernate.SimpleHibernateTemplate;
import com.norteksoft.acs.base.utils.Ldaper;
import com.norteksoft.acs.entity.organization.User;
import com.norteksoft.acs.entity.sysSetting.ServerConfig;
import com.norteksoft.acs.ldap.LdapService;
import com.norteksoft.acs.service.organization.AsynOrgManager;
import com.norteksoft.acs.service.organization.CompanyManager;
import com.norteksoft.acs.service.organization.SynchroOrganizationService;
import com.norteksoft.acs.service.organization.UserInfoManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.ParameterUtils;
import com.norteksoft.product.util.ThreadParameters;
import com.norteksoft.product.web.struts2.Struts2Utils;

/**
 *系统参数设置接口
 * 
 * @author 陈成虎 2009-3-2上午11:52:40
 */
@Service
@Transactional
public class ServerConfigManager extends SynchroOrganizationService{

	private SimpleHibernateTemplate<ServerConfig, Long> serverConfigDao;
	@Autowired
	private AsynOrgManager asynOrgManager;
	@Autowired
	private UserInfoManager userInfoManager;
	@Autowired
	private CompanyManager companyManager;

	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		serverConfigDao = new SimpleHibernateTemplate<ServerConfig, Long>(
				sessionFactory, ServerConfig.class);
	}

	/**
	 * 保存
	 * @param entity
	 */
	public void save(ServerConfig entity) {
		serverConfigDao.save(entity);
	}

	/**
	 * 取实体
	 * @param id
	 * @return
	 */
	@Transactional(readOnly = true)
	public ServerConfig getServerConfig(Long id) {
		return serverConfigDao.get(id);
	}

	/**
	 * 取公司的服务器配置方式
	 * @param companyId
	 * @return
	 */
	public ServerConfig getServerConfigByCompanyId(Long companyId){
		return (ServerConfig)serverConfigDao.findUnique("from ServerConfig s where s.companyId=?", companyId) ;
	}
	
	public String sysOrgLdap(ServerConfig serverConfig,String synType,String synLdapUserInfo){
		save(serverConfig);
		LdapService ldap = Ldaper.getLdapService();
		LdapContext context = ldap.getLdapContext();
		if(context==null){
			return "fail";
		}else{
			ldap.closeLdap(context);
			String message = "";
			message = userInfoManager.synchronize(ldap,synType,synLdapUserInfo);
			return message;
		}
	}
	
	public String sysOrgImatrix(ServerConfig serverConfig) throws Exception{
		save(serverConfig);
		/*cao#cao#cao@cao.cao#true#1#202cb962ac59075b964b07152d234b70#_0;
		test.auditAdmin#auditAdmin#null#null#1#1aa7f5570a8eaf369962fce2f5c7dd47#_0;
		test.securityAdmin#securityAdmin#null#null#1#bb5a71dd3248c038226ef792e4f1a429#_0;
		test.systemAdmin#systemAdmin#null#null#1#b1de53c1c9665b5a7b7b8a34855d642e#_0;
		开发部_-\liudongxia#刘冬霞#ldx@n.com#false#1#202cb962ac59075b964b07152d234b70#_1;
		开发部_-\ggg#ggg#ggg@ggg.ggg#false#1#ba248c985ace94863880921d8900c53f#_2;
		测试部_-\ggg#ggg#ggg@ggg.ggg#false#1#ba248c985ace94863880921d8900c53f#_1;
		测试部_-\部门1_-\ggg#ggg#ggg@ggg.ggg#false#1#ba248c985ace94863880921d8900c53f#_2;
		部门2_-\ddd#ddd#ddd@ddd.ddd#false#1#77963b7a931377ad4ab5ad6a9cd718aa#_1;
		部门2_-\分支2_+\fff#fff#fff@fff.fff#false#1#343d9040a671c45832ee5381860e2996#_0;
		部门2_-\分支2_+\部门3_-\vvv#vvv#vvv@vvv.vvv#false#1#4786f3282f04de5b5c7317c490c6d922#_1;
		(用户信息有”#”隔开【loginName,name,email,sex,password】)
		用户_后面（"0"代表在无部门下,"1"正职部门,"2"兼职部门）
		&
		开发部_-;
		子公司1_+;
		测试部_-\部门1_-;
		测试部_-;
		部门2_-\分支2_+\部门3_-;
		部门2_-\分支2_+;
		部门2_-;*/
		String imatrixUrl = serverConfig.getImatrixUrl();
		if(StringUtils.isNotEmpty(imatrixUrl)){
			if(imatrixUrl.lastIndexOf("/")==imatrixUrl.length()-1){//以“/”结束
				imatrixUrl = imatrixUrl.substring(0,imatrixUrl.length()-1);
			}
			URL url = new URL(imatrixUrl+"/acs/syssetting/user-findOrg.htm?companyCode="+serverConfig.getCompanyCode());
			 
			String remoteMsg=IOUtils.toString(url,"utf-8");
			if("unallowSysOrg".equals(remoteMsg)){
				return serverConfig.getCompanyCode()+Struts2Utils.getText("acs.theOrganizationStructure");
			}else if("comanyNotFound".equals(remoteMsg)){
				return serverConfig.getCompanyCode()+Struts2Utils.getText("acs.theCompanyDoes");
			}
			
			SynchroOrganizationService service = null;
			try{
				service = (SynchroOrganizationService)ContextUtils.getBean("synOrgiMatrixHandler");
			}catch (Exception e) {
				service = null;
			}
			
			if(service==null){
				return synchroOrganization(remoteMsg);
			}else{
				String result = service.synchroOrganization(remoteMsg);
				if(StringUtils.isNotEmpty(result)){
					return  result;
				}
				//如果result为空，表示是使用默认实现，用户没有重写同步方法
				return synchroOrganization(remoteMsg);
			}
			
		}else{
			return Struts2Utils.getText("acs.pleaseFillIn");
		}
		 
	}
	
	public String getOrgStr(Long companyId){
		StringBuilder usersb=new StringBuilder();
		StringBuilder deptsb=new StringBuilder();
		ThreadParameters parameters = new ThreadParameters(companyId);
		ParameterUtils.setParameters(parameters);
		List<com.norteksoft.product.api.entity.Department> departments=ApiFactory.getAcsService().getDepartments();//获取顶级部门
		addNoDepartmentUser(usersb);
		for(com.norteksoft.product.api.entity.Department dept:departments){
			addPath("",deptsb,usersb,dept);
		}
		addDelUser(usersb);
		return usersb.append("&").append(deptsb).toString();
	}
	private void addNoDepartmentUser(StringBuilder usersb) {
		for(User user:ApiFactory.getAcsService().getEntityUsersWithoutDepartment()){
			if(!isDefaultAdmin(user)){//不是系统默认的三员，则可以同步
				usersb.append(getLoginNameMsg(user)+"_0;");
			}
		}
	}
	private void addDelUser(StringBuilder usersb){
		for(User user:asynOrgManager.getDeletedUser()){
			if(!isDefaultAdmin(user)){//不是系统默认的三员，则可以同步
				String delPath = "";
				Long subCompanyId = user.getSubCompanyId();
				if(subCompanyId!=null){
					delPath = user.getSubCompanyName();
				}
				usersb.append(delPath +"\\"+getLoginNameMsg(user)+"_3;");//已删除用户
			}
		}
	}
	private String getLoginNameMsg(User user){
		return user.getLoginName()+"#"+user.getName()+"#"+user.getEmail()+"#"+user.getSex()+"#"+user.getWeight()+"#"+user.getPassword()+"#";
	}
	//拼接人
	private void addPath(String p,StringBuilder deptsb,StringBuilder usersb, com.norteksoft.product.api.entity.Department dept) {
		String path=(p.equals("")?"":p+"\\")+dept.getName()+getDepartmentMark(dept);
		if(dept.getBranch()){
			for(User user:ApiFactory.getAcsService().getEntityUsersWithoutBranch(dept.getId())){
				if(!isDefaultAdmin(user)){//不是系统默认的三员，则可以同步
					usersb.append(path+"\\"+getLoginNameMsg(user)+"_0;");//无部门
					
				}
			}
		}else{
			for(User user:ApiFactory.getAcsService().getEntityUsersByDepartment(dept.getId())){
				if(!isDefaultAdmin(user)){//不是系统默认的三员，则可以同步
					if(dept.getId().equals(user.getMainDepartmentId())){
						usersb.append(path+"\\"+getLoginNameMsg(user)+"_1;");//正职部门
					}else{
						usersb.append(path+"\\"+getLoginNameMsg(user)+"_2;");//兼职部门
					}
				}
			}
		}
		List<com.norteksoft.product.api.entity.Department> depts=ApiFactory.getAcsService().getSubDepartmentList(dept.getId());
		if(depts!=null&&depts.size()>0){
			for(com.norteksoft.product.api.entity.Department d:depts){
				addPath(path,deptsb,usersb,d);
			}
		}
		deptsb.append(path+";");
	}
	/**
	 * 是否是系统默认的三员
	 * @param user
	 * @return
	 */
	private boolean isDefaultAdmin(User user){
		if(!user.getLoginName().contains(".systemAdmin")&&!user.getLoginName().contains(".securityAdmin")&&!user.getLoginName().contains(".auditAdmin")){
			return false;
		}
		return true;
	}

	private String getDepartmentMark(com.norteksoft.product.api.entity.Department dept) {
		if(dept.getBranch()){
			return "_+";//表示分支机构
		}
		return "_-";//部门
	}
	
	public boolean isRtxInvocation(){
		List<ServerConfig> serverConfigs = serverConfigDao.find("from ServerConfig s where s.companyId=? and s.rtxInvocation=?", ContextUtils.getCompanyId(),true) ;
		if(serverConfigs.size()>0){
			return true;
		}else{
			return false;
		}
	}
	public boolean isLdapInvocation(){
		List<ServerConfig> serverConfigs = serverConfigDao.find("from ServerConfig s where s.companyId=? and s.ldapInvocation=?", ContextUtils.getCompanyId(),true) ;
		if(serverConfigs.size()>0){
			return true;
		}else{
			return false;
		}
	}
	public boolean isOtherInvocation(){
		List<ServerConfig> serverConfigs = serverConfigDao.find("from ServerConfig s where s.companyId=? and s.extern=?", ContextUtils.getCompanyId(),true) ;
		if(serverConfigs.size()>0){
			return true;
		}else{
			return false;
		}
	}
	
	public void resetLoginInvocation(String oldInvocationType){
		String hql = "";
		if("ldap".equals(oldInvocationType)){
			hql = "update ServerConfig set ldapInvocation=? where companyId=?";
		}else if("rtx".equals(oldInvocationType)){
			hql = "update ServerConfig set rtxInvocation=? where companyId=?";
		}else if("other".equals(oldInvocationType)){
			hql = "update ServerConfig set extern=? where companyId=?";
		}
		if(StringUtils.isNotEmpty(hql)){
			serverConfigDao.createQuery(hql, false,ContextUtils.getCompanyId()).executeUpdate();
		}
	}
	
	public String findUserInfos(Long companyId){
		SynchroOrganizationService service = null;
		try{
			service = (SynchroOrganizationService)ContextUtils.getBean("synOrgiMatrixHandler");
		}catch (Exception e) {
			service = null;
		}
		if(service==null){
			return findOrganization(companyId);
		}else{
			String result = service.findOrganization(companyId);
			if(StringUtils.isNotEmpty(result)){
				return  result;
			}
			//如果result为空，表示是使用默认实现，用户没有重写查找用户信息的方法
			return findOrganization(companyId);
		}
	}

	public String findOrganization(Long companyId) {
		return getOrgStr(companyId);
	}

	public String synchroOrganization(String remoteMsg) {
		String localMsg=getOrgStr(ContextUtils.getCompanyId());
		
		Set<String> remoteUserSet=new HashSet<String>();
		Set<String> remoteDeptSet=new HashSet<String>();
		Set<String> localUserSet=new HashSet<String>();
		Set<String> localDeptSet=new HashSet<String>();
		String[] remoteArr=remoteMsg.split("&");
		for(String username:remoteArr[0].split(";")){
			remoteUserSet.add(username);
		}
		if(remoteArr.length>1){
			for(String departmentname:remoteArr[1].split(";")){
				remoteDeptSet.add(departmentname);
			}
		}
		String[] localArr=localMsg.split("&");
		for(String username:localArr[0].split(";")){
			localUserSet.add(username);
		}
		if(localArr.length>1){
			for(String departmentname:localArr[1].split(";")){
				localDeptSet.add(departmentname);
			}
		}
		return asynOrgManager.synOrg(remoteUserSet,remoteDeptSet,localUserSet,localDeptSet);
	}

}
