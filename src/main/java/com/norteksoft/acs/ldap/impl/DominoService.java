package com.norteksoft.acs.ldap.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;

import com.norteksoft.acs.base.utils.Ldaper;
import com.norteksoft.acs.ldap.LdapService;
import com.norteksoft.acs.ldap.LdapUserFilter;
import com.norteksoft.product.util.PropUtils;

public class DominoService extends LdapService{

	private static Map<String,String> returnedMap = new HashMap<String,String>(){{ 
		put("uid","loginName"); // 登录名
		put("cn","name");   // 用户名
		put("mail","email");   // email
		put("objectClass","");  // 
	}};
	
	public DominoService() {
		super();
	}

	public DominoService(String adminName, String password, String ldapUrl, String ldapBaseDomain) {
		super(adminName, password, ldapUrl, ldapBaseDomain);
	}
	
	public static void main(String[] args) {
		DominoService ad = new DominoService("cn=admin", "123456", "ldap://192.168.1.238:389", "dc=example,dc=com");
		List<LdapUser> users = ad.getAllUser();
		System.out.println(users);
	}

	@Override
	public List<LdapUser> getAllUser() {
		LdapContext ldatCtx = Ldaper.getConnectionFromPool();
		SearchControls searchCtls = new SearchControls(); // Create the search controls
		searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE); // Specify the search scope
		String searchBase = getLdapBaseDomain(); // Specify the Base for the search//搜索域节点
		
		LdapUserFilter ldapUserFilter = new LdapUserFilterImpl();
		try {
			String className = PropUtils.getProp("ldap.user.filter.className");
			if(StringUtils.isNotEmpty(className)){
				Class<?> classObject = Class.forName(className);
				ldapUserFilter = (LdapUserFilter)classObject.newInstance();
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
			ldapUserFilter = new LdapUserFilterImpl();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			ldapUserFilter = new LdapUserFilterImpl();
		}
		try {
			List<String> filtrationAttributeList = ldapUserFilter.filtrationAttributes();
			String[] returningAttributes = getReturningAttributes(returnedMap,filtrationAttributeList);
			searchCtls.setReturningAttributes(returningAttributes); // 设置返回属性集
			
			List<LdapUser> users = new ArrayList<LdapUser>();
			search(ldatCtx, searchBase, searchCtls, users, returningAttributes, ldapUserFilter);
			return users;
		} catch (NamingException e) {
			logger.error(" get all user error. ", e);
			e.printStackTrace();
			return null;
		} catch (IllegalAccessException e) {
			logger.error(" get all user error. ", e);
			e.printStackTrace();
			return null;
		} catch (InvocationTargetException e) {
			logger.error(" get all user error. ", e);
			e.printStackTrace();
			return null;
		} finally{
			closeLdap(ldatCtx);
		}
	}
	
	public void search(LdapContext ldatCtx, String searchBase, SearchControls searchCtls, List<LdapUser> users, String[] returningAttributes, LdapUserFilter ldapUserFilter) throws NamingException, IllegalAccessException, InvocationTargetException{
		NamingEnumeration<SearchResult> answer = ldatCtx.search(searchBase, null);
		LdapUser user = null;
		
		while (answer.hasMoreElements()) {
			Map<String, String> filtrationItem = new HashMap<String, String>();
			SearchResult sr = answer.next();
			Attributes attrs = sr.getAttributes();
			if(attrs.get("objectClass").contains("dominoPerson")){ // person
				user = new LdapUser();
				user.setUserDn(sr.getNameInNamespace());
				for(String attribute:returningAttributes){
					if("objectClass".equals(attribute))continue;
					String ldapUserAttribute = returnedMap.get(attribute);
					String ldapAttributeValue = getAttributeValue(attrs, attribute);
					if(ldapUserAttribute != null){
						BeanUtils.setProperty(user,ldapUserAttribute,ldapAttributeValue);
					}
					filtrationItem.put(attribute,ldapAttributeValue);
				}
				if(user.getLoginName() == null) user.setLoginName(user.getName());
				String department = getAttributeValue(attrs, "department");
				if(department != null){
					user.setDepartmentName(department);
					filtrationItem.put("department",department);
				}
				String mobile = getAttributeValue(attrs, "mobile");
				if(mobile != null){
					user.setTelephone(mobile);//移动电话
					filtrationItem.put("mobile",mobile);
				}
				if(ldapUserFilter.isPersonnel(filtrationItem)){
					users.add(user);
				}
			}else if(attrs.get("objectClass").contains("dominoOrganizationalUnit")){ // organizationalUnit
				search(ldatCtx, sr.getNameInNamespace(), searchCtls, users, returningAttributes, ldapUserFilter);
			}
		}
	}
	
	@Override
	public LdapContext getLdapContext() {
		Properties env = new Properties();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, getLdapUrl());
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_PRINCIPAL, getAdminName());
		env.put(Context.SECURITY_CREDENTIALS, getPassword());
		env.put("com.sun.jndi.ldap.connect.pool", "true");
		env.put("java.naming.referral", "follow");
		try {
			return new InitialLdapContext(env, null);
		} catch (NamingException e) {
			return null;
		}
	}
	
}
