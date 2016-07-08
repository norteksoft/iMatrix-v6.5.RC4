package com.norteksoft.bs.options.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.norteksoft.bs.options.entity.DatasourceSetting;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.orm.hibernate.HibernateDao;
import com.norteksoft.product.util.ContextUtils;

/**
 * 数据源配置
 * @author Administrator
 *
 */
@Repository
public class DatasourceSettingDao extends HibernateDao<DatasourceSetting, Long> {
	
	public DatasourceSetting getDatasourceSettingById(Long id){
		List<DatasourceSetting> datasources =  this.find("from DatasourceSetting i where i.companyId=? and i.id=? ",ContextUtils.getCompanyId(),id);
		if(datasources.size()>0){
			return datasources.get(0);
		}
		return null;
	}

	/**
	 * 获得所有的数据源配置
	 * @param page
	 */
	public void getDatasourceSettingPage(Page<DatasourceSetting> page) {
		this.searchPageByHql(page, "from DatasourceSetting i where i.companyId=? ", ContextUtils.getCompanyId());
	}

	/**
	 * 根据编号获得数据源配置
	 * @param code
	 * @return
	 */
	public DatasourceSetting getDatasourceSettingByCode(String code) {
		return this.findUnique("from DatasourceSetting i where i.companyId=? and i.code=? ",ContextUtils.getCompanyId(),code);
	}
	/**
	 * 获得所有数据源配置
	 * @return
	 */
	public List<DatasourceSetting> getAllDatasourceSettings(){
		return this.find("from DatasourceSetting i where i.companyId=? ",ContextUtils.getCompanyId());
	}
	
}
