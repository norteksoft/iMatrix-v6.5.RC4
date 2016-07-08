<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/acs-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<%@ include file="/common/acs-iframe-meta.jsp"%>
	<title><s:text name="header.parameterSetting"/></title>
<script type="text/javascript">

$().ready(function(){
	validateOther();
});

function validateOther(){
	$("#inputForm").validate({
		submitHandler: function() {
			if($("#externalInvocation1").attr("checked")){
				$("#externalInvocation").attr("value","true");
			}else{
				$("#externalInvocation").attr("value","false");
			}
			if($("#externalInvocation1").attr("checked")){
				loginInvocationCheck("other");
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
	validateOther();
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
					<input  type="hidden" name="type" size="40" value="other" />
					<input type="hidden" id="externalInvocation" name="extern" value="${serverConfig.extern}"/>
					<table class="form_table1">
						<tr>
							<td style="300px"><s:text name="common.operate"/><!--  操作-->：</td>
							<td><input type="checkbox" id="externalInvocation1"   <s:if test="serverConfig.extern">checked="checked"</s:if>/><s:text name="ldap.openTheSingleSignOn"/><!-- 是否开启单点登陆 --></td>
						</tr>
						<tr>
							<td style="300px"><s:text name="ldap.way"/><!--  方式-->：</td>
							<td><select name="externalType">
								        <option value="HTTP" <s:if test="serverConfig.externalType==@com.norteksoft.acs.entity.sysSetting.ExternalType@HTTP"> selected="selected" </s:if> >http</option>
								        <option value="RESTFUL" <s:if test="serverConfig.externalType==@com.norteksoft.acs.entity.sysSetting.ExternalType@RESTFUL"> selected="selected" </s:if> >RESTful</option>
								        <option value="WEBSERVICE" <s:if test="serverConfig.externalType==@com.norteksoft.acs.entity.sysSetting.ExternalType@WEBSERVICE"> selected="selected" </s:if> >webservice</option>
								      </select></td>
						</tr>
						<tr>
							<td style="300px"><s:text name="ldap.address"/><!--  地址-->：</td>
							<td><input  type="text" id="externalUrl" name="externalUrl"  value="${serverConfig.externalUrl}" style="width: 400px;"  maxlength="150"/></td>
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