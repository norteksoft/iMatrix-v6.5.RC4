package com.norteksoft.bs.sms.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.norteksoft.bs.sms.base.enumeration.GatewayStatus;
import com.norteksoft.bs.sms.base.enumeration.SendReceiveStatus;
import com.norteksoft.bs.sms.entity.SmsGatewaySetting;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.orm.hibernate.HibernateDao;
import com.norteksoft.product.util.ContextUtils;
/**
 * 短信网关设置
 * @author lenove1
 *
 */
@Repository
public class SmsGatewaySettingDao extends HibernateDao<SmsGatewaySetting,Long>{

	/**
	 * 短信网关设置/列表
	 * @author c
	 */
	public void getAllSmsGatewaySetting(Page<SmsGatewaySetting> page) {
		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append("from SmsGatewaySetting s where  s.companyId=?");
		this.searchPageByHql(page, sBuilder.toString(), ContextUtils.getCompanyId());
	}
	/**
	 * 根据网关编号得到某网关
	 * @param gatewayCode
	 * @return
	 */
	public SmsGatewaySetting getSmsGatewaySetByCode(String gatewayCode) {
		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append("from SmsGatewaySetting s where  s.companyId=? and s.gatewayCode = ?");
		return this.findUnique( sBuilder.toString(), ContextUtils.getCompanyId(),gatewayCode);
	}

	/**
	 * 短信网关设置/取出所有启用的,可以发送短信的网关
	 * @author c
	 */
	public List<SmsGatewaySetting> getAllSmsGatewaySetting() {
		List<SmsGatewaySetting> settings = new ArrayList<SmsGatewaySetting>();
		StringBuilder sb = new StringBuilder();
		sb.append("from SmsGatewaySetting s where s.companyId=? and s.gatewayStatus=? and s.sendReceiveStatus !=?");
		settings = this.find(sb.toString(),ContextUtils.getCompanyId(),
								GatewayStatus.ENABLE,SendReceiveStatus.OBLYRECEIVE);
		return settings;
	}
	
	
	
}
