function doQuery(obj){
		if($(obj).html() == "查询"){
			$(obj).html('取消查询');

			$('#queryDiv').css('display', 'block');
		}else{
			$(obj).html('查询');
			$("#hiddenDiv").html("");
			$('#queryDiv').css('display', 'none');
		}
	}
	function fieldCng(obj){
		var typeValue = $(obj).attr('value').split('-');
		if(typeValue == ''){
			$('#cdsOpt').html('');
			$('#cdtSpan').html('');
			return;
		}
		if(typeValue[0]=='TEXT'){
			$('#cdsOpt').html('<option value="=">等于</option><option value="<>">不等于</option>');
			$('#cdtSpan').html('<input id="condition" type="text"/>');
		}else if(typeValue[0]=='DATE'){
			$('#cdsOpt').html('<option value=">">大于</option><option value="<">小于</option><option value="=">等于</option>');
			$('#cdtSpan').html('<input id="condition" type="text" readonly="readonly"  onfocus="WdatePicker({dateFmt:\'yyyy-MM-dd\',el:\'condition\'})"/>');
		}else if(typeValue[0]=='TIME'){
			$('#cdsOpt').html('<option value=">">大于</option><option value="<">小于</option><option value="=">等于</option>');
			$('#cdtSpan').html('<input id="condition" type="text" readonly="readonly" onfocus="WdatePicker({dateFmt:\'yyyy-MM-dd HH:mm\',el:\'condition\'})" />');
		}else {
			$('#cdsOpt').html('<option value="=">等于</option><option value=">">大于</option><option value="<">小于</option>');
			$('#cdtSpan').html('<input id="condition" type="text"  onkeyup="value=value.replace(/[^0-9\.]/g,\'\');"/>');
		}
		
	}
	function addCds(){
		if(typeof($('#condition').attr('value'))=='undefined' || $('#condition').attr('value') == ''){
			$("#message").html("<font class=\"onError\"><nobr>请输入条件</nobr></font>");
			showMsg("message");
			return;
		}
		var addCdn = $('#fieldCng').attr('value').split('-')[1] + '  ' +
					$('#cdsOpt').attr('value') +  '  ' +
					$('#condition').attr('value');
		var tr = '<tr class="str">' 
				+'<td style="border: 0px;">'+getHiddenInput('fieldIds',$('#fieldCng').attr('value').split('-')[1]) + $('#fieldCng option:selected').text() +'</td>'
				+'<td style="border: 0px;">'+getHiddenInput('operates',$('#cdsOpt').attr('value')) + $('#cdsOpt option:selected').text() +'</td>'
				+'<td style="border: 0px;">'+getHiddenInput('searchValues',$('#condition').attr('value')) + getEsc($('#condition').attr('value')) +'</td>'
				+'<TD style="border: 0px;"><a class="delete" href="#" >删除</a></TD>';
		
		$(tr).appendTo("#conditiontable").find(".delete").click(function(){
		             $(this).parent().parent().remove(); });
	}
	function getHiddenInput(name,value){
		return '<input type="hidden" name="'+name+'" value="'+value+'"/>';
	}

	function searchForm(){
		ajaxSubmit('search_form',webRoot+'/engine/workflow-definition-search.htm','monitorList,totalMessage');
	}

	//更改办理人
	function changeTransactor(btnId){
		var ids = jQuery("#main_table").getGridParam('selarrrow');
		if(ids==''){
			$("#message").html("<font class=\"onError\"><nobr>"+iMatrixMessage["selectOneInfo"]+"</nobr></font>");
			showMsg("message");
		}else if(ids.length > 1){
			$("#message").html("<font class=\"onError\"><nobr>"+iMatrixMessage["dataAuth.onlySelectOneData"]+"</nobr></font>");
			showMsg("message");
		}else{
			var workflowId = ids[0];
			var rowData=jQuery("#main_table").jqGrid('getRowData',workflowId );
			var state=rowData['processState'];
			if(state=="SUBMIT"){
				$.colorbox({href:webRoot+'/engine/task-changeTransactor.htm?instanceId='+workflowId,iframe:true, width:400, height:500,overlayClose:false,title:iMatrixMessage["wf.engine.choose"],onClosed:function(){$("#"+btnId).removeClass("cboxElement");}});
			}else{
				iMatrix.alert(iMatrixMessage["wf.engine.processHasChange"]);
			}
		}
	}
	//环节跳转/弹窗“选择环节”
	function backView(btnId,pos){
		var ids = jQuery("#main_table").getGridParam('selarrrow');
		if(ids==''){
			$("#message").html("<font class=\"onError\"><nobr>"+iMatrixMessage["selectOneInfo"]+"</nobr></font>");
			showMsg("message");
		}else if(ids.length > 1){
			$("#message").html("<font class=\"onError\"><nobr>"+iMatrixMessage["dataAuth.onlySelectOneData"]+"</nobr></font>");
			showMsg("message");
		}else{
			var workflowId = ids[0];//$(workflowIds[0]).attr("value");
			if(pos=="monitorManager"){
				$.colorbox({href:webRoot+'/engine/task-backView.htm?instanceId='+workflowId+'&type='+$("#wf_type1").attr("value")+'&definitionCode='+$("#wf_name1").attr("value")+'&position='+pos,iframe:true, width:300, height:400,overlayClose:false,title:iMatrixMessage["wf.engine.choose"],onClosed:function(){$("#"+btnId).removeClass("cboxElement");}});
			}else{
				$.colorbox({href:webRoot+'/engine/task-backView.htm?instanceId='+workflowId+'&position='+pos,iframe:true, width:300, height:400,overlayClose:false,title:iMatrixMessage["wf.engine.choose"],onClosed:function(){$("#"+btnId).removeClass("cboxElement");}});
			}
		}
	}
	//批量环节跳转/弹窗“选择环节”
	function volumeBackView(btnId){
		var ids = jQuery("#main_table").getGridParam('selarrrow');
		if(ids==''){
			$("#message").html("<font class=\"onError\"><nobr>"+iMatrixMessage["selectInfo"]+"</nobr></font>");
			showMsg("message");
		}else{
			$.colorbox({href:webRoot+'/engine/task-volumeBackView.htm?instanceIds='+ids+'&position=monitor',iframe:true, width:300, height:400,overlayClose:false,title:iMatrixMessage["wf.engine.choose"],onClosed:function(){$("#"+btnId).removeClass("cboxElement");}});
		}
	}
	
	//删除监控实例
	function delete_monitor_workflow(position){
		var ids = jQuery("#main_table").getGridParam('selarrrow');
		if(ids==''){
			$("#message").html("<font class=\"onError\"><nobr>"+iMatrixMessage["selectInfo"]+"</nobr></font>");
			showMsg("message");
		}else{
			iMatrix.confirm({
				message:iMatrixMessage["wf.engine.areYouSure"],
				confirmCallback:delete_monitor_workflowOk,
				parameters:{ids:ids,position:position}
			});
		}
	}
	function delete_monitor_workflowOk(obj){
		if($("#_loading_div_id").length==0){
			$(".opt-btn").append('<div id="_loading_div_id" class="loading">'+iMatrixMessage["log.nowDelete"]+'</div>');
		}
		$("#_loading_div_id").show();
		var prmt = '';
        for(var i=0;i<obj.ids.length;i++){
            if(prmt != '') prmt += '&';
            prmt+=('workflowIds='+obj.ids[i]);
        }
		$("#position").attr("value",obj.position);
        $.post(webRoot+"/engine/workflow-definition-deleteWorkflow.htm?"+prmt, "", function(data) {
        	$("#_loading_div_id").hide();
        	$("#message").html("<font class=\"onError\"><nobr>"+data+"</nobr></font>");
			showMsg("message");
			//setPageState();
			jQuery("#main_table").jqGrid().trigger("reloadGrid");
		});
	}
	//删除历史监控实例
	function delete_monitor_workflow_history(){
		var ids = jQuery("#main_table").getGridParam('selarrrow');

		if(ids==''){
			$("#message").html("<font class=\"onError\"><nobr>"+iMatrixMessage["selectInfo"]+"</nobr></font>");
			showMsg("message");
		}else{
			iMatrix.confirm({
				message:iMatrixMessage["wf.engine.areYouSure"],
				confirmCallback:delete_monitor_workflow_historyOk,
				parameters:ids
			});
		}
	}
	function delete_monitor_workflow_historyOk(ids){
		if($("#_loading_div_id").length==0){
			$(".opt-btn").append('<div id="_loading_div_id" class="loading">'+iMatrixMessage["log.nowDelete"]+'</div>');
		}
		$("#_loading_div_id").show();
		var prmt = '';
        for(var i=0;i<ids.length;i++){
            if(prmt != '') prmt += '&';
            prmt+=('workflowIds='+ids[i]);
        }
        $.post(webRoot+"/engine/workflow-definition-deleteWorkflowHistory.htm?"+prmt, "", function(data) {
        	$("#_loading_div_id").hide();
        	$("#message").html("<font class=\"onError\"><nobr>"+data+"</nobr></font>");
			showMsg("message");
			jQuery("#main_table").jqGrid().trigger("reloadGrid");
		});
	}
	//环节跳转/弹窗的关闭时的回调方法
	function backViewClose(wfdId,pos,type,defCode){
		if(pos=="monitor"){
			setPageState();
			ajaxSubmit('wf_form', webRoot+'/engine/workflow-definition-monitor.htm?wfdId='+wfdId, 'monitorList'); 
		}else if(pos=="monitorManager"){
			setPageState();
			$("#wf_type1").attr('value', type);
			$("#wf_name1").attr('value', defCode);
			ajaxSubmit('wf_form', webRoot+'/engine/workflow-definition-monitorDefintion.htm', 'monitorList'); 
		}
	}
	//取消流程
	function end_workflow_def(){
		var ids = jQuery("#main_table").getGridParam('selarrrow');
		if(ids==''){
			$("#message").html("<font class=\"onError\"><nobr>"+iMatrixMessage["selectInfo"]+"</nobr></font>");
			showMsg("message");
			return;
		}else{
			iMatrix.confirm({
				message:iMatrixMessage["wf.engine.doYouConfirm"],
				confirmCallback:end_workflow_defOk,
				parameters:ids
			});
		}
	}
	function end_workflow_defOk(ids){
		var prmt = '';
        for(var i=0;i<ids.length;i++){
            if(prmt != '') prmt += '&';
            prmt+=('workflowIds='+ids[i]);
        }
        $.post(webRoot+"/engine/workflow-definition-endWorkflow.htm?"+prmt, "", function(data) {
        	$("#message").html("<font class=\"onError\"><nobr>"+data+"</nobr></font>");
			showMsg("message");
			jQuery("#main_table").jqGrid().trigger("reloadGrid");
		});
	}
	//强制结束
	function compel_end_workflow_def(){
		var ids = jQuery("#main_table").getGridParam('selarrrow');
		if(ids==''){
			$("#message").html("<font class=\"onError\"><nobr>"+iMatrixMessage["selectInfo"]+"</nobr></font>");
			showMsg("message");
			return;
		}else{
			iMatrix.confirm({
				message:iMatrixMessage["wf.engine.confirmTheMandatory"],
				confirmCallback:compel_end_workflow_defOk,
				parameters:ids
			});
		}
	}
	function compel_end_workflow_defOk(ids){
		var prmt = '';
		for(var i=0;i<ids.length;i++){
			if(prmt != '') prmt += '&';
			prmt+=('workflowIds='+ids[i]);
		}
		$.post(webRoot+"/engine/workflow-definition-compelEndWorkflow.htm?"+prmt, "", function(data) {
			$("#message").html("<font class=\"onSuccess\"><nobr>"+data+"</nobr></font>");
			showMsg("message");
			jQuery("#main_table").jqGrid().trigger("reloadGrid");
		});
	}
	/**
	 * 流程监控/查看表单
	 * @param url
	 * @param zone
	 * @param formName
	 * @param obj
	 * @return
	 */
	function selectList(url,zone,formName,obj){
		selete(obj);
		ajaxSubmit(formName,url,zone,automaticHeight);
	}
	
	function view_standard_form(instanceId,url){
		var screenWidth=screen.availWidth-12;
		var screenHeight=screen.availHeight-58;
		if(url==""||typeof (url)=="undefined"){//查看自定义表单
			url=webRoot+'/engine/workflow-monitorView.htm?workflowId='+workflowId;
			var childWin = window.open(url,'childWin',"top=0,left=0,toolbar=no,location=no,directories=no,status=yes,menubar=no,scrollbars=false,resizable=no,width="+screenWidth+",height="+screenHeight);
		}else{
			var win = window.open(webRoot+"/engine/monitor-view.htm?instanceId="+instanceId+"&operationName=view",'win',"top=0,left=0,toolbar=no,location=no,directories=no,status=yes,menubar=no,scrollbars=false,resizable=no,width="+screenWidth+",height="+screenHeight);
		}
	}
	
	function view_standard_form_history(instanceId,url){
		var screenWidth=screen.availWidth-12;
		var screenHeight=screen.availHeight-58;
		if(url==""||typeof (url)=="undefined"){//查看自定义表单
			url=webRoot+'/engine/workflow!monitorHistoryView.htm?workflowId='+workflowId;
			var childWin = window.open(url,'childWin',"top=0,left=0,toolbar=no,location=no,directories=no,status=yes,menubar=no,scrollbars=yes,resizable=yes,width="+screenWidth+",height="+screenHeight);
		}else{
			var win = window.open(webRoot+"/engine/monitor-view-history.htm?instanceId="+instanceId+"&operationName=view",'win',"top=0,left=0,toolbar=no,location=no,directories=no,status=yes,menubar=no,scrollbars=false,resizable=no,width="+screenWidth+",height="+screenHeight);
		}
	}
	
	//根据系统code获取url
	function processUrl(url){
		if(url.indexOf("http://")!=-1){
		}else{
			var index = url.indexOf("/");
			var code = url.substring(0, index);
			$.ajax({
				   type: "POST",
				   url: "obtain-system-url.htm",
				   data: "systemCode="+code,
				   success: function(systemUrl){
					if(systemUrl==""||typeof(systemUrl)=='undefined'){
						iMatrix.alert(iMatrixMessage["wf.engine.pathIsNotCorrect"]);
					}else{
						var screenWidth=screen.availWidth-12;
						var screenHeight=screen.availHeight-58;
					    url = systemUrl + url.substring(index, url.length);
						var childWin = window.open(url,'',"top=0,left=0,toolbar=no,location=no,directories=no,status=yes,menubar=no,scrollbars=false,resizable=no,width="+screenWidth+",height="+screenHeight);
					}
					
				   }
				});
		}
	}
	
	function fieldManagerCng(obj){
		var typeValue = $(obj).attr('value').split('-');
		if(typeValue == ''){
			$('#cdsOpt').html('');
			$('#cdtSpan').html('');
			return;
		}
		if(typeValue[0]=='TEXT'){
			if(typeValue[1]=='processState'||typeValue[1]=='process_state'){
				$('#cdsOpt').html('<option value="=">等于</option><option value="<>">不等于</option>');
				$('#cdtSpan').html('<select id="condition"><option value="1">已提交</option><option value="2">已结束</option><option value="3">已取消</option></select>');
			}else{
				$('#cdsOpt').html('<option value="=">等于</option><option value="<>">不等于</option><option value="like">包含</option><option value="not like">不包含</option>');
				$('#cdtSpan').html('<input id="condition" type="text"/>');
			}
		}else if(typeValue[0]=='DATE'){
			$('#cdsOpt').html('<option value=">">大于</option><option value="<">小于</option><option value="=">等于</option>');
			$('#cdtSpan').html('<input id="condition" type="text" readonly="readonly"  onfocus="WdatePicker({dateFmt:\'yyyy-MM-dd\',el:\'condition\'})"/>');
		}else if(typeValue[0]=='TIME'){
			$('#cdsOpt').html('<option value=">">大于</option><option value="<">小于</option><option value="=">等于</option>');
			$('#cdtSpan').html('<input id="condition" type="text" readonly="readonly" onfocus="WdatePicker({dateFmt:\'yyyy-MM-dd HH:mm\',el:\'condition\'})" />');
		}else {
				$('#cdsOpt').html('<option value="=">等于</option><option value=">">大于</option><option value="<">小于</option>');
				$('#cdtSpan').html('<input id="condition" type="text"  onkeyup="value=value.replace(/[^0-9]/g,\'\');"/>');
			
		}
		
	}
	
	function addManagerCds(){
		if(typeof($('#condition').attr('value'))=='undefined' || $('#condition').attr('value') == ''){
			$("#message").html("<font class=\"onError\"><nobr>请输入条件</nobr></font>");
			showMsg("message");
			return;
		}
		var typeValue = $('#fieldCng').attr('value').split('-');
		var searchVal="";
		if(typeValue[1]=='processState'||typeValue[1]=='process_state'){
			searchVal='<td style="border: 0px;">'+getHiddenInput('searchValues',$('#condition').val()) + getEsc($('#condition option:selected').text()) +'</td>';
		}else{
			searchVal='<td style="border: 0px;">'+getHiddenInput('searchValues',$('#condition').attr('value')) + getEsc($('#condition').attr('value')) +'</td>';
		}
			
		var tr = '<tr class="str">' 
			+'<td style="border: 0px;">'+getHiddenInput('enNames',$('#fieldCng').attr('value').split('-')[1])
			+getHiddenInput('chNames',$('#fieldCng option:selected').text())
			+getHiddenInput('dataTypes',$('#fieldCng').attr('value').split('-')[0])
			+ $('#fieldCng option:selected').text() +'</td>'
				+'<td style="border: 0px;">'+getHiddenInput('operates',$('#cdsOpt').attr('value')) + $('#cdsOpt option:selected').text() +'</td>'
				+searchVal
				+'<TD style="border: 0px;"><a class="delete" href="#" >删除</a></TD>';
		
		$(tr).appendTo("#conditiontable").find(".delete").click(function(){
		             $(this).parent().parent().remove();
		             setHiddenDiv();
		});
		setHiddenDiv();
	}
	
//	function searchManagerForm(){
//		$("#def_type").attr('value', $("#wf_type").attr('value'));
//		$("#def_name").attr('value', $("#wf_name").attr('value'));
//		ajaxSubmit('search_form',webRoot+'/engine/workflow-definition!searchManager.htm','monitorList',searchCallBack);
//	}
	
	function setHiddenDiv(){
		if($("#searchbutton").html() == "取消查询"){
			$("#hiddenDiv").html($("#searchDiv").html());
			$("#hiddenDiv").find("table[id='conditiontable']").removeAttr("id");
		}
	}
	
	function searchCallBack(){
		setHiddenDiv();
	}
	
	function setValue(){
		$("#wf_type1").attr('value', $("#wf_type").attr('value'));
		$("#wf_name1").attr('value', $("#wf_name").attr('value'));
	}
	
	//应急处理按钮(当前流程实例)
	function urgen_done(){
		var ids = jQuery("#main_table").getGridParam('selarrrow');
		if(ids==''){
			$("#message").html("<font class=\"onError\"><nobr>"+iMatrixMessage["selectOneInfo"]+"</nobr></font>");
			showMsg("message");
		}else if(ids.length > 1){
			$("#message").html("<font class=\"onError\"><nobr>"+iMatrixMessage["dataAuth.onlySelectOneData"]+"</nobr></font>");
			showMsg("message");
		}else{
			var rowData=jQuery("#main_table").jqGrid('getRowData',ids[0] );
			var url = rowData['emergencyUrl'];
			
			if(url==""||typeof (url)=="undefined"){
				iMatrix.alert(iMatrixMessage["wf.engine.thereIsNo"]);
			}else{
				var screenWidth=screen.availWidth-12;
				var screenHeight=screen.availHeight-58;
				var win = window.open(webRoot+"/engine/monitor-view.htm?instanceId="+ids[0]+"&operationName=urgenDone",'win',"top=0,left=0,toolbar=no,location=no,directories=no,status=yes,menubar=no,scrollbars=false,resizable=no,width="+screenWidth+",height="+screenHeight);
			}
		}
	}
	//应急处理按钮(历史流程实例)
	function urgen_done_history(){
		var ids = jQuery("#main_table").getGridParam('selarrrow');
		if(ids==''){
			$("#message").html("<font class=\"onError\"><nobr>"+iMatrixMessage["selectOneInfo"]+"</nobr></font>");
			showMsg("message");
		}else if(ids.length > 1){
			$("#message").html("<font class=\"onError\"><nobr>"+iMatrixMessage["dataAuth.onlySelectOneData"]+"</nobr></font>");
			showMsg("message");
		}else{
			var rowData=jQuery("#main_table").jqGrid('getRowData',ids[0] );
			var url = rowData['emergencyUrl'];
			
			if(url==""||typeof (url)=="undefined"){
				iMatrix.alert(iMatrixMessage["wf.engine.thereIsNo"]);
			}else{
				var screenWidth=screen.availWidth-12;
				var screenHeight=screen.availHeight-58;
				var win = window.open(webRoot+"/engine/monitor-view-history.htm?instanceId="+ids[0]+"&operationName=urgenDone",'win',"top=0,left=0,toolbar=no,location=no,directories=no,status=yes,menubar=no,scrollbars=false,resizable=no,width="+screenWidth+",height="+screenHeight);
			}
		}
	}
	function taskJumpAssignTransactor(workflowId,taskName,wfdId,pos,type,defCode){
		if(pos=="monitorManager"){
			$.colorbox({href:webRoot+'/engine/task-taskJumpAssignTransactor.htm?workflowId='+workflowId+"&backto="+taskName+"&type="+type+"&definitionCode="+defCode+"&position="+pos,iframe:true, width:300, height:500,overlayClose:false,title:iMatrixMessage["wf.engine.choose"]});
		}else{
			$.colorbox({href:webRoot+'/engine/task-taskJumpAssignTransactor.htm?workflowId='+workflowId+"&backto="+taskName+"&wfdId="+wfdId+"&position="+pos,iframe:true, width:300, height:500,overlayClose:false,title:iMatrixMessage["wf.engine.choose"]});
		}
	}
	
	function taskJumpAssignTransactorVolume(workflowIds,taskName,wfdId,pos,type,defCode){
		$.colorbox({href:webRoot+'/engine/task-taskJumpAssignTransactorVolume.htm?workflowIds='+workflowIds+"&backto="+taskName+"&wfdId="+wfdId+"&position="+pos,iframe:true, width:300, height:500,overlayClose:false,title:iMatrixMessage["wf.engine.choose"]});
	}
	
	function taskJumpChoiceTransactor(canChoiceTransacators,workflowId,taskName,wfdId,pos,type,defCode){
		if(pos=="monitorManager"){
		$.colorbox({href:webRoot+'/engine/task-taskJumpChoiceTransactor.htm?canChoiceTransacators='+canChoiceTransacators+'&workflowId='+workflowId+'&backto='+taskName+"&type="+type+"&definitionCode="+defCode+"&position="+pos,iframe:true, width:400, height:400,overlayClose:false,title:iMatrixMessage["wf.engine.choose"]});
		}else{
			$.colorbox({href:webRoot+'/engine/task-taskJumpChoiceTransactor.htm?canChoiceTransacators='+canChoiceTransacators+'&workflowId='+workflowId+'&backto='+taskName+"&wfdId="+wfdId+"&position="+pos,iframe:true, width:400, height:400,overlayClose:false,title:iMatrixMessage["wf.engine.choose"]});
		}
	}
	
	//增加办理人
	function addTransactor(){
		var ids = jQuery("#main_table").getGridParam('selarrrow');
		if(ids==''){
			$("#message").html("<font class=\"onError\"><nobr>"+iMatrixMessage["selectOneInfo"]+"</nobr></font>");
			showMsg("message");
		}else if(ids.length > 1){
			$("#message").html("<font class=\"onError\"><nobr>"+iMatrixMessage["dataAuth.onlySelectOneData"]+"</nobr></font>");
			showMsg("message");
		}else{
			var workflowId = ids[0];
			var rowData=jQuery("#main_table").jqGrid('getRowData',workflowId );
			var state=rowData['processState'];
			if(state=="SUBMIT"){
				$.colorbox({href:webRoot+'/engine/task-addTransactor.htm?instanceId='+workflowId,iframe:true, width:300, height:500,overlayClose:false,title:iMatrixMessage["wf.engine.choose"]});
			}else{
				iMatrix.alert(iMatrixMessage["wf.engine.theProcessAdd"]);
			}
		}
	}
	
	//减少办理人
	function delTransactor(btnId){
		var ids = jQuery("#main_table").getGridParam('selarrrow');
		if(ids==''){
			$("#message").html("<font class=\"onError\"><nobr>"+iMatrixMessage["selectOneInfo"]+"</nobr></font>");
			showMsg("message");
		}else if(ids.length > 1){
			$("#message").html("<font class=\"onError\"><nobr>"+iMatrixMessage["dataAuth.onlySelectOneData"]+"</nobr></font>");
			showMsg("message");
		}else{
			var workflowId = ids[0];
			var rowData=jQuery("#main_table").jqGrid('getRowData',workflowId );
			var state=rowData['processState'];
			if(state=="SUBMIT"){
				$.colorbox({href:webRoot+'/engine/task-delTransactor.htm?instanceId='+workflowId,iframe:true, width:400, height:500,overlayClose:false,title:iMatrixMessage["wf.engine.choose"]});
			}else{
				iMatrix.alert(iMatrixMessage["wf.engine.theProcessReduced"]);
			}
		}
	}
	
	function viewWFDM(ts1,cellval,opts,rwdat,_act){
		var v="<a  href=\"#\" hidefocus=\"true\" onclick=\"view_standard_form("+opts.id+",'"+opts.formUrl+"');\">" + ts1 + "</a>";
		if(opts.processState=='END'){
		  v="<a  href=\"#\" hidefocus=\"true\" onclick=\"view_standard_form("+opts.id+",'"+opts.formUrl+"');\"><font color=\"#00FF00\">" + ts1 + "</font></a>";
		}else if(opts.processState=='MANUAL_END'){
		  v="<a  href=\"#\" hidefocus=\"true\" onclick=\"view_standard_form("+opts.id+",'"+opts.formUrl+"');\"><font color=\"red\">" + ts1 + "</font></a>";
		}else if(opts.processState=='PAUSE'){
		  v="<a  href=\"#\" hidefocus=\"true\" onclick=\"view_standard_form("+opts.id+",'"+opts.formUrl+"');\"><font color=\"#C58B07\">" + ts1 + "</font></a>";
		}
		return v;
	}
	
	function viewWFDMHistory(ts1,cellval,opts,rwdat,_act){
		var v="<a  href=\"#\" hidefocus=\"true\" onclick=\"view_standard_form_history("+opts.id+",'"+opts.formUrl+"');\">" + ts1 + "</a>";
		if(opts.processState=='END'){
		  v="<a  href=\"#\" hidefocus=\"true\" onclick=\"view_standard_form_history("+opts.id+",'"+opts.formUrl+"');\"><font color=\"#00FF00\">" + ts1 + "</font></a>";
		}else if(opts.processState=='MANUAL_END'){
		  v="<a  href=\"#\" hidefocus=\"true\" onclick=\"view_standard_form_history("+opts.id+",'"+opts.formUrl+"');\"><font color=\"red\">" + ts1 + "</font></a>";
		}else if(opts.processState=='PAUSE'){
		  v="<a  href=\"#\" hidefocus=\"true\" onclick=\"view_standard_form_history("+opts.id+",'"+opts.formUrl+"');\"><font color=\"#C58B07\">" + ts1 + "</font></a>";
		}
		return v;
	}
	function viewWFDMUser(ts1,cellval,opts,rwdat,_act){
		var v="<a  href=\"#\" hidefocus=\"true\" onclick=\"view_standard_form("+opts.id+",'"+opts.formUrl+"');\">" + ts1 + "</a>";
		return v;
	}
	
	function viewWFDMUserHistory(ts1,cellval,opts,rwdat,_act){
		var v="<a  href=\"#\" hidefocus=\"true\" onclick=\"view_standard_form_history("+opts.id+",'"+opts.formUrl+"');\">" + ts1 + "</a>";
		return v;
	}
	//流程定义/流程监控/暂停流程
	function pause_workflow(wfdId){
		var item = jQuery("#main_table").getGridParam('selarrrow');
		if(item.length==0){
			$("#message").html("<font class=\"onError\"><nobr>"+iMatrixMessage["selectInfo"]+"</nobr></font>");
			showMsg("message");
			return;
		}else{
			iMatrix.confirm({
				message:iMatrixMessage["wf.engine.confirmTheSuspension"],
				confirmCallback:pause_workflowOk,
				parameters:item
			});
		}
	}
	function pause_workflowOk(item){
		var prmt = '';
        for(var i=0;i<item.length;i++){
            if(prmt != '') prmt += '&';
            prmt+=('workflowIds='+item[i]);
        }
        $.post(webRoot+"/engine/workflow-definition-pauseWorkflows.htm?"+prmt, "", function(data) {
			$("#message").html("<font class=\"onSuccess\"><nobr>"+data+"</nobr></font>");
			showMsg("message");
			jQuery("#main_table").jqGrid().trigger("reloadGrid");
		});
	}
	//流程监控/暂停流程
	function pause_workflow_def(typeId,definitionCode){
		var item = jQuery("#main_table").getGridParam('selarrrow');
		if(item.length==0){
			$("#message").html("<font class=\"onError\"><nobr>"+iMatrixMessage["selectInfo"]+"</nobr></font>");
			showMsg("message");
			return;
		}else{
			iMatrix.confirm({
				message:iMatrixMessage["wf.engine.confirmTheSuspension"],
				confirmCallback:pause_workflow_defOk,
				parameters:item
			});
		}
	}
	function pause_workflow_defOk(item){
		var prmt = '';
        for(var i=0;i<item.length;i++){
            if(prmt != '') prmt += '&';
            prmt+=('workflowIds='+item[i]);
        }
        prmt+="&type="+$("#wf_type").attr('value')+"&definitionCode="+$("#wf_name").attr('value');
		$.post(webRoot+"/engine/workflow-definition-pauseWorkflowDef.htm?"+prmt, "", function(data) {
			$("#message").html("<font class=\"onSuccess\"><nobr>"+data+"</nobr></font>");
			showMsg("message");
			jQuery("#main_table").jqGrid().trigger("reloadGrid");
		});
	}
	//流程定义/流程监控/继续流程：继续被暂停的实例
	function continue_workflow(wfdId){
		var item = jQuery("#main_table").getGridParam('selarrrow');
		if(item.length==0){
			$("#message").html("<font class=\"onError\"><nobr>"+iMatrixMessage["selectInfo"]+"</nobr></font>");
			showMsg("message");
			return;
		}else{
			iMatrix.confirm({
				message:iMatrixMessage["wf.engine.confirmToContinue"],
				confirmCallback:continue_workflowOk,
				parameters:item
			});
		}
	}
	function continue_workflowOk(item){
		var prmt = '';
        for(var i=0;i<item.length;i++){
            if(prmt != '') prmt += '&';
            prmt+=('workflowIds='+item[i]);
        }
        $.post(webRoot+"/engine/workflow-definition-continueWorkflows.htm?"+prmt, "", function(data) {
			$("#message").html("<font class=\"onSuccess\"><nobr>"+data+"</nobr></font>");
			showMsg("message");
			jQuery("#main_table").jqGrid().trigger("reloadGrid");
		});
	}
	//流程监控/继续流程
	function continue_workflow_def(typeId,definitionCode){
		var item = jQuery("#main_table").getGridParam('selarrrow');
		if(item.length==0){
			$("#message").html("<font class=\"onError\"><nobr>"+iMatrixMessage["selectInfo"]+"</nobr></font>");
			showMsg("message");
			return;
		}else{
			iMatrix.confirm({
				message:iMatrixMessage["wf.engine.confirmToContinue"],
				confirmCallback:continue_workflow_defOk,
				parameters:item
			});
		}
	}
	function continue_workflow_defOk(item){
		var prmt = '';
        for(var i=0;i<item.length;i++){
            if(prmt != '') prmt += '&';
            prmt+=('workflowIds='+item[i]);
        }
        prmt+="&type="+$("#wf_type").attr('value')+"&definitionCode="+$("#wf_name").attr('value');
		$.post(webRoot+"/engine/workflow-definition-continueWorkflowDef.htm?"+prmt, "", function(data) {
			$("#message").html("<font class=\"onSuccess\"><nobr>"+data+"</nobr></font>");
			showMsg("message");
			jQuery("#main_table").jqGrid().trigger("reloadGrid");
		});
	}
	//流程监控/批量移除任务
	function delTasks_batch(pos,wfdId){
		if(pos=="monitorManager"){
			$.colorbox({href:webRoot+'/engine/workflow-definition-searchTasks.htm?type='+$("#wf_type").attr('value')+'&definitionCode='+$("#wf_name").attr('value'),iframe:true, width:860, height:490,overlayClose:false,title:iMatrixMessage["wf.engine.batchRemoveTask"]});
		}else{
			$.colorbox({href:webRoot+'/engine/workflow-definition-searchTasks.htm?wfdId='+wfdId,iframe:true, width:860, height:490,overlayClose:false,title:iMatrixMessage["wf.engine.batchRemoveTask"]});
		}
	}
	
	/**下拉按钮效果 ****/
	function initBtnGroup(){//默认按钮效果  
		$("#parentFlowBtn")
			.button()
				.click(function() {
					removeSearchBox();
					$("#transactorbtn").hide();
					$("#taskbtn").hide();
					showFlowBtnDiv();
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
					$("#transactorbtn").hide();
					$("#taskbtn").hide();
					showFlowBtnDiv();
				})
				.parent()
				.buttonset();

		$("#parentTransactorBtn")
		.button()
			.click(function() {
				removeSearchBox();
				$("#flowbtn").hide();
				$("#taskbtn").hide();
				showTransactorBtnDiv();
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
				$("#flowbtn").hide();
				$("#taskbtn").hide();
				showTransactorBtnDiv();
			})
			.parent()
			.buttonset();

		$("#parentTaskBtn")
		.button()
			.click(function() {
				removeSearchBox();
				$("#transactorbtn").hide();
				$("#flowbtn").hide();
				showTaskBtnDiv();
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
				$("#transactorbtn").hide();
				$("#flowbtn").hide();
				showTaskBtnDiv();
			})
			.parent()
			.buttonset();
		}
	function showFlowBtnDiv(){//显示更多按钮效果位置  
		if($("#flowbtn").css("display")=='none'){
			$("#flowbtn").show();
			var position = $("#_flowbtn").position();
			$("#flowbtn").css("left",position.left+0);
			$("#flowbtn").css("top",position.top+24);
		}else{
			$("#flowbtn").hide();
		};
		$("#flowbtn").hover(
			function (over ) {
				$("#flowbtn").show();
			},
			function (out) {
				 $("#flowbtn").hide();
			}
		); 

	}

	function showTransactorBtnDiv(){//显示更多按钮效果  位置
		if($("#transactorbtn").css("display")=='none'){
			$("#transactorbtn").show();
			var position = $("#_transactorBtn").position();
			$("#transactorbtn").css("left",position.left+0);
			$("#transactorbtn").css("top",position.top+24);
		}else{
			$("#transactorbtn").hide();
		};
		$("#transactorbtn").hover(
			function (over ) {
				$("#transactorbtn").show();
			},
			function (out) {
				 $("#transactorbtn").hide();
			}
		); 
	}

	function showTaskBtnDiv(){//显示更多按钮效果位置  
		if($("#taskbtn").css("display")=='none'){
			$("#taskbtn").show();
			var position = $("#_taskBtn").position();
			$("#taskbtn").css("left",position.left+0);
			$("#taskbtn").css("top",position.top+24);
		}else{
			$("#taskbtn").hide();
		};
		$("#taskbtn").hover(
			function (over ) {
				$("#taskbtn").show();
			},
			function (out) {
				 $("#taskbtn").hide();
			}
		); 
	}
	function changeInstanceList(flag,wfdId){
		if(flag=='current'){
			ajaxSubmit('wf_form', webRoot+'/engine/workflow-definition-monitor.htm?wfdId='+wfdId, 'wfd_main',initBtnGroup);
		}else{
			ajaxSubmit('wf_form', webRoot+'/engine/workflow-definition-monitorHistory.htm?wfdId='+wfdId, 'monitorButton,monitorList');
		}
	}
	
	//给字符串替换尖括号
	function replaceSpecialSymbol(str){
		 if(str.indexOf('<')!=-1){
			 str = replaceSpecialSymbol(str.replace('<','&lt'));
		 }else if(str.indexOf('>')!=-1){
			 str = replaceSpecialSymbol(str.replace('>','&gt'));
		 }
		 return str;
	}
	//格式化超期任务监控中任务名称列
	function fmtTitle(cellvalue, options, rowObject){
		var value =cellvalue;
		var trustorName = rowObject.trustorName;
		var transferName = rowObject.transferName;
		if(transferName!="&nbsp;"&&typeof(transferName)!="undefined"&&transferName!=""){//移交人不为空，表示是接收的任务
			if(trustorName!="&nbsp;"&&typeof(trustorName)!="undefined"&&trustorName!=""&&trustorName!=null){
				value = "<span style='color:#CC0000;'>（"+transferName+"移交于"+trustorName+","+trustorName+"委托）"+value+"</span>";
			}else{
				value = "<span style='color:#CC0000;'>（"+transferName+"移交）"+value+"</span>";
			}
		}else{
			if(trustorName!="&nbsp;"&&typeof(trustorName)!="undefined"&&trustorName!=""&&trustorName!=null){
				value = "<span style='color:#CC0000;'>（"+trustorName+"委托）"+value+"</span>";
			}
		}
		
		return value;
	}
	//格式化超期人监控中姓名列
	function fmtTitleTaskUser(cellvalue, options, rowObject){
		var value =cellvalue;
		value = "<a  href=\"#\" hidefocus=\"true\" onclick=\"openOverdueTaskDetail('"+rowObject['transactorId']+"','"+rowObject['transactor']+"','taskActive');\">" +value + "</a>";
		return value;
	}
	//格式化超期人监控中归档任务中的姓名列
	function fmtTitleHistoryTaskUser(cellvalue, options, rowObject){
		var value =cellvalue;
		value = "<a  href=\"#\" hidefocus=\"true\" onclick=\"openOverdueTaskDetail('"+rowObject['transactorId']+"','"+rowObject['transactor']+"','taskHistory');\">" +value + "</a>";
		return value;
	}
	
	function openOverdueTaskDetail(transactorId,transactor,position){
		var searchPar = getSearchParam();
		if(transactorId!="&nbsp;"&&typeof(transactorId)!="undefined"&&transactorId!=""&&transactorId!=null){
			init_colorbox(webRoot+"/engine/workflow-definition-monitor-task-list-user.htm?transactorId="+transactorId+"&"+searchPar,iMatrixMessage["wf.engine.details"]);
		}else{
			init_colorbox(webRoot+"/engine/workflow-definition-monitor-task-list-user.htm?transactor="+transactor+"&"+searchPar,iMatrixMessage["wf.engine.details"]);
		}
	}
	//格式化“是否移交”
	function fmtTransferabled(cellvalue, options, rowObject){
		var value =cellvalue;
		var transferabled = rowObject.transferabled;
		if(transferabled=="true"){
			value = iMatrixMessage["basicSetting.yes"];
		}else{
			value = iMatrixMessage["basicSetting.no"];
		}
		if(transferabled!="&nbsp;"&&typeof(transferabled)!="undefined"&&transferabled!=""&&transferabled!=null&&transferabled=="true"){
			value = "<a  href=\"#\" hidefocus=\"true\" onclick=\"openTransferTaskDetail('"+rowObject['id']+"','taskActive');\">" +value + "</a>";
		}
		return value;
	}
	//格式化归档任务中“是否移交”
	function fmtHistorlyTransferabled(cellvalue, options, rowObject){
		var value =cellvalue;
		var transferabled = rowObject.transferabled;
		if(transferabled=="true"){
			value = iMatrixMessage["basicSetting.yes"];
		}else{
			value = iMatrixMessage["basicSetting.no"];
		}
		if(transferabled!="&nbsp;"&&typeof(transferabled)!="undefined"&&transferabled!=""&&transferabled!=null&&transferabled=="true"){
			value = "<a  href=\"#\" hidefocus=\"true\" onclick=\"openTransferTaskDetail('"+rowObject['sourceTaskId']+"','taskHistory');\">" +value + "</a>";
		}
		return value;
	}
	//打开移交任务列表
	function openTransferTaskDetail(taskId,position){
		init_colorbox(webRoot+"/engine/workflow-definition-monitor-task-transfer.htm?taskId="+taskId+"&position="+position,iMatrixMessage["wf.engine.transferTaskList"]);
	}
	//格式化任务移交数
	function formatTransferTaskNum(cellvalue, options, rowObject){
		var value = "&nbsp;&nbsp;<a  href=\"#\" hidefocus=\"true\" onclick=\"openTransactorTransferTask('"+rowObject['transactorId']+"','taskUserActive');\">" +cellvalue + "</a>";
		return value;
	}
	//格式化归档任务中的“任务移交数”
	function formatTransferHistoryTaskNum(cellvalue, options, rowObject){
		var value = "&nbsp;&nbsp;<a  href=\"#\" hidefocus=\"true\" onclick=\"openTransactorTransferTask('"+rowObject['transactorId']+"','taskUserHistory');\">" +cellvalue + "</a>";
		return value;
	}
	
	function openTransactorTransferTask(transactorId,position){
		var searchPar = getSearchParam();
		init_colorbox(webRoot+"/engine/workflow-definition-monitor-transfer-task-list-user.htm?transactorId="+transactorId+"&"+searchPar,iMatrixMessage["wf.engine.transferTaskList"]);
	}
	
	function lastTransactTimeInit(inputId){
		$("#"+inputId).datetimepicker({
		      "dateFormat":'yy-mm-dd',
		       changeMonth:true,
		       changeYear:true,
		       showSecond: false,
				showMillisec: false,
				"timeFormat": 'hh:mm'
		});
	}

	function searchOverdueTaskUser(){
		var transactorName = $("#transactorName").val();
		var lastTransactTimeStart = $("#lastTransactTimeStart").val();
		var lastTransactTimeEnd = $("#lastTransactTimeEnd").val();
		$("#transactorName1").attr("value",transactorName);
		$("#lastTransactTimeStart1").attr("value",lastTransactTimeStart);
		$("#lastTransactTimeEnd1").attr("value",lastTransactTimeEnd);

		var obj = ____getValueFromForm("wf_form");
		var gridId = ___getGridId();
		jQuery("#"+gridId).jqGrid('setGridParam',{url:encodeURI(webRoot+"/engine/workflow-definition-monitor-task-list-data.htm"),mtype:'post',postData:obj,page:1}).trigger("reloadGrid");
	}
	//获得form中的input值
	function ____getValueFromForm(submitForm){
		var object = {};
		var elements = $("#"+submitForm).children('input');
		var lengt = elements.length;
		for(var i=0; i<lengt; i++){
			var name = $(elements[i]).attr("name");
			var value =  $(elements[i]).attr("value");
			object[name] = value;
		}
		return object;
	}
	function ___getGridId(){
		var gridId = "main_table_user";
		var position = $("#position").val();
		if(position=="taskUserHistory"){
			gridId = "main_table_user_history";
		}
		return gridId;
	}

	function exportOVerdueTaskUser(){
		var searchPar = getSearchParam();
		var url =webRoot+'/engine/workflow-definition-monitor-task-export.htm?'+searchPar;
		iMatrix.export_Data(url,'true');
	}
	
	function getSearchParam(){
		var position = $("#position").val();
		var transactorName = $("#transactorName1").val();
		var lastTransactTimeStart = $("#lastTransactTimeStart1").val();
		var lastTransactTimeEnd = $("#lastTransactTimeEnd1").val();
		return 'position='+position+'&transactorName='+transactorName+'&lastTransactTimeStart='+lastTransactTimeStart+'&lastTransactTimeEnd='+lastTransactTimeEnd;
	}
