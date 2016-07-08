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


public class SmsWaitTosendManagerTest extends BaseServiceTest{
	@SpringBeanByName
	private SmsWaitTosendManager smsWaitTosendManager;
	
	@Before
	public void init(){
		ThreadParameters parameters=new ThreadParameters(null,null);
		parameters.setCompanyId(1L);
		ParameterUtils.setParameters(parameters);
	}

	@Test
	@DataSet("empty-WaitTosend.xls")
	public void createWaitToSend() {
		String ids = "";
		smsWaitTosendManager.createWaitToSend(ids,"13300001111","我是发送的内容。。。");
		
		SmsWaitTosend send = jdbcTemplate.queryForObject("select * from BS_SMS_WAIT_TOSEND where Receiver = ?"
	    		,ParameterizedBeanPropertyRowMapper.newInstance(SmsWaitTosend.class),new Object[]{"13300001111"});
		
		assertThat(send.getId()).isNotNull();
		assertThat(send.getContent()).isEqualTo("我是发送的内容。。。");
		assertThat(send.getSendTime()).isEqualTo(0);
		
	}
	

	@Test
	public void cgcslCancelOrderSendMail() throws Exception {
//		smsWaitTosendManager.cgcslCancelOrderSendMail("2222222222222");
		
	}
	
	
	
}
