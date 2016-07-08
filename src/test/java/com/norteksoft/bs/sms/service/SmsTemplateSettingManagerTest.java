package com.norteksoft.bs.sms.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.unitils.dbunit.annotation.DataSet;
import org.unitils.spring.annotation.SpringBeanByName;

import com.norteksoft.bs.sms.base.BaseServiceTest;
import com.norteksoft.bs.sms.dao.SmsTemplateSettingDao;
import com.norteksoft.bs.sms.entity.SmsAuthoritySetting;
import com.norteksoft.product.util.ParameterUtils;
import com.norteksoft.product.util.ThreadParameters;


public class SmsTemplateSettingManagerTest extends BaseServiceTest{
	@SpringBeanByName
	private SmsTemplateSettingManager smsTemplateSettingManager;
	@SpringBeanByName
	private SmsAuthoritySettingManager smsAuthoritySettingManager;
	@SpringBeanByName
	private SmsTemplateSettingDao smsTemplateSettingDao;
	
	@Before
	public void init(){
		ThreadParameters parameters=new ThreadParameters(null,null);
		parameters.setCompanyId(1L);
		ParameterUtils.setParameters(parameters);
	}

	@Test
	@DataSet("delete-TemplateSetting.xls")//3条数据
	public void delete() {
		String ids = "1";
		smsTemplateSettingManager.delete(ids);
		sessionFactory.getCurrentSession().flush();
		int i = smsTemplateSettingDao.countSql("select count(1) from bs_sms_template_setting ");
		assertThat(i).isEqualTo(2);
		
	}
	@Test
	@DataSet("validateCode-TemplateSetting.xls")
	public void validateCode() {
		boolean flag = smsTemplateSettingManager.validateCode("2001",1L);
		assertThat(flag).isFalse();
		flag = smsTemplateSettingManager.validateCode("2001",2L);
		assertThat(flag).isTrue();
		flag = smsTemplateSettingManager.validateCode("2001",4L);
		assertThat(flag).isTrue();
		flag = smsTemplateSettingManager.validateCode("2004",4L);
		assertThat(flag).isFalse();
		
	}
	@Test
	@DataSet("parseArgsToContent-AuthoritySetting.xls")
	public void parseArgsToContent() {
		SmsAuthoritySetting setting = smsAuthoritySettingManager.getSmsAuthoritySettingById(1L);
		String[] args = {"param1","param2","param3"};
		String flag = smsTemplateSettingManager.parseArgsToContent(setting,args);//有模版，
		assertThat(flag).isEqualTo("我是第1个模版param1,发送我param2,在发送我一次param3");
		
		String[] args2 = {"param1","param2"};
		flag = smsTemplateSettingManager.parseArgsToContent(setting,args2);//有模版，参数不足
		assertThat(flag).isEqualTo("我是第1个模版param1,发送我param2,在发送我一次#param#");
		
		String[] args3 = {"param1","param2","param3","param4"};
		flag = smsTemplateSettingManager.parseArgsToContent(setting,args3);//有模版，参数多
		assertThat(flag).isEqualTo("我是第1个模版param1,发送我param2,在发送我一次param3");
		
		setting = smsAuthoritySettingManager.getSmsAuthoritySettingById(2L);
		String[] args4 = {"我是要发送的内容1"};
		flag = smsTemplateSettingManager.parseArgsToContent(setting,args4);//没有模版，并只有一个参数
		assertThat(flag).isEqualTo("我是要发送的内容1");
		
		setting = smsAuthoritySettingManager.getSmsAuthoritySettingById(2L);
		String[] args5 = {"我是要发送的内容1","我是要发送的内容2"};
		flag = smsTemplateSettingManager.parseArgsToContent(setting,args5);//没有模版，并有多个参数,只取第一个参数
		assertThat(flag).isEqualTo("我是要发送的内容1");
		
		
		
	}
	
	
	
	
}
