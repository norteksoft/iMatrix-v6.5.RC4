package com.norteksoft.acs.base.utils.permission.impl.dataRule.advanced;

import java.util.List;

import com.norteksoft.acs.base.utils.PermissionUtils;
import com.norteksoft.acs.entity.authority.PermissionInfo;
import com.norteksoft.acs.entity.authority.PermissionItem;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.entity.Role;

/**
 * 当前用户角色名称
 * @author nortek
 *
 */
public class CurrentUserRoleName implements DataRuleConditionValueSetting{
	/**
	 * 数据分类中条件值中的标准值:当前用户角色名称
	 * @author Administrator
	 *
	 */
	public ConditionVlaueInfo getValues(String conditionValue,List<PermissionItem> permissionItems,PermissionInfo permissionInfo) {
		String value="";
		Long userId = permissionInfo.getUserId();
		List<Role> roles = ApiFactory.getAcsService().getRolesExcludeTrustedRole(userId);
		 for (Role r : roles) {
			 if(!PermissionUtils.isCommonUser(permissionInfo.getSystemCodes(), r.getCode())){//如果当前角色不是系统默认的普通用户角色
				 value = value+ r.getName()+",";
			 }
		}
		if(value.indexOf(",")>=0)value = value.substring(0,value.lastIndexOf(","));
		return new ConditionVlaueInfo(DataRuleConditionValueType.CUSTOM_VALUE,value);
	}
}
