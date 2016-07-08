package com.norteksoft.bs.sms.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.unitils.dbunit.annotation.DataSet;
import org.unitils.spring.annotation.SpringBeanByName;

import com.norteksoft.bs.sms.base.BaseServiceTest;
import com.norteksoft.bs.sms.dao.SmsWaitTosendDao;
import com.norteksoft.bs.sms.entity.SmsLog;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.ParameterUtils;
import com.norteksoft.product.util.ThreadParameters;


public class SmsSendMessageManagerTest extends BaseServiceTest{
	@SpringBeanByName
	private SmsSendMessageManager smsSendMessageManager;
	@SpringBeanByName
	private SmsWaitTosendDao smsWaitTosendDao;
	
	@Before
	public void init(){
		ThreadParameters parameters=new ThreadParameters(null,null);
		parameters.setCompanyId(1L);
		ParameterUtils.setParameters(parameters);
	}

	@Test
	@DataSet("send-GatewaySetting.xls")
	public void send() {
		ContextUtils.setContext(applicationContext);
		try {
			smsSendMessageManager.send();
		} catch (Exception e) {
			e.printStackTrace();
		}
		SmsLog log = jdbcTemplate.queryForObject("select * from bs_sms_log where sender_or_receiver = ?", 
				ParameterizedBeanPropertyRowMapper.newInstance(SmsLog.class),
				new Object[] { "13300000000;13311111111" });

		assertThat(log).isNotNull();
		assertThat(log.getContent()).isEqualTo("我是content1");
		assertThat(log.getBackUrl()).isEqualTo("无");
		assertThat(log.getLogType()).isEqualTo("send");
		assertThat(log.getGatewayCode()).isEqualTo("1003");
		
		sessionFactory.getCurrentSession().flush();
		
		int i = smsWaitTosendDao.countSql("select count(1) from BS_SMS_WAIT_TOSEND ");
		assertThat(i).isEqualTo(0);

	
	}
	
	
	
}
