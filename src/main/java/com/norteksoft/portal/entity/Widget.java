package com.norteksoft.portal.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.norteksoft.portal.service.IndexManager;
import com.norteksoft.product.orm.IdEntityNoExtendField;
import com.norteksoft.product.util.ContextUtils;


/**
 * 小窗口
 */
@Entity
@Table(name="PORTAL_WIDGET")
public class Widget extends IdEntityNoExtendField implements Comparable<Widget>{

	private static final long serialVersionUID = 1L;
	
	@Column(length=50)
	private String name;      //窗口名称
	@Transient
	private String viewName;  //窗口名称(用于显示)
	
	@Column(length=150)
	private String url;       //窗口内容的URL
	
	private Boolean acquiescent = false; //是否默认显示(所有人都有)
	
	@Column(length=50)
	private String code;//小窗体编码,确定窗口唯一
	
	@Column(length=255)
	private String systemCode;//系统id
	
	private Boolean pageVisible = false; //是否显示分页
	
	private Boolean borderVisible = true;//一栏页签中，小窗体是否显示边框
	
	private Boolean iframeable=false;//小窗体的内容是否以iframe方式获得
	
	//小窗体默认位置
	private Integer position;      //（0：left,1:center,2:right）分栏后 在那一栏里
	
	@OneToMany(mappedBy="widget", cascade=CascadeType.REMOVE)
	private List<WidgetParameter> parameters = new ArrayList<WidgetParameter>(); //窗口参数
	@Transient
	private String systemUrl;
	
	private Integer widgetHeight;//小窗体高度
	@Transient
	private String tempUrl;//iframe时的链接地址
	@Transient
	private String nameil8;      //窗口名称-国际化的名称
	
	private Boolean autoLoginable=false;//小窗体的内容以iframe方式获得时，是否需要单点登录

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public String getSystemCode() {
		return systemCode;
	}

	public void setSystemCode(String systemCode) {
		this.systemCode = systemCode;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<WidgetParameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<WidgetParameter> parameters) {
		this.parameters = parameters;
	}

	public int compareTo(Widget widget) {
		Long result = this.getId()-widget.getId();
		return result.intValue() ;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Boolean getAcquiescent() {
		return acquiescent;
	}

	public void setAcquiescent(Boolean acquiescent) {
		this.acquiescent = acquiescent;
	}

	public Boolean getPageVisible() {
		return pageVisible;
	}

	public void setPageVisible(Boolean pageVisible) {
		this.pageVisible = pageVisible;
	}

	public Boolean getBorderVisible() {
		return borderVisible;
	}

	public void setBorderVisible(Boolean borderVisible) {
		this.borderVisible = borderVisible;
	}

	public Boolean getIframeable() {
		return iframeable;
	}

	public void setIframeable(Boolean iframeable) {
		this.iframeable = iframeable;
	}

	public String getSystemUrl() {
		return systemUrl;
	}

	public void setSystemUrl(String systemUrl) {
		this.systemUrl = systemUrl;
	}

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	public Integer getWidgetHeight() {
		return widgetHeight;
	}

	public void setWidgetHeight(Integer widgetHeight) {
		this.widgetHeight = widgetHeight;
	}
 
	public String getTempUrl() {
		return tempUrl;
	}

	public void setTempUrl(String tempUrl) {
		this.tempUrl = tempUrl;
	}

	public Boolean getAutoLoginable() {
		return autoLoginable;
	}

	public void setAutoLoginable(Boolean autoLoginable) {
		this.autoLoginable = autoLoginable;
	}

	public String getNameil8() {
		IndexManager indexManager = (IndexManager)ContextUtils.getBean("indexManager");
		return indexManager.getNameToi18n(this.name);
	}

	public void setNameil8(String nameil8) {
		this.nameil8 = nameil8;
	}

	public String getViewName() {
		IndexManager indexManager = (IndexManager)ContextUtils.getBean("indexManager");
		return indexManager.getNameToi18n(this.name);
	}

}
