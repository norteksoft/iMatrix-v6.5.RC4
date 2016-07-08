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
		ajaxAnyWhereSubmit("macroForm", "", "controlContent",choiceControlCallBack);
	}
	function choiceControlCallBack(){
		$("#selectList").html(selectListHtml);
	}
	function generateHtml(){
		var dataType = $("#dataType").val();
		var controlType=$("#controlType").attr("value");
		var macroReadonly=$("#macroReadonly").attr("checked");
		var classStyle="";
		var styleContent="";
		if($("#classId").attr("value")!=""){
			classStyle=" class='"+$("#classId").attr("value")+"'";
		}
		if($("#styleId").attr("value")!=""){
			styleContent=" style='"+$("#styleId").attr("value")+"'";
		}
		var html;
		if($("#macroType").attr("value").indexOf("SYS_LIST_")>-1){
			html ='<select pluginType="MACRO" dataType="TEXT"'
				+" id='"+($("#controlId").attr("value")==""?$("#name").attr("value"):$("#controlId").attr("value"))+"' "
				+' name="'+$("#name").attr("value")+'" ' 
				+' title="'+$("#title").attr("value")+'" '
				+' macroType="'+$("#macroType").attr("value")+'" '
				+' macroHide="'+$("#macroHide").attr('checked')+'" '
				+' macroReadonly="'+$("#macroReadonly").attr('checked')+'" '
				+classStyle
				+styleContent
				+'>'
				+'<option selected="selected" value="">{MACRO}</option>';
			html=html+"</select>";
		}else{
			html ="<input pluginType='MACRO' type='TEXT' dataType='TEXT' value='{MACRO}'"
				+" id='"+($("#controlId").attr("value")==""?$("#name").attr("value"):$("#controlId").attr("value"))+"' "
				+" name='"+$("#name").attr("value")+"' " 
				+" title='"+$("#title").attr("value")+"' "
				+" macroType='"+$("#macroType").attr("value")+"' "
				+" macroHide='"+$("#macroHide").attr('checked')+"' "
				+' macroReadonly="'+$("#macroReadonly").attr('checked')+'" '
				+classStyle
				+styleContent;
			html=html+"/>";
		}
		parent.html(html);
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
				<button  class="btn" onclick="$('#macroForm').submit();"><span><span><s:text name="menuManager.confirm"></s:text></span></span></button>
				<button class="btn" onclick='parent.$.colorbox.close();'><span><span ><s:text name="cancel"></s:text></span></span></button>
			</div>
			<div id="opt-content">
				<aa:zone name="controlContent">
					<form name="macroForm" id="macroForm" action="${mmsCtx }/form/form-view-text.htm">
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
									<s:text name="%{formControl.controlType.code}"></s:text>
									<s:hidden theme="simple" id="controlType" name="formControl.controlType" ></s:hidden>
								</td>
								<td></td>	
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
									<s:textfield theme="simple" id="name" maxlength="27" name="formControl.name" onblur="fieldNameOk(this);" cssClass="{required:true,messages: {required:'必填'}}" ></s:textfield>
									<span class="required">*</span>
								</td>
								<td>
									<span id="nameTip"></span>
								</td>	
							</tr>
							</s:else>
							<tr>	
							<td class="content-title"><s:text name="formManager.hong"></s:text>：</td>	
							<td>
						 		<select id="macroType" style="width: 300px;">
								<optgroup label="<s:text name='formManager.single.input.box'/>">
								<option value="SYS_DATE"><s:text name="formManager.sys.date"></s:text></option>
								<option value="SYS_DATE_CN" ><s:text name="formManager.sys.date.cn"/></option>
								<option value="SYS_DATE_CN_SHORT3"><s:text name="formManager.sys.date.cn.short3"/></option>
								<option value="SYS_DATE_CN_SHORT4"><s:text name="formManager.sys.date.cn.short4"/></option>
								<option value="SYS_DATE_CN_SHORT1"><s:text name="formmanager.sys.date.cn.short1"/></option>
								<option value="SYS_DATE_CN_SHORT2"><s:text name="formmanager.sys.date.cn.short2"/></option>
								<option value="SYS_TIME"><s:text name="formManager.sys.time"/></option>
								<option value="SYS_DATETIME"><s:text name="formmanager.sys.datetime"/></option>
								<option value="SYS_WEEK"><s:text name="formmanager.sys.week"/></option>
								<option value="SYS_USERID"><s:text name="formManager.sys.userid"/></option>
								<option value="SYS_USERNAME"><s:text name="formManager.sys.username"></s:text></option>
								<option value="SYS_DEPTNAME"><s:text name="formmanager.sys.deptname"/></option>
								<option value="SYS_DEPTNAME_SHORT"><s:text name="formmanager.sys.deptname.short"/></option>
								<option value="SYS_USERROLE"><s:text name="formmanager.sys.userrole"/></option>
								<option value="SYS_USERNAME_DATE"><s:text name="formmanager.sys.username.date"/></option>
								<option value="SYS_USERNAME_DATETIME"><s:text name="formmanager.sys.username.datetime"/></option>
								<option value="SYS_FORMNAME"><s:text name="formmanager.sys.formname"/></option>
								<option value="SYS_MANAGER_NAME"><s:text name="formmanager.sys.manager.name"/></option>
								<option value="SYS_MANAGER_ID"><s:text name="formManager.sys.manager.id"/></option>
								<option value="SYS_MANAGER_DEPTNAME"><s:text name="formManager.sys.manager.deptname"/></option>
								<option value="SYS_MANAGER_DEPTNAME_SHORT"><s:text name="formmanager.sys.manager.deptname.short"/></option>
								<option value="SYS_TOPDEPTNAME"><s:text name="formmanager.sys.topdeptname"/></option>
								<option value="SYS_TOPDEPTNAME_SHORT"><s:text name="formmanager.sys.topdeptname.short"/></option>
								</optgroup>
								<optgroup label="<s:text name='formManager.pull.down.menu'/>">
								<option value="SYS_LIST_ROLE"><s:text name="formManager.sys.list.role"/></option>
								<option value="SYS_LIST_SUBORDINATE"><s:text name="formmanager.sys.list.subordinate"/></option>
								</optgroup>
							</select>
							</td>
							</tr>
							<tr>
								<td class="content-title"><s:text name="formManager.fieldOtherName"></s:text>：</td>
								<td>
								<input id="title" name="formControl.title" class="{required:true,messages: {required:'必填'}}" value="${formControl.title }"/><span class="required">*</span>
								<input id="controlId" type="hidden" name="formControl.controlId" class="" value="${formControl.controlId }"/>
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
							<tr>
								<td class="content-title"><s:text name="formManager.readOnly"></s:text>：</td>
								<td>
									<input  type="checkbox" id="macroReadonly" name="formControl.macroReadonly" value="false" <s:if test="formControl.macroReadonly">checked="checked"</s:if>/>
								</td>
								<td></td>	
							</tr>	
							<tr>
								<td class="content-title"><s:text name="formManager.singleTextField"></s:text>：</td>
								<td>
									<input type="checkbox" id="macroHide" name="formControl.macroHide" value="true" <s:if test="formControl.macroHide">checked="checked"</s:if>/>
								</td>
								<td></td>	
							</tr>	
						</table>
					</form>
					<script type="text/javascript">
					$(document).ready(function(){
						var _macroType="${formControl.macroType }";
						if(_macroType!=""){
							$("#macroType").attr("value",_macroType);
						}
					});
					function validateText(){
						$("#macroForm").validate({
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
