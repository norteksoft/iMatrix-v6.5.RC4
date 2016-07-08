<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/mms-taglibs.jsp"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>紧急程度设置控件</title>
	<%@ include file="/common/mms-iframe-meta.jsp"%>

	<script src="${imatrixCtx}/widgets/formeditor/kindeditor.js" type="text/javascript"></script>
	<script src="${imatrixCtx}/widgets/formeditor/lang/zh_CN.js" type="text/javascript"></script>
	<script src="${imatrixCtx}/widgets/formeditor/formeditor.js" type="text/javascript"></script>
	<link href="${imatrixCtx}/widgets/formeditor/formeditor.css" rel="stylesheet" type="text/css" />
	<script type="text/javascript">
	function choiceControl(){
		if($("#tableColumnId").get(0).selectedIndex==0){
			$("#name").attr("value","");
		}
		ajaxAnyWhereSubmit("textForm", "", "controlContent");
	}
	function generateHtml(){
		if("${standard}"=="true"){
			if($("#tableColumnId").get(0).selectedIndex==0){
				iMatrix.alert(iMatrixMessage["formManager.selectFieldInfo"]);
				return;
			}else{
				if($("#controlType").attr("value").toUpperCase()=="CREATE_SPECIAL_TASK"){
					parent.specialTaskHtml($("#tableColumnId").attr("value"));
				}else{
					iMatrix.alert(iMatrixMessage["formManager.specialFieldInfo"]);
					return;
				}
			}
		}else{
			parent.specialTaskHtml("");
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
		<button class="btn" onclick="generateHtml();"><span><span><s:text name="menuManager.confirm"></s:text></span></span></button>
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
				<div style="margin-bottom: 10px;"><s:text name="formManager.specialControlInfo"></s:text>
				</div>
				<table  class="form-table-without-border">
					<tr>
						<td class="content-title"><s:text name="formManager.controlType"></s:text>：</td>
						<td>
							<s:text name="%{formControl.controlType.code}"></s:text>  <!--textfield改-->
							<s:hidden theme="simple" id="controlType" name="formControl.controlType" ></s:hidden>
						</td>
					</tr>	
					<s:if test="standard">
						<tr>
							<td class="content-title"><s:text name="formManager.field"></s:text>：</td>
							<td>
								<s:select onchange="choiceControl();" id="tableColumnId" name="tableColumnId" list="columns" theme="simple" listKey="id" listValue="alias" headerKey="0" headerValue="请选择"></s:select>
							</td>
							<td></td>	
						</tr>
						<tr>
							<td class="content-title"><s:text name="formManager.fieldName"></s:text>：</td>
							<td>
								<s:if test="tableColumnId==null||tableColumnId==0">
									<s:textfield theme="simple" id="name" name="formControl.name" ></s:textfield>
								</s:if>
								<s:else>
									<s:textfield theme="simple" id="name" name="formControl.name" readonly="true"></s:textfield>
								</s:else>
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
								<input id="name" name="formControl.name" value="CREATE_SPECIAL_TASK" readonly="readonly"/>
							</td>
							<td>
								<span id="nameTip"></span>
							</td>	
						</tr>
					</s:else>
					<tr>
						<td class="content-title"><s:text name="formManager.fieldAlias"></s:text>：</td>
						<td>
							<s:textfield theme="simple" id="title" name="formControl.title" ></s:textfield>
						</td>
						<td><span id="titleTip"></span></td>	
					</tr>
			    </table>
			</form>
		</aa:zone>
	</div>
  </div>
 </div>
</body>
<script src="${resourcesCtx}/widgets/timepicker/timepicker_<%=com.norteksoft.product.util.ContextUtils.getCurrentLanguage()%>.js" type="text/javascript"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>