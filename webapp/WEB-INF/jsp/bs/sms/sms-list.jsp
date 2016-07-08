<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/setting-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<%@ include file="/common/setting-meta.jsp"%>
	<link rel="stylesheet" href="${resourcesCtx}/widgets/ztree/css/zTreeStyle/zTreeStyle.css" type="text/css">
	<script type="text/javascript" src="${resourcesCtx}/widgets/ztree/js/jquery.ztree.core-3.5.js"></script>
	<script type="text/javascript" src="${resourcesCtx}/js/tree.js"></script>
	<title><s:text name="messagePlatform.messagePlatform"/></title>
	<script type="text/javascript">
		$(document).ready(function() {
			createjobInfoTree("sms_tree","${settingCtx}/sms/sms-tree.htm",false);//短信平台树
		});
	</script>
</head>

<body onclick="$('#sysTableDiv').hide();$('#styleList').hide();">
	<script type="text/javascript">
  		var thirdMenu = "sms_manager";
  	</script>
	<%@ include file="/menus/header.jsp"%>
	<div class="ui-layout-west">
		<%@ include file="/menus/setting-sms-menu.jsp"%>
	</div>
	<div class="ui-layout-center">
		<iframe id="myIFrame" name="myIFrame" src="${settingCtx}/sms/sms-gateway-setting.htm" style="height:100%;" frameborder="0"></iframe>
	</div>
</body>

</html>