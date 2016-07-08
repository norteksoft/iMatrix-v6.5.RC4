package com.norteksoft.security.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.security.dao.LicenseFileDao;
import com.norteksoft.security.entity.LicenseFile;
import com.norteksoft.utilSecret.license.License;

@Service
@Transactional
public class LicenseFileManager {
	@Autowired
	private LicenseFileDao licenseFileDao;
	
	/**
	 * 通过租户名称获得License文件
	 * @param name
	 * @return
	 */
	public LicenseFile getLicenseFileByName(String name){
		LicenseFile licenseFile = null;
			List<LicenseFile> list = licenseFileDao.getAllLicenseFile();
			if(list != null && list.size()>0){
				for(LicenseFile file:list){
					Map<String,String> licenseMap = null;
					try {
						licenseMap = License.getLicense(file.getContent().toString());
					} catch (Exception e) {
					}
					if(licenseMap!=null){
						String companyName = licenseMap.get("company_name");//租户名称
						String licenseVersion = licenseMap.get("ver_type");//版本类型
						if(companyName.equals(name) && "正式版本".equals(licenseVersion)){
							licenseFile = packagingLicenseFile(licenseMap,file);
							break;
						}
					}
				}
			}
		return licenseFile;
	}
	
	/**
	 * 通过license类型获得License文件列表
	 * @param type 试用版本、正式版本
	 * @param addErrorLicenseFlag 返回值中是否需要添加有异常的license，只有在sales的License管理列表中才需要显示异常的license
	 * @return
	 */
	public List<LicenseFile> getLicenseFileByType(String type,boolean addErrorLicenseFlag){
		List<LicenseFile> licenseFileList = new ArrayList<LicenseFile>();
		
		List<LicenseFile> list = licenseFileDao.getAllLicenseFile();
		if(list != null && list.size()>0){
			for(LicenseFile file:list){
				Map<String,String> licenseMap = null;
				String message = "";
				try {
					licenseMap = License.getLicense(file.getContent().toString());
					
				} catch (Exception e) {
					message = e.getMessage();
				}
				if(licenseMap != null){
					String licenseVersion = licenseMap.get("ver_type");//版本类型
					if(type.equals(licenseVersion)){
						licenseFileList.add(packagingLicenseFile(licenseMap,file));
					}
				}else{
					if(addErrorLicenseFlag){
						LicenseFile licenseFile = new LicenseFile();
						licenseFile.setExceptionMessage(message);
						licenseFileList.add(licenseFile);
					}
				}
			}
		}
		return licenseFileList;
	}
	/**
	 * 保存License文件
	 */
	public void saveNoCompany(LicenseFile licenseFile){
		licenseFileDao.saveNoCompany(licenseFile);;
	}
	/**
	 * 根据License版本类型删除License文件
	 * @param type
	 */
	public void deleteTrialVersion(String type){
		List<LicenseFile> fileList = getLicenseFileByType(type,false);
		if(fileList!=null && fileList.size()>0){
			for(LicenseFile file:fileList){
				licenseFileDao.createQuery("delete from LicenseFile where id = ?", file.getId()).executeUpdate();
			}
		}
	}
	private LicenseFile packagingLicenseFile(Map<String, String> licenseMap,LicenseFile file){
		LicenseFile licenseFile = new LicenseFile();
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			licenseFile.setId(file.getId());
			licenseFile.setCompanyName(licenseMap.get("company_name"));//租户名称
			licenseFile.setFormerName(licenseMap.get("former_name"));//曾用名
			licenseFile.setEnrollment(Integer.valueOf(licenseMap.get("users")));//注册人数
			licenseFile.setStartTime(format.parse(licenseMap.get("start_time")));//生效日期
			licenseFile.setEndTime(format.parse(licenseMap.get("end_time")));//失效日期
			licenseFile.setLicenseVersion(licenseMap.get("ver_type"));//版本
			licenseFile.setContent(file.getContent());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		return licenseFile;
	}
}
