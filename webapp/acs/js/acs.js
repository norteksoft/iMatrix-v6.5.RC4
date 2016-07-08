function demonstrateOperate(){
		iMatrix.alert(iMatrixMessage["shielding.function.prompt"]);
	}

//是否开启单点登录设置时，四种方式中只能有一种是启用的状态
function loginInvocationCheck(type){
	var loginType = "";
	if(type=="ldap"){
		if($("#ldapInvocation1").attr("checked")){
			loginType = "ldap";
		}
	}else if(type=="rtx"){
		if($("#rtxInvocation1").attr("checked")){
			loginType = "rtx";
		}
	}else if(type=="other"){
		if($("#externalInvocation1").attr("checked")){
			loginType = "other";
		}
	}
	if(loginType!=""){
		$.ajax({
			   type: "POST",
			   url: webRoot+"/syssetting/validate-loginInvocation.htm",
			   data:{type:loginType},
			   success: function(data, textStatus) {
				   if(data=="success"){
					   var url = $("#inputForm").attr("action");
		    			ajaxSubmit("inputForm",url,"acs_content",saveCallback);
				   }else if(data=="ldap"){
					   iMatrix.confirm({message:iMatrixMessage['syn.ldap.info'],confirmCallback:saveWithLoginInvocationOk,cancelCallback:saveWithLoginInvocationCancel,parameters:{data:data,loginType:loginType}});
				   }else if(data=="rtx"){
					   iMatrix.confirm({message:iMatrixMessage['syn.rtx.info'],confirmCallback:saveWithLoginInvocationOk,cancelCallback:saveWithLoginInvocationCancel,parameters:{data:data,loginType:loginType}});
				   }else if(data=="other"){
					   iMatrix.confirm({message:iMatrixMessage['syn.other.info'],confirmCallback:saveWithLoginInvocationOk,cancelCallback:saveWithLoginInvocationCancel,parameters:{data:data,loginType:loginType}});
				   }
		      },
		      error : function(XMLHttpRequest, textStatus) {
					iMatrix.alert(textStatus);
				}
		  }); 
	}
}
function saveWithLoginInvocationOk(obj){
	saveWithLoginInvocation(obj.data);
}
function saveWithLoginInvocationCancel(obj){
	saveWithoutLoginInvocation(obj.loginType);
}
function saveWithoutLoginInvocation(type){
	if(type=="ldap"){
		 $("#ldapInvocation").attr("value","false");
	}else if(type=="rtx"){
		$("#rtxInvocation").attr("value","false");
	}else if(type=="other"){
		$("#externalInvocation").attr("value","false");
	}
	$("#oldInvocationType").attr("value","");
   var url = $("#inputForm").attr("action");
	ajaxSubmit("inputForm",url,"acs_content",saveCallback);
}
function saveWithLoginInvocation(oldInvocationType){
	$("#oldInvocationType").attr("value",oldInvocationType);
	var url = $("#inputForm").attr("action");
	ajaxSubmit("inputForm",url,"acs_content",saveCallback);
}