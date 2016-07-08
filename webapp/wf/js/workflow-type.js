/************************************************************
 模块名称: 流程类型主页面对应js
 创 建 者：吴荣
 说    明: 该js文件名和jsp主页文件名相同
 ************************************************************/
function treechange(id){
	ajaxSubmit('defaultForm',webRoot +'/engine/workflow-type.htm', 'wf_type'); 
}

function option(opt){
	var ids = jQuery("#main_table").getGridParam('selarrrow');
	if(opt == "add"){
		ajaxAnyWhereSubmit("wf_type_form",webRoot+"/engine/workflow-type-input.htm","wf_type",validate);
	}else if(opt == "update"){
		//var rds = $("input[name='wf_type_rd']:checked");
		if(ids==''){
			iMatrix.alert(iMatrixMessage["selectInfo"]);
			return;
		}else if(ids.length>=2){
			iMatrix.alert(iMatrixMessage["dataAuth.onlySelectOneData"]);
			return;
		}else{
			$("#wftypeId").attr("value",ids);
			ajaxAnyWhereSubmit("wf_type_form",webRoot+"/engine/workflow-type-input.htm","wf_type",validate);
		}
	}else if(opt == "delete"){
		if(ids==''){
			iMatrix.alert(iMatrixMessage["selectInfo"]);
			return;
		}else{
			iMatrix.confirm({
				message:iMatrixMessage["deleteInfo"],
				confirmCallback:optionOk,
				parameters:ids
			});
		}
	}
}
function optionOk(ids){
	for(var i=0; i<ids.length; i++){
		$("#wf_type_form").html($("#wf_type_form").html()+'<input type="hidden" name="typeIds" value="'+ids[i]+'">');
	}
	setPageState();
	ajaxAnyWhereSubmit("wf_type_form",webRoot+"/engine/workflow-type-delete.htm","wf_type",deleteCallBack,"page");
}
function deleteCallBack(arg){
	showMsg();
	//resetJmessaPageNo(arg);
}
function save_form(){
	$("#inputForm").submit();
	
}

//保存
function ajax_submit_form(){
	$.ajax({
		data:{code:$("#code").val(),id:$("#id").val()},
		cache:false,
		type:"post",
		url:webRoot+"/engine/workflow-type-validateCode.htm",
		success:function(data){
			if(data=="ok"){
				ajaxAnyWhereSubmit('inputForm', '', 'wf_type', saveCallback);
			}else{
				iMatrix.alert(iMatrixMessage["wf.engine.processTypeEncoding"]);
			}
		}
    });
	
}
function saveCallback(){
	validate();
	showMsg();
}

//页面验证
function  validate(){
	$("#inputForm").validate({
		submitHandler: function() {
			ajax_submit_form();
		},
		rules: {
			code: "required",
			name:"required"
		},
		messages: {
			code: iMatrixMessage["common.required"],
			name:iMatrixMessage["common.required"]
		}
	});
 }

function validateReadio(){
	var rds = $("input[name='typeIds']");
	for(var i = 0; i < rds.length; i++){
		if($(rds[i]).attr("checked") || $(rds[i]).attr("checked") == "checked"){
			$("#wftypeId").attr("value", $(rds[i]).attr("value"));
			return true;
		}	
	}
	return false;
}
function jmesaSubmit(){
	ajaxAnyWhereSubmit("wf_type_form",webRoot+"/engine/workflow-type.htm","wf_type");
}
function wfTypeBack(form,url,zoons){
	ajaxSubmit(form,url,zoons); 
}
