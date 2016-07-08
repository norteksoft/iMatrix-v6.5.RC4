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
	
	<script type="text/javascript">
	function generateHtml(){
		var maxlength = $("#maxLength").val();
		if(maxlength>200){
			alert("文件大小超过最大值200");
			return;
		}
		var controlId = $("#controlId").attr("value");
		var controlIdReg = /[^-_A-Za-z0-9]/g;
		 if(controlIdReg.test(controlId)){//controlId是否符合规则
			 alert("控件id中包含不合法字符,只能包含-_A-Za-z0-9");
			 return;
		 }else{
			var html ="<img pluginType='ATTACH_UPLOAD' " 
					+" src='../../widgets/formeditor/customizeImg/attach.png'"
					+" uploadUrl='"+$("#controlValue").attr("value")
					+"' controlId='"+$("#controlId").attr("value")
					+"' fileSize='"+$("#maxLength").attr("value")
					+"' fileType='"+$("#format").attr("value")
					+"' fileTypeDescription='"+$("#title").attr("value")
					+"'/>";
			parent.html(html);
		 }
	}

	function validateSize(){
		var maxlength = $("#maxLength").val();
		if(maxlength>200){
			$("#maxLength").attr("value","");
		}
	}
	</script>
	<style type="text/css">
	.form-table-without-border td input{
		width:200px;
	}
	</style>
</head>
<body onload="getContentHeight();">
<div class="ui-layout-center">
<div class="opt-body">
	<div class="opt-btn">
		<button class="btn" onclick="$('#textForm').submit();"><span><span><s:text name="menuManager.confirm"></s:text></span></span></button>
		<button class="btn" onclick='parent.$.colorbox.close();'><span><span ><s:text name="cancel"></s:text></span></span></button>
	</div>
	<div id="opt-content">
		<aa:zone name="controlContent">
			<div style="margin: 10px;text-align: left;">
				<form name="textForm" id="textForm" action="${mmsCtx }/form/form-view-text.htm">
					
					<fieldset style="border: #f0f0ee solid 1px; padding: 3px;">
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
								<td class="content-title"><s:text name="formManager.uploadUrl"></s:text>：</td>
								<td>
									<s:textfield theme="simple" id="controlValue" name="formControl.controlValue" ></s:textfield>
								</td>
								<td></td>	
							</tr>
							<tr>
								<td class="content-title"><s:text name="form.inputstandard.field.fieldid"></s:text>：</td>
								<td>
									<input  id="controlId" name="controlId" value="${ formControl.controlId}" class="{required:true,messages: {required:'必填'}}"  ></input>
								</td>
								<td></td>	
							</tr>
							<tr>
								<td class="content-title"><s:text name="formManager.fileSize"></s:text>：</td>
								<td>
									<s:textfield theme="simple" id="maxLength" name="formControl.maxLength" onkeyup="value=this.value.replace(/[^0-9]/,'');validateSize();" maxlength="3"></s:textfield><s:text name="formManager.maxUploadSize"></s:text>
								</td>
								<td></td>		
							</tr>
							<tr>
								<td class="content-title"><s:text name="formManager.fileType"></s:text>：</td>
								<td>
									<s:textfield theme="simple" id="format" name="formControl.format"></s:textfield><s:text name="formManager.exampleExampleType"></s:text>
								</td>
								<td></td>	
							</tr>
							<tr>
								<td class="content-title"><s:text name="formManager.typeDescribe"></s:text>：</td>
								<td>
									<s:textfield theme="simple" id="title" name="formControl.title"></s:textfield>
								</td>
								<td></td>	
							</tr>
						</tbody>
					</table>
					</fieldset>
				</form>
				<script type="text/javascript">
					function validateText(){
						$("#textForm").validate({
							submitHandler: function() {
								generateHtml();
							},
							rules: {
								controlId:"required"
							},
							messages: {
								controlId:iMatrixMessage["menuManager.required"]
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
