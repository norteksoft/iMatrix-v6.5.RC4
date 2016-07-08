<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/mms-taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<title><s:text name="mms.formManager"/></title>
</head>
<body>
<div class="ui-layout-center">
	<aa:zone name="btnZone">
		<div class="opt-btn">
			<button class="btn" onclick="saveSetting();"><span><span ><s:text name="menuManager.save"></s:text> </span></span></button>
			<button class="btn" onclick="setPageState();returnTableList();"><span><span ><s:text name="menuManager.back"></s:text></span></span></button>
		</div>
	</aa:zone>
	<aa:zone name="contentZone">
		<form id="contentFrom" name="contentFrom" method="post">
			<input type="hidden" name="states" value="${states}"/>
			<input type="hidden" id="men_id"  name="menuId" value="${menuId }"/>
		</form>
		<aa:zone name="columnList">
		<div id="msg"></div>
		<div id="message" style="display:none;"><s:actionmessage theme="mytheme" /></div>
			<form action="" id="inputForm" name="inputForm" method="post">
				<input type="hidden" name="settingId" id="settingId" value="${generateSetting.id}"/>
				<input type="hidden" name="tableId" id="tabelId" value="${generateSetting.tableId}"/>
				<table class="form-table-without-border">
					<tr>
						<td class="content-title" style="width: 90px;"><s:text name="formManager.entityGenerate"></s:text>：</td>
						<td> <s:select theme="simple" list="#{'true':getText('common.yes'),'false':getText('common.no')}" name="generateSetting.entitative"></s:select> </td>
						<td></td>
					</tr>	
					<tr>
						<td class="content-title"><s:text name="formManager.throughWorkFlow"></s:text>：</td>
						<td> <s:select id="flowable" theme="simple" list="#{'true':getText('common.yes'),'false':getText('common.no')}" name="generateSetting.flowable"></s:select>  </td>
						<td></td>
					</tr>
					<tr>
						<td class="content-title"><s:text name="formManager.workFlowCode"></s:text>：</td>
						<td> <input id="workflowCode" name="generateSetting.workflowCode" value="${generateSetting.workflowCode}"/>  </td>
						<td></td>
					</tr>
					<tr>
						<td class="content-title"><s:text name="formManager.chooseTemplate"></s:text>：</td>
						<td>
							<input type="radio" name="generateSetting.templateEnum" <s:if test="generateSetting.templateEnum==null||generateSetting.templateEnum.toString()=='TWO_COLUMN'">checked="checked"</s:if> value="TWO_COLUMN"/><s:text name="template.two.column"></s:text>
							<input type="radio" name="generateSetting.templateEnum" <s:if test="generateSetting.templateEnum.toString()=='FOUR_COLUMN'">checked="checked"</s:if> value="FOUR_COLUMN"/><s:text name="template.four.column"></s:text>
							<input type="radio" name="generateSetting.templateEnum" <s:if test="generateSetting.templateEnum.toString()=='SIX_COLUMN'">checked="checked"</s:if> value="SIX_COLUMN"/><s:text name="template.six.column"></s:text>
						</td>
						<td></td>
					</tr>
				</table>
			</form>
		</aa:zone>
	</aa:zone>
</div>
</body>
</html>