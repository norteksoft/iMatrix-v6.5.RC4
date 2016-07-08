package com.norteksoft.acs.ldap;

import com.norteksoft.acs.entity.sysSetting.LdapType;
import com.norteksoft.acs.ldap.impl.ApacheDsService;
import com.norteksoft.acs.ldap.impl.DominoService;
import com.norteksoft.acs.ldap.impl.WindowsAdService;
import com.norteksoft.product.util.ContextUtils;

public class LdapFactory {

	public static LdapService getLdapService(LdapType type, String username, String password, String url, String ldapBaseDomain){
		LdapService synOrgHandler = getLdapService(username,password,url);
		//当xml中没有配置该bean时
		if(synOrgHandler==null){
			switch (type) {
				case APACHE:  return new ApacheDsService(username, password, url, ldapBaseDomain);
				case DOMINO: return new DominoService(username, password, url, ldapBaseDomain);
				case WINDOWS_AD: return new WindowsAdService(username, password, url, ldapBaseDomain);
			}
		}
		return synOrgHandler;
	}
	
	private static LdapService getLdapService(String username, String password, String url){
		LdapService service = null;
		try{
			service = (LdapService)ContextUtils.getBean("synOrgLdapHandler");
			if(service==null)return null;
			service.setAdminName(username);
			service.setPassword(password);
			service.setLdapUrl(url);
		}catch (Exception e) {
			return null;
		}
		return service;
	}
	
}
