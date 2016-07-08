<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
<title>报销单</title>
<%@include file="/common/meta.jsp"%>
	<script src="${resourcesCtx}/widgets/validation/validate-all-1.0.js" type="text/javascript"></script>
	<script src="${resourcesCtx}/widgets/validation/dynamic.validate.js" type="text/javascript"></script>
	<script type="text/javascript" src="${resourcesCtx}/js/staff-tree.js"></script>
	<script type="text/javascript" src="${ctx}/widgets/workflowEditor/swfobject.js"></script>
	<#if containWorkflow?if_exists>
	<script type="text/javascript" src="${resourcesCtx}/js/opinion.js"></script>
	</#if>

<script type="text/javascript">
var buttonSign="";
isUsingComonLayout=false;
<#if containWorkflow?if_exists>
<#if popupable?if_exists>
	//流转历史和表单信息切换
	function changeViewSet(opt){
			if(opt=="basic"){
				ajaxSubmit("defaultForm1", "${ctx}/${nameSpace}/${lowCaseEntityName}-view.htm", 'viewZone');
			}else if(opt=="history"){
				ajaxSubmit("defaultForm1", "${ctx}/${nameSpace}/${lowCaseEntityName}-showHistory.htm", 'viewZone');
			}
	}
</#if>	
</#if>	
</script>
</head>
<#if containWorkflow?if_exists>
		<#if popupable?if_exists>
			<body  onunload="iMatrix.removeHistoryFlowChar();">
		<#else>
			<body>
		</#if>
	<#else>
		<body>
</#if>
<div class="opt-body">
<aa:zone name="main">
			<div class="opt-btn">
						<#if popupable?if_exists>
							<button class='btn' onclick='window.parent.$.colorbox.close();'><span><span>返回</span></span></button>
						<#else>
							<button class='btn' onclick='setPageState();ajaxSubmit("defaultForm","${ctx}/${nameSpace}/${lowCaseEntityName}-list.htm","main");'><span><span>返回</span></span></button>
						</#if>
			</div>
			<div id="opt-content" class="form-bg">
				<form id="defaultForm1" name="defaultForm1"action="">
					<input type="hidden" name="id" id="id" value="${id}"  />
					<input name="taskId" id="taskId" value="${taskId}" type="hidden"/>
					<input id="selecttacheFlag" type="hidden" value="true"/>
				</form>
				<#if containWorkflow?if_exists>
				<div id="tabs">
					<ul>
						<li><a href="#tabs-1" onclick="changeViewSet('basic');">表单信息</a></li>
						<li><a href="#tabs-1" onclick="changeViewSet('history');">流转历史</a></li>
					</ul>
					<div id="tabs-1">
						<aa:zone name="viewZone">
							<form id="${lowCaseEntityName}Form" name="${lowCaseEntityName}Form" method="post"
								action="">
								<input type="hidden" name="id" id="id" value="${id }" />
								<input type="hidden" name="taskId" id="taskId" value="${taskId }" />
								<grid:formView code="${formCode}" entity="${entity}" viewable="true" taskId="${taskId}"></grid:formView>
							</form>
							<script type="text/javascript">
								$(document).ready(function(){
									addFormValidate('${fieldPermission}',"${lowCaseEntityName}Form");
									$( "#tabs" ).tabs();
								});
							</script>
						</aa:zone>
					</div>
				</div>
				<#else>
						<aa:zone name="viewZone">
							<form id="${lowCaseEntityName}Form" name="${lowCaseEntityName}Form" method="post"
								action="">
								<input type="hidden" name="id" id="id" value="${id }" />
								<grid:formView code="${formCode}" entity="${entity}" viewable="true" ></grid:formView>
							</form>
							<script type="text/javascript">
								$(document).ready(function(){
									addFormValidate('${fieldPermission}',"${lowCaseEntityName}Form");
								});
							</script>
						</aa:zone>
				</#if>
			</div>
		</aa:zone>
	</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
</html>