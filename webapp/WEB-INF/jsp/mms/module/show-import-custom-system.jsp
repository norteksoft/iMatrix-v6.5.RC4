<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/mms-taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
	<title><s:text name="mms.pageManager"></s:text></title>
	<%@ include file="/common/mms-iframe-meta.jsp"%>
	
	<script type="text/javascript" src="${resourcesCtx }/widgets/jstree/jquery.jstree.js"></script>
	
	<script src="${mmsCtx}/js/menu.js" type="text/javascript"></script>
	<style type="text/css">
		.actionMessage{ list-style-type: none; color: red;}
	</style>
	<script type="text/javascript">
		$().ready(function(){
			if("ok"=="${importType}"){
				iMatrix.alert(iMatrixMessage["menuManager.importSuccess"]);
				window.parent.jQuery("#menu-tree").jstree("refresh",-1); 
				window.parent.$.colorbox.close();
			}
		});
		function submitbutt(){
			if($("#filename").val()==""){
				_showMessage('_msg', iMatrixMessage["menuManager.importValidate"]);
			}else{
				$("#importForm").attr("action",webRoot+"/module/validate-import-custom-system.htm");
				$("#importForm").ajaxSubmit(function (id){
					id=id.replace("<pre>","").replace("</pre>","");
					id=id.replace("<PRE>","").replace("</PRE>","");
					id=id.replace("<pre style=\"word-wrap: break-word; white-space: pre-wrap;\">","");
					
					if("ok"==id){
						$("#importForm").attr("action",webRoot+"/module/import-custom-system.htm");
						$("#importForm").submit();
					}else{
						iMatrix.alert(id);
						iMatrix.confirm({
							message:iMatrixMessage["menuManager.confirmCover"],
							confirmCallback:submitbuttOk,
							cancelCallback:submitbuttCancel
						});
					}
				});
			}
		}
		
		function submitbuttOk(){
			$("#importForm").attr("action",webRoot+"/module/import-custom-system.htm?importType=replace");
			$("#importForm").submit();
		}
		function submitbuttCancel(){
			window.parent.$.colorbox.close();
		}
		function _showMessage(id, msg){
			if(msg != ""){
				$("#"+id).html(msg);
			}
			$("#"+id).show("show");
			$("."+id).show();
			setTimeout('$("#'+id+'").hide("show");$(".'+id+'").hide();',3000);
		}
	</script>
</head>
<body >
<div class="ui-layout-center">
	<form id="importForm" name="importForm" action="" method="post" enctype="multipart/form-data">
		<p style="padding-top: 8px;text-align: center;">
			<input type="file" id="filename" name="file"/>
			<a href="#" onclick="submitbutt();" title="<s:text name="menuManager.confirm"></s:text>" class="small-btn"><span><span><s:text name="menuManager.confirm"></s:text></span></span></a>
		</p>
	</form>
</div>
</body>
</html>
