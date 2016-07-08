<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/acs-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<%@ include file="/common/acs-iframe-meta.jsp"%>
	<title><s:text name="header.parameterSetting"/></title>
<script type="text/javascript">

$().ready(function(){
	validateRtx();
});

function validateRtx(){
	$("#inputForm").validate({
		submitHandler: function() {
			if($("#rtxInvocation1").attr("checked")){
				$("#rtxInvocation").attr("value","true");
			}else{
				$("#rtxInvocation").attr("value","false");
			}
			if($("#rtxInvocation1").attr("checked")){
				loginInvocationCheck("rtx");
			}else{
				var url = $("#inputForm").attr("action");
				ajaxSubmit("inputForm",url,"acs_content",saveCallback);
			}
			
		},
		rules: {
	     },
		   messages: {
		}
	});
}

function saveCallback(){
	showMsg();
	validateRtx();
}

function save(){
	$('#inputForm').attr("action","${acsCtx}/syssetting/integration-save.htm");
	$('#inputForm').submit();
}

</script>
</head>
<body>
<div class="ui-layout-center" style="height: 1000px;">
	<div class="opt-body">
		<div class="opt-btn">
			<button class='btn' onclick="save();"><span><span><s:text name="common.save"/></span></span></button>
		</div>
		<aa:zone name="acs_content">
			<div id="message"><s:actionmessage theme="mytheme"/></div>
			<div id="opt-content">
			<form id="inputForm" name="inputForm" action="" method="post">
				<input  type="hidden" name="id" size="40" value="${serverConfig.id}" />
				<input  type="hidden" name="oldInvocationType" id="oldInvocationType"/>
				<input  type="hidden" name="type" size="40" value="rtx" />
				<input  type="hidden" name="rtxInvocation" id="rtxInvocation" size="40" value="${serverConfig.rtxInvocation}" />
				<table class="form_table1">
				<tr>
					<td style="300px"><s:text name="common.operate"/><!--  操作-->：</td>
					<td><input type="checkbox" id="rtxInvocation1"   <s:if test="serverConfig.rtxInvocation">checked="checked"</s:if>/><s:text name="ldap.openTheSingleSignOn"/><!-- 是否开启单点登陆 --></td>
				</tr>
				<tr>
					<td style="300px"><s:text name="ldap.rtxAddr"/><!-- rtx地址-->：</td>
					<td><input type="text" id="ldapRtxUrl" name="rtxUrl"  value="${serverConfig.rtxUrl}"  maxlength="150" style="width: 200px;" /> </td>
					<td><span style="color: red;">(192.168.1.54)</span></td>
				</tr>
				</table>
			</form>
		</div>
		</aa:zone>
	</div>
</div>  	
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>