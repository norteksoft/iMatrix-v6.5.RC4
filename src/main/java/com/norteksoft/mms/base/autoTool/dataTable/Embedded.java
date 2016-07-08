package com.norteksoft.mms.base.autoTool.dataTable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import com.norteksoft.mms.form.entity.TableColumn;

public class Embedded implements AnnotationAnalysis{


	/**
	 * 说明：@Embedded注解
	 */
	public void getTableColumn(Annotation fieldAnnotation,
			TableColumn tableColumn, Field field) {
		tableColumn=null;
		
	}

}
