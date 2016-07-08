package com.norteksoft.bs.sms.dao;

import org.springframework.stereotype.Repository;

import com.norteksoft.bs.sms.entity.SmsLog;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.orm.hibernate.HibernateDao;
import com.norteksoft.product.util.ContextUtils;
/**
 * 短信平台/短信日志/发送日志以及接收日志
 * @author c
 *
 */
@Repository
public class SmsLogDao extends HibernateDao<SmsLog,Long>{
	
	/**
	 * 短信平台/短信日志/列表
	 * @param logType
	 * @return
	 */
	public void getAllSmsLog(Page<SmsLog> page, String logType) {
		StringBuilder result = new StringBuilder();
		result.append("from SmsLog  s where s.companyId = ? and s.logType = ?");
		this.searchPageByHql(page, result.toString(), ContextUtils.getCompanyId(),logType);
	}
	




	
	
	
}
