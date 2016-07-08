package com.norteksoft.mms.form.web;

import java.io.File;
import java.io.IOException;
import java.net.URLClassLoader;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import com.norteksoft.mms.base.autoTool.MyClassLoader;
import com.norteksoft.mms.base.autoTool.Util;
import com.norteksoft.mms.form.service.DataTableManager;
import com.norteksoft.mms.form.service.FormViewManager;
import com.norteksoft.mms.module.entity.Menu;
import com.norteksoft.mms.module.service.MenuManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.entity.Company;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.ParameterUtils;
import com.norteksoft.product.util.ThreadParameters;
import com.norteksoft.product.util.ZipUtils;
import com.norteksoft.product.util.freemarker.TemplateRender;
import com.norteksoft.product.util.zip.ZipFile;
/**
 * swing代码生成工具点击生成元数据按钮
 * @author ldx
 *
 */
public class GenerateDataServlet extends HttpServlet
{
    private String companyCode;
    private String systemCode;
    File tempPathFile;
   
    public void init() throws ServletException {
     }


public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
{
   try
   {
	   companyCode = request.getParameter("companyCode");
	   systemCode = request.getParameter("systemCode");
	   String path = getGenerateDir();
	   File folder = new File(path);
		if(!folder.exists()){
			folder.mkdir();
		}
	   String tempPath = null;
	   File savedFile = null;
    // Create a factory for disk-based file items
    DiskFileItemFactory factory = new DiskFileItemFactory();
    // Set factory constraints
    factory.setSizeThreshold(4096); // 设置缓冲区大小，这里是4kb
    factory.setRepository(tempPathFile);// 设置缓冲区目录
    // Create a new file upload handler
    ServletFileUpload upload = new ServletFileUpload(factory);
    // Set overall request size constraint
    upload.setSizeMax(4194304); // 设置最大文件尺寸，这里是4MB
    List<FileItem> items = upload.parseRequest(request);// 得到所有的文件
    Iterator<FileItem> i = items.iterator();
    while (i.hasNext())
    {
     FileItem fi = (FileItem) i.next();
     String fileName = fi.getName();
     if (fileName != null)
     {
    	 String fileNamePart = UUID.randomUUID().toString();
    	 tempPath = path+fileNamePart;
    	 fileName = fileNamePart+".zip";
      savedFile = new File(path, fileName);
      fi.write(savedFile);
     }
    }
    ZipFile zipFile = new ZipFile(savedFile,"utf-8");
	ZipUtils.unZipFileByOpache(zipFile, tempPath);
	
    MenuManager menuManager = (MenuManager)ContextUtils.getBean("menuManager");
    DataTableManager dataTableManager = (DataTableManager)ContextUtils.getBean("dataTableManager");
	Util.classPath = tempPath;
	Util.libPath = getLibPath();
	
	URLClassLoader classLoader = (URLClassLoader)Thread.currentThread().getContextClassLoader();
	
	MyClassLoader myClassLoader = new MyClassLoader( classLoader.getURLs(), classLoader ); 
	Util._loadClassInJars(myClassLoader);
	Long companyId = getCompanyId();//将公司id放入线程中
	Menu menu = menuManager.getRootMenuByCode(systemCode);
	Long menuId = menu.getId();
	//filePath:项目工程路径/web目录/WEB-INF
	//工具中的“系统编码”不为空 或 “系统编码”为空,则表示系统的编码由表名获得，表名规则：系统编码_....
	dataTableManager.loadDataTableClass(companyId,menuId,myClassLoader,tempPath);
	dataTableManager.loadActionClass(tempPath, systemCode, myClassLoader);
	
	//删除文件和文件夹
	if(savedFile!=null)savedFile.delete();
	File file = new File(tempPath);
	if(file.exists()){
		FileUtils.deleteDirectory(file);
	}
   }
   catch (Exception e)
   {
    // 可以跳转出错页面
    e.printStackTrace();
   }
}
private String getGenerateDir(){
	String path=FormViewManager.class.getClassLoader().getResource("application.properties").getPath();
	path=path.substring(1, path.indexOf("WEB-INF/classes"))+TemplateRender.GENERATE_DIR;
	return path;
}
private String getLibPath(){
	String path=FormViewManager.class.getClassLoader().getResource("application.properties").getPath();
	path=path.substring(1, path.indexOf("WEB-INF/classes"));
	return path+"WEB-INF/lib";
}
private Long getCompanyId(){
	Long companyId = null;
	if(StringUtils.isEmpty(companyCode)){
		List<Company> list =ApiFactory.getAcsService().getAllCompanys();
		if(list.size()>0)companyId = list.get(0).getId();
	}else{
		Company company = ApiFactory.getAcsService().getCompanyByCode(companyCode);
		if(company!=null)companyId = company.getId();
	}
	if(companyId!=null){
		ThreadParameters parameters = new ThreadParameters(companyId);
		ParameterUtils.setParameters(parameters);
	}
	return companyId;
}
}
