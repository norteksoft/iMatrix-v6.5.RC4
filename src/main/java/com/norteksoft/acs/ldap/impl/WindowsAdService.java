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
import javax.naming.directory.Attribute;
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

public class WindowsAdService extends LdapService{
	
	private static Map<String,String> returnedMap = new HashMap<String,String>(){{ 
		put("sAMAccountName","loginName"); // 登录名
		put("name","name");   // 用户名
		put("mail","email");   // email
		put("telephoneNumber","telephone"); // 电话
	}};
	
	public WindowsAdService() {
		super();
	}

	public WindowsAdService(String adminName, String password, String ldapUrl, String ldapBaseDomain) {
		super(adminName, password, ldapUrl, ldapBaseDomain);
	}
	
	public List<LdapUser> getAllUser(){
		LdapContext ldatCtx = Ldaper.getConnectionFromPool();
		SearchControls searchCtls = new SearchControls(); // Create the search controls
		searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE); // Specify the search scope
		String searchFilter = "objectClass=User"; // specify the LDAP search filter
		searchFilter="objectClass=Group";
		searchFilter="objectClass=Person";
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
			// Search for objects using the filter
			NamingEnumeration<SearchResult> answer = ldatCtx.search(searchBase, searchFilter, searchCtls);
			List<LdapUser> users = new ArrayList<LdapUser>();
			LdapUser user = null;
			while (answer.hasMoreElements()) {
				SearchResult sr = answer.next();
				Attributes attrs = sr.getAttributes();
				user = new LdapUser();
				user.setUserDn(sr.getName());
				Map<String, String> filtrationItem = new HashMap<String, String>();
				if (attrs != null) {
					NamingEnumeration<? extends Attribute> ae = attrs.getAll();
					while(ae.hasMore()){
						Attribute attr = (Attribute) ae.next();
						NamingEnumeration<?> e = attr.getAll();
						if(e.hasMore()){
							String ldapUserAttribute = returnedMap.get(attr.getID());
							String ldapAttributeValue = getString(e.next());
							if(ldapUserAttribute != null){
								BeanUtils.setProperty(user,ldapUserAttribute,ldapAttributeValue);
							}
							filtrationItem.put(attr.getID(),ldapAttributeValue);
						}
					}
				}
				if(ldapUserFilter.isPersonnel(filtrationItem)){
					users.add(user);
				}
			}
			return users;
		} catch (NamingException e) {
			logger.error(" get all user error. ", e);
			e.printStackTrace();
			return null;
		} catch (SecurityException e) {
			logger.error(" get all user error. ", e);
			e.printStackTrace();
			return null;
		}  catch (IllegalAccessException e) {
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
	
	public static void main(String[] args) {
		WindowsAdService ad = new WindowsAdService("administrator@nortek.com", "123abc,.", "ldap://192.168.1.5:389", "dc=example,dc=com");
		List<LdapUser> users = ad.getAllUser();
		System.out.println(users);
		System.exit(0);
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
