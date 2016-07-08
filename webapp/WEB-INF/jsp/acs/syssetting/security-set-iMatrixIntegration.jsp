<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/acs-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<%@ include file="/common/acs-iframe-meta.jsp"%>
	<title><s:text name="header.parameterSetting"/></title>
<script type="text/javascript">

	$().ready(function(){
		if("${serverConfig.synImatrixInvocation}"=="true"){
			$(".opt-btn").append("<button class='btn' onclick='synClick();' id='sysBtn'><span><span>"+iMatrixMessage["ldap.synchronousOrganizationalStructure"]+"</span></span></button>");
		}
		validateImatrix();
	});
	
	function validateImatrix(){
		$("#inputForm").validate({
			submitHandler: function() {
				if($("#synInvocation1").attr("checked")){
					$("#synInvocation").attr("value","true");
				}else{
					$("#synInvocation").attr("value","false");
				}
				if($("#sysOrgAllowable1").attr("checked")){
					$("#sysOrgAllowable").attr("value","true");
				}else{
					$("#sysOrgAllowable").attr("value","false");
				}
				var url = $("#inputForm").attr("action");
				ajaxSubmit("inputForm",url,"acs_content",saveCallback);
			},
			rules: {
		     },
			   messages: {
			}
		});
	}
	
	function saveCallback(){
		showMsg();
		validateImatrix();
	}
	
	function save(){
		$('#inputForm').attr("action","${acsCtx}/syssetting/integration-save.htm");
		$('#inputForm').submit();
	}
	
	function synInvocationClick(){
		$("#sysBtn").remove();
		if($("#synInvocation1").attr("checked")){
			$(".opt-btn").append("<button class='btn' onclick='synClick();' id='sysBtn'><span><span>"+iMatrixMessage["ldap.synchronousOrganizationalStructure"]+"</span></span></button>");
		}
	}
	
	function synClick(){
		$('#inputForm').attr("action","${acsCtx}/syssetting/integration-sysOrg.htm");
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
					<input  type="hidden" name="type" size="40" value="iMatrix" />
					<input  type="hidden" name="synImatrixInvocation" id="synInvocation" size="40" value="${serverConfig.synImatrixInvocation}" />
					<input  type="hidden" name="sysOrgAllowable" id="sysOrgAllowable" size="40" value="${serverConfig.sysOrgAllowable}" />
						<table class="form-table-without-border" style="width: auto;">
							<tr>
								<td style="300px;"><s:text name="common.operate"/><!--  操作-->：</td>
				                   <td><input type="checkbox" id="synInvocation1"  <s:if test="serverConfig.synImatrixInvocation">checked="checked"</s:if> onclick="synInvocationClick();"/><s:text name="ldap.synchronizationStructure"/><!-- 是否同步组织结构 -->
				                   <input type="checkbox" id="sysOrgAllowable1"  <s:if test="serverConfig.sysOrgAllowable">checked="checked"</s:if> /><s:text name="ldap.companyOrganizationalStructure"/><!--是否允许其他公司同步本公司组织结构 --></td>
							</tr>
							<tr>
								<td style="300px;"><s:text name="ldap.iMatrixServerAddr"/><!--  iMatrix服务地址-->：</td>
				                   <td><input  type="text" id="imatrixUrl" name="imatrixUrl"  value="${serverConfig.imatrixUrl}" style="width: 200px;"  maxlength="150"/></td>
							</tr>
							<tr>
								<td style="300px;"><s:text name="ldap.companeyCode"/><!--  公司编码-->：</td>
				                   <td><input  type="text" id="companyCode" name="companyCode"  value="${serverConfig.companyCode}" style="width: 200px;"  maxlength="50"/></td>
							</tr>
						</table>
				</form>
				<span style="color: red;"><s:text name="ldap.configurationOfTheSynchronous"/><!--  注：将该配置的iMatrix服务的组织结构同步到当前iMatrix服务的组织结构中--></span>
			</div>
		</aa:zone>
	</div>
</div>  	
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>