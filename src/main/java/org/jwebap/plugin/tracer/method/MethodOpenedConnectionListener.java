package org.jwebap.plugin.tracer.method;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.jwebap.plugin.tracer.jdbc.ConnectionEventListener;

public class MethodOpenedConnectionListener implements ConnectionEventListener {

	private static ThreadLocal detectSeed=new ThreadLocal();
	
	public void fire(Connection conn) {
		if(detectSeed.get()==null){
			return;
		}
		
		try{
			List seeds=(List)detectSeed.get();
			for(int i=0;i<seeds.size();i++){
				MethodTrace trace=(MethodTrace)seeds.get(i);
				trace.openConnection();
			}
		}catch(Exception e){
			
		}
		
	}
	
	public static void addDetectSeed(MethodTrace trace){
		if(detectSeed!=null){
			List seeds=(List)detectSeed.get();
			if(seeds==null){
				seeds=new ArrayList();
				detectSeed.set(seeds);
			}
			seeds.add(trace);
			
		}
	}
	
	public static void removeDetectSeed(MethodTrace trace){
		if(detectSeed!=null){
			List seeds=(List)detectSeed.get();
			if(seeds!=null){
				seeds.remove(trace);
			}
			
			
		}
	}

}
