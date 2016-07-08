<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/wf-taglibs.jsp"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>流程监控</title>
	<%@ include file="/common/wf-iframe-meta.jsp"%>
	<link type="text/css" rel="stylesheet" href="${resourcesCtx}/widgets/formValidator/validator.css"></link>
	<script src="${wfCtx }/js/workflow-definition.js" type="text/javascript"></script>
	
	<script type="text/javascript">
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
				<button class='btn' onclick="iMatrix.showSearchDIV(this);" hidefocus="true"><span><span><s:text name="formManager.search"></s:text></span></span></button>
			</div>
									
								
			<aa:zone name="monitorList">
				<div id="message" style="display:none;"><s:actionmessage theme="mytheme" /></div>
												
				<form id="wf_form" name="wf_form" method="post" action="">
					<input name="position" id="position" type="hidden"/>
				</form>
				
				<div id="opt-content" >
					<form id="searchSubmit" name="searchSubmit" action="" method="post">
						<s:if test="position=='taskActive'">
							<view:jqGrid url="${wfCtx}/engine/workflow-definition-monitor-task-transfer-list-data.htm?position=${position }&taskId=${taskId }" pageName="tasks" code="WORKFLOW_MONITOR_TRANSFER_TASK" gridId="main_table"></view:jqGrid>
						</s:if><s:elseif test="position=='taskHistory'">
							<view:jqGrid url="${wfCtx}/engine/workflow-definition-monitor-task-transfer-list-data.htm?position=${position }&taskId=${taskId }" pageName="historyTasks" code="WORKFLOW_MONITOR_TRANSFER_HISTORY_TASK" gridId="main_table_history"></view:jqGrid>
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
