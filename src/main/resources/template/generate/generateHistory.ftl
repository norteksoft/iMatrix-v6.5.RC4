<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
	<%@include file="/common/meta.jsp" %>
	<script type="text/javascript" src="${ctx}/widgets/workflowEditor/swfobject.js"></script>
</head>
<body onload="getContentHeight();"  onunload="iMatrix.removeHistoryFlowChar();">
	<div class="ui-layout-center">
		<div class="opt-body">
			<div id="opt-content">
				<aa:zone name="btnZone">
				</aa:zone>
				<aa:zone name="viewZone">
					<!--流转历史-->
					<wf:history companyId="${companyId}" url="${ctx}/widgets/workflowEditor/FlowChartProject.swf" taskId="${taskId}" ></wf:history>
					<script>
					$(function(){$("#flashcontent").height($(window).height()-140);});
					</script>
				</aa:zone>
			</div>
		</div>
	</div>
</body>
</html>