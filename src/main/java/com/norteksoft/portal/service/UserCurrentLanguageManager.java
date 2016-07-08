package com.norteksoft.portal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.portal.dao.UserCurrentLanguageDao;
import com.norteksoft.portal.entity.UserCurrentLanguage;


@Service
@Transactional
public class UserCurrentLanguageManager   {
	@Autowired
    private UserCurrentLanguageDao userCurrentLanguageDao;
	/**
	 * 获取某用户设置的语言
	 * @return
	 */
	public String getUserLanguageByUserId(Long userId, Long companyId){
		UserCurrentLanguage language = userCurrentLanguageDao.getUserCurrentLanguage(userId, companyId);
		if(language == null){
			return "";
		}else{
			return language.getCurrentLanguage();
		}
	}
	/**
	 * 获取某用户设置的语言实体
	 * @return
	 */
	public UserCurrentLanguage getUserLanguageByUid(Long userId, Long companyId){
		UserCurrentLanguage userLanguage = userCurrentLanguageDao.getUserCurrentLanguage(userId, companyId);
		return userLanguage;
	}
	public void save(UserCurrentLanguage language) {
		userCurrentLanguageDao.save(language);
	}
}
