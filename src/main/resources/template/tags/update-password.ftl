<html>
	<head>
		<title>密码修改</title>
		<meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
        <meta http-equiv="Cache-Control" content="no-store"/>
        <meta http-equiv="Pragma" content="no-cache"/>
        <meta http-equiv="Expires" content="0"/>
        
        <script src="${resourceCtx}/js/jquery-all-1.0.js" type="text/javascript"></script>
        <link   type="text/css" href="${resourceCtx}/css/${theme}/jquery-ui-1.8.16.custom.css" rel="stylesheet" id="_style"/>
		<script src="${resourceCtx}/js/public_${language}.js" type="text/javascript"></script>
		<script src="${resourceCtx}/js/public.js" type="text/javascript"></script>
		<script type="text/javascript">
			
			function checkLoginPassword(pass){// 规则
				$.ajax({
					type: "POST",
					url: "${base}/portal/check-login-password.action",
					data:{orgPassword:pass.value},
					success: function(msg, textStatus){
						if(msg!=""){
							iMatrix.alert(msg,"300px","180px");
							document.getElementById("password").value="";
						}
					},error : function(XMLHttpRequest, textStatus) {
						iMatrix.alert(textStatus,"300px","180px");
					}
				}); 
			}
			
			var counts = 0;
			function checkOldPassword(){
				if($("#password").val()==''){
		  			iMatrix.alert("${enterNewPassword}","300px","180px");
		  			return;
		        }
				if($("#password").val()!=$("#passwordConfirm").val()){
		  			iMatrix.alert("${passwordTwiceNotSame}","300px","180px");
		  			return;
		        }
				$.ajax({
					type: "POST",
					url: "${base}/portal/update-old-password.action",
					data:{oraginalPassword:$("#oldPassword").val(),password:$("#password").val(),id:$("#id").val()},
					success: function(msg, textStatus){
						if(msg!=""){
							counts = counts+1;
							if(msg == 'old_pwd_error'){
								iMatrix.alert({message:'${passwordError}',confirmCallback:passwordOutThree,parameters:counts,width:"300px",height:"180px"});
							}
							
						}else{
							iMatrix.alert({message:'${passwordUpdateSuccess}',confirmCallback:redirect,width:"300px",height:"180px"});
						}
					}, error : function(XMLHttpRequest, textStatus) {
						iMatrix.alert(textStatus,"300px","180px");
					}
				}); 
			}
			function passwordOutThree(counts){
				if(counts == 3){
					iMatrix.alert({message:"${passwordOutThree}",confirmCallback:redirect,width:"300px",height:"180px"});
				}
			}
			function redirect(){
				var url = document.getElementById("redirectUrl").value;
				if(url.indexOf("http")==0){
					window.location.href=url;
				}else{
					window.location.href="${base}" + url;
				}
			}
		</script>
		<style type="text/css">
			body{background-color: #f5f5f5;}
			#contentTable tr{margin: 5px 0;}
			#contentTable tr td{height: 30px;line-height: 30px;}
		</style>
	</head>
	
	<body>
	
		<form action="${base}/update-old-password.action" id="inputForm" name="inputForm" method="post">
			<input type="hidden" id="id" name="id" size="40" value="${id}" />
			<input type="hidden" id="redirectUrl" name="redirectUrl" size="40" value="${url}" />
			<table width="90%;" height="80%">
				<tr><td align="center" valign="middle">
					<table id="contentTable">
						<#if resetPassword?if_exists !="">
						<caption style="color:red;">${updatePasswordS}。</caption>
						<#else>
							<#if overdue?if_exists =="0">
								<caption style="color:red;">${passwordExpiredPleaseModifyPassword}</caption>
							<#else>
								<caption style="color:red;">${passwordInfo} ${overdue}${passwordOverdueUpdate}。</caption>
							</#if>
						</#if>
						<tr><td>${userName}：</td> <td>${name }</td></tr>
						<tr><td>${originalPassword}：</td> <td><input type="password" id="oldPassword" name="oldPassword"/></td></tr>
						<tr>
						<td>${newPassword}：</td> <td><input type="password" id ="password" name="password" onblur="checkLoginPassword(this)"/></td>
						</tr>
						<tr><td>${repeatPassword}：</td> <td><input type="password" name="passwordConfirm" id="passwordConfirm"/></td></tr>
						  
						      	<tr align="center">
						   <td colspan="2" align="center">
						   <#if resetPassword?if_exists =="" && overdue?if_exists !="0">
							<input type="button" value="${skip}" onclick="redirect();"/>&nbsp;
							</#if>
						   
						   <input type="reset" value="${clear}" />&nbsp;
						   <input type="button" value="${ftlSubmit}" onclick="checkOldPassword()"/>
						  </td>
						</tr>
					</table>
				</td></tr>
			</table>
		</form>
		
	</body>
</html>
