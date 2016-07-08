package com.norteksoft.mms.base.autoTool.dataTable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import org.apache.commons.lang.StringUtils;

import com.norteksoft.mms.form.entity.TableColumn;

public class MetaDataAnaly implements AnnotationAnalysis{

	public void getTableColumn(Annotation fieldAnnotation,
			TableColumn tableColumn, Field field) {
		com.norteksoft.product.web.struts2.MetaData metaData = (com.norteksoft.product.web.struts2.MetaData)fieldAnnotation;
		String describe = metaData.describe();
		if(tableColumn!=null&&StringUtils.isNotEmpty(describe)&&StringUtils.isEmpty(tableColumn.getAlias()))tableColumn.setAlias(describe);//如果注解中设置数据表名且数据表别名字段为空
	}

}
