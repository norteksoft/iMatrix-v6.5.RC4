<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/mms-taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<title><s:text name="mms.formManager"/></title>
	
</head>
<body style="height:100%;width: 100%;text-align: left;">
<div class="ui-layout-west">
	</div>
<div class="ui-layout-center">
	  	<aa:zone name="form_main">
	  		<div id="message" style="display:none;"></div>
			<table width="100%" align="center" height="100%" class="TableBlock">
				<tbody>
					<tr bgcolor="#DDDDDD">
						<td valign="top" align="center" >
							<div id="controlDivId" style="width:185px;overflow: auto;">
							<table cellspacing="0" cellpadding="0" border="0" align="center" class="TableBlock">
								<tbody>
									<tr class="TableHeader">
										<td align="center"><s:text name="formManager.formController"></s:text></td>
									</tr>
									<tr class="TableData">
									  	<td align="center">
									  	<button onclick="controlClick('TEXT');" class="btnTool"><img src="${imatrixCtx}/widgets/formeditor/form/textfield.gif"/> <s:text name="formManager.textField"></s:text></button><br>
										<button onclick="controlClick('TEXTAREA');" class="btnTool"><img src="${imatrixCtx}/widgets/formeditor/form/textarea.gif"/> <s:text name="formManager.textarea"></s:text></button><br>
										<button onclick="controlClick('PULLDOWNMENU');" class="btnTool"><img src="${imatrixCtx}/widgets/formeditor/form/listmenu.gif"/> <s:text name="formManager.select"></s:text></button><br>
										<button onclick="controlClick('RADIO');" class="btnTool"><img src="${imatrixCtx}/widgets/formeditor/form/checkbox.gif"/> <s:text name="formManager.radioAndcheckbox"></s:text></button><br>
										<button onclick="controlClick('TIME');" class="btnTool"><img src="${imatrixCtx}/widgets/formeditor/form/calendar.gif"/> <s:text name="formManager.calendar"></s:text></button><br>
										<button onclick="controlClick('PLACEHOLDER');" class="btnTool"><img src="${imatrixCtx}/widgets/formeditor/form/label.gif"/> <s:text name="formManager.placeholder"></s:text></button><br>
										<button onclick="controlClick('MACRO');" class="btnTool"><img src="${imatrixCtx}/widgets/formeditor/form/auto.gif"/> <s:text name="formManager.macro"></s:text></button><br>
										<button onclick="controlClick('CALCULATE_COMPONENT');" class="btnTool"><img src="${imatrixCtx}/widgets/formeditor/form/calc.gif"/> <s:text name="formManager.calculate"></s:text></button><br>
										<button onclick="controlClick('SELECT_MAN_DEPT');" class="btnTool"><img src="${imatrixCtx}/widgets/formeditor/form/user.gif"/> <s:text name="formManager.departPerson"></s:text></button><br>
										<button onclick="controlClick('BUTTON');" class="btnTool"><img src="${imatrixCtx}/widgets/formeditor/form/button.gif"/> <s:text name="formManager.button"></s:text></button><br>
										<button onclick="controlClick('LABEL');" class="btnTool"><img src="${imatrixCtx}/widgets/formeditor/form/label.gif"/> <s:text name="formManager.label"></s:text></button><br>
										<button onclick="controlClick('STANDARD_LIST_CONTROL');" class="btnTool"><img src="${imatrixCtx}/widgets/formeditor/form/standardList.gif"/> <s:text name="formManager.standList"></s:text></button><br>
										<button onclick="JCControlClick('JAVASCRIPT_CSS');" class="btnTool"><img src="${imatrixCtx}/widgets/formeditor/form/js_css.gif"/> JavaScript/CSS</button><br>
										<button onclick="controlClick('ATTACH_UPLOAD');" class="btnTool"><img src="${imatrixCtx}/widgets/formeditor/form/attach.gif"/> <s:text name="formManager.attachUpload"></s:text></button><br>
										<button onclick="imageControlClick('IMAGE');" class="btnTool"><img src="${imatrixCtx}/widgets/formeditor/form/imgupload.gif"/> <s:text name="formManager.picture"></s:text></button><br>
										<button onclick="controlClick('IMAGE_UPLOAD');" class="btnTool"><img src="${imatrixCtx}/widgets/formeditor/form/imgupload.gif"/> <s:text name="formManager.pictureUpload"></s:text></button><br>
										<button onclick="controlClick('SIGNATURE_CONTROL');" class="btnTool"><img src="${imatrixCtx}/widgets/formeditor/form/seal.gif"/> <s:text name="formManager.signature"></s:text></button><br>
										<button onclick="controlClick('LIST_CONTROL');" class="btnTool"><img src="${imatrixCtx}/widgets/formeditor/form/customList.gif"/> <s:text name="formManager.customList"></s:text></button><br>
										<button onclick="controlClick('DATA_SELECTION');" class="btnTool"><img src="${imatrixCtx}/widgets/formeditor/form/selectData.gif"/> <s:text name="formManager.dataSelect"></s:text></button><br>
										<button onclick="controlClick('DATA_ACQUISITION');" class="btnTool"><img src="${imatrixCtx}/widgets/formeditor/form/getData.gif"/> <s:text name="formManager.dataGet"></s:text></button><br>
										<button onclick="controlClick('URGENCY');" class="btnTool"><img src="${imatrixCtx}/widgets/formeditor/form/urgen.gif"/> <s:text name="formManager.urgencyLevel"></s:text></button><br>
										<button onclick="controlClick('CREATE_SPECIAL_TASK');" class="btnTool"><img src="${imatrixCtx}/widgets/formeditor/form/urgen.gif"/> <s:text name="formManager.special"></s:text></button><br>
										<button onclick="controlClick('OPINION');" class="btnTool"><img src="${imatrixCtx}/widgets/formeditor/form/textarea.gif"/> <s:text name="formManager.suggestion"></s:text></button><br>
										<button onclick="editorSave(editor);" class="btnControl"><s:text name="formManager.saveForm"></s:text></button><br>
										<button onclick="toPreview();" class="btnControl"><s:text name="formManager.previewForm"></s:text></button><br>
										<button onclick="cancelClick();" class="btnControl"><s:text name="formManager.closeDesigner"></s:text></button>
										</td>
									</tr>
								</tbody>
							</table>
							</div>
						</td>
						<td  valign="top" bgcolor="#DDDDDD"  align="left">
							<div class="editor" style="width:100%;margin: 0px 5px 5px 0px;">
								<textarea id="content"  style="visibility:hidden;">${htmlCode}</textarea>
							</div>
						</td>
					</tr>
				</tbody>
			</table>
			<s:actionmessage/>
			<div style="display: none;">
				<form id="inputForm" name="inputForm" action="${mmsCtx }/form/form-view-save.htm" method="post">
					<s:hidden id="formId" name="formId"></s:hidden>
					<s:hidden id="operation" name="operation"></s:hidden>
					<s:textfield id="menuId" name="menuId" theme="simple"></s:textfield>
					<s:textfield id="isStandard" name="isStandard" theme="simple"></s:textfield>
					<s:textfield id="code" name="code" theme="simple"></s:textfield>
					<s:textfield id="name" name="name" theme="simple"></s:textfield> 
					<s:textfield id="version" name="version" theme="simple"></s:textfield>
					<s:textfield id="formStates" name="formState" theme="simple"></s:textfield> 
					<s:textarea theme="simple"  name="remark" id="remark"  cols="55" rows="5" ></s:textarea>
					<input id="html" name="htmlResult"></input>
				</form>			
			</div>	
			<div style="display: none;">
				<div id="saveChoice" style="margin-top: 5px">
					<div class="opt-btn" style="margin-bottom: 5px">
						<button class="btn" onclick="updateVersion();$.colorbox.close();"><span><span><s:text name="formManager.updateCurrentVesion"></s:text></span></span></button>
						<button class="btn" onclick="saveNewVersion();$.colorbox.close();"><span><span><s:text name="formManager.saveToNewVesion"></s:text></span></span></button>
						<button class="btn" onclick="$.colorbox.close();"><span><span ><s:text name="cancel"></s:text></span></span></button>
					</div>
					<div style="margin-left: 5px">
						<font color="red"><s:text name="formManager.updateFormInfo"></s:text></font> 
					</div>
				</div>
			</div>
			<form name="backForm" id="backForm" action="${mmsCtx }/form/list-data.htm" method="post">
				<s:hidden id="menuId" name="menuId"></s:hidden>
				<s:hidden name="dataTableId"></s:hidden>
			</form>
			<script type="text/javascript" >
				setControlDivStyle();
				function setControlDivStyle(){
					var h = $(window).height()+50;
					$("#controlDivId").css("height",h);
				}
			</script>
			
		</aa:zone>
</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script src="${resourcesCtx}/widgets/timepicker/timepicker_<%=com.norteksoft.product.util.ContextUtils.getCurrentLanguage()%>.js" type="text/javascript"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>
