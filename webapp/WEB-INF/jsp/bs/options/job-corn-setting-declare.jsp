<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/setting-taglibs.jsp"%>
<div id="discription" style="display: none;margin: 15px;">
	<table class="form-table-border-left">
		<tr>
			<td>
				<!-- 定时器的配置:按照Quartz表达式的规则配置，表达式从左到右每一位分别表示：秒（0-59） 、分（0-59）、时（0-23）、日期（0-31）、月份（1-12）、星期（1-7）、年（1907-2099），其中“年”可选，其他必填，每一位都有取值范围，不能随意取值，详细规则查阅Quartz表达式的相关资料，以下是范例说明: -->
				<s:text name="bs.jobCornSettingDeclare.timerConfiguration"></s:text><br/>
				<!-- "0 0 12 * * ?" 每天中午12点触发 -->
				"0 0 12 * * ?" <s:text name="bs.jobCornSettingDeclare.TriggerAtTwelveNoon"></s:text><br/>
				<!-- "0 15 10 ? * *" 每天上午10:15触发 -->
				"0 15 10 ? * *" <s:text name="bs.jobCornSettingDeclare.everyMorningTen"></s:text><br/>
				<!-- "0 15 10 * * ?" 每天上午10:15触发 -->
				"0 15 10 * * ?" <s:text name="bs.jobCornSettingDeclare.everyMorningTen"></s:text><br/>
				<!-- "0 15 10 * * ? *" 每天上午10:15触发  -->
				"0 15 10 * * ? *" <s:text name="bs.jobCornSettingDeclare.everyMorningTen"></s:text><br/>
				<!-- "0 15 10 * * ? 2005" 2005年的每天上午10:15触发 -->
				"0 15 10 * * ? 2005" <s:text name="bs.jobCornSettingDeclare.everyMorningTenYear"></s:text><br/>
				<!-- "0 * 14 * * ?" 在每天下午2点到下午2:59期间的每1分钟触发 -->
				"0 * 14 * * ?" <s:text name="bs.jobCornSettingDeclare.afternoonOneMinutesTrigger"></s:text><br/>
				<!-- "0 0/5 14 * * ?" 在每天下午2点到下午2:55期间的每5分钟触发 -->
				"0 0/5 14 * * ?" <s:text name="bs.jobCornSettingDeclare.afternoonFiveMinutes"></s:text><br/>
				<!-- "0 0/5 14,18 * * ?" 在每天下午2点到2:55期间和下午6点到6:55期间的每5分钟触发 -->
				"0 0/5 14,18 * * ?" <s:text name="bs.jobCornSettingDeclare.pmFiveMinutes"></s:text><br/>
				<!-- "0 0-5 14 * * ?" 在每天下午2点到下午2:05期间的每1分钟触发 -->
				"0 0-5 14 * * ?" <s:text name="bs.jobCornSettingDeclare.pmOneMinutes"></s:text><br/>
				<!-- "0 10,44 14 ? 3 WED" 每年三月的星期三的下午2:10和2:44触发 -->
				"0 10,44 14 ? 3 WED" <s:text name="bs.jobCornSettingDeclare.wednesdayMarch"></s:text><br/>
				<!-- "0 15 10 ? * MON-FRI" 周一至周五的上午10:15触发 -->
				"0 15 10 ? * MON-FRI" <s:text name="bs.jobCornSettingDeclare.mondayFriday"></s:text><br/>
				<!-- "0 15 10 15 * ?" 每月15日上午10:15触发 -->
				"0 15 10 15 * ?" <s:text name="bs.jobCornSettingDeclare.fifteenDayAm"></s:text><br/>
				<!-- "0 15 10 L * ?" 每月最后一日的上午10:15触发 -->
				"0 15 10 L * ?" <s:text name="bs.jobCornSettingDeclare.monthLastDayAm"></s:text><br/>
				<!-- "0 15 10 ? * 6L" 每月的最后一个星期五上午10:15触发 -->
				"0 15 10 ? * 6L" <s:text name="bs.jobCornSettingDeclare.monthLastFridayAm"></s:text><br/>
				<!-- "0 15 10 ? * 6L 2002-2005" 2002年至2005年的每月的最后一个星期五上午10:15触发 -->
				"0 15 10 ? * 6L 2002-2005" <s:text name="bs.jobCornSettingDeclare.yearMonthLastFridayAm"></s:text><br/>
				<!-- "0 15 10 ? * 6#3" 每月的第三个星期五上午10:15触发 -->
				"0 15 10 ? * 6#3" <s:text name="bs.jobCornSettingDeclare.thirdFridayMorning"></s:text><br/>
			</td>
		</tr>
	</table>
<br></br>
</div>