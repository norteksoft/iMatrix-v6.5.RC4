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
		function choiceControl(type){
			$("#infoType").attr("value",type);
			ajaxAnyWhereSubmit("deptForm", "", "controlContent");
		}
		function generateHtml(){
			if("${standard}"=="true"){
				if($("#saveDeptControlValue").get(0).selectedIndex==0){
					alert("请选择保存信息的字段");
					return;
				}else{
					parent.sinatureHtml($("#name").attr("value")
							,$("#controlType").attr("value")
							,$("#showDeptControlValue").attr("value")
							,$("#showDeptControlId").attr("value")
							,$("#saveDeptControlValue").attr("value")
							,$("#saveDeptControlId").attr("value")
							,$("#classId").attr("value")
							,$("#styleId").attr("value")
							,$("#controlId").attr("value"));
				}
			}else{
				parent.sinatureHtml($("#name").attr("value")
						,$("#controlType").attr("value")
						,""
						,$("#showDeptControlId").attr("value")
						,""
						,$("#saveDeptControlId").attr("value")
						,$("#classId").attr("value")
						,$("#styleId").attr("value")
						,$("#controlId").attr("value"));
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
		<button class="btn" onclick="$('#deptForm').submit();"><span><span><s:text name="menuManager.confirm"></s:text></span></span></button>
		<button class="btn" onclick='parent.$.colorbox.close();'><span><span ><s:text name="cancel"></s:text></span></span></button>
	</div>
	<div id="opt-content">
		<aa:zone name="controlContent">
			<form name="deptForm" id="deptForm" action="${mmsCtx }/form/form-view-text.htm">
				<s:hidden name="id"></s:hidden>
				<s:hidden id="formId" name="formId"></s:hidden>
				<s:hidden id="code" name="code"></s:hidden>
				<s:hidden id="version" name="version"></s:hidden>
				<s:hidden id="standard" name="standard"></s:hidden>
				<s:hidden id="infoType" name="infoType"></s:hidden>
				<s:hidden id="occasion" name="occasion" value="changeSource"></s:hidden>
				<table>
					<tr>
				      		<td width="150"><s:text name="formManager.controlName"></s:text>：</td>
				      		<td><s:textfield  theme="simple" name="formControl.name" id="name"  cssClass="{required:true,messages: {required:'必填'}}"/>
				      		</td>
				      		<td><span id="nameTip"></span></td>
				      </tr>
				      <tr>
							<td><s:text name="formManager.controlType"></s:text>：</td>
							<td>
								<s:text name="%{formControl.controlType.code}"></s:text><!-- textfield改 -->
								<s:hidden theme="simple" id="controlType" name="formControl.controlType" ></s:hidden>
							</td>
							<td><span id="controlTypeTip"></span></td>	
						</tr>
						<s:if test="standard">
						  <tr>
					      	<td><s:text name="formManager.showInformationFieldName"></s:text>：</td>
					      	<td>
								<s:select onchange="choiceControl('show');" id="showDeptControlValue" name="formControl.showDeptControlValue" list="columns" theme="simple" listKey="name" listValue="alias" headerKey="0" headerValue="请选择"></s:select>
							</td>
					      	<td></td>
					      </tr>
					      <tr >
					      		<td><s:text name="formManager.showInformationControlFiledName"></s:text>：</td>
					      		<td><s:textfield  theme="simple" name="formControl.showDeptControlId" id="showDeptControlId"/></td>
					      		<td><span id="showDeptControlIdTip"></span></td>
					      </tr>
					      <tr >
					      		<td><s:text name="formManager.searchSignFieldName"></s:text>：</td>
					      		<td>
									<s:select onchange="choiceControl('save');" id="saveDeptControlValue" name="formControl.saveDeptControlValue" list="columns" theme="simple" listKey="name" listValue="alias" headerKey="0" headerValue="请选择"></s:select>
								</td>
					      		<td></td>
					      </tr>
					      <tr >
					      		<td><s:text name="formManager.saveSignInputControlName"></s:text>：</td>
					      		<td><s:textfield  theme="simple" name="formControl.saveDeptControlId" id="saveDeptControlId"  cssClass="{required:true,messages: {required:'必填'}}"/></td>
					      		<td><span id="saveDeptControlIdTip"></span></td>
					      </tr>
						</s:if><s:else>
						<tr >
				      		<td><s:text name="formManager.showInformationControlFiledName"></s:text>：</td>
				      		<td><s:textfield  theme="simple" name="formControl.showDeptControlId" id="showDeptControlId"/></td>
				      		<td><span id="showDeptControlIdTip"></span></td>
					      </tr>
					     <tr >
				      		<td><s:text name="formManager.saveSignInputControlName"></s:text>：</td>
				      		<td><s:textfield  theme="simple" name="formControl.saveDeptControlId" id="saveDeptControlId" cssClass="{required:true,messages: {required:'必填'}}"/></td>
				      		<td><span id="saveDeptControlIdTip"></span></td>
					      </tr>
						</s:else>
				     <tr>
						<td><s:text name="formManager.styleClass"></s:text>：</td>
						<td>
							<s:textfield theme="simple" id="classId" name="formControl.classStyle"></s:textfield>
							<br/><s:text name="formManager.attention"></s:text>
						</td>
						<td></td>	
					</tr>
					<tr>
						<td><s:text name="formManager.inlineStyle"></s:text>：</td>
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
				$("#deptForm").validate({
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
<script src="${resourcesCtx}/widgets/timepicker/timepicker_<%=com.norteksoft.product.util.ContextUtils.getCurrentLanguage()%>.js" type="text/javascript"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
</html>
