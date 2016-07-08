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
	
	<script src="${imatrixCtx}/widgets/formeditor/pullDownMenu.js" type="text/javascript" charset="UTF-8"></script>
	<script type="text/javascript">
	var selectListHtml;
	function choiceControl(){
		selectListHtml=$("#selectList").html();
		if($("#tableColumnId").get(0).selectedIndex==0){
			$("#name").attr("value","");
		}
		ajaxAnyWhereSubmit("radioCheckboxForm", "", "controlContent",choiceControlCallBack);
	}
	function choiceControlCallBack(){
		$("#selectList").html(selectListHtml);
	}
	function generateHtml(){
		var dataType = $("#dataType").val();
		var controlType=$("#controlType").attr("value") ;
		var classStyle="";
		var styleContent="";
		var event="";
		if($("#classId").attr("value")!=""){
			classStyle=" class='"+$("#classId").attr("value")+"'";
		}
		if($("#styleId").attr("value")!=""){
			styleContent=" style='"+$("#styleId").attr("value")+"'";
		}
		var html;
		var select=document.getElementById("selectList");
		var radioCheckboxfield="";
		for(var i=0;i<select.length;i++){
			if(radioCheckboxfield != ""){
				radioCheckboxfield+=",";
			}
			radioCheckboxfield+=select.options[i].text+";"+select.options[i].value;
		}
		var item=$("#item").val();
		if(item==="0"){
			radioCheckboxfield="";
		}
	    html ="<input  pluginType='RADIO'  type='"+controlType
			+"' name='"+$("#name").attr("value") 
			+"' title='"+$("#title").attr("value")
			+"' dataType='"+$("#dataType").attr("value")
			+"' initSelectValue='"+$("#initSelectValue").attr("value")
			+"' selectValues='"+radioCheckboxfield
			+"' item='"+$("#item").attr("value")
			+"' optionGroupCode='"+$("#optionGroupCode").attr("value")
			+"' optionGroupId='"+$("#optionGroupId").attr("value")+"'"
			+classStyle
			+styleContent;
		if("${standard}"=="true"){
			if($("#tableColumnId").get(0).selectedIndex==0){
				//请选择对应字段
				iMatrix.alert(iMatrixMessage["formManager.selectFieldInfo"]);
				return;
			}else{
				html = html
					+" dbName='"+$("#dbName").attr("value")+"'";
			}
		}
		html=html+"/>";
		parent.radioCheckbox(html);
	}
	function mytip(item)
	{
	  if($("#"+item).css("display")=="none")
		  $("#"+item).css("display","block");
	  else
		  $("#"+item).css("display","none");   
	}
	function showContent(obj){
		if(obj.value==="0"){
			$("#s_tb0").show();
			$("#s_tb1").hide();
			$("#initSelectValue").attr("value","");
			$("#initSelectViewValue").attr("value","");
			$("#childControlIds").attr("value","");
			$("#childControlIds").attr("disabled","disabled");
			$("#selectList").attr("value","");
		}else{
			$("#s_tb0").hide();
			$("#s_tb1").show();
			$("#initSelectValue").attr("value","");
			$("#initSelectViewValue").attr("value","");
			$("#childControlIds").attr("disabled","");
			$("#optionGroupCode").attr("value","");
			$("#optionGroupId").attr("value","");
		}
	}
	$(function(){
		var item="${item}";
		if(item=="0"){
			$("#select0").attr("selected","selected");
			$("#initSelectValue").attr("value","");
			$("#selectList").attr("value","");
			$("#selectList").html("");
			$("#s_tb0").show();
			$("#s_tb1").hide();
		}else{
			$("#select1").attr("selected","selected");
			$("#optionGroupCode").attr("value","");
			$("#optionGroupId").attr("value","");
			$("#s_tb0").hide();
			$("#s_tb1").show();
		}
	});
	function selectOptionSource(){
		$.colorbox({href:webRoot+"/form/option-source.htm?selectPageFlag="+true,iframe:true, innerWidth:400, innerHeight:300,overlayClose:false,title:iMatrixMessage["formManager.optionSource"],onClosed:function(){
		}});
	}
	</script>
	<style type="text/css">
	.form-table-without-border td input{
		width:80%;
	}
	</style>
	</head>
	<body onload="getContentHeight();">
	<div class="ui-layout-center">
		<div class="opt-body">
			<div class="opt-btn">
				<button class="btn" onclick="$('#radioCheckboxForm').submit();"><span><span><s:text name="menuManager.confirm"></s:text></span></span></button>
				<button class="btn" onclick='parent.$.colorbox.close();'><span><span ><s:text name="cancel"></s:text></span></span></button>
			</div>
			<div id="opt-content">
				<aa:zone name="controlContent">
					<form name="radioCheckboxForm" id="radioCheckboxForm" action="${mmsCtx }/form/form-view-text.htm">
						<s:hidden name="id"></s:hidden>
						<s:hidden id="formId" name="formId"></s:hidden>
						<s:hidden id="code" name="code"></s:hidden>
						<s:hidden id="version" name="version"></s:hidden>
						<s:hidden id="standard" name="standard"></s:hidden>
						<s:hidden id="occasion" name="occasion" value="changeSource"></s:hidden>
						<table class="form-table-without-border">
							<tr>
								<td class="content-title" style="width: 22%;"><s:text name="formManager.controlType"></s:text>：</td>
								<td>
									<s:if test="standard">
										<s:select theme="simple" id="controlType" list="#{'RADIO':getText('formManager.radio'),'CHECKBOX':getText('formManager.checkbox')}" 
										 name="formControl.controlType"></s:select>
									 </s:if><s:else>
											<s:select theme="simple" id="controlType" list="#{'RADIO':getText('formManager.radio'),'CHECKBOX':getText('formManager.checkbox')}" 
											 name="formControl.controlType"></s:select>
									 </s:else>
								</td>
								<td><span id="controlTypeTip"></span></td>	
							</tr>
							<s:if test="standard">
								<tr>
									<td class="content-title"><s:text name="formManager.field"></s:text>：</td>
									<td>
										<s:hidden id="dataType" name="formControl.dataType"></s:hidden>
										<s:select onchange="choiceControl();" id="tableColumnId" name="tableColumnId" list="columns" theme="simple" listKey="id" listValue="displayName" headerKey="0" headerValue="请选择"></s:select>
									</td>
									<td></td>	
								</tr>
								<tr>
									<td class="content-title"><s:text name="formManager.fieldName"></s:text>：</td>
									<td>
										<s:if test="tableColumnId==null||tableColumnId==0">
											<s:textfield theme="simple" id="name" name="formControl.name" cssClass="{required:true,messages: {required:'必填'}}" ></s:textfield>
										</s:if>
										<s:else>
											<s:textfield theme="simple" id="name" name="formControl.name" readonly="true" cssClass="{required:true,messages: {required:'必填'}}" ></s:textfield>
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
										<s:textfield theme="simple" id="name" maxlength="27" name="formControl.name"  cssClass="{required:true,messages: {required:'必填'}}" ></s:textfield>
										<span class="required">*</span>
									</td>
									<td>
										<span id="nameTip"></span>
									</td>	
								</tr>
								<tr>	
									<td class="content-title"><s:text name="formManager.fieldType"></s:text>：</td>	
									<td>
										<s:select theme="simple" id="dataType" list="#{'TEXT':getText('formManager.text'),'INTEGER':getText('formManager.integer'),'LONG':getText('formManager.long'),'DOUBLE':getText('formManager.double'),'FLOAT':getText('formManager.float'),'BOOLEAN':getText('formManager.boolean'),'CLOB':getText('formManager.longText')}" 
								 name="formControl.dataType"></s:select>
									</td>
								</tr>
							</s:else>
							<tr>
								<td class="content-title"><s:text name="formManager.fieldAlias"></s:text>：</td>
								<td>
									<s:textfield theme="simple" id="title" name="formControl.title" cssClass="{required:true,messages: {required:'必填'}}" ></s:textfield>
									<span class="required">*</span>
								</td>
								<td><span id="titleTip"></span></td>	
							</tr>
							<tr>
								<td  class="content-title"><s:text name="formManager.initChoose"></s:text>：</td>
								<td >
								<s:textfield theme="simple" id="initSelectViewValue" name="" readonly="true"/>&nbsp;<a href="#" onclick="$('#initSelectValue').attr('value','');$('#initSelectViewValue').attr('value','');" title='<s:text name="menuManager.clear"></s:text>'  class="small-btn"><span><span><s:text name="menuManager.clear"></s:text></span></span></a>
								<s:hidden  theme="simple" id="initSelectValue" name="formControl.initSelectValue"/>
								</td>
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
								<td class="content-title"><s:text name="formManager.optionSource"></s:text>:</td>
								<td>
									<select name="formControl.item" id="item" onchange="showContent(this);">
										<option id="select0" value="0"><s:text name="formManager.optionGroup"></s:text></option>
										<option id="select1" value="1" selected="selected"><s:text name="formManager.keyValue"></s:text></option>
									</select>
								</td>
								<td></td>	
							</tr>
							<tr id="s_tb0" style="display: none;">
								<td class="content-title"><s:text name="formManager.optionGroup"></s:text>:</td>
								<td>
									<input readonly="readonly" name="formControl.optionGroupCode" id="optionGroupCode" value=<s:property value="formControl.optionGroupId.equals(\"null\")?\"\":formControl.optionGroupCode" /> >&nbsp;<a href="#" onclick="selectOptionSource()"  class="small-btn"><span><span><s:text name="formManager.button.choose"></s:text></span></span></a>
									<input type="hidden" name="formControl.optionGroupId" id="optionGroupId" value="${formControl.optionGroupId }" />
								</td>
								<td></td>	
							</tr>	
						</table>
						<br>
						<table id="s_tb1">
							<tr>
								<td><s:text name="formManager.radioCheckBox"></s:text><br>
									<s:text name="formManager.show"></s:text>:<input id="txtText" name="txtText" style="width:150px;"/>&nbsp;&nbsp;<s:text name="formManager.cancelStorageValue"></s:text>:<input id="txtVal" name="txtVal" style="width:130px;"/>
								</td>
								<td vAlign="bottom" align="right">
									<a href="#" onclick="Add();" title="<s:text name="formManager.add"></s:text>"  class="small-btn"><span><span><s:text name="formManager.add"></s:text></span></span></a>
									<a href="#" onclick="Modify(this);" title="<s:text name="menuManager.update"></s:text>"  class="small-btn"><span><span><s:text name="menuManager.update"></s:text></span></span></a>
								</td>
							</tr>
							<tr>
								<td>
									<select id="selectList" style="WIDTH: 340px" onchange="$('#selectList').get(0).selectedIndex = this.selectedIndex;Select(this);"
										size="5" name="selectList">
									<s:iterator value="selectList" status="ind" var="list">
										<option value="${list[1]}">${list[0]}</option>
									</s:iterator>	
									</select>
									<s:hidden id="selectValues" name="formControl.selectValues"></s:hidden>
								</td>
								<td vAlign="bottom">
									<a href="#" onclick="Move(-1);" title="<s:text name="formManager.moveUp"></s:text>"  class="small-btn"><span><span><s:text name="formManager.moveUp"></s:text></span></span></a>
									<br/>
									<a href="#" onclick="Move(1);" title="<s:text name="formManager.moveDown"></s:text>"  class="small-btn"><span><span><s:text name="formManager.moveDown"></s:text></span></span></a>
								</td>
							</tr>
							<TR>
								<TD colSpan="2">
									<a href="#" onclick="SetSelectedValue();" title="<s:text name="formManager.setInitChooseValue"></s:text>"  class="small-btn"><span><span><s:text name="formManager.setInitChooseValue"></s:text></span></span></a>
									<a href="#" onclick="Delete();" title="<s:text name="menuManager.delete"></s:text>"  class="small-btn"><span><span><s:text name="menuManager.delete"></s:text></span></span></a>
							</TR>
						</table>
					</form>
					<script type="text/javascript">
					$(document).ready(function(){
						setInitSelectViewValue();
					});
					
					function validateText(){
						$("#radioCheckboxForm").validate({
							submitHandler: function() {
								generateHtml();
							}
						});
					}
					validateText();
					</script>
			</aa:zone>
			</div>
		</div>
	</div>
	</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script src="${resourcesCtx}/widgets/timepicker/timepicker_<%=com.norteksoft.product.util.ContextUtils.getCurrentLanguage()%>.js" type="text/javascript"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
</html>
