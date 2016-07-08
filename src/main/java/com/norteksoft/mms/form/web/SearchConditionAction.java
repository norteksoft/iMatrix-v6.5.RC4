package com.norteksoft.mms.form.web;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;

import com.norteksoft.mms.form.entity.ListColumn;
import com.norteksoft.mms.form.entity.SearchCondition;
import com.norteksoft.mms.form.entity.SearchConditionValue;
import com.norteksoft.mms.form.enumeration.QueryType;
import com.norteksoft.mms.form.service.ListColumnManager;
import com.norteksoft.mms.form.service.SearchConditionManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.web.struts2.CrudActionSupport;
import com.norteksoft.product.web.struts2.Struts2Utils;


/**
 * 查询条件
 * @author admin
 *
 */
@Namespace("/form")
@ParentPackage("default")
@Results( { @Result(name = CrudActionSupport.RELOAD, location = "search-condition", type = "redirectAction") })
public class SearchConditionAction extends CrudActionSupport<SearchCondition> {
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String listCode;//列表编号
	private SearchCondition searchCondition;//查询条件
	private String conditionValue;//条件值
	private List<SearchCondition> searchConditionList=new ArrayList<SearchCondition>();
	private QueryType queryType;
	
	@Autowired
	private SearchConditionManager searchConditionManager;
	@Autowired
	private ListColumnManager listColumnManager;

	@Override
	@Action("search-condition-delete")
	public String delete() throws Exception {
		searchConditionManager.deleteSearchCondition(id);
		String callback=Struts2Utils.getParameter("callback");
		this.renderText(callback+"({data:'ok'})");
		return null;
	}

	@Override
	@Action("search-condition-input")
	public String input() throws Exception {
		List<SearchConditionValue> searchConditionValueList=searchConditionManager.getSearchConditionValueList(id);
		StringBuilder str=new StringBuilder();
		List<ListColumn> listColumns=listColumnManager.getQueryColumnsByCode(listCode);
//		ThreadParameters parameters = new ThreadParameters(1l);
//		ParameterUtils.setParameters(parameters);
		for(SearchConditionValue scv:searchConditionValueList){
			if(StringUtils.isNotEmpty(str.toString()))str.append(",");
			str.append("{");
			str.append("\"leftBracket\":\"").append(StringUtils.isNotEmpty(scv.getLeftBracket())?scv.getLeftBracket():"").append("\",");
			str.append("\"propName\":\"").append(StringUtils.isNotEmpty(scv.getFieldName())?scv.getFieldName():"").append("\",");
			str.append("\"dbName\":\"").append(StringUtils.isNotEmpty(scv.getDbFieldName())?scv.getDbFieldName():"").append("\",");
			str.append("\"propValue\":\"").append(StringUtils.isNotEmpty(scv.getConditionValue())?scv.getConditionValue():"").append("\",");
			str.append("\"rightBracket\":\"").append(StringUtils.isNotEmpty(scv.getRightBracket())?scv.getRightBracket():"").append("\",");
			str.append("\"optSign\":\"").append(StringUtils.isNotEmpty(scv.getFieldOperator())?scv.getFieldOperator():"").append("\",");
			str.append("\"dataType\":\"").append(StringUtils.isNotEmpty(scv.getDataType())?scv.getDataType():"").append("\",");
			str.append("\"position\":\"").append(StringUtils.isNotEmpty(scv.getPosition())?scv.getPosition():"").append("\",");
			str.append("\"enumName\":\"").append(StringUtils.isNotEmpty(scv.getEnumName())?scv.getEnumName():"").append("\",");
			String fieldAlias=getFieldAlias(StringUtils.isNotEmpty(scv.getFieldName())?scv.getFieldName():"",StringUtils.isNotEmpty(scv.getDbFieldName())?scv.getDbFieldName():"",listColumns);
			str.append("\"fieldAlias\":\"").append(fieldAlias).append("\",");
			str.append("\"conditionName\":\"").append(StringUtils.isNotEmpty(scv.getConditionName())?scv.getConditionName():"").append("\",");
			str.append("\"joinSign\":\"").append(StringUtils.isNotEmpty(scv.getLogicOperator())?scv.getLogicOperator():"").append("\"");
			str.append("}");
		}
		String callback=Struts2Utils.getParameter("callback");
		if(StringUtils.isNotEmpty(str.toString())){
			this.renderText(callback+"({data:'["+str.toString()+"]'})");
		}else{
			this.renderText(callback+"({data:''})");
		}
		return null;
	}

	private String getFieldAlias(String fieldName,String dbName, List<ListColumn> listColumns) {
		String name="";
		for(ListColumn listColumn:listColumns){
			if(fieldName.equals(listColumn.getTableColumn().getName())||fieldName.equals(listColumn.getTableColumn().getDbColumnName())){
				name=getInternation(listColumn.getHeaderName());
				break;
			}
		}
		return name;
	}
	 public String getInternation(String code){
		 return ApiFactory.getSettingService().getText(code);
	 }

	@Override
	@Action("search-condition-listPage")
	public String list() throws Exception {
		searchConditionList=searchConditionManager.getSearchConditionList(listCode,queryType);
		StringBuilder sb=new StringBuilder();
		if(searchConditionList!=null&&searchConditionList.size()>0){
			for(SearchCondition sc:searchConditionList){
				if(StringUtils.isNotEmpty(sb.toString())){
					sb.append(",");
				}
				sb.append("{");
				sb.append("id:\"").append(sc.getId()).append("\",");
				sb.append("name:\"").append(sc.getName()).append("\"");
				sb.append("}");
			}
		}
		String callback=Struts2Utils.getParameter("callback");
		if(StringUtils.isNotEmpty(sb.toString())){
			this.renderText(callback+"({data:'["+sb.toString()+"]'})");
		}else{
			this.renderText(callback+"({data:''})");
		}
		return null;
	}

	@Override
	protected void prepareModel() throws Exception {
		if(id==null){
			searchCondition=new SearchCondition();
			searchCondition.setCompanyId(ContextUtils.getCompanyId());
			searchCondition.setUserId(ContextUtils.getUserId());
		}
	}

	@Override
	@Action("search-condition-save")
	public String save() throws Exception {
		searchConditionManager.saveSearchCondition(searchCondition,conditionValue);
		String callback=Struts2Utils.getParameter("callback");
		this.renderText(callback+"({id:'"+searchCondition.getId().toString()+"'})");
		return null;
	}
	
	@Action("search-condition-savePage")
	public String savePage() throws Exception {
		return "search-condition-savePage";
	}

	public SearchCondition getModel() {
		return searchCondition;
	}

	public String getListCode() {
		return listCode;
	}

	public void setListCode(String listCode) {
		this.listCode = listCode;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getConditionValue() {
		return conditionValue;
	}

	public void setConditionValue(String conditionValue) {
		this.conditionValue = conditionValue;
	}

	public List<SearchCondition> getSearchConditionList() {
		return searchConditionList;
	}

	public void setSearchConditionList(List<SearchCondition> searchConditionList) {
		this.searchConditionList = searchConditionList;
	}

	public QueryType getQueryType() {
		return queryType;
	}

	public void setQueryType(QueryType queryType) {
		this.queryType = queryType;
	}
}
