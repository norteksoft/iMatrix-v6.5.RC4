package org.jwebap.plugin.tracer.http;

import java.sql.Connection;

import org.jwebap.plugin.tracer.jdbc.ConnectionEventListener;

public class ServletOpenedConnectionListener implements ConnectionEventListener {

	private static ThreadLocal detectSeed=new ThreadLocal();
	
	public void fire(Connection conn) {
		if(detectSeed.get()==null){
			return;
		}
		
		try{
			HttpRequestTrace trace=(HttpRequestTrace)detectSeed.get();
			trace.openConnection();
		}catch(Exception e){
			
		}
		
	}
	
	public static void addDetectSeed(HttpRequestTrace trace){
		if(detectSeed!=null){
			detectSeed.set(trace);
		}
	}

	public static void clearDetectSeed(){
		if(detectSeed!=null){
			detectSeed.set(null);
		}
	}
}
