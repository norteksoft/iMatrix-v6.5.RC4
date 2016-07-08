<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/mms-taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<title><s:text name="mms.formManager"/></title>
</head>
<body>
<div class="ui-layout-center">
	<aa:zone name="dataTableContent">
		<script type="text/javascript">
			$(document).ready(function() {
				$( "#tabs" ).tabs({select:function(event,ui){}});
			});
		</script>
		<aa:zone name="btnZone">
			<div class="opt-btn">
				<button class="btn" onclick="setPageState();returnCustomTableList();"><span><span ><s:text name="menuManager.back"></s:text></span></span></button>
			</div>
		</aa:zone>
		<div id="opt-content">
			<div id="tabs">
				<ul>
					<li><a href="#tabs-1" onclick="viewClickChange('a');"><s:text name="formManager.basic"></s:text></a></li>
					<li><a href="#tabs-1" onclick="viewClickChange('b')"><s:text name="formManager.column"></s:text></a></li>
				</ul>
				<div id="tabs-1">
				<aa:zone name="contentZone">
					<form id="contentFrom" name="contentFrom" method="post">
						<input type="hidden" id="men_id"  name="menuId" value="${menuId }"/>
						<input type="hidden" name="states" value="${states}"/>
					</form>
					<aa:zone name="dataTableContext">
						<script type="text/javascript">
						$(document).ready(function() {
							$("#men_id").attr("value",$("#menuId").val());
						});
						</script>
							<div id="message" style="display:none;"><s:actionmessage theme="mytheme" /></div>
							<form id="inputForm" name="inputForm" action="" method="post">
								<input type="hidden" id="men_id"  name="menuId" value="${menuId}"/>
								<input type="hidden" name="tableId" id="tabelId" value="${tableId}"/>
								<table class="form-table-without-border">
									<tr>
										<td class="content-title" style="width: 90px;"><s:text name="formManager.dataSheetName"></s:text>: </td>
										<td> ${dataTable.name}</td>
										<td></td>
									</tr>	
									<tr>
										<td class="content-title"><s:text name="formManager.dataSheetAnotherName"></s:text>：</td>
										<td> ${dataTable.alias} </td>
										<td></td>
									</tr>
									<tr>
										<td class="content-title"><s:text name="formManager.formDescribe"></s:text>：</td>
										<td> </td>
										<td></td>
									</tr>
									<tr>
										<td class="content-title"></td>
										<td colspan="2"> 
											<textarea id="remark" name="remark" cols="55" rows="10" style="font-family:Arial,Helvetica,sans-serif;overflow: auto;">${dataTable.remark}</textarea>
										</td>
									</tr>
								</table>
							</form>	
					</aa:zone>
				</aa:zone>
				</div>
			</div>
		</div>
	</aa:zone>
</div>
</body>
</html>