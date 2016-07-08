<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/setting-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<%@ include file="/common/setting-iframe-meta.jsp"%>
	<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
	
	
	<title>选择短信模版</title>
	<script type="text/javascript">

		function submitSetting(){
			var formRadio=$("input[name='checkvalue']:checked");
			if(formRadio.length>0){
				var value = $(formRadio).val();
				parent.$("#templateCode").attr("value",value);
				parent.$.colorbox.close();
			}else{
				iMatrix.alert(iMatrixMessage["messagePlatform.chooseTemplate"]);
			}
			
		}
	</script>
</head>
<body onload="">
<div class="ui-layout-center">
<div class="opt-body">
	<div class="opt-btn">
		<a class="btn" href="#" onclick="submitSetting();"><span><span><s:text name="menuManager.confirm"></s:text></span></span></a>
		<a class="btn" href="#" onclick="window.parent.$.colorbox.close();"><span><span><s:text name="interfaceManager.close"></s:text></span></span></a>
	</div>
	<div id="opt-content" >
		<aa:zone name="smsSettingZone">
			<div id="message" style="display: none;"><s:actionmessage theme="mytheme" /></div>
			
			<form action="" name="smsSettingForm" id="smsSettingForm" method="post">

				<table class="leadTable" border=1  style="width: 420px;">
				
					<s:iterator value="smsTemplateSettings" var="var">
						<tr>
							<td class="content-title" style="width: 50px;">
								<input id="checkvalue" name="checkvalue" type="radio" value="<s:property value= '#var.templateCode'/>"/>
							</td>
							<td style="width: 50px;"> 
								<s:property value= '#var.templateCode'/>
							</td>
							<td style="width: 280px;"> 
								<s:property value= '#var.templateName'/>
							</td>
						</tr>	
					</s:iterator>
					
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