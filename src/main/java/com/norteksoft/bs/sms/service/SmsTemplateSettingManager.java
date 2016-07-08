package com.norteksoft.bs.sms.service;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.bs.sms.dao.SmsTemplateSettingDao;
import com.norteksoft.bs.sms.entity.SmsAuthoritySetting;
import com.norteksoft.bs.sms.entity.SmsTemplateSetting;
import com.norteksoft.product.orm.Page;


/**
 * 短信网关设置
 * @author c
 *
 */
@Repository
@Transactional
public class SmsTemplateSettingManager {
	private static final String TEMPLATE_STRING = "#param#";
	@Autowired
	private SmsTemplateSettingDao smsTemplateSettingDao;

	public void getAllSmsTemplateSetting(Page<SmsTemplateSetting> page) {
		smsTemplateSettingDao.getAllSmsTemplateSetting(page);
	}
	public List<SmsTemplateSetting> getAllSmsTemplateSetting() {
		return smsTemplateSettingDao.getAllSmsTemplateSetting();
	}

	public SmsTemplateSetting getSmsTemplateSettingById(Long id) {
		return smsTemplateSettingDao.get(id);
	}
	
	public void save(SmsTemplateSetting SmsTemplateSetting) {
		smsTemplateSettingDao.save(SmsTemplateSetting);
	}

	public void delete(String ids) {
		String[] idArr = ids.split(",");
		for (String id : idArr) {
			smsTemplateSettingDao.delete(Long.valueOf(id));
		}
	}
	public void delete(Long id) {
		smsTemplateSettingDao.delete(id);
	}
	/**
	 * 短信平台/短信模版设置/保存/验证编号是否唯一
	 * @param templateCode 模板编号
	 * @param id id
	 */
	public boolean validateCode(String templateCode, Long id) {
		SmsTemplateSetting setting = smsTemplateSettingDao.getSmsTemplateSetByCode(templateCode);
		 if(setting == null){
			 return false;//不存在
		 }else{
			 if(id == null) return true;//新建时，根据编号取出数据，说明已存在
			 if(setting.getId().equals(id)){
				 return false;
			 }else{
				 return true;
			 }
		 }
	}
	/**
	 * 解析参数:如果接口编号取出接口后判断有无模板编号，
	 * 		            如果有，说明args是模版参数，应首先组装成内容，再发送
	 * 	   	            如果没有，说明args就是短信内容，直接发送
	 */
	public String parseArgsToContent(SmsAuthoritySetting setting, String[] args) {
		String content = "";
		String templateCode = setting.getTemplateCode();//模板编号
		if(StringUtils.isNotEmpty(templateCode)){
			SmsTemplateSetting settingTemp = smsTemplateSettingDao.getSmsTemplateSetByCode(templateCode);
			//根据模版编号获取模版
			content = settingTemp.getTemplateName() == null?"" : settingTemp.getTemplateName();
			//根据参数的个数依次替换模版中的占位符，如果参数不够，占位符保留不变
			for (int i = 0; i < args.length; i++) {
				content = content.replaceFirst(TEMPLATE_STRING, args[i]);
			}
		}else {
			return args[0];//不使用模板，args即是内容
		}
		return content;
	}
	
}
