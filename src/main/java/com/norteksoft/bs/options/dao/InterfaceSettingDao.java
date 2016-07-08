package com.norteksoft.bs.options.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.norteksoft.bs.options.entity.InterfaceSetting;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.orm.hibernate.HibernateDao;
import com.norteksoft.product.util.ContextUtils;

/**
 * 数据源配置
 * @author Administrator
 *
 */
@Repository
public class InterfaceSettingDao extends HibernateDao<InterfaceSetting, Long> {
	
	public InterfaceSetting getInterfaceSettingById(Long id){
		List<InterfaceSetting> datasources =  this.find("from InterfaceSetting i where i.companyId=? and i.id=? ",ContextUtils.getCompanyId(),id);
		if(datasources.size()>0){
			return datasources.get(0);
		}
		return null;
	}

	/**
	 * 获得所有的数据源配置
	 * @param page
	 */
	public void getInterfaceSettingPage(Page<InterfaceSetting> page) {
		this.searchPageByHql(page, "from InterfaceSetting i where i.companyId=? ", ContextUtils.getCompanyId());
	}

	/**
	 * 根据编号获得数据源配置
	 * @param code
	 * @return
	 */
	public InterfaceSetting getInterfaceSettingByCode(String code) {
		return this.findUnique("from InterfaceSetting i where i.companyId=? and i.code=? ",ContextUtils.getCompanyId(),code);
	}
	/**
	 * 根据数据源获得接口集合
	 * @param datasourceId
	 * @return
	 */
	public List<String> getInterfaceSettingByDatasource(Long datasourceId) {
		return this.find("select i.code from InterfaceSetting i where i.companyId=? and i.dataSourceId=? ",ContextUtils.getCompanyId(),datasourceId);
	}
	
}
