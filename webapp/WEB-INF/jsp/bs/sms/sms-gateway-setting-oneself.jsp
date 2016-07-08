<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/setting-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<%@ include file="/common/setting-iframe-meta.jsp"%>
	<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
	
	
	<title>自定义配置</title>
	<script type="text/javascript">
	$(document).ready(function(){
		validateSave();
	});
	
	function validateSave(){
		$("#smsGatewaySettingFrom").validate({
			submitHandler: function() {
			ajaxSubmit("smsGatewaySettingFrom",webRoot+"/sms/sms-gateway-setting-saveConfig.htm", "smsGatewaySettingZone",saveCallback);
			},
			rules: {
				configuration: "required",
				maxTime: "required"
			},
			messages: {
				configuration: iMatrixMessage["menuManager.required"],
				maxTime: iMatrixMessage["menuManager.required"]
			}
		});
	}
	
	function saveCallback(){
		showMsg();
		validateSave();
	}
	//保存	
	function save(){
		var id = $("#id").val();//id
		
		var maxTime = $("#maxTime").val();//最大发送次数
		var sendReceiveStatus = $("#sendReceiveStatus").val();//收发设置

		var content = $("#configuration").attr("value");//自定义配置
		var reg = new RegExp("(\n)","g"); 
		content = content.replace(reg,"#$");
		var reg1 = new RegExp("(\t)","g"); 
		content = content.replace(reg1,"#$");
		//var reg2 = new RegExp("(,)","g"); 
		//content = content.replace(reg2,"#$");
		//var reg3 = new RegExp("(，)","g"); 
		//content = content.replace(reg3,"#$");
		//var reg4 = new RegExp("(;)","g"); 
		//content = content.replace(reg4,"#$");
		//var reg5 = new RegExp("(；)","g"); 
		//content = content.replace(reg5,"#$");
		//var reg6 = new RegExp("( )","g"); 
		//content = content.replace(reg6,"#$");

		var strs = content.split("#$");
		var configuration  =  "";
		for(var i = 0;i<strs.length;i++){
			if(strs[i]!= ""&&strs[i]!= ''){
				configuration += strs[i] + "#$";
			}
		}
		configuration = configuration.substring(0,configuration.length-2);

	    $("#smsGatewaySettingFrom").submit();

	}
	
	</script>
</head>
<body onload="">
<div class="ui-layout-center">
<div class="opt-body">
	<div class="opt-btn">
		<a class="btn" href="#" onclick="save();"><span><span><s:text name="menuManager.save"></s:text></span></span></a>
		<a class="btn" href="#" onclick="window.parent.$.colorbox.close();"><span><span><s:text name="interfaceManager.close"></s:text></span></span></a>
	</div>
	<div id="opt-content" >
		<aa:zone name="smsGatewaySettingZone">
			<div id="message" style="display: none;"><s:actionmessage theme="mytheme" /></div>
			
			<form action="" name="smsGatewaySettingFrom" id="smsGatewaySettingFrom" method="post">
				
				<input  type="hidden" name="id" id="id" value="${id}"/>
				<input  type="hidden" name="flag" id="flag" value="oneself"/>
				<input type="hidden" name="gatewayName" id="gatewayName" value="${gatewayName}"/>
				<table class="form-table-without-border">
			
					<tr>
						<td class="content-title" style="width:150px;"><s:text name="messagePlatform.maxSendNumber"></s:text>：</td>
						<td> <input name="maxTime" id="maxTime" maxlength="30" value="${maxTime }"/> <span class="required">*</span></td>
						<td class="content-title" style="width: 130px;"><s:text name="interfaceManager.close"></s:text><s:text name="messagePlatform.shoufaSet"></s:text>：</td>
						<td> 
							<select name="sendReceiveStatus" id="sendReceiveStatus" >
							
								<s:iterator value="sendReceiveStatusList" var="var">
									<option value="<s:property value= '#var'/>" <s:if test="#var == smsGatewaySetting.sendReceiveStatus">selected="selected"</s:if>  >
										<s:if test="#var == @com.norteksoft.bs.sms.base.enumeration.SendReceiveStatus@ONLYSEND"><s:text name="interfaceManager.close"></s:text><s:text name="messagePlatform.maxSendNumber"></s:text></s:if>
										<s:elseif test="#var == @com.norteksoft.bs.sms.base.enumeration.SendReceiveStatus@OBLYRECEIVE"><s:text name="interfaceManager.close"></s:text><s:text name="messagePlatform.sendOnly"></s:text></s:elseif>
										<s:elseif test="#var == @com.norteksoft.bs.sms.base.enumeration.SendReceiveStatus@SENDANDRECEIVE"><s:text name="interfaceManager.close"></s:text><s:text name="messagePlatform.sendAndReceive"></s:text></s:elseif>
									</option>
								</s:iterator>
							
							</select>
						</td>
					</tr>
					
					<tr>
						<td colspan="4">
							<textarea name="configuration" cols="30" rows="12" id="configuration"   style="border: 1 solid #888888;LINE-HEIGHT:18px;padding: 3px;">${configuration }</textarea>
					 		<span class="required">*</span>
					 	</td>
					</tr>
					
					
					<tr>
					
					
						
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