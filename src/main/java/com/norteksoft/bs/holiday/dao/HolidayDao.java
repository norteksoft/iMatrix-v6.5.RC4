package com.norteksoft.bs.holiday.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.norteksoft.bs.holiday.entity.DateType;
import com.norteksoft.bs.holiday.entity.Holiday;
import com.norteksoft.bs.holiday.entity.HolidaySettingType;
import com.norteksoft.product.orm.hibernate.HibernateDao;
import com.norteksoft.product.util.ContextUtils;

@Repository
public class HolidayDao extends HibernateDao<Holiday, Long>{
	
	public Long getCompanyId(){
		return ContextUtils.getCompanyId();
	}
	
	public List<Holiday> getHolidaySetting(Date startDate, Date endDate,Long branchId){
		if(branchId!=null){
			return this.find("from Holiday h where h.companyId=? and h.specialDate between ? and ? and h.subCompanyId=?", getCompanyId(), startDate, endDate,branchId);
		}else{
			return this.find("from Holiday h where h.companyId=? and h.specialDate between ? and ? and h.subCompanyId is null", getCompanyId(), startDate, endDate);
		}
	}
	
	public Holiday getHolidayByDate(Date date,Long branchId){
		List<Holiday> list = new ArrayList<Holiday>();
		if(branchId!=null){
			list = this.find("from Holiday h where h.companyId=? and h.specialDate=? and h.subCompanyId=?", getCompanyId(), date,branchId);
		}else{
			list = this.find("from Holiday h where h.companyId=? and h.specialDate=? and h.subCompanyId is null", getCompanyId(), date);
		}
		if(list.size() == 1) {
			return list.get(0);
		}else{
			return null;
		}
	}

	/**
	 * 根据分支机构id获得该分支机构下的所有节假日设置
	 * @param branchId
	 * @return
	 */
	public List<Holiday> getHolidaySetting(Long branchId) {
		return this.find("from Holiday h where h.companyId=? and h.subCompanyId=? ",ContextUtils.getCompanyId(),branchId);
	}
	/**
	 * 获得某公司的工作日设置的时间设置
	 * @return
	 */
	public Object[] getWorkHolidaySetting(Long branchId){
		List<Object[]> list = new ArrayList<Object[]>();
		if(branchId!=null){
			list = this.find("select distinct(h.subCompanyId),h.workStartTime,h.workEndTime from Holiday h where h.companyId=?   and h.subCompanyId=?", getCompanyId(),branchId);
		}else{
			list = this.find("select distinct(h.companyId),h.workStartTime,h.workEndTime from Holiday h where h.companyId=?  and h.subCompanyId is null", getCompanyId());
		}
		if(list.size()>0)return list.get(0);
		return null;
	}
	
	/**
	 * 当前分支如果设置了节假日，获得设置的节假日
	 * @param branchId 分支id，值为null表示是集团公司
	 * @return
	 */
	public List<Date> getHolidays(Long branchId) {
		if(branchId==null){
			return this.find("select h.specialDate from Holiday h where h.companyId=? and h.subCompanyId is null and h.dateType=? order by h.specialDate",ContextUtils.getCompanyId(),DateType.HOLIDAY);
		}else{
			return this.find("select h.specialDate from Holiday h where h.companyId=? and h.subCompanyId=?  and h.dateType=?  order by h.specialDate",ContextUtils.getCompanyId(),branchId,DateType.HOLIDAY);
		}
	}
	/**
	 * 当前分支如果设置了工作日日，获得设置的工作日日
	 * @param branchId 分支id，值为null表示是集团公司
	 * @return
	 */
	public List<Date> getWorkDates(Long branchId) {
		if(branchId==null){
			return this.find("select h.specialDate from Holiday h where h.companyId=? and h.subCompanyId is null and h.dateType=? order by h.specialDate",ContextUtils.getCompanyId(),DateType.WORKING_DAY);
		}else{
			return this.find("select h.specialDate from Holiday h where h.companyId=? and h.subCompanyId=?  and h.dateType=?   order by h.specialDate",ContextUtils.getCompanyId(),branchId,DateType.WORKING_DAY);
		}
	}
	
	public void updateWorkTime(Long branchId,String workStartTime,String workEndTime){
		if(branchId==null){
			 this.createQuery("update Holiday h set h.workStartTime=?,h.workEndTime=? where h.companyId=? and h.subCompanyId is null  ",workStartTime,workEndTime,ContextUtils.getCompanyId()).executeUpdate();
		}else{
			 this.createQuery("update Holiday h set h.workStartTime=?,h.workEndTime=?  where h.companyId=? and h.subCompanyId=? ",workStartTime,workEndTime,ContextUtils.getCompanyId(),branchId).executeUpdate();
		}
	}
}
