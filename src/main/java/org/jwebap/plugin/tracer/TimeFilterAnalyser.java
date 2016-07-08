package org.jwebap.plugin.tracer;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;

import org.jwebap.core.Analyser;
import org.jwebap.core.Trace;
import org.jwebap.plugin.tracer.http.HttpRequestTrace;
import org.jwebap.plugin.tracer.jdbc.ProxyConnection;
import org.jwebap.plugin.tracer.method.MethodTrace;
import org.jwebap.startup.JwebapListener;

import com.norteksoft.product.util.MemCachedUtils;

public class TimeFilterAnalyser implements Analyser{
	
	protected int maxTraceSize;

	private List traces;

	private long tracefilterActivetime;

	public TimeFilterAnalyser() {
		maxTraceSize = -1;
		traces = new Vector();
		tracefilterActivetime = -1L;
	}
	
	public void activeProcess(Trace trace) {
		if (getMaxTraceSize() > -1 && traces.size() >= getMaxTraceSize()) {
			removeFirstTrace();
		}
		traces.add(trace);
		
	}
	
	public static Map<String,List<String>> monitor_map = new HashMap<String, List<String>>();
	
	public static List<String> https = new ArrayList<String>();
	public static List<String> meths = new ArrayList<String>();
	public static List<String> jdbcs = new ArrayList<String>();
	
	

	/**
	 * zzl  
	 */
	public void inactiveProcess(Trace trace) {
		long activeTime = System.currentTimeMillis() - trace.getCreatedTime();
		
		if (trace != null && tracefilterActivetime >= 0L && tracefilterActivetime >= activeTime){
			removeTrace(trace);
		}else{
			trace.inActive(); 
			
			//zzl  
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if (trace instanceof HttpRequestTrace) {
				
				HttpRequestTrace h=(HttpRequestTrace)trace;
				String system_Code=JwebapListener.system_code;
				String jweb_type="http";
				String is_Closed=h.getInactiveTime() > 0 ? "closed" : "opened";
				String cost=String.valueOf(h.getActiveTime());//访问完成用时
				String created_Date=format.format(new Timestamp(h.getCreatedTime()));;//访问开始时间
				String in_Active_Time=format.format(new Timestamp(h.getInactiveTime()));//访问结束时间
				String ip=h.getIp();
				String uri=h.getUri();
				String detail=h.getParams();
				
				MonitorInfor monitorInfor = new MonitorInfor();
				monitorInfor.setSystemCode(system_Code);
				monitorInfor.setJweb_type(jweb_type);
				monitorInfor.setIsClosed(is_Closed);
				monitorInfor.setCost(cost);
				monitorInfor.setCreatedDate(created_Date);
				monitorInfor.setInActiveTime(in_Active_Time);
				monitorInfor.setIp(ip);
				monitorInfor.setUri(uri);
				monitorInfor.setDetail(detail);
				
				String code=system_Code+"_http";
				String key= code+UUID.randomUUID().toString().replace("-", "");
				https.add(key);
				monitor_map.put(code, https);
				MemCachedUtils.add(key,monitorInfor);
				//DBA.insetParmeter(system_Code, jweb_type, is_Closed, cost, created_Date, in_Active_Time, "", ip, uri, detail, "");
				//System.out.println( "HttpRequestTrace=============================================");
			}
			
			
			if (trace instanceof MethodTrace) { 
				MethodTrace h=(MethodTrace)trace;
				String system_Code=JwebapListener.system_code;
				String jweb_type="meth";
				String is_Closed=h.getInactiveTime() > 0 ? "closed" : "opened";
				String cost=String.valueOf(h.getActiveTime());//访问完成用时
				String created_Date=format.format(new Timestamp(h.getCreatedTime()));;//访问开始时间
				String in_Active_Time=format.format(new Timestamp(h.getInactiveTime()));//访问结束时间
				String method=h.getMethod();
				String detail=h.getParams();
				
				MonitorInfor monitorInfor = new MonitorInfor();
				monitorInfor.setSystemCode(system_Code);
				monitorInfor.setJweb_type(jweb_type);
				monitorInfor.setIsClosed(is_Closed);
				monitorInfor.setCost(cost);
				monitorInfor.setCreatedDate(created_Date);
				monitorInfor.setInActiveTime(in_Active_Time);
				monitorInfor.setMethod(method);
				monitorInfor.setDetail(detail);
				
				
				String code=system_Code+"_meth";
				String key= code+UUID.randomUUID().toString().replace("-", "");
				meths.add(key);
				monitor_map.put(code, meths);
				MemCachedUtils.add(key,monitorInfor);
				//DBA.insetParmeter(system_Code, jweb_type, is_Closed, cost, created_Date, in_Active_Time, method, "", "", detail, "");
				//System.out.println( "MethodTrace=============================================");
				
			}
			
			
			
			if (trace instanceof ProxyConnection) {
				ProxyConnection h=(ProxyConnection)trace;
				String system_Code=JwebapListener.system_code;
				String jweb_type="jdbc";
				String is_Closed=h.getInactiveTime() > 0 ? "closed" : "opened";
				String cost=String.valueOf(h.getActiveTime());//访问完成用时
				String created_Date=format.format(new Timestamp(h.getCreatedTime()));;//访问开始时间
				String in_Active_Time=format.format(new Timestamp(h.getInactiveTime()));//访问结束时间
				String sqlList=h.getSql();
				String detail=h.getParams();
				
				MonitorInfor monitorInfor = new MonitorInfor();
				monitorInfor.setSystemCode(system_Code);
				monitorInfor.setJweb_type(jweb_type);
				monitorInfor.setIsClosed(is_Closed);
				monitorInfor.setCost(cost);
				monitorInfor.setCreatedDate(created_Date);
				monitorInfor.setInActiveTime(in_Active_Time);
				monitorInfor.setSqlList(sqlList);
				monitorInfor.setDetail(detail);
				
				
				String code=system_Code+"_jdbc";
				String key= code+UUID.randomUUID().toString().replace("-", "");
				jdbcs.add(key);
				monitor_map.put(code, jdbcs);
				MemCachedUtils.add(key,monitorInfor);
				//DBA.insetParmeter(system_Code, jweb_type, is_Closed, cost, created_Date, in_Active_Time, "", "", "", detail,sqlList);
				//System.out.println("ProxyConnection=============================================");
				
			}
			System.out.println("----------------------------------------------");
			
		}
		
		
	}
	
	public void destoryProcess(Trace trace) {
		
		
	}
	
	/**
	 * 清空统计数据
	 *
	 */
	public void clear() {
		List children = new ArrayList(getTraces());
		Trace trace;
		for (Iterator it = children.iterator(); it.hasNext();){
			trace = (Trace) it.next();
			trace.destroy();	
		}
		traces.clear();	
	}

	public List getTraces() {
		return traces;
	}
	
	public long getTracefilterActivetime() {
		return tracefilterActivetime;
	}

	public void setTracefilterActivetime(long tracefilterActivetime) {
		this.tracefilterActivetime = tracefilterActivetime;
	}

	public int getMaxTraceSize() {
		return maxTraceSize;
	}

	public void setMaxTraceSize(int maxTraceSize) {
		this.maxTraceSize = maxTraceSize;
	}
	
	protected synchronized void removeFirstTrace() {
		if (traces != null && traces.size() > 0)
			removeTrace((Trace) traces.get(0));
	}
	
	protected synchronized void removeTrace(Trace trace) {
		if (traces != null) {
			traces.remove(trace);
			trace.destroy();
		}
	}
}