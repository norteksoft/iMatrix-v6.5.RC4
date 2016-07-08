package com.norteksoft.mms.base.autoTool.dataTable;

import java.lang.annotation.Annotation;

public class DataTableAnnotationFactory {

	public static AnnotationAnalysis getAnnotationAnalysis(Annotation annotation){
		AnnotationAnalysis annotationAnalysis = null;
		String fnTypeName = annotation.annotationType().getName();
		if("javax.persistence.Transient".equals(fnTypeName)){
			annotationAnalysis = new Transient();
		}else if("javax.persistence.Column".equals(fnTypeName)){
			annotationAnalysis = new Column();
		}else if("javax.persistence.OneToMany".equals(fnTypeName)){
			annotationAnalysis = new OneToMany();
		}else if("javax.persistence.ManyToOne".equals(fnTypeName)||"javax.persistence.OneToOne".equals(fnTypeName)){
			annotationAnalysis = new ManyToOne();
		}else if("javax.persistence.JoinColumn".equals(fnTypeName)){
			annotationAnalysis = new JoinColumn();
		}else if("javax.persistence.Embedded".equals(fnTypeName)){
			annotationAnalysis = new Embedded();
		}else if("com.norteksoft.product.web.struts2.MetaData".equals(fnTypeName)){//自定义注解@MetaData
			annotationAnalysis = new MetaDataAnaly();
		}
		return annotationAnalysis;
	}
}
