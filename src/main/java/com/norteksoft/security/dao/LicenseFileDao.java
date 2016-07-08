package com.norteksoft.security.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.norteksoft.product.orm.hibernate.HibernateDao;
import com.norteksoft.security.entity.LicenseFile;

/**
 * License管理
 * @author nortek
 *
 */
@Repository
public class LicenseFileDao extends HibernateDao<LicenseFile, Long>{
	public List<LicenseFile> getAllLicenseFile() {
		return this.findNoCompanyCondition("from LicenseFile t order by t.id desc");
	}
	
}
