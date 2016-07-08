package com.norteksoft.product.api.impl;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import com.ibm.icu.text.SimpleDateFormat;
import com.norteksoft.acs.entity.organization.Company;
import com.norteksoft.acs.service.organization.CompanyManager;
import com.norteksoft.product.api.FileService;
import com.norteksoft.product.enumeration.UploadFileType;
import com.norteksoft.product.mongo.MongoFile;
import com.norteksoft.product.mongo.MongoService;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.PropUtils;
import com.norteksoft.product.util.ThreeDes;

public class FileServiceImpl implements FileService {
	private MongoService mongoService;
	
	public void setMongoService(MongoService mongoService) {
		this.mongoService = mongoService;
	}

	public String saveFile(File file,Long companyId) {
		
		String uploadFileType=PropUtils.getProp("application.properties","upload.file.type");
		if(StringUtils.isEmpty(uploadFileType)){
			uploadFileType=PropUtils.getProp("applicationContent.properties","upload.file.type");
		}
		String filePath="";
		try {
			switch (UploadFileType.valueOf(uploadFileType)) {
			case SERVERS_SECRET:
				filePath=uploadSecret(file,companyId);
				break;
			case SERVERS_NORMAL:
				filePath=uploadNormal(file,companyId);
				break;
			case MONGO_SERVERS:
				MongoFile mongoFile = mongoService.saveFile(file, "");
				filePath=mongoFile.getFileId();
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return filePath;
	}
	/**
	 * 路径中添加公司编码，便于用户自己删除上传的文件
	 * @param companyId
	 * @param path
	 * @return
	 */
	private String getCompanyCode(Long companyId){
		String path="";
		if(companyId!=null){
			CompanyManager companyManager = (CompanyManager)ContextUtils.getBean("companyManager");
			Company company = companyManager.getCompany(companyId);
			if(company!=null)path = company.getCode();
		}
		return path;
	}
	
	private String getUploadFilePath(){
		String path=PropUtils.getProp("application.properties","upload.file.path");
		if(StringUtils.isEmpty(path)){
			path=PropUtils.getProp("applicationContent.properties","upload.file.path");
		}
		if(!(path.lastIndexOf("/")==path.length()-1)){
			path=path+"/";
		}
		return path;
	}
	
	private String uploadSecret(File file,Long companyId)throws Exception{
		String path=getUploadFilePath();
		String fullPath=ThreeDes.encryptFile(path+getCompanyCode(companyId),file.getPath());
		return fullPath.replace(path, "");
	}
	
	private String uploadNormal(File file,Long companyId)throws Exception{
		String path=getUploadFilePath();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String fullPath = cretaFolder(path+getCompanyCode(companyId)+"/"+format.format(new Date())+"/")+UUID.randomUUID().toString();
		fullPath=uploadFile(file, fullPath);
		return fullPath.replace(path, "");
	}
	
	public String saveFile(byte[] file,Long companyId) {
		String uploadFileType=PropUtils.getProp("application.properties","upload.file.type");
		if(StringUtils.isEmpty(uploadFileType)){
			uploadFileType=PropUtils.getProp("applicationContent.properties","upload.file.type");
		}
		String filePath="";
		try {
			switch (UploadFileType.valueOf(uploadFileType)) {
			case SERVERS_SECRET:
				filePath=uploadSecret(file,companyId);
				break;
			case SERVERS_NORMAL:
				filePath=uploadNormal(file,companyId);
				break;
			case MONGO_SERVERS:
				MongoFile mongoFile = mongoService.saveFile(file, "");
				filePath=mongoFile.getFileId();
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return filePath;
	}
	
	private String uploadSecret(byte[] file,Long companyId)throws Exception{
		String path=getUploadFilePath();
		String fullPath = ThreeDes.encryptFile(path+getCompanyCode(companyId),file);
		return fullPath.replace(path, "");
	}
	
	private String uploadNormal(byte[] file,Long companyId)throws Exception{
		String path=getUploadFilePath();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String fullPath = cretaFolder(path+getCompanyCode(companyId)+"/"+format.format(new Date())+"/")+UUID.randomUUID().toString();
		BufferedInputStream bis = null;
		FileOutputStream out = null;
		try {
			bis = new BufferedInputStream(new ByteArrayInputStream(file));
			byte[]  buffer = new byte[1024*1024];
			int size=0;
			out = new FileOutputStream(fullPath);
			while ((size=bis.read(buffer, 0, buffer.length)) != -1) {
				out.write(buffer, 0,size);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			out.close();
			bis.close();
		}
		return fullPath.replace(path, "");
	}
	
	/**
	 * 创建文件夹
	 * @param path
	 * @return
	 */
	private String cretaFolder(String path){
		File file = new File(path);
		if(!file.exists()){
			file.mkdirs();
		}
		return path;
	}
	
	/**
	 * 上传文件
	 */
	private String uploadFile(File path,String serverPath)throws Exception{
		FileUtils.copyFile(path, new File(serverPath));
		return serverPath;
	}
	
	private String getFullPath(String filePath){
		String path=getUploadFilePath();
		filePath = filePath.replace("\\", "/");
		if(path.indexOf("\\")>=0){
			path = path.replace("\\", "/");
		}
		if(filePath.contains(path)){
			return filePath;
		}else{
			return path+filePath;
		}
	}

	public byte[] getFile(String filePath) {
		String uploadFileType=PropUtils.getProp("application.properties","upload.file.type");
		if(StringUtils.isEmpty(uploadFileType)){
			uploadFileType=PropUtils.getProp("applicationContent.properties","upload.file.type");
		}
		byte[] file=null;
		try {
			switch (UploadFileType.valueOf(uploadFileType)) {
			case SERVERS_SECRET:
				file =ThreeDes.decryptFile(getFullPath(filePath));
				break;
			case SERVERS_NORMAL:
				File myFile = new File(getFullPath(filePath));
				if(myFile.exists()){
					file = FileUtils.readFileToByteArray(myFile);
				}
				break;
			case MONGO_SERVERS:
				file = mongoService.getFile(filePath);
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return file;
	}
	
	public void writeTo(String filePath,OutputStream out) {
		String uploadFileType=PropUtils.getProp("application.properties","upload.file.type");
		if(StringUtils.isEmpty(uploadFileType)){
			uploadFileType=PropUtils.getProp("applicationContent.properties","upload.file.type");
		}
		try {
			switch (UploadFileType.valueOf(uploadFileType)) {
			case SERVERS_SECRET:
				ThreeDes.decryptFile(getFullPath(filePath),out);
				break;
			case SERVERS_NORMAL:
				byte[] file=FileUtils.readFileToByteArray(new File(getFullPath(filePath)));
				out.write(file);
				out.close();
				break;
			case MONGO_SERVERS:
				mongoService.writeTo(filePath, out);
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void deleteFile(String filePath){
		String uploadFileType=PropUtils.getProp("application.properties","upload.file.type");
		if(StringUtils.isEmpty(uploadFileType)){
			uploadFileType=PropUtils.getProp("applicationContent.properties","upload.file.type");
		}
		try {
			switch (UploadFileType.valueOf(uploadFileType)) {
			case SERVERS_SECRET:
				FileUtils.deleteDirectory(new File(getFullPath(filePath)));
				break;
			case SERVERS_NORMAL:
				FileUtils.deleteQuietly(new File(getFullPath(filePath)));
				break;
			case MONGO_SERVERS:
				mongoService.deleteFile(filePath);
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
