package com.norteksoft.mms.base.autoTool.dataTable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import com.norteksoft.mms.form.entity.TableColumn;

public interface AnnotationAnalysis {

	/**
	 * ���ע������ݱ��ֶ�
	 * @param annotation
	 * @return
	 */
	public void getTableColumn(Annotation fieldAnnotation,TableColumn tableColumn,Field field);
}
