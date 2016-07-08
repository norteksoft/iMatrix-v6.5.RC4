<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/setting-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<%@ include file="/common/setting-iframe-meta.jsp"%>
	<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
	<script type="text/javascript" src="${resourcesCtx}/widgets/multiselect/jquery.multiselect.min.js"></script>
	<link   type="text/css" rel="stylesheet" href="${resourcesCtx}/widgets/multiselect/jquery.multiselect.css" />
	
	<!-- 树 -->
	<script type="text/javascript" src="${resourcesCtx}/js/staff-tree.js"></script>
	<script type="text/javascript" src="${settingCtx}/js/interface.js"></script>
	<script type="text/javascript" src="${settingCtx}/js/job-info.js"></script>
	
	<title>定时设置</title>
	<script type="text/javascript">
	$(document).ready(function(){
		validateTimer();
		checkBoxSelect('everyWeek');
		timeFormat('everyDate');
		dateFormat('appointTime');
	});
	</script>
</head>
<body onload="">
<div class="ui-layout-center">
<div class="opt-body">
	<div class="opt-btn">
		<a class="btn" href="#" onclick="submitJobInfo();"><span><span><s:text name="portal.submit"></s:text></span></span></a>
	</div>
	<div id="opt-content" >
		<div id="message" style="display: none;"><font class='onSuccess'><nobr><s:text name="common.saved"></s:text>！</nobr></font></div>
		<form action="" name="jobInfoFrom" id="jobInfoFrom" method="post">
			<input  type="hidden" name="id" id="id" value="${id}"/>
			<input type="hidden" name="systemId" id="systemId" value="${systemId}"/>
			<table>
				<tbody>
					<tr><td><s:text name="bs.timedRunStatus"></s:text>：</td>
						<td><input readonly="readonly" type="text" id="runAsUserName" name="runAsUserName" value="${runAsUserName }"/><input 
								readonly="readonly" type="hidden" id="runAsUserNameId" /><input 
								type="hidden" id="runAsUser" name="runAsUser" value="${runAsUser }"/><a 
								href="#" onclick="selectPrincipal('runAsUserName','runAsUserNameId')" title='<s:text name="basicSetting.chooseBtn"></s:text>' 
								class="small-btn" id="selectBtn"><span><span><s:text name="basicSetting.chooseBtn"></s:text></span></span></a></td>
					</tr>
					<tr><td><s:text name="bs.timingTaskNumber"></s:text>：</td>
						<td><input type="text" name="code" id="jobCode" value="${code}" maxlength="60"/><span 
								style="color: red;margin: 40px;">(<s:text name="bs.mustBeUnique"></s:text>)</span></td>
					</tr>
					<tr><td><s:text name="bs.timingRequestType"></s:text>：</td>
						<td><select name="applyType" id="applyType">
								<s:iterator value="@com.norteksoft.bs.options.enumeration.ApplyType@values()" var="FK">
									<option value="${FK}"><s:text name="%{code}"></s:text></option>
								</s:iterator>
							</select>
						</td>
					</tr>
					<tr><td><s:text name="bs.timedTaskAddress"></s:text>：</td>
						<td><input type="text" name="url" id="url" value="${url}" maxlength="60"/><span 
								style="color: red;margin: 40px;">(<s:text name="bs.suchAs"></s:text>:"/rest/wf/delegate")</span>
						</td>
					</tr>
					<s:if test="id==null">
						<%@ include file="job-corn-info.jsp"%>
					</s:if>
				</tbody>
			</table>
			<table>
				<tbody>
					<tr><td><s:text name="bs.timedTaskNotes"></s:text>：</td>
						<td><input type="text" name="description" id="urlInfo" value="${description}" maxlength="60"/></td>
					</tr>
					<tr><td><s:text name="bs.dataSourceReference"></s:text>：</td>
						<td><select name="dataSourceId" id="dataSourceId">
								<option value="">--<s:text name="basicSetting.choose"></s:text>--</option>
								<s:iterator value="datasources">
									<option value="${id }" <s:if test="dataSourceId==id">selected="selected"</s:if>>${code }</option>
								</s:iterator>
							</select></td>
					</tr>
					<tr><td><s:text name="bs.mailAlert"></s:text>:</td>
						<td><input id="emails" name="emails" value="${emails }" style="width:300px;" />&nbsp;&nbsp;<a 
								href="#" onclick="remindUser('email');" class="small-btn"><span><span><s:text name="basicSetting.chooseBtn"></s:text></span></span></a>&nbsp;<a 
								href="#" onclick="clearValue('emails');" class="small-btn"><span><span><s:text name="menuManager.clear"></s:text></span></span></a></td>
					</tr>
					<tr><td><s:text name="bs.RTXReminder"></s:text>:</td>
						<td><input readonly="readonly" id="rtxAccounts" name="rtxAccounts" value="${rtxAccounts}" style="width:300px;" />&nbsp;&nbsp;<a 
								href="#" onclick="remindUser('RTX');" class="small-btn"><span><span><s:text name="basicSetting.chooseBtn"></s:text></span></span></a>&nbsp;<a 
								href="#" onclick="clearValue('rtxAccounts');" class="small-btn"><span><span><s:text name="menuManager.clear"></s:text></span></span></a></td>
					</tr>
					<tr><td><s:text name="bs.SMSAlert"></s:text>:</td>
						<td><input id="phoneReminderNums" name="phoneReminderNums" value="${phoneReminderNums}" style="width:300px;" onkeyup="value=this.value.replace(/[^-,0-9]/g,'');" />&nbsp;&nbsp;<a 
								href="#" onclick="remindUser('phone');" class="small-btn"><span><span><s:text name="basicSetting.chooseBtn"></s:text></span></span></a>&nbsp;<a 
								href="#" onclick="clearValue('phoneReminderNums');" class="small-btn" ><span><span><s:text name="menuManager.clear"></s:text></span></span></a></td>
					</tr>
					<tr><td><s:text name="bs.officeAssistantToRemindPeople"></s:text>:</td>
						<td><input readonly="readonly" id="officeHelperReminderNames" name="officeHelperReminderNames" value="${officeHelperReminderNames}" style="width:300px;" /><input 
								type="hidden" id="officeHelperReminderIds" name="officeHelperReminderIds" value="${officeHelperReminderIds}" />&nbsp;&nbsp;<a 
								href="#" onclick="remindUser('swing');" class="small-btn" ><span><span><s:text name="basicSetting.chooseBtn"></s:text></span></span></a>&nbsp;<a 
								href="#" onclick="clearValue('officeHelperReminderNames','officeHelperReminderIds');" class="small-btn" ><span><span><s:text name="menuManager.clear"></s:text></span></span></a></td>
					</tr>
				</tbody>
			</table>
		</form>
	</div>
</div>
</div>
<%@ include file="job-corn-setting-declare.jsp"%>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script src="${resourcesCtx}/widgets/timepicker/timepicker_<%=com.norteksoft.product.util.ContextUtils.getCurrentLanguage()%>.js" type="text/javascript"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
</html>