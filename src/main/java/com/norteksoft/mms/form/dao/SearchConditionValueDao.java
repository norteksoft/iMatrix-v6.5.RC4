package com.norteksoft.mms.form.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.norteksoft.mms.form.entity.SearchConditionValue;
import com.norteksoft.product.orm.hibernate.HibernateDao;

/**
 * 查询条件值
 * @author admin
 *
 */
@Repository
public class SearchConditionValueDao extends HibernateDao<SearchConditionValue, Long> {

	public void deleteValueBySearchConditionId(Long searchConditionId) {
		this.batchExecute("delete from SearchConditionValue scv where scv.searchConditionId=?", searchConditionId);
	}

	public List<SearchConditionValue> getSearchConditionValueList(Long searchConditionId) {
		return this.findNoCompanyCondition("from SearchConditionValue scv where scv.searchConditionId=? order by scv.displayIndex asc", searchConditionId);
	}

}
