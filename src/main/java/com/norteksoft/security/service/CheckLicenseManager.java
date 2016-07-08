package com.norteksoft.security.service;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.acs.base.enumeration.LockedState;
import com.norteksoft.acs.entity.organization.Company;
import com.norteksoft.acs.entity.organization.User;
import com.norteksoft.acs.service.organization.CompanyManager;
import com.norteksoft.acs.service.organization.UserManager;
import com.norteksoft.acs.service.sale.SubsciberManager;
import com.norteksoft.security.entity.LicenseFile;
import com.norteksoft.utilSecret.license.License;
import com.norteksoft.utilSecret.license.LicenseConfigErrorException;
import com.norteksoft.utilSecret.license.LicenseConfigNullException;
import com.norteksoft.utilSecret.license.LicenseErrorException;
import com.norteksoft.utilSecret.license.LicenseNullException;

@Service
@Transactional
public class CheckLicenseManager {
	private Log log = LogFactory.getLog(getClass());
	@Autowired
	private LicenseFileManager licenseFileManager;
	@Autowired
	private CompanyManager companyManager;
	@Autowired
	private UserManager userManager;
	@Autowired
	private SubsciberManager subsciberManager;

	public void checkLicense() {
		List<Company> companies = companyManager.getCompanyList();
		if(companies == null || companies.size() < 1)return;
		for (Company company : companies) {
			checkCompany(company,companies.size());
		}
	}
	
	/*
	 * 验证该公司下是否有符合的license文件
	 */
	private void checkCompany(Company company,int companysSize ) {
		//检测正式版
		checkFormalLicense(company);
		List<LicenseFile> list = licenseFileManager.getLicenseFileByType("正式版本",false);
		if(list==null || list.size()==0){
			//检测试用版
			checkModifyDatas(company,companysSize);
		}
	}
	
	/*
	 * 检测正式版
	 */
	public String checkFormalLicense(Company company) {
		return checkFormalLicense(company.getName());
	}
	/*
	 * 根据company id检测正式版重载
	 */
	public String checkFormalLicense(Long id) {
		return checkFormalLicense(companyManager.getCompany(id));
	}
	/*
	 * 根据company id检测试用版重载
	 */
	public String checkModifyDatas(Long id,int companysSize) {
		return checkModifyDatas(companyManager.getCompany(id),companysSize);
	}
	/*
	 * 检测试用版
	 */
	public String checkModifyDatas(Company company,int companysSize) {
		return checkModifyDatas(company.getName(),companysSize);
	}
	/*
	 * 根据company id检测正式版重载
	 */
	public String checkFormalLicense(String companyName) {
		LicenseFile licenseFile = licenseFileManager.getLicenseFileByName(companyName);
		if(licenseFile == null || licenseFile.getContent() == null){
			//无license，锁定租户
			lockCompany(companyName,true);
			License.setSecurityFlag();
			log.debug(companyName + "：没有license，或license中licenseXml为空，锁定租户");
			return "该租户无license文件";
		}
		//验证license是否被篡改
		if( validateLicenseXml(licenseFile.getContent())){
			//license无效，锁定租户
			lockCompany(companyName,true);
			log.debug(companyName + "：正式版license篡改，锁定租户");
			return "该租户license篡改";
		}
		if(licenseFile.getStartTime()==null || licenseFile.getEndTime()==null ){
			//无生失效日期，锁定租户
			lockCompany(companyName,true);
			log.debug(companyName + "：正式版license篡改，生失效日期为空，锁定租户");
			return "正式版license篡改，生失效日期为空";
		}
		try{
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd"); 
			Date currentDate = df.parse(df.format(new Date()));
			long cunrrentTime = currentDate.getTime();
			if(cunrrentTime < licenseFile.getStartTime().getTime() || cunrrentTime > licenseFile.getEndTime().getTime()){
				//license失效，锁定租户
				lockCompany(companyName,true);
				log.debug(companyName + "：正式版license失效，锁定租户");
				return "正式版license失效";
			}
		}catch(Exception e){
			
		}
		//验证用户数量
		String validateResult = validateUserNum(companyName,licenseFile,"正式版");
		if(!validateResult.equals("false"))return validateResult;
		
		lockCompany(companyName,false);//解锁租户
		return "ok";//返回的ok结果不可修改，影响到sales
	}
	/*
	 * 根据company id检测试用版重载
	 */
	public String checkModifyDatas(String companyName,int companysSize) {
		List<LicenseFile> licenses = licenseFileManager.getLicenseFileByType("试用版本",false);
		if(licenses.size()<=0 || licenses.get(0).getContent() == null){
			lockCompany(companyName,true);//锁定租户
			License.setSecurityFlag();
			return "不存在试用版本license，请初始化平台导入试用版本license";
		}
		LicenseFile licenseFile= licenses.get(0);
		//验证license是否被篡改
		if(validateLicenseXml(licenseFile.getContent())){
			//license无效，锁定租户
			log.debug(companyName + "：试用版license篡改，锁定租户");
			lockCompany(companyName,true);
			return "该租户试用版license篡改";
		}
		if(companysSize < 1)return "ok";//无租户
		if( companysSize > 1){
			//试用版license有多个租户，锁定租户
			log.debug(companyName + "：试用版license，但存在多个租户，锁定租户");
			lockCompany(companyName,true);return "试用版license，但存在多个租户";
		}else {
			//验证用户数量
			String validateResult = validateUserNum(companyName,licenseFile,"试用版");
			if(!validateResult.equals("false"))return validateResult;
		}
		lockCompany(companyName,false);//解锁租户
		return "ok";
		
	}
	
	private String validateUserNum(String companyName,LicenseFile licenseFile,String type){
		Long companyId = companyManager.getSchoolId(companyName);
		if(companyId!=null){
			//自动修改订单中的注册用户数
			log.debug(companyName + "："+type+"license，租户订单中注册用户数量超过 "+licenseFile.getEnrollment()+"，自动修改订单中的注册用户数");
			//修改租户注册用户数
			subsciberManager.updateUseNumber(companyId, licenseFile.getEnrollment()); 
			
			//判断用户数量
			List<User> users = userManager.getUsersByCompanyId(companyId);
			if(users != null && users.size() > licenseFile.getEnrollment()){
				//license无效，锁定租户
				log.debug(companyName + "："+type+"license，已注册用户数量超过 "+licenseFile.getEnrollment()+"，锁定租户");
				lockCompany(companyName,true);return type+"license，已注册用户数量超过 "+licenseFile.getEnrollment();
			}
		}
		return "false";
	}
	/*
	 * 检测license是否篡改
	 */
	public boolean validateLicenseXml(String licenseXml){
		try {
			License.getLicense(licenseXml);
		} catch (LicenseConfigNullException e) {
			return  true;
		} catch (LicenseConfigErrorException e) {
			return  true;
		} catch (LicenseNullException e) {
			return  true;
		} catch (LicenseErrorException e) {
			return  true;
		} catch (UnsupportedEncodingException e) {
			return  true;
		}
		return  false;
	}
	/*
	 * 锁定公司
	 */
	public void lockCompany(String companyName,boolean lock) {
		Long id = companyManager.getSchoolId(companyName);
		if(id == null )return ;
		Company company = companyManager.getCompany(id);
		if (company != null) {
			if(lock){
				company.setLockedState(LockedState.LOCKED);
			}else{
				company.setLockedState(LockedState.NORMAL);
			}
			companyManager.saveCompany(company);
		}
	}
	
}
