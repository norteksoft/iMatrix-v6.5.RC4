package com.norteksoft.bs.signature.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.norteksoft.product.orm.IdEntityNoExtendField;

/**
 * 签章
 * @author Administrator
 *
 */
@Entity
@Table(name = "BS_SIGNATURE")
public class Signature extends IdEntityNoExtendField implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private Long  userId;       //用户ID
	@Column(length=25)
	private String userName;    //用户名称
	@Column(length=150)
	private String pictureSrc;  //图片路径
	@Column(length=50)
	private String subCompanyName;  //公司名称

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPictureSrc() {
		return pictureSrc;
	}

	public void setPictureSrc(String pictureSrc) {
		this.pictureSrc = pictureSrc;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getSubCompanyName() {
		return subCompanyName;
	}

	public void setSubCompanyName(String subCompanyName) {
		this.subCompanyName = subCompanyName;
	}
}
