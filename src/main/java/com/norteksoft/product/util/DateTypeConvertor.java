package com.norteksoft.product.util;

import java.lang.reflect.Member;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.norteksoft.mms.form.entity.DataTable;
import com.norteksoft.mms.form.entity.TableColumn;
import com.norteksoft.mms.form.enumeration.DataType;
import com.norteksoft.mms.form.service.DataTableManager;
import com.norteksoft.mms.form.service.TableColumnManager;
import com.opensymphony.xwork2.conversion.impl.DefaultTypeConverter;




public class DateTypeConvertor extends DefaultTypeConverter {

	@Override
	public Object convertValue(Map<String, Object> context, Object target,
			Member member, String propertyName, Object value, Class toType) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat sdfTime = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			if(toType==Date.class){
				DataType dataType = DataType.DATE;
				String[] params = (String[])value;
				try {
					String val = params[0];
					if(StringUtils.isNotEmpty(val)){
						if(val.contains(":")){//表示是有时间
							dataType = DataType.TIME;
							if(target!=null&&target.getClass()!=null){
								DataTableManager dataTableManager = (DataTableManager)ContextUtils.getBean("dataTableManager");
								TableColumnManager tableColumnManager = (TableColumnManager)ContextUtils.getBean("tableColumnManager");
								String className = target.getClass().getName();
								if(className.indexOf("_$$")>0){
									className = className.substring(0, className.indexOf("_$$"));
								}
								DataTable table = dataTableManager.getDataTableByEntity(className);
								if(table!=null){
									TableColumn column = tableColumnManager.getTableColumnByColName(table.getId(), propertyName);
									if(column!=null&&(column.getDataType()==DataType.DATE||column.getDataType()==DataType.TIME)){
										dataType = column.getDataType();
									}
								}
							}
						}else{//表示只有日期
							dataType = DataType.DATE;
						}
						if(dataType==DataType.TIME){
							return sdfTime.parseObject(val);
						}else{
							return sdf.parseObject(val);
						}
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		    return null;
	}
	
	
}
