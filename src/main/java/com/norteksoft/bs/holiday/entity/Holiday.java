package com.norteksoft.bs.holiday.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import com.norteksoft.product.orm.IdEntityNoExtendField;


@Entity
@Table(name = "BS_HOLIDAY")
public class Holiday extends IdEntityNoExtendField {
	private static final long serialVersionUID = 1L;

	private Date specialDate; // 日期
	@Enumerated(EnumType.STRING)
	private DateType dateType; // 日期类别： 工作日，节假日
	@Enumerated(EnumType.STRING)
	private HolidaySettingType holidaySettingType; // 设置类别：常规设置 ，快速设置
	@Column(length=10)
	private String workStartTime="09:00";//工作日开始工作时间
	@Column(length=10)
	private String workEndTime="18:00";//工作日结束工作时间

	public Date getSpecialDate() {
		return specialDate;
	}

	public void setSpecialDate(Date specialDate) {
		this.specialDate = specialDate;
	}

	public DateType getDateType() {
		return dateType;
	}

	public void setDateType(DateType dateType) {
		this.dateType = dateType;
	}
	
	public HolidaySettingType getHolidaySettingType() {
		return holidaySettingType;
	}

	public void setHolidaySettingType(HolidaySettingType holidaySettingType) {
		this.holidaySettingType = holidaySettingType;
	}

	public String getWorkStartTime() {
		return workStartTime;
	}

	public void setWorkStartTime(String workStartTime) {
		this.workStartTime = workStartTime;
	}

	public String getWorkEndTime() {
		return workEndTime;
	}

	public void setWorkEndTime(String workEndTime) {
		this.workEndTime = workEndTime;
	}

	@Override
	public String toString() {
		return "id:"+this.getId()+"；日期："+this.specialDate+"；日期类别："+this.dateType;
	}
}
