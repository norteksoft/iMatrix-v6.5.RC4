package com.norteksoft.portal.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.norteksoft.product.orm.IdEntityNoExtendField;

/**
 * 用户当前语言
 */
@Entity
@Table(name="PORTAL_USER_CURRENT_LANGUAGE")
public class UserCurrentLanguage extends IdEntityNoExtendField{
	private static final long serialVersionUID = 1L;
	
	private Long userId;			//用户ID
	@Column(length=50)
	private String currentLanguage;		//用户当前语言，zh_CN,en_US
	
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getCurrentLanguage() {
		return currentLanguage;
	}
	public void setCurrentLanguage(String currentLanguage) {
		this.currentLanguage = currentLanguage;
	}


}
