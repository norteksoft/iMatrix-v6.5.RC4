package com.norteksoft.mm.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.norteksoft.product.orm.IdEntityNoExtendField;


/**
 * 性能参监控参数
 */
@Entity
@Table(name="MONITOR_PARMETER")
public class MonitorParmeter extends IdEntityNoExtendField {
	private static final long serialVersionUID = 1L;
	
	private Integer trace_max_size_http=100;  //最大用时
	
	private Integer trace_filter_active_time_http=1000; //最多存储量
	
	private Integer trace_max_size_jdbc=100;  //最大用时
	
	private Integer trace_filter_active_time_jdbc=1000; //最多存储量
	
	private Integer trace_max_size_meth=100;  //最大用时
	
	private Integer trace_filter_active_time_meth=1000; //最多存储量
	@Column(length=100)
	private String driver_clazzs; //数据库驱动
	
	@Column(length=1000)
	private String detect_clazzs; //要监控的类
	
	@Column(length=50)
	private String systemCode;//系统编号
	@Column(length=50)
	private String systemName;//系统名称


	

	public Integer getTrace_max_size_http() {
		return trace_max_size_http;
	}

	public void setTrace_max_size_http(Integer traceMaxSizeHttp) {
		trace_max_size_http = traceMaxSizeHttp;
	}

	public Integer getTrace_filter_active_time_http() {
		return trace_filter_active_time_http;
	}

	public void setTrace_filter_active_time_http(Integer traceFilterActiveTimeHttp) {
		trace_filter_active_time_http = traceFilterActiveTimeHttp;
	}

	public Integer getTrace_max_size_jdbc() {
		return trace_max_size_jdbc;
	}

	public void setTrace_max_size_jdbc(Integer traceMaxSizeJdbc) {
		trace_max_size_jdbc = traceMaxSizeJdbc;
	}

	public Integer getTrace_filter_active_time_jdbc() {
		return trace_filter_active_time_jdbc;
	}

	public void setTrace_filter_active_time_jdbc(Integer traceFilterActiveTimeJdbc) {
		trace_filter_active_time_jdbc = traceFilterActiveTimeJdbc;
	}

	public Integer getTrace_max_size_meth() {
		return trace_max_size_meth;
	}

	public void setTrace_max_size_meth(Integer traceMaxSizeMeth) {
		trace_max_size_meth = traceMaxSizeMeth;
	}

	public Integer getTrace_filter_active_time_meth() {
		return trace_filter_active_time_meth;
	}

	public void setTrace_filter_active_time_meth(Integer traceFilterActiveTimeMeth) {
		trace_filter_active_time_meth = traceFilterActiveTimeMeth;
	}

	public String getDriver_clazzs() {
		return driver_clazzs;
	}

	public void setDriver_clazzs(String driverClazzs) {
		driver_clazzs = driverClazzs;
	}

	public String getDetect_clazzs() {
		return detect_clazzs;
	}

	public void setDetect_clazzs(String detectClazzs) {
		detect_clazzs = detectClazzs;
	}


	public String getSystemCode() {
		return systemCode;
	}

	public void setSystemCode(String systemCode) {
		this.systemCode = systemCode;
	}

	public String getSystemName() {
		return systemName;
	}

	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}
	
	
}
