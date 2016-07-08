package com.norteksoft.acs.base.utils.permission.impl.dataRule.advanced;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.norteksoft.acs.entity.authority.PermissionInfo;

import com.norteksoft.acs.entity.authority.PermissionItem;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.entity.User;

/**
 * 数据分类中条件值中的标准值:当前用户直属下级的分支
 * @author Administrator
 *
 */
public class CurrentUserDirectLowerBranch implements DataRuleConditionValueSetting{

	public ConditionVlaueInfo getValues(String conditionValue,List<PermissionItem> permissionItems,PermissionInfo permissionInfo) {
		String value="";
		Long userId =permissionInfo.getUserId();
		List<User> users = ApiFactory.getDataDictService().getDirectLower(userId);
		Set<String> result = new HashSet<String>();//分支id集合
		for (User u : users) {
			if(u.getSubCompanyId()!=null){
				//去掉重复
				result.add(u.getSubCompanyId()+"");
			}else{
				result.add("company-"+u.getCompanyId());
			}
		}
		if(result.size()>0){
			value = result.toString().replace("[", "").replace("]", "").replace(" ", "");
		}
		return new ConditionVlaueInfo(DataRuleConditionValueType.STANDARD_VALUE,value);
	}


}
