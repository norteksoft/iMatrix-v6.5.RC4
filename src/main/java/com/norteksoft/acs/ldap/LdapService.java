package com.norteksoft.acs.ldap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.ldap.LdapContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class LdapService {

	protected static Log logger = LogFactory.getLog(LdapService.class);
	private String adminName;
	private String password;
	private String ldapUrl;
	private boolean ssl = false;
	private String ldapBaseDomain;
	
	public LdapService() {
		super();
	}
	
	public LdapService(String adminName, String password, String ldapUrl, String ldapBaseDomain){
		this.adminName = adminName;
		this.password = password;
		this.ldapUrl = ldapUrl;
		this.ldapBaseDomain = ldapBaseDomain;
	}
	
	public LdapService(String adminName, String password, String ldapUrl, boolean isSsl, String ldapBaseDomain){
		this(adminName, password, ldapUrl, ldapBaseDomain);
		this.password = password;
		this.ldapUrl = ldapUrl;
	}
	
	public abstract List<LdapUser> getAllUser();
	public abstract LdapContext getLdapContext();
	
	public boolean closeLdap(LdapContext ldatCtx) {
		try {
			logger.debug(" start close ldap context ... ");
			ldatCtx.close();
			logger.debug(" close ldap context success. ");
			return true;
		} catch (NamingException e) {
			logger.error(" close ldap context failed. ", e);
			return false;
		}
	}
	
	public String[] getReturningAttributes(Map<String,String> returnedMap, List<String> filtrationAttributes){
		Set<String> returningAttributes = new HashSet<String>();
		returningAttributes.addAll(returnedMap.keySet());
		if(filtrationAttributes != null && filtrationAttributes.size() > 0){
			for(String attribute:filtrationAttributes){
				returningAttributes.add(attribute);
			}
		}
		return returningAttributes.toArray(new String[returningAttributes.size()]);
	}

	public void setAdminName(String adminName) {
		this.adminName = adminName;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setLdapUrl(String ldapUrl) {
		this.ldapUrl = ldapUrl;
	}

	public void setSsl(boolean isSsl) {
		this.ssl = isSsl;
	}
	
	public String getAttributeValue(Attributes attrs, String key) throws NamingException{
		Attribute attr = attrs.get(key);
		if(attr != null) return  getString(attr.get());
		else return null;
	}
	
	public String getAdminName() {
		return adminName;
	}
	
	public String getPassword() {
		return password;
	}
	public String getLdapUrl() {
		return ldapUrl;
	}
	
	public String getLdapBaseDomain() {
		return ldapBaseDomain;
	}

	public void setLdapBaseDomain(String ldapBaseDomain) {
		this.ldapBaseDomain = ldapBaseDomain;
	}

	public static String getString(Object obj){
		if(obj == null) return null;
		else return obj.toString();
	}
	
	public static class LdapUser{
		private String userDn;
		private String loginName;
		private String name;
		private String email;
		private String telephone;
		private String departmentName;
		private List<String> departments = new ArrayList<String>();
		
		public String getUserDn() {
			return userDn;
		}
		public void setUserDn(String userDn) {
			this.userDn = userDn;
		}
		public String getLoginName() {
			return loginName;
		}
		public void setLoginName(String loginName) {
			this.loginName = loginName;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getEmail() {
			return email;
		}
		public void setEmail(String email) {
			this.email = email;
		}
		public String getTelephone() {
			return telephone;
		}
		public void setTelephone(String telephone) {
			this.telephone = telephone;
		}
		public String getDepartmentName() {
			return departmentName;
		}
		public void setDepartmentName(String departmentName) {
			this.departmentName = departmentName;
		}
		@Override
		public String toString() {
			return "User [name=" + name + 
				", username=" + loginName +
				", email=" + email +  
				", telephone=" + telephone + 
				"] depts: "+getDepartment()+"\n";
		}
		public List<String> getDepartment(){
			String[] dn = userDn.split(",");
			for(int i=dn.length-1; i>0; i--){
				if(dn[i].startsWith("OU=")||dn[i].startsWith("ou=")){
					departments.add(dn[i].replace("OU=", "").replace("ou=", ""));
				}
			}
			return departments;
		}
	}
}
