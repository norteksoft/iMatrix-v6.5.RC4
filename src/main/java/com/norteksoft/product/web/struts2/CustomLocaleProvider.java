package com.norteksoft.product.web.struts2;

import java.util.Locale;

import com.opensymphony.xwork2.LocaleProvider;

public class CustomLocaleProvider implements LocaleProvider {
	private String language;
	public Locale getLocale() {
		return new Locale(language);
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}

}
