//新加行初始化数据
function packagingOperator(dataType,conditionId,data){
	var event = '';
	var doubleClickEvent = '';
	var standardFieldEvent = '';//标准字段时的点击事件
	//配置枚举或者键值对的时候
	if(data!=""){
		if(conditionId.indexOf("new")>=0){
			standardFieldEvent = 'onclick="standardFieldConditionEvent(\''+conditionId+'\');"';
		}else{
			standardFieldEvent = 'onclick="standardFieldConditionEvent('+conditionId+');"';
		}
	}
	
	//添加选择相对条件事件
	if(conditionId.indexOf("new")>=0){
		doubleClickEvent = 'ondblclick="selectRelativeCondition(\''+conditionId+'\');"';
		event = 'onclick="conditionValueEvent(\''+conditionId+'\',\''+dataType+'\');"';
	}else{
		doubleClickEvent = 'ondblclick="selectRelativeCondition('+conditionId+');"';
		event = 'onclick="conditionValueEvent('+conditionId+',\''+dataType+'\');"';
	}
	
	var result='<option role="option" value="">'+iMatrixMessage["authorization.select"]+'</option>';
	var tdContent='';
	if(dataType == 'TEXT'){
		result+='<option value="ET">'+iMatrixMessage["authorization.eq"]+'</option><option value="CONTAIN">'+iMatrixMessage["authorization.contain"]+'</option><option value="NOT_CONTAIN">'+iMatrixMessage["authorization.notContain"]+'</option><option value="IS_NULL">'+iMatrixMessage["authorization.null"]+'</option><option value="NOT_NULL">'+iMatrixMessage["authorization.notNull"]+'</option>';
		tdContent='<input '+event+' id="'+conditionId+'_conditionName" class="editable" onkeyup="validateFieldString(this,\''+conditionId+'\');" type="text" name="conditionName" style="width: 98%;" role="textbox">';
	}else if(dataType == 'ENUM'){
		result+='<option value="ET">'+iMatrixMessage["authorization.eq"]+'</option><option value="NET">'+iMatrixMessage["authorization.noteq"]+'</option><option value="IS_NULL">'+iMatrixMessage["authorization.null"]+'</option><option value="NOT_NULL">'+iMatrixMessage["authorization.notNull"]+'</option>';
		tdContent='<input '+event+' id="'+conditionId+'_conditionName" class="editable"  type="text" name="conditionName" style="width: 98%;" role="textbox" onkeyup="setConditionValue(this,\''+conditionId+'\');">';
	}else if(dataType == 'BOOLEAN'){
		result+='<option value="ET">'+iMatrixMessage["authorization.eq"]+'</option><option value="IS_NULL">'+iMatrixMessage["authorization.null"]+'</option><option value="NOT_NULL">'+iMatrixMessage["authorization.notNull"]+'</option>';
		tdContent='<input '+event+' id="'+conditionId+'_conditionName" class="editable" onkeyup="validateFieldString(this);" type="text" name="conditionName" style="width: 98%;" role="textbox">';
	}else if(dataType == 'NUMBER' || dataType == 'AMOUNT' || dataType == 'DOUBLE' || dataType == 'FLOAT'){
		result+='<option value="ET">'+iMatrixMessage["authorization.eq"]+'</option><option value="GT">'+iMatrixMessage["authorization.gt"]+'</option><option value="LT">'+iMatrixMessage["authorization.lt"]+'</option><option value="GET">'+iMatrixMessage["authorization.gteq"]+'</option><option value="LET">'+iMatrixMessage["authorization.lteq"]+'</option><option value="NET">'+iMatrixMessage["authorization.noteq"]+'</option><option value="IS_NULL">'+iMatrixMessage["authorization.null"]+'</option><option value="NOT_NULL">'+iMatrixMessage["authorization.notNull"]+'</option>';
		tdContent='<input '+event+' id="'+conditionId+'_conditionName" class="editable" onkeyup="value=value.replace(/[^0-9\.]/g,\'\');" type="text" name="conditionName" style="width: 98%;" role="textbox">';
	}else if(dataType == 'INTEGER'){
		result+='<option value="ET">'+iMatrixMessage["authorization.eq"]+'</option><option value="GT">'+iMatrixMessage["authorization.gt"]+'</option><option value="LT">'+iMatrixMessage["authorization.lt"]+'</option><option value="GET">'+iMatrixMessage["authorization.gteq"]+'</option><option value="LET">'+iMatrixMessage["authorization.lteq"]+'</option><option value="NET">'+iMatrixMessage["authorization.noteq"]+'</option><option value="IS_NULL">'+iMatrixMessage["authorization.null"]+'</option><option value="NOT_NULL">'+iMatrixMessage["authorization.notNull"]+'</option>';
		tdContent='<input '+event+' id="'+conditionId+'_conditionName" class="editable" onkeyup="value=value.replace(/[^0-9]/g,\'\');" type="text" name="conditionName" style="width: 98%;" role="textbox">';
	}else if(dataType == 'DATE'){
		result+='<option value="ET">'+iMatrixMessage["authorization.eq"]+'</option><option value="GT">'+iMatrixMessage["authorization.gt"]+'</option><option value="LT">'+iMatrixMessage["authorization.lt"]+'</option><option value="GET">'+iMatrixMessage["authorization.gteq"]+'</option><option value="LET">'+iMatrixMessage["authorization.lteq"]+'</option><option value="IS_NULL">'+iMatrixMessage["authorization.null"]+'</option><option value="NOT_NULL">'+iMatrixMessage["authorization.notNull"]+'</option>';
		tdContent='<input '+event+' id="'+conditionId+'_conditionName" class="editable" readonly="readonly" type="text" name="conditionName" style="width: 98%;" role="textbox">';
	}else if(dataType == 'TIME'){
		result+='<option '+event+' value="ET">'+iMatrixMessage["authorization.eq"]+'</option><option value="GT">'+iMatrixMessage["authorization.gt"]+'</option><option value="LT">'+iMatrixMessage["authorization.lt"]+'</option><option value="GET">'+iMatrixMessage["authorization.gteq"]+'</option><option value="LET">'+iMatrixMessage["authorization.lteq"]+'</option><option value="IS_NULL">'+iMatrixMessage["authorization.null"]+'</option><option value="NOT_NULL">'+iMatrixMessage["authorization.notNull"]+'</option>';
		tdContent='<input id="'+conditionId+'_conditionName" readonly="readonly" class="editable" type="text" name="conditionName" style="width: 98%;" role="textbox">';
	}else if(dataType == 'LONG'){
		var field = window.parent.$("#conditionGrid").jqGrid('getCell',conditionId,"field");
		if(field.indexOf("~~")>=0){//如果是标准字段
			result+='<option value="ET">'+iMatrixMessage["authorization.eq"]+'</option><option value="NET">'+iMatrixMessage["authorization.noteq"]+'</option>';
			tdContent='<input ' +standardFieldEvent+'  id="'+conditionId+'_conditionName" class="editable" readonly="readonly"  type="text" name="conditionName" style="width: 98%;" role="textbox">';
		}else{//如果不是标准字段
			result+='<option value="ET">'+iMatrixMessage["authorization.eq"]+'</option><option value="GT">'+iMatrixMessage["authorization.gt"]+'</option><option value="LT">'+iMatrixMessage["authorization.lt"]+'</option><option value="GET">'+iMatrixMessage["authorization.gteq"]+'</option><option value="LET">'+iMatrixMessage["authorization.lteq"]+'</option><option value="NET">'+iMatrixMessage["authorization.noteq"]+'</option><option value="IS_NULL">'+iMatrixMessage["authorization.null"]+'</option><option value="NOT_NULL">'+iMatrixMessage["authorization.notNull"]+'</option>';
			tdContent='<input '+event+'  id="'+conditionId+'_conditionName" class="editable" onkeyup="value=value.replace(/[^0-9]/g,\'\');setConditionValue(this,\''+conditionId+'\');" type="text" name="conditionName" style="width: 98%;" role="textbox">';
		}
	}
	window.parent.$("#"+conditionId+"_operator").html(result);
	window.parent.$("#"+conditionId+"_conditionValue").attr("value","");
	window.parent.$("#"+conditionId+"_conditionName").parent().html(tdContent);
}
//当在conditionName中输入值时更新conditonValue的值
function setConditionValue(obj,rowId){
	$("#"+rowId+"_conditionValue").attr("value",$(obj).val());
}

//当是标准字段：创建人、创建人部门、创建人工作组、创建人角色时，单击Long类型的文本框时才会有该单击事件
function standardFieldConditionEvent(rowId){
	var field = jQuery("#conditionGrid").jqGrid('getCell',rowId,"field");
	$.colorbox({href:webRoot+"/authority/data-rule-tree-page.htm?standardField="+field+"&rowId="+rowId,iframe:true, innerWidth:500, innerHeight:320,overlayClose:false,title:iMatrixMessage["wf.engine.choose"]});
}

//修改数据类别初始化已有数据
function packagingOperatorUpdate(dataType,conditionId,tableId){
	var event = '';
	var doubleClickEvent = '';
	var standardFieldEvent = '';//标准字段时的点击事件
	//添加选择相对条件事件，配置枚举或者键值对的时候
	if(conditionId.indexOf("new")>=0){
		event = 'onclick="conditionValueEvent(\''+conditionId+'\',\''+dataType+'\');"';
		doubleClickEvent = 'ondblclick="selectRelativeCondition(\''+conditionId+'\');"';
		standardFieldEvent = 'onclick="standardFieldConditionEvent(\''+conditionId+'\');"';
	}else{
		event = 'onclick="conditionValueEvent('+conditionId+',\''+dataType+'\');"';
		doubleClickEvent = 'ondblclick="selectRelativeCondition('+conditionId+');"';
		standardFieldEvent = 'onclick="standardFieldConditionEvent('+conditionId+');"';
	}
	
	var result='<option role="option" value="">'+iMatrixMessage["authorization.select"]+'</option>';
	var tdContent='';
	if(dataType == 'TEXT'){
		result+='<option value="ET">'+iMatrixMessage["authorization.eq"]+'</option><option value="CONTAIN">'+iMatrixMessage["authorization.contain"]+'</option><option value="NOT_CONTAIN">'+iMatrixMessage["authorization.notContain"]+'</option><option value="IS_NULL">'+iMatrixMessage["authorization.null"]+'</option><option value="NOT_NULL">'+iMatrixMessage["authorization.notNull"]+'</option>';
		tdContent='<input '+event+'  id="'+conditionId+'_conditionName" class="editable" onkeyup="validateFieldString(this,'+conditionId+');" type="text" name="conditionName" style="width: 98%;" role="textbox">';
	}else if(dataType == 'ENUM'){
		result+='<option value="ET">'+iMatrixMessage["authorization.eq"]+'</option><option value="NET">'+iMatrixMessage["authorization.noteq"]+'</option><option value="IS_NULL">'+iMatrixMessage["authorization.null"]+'</option><option value="NOT_NULL">'+iMatrixMessage["authorization.notNull"]+'</option>';
		tdContent='<input '+event+' id="'+conditionId+'_conditionName" class="editable"  type="text" name="conditionName" style="width: 98%;" role="textbox">';
	}else if(dataType == 'BOOLEAN'){
		result+='<option value="ET">'+iMatrixMessage["authorization.eq"]+'</option><option value="IS_NULL">'+iMatrixMessage["authorization.null"]+'</option><option value="NOT_NULL">'+iMatrixMessage["authorization.notNull"]+'</option>';
		tdContent='<input '+event+' id="'+conditionId+'_conditionName" class="editable" onkeyup="validateFieldString(this);" type="text" name="conditionName" style="width: 98%;" role="textbox">';
	}else if(dataType == 'NUMBER' || dataType == 'AMOUNT' || dataType == 'DOUBLE' || dataType == 'FLOAT'){
		result+='<option '+event+' value="ET">'+iMatrixMessage["authorization.eq"]+'</option><option value="GT">'+iMatrixMessage["authorization.gt"]+'</option><option value="LT">'+iMatrixMessage["authorization.lt"]+'</option><option value="GET">'+iMatrixMessage["authorization.gteq"]+'</option><option value="LET">'+iMatrixMessage["authorization.lteq"]+'</option><option value="NET">'+iMatrixMessage["authorization.noteq"]+'</option><option value="IS_NULL">'+iMatrixMessage["authorization.null"]+'</option><option value="NOT_NULL">'+iMatrixMessage["authorization.notNull"]+'</option>';
		tdContent='<input id="'+conditionId+'_conditionName" class="editable" onkeyup="value=value.replace(/[^0-9\.]/g,\'\');" type="text" name="conditionName" style="width: 98%;" role="textbox">';
	}else if(dataType == 'INTEGER' ){
		result+='<option '+event+' value="ET">'+iMatrixMessage["authorization.eq"]+'</option><option value="GT">'+iMatrixMessage["authorization.gt"]+'</option><option value="LT">'+iMatrixMessage["authorization.lt"]+'</option><option value="GET">'+iMatrixMessage["authorization.gteq"]+'</option><option value="LET">'+iMatrixMessage["authorization.lteq"]+'</option><option value="NET">'+iMatrixMessage["authorization.noteq"]+'</option><option value="IS_NULL">'+iMatrixMessage["authorization.null"]+'</option><option value="NOT_NULL">'+iMatrixMessage["authorization.notNull"]+'</option>';
		tdContent='<input id="'+conditionId+'_conditionName" class="editable" onkeyup="value=value.replace(/[^0-9]/g,\'\');" type="text" name="conditionName" style="width: 98%;" role="textbox">';
	}else if(dataType == 'DATE'){
		result+='<option value="ET">'+iMatrixMessage["authorization.eq"]+'</option><option value="GT">'+iMatrixMessage["authorization.gt"]+'</option><option value="LT">'+iMatrixMessage["authorization.lt"]+'</option><option value="GET">'+iMatrixMessage["authorization.gteq"]+'</option><option value="LET">'+iMatrixMessage["authorization.lteq"]+'</option><option value="IS_NULL">'+iMatrixMessage["authorization.null"]+'</option><option value="NOT_NULL">'+iMatrixMessage["authorization.notNull"]+'</option>';
		tdContent='<input '+event+' id="'+conditionId+'_conditionName" class="editable" readonly="readonly" type="text" name="conditionName" style="width: 98%;" role="textbox"/>';
	}else if(dataType == 'TIME'){
		result+='<option value="ET">'+iMatrixMessage["authorization.eq"]+'</option><option value="GT">'+iMatrixMessage["authorization.gt"]+'</option><option value="LT">'+iMatrixMessage["authorization.lt"]+'</option><option value="GET">'+iMatrixMessage["authorization.gteq"]+'</option><option value="LET">'+iMatrixMessage["authorization.lteq"]+'</option><option value="IS_NULL">'+iMatrixMessage["authorization.null"]+'</option><option value="NOT_NULL">'+iMatrixMessage["authorization.notNull"]+'</option>';
		tdContent='<input '+event+' id="'+conditionId+'_conditionName" readonly="readonly" class="editable" type="text" name="conditionName" style="width: 98%;" role="textbox">';
	}else if(dataType == 'LONG'){
		var field = jQuery("#conditionGrid").jqGrid('getCell',conditionId,"field");
		if(field.indexOf("~~")>=0){//如果是标准字段
			result+='<option value="ET">'+iMatrixMessage["authorization.eq"]+'</option><option value="NET">'+iMatrixMessage["authorization.noteq"]+'</option>';
			tdContent='<input ' +standardFieldEvent+'  id="'+conditionId+'_conditionName" class="editable" readonly="readonly" type="text" name="conditionName" style="width: 98%;" role="textbox">';
		}else{//如果不是标准字段
			result+='<option value="ET">'+iMatrixMessage["authorization.eq"]+'</option><option value="GT">'+iMatrixMessage["authorization.gt"]+'</option><option value="LT">'+iMatrixMessage["authorization.lt"]+'</option><option value="GET">'+iMatrixMessage["authorization.gteq"]+'</option><option value="LET">'+iMatrixMessage["authorization.lteq"]+'</option><option value="NET">'+iMatrixMessage["authorization.noteq"]+'</option><option value="IS_NULL">'+iMatrixMessage["authorization.null"]+'</option><option value="NOT_NULL">'+iMatrixMessage["authorization.notNull"]+'</option>';
			tdContent='<input ' +event+'  id="'+conditionId+'_conditionName" class="editable" onkeyup="value=value.replace(/[^0-9]/g,\'\');" type="text" name="conditionName" style="width: 98%;" role="textbox">';
		}
	}
	var operator=$("#"+conditionId+"_operator").val();
	var conditionName=$("#"+conditionId+"_conditionName").val();
	$("#"+conditionId+"_operator").html(result);
	if(operator != '')
		$("#"+conditionId+"_operator").attr("value",operator);
	$("#"+conditionId+"_conditionName").parent().html(tdContent);
	if(conditionName != '')
		$("#"+conditionId+"_conditionName").attr("value",conditionName);
}


//弹框选值后，继续初始化列表事件
function setOperatorValue(conditionId,value,title){
	var event = '';
	if(conditionId.indexOf("new")>=0){
		event = 'onclick="conditionValueEvent(\''+conditionId+'\')";';
	}else{
		event = 'onclick="conditionValueEvent('+conditionId+')";';
	}
	var tdContent='<input '+event+' id="'+conditionId+'_conditionName" value="'+title+'"  class="editable" onkeyup="validateFieldString(this,'+conditionId+');" type="text" name="conditionName" style="width: 98%;" role="textbox">';
	window.parent.$("#"+conditionId+"_conditionValue").attr("value",value+"~~~~");//value带有~~~~:表示是在弹框中回填的值
	window.parent.$("#"+conditionId+"_conditionName").parent().html(tdContent);
}

//选择相对条件
function selectRelativeCondition(conditionId){
	var url = webRoot+'/authority/data-rule-selectRelativeCondition.htm';
	custom_ztree({url:url,
		onsuccess:function(){selectConditionCallBack(conditionId+"",getSelectValue('id'));},//回调方法
		width:500,
		height:320,
		title:'选择相对条件',
		nodeInfo:['type','code','name'],
		multiple:true,
		webRoot:imatrixRoot
	});
}

function selectConditionCallBack(conditionId,value){
	var codes = getSelectValue("code");
	var names = getSelectValue("name");
	var conditionValues = "";
	var conditionNames = "";
	if(codes!=""&&codes.length>0){
		for(var i=0;i<codes.length;i++){
			if(codes[i]!="root"){//不是根节点
				if(conditionValues==""){
					conditionValues += codes[i];
					conditionNames += names[i];
				}else{
					conditionValues += ","+codes[i];
					conditionNames += ","+names[i];
				}
			}
		}
		var doubleClickEvent = '';
		var event = '';
		if(conditionId.indexOf("new")>=0){
//			event = 'onclick="conditionValueEvent(\''+conditionId+'\')";';
			doubleClickEvent = 'ondblclick="selectRelativeCondition(\''+conditionId+'\')";';
		}else{
//			event = 'onclick="conditionValueEvent('+conditionId+')";';
			doubleClickEvent = 'ondblclick="selectRelativeCondition('+conditionId+')";';
		}
		var tdContent='<input '+event+" "+doubleClickEvent+' id="'+conditionId+'_conditionName" value="'+conditionNames+'"  class="editable" onkeyup="validateFieldString(this,'+conditionId+');" type="text" name="conditionName" style="width: 98%;" role="textbox">';
		$("#"+conditionId+"_conditionName").parent().html(tdContent);
		$("#"+conditionId+"_conditionValue").attr("value",conditionValues+"~~~~");//value带有~~~~:表示是在弹框中回填的值
	}else{
		iMatrix.alert(iMatrixMessage["authority.pleaseSelectStandardValue"]);
	}
}

