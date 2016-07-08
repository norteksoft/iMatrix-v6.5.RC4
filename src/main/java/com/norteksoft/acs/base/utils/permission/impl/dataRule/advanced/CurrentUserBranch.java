package com.norteksoft.acs.base.utils.permission.impl.dataRule.advanced;

import java.util.List;


import com.norteksoft.acs.entity.authority.PermissionInfo;

import com.norteksoft.acs.entity.authority.PermissionItem;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.entity.User;

/**
 * 当前用户所在分支的相关处理
 * @author Administrator
 *
 */
public class CurrentUserBranch implements DataRuleConditionValueSetting{
	/**
	 * 数据分类中条件值中的标准值:当前用户的分支
	 * @author Administrator
	 *
	 */
	public ConditionVlaueInfo getValues(String conditionValue,List<PermissionItem> permissionItems,PermissionInfo permissionInfo) {
		String value="";
		Long userId = permissionInfo.getUserId();
		User user = ApiFactory.getAcsService().getUserById(userId);
		 if(user!=null){
			 if(user.getSubCompanyId()!=null){
				 value = user.getSubCompanyId()+"";
			 }else{
				 value = "company-"+user.getCompanyId();
			 }
		 }
		return new ConditionVlaueInfo(DataRuleConditionValueType.STANDARD_VALUE,value);
	}

}
