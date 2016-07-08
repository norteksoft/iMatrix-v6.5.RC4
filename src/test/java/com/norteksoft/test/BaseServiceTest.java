package com.norteksoft.test;

import org.junit.Ignore;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.unitils.UnitilsJUnit4;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBean;

@SpringApplicationContext( {"applicationContext-test.xml" })
@Ignore
public class BaseServiceTest extends UnitilsJUnit4{
	@SpringApplicationContext
	protected ApplicationContext applicationContext;
	
	@SpringBean("simpleJdbcTemplate")
	protected SimpleJdbcTemplate jdbcTemplate;
}
