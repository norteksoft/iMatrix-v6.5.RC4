<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/setting-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>

<aa:zone name="dict_zone">

	<div class="opt-btn">
		<button class="btn" onclick="$('#dictRankForm').submit();" hidefocus="true"><span><span><s:text name="menuManager.save"></s:text></span></span></button>
		<button class="btn" onclick="setPageState();ajaxSubmit('rankform','${settingCtx}/rank/list-data.htm','dict_zone');" hidefocus="true"><span><span ><s:text name="menuManager.back"></s:text></span></span></button>
	</div>

	<div id="backMsg"  style="display: none;"><s:actionmessage theme="mytheme"/></div>
	
	<form id="dictRankForm" name="dictRankForm" action="" method="post">
		<input id="bs_superior_id" type="hidden" name="id" value="${id}"/>
		
		<table class="Table" style="margin: 5px;">
	 		<tr style="height: 30px;">
				<td><span style="margin-left: 5px;"><s:text name="basicSetting.title"></s:text>：
					<input id="title" name="title" value="${title}" style="width: 12em;" maxlength="40"/><span class="required">*</span> </span></td></tr>
			<tr style="height: 30px;">
				<td><span style="margin-left: 5px;"><s:text name="basicSetting.person"></s:text>：
				<input id="userNames" name="userNames" value="${userNames}" style="width: 12em;" readonly="readonly"/>
				<a id="selectUser" href="#" onclick="chooseUser('selectUser','${id}');" title="<s:text name="basicSetting.chooseBtn"></s:text>"  class="small-btn"><span><span><s:text name="basicSetting.chooseBtn"></s:text></span></span></a>
				<span class="required">*</span></span>
				<div id="userDiv" style="display: none;">
				<s:iterator value="userInfos" status="ind">
					<input name="userInfos" value="${userInfos[ind.index]}"></input>
				</s:iterator>
				</div></td></tr>
			<tr style="height: 30px;">
			<td><span style="margin-left: 5px;"><s:text name="basicSetting.father"></s:text>：
			<input id="name" name="name" value="${name}" style="width: 12em;" readonly="readonly"/>
				<a id="selectSuperiorUser" href="#" onclick="chooseUser('selectSuperiorUser','${id}');" title="<s:text name="basicSetting.chooseBtn"></s:text>"  class="small-btn"><span><span><s:text name="basicSetting.chooseBtn"></s:text></span></span></a>
				<span class="required">*</span></span><input id="superiorUserId" name="userId" value="${userId}" type="hidden"/>
			<input id="superiorLoginName" name="loginName" value="${loginName}" type="hidden" /></td></tr>
		  </table>
	</form>
</aa:zone>

</html>
