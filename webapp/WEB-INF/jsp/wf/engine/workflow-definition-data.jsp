<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/wf-taglibs.jsp"%>



<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title><s:text name="wf.workflow.management"/></title>
<%@ include file="/common/wf-iframe-meta.jsp"%>
<link rel="stylesheet" href="${resourcesCtx}/widgets/wfeditor/css/wf-html5.css" type="text/css" />
<script type="text/javascript" src="${resourcesCtx}/js/raphael-2.1.2.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/wfeditor/wfeditor.js"></script>

<script src="${wfCtx }/js/workflow-definition.js" type="text/javascript"></script>

<script src="${wfCtx }/js/util.js" type="text/javascript"></script>

<script src="${wfCtx }/js/workflow.js" type="text/javascript"></script>

<script src="${imatrixCtx}/widgets/workflowEditor/rightClick.js" type="text/javascript"></script>
<script type="text/javascript" src="${imatrixCtx}/widgets/workflowEditor/swfobject.js"></script>

<script type="text/javascript" src="${resourcesCtx}/js/staff-tree.js"></script>

<script type="text/javascript">
	function option(opt, id){
		if(opt == "add"){
			if($("#wf_type").val()==""){
				$("#message").html("<font class=\"onError\"><nobr>"+iMatrixMessage["wf.engine.selectSpecificType"]+"</nobr></font>");
				showMsg("message");
				return;
			}
			$("#wfd_Id").attr("value","");
			$.colorbox({href:webRoot+'/engine/workflow-definition-selectTemplate.htm?type='+$("#wf_type").val(),iframe:true, innerWidth:730, innerHeight:420,overlayClose:false,title:iMatrixMessage["wf.engine.selectTemplate"]});
		}else if(opt == "deploy"){
			var ids = jQuery("#main_table").getGridParam('selarrrow');
			if(ids==''){
				var arr = new Array();
				arr.push("大个");
				arr.push("中个");
				arr.push("小个");
				$("#message").html("<font class=\"onError\"><nobr>"+iMatrix.getText(iMatrixMessage["wf.engine.selectProcess"],arr)+"</nobr></font>");
				showMsg("message");
				return;
			}else if(ids.toString().indexOf(',')>0){
				$("#message").html("<font class=\"onError\"><nobr>"+iMatrixMessage["dataAuth.onlySelectOneData"]+"</nobr></font>");
				showMsg("message");
				return;
			}
			var state=$("#main_table").jqGrid('getCell',ids,"enable");
			var message = "";
			if(state=='DISABLE' || state=='DRAFT'){
				message=iMatrixMessage["pageManager.confirmEnable"];
			}else if(state=='ENABLE'){
				message=iMatrixMessage["pageManager.confirmDisable"];
			}
			iMatrix.confirm({
				message:message,
				confirmCallback:optionDeployOk,
				parameters:ids
			});
		}else if(opt == "delete"){
			var ids = jQuery("#main_table").getGridParam('selarrrow');
			if(ids==''){
				$("#message").html("<font class=\"onError\"><nobr>"+iMatrixMessage["wf.engine.selectProcess"]+"</nobr></font>");
				showMsg("message");
				return;
			}else{
				if(versionType=="online"){
					var state=$("#main_table").jqGrid('getCell',ids,"enable");
					if(state=='DRAFT'){
		                iMatrix.confirm({
		                	message:iMatrixMessage["deleteInfo"],
		                	confirmCallback:onlineOptionDeleteOk,
		                	parameters:ids
		                });
					}else{
						////alert("为确保系统的正常演示，当流程定义是启用或禁用状态时，屏蔽了【删除】功能");
						iMatrix.alert(iMatrixMessage["wf.shieldingDeleteFunction"]);
					}
				}else{
					iMatrix.confirm({
						message:iMatrixMessage["deleteInfo"],
						confirmCallback:optionDeleteOk,
						parameters:ids
					});
				}
			}
		}else if(opt == "update" || opt=="view"){
			$("#wfd_option").attr("value",opt);
			if(opt == "view"){
				$("#wfd_Id").attr("value", id);
				//ajaxSubmit("defaultForm","${wfCtx}/engine/workflow-definition-update.htm","wfd_main",showViewFlash);
				ajaxSubmit("defaultForm","${wfCtx}/engine/workflow-editor-input.htm","wfd_main",showWorkflowEditor);
			}else{
				var ids = jQuery("#main_table").getGridParam('selarrrow');
				if(ids==''){
					$("#message").html("<font class=\"onError\"><nobr>"+iMatrixMessage["wf.engine.selectProcess"]+"</nobr></font>");
					showMsg("message");
					return;
				}else if(ids.toString().indexOf(',')>0){
					$("#message").html("<font class=\"onError\"><nobr>"+iMatrixMessage["dataAuth.onlySelectOneData"]+"</nobr></font>");
					showMsg("message");
					return;
				}
				var enable = $("#enable_" + $("#wfd_Id").attr("value")).attr("value");
				if(enable == 2|| enable == 3){
					iMatrix.confirm({
						message:iMatrixMessage["wf.engine.processHasBeen"],
						confirmCallback:optionUpdateOrViewOk,
						parameters:ids
					});
				}else{
					$("#wfd_Id").attr("value", ids);
					//ajaxSubmit("defaultForm","${wfCtx}/engine/workflow-definition-update.htm","wfd_main",showUpdateFlash);
					ajaxSubmit("defaultForm","${wfCtx}/engine/workflow-editor-input.htm","wfd_main",showWorkflowEditor);
				}
			}
		}else if(opt == "updateBasic"){
			var ids = jQuery("#main_table").getGridParam('selarrrow');
			if(ids==''){
				$("#message").html("<font class=\"onError\"><nobr>"+iMatrixMessage["wf.engine.selectProcess"]+"</nobr></font>");
				showMsg("message");
				return;
			}else if(ids.toString().indexOf(',')>0){
				$("#message").html("<font class=\"onError\"><nobr>"+iMatrixMessage["dataAuth.onlySelectOneData"]+"</nobr></font>");
				showMsg("message");
				return;
			}
			if(versionType=="online"){
				var state=$("#main_table").jqGrid('getCell',ids,"enable");
				if(state=='ENABLE'||state=='DISABLE'){
					//alert("为确保系统的正常演示，当流程定义是启用或禁用状态时，屏蔽了【修改基本属性】功能");
					iMatrix.alert(iMatrixMessage["wf.shieldingModifyBasicPropertiesFunction"]);
				}else{
					$("#wfd_Id").attr("value", ids);
					ajaxSubmit("defaultForm","${wfCtx}/engine/workflow-definition-basic-input.htm","wfd_main",validateBasic);
				}
			}else{
					$("#wfd_Id").attr("value", ids);
					ajaxSubmit("defaultForm","${wfCtx}/engine/workflow-definition-basic-input.htm","wfd_main",validateBasic);
			}
		}else if(opt == "edit"){
			var version=$("#main_table").jqGrid('getCell',id,"fromVersion");
			var code=$("#main_table").jqGrid('getCell',id,"formCode");
			var screenWidth=screen.availWidth-12;
			var screenHeight=screen.availHeight-58;
			var win=window.open("${imatrixCtx}/mms/form/form-view-wfEditor.htm?code="+code+"&version="+version,"win","top=0,left=0,toolbar=no,location=no,directories=no,status=yes,menubar=no,scrollbars=false,resizable=no,width="+screenWidth+",height="+screenHeight);
		}
	}
	
	function optionUpdateOrViewOk(ids){
		$("#wfd_Id").attr("value", ids);
		ajaxSubmit("defaultForm","${wfCtx}/engine/workflow-definition-update.htm","wfd_main",showUpdateFlash);
	}
	
	function optionDeployOk(ids){
		$.post(webRoot+"/engine/workflow-definition-deploy.htm?wfdId="+ids, "", function(data) {
        	$("#message").html(data);
			showMsg("message");
			jQuery("#main_table").jqGrid().trigger("reloadGrid");
		});
	}
	
	function onlineOptionDeleteOk(ids){
		var prmt = '';
        for(var i=0;i<ids.length;i++){
            if(prmt != '') prmt += '&';
            prmt+=('wfdIds='+ids[i]);
        }
        $.post(webRoot+"/engine/workflow-definition-delete.htm?"+prmt, "", function(data) {
        	$("#message").html("<font class=\"onError\"><nobr>"+data+"</nobr></font>");
			showMsg("message");
			setPageState();
			jQuery("#main_table").jqGrid().trigger("reloadGrid");
		});
	}
	
	function optionDeleteOk(ids){
		var prmt = '';
        for(var i=0;i<ids.length;i++){
            if(prmt != '') prmt += '&';
            prmt+=('wfdIds='+ids[i]);
        }
        $.post(webRoot+"/engine/workflow-definition-delete.htm?"+prmt, "", function(data) {
        	$("#message").html("<font class=\"onError\"><nobr>"+data+"</nobr></font>");
			showMsg("message");
			setPageState();
			jQuery("#main_table").jqGrid().trigger("reloadGrid");
		});
	}
	function monitor(){
		var ids = jQuery("#main_table").getGridParam('selarrrow');
		if(ids==''){
			$("#message").html("<font class=\"onError\"><nobr>"+iMatrixMessage["wf.engine.selectProcess"]+"</nobr></font>");
			showMsg("message");
			return;
		}else if(ids.toString().indexOf(',')>0){
			$("#message").html("<font class=\"onError\"><nobr>"+iMatrixMessage["dataAuth.onlySelectOneData"]+"</nobr></font>");
			showMsg("message");
			return;
		}
		$("#wfdId").attr("value", ids);
		ajaxSubmit('wf_form', '${wfCtx}/engine/workflow-definition-monitor.htm', 'wfd_main',initBtnGroup); 
	}
	
	function intoInput(templateId,typeId){
		$("#templateId").attr("value",templateId);
		if(typeof(typeId)!="undefined"){
			$("#wf_type").attr("value",typeId);
		}
		//ajaxSubmit("defaultForm","${wfCtx}/engine/workflow-definition-input.htm","wfd_main",showFlash);
		ajaxSubmit("defaultForm","${wfCtx}/engine/workflow-editor-input.htm","wfd_main",showWorkflowEditor);
	}
	
	function createWorkflowDefinition(workflowDefinitionId){
		if(workflowDefinitionId==0){
			ajaxSubmit("defaultForm","${wfCtx}/engine/workflow-definition-input.htm","wfd_main",showFlash);
		}else{
			if($("#wfd_option").val() == "view"){
				ajaxSubmit("defaultForm","${wfCtx}/engine/workflow-definition-update.htm","wfd_main",showViewFlash);
			}else{
				$("#wfd_Id").attr("value", workflowDefinitionId);
				ajaxSubmit("defaultForm","${wfCtx}/engine/workflow-definition-update.htm","wfd_main",showUpdateFlash);
			}
		}
	}
	
	function showWorkflowEditor(){
		resizeFlasContent();
		parent.hideWestAndNorth();
	}

	function showFlash(){
		resizeFlasContent();
		parent.hideWestAndNorth();
		addSWf("add");
	}

	function showViewFlash(){
		$("#flashcontent").height($(window).height()+32);
		parent.hideWestAndNorth();
		addSWf("view");
	}

	function showUpdateFlash(){
		resizeFlasContent();
		parent.hideWestAndNorth();
		addSWf("update");
	}

	function resizeFlasContent(){
		$("#flashcontent").height($(window).height()+64);
	}

	function flexReturn(){
		goBackWfd("wfdForm","${wfCtx}/engine/workflow-definition-data.htm","wfd_main","wfdPage");
	}

	//返回调用,保持页数。
	function goBackWfd(form,url,zone,jemesaId){
		$("input[name='type']").attr('value',"${type}");
		ajaxSubmit(form, url, zone, goBackCallback);
	}
	
	function goBackCallback(){
		parent.showWestAndNorth();
	}

	function monitorGoBack(){
		ajaxSubmit("wf_form","${wfCtx}/engine/workflow-definition-data.htm","wf_definition");
	}

	function viewWorkflow(ts1,cellval,opts,rwdat,_act){
		var v="<a  href=\"#\" hidefocus=\"true\" onclick=\"option('view', "+opts.id+");\">" + ts1 + "</a>";
		return v;
	}

	function editWfForm(ts1,cellval,opts,rwdat,_act){
		var v=ts1+"<a  class=\"small-button-bg\" href=\"#\" hidefocus=\"true\" onclick=\"option('edit', "+opts.id+");\">" + "<span class='ui-icon ui-icon-pencil'></span>" + "</a>";
		return v;
	}

	//选择管理员
	function selectPerson(){
		var acsSystemUrl = "${wfCtx}";
//		popTree({ title :'选择人员',
//			innerWidth:'400',
//			treeType:'MAN_DEPARTMENT_TREE',
//			defaultTreeValue:'id',
//			leafPage:'false',
//			multiple:'false',
//			hiddenInputId:"adminLoginName",
//			showInputId:"adminName",
//			userWithoutDeptVisible:"true",
//			acsSystemUrl:imatrixRoot,
//			callBack:function(){selectPersonCallBack();}});
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
			                //showInput:"point_user",
			                //hiddenInput:"point_user_value",
			                append:false
				},
				callback: {
					onClose:function(){
						selectPersonCallBack();
					}
				}			
				};
			    popZtree(zTreeSetting);
	}

	function selectPersonCallBack(){
		var adminName = ztree.getName();
		$('#adminName').attr("value",adminName);
		$('#adminLoginName').attr("value",ztree.getLoginName());
		$('#adminId').attr("value",ztree.getId());
	}

	function validateBasic(){
		$.validator.addMethod("typeRequired", function(value, element) {
			var $element = $(element);
			if($element.val()=='请选择类型'||$element.val()==''||$element.val()=='0'){
				return false;
			}
			return true;
		}, iMatrixMessage["common.required"]);
		$.validator.addMethod("systemRequired", function(value, element) {
			var $element = $(element);
			if($element.val()=='请选择系统'||$element.val()==''||$element.val()=='0'){
				return false;
			}
			return true;
		}, iMatrixMessage["common.required"]);
		$.validator.addMethod("branchRequired", function(value, element) {
			var $element = $(element);
			if($element.val()=='请选择分支机构'||$element.val()==''||$element.val()=='0'){
				return false;
			}
			return true;
		}, iMatrixMessage["common.required"]);
		$("#inputForm").validate({
		submitHandler: function() {
			saveBasic();
		},
		rules: {
			name:"required",
			adminName:"required"
		},
		messages: {
			name:iMatrixMessage["common.required"],
			adminName:iMatrixMessage["common.required"]
		}
	});
	}

	function submitBasic(){
		$("#inputForm").submit();
	}

	function saveBasic(){
		ajaxSubmit("inputForm", "${wfCtx}/engine/workflow-definition-save-basic.htm", "wfd_main", saveFormCallBack);
	}

	function saveFormCallBack(){
		validateBasic();
		showMsg("message");
	}

	function basicGoBack(){
		ajaxSubmit("inputForm","${wfCtx}/engine/workflow-definition-data.htm","wfd_main");
	}
	
	function returnWorkflowDefinitionList(){
		setPageState();goBackWfd('wfdForm','${wfCtx}/engine/workflow-definition-data.htm','wfd_main','wfdPage');
	}
	
	function flexToJsVersion(){
		ajaxSubmit("defaultForm","${wfCtx}/engine/workflow-editor-input.htm","wfd_main",showWorkflowEditor);
	}
</script>
</head>
<body>
<div class="ui-layout-center">
<form id="defaultForm" action="" name="defaultForm" method="post">
	<input type="hidden" id="templateId" name="templateId" value=""/>
	<input id="wf_type" name="type" type="hidden" value="${type}"/>
	<input id="system_id" name="sysId" type="hidden" value="${sysId}"/>
	<input id="vertion_type" name="vertionType" type="hidden" value="${vertionType}"/>
	<input type="hidden" name="wfdId" id="wfd_Id" value="" />
	<input type="hidden" name="option" id="wfd_option" value="" />
</form>
<div class="opt-body">
	<aa:zone name="wfd_main">
		<div class="opt-btn">
			<button class='btn' onclick="iMatrix.showSearchDIV(this);" hidefocus="true"><span><span><s:text name="formManager.search"></s:text></span></span></button>
			<button class='btn' onclick="option('add');" hidefocus="true"><span><span><s:text name="menuManager.new"></s:text></span></span></button>
			<button class='btn' onclick="option('update');" hidefocus="true"><span><span><s:text name="menuManager.update"></s:text></span></span></button>
			<button class='btn' onclick="option('updateBasic');" hidefocus="true"><span><span><s:text name="wf.button.updateBaseAttr"></s:text></span></span></button>
			<button class='btn' onclick="option('delete');" hidefocus="true"><span><span><s:text name="menuManager.delete"></s:text></span></span></button>
			<button class='btn' onclick="option('deploy');" hidefocus="true"><span><span><s:text name="formManager.startOrForbidden"></s:text></span></span></button>
			<button class='btn' onclick="monitor();" hidefocus="true"><span><span><s:text name="wf.menu.processMonitoring"></s:text></span></span></button>
		</div>
		<div id="opt-content" >
		<div style="display: none;" id="message"><s:actionmessage theme="mytheme" /></div>
		<form id="wf_form" name="wf_form" method="post">
			<input type="hidden" name="wfdId" id="wfdId" value="" />
			<input type="hidden" name="type" id="type" value="${type}" />
			<input id="systemwf_id" name="sysId" type="hidden" value="${sysId}" />
            <input id="vertionwf_type" name="vertionType" value="${vertionType}" type="hidden"/>
		</form>
		<form name="dataForm" id="dataForm" method="post" action="">
			<form name="dataForm" id="dataForm" method="post" action="">
				<view:jqGrid url="${wfCtx}/engine/workflow-definition-data.htm?sysId=${sysId}&vertionType=${vertionType}&type=${type}" 
					pageName="wfdPage" code="WF_DEFINITION" gridId="main_table"></view:jqGrid>
			</form>
		</form>
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