<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/acs-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<%@ include file="/common/acs-iframe-meta.jsp"%>
	<title><s:text name="header.parameterSetting"/></title>
	<script type="text/javascript">

	function synOrg(){
		$("#synType").val(getSynType());
		$("#synLdapUserInfo").val(getSynUserInfo());
		ajaxSubmit("inputForm","${acsCtx}/syssetting/integration-sysOrg.htm","acs_content",showMsg);
	}
	
	function getSynType(){
		var synUserRadio = $("input[name='synUser']:checked");
		return $(synUserRadio).val();
	}

	function selectUserInfo(obj){
		var synType = getSynType();
		var isChecked = $(obj).attr("checked");
		if(isChecked&&synType=="synSelectUserInfo"){
			$("#userInfoTable").css("display","block");
		}else{
			$("#userInfoTable").css("display","none");
		}
	}
	
	function getSynUserInfo(){
		var userInfos = "";
		var checkedUserInfo = $("input[name='userInfo']:checked");
		for(var i=0;i<checkedUserInfo.length;i++){
			userInfos = userInfos+$(checkedUserInfo[i]).val()+",";
		}
		return userInfos;
	}
</script>
</head>
<body>
<div class="ui-layout-center" style="height: 1000px;">
	<div class="opt-body">
		<div class="opt-btn">
			<button class='btn' onclick="synOrg();"><span><span><s:text name="menu.ldap.syn"/></span></span></button>
		</div>
		<aa:zone name="acs_content">
		<div id="message"><s:actionmessage theme="mytheme"/></div>
		</aa:zone>
			<div id="opt-content">
			<form id="inputForm" name="inputForm" action="" method="post">
			<input  type="hidden" name="id" size="40" value="${id }" />
			<input  type="hidden" name="type" size="40" value="ldap" />
			<input  type="hidden" name="synType" id="synType"  />
			<input  type="hidden" name="synLdapUserInfo" id="synLdapUserInfo"  />
				<table class="form_table1">
					<tr>
					   <td ><input type="radio" id="synAllUser"  name="synUser"   value="synAllUser"  onclick="selectUserInfo(this);"  checked="checked" title="<s:text name="menu.ldap.synuser.all.title"/>"/><s:text name="menu.ldap.synuser.all"/></td>
					</tr>
					<tr>
						<td><input type="radio" id="onlySynAdd"  name="synUser" value="onlySynAdd"  onclick="selectUserInfo(this);"  title="<s:text name="menu.ldap.synuser.add.title"/>"/><s:text name="menu.ldap.synuser.add"/></td>
					</tr>
					 <tr>
					   <td ><input type="radio" id="synSelectUserInfo" name="synUser"  value="synSelectUserInfo"  onclick="selectUserInfo(this);"  title="<s:text name="menu.ldap.synuser.update.title"/>"/><s:text name="menu.ldap.synuser.update"/></td>
					 </tr>
				</table>
				<table class="form_table1" id="userInfoTable" style="display: none;">
					<tr>
						<td ><input type="checkbox" value="userName"  name="userInfo"/><s:text name="menu.ldap.synuser.update.name"/></td>
					</tr>
					<tr>
						<td ><input type="checkbox" value="mainDept"  name="userInfo" /><s:text name="menu.ldap.synuser.update.department"/></td>
					</tr>
					<tr>
						<td ><input type="checkbox" value="email"   name="userInfo"/><s:text name="menu.ldap.synuser.update.email"/></td>
					</tr>
					<tr>
						<td ><input type="checkbox" value="telephone"   name="userInfo"/><s:text name="menu.ldap.synuser.update.telephone"/></td>
					</tr>
				</table>
			</form>
			</div>
		
	</div>
</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>
