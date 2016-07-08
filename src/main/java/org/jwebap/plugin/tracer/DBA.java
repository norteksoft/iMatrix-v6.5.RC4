package org.jwebap.plugin.tracer;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.jwebap.startup.JwebapListener;




/**
 * zzl  
 * @author Administrator
 *
 */
public class DBA {
	private static Connection con=null ;
	public static String trace_max_size_http="100";
	public static String trace_filter_active_time_http="100";
	public static String trace_max_size_jdbc="100";
	public static String trace_filter_active_time_jdbc="100";
	public static String trace_max_size_meth="100";
	public static String trace_filter_active_time_meth="100";
	public static String driver_clazzs=null;
	public static String detect_clazzs=null;
	public static synchronized  Connection  getConByJDBC() throws ClassNotFoundException, SQLException, IOException{
		//定义了数据库连接串 
		String dbUrl = readProperties("hibernate.connection.url"); 
		//数据库的用户名 
		String user = readProperties("hibernate.connection.username"); 
		//数据库的用户口令 
		String password = readProperties("hibernate.connection.password"); 
		// 加载jdbc-odbc bridge驱动程序 
		Class.forName(readProperties("hibernate.connection.driver_class"));
		// 与url指定的数据源建立连接 
		con = DriverManager.getConnection(dbUrl, user, password); 
		return con;
	}
	
	 public static  synchronized Connection getConByJNDI()throws Exception{
	     Context ctx = new InitialContext();
	     String name=readProperties("jndi.name");
	      DataSource ds = (DataSource)ctx.lookup("java:comp/env/jdbc/"+name);
	      con= ds.getConnection();
	      return con;
	 }
	 

	
	public static void close() throws SQLException{
		if(con!=null)
			con.close();
	}
	
	public static void getParmeter(){
		Statement state=null;
		ResultSet resoult=null;
		try {
			Connection con=DBA.getConByJDBC();
			state = con.createStatement(); 
			resoult = state.executeQuery("SELECT trace_max_size_http,trace_filter_active_time_http,trace_max_size_jdbc,trace_filter_active_time_jdbc,trace_max_size_meth,trace_filter_active_time_meth,driver_clazzs,detect_clazzs from MONITOR_PARMETER where system_Code='"+JwebapListener.system_code+"'"); 
			if(resoult.next()){
				trace_max_size_http=resoult.getString("trace_max_size_http");
				trace_filter_active_time_http=resoult.getString("trace_filter_active_time_http");
				trace_max_size_jdbc=resoult.getString("trace_max_size_jdbc");
				trace_filter_active_time_jdbc=resoult.getString("trace_filter_active_time_jdbc");
				trace_max_size_meth=resoult.getString("trace_max_size_meth");
				trace_filter_active_time_meth=resoult.getString("trace_filter_active_time_meth");
				driver_clazzs=resoult.getString("driver_clazzs");
				detect_clazzs=resoult.getString("detect_clazzs");
			}
			
		}catch (Exception e1) {
			e1.printStackTrace();
		}finally{
			try {
				resoult.close();
				state.close(); 
				DBA.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void insetParmeter(String system_Code,String jweb_type,String is_Closed,String cost,String created_Date,String in_Active_Time,String method,String ip,String uri,String detail,String sqlList){
		try {
			Connection con=DBA.getConByJDBC();  
			String sql="insert into MONITOR_MONITORINFOR (system_Code,jweb_type,is_Closed,cost,created_Date,in_Active_Time,method,ip,uri,detail,sql_List) values(?,?,?,?,?,?,?,?,?,?,?)";
			PreparedStatement preparedStatement= con.prepareStatement(sql); 
			preparedStatement.setString(1, system_Code);
			preparedStatement.setString(2, jweb_type);
			preparedStatement.setString(3, is_Closed);
			preparedStatement.setString(4, cost);
			preparedStatement.setString(5, created_Date);
			preparedStatement.setString(6, in_Active_Time);
			preparedStatement.setString(7, method);
			preparedStatement.setString(8, ip);
			preparedStatement.setString(9, uri);
			preparedStatement.setString(10, detail);
			preparedStatement.setString(11, sqlList);
			preparedStatement.addBatch();
			preparedStatement.execute();
			preparedStatement.close(); 
		}catch (Exception e1) {
			e1.printStackTrace();
		}finally{
			try {
				DBA.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	private static String readProperties(String key) throws IOException{
		Properties propert = new Properties();
		propert.load(DBA.class.getClassLoader().getResourceAsStream("application.properties"));
		return propert.getProperty(key);
	}
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {
		Connection con=DBA.getConByJDBC();
		Statement state = con.createStatement(); 
		ResultSet resoult = state.executeQuery("SELECT title from OA_ACADEMY_ARCHIVES"); 
		while(resoult.next()) {
			System.out.println(resoult.getString("title"));
		} 
		state.close();
		DBA.close();
	}
}
