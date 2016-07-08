<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/setting-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<%@ include file="/common/setting-iframe-meta.jsp"%>
	<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
	<script type="text/javascript" src="${settingCtx}/js/interface.js"></script>
	
	
	<title>普通接口</title>
	<script type="text/javascript">
	$(document).ready(function(){
		validateInterface();
	});
	
	function validateInterface(){
		$("#jobInfoFrom").validate({
			submitHandler: function() {
				var emails = $("#emails").attr("value");
				if(emails.length>1000){
					iMatrix.alert(iMatrixMessage["bs.jobInfo.fieldEmailReminder"]+emails.length);
					return;
				}
				var rtxAccounts = $("#rtxAccounts").attr("value");
				if(rtxAccounts.length>1000){
					iMatrix.alert(iMatrixMessage["interfaceManager.rtxValidate"]+rtxAccounts.length);
					return;
				}
				var phoneReminderNums = $("#phoneReminderNums").attr("value");
				if(phoneReminderNums.length>1000){
					iMatrix.alert(iMatrixMessage["interfaceManager.messageValidate"]+phoneReminderNums.length);
					return;
				}
				var officeHelperReminderNames = $("#officeHelperReminderNames").attr("value");
				if(officeHelperReminderNames.length>490){
					iMatrix.alert(iMatrixMessage["interfaceManager.officerValidate"]+officeHelperReminderNames.length);
					return;
				}
				ajaxSubmit("jobInfoFrom",webRoot+"/options/interface-setting-save.htm", "interface-zones",saveCallback);
			},
			rules: {
				code: "required"
			},
			messages: {
				code: iMatrixMessage["menuManager.required"]
			}
		});
	}
	$.validator.addMethod("customRequired", function(value, element) {
		var $element = $(element);
		if($element.val()!=null&&$element.val()!=''&&typeof ($element.val())!='undefined'){
			return true;
		}
		
		if($("#dataSourceId").val()!=null&&$("#dataSourceId").val()!=''&&typeof ($("#dataSourceId").val())!='undefined'){
			return true;
		}
	}, iMatrixMessage["menuManager.required"]);
	function saveCallback(){
		showMsg();
		validateInterface();
	}

	//小窗体是否存在
	function isInterfaceExist(){
		$.ajax({
			   type: "POST",
			   url: webRoot+"/options/interface-setting-validate-code.htm",
			   data: "interfaceCode="+$("#code").attr("value")+"&id="+$($("form[id='jobInfoFrom']").find("input[id='id']")[0]).attr("value"),
			   success: function(msg){
				   if(msg=="true"){
					  iMatrix.alert(iMatrixMessage["interfaceManager.sourceCodeValidate"]);
				   }else{
					   $("#jobInfoFrom").submit();
				   }
			   }
		});
	}
	
	function submitInterface(){
		if($("#code").attr("value")==""){
			$("#jobInfoFrom").submit();
		}else{
			isInterfaceExist();
		}
	}

	
	</script>
</head>
<body onload="">
<div class="ui-layout-center">
<div class="opt-body">
	<div class="opt-btn">
		<a class="btn" href="#" onclick="submitInterface();"><span><span><s:text name="menuManager.save"></s:text> </span></span></a>
		<a class="btn" href="#" onclick="window.parent.$.colorbox.close();"><span><span><s:text name="interfaceManager.close"></s:text> </span></span></a>
	</div>
	<div id="opt-content" >
		<aa:zone name="interface-zones">
			<div id="message" style="display: none;"><s:actionmessage theme="mytheme" /></div>
			<form action="" name="jobInfoFrom" id="jobInfoFrom" method="post">
				<input  type="hidden" name="id" id="id" value="${id}"/>
				<table class="form-table-without-border">
						<tr>
							<td class="content-title"><s:text name="menuManager.code"></s:text> ：</td>
							<td> <input name="code" id="code" maxlength="30" value="${code }"/> <span class="required">*</span></td>
						</tr>
						<tr>
							<td class="content-title" style="width: 130px;"><s:text name="menuManager.name"></s:text> ：</td>
							<td> <input name="name" id="name" maxlength="100" value="${name }"/></td>
						</tr>	
						<tr>
							<td class="content-title"><s:text name="interfaceManager.dataSourceQuote"></s:text>：</td>
							<td> <select name="dataSourceId" id="dataSourceId" class="customRequired">
										<option value="">--<s:text name="basicSetting.choose"></s:text>--</option>
										<s:iterator value="datasources">
											<option value="${id }" <s:if test="dataSourceId==id">selected="selected"</s:if>>${code }</option>
										</s:iterator>
									</select><span class="required">*</span></td>
						</tr>	
						<tr>
							<td class="content-title" ><s:text name="interfaceManager.emailReminder"></s:text>:</td>
							<td > <input id="emails" name="emails" value="${emails }" style="width:300px;" ></input>&nbsp;&nbsp;<a href="#" onclick="remindUser('email');" class="small-btn" >
							<span><span><s:text name="basicSetting.chooseBtn"></s:text></span></span>
						</a>&nbsp;<a href="#" onclick="clearValue('emails');" class="small-btn" >
							<span><span><s:text name="menuManager.clear"></s:text></span></span>
						</a></td>
						</tr>
						<tr>
							<td class="content-title" ><s:text name="interfaceManager.rtxReminder"></s:text>:</td>
							<td> <input readonly="readonly" id="rtxAccounts" name="rtxAccounts" value="${rtxAccounts}" style="width:300px;"></input>&nbsp;&nbsp;<a href="#" onclick="remindUser('RTX');" class="small-btn" >
							<span><span><s:text name="basicSetting.chooseBtn"></s:text></span></span>
						</a>&nbsp;<a href="#" onclick="clearValue('rtxAccounts');" class="small-btn" >
							<span><span><s:text name="menuManager.clear"></s:text></span></span>
						</a></td>
						</tr>
						<tr>
							<td class="content-title" ><s:text name="interfaceManager.messageReminder"></s:text>:</td>
							<td> <input id="phoneReminderNums" name="phoneReminderNums" value="${phoneReminderNums}" style="width:300px;" onkeyup="value=this.value.replace(/[^-,0-9]/g,'');"></input>&nbsp;&nbsp;<a href="#" onclick="remindUser('phone');" class="small-btn" >
							<span><span><s:text name="basicSetting.chooseBtn"></s:text></span></span>
						</a>&nbsp;<a href="#" onclick="clearValue('phoneReminderNums');" class="small-btn" >
							<span><span><s:text name="menuManager.clear"></s:text></span></span>
						</a></td>
						</tr>
						<tr>
							<td class="content-title" ><s:text name="interfaceManager.officeReminder"></s:text>:</td>
							<td> <input readonly="readonly" id="officeHelperReminderNames" name="officeHelperReminderNames" value="${officeHelperReminderNames}" style="width:300px;"></input><input type="hidden" id="officeHelperReminderIds" name="officeHelperReminderIds" value="${officeHelperReminderIds}"></input>&nbsp;&nbsp;<a href="#" onclick="remindUser('swing');" class="small-btn" >
							<span><span><s:text name="basicSetting.chooseBtn"></s:text></span></span>
						</a>&nbsp;<a href="#" onclick="clearValue('officeHelperReminderNames','officeHelperReminderIds');" class="small-btn" >
							<span><span><s:text name="menuManager.clear"></s:text></span></span>
						</a></td>
						</tr>
						<tr>
							<td class="content-title"><s:text name="interfaceManager.creator"></s:text>：</td>
							<td>
								${creatorName }
							 </td>
						</tr>
						<tr>
							<td class="content-title"><s:text name="interfaceManager.createTime"></s:text>：</td>
							<td>
							<s:date name="createdTime"  format="yyyy-MM-dd HH:mm" />
							 </td>
						</tr>
						<tr>
							<td class="content-title"><s:text name="interfaceManager.instruction"></s:text>：</td>
							<td><textarea rows="5" cols="15" name="remark" onkeyup="javascript:if(this.value.length >=120){this.value=this.value.slice(0,120);alert('<s:text name="interfaceManager.charMaxLengthValidate"></s:text>');return false;}">${remark }</textarea></td>
						</tr>	
					</table>
			</form>
		</aa:zone>
	</div>
</div>
</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script src="${resourcesCtx}/widgets/timepicker/timepicker_<%=com.norteksoft.product.util.ContextUtils.getCurrentLanguage()%>.js" type="text/javascript"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
</html>