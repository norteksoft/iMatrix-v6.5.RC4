package com.norteksoft.security.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.acs.entity.organization.Company;
import com.norteksoft.acs.service.organization.CompanyManager;
import com.norteksoft.mms.base.data.DataHandle;
import com.norteksoft.mms.base.data.DataTransfer;
import com.norteksoft.mms.base.data.FileConfigModel;
import com.norteksoft.mms.base.utils.ImportExportExecption;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.PropUtils;
import com.norteksoft.product.util.ZipUtils;
import com.norteksoft.product.util.zip.ZipFile;
import com.norteksoft.security.entity.LicenseFile;
import com.norteksoft.utilSecret.license.License;
/**
 * mms中数据的导入导出
 * @author liudongxia
 *
 */
@Service
@Transactional
public class DataExecuteHandle{
	private Log log = LogFactory.getLog(DataExecuteHandle.class);
	@Autowired
	private CompanyManager companyManager;
	@Autowired
	private CheckLicenseManager checkLicenseManager;
	@Autowired
	private LicenseFileManager licenseFileManager;
	@Autowired
	private DataHandle dataHandle;
	
	
	/**
	 * 导出数据处理
	 * @param fileOut
	 * @param systemIds
	 * @param companyId
	 * @param dataCodes
	 */
	public void exportExecute(OutputStream fileOut,String systemIds,Long companyId,String dataCodes) throws ImportExportExecption{
		//将数据导出到文件夹中
		if(StringUtils.isEmpty(dataCodes)){
			List<FileConfigModel> result=new ArrayList<FileConfigModel>();
			List<FileConfigModel> acsFileConfigs=dataHandle.getFileConfigByCategory("basicData");
			List<FileConfigModel> initFileConfigs=dataHandle.getFileConfigByCategory("initData");
			result.addAll(acsFileConfigs);
			result.addAll(initFileConfigs);
			for(FileConfigModel config:result){
				if(StringUtils.isNotEmpty(config.getBeanname())){
					//创建导出文件夹，导出的文件暂存的位置
					File folder = new File(config.getExportRootPath()+"/"+config.getExportPath());
					if(!folder.exists()){
						folder.mkdirs();
					}
					
					DataTransfer bean=(DataTransfer)ContextUtils.getBean(config.getBeanname());
					bean.backup(systemIds, companyId, config);
				}
			}
		}else{
			String[] codes=dataCodes.split(",");
			for(String dataCode:codes){
				FileConfigModel config=dataHandle.getFileConfigByData(dataCode);
				if(StringUtils.isNotEmpty(config.getBeanname())){
					//创建导出文件夹，导出的文件暂存的位置
					File folder = new File(config.getExportRootPath()+"/"+config.getExportPath());
					if(!folder.exists()){
						folder.mkdirs();
					}
					
					DataTransfer bean=(DataTransfer)ContextUtils.getBean(config.getBeanname());
					bean.backup(systemIds, companyId, config);
				}
			}
		}
		
    	//获得导出的根节点
    	String[] rootPaths=dataHandle.getRootPath();
    	String exportRootPath=rootPaths[0];
    	
		//将生成的文件夹打成zip包且删除暂时文件夹
		 try {
			 ZipUtils.zipFolder(exportRootPath, fileOut);
		} catch (Exception e) {
			log.debug(PropUtils.getExceptionInfo(e));
		}finally{
			try {
				if(fileOut!=null)fileOut.close();
				
				FileUtils.deleteDirectory(new File(exportRootPath));
			}catch (Exception e) {
				log.debug(PropUtils.getExceptionInfo(e));
			}
		}
	}
	/**
	 * 导入数据处理
	 * @param file
	 * @param companyId
	 * @param importPosition
	 * @param imatrixInfo
	 * @throws Exception 
	 */
	public void importExecute(boolean isBasicData,File file,Long companyId,String category,String... imatrixInfo) throws Exception{
		String importRootPath = "";
		try {
	    	//获得导出的根节点
	    	String[] rootPaths=dataHandle.getRootPath();
	    	importRootPath=rootPaths[1];
	    	//清除历史留下的临时数据
	    	FileUtils.deleteDirectory(new File(importRootPath));
			ZipFile zipFile = new ZipFile(file);
			ZipUtils.unZipFileByOpache(zipFile, importRootPath); 
			if("basicData".equals(category)&&isBasicData){//初始化平台时验证zip包中的License.xml
				//验证license start
				File licenseXmlFile = new File(importRootPath + "/License.xml");
				if(!licenseXmlFile.exists()){
					throw new FileNotFoundException("license文件不存在");
				}
				if(FileUtils.readFileToString(licenseXmlFile) == null)
					throw new Exception("文件内容为空");
				String licenseXmlFileContent = FileUtils.readFileToString(licenseXmlFile, "UTF-8");
				if(checkLicenseManager.validateLicenseXml(licenseXmlFileContent)) 
					throw new Exception("license文件篡改");
				List<Company> companies = companyManager.getCompanyList();
				Map<String, String> licenseContent = License.getLicense(licenseXmlFileContent);
				if(StringUtils.equals("试用版本", licenseContent.get("ver_type"))){
					//非空库
					if(licenseFileManager.getLicenseFileByType("正式版本", false).size()<=0&&companies != null && companies.size() > 1){//当数据库中不存在任何正式版本license时，则初始化平台时验证是否有多个租户
						throw new Exception("有多个租户,不能初始化");
					}
				}else {
					throw new Exception("license无效,只能是试用版license");
				}
				addLicenseFile(licenseContent,licenseXmlFileContent);
				//验证license end
			}
			List<FileConfigModel>fileConfigs=dataHandle.getFileConfigByCategory(category);
			for(FileConfigModel config:fileConfigs){
				if(StringUtils.isNotEmpty(config.getBeanname())){
					DataTransfer bean=(DataTransfer)ContextUtils.getBean(config.getBeanname());
					bean.restore(isBasicData,companyId, config,imatrixInfo);
				}
			}
			FileUtils.deleteDirectory(new File(importRootPath));
		}catch (Exception e) {
			log.debug(PropUtils.getExceptionInfo(e));
			if(StringUtils.isNotEmpty(importRootPath))
				FileUtils.deleteDirectory(new File(importRootPath));//删除指定文件
			throw e;
		}  
	}
	/*
	 * license有效，放到数据库中
	 */
	private void addLicenseFile(Map<String, String> licenseContent,String licenseXmlFileContent) throws  Exception {
		//删除试用版本的license
		licenseFileManager.deleteTrialVersion("试用版本");
		LicenseFile licenseFile = new LicenseFile();
		licenseFile.setContent(licenseXmlFileContent);
		licenseFileManager.saveNoCompany(licenseFile);
	}
}

