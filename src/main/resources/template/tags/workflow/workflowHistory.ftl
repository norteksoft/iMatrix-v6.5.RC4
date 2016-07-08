  <div style="width:auto; padding: 0; margin: 0;">
  	<div id="tabs-history">
		<ul>
			<li id="textli"><a href="#tabs-history-2" onclick="selectContact('textli');">${textContent}</a></li>
			<li id="flashli"><a href="#tabs-history-2" onclick="selectContact('flashli');">${flashContent}</a></li>
		</ul>
		<div id="tabs-history-2">
  	<#if view>
		<div class="demo" id="textContent" style="display: block; height: auto;"  >
           <table class="form-table-border-left">
				<thead>
				<tr>
				<th style="width: 5%">${sequence}</th>
				<th style="width: 15%">${name}</th>
				<th style="width: 35%">${history}</th>
				<th style="width: 45%">${opinion}</th>
				</tr>
				</thead>
				<tbody>
				<#list instanceHistory?if_exists as item>
				<tr>
				<td>
				 ${item_index+1}
				 <#assign _currentSequence="${item_index+1}">
				</td>
				<td>
				  <#if item.taskName?if_exists=="" > 
					<#if item.type?if_exists==3>
					${end}
					<#else>
					${start}
					</#if>   
				  <#else>
				  	<#if item.subTaskId?if_exists!=0 > 
				  	<a href="#" onclick="showSubWorkflowHistory('${item.subTaskId?if_exists}','${item.taskName?if_exists }');">${item.taskName?if_exists }</a>
				  	<#else>
				  		${item.taskName?if_exists }  
				  	</#if>
				  </#if> 
				</td>
				<td>
				 ${item.internationalizationResult?if_exists }
				</td>
				<td>
				 ${item.transactorOpinion?if_exists }
				</td>
				</tr>
				</#list>
				<#list historyInstanceHistory?if_exists as item>
				<tr>
				<td>
				 ${item_index+1}
				</td>
				<td>
				  <#if item.taskName?if_exists=="" > 
					<#if item.type?if_exists==3>
					${end}
					<#else>
					${start}
					</#if>   
				  <#else>
				  	<#if item.subTaskId?if_exists!=0 > 
				  	<a href="#" onclick="showSubWorkflowHistory('${item.subTaskId?if_exists}','${item.taskName?if_exists }');">${item.taskName?if_exists }</a>
				  	<#else>
				  		${item.taskName?if_exists }  
				  	</#if>
				  </#if> 
				</td>
				<td>
				 ${item.internationalizationResult?if_exists }
				</td>
				<td>
				 ${item.transactorOpinion?if_exists }
				</td>
				</tr>
				</#list>
				<#list currentTasks?if_exists as item>
				<tr>
				<td>
				 ${_currentSequence?number+item_index+1}
				</td>
				<td>
					<#if item[2]?if_exists=='subWorkflow' > 
						<a href="#" onclick="showSubWorkflowHistory('${item[3]}','${item[0]}');">${item[0]}</a>
					<#else>
				  		${item[0]}
					</#if>
				</td>
				<td>
					<#if item[2]?if_exists=='subWorkflow' > 
						${createPerson}：${item[1]}
					<#else>
				  		 ${handlePerson}：${item[1]}
					</#if>
				</td>
				<td>
				 
				</td>
				</tr>
				</#list>
				
				</tbody>   
			</table>
        </div>
	<#else>
              ${NotViewProcessFlowHistory}
	</#if>
         <div id="flashcontent" style="display: none; height:550px;" ></div>
		</div>
	</div>
</div>



<script type="text/javascript" >
	$().ready(function() {
		$( "#tabs-history" ).tabs();
	});
    function selectContact(id){
    	<#if view>
			if(id=="textli"){
				$("#textContent").show();
				$("#flashcontent").hide();
			}else if(id=="flashli"){
				$("#textContent").hide();
				$("#flashcontent").show();
				$(function(){$("#flashcontent").height($(window).height()-140);});
				<#if url=="">
					$("#flashcontent").width($(window).width()-80);
					showJsGraph();
				<#else>
					showFlexGraph();
				</#if>
			}
		</#if>
	}
	function showJsGraph(){
	var obj={
		resourceRoot:"${resourceRoot}",
		iMatrixRoot:"${iMatrixRoot}",
		processInstanceId:"${workflowId}",
		pageSign:"history"
	};
	$("#flashcontent").wfeditor(obj);
	}
	function showFlexGraph(){
		var so = new SWFObject(
					"${url}",
					"FlowChartProject", "100%", "100%", "9", "#CCCCCC");
			so.addParam("quality", "high");
			so.addParam("name", "FlowChartProject");
			so.addParam("id", "FlowChartProject");
			so.addParam("AllowScriptAccess", "always");
			so.addParam("menu", "false");
			so.addVariable("webRoot", "${ctx}");
			so.addVariable("companyId", "${companyId }");
			so.addVariable("instanceId", "${workflowId }");
			so.addVariable("localeLanguage", "${locale }");
			
			so.addVariable("page", "viewHistoryProcess");
			so.write("flashcontent");
	}
	function showHistoryList(){
		$.ajax({
				type : "POST",
				url : "${ctx}/engine/text-history.action",
				data:{workflowId:'${workflowId }'},
				success : function(data){
		            $('#textContent').html(data);
				},
				error: function(){ $('#_textHistory').html('${displayError}'); }
			});
	}
	
	//显示子流程流转历史
	function showSubWorkflowHistory(taskId,taskName){
		var showSubFlowUrl=$showSubWorkflowHistory(taskName);
		if(typeof(showSubFlowUrl)=='undefined' || showSubFlowUrl==''){
			showSubFlowUrl='${showSubFlowUrl}';
			if(typeof(showSubFlowUrl)=='undefined' || showSubFlowUrl==''){
				alert("${subFlowHistoryNotFill}");
				return;
			}
		}
		iMatrixSubWorkflowHistory(taskId,showSubFlowUrl);
	}
	function $showSubWorkflowHistory(taskName){
		return "";
	}
	function iMatrixSubWorkflowHistory(taskId,showSubFlowUrl){
	<#if subFlowWidth==0&&subFlowHeight==0>
		init_colorbox(showSubFlowUrl+'?companyId=${companyId }&taskId='+taskId,"${circulationHistory}");
	</#if>
	<#if subFlowWidth!=0&&subFlowHeight==0>
		init_colorbox(showSubFlowUrl+'?companyId=${companyId }&taskId='+taskId,"${circulationHistory}",${subFlowWidth});
	</#if>
	<#if subFlowWidth==0&&subFlowHeight!=0>
		init_colorbox(showSubFlowUrl+'?companyId=${companyId }&taskId='+taskId,"${circulationHistory}","",${subFlowHeight});
	</#if>
	<#if subFlowWidth!=0&&subFlowHeight!=0>
		init_colorbox(showSubFlowUrl+'?companyId=${companyId }&taskId='+taskId,"${circulationHistory}",${subFlowWidth},${subFlowHeight});
	</#if>
	}
</script>