<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/mms-taglibs.jsp"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title><s:text name="mms.formManager"/></title>
	<%@ include file="/common/mms-iframe-meta.jsp"%>
	<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
	<script src="${imatrixCtx}/widgets/formeditor/kindeditor.js" type="text/javascript"></script>
	<script src="${imatrixCtx}/widgets/formeditor/lang/zh_CN.js" type="text/javascript"></script>
	<script src="${imatrixCtx}/widgets/formeditor/formeditor.js" type="text/javascript"></script>
	<link href="${imatrixCtx}/widgets/formeditor/formeditor.css" rel="stylesheet" type="text/css" />
	<script type="text/javascript" src="${imatrixCtx}/widgets/swfupload/swfupload.js"></script>
	<script type="text/javascript" src="${imatrixCtx}/widgets/swfupload/handlers.js"></script>
	<script type="text/javascript" src="${mmsCtx}/js/formControl.js"></script>
	
	
	<script type="text/javascript">
	folderPosition = "formResources/formJCs/";
	function generateHtml(){
		var boxs = $("input[name='jcs']:checked");
		if(boxs.length<=0){
			iMatrix.alert(iMatrixMessage["formManager.fileValidate"]);
			return;
		}
		var showNames = "";
		var filepaths = "";
		for(var i=0;i<boxs.length;i++){
			if(showNames==""){
				showNames = $(boxs[i]).attr("fileName");
				filepaths = $(boxs[i]).attr("value");
			}else{
				showNames = showNames+","+$(boxs[i]).attr("fileName");
				filepaths = filepaths+","+$(boxs[i]).attr("value");
			}
		}
		var html ="<img pluginType='JAVASCRIPT_CSS' " 
			+" showName='"+showNames
			+"' filename='"+filepaths
			+"' src='../../widgets/formeditor/customizeImg/JC.gif"
			+"'/>";
		
		parent.html(html);
	}
	

	$(document).ready(function() {
		initUploadControl("*.js;*.css","所有.js,.css文件");
		initFileTable();
	});

	function initFileTable(){
		var jcs = $("input[name='jcs']");
		var filenames = $("#filename").attr("value");//"formResources/formJCs/公司编码/文件名.js,..."
		if(typeof(filenames)!='undefined'){
			var filenameArr=filenames.split(",");
			for(var i=0;i<filenameArr.length;i++){
				for(var j=0;j<jcs.length;j++){
					var filePath = $(jcs[j]).attr("value");
					if(filenameArr[i]==filePath){
						$(jcs[j]).attr("checked","checked");
						break;
					}
				}
			}
		}
	}

	</script>
	<style type="text/css">
	.form-table-without-border td input{
		width:200px;
	}
	</style>
</head>
<body onload="getContentHeight();" onunload="myDestroyUploadControl();">
<div class="ui-layout-center">
<div class="opt-body">
	<div class="opt-btn">
		<button class="btn" onclick="$('#textForm').submit();"><span><span><s:text name="menuManager.confirm"></s:text></span></span></button>
		<button class="btn" onclick='parent.$.colorbox.close();'><span><span ><s:text name="cancel"></s:text></span></span></button>
	</div>
	<div id="opt-content">
		<aa:zone name="controlContent">
			<div style="margin: 10px;text-align: left;">
				<form name="textForm" id="textForm" action=""  method="post">
					<s:hidden id="companyCode" name="companyCode"></s:hidden>
					<s:hidden id="filename" name="formControl.controlValue"></s:hidden>
					<table class="form-table-without-border">
						<tbody>
							<tr>
								<td class="content-title" style="width: 100px;"><s:text name="formManager.controlType"></s:text>：</td>
								<td>
									<s:text name="%{formControl.controlType.code}"></s:text>
								</td>
								<td>
								</td>	
							</tr>	
						</tbody>
					</table>
					<table class="form-table-without-border">
						<tbody>
							<tr>
								<td class="content-title"><s:text name="formManager.fileUpload"></s:text>：</td>
								<td>
									<div id="spanButtonPlaceholder" ></div>
									<span id="divFileProgressContainer"></span>
								</td>
								<td></td>	
							</tr>
						</tbody>
					</table>
					<table class="form-table-border-left" id="uploadfiles" style="width:450px;" >
						<thead>
							<tr>
							<td style="width:100px;">
							</td>
							<td style="width:200px;">
							<s:text name="pageGenerate.fileName"></s:text>
							</td>
							<td style="width:150px;">
							<s:text name="form.inputstandard.field.operate"></s:text>
							</td>
							</tr>
						</thead>
						<tbody id="filesTB">
							<s:iterator value="formAttachs">
								<tr filePath="${filePath }">
									<td><input name="jcs" value="${filePath}" type="checkbox" fileName="${fileName }"></input></td>
									<td>${fileName }</td>
									<td><a href='#' onclick="downloadImage('${filePath}','${fileName }');"><s:text name="formManager.downLoad"></s:text></a>&nbsp;&nbsp;<a href='#' onclick="deleteFile('${filePath}');"><s:text name="form.inputstandard.field.deleterow"></s:text></a></td>
								</tr>
							</s:iterator>
						</tbody>
					</table>
				</form>
				<script type="text/javascript">
				function validateText(){
					$("#textForm").validate({
						submitHandler: function() {
							generateHtml();
						}
					});
				}
				validateText();
				</script>
			</div>
		</aa:zone>
	</div>
</div>
</div>	
</body>
<script src="${resourcesCtx}/widgets/timepicker/timepicker_<%=com.norteksoft.product.util.ContextUtils.getCurrentLanguage()%>.js" type="text/javascript"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
</html>
