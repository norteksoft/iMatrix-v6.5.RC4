<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/wf-taglibs.jsp"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>流程监控</title>
	<%@ include file="/common/wf-iframe-meta.jsp"%>
	<link type="text/css" rel="stylesheet" href="${resourcesCtx}/widgets/formValidator/validator.css"></link>
	<script src="${wfCtx }/js/workflow-definition.js" type="text/javascript"></script>
	
	<script language="javascript" type="text/javascript" src="${imatrixCtx}/widgets/calendar/WdatePicker.js"></script>
	<script type="text/javascript">
	$(document).ready(function(){
		lastTransactTimeInit("lastTransactTimeStart");
		lastTransactTimeInit("lastTransactTimeEnd");
	});
	
	</script>
</head>
	<body>
	<div class="ui-layout-center">
		<form id="defaultForm" action="" name="defaultForm" method="post">
			<input id="workflowId" name="workflowId" type="hidden"/>
		</form>
		<div class="opt-body">
		<aa:zone name="wf_definition">	
			<div class="opt-btn">
				<s:if test="position=='taskActive' || position=='taskHistory'">
					<button class='btn' onclick="iMatrix.showSearchDIV(this);" hidefocus="true"><span><span><s:text name="formManager.search"></s:text></span></span></button>
					<button class='btn' onclick="iMatrix.export_Data('${wfCtx}/engine/workflow-definition-monitor-task-export.htm?position=${position }','true');"><span><span><s:text name="menuManager.export"></s:text></span></span></button>
				</s:if><s:else>
					<s:text name="wf.button.transactor"></s:text>:<input 
							name="transactorName" id="transactorName"></input>&nbsp;&nbsp;<s:text name="wf.button.forTheTimeLimit"></s:text>:<input
							name="lastTransactTimeStart" id="lastTransactTimeStart"
							readonly="readonly"></input>-<input name="lastTransactTimeEnd"
							id="lastTransactTimeEnd" readonly="readonly"></input>
					<button class='btn' onclick="searchOverdueTaskUser();" hidefocus="true"><span><span><s:text name="formManager.search"></s:text></span></span></button>
					<button class='btn' onclick="exportOVerdueTaskUser();"><span><span><s:text name="menuManager.export"></s:text></span></span></button>
				</s:else>
			</div>
									
								
			<aa:zone name="monitorList">
				<div id="message" style="display:none;"><s:actionmessage theme="mytheme" /></div>
												
				<form id="wf_form" name="wf_form" method="post" action="">
					<input name="position" id="position" value="${position }" type="hidden"/>
					<input name="transactorName" id="transactorName1" type="hidden"/>
					<input name="lastTransactTimeStart" id="lastTransactTimeStart1" type="hidden"/>
					<input name="lastTransactTimeEnd" id="lastTransactTimeEnd1" type="hidden"/>
					<input name="_list_code" id="listCode" type="hidden" value="<s:if test="position=='taskUserActive'">WORKFLOW_TASK_OVERDUE_USER</s:if><s:else>WORKFLOW_HISTORY_TASK_OVERDUE_USER</s:else>"/>
				</form>
				
				<div id="opt-content" >
					<form id="searchSubmit" name="searchSubmit" action="" method="post">
						<s:if test="position=='taskActive'">
							<view:jqGrid url="${wfCtx}/engine/workflow-definition-monitor-task-list-data.htm?position=${position }" pageName="tasks" code="WORKFLOW_MONITOR_TASK" gridId="main_table" dynamicColumn="${dynamicColumn}"></view:jqGrid>
						</s:if><s:elseif test="position=='taskHistory'">
							<view:jqGrid url="${wfCtx}/engine/workflow-definition-monitor-task-list-data.htm?position=${position }" pageName="historyTasks" code="WORKFLOW_MONITOR_HISTORY_TASK" gridId="main_table_history"  dynamicColumn="${dynamicColumn}"></view:jqGrid>
						</s:elseif><s:elseif test="position=='taskUserActive'">
							<view:jqGrid url="${wfCtx}/engine/workflow-definition-monitor-task-list-data.htm?position=${position }" pageName="taskInfos" code="WORKFLOW_TASK_OVERDUE_USER" gridId="main_table_user"  dynamicColumn="${dynamicColumn}"></view:jqGrid>
						</s:elseif><s:elseif test="position=='taskUserHistory'">
							<view:jqGrid url="${wfCtx}/engine/workflow-definition-monitor-task-list-data.htm?position=${position }" pageName="taskInfos" code="WORKFLOW_HISTORY_TASK_OVERDUE_USER" gridId="main_table_user_history"  dynamicColumn="${dynamicColumn}"></view:jqGrid>
						</s:elseif>
					</form>	
				</div>
			</aa:zone>
	</aa:zone>
	</div>
	</div>							
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script src="${resourcesCtx}/widgets/timepicker/timepicker_<%=com.norteksoft.product.util.ContextUtils.getCurrentLanguage()%>.js" type="text/javascript"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>
