package com.norteksoft.mm.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.norteksoft.product.orm.IdEntityNoExtendField;


/**
 * 性能参监控参数
 */
@Entity
@Table(name="MONITOR_MONITORINFOR")
public class MonitorInfor extends IdEntityNoExtendField {
	private static final long serialVersionUID = 1L;
	@Column(length=50)
	private String systemCode;
	@Column(length=50)
	private String jweb_type;  //http,,jdbc,,meth
	@Column(length=50)
	private String isClosed;//关闭状态
	@Column(length=10)
	private String cost;//用时
	
	/**
	 * 创建时间
	 */
	@Column(length=15)
	private String createdDate;

	/**
	 * 不活动时间
	 */
	@Column(length=15)
	private String inActiveTime;

	/**
	 * 轨迹内容
	 */
	@Column(length=100)
	private String content;
	@Column(length=50)
	private String method;
	@Column(length=20)
	private String ip = null;

	/** 请求URI */
	@Column(length=255)
	private String uri = null;

	/** 请求queryString */
	@Column(length=100)
	private String queryString = null;
	
	@Column(length=1000)
	private String detail;//详细轨迹
	
	@Column(length=800)
	private String sqlList;//详细轨迹
	
	



	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getQueryString() {
		return queryString;
	}

	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}


	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public String getInActiveTime() {
		return inActiveTime;
	}

	public void setInActiveTime(String inActiveTime) {
		this.inActiveTime = inActiveTime;
	}

	public String getSystemCode() {
		return systemCode;
	}

	public void setSystemCode(String systemCode) {
		this.systemCode = systemCode;
	}

	public String getJweb_type() {
		return jweb_type;
	}

	public void setJweb_type(String jwebType) {
		jweb_type = jwebType;
	}

	public String getIsClosed() {
		return isClosed;
	}

	public void setIsClosed(String isClosed) {
		this.isClosed = isClosed;
	}

	public String getCost() {
		return cost;
	}

	public void setCost(String cost) {
		this.cost = cost;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getSqlList() {
		return sqlList;
	}

	public void setSqlList(String sqlList) {
		this.sqlList = sqlList;
	}
	
	
}
