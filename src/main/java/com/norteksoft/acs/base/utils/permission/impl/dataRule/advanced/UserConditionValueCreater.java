package com.norteksoft.acs.base.utils.permission.impl.dataRule.advanced;

import java.util.List;

import com.norteksoft.acs.entity.authority.PermissionInfo;
import com.norteksoft.acs.entity.authority.PermissionItem;
import com.norteksoft.product.util.ContextUtils;

public class UserConditionValueCreater implements DataRuleConditionValueSetting{
	public ConditionVlaueInfo getValues(String conditionValue,List<PermissionItem> permissionItems,PermissionInfo permissionInfo) {
		String beanName = conditionValue.replaceFirst("beanName:", "");
		ConditionValueCreater conditionValueCreater = (ConditionValueCreater)ContextUtils.getBean(beanName);
		String values = conditionValueCreater.getValues(permissionInfo.getDataTableName(), permissionInfo.getFieldName());
		return new ConditionVlaueInfo(DataRuleConditionValueType.CUSTOM_VALUE,values);
	}
}
