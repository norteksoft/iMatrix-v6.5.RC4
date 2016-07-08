<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/wf-taglibs.jsp"%>



<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title><s:text name="wf.workflow.management"/></title>
<%@ include file="/common/wf-iframe-meta.jsp"%>
<script src="${wfCtx }/js/workflow-definition.js" type="text/javascript"></script>

<script src="${wfCtx }/js/util.js" type="text/javascript"></script>

<script src="${wfCtx }/js/workflow.js" type="text/javascript"></script>

<script src="${imatrixCtx}/widgets/workflowEditor/rightClick.js" type="text/javascript"></script>
<script type="text/javascript" src="${imatrixCtx}/widgets/workflowEditor/swfobject.js"></script>
<script type="text/javascript" src="${resourcesCtx}/js/staff-tree.js"></script>
<script type="text/javascript" src="${resourcesCtx}/js/jquery.timers-1.2.js"></script>
<script type="text/javascript">
	$(document).ready(function(){
		resizeFlasContent();
		if("${wfdId}"==""){
			addSWf("add");
		}else{
			if("${flag}" == "view"){
				addSWf("view");
				//parent.hideWestAndNorth();
			}else{
				addSWf("update");
			}
		}
	});
	function resizeFlasContent(){
		$("#flashcontent").height($(window).height()-30);
	}

	function flexReturn(){
		var backUrl = "${backUrl}";
		if(backUrl.indexOf("~~")>=0){//?号代表~~
			backUrl = backUrl.replace(/~~/g, "?");
		}
		if(backUrl.indexOf("@@")>=0){//=号代表@@
			backUrl = backUrl.replace(/@@/g, "=");
		}
		if(backUrl.indexOf("~@")>=0){//&号代表>=0
			backUrl = backUrl.replace(/~@/g, "&");
		}
		window.location = backUrl;
	}
	
</script>
</head>
<body >
<div class="ui-layout-center">
		<form id="wfdForm" name="wfdForm">
			<input type="hidden" name="type" value="${type }"/>
			<input id="system_id" name="sysId" type="hidden" value="${sysId }"/>
			<input id="vertion_type" name="vertionType" type="hidden" value="${vertionType }"/>
			<input id="wfdId" name="wfdId" type="hidden" value="${wfdId }"/>
			<input id="flag" name="flag" type="hidden" value="${flag}"/> <!-- 查看标识 -->
		</form>
			<input id="backUrl" name ="backUrl"  type="hidden" value="${backUrl}"></input>
			<input id="saveUrl" name ="saveUrl" type="hidden" value="${saveUrl}"></input>
			<input id="companyId" name="defCompanyId" type="hidden" value="${defCompanyId}"/>
			<input id="creator" name="defCreator" type="hidden" value="${defCreator}"/>
			<input id="creatorName" name="defCreatorName" type="hidden" value="${defCreatorName}"/>
			<input id="currentorLoginName" name="currentorLoginName" type="hidden" value="${currentorLoginName}"/>
			<input id="currentorName" name="currentorName" type="hidden" value="${currentorName}"/>
			<input id="defCreatorSubCompanyId" name="defCreatorSubCompanyId" type="hidden" value="${defCreatorSubCompanyId}"/>
			<input id="type" name="type" type="hidden" value="${type}"/>
			<input id="templateId" name="templateId" type="hidden" value="${templateId }"/>
			<input id="systemId" name="defSystemId" type="hidden" value="${defSystemId }"/>
			<input type="hidden" name="localeLang" id="localeLang" value="<%= request.getLocale().getLanguage() %>" />
			<div id="flashcontent" style="width:100%;">
			</div>
</div>			
</body>
</html>
