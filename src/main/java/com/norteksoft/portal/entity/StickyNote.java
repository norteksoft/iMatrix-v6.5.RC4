package com.norteksoft.portal.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.norteksoft.product.orm.IdEntityNoExtendField;

/**
 * 便签
 */
@Entity
@Table(name="PORTAL_STICKY_NOTE")
public class StickyNote extends IdEntityNoExtendField{
	private static final long serialVersionUID = 1L;
	private Long userId;
	@Column(length=1000)
	private String content;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
