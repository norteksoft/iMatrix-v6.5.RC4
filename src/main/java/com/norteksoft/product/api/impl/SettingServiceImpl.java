package com.norteksoft.product.api.impl;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.norteksoft.bs.holiday.service.HolidayManager;
import com.norteksoft.bs.options.entity.InterfaceSetting;
import com.norteksoft.bs.options.entity.TimedTask;
import com.norteksoft.bs.options.enumeration.InternationType;
import com.norteksoft.bs.options.service.DatasourceSettingManager;
import com.norteksoft.bs.options.service.InterfaceSettingManager;
import com.norteksoft.bs.options.service.InternationManager;
import com.norteksoft.bs.options.service.JobInfoManager;
import com.norteksoft.bs.options.service.OptionGroupManager;
import com.norteksoft.bs.signature.service.SignatureManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.SettingService;
import com.norteksoft.product.api.entity.DatasourceSetting;
import com.norteksoft.product.api.entity.Option;
import com.norteksoft.product.api.entity.OptionGroup;
import com.norteksoft.product.api.utils.BeanUtil;
import com.norteksoft.product.enumeration.DataState;
import com.norteksoft.product.util.AsyncMailUtils;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.MemCachedUtils;
import com.norteksoft.product.util.PropUtils;
import com.norteksoft.product.web.struts2.Struts2Utils;

@Service
public class SettingServiceImpl implements SettingService{
	
	@Autowired
	private OptionGroupManager optionGroupManager;
	
	@Autowired
	private HolidayManager holidayManager;
	@Autowired
	private InternationManager internationManager;
	@Autowired
	private SignatureManager signatureManager;
	@Autowired
	private DatasourceSettingManager datasourceSettingManager;
	@Autowired
	private InterfaceSettingManager interfaceSettingManager;
	@Autowired
	private JobInfoManager jobInfoManager;

	public String getOptionGroupDefaultValue(String optionGroupCode) {
		Assert.notNull(optionGroupCode, "optionGroupCode选项组编号不能为null");
		com.norteksoft.bs.options.entity.Option option = optionGroupManager.getDefaultOptionByOptionGroupCode(optionGroupCode);
		return option.getValue();
	}
	
	public String getOptionGroupDefaultValue(Long optionGroupId) {
		Assert.notNull(optionGroupId, "optionGroupId选项组id不能为null");
		com.norteksoft.bs.options.entity.Option option = optionGroupManager.getDefaultOptionByOptionGroupId(optionGroupId);
		if(option==null)return null;
		return option.getValue();
	}
	
	/**
	 * 查询所有的选项组
	 */
	public List<OptionGroup> getOptionGroups() {
		return BeanUtil.turnToModelOptionGroupList(optionGroupManager.getOptionGroups());
	}

	/**
	 * 根据选项组查询选项
	 */
	public List<Option> getOptionsByGroup(Long optionGroupId) {
		List<Option> options = BeanUtil.turnToModelOptionList(optionGroupManager.getOptionsByGroup(optionGroupId));
		for (Option option : options) {
			String nameStr = getText(option.getName()); 
			option.setName(nameStr);
		}
		return options;
	}

	public OptionGroup getOptionGroupByCode(String code) {
		return BeanUtil.turnToModelOptionGroup(optionGroupManager.getOptionGroupByCode(code));
	}

	public OptionGroup getOptionGroupByName(String name) {
		return BeanUtil.turnToModelOptionGroup(optionGroupManager.getOptionGroupByName(name));
	}

	public List<Option> getOptionsByGroupCode(String code) {
		List<Option> options = BeanUtil.turnToModelOptionList(optionGroupManager.getOptionsByGroupCode(code));
		for (Option option : options) {
			String nameStr = getText(option.getName()); 
			option.setName(nameStr);
		}
		return options;
	}

	public List<Option> getOptionsByGroupName(String name) {
		List<Option> options =  BeanUtil.turnToModelOptionList(optionGroupManager.getOptionsByGroupName(name));
		for (Option option : options) {
			String nameStr = getText(option.getName()); 
			option.setName(nameStr);
		}
		return options;
	}

	public Map<String, List<Date>> getHolidaySettingDays(Date startDate, Date endDate){
		return holidayManager.getHolidaySettingDays(startDate, endDate,ContextUtils.getSubCompanyId());
	}
	public Map<String, List<Date>> getHolidaySettingDays(Date startDate, Date endDate,Long branchId){
		return holidayManager.getHolidaySettingDays(startDate, endDate,branchId);
	}
	
	/**
	 * 公共国际化代码
	 */
	public String getText(String code) {
		HttpServletRequest requrest=Struts2Utils.getRequest();
		if(requrest==null){//定时等没有Struts环境中时，直接去数据库中取
			return code;
		}
		return getI18nValue(code);
	}
	/**
	 * 国际化
	 * @param menuName
	 * @return
	 */
	private String getI18nValue(String menuName) {
		String result = menuName;
		try {
			if(menuName.contains("${")&&menuName.contains("}")){
				menuName = menuName.substring(menuName.indexOf("${")+2,menuName.indexOf("}"));
				menuName = Struts2Utils.getText(menuName);
			}
			return menuName;
		} catch (Exception e) {
			return result;
		}
	}
	@SuppressWarnings("unchecked")
	private String getMemcachedInternationOptionValue(String code,String language,String type,List<String> messages){
		Object obj=MemCachedUtils.get((ContextUtils.getCompanyId()+"_"+code+"_"+type).hashCode()+"");
		if(obj==null)return code;
		Map<String,String> interOpts=(Map<String,String>)obj;
		String interOpt=interOpts.get(language);
		if(StringUtils.isNotEmpty(interOpt))return getTextForPlaceholder(interOpt,messages);
		return code;
	}
	
	private String getTextForPlaceholder(String i18nVal,List<String> messages){
		if(messages==null||messages.size()<=0)return i18nVal;
		if(i18nVal==null)return "";
		String[] vals = i18nVal.split("\\{");
		if(vals.length<=0)return i18nVal;//表示没有占位符
		String result = "";
		for(int i=0;i<vals.length;i++){
			String info = "";
			int n = -1;
			if(vals[i].indexOf("}")>=0){
				n = Integer.valueOf(vals[i].substring(0,vals[i].indexOf("}")));
				info = vals[i].substring(vals[i].indexOf("}")+1);
			}else{
				info = vals[i];
			}
			String message = "";
			if(n!=-1)message = messages.get(n);
			result = result+(message+info);
		}
		return result;
	}

	public Long getSignIdByUserName(String userName) {
		return signatureManager.getSignIdByUserName(userName);
	}
	public Long getSignIdByUserId(Long userId) {
		return signatureManager.getSignIdByUserId(userId);
	}

	public DatasourceSetting getDatasourceByCode(String code) {
		return BeanUtil.turnToModelDatasource(datasourceSettingManager.getDatasourceSettingByCode(code));
	}

	public DatasourceSetting getDatasourceByInterfaceCode(String interfaceCode) {
		return BeanUtil.turnToModelDatasource(interfaceSettingManager.getDatasourceByInterfaceCode(interfaceCode));
	}

	public boolean isInterfaceEnable(String interfaceCode) {
		InterfaceSetting interfaceSetting = interfaceSettingManager.getInterfaceSettingByCode(interfaceCode);
		if(interfaceSetting.getDataState()!=null&&interfaceSetting.getDataState()==DataState.ENABLE)return true;
		return false;
	}

	public boolean isTimeEnable(String timeCode) {
		TimedTask time = jobInfoManager.getJobInfoByCode(timeCode, ContextUtils.getSystemCode());
		if(time.getDataState()!=null&&time.getDataState()==DataState.ENABLE)return true;
		return false;
	}

	public void remindForInterface(String interfaceCode, String message) throws Exception{
		InterfaceSetting interfaceSetting = interfaceSettingManager.getInterfaceSettingByCode(interfaceCode);
		emailRemind(interfaceSetting.getEmails(),message,interfaceSetting.getName());//邮箱提醒
		rtxRemind(interfaceSetting.getRtxAccounts(),message,interfaceSetting.getName());//rtx提醒
		swingRemind(interfaceSetting.getOfficeHelperReminderIds(),message,interfaceSetting.getName());//办公助手提醒
		telephoneRemind(interfaceSetting.getPhoneReminderNums(),message,interfaceSetting.getName());//短信提醒
		
	}

	public void remindForTime(String timeCode, String message) throws Exception{
		TimedTask time = jobInfoManager.getJobInfoByCode(timeCode, ContextUtils.getSystemCode());
		emailRemind(time.getEmails(),message,time.getCode());//邮箱提醒
		rtxRemind(time.getRtxAccounts(),message,time.getCode());//rtx提醒
		swingRemind(time.getOfficeHelperReminderIds(),message,time.getCode());//办公助手提醒
		telephoneRemind(time.getPhoneReminderNums(),message,time.getCode());//短信提醒
		
	}
	/**
	 * 邮箱提醒
	 * @param emails
	 * @param message
	 */
	private void emailRemind(String emails,String message,String interfaceName){
		Set<String> mailAddrs = new HashSet<String>();
		if(StringUtils.isNotEmpty(emails)){
			String[] emailAddrs = emails.split(",");
			for(String email: emailAddrs){
				mailAddrs.add(email);
			}
			String subject = PropUtils.getProp("interface.remind.subject");
			if(subject==null)subject = interfaceName+"接口提醒";
			AsyncMailUtils.sendMail(mailAddrs,subject , message);
		}
	}
	private void rtxRemind(String rtxAccounts,String message,String interfaceName){
		if(StringUtils.isNotEmpty(rtxAccounts)){
			String subject = PropUtils.getProp("interface.remind.mail.subject");
			if(subject==null)subject = interfaceName+"接口提醒";
			String[] rtxAddrs = rtxAccounts.split(",");
			for(String rtxAccount: rtxAddrs){
				rtx.RtxMsgSender.sendNotify(rtxAccount, subject, "1", message , "",ContextUtils.getCompanyId());
			}
		}
		
	}
	private void swingRemind(String reminderIds,String message,String interfaceName) throws Exception{
		if(StringUtils.isNotEmpty(reminderIds)){
			String subject = PropUtils.getProp("interface.remind.mail.subject");
			if(subject==null)subject = interfaceName+"接口提醒";
			String[] remindUserIds = reminderIds.split(",");
			for(String userid: remindUserIds){
				com.norteksoft.product.api.entity.User user = ApiFactory.getAcsService().getUserById(Long.parseLong(userid));
				ApiFactory.getPortalService().addMessage(ContextUtils.getSystemCode(), ContextUtils.getUserName(), ContextUtils.getUserId(), user.getId(),subject, message,"",true,null);
			}
		}
	}
	private void telephoneRemind(String telephones,String message,String interfaceName){
		
	}

	public boolean isHolidayDay(Date date,Long branchId){
		List<Date> holidays = holidayManager.getHolidays(branchId);//获得节假日
		date = holidayManager.clearDateTime(date);
		if(holidays.size()>0){
			if(holidays.contains(date)){
				return true;
			}
		}
		List<Date> workDates = holidayManager.getWorkDates(branchId);//获得工作日
		date = holidayManager.clearDateTime(date);
		if(workDates.size()>0){
			if(workDates.contains(date)){
				return false;
			}
		}
		if(branchId!=null&&holidays.size()<=0&&workDates.size()<=0){//branchId不为空表示是分支，如果分支没有设置节假日和工作日则使用集团的设置
			boolean isHoliday = isHolidayInCompany(date);
			if(isHoliday)return true;
			boolean isWorkDate = isWorkDateInCompany(date);
			if(isWorkDate)return false;
		}
		//如果没有特殊设置周六日，则默认周六、周日为节假日
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		if(cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || 
				cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY){
			return true;
		}
		return false;
	}
	
	private boolean isHolidayInCompany(Date date){
		List<Date> holidays = holidayManager.getHolidays(null);//获得节假日
		date = holidayManager.clearDateTime(date);
		if(holidays.size()>0){
			if(holidays.contains(date)){
				return true;
			}
		}
		return false;
	}
	/**
	 * 
	 * @param date
	 * @return 返回true表示是工作日，不是节假日；返回false并不表示当前日期是节假日
	 */
	private boolean isWorkDateInCompany(Date date){
		List<Date> workDates = holidayManager.getWorkDates(null);//获得工作日
		date = holidayManager.clearDateTime(date);
		if(workDates.size()>0){
			if(workDates.contains(date)){
				return true;
			}
		}
		return false;
	}

	public String[] getWorkTimeSetting(Long branchId) {
		String[] result = new String[2];
		Object[] hsetting = holidayManager.getWorkHolidaySetting(branchId);
		if(hsetting!=null&&hsetting.length==3){
			result[0] = hsetting[1].toString();
			result[1] = hsetting[2].toString();
		}else{//如果没有设置工作时间，则默认为9点到18点
			if(branchId!=null){//当子公司没有设置节假日时，按集团公司设置的为准
				hsetting = holidayManager.getWorkHolidaySetting(null);
			}
			if(hsetting!=null&&hsetting.length==3){
				result[0] = hsetting[1].toString();
				result[1] = hsetting[2].toString();
			}else{
				result[0] =  "09:00";
				result[1] = "18:00";
				
			}
		}
		return result;
	}

	public Date clearDateTime(Date date) {
		return holidayManager.clearDateTime(date);
	}

	public long getWorkTimes(Long branchId) {
		String[] workTimeSettting = getWorkTimeSetting(branchId);
		String startTime = workTimeSettting[0];
		String endTime = workTimeSettting[1];
		Date startDate = getWorkDate(new Date(),startTime);
		Date endDate = getWorkDate(new Date(),endTime);
		if(endDate.getTime()<startDate.getTime()){//配置的结束时间小于开始时间，则返回0
			return 0;
		}
		return endDate.getTime()-startDate.getTime();//每天的工作时间换算成的毫秒数
	}
	
	public Date getWorkDate(Date date,String workTime){
		String[] times = workTime.split(":");
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		Date resultDate = clearDateTime(cal.getTime());
		cal.setTime(resultDate);
		cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(times[0]));
		cal.set(Calendar.MINUTE, Integer.parseInt(times[1]));
		resultDate = cal.getTime();
		return resultDate;
	}


	public String getInternationOptionValueByLanguage(String code, String language) {
		return getMemcachedInternationOptionValue(code,language,InternationType.PUBLIC_RESOURCE.toString(),null);
	}
	 
	public String getInternationOptionValue(String code) {
		return getMemcachedInternationOptionValue(code,ContextUtils.getCurrentLanguage(),InternationType.PUBLIC_RESOURCE.toString(),null);
	}
	 
	public String getInternationOptionValue(String code,String type) {
		return getMemcachedInternationOptionValue(code,ContextUtils.getCurrentLanguage(),type,null);
	}
	 
	public String getInternationOptionValue(String code,String language,String type) {
		return getMemcachedInternationOptionValue(code,language,type,null);
	}

	public String getInternationOptionValue(String code, List<String> messages) {
		return getMemcachedInternationOptionValue(code,ContextUtils.getCurrentLanguage(),InternationType.PUBLIC_RESOURCE.toString(),messages);
	}

	public String getInternationOptionValue(String code, String language,
			String type, List<String> messages) {
		return getMemcachedInternationOptionValue(code,language,type,messages);
	}

	public String getInternationOptionValueByLanguage(String code,
			String language, List<String> messages) {
		return getMemcachedInternationOptionValue(code,language,InternationType.PUBLIC_RESOURCE.toString(),messages);
	}

	public String getInternationOptionValue(String code, String type,
			List<String> messages) {
		return getMemcachedInternationOptionValue(code,ContextUtils.getCurrentLanguage(),type,messages);
	}

}
