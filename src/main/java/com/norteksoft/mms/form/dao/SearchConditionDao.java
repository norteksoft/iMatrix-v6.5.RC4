package com.norteksoft.mms.form.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.norteksoft.mms.form.entity.SearchCondition;
import com.norteksoft.mms.form.enumeration.QueryType;
import com.norteksoft.product.orm.hibernate.HibernateDao;
import com.norteksoft.product.util.ContextUtils;

/**
 * 查询条件
 * @author admin
 *
 */
@Repository
public class SearchConditionDao extends HibernateDao<SearchCondition, Long> {

	public List<SearchCondition> getSearchConditionList(String listCode,QueryType queryType) {
		return this.findNoCompanyCondition("from SearchCondition sc where sc.companyId=? and sc.userId=? and sc.listCode=? and sc.queryType=?", ContextUtils.getCompanyId(),ContextUtils.getUserId(),listCode,queryType);
	}

}
