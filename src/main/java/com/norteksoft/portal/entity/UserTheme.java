package com.norteksoft.portal.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.norteksoft.product.orm.IdEntityNoExtendField;

/**
 * 主题
 */
@Entity
@Table(name="PORTAL_USER_THEME")
public class UserTheme extends IdEntityNoExtendField{
	private static final long serialVersionUID = 1L;
	
	private Long userId;			//用户ID
	@Column(length=50)
	private String themeCode;		//css和js名字,主题名字
	
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getThemeCode() {
		return themeCode;
	}

	public void setThemeCode(String themeCode) {
		this.themeCode = themeCode;
	}

}
