<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/setting-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<%@ include file="/common/setting-iframe-meta.jsp"%>
	<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
	
	
	<title>收发接口设置新建</title>
	<script type="text/javascript">
	$(document).ready(function(){
		validateSave();
	});
	
	function validateSave(){
		$("#smsSettingForm").validate({
			submitHandler: function() {
				ajaxSubmit("smsSettingForm",webRoot+"/sms/sms-authority-setting-save.htm", "smsSettingZone",saveCallback);
			},
			rules: {
				interCode: "required"
			},
			messages: {
				interCode: iMatrixMessage["menuManager.required"]
			}
		});
	}
	
	function saveCallback(){
		showMsg();
		validateSave();
	}
	//保存	
	function submitSetting(){

		var requestType = $('#requestType').val();
		var backUrl = $('#backUrl').val();
		var flag1 = (requestType != null && requestType != '') && (backUrl != null && backUrl != '');
		var flag2 = (requestType == null || requestType == '') && (backUrl == null || backUrl == '');
		if(flag1 || flag2){
			$.ajax({
				   type: "POST",
				   url: webRoot+"/sms/sms-authority-setting-validateCode.htm",//验证唯一性
				   data: "interCode="+$('#interCode').val()+"&id="+$('#id').val(),
				   success: function(msg){
					   if(msg == "true"){
						   iMatrix.alert(iMatrixMessage["basicSetting.optionCodeExist"]);
					   }else if(msg == "false"){
						   $("#smsSettingForm").submit();
					   }
				   }
			});
		}else{
			iMatrix.alert(iMatrixMessage["messagePlatform.chooseRequestValidate"]);
			return;
		}
		

	}
	//选择收发类型，接收时
	function changeType(){
		var type = $('#type').val();
		if(type == 'RECEIVE'){
			$('#templateCodeTr').hide();
		}else{
			$('#templateCodeTr').show();
		}
	}
	//选择模版
	function selectTemplate(){
		var url = "${settingCtx}/sms/sms-authority-setting-selectTemplate.htm";
		init_colorbox(url,iMatrixMessage["messagePlatform.chooseTemplate"],450,300,false,refreshListData);
	}
	//回调
	function refreshListData(){
		jQuery("#page").trigger("reloadGrid");
	}
	</script>
</head>
<body >
<div class="ui-layout-center">
<div class="opt-body">
	<div class="opt-btn">
		<a class="btn" href="#" onclick="submitSetting();"><span><span><s:text name="menuManager.save"></s:text></span></span></a>
		<a class="btn" href="#" onclick="window.parent.$.colorbox.close();"><span><span><s:text name="interfaceManager.close"></s:text></span></span></a>
	</div>
	<div id="opt-content" >
		<aa:zone name="smsSettingZone">
			<div id="message" style="display: none;"><s:actionmessage theme="mytheme" /></div>
			
			<form action="" name="smsSettingForm" id="smsSettingForm" method="post">
				
				<input  type="hidden" name="id" id="id" value="${id}"/>
				<input  type="hidden" name="systemId" id="systemId" value="${systemId}"/>
				
				<table class="form-table-without-border">
				
					<tr>
						<td class="content-title" style="width: 130px;"><s:text name="messagePlatform.interfaceCode"></s:text>：</td>
						<td> 
							<input name="interCode" style="width: 130px;" id="interCode" maxlength="500" value="${interCode }"/> 
							<span class="required">*</span>
							
						</td>
					</tr>
					
					<tr>
						<td class="content-title"><s:text name="messagePlatform.type"></s:text>：</td>
						<td> 
							<select name="type" id="type"  style="width: 130px;" onchange="changeType();">
								<s:iterator value="@com.norteksoft.bs.sms.base.enumeration.ReceiveDispatchType@values()" var="typeVar">
									<option <s:if test="#typeVar==type">selected="selected"</s:if> value="${typeVar}"><s:text name="%{code}"></s:text></option>
								</s:iterator>
							</select>
						</td>
					</tr>
					<tr id="templateCodeTr">
						<td class="content-title" style="width: 130px;"><s:text name="messagePlatform.templateCode"></s:text>：</td>
						<td> 
							<input name="templateCode"  id="templateCode" maxlength="500" value="${templateCode }" readonly="readonly" /> 
							<a class="btn" href="#" onclick="selectTemplate();"><span><span><s:text name="messagePlatform.chooseTemplate"></s:text></span></span></a>
						</td>
					</tr>	
					
					<tr>
						<td class="content-title" style="width: 130px;"><s:text name="messagePlatform.requestType"></s:text>：</td>
						<td> 
							<select name="requestType" id="requestType" >
									<option value="" <s:if test="id == null">selected="selected"</s:if>  ><s:text name="basicSetting.choose"></s:text></option>
								
								<s:iterator value="@com.norteksoft.bs.sms.base.enumeration.RequestType@values()" var="var">
									<option value="<s:property value= '#var'/>" <s:if test="#var == smsAuthoritySetting.requestType">selected="selected"</s:if>  >
										<s:if test="#var == @com.norteksoft.bs.sms.base.enumeration.RequestType@HTTP"><s:text name="messagePlatform.httpRequest"></s:text></s:if>
										<s:elseif test="#var == @com.norteksoft.bs.sms.base.enumeration.RequestType@WEBSERVICE"><s:text name="messagePlatform.webserviceRequest"></s:text></s:elseif>
										<s:elseif test="#var == @com.norteksoft.bs.sms.base.enumeration.RequestType@RESTFUL"><s:text name="messagePlatform.restfulRequest"></s:text></s:elseif>
									</option>
								</s:iterator>
							
							</select>
							
						</td>
					</tr>	
					
					
					<tr>
						<td class="content-title" style="width: 130px;"><s:text name="messagePlatform.describe"></s:text>：</td>
						<td> 
							<input name="description" style="width: 200px;" id="description" maxlength="500" value="${description }"/> 
						</td>
					</tr>	
					
					<tr>
						<td class="content-title" style="width: 130px;"><s:text name="messagePlatform.callBackUrl"></s:text>：</td>
						<td> 
							<input name="backUrl" style="width: 200px;" id="backUrl" maxlength="250" value="${backUrl }"/> 
							
						</td>
					</tr>	
					
			
					
				</table>
			</form>
			<script type="text/javascript">
				$(document).ready(function(){
					changeType();
				});
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