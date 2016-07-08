<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/mms-taglibs.jsp"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>列表管理</title>
	<%@ include file="/common/mms-iframe-meta.jsp"%>
</head>
<body>
<div class="ui-layout-center">
	<aa:zone name="viewList">
		<script type="text/javascript">
			$(document).ready(function() {
				$( "#tabs" ).tabs();
			});
		</script>
		<style type="text/css">
			#tabs,.ui-tabs .ui-tabs-nav li,.ui-jqgrid,.ui-jqgrid .ui-jqgrid-htable th div,.ui-jqgrid .ui-jqgrid-view,.ui-jqgrid .ui-jqgrid-hdiv,.ui-jqgrid .ui-jqgrid-bdiv{ position: static; }
		</style>
		<aa:zone name="btnZone">
			<div class="opt-btn">
				<button class="btn" onclick="saveView();"><span><span><s:text name="menuManager.save"></s:text></span></span></button>
				<button class="btn" onclick='setPageState();listViewBack("viewSaveForm","${mmsCtx }/form/list-view.htm");'><span><span ><s:text name="menuManager.back"></s:text></span></span></button>
			</div>
		</aa:zone>
		<div id="opt-content">
				<form id="defaultForm1" name="defaultForm1"action="">
					<input id="view_id1" type="hidden" name="viewId" value="${viewId }"></input>
					<input id="column_id" type="hidden" name="columnId"></input>
					<input id="menuId" name="menuId" value="${menuId}" type="hidden"></input>
					<input  name="dataTableName" value="${dataTableName}" type="hidden"></input>
				</form>
				<div id="tabs">
					<ul>
						<li><a href="#tabs-1" onclick="changeViewSet('basic',this);"><s:text name="formManager.basic"></s:text></a></li>
						<li><a href="#tabs-1" onclick="changeViewSet('column',this);"><s:text name="formManager.column"></s:text></a></li>
						<li><a href="#tabs-1" onclick="changeViewSet('groupHeader',this);"><s:text name="formManager.groupHeader"></s:text></a></li>
					</ul>
					<div id="tabs-1">
						<aa:zone name="viewZone">
							<div id="message" style="display:none;"><s:actionmessage theme="mytheme" /></div>
							<input id="frozenColumnAmount" value="${frozenColumnAmount }" type="hidden"/>
							<input id="haveGroupHeader" value="${haveGroupHeader }" type="hidden"/>
							<form action="" name="viewSaveForm" id="viewSaveForm" method="post"> 
								<input type="hidden" id="dataTableName" name="dataTableName" value="${dataTableName }"/>
								<input type="hidden" name="viewId" value="${viewId}" id="viewId"/>
								<input id="menuId" name="menuId" value="${menuId}" type="hidden"></input>
								<table class="form-table-without-border">
								<s:if test="id==null">
									<tr>
										<td class="content-title" style="width:200px"><s:text name="menuManager.code"></s:text>：</td>
							  			<td><s:textfield theme="simple" id="code" name="code" maxlength="64" size="60"></s:textfield><span class="required">*</span></td>
							  			<td><span id="codeTip"></span></td>
									</tr>
								</s:if><s:else>
									<tr>
										<td class="content-title" style="width:200px"><s:text name="menuManager.code"></s:text>：</td>
							  			<td><s:textfield readonly="true" theme="simple" id="code" name="code" maxlength="64" size="60"></s:textfield><span class="required">*</span></td>
							  			<td><span id="codeTip"></span></td>
									</tr>
								</s:else>
									<tr>
										<td class="content-title"><s:text name="menuManager.name"></s:text>：</td>
							  			<td><s:textfield  theme="simple" id="name" name="name" maxlength="64" size="60"></s:textfield><span class="required">*</span></td>
							  			<td><span id="nameTip"></span></td>
									</tr>
									<tr>
										<td class="content-title"><s:text name="formManager.multiselect"></s:text>：</td>
							  			<td>
							  				<s:select theme="simple" list="#{false:getText('common.no'),true:getText('common.yes')}" name="multiSelect"></s:select>
										</td>
							  			<td>
							  			</td>
									</tr>
									<tr>
										<td class="content-title"><s:text name="formManager.onlyClickCheckboxToSelected"></s:text>：</td>
							  			<td>
							  				<s:select theme="simple" list="#{false:getText('common.no'),true:getText('common.yes')}" name="multiboxSelectOnly"></s:select>
										</td>
							  			<td>
							  			</td>
									</tr>
									<tr>
										<td class="content-title"><s:text name="formManager.showNumber"></s:text>：</td>
							  			<td><s:select theme="simple" list="#{false:getText('common.no'),true:getText('common.yes')}" name="rowNumbers"></s:select>
							  			</td>
							  			<td>
							  			</td>
									</tr>
									<tr>
										<td class="content-title"><s:text name="formManager.whetherOperation"></s:text>：</td>
							  			<td><s:select theme="simple" list="#{false:getText('common.no'),true:getText('common.yes')}" name="editable"></s:select>
							  			</td>
							  			<td>
							  			</td>
									</tr>
									<tr>
										<td class="content-title"><s:text name="formManager.columnSet"></s:text>：</td>
							  			<td><s:textfield  theme="simple" id="actWidth" name="actWidth" maxlength="6" size="60"></s:textfield></td>
							  			<td><span id="actWidthTip"></span></td>
									</tr>
									<tr>
										<td class="content-title"><s:text name="formManager.pagination"></s:text>：</td>
							  			<td><s:select theme="simple" list="#{false:getText('common.no'),true:getText('common.yes')}" name="pagination"></s:select>
							  			</td>
							  			<td>
							  			</td>
									</tr>
									<tr>
										<td class="content-title"><s:text name="formManager.startQuery"></s:text>：</td>
										<td>
											<select name="startQuery">
												<s:iterator value="@com.norteksoft.mms.form.enumeration.StartQuery@values()" var="startQueryVar">
													<option <s:if test="#startQueryVar==startQuery">selected="selected"</s:if> value="${startQueryVar}"><s:text name="%{code}"></s:text></option>
												</s:iterator>
											</select>
										</td>
										<td></td>
									</tr>
									<tr>
										<td class="content-title"><s:text name="formManager.whetherOperation"></s:text><s:text name="formManager.showQueryWay"></s:text>：</td>
										<td>
											<s:select theme="simple" list="#{false:getText('formManager.embedded'),true:getText('formManager.popUp')}" name="popUp"></s:select>
										</td>
										<td></td>
									</tr>
									<tr>
										<td class="content-title"><s:text name="formManager.startSeniorQuery"></s:text>：</td>
										<td><s:select theme="simple" list="#{false:getText('common.no'),true:getText('common.yes')}" name="advancedQuery"></s:select></td>
										<td></td>
									</tr>
									<tr>
										<td class="content-title"><s:text name="formManager.defaultSortField"></s:text>：</td>
										<td>
											<select name="defaultSortField">
												<option value=""><s:text name="formManager.choose"></s:text></option>
												<s:iterator value="tableColumns" var="tableColumnVar">
													<option <s:if test="#tableColumnVar.name==defaultSortField">selected="selected"</s:if> value="${tableColumnVar.name}">${tableColumnVar.alias }</option>
												</s:iterator>
											</select>
										</td>
										<td></td>
									</tr>
									<tr>
										<td class="content-title"><s:text name="formManager.defaultSortWay"></s:text>：</td>
										<td>
											<select name="orderType">
												<s:iterator value="@com.norteksoft.mms.form.enumeration.OrderType@values()" var="orderTypeVar">
													<option <s:if test="#orderTypeVar==orderType">selected="selected"</s:if> value="${orderTypeVar}"><s:text name="%{code}"></s:text></option>
												</s:iterator>
											</select>
										</td>
										<td></td>
									</tr>
									<tr>
										<td class="content-title"><s:text name="formManager.totalWay"></s:text>：</td>
										<td>
											<select name="totalType">
												<s:iterator value="@com.norteksoft.mms.form.enumeration.TotalType@values()" var="totalTypeVar">
													<option <s:if test="#totalTypeVar==totalType">selected="selected"</s:if> value="${totalTypeVar}"><s:text name="%{code}"></s:text></option>
												</s:iterator>
											</select>
										</td>
										<td></td>
									</tr>
									<tr>
										<td class="content-title"><s:text name="formManager.rowSortName"></s:text>：</td>
							  			<td><s:textfield  theme="simple" id="indexName" name="orderFieldName" maxlength="255" size="60"></s:textfield>
							  			</td>
							  			<td>
							  			</td>
									</tr>
									<tr>
										<td class="content-title"><s:text name="formManager.rowDragAfterSaveUrl"></s:text>：</td>
							  			<td><s:textfield  theme="simple" id="dragRowUrl" name="dragRowUrl" maxlength="255" size="60"></s:textfield></td>
							  			<td><span id="dragRowUrlTip"></span></td>
									</tr>
									<tr>
										<td class="content-title"><s:text name="formManager.formEditSaveUrl"></s:text>：</td>
							  			<td><s:textfield  theme="simple" id="editorUrl" name="editUrl" maxlength="255" size="60"></s:textfield></td>
							  			<td><span id="editorUrlTip"></span></td>
									</tr>
									<tr>
										<td class="content-title"><s:text name="formManager.formEditDeleteUrl"></s:text>：</td>
							  			<td><s:textfield  theme="simple" id="deleteUrl" name="deleteUrl" maxlength="255" size="60"></s:textfield></td>
							  			<td><span id="deleteUrlTip"></span></td>
									</tr>
									<tr>
										<td class="content-title"><s:text name="formManager.defaultRowNumbers"></s:text>：</td>
							  			<td><s:textfield  theme="simple" id="rowNum" name="rowNum" maxlength="3" size="60"></s:textfield></td>
							  			<td><span id="rowNumTip"></span></td>
									</tr>
									<tr>
										<td class="content-title"><s:text name="formManager.optionalRowNumbers"></s:text>：</td>
							  			<td><s:textfield  theme="simple" id="rowList" name="rowList" maxlength="64" size="60"></s:textfield></td>
							  			<td><span id="rowListTip"></span></td>
									</tr>
									<tr>
										<td class="content-title"><s:text name="formManager.freezeColumNumbers"></s:text>：</td>
							  			<td><s:textfield  theme="simple" id="frozenColumn" name="frozenColumn" maxlength="3" size="60"></s:textfield></td>
							  			<td><span id="frozenColumnTip"></span></td>
									</tr>
									<tr>
										<td class="content-title"><s:text name="formManager.showTotalNumbers"></s:text>：</td>
							  			<td>
							  				<s:select theme="simple" list="#{'true':getText('common.yes'),'false':getText('common.no')}" name="totalable"></s:select>
										</td>
							  			<td>
							  			</td>
									</tr>
									<tr>
										<td class="content-title"><s:text name="formManager.showTotalNumbersWhenQuery"></s:text>：</td>
							  			<td>
							  				<s:select theme="simple" list="#{'true':getText('common.yes'),'false':getText('common.no')}" name="searchTotalable"></s:select>
										</td>
							  			<td>
							  			</td>
									</tr>
									<tr>
										<td class="content-title"><s:text name="formManager.fuzzyQuery"></s:text>：</td>
							  			<td>
							  				<s:select theme="simple" list="#{'true':getText('common.yes'),'false':getText('common.no')}" name="searchFaint"></s:select>
										</td>
							  			<td>
							  			</td>
									</tr>
									<tr>
										<td class="content-title" id="pos"><s:text name="formManager.propertyFreeExtend"></s:text>：</td>
										<td><view:formGrid gridId="propertyGridId" code="MMS_JQ_GRID_PROPERTY" entity="${view}" attributeName="jqGridPropertys"></view:formGrid>
										</td>
										<td></td>
									</tr>
									<tr>
										<td class="content-title"><s:text name="formManager.remark"></s:text>：</td>
										<td><s:textarea  cols="50" rows="10" id="remark" name="remark"></s:textarea></td>
										<td><span id="remarkTip"></span></td>
									</tr>
								</table>
							</form>	
							<script>
							$(function(){
								$("div.ui-jqgrid-bdiv div").css("position","static");
								$("#tabs,.ui-tabs .ui-tabs-nav li,.ui-jqgrid,.ui-jqgrid .ui-jqgrid-htable th div,.ui-jqgrid .ui-jqgrid-view,.ui-jqgrid .ui-jqgrid-hdiv,.ui-jqgrid .ui-jqgrid-bdiv").css("position","static");
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
