package com.norteksoft.bs.sms.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.unitils.dbunit.annotation.DataSet;
import org.unitils.spring.annotation.SpringBeanByName;

import com.norteksoft.bs.sms.base.BaseServiceTest;
import com.norteksoft.bs.sms.base.enumeration.ReceiveDispatchType;
import com.norteksoft.bs.sms.entity.SmsAuthoritySetting;
import com.norteksoft.product.util.ParameterUtils;
import com.norteksoft.product.util.ThreadParameters;


public class SmsAuthoritySettingManagerTest extends BaseServiceTest{
	@SpringBeanByName
	private SmsAuthoritySettingManager smsAuthoritySettingManager;
	
	@Before
	public void init(){
		ThreadParameters parameters=new ThreadParameters(null,null);
		parameters.setCompanyId(1L);
		ParameterUtils.setParameters(parameters);
	}

	@Test
	@DataSet("delete-AuthoritySetting.xls")
	public void delete() {
		String ids = "1";
		String flag = smsAuthoritySettingManager.delete(ids);
		assertThat(flag).isEqualTo("不可删除已启用的接口");
		
		ids = "2";
		flag = smsAuthoritySettingManager.delete(ids);
		assertThat(flag).isEqualTo("");
		
		
	}
	
	//验证删除
	@Test
	@DataSet("validate-AuthoritySetting.xls")
	public void validateDelete() {
		String ids = "1";
		String flag = smsAuthoritySettingManager.validateDelete(ids);
		assertThat(flag.equals("不可删除已启用的网关"));
		
		ids = "2";
		flag = smsAuthoritySettingManager.validateDelete(ids);
		assertThat(flag.equals(""));
	}
	
	
	//验证编号的唯一性
	@Test
	@DataSet("validateCode-AuthoritySetting.xls")
	public void validateCode() {
		boolean flag = smsAuthoritySettingManager.validateCode("1005", 3L);
		assertThat(flag).isFalse();
		
	    flag = smsAuthoritySettingManager.validateCode("inter1", 2L);
		assertThat(flag).isTrue();
	}
	
	//验证删除
	@Test
	@DataSet("validateCode-AuthoritySetting.xls")
	public void changeGatewayState() {
		String result = smsAuthoritySettingManager.changeStatus("1");
		assertThat(result).isEqualTo("草稿到启用:0;禁用到启用:0;启用到禁用:1");
		
		result = smsAuthoritySettingManager.changeStatus("2");
		assertThat(result).isEqualTo("草稿到启用:1;禁用到启用:0;启用到禁用:0");
		
		result = smsAuthoritySettingManager.changeStatus("3");
		assertThat(result).isEqualTo("草稿到启用:0;禁用到启用:1;启用到禁用:0");
		
		
	}
	
	//验证删除
	@Test
	@DataSet({"save-AuthoritySetting.xls"})
	public void save() {
		SmsAuthoritySetting smsAuthoritySetting = smsAuthoritySettingManager.getSmsAuthoritySettingById(2L);
		
		smsAuthoritySetting.setType(ReceiveDispatchType.RECEIVE);
		smsAuthoritySettingManager.save(2L, "", smsAuthoritySetting);
		smsAuthoritySetting = smsAuthoritySettingManager.getSmsAuthoritySettingById(2L);
		
		
		assertThat(smsAuthoritySetting.getSystemId()).isEqualTo(2);
		assertThat(smsAuthoritySetting.getSystemCode().equals("sales"));
		assertThat(smsAuthoritySetting.getTemplateCode()).isNull();
	}
	
	
	
}
