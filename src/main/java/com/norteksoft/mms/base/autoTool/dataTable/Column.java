package com.norteksoft.mms.base.autoTool.dataTable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import org.apache.commons.lang.StringUtils;

import com.norteksoft.mms.form.entity.TableColumn;
import com.norteksoft.mms.form.enumeration.DataType;

public class Column implements AnnotationAnalysis{

	public void getTableColumn(Annotation fieldAnnotation,TableColumn tableColumn,Field field) {
		javax.persistence.Column c = (javax.persistence.Column)fieldAnnotation;
		DataType dataType = null;
		String columnDbName = null;
		String columnDefinition = StringUtils.upperCase(c.columnDefinition());
		if("java.lang.String".equals(field.getType().getName())){
			if("LONGTEXT".equals(columnDefinition)||"CLOB".equals(columnDefinition)||"NTEXT".equals(columnDefinition)){//分别表示不同数据库中大文本的注解写法
				dataType = DataType.CLOB;
			}
		}else if("[B".equals(field.getType().getName())){
			if("LONGBLOB".equals(columnDefinition)||"BLOB".equals(columnDefinition)||"IMAGE".equals(columnDefinition)){//分别表示不同数据库中大文本的注解写法
				dataType = DataType.BLOB;
			}
		}
		if(StringUtils.isNotEmpty(c.name())){//@Column注解的name属性
			columnDbName = c.name();
		}
		if(dataType!=null){
			tableColumn.setDataType(dataType);
		}
		if(columnDbName!=null){
			tableColumn.setDbColumnName(columnDbName);
		}
		
	}

}
