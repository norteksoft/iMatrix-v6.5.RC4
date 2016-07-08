package com.norteksoft.product.api;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.norteksoft.product.api.entity.DatasourceSetting;
import com.norteksoft.product.api.entity.InternationOption;
import com.norteksoft.product.api.entity.Option;
import com.norteksoft.product.api.entity.OptionGroup;


public interface SettingService {
	
	/**
	 * 根据选项组编号查询选项组默认值
	 * @param optionGroupCode
	 * @return
	 */
	String getOptionGroupDefaultValue(String optionGroupCode);
	
	/**
	 * 根据选项组ID查询选项组默认值
	 * @param optionGroupCode
	 * @return
	 */
	String getOptionGroupDefaultValue(Long optionGroupId);
	
	/**
     * 查询所有选项组
     * @return 选项组集合
     */
    public List<OptionGroup> getOptionGroups();
	
    /**
     * 根据选项组编号查询选项组
     * @param code 选项组编号
     * @return 选项组
     */
    public OptionGroup getOptionGroupByCode(String code);

    /**
     * 根据选项组名称查询选项组
     * @param code 选项组编号
     * @return 选项组
     */
    public OptionGroup getOptionGroupByName(String name);
    
    /**
     * 根据选项组ID查询所有选项 
     * @param optionGroupId 选项组id
     * @return 选项集合
     */
    public List<Option> getOptionsByGroup(Long optionGroupId);
    
    /**
     * 根据选项组编号查询选项
     * @param code 选项组编号
     * @return 选项集合
     */
    public List<Option> getOptionsByGroupCode(String code);
    
    /**
     * 根据选项组名称查询选项
     * @param name 选项名称 
     * @return 选项集合
     */
    public List<Option> getOptionsByGroupName(String name);
    
    /**
     * 查询给定日期段中所有的节假日和工作日
     * @param startDate
     * @param endDate
     * @return Map<String, List<Date>>  key[spareDate:节假日, workDate:工作日]
     */
    public Map<String, List<Date>> getHolidaySettingDays(Date startDate, Date endDate);
    /**
     * 查询给定日期段中所有的节假日和工作日
     * @param startDate
     * @param endDate
     * @param branchId
     * @return Map<String, List<Date>>  key[spareDate:节假日, workDate:工作日]
     */
    public Map<String, List<Date>> getHolidaySettingDays(Date startDate, Date endDate,Long branchId);
    /**
     * 查询当前给定的时间在当前分支是否是节假日
     * @param date 给定时间
     * @param branchId 分支id，值为null表示是集团公司
     * @return true 表示是节假日，false 表示是工作日
     */
    public boolean isHolidayDay(Date date,Long branchId);
    /**
     * 获得分支的工作时间设置
     * @param branchId
     * @return String[]  [开始时间,结束时间]，例如[09:00,18:00]
     */
    public String[] getWorkTimeSetting(Long branchId);
    /**
     * 获得当前分支的每天工作时长
     */
    public long getWorkTimes(Long branchId);
    /**
     * 获得给定时间date的开始工作时间或结束的工作时间
     * @param date 
     * @param workTimeSetting 节假日设置中工作时间的设置。开始工作时间或结束工作时间，例如:09:00或18:00
     * @return
     */
    public Date getWorkDate(Date date,String workTimeSetting);
    /**
     * 获得日期，清除时、分、秒、毫秒
     * @param date
     * @return 日期，例如：参数为2014-10-10-18:15:20，使用该api结果为：2014-10-10
     */
    public Date clearDateTime(Date date);
    /**
     * 读取properties国际化文件，获得编码对应的value值
     * @param code
     * @return 编码对应的国际化值
     */
    public String getText(String code);
    /**
     * 根据国际化配置，获得公用资源类型(PUBLIC_RESOURCE)下的编码和当前用户使用的语言对应的value值
     * @param code  国际化编号 
     * @return 编码对应的国际化值
     */
    public String getInternationOptionValue(String code);
    /**
     * 根据国际化配置，获得公用资源类型(PUBLIC_RESOURCE)下的编码和当前用户使用的语言对应的value值
     * @param code  国际化编号 
     * @param messages  替换的占位符的值，例如国际化值为：删除成功{0}个,删除失败{1}个，{0},{1}的值需要在该集合中传过去
     * @return 编码对应的国际化值
     */
    public String getInternationOptionValue(String code,List<String> messages);
    /**
     * 根据国际化配置，获得指定类型下的编码和指定语言对应的value值
     * @param code  国际化编号 
     * @param language 语言
     * @param type 资源类型，资源类型可以为:MENU_RESOURCE,WORKFLOW_RESOURCE,PUBLIC_RESOURCE
     * @return 编码对应的国际化值
     */
    public String getInternationOptionValue(String code,String language,String type);
    /**
     * 根据国际化配置，获得指定类型下的编码和指定语言对应的value值
     * @param code  国际化编号 
     * @param language 语言
     * @param type 资源类型，资源类型可以为:MENU_RESOURCE,WORKFLOW_RESOURCE,PUBLIC_RESOURCE
     * @param messages  替换的占位符的值，例如国际化值为：删除成功{0}个,删除失败{1}个，{0},{1}的值需要在该集合中传过去
     * @return 编码对应的国际化值
     */
    public String getInternationOptionValue(String code,String language,String type,List<String> messages);
    /**
     * 根据国际化配置，获得公用资源类型(PUBLIC_RESOURCE)下的编码和指定语言对应的value值
     * @param code  国际化编号 
     * @param language 语言
     * @return 编码对应的国际化值
     */
    public String getInternationOptionValueByLanguage(String code,String language);
    /**
     * 根据国际化配置，获得公用资源类型(PUBLIC_RESOURCE)下的编码和指定语言对应的value值
     * @param code  国际化编号 
     * @param language 语言
     * @param messages  替换的占位符的值，例如国际化值为：删除成功{0}个,删除失败{1}个，{0},{1}的值需要在该集合中传过去
     * @return 编码对应的国际化值
     */
    public String getInternationOptionValueByLanguage(String code,String language,List<String> messages);
    /**
     * 根据国际化配置，获得指定类型下的编码和当前用户使用的语言对应的value值
     * @param code  国际化编号 
     * @param type 资源类型，资源类型可以为:MENU_RESOURCE,WORKFLOW_RESOURCE,PUBLIC_RESOURCE
     * @return 编码对应的国际化值
     */
    public String getInternationOptionValue(String code,String type);
    /**
     * 根据国际化配置，获得指定类型下的编码和当前用户使用的语言对应的value值
     * @param code  国际化编号 
     * @param type 资源类型，资源类型可以为:MENU_RESOURCE,WORKFLOW_RESOURCE,PUBLIC_RESOURCE
     * @param messages  替换的占位符的值，例如国际化值为：删除成功{0}个,删除失败{1}个，{0},{1}的值需要在该集合中传过去
     * @return 编码对应的国际化值
     */
    public String getInternationOptionValue(String code,String type,List<String> messages);

    /**
     * 根据用户名称取得签章id
     * @param userName
     * @return
     */
    public Long getSignIdByUserName(String userName);
    /**
     * 根据用户id取得签章id
     * @param userId
     * @return
     */
    public Long getSignIdByUserId(Long userId);
    /**
     * 根据接口编码获得数据源配置
     * @param interfaceCode 接口编码
     * @return
     */
    public DatasourceSetting getDatasourceByInterfaceCode(String interfaceCode);
    /**
     * 根据编码获得数据源配置
     * @param code 数据源编码
     * @return
     */
    public DatasourceSetting getDatasourceByCode(String code);
    /**
     * 根据接口编码对应的接口中配置的提醒人，发送提醒消息
     * @param interfaceCode 接口编码
     * @param message 提醒消息
     */
    public void remindForInterface(String interfaceCode,String message) throws Exception;
    /**
     * 根据定时接口编码对应的接口中配置的提醒人，发送提醒消息
     * @param timeCode 定时接口编码
     * @param message 提醒消息
     */
    public void remindForTime(String timeCode,String message) throws Exception;
    /**
     * 接口是否启用
     * @param interfaceCode 接口编码
     * @return
     */
    public boolean isInterfaceEnable(String interfaceCode);
    /**
     * 定时接口是否启用
     * @param interfaceCode 接口编码
     * @return
     */
    public boolean isTimeEnable(String timeCode);
    
}
