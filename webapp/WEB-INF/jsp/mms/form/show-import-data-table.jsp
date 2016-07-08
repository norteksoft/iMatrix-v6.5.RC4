<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/mms-taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
	<title>系统元数据管理</title>
	<%@ include file="/common/mms-iframe-meta.jsp"%>
	
	<style type="text/css">
		.actionMessage{ list-style-type: none; color: red;}
	</style>
	<script type="text/javascript">
		function submitbutt(){
			if($("#filename").val()==''){
				iMatrix.alert(iMatrixMessage["formManager.importFormValidate"]);
				return;
			}
			$("#importForm").ajaxSubmit(function (id){
				id=id.replace("<pre>","").replace("</pre>","");
				id=id.replace("<PRE>","").replace("</PRE>","");
				id=id.replace("<pre style=\"word-wrap: break-word; white-space: pre-wrap;\">","");
				iMatrix.alert({message:id,confirmCallback:refereshParentPage});
			});
		}

		function refereshParentPage(){
			window.parent.dataTableList(); 
			window.parent.$.colorbox.close();
		}
	</script>
</head>
<body >
<div class="ui-layout-center">
	<div style="text-align: center;margin-top: 40px;">
	<form id="importForm" name="importForm" action="${mmsCtx}/form/import-data-table.htm" method="post" enctype="multipart/form-data">
		<p style="padding-top: 8px;text-align: center;">
			<input type="file" id="filename" name="file"/>
			<a href="#" onclick="submitbutt();" title="<s:text name="menuManager.confirm"></s:text>"  class="small-btn"><span><span><s:text name="menuManager.confirm"></s:text> </span></span></a>
		</p>
	</form>
	<s:actionmessage />
	<ul class="_msg" style="display: none;list-style-type: none; margin-top: 6px;">
		<li> <span id="_msg" style="color: red;display: none;list-style-type: none;"></span> </li>
	</ul>
	</div>
</div>
</body>
</html>
