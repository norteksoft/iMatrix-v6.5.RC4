package com.norteksoft.acs.base.utils.permission.impl.dataRule.advanced;

import java.util.List;

import com.norteksoft.acs.entity.authority.PermissionInfo;
import com.norteksoft.acs.entity.authority.PermissionItem;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.entity.Department;
import com.norteksoft.product.util.ContextUtils;

/**
 * 当前用户正职部门名称
 * @author nortek
 *
 */
public class CurrentUserMainDepartmentName implements DataRuleConditionValueSetting{
	public ConditionVlaueInfo getValues(String conditionValue,
			List<PermissionItem> permissionItems, PermissionInfo permissionInfo) {
		if(ContextUtils.getDepartmentId()==null){
			return new ConditionVlaueInfo(DataRuleConditionValueType.CUSTOM_VALUE,"");
		}else{
			Department department = ApiFactory.getAcsService().getDepartmentById(ContextUtils.getDepartmentId());
			return new ConditionVlaueInfo(DataRuleConditionValueType.CUSTOM_VALUE,department.getName());
		}
	}
}
