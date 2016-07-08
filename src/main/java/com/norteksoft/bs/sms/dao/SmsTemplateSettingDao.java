package com.norteksoft.bs.sms.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.norteksoft.bs.sms.entity.SmsTemplateSetting;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.orm.hibernate.HibernateDao;
import com.norteksoft.product.util.ContextUtils;
/**
 * 短信网关设置
 * @author lenove1
 *
 */
@Repository
public class SmsTemplateSettingDao extends HibernateDao<SmsTemplateSetting,Long>{

	public void getAllSmsTemplateSetting(Page<SmsTemplateSetting> page) {
		StringBuilder result = new StringBuilder();
		result.append("from SmsTemplateSetting t where t.companyId = ?");
		this.searchPageByHql(page, result.toString(), ContextUtils.getCompanyId());
	}
	/**
	 * 根据模版编号获得模版
	 * @param templateCode
	 * @return
	 */
	public SmsTemplateSetting getSmsTemplateSetByCode(String templateCode) {
		StringBuilder result = new StringBuilder();
		result.append("from SmsTemplateSetting t where t.companyId = ? and t.templateCode = ?");
		return this.findUnique(result.toString(), ContextUtils.getCompanyId(),templateCode);
	}
	public List<SmsTemplateSetting> getAllSmsTemplateSetting() {
		StringBuilder result = new StringBuilder();
		result.append("from SmsTemplateSetting t where t.companyId = ?");
		List<SmsTemplateSetting> list = this.find(result.toString(), ContextUtils.getCompanyId());
		return list;
	}




	
	
	
}
