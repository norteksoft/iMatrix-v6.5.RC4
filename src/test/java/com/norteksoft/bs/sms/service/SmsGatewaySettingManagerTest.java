package com.norteksoft.bs.sms.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.unitils.dbunit.annotation.DataSet;
import org.unitils.spring.annotation.SpringBeanByName;

import com.norteksoft.bs.sms.base.BaseServiceTest;
import com.norteksoft.bs.sms.entity.SmsGatewaySetting;
import com.norteksoft.product.util.ParameterUtils;
import com.norteksoft.product.util.ThreadParameters;


public class SmsGatewaySettingManagerTest extends BaseServiceTest{
	@SpringBeanByName
	private SmsGatewaySettingManager smsGatewaySettingManager;
	
	@Before
	public void init(){
		ThreadParameters parameters=new ThreadParameters(null,null);
		parameters.setCompanyId(1L);
		ParameterUtils.setParameters(parameters);
	}

	@Test
	@DataSet("empty-SmsGatewaySetting.xls")
	public void save() {
		SmsGatewaySetting smsGatewaySetting = new SmsGatewaySetting();
		smsGatewaySetting.setGatewayCode("1005");
		smsGatewaySetting.setGatewayName("测试的网关");
		smsGatewaySettingManager.save(smsGatewaySetting);
		Long orderId=jdbcTemplate.queryForLong("select id from bs_sms_gateway_setting where gateway_code = ?"
	    		,new Object[]{"1005"});
		assertThat(orderId).isNotNull();
	}
	//验证删除
	@Test
	@DataSet("validateDelete.xls")
	public void validateDelete() {
		String ids = "1";
		String flag = smsGatewaySettingManager.validateDelete(ids);
		assertThat(flag.equals("不可删除已启用的网关"));
		
		ids = "2";
		flag = smsGatewaySettingManager.validateDelete(ids);
		assertThat(flag.equals(""));
	}
	
	//验证编号的唯一性
	@Test
	@DataSet("validateCode.xls")
	public void validateCode() {
		boolean flag = smsGatewaySettingManager.validateCode("1005", 3L);
		assertThat(flag).isFalse();
		
	    flag = smsGatewaySettingManager.validateCode("1003", 2L);
		assertThat(flag).isTrue();
	}
	//验证删除
	@Test
	@DataSet("changeGatewayState.xls")
	public void changeGatewayState() {
		String result = smsGatewaySettingManager.changeGatewayState("1");
		assertThat(result).isEqualTo("草稿到启用:0;禁用到启用:0;启用到禁用:1");
		
		result = smsGatewaySettingManager.changeGatewayState("2");
		assertThat(result).isEqualTo("草稿到启用:1;禁用到启用:0;启用到禁用:0");
		
		result = smsGatewaySettingManager.changeGatewayState("3");
		assertThat(result).isEqualTo("草稿到启用:0;禁用到启用:1;启用到禁用:0");
		
		
	}
	//短信猫保存配置
	@Test
	@DataSet("saveConfig.xls")
	public void saveConfig() {
		smsGatewaySettingManager.saveConfig(1L, "configration", "3", "SENDANDRECEIVE");
		//先查询一次，flush
		SmsGatewaySetting setting = smsGatewaySettingManager.getSmsGatewaySettingById(1l);
	    		
		System.out.println(setting.getId());
		assertThat(setting.getConfiguration()).isEqualTo("configration");
		assertThat(setting.getMaxTime()).isEqualTo(3);
	}
	
	//得到短信猫配置，转为数组
	@Test
	@DataSet("getAllConfigCat.xls")
	public void getAllConfigCat() {
		SmsGatewaySetting setting = smsGatewaySettingManager.getSmsGatewaySettingById(1l);
		setting.setMaxTime(5);
		String[] result = smsGatewaySettingManager.getAllConfigCat(setting);
		
		assertThat(result.length).isEqualTo(5);
		assertThat(result[0]).isEqualTo("101");
		assertThat(result[1]).isEqualTo("comname");
		assertThat(result[2]).isEqualTo("9600");
		
		assertThat(result[3]).isEqualTo("zhangsan");
		assertThat(result[4]).isEqualTo("5");
	}
	//得到短信猫配置，转为数组
	@Test
	@DataSet("getAllConfigWeimi.xls")
	public void getAllConfigWeimi() {
		SmsGatewaySetting setting = smsGatewaySettingManager.getSmsGatewaySettingById(1l);
		setting.setMaxTime(10);
		String[] result = smsGatewaySettingManager.getAllConfigWeimi(setting);
		
		assertThat(result.length).isEqualTo(4);
		assertThat(result[0]).isEqualTo("101");
		assertThat(result[1]).isEqualTo("weimiPw");
		assertThat(result[2]).isEqualTo("我是签名1");
		
		assertThat(result[3]).isEqualTo("10");
	}
	
	
	
	
	
}
