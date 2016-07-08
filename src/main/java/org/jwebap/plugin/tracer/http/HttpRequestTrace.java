package org.jwebap.plugin.tracer.http;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.json.JSONString;
import org.jwebap.core.StatistableTrace;
import org.jwebap.core.Trace;
import org.jwebap.util.Arrays;

public class HttpRequestTrace extends StatistableTrace implements JSONString {

	private int i = 0;

	private String ip = null;

	/** 请求URI */
	private String uri = null;

	/** 请求queryString */
	private String queryString = null;

	/** 请求的参数列表 */
	private Map parameters = new HashMap();

	public HttpRequestTrace(Trace stackTrace, HttpServletRequest request) {
		super(stackTrace);
		ini(request);
	}

	public HttpRequestTrace(HttpServletRequest request) {
		ini(request);
	}

	private void ini(HttpServletRequest request) {
		// uri
		setUri(request.getRequestURI());
		// queryString
		if (request.getQueryString() != null) {
			setQueryString(request.getQueryString());
		}
		// ip
		setIp(request.getRemoteHost());

		// 参数
		setParameters(request.getParameterMap());

		// inner key:用于统计访问频率，以uri为key，统计访问次数和时间
		StatistableTrace.InnerKey key = new StatistableTrace.InnerKey(uri);
		setKey(key);

	}

	public void openConnection() {
		i++;
	}

	public int getOpenedConnectionCount() {
		return i;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Map getParameters() {
		return parameters;
	}

	public void setParameters(Map parameters) {
		this.parameters = parameters;
	}

	public String getQueryString() {
		return queryString;
	}

	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	/**
	 * 返回轨迹对应的Json对象，供视图使用
	 * 
	 * @return
	 */
	public String toJSONString() {
		Map map = new HashMap();

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String createTime = "--:--";
		String destroyTime = "--:--";
		if (getCreatedTime() > 0) {
			createTime = format.format(new Timestamp(getCreatedTime()));
		}
		if (getInactiveTime() > 0) {
			destroyTime = format.format(new Timestamp(getInactiveTime()));
		}
		map.put("isClosed", getInactiveTime() > 0 ? "closed" : "opened");
		map.put("cost", String.valueOf(getActiveTime()));
		map.put("createTime", createTime);
		map.put("destoryTime", destroyTime);
		map.put("ip", getIp());
		map.put("uri", getUri());
		map.put("jdbcOpened", String.valueOf(getOpenedConnectionCount()));
		// http轨迹的详细信息
		String params = "";
		Map parameters = getParameters();

		String lf = "<br/>";
		for (Iterator keys = parameters.keySet().iterator(); keys.hasNext();) {
			Object key = keys.next();
			Object value = parameters.get(key);
			String strValue = "";
			if (value instanceof Object[]) {
				strValue = Arrays.deepToString((Object[]) value);
			} else {
				strValue = value.toString();
			}
			params += "  " + key + " : " + strValue + lf;
		}

		String detail = "";
		detail += "ip     :" + getIp() + lf + "uri    :" + getUri() + lf
				+ "query  :" + getQueryString() + lf + "params :" + "<hr/>"
				+ params;

		map.put("detail", detail);
		return new JSONObject(map).toString();
	}
	
	/**
	 * zzl  
	 * @return
	 */
	public String getParams(){
		// http轨迹的详细信息
		String params = "";
		Map parameters = getParameters();

		String lf = "<br/>";
		for (Iterator keys = parameters.keySet().iterator(); keys.hasNext();) {
			Object key = keys.next();
			Object value = parameters.get(key);
			String strValue = "";
			if (value instanceof Object[]) {
				strValue = Arrays.deepToString((Object[]) value);
			} else {
				strValue = value.toString();
			}
			params += "  " + key + " : " + strValue + lf;
		}
		return params;
	}
}
