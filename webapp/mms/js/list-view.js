var saveSign=false;

function mypropertyname(value, colname){
	var data=$("#propertyGridId").jqGrid('getRowData');
	if(data.length==1&&saveSign&&value==""){
		saveSign=false;
		return [true,""];
	}else{
		if(value==""){
			return [false,colname+iMatrixMessage["list.view.isRequired"]];//是必填的!
		}else{
			var reservedProperties=['url','prmNames','gridComplete','colNames','colModel','rownumbers','onSelectRow','ondblClickRow','editurl','rowNum','rowList','multiselect','multiboxonly','pager','serializeRowData','postData'];
			for(var i=0;i<reservedProperties.length;i++){
				if(value==reservedProperties[i]){
					return [false,iMatrixMessage["list.view.reservedWordPleaseReEnter"]];//是保留字,请重新填写!
				}
			}
			return [true,""];
		}
	}
}

//页面验证
function  validate(){
	$("#viewSaveForm").validate({
		submitHandler: function() {
			var haveGroupHeader=$("#haveGroupHeader").val();
			if("yes"==$("#haveGroupHeader").val() && $("#frozenColumn").val()!=''){
				iMatrix.alert(iMatrixMessage["formManager.cancelFreezeValidate"]);
			}else{
				saveSign=true;
				var subTable=iMatrix.getFormGridDatas("viewSaveForm","propertyGridId");
				if(subTable){
					ajaxSubmit("viewSaveForm",  webRoot+"/form/list-view-save.htm", "viewList",saveViewCallBack);
				}
			}
		},
		rules: {
			code:"required",
			name: "required",
			remark:{
				maxlength:500
			},
			rowNum:{
				number:true
			},
			rowList:{maxlength:255},
			frozenNum:{number:true}
		},
		messages: {
			code:iMatrixMessage["menuManager.required"],
			name: iMatrixMessage["menuManager.required"],
			remark:{
				maxlength:iMatrixMessage["formManager.validateMaxLength"]
			},
			rowNum:{
				number:iMatrixMessage["menuManager.digitRequired"]
			},
			rowList:{maxlength:iMatrixMessage["formManager.validateDigit"]},
			frozenNum:{number:iMatrixMessage["menuManager.digitRequired"]}
		}
	});
	validateViewCode();
}

function saveViewCallBack(){
	showMsg();
	validate();
	$("#view_id1").attr("value",$("#viewId").val());//字段设置和按钮设置用到
	getContentHeight();
}

function saveView(){
	$("#dataTableId").attr("value",$("#dataTable").val());
	$('#viewSaveForm').submit();
}

function createView(){
	$("#view_id").attr("value","");
	ajaxSubmit("defaultForm",webRoot+"/form/list-view-input.htm", "viewList", validate);
}

function copyView(){
	var ids = jQuery("#page").getGridParam('selarrrow');
	if(ids==""){
		showMessage("message", "<font color=\"red\">"+iMatrixMessage.selectOneInfo+"</font>");
	}else if(ids.length > 1){
		showMessage("message", "<font color=\"red\">"+iMatrixMessage["common.pleaseSelectOne"]+"</font>");
	}else if(ids.length == 1){
		var meId = $("#menuId").val();
		$.colorbox({href:webRoot+"/form/list-view-copy.htm?menuId="+meId+"&viewId="+ids[0],iframe:true, innerWidth:400, innerHeight:300,overlayClose:false,title:iMatrixMessage["formManager.copyView"]});
	}
}

function updateViewCallBack(){
	HideSuperSearchBox();
	validate();
	$("#view_id1").attr("value",$("#viewId").val());//字段设置和按钮设置用到
	getContentHeight();
}

function updateView(id){
	if(versionType=="online"){
		if(parent.tree.getSelectNode().name.indexOf("开发案例")>0||(parent.tree.getSelectNode().name.indexOf("权限系统")<0&&parent.tree.getSelectNode().name.indexOf("工作流")<0
				&&parent.tree.getSelectNode().name.indexOf("门户")<0&&parent.tree.getSelectNode().name.indexOf("待办事宜")<0&&parent.tree.getSelectNode().name.indexOf("平台系统")<0
				&&parent.tree.getSelectNode().name.indexOf("系统元数据管理")<0&&parent.tree.getSelectNode().name.indexOf("基础设置系统")<0)){
			if(id!=""&&typeof(id)!='undefined'){
				$("#view_id").attr("value",id);
				ajaxSubmit("defaultForm",webRoot+"/form/list-view-input.htm", "viewList", updateViewCallBack);
			}else{
				var ids = jQuery("#page").getGridParam('selarrrow');
				if(ids==""){
					showMessage("message", "<font color=\"red\">"+iMatrixMessage["selectOneDataInfo"]+"</font>");
				}else if(ids.length > 1){
					showMessage("message", "<font color=\"red\">"+iMatrixMessage.selectOnlyOneInfo+"</font>");
				}else if(ids.length == 1){
					$("#view_id").attr("value",ids[0]);
					ajaxSubmit("defaultForm",webRoot+"/form/list-view-input.htm", "viewList,column", updateViewCallBack);
				}
			}
		}else{
			iMatrix.alert(iMatrixMessage["formManager.updateInfo"]);
		}
	}else{
		if(id!=""&&typeof(id)!='undefined'){
			$("#view_id").attr("value",id);
			ajaxSubmit("defaultForm",webRoot+"/form/list-view-input.htm", "viewList", updateViewCallBack);
		}else{
			var ids = jQuery("#page").getGridParam('selarrrow');
			if(ids==""){
				showMessage("message", "<font color=\"red\">"+iMatrixMessage["selectOneDataInfo"]+"</font>");
			}else if(ids.length > 1){
				showMessage("message", "<font color=\"red\">"+iMatrixMessage.selectOnlyOneInfo+"</font>");
			}else if(ids.length == 1){
				$("#view_id").attr("value",ids[0]);
				ajaxSubmit("defaultForm",webRoot+"/form/list-view-input.htm", "viewList,column", updateViewCallBack);
			}
		}
	}
}

function deleteViews(){
	var ids = jQuery("#page").getGridParam('selarrrow');
	if(ids==""){
		showMessage("message", "<font color=\"red\">"+iMatrixMessage.selectOneDataInfo+"</font>");
	}else if(ids.length >= 1){
		iMatrix.confirm({
			message:iMatrixMessage.deleteInfo,
			confirmCallback:deleteViewsOk,
			parameters:ids
		});
	}
}
function deleteViewsOk(ids){
	for(var i=0;i<ids.length;i++){
		if($("#viewIds").attr("value")==""){
			$("#viewIds").attr("value",ids[i]);
		}else{
			$("#viewIds").attr("value",$("#viewIds").attr("value")+","+ids[i]);
		}
	}
	setPageState();
	ajaxSubmit("defaultForm",webRoot+"/form/list-view-delete.htm", "viewTable",deleteCallBack);
}
function deleteEnableListView(){
	var ids = jQuery("#page").getGridParam('selarrrow');
	if(ids==""){
		showMessage("message", "<font color=\"red\">"+iMatrixMessage.selectInfo+"</font>");
	}else if(ids.length >= 1){
		iMatrix.confirm({
			message:iMatrixMessage["formManager.deleteTableInfo"],
			confirmCallback:deleteEnableListViewOk,
			parameters:ids
		});
	}
}

function deleteEnableListViewOk(ids){
	for(var i=0;i<ids.length;i++){
		if($("#viewIds").attr("value")==""){
			$("#viewIds").attr("value",ids[i]);
		}else{
			$("#viewIds").attr("value",$("#viewIds").attr("value")+","+ids[i]);
		}
	}
	var input = "<input type='hidden' name='deleteEnable' value='true' />";
	$("#defaultForm").append(input);
	ajaxSubmit("defaultForm",webRoot+"/form/list-view-delete.htm", "viewTable",deleteCallBack);
}
function deleteCallBack(){
	$("#viewIds").attr("value","");
	$("#deleteEnable").attr('value', 'false');
	showMsg();
}

//换页签
function changeViewSet(opt,obj){
	if($("#view_id1").attr("value")!=""){
		//selete(obj);
		if(opt=="basic"){
			ajaxSubmit("defaultForm1",webRoot+"/form/list-view-input.htm", "btnZone,viewZone",validate);
		}else if(opt=="column"){
			ajaxSubmit("defaultForm1",webRoot+"/form/list-column.htm", "btnZone,viewZone",listColumBack);
		}else{
			var frozenColumnAmount=$("#frozenColumnAmount").val();
			if(frozenColumnAmount=='' || frozenColumnAmount==0){
				ajaxSubmit("defaultForm1",webRoot+"/form/group-header.htm", "btnZone,viewZone",listHeaderBack);
			}else{
				iMatrix.alert(iMatrixMessage["formManager.cancelfreezeInfo"]);
			}
		}
	}else{
		iMatrix.alert(formManager.saveBaseInfo);
	}
}

function listColumBack(){
	setFormgridHeight('listColumnId',$(window).height()-140);
}

function listHeaderBack(){
	setFormgridHeight('groupHeaderGridId',$(window).height()-140);
}

function validateViewCode(){
	if($("#code").val()==0){
	$("#code").blur(function(){
		$.ajax({
			   type: "POST",
			   url: "list-view-validateCode.htm",
			   data: "myCode="+$("#code").attr("value")+"&viewId="+$("#viewId").attr("value"),
			   success: function(data){
			   		if(data=="true"){
			   			iMatrix.alert(iMatrixMessage.code+$("#code").attr("value")+iMatrixMessage.exist);
		   				$("#code").attr("value","");
		   				$("#code").focus();
			   		}
			   }
			}); 
	});
	}
}

function listViewBack(formId,url){
	goBack(formId,url, "viewList", "page");
}

function defaultDisplaySet(type){
	var ids = jQuery("#page").getGridParam('selarrrow');
	if(ids==""){
		iMatrix.alert(iMatrixMessage.selectOneDataInfo);
	}else if(ids.length == 1){
		$("#view_id").attr("value",ids[0]);
		ajaxSubmit("defaultForm",webRoot+"/form/list-view-defaultDisplaySet.htm", "viewTable",showMsg);
	}else{
		iMatrix.alert(iMatrixMessage.selectOnlyOneInfo);
	}
}

