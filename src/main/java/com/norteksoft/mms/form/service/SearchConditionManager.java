package com.norteksoft.mms.form.service;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.acs.base.enumeration.FieldOperator;
import com.norteksoft.acs.base.enumeration.LeftBracket;
import com.norteksoft.acs.base.enumeration.LogicOperator;
import com.norteksoft.acs.base.enumeration.RightBracket;
import com.norteksoft.mms.form.dao.SearchConditionDao;
import com.norteksoft.mms.form.dao.SearchConditionValueDao;
import com.norteksoft.mms.form.entity.SearchCondition;
import com.norteksoft.mms.form.entity.SearchConditionValue;
import com.norteksoft.mms.form.enumeration.DataType;
import com.norteksoft.mms.form.enumeration.QueryType;
import com.norteksoft.product.util.ContextUtils;

/**
 * 查询条件
 * @author admin
 *
 */
@Service
@Transactional
public class SearchConditionManager {
	@Autowired
	private SearchConditionDao searchConditionDao;
	@Autowired
	private SearchConditionValueDao searchConditionValueDao;

	public void saveSearchCondition(SearchCondition searchCondition,String conditionValue) {
		searchConditionDao.save(searchCondition);
		saveSearchConditionValues(searchCondition.getId(),conditionValue);
	}
	private void saveSearchConditionValues(Long searchConditionId,String conditionValue) {
		try {
			JSONArray jsons=JSONArray.fromObject(conditionValue);
			Iterator<JSONObject> it=jsons.iterator();
			int i=0;
			while(it.hasNext()){
				i++;
				JSONObject obj=it.next();
				saveSearchConditionValue(searchConditionId,obj,i);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	private void saveSearchConditionValue(Long searchConditionId,JSONObject conditionValue,Integer displayIndex) {
		SearchConditionValue searchConditionValue=new SearchConditionValue();
		String leftBracket=conditionValue.get("leftBracket").toString();
		searchConditionValue.setLeftBracket(leftBracket);
		String fieldName=conditionValue.get("propName").toString();
		searchConditionValue.setFieldName(fieldName);
		String dbFieldName=conditionValue.get("dbName").toString();
		searchConditionValue.setDbFieldName(dbFieldName);
		String value=conditionValue.get("propValue").toString();
		searchConditionValue.setConditionValue(value);
		String rightBracket=conditionValue.get("rightBracket").toString();
		searchConditionValue.setRightBracket(rightBracket);
		String fieldOperator=conditionValue.get("optSign").toString();
		searchConditionValue.setFieldOperator(fieldOperator);
		String dataType=conditionValue.get("dataType").toString();
		searchConditionValue.setDataType(dataType);
		Object pos = conditionValue.get("position");
		if(pos!=null){//只有是时间或日期类型时，该属性才有值，记录是开始时间还是结束时间
			String position=pos.toString();
			searchConditionValue.setPosition(position);
		}
		String logicOperator=conditionValue.get("joinSign").toString();
		searchConditionValue.setLogicOperator(logicOperator);
		String enumName=conditionValue.get("enumName").toString();
		searchConditionValue.setEnumName(enumName);
		String conditionName=conditionValue.get("conditionName").toString();
		searchConditionValue.setConditionName(conditionName);
		searchConditionValue.setDisplayIndex(displayIndex);
		searchConditionValue.setSearchConditionId(searchConditionId);
		searchConditionValue.setCompanyId(ContextUtils.getCompanyId());
		searchConditionValue.setCreatorId(ContextUtils.getUserId());
		searchConditionValueDao.save(searchConditionValue);
	}
	
	public List<SearchCondition> getSearchConditionList(String listCode,QueryType queryType) {
		return searchConditionDao.getSearchConditionList(listCode,queryType);
	}
	
	public void deleteSearchCondition(Long searchConditionId) {
		searchConditionValueDao.deleteValueBySearchConditionId(searchConditionId);
		searchConditionDao.delete(searchConditionId);
	}
	
	public List<SearchConditionValue> getSearchConditionValueList(Long searchConditionId) {
		return searchConditionValueDao.getSearchConditionValueList(searchConditionId);
	}

}
