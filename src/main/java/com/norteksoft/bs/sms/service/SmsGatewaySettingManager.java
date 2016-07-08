package com.norteksoft.bs.sms.service;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.bs.sms.base.enumeration.GatewayStatus;
import com.norteksoft.bs.sms.base.enumeration.SendReceiveStatus;
import com.norteksoft.bs.sms.dao.SmsGatewaySettingDao;
import com.norteksoft.bs.sms.entity.SmsGatewaySetting;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.web.struts2.Struts2Utils;


/**
 * 短信网关设置
 * @author c
 *
 */
@Repository
@Transactional
public class SmsGatewaySettingManager {
	@Autowired
	private SmsGatewaySettingDao smsGatewaySettingDao;
	/**
	 * 短信网关设置/列表
	 * @author c
	 */
	public void getAllSmsGatewaySetting(Page<SmsGatewaySetting> page) {
		smsGatewaySettingDao.getAllSmsGatewaySetting(page);
	}
	
	public String delete(String ids) {
		//先验证有没有已启用的
		String flag = validateDelete(ids);
		if(StringUtils.isNotEmpty(flag)) return flag;
		String[] idArr = ids.split(",");
		for (String id : idArr) {
			smsGatewaySettingDao.delete(Long.valueOf(id));
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
			SmsGatewaySetting setting = this.getSmsGatewaySettingById(Long.valueOf(id));
			if(GatewayStatus.ENABLE.equals(setting.getGatewayStatus())){
				return Struts2Utils.getText("basicSetting.deleteGatewayValidate");
			}
		}
		return "";
	}
	public void delete(Long id) {
		smsGatewaySettingDao.delete(id);
	}

	public void save(SmsGatewaySetting smsGatewaySetting) {
		smsGatewaySetting.setGatewayStatus(GatewayStatus.DRAFT);//网关状态为草稿
		smsGatewaySetting.setSendReceiveStatus(SendReceiveStatus.SENDANDRECEIVE);
		smsGatewaySetting.setMaxTime(3);
		smsGatewaySettingDao.save(smsGatewaySetting);
	}

	public SmsGatewaySetting getSmsGatewaySettingById(Long id) {
		return smsGatewaySettingDao.get(id);
	}

	/**
	 * 短信网关设置/保存/验证编号的唯一性
	 * @param gatewayCode
	 * @param id
	 * @return
	 */
	public boolean validateCode(String gatewayCode, Long id) {
		SmsGatewaySetting setting = smsGatewaySettingDao.getSmsGatewaySetByCode(gatewayCode);
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
	 * 短信网关设置/启用禁用网关
	 * @author c
	 */
	public String changeGatewayState(String ids) {
		StringBuilder sb = new StringBuilder();
		int draftToEnableNum = 0, disableToEnableNum = 0, enableToDisableNum = 0;
		String[] idArr = ids.split(",");
		for (String id : idArr) {
			SmsGatewaySetting smsGatewaySetting = smsGatewaySettingDao.get(Long.valueOf(id));
			if (smsGatewaySetting.getGatewayStatus() == null 
					|| smsGatewaySetting.getGatewayStatus() == GatewayStatus.DRAFT) {// 草稿到启用
				smsGatewaySetting.setGatewayStatus(GatewayStatus.ENABLE);
				draftToEnableNum++;
			} else if (smsGatewaySetting.getGatewayStatus() == GatewayStatus.DISABLE) {// 禁用到启用
				smsGatewaySetting.setGatewayStatus(GatewayStatus.ENABLE);
				disableToEnableNum++;
			} else if (smsGatewaySetting.getGatewayStatus() == GatewayStatus.ENABLE) {// 启用到禁用
				smsGatewaySetting.setGatewayStatus(GatewayStatus.DISABLE);
				enableToDisableNum++;
			}
			smsGatewaySettingDao.save(smsGatewaySetting);
		}
		sb.append(Struts2Utils.getText("interfaceManager.draftToStart")).append(draftToEnableNum).
		append(Struts2Utils.getText("interfaceManager.forbiddenToStart")).append(disableToEnableNum).
		append(Struts2Utils.getText("interfaceManager.startToforbidden")).append(enableToDisableNum);
		return sb.toString();
	}

	/**
	 * 短信猫保存配置
	 * @param id
	 * @param string
	 * @param maxTime
	 * @param sendReceiveStatus
	 */
	public void saveConfig(Long id, String result, String maxTime, String sendReceiveStatus) {
		SmsGatewaySetting setting = smsGatewaySettingDao.get(id);
		setting.setMaxTime(Integer.valueOf(maxTime));
		for (SendReceiveStatus value : SendReceiveStatus.values()) {
			if(value.name().equals(sendReceiveStatus)){
				setting.setSendReceiveStatus(value);
			}
		}
		setting.setConfiguration(result);
		smsGatewaySettingDao.save(setting);
	}
	/**
	 * 解析短信猫的配置/转为数组
	 * @param id
	 * @return
	 */
	public String[] getAllConfigCat(SmsGatewaySetting setting) {
		String[] result = new String[5];
		if(StringUtils.isEmpty(setting.getConfiguration())){
			setting.setConfiguration("gatewayId:,comName:,bitTimer:,creater:");
		}
		result = stringToArr(setting.getConfiguration(), result);
		result[4] = setting.getMaxTime() + "";
		return result;
	}
	/**
	 * 解析微米网的配置/转为数组
	 * @param id
	 * @return
	 */
	public String[] getAllConfigWeimi(SmsGatewaySetting setting) {
		String[] result = new String[4];
		if(StringUtils.isEmpty(setting.getConfiguration())){
			setting.setConfiguration("weimiId:,weimiPw:,smsSign:");
		}
		result = stringToArr(setting.getConfiguration(), result);
		result[3] = setting.getMaxTime() + "";
		return result;
	}
	/**
	 * 将string转为string[] 
	 * 	固定格式为：param = a:a,b:b,c:c...
	 * @return
	 */
	private String[] stringToArr(String param,String[] result ){
		String[] configArr = param.split(",");
		for (int i = 0;i < configArr.length ;i++) {
			if(configArr[i].split(":").length < 2){
				result[i] = null;//如果没值，设为空
			}else {
				result[i] = configArr[i].split(":")[1];
			}
		}
		return result;
	}

	/**
	 * 将 string : id=1#$name=zhangsan#$code=aaa 
	 * 	转为 ： id=1
	 * 		 name=zhangsan
	 * 		 code=aaa
	 * @param smsGatewaySetting
	 * @return
	 */
	public String getAllConfigOneself(String configuration) {
		if(StringUtils.isEmpty(configuration)) return null;
		return configuration.replace("#$", "\n");
	}
	

}
