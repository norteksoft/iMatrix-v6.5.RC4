<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/acs-taglibs.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
   <head>
   
    <%@ include file="/common/acs-iframe-meta.jsp"%>
	
	<script src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js" type="text/javascript"></script>
      
	<style type="text/css">
		table.full{
			border:1px solid #C5DBEC;
			border-collapse:collapse;
			width: 100%;
		}
		table.full thead tr{
			background: url("../../images/ui-bg_glass_85_dfeffc_1x400.png") repeat-x scroll 50% 50% #DFEFFC;
			height:20px;
			width: 33.3%;
			color:#2E6E9E;
			font-weight:bold;
		}
		table.full thead tr th, table.full tbody tr td { border:1px solid #C5DBEC;}
		table.full thead tr th {  text-align: left; }
		table.full thead tr th,table.full tbody tr td{ padding-left: 20px;}
		.sng_row li {
			list-style:none outside none;
			text-align:left;
			line-height:1.5em;
		}
	</style>
	<script type="text/javascript">
		function hasAll(operate,roleId){
			$.ajax({
				   type: "POST",
				   url: "${acsCtx}/authorization/role-hasAll.action",
				   data:{buttonType:operate,roleId:roleId},
				   success:function(msg, textStatus){
					   if(msg!=""){
						   iMatrix.confirm({
								message:msg,
								confirmCallback:hasAllOk,
								parameters:operate
							});
					   }else{
						   openTree(operate);
					   }
			      },
				   error:function(XMLHttpRequest, textStatus) {
						iMatrix.alert(textStatus);
				   }
			  }); 
		}
		
		function hasAllOk(operate){
			openTree(operate);
		}
		function openTree(operate){
			if(operate == "ADD_USER"){
				$("#message").html("");
				roleAddUser();
			}else if(operate == "ADD_DEPARTMENT"){
				$("#message").html("");
				roleAddDepartment();
			}else if(operate == "ADD_WORKGROUP"){
				$("#message").html("");
				roleAddWorkgroup();
			}
		}
		function opt(operate){
			var roleId = $("#id").attr("value");
			if(roleId==""){
				//请选择角色
				iMatrix.alert(iMatrixMessage["authorization.pleaseSelectRole"]);
				return;
			}
			if(operate != "REMOVE_ALL"){
				hasAll(operate,roleId);
			}else{
				if(validateRemove()) return;
				//确定要从角色中移除所选项?
				iMatrix.confirm({
					message:iMatrixMessage["authorization.sureDeleteSelectedItems"],
					confirmCallback:optOk
				});
			}
		}
		function optOk(){
			$("#removeForm").submit();
			return;
		}
		function roleAddUser(){
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
						chkboxType:"{'Y' : 'ps', 'N' : 'ps' }",
						branchIds:"${manageBranchesIds}"
					},
					view: {
						title: iMatrixMessage["user.selectStaff"],//选择人员
						width: 300,
						height:400,
						url:imatrixRoot,
						showBranch:true
					},
					feedback:{
						enable: true,
				                //showInput:"point_user",
				                //hiddenInput:"point_user_value",
				                append:false
					},
					callback: {
						onClose:function(){
						submitFormAuthorization("user");
						}
					}			
					};
				    popZtree(zTreeSetting);
		}

		function roleAddDepartment(){
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
						chkboxType:"{'Y' : 's', 'N' : 's'}",
						branchIds:"${manageBranchesIds}"
					},
					view: {
						title: iMatrixMessage["authorization.selectDep"],//"选择部门"
						width: 300,
						height:400,
						url:imatrixRoot,
						showBranch:true
					},
					feedback:{
						enable: true,
				                //showInput:"point_user",
				                //hiddenInput:"point_user_value",
				                append:false
					},
					callback: {
						onClose:function(){
						submitFormAuthorization("department");
						}
					}			
					};
				    popZtree(zTreeSetting);
		}
		
		function roleAddWorkgroup(){
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
						chkboxType:"{'Y' : 's', 'N' : 's'}",
						branchIds:"${manageBranchesIds}"
					},
					view: {
						title: iMatrixMessage["authorization.selectWorkGroup"],// "选择工作组",
						width: 300,
						height:400,
						url:imatrixRoot,
						showBranch:true
					},
					feedback:{
						enable: true,
				                //showInput:"point_user",
				                //hiddenInput:"point_user_value",
				                append:false
					},
					callback: {
						onClose:function(){
						submitFormAuthorization("workGroup");
						}
					}			
					};
				    popZtree(zTreeSetting);
		}
		function validateRemove(){
			var userList = document.getElementsByName("userIds");
			var deptList = document.getElementsByName("departmentsIds");
			var workgroupList = document.getElementsByName("workGroupIds");
			for(var i=0; i<userList.length; i++){
				var name=$("#name_"+$(userList[i]).attr("value")).html();
				if($(userList[i]).attr("checked")){
					var roleName=$("#roleName").val();
				if((name=="systemAdmin"&&roleName=="系统管理员")||(name=="securityAdmin"&&roleName=="安全管理员")||(name=="auditAdmin"&&roleName=="审计管理员")){ 
					
					//系统默认的管理员不能删除！
					iMatrix.alert(iMatrixMessage["authorization.defaultManagerCannotDelete"]);
					return true;
				}
				}
			}
			for(var i=0; i<userList.length; i++){
				if($(userList[i]).attr("checked")) return false; 
			}
			for(var i=0; i<deptList.length; i++){
				if($(deptList[i]).attr("checked")) return false;
			}
			for(var i=0; i<workgroupList.length; i++){
				if($(workgroupList[i]).attr("checked")) return false;
			}
			//请选择需要移除的项
			iMatrix.alert(iMatrixMessage['authorization.selectDelete']);
			return true;
		}
		function checkAll(obj){
			if($(obj).attr('id') == 'acs_users'){
				checkEveryOne(obj, 'userIds');
			}else if($(obj).attr('id') == 'acs_depts'){
				checkEveryOne(obj, 'departmentsIds');
			}else if($(obj).attr('id') == 'acs_workgroups'){
				checkEveryOne(obj, 'workGroupIds');
			}
		}
		function checkEveryOne(obj, name){
			var ids = $('input[name='+name+']');
			if($(obj).attr('checked')){
				for(var i = 0; i < ids.length; i++){
					var isAdmin = $(ids[i]).attr("aa");
					if(!isAdmin){
					   $(ids[i]).attr('checked', 'checked');
					}
				}
			}else{
				for(var i = 0; i < ids.length; i++){
					$(ids[i]).attr('checked', '');
				}
			}
		}
		
		function _click_one(boxId,inputName){
			var ones = $("input[name='"+inputName+"']");
			var allChecked = true;
			for(var i=0;i<ones.length;i++){
			  var isAdmin = $(ones[i]).attr("aa");
	          if($(ones[i]).attr("checked")==false && !isAdmin){
	              allChecked=false;
	              break;
	          }
			}
			if(allChecked){
				$("#"+boxId).attr("checked","checked");
			}else{
				$("#"+boxId).attr("checked","");
			}
		}

		function packagingSelectValue(values,name){
			var ids=values.split(",");
			for(var i=0;i<ids.length;i++){
				var inpt = document.createElement("input");
				inpt.setAttribute("name", name);
				inpt.setAttribute("value", ids[i]);
				inpt.setAttribute("type", "hidden");
				document.getElementById("acsAddFrom").appendChild(inpt);
			}
		}

		function submitFormAuthorization(type){
			if(type=="user"){
				var values=ztree.getIds();
				if(ztree.getNames()=="所有人员"){
					if("${isAdminRole}"=='false'){
						values="ALL_USER";
					}
				}
				if(values!=""){
					//packagingSelectValue(values,"userIds");
					$("#ajax_from").append('<textarea id="addUserIds" name="addUserIds" style="display: none;"></textarea>');
					$("#addUserIds").val(values);
					$("#ajax_from").attr("action","${acsCtx}/authorization/role-addUsersToRole.action");
					$("#ajax_from").submit();
					$("#addUserIds").remove();
				}else{
					iMatrix.alert('<s:text name="user.seleteUser"/>');
					return;
				}
			}else if(type=="department"){
				var values=ztree.getDepartmentIds();
				if(ztree.getDepartmentNames()=="所有部门"){
					if("${isAdminRole}"=='false'){
						values="ALL_DEPARTMENT";
					}
					
				}
				if(values!=""){
					//packagingSelectValue(values,"departmentsIds");
					$("#ajax_from").append('<textarea id="addDepartmentIds" name="addDepartmentIds" style="display: none;"></textarea>');
					$("#addDepartmentIds").val(values);
					$("#ajax_from").attr("action","${acsCtx}/authorization/role-addUsersToRole.action");
					$("#ajax_from").submit();
					$("#addDepartmentIds").remove();
				}else{
					iMatrix.alert('<s:text name="department.selectDepartment"/>');
					return;
				}
			}else if(type=="workGroup"){
				var values=ztree.getWorkGroupIds();
				if(ztree.getWorkGroupNames()=="所有工作组"){
					if("${isAdminRole}"=='false'){
						values="ALL_WORKGROUP";
					}
				}
				if(values!=""){
					//packagingSelectValue(values,"workGroupIds");
					$("#ajax_from").append('<textarea id="addWorkgroupIds" name="addWorkgroupIds" style="display: none;"></textarea>');
					$("#addWorkgroupIds").val(values);
					$("#ajax_from").attr("action","${acsCtx}/authorization/role-addUsersToRole.action");
					$("#ajax_from").submit();
					$("#addWorkgroupIds").remove();
				}else{
					iMatrix.alert('<s:text name="workGroup.selectWorkGroup"/>');
					return;
				}
			}
		}

		function loadTreeContent(type, id){
			if(type == "BUSINESSSYSTEM"){
				return;
			}else if(type == "ROLE"){
				$("#id").attr("value", id);
				$("#roleId").attr("value", id);
				$("#ajax_from").attr("action", "${acsCtx}/authorization/standard-role-authoritys.action");
			}else return;
			ajaxAnywhere.formName = "ajax_from";
			ajaxAnywhere.getZonesToReload = function() {
				return "acs_button,acs_content,acs_footer";
			};
			ajaxAnywhere.submitAJAX();
		}

		function returnList(roleId){
			$("#ajax_from").attr("action", "${acsCtx}/authorization/standard-role-authoritys.action");
			ajaxAnywhere.formName = "ajax_from";
			ajaxAnywhere.getZonesToReload = function() {
				return "acs_button,acs_content";
			};
			ajaxAnywhere.submitAJAX();
		}
		function loadContent(obj){
			var index = obj.indexOf("_");
			loadTreeContent(obj.substring(0, index), obj.substring(index+1, obj.length));
		}
		//导出
		function exportRole(){
			$("#ajax_from").attr("action",webRoot+"/authorization/standard-role-exportRole.action");
			$("#ajax_from").submit();
		}
		//查看权限
		function viewAuthority(){
			if($("#roleId").val()==''){
				iMatrix.alert(iMatrixMessage["authorization.pleaseSelectRole"]);
			}else{
				init_colorbox(webRoot+"/authorization/standard-role-viewAuthority.action?roleId="+$("#roleId").val(),iMatrixMessage['authorization.showAuth'],750,450);
			}
		}
	</script>
</head>

<body onload="getContentHeight();">
<div class="ui-layout-center">
<div class="opt-body">
				<aa:zone name="acs_button">
				<div class="opt-btn">
					<s:if test="!isDefaultRole">
					<security:authorize ifAnyGranted="standardRoleRemoveUser"><button  class='btn' onclick="opt('ADD_USER')"><span><span><s:text name="authorization.addUser"/><!-- 添加用户 --></span></span></button></security:authorize>
					<s:if test="!isAdminRole">
						<security:authorize ifAnyGranted="standardRoleRemoveUser"><button  class='btn' onclick="opt('ADD_DEPARTMENT')"><span><span><s:text name="role.addDepartment"/><!-- 添加部门--></span></span></button></security:authorize>
						<security:authorize ifAnyGranted="standardRoleRemoveUser"><button  class='btn' onclick="opt('ADD_WORKGROUP')"><span><span><s:text name="role.addWorkGroup"/><!-- 添加工作组--></span></span></button></security:authorize>
					</s:if>
					<security:authorize ifAnyGranted="standardRoleRemoveUser"><button  class='btn' onclick="opt('REMOVE_ALL')"><span><span><s:text name="authorization.remove"/><!--移除--></span></span></button></security:authorize>
					<security:authorize ifAnyGranted="exportRole"><button  class='btn' onclick="exportRole()"><span><span><s:text name="user.export"/><!-- 导出 --></span></span></button></security:authorize>
					<security:authorize ifAnyGranted="authorization-standard-role-viewAuthority"><button  class='btn' onclick="viewAuthority()"><span><span><s:text name="authorization.showAuth"/><!-- 查看权限 --></span></span></button></security:authorize>
					</s:if>
					<s:else>
					<security:authorize ifAnyGranted="authorization-standard-role-viewAuthority"><button  class='btn' onclick="viewAuthority()"><span><span><s:text name="authorization.showAuth"/><!-- 查看权限 --></span></span></button></security:authorize>
					</s:else>
				</div>
				</aa:zone>
					
				<aa:zone name="acs_content">
				<div id="opt-content">
					<div id="message"><s:actionmessage theme="mytheme"/></div>
					<script type="text/javascript">
						setTimeout('$("#message").hide();',3000);
					</script>
					<form id="ajax_from" name="ajax_from" action="" method="post">
						<input type="hidden" id="businessSystemId" name="businessSystemId" value="${businessSystemId}" />
						<input type="hidden" id="roleId" name="roleId" value="${roleId}"/>
						<input type="hidden" id="id" name="id" value="${roleId}"/>
					</form>
					<input type="hidden" id="roleName" name="name" value=""/>
					<form  method="post" id="removeForm" name="removeForm" action="${acsCtx}/authorization/role-removeUsers.action">
					<input type="hidden" name="roleId" value="${roleId}">
						<div style="padding: 5px;">
							<table border="1" cellpadding="0" cellspacing="0" class="leadTable">
								<thead>
									<tr>
										<th style="text-align:left; padding-left:4px;"><input type="checkbox" id="acs_users" onclick="checkAll(this)"/><s:text name="user.user"/></th>
										<s:if test="!isAdminRole">
										<th style="text-align:left; padding-left:4px;"><input type="checkbox" id="acs_depts" onclick="checkAll(this)"/><s:text name="department.department"/></th>
										<th style="text-align:left; padding-left:4px;"><input type="checkbox" id="acs_workgroups" onclick="checkAll(this)"/><s:text name="workGroup.workGroup"/></th>
										</s:if>
									</tr>
								</thead>
								<tbody>
									<tr>
										<td valign="top">
											<ul style="list-style-type:none;margin:0;padding-left: 0px;">
											<s:iterator value="users">
											<li>
											<s:if test="defaultAdmin.contains(loginName)&&isAdminRole==true">
											   <input type="checkbox" disabled="disabled" aa="admin" name="userIds" value="${id}" />
											</s:if>
											<s:else>
											   <input type="checkbox" name="userIds" value="${id}" onclick="_click_one('acs_users','userIds');"/>
											</s:else>
											<span id="name_${id}">${name}<s:if test="containBranches"><c:if test="${id!=-1}">(${subCompanyName})</c:if></s:if></span>
											</li>
											</s:iterator>
											</ul>
										</td>
										<s:if test="!isAdminRole">
										<td>
											<ul style="list-style-type:none;margin:0;padding-left: 0px;">
											<s:iterator value="departments">
											<li>
											<input type="checkbox" name="departmentsIds" value="${id}" onclick="_click_one('acs_depts','departmentsIds');"/>
											<span>${name}<s:if test="containBranches&&!branch"><c:if test="${id!=-1}">(${subCompanyName})</c:if></s:if></span>
											</li>
											</s:iterator>
											</ul>
										</td>
										<td>
											<ul style="list-style-type:none;margin:0;padding-left: 0px;">
											<s:iterator value="workgroups">
											<li>
											<input type="checkbox" name="workGroupIds" value="${id}" onclick="_click_one('acs_workgroups','workGroupIds');"/>
											<span>${name}<s:if test="containBranches"><c:if test="${id!=-1}">(${subCompanyName})</c:if></s:if></span>
											</li>
											</s:iterator>
											</ul>
										</td>
										</s:if>
									</tr>
								</tbody>
							</table>
						</div>	
					</form>
				</div>
				<style type="text/css">.leadTable{ background-color: #FCFDFD; } .leadTable tbody tr:hover, .leadTable tbody td:hover a{ background-color: #FCFDFD; } </style>
				</aa:zone>
			</div>
</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script src="${resourcesCtx}/widgets/timepicker/timepicker_<%=com.norteksoft.product.util.ContextUtils.getCurrentLanguage()%>.js" type="text/javascript"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>
	