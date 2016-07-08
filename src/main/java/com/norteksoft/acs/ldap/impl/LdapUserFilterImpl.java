package com.norteksoft.acs.ldap.impl;

import java.util.List;
import java.util.Map;

import com.norteksoft.acs.ldap.LdapUserFilter;

public class LdapUserFilterImpl implements LdapUserFilter {
	public List<String> filtrationAttributes() {
		return null;
	}

	public boolean isPersonnel(Map<String, String> filtrationItem) {
		return true;
	}
}
