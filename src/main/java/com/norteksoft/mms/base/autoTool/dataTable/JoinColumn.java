package com.norteksoft.mms.base.autoTool.dataTable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import com.norteksoft.mms.form.entity.TableColumn;

public class JoinColumn implements AnnotationAnalysis{

	public void getTableColumn(Annotation fieldAnnotation,
			TableColumn tableColumn, Field field) {
		javax.persistence.JoinColumn jc = (javax.persistence.JoinColumn)fieldAnnotation;
		tableColumn.setDbColumnName(jc.name());
	}


}
