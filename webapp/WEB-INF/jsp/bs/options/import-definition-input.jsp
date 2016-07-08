<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/setting-taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<title>导入管理</title>
	<%@ include file="/common/setting-iframe-meta.jsp"%>
</head>
<body>
<div class="ui-layout-center">
	<aa:zone name="import_main">
		<script type="text/javascript">
			$(document).ready(function() {
				$( "#tabs" ).tabs();
			});
		</script>
		<aa:zone name="btnZone">
			<div class="opt-btn">
				<button class="btn" onclick="saveImportDefinition('${settingCtx}/options/import-definition-save.htm');"><span><span><s:text name="menuManager.save"></s:text></span></span></button>
				<button class="btn" onclick='setPageState();ajaxSubmit("defaultForm","${settingCtx}/options/import-definition.htm","import_main");'><span><span ><s:text name="menuManager.back"></s:text></span></span></button>
			</div>
		</aa:zone>
		<div id="opt-content">
			<div id="tabs">
				<ul>
					<li><a href="#tabs-1" onclick="pageUlChange('a');"><s:text name="basicSetting.baseInfo"></s:text></a></li>
					<li><a href="#tabs-1" onclick="pageUlChange('b')"><s:text name="basicSetting.fieldInfo"></s:text></a></li>
				</ul>
				<div id="tabs-1">
					<aa:zone name="importContext">
							<div id="message" style="display:none;"><s:actionmessage theme="mytheme" /></div>
							<form id="inputForm" name="inputForm" action="" method="post">
								<input type="hidden" name="importDefinitionId" id="importDefinitionId" value="${importDefinitionId}"/>
								<table class="form-table-without-border">
									<tr>
										<td class="content-title" style="width: 90px;"><s:text name="basicSetting.code"></s:text>：</td>
										<td> <input id="code" name="code" value="${code}" maxlength="100" ></input><span class="required">*</span> </td>
										<td></td>
									</tr>	
									<tr>
										<td class="content-title"><s:text name="basicSetting.otherName"></s:text>：</td>
										<td> <input id="alias" name="alias" value="${alias}" maxlength="255"></input><span class="required">*</span> </td>
										<td></td>
									</tr>
									<tr id="content-title">
										<td class="content-title"><s:text name="basicSetting.tableName"></s:text>：</td>
										<td><input id="name" name="name" value="${name}"  maxlength="255"></input></td>
										<td></td>
									</tr>
									<tr id="content-title">
										<td class="content-title"><s:text name="basicSetting.textImportType"></s:text>：</td>
										<td>
											<select name="importType" onchange="importTypeChange(this);">
												<option value=""><s:text name="basicSetting.choose"></s:text></option>
												<s:iterator value="@com.norteksoft.bs.options.enumeration.ImportType@values()" var="importTypeVar">
													<option <s:if test="#importTypeVar==importType">selected="selected"</s:if> value="${importTypeVar}"><s:text name="%{code}"></s:text></option>
												</s:iterator>
											</select>
										</td>
										<td></td>
									</tr>
									
									<tr class="content-title" id="divideTr" style="display: none;">
										<td class="content-title"><s:text name="basicSetting.separator"></s:text>：</td>
										<td><input id="divide" name="divide" value="${divide}"  maxlength="255"></input></td>
										<td></td>
									</tr>
									<tr id="content-title">
										<td class="content-title"><s:text name="basicSetting.importType"></s:text>：</td>
										<td>
											<select name="importWay">
												<s:iterator value="@com.norteksoft.bs.options.enumeration.ImportWay@values()" var="importWayVar">
													<option <s:if test="#importWayVar==importWay">selected="selected"</s:if> value="${importWayVar}"><s:text name="%{code}"></s:text></option>
												</s:iterator>
											</select>
										</td>
										<td></td>
									</tr>
									<tr id="content-title">
										<td class="content-title"><s:text name="basicSetting.relationaTable"></s:text>：</td>
										<td><input id="relevanceName" name="relevanceName" value="${relevanceName}"  maxlength="255"></input></td>
										<td></td>
									</tr>
									<tr id="content-title">
										<td class="content-title"><s:text name="basicSetting.foreiinKey"></s:text>：</td>
										<td><input id="foreignKey" name="foreignKey" value="${foreignKey}"  maxlength="255"></input></td>
										<td></td>
									</tr>
									<tr id="content-title">
										<td class="content-title"><s:text name="basicSetting.headerRowNumber"></s:text>：</td>
										<td><input id="headRow" name="headRow" value="${headRow}" onkeyup="value=value.replace(/[^1-9\.]/g,'');" maxlength="5"></input></td>
										<td></td>
									</tr>
									<tr id="content-title">
										<td class="content-title"><s:text name="basicSetting.importRow"></s:text>：</td>
										<td>
										<input id="startRow" name="startRow" value="${startRow}" maxlength="5"></input>
										<s:text name="basicSetting.to"></s:text>
										<input id="endRow" name="endRow" value="${endRow}" maxlength="5"></input>
										<s:text name="basicSetting.minusInfo"></s:text>
										</td>
										<td></td>
									</tr>
									<tr>
										<td class="content-title"><s:text name="basicSetting.remark"></s:text>：</td>
										<td> </td>
										<td></td>
									</tr>
									<tr>
										<td class="content-title"></td>
										<td colspan="2"> 
											<textarea id="remark" name="remark" cols="55" rows="10" style="font-family:Arial,Helvetica,sans-serif;overflow: auto;">${remark}</textarea>
										</td>
									</tr>
								</table>
							</form>	
							<script type="text/javascript">
								$(document).ready(function() {
									var importType="${importType}";
									if('TXT_DIVIDE'==importType){
										$("#divideTr").show();
									}
								});
							</script>
					</aa:zone>
				</div>
			</div>
		</div>
	</aa:zone>
</div>
</body>
</html>