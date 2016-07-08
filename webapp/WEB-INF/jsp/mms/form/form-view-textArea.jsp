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
	<script src="${mmsCtx}/js/formControl.js" type="text/javascript"></script>
	<script type="text/javascript">
	function choiceControl(){
		if($("#tableColumnId").get(0).selectedIndex==0){
			$("#name").attr("value","");
		}
		ajaxAnyWhereSubmit("textForm", "", "controlContent");
	}
	function generateHtml(){
			var dataType = $("#dataType").val();
			if(dataType!="TEXT"&&dataType!="CLOB"){
				alert("文本域只能建文本或大文本类型的字段");
				return;
			}
			var type = $("#controlType").attr("value");
			var name = $("#name").attr("value");
			var controlId = $("#controlId").attr("value");
			var styleId=$("#styleId").attr("value");
			var styleObject={};
			var tempStyleId="";
			if($.trim(styleId)==''){
				styleId="width:354px;height:139px;";
			}else{
				var arr=styleId.split(";");
				for(var i=0,j=arr.length;i<j;i++){
					var entity=arr[i].split(":");
					if(entity.length==2){
						styleObject[entity[0].replace(/(^\s*)|(\s*$)/g, "").toLowerCase()]=entity[1].replace(/(^\s*)|(\s*$)/g, "").toLowerCase();
					}
				}
				if(!styleObject["width"]){
					styleObject["width"]="354px";
				}
				if(!styleObject["height"]){
					styleObject["height"]="139px";
				}
				for(var key in styleObject){
					tempStyleId=tempStyleId+key+":"+styleObject[key]+";";
				}
				styleId=tempStyleId;
			}
			var rich="";
			if($("input[id='rich']:checked").length==1){
				rich="1";
			}else{
				rich="0";
			}
			if("${standard}"=="true"){
				if($("#tableColumnId").get(0).selectedIndex==0){
					alert("请选择对应字段");
					return;
				}else{
					parent.textAreaHtml($("#tableColumnId").attr("value")
							,$("#controlId").attr("value")
							,name
							,$("#title").attr("value")
							,$("#defaultValue").attr("value")
							,$("#maxLength").attr("value")
							,$("#dataType").attr("value")
							,$("#classId").attr("value")
							,styleId,rich,$("#componentWidth").attr("value"),$("#componentHeight").attr("value"));
				}
			}else{
				parent.textAreaHtml(""
							,$("#controlId").attr("value")
							,name
							,$("#title").attr("value")
							,$("#defaultValue").attr("value")
							,$("#maxLength").attr("value")
							,$("#dataType").attr("value")
							,$("#classId").attr("value")
							,styleId,rich,$("#componentWidth").attr("value"),$("#componentHeight").attr("value"));
			}
	}
	function setRichWidthHeight(){
		if($("input[id='rich']:checked").length==1){
			$("#componentWidthTr").show();
			$("#componentHeightTr").show();
		}else{
			$("#componentWidthTr").hide();
			$("#componentHeightTr").hide();
			$("#componentWidth").attr("value","");
			$("#componentHeight").attr("value","");
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
			<form name="textForm" id="textForm" action="${mmsCtx }/form/form-view-text.htm">
				<s:hidden name="id"></s:hidden>
				<s:hidden id="code" name="code"></s:hidden>
				<s:hidden id="version" name="version"></s:hidden>
				<s:hidden id="standard" name="standard"></s:hidden>
				<s:hidden id="occasion" name="occasion" value="changeSource"></s:hidden>
				<input id="maxLengthExist" value="${formControl.maxLength }" type="hidden"/>
				<table class="form-table-without-border">
					<tbody>
						<tr>
							<td class="content-title"><s:text name="formManager.controlType"></s:text>：</td>
							<td class="no-edit">
								<s:text name="%{formControl.controlType.code}"></s:text>
								<s:hidden theme="simple" id="controlType" name="formControl.controlType" ></s:hidden>
							</td>
							<td>
							</td>	
						</tr>	
						<s:if test="standard">
							<tr>
								<td class="content-title"><s:text name="formManager.field"></s:text>：</td>
								<td>
								<s:hidden id="dataType" name="formControl.dataType"></s:hidden>
									<s:select onchange="choiceControl();" id="tableColumnId" name="tableColumnId" list="columns" theme="simple" listKey="id" listValue="alias" headerKey="0" headerValue="请选择"></s:select>
								</td>
								<td></td>	
							</tr>
							<tr>
								<td class="content-title"><s:text name="formManager.field"></s:text>：</td>
								<td>
									<s:if test="tableColumnId==null||tableColumnId==0">
										<input  id="name" name="formControl.name" class="{required:true,messages: {required:'<s:text name="required"></s:text>'}}"></input>
									</s:if>
									<s:else>
										<input  id="name" name="formControl.name" readonly="true" class="{required:true,messages: {required:'<s:text name="required"></s:text>'}}"></input>
									</s:else>
									<s:hidden  theme="simple" name="formControl.dataType" id="dataType"/>
									<span class="required">*</span>
								</td>
								<td>
									<span id="nameTip"></span>
								</td>	
							</tr>
						</s:if>
						<s:else>
							<tr>
								<td class="content-title"><s:text name="formManager.fieldName"></s:text>：</td>
								<td>
									<input  id="name" name="formControl.name"  class="{required:true,messages: {required:'<s:text name="required"></s:text>'}}" onblur="fieldNameOk(this);" maxlength="27"/>
									<span class="required">*</span>
								</td>
								<td>
									<span id="nameTip"></span>
								</td>	
							</tr>
							<tr>	
								<td class="content-title"><s:text name="formManager.fieldType"></s:text>：</td>	
								<td>
									<s:select theme="simple" id="dataType" list="#{'TEXT':getText('formManager.text'),'CLOB':getText('formManager.longText')}" 
							 name="formControl.dataType" onchange="defautMaxlengthSet();"></s:select>
								</td>
							</tr>
						</s:else>
						<tr>
							<td class="content-title"><s:text name="formManager.fieldAlias"></s:text>：</td>
							<td>
								<input  id="title" name="formControl.title"  class="{required:true,messages: {required:'<s:text name="required"></s:text>'}}"></input>
								<span class="required">*</span>
								<s:hidden  theme="simple" name="formControl.controlId" id="controlId"/>
							</td>
							<td><span id="titleTip"></span></td>	
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
						<s:if test="formControl.controlType.enumName!='SELECT'">
							<tr>
								<td class="content-title"><s:text name="formManager.defaultValue"></s:text>：</td>
								<td>
									<s:textfield theme="simple" id="defaultValue" name="formControl.controlValue" ></s:textfield>
								</td>
								<td><span id="controlValueTip"></span></td>	
							</tr>	
						</s:if>
						<s:if test="standard">
							<tr>
								<td class="content-title"><s:text name="formManager.maxLenth"></s:text>：</td>
								<td>
									<s:if test="tableColumnId==null||tableColumnId==0">
										<s:textfield theme="simple" id="maxLength" name="formControl.maxLength" onkeyup="value=this.value.replace(/[^0-9]/,'');"></s:textfield>
									</s:if><s:else>
										<s:textfield theme="simple" id="maxLength" name="formControl.maxLength" onkeyup="value=this.value.replace(/[^0-9]/,'');" readonly="true"></s:textfield>
									</s:else>
								</td>
							</tr>
						</s:if><s:else>
							<tr>
								<td class="content-title"><s:text name="formManager.maxLenth"></s:text>：</td>
								<td>
									<s:textfield theme="simple" id="maxLength" name="formControl.maxLength" onkeyup="value=this.value.replace(/[^0-9]/,'');"></s:textfield>
								</td>
							</tr>
						</s:else>
						<tr>
							<td class="content-title"><s:text name="formManager.richType"></s:text>：</td>
							<td colspan="2">
								<input id="rich" <s:if test="formControl.rich==1">checked="checked"</s:if> type="checkbox" onclick="setRichWidthHeight();"/>
								<span style="color: red;"><s:text name="formManager.richAttention"></s:text></span>
							</td>
						</tr>
						<tr id="componentWidthTr" style="display: none;">
							<td class="content-title"><s:text name="formManager.rickWidth"></s:text>：</td>
							<td>
								<s:textfield theme="simple" id="componentWidth" name="formControl.componentWidth" onkeyup="value=this.value.replace(/[^0-9]/,'');"></s:textfield>
							</td>
							<td></td>	
						</tr>
						<tr id="componentHeightTr" style="display: none;">
							<td class="content-title"><s:text name="formManager.richHeigt"></s:text>：</td>
							<td>
								<s:textfield theme="simple" id="componentHeight" name="formControl.componentHeight" onkeyup="value=this.value.replace(/[^0-9]/,'');"></s:textfield>
							</td>
							<td></td>	
						</tr>
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
			setRichWidthHeight();
			</script>
		</aa:zone>
	</div>
</div>
</div>
</body>
<script src="${resourcesCtx}/widgets/timepicker/timepicker_<%=com.norteksoft.product.util.ContextUtils.getCurrentLanguage()%>.js" type="text/javascript"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
</html>
