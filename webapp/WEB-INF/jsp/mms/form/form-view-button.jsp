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
		//<a href='#' class='small_btn' onclick=\\\"jQuery('#").append(tableId).append("').jqGrid('editRow','\"+cl+\"');\\\"  ><span>编辑</span></a>
		var classStyle="";
		var styleContent="";
		var event="";
		if($("#classId").attr("value")!=""){
			classStyle=" class='"+$("#classId").attr("value")+"'";
		}
		if($("#styleId").attr("value")!=""){
			styleContent=" style='"+$("#styleId").attr("value")+"'";
		}
		var fun="";
		if($("#eventId").attr("value")!=""){
			fun=$("#eventId").attr("value");
			event=" onclick='"+fun+"'";
		}
		var controlId = $("#controlId").attr("value");
		var controlIdReg = /[^-_A-Za-z0-9]/g;
		 if(controlIdReg.test(controlId)){//controlId是否符合规则
			 alert("控件id中包含不合法字符,只能包含-_A-Za-z0-9");
			 return;
		 }else{
			var html ="<input type='button' pluginType='BUTTON' " 
					+classStyle
					+styleContent
					+event
					+" fun='"+fun+"'"
					+" value='"+$("#name").attr("value")
					+"' id='"+$("#controlId").attr("value")
					+"' hiddenid='"+$("#showButtonControlId").attr("value")
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
						<legend style="margin: 0 10px; color: #0046d5;">&nbsp;<s:text name="formManager.controlDescribe"></s:text>&nbsp;</legend>
						<table class="form-table-without-border">
						<tbody>
							<tr>
								<td class="content-title"><s:text name="formManager.controlType"></s:text></td>
								<td>
									<s:text name="%{formControl.controlType.code}"></s:text>
								</td>
								<td>
								</td>	
							</tr>	
							<tr>
								<td class="content-title"><s:text name="formManager.controlName"></s:text>：</td>
								<td>
									<s:textfield theme="simple" id="name" name="formControl.name"  cssClass="{required:true,messages: {required:'必填'}}" ></s:textfield>
								</td>
								<td>
									<span id="nameTip"></span>
								</td>	
							</tr>
							<tr>
								<td class="content-title"><s:text name="form.inputstandard.field.fieldid"></s:text>：</td>
								<td>
									<s:textfield theme="simple" id="controlId" name="formControl.controlId"  cssClass="{required:true,messages: {required:'必填'}}"></s:textfield>
								</td>
								<td><span id="controlIdTip"></span></td>	
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
							<tr>
								<td class="content-title"><s:text name="formManager.controlEvent"></s:text>：</td>
								<td>
									<s:textfield theme="simple" id="eventId" name="formControl.clickEvent"></s:textfield>
								</td>
								<td></td>	
							</tr>
							 <tr >
					      		<td><s:text name="formManager.bindButtionInfo"></s:text>：</td>
					      		<td><s:textfield  theme="simple" name="formControl.showButtonControlId" id="showButtonControlId"/></td>
					      		<td><span id="showButtonControlIdTip"></span></td>
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
