package com.norteksoft.mms.base.autoTool.dataTable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import com.norteksoft.mms.form.entity.TableColumn;
import com.norteksoft.mms.form.enumeration.DataType;

public class OneToMany implements AnnotationAnalysis{

	public void getTableColumn(Annotation fieldAnnotation,
			TableColumn tableColumn, Field field) {
		tableColumn.setDbColumnName(null);
		tableColumn.setDataType(DataType.COLLECTION);
	}

}
