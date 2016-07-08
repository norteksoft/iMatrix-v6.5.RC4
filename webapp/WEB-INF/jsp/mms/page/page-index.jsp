<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/mms-taglibs.jsp"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title><s:text name="pageGenerate.page.generation"/></title>
	<%@ include file="/common/mms-meta.jsp"%>
	<script type="text/javascript" src="${resourcesCtx }/widgets/jstree/jquery.jstree.js"></script>
	<script type="text/javascript">
	</script>
</head>
<body onclick="$('#sysTableDiv').hide();$('#styleList').hide();">
	<script type="text/javascript">
  		var thirdMenu = "createhtml-manager";
  	</script>
  	<%@ include file="/menus/header.jsp"%>
	<div class="ui-layout-west">
		<%@ include file="/menus/mms-createPage-thd-menu.jsp"%>
	</div>
	<div class="ui-layout-center">
		<iframe id="myIFrame" name="myIFrame" src="${mmsCtx}/page/createhtml-editor.htm" style=" height:100%;" frameborder="0" allowtransparency="no"></iframe>
	</div>
</body>
</html>