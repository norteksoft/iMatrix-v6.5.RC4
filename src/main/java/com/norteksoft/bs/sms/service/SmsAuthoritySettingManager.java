package com.norteksoft.bs.sms.service;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.acs.entity.authorization.BusinessSystem;
import com.norteksoft.acs.service.authorization.BusinessSystemManager;
import com.norteksoft.bs.sms.base.enumeration.GatewayStatus;
import com.norteksoft.bs.sms.base.enumeration.ReceiveDispatchType;
import com.norteksoft.bs.sms.base.enumeration.RequestType;
import com.norteksoft.bs.sms.dao.SmsAuthoritySettingDao;
import com.norteksoft.bs.sms.entity.SmsAuthoritySetting;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.web.struts2.Struts2Utils;


/**
 * 收发接口设置/短信平台的权限控制/控制哪些模块可以使用短信功能
 * @author c
 *
 */
@Repository
@Transactional
public class SmsAuthoritySettingManager {
	@Autowired
	private SmsAuthoritySettingDao smsAuthoritySettingDao;
	@Autowired
	private BusinessSystemManager businessSystemManager;
	/**
	 * 短信平台/收发接口设置/列表
	 * @param systemId 
	 * @return
	 */
	public void getAllSmsAuthoritySetting(Page<SmsAuthoritySetting> page, Long systemId) {
		smsAuthoritySettingDao.getAllSmsAuthoritySetting(page,systemId);
	}
	/**
	 * 根据id获得实体
	 * @param id
	 * @return
	 */
	public SmsAuthoritySetting getSmsAuthoritySettingById(Long id) {
		return smsAuthoritySettingDao.get(id);
	}
	/**
	 * 删除
	 * @param ids
	 */
	public String delete(String ids) {
		//先验证有没有已启用的
		String flag = validateDelete(ids);
		if(StringUtils.isNotEmpty(flag)) return flag;
		String[] idArr = ids.split(",");
		for (String id : idArr) {
			smsAuthoritySettingDao.delete(Long.valueOf(id));	
		}
		return "";
	}
	/**
	 * 验证是否可以删除
	 * @param ids
	 * @return
	 */
	public String validateDelete(String ids) {
		String[] idArr = ids.split(",");
		for (String id : idArr) {
			SmsAuthoritySetting setting = this.getSmsAuthoritySettingById(Long.valueOf(id));
			if(setting.getUseStatus().equals(GatewayStatus.ENABLE)){
				return Struts2Utils.getText("messagePlatform.deleteInterfaceValidate");
			}
		}
		return "";
	}
	/**
	 * 保存
	 * @param smsAuthoritySetting
	 */
	public void save(SmsAuthoritySetting smsAuthoritySetting) {
		smsAuthoritySettingDao.save(smsAuthoritySetting);
	}
	/**
	 * 验证编号唯一性
	 * @param interCode
	 * @param id
	 * @return
	 */
	public boolean validateCode(String interCode, Long id) {
		SmsAuthoritySetting setting = smsAuthoritySettingDao.getSmsAuthoritySettingByCode(interCode);
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
	 * 
	 * @param systemId  系统id
	 * @param requestType  请求类型
	 * @param smsAuthoritySetting
	 */
	public void save(Long systemId, String requestType, SmsAuthoritySetting smsAuthoritySetting) {
		BusinessSystem bs = businessSystemManager.getBusiness(systemId);
		smsAuthoritySetting.setSystemId(systemId);//系统id
		smsAuthoritySetting.setSystemCode(bs.getCode());//系统编号
		smsAuthoritySetting.setUseStatus(GatewayStatus.DRAFT);//接口状态，设为草稿
		for (RequestType value : RequestType.values()) {
			if(value.name().equals(requestType)){
				smsAuthoritySetting.setRequestType(value);//请求类型
			}
		}
		if(ReceiveDispatchType.RECEIVE.equals(smsAuthoritySetting.getType())){
			smsAuthoritySetting.setTemplateCode(null);//请求类型
		}
		this.save(smsAuthoritySetting);
	}
	/**
	 * 短信平台/收发接口设置/启用禁用
	 * @return
	 * @param removeEnd
	 * @return
	 */
	public String changeStatus(String ids) {
		StringBuilder sb = new StringBuilder();
		int draftToEnableNum = 0, disableToEnableNum = 0, enableToDisableNum = 0;
		String[] idArr = ids.split(",");
		for (String id : idArr) {
			SmsAuthoritySetting setting = smsAuthoritySettingDao.get(Long.valueOf(id));
			if (setting.getUseStatus() == null 
					|| (setting.getUseStatus() == GatewayStatus.DRAFT )){//草稿到启用
				setting.setUseStatus(GatewayStatus.ENABLE);
				draftToEnableNum++;
			} else if (setting.getUseStatus() == GatewayStatus.DISABLE) {//禁用到启用
				setting.setUseStatus(GatewayStatus.ENABLE);
				disableToEnableNum++;
			} else if (setting.getUseStatus() == GatewayStatus.ENABLE) {//启用到禁用
				setting.setUseStatus(GatewayStatus.DISABLE);
				enableToDisableNum++;
			}
			smsAuthoritySettingDao.save(setting);
		}
		sb.append(Struts2Utils.getText("interfaceManager.draftToStart")).append(draftToEnableNum).
			append(Struts2Utils.getText("interfaceManager.forbiddenToStart")).append(disableToEnableNum).
			append(Struts2Utils.getText("interfaceManager.startToforbidden")).append(enableToDisableNum);
		return sb.toString();
	}

}
