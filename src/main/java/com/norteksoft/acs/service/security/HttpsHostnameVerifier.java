package com.norteksoft.acs.service.security;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

public class HttpsHostnameVerifier implements HostnameVerifier{

	public boolean verify(String hostname, SSLSession session) {
		return true;
	}

}
