<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/setting-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<%@ include file="/common/setting-iframe-meta.jsp"%>
	<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
	
	
	<title>短信网关设置新建</title>
	<script type="text/javascript">
	$(document).ready(function(){
		validateSave();
	});
	
	function validateSave(){
		$("#smsGatewaySettingFrom").validate({
			submitHandler: function() {
				ajaxSubmit("smsGatewaySettingFrom",webRoot+"/sms/sms-gateway-setting-save.htm", "smsGatewaySettingZone",saveCallback);
			},
			rules: {
				gatewayCode: "required",
				gatewayName: "required"
			},
			messages: {
				gatewayCode: iMatrixMessage["menuManager.required"],
				gatewayName: iMatrixMessage["menuManager.required"]
			}
		});
	}
	
	function saveCallback(){
		showMsg();
		validateSave();
	}
	//保存	
	function submitSmsGatewaySetting(){
		var gatewayType = $("#gatewayType").val();
		var implClassName = $("#implClassName").val();
		if(gatewayType == 'smsSelf' && (implClassName == null || implClassName == '')){
			iMatrix.alert(iMatrixMessage["interfaceManager.gatewayValidate"]);
			return ;
		}
		
		$.ajax({
			   type: "POST",
			   url: webRoot+"/sms/sms-gateway-setting-validateCode.htm",//验证唯一性
			   data: "gatewayCode="+$('#gatewayCode').val()+"&id="+$('#id').val(),
			   success: function(msg){
				   if(msg == "true"){
					  iMatrix.alert(iMatrixMessage["interfaceManager.gatewayCodeExist"]);
				   }else if(msg == "false"){
					   $("#smsGatewaySettingFrom").submit();
				   }
			   }
		});

	}
	function changeGatewayType(){
		
		var values = $("#gatewayType").val();

		if(values != 'smsSelf'){
			$('#implClassName').attr("disabled","disabled");
			$('#implClassName').attr("style","background: #999999");
		}else{
			$('#implClassName').attr("disabled","");
			$('#implClassName').attr("style","background: #FFFFFF");
		}
	}
	</script>
</head>
<body onload="">
<div class="ui-layout-center">
<div class="opt-body">
	<div class="opt-btn">
		<a class="btn" href="#" onclick="submitSmsGatewaySetting();"><span><span><s:text name="menuManager.save"></s:text></span></span></a>
		<a class="btn" href="#" onclick="window.parent.$.colorbox.close();"><span><span><s:text name="interfaceManager.close"></s:text></span></span></a>
	</div>
	<div id="opt-content" >
		<aa:zone name="smsGatewaySettingZone">
			<div id="message" style="display: none;"><s:actionmessage theme="mytheme" /></div>
			
			<form action="" name="smsGatewaySettingFrom" id="smsGatewaySettingFrom" method="post">
				
				<input  type="hidden" name="id" id="id" value="${id}"/>
				
				<table class="form-table-without-border">
					<tr>
						<td class="content-title"><s:text name="messagePlatform.getewayCode"></s:text>：</td>
						<td> <input name="gatewayCode" id="gatewayCode" maxlength="30" value="${gatewayCode }"/> <span class="required">*</span></td>
					</tr>
					<tr>
						<td class="content-title" style="width: 130px;"><s:text name="messagePlatform.getewayName"></s:text>：</td>
						<td> <input name="gatewayName" id="gatewayName" maxlength="100" value="${gatewayName }"/> <span class="required">*</span></td>
					</tr>	
					<tr>
						<td class="content-title" style="width: 130px;"><s:text name="messagePlatform.getewayType"></s:text>：</td>
						<td> 
							<select id="gatewayType" name="gatewayType" onchange="changeGatewayType();" >
								<option value="smsCat" <s:if test="gatewayType == 'smsCat'">selected="selected"</s:if>><s:text name="messagePlatform.smsCat"></s:text></option>
								<option value="smsWeimi" <s:if test="gatewayType == 'smsWeimi'">selected="selected"</s:if>><s:text name="messagePlatform.MicroNet"></s:text></option>
								<option value="smsSelf" <s:if test="gatewayType == 'smsSelf'">selected="selected"</s:if>><s:text name="messagePlatform.custom"></s:text></option>
							</select>	
							<span class="required">*</span>
						</td>
					</tr>
						
					<tr   >
						<td class="content-title" style="width: 130px;"><s:text name="messagePlatform.warnMaxSms"></s:text>：</td>
						<td> <input name="maxcountTowarn" id="maxcountTowarn" maxlength="100"  <s:if test="id == null ">value="200"</s:if> <s:else>value="${maxcountTowarn }"</s:else> /> </td>
					</tr>	
					
					<tr   >
						<td class="content-title" style="width: 130px;"><s:text name="messagePlatform.implementor"></s:text>：</td>
						<td> <input name="implClassName" id="implClassName" maxlength="100" value="${implClassName }" <s:if test="id != null && gatewayType == 'smsSelf'"> </s:if> <s:else>disabled="disabled" style="background: #999999" </s:else>/> <span class="required">*</span></td>
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