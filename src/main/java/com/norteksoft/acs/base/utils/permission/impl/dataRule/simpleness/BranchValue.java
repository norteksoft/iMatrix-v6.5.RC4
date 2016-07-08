package com.norteksoft.acs.base.utils.permission.impl.dataRule.simpleness;

import java.util.List;

import com.norteksoft.acs.entity.authority.PermissionInfo;
import com.norteksoft.acs.entity.authority.PermissionItem;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.entity.User;
import com.norteksoft.product.util.ContextUtils;

/**
 * 数据分类的简易设置:本分支机构所有数据
 * @author Administrator
 *
 */
public class BranchValue implements DataRangeSetting {
	public String getValues(List<PermissionItem> permissionItems,PermissionInfo permissionInfo) {
		Long userId = ContextUtils.getUserId();
		if(userId==null){
			return "";
		}else{
			User user = ApiFactory.getAcsService().getUserById(userId);
			if(user!=null&&user.getSubCompanyId()!=null){
				return user.getSubCompanyId()+"";
			}
			return "";
		}
	}

}
