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
	folderPosition = "formResources/formImages/";
	function generateHtml(){
		var radios = $("input[name='images']:checked");
		if(radios.length<=0){
			iMatrix.alert(iMatrixMessage["formManager.choosePicture"]);
			return;
		}
		var width = $("#componentWidth").attr("value");
		var height = $("#componentHeight").attr("value");
		if(width==""||typeof(width)=='undefined'){
			width = 200;
		}
		if(height==""||typeof(height)=='undefined'){
			height = 100;
		}
		var showName=$(radios[0]).attr("fileName");
		var filepath = $(radios[0]).attr("value");
		var myfileName = "../../"+filepath;
		var html ="<img pluginType='IMAGE' " 
				+" showName='"+showName
				+"' filename='"+filepath
				+"' src='"+myfileName
				+"' width='"+width
				+"' height='"+height
				+"'/>";
		
		parent.html(html);
	}

	$(document).ready(function() {
		initUploadControl("*.png;*.jpg;*.jpeg;*.bmp;*.gif","请上传png,jpg,jpeg,bmp,gif格式的图片");
	});

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
							<tr>
								<td class="content-title"><s:text name="formManager.pictureWidth"></s:text>：</td>
								<td>
									<input id="componentWidth" name="componentWidth" value="${formControl.componentWidth}" onkeyup="value=this.value.replace(/[^0-9]/,'');" maxlength="9"/>
								</td>
								<td></td>		
							</tr>
							<tr>
								<td class="content-title"><s:text name="formManager.pictureHeight"></s:text>：</td>
								<td>
									<input id="componentHeight" name="componentHeight" value="${formControl.componentHeight}" onkeyup="value=this.value.replace(/[^0-9]/,'');" maxlength="9"/>
								</td>
								<td></td>	
							</tr>
						</tbody>
					</table>
					<table class="form-table-without-border" id="newfile">
						<tbody>
							<tr>
								<td class="content-title"><s:text name="formManager.pictureWidth"></s:text>：</td>
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
							<s:text name="formManager.fileName"></s:text>
							</td>
							<td style="width:150px;">
							<s:text name="form.inputstandard.field.operate"></s:text>
							</td>
							</tr>
						</thead>
						<tbody id="filesTB">
							<s:iterator value="formAttachs">
								<tr filePath="${filePath }">
									<td><input name="images" value="${filePath}" <s:if test="formControl.controlValue==filePath">checked="checked" </s:if> type="radio" fileName="${fileName }"></input></td>
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
						},
						rules: {
							componentWidth: {
								min:20
							},
							componentHeight:{
								min:20
							}
						},
						messages: {
							componentWidth: {
								min:iMatrixMessage["formManager.inputGt20"]
							},
							componentHeight:{
								min:iMatrixMessage["formManager.inputGt20"]
							}
						}
					});
				}
				$(document).ready(function() {
					validateText();
				});
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
