package com.norteksoft.acs.base.utils.permission.impl.dataRule.advanced;

import java.util.List;

import com.norteksoft.acs.entity.authority.PermissionInfo;
import com.norteksoft.acs.entity.authority.PermissionItem;
import com.norteksoft.product.util.ContextUtils;

/**
 * 当前用户正职部门id
 * @author nortek
 *
 */
public class CurrentUserMainDepartment implements DataRuleConditionValueSetting{
	public ConditionVlaueInfo getValues(String conditionValue,
			List<PermissionItem> permissionItems, PermissionInfo permissionInfo) {
		if(ContextUtils.getDepartmentId()==null){
			return new ConditionVlaueInfo(DataRuleConditionValueType.CUSTOM_VALUE,"");
		}else{
			return new ConditionVlaueInfo(DataRuleConditionValueType.CUSTOM_VALUE,ContextUtils.getDepartmentId()+"");
		}
	}
}
