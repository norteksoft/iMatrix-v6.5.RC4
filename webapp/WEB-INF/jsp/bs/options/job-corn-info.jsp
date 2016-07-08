<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/setting-taglibs.jsp"%>
<table>
		<tbody>
			<tr>
				<td>
					<s:text name="bs.timedTaskMode"></s:text><select name="typeEnum" id="typeEnum" onchange="typeChange(this.value);">
									<option value=""><s:text name="basicSetting.choose"></s:text></option>
								<s:iterator value="@com.norteksoft.bs.options.enumeration.TimingType@values()" var="FK">
									<option value="${FK}"><s:text name="%{code}"></s:text></option>
								</s:iterator>
							</select>
				</td>
			</tr>
			<tr id="tr_everyMonth" style="display: none;">
				<td>
					<s:text name="bs.timerTaskPerMonth"></s:text>：<select id="everyMonth" name="everyMonth"  class="customRequired">
								<option value=""><s:text name="basicSetting.choose"></s:text></option>
								<s:iterator value="@com.norteksoft.bs.options.enumeration.DateEnum@values()" var="FK">
									<option value="${FK}"><s:text name="%{code}"></s:text></option>
								</s:iterator>
							</select>
				</td>
			</tr>
			<tr id="tr_everyWeek" style="display: none;">
				<td>
					<s:text name="bs.regularWeeklyTasks"></s:text>：<select id="everyWeek" name="everyWeek" multiple="multiple">
								<s:iterator value="@com.norteksoft.bs.options.enumeration.WeekEnum@values()" var="FK">
									<option value="${FK}"><s:text name="%{code}"></s:text></option>
								</s:iterator>
							</select>
				</td>
			</tr>
			<tr id="tr_everyDate" style="display: block;">
				<td>
					<s:text name="bs.timingTasksPerDay"></s:text>：<input id="everyDate" name="everyDate" value="" readonly="readonly" class="customRequired"/>
				</td>
			</tr>
			<tr id="tr_appointTime" style="display: none;">
				<td>
					<s:text name="bs.dateSpecified"></s:text>：<input id="appointTime" name="appointTime" value="" readonly="readonly" class="customRequired"/>
				</td>
			</tr>
			<tr id="tr_appointSet" style="display: none;">
				<td>
					<s:text name="bs.timingAdvancedSettings"></s:text>：<input id="appointSet" name="appointSet" value="" maxlength="30" 
						class="customRequired"/><span style="color: red;margin: 40px;">(<s:text name="bs.mustBeSetIn"></s:text>)</span>
				</td>
			</tr>
			<tr id="tr_appointSet_discription" style="display: none;">
				<td>
					<s:text name="bs.advancedSettingInstructions"></s:text>：<a href="#" onclick="showDiscription();"><s:text name="bs.view"></s:text></a>
				</td>
			</tr>
			<tr id="tr_intervalTime_type" style="display: none;">
				<td>
					<s:text name="bs.timeIntervalUnit"></s:text>：<select id="intervalType" onchange="intervalTypeChange(this.value);" class="customRequired">
					                <option value=""><s:text name="basicSetting.choose"></s:text></option>
									<option value="secondType"><s:text name="bs.minute"></s:text></option>
									<option value="hourType"><s:text name="bs.hour"></s:text></option>
							  </select>
				</td>
			</tr>
			<tr id="tr_intervalTime_second" style="display: none;">
				<td>
					<s:text name="bs.timeIntervalMinutes"></s:text>：<input id="everySecond" name="everySecond" value=""  onclick="secondOrHour(this);" class="customRequired intervalTimeValidate"/></br>
				</td>
			</tr>
			<tr id="tr_intervalTime_hour" style="display: none;">
				<td>
					<s:text name="bs.timeInterval"></s:text>：<input id="everyHour" name="everyHour" value="" onclick="secondOrHour(this);" class="customRequired intervalTimeValidate"/>
				</td>
			</tr>
		</tbody>
	</table>
