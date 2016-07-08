package com.norteksoft.acs.ldap;

import java.util.List;
import java.util.Map;

/**
 * LDAP人员过滤
 * @author nortek
 *
 */
public interface LdapPersonnelFilter {
	/**
	 * LDAP需要过滤的属性
	 * @return
	 */
	public List<String> filtrationAttributes();
	/**
	 * 通过过滤项判断是否是人员
	 * @param filtrationItem
	 * @return
	 */
	public boolean isPersonnel(Map<String, String> filtrationItem);
}
