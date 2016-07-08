package com.norteksoft.mms.form.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.norteksoft.product.orm.IdEntityNoExtendField;

/**
 * jqGrid属性
 */
@Entity
@Table(name="MMS_JQ_GRID_PROPERTY")
public class JqGridProperty extends IdEntityNoExtendField  implements Serializable,Cloneable {
	private static final long serialVersionUID = 1L;
	@Column(length=20)
	private String name;//属性名称
	@Column(length=200)
	private String value;//属性值
	@ManyToOne
	@JoinColumn(name="FK_LIST_VIEW_ID")
	private ListView listView;//列表视图
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public ListView getListView() {
		return listView;
	}
	public void setListView(ListView listView) {
		this.listView = listView;
	}
	@Override
	public JqGridProperty clone(){
		try {
			return (JqGridProperty) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			throw new RuntimeException("JqGridProperty clone failure");
		}
	}
}
