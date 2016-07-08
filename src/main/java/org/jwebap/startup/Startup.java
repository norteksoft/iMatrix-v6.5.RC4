package org.jwebap.startup;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jwebap.cfg.model.ComponentDef;
import org.jwebap.cfg.model.JwebapDef;
import org.jwebap.cfg.persist.PersistManager;
import org.jwebap.core.ContextFactory;
import org.jwebap.core.RuntimeContext;

/**
 * Jwebap启动类
 * 
 * @author leadyu
 * @since Jwebap 0.5
 * @date 2007-8-16
 */
public class Startup {

	private static final Log log = LogFactory.getLog(Startup.class);

	/**
	 * Jwebap运行时，包含了所有Jwebap运行时对象
	 * 
	 * 目前，Jwebap对于一个Application来说是单实例的
	 */
	private static RuntimeContext _context = null;
	// TODO 由RumtimeContextFactory创建jwebap实例
	
	/**
	 * 返回Jwebap当前实例
	 */
	public static RuntimeContext getRuntimeContext(){
		if(_context==null){
			throw new JwebapInitialException("jwebap not startup successfully,please check the application log.");
		}
		return _context;
	}
	
	/**
	 * Jwebap启动方法,以文件路径启动jwebap
	 * 
	 * @param args
	 * @throws MalformedURLException 
	 * @throws StartupException
	 */
	public static void startup(String path) throws MalformedURLException  {
		URL url=new URL("file:"+path);
		startup(url);
	}
	
	/**
	 * Jwebap启动方法,以URL启动,配置只读,不保证可写
	 * 
	 * @param args
	 * @throws StartupException
	 */
	public static void startup(URL url)  {
		
		ContextFactory contextFactory=new ContextFactory();
		JwebapDef config = null;
		PersistManager defManager=new PersistManager(url);
		
		try {
			config = defManager.get();
		} catch (Exception e) {
			throw new JwebapInitialException("",e);
		}
		

		RuntimeContext context = contextFactory.createRuntimeContext(config,defManager);
		Collection components = config.getAllComponentDefs();
		Iterator componentIt=components.iterator();
		/**
		 * 注册Components
		 */
		while (componentIt.hasNext()) {			
			ComponentDef def = (ComponentDef)componentIt.next();

			try {
				context.registerComponent(def.getName(), def);
			} catch (Exception e) {
				log.error("register component:" + def.getType() + " fail."
						+ e.getMessage());
				e.printStackTrace();
			}

		}
		//Startup.getRuntimeContext()用以检查jwebap是否正常启动，所以在最后对RuntimeContext赋值
		_context=context;
		log.info("jwebap component startup.");

	}
}
