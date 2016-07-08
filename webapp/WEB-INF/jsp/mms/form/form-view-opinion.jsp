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
	function generateHtml(){
		var componentWidth=$("#componentWidth").attr("value");
		var componentHeight=$("#componentHeight").attr("value");
		if(componentWidth=="")componentWidth=300;
		if(componentHeight=="")componentHeight=100;
		html ="<textarea pluginType='OPINION' "
		+" id='"+$("#controlId").attr("value")+"'"
		+" title='"+$("#title").attr("value")+"'"
		+" name='"+$("#controlId").attr("value")+"'"
		+" style='width:"+componentWidth+"px;height:"+componentHeight+"px;'"
		+" width='"+componentWidth+"'"
		+" height='"+componentHeight+"'"
		+" conclusionSource='"+$("#conclusionSource").val()+"'"
		+" titleVisible='"+$("#titleVisible").attr('checked')+"'"
		+" conclusionPosition='"+$("#conclusionPosition").val()+"'"
		+" conclusionType='"+$("#conclusionType").val()+"'"
		+" signRename='"+$("#signRename").val()+"'"
		+" dateRename='"+$("#dateRename").val()+"'"
		+" conclusionSourceValue='"+$("#conclusionSourceValue").val()+"'"
		+"></textarea>";
		parent.html(html);
	}

	function clearConclusionSource(){
		$("#conclusionSource").attr("value","");
		$("#conclusionSourceValue").attr("value","");
	}
	function selectConclusionSource(){
		$.colorbox({href:webRoot+"/form/conclusion-source.htm?selectPageFlag="+true,iframe:true, innerWidth:400, innerHeight:300,overlayClose:false,title:iMatrixMessage["formManager.conclusionSource"]});
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
				<table class="form-table-without-border">
					<tbody>
						<tr>
							<td class="content-title" style="width: 120px;"><s:text name="formManager.controlType"></s:text>：</td>
							<td class="no-edit">
								<s:text name="%{formControl.controlType.code}"></s:text>  <!--textfield改-->
								<s:hidden theme="simple" id="controlType" name="formControl.controlType" ></s:hidden>
							</td>
							<td>
							</td>	
						</tr>	
						<tr>
							<td class="content-title"><s:text name="form.inputstandard.field.fieldid"></s:text>：</td>
							<td>
								<s:textfield theme="simple" id="controlId" name="formControl.controlId"  cssClass="{required:true,messages: {required:'必填'}}" maxlength="27"></s:textfield>
								<span class="required">*</span>
							</td>
							<td>
								<span id="controlIdTip"></span>
							</td>	
						</tr>
						<tr>
							<td class="content-title"><s:text name="menuManager.name"></s:text>：</td>
							<td>
								<s:textfield theme="simple" id="title" name="formControl.title"  cssClass="{required:true,messages: {required:'必填'}}" maxlength="27"></s:textfield>
								<span class="required">*</span>
							</td>
							<td>
								<span id="titleTip"></span>
							</td>	
						</tr>
						<tr>
							<td class="content-title"><s:text name="formManager.showNameInfo"></s:text>：</td>
							<td>
								<input id="titleVisible" type="checkbox"  name="formControl.titleVisible"<s:if test="formControl.titleVisible">checked="checked"</s:if>>  <s:text name="common.yes"></s:text>
							</td>
							<td>
							</td>	
						</tr>
						<tr>
							<td class="content-title"><s:text name="formManager.conclusionSource"></s:text>：</td>
							<td>
								<s:textfield theme="simple" readonly="true" id="conclusionSource" name="formControl.dataSrcName" ></s:textfield>
								<s:hidden theme="simple" id="conclusionSourceValue" name="formControl.dataSrc" ></s:hidden>
								<a class="small-btn" onclick="selectConclusionSource();"><span><span><s:text name="formManager.button.choose"></s:text></span></span></a>&nbsp;<a class="small-btn" onclick="clearConclusionSource();"><span><span><s:text name="menuManager.clear"></s:text></span></span></a>
							</td>
							<td>
							</td>	
						</tr>
						<tr>
							<td class="content-title"><s:text name="formManager.conclusionPosition"></s:text>：</td>
							<td>
								<s:select theme="simple" id="conclusionPosition" name="formControl.selectValues"
											list="#{'top':getText('formManager.opinionBoxUp'),'bottom':getText('formManager.opinionBoxDown')}"></s:select>
							</td>
							<td>
							</td>	
						</tr>
						<tr>
							<td class="content-title"><s:text name="formManager.conclusionControl"></s:text>：</td>
							<td>
								<s:select theme="simple" id="conclusionType" name="formControl.macroType"
											list="#{'radio':getText('formManager.radio'),'checkbox':getText('formManager.checkbox')}"></s:select>
							</td>
							<td>
							</td>	
						</tr>
						<tr>
							<td class="content-title"><s:text name="formManager.signRename"></s:text>：</td>
							<td>
								<s:textfield theme="simple" id="signRename" name="formControl.lcTitles"  ></s:textfield>
							</td>
							<td>
							</td>	
						</tr>
						<tr>
							<td class="content-title"><s:text name="formManager.dateRename"></s:text>：</td>
							<td>
								<s:textfield theme="simple" id="dateRename" name="formControl.lcSums"  ></s:textfield>
							</td>
							<td>
							</td>	
						</tr>
						<tr>
							<td class="content-title"><s:text name="formManager.width"></s:text>：</td>
							<td>
								<s:textfield theme="simple" id="componentWidth" name="formControl.componentWidth" onkeyup="value=this.value.replace(/[^0-9]/,'');" maxlength="9"></s:textfield>
							</td>
							<td></td>	
						</tr>
						<tr>
							<td class="content-title"><s:text name="formManager.height"></s:text>：</td>
							<td>
								<s:textfield theme="simple" id="componentHeight" name="formControl.componentHeight" onkeyup="value=this.value.replace(/[^0-9]/,'');" maxlength="9"></s:textfield>
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
			</script>
		</aa:zone>
	</div>
</div>
</div>
</body>
<script src="${resourcesCtx}/widgets/timepicker/timepicker_<%=com.norteksoft.product.util.ContextUtils.getCurrentLanguage()%>.js" type="text/javascript"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
</html>
