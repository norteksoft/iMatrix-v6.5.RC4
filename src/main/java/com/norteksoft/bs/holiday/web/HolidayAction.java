package com.norteksoft.bs.holiday.web;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;

import com.norteksoft.acs.entity.organization.Department;
import com.norteksoft.acs.service.organization.DepartmentManager;
import com.norteksoft.bs.holiday.entity.DateType;
import com.norteksoft.bs.holiday.entity.Holiday;
import com.norteksoft.bs.holiday.entity.HolidaySettingType;
import com.norteksoft.bs.holiday.service.HolidayManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.JsonParser;
import com.norteksoft.product.util.tree.ZTreeNode;
import com.norteksoft.product.web.struts2.CrudActionSupport;

@Namespace("/holiday")
@ParentPackage("default")
@Results( { @Result(name = CrudActionSupport.RELOAD, location = "holiday?branchId=${branchId}", type = "redirectAction")})
public class HolidayAction extends CrudActionSupport<Holiday>{
	private static final long serialVersionUID = 1L;
	@Autowired
	private HolidayManager holidayManager;
	@Autowired
	private DepartmentManager departmentManager;
	private Long id;
	private Holiday holiday;	
	private Date startDate; // 开始日期
	private Date endDate; // 结束日期
	private DateType dateType;
	private Date targetDate;
	private String specialDates;
	private Long branchId;//分支机构id
	private String nodeType;//树节点的类型：COMPANY（集团公司），BRANCHES（分支机构），DEPARTMENT（部门）
	private List<Integer> dayOfWeek=new ArrayList<Integer>();
	
	private String workStartTime="09:00";//工作日开始工作时间，例如：8:00
	private String workEndTime="18:00";//工作日结束工作时间，例如：18:00
	
	public String list() throws Exception {
		if(!"DEPARTMENT".equals(nodeType)){
			if(targetDate == null) targetDate = new Date();
			List<Integer> dates = holidayManager.getMonthSetting(targetDate,branchId);
			specialDates = calendarString(targetDate, dates);
			Object[] holiday = holidayManager.getWorkHolidaySetting(branchId);
			if(holiday!=null&&holiday.length==3){
				workStartTime = holiday[1].toString();
				workEndTime = holiday[2].toString();
			}
			ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("bs.holidayConfigure"), ApiFactory.getBussinessLogService().getI18nLogInfo("bs.viewHolidayConfigure"),ContextUtils.getSystemId("bs"));
		}
		return SUCCESS;
	}
	/**
	 * 是否设置了节假日或工作日
	 * @return true表示设置了，false表示没有设置
	 */
	private boolean isSetHoliday(){
		Object[] holiday = holidayManager.getWorkHolidaySetting(branchId);
		if(holiday==null){
			return false;
		}else{
			return true;
		}
	}
	// 日历json格式，包含节假日
	private String calendarString(Date date, List<Integer> dates){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		
		Calendar newCal = Calendar.getInstance();
		newCal.setTime(new Date(0));
		newCal.set(Calendar.YEAR, cal.get(Calendar.YEAR));
		newCal.set(Calendar.MONTH, cal.get(Calendar.MONTH));
		
		int month = newCal.get(Calendar.MONTH);
		boolean hasWeekday = false;
		StringBuilder calString = new StringBuilder("[{");
		for(;newCal.get(Calendar.MONTH) == month; newCal.add(Calendar.DAY_OF_YEAR, 1)){
			if(!hasWeekday){
				calString.append("'firstWeekday':").append(newCal.get(Calendar.DAY_OF_WEEK));
				calString.append(",'year':").append(newCal.get(Calendar.YEAR));
				calString.append(",'month':").append(newCal.get(Calendar.MONTH));
				calString.append(",'days':[");
				hasWeekday = true;
			}
			calString.append("{").append("'day':").append(newCal.get(Calendar.DAY_OF_MONTH));
			calString.append(",'isHoliday':");
			if(dates.contains(newCal.get(Calendar.DAY_OF_MONTH))){
				calString.append("true");
			}else{
				calString.append("false");
			}
			calString.append("},");
		}
		calString.delete(calString.length()-1, calString.length());
		calString.append("]}]");
		return calString.toString();
	}

	@Action("holiday-input")
	public String input() throws Exception {
		return SUCCESS;
	}

	@Action("holiday-save")
	public String save() throws Exception {
		boolean needUpdate = true;
		List<Holiday> holidays = new ArrayList<Holiday>();
		if(dayOfWeek.size()>0){
			for(Integer week:dayOfWeek){
				for(Date d:weekOfYear(week)){
					holiday = createHoliday(d,HolidaySettingType.SHORTCUT);
					holidays.add(holiday);
				}
			}
		}
		if(startDate!=null){
			if(endDate == null){
				holiday = createHoliday(startDate,HolidaySettingType.CONVENTION);
				holidays.add(holiday);
			}else{
				endDate = addDays(endDate, 1);
				for(;startDate.before(endDate);){
					holiday = createHoliday(startDate,HolidaySettingType.CONVENTION);
					holidays.add(holiday);
					startDate = addDays(startDate, 1);
				}
			}
		}
		if(!isSetHoliday()){//没有设置过节假日工作日，则默认是周六周日为节假日
			if(dayOfWeek.size()<=0&&startDate==null){//页面中没有设置开始结束日期 和快速设置
				needUpdate = false;
				for(Date d:getAllDefaultHolidays()){
					holiday = createHoliday(d,HolidaySettingType.SHORTCUT);
					holiday.setDateType(DateType.HOLIDAY);
					holidays.add(holiday);
				}
			}
		}
		if(needUpdate)//需要更新工作时间
			holidayManager.updateWorkTime(branchId, workStartTime, workEndTime);//更新当前分支或集团下的所有记录的工作时间
		holidayManager.saveHoliday(holidays);
		ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("bs.holidayConfigure"), ApiFactory.getBussinessLogService().getI18nLogInfo("bs.saveHolidayConfigure"),ContextUtils.getSystemId("bs"));
		return RELOAD;
	}
	
	private List<Date> weekOfYear(Integer week){
		List<Date> dateList=new ArrayList<Date>();
		Calendar calendar =Calendar.getInstance(); //当前日期
        int currentyear = calendar.get(Calendar.YEAR);
        int nextyear = 1+calendar.get(Calendar.YEAR);
        Calendar cstart =Calendar.getInstance(); 
        Calendar cend =Calendar.getInstance(); 
        cstart.set(currentyear, 0, 1);//2013-1-1
        cend.set(nextyear, 0, 1);//2014-1-1
 		int dayOfWeek=calendar.get(Calendar.DAY_OF_WEEK)-1;
 		int offset1=week-dayOfWeek;
 		calendar.add(Calendar.DATE, offset1-7);
        Calendar d = (Calendar)calendar.clone();
        //向前
        for(;calendar.before(cend)&&calendar.after(cstart);calendar.add(Calendar.DAY_OF_YEAR, -7)){
             dateList.add(holidayManager.clearDateTime(calendar.getTime()));
        }
        int i=0;
        //向后
        for(;d.before(cend)&&d.after(cstart);d.add(Calendar.DAY_OF_YEAR, 7)){
        	if(i>0){
        		dateList.add(holidayManager.clearDateTime(d.getTime()));
        	}
        	i++;
        }
		return dateList;
	}

	/**
	 * 获得一年内的默认的节假日，即周六、周日
	 * @return
	 */
	private List<Date> getAllDefaultHolidays(){
		List<Date> result = new ArrayList<Date>();
		List<Date> sundays = new ArrayList<Date>();
		List<Date> saturdays = new ArrayList<Date>();
		sundays = weekOfYear(7);
		saturdays = weekOfYear(6);
		result.addAll(sundays);
		result.addAll(saturdays);
		return result;
	}
	
	
	private Date addDays(Date date,int amount){
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_MONTH,amount);
		return calendar.getTime();
	}
	
	private Holiday createHoliday(Date specialDate,HolidaySettingType settingType){
		holiday = new Holiday();
		holiday.setSpecialDate(specialDate);
		holiday.setDateType(dateType);
		holiday.setSubCompanyId(branchId);
		holiday.setHolidaySettingType(settingType);
		holiday.setWorkStartTime(workStartTime);
		holiday.setWorkEndTime(workEndTime);
		return holiday;
	}

	@Action("holiday-delete")
	public String delete() throws Exception {
		holidayManager.deleteHoliday(id);
		ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("bs.holidayConfigure"), ApiFactory.getBussinessLogService().getI18nLogInfo("bs.deleteHolidayConfigure"),ContextUtils.getSystemId("bs"));
		return RELOAD;
	}
	
	@Action("holiday-tree")
	public String holidayTree(){
		List<ZTreeNode> companyNodes = new ArrayList<ZTreeNode>();
		
		ZTreeNode root = new ZTreeNode("COMPANY_" + ContextUtils.getCompanyId(),"0",ContextUtils.getCompanyName(), "true", "false", "", "", "root", "");
		companyNodes.add(root);
		getSubBranches(null,companyNodes,"COMPANY_" + ContextUtils.getCompanyId());
		this.renderText(JsonParser.object2Json(companyNodes));
		return null;
	}
	
	private String getSubBranches(Long departmentId,List<ZTreeNode> companyNodes,String parentId) {
		StringBuilder nodes = new StringBuilder();
		List<Department> departments = new ArrayList<Department>();
		if(departmentId==null){
			departments = departmentManager.getRootDepartment();
		}else{
			departments = departmentManager.getSubDeptments(departmentId);
		}
		
		for(Department d:departments){
			if(d.getBranch()){
				if(StringUtils.isNotEmpty(nodes.toString())){
					nodes.append(",");
				}
				ZTreeNode root = new ZTreeNode("BRANCHES_" + d.getId(),parentId,StringUtils.isNotEmpty(d.getShortTitle())?d.getShortTitle():d.getName(), "false", "false", "", "", "branch", "");
				companyNodes.add(root);
				List<Department> subDepartments = departmentManager.getSubDeptments(d.getId());
				if(subDepartments != null && subDepartments.size()>0 && haveBranchesValidate(d.getId())){
					getSubBranches(d.getId(),companyNodes,"BRANCHES_" + d.getId());
				}
			}else{
				boolean haveBranches=haveBranchesValidate(d.getId());
				if(haveBranches){
					if(StringUtils.isNotEmpty(nodes.toString())){
						nodes.append(",");
					}
					ZTreeNode root = new ZTreeNode("DEPARTMENT_" + d.getId(),parentId,StringUtils.isNotEmpty(d.getShortTitle())?d.getShortTitle():d.getName(), "false", "false", "", "", "department", "");
					companyNodes.add(root);
					getSubBranches(d.getId(),companyNodes,"DEPARTMENT_" + d.getId());
//					nodes.append(JsTreeUtils.generateJsTreeNodeNew("DEPARTMENT_" + d.getId(), "closed", StringUtils.isNotEmpty(d.getShortTitle())?d.getShortTitle():d.getName(), getSubBranches(d.getId()), "folder"));
				}
			}
		}
		return nodes.toString();
	}
	
	/**
	 * 判断departmentId（部门id或分支机构id）中是否含有子分支机构，如果有返回true,否则返回false
	 * @param departmentId
	 * @return
	 */
	private boolean haveBranchesValidate(Long departmentId) {
		boolean haveBranches=false;
		List<Department> subBranches=departmentManager.getSubDeptments(departmentId);
		for(Department depart:subBranches){
			if(haveBranches){
				haveBranches=true;
				break;
			}
			if(depart.getBranch()){
				haveBranches=true;
				break;
			}else{
				haveBranches=haveBranchesValidate(depart.getId());
			}
		}
		return haveBranches;
	}

	protected void prepareModel() throws Exception {
		if(id == null){
			holiday = new Holiday();
		}else{
			holiday = holidayManager.getHoliday(id);
		}
	}

	public Holiday getModel() {
		return null;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public void setDateType(DateType dateType) {
		this.dateType = dateType;
	}

	public Date getTargetDate() {
		return targetDate;
	}

	public void setTargetDate(Date targetDate) {
		this.targetDate = targetDate;
	}

	public String getSpecialDates() {
		return specialDates;
	}
	public Long getBranchId() {
		return branchId;
	}
	public void setBranchId(Long branchId) {
		this.branchId = branchId;
	}
	public String getNodeType() {
		return nodeType;
	}
	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}
	public List<Integer> getDayOfWeek() {
		return dayOfWeek;
	}
	public void setDayOfWeek(List<Integer> dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
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

}
