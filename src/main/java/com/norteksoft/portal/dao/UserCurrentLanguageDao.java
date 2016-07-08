package com.norteksoft.portal.dao;

import org.springframework.stereotype.Repository;

import com.norteksoft.portal.entity.UserCurrentLanguage;
import com.norteksoft.product.orm.hibernate.HibernateDao;

@Repository
public class UserCurrentLanguageDao extends HibernateDao<UserCurrentLanguage, Long>{

	
	public UserCurrentLanguage getUserCurrentLanguage(Long userId, Long companyId){
		return this.findUnique("from UserCurrentLanguage t where t.userId=? and t.companyId=?",userId,companyId);
	}
	
}
