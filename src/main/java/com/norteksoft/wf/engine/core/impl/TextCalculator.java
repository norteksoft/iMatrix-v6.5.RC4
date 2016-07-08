package com.norteksoft.wf.engine.core.impl;

import org.apache.commons.lang.StringUtils;

import com.norteksoft.wf.base.enumeration.TextOperator;
import com.norteksoft.wf.engine.core.Computable;

/**
 * 文本相等、不等、包含、不包含的运算器
 */
public class TextCalculator implements Computable {

	public Boolean execute(String subExpression) {
		String operatorInfo = subExpression.substring(subExpression.lastIndexOf("(")+1);
		String operator = operatorInfo.substring(0,operatorInfo.indexOf("\'")).trim() ;
		String rightOperand = operatorInfo.substring(operatorInfo.indexOf("\'")+1,operatorInfo.lastIndexOf("\'"));
		String leftOperand = subExpression.substring(0,subExpression.lastIndexOf("(")).trim();
		if(StringUtils.contains(operator, TextOperator.NET.getCode())){
			return !leftOperand.equals(rightOperand);
		}else if(StringUtils.contains(operator, TextOperator.ET.getCode())){
			return leftOperand.equals(rightOperand);
		}else if(StringUtils.contains(operator, TextOperator.NOT_CONTAINS.getCode())){
			return !StringUtils.contains(leftOperand, rightOperand);
		}else if(StringUtils.contains(operator, TextOperator.CONTAINS.getCode())){
			return StringUtils.contains(leftOperand, rightOperand);
		}
		throw new RuntimeException(subExpression + " is invalid expression.");
	}

}
