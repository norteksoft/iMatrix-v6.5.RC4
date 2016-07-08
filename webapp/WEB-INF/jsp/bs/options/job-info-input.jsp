<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/setting-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<%@ include file="/common/setting-iframe-meta.jsp"%>
	<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
	<script type="text/javascript" src="${resourcesCtx}/widgets/multiselect/jquery.multiselect.min.js"></script>
	<link   type="text/css" rel="stylesheet" href="${resourcesCtx}/widgets/multiselect/jquery.multiselect.css" />
	
	<!-- 树 -->
	<script type="text/javascript" src="${resourcesCtx}/js/staff-tree.js"></script>
	<script type="text/javascript" src="${settingCtx}/js/interface.js"></script>
	<script type="text/javascript" src="${settingCtx}/js/job-info.js"></script>
	
	<title>定时设置</title>
	<script type="text/javascript">
	$(document).ready(function(){
		validateTimer();
		checkBoxSelect('everyWeek');
		timeFormat('everyDate');
		dateFormat('appointTime');
	});
	</script>
</head>
<body onload="">
<div class="ui-layout-center">
<div class="opt-body">
	<div class="opt-btn">
		<a class="btn" href="#" onclick="submitJobInfo();"><span><span><s:text name="basicSetting.submit"/></span></span></a>
	</div>
	<div id="opt-content" >
		<div id="message" style="display: none;"><font class='onSuccess'><nobr><s:text name="bs.job.info.save.success"></s:text></nobr></font></div>
		<form action="" name="jobInfoFrom" id="jobInfoFrom" method="post">
			<input  type="hidden" name="id" id="id" value="${id}"/>
			<input type="hidden" name="systemId" id="systemId" value="${systemId}"/>
			<%@ include file="job-corn-info.jsp"%>
		</form>
	</div>
</div>
</div>
<%@ include file="job-corn-setting-declare.jsp"%>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script src="${resourcesCtx}/widgets/timepicker/timepicker_<%=com.norteksoft.product.util.ContextUtils.getCurrentLanguage()%>.js" type="text/javascript"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
</html>