<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/setting-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<%@ include file="/common/setting-meta.jsp"%>
	<title><s:text name="bs.interface.management"/></title>
</head>

<body onclick="$('#sysTableDiv').hide();$('#styleList').hide();">
	<%@ include file="/menus/header.jsp"%>
	<div class="ui-layout-west">
		<%@ include file="/menus/setting-interface-menu.jsp"%>
	</div>
	<div class="ui-layout-center">
		<iframe id="myIFrame" name="myIFrame" src="${settingCtx}/options/datasource-setting-list-data.htm" style="height:100%;" frameborder="0"></iframe>
	</div>
</body>

</html>