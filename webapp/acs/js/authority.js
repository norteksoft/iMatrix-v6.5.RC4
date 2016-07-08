var existable=false;
//数据授权是否存在
function isPermissionCodeExist(pos){
	var code = $("#code").val();
	if(code.indexOf("dataAuth-")==0&&code.length>27){
		iMatrix.alert(iMatrixMessage["permission.defaultCodeLonest27"]);
		return;
	}
	if(code.indexOf(" ")==0||code.lastIndexOf(" ")==code.length-1){//最前面有空格
		iMatrix.alert(iMatrixMessage["workgroup.codeNotContainsSpace"]);
		return;
	}
	if(code.indexOf(">")>=0||code.indexOf("<")>=0||code.indexOf("\"")>=0||code.indexOf("'")>=0
			||code.indexOf("/")>=0){//包含特殊字符
//		alert("编码不能包含符号>、<、\"、'、/");
		iMatrix.alert(iMatrixMessage["department.codeFail"]+">、<、\"、'、/");
		return;
	}
	$.ajax({
		   type: "POST",
		   url: "validate-permission-code.htm",
		   data: "permissionCode="+code+"&permissionId="+$($("form[id='viewSaveForm']").find("input[id='permissionId']")[0]).attr("value"),
		   success: function(msg){
			   if(msg=="true"){
				   existable=true;
				  //该授权编号已存在
				  iMatrix.alert(iMatrixMessage['dataAuth.codeIsExist']);
			   }else{
				   if(pos=="save"){
						savePermission();
				   }
				   existable=false;
			   }
		   }
	});
}

function selectListView(pos){
	var dataTableId = $("#dataTableId").val();
	if(pos=="fast"){
		if(dataTableId==""||typeof(dataTableId)=='undefined'){
			iMatrix.alert(iMatrixMessage['permission.selectDataTables']);//"请先选择数据表!"
		}else{
			init_colorbox(webRoot+"/authority/data-rule-selectListView.htm?dataTableId="+dataTableId,iMatrixMessage['permission.listTables'],600,500);
		}
	}else{
		var _dataRuleId = $("#_dataRuleId").val();
		if(_dataRuleId==""||typeof(_dataRuleId)=='undefined'){
			//alert("请先选择数据分类!");
			iMatrix.alert(iMatrixMessage['permission.selectDataTypes']);
		}else{
			init_colorbox(webRoot+"/authority/data-rule-selectListView.htm?dataTableId="+dataTableId,iMatrixMessage['permission.listTables'],600,500);
		}
	}
}

//全选
function selectAllPermission(obj, boxName){
	if($(obj).attr('checked')){
		$('input[name="'+boxName+'"]').attr('checked', 'checked');
	}else{
		$('input[name="'+boxName+'"]').attr('checked', '');
	}
	showSelectList();
}

function showSelectList(){
	if($("input[code='4']").attr("checked")||$("input[code='8']").attr("checked")){//当是修改[$("input[code='4']")]、删除权限[$("input[code='8']")]时,默认选中查看权限,并显示选择列表功能
		$("input[code='1']").attr("checked","checked");
		$(".listTd").css("display","block");
		$("#_list_id").html(iMatrixMessage["permission.listTables"]);
	}else{//当修改、删除权限均没有被选中时，只有查看权限时
		var searchChecked = $("input[code='1']").attr("checked");
		if(searchChecked){
			$(".listTd").css("display","block");
			$("#_list_id").html(iMatrixMessage["permission.listTables"]);
		}else{
			$(".listTd").css("display","none");
			$("#_list_id").html("");
			//选择的列表清空
			clearList();
		}
	}
}
//选择的列表清空
function clearList(){
	$("#listViewName").attr("value","");
	$("#listViewId").attr("value","");
}

function showImp(type){
	var deptChecked = $("#dept").attr("checked");
	if(deptChecked){
		$("#deptImp").css("display","block");
	}else{
		$("#deptImp").css("display","none");
	}
	settingConfigurableField(type);
}

function settingConfigurableField(type){
	var obj = $('input[name="dataRange"]:checked');
	if(obj.length == 1){
		var val = $(obj).val();
		if("MYSELF" == val || "CURRENT_DEPARTMENT" == val){
			$("#configurableFieldTr").show();
			$("#configurableFieldAnnotationTr").show();
		}else{
			$("#configurableFieldTr").hide();
			$("#configurableFieldAnnotationTr").hide();
		}
	}else{
		$("#configurableFieldTr").show();
		$("#configurableFieldAnnotationTr").show();
	}
	if("change" == type){
		$("#configurableField").val("");
	}
}

function getPointRole(){
	var roleid="";
	var rolename="";
	var rolenameArr = getSelectValue('name');//角色名称(系统名称/分支机构简称\名称)
	var roleidArr = getSelectValue('id');
	var typeArr = getSelectValue('type');
	if(rolenameArr.length>0){
		var rolenames = rolenameArr.join(",");
		for(var i=0;i<roleidArr.length;i++){
			if(typeArr[i]=="role"){
				if(roleid==""){
					roleid = roleidArr[i];
					rolename = rolenameArr[i];
				}else{
					roleid += ","+roleidArr[i];
					rolename +=  ","+rolenameArr[i];
				}
			}
		}
	}
	return [rolename,roleid];
}

