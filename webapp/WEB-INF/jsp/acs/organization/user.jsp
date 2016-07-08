<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/acs-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<%@ include file="/common/acs-iframe-meta.jsp"%>
	<title><s:text name="common.userManager"/></title>
	<script  type="text/javascript" src="${imatrixCtx}/widgets/calendar/WdatePicker.js"></script>
	
	<script type="text/javascript">
		//新建
		function addUser(id, url_, opt) {
			$("#ajaxId").attr("value","");
			checkUserRegister(id, url_, opt);
		}

		function checkUserRegister(id, url_, opt) {
			$.ajax( {
				data : {
					weburl : url_
				},
				type : "post",
				url : "${acsCtx}/organization/user-checkUserRegister.action",
				beforeSend : function(XMLHttpRequest) {
				},
				success : function(data, textStatus) {
					if (data == '1') {
						iMatrix.alert('<s:text name="user.userAlert"/>');
					} else {
						var deId = $("#departId").val();
						var oldType = $("#oType").val();
						if(oldType!='USERSBYDEPARTMENT'&&oldType!='USERSBYBRANCH'){
							$("#departId").attr("value",'');
						}
						$("#ajax_from").attr("action", data);
						ajaxAnywhere.formName = "ajax_from";
						ajaxAnywhere.getZonesToReload = function() {
							return "acs_content";
						};
						ajaxAnywhere.onAfterResponseProcessing = function() {
							HideSearchBox();
							var deId = $("#departId").val();
							var oldType = $("#oType").val();
							var bId = $('#brcheId').val();

							if(bId==''){
								bId = $("#fromBracnhId").val();
							}
							
							$("#oneDid").attr("value",deId);
							$("#oldDid").attr("value",deId);
							$("#oldType").attr("value",oldType);
							$("#bId").attr("value",bId);
							ruleInput();
							getContentHeight();
						};
						ajaxAnywhere.submitAJAX();
					}
				},
				error : function(XMLHttpRequest, textStatus) {
					iMatrix.alert(textStatus);
				}
			});
		}
		
		//名称是否不包含下等号(=)、竖线(|)、加号(+)、波浪线(~)、前后不能包含空格,包括时返回false,不含时返回true
		function validateName(name){
			if(name.indexOf("=")>=0||name.indexOf("|")>=0
			    ||name.indexOf("+")>=0||name.indexOf("~")>=0
			    ||name.indexOf("@")>=0||name.indexOf("#")>=0
			    ||name.indexOf("$")>=0||name.indexOf("%")>=0
				||name.indexOf("^")>=0||name.indexOf("&")>=0
				||name.indexOf("!")>=0||name.indexOf("~")>=0
				||name.indexOf("(")>=0||name.indexOf(")")>=0
				||name.indexOf("<")>=0||name.indexOf(">")>=0
				||name.indexOf("?")>=0||name.indexOf("/")>=0
				||name.indexOf("*")>=0||name.indexOf(" ")==0
				||(name.length>0&&name.lastIndexOf(" ")==name.length-1)){
				
				return false;
			}
			return true;
		}
		function isAllNumber(name){
			 re = new RegExp("[0-9]","g");
			 var j=0;
			 for(var i=0;i<name.length;i++){
                 var unit = name.charAt(i);
                 if(unit=='-'||unit=='_')j++;
                 if(unit.match(re)!=null){
                     j++;
                 }
		     }
		     if(name.length==j){
			     return true;
		     }else{
                 return false;
			 }
		}
		function ruleInput(){
    		$("#inputForm").validate({
    			submitHandler: function() {
    			//先登录名验证
    			var mainDepartmentId = $("#departSubCompanyId").val();
    			var bId = $('#brcheId').val();
    			var fromBracnhId="${fromBracnhId}";
    			var validateLoginName = $("#loginName").val();
    			var userName = $("#trueName1").val();
    			$.ajax( {
    				data : {
    					id:$("#id").attr("value"),dscId:mainDepartmentId,validateLoginName:validateLoginName,fromBracnhId:fromBracnhId,branId:bId
    				},
    				type : "post",
    				url : "${acsCtx}/organization/user-validateEamil.action",
    				beforeSend : function(XMLHttpRequest) {
    				},
    				success : function(data, textStatus) {
    					if(data=="loginNameFlase"){
    						 iMatrix.alert("<s:text name='user.usernameRepat'/>");
        			    }else{
    						if(!validateName($("#loginName").val())){
    			    			$("#loginName").parent().append('<label  class="error"><s:text name="user.loginRequired"/></label>');
    		    			}else  if(iMatrix.validateValueTrim(userName)){
    			    			$("#trueName1").parent().append('<label  class="error"><s:text name="user.realNameRequired"/></label>');//真实姓名必填,真实姓名的前后不能包含空格
    		    			}else if($("#mailboxDeploy").val()==''){
    							//请选择邮箱配置
    							iMatrix.alert("<s:text name='user.selectEmailConfig'/>！");
    					    }else{
        					    if(data=="loginNameMessage"){
            					  //你的登录名在其他分支机构也存在，确定保存吗
            					    iMatrix.confirm({
            					    	message:"<s:text name='user.loginNameHasExist'/>？",
            					    	confirmCallback:ruleInputOk
            					    });
        					    }else{
        					    	 saveUser(); 
            					}
    		    			}
    					}
    				},
    				error : function(XMLHttpRequest, textStatus) {
    					iMatrix.alert(textStatus);
    				}
    			});

        			
	    			
    			},
				rules: {
	    			passwordConfirm: {
	    				required: true,
	    				equalTo: "#password"
	    			},
	    			high: {
	    				number: true
	    			},
	    			weight: {
	    				number: true
	    			},
	    			IDcard: {
	    				creditcard: true
	    			},
	    			homePostCode: {
	    				digits: true
	    			},
	    			matePostCode: {
	    				digits: true
	    			},
	    			FMPostCode: {
	    				digits: true
	    			}
			     },
				   messages: {
			    	 'user.loginName':"<s:text name='common.required'/>",//必填
			    	 'user.name':"<s:text name='common.required'/>",//必填
			    	'user.password':"<s:text name='common.required'/>",//必填
			    	passwordConfirm:{
			    	 	required:"<s:text name='common.required'/>",//必填
			    	 	equalTo:"<s:text name='user.passwordConfirmCheckRepeat'/>"//密码不一致
			     	},
			    	'user.email':{
			    	 	required:"<s:text name='common.required'/>",//必填
			    	 	email : "<s:text name='user.emailCheck'/>"//请输入正确的邮件地址
				    },
					'user.mailSize':{
				    	required:"<s:text name='common.required'/>",//必填
				    	number : "<s:text name='user.mailSize'/>"//请输入8位以下的数字
					},
					'IDcard':{
						creditcard:"<s:text name='user.idCardCheck'/>"//请输入合法的身份证号码
					},
					'homePostCode':{
						digits:"<s:text name='user.highCheckNumber'/>"//请只输入数字
					},
					'matePostCode':{
						digits:"<s:text name='user.highCheckNumber'/>"//请只输入数字
					},
					'FMPostCode':{
						digits:"<s:text name='user.highCheckNumber'/>"//请只输入数字
					}
				}
			});
		}
		
		function ruleInputOk(){
			saveUser(); 
		}
		//修改页面提交方法
		function submitForm() {
			$('#inputForm').submit();
		}

		function saveUser(){
			var departmentIds=$("#dids").attr("value");
			var departmentId=$("#deId").attr("value");
		    var i=$("#departId").val();
		    var t=$("#oType").val();
		    //如果正职部门的名称是空，表示没有选择正职部门，则设置正职部门id控件为空
		    var mainDeptName = $("#mainDepartmentName").val();
		    if(mainDeptName==""||typeof(mainDeptName)=='undefined'){
		    	$("#oneDid").attr("value","");
		    }
		    $("#oldDid").attr("value",i);
		    $("#oldType").attr("value",t);
			ajaxAnywhere.formName = "inputForm";
			ajaxAnywhere.getZonesToReload = function() {
				return "acs_content";
			};
			ajaxAnywhere.onAfterResponseProcessing = function() {
				ruleInput();
			    $('#message').show();
			    setTimeout("$('#message').hide()",3000);
			    getContentHeight();
			};
			ajaxAnywhere.submitAJAX(); 
		}

		//取消
		function cancel(){
		    var i=$("#departId").val();
		    var t=$("#oType").val();
		    var b=$('#brcheId').val(); 
		    $("#departmId").attr("value",i);
		    $("#departmType").attr("value",t);
		    $("#branchId").attr("value",b);
		    if(t=='DELETED_USER'){
			    $('#cancelForm').attr('action', '${acsCtx}/organization/user-deleteList.action');
			}
			if(t=='USERSBYDEPARTMENT'){
				var url=$('#cancelForm').attr('action');
				url+="?oldType="+t;
				$('#cancelForm').attr('action', url);
			}
			ajaxAnywhere.formName = "cancelForm";
			ajaxAnywhere.getZonesToReload = function() {
				return "acs_content";
			};
			ajaxAnywhere.onAfterResponseProcessing = function() {
				initButtonGroup();
				$('input').attr('disabled', '');
				$('select').attr('disabled', '');
				//initUserTable();
			};
			ajaxAnywhere.submitAJAX(); 
		}

		//保存用户状态
		function saveUserState(){
			setPageState();

			 var i=$("#departId").val();
		    var t=$("#oType").val();
		    var b=$('#brcheId').val(); 
		    $("#departmId").attr("value",i);
		    $("#departmType").attr("value",t);
		    $("#branchId").attr("value",b);
		    $("#oldType").attr("value",t);
			
			var webroot="${acsCtx}";
			var enable = $("input[name='_states_enable']:checked").val();
			var accountUnlock = $("#accountUnlock").attr("checked");
			var result = "";
			if(accountUnlock==true){
				result="accountUnLock";
			}else{
				result="accountLock";
			}
			result=result+","+enable;
			$("#states").attr("value",result);
			
			 var url=webroot+"/organization/user-saveUserState.action";		 
			$("#inputForm").attr("action",url);
			ajaxAnywhere.formName = "inputForm";
			ajaxAnywhere.getZonesToReload = function() {
				return "acs_content";
			};
			ajaxAnywhere.onAfterResponseProcessing = function() {
				initButtonGroup();
			};
			ajaxAnywhere.submitAJAX(); 
		}

		function cancelUserState(){
			setPageState();
			var i=$("#departId").val();
		    var t=$("#oType").val();
		    var b=$('#brcheId').val(); 
		    $("#departmId").attr("value",i);
		    $("#departmType").attr("value",t);
		    $("#branchId").attr("value",b);
		    $("#oldType").attr("value",t);
		    
		    var url="${acsCtx}/organization/user.action";		 
			$("#inputForm").attr("action",url);
			ajaxAnywhere.formName = "inputForm";
			ajaxAnywhere.getZonesToReload = function() {
				return "acs_content";
			};
			ajaxAnywhere.onAfterResponseProcessing = function() {
				initButtonGroup();
			};
			ajaxAnywhere.submitAJAX(); 
		}

		//ajax提交方法
		//修改
		function opt(url_, opt, id,id2) {
			var uIds = jQuery("#main_table").getGridParam('selarrrow');
			var bId = $('#brcheId').val();
			if(uIds==''){
				//请先选择
				iMatrix.alert("<s:text name='user.pleaseSelect'/>");
			}else if(uIds.length > 1){
				 //只能选择一条
				 iMatrix.alert("<s:text name='user.pleaseSelectOne'/>！");
			}else{
				if(opt!="LOOK"){
					$("#ajaxId").val(uIds);
					$("#edit").val(opt);
					$("#look").val(opt);
				}else{
					$("#ajaxId").val(id);
					$("#look").val(opt);
				}
				if(id!=''){
					$("#ajax_from").attr("action", url_+'?did='+id);
				}else{
					$("#ajax_from").attr("action", url_);
				}
				ajaxAnywhere.formName = "ajax_from";
				ajaxAnywhere.getZonesToReload = function() {
					return "acs_content";
				};
				ajaxAnywhere.onAfterResponseProcessing = function() {
					HideSearchBox();
					$("#look").attr("value","");
					if(opt=="LOOK"){
						$('input').attr('disabled', 'disabled');
						$('select').attr('disabled', 'disabled');
					}else{
						var bId = $('#brcheId').val();
						$("#bId").attr("value",bId);
						ruleInput();
					}
					getContentHeight();
				};
				ajaxAnywhere.submitAJAX();
			}
		}

		//删除用户
		function opt_delete(url_, opt) {
			var uIds = jQuery("#main_table").getGridParam('selarrrow');
			if(uIds==''){
				iMatrix.alert('<s:text name="common.selectOne"/>');
				return;
			}else{
				//确认移除吗
				iMatrix.confirm({
					message:"<s:text name='user.sureRemove'/>？",
					confirmCallback:opt_deleteOk,
					parameters:uIds
				});
			}
		} 
		function opt_deleteOk(uIds){
			setPageState();
			ajaxSubmit("ajax_from",webRoot+"/organization/user-falseDelete.action?ids="+uIds+"&departmType="+$("#oType").val(),"acs_list");
		}
		function initButtonGroup(){
			initUpdateBtnGroup();
			initExportBtnGroup();
			initCopy();
		}


		//导入
		function importUser(){
			$.colorbox({href:'${acsCtx}/organization/user-showImportUser.action',
				iframe:true, innerWidth:400, innerHeight:200,overlayClose:false,title:"<s:text name='user.importUser'/>"});//导入用户
		} 

		//导出
		function exportUser(){
			$("#ajax_from").attr("action","${acsCtx}/organization/user-exportUser.action");
			$("#ajax_from").submit();
		}  

		//选择
		function Dtree2(treeStyle){
			if(treeStyle=='multiple'){
				$.colorbox({href:'${acsCtx}/organization/user-chooseDepartments.action?type=old',iframe:true, innerWidth:600, innerHeight:500,overlayClose:false,title:"<s:text name='role.selectDepartment'/>"});//请选择部门
			}else{
				$.colorbox({href:'${acsCtx}/organization/user-chooseOneDepartment.action?type=old',iframe:true, innerWidth:600, innerHeight:500,overlayClose:false,title:"<s:text name='role.selectDepartment'/>"});//请选择部门
			}
		}

		function checkLoginPassword(pass) {
			$.ajax({
				   type: "POST",
				   url: "user-checkLoginPassword.action",
				   data:{orgPassword:pass.value},
				   success: function(msg, textStatus){
					   if(msg!=""){
						   iMatrix.alert(msg);
						   $("#password").val("");
						   $("#password").blur();
					   }
			      },
					error : function(XMLHttpRequest, textStatus) {
						iMatrix.alert(textStatus);
					}
			  }); 
		}

		/**
		 *修改密码
		 *liudongxia
		 */
	  	function modifyPassWord(id,url_) {
		  	if(versionType=="online"){
		  		demonstrateOperate();
		  	}else{
		  		$.colorbox({href:url_+'?id='+id,iframe:true, innerWidth:640, innerHeight:200,overlayClose:false,title:"<s:text name='userInfo.updatePassword'/>"});//修改密码
		  	}
	  		//$.colorbox({href:url_+'?id='+id,iframe:true, innerWidth:500, innerHeight:160,overlayClose:false,title:"修改密码"});
	  	}

	  	/**
		 *密码弹框”确定“按钮,设置密码
		 *liudongxia
		 */
	  	function setPassWord(password) {
	  		$("#password").attr("value",password);
	  		$("#passWordChange").attr("value","yes");
	  	}
	  	
	  	function shiftCheckbox(obj) {
			//checkset = document.getElementsByName("states");
			//if(indexs==0){
			//	checkset[1].checked = false;
			//}else{
			//	checkset[0].checked = false;
			//}
	  	  var objValue = $(obj).attr("checked");
          if(objValue==true){
       	   $(obj).attr('value','invocation');
          }else{
       	   $(obj).attr('value','enabled');
          }
		}
		function shiftCheckboxThree(obj){
           var objValue = $(obj).attr("checked");
           if(objValue==true){
        	   $(obj).attr('value','accountNonExpired');
           }else{
        	   $(obj).attr('value','accountNonExpiredNotChecked');
           }
		}

		//查看用户表
		function viewUser(ts1,cellval,opts,rwdat,_act){
			var v="<a  href=\"#\" hidefocus=\"true\" onclick=\"_click_fun("+opts.id+");\">" + ts1 + "</a>";
			return v;
		}
		
		function _click_fun(id){
			$("#ajaxId").attr("value",id);
			$("#look").attr("value","LOOK");
			ajaxSubmit("ajax_from", webRoot+'/organization/user-inputLook.action', "acs_content",_click_fun_callback);
		}

		function _click_fun_callback(){
			getContentHeight();
			initButtonGroup();
		}


		function clearInput(inputId){
			$("#"+inputId).attr("value","");
		}

		function unlockUser(){
			var uIds = jQuery("#main_table").getGridParam('selarrrow');
			if(uIds==''){
				iMatrix.alert('<s:text name="common.selectOne"/>');
				return;
			}else{
				//确认解锁吗
				iMatrix.confirm({
					message:"<s:text name='user.confirmUnlock'/>？",
					confirmCallback:unlockUserOk,
					parameters:uIds
				});
			}
		}
		function unlockUserOk(uIds){
			$.ajax({
				   type: "POST",
				   url: "user-unlockUser.action?ids="+uIds,
				   success: function(data, textStatus){
							jQuery("#main_table").jqGrid().trigger("reloadGrid"); 
			                iMatrix.alert(data);
			                return;
				},error : function(XMLHttpRequest, textStatus) {
						iMatrix.alert(textStatus);
				}
			});
		}
		function unlockData(){
			var uIds = jQuery("#main_table").getGridParam('selarrrow');
			if(uIds==''){
				iMatrix.alert('<s:text name="common.selectOne"/>');
				return;
			}else{
				//确认解锁吗
				iMatrix.confirm({
					message:"<s:text name='user.confirmUnlock'/>？",
					confirmCallback:unlockDataOk,
					parameters:uIds
				});
			}
		}
		function unlockDataOk(uIds){
			$.ajax({
				   type: "POST",
				   url: webRoot+"/organization/user-unlockData.htm?ids="+uIds,
				   success: function(data, textStatus){
			                //数据解锁成功
			                iMatrix.alert("<s:text name='user.unlockDatasuccess'/>");
			                return;
				},error : function(XMLHttpRequest, textStatus) {
						iMatrix.alert(textStatus);
				}
			});
		}
		//显示提示信息，3秒后隐藏
		function showMsg(id,time){
			if(id==undefined)id="message";
			$("#"+id).show();
			if(time==undefined)time=3000;
			setTimeout('$("#'+id+'").hide();',time);
		}	
        //批量更换主职部门
		function changeMainDepartment(){
			 var uIds = jQuery("#main_table").getGridParam('selarrrow');
				if(uIds.length==0){
					 $("#notice").html("<span style='color: red;'><s:text name='user.seleteUser'/>！</span>");//请选择用户
					 $("#notice").children("span").fadeOut(5000);
					 return;
				}else{
					$.colorbox({href:'${acsCtx}/organization/user-toDepartmentToUsersDel.action?ids='+uIds+'&fromChangeMainDepartment=true',iframe:true, innerWidth:600, innerHeight:500,overlayClose:false,title:"<s:text name='department.selectDepartment'/>"});//请选择部门
				}
		}
		function changeBatchUserMainDepartment(deptId){
			var uIds = jQuery("#main_table").getGridParam('selarrrow');
			if(uIds==''){
				 //请先选择
				 iMatrix.alert("<s:text name='user.pleaseSelect'/>");
			}else{
				var userIds = "";
				for(var i=0;i<uIds.length;i++){
                   userIds+=uIds[i]+",";
				}
				 userIds=userIds.substring(0,userIds.length-1);
				 var departmentId = $("#departId").val();
			     $("#formName").attr("action","${acsCtx}/organization/user-batchChangeUserMainDepartment.action?ids="+userIds+"&newMainDepartmentId="+deptId+"&departmentId="+departmentId);
		         ajaxAnywhere.formName = "formName";
				 ajaxAnywhere.getZonesToReload = function() {
					return "acs_content";
				 };
				 ajaxAnywhere.onAfterResponseProcessing = function () {
					 initButtonGroup();
				 };
				 ajaxAnywhere.submitAJAX();
			}
		}
		//提交为工作组添加的用户
		function workgroupAddUserSubmit(){
			var lists =allUsers("user_tree") ;
			if(lists.length <= 0){
				iMatrix.alert('<s:text name="user.seleteUser"/>');
				return;
			}
			var hasEffectiveUser = false;
			for(var i=0; i<lists.length; i++){
				    var type=lists[i].substring(0,lists[i].indexOf("_"));
				  if(type=="user"){  
					var parentLi = lists[i].substring(lists[i].indexOf("~")+1,lists[i].length);
					var parentDeptId = $('#tree_selected_id').attr('value');
					if(parentLi == parentDeptId){
						continue;
					}
					var userId= lists[i].substring(lists[i].indexOf("_")+1,lists[i].indexOf("="));
					if(userId.length > 0){
						var inpt = document.createElement("input");
						inpt.setAttribute("name", "userIds");
						inpt.setAttribute("value", userId);
						inpt.setAttribute("type", "hidden");
						document.getElementById("workgroupAddUserForm").appendChild(inpt);
						hasEffectiveUser = true;
					}
				  }
			}
			if(hasEffectiveUser){
				$('#workgroupAddUserForm').submit();
			}else {
				//所选用户已在该部门
				iMatrix.alert('<s:text name="user.selectedUserHasBeen"/>');
			}
		}

		
		//验证用户邮箱唯一
		/*
		function validateEmail(){
			$.ajax( {
				data : {
					userEmail : $("#email").attr("value"),id:$("#id").attr("value")
				},
				type : "post",
				url : "${acsCtx}/organization/user-validateEamil.action",
				beforeSend : function(XMLHttpRequest) {
				},
				success : function(data, textStatus) {
					if(data=="false"){
						alert("此邮箱地址已被注册！");
					}
				},
				error : function(XMLHttpRequest, textStatus) {
					alert(textStatus);
				}
			});
		}
		*/
		function clearUser(){
			var uIds = jQuery("#main_table").getGridParam('selarrrow');
			if(uIds==''){
				iMatrix.alert('<s:text name="common.selectOne"/>');
				return;
			}else{
				iMatrix.confirm({
					message:"<s:text name='user.sureDelete'/>？",
					confirmCallback:clearUserOk,
					parameters:uIds
				});
			}
		}
		function clearUserOk(uIds){
			$.ajax({
				   type: "POST",
				   url: "user-checkIsAdmin.action?ids="+uIds,
				   success: function(data, textStatus){
					   if( data == "yes" ){
			                iMatrix.alert('<s:text name="common.delete.info"/>');
			                return;
						}else{
							setPageState();
							ajaxSubmit("ajax_from",webRoot+"/organization/user-clearUser.action?ids="+uIds+"&departmType="+$("#oType").val()+"&branchId="+$("#brcheId").val(),"acs_list",clearUserBack);
						}
				},error : function(XMLHttpRequest, textStatus) {
						iMatrix.alert(textStatus);
				}
			});
		}
		function clearUserBack(){
			jQuery("#main_table").jqGrid().trigger("reloadGrid"); 
		}
		function userTree(){
			var ids="";
			var uIds = jQuery("#main_table").getGridParam('selarrrow');
			if(uIds.length==0){
				//请选择人员
				iMatrix.alert("<s:text name='user.selectPerson'/>!");
				return;
			}
			var zTreeSetting={
					leaf: {
						enable: false
					},
					type: {
						treeType: "MAN_DEPARTMENT_TREE",
						noDeparmentUser:true,           
						onlineVisible:false
					},
					data: {
						chkStyle:"checkbox",
						chkboxType:"{'Y' : 'ps', 'N' : 'ps' }"
					},
					view: {
						title: "<s:text name='user.selectStaff'/>",//选择人员
						width: 300,
						height:400,
						url:imatrixRoot,
						showBranch:true
					},
					feedback:{
						enable: true
					},
					callback: {
						onClose:function(){
							for(var i=0;i<uIds.length;i++){
								ids=ids+uIds[i]+(i==uIds.length-1?"":",");
							}
							copyRoleToUser(ids);
						}
					}			
					};
				    popZtree(zTreeSetting);
		}

		function departmentTree(){
			var ids="";
			var uIds = jQuery("#main_table").getGridParam('selarrrow');
			if(uIds.length==0){
				//请选择人员
				iMatrix.alert("<s:text name='user.selectPerson'/>!");
				return;
			}
			var zTreeSetting={
					leaf: {
						enable: false
					},
					type: {
						treeType: "DEPARTMENT_TREE",
						noDeparmentUser:true,
						onlineVisible:false
					},
					data: {
						chkStyle:"checkbox",
						chkboxType:"{'Y' : 's', 'N' : 's'}"
					},
					view: {
						title: "<s:text name='user.selectDepartment'/>",//选择部门
						width: 300,
						height:400,
						url:imatrixRoot,
						showBranch:true
					},
					feedback:{
						enable: true
					},
					callback: {
						onClose:function(){
							for(var i=0;i<uIds.length;i++){
								ids=ids+uIds[i]+(i==uIds.length-1?"":",");
							}
							copyRoleToDepartment(ids);
						}
					}			
					};
				    popZtree(zTreeSetting);
		}
		
		function workgroupTree(){
			var ids="";
			var uIds = jQuery("#main_table").getGridParam('selarrrow');
			if(uIds.length==0){
				//请选择人员
				iMatrix.alert("<s:text name='user.selectPerson'/>!");
				return;
			}
			var zTreeSetting={
					leaf: {
						enable: false
					},
					type: {
						treeType: "GROUP_TREE",
						noDeparmentUser:true,
						onlineVisible:false
					},
					data: {
						chkStyle:"checkbox",
						chkboxType:"{'Y' : 's', 'N' : 's'}"
					},
					view: {
						title: "<s:text name='user.selectWorkGroup'/>",//选择工作组
						width: 300,
						height:400,
						url:imatrixRoot,
						showBranch:true
					},
					feedback:{
						enable: true
					},
					callback: {
						onClose:function(){
							for(var i=0;i<uIds.length;i++){
								ids=ids+uIds[i]+(i==uIds.length-1?"":",");
							}
							copyRoleToWorkgroup(ids);
						}
					}			
					};
				    popZtree(zTreeSetting);
		}
		function copyRoleToUser(sourceIds){
			var userIds=ztree.getIds();
			if(ztree.getNames()=="所有人员"){
				userIds="ALL_USER";
			}
			if(!userIds||userIds==""){
				//请选择需要被授权的人员
				iMatrix.alert("<s:text name='user.selectNeedAuthorizedPer'/>！");
				return;
			}
			//确认复制？(管理员权限不会被复制)
			iMatrix.confirm({
				message:"<s:text name='user.copyAuthorizes'/>",
				confirmCallback:copyRoleToUserOk,
				parameters:{sourceIds:sourceIds,userIds:userIds}
			});
		}
		function copyRoleToUserOk(obj){
			$.ajax({
				   data:{uids:obj.sourceIds,userIds:obj.userIds},
				   type: "POST",
				   cache:false,
				   url: "user-copyRoleToUser.action",
				   success: function(data, textStatus){
						iMatrix.alert(data);
				   },
				   error : function(XMLHttpRequest, textStatus) {
						iMatrix.alert(textStatus);
				   }
			});
		}
		function copyRoleToDepartment(sourceIds){
			var deptIds=ztree.getDepartmentIds();
			if(ztree.getDepartmentNames()=="所有部门"){
				deptIds="ALL_DEPARTMENT";
			}
			if(!deptIds||deptIds==""){
				//请选择需要被授权的部门
				iMatrix.alert("<s:text name='user.selectNeedAhthorizedDep'/>！");
				return;
			}
			//确认复制？(管理员权限不会被复制)
			iMatrix.confirm({
				message:"<s:text name='user.copyAuthorizes'/>",
				confirmCallback:copyRoleToDepartmentOk,
				parameters:{sourceIds:sourceIds,deptIds:deptIds}
			});
		}
		function copyRoleToDepartmentOk(obj){
			$.ajax({
				data:{uids:obj.sourceIds,deptIds:obj.deptIds},
				type: "POST",
				cache:false,
				url: "user-copyRoleToDepartment.action",
			    success: function(data, textStatus){
				   iMatrix.alert(data);
			    },
			    error : function(XMLHttpRequest, textStatus) {
					iMatrix.alert(textStatus);
			    }
			});
		}
		function copyRoleToWorkgroup(sourceIds){
			var workgroupIds=ztree.getWorkGroupIds();
			if(ztree.getWorkGroupNames()=="所有工作组"){
				workgroupIds="ALL_WORKGROUP";
			}
			if(!workgroupIds||workgroupIds==""){
				//请选择需要被授权的工作组
				iMatrix.alert("<s:text name='user.selectNeedAhthorizedWorkg'/>！");
				return;
			}
			//确认复制？(管理员权限不会被复制)
			iMatrix.confirm({
				message:"<s:text name='user.copyAuthorizes'/>",
				confirmCallback:copyRoleToWorkgroupOk,
				parameters:{sourceIds:sourceIds,workgroupIds:workgroupIds}
			});
		}
		function copyRoleToWorkgroupOk(obj){
			$.ajax({
				   data:{uids:obj.sourceIds,workgroupIds:obj.workgroupIds},
				   type: "POST",
				   cache:false,
				   url: "user-copyRoleToWorkgroup.action",
				   success: function(data, textStatus){
					   iMatrix.alert(data);
				   },
				   error : function(XMLHttpRequest, textStatus) {
						iMatrix.alert(textStatus);
				   }
			});
		}
		//更新用户缓存
		function updatUserCache(){
			ajaxSubmit("defaultForm", "${acsCtx}/organization/update-user-cache.action","",updatUserCacheCallback);
		}

		function updatUserCacheCallback(){//notice
			$('#notice').css("display","block");
			$('#notice').html("<font class=\"onSuccess\"><nobr><s:text name='user.updateUserCacheSuccess'/></nobr></font>");//更新用户缓存成功
			setTimeout('$("#notice").css("display","none");',3000);
		}

		/**下拉按钮效果 ****/
		function initUpdateBtnGroup(){//默认按钮效果  
			$("#parentUpdateBtn")
					.button()
					.click(function() {
						}).next()
						.button( {
							text: false,
							icons: {
								primary: "ui-icon-triangle-1-s"
							}
						})
						.click(function() {
							removeSearchBox();
							$("#exportbtn").hide();
							showUpdateBtnDiv();
						})
						.parent()
						.buttonset();

			}
		function initExportBtnGroup(){//默认按钮效果  
			$("#parentExportBtn")
				.button()
				.click(function() {
				})
				.next()
				.button( {
					text: false,
					icons: {
						primary: "ui-icon-triangle-1-s"
					}
				})
				.click(function() {
					removeSearchBox();
					$("#updatebtn").hide();
					showExportbtnDiv();
				})
				.parent()
				.buttonset();
			}
		
		
		

		function showExportbtnDiv(){//显示更多按钮效果  位置
			if($("#exportbtn").css("display")=='none'){
				$("#exportbtn").show();
				var position = $("#_exportbtn").position();
				$("#exportbtn").css("left",position.left+0);
				$("#exportbtn").css("top",position.top+24);
			}else{
				$("#exportbtn").hide();
			};
			$("#exportbtn").hover(
				function (over ) {
					$("#exportbtn").show();
				},
				function (out) {
					 $("#exportbtn").hide();
				}
			); 
		}
		function showUpdateBtnDiv(){//显示更多按钮效果位置  
			if($("#updatebtn").css("display")=='none'){
				$("#updatebtn").show();
				var position = $("#_updatebtn").position();
				$("#updatebtn").css("left",position.left+0);
				$("#updatebtn").css("top",position.top+24);
			}else{
				$("#updatebtn").hide();
			};
			$("#updatebtn").hover(
				function (over ) {
					$("#updatebtn").show();
				},
				function (out) {
					 $("#updatebtn").hide();
				}
			); 

		}
		function initCopy(){//默认按钮效果  
			$("#parentCopyBtn")
				.button()
				.click(function() {
				})
				.next()
				.button( {
					text: false,
					icons: {
						primary: "ui-icon-triangle-1-s"
					}
				})
				.click(function() {
					removeSearchBox();
					showCopyDiv();
				})
				.parent()
				.buttonset();
			}
		function showCopyDiv(){
			if($("#copyAuthorize").css("display")=='none'){
				$("#copyAuthorize").show();
				var position = $("#_copyAuthorize").position();
				$("#copyAuthorize").css("left",position.left+0);
				$("#copyAuthorize").css("top",position.top+24);
			}else{
				$("#copyAuthorize").hide();
			};
			$("#copyAuthorize").hover(
				function (over ) {
					$("#copyAuthorize").show();
				},
				function (out) {
					 $("#copyAuthorize").hide();
				}
			); 

		}
		function initDate(id){
			jQuery("#"+id).datepicker({
		       	"dateFormat":'yy-mm-dd',
			      changeMonth:true,
			      changeYear:true,
			      showButtonPanel:"true"
		       });
		}
	</script>
</head>
<body>
<div class="ui-layout-center">
	<div class="opt-body">
		 <form id="ajax_from" name="ajax_from" action="" method="post">  
            <input type="hidden" name="userId" id="ajaxId" />
	        <input type="hidden" name="departmentIds" id="departId" value="${oldDid }" />
	        <input type="hidden" name="oType" id="oType" value="${oldType }" />
	        <input type="hidden" name="look" id="look" />
	        <input type="hidden" name="comy" id="comy" value="${comy }"/>
	        <input type="hidden" name="edit" id="edit" />
	        <input type="hidden" name="fromWorkgroup" id="fromWorkgroup" value="${fromWorkgroup}">
	        <input type="hidden" name="workGroupId" id="workGroupId" value="${workGroupId}">
	        <input type="hidden" id="brcheId" value="${branchId}">
	        <input type="hidden" id="fromBracnhId" value="${fromBracnhId}">
		</form>
		<form action="#" name="defaultForm" id="defaultForm"></form>
		<script type="text/javascript">
			$(document).ready(function() {
				initButtonGroup();
			});
		</script>
		<aa:zone name="acs_content">
			<s:if test="look==null">
			<div class="opt-btn">
				<security:authorize ifAnyGranted="query_queryUser">
				<button  id="searchButton" class='btn' onclick="iMatrix.showSearchDIV(this);"><span><span><s:text name="common.search"/></span></span></button>
				</security:authorize>
				<security:authorize ifAnyGranted="copy_authority">
							<div class="btndiv" id="_copyAuthorize" style="*top:-2px;">
								<button  class="ui-button ui-widget ui-state-default ui-button-text-only ui-corner-left" id="parentCopyBtn" onclick="userTree()">
									<span class="ui-button-text"><s:text name="user.CopyPermissionsToThePersonnel" /></span><!-- 复制权限给人员-->
								</button>
								<button  title="<s:text name='user.more'/>"  class="ui-button ui-widget ui-state-default ui-button-icon-only ui-corner-right" id="select">
									<span class="ui-button-icon-primary ui-icon ui-icon-triangle-1-s"></span>
									<span class="ui-button-text"><s:text name="user.more"/></span><!-- 更多 -->
								</button>
							</div>
							<div id="copyAuthorize" class="flag" >
								<ul style="width: 100%" >
									<li ><a href="#"  onclick="departmentTree()"><s:text name="user.CopyPermissionsToTheDepartment"/></a></li><!-- 复制权限给部门 -->
									<li ><a href="#"  onclick="workgroupTree()"><s:text name="user.CopyPermissionsToTheWorkingGroup"/></a></li><!-- 复制权限给工作组 -->
								</ul>
							</div>
				</security:authorize>
					<security:authorize ifAnyGranted="addUser">
					<button  class='btn' <s:if test="!canEditUser">style="display: none;"</s:if> onclick="addUser('null','${acsCtx}/organization/user-input.action','ADD');"><span><span><s:text name="common.create"/></span></span></button>
					</security:authorize>
					<security:authorize ifAnyGranted="addUser">
					<button  class='btn' <s:if test="!canEditUser">style="display: none;"</s:if> onclick="updatUserCache();"><span><span><s:text name="user.updateUserCache"/></span></span></button>
					</security:authorize>
					<s:if test="canEditUser">
						<security:authorize ifAnyGranted="addUser">
							<div class="btndiv" id="_updatebtn" style="*top:-2px;">
								<button  class="ui-button ui-widget ui-state-default ui-button-text-only ui-corner-left" id="parentUpdateBtn" <s:if test="canEditUser">onclick="opt(webRoot+'/organization/user-input.action','NEW')"</s:if>>
									<span class="ui-button-text"><s:text name="common.alter"/></span>
								</button>
								<button  title="<s:text name='user.more'/>"  class="ui-button ui-widget ui-state-default ui-button-icon-only ui-corner-right" id="select">
									<span class="ui-button-icon-primary ui-icon ui-icon-triangle-1-s"></span>
									<span class="ui-button-text"><s:text name="user.more"/></span><!-- 更多 -->
								</button>
							</div>
							<div id="updatebtn" class="flag" >
								<ul style="width: 100%">
									<security:authorize ifAnyGranted="falseDelete">
										<s:if test='#versionType=="online"'>
											<li id="clear_department_sign" ><a href="#" onclick="demonstrateOperate();"><s:text name="user.removeDepartment"/></a></li><!-- 移除部门 -->
										</s:if><s:else>
										    <s:if test='oldType=="USERSBYDEPARTMENT"'>
										    	<li id="clear_department_sign" ><a href="#" onclick="opt_delete('${acsCtx}/organization/user-falseDelete.action','NEW');"><s:text name="user.removeDepartment"/></a></li><!-- 移除部门 -->
											</s:if>
										</s:else>
									</security:authorize>
									<security:authorize ifAnyGranted="userManager">
											<li ><a href="#"  onclick="opt('${acsCtx}/organization/user-userManger.action','NEW','${departmentId}');"><s:text name="serverConfig.start"/>/<s:text name="serverConfig.end"/></a></li><!-- 启用/禁用 -->
									</security:authorize>
									<security:authorize ifAnyGranted="addUser">
											<li ><a href="#" onclick="unlockUser();"><s:text name="user.unlockUser"/></a></li> <!-- 用户解锁 -->
									</security:authorize>
									<s:if test="departmType!='NODEPARTMENT'&&departmType!='NODEPARTMENT_USER'&&departmType!='NOBRANCH'">
										<security:authorize ifAnyGranted="acs_organization_changeMainDepartment">
											<li ><a href="#" onclick="changeMainDepartment();"><s:text name="user.changeMainDepartment"/></a></li><!-- 更换正职 -->
								        </security:authorize>
							        </s:if>
							        <security:authorize ifAnyGranted="acs-organization-user-unlockData">
											<li ><a href="#" onclick="unlockData();"><s:text name="user.unlockData"/></a></li><!-- 数据解锁 -->
									</security:authorize>
								</ul>
							</div>
							<script type="text/javascript">
								$(document).ready(function() {
									if($("#departId").val() != ''){
										$( "#clear_department_sign" ).show();
									}else{
										$( "#clear_department_sign" ).hide();
									}
								});
							</script>
						</security:authorize>
					</s:if>
						
					<security:authorize ifAnyGranted="acs_clear_user">
						<s:if test='#versionType=="online"'>
							<button  class='btn' <s:if test="!canEditUser">style="display: none;"</s:if>  onclick="demonstrateOperate();"><span><span><s:text name="common.delete"/></span></span></button><!-- 删除 -->
						</s:if><s:else>
							<button  class='btn'  <s:if test="!canEditUser">style="display: none;"</s:if> onclick="clearUser();"><span><span><s:text name="common.delete"/></span></span></button><!-- 删除 -->
						</s:else>
			        </security:authorize>
					<!--<security:authorize ifAnyGranted="acs_validateLdapStart">
					<button  class='btn'  <s:if test="!canEditUser">style="display: none;"</s:if> onclick="ldapValidate();"><span><span>同步LDAP</span></span></button>
					</security:authorize>
					-->
					<s:if test="canEditUser">
						<security:authorize ifAnyGranted="acs_organization_user_showImportUser">
							<div class="btndiv" id="_exportbtn" style="*top:-2px;">
								<button  class="ui-button ui-widget ui-state-default ui-button-text-only ui-corner-left" id="parentExportBtn" <s:if test="canEditUser">onclick="importUser();"</s:if>>
									<span class="ui-button-text"><s:text name="user.import"/></span><!-- 导入 -->
								</button>
								<button  title='<s:text name="user.more"/>'  class="ui-button ui-widget ui-state-default ui-button-icon-only ui-corner-right" id="select">
									<span class="ui-button-icon-primary ui-icon ui-icon-triangle-1-s"></span>
									<span class="ui-button-text"><s:text name="user.more"/></span><!-- 更多 -->
								</button>
							</div>
							<div id="exportbtn" class="flag" >
								<ul style="width: 100%">
									<security:authorize ifAnyGranted="acs_user_exportUser">
										<li><a href="#" onclick="exportUser();"><s:text name="user.export"/></a></li><!-- 导出 -->
									</security:authorize>
								</ul>
							</div>
						</security:authorize>
					</s:if>
			</div>
			</s:if><s:else>
				<div class="opt-btn">
					<security:authorize ifAnyGranted="getUserByWorkGroup ">
						<button  class='btn' onclick="iMatrix.showSearchDIV(this);"><span><span><s:text name="common.search" /></span></span></button><!-- 查询 -->
					</security:authorize>
					<security:authorize ifAnyGranted="addWorkGroupToUser">
				        <button  class='btn' onclick="addUsersToWorkgroup();"><span><span><s:text name="workGroup.addUser" /></span></span></button>
					</security:authorize>
					<security:authorize ifAnyGranted="workGroupRemoveUser">
					    <button  class='btn' onclick="removeUsersToWorkgroup();"><span><span><s:text name="workGroup.removeUser" /></span></span></button>
					</security:authorize>
				</div>
			</s:else>
			<div id="notice"> <s:actionmessage /> </div>	
			<div id="opt-content" >
			   <aa:zone name="acs_list">
				<form id="formName" name="formName" action="" method="post">
				     <input type="hidden" name="olDid" id="olDid" value="">
				     <input type="hidden" name="olType" id="olType" value="">
				     <input type="hidden" name="companyId" id="companyId" value="${companyId}">
				     <s:if test="containBranches">
					     <s:if test="comeFrom=='workgroup'">
					     <view:jqGrid url="${acsCtx}/organization/user.action?workGroupId=${workGroupId }&departmentId=${departmentId}&departmentIds=${oldDid}&oType=${oldType }&departmType=${departmType }&branchId=${branchId}" pageName="userPage" code="ACS_USER_SUB_COMPANY" gridId="main_table"></view:jqGrid>
					     </s:if>
					     <s:else>
					     <view:jqGrid url="${acsCtx}/organization/user.action?workGroupId=${workGroupId }&departmentId=${departmentId}&departmentIds=${oldDid}&oType=${oldType }&departmType=${departmType }&branchId=${branchId}" pageName="page" code="ACS_USER_SUB_COMPANY" gridId="main_table"></view:jqGrid>
					     </s:else>
				     </s:if><s:else>
				     	 <s:if test="comeFrom=='workgroup'">
					     <view:jqGrid url="${acsCtx}/organization/user.action?workGroupId=${workGroupId }&departmentId=${departmentId}&departmentIds=${oldDid}&oType=${oldType }&departmType=${departmType }&branchId=${branchId}" pageName="userPage" code="ACS_USER" gridId="main_table"></view:jqGrid>
					     </s:if>
					     <s:else>
					     <view:jqGrid url="${acsCtx}/organization/user.action?workGroupId=${workGroupId }&departmentId=${departmentId}&departmentIds=${oldDid}&oType=${oldType }&departmType=${departmType }&branchId=${branchId}" pageName="page" code="ACS_USER" gridId="main_table"></view:jqGrid>
					     </s:else>
				     </s:else>
				</form>
				</aa:zone>
			</div>
		</aa:zone>
	</div>
</div>    	
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script src="${resourcesCtx}/widgets/timepicker/timepicker_<%=com.norteksoft.product.util.ContextUtils.getCurrentLanguage()%>.js" type="text/javascript"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>
