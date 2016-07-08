//树跳转
function treechange(currentId){
	ajaxSubmit("defaultForm",webRoot+"/module/module-page.htm", "viewList");
}


//页面验证
function  validate(){
	$("#viewSaveForm").validate({
			submitHandler: function() {
				ajaxSubmit("viewSaveForm",  webRoot+"/module/module-page-save.htm", "pageTable",savePageCallBack);
			},
			rules: {
				code: "required",
				name:"required",
				viewName:"required"
				},
			messages: {
				code: iMatrixMessage["menuManager.required"],
				name:iMatrixMessage["menuManager.required"],
				viewName:iMatrixMessage["menuManager.required"]
			}
		});
	validatePageCode();
}

function savePageCallBack(){
	showMsg();
	validate();
	$("#page_id1").attr("value",$("#pageId").val());//字段设置和按钮设置用到
}

function savePage(){
	$('#viewSaveForm').submit();
}

function createView(){
	$("#page_id").attr("value","");
	ajaxSubmit("defaultForm",webRoot+"/module/module-page-input.htm", "pageTable", validate);
}

function updateViewCallBack(){
	validate();
	$("#page_id1").attr("value",$("#pageId").val());//字段设置和按钮设置用到
}

function updateView(id){
	if(id!=""&&typeof(id)!='undefined'){
		$("#page_id").attr("value",id);
		ajaxSubmit("defaultForm",webRoot+"/module/module-page-input.htm", "pageTable", updateViewCallBack);
	}else{
		var ids = jQuery("#pageTableId").getGridParam('selarrrow');
		if(ids.length==0){
			iMatrix.alert(iMatrixMessage.selectInfo);
		}else if(ids.length==1){
			$("#page_id").attr("value",ids[0]);
			ajaxSubmit("defaultForm",webRoot+"/module/module-page-input.htm", "pageTable", updateViewCallBack);
		}else{
			iMatrix.alert(iMatrixMessage.selectOneInfo);
		}
	}
}

function deleteViews(){
	var ids = jQuery("#pageTableId").getGridParam('selarrrow');
	if(ids==''){
		showMessage("message", "<font color=\"red\">"+iMatrixMessage.selectOneInfo+"</font>");
	}else{
		var canPost = true;
		if(versionType=="online"){
			$.each(ids, function(i){
				var id = ids[i];
				var state=jQuery("#pageTableId").jqGrid("getCell",id,"enableState");
				if(state!= "DRAFT"){
					canPost = false;
				}
			});
		}
		if(canPost){
			iMatrix.confirm({
				message:iMatrixMessage.deleteInfo,
				confirmCallback:deleteViewsOk
			});
		}else{
			iMatrix.alert(iMatrixMessage["formManager.deleteDraftInfo"]);
		}
	}
}
// deleteViewsOk为回调,目的就是模拟confirm弹出框
function deleteViewsOk(){
	var ids = jQuery("#pageTableId").getGridParam('selarrrow');
	$.each(ids, function(i){
		if($("#pageIds").attr("value")==""){
			$("#pageIds").attr("value",ids[i]);
		}else{
			$("#pageIds").attr("value",$("#pageIds").attr("value")+","+ids[i]);
		}
	});
	setPageState();
	ajaxSubmit("defaultForm",webRoot+"/module/module-page-delete.htm", "pageTablelist",deleteCallBack);
}

function deleteCallBack(){
	$("#pageIds").attr("value","");
	showMsg();
}

function defaultDisplaySet(type){
	var ids = jQuery("#pageTableId").getGridParam('selarrrow');
	if(ids==''){
		showMessage("message", "<font color=\"red\">"+iMatrixMessage.selectOneInfo+"</font>");
	}else if(ids.length==1){
		iMatrix.confirm({
			message:iMatrixMessage["formManager.setOrCancelInfo"],
			confirmCallback:defaultDisplaySetOk,
			parameters:ids
		});
	}else{
		showMessage("message", "<font color=\"red\">"+iMatrixMessage.selectOneInfo+"</font>");
	}
}

function defaultDisplaySetOk(ids){
	$("#page_id").attr("value",ids[0]);
	ajaxSubmit("defaultForm",webRoot+"/module/module-page-defaultDisplaySet.htm", "pageTablelist",showMsg);
}
function enabelSet(type){
	var ids = jQuery("#pageTableId").getGridParam('selarrrow');
	if(ids==''){
		showMessage("message", "<font color=\"red\">"+iMatrixMessage.selectOneInfo+"</font>");
	}else{
		if(versionType=="online"){
			if(ids.length==1){
				var state=$("#pageTableId").jqGrid('getCell',ids[0],"enableState");
				if(state=='DISABLE' || state=='DRAFT'){
					iMatrix.confirm({
						message:iMatrixMessage["pageManager.confirmStartInfo"],
						confirmCallback:onlineEnabelSetOk,
						parameters:ids
					});
				}else if(state=='ENABLE'){
					iMatrix.alert(iMatrixMessage["pageManager.confirmRunFInfo"]);
				}
			}else{
				iMatrix.alert(iMatrixMessage["pageManager.confirmRunInfo"]);
			}
		}else{
			iMatrix.confirm({
				message:iMatrixMessage["pageManager.confirmStartOrForbidden"],
				confirmCallback:enabelSetOk,
				parameters:ids
			});
		}
		
	}
}

function onlineEnabelSetOk(ids){
	$("#pageIds").attr("value",ids[0]);
	ajaxSubmit("defaultForm",webRoot+"/module/module-page-enableSet.htm", "pageTablelist",enabelSetallBack);
}
function enabelSetOk(ids){
	$.each(ids, function(i){
		if($("#pageIds").attr("value")==""){
			$("#pageIds").attr("value",ids[i]);
		}else{
			$("#pageIds").attr("value",$("#pageIds").attr("value")+","+ids[i]);
		}
	});
	ajaxSubmit("defaultForm",webRoot+"/module/module-page-enableSet.htm", "pageTablelist",enabelSetallBack);
}
function enabelSetallBack(){
	$("#pageIds").attr("value","");
	showMsg();
}

//换页签
function changePageLeaf(flag){
	var page_id1 = $("#page_id1").attr("value");
	if(flag=="basic"){
		$("#menu_id").attr("value",$("#menuId").attr("value"));
		ajaxSubmit("defaultForm1",webRoot+"/module/module-page-input.htm", "contentZone,btnZone",changeViewBasicCallBack);
	}else{
		if(page_id1!=""){
			ajaxSubmit("defaultForm1",webRoot+"/module/button.htm", "contentZone,btnZone",changeViewSetButtonCallBack);
		}else{
			showMessage("message", "<font color=\"red\">"+iMatrixMessage["pageManager.saveSetInfo"]+"</font>");
		}
	}
}
//通用消息提示
function showMessage(id, msg){
	if(msg != ""){
		$("#"+id).html(msg);
	}
	$("#"+id).show("show");
	setTimeout('$("#'+id+'").hide("show");',3000);
}

function changeViewBasicCallBack(){
	$("#pageId").attr("value",$("#page_id1").attr("value"));
	validate();
}

function changeViewSetButtonCallBack(){
	$("#menu_Id").attr("value",$("#menuId").attr("value"));
	//validateBtns();
}

function changeViewType(){
	  $("#__viewId").attr("value","");
	  $("#viewName").attr("value","");
}

function validatePageCode(){
	$("#code").blur(function(){
		$.ajax({
			   type: "POST",
			   url: "module-page-validateCode.htm",
			   data: "myCode="+$("#code").attr("value")+"&pageId="+$("#pageId").attr("value"),
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

function previewPage(){
	if($("#page_id1").attr("value")!=""){
		var win = window.open(webRoot+'/module/module-page-preview.htm?pageId='+$("#page_id1").attr("value"),'win',"top=0,left=0,toolbar=no,location=no,directories=no,status=yes,menubar=no,scrollbars=yes,resizable=no,width="+screen.availWidth+",height="+screen.availHeight);
	}else{
		iMatrix.alert(iMatrixMessage["pageManager.savePreviewInfo"]);
	}
	
}
function previewButton(){
	if($("#page_id1").attr("value")!=""){
		var win = window.open(webRoot+'/module/module-page-preview.htm?pageId='+$("#page_id1").attr("value")+'&fromBottonView=fromBottonView','win',"top=0,left=0,toolbar=no,location=no,directories=no,status=yes,menubar=no,scrollbars=yes,resizable=no,width="+screen.availWidth+",height="+screen.availHeight);
	}else{
		iMatrix.alert(iMatrixMessage["pageManager.savePreviewInfo"]);
	}
}

//返回到页面管理列表
function returnPageList(){
	ajaxSubmit("defaultForm",webRoot+"/module/module-page-list.htm","pageTable");
}

//button
//调用列表控件的行点击时间 命名规则:columnName+Click
function toPageNameClick(rowInputId){
	$.colorbox({href:webRoot+'/module/button-selectPage.htm?pageId='+$("#page_id1").attr("value")+'&menuId='+$("#menuId").attr("value")+'&rowid='+rowInputId,iframe:true, innerWidth:500, innerHeight:400,overlayClose:false,title:iMatrixMessage["pageManager.selectColorboxTitle"]});
}
function clearInput(rowInputId){
	$("input[id='"+rowInputId+"']").attr("value","");
	var rowid=rowInputId.substring(0,rowInputId.indexOf("_"));
	$("input[id='"+rowid+"_toPage']").attr("value","");
}
//自定义元素
function toPageNameElement (value, options) {
	var el="<input  type=\"text\" style=\"width:90px;\" value = \""+value+"\" onclick=\"toPageNameClick('"+options.id+"');\"></input><button onclick=\"clearInput('"+options.id+"');\" type=\"button\">"+iMatrixMessage["pageManager.clear"]+"</button>";
	return el;
  }
//自定义元素的值
function toPageNameValue(elem) {
    return $(elem).val();
  }

function addViewValues(pageId,viewCode,rowInputId){
	//rowInputId:rowid_属性名
	$("input[id='"+rowInputId+"']").attr("value",viewCode);
	var rowid=rowInputId.substring(0,rowInputId.lastIndexOf("_"));
	//给引用类型传id
	$("#"+rowid+"_toPage").attr("value",pageId);
}

//调用列表控件的行点击时间 命名规则:columnName+Click
function eventClick(obj){
var rowid = obj.rowid;
$.colorbox({href:webRoot+'/module/button-showEvent.htm?rowid='+rowid,iframe:true, innerWidth:500, innerHeight:400,overlayClose:false,title:""});
}

function addEvent(event,rowid){
	$("#"+rowid+"_event").attr("value",event);
}

function successSave(){
//列表组建需调的方法
	var result = iMatrix.getFormGridDatas("buttonSaveForm","buttonGrid");
	if(result){
		ajaxSubmit("buttonSaveForm",  webRoot+"/module/button-saveButtons.htm", "contentZone", saveButtonCallBack);
	}
}
function saveButtonCallBack(){
	showMsg();
}
//选择表单列表
function selectView(){
	custom_ztree({url:webRoot+'/module/module-page-showViews.htm?type='+$("#viewType").val()+"&menuId="+$("#menuId").val(),
		onsuccess:function(){closeFun();},
		width:300,
		height:300,
		title:iMatrixMessage["pageManager.customZtreeTitle"],
		webRoot:imatrixRoot,
		nodeInfo:['id','name']
	});
}

function closeFun(){
	$("#__viewId").attr("value",getSelectValue('id'));
	$("#viewName").attr("value",getSelectNodeTitle());
}