package com.norteksoft.mms.base.autoTool;



import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.norteksoft.mms.form.enumeration.DataType;

/**
 * swing工具生成元数据功能工具类
 * @author ldx
 *
 */
public class Util {
	private static Log logger = LogFactory.getLog(Util.class);
	public static String rootPath = "";
	public static String classPath = "";
	public static String libPath = "";
	public static String fileName = "setting_data.properties";
	/**
	 * 获得数据库字段：myNameField--my_name_field
	 * @param fieldName
	 * @return
	 */
	public static String analysisFieldName(String fieldName)
	 {
		StringBuilder result = new StringBuilder();
		int j = 0;
	  for(int i = 0; i < fieldName.length(); i++)
	  {
	   char c = fieldName.charAt(i);
	   if (!Character.isLowerCase(c))
	   {//是大写字母时处理
		   if(StringUtils.isEmpty(result.toString())){
			   result.append(fieldName.substring(j,i)).append("_").append(Character.toUpperCase(c));
		   }else{
			   result.append(fieldName.substring(j+1,i)).append("_").append(Character.toUpperCase(c));
		   }
		   j = i;
	   }
	  }
	  if(StringUtils.isEmpty(result.toString())){
		  result.append(fieldName);
	  }else{
		  result.append(fieldName.substring(j+1));
	  }
	  return result.toString();
	 }
	/**
	 * 获得字段类型
	 * @return
	 */
	public static DataType getFieldType(Class type){
		if("java.util.Date".equals(type.getName())){
			return DataType.DATE;
		}else if("java.lang.String".equals(type.getName())){
			return DataType.TEXT;
		}else if("java.lang.Integer".equals(type.getName())){
			return DataType.INTEGER;
		}else if("java.lang.Long".equals(type.getName())){
			return DataType.LONG;
		}else if("java.lang.Double".equals(type.getName())||"double".equals(type.getName())){
			return DataType.DOUBLE;
		}else if("java.lang.Float".equals(type.getName())){
			return DataType.FLOAT;
		}else if("java.lang.Boolean".equals(type.getName())||"boolean".equals(type.getName())){
			return DataType.BOOLEAN;
		}else if("java.util.List".equals(type.getName())){
			return DataType.COLLECTION;
		}
		return null;
	}
	
	public static String getPath(){
		String path = StringUtils.removeStart(Util.class.getResource("/pic/").getFile(), "/");
		logger.debug(" conf path : "+path);
		return path;
	}
	
	 /**
     * 根据路径获得class
     * @param filePath D:/MyToolbox/eclipse3.5/ws/imatrix-6.0.0.RC/webapp/WEB-INF/classes/com/norteksoft/bs/holiday/entity/DataType.class
     * @return
     * @throws ClassNotFoundException
     */
    public static void _loadClassInJars(URLClassLoader myClassLoader) throws ClassNotFoundException {
    	//项目的lib包路径
    	String jarPath = libPath;
    	if(StringUtils.isNotEmpty(jarPath)){
    		File libDirectory =new File(jarPath);
    		if(libDirectory.exists()){
    			File[] files = libDirectory.listFiles();
    			if(files!=null){
    				for(int i=0;i<files.length;i++){
    					File filei = files[i];
    					if (filei.getAbsolutePath().endsWith(".jar")) {
    						loadFile(filei,myClassLoader);
    					}
    				}
    			}
    		}
    	}
    }
    private static Method addURL = initAddMethod(); 

		/** 初始化方法 */ 
	private static final Method initAddMethod() { 
		try { 
		Method add = URLClassLoader.class.getDeclaredMethod("addURL", 
		new Class[] { URL.class }); 
		add.setAccessible(true); 
		return add; 
		} catch (Exception e) { 
			logger.debug(" initAddMethod : "+e.getMessage()); 
			e.printStackTrace();
		} 
		return null; 
	} 


    public static final void loadFile(File file,URLClassLoader myClassLoader) { 
    	try { 
	    	addURL.invoke(myClassLoader, new Object[] { file.toURI().toURL() }); 
	    	System.out.println("加载类文件：" + file.getAbsolutePath()); 
    	} catch (Exception e) { 
    		logger.debug(" loadFile : "+e.getMessage()); 
    		e.printStackTrace(); 
    	} 
	} 
    
    /**
     * @param filePath D:/MyToolbox/eclipse3.5/ws/imatrix-6.0.0.RC/webapp/WEB-INF/classes/com/norteksoft/bs/holiday/entity/DataType.class
     * @return 类似com.norteksoft.bs.holiday.entity.DataType
     */
    public static String getClassName(String filePath){
    	String root = classPath;
    	if(!".class".equals(filePath.substring(filePath.lastIndexOf(".")))){//当类有继承关系时会递归调用该方法，例如继承类IdEntity，则filePath为com.norteksoft.product.orm.IdEntity,需要转换为文件路径
    		filePath = root+"/"+filePath.replace(".", "/")+".class";
    	}
    	String name = filePath.substring(root.length()+1,filePath.lastIndexOf("."));//com/norteksoft/bs/holiday/entity/DataType
		name = name.replace("/", ".").replace("\\", ".");//com.norteksoft.bs.holiday.entity.DataType
    	return name;
    }
    
    public static Map<String,String> getLibAndClassPath(String rootPath){
    	Map<String,String> result = new HashMap<String, String>();
    	File libDirectory =new File(rootPath);
		if(libDirectory.exists()){
			File[] files = libDirectory.listFiles();
			if(files!=null){
				for(int i=0;i<files.length;i++){
					String filename = files[i].getName();
					if(result.get("lib")!=null&&result.get("classes")!=null)return result;//减少循环
					if(files[i].isDirectory()&&"lib".equals(filename)){
						result.put("lib", files[i].getPath()) ;
					}
					if(files[i].isDirectory()&&"classes".equals(filename)){
						result.put("classes", files[i].getPath()) ;
					}
				}
			}
		}
		return result;
    }
}
