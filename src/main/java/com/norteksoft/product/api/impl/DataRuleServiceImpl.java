package com.norteksoft.product.api.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.acs.service.authority.DataRuleManager;
import com.norteksoft.product.api.DataRuleService;

@Service
@Transactional
public class DataRuleServiceImpl implements DataRuleService {

	@Autowired
	private DataRuleManager dataRuleManager;

	public void getConditionResult(String hql,List<String> dataRuleCodes) {
		dataRuleManager.addConditionResult(hql,dataRuleCodes);
	}
	public void getConditionResult(String hql,Object entity) {
		dataRuleManager.addConditionResult(hql,entity);
	}
}
