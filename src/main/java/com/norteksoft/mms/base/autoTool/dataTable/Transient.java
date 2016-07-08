package com.norteksoft.mms.base.autoTool.dataTable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import com.norteksoft.mms.form.entity.TableColumn;

public class Transient implements AnnotationAnalysis{
	/**
	 * 说明@Transient注解的属性不存入数据库
	 */
	public void getTableColumn(Annotation fieldAnnotation,
			TableColumn tableColumn, Field field) {
		tableColumn=null;
	}

}
