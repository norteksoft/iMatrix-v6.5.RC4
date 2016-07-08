package com.norteksoft.bs.options.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.norteksoft.bs.options.entity.Internation;
import com.norteksoft.bs.options.enumeration.InternationType;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.orm.hibernate.HibernateDao;
@Repository
public class InternationDao extends HibernateDao<Internation,Long>{
	public void getInternations(Page<Internation> page,InternationType type){
		String hql="select t from Internation t where t.internationType=? order by t.id desc";
		this.searchPageByHql(page, hql,type);
	}
	
	public Internation getInternationByCode(String code,InternationType type){
		String hql="from Internation t where t.code=? and t.internationType=?";
		List<Internation> inters=this.find(hql, code,type);
		if(inters.size()>0)return inters.get(0);
		return null;
	}
	//监听中用到
	public List<Internation> getAllInternations(){
		return this.findNoCompanyCondition("from Internation t order by t.id desc");
	}
	//监听中用到
	public List<Internation> getInternations(Long companyId){
		return this.findNoCompanyCondition("from Internation t where t.companyId=? order by t.id desc",companyId);
	}
	
	public List<Internation> getInternations(){
		return this.find("from Internation t order by t.id desc");
	}
}
