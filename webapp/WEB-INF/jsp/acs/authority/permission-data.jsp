<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/acs-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title><s:text name="data.range.data.authority"/></title>
	<%@ include file="/common/acs-iframe-meta.jsp"%>
	
	<script type="text/javascript" src="${resourcesCtx }/widgets/jstree/jquery.jstree.js"></script>
	<script src="${resourcesCtx}/js/staff-tree.js" type="text/javascript"></script>
	<script src="${resourcesCtx}/js/custom.tree.js" type="text/javascript"></script>
	<link   type="text/css" rel="stylesheet" href="${acsCtx}/css/custom.css" />
	<script src="${acsCtx}/js/authority.js" type="text/javascript"></script>

	
	<script type="text/javascript">
	function createPermission(){
		var dataRuleId=$("#dataRuleId").val();
		if(dataRuleId!='' && typeof (dataRuleId)!='undefined'){
			$("#entiyId").attr("value","");
			ajaxSubmit("defaultForm", "${acsCtx}/authority/permission-input.htm", "pageTable",validatePermission);
		}else{
			//请选择对应的数据分类!
			iMatrix.alert(iMatrixMessage["dataAuth.selectDataClassification"]);
		}
	}
	function validatePermission(){
		getContentHeight();
		$("#viewSaveForm").validate({
			submitHandler: function() {
				iMatrix.removeBtn();
				
				var checkedAuths=$("input[name='docAuthes']:checked");
				if(checkedAuths.length>0){
					if($("#allUser").attr("checked")){
						//清空设置人员的列表，否则无法保存
						$("#userSetGrid").html("");
							ajaxSubmit('viewSaveForm','${acsCtx}/authority/permission-save.htm','pageTablelist',saveCallback);
					}else if($("#userSet").attr("checked")){
						var cansave=iMatrix.getFormGridDatas("viewSaveForm","childGridId");
						if(cansave){
							var leftBrackets = jQuery("#childGridId").jqGrid("getCol","leftBracket");
							var rightBrackets = jQuery("#childGridId").jqGrid("getCol","rightBracket");
							var leftBracketCount = 0;
							var rightBracketCount = 0;
							$.each(leftBrackets,function(i){
								if(leftBrackets[i] == "LEFTSINGLE"){
									leftBracketCount+=1;
								}else if(leftBrackets[i] == "LEFTDOUBLE"){
									leftBracketCount+=2;
								}
							});
							$.each(rightBrackets,function(i){
								if(rightBrackets[i] == "RIGHTSINGLE"){
									rightBracketCount+=1;
								}else if(rightBrackets[i] == "RIGHTDOUBLE"){
									rightBracketCount+=2;
								}
							});
							if(leftBracketCount!=rightBracketCount){
								iMatrix.addBtn();
								//"‘左括号’与‘右括号’不匹配！"
								iMatrix.alert(iMatrixMessage['dataAuth.parenthesisDoesNotMatch']);
							}else{
								ajaxSubmit('viewSaveForm','${acsCtx}/authority/permission-save.htm','pageTablelist',saveCallback);
							}
						}else{
							iMatrix.addBtn();
						}
					}
				}else{
					iMatrix.addBtn();
					//"请选择操作权限"
					iMatrix.alert(iMatrixMessage['dataAuth.selectDataAuth']);
				}
			},
			rules: {
				code:"required",
				name:"required"
			},
			messages: {
				code:iMatrixMessage['common.required'],//"必填",
				name:iMatrixMessage['common.required']//"必填",
			}
		});
	}

	function saveCallback(){
		iMatrix.addBtn();
		showMsg();
		validatePermission();
	}
	function updatePermission(){
		var boxes = jQuery("#page").jqGrid("getGridParam",'selarrrow');
		if(boxes.length<=0){
			iMatrix.alert(iMatrixMessage["selectInfo"]);
			return;
		}else if(boxes.length>1){
			//只能选择一条记录
			iMatrix.alert(iMatrixMessage["dataAuth.onlySelectOneData"]);
			return;
		}else{
			$("#entiyId").attr("value",boxes[0]);
			ajaxSubmit("defaultForm", "${acsCtx}/authority/permission-input.htm", "pageTable",validatePermission);
		}
	}
	function deletePermission(){
		var boxes = jQuery("#page").jqGrid("getGridParam",'selarrrow');
		if(boxes.length<=0){
			//"请选择记录"
			iMatrix.alert(iMatrixMessage["selectInfo"]);
			return;
		}else{
			$("#ids").attr("value",boxes.join(','));
			ajaxSubmit("defaultForm", "${acsCtx}/authority/permission-delete.htm", "pageTablelist",showMsg);
		}
	}
	function savePermission(){
		$("#viewSaveForm").submit();
	}

	function backPage(){
		setPageState();
		var dataRuleId=$("#dataRuleId").val();
		if(dataRuleId!='' && typeof (dataRuleId)!='undefined'){
			ajaxSubmit("viewSaveForm", "${acsCtx}/authority/permission-data.htm", "pageTable");
		}else{
			$("#_dataRuleId").attr("value","");
			$("#_menuId").attr("value",$("#menuId").attr("value"));
			ajaxSubmit("viewSaveForm", "${acsCtx}/authority/permission-data.htm", "pageTable");
		}
	}

	function internationalizationNameClick(obj){
		conditionNameClick(obj);
	}
	function conditionNameClick(obj){
		var id = $("#_dataRuleId").attr("value");
		var itemType=$("#"+obj.rowid+"_itemType").val();
		if(itemType==""){
			//先选择条件类型！
			iMatrix.alert(iMatrixMessage["dataAuth.selectContionType"]);
			return;
		}
		if(itemType=="ALL_USER"){
			$("#"+obj.rowid+"_conditionValue").attr("value","ALL_USER");
			$("#"+obj.rowid+"_conditionName").attr("value","所有人");
		}else{
			if(itemType=="USER"){
				userTree(obj);
			}else if(itemType=="DEPARTMENT"){
				DeptTree(obj);
			}else if(itemType=="ROLE"){
				roleTree(obj);
				//$.colorbox({href:webRoot+"/authority/permission-item-tree-page.htm?itemType="+itemType+"&rowId="+obj.rowid,iframe:true, innerWidth:600, innerHeight:400,overlayClose:false,title:"选择人员"});
			}else if(itemType=="WORKGROUP"){
				workgroupTree(obj);
			}
		}

	}

	function userTree(obj){
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
					title: iMatrixMessage["user.selectStaff"],
					width: 300,
					height:400,
					url:imatrixRoot,
					showBranch:true
				},
				feedback:{
					enable: true,
	                append:false
				},
				callback: {
					onClose:function(){
						getUserInfo(obj);
					}
				}			
				};
			    popZtree(zTreeSetting);
			    
	}

	function getUserInfo(obj){
		var usernames = ztree.getNames();//用户姓名(部门/分支机构简称\名称)
		var userIds = ztree.getIds();//不是无部门人员时:userId~deptId;无部门人员时:userId
		var internationalizationName = usernames;
		if(usernames=="所有人员"){
			userIds="ALL_USER";
			usernames = "\\${common.allPersonnel}";
			internationalizationName = iMatrixMessage["common.allPersonnel"];
		}
		$("#"+obj.rowid+"_conditionName").attr("value",usernames);
		$("#"+obj.rowid+"_internationalizationName").attr("value",internationalizationName);
		$("#"+obj.rowid+"_conditionValue").attr("value",userIds);
	}
	function DeptTree(obj){
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
					chkboxType:"{'Y' : 's', 'N' : 's' }"
				},
				view: {
					title: iMatrixMessage["authorization.selectDep"],
					width: 300,
					height:400,
					url:imatrixRoot,
					showBranch:true
				},
				feedback:{
					enable: true,
	                append:false
				},
				callback: {
					onClose:function(){
						getDeptInfo(obj);
					}
				}			
				};
			    popZtree(zTreeSetting);
	}
	function getDeptInfo(obj){
		var deptnames = ztree.getDepartmentNames();//部门名称(分支机构简称\名称)
		var deptids = "";
		var internationalizationName = deptnames;
		if(deptnames=="所有部门"){
			deptids="ALL_DEPARTMENT";
			deptnames = "\\${common.allDepartment}";
			internationalizationName = iMatrixMessage["common.allDepartment"];
		}else{
			deptids = ztree.getDepartmentIds();
		}
		$("#"+obj.rowid+"_conditionName").attr("value",deptnames);
		$("#"+obj.rowid+"_internationalizationName").attr("value",internationalizationName);
		$("#"+obj.rowid+"_conditionValue").attr("value",deptids);
	}
	//选择具体角色
	function roleTree(obj){
		custom_ztree({url:webRoot+'/authority/select-role-tree.htm',
			onsuccess:function(){getRoleInfo(obj);},//回调方法
			width:500,
			height:400,
			title:iMatrixMessage['dataAuth.role'],//'角色',
			nodeInfo:['type','id','name'],
			multiple:true,
			webRoot:imatrixRoot
		});
		
	}

	function getRoleInfo(obj){
		var roleInfos = getPointRole();
		var rolename=roleInfos[0];//角色名称(系统名称/分支机构简称\名称)
		var roleid=roleInfos[1];
		var internationalizationName = rolename;
		$("#"+obj.rowid+"_conditionName").attr("value",rolename);
		$("#"+obj.rowid+"_internationalizationName").attr("value",internationalizationName);
		$("#"+obj.rowid+"_conditionValue").attr("value",roleid);
	}
	function workgroupTree(obj){
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
					chkboxType:"{'Y' : 's', 'N' : 's' }"
				},
				view: {
					title: iMatrixMessage['authorization.selectWorkGroup'],//"选择工作组",
					width: 300,
					height:400,
					url:imatrixRoot,
					showBranch:true
				},
				feedback:{
					enable: true,
	                append:false
				},
				callback: {
					onClose:function(){
						getWorkgroupInfo(obj);
					}
				}			
				};
			    popZtree(zTreeSetting);
			    
	}
	function getWorkgroupInfo(obj){
		var wgnames = ztree.getWorkGroupNames();//工作组名称(分支机构简称\名称)
		var wgids = "";
		var internationalizationName = wgnames;
		wgids = ztree.getWorkGroupIds();
		var wgidArr = wgids.split(",");
		if(wgnames=="所有工作组"){
			wgids="ALL_WORKGROUP";
			wgnames = "\\${common.allWorkgroup}";
			internationalizationName = iMatrixMessage["common.allWorkgroup"];
		}
		$("#"+obj.rowid+"_conditionName").attr("value",wgnames);
		$("#"+obj.rowid+"_internationalizationName").attr("value",internationalizationName);
		$("#"+obj.rowid+"_conditionValue").attr("value",wgids);
	}
	function itemTypeChange(obj){
		$("#"+obj.rowid+"_conditionValue").attr("value","");
		$("#"+obj.rowid+"_conditionName").attr("value","");
	}
	function getRoleInformation(obj){
		var ids=getSelectNodeId();
		if(ids!=""&&ids.length>0){
			var id=ids[0];
			var roleCode=id.substring(id.indexOf("-")+1,id.lastIndexOf("-"));
			var roleName=id.substring(id.lastIndexOf("-")+1);
			$("#"+obj.rowid+"_conditionValue").attr("value",roleCode);
			$("#"+obj.currentInputId).attr("value",roleName);
		}else{
			//"请选择角色");
			iMatrix.alert(iMatrixMessage['authorization.pleaseSelectRole']);
		}
	}

	function  validatePermissionSave(){
		if($("#code").attr("value")==""){
			savePermission();
		}else{
			if(existable){//当焦点在编号文本框时,点击保存会走2次isPermissionCodeExist方法,为了预防该种情况设置了该条件
				//该授权编号已存在
				iMatrix.alert(iMatrixMessage["dataAuth.codeIsExist"]);
			}else{
				isPermissionCodeExist("save");
			}
		}
	}

	function viewCondition(){
		var notSave=isHasEdit("childGridId");
		if(!notSave){
			init_colorbox(webRoot+"/authority/permission-viewCondition.htm",iMatrixMessage["dataAuth.showContion"],600,500);
		}
	}

	function settingUserType(){
		if($("#allUser").attr("checked")){//所有人
			$("#userSetGrid").css("display","none");
		}else if($("#userSet").attr("checked")){//不是所有人
			$("#userSetGrid").css("display","block");
		}
	}
	</script>
	<style type="text/css">
	.form-table-without-border td input{
		width:200px;
	}
	</style>
</head>
<body >
<div class="ui-layout-center">
<div class="opt-body">
	<form id="defaultForm" name="defaultForm"action="" method="post" >
			<input type="hidden" id="menuId"  name="sysMenuId" value="${sysMenuId }"/>
			<input type="hidden" id="dataRuleId"  name="dataRuleId" value="${dataRuleId }"/>
			<input type="hidden" id="entiyId"  name="permissionId" />
			<input type="hidden" id="ids"  name="ids" />
			<input type="hidden" id="hasBranch"  name="hasBranch" value="${hasBranch }"/>
		</form>
	<aa:zone name="pageTable">
		<div class="opt-btn">
			<button class="btn" onclick="iMatrix.showSearchDIV(this);"><span><span><s:text name="common.search"/></span></span></button>
			<button class="btn" onclick='createPermission();' id="create"><span><span ><s:text name="common.create"/></span></span></button>
			<button class="btn" onclick="updatePermission();"><span><span ><s:text name="permission.authorize.update"/></span></span></button>
			<button class="btn" onclick="deletePermission();"><span><span ><s:text name="common.delete"/></span></span></button>
		</div>
		<aa:zone name="pageTablelist">
			<div id="opt-content">
				<div id="message" style="display:none;"><s:actionmessage theme="mytheme" /></div>
				<form action="${acsCtx}/authority/permission-data.htm" name="pageForm" id="pageForm" method="post">
					<view:jqGrid url="${acsCtx}/authority/permission-data.htm?sysMenuId=${sysMenuId }&dataRuleId=${dataRuleId }" code="ACS_PERMISSION" gridId="page" pageName="page"></view:jqGrid>
				</form>
			</div>
		</aa:zone>
	</aa:zone>
</div>
</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>
