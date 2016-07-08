package com.norteksoft.acs.base.utils.permission.impl.dataRule.advanced;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.norteksoft.acs.entity.authority.PermissionInfo;
import com.norteksoft.acs.entity.authority.PermissionItem;
import com.norteksoft.product.util.ContextUtils;
/**
 * 当前用户名
 * @author nortek
 *
 */
public class CurrentUserName implements DataRuleConditionValueSetting{

	public ConditionVlaueInfo getValues(String conditionValue,
			List<PermissionItem> permissionItems, PermissionInfo permissionInfo) {
		if(StringUtils.isEmpty(ContextUtils.getUserName())){
			return new ConditionVlaueInfo(DataRuleConditionValueType.CUSTOM_VALUE,"");
		}else{
			return new ConditionVlaueInfo(DataRuleConditionValueType.CUSTOM_VALUE,ContextUtils.getUserName());
		}
	}

}
