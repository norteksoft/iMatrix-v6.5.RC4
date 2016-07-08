package com.norteksoft.bs.sms.dao;

import org.springframework.stereotype.Repository;

import com.norteksoft.bs.sms.entity.SmsAuthoritySetting;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.orm.hibernate.HibernateDao;
import com.norteksoft.product.util.ContextUtils;
/**
 * 收发接口设置/短信平台的权限控制/控制哪些模块可以使用短信功能
 * @author c
 *
 */
@Repository
public class SmsAuthoritySettingDao extends HibernateDao<SmsAuthoritySetting,Long>{
	
	public void getAllSmsAuthoritySetting(Page<SmsAuthoritySetting> page,Long systemId) {
		StringBuilder result = new StringBuilder();
		result.append("from SmsAuthoritySetting s where s.companyId = ? and s.systemId = ?");
		this.searchPageByHql(page, result.toString(), ContextUtils.getCompanyId(),systemId);
	}

	/**
	 * 根据接口编号取出网关
	 * @param interCode 接口编号
	 * @return
	 */
	public SmsAuthoritySetting getSmsAuthoritySettingByCode(String interCode) {
		StringBuilder result = new StringBuilder();
		result.append("from SmsAuthoritySetting s where s.companyId = ? and s.interCode = ?");
		return this.findUnique(result.toString(), ContextUtils.getCompanyId(),interCode);
		
	}




	
	
	
}
