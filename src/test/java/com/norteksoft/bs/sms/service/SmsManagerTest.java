package com.norteksoft.bs.sms.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.unitils.dbunit.annotation.DataSet;
import org.unitils.spring.annotation.SpringBeanByName;

import com.norteksoft.bs.sms.base.BaseServiceTest;
import com.norteksoft.bs.sms.entity.SmsWaitTosend;
import com.norteksoft.product.util.ParameterUtils;
import com.norteksoft.product.util.ThreadParameters;


public class SmsManagerTest extends BaseServiceTest{
	@SpringBeanByName
	private SmsManager smsManager;
	
	@Before
	public void init(){
		ThreadParameters parameters=new ThreadParameters(null,null);
		parameters.setCompanyId(1L);
		ParameterUtils.setParameters(parameters);
	}
	@Test
	@DataSet("sendMessageNoGateway.xls")
	public void sendMessageNoGateway() {
		String[] args = {"",""};
		String flag = smsManager.sendMessage("13011112222","inter1", args);//
		assertThat(flag).isEqualTo("没有可用的网关");
//		
		
	}
	@Test
	@DataSet("sendMessageGateway.xls")
	public void sendMessage() {
		String[] args = {"-我是参数1","-我是参数2","-我是参数3"};
		String flag = smsManager.sendMessage("13011112222","inter0", args);//
		assertThat(flag).isEqualTo("接口编号不存在");
		
		
		flag = smsManager.sendMessage("13011112222","inter1", args);//
		SmsWaitTosend send = jdbcTemplate.queryForObject("select * from bs_sms_wait_tosend where receiver = ?"
	    		,ParameterizedBeanPropertyRowMapper.newInstance(SmsWaitTosend.class),new Object[]{"13011112222"});
		assertThat(send).isNotNull();
		assertThat(send.getContent()).isEqualTo("我是第1个模版-我是参数1,发送我-我是参数2,在发送我一次-我是参数3");
		assertThat(flag).isEqualTo("已添加到待发送列表");
		
		
		
//		
		
	}
	
	
}
