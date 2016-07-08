package com.norteksoft.bs.sms.web;


import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;

import com.norteksoft.bs.sms.base.enumeration.SendReceiveStatus;
import com.norteksoft.bs.sms.entity.SmsGatewaySetting;
import com.norteksoft.bs.sms.service.SmsGatewaySettingManager;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.PageUtils;
import com.norteksoft.product.web.struts2.CrudActionSupport;
import com.norteksoft.product.web.struts2.Struts2Utils;
/**
 * 短信平台
 * @author lenove1
 *
 */
@Namespace("/sms")
@ParentPackage("default")
@Results( { @Result(name = CrudActionSupport.RELOAD, location = "sms-gateway-setting", type = "redirectAction") })
public class SmsGatewaySettingAction extends CrudActionSupport<SmsGatewaySetting> {

	private static final long serialVersionUID = 1L;
	private Long id;
	private String ids;
	private Page<SmsGatewaySetting> page = new Page<SmsGatewaySetting>(0,true);

	private SmsGatewaySetting smsGatewaySetting;
	private String gatewayCode;// 网关编号
	private String gatewayName;// 网关名称

	private String gatewayId;// 网关ID
	private String comName;// 串口名称
	private String bitTimer;// 串口每秒发送数据的bit位数
	private String creater;// 短信猫生产产商
	private String maxTime;// 最大发送次数
	private String sendReceiveStatus;// 收发设置

	private String weimiId;//微米Id
	private String weimiPw;//微米密码
	private String smsSign;//短信签名

	private String configuration;//自定义网关的配置
	
	private String flag;// 配置类型
	@Autowired
	private SmsGatewaySettingManager smsGatewaySettingManager;
	private List<SendReceiveStatus> sendReceiveStatusList;

	private static final String SUCCESS_MESSAGE_LEFT = "<font class=\"onSuccess\"><nobr>";
	private static final String MESSAGE_RIGHT = "</nobr></font>";
	private static final String ERROR_MESSAGE_LEFT = "<font class=\"onError\"><nobr>";

	protected void addErrorMessage(String message) {
		this.addActionMessage(ERROR_MESSAGE_LEFT + message + MESSAGE_RIGHT);
	}
	protected void addSuccessMessage(String message){
		this.addActionMessage(SUCCESS_MESSAGE_LEFT+message+MESSAGE_RIGHT);
	}
	/**
	 * 短信网关设置/列表
	 * @author c
	 */
	@Override
	@Action("sms-gateway-setting")
	public String list() throws Exception {
		if(page.getPageSize()>1){
			smsGatewaySettingManager.getAllSmsGatewaySetting(page);
			this.renderText(PageUtils.pageToJson(page));
			return null;
		}
		return "sms-gateway-setting";
	}
	/**
	 * 短信网关设置/删除
	 * @author c
	 */
	@Override
	@Action("sms-gateway-setting-delete")
	public String delete() throws Exception {
		String flag = smsGatewaySettingManager.delete(StringUtils.removeEnd(ids, ","));
		if(StringUtils.isNotEmpty(flag)){
			this.addErrorMessage(flag);
		}else {
			this.addSuccessMessage(Struts2Utils.getText("basicSetting.deleteSuccess"));
		}
		return "sms-gateway-setting";
	}
	/**
	 * 短信网关设置/新建
	 * @author c
	 */
	@Override
	@Action("sms-gateway-setting-input")
	public String input() throws Exception {
		return "sms-gateway-setting-input";
	}
	/**
	 * 短信网关设置/保存/验证编号的唯一性
	 * @author c
	 */
	@Action("sms-gateway-setting-validateCode")
	public String validateCode() throws Exception {
		boolean flag = smsGatewaySettingManager.validateCode(gatewayCode,id);
		this.renderText(String.valueOf(flag));
		return null;
	}
	/**
	 * 短信网关设置/保存
	 * @author c
	 */
	@Override
	@Action("sms-gateway-setting-save")
	public String save() throws Exception {
		smsGatewaySettingManager.save(smsGatewaySetting);
		this.addSuccessMessage(Struts2Utils.getText("form.save.success"));
		return "sms-gateway-setting-input";
	}
	/**
	 * 短信网关设置/启用禁用网关
	 * @author c
	 */
	@Action("sms-gateway-setting-changeGatewayState")
	public String changeGatewayState() throws Exception {
		String result= smsGatewaySettingManager.changeGatewayState(ids);
		this.renderText(result);
		return null;
	}
	/**
	 * 短信网关设置/短信猫配置
	 * @author c
	 */
	@Action("sms-gateway-setting-config")
	public String config() throws Exception {
		if(gatewayName == null) return null;
		sendReceiveStatusList = Arrays.asList(SendReceiveStatus.values());
		smsGatewaySetting = smsGatewaySettingManager.getSmsGatewaySettingById(id);
		if("smsCat".equals(smsGatewaySetting.getGatewayType())){
			String[] valueArr = smsGatewaySettingManager.getAllConfigCat(smsGatewaySetting);
			gatewayId = valueArr[0];// 网关ID
			comName = valueArr[1];// 串口名称
			bitTimer = valueArr[2];// 串口每秒发送数据的bit位数
			creater = valueArr[3];// 短信猫生产产商
			maxTime = valueArr[4];//最大发送次数
			return "sms-gateway-setting-smscat";
		}else if ("smsWeimi".equals(smsGatewaySetting.getGatewayType())) {
			String[] valueArr = smsGatewaySettingManager.getAllConfigWeimi(smsGatewaySetting);
			weimiId = valueArr[0];// 微米Id
			weimiPw = valueArr[1];// 微米密码
			smsSign = valueArr[2];// 短信签名
			maxTime = valueArr[3];//最大发送次数
			return "sms-gateway-setting-smsweimi";
		}else {
			configuration = smsGatewaySettingManager.getAllConfigOneself(smsGatewaySetting.getConfiguration());
			maxTime = smsGatewaySetting.getMaxTime() + "";
			return "sms-gateway-setting-oneself";
		}
	}
	/**
	 * 短信网关设置/短信猫配置/保存配置
	 * @author c
	 */
	@Action("sms-gateway-setting-saveConfig")
	public String saveConfig() throws Exception {
		sendReceiveStatusList = Arrays.asList(SendReceiveStatus.values());
		if(flag != null && "smscat".equals(flag)){
			StringBuilder result = new StringBuilder();
			result.append("gatewayId:" + gatewayId + ",");//网关ID
			result.append("comName:" + comName + ",");//串口名称
			result.append("bitTimer:" + bitTimer + ",");//串口每秒发送数据的bit位数
			result.append("creater:" + creater );//短信猫生产产商
			smsGatewaySettingManager.saveConfig(id,result.toString(),maxTime,sendReceiveStatus);
			this.addSuccessMessage(Struts2Utils.getText("form.save.success"));
			return config();
		}else if(flag != null && "weimi".equals(flag)){
			StringBuilder result = new StringBuilder();
			result.append("weimiId:" + weimiId + ",");//微米Id
			result.append("weimiPw:" + weimiPw + ",");//微米密码
			result.append("smsSign:" + smsSign);//微米签名
			smsGatewaySettingManager.saveConfig(id,result.toString(),maxTime,sendReceiveStatus);
			this.addSuccessMessage(Struts2Utils.getText("form.save.success"));
			return config();
		}else {
			smsGatewaySettingManager.saveConfig(id,configuration,maxTime,sendReceiveStatus);
			this.addSuccessMessage(Struts2Utils.getText("form.save.success"));
			return config();
		}
	}
	
	@Override
	protected void prepareModel() throws Exception {
		if(id == null){
			smsGatewaySetting = new SmsGatewaySetting();
		}else {
			smsGatewaySetting = smsGatewaySettingManager.getSmsGatewaySettingById(id);
		}
	}
	public SmsGatewaySetting getModel() {
		return smsGatewaySetting;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}


	public SmsGatewaySetting getSmsGatewaySetting() {
		return smsGatewaySetting;
	}

	public void setSmsGatewaySetting(SmsGatewaySetting smsGatewaySetting) {
		this.smsGatewaySetting = smsGatewaySetting;
	}

	public Page<SmsGatewaySetting> getPage() {
		return page;
	}

	public void setPage(Page<SmsGatewaySetting> page) {
		this.page = page;
	}
	public String getGatewayCode() {
		return gatewayCode;
	}
	public void setGatewayCode(String gatewayCode) {
		this.gatewayCode = gatewayCode;
	}
	public String getIds() {
		return ids;
	}
	public void setIds(String ids) {
		this.ids = ids;
	}
	public String getGatewayName() {
		return gatewayName;
	}
	public void setGatewayName(String gatewayName) {
		this.gatewayName = gatewayName;
	}
	public String getGatewayId() {
		return gatewayId;
	}
	public void setGatewayId(String gatewayId) {
		this.gatewayId = gatewayId;
	}
	public String getComName() {
		return comName;
	}
	public void setComName(String comName) {
		this.comName = comName;
	}
	public String getBitTimer() {
		return bitTimer;
	}
	public void setBitTimer(String bitTimer) {
		this.bitTimer = bitTimer;
	}
	public String getCreater() {
		return creater;
	}
	public void setCreater(String creater) {
		this.creater = creater;
	}
	public String getMaxTime() {
		return maxTime;
	}
	public void setMaxTime(String maxTime) {
		this.maxTime = maxTime;
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	public List<SendReceiveStatus> getSendReceiveStatusList() {
		return sendReceiveStatusList;
	}
	public void setSendReceiveStatusList(List<SendReceiveStatus> sendReceiveStatusList) {
		this.sendReceiveStatusList = sendReceiveStatusList;
	}
	public String getSendReceiveStatus() {
		return sendReceiveStatus;
	}
	public void setSendReceiveStatus(String sendReceiveStatus) {
		this.sendReceiveStatus = sendReceiveStatus;
	}
	public String getWeimiId() {
		return weimiId;
	}
	public void setWeimiId(String weimiId) {
		this.weimiId = weimiId;
	}
	public String getWeimiPw() {
		return weimiPw;
	}
	public void setWeimiPw(String weimiPw) {
		this.weimiPw = weimiPw;
	}
	public String getSmsSign() {
		return smsSign;
	}
	public void setSmsSign(String smsSign) {
		this.smsSign = smsSign;
	}
	public String getConfiguration() {
		return configuration;
	}
	public void setConfiguration(String configuration) {
		this.configuration = configuration;
	}
	
	
}
