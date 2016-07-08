package com.norteksoft.mms.base.autoTool;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.net.URLClassLoader;
public class MyClassLoader extends URLClassLoader {
	public MyClassLoader(URL[] urls, URLClassLoader parent) {
		super(urls, parent);
	}
	
    protected Class<?> findClass(String name) throws ClassNotFoundException {
    	Class<?> clazz = null; 
    	
    	try{
    	clazz = super.findClass(name);
    	}catch(ClassNotFoundException ex){
	    	if( clazz == null ) 
	    		clazz = _findClass(name); 
	    	
	    	if( clazz == null )
	    		throw ex;
    	}
    	return clazz; 
    }
    /**
     * 根据路径获得class
     * @param filePath D:/MyToolbox/eclipse3.5/ws/imatrix-6.0.0.RC/webapp/WEB-INF/classes/com/norteksoft/bs/holiday/entity/DataType.class
     * @return
     * @throws ClassNotFoundException
     */
    private Class<?> _findClass(String filePath) throws ClassNotFoundException {
    	Class<?> clazz = null; 
    	String root = Util.classPath;
    	if(!".class".equals(filePath.substring(filePath.lastIndexOf(".")))){//当类有继承关系时会递归调用该方法，例如继承类IdEntity，则filePath为com.norteksoft.product.orm.IdEntity,需要转换为文件路径
    		filePath = root+"/"+filePath.replace(".", "/")+".class";
    	}
    	File file =new File(filePath);
    	if(file.exists()){
    		String name = filePath.substring(root.length()+1,filePath.lastIndexOf("."));//com/norteksoft/bs/holiday/entity/DataType
    		name = name.replace("/", ".").replace("\\", ".");//com.norteksoft.bs.holiday.entity.DataType
    		try{
    			FileInputStream fileInputStream = new FileInputStream(file);
    			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    			int size, SIZE = 4096; 
    			byte[] buffer = new byte[SIZE]; 
    			while( (size = fileInputStream.read(buffer)) > 0 ){
    				outputStream.write(buffer, 0, size); 
    			}
    			fileInputStream.close(); 
    			byte[] classBytes = outputStream.toByteArray(); 
    			outputStream.close();
    			clazz = findLoadedClass(name);
    			if (clazz == null) {
					clazz = defineClass(name, classBytes, 0, classBytes.length);
    			}
    		}catch(Exception ex){
    			throw new ClassNotFoundException(name);
    		}
    	}
    	return clazz; 
    }
    
}
