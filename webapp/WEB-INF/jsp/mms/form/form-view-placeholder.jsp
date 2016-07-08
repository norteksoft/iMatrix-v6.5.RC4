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
		var width = $("#componentWidth").attr("value");
		var height = $("#componentHeight").attr("value");
		if(width==""||typeof(width)=='undefined'){
			width = 200;
		}
		if(height==""||typeof(height)=='undefined'){
			height = 100;
		}
		var controlId = $("#controlId").attr("value");
		var controlIdReg = /[^-_A-Za-z0-9]/g;
		 if(controlIdReg.test(controlId)){//controlId是否符合规则
			 alert("控件id中包含不合法字符,只能包含-_A-Za-z0-9");
			 return;
		 }else{
			var html ="<img pluginType='PLACEHOLDER' " 
					+" src='../../widgets/formeditor/customizeImg/placeholder.gif'"
					+" controlId='"+$("#controlId").attr("value")
					+"' width='"+width
					+"' height='"+height
					+"' imageWidth='"+width
					+"' imageHeight='"+height
					+"' classStyle='"+$("#classId").attr("value")
					+"' styleContent='"+$("#styleId").attr("value")
					+"' jsFun='"+$("#jsFun").attr("value")
					+"' bean='"+$("#bean").attr("value")
					+"'/>";
			parent.html(html);
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
								<td class="content-title"><s:text name="form.inputstandard.field.fieldid"></s:text>：</td>
								<td>
									<input  id="controlId" name="controlId" value="${ formControl.controlId}" class="{required:true,messages: {required:'必填'}}"></input>
								</td>
								<td></td>	
							</tr>
							<tr>
								<td class="content-title"><s:text name="formManager.width"></s:text>：</td>
								<td>
									<input id="componentWidth" name="componentWidth" value="${formControl.componentWidth}" onkeyup="value=this.value.replace(/[^0-9]/,'');" maxlength="9"/>
								</td>
								<td></td>		
							</tr>
							<tr>
								<td class="content-title"><s:text name="formManager.height"></s:text>：</td>
								<td>
									<input id="componentHeight" name="componentHeight" value="${formControl.componentHeight}" onkeyup="value=this.value.replace(/[^0-9]/,'');" maxlength="9"/>
								</td>
								<td></td>	
							</tr>
							<tr>
								<td class="content-title"><s:text name="formManager.bean"></s:text>：</td>
								<td>
									<s:textfield theme="simple" id="bean" name="formControl.controlValue"></s:textfield>
									<br/><s:text name="formManager.service"></s:text>
								</td>
								<td></td>	
							</tr>
							<tr>
								<td class="content-title"><s:text name="formManager.javascript"></s:text>：</td>
								<td>
									<s:textfield theme="simple" id="jsFun" name="formControl.format"></s:textfield>
								</td>
								<td></td>	
							</tr>
							<tr>
								<td class="content-title"><s:text name="formManager.styleClass"></s:text>：</td>
								<td>
									<s:textfield theme="simple" id="classId" name="formControl.classStyle"></s:textfield>
									<br/><s:text name="formManager.attention"></s:text>
								</td>
								<td></td>	
							</tr>
							<tr>
								<td class="content-title"><s:text name="formManager.inlineStyle"></s:text>：</td>
								<td>
									<s:textfield theme="simple" id="styleId" name="formControl.styleContent"></s:textfield>
									<br/><s:text name="formManager.example"></s:text>：color: red;
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
								controlId:"required",
								componentWidth: {
									min:50
								},
								componentHeight:{
									min:50
								}
							},
							messages: {
								controlId:iMatrixMessage["menuManager.required"],
								componentWidth: {
									min:"请输入大于等于50的数"
								},
								componentHeight:{
									min:"请输入大于等于50的数"
								}
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
