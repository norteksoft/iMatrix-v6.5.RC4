<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/acs-taglibs.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
   <head>
   
	<title><s:text name="common.standRoleManager"></s:text></title>
     <%@ include file="/common/acs-iframe-meta.jsp"%>
      
	<script type="text/javascript" src="${resourcesCtx}/js/custom.tree.js" ></script>
	<style type="text/css">
		.message{ padding: 2px;color: red;}
	</style>
	<script type="text/javascript">
		function loadContent(treeId){
			var sysId = treeId.split('_')[1];
			$('#_systemId').attr('value', sysId);
			$('#businessSystemId').attr('value', sysId);
			if($("#role_table").attr('id') != "role_table"){
				returnRoleList();
			}else{
				if($("#role_table").attr('init') == '0'){
					initRoleTable();
					$("#role_table").attr('init', '1');
				}else{
					reloadRoleTable();
				}
			}
		}
		//导出
		function exportRole(){
			$("#ajax_from").attr("action","${acsCtx}/authorization/role-exportRole.action");
			$("#ajax_from").submit();
			
		}
		//导入角色功能关系
		function importRole(){
			$.colorbox({href:'${acsCtx}/authorization/role-showImportRole.action',
				iframe:true, innerWidth:400, innerHeight:200,overlayClose:false,title:iMatrixMessage["authorization.importRole"]});
		}
		function accredit(){
			$.colorbox({href:'${acsCtx}/authorization/role-showAccredit.action',
				iframe:true, innerWidth:400, innerHeight:200,overlayClose:false,title:iMatrixMessage["authorization.importRoleUserContion"]});
		}
		//导入用户角色关系
		function createRole(){
			if(''==$("#_systemId").val()){
				//"请选择系统！"
				iMatrix.alert(iMatrixMessage["authorization.selectSystem"]);
			}else{
				//隐藏查询框
				HideSearchBox();
				ajaxSubmit('roleForm', '${acsCtx}/authorization/role-input.action', 'acs_button,acs_content', roleValidate);
			}
		}
		function updateRole(){
			var roles = jQuery("#main_table").getGridParam('selarrrow');
			if(roles.length == 1){
				$('#role_id').attr('value', roles);
				$.ajax({
					data:{id:$('#role_id').attr('value')},
					type:"post",
					url:webRoot+"/authorization/role-validateRole.action",
					beforeSend:function(XMLHttpRequest){},
					success:function(data, textStatus){
						if("ok"==data){
							//隐藏查询框
							HideSearchBox();
							ajaxSubmit('roleForm', '${acsCtx}/authorization/role-input.action', 'acs_button,acs_content', roleValidate);
						}else{
							//"无权限修改此角色！"
							iMatrix.alert(iMatrixMessage['authorization.noAuthUpdateRole']);
						}
					},
					complete:function(XMLHttpRequest, textStatus){},
			        error:function(){
					}
				});
			}else{
				showMessage('role_msg',iMatrixMessage['authorization.selectUpdateRole']);// '请选择一个要修改的角色'
			}
		}
		function deleteRole(){
			var roles = jQuery("#main_table").getGridParam('selarrrow');
			if(roles.length > 0){
				if(deletable(roles)){
					//"确定要删除吗？"
					iMatrix.confirm({
						message:iMatrixMessage['authorization.sureDelete'],
						confirmCallback:deleteRoleOk,
						parameters:roles
					});
				}else{
					showMessage('role_msg', iMatrixMessage['authorization.threeRoleCannotDelete']+'(acsSystemAdmin、acsSecurityAdmin、acsAuditAdmin)');//三员角色不允许删除 
				}
			}else{
				showMessage('role_msg', iMatrixMessage['authorization.selectDeleteRole']);//'请选择要删除的角色'
			}
		}
		
		function deleteRoleOk(roles){
			for(var i=0; i<roles.length; i++){
				$("#roleForm").html($("#roleForm").html()+'<input type="hidden" name="roleIds" value="'+roles[i]+'">');
			}
			//隐藏查询框
			HideSearchBox();
			setPageState();
			ajaxSubmit('roleForm', '${acsCtx}/authorization/role-delete.action', 'acs_content');
		}
		function deletable(roles){
			for(var i=0; i<roles.length;i++){
				var roleCode = $('tr#'+roles[i]+' td:eq(1)').attr('title');
				if(roleCode=='acsSystemAdmin'||roleCode=='acsSecurityAdmin'||roleCode=='acsAuditAdmin'){
					return false;
				}
			}
			return true;
		}
		function addResource(){
			var roles = jQuery("#main_table").getGridParam('selarrrow');
			if(roles.length == 1){
				$.colorbox({href:'${acsCtx}/authorization/role-roleToFunctionList.action?roleId='+roles[0],
					 iframe:true, innerWidth:500, innerHeight:400,overlayClose:false,title:iMatrixMessage['authorization.addResources']});//"添加资源"
			}else{
				showMessage('role_msg', iMatrixMessage['authorization.selectRoleToAddResources']);//'请选择一个要添加资源的角色'
			}
		}
		function removeResource(){
			var roles = jQuery("#main_table").getGridParam('selarrrow');
			if(roles.length == 1){
				$.colorbox({href:'${acsCtx}/authorization/role-roleRomoveFunctionList.action?roleId='+roles[0],
					 iframe:true, innerWidth:500, innerHeight:400,overlayClose:false,title:iMatrixMessage['authorization.removeResources']});//"移除资源"
			}else{
				showMessage('role_msg', iMatrixMessage['authorization.selectRoleToDeleteResources']);//'请选择一个要移除资源的角色'
			}
		}
		function queryFunctionRole(){
			$.colorbox({href:'${acsCtx}/authorization/role-query.action?queryType='+'roleByFunction',
				 iframe:true, innerWidth:$('.ui-layout-center').width()-100, innerHeight:$('.ui-layout-center').height()-50,overlayClose:false,title:iMatrixMessage['authorization.showResources']});//"查询资源"
		}
		function getRoleQueryContentHeight(){
			var h = $('.ui-layout-center').height();
			$("#opt-content").css("height",h-80); 
		}
		function saveRole(){
			$('#roleForm').submit();
		}
		function validateRolename(name){
			if(name.indexOf("(")>=0||name.indexOf(")")>=0||name.indexOf("()")>=0
					||name.indexOf("（")>=0||name.indexOf("）")>=0||name.indexOf("（）")>=0){
				return false;
			}
			return true;
		}
		function returnRoleList(){
			ajaxSubmit('ajax_from', '${acsCtx}/authorization/role.action', 'acs_button,acs_content');
		}
		function ajaxSubmit(form, url, zoons, ajaxCallback){
			var formId = "#"+form;
			if(url != ""){
				$(formId).attr("action", url);
			}
			ajaxAnywhere.formName = form;
			ajaxAnywhere.getZonesToReload = function() {
				return zoons;
			};
			ajaxAnywhere.onAfterResponseProcessing = function () {
				if(typeof(ajaxCallback) == "function"){
					ajaxCallback();
				}
			};
			ajaxAnywhere.submitAJAX();
		}
		function showMessage(id, msg){
			if(msg != ""){
				$("#"+id).html(msg);
			}
			$("#"+id).show("show");
			setTimeout('$("#'+id+'").hide("show");',3000);
		}

		function roleValidate(){
			$("#roleForm").validate({
				submitHandler: function() { 
				if(validateRolename($("#roleName").val())){
					if("branchAdmin"==$("#adminSign").val() && $("#branchSum").val()>1 && $("#subCompanyId").val()==''){
						//请选择所属分支机构！
						iMatrix.alert(iMatrixMessage['authorization.selectBranchs']);
					}else{
						var code = $("#roleCode").val();
						if(code.indexOf("role-")==0&&code.length>23){
							//"默认编号时超出最大长度23"
							iMatrix.alert(iMatrixMessage['authorization.defaultCodeLonest23']);
							return;
						}
						if(code.indexOf(" ")==0||code.lastIndexOf(" ")==code.length-1){//最前面有空格
							//"编码前后不能包含空格!"
							iMatrix.alert(iMatrixMessage["workgroup.codeNotContainsSpace"]);
							return;
						}
						if(code.indexOf(">")>=0||code.indexOf("<")>=0||code.indexOf("\"")>=0||code.indexOf("'")>=0
								||code.indexOf("/")>=0){//包含特殊字符
							//编码不能包含符号
							iMatrix.alert(iMatrixMessage['workgroup.codeNotContainsfuhao']+">、<、\"、'、/");
							return;
						}
						$.ajax({
							data:{id:$("#roleId").val(),businessSystemId:$("#businessSystemId").val(),roleName:$("#roleName").val(),branchesId:$("#subCompanyId").val(),roleCode:$("#roleCode").val()},
							type:"post",
							url:'${acsCtx}/authorization/role-validateNameOnly.action',
							beforeSend:function(XMLHttpRequest){},
							success:function(data, textStatus){
								if("ok"==data){
									ajaxSubmit('roleForm', '${acsCtx}/authorization/role-save.action', 'acs_button,acs_content',roleValidate);
								}else if("codeNameRepeat"==data){
									//角色编号重复！\n角色名称重复！
									iMatrix.alert(iMatrixMessage['authorization.roleCodeRepeat'] + "\n" + iMatrixMessage['authorization.roleNameRepeat']);
								}else if("codeRepeat"==data){
									//角色编号重复！
									iMatrix.alert(iMatrixMessage['authorization.roleCodeRepeat']);
								}else if("nameRepeat"==data){
									//"角色名称重复！"
									iMatrix.alert(iMatrixMessage['authorization.roleNameRepeat']);
								}
							},
							complete:function(XMLHttpRequest, textStatus){},
					        error:function(){
				
							}
						});
					}
				}else{
					$("#roleName").parent().append('<label  class="error">'+iMatrixMessage['authorization.roleNameNotContainesParentheses']+'</label>');//角色名称中不能包含括号！
				}
			},
			rules: {
				code: {
					required: true
				},
				name: {
					required: true
				}
				},
			messages: {
					'code':iMatrixMessage['common.required'],//"必填",
			    	'name': iMatrixMessage['common.required']//"必填",
				}
		});
			showMessage('message');
		}

		function selectSystem(){
			var roles = jQuery("#main_table").getGridParam('selarrrow');
			var canCopy = true; 
			var canCopyWf = true; 
			for(var i=0;i<roles.length;i++){
				var code=$("#main_table").jqGrid('getCell',roles[i],"code");
				if(code=="acsBranchAdmin"||code=="acsSystemAdmin"||code=="acsSecurityAdmin"||code=="acsAuditAdmin"){
					canCopy = false;
					break;
				}
				if(code=="workflowManager"||code=="defManager"){
					canCopyWf = false;
					break;
				}
			}
			if(!canCopy){
				//不能复制安全管理员、系统管理员、审计管理员、分支机构管理员的权限
				iMatrix.alert(iMatrixMessage['authorization.cannotCopyAuth']);
				return;
			}
			if(!canCopyWf){
				//不能复制工作流管理员、流程定义管理员的权限
				iMatrix.alert(iMatrixMessage['authorization.cannotCopyAuthManager']);
				return;
			}
			var roleIds="";
			if(roles.length >= 1){
				roleIds = roles.join(",");
				custom_ztree({url:webRoot+'/authorization/role-roleTree.htm',
					onsuccess:function(){closeFun();},
					width:300,
					height:400,
					title:iMatrixMessage['authorization.selectRole'],
					postData:{businessSystemId:$("#businessSystemId").attr("value"),roleIdStrs:roleIds},
					nodeInfo:['type','roleId'],
					multiple:true,
					webRoot:imatrixRoot
				});
			}else{
				showMessage('role_msg', iMatrixMessage['authorization.pleaseSelectRole']);
			}
		}

		function closeFun(){
			var roleIds=getSelectValue("roleId");
			if(roleIds=="system"){
				iMatrix.alert(iMatrixMessage['authorization.pleaseSelectRole']);
				return;
			}
			//"确定复制权限吗?"
			iMatrix.confirm({
				message:iMatrixMessage['authorization.sureCopyAuth'],
				confirmCallback:closeFunOk,
				parameters:roleIds
			});
		}
		function closeFunOk(roleIds){
			if(roleIds!=""&&roleIds.length>0){
				var roles = jQuery("#main_table").getGridParam('selarrrow');
				$.ajax({
					data:{roleIdStrs:roles.join(","),selectRoleIds:roleIds.join(",")},
					type:"post",
					url:webRoot+"/authorization/role-copyRoleAndFunction.htm",
					beforeSend:function(XMLHttpRequest){},
					success:function(data, textStatus){
						//复制权限成功
						iMatrix.alert(iMatrixMessage['authorization.sureCopyAuthSuccess']);
					},
					complete:function(XMLHttpRequest, textStatus){},
			        error:function(){
		
					}
				});
			}else{
				iMatrix.alert(iMatrixMessage['authorization.pleaseSelectRole']);
			}
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
// 			alert( $("#_copyAuthorize").width());
			$("#copyAuthorize").width($("#_copyAuthorize").width());
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
		

	</script>
</head>

<body>
<div class="ui-layout-center">
			<div class="opt-body">
				<form id="ajax_from" name="ajax_from" action="" method="post">
					<input type="hidden" id="_systemId" name="businessSystemId" value="${businessSystemId}" />
				</form>
				<form id="default_search_from" name="default_search_from" action="" method="post"></form>
				<aa:zone name="acs_button">
				<script>
					$(function(){
						initCopy();
					});
				</script>
				<div class="opt-btn">
					<security:authorize ifAnyGranted="editRole"><button  class='btn' onclick="iMatrix.showSearchDIV(this);"><span><span><s:text name="common.search"/></span></span></button></security:authorize>
					<security:authorize ifAnyGranted="editRole"><button  class='btn' onclick="createRole();"><span><span><s:text name="common.create"/></span></span></button></security:authorize>
					
					<s:if test='#versionType=="online"'>
						<security:authorize ifAnyGranted="editRole"><button  class='btn' onclick="demonstrateOperate();"><span><span><s:text name="permission.authorize.update"/></span></span></button></security:authorize>
						<security:authorize ifAnyGranted="acs_deleteRoles"><button  class='btn' onclick="demonstrateOperate();"><span><span><s:text name="permission.authorize.delete"/></span></span></button></security:authorize>
						<s:if test="hasSecurityAdmin">
							<security:authorize ifAnyGranted="acs_roleToFunction"><button  class='btn' onclick="demonstrateOperate();"><span><span><s:text name="role.addFunction"/></span></span></button></security:authorize>
							<security:authorize ifAnyGranted="roleRemoveFunction"><button  class='btn' onclick="demonstrateOperate();"><span><span><s:text name="role.romoveFunction"/></span></span></button></security:authorize>
							<security:authorize ifAnyGranted="exportRole"><button  class='btn' onclick="exportRole()"><span><span><s:text name="authorization.exportAuthorization"/></span></span></button></security:authorize>
							<security:authorize ifAnyGranted="importRole"><button  class='btn' onclick="importRole()"><span><span><s:text name="authorization.importRole"/></span></span></button></security:authorize>
						</s:if>
					</s:if><s:else>
						<security:authorize ifAnyGranted="editRole"><button  class='btn' onclick="updateRole();"><span><span><s:text name="permission.authorize.update"/></span></span></button></security:authorize>
						<security:authorize ifAnyGranted="acs_deleteRoles"><button  class='btn' onclick="deleteRole();"><span><span><s:text name="permission.authorize.delete"/></span></span></button></security:authorize>
						<security:authorize ifAnyGranted="acs_role_role-roleTree"><button  class='btn' onclick="selectSystem();"><span><span><s:text name="authorization.copyAuthorization"/></span></span></button></security:authorize>
						<s:if test="hasSecurityAdmin">
							<security:authorize ifAnyGranted="acs_roleToFunction"><button  class='btn' onclick="addResource();"><span><span><s:text name="role.addFunction"/></span></span></button></security:authorize>
							<security:authorize ifAnyGranted="roleRemoveFunction"><button  class='btn' onclick="removeResource();"><span><span><s:text name="role.romoveFunction"/></span></span></button></security:authorize>
							<button  class='btn' onclick="queryFunctionRole();"><span><span><s:text name="authorization.queryAuthorization"/></span></span></button>
							<security:authorize ifAnyGranted="exportRole"><button  class='btn' onclick="exportRole()"><span><span><s:text name="authorization.exportAuthorization"/></span></span></button></security:authorize>
							<security:authorize ifAnyGranted="importRole">
								<div class="btndiv" id="_copyAuthorize" style="*top:-2px;">
								<button  class="ui-button ui-widget ui-state-default ui-button-text-only ui-corner-left" id="parentCopyBtn" onclick="importRole()">
									<span class="ui-button-text"><s:text name="authorization.importRole"/></span>
								</button>
								<button  title='<s:text name="user.more"/>'  class="ui-button ui-widget ui-state-default ui-button-icon-only ui-corner-right" id="select">
									<span class="ui-button-icon-primary ui-icon ui-icon-triangle-1-s"></span>
									<span class="ui-button-text"><s:text name="user.more"/></span>
								</button>
								</div>
								<div style="width: 130px" id="copyAuthorize" class="flag" >
								<ul style="width: 100%" >
									<li ><a href="#"  onclick="accredit()"><s:text name="authorization.importRoleUserContion"/></a></li><!-- 导入角色用户关系 -->
								</ul>
							</div>
							</security:authorize>
						</s:if>
					</s:else>
				</div>
				<div id="role_msg" style="color: red;"></div>
				</aa:zone>
				<aa:zone name="acs_content">
				<div id="message"><s:actionmessage theme="mytheme"/></div>
				<script type="text/javascript">
					setTimeout('$("#message").hide();',3000);
				</script>
					<form id="roleForm" name="roleForm" action="#" method="post">
					<input type="hidden" name="id" id="role_id"/>
					<input type="hidden" id="businessSystemId" name="businessSystemId" value="${businessSystemId}" />
					<input type="hidden" id="ids" name="ids"/>
					</form>
				<div id="opt-content" >
					<view:jqGrid url="${acsCtx}/authorization/role.action?businessSystemId=${businessSystemId}" code="ACS_ROLES" pageName="page" submitForm="default_search_from" gridId="main_table"></view:jqGrid>
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
	