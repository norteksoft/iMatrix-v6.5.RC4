package com.norteksoft.bs.sms.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.norteksoft.bs.sms.entity.SmsGatewaySetting;
import com.norteksoft.bs.sms.entity.SmsWaitTosend;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.orm.hibernate.HibernateDao;
import com.norteksoft.product.util.ContextUtils;
/**
 *  短信平台/待发送列表
 * @author c
 *
 */
@Repository
public class SmsWaitTosendDao extends HibernateDao<SmsWaitTosend,Long>{

	public void getAllSmsWaitTosend(Page<SmsWaitTosend> page) {
		StringBuilder result = new StringBuilder();
		result.append("from SmsWaitTosend t where t.companyId = ?");
		this.searchPageByHql(page, result.toString(), ContextUtils.getCompanyId());
	}

	/**
	 * 获得所有的待发送的数据,已发送次数是小于所用网关配置的最大次数
	 * @return
	 */
	public List<SmsWaitTosend> getAllDatas(SmsGatewaySetting smsGatewaySetting) {
		StringBuilder result = new StringBuilder();
		result.append("from SmsWaitTosend t where t.companyId = ? and t.sendTime < ?");
		return this.find( result.toString(), ContextUtils.getCompanyId(),smsGatewaySetting.getMaxTime());
	}
	/**
	 * 获得所有的待发送的数据,已发送次数是小于所用网关配置的最大次数
	 * @return
	 */
	public long getCountAllDatas(SmsGatewaySetting smsGatewaySetting) {
		StringBuilder result = new StringBuilder();
		result.append("select count(1) from SmsWaitTosend t where t.companyId = ? and t.sendTime < ?");
		return this.countHqlResult( result.toString(), ContextUtils.getCompanyId(),smsGatewaySetting.getMaxTime());
		
	}


	
	
	
}
