<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/mms-taglibs.jsp"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title><s:text name="mms.formManager"/></title>
	<%@ include file="/common/mms-iframe-meta.jsp"%>

	<script src="${imatrixCtx}/widgets/formeditor/kindeditor.js" type="text/javascript"></script>
	<script src="${imatrixCtx}/widgets/formeditor/lang/zh_CN.js" type="text/javascript"></script>
	<script src="${imatrixCtx}/widgets/formeditor/formeditor.js" type="text/javascript"></script>
	<script src="${mmsCtx}/js/formControl.js" type="text/javascript"></script>
	<link href="${imatrixCtx}/widgets/formeditor/formeditor.css" rel="stylesheet" type="text/css" />
	
	<script type="text/javascript">
	function choiceControl(){
		if($("#tableColumnId").get(0).selectedIndex==0){
			$("#name").attr("value","");
		}
		ajaxAnyWhereSubmit("calculateForm", "", "controlContent");
	}

	function mytip(item)
	{
	  if($("#"+item).css("display")=="none")
		  $("#"+item).css("display","block");
	  else
		  $("#"+item).css("display","none");   
	}
	function generateHtml(){
		//tableColumnId,dataType,controlType,name,title,controlId,computational,precision,fontSize,componentWidth,componentHeight
		if("${standard}"=="true"){
			if($("#tableColumnId").get(0).selectedIndex==0){
				alert("请选择对应字段");return;
			}else{
			//	if($("#dataType").attr("value")=="INTEGER"
			//		||$("#dataType").attr("value")=="LONG"||$("#dataType").attr("value")=="DOUBLE"){
				parent.calculateHtml($("#tableColumnId").attr("value")
							,$("#dataType").attr("value")
							,$("#controlType").attr("value")
							,$("#name").attr("value")
							,$("#title").attr("value")
							,$("#controlId").attr("value")
							,$("#computational").attr("value")
							,$("#precision").attr("value")
							,$("#maxLength").attr("value")
							,$("#classId").attr("value")
							,$("#styleId").attr("value")
							,$("#dbName").attr("value"));
			//	}else{
					//alert("数据类型必须为整型,长整型,浮点数");
			//		return;
			//	}
			}
		}else{
			parent.calculateHtml(""
					,$("#dataType").attr("value")
					,$("#controlType").attr("value")
					,$("#name").attr("value")
					,$("#title").attr("value")
					,$("#controlId").attr("value")
					,$("#computational").attr("value")
					,$("#precision").attr("value")
					,$("#maxLength").attr("value")
					,$("#classId").attr("value")
					,$("#styleId").attr("value")
					,"");
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
			<button class="btn" onclick="$('#calculateForm').submit();"><span><span><s:text name="menuManager.confirm"></s:text></span></span></button>
			<button class="btn" onclick='parent.$.colorbox.close();'><span><span ><s:text name="cancel"></s:text></span></span></button>
		</div>
		<div id="opt-content">
			<aa:zone name="controlContent">
			<div style="margin: 10px;text-align: left;">
				<form name="calculateForm" id="calculateForm" action="${mmsCtx }/form/form-view-text.htm">
					<s:hidden name="id"></s:hidden>
					<s:hidden id="code" name="code"></s:hidden>
				<s:hidden id="version" name="version"></s:hidden>
					<s:hidden id="standard" name="standard"></s:hidden>
					<s:hidden id="occasion" name="occasion" value="changeSource"></s:hidden>
					<input id="maxLengthExist" value="${formControl.maxLength }" type="hidden"/>
					<table  class="form-table-without-border">
						<tr>
							<td class="content-title"><s:text name="formManager.controlType"></s:text>：</td>
							<td>
								<s:text name="%{formControl.controlType.code}"></s:text>
								<s:hidden theme="simple" id="controlType" name="formControl.controlType" ></s:hidden>
							</td>
							<td><span id="controlTypeTip"></span></td>	
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
								<td class="content-title"><s:text name="formManager.fieldName"></s:text>  //字段名：</td>
								<td>
									<s:if test="tableColumnId==null||tableColumnId==0">
										<s:textfield theme="simple" id="name" name="formControl.name" cssClass="{required:true,messages: {required:'必填'}}"></s:textfield>
									</s:if>
									<s:else>
										<s:textfield theme="simple" id="name" name="formControl.name" readonly="true" cssClass="{required:true,messages: {required:'必填'}}"></s:textfield>
									</s:else>
									<s:hidden  theme="simple" name="formControl.dbName" id="dbName"/>
									<s:hidden  theme="simple" name="formControl.dataType" id="dataType"/>
									<span class="required">*</span>
								</td>
								<td>
									<span id="nameTip"></span>
								</td>	
							</tr>
						</s:if><s:else>
							<tr>
								<td class="content-title"><s:text name="formManager.fieldName"></s:text>：</td>
								<td>
									<s:textfield theme="simple" id="name" maxlength="27" name="formControl.name" cssClass="{required:true,messages: {required:'必填'}}" onblur="fieldNameOk(this);"></s:textfield>
									<span class="required">*</span>
								</td>
								<td>
									<span id="nameTip"></span>
								</td>	
							</tr>
							<tr>	
								<td class="content-title"><s:text name="formManager.fieldType"></s:text>：</td>	
								<td>
									<s:select theme="simple" id="dataType" list="#{'INTEGER':getText('formManager.integer'),'LONG':getText('formManager.long'),'DOUBLE':getText('formManager.double'),'FLOAT':getText('formManager.float'),'TEXT':getText('formManager.text')}" 
							 name="formControl.dataType" onchange="defautMaxlengthSet();"></s:select>
								</td>
							</tr>
						</s:else>
					     
						<tr>
							<td class="content-title"><s:text name="formManager.fieldAlias"></s:text>：</td>
							<td>
								<s:textfield theme="simple" id="title" name="formControl.title" cssClass="{required:true,messages: {required:'必填'}}"></s:textfield>
								<span class="required">*</span>
								<s:hidden  theme="simple" name="formControl.controlId" id="controlId"/>
							</td>
							<td><span id="titleTip"></span></td>	
						</tr>
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
					     	<td class="content-title"><s:text name="formManager.formula"></s:text>：</td>
					     	<td><s:textarea theme="simple" name="formControl.computational" id="computational" rows="3"></s:textarea></td>
					     	<td >
				              <span id="computationalTip"></span>
				            </td>
					     </tr>
					     <tr>
					     	<td class="content-title"></td>
					     	<td colspan="2">
				             <a href="#" onClick="mytip('tip')"><s:text name="formManager.formulaIns"></s:text></a>
				             <div style="font-size: 10pt;font-family:宋体;display:none;" id="tip">
				             	<s:text name="mms.formulaSupport"></s:text>
				              	 <b><s:text name="mms.theComputingFunction"></s:text>：</b><br>
				              	<s:text name="mms.MAXNumerical1"></s:text>
				              <b><s:text name="mms.noteTheNameOf"></s:text></b>
				              </div>
				            </td>
				            <td></td>
					     </tr>
					     <tr>
					     	<td class="content-title"><s:text name="formManager.precision"></s:text>：</td>
					   		<td><s:textfield  theme="simple" name="formControl.precision" id="precision" onkeyup="value=value.replace(/[^\d]/g,'')"/></td>
					   		<td><s:text name="formManager.decimal"></s:text></td>
					     </tr>
					     <tr>
							<td class="content-title"><s:text name="formManager.styleClass"></s:text>：</td>
							<td>
								<s:textfield theme="simple" id="classId" name="formControl.classStyle" ></s:textfield>
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
				    </table>
				</form>
				<script type="text/javascript">
				function validateText(){
					$("#calculateForm").validate({
						submitHandler: function() {
							generateHtml();
						}
					});
				}
				$(function(){
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
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>
