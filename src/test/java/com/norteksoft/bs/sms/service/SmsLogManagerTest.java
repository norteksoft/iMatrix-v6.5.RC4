package com.norteksoft.bs.sms.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.unitils.dbunit.annotation.DataSet;
import org.unitils.spring.annotation.SpringBeanByName;

import com.norteksoft.bs.sms.base.BaseServiceTest;
import com.norteksoft.bs.sms.entity.SmsAuthoritySetting;
import com.norteksoft.bs.sms.entity.SmsGatewaySetting;
import com.norteksoft.bs.sms.entity.SmsLog;
import com.norteksoft.bs.sms.entity.SmsWaitTosend;
import com.norteksoft.product.util.ParameterUtils;
import com.norteksoft.product.util.ThreadParameters;


public class SmsLogManagerTest extends BaseServiceTest{
	@SpringBeanByName
	private SmsLogManager smsLogManager;
	@SpringBeanByName
	private SmsGatewaySettingManager smsGatewaySettingManager;
	@SpringBeanByName
	private SmsAuthoritySettingManager smsAuthoritySettingManager;
	@SpringBeanByName
	private SmsWaitTosendManager smsWaitTosendManager;
	
	@Before
	public void init(){
		ThreadParameters parameters=new ThreadParameters(null,null);
		parameters.setCompanyId(1L);
		ParameterUtils.setParameters(parameters);
	}

	@Test
	@DataSet("createLog-AuthoritySetting.xls")
	public void createLog() {
		SmsGatewaySetting smsGatewaySetting = smsGatewaySettingManager.getSmsGatewaySettingById(1L);
		SmsWaitTosend smsWaitTosend = smsWaitTosendManager.getSmsWaitTosendById(1L);
		SmsAuthoritySetting setting = smsAuthoritySettingManager.getSmsAuthoritySettingById(1L);
		smsLogManager.createLog(smsGatewaySetting,smsWaitTosend,setting,"ok");
		SmsLog log = jdbcTemplate.queryForObject("select * from bs_sms_log where sender_or_receiver = ?"
		    		,ParameterizedBeanPropertyRowMapper.newInstance(SmsLog.class),new Object[]{"13300000000;13311111111"});
		
		assertThat(log).isNotNull();
		assertThat(log.getContent()).isEqualTo("我是content1");
		assertThat(log.getBackUrl()).isEqualTo("http://localhost:8080/cbm");
		assertThat(log.getSendTime()).isEqualTo(1);
		assertThat(log.getLogType()).isEqualTo("send");
		assertThat(log.getGatewayCode()).isEqualTo("1003");
		
	}
	
	
}
