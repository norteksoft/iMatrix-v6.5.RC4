<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/wf-taglibs.jsp"%>
<script src="${imatrixCtx}/js/message_<%=com.norteksoft.product.util.ContextUtils.getCurrentLanguage()%>.js" type="text/javascript"></script>
<script type="text/javascript">
	function treechange(id){
		var url="";
		if(id.indexOf('ENABLE_WFT_') != -1 || id.indexOf('ENABLE_BSYS_') != -1 || id.indexOf('ENABLE_ALL_') != -1 || id.indexOf('UNABLE_ALL_') != -1 || id.indexOf('UNABLE_WFT_') != -1 || id.indexOf('UNABLE_BSYS_') != -1){
			var vertionType = id.split('_')[0];
			var showBy = id.split('_')[1];
			var type;
			var sysId;
			if(showBy=='BSYS'){
				sysId = id.split('_')[2];
				type = -1;
			}else if(showBy=='ALL'){
				sysId = '';
				type = '';
			}else{
				sysId = -1;
				type = id.split('_')[2];
			}
			url=webRoot+"/engine/workflow-definition-data.htm?type="+type+"&sysId="+sysId+"&vertionType="+vertionType;
		}else if(id.indexOf('WFT_monitor_') != -1 ){
			var type;
			var definitionCode;
			if(id.split('_')[1]=="monitor"){
				type = id.split('_')[2];
				if(type=="def"){
					type="0";
				}
				var arr=id.split('_');
				if(arr.length==4){
					definitionCode = id.split('_')[3]; 
				}else{
					definitionCode = ""; 
				}
			}
			url=webRoot+"/engine/workflow-definition-monitorDefintion.htm?type="+type+"&definitionCode="+definitionCode;
		}else if(id.indexOf('WFT_history_monitor_') != -1 ){
			var type;
			var definitionCode;
			if(id.split('_')[1]=="history"){
				type = id.split('_')[3];
				if(type=="def"){
					type="0";
				}
				var arr=id.split('_');
				if(arr.length==5){
					definitionCode = id.split('_')[4]; 
				}else{
					definitionCode = ""; 
				}
			}
			url=webRoot+"/engine/workflow-definition-monitorDefintionHistory.htm?type="+type+"&definitionCode="+definitionCode;
		}else if(id.indexOf('WFT_myCreate') != -1 || id.indexOf('WFT_type_') != -1 ){
			if(id=="WFT_myCreate_0"){
				url=webRoot+"/engine/data-dictionary.htm";
			}else if(id=="WFT_type_0"){
				url=webRoot+"/engine/data-dictionary-type.htm";
			}
		}else if(id.indexOf('process-type-tree') != -1 || id.indexOf('type-def') != -1){
			url=webRoot+"/engine/workflow-type.htm";
		}else if(id.indexOf('WFT_') != -1 ){
			url=webRoot+"/engine/office-template.htm?typeId="+id.split('_')[1]+"&backTypeId="+id.split('_')[1];
		}else if(id.indexOf('WFDT_') != -1 ){
			url=webRoot+"/engine/workflow-definition-template-list.htm?typeId="+id.split('_')[1];
		}else if(id.indexOf('overdueTask') != -1 ){//超期任务监控
			var position = "";
			var pos = id.split("-");
			if(pos[0]=='overdueTask'){//超期任务
				if(pos.length>1&&pos[1]=='active'){
					position = "taskActive";//超期任务/当前任务
				}else if(pos.length>1&&pos[1]=='history'){
					position = "taskHistory";//超期任务/归档任务
				}
			}else if(pos[0]=='overdueTaskUser'){//超期人
				if(pos.length>1&&pos[1]=='active'){
					position = "taskUserActive";//超期人/当前任务
				}else if(pos.length>1&&pos[1]=='history'){
					position = "taskUserHistory";//超期人/归档任务
				}
			}
			if(position!=""){
				url=webRoot+"/engine/workflow-definition-monitor-task-list.htm?position="+position;
			}
		}
		if(url!=""){
			 $('#myIFrame').attr('src',url);
		}
	}

	function treeselected(currentId){
		treechange(currentId);
	}
	function startProcess(){
		var id = $("#selectedMu").attr("value");
		if(id == "aaa"){
			$("#start_").unbind();
			$("#start_").removeClass("thickbox");
			$("#start_").attr("href", "process-input.html");
		}
	}

	function treeDefinition(){
		$.ajaxSetup({cache:false});
		//treeId:,url:,data(静态树才需要该参数):,multiple:,callback:
		tree.initTree({treeId:"treeid-process",
			url:"${wfCtx}/engine/tree-wfTypes.htm?currentId=INITIALIZED_PROCESS",
			type:"ztree",
			initiallySelect:"ENABLE_WFT_${type}",
			initiallySelectFirstChild:true,
			callback:{
					onClick:selectNode
				}});
		
	    }
    function treeMonitor(){
    	$.ajaxSetup({cache:false});
		//treeId:,url:,data(静态树才需要该参数):,multiple:,callback:
		tree.initTree({treeId:"treeid-monitor",
			url:"${wfCtx}/engine/tree-wfTypes.htm?currentId=INITIALIZED_MONITOR",
			type:"ztree",
			initiallySelect:"WFT_monitor_${type}",
			initiallySelectFirstChild:true,
			callback:{
					onClick:selectNode
				}});
		
	   
        }
    function treeTaskMonitor(){
    	$.ajaxSetup({cache:false});
		//treeId:,url:,data(静态树才需要该参数):,multiple:,callback:
		tree.initTree({treeId:"treeid-taskMonitor",
			data:[{ id:'overdueTask', pId:0, name:iMatrixMessage["wf.menu.extendedTask"], iconSkin:'folder'},
			      { id:'overdueTask-active', pId:'overdueTask', name:iMatrixMessage["wf.menu.currentTask"], iconSkin:'folder'},
			      { id:'overdueTask-history', pId:'overdueTask', name:iMatrixMessage["wf.menu.archivingTask"], iconSkin:'folder'},
			      { id:'overdueTaskUser', pId:0, name:iMatrixMessage["wf.menu.extendedStatistics"],open:"true", iconSkin:'folder'},
			      { id:'overdueTaskUser-active', pId:'overdueTaskUser', name:iMatrixMessage["wf.menu.currentTask"], iconSkin:'folder'},
			      { id:'overdueTaskUser-history', pId:'overdueTaskUser', name:iMatrixMessage["wf.menu.archivingTask"], iconSkin:'folder'}],
			type:"ztree",
			initiallySelect:"overdueTask-active",
			initiallySelectFirstChild:true,
			callback:{
					onClick:selectNode
				}});
		
	   
        }
    function treeDictionary(){
    	$.ajaxSetup({cache:false});
		//treeId:,url:,data(静态树才需要该参数):,multiple:,callback:
		tree.initTree({treeId:"data-dict-tree",
			url:"${wfCtx}/engine/tree-wfTypes.htm?currentId=INITIALIZED_DICT",
			type:"ztree",
			initiallySelect:"WFT_myCreate_0",
			callback:{
					onClick:selectNode
				}});
    }
    function treeTemplate(){
    	$.ajaxSetup({cache:false});
		//treeId:,url:,data(静态树才需要该参数):,multiple:,callback:
		tree.initTree({treeId:"office-template-tree",
			url:"${wfCtx}/engine/tree-wfTypes.htm?currentId=INITIALIZED_TEMPLATE",
			type:"ztree",
			initiallySelect:"WFT_${type}",
			initiallySelectFirstChild:true,
			callback:{
					onClick:selectNode
				}});
		
    }
    function treeWorkflowDefinitionTemplate(){
    	$.ajaxSetup({cache:false});
		//treeId:,url:,data(静态树才需要该参数):,multiple:,callback:
		tree.initTree({treeId:"wfd-template-tree",
			url:"${wfCtx}/engine/tree-wfTypes.htm?currentId=INITIALIZED_WFD_TEMPLATE",
			type:"ztree",
			initiallySelect:"WFDT_${type}",
			initiallySelectFirstChild:true,
			callback:{
					onClick:selectNode
				}});
    }
        
	function selectNode(){
		var currentId = tree.getSelectNodeId();
		treeselected(currentId);
	}
	function selectDefaultNode(treeId, currentId){
		$("#"+currentId).children("a").addClass("jstree-clicked");
		if(currentId != "option-def"){
			treeselected(currentId);
		}
	}
</script>

  	<div id="accordion" >
		<h3><a href="workflow-definition-data.htm" id="process_definition"><s:text name="wf.menu.processDefinition"></s:text></a></h3>
		<div>
			<ul class="ztree" id="treeid-process"></ul>
		</div>
		<h3><a href="workflow-definition-monitorDefintion.htm" id="process_monitor"><s:text name="wf.menu.processMonitoring"></s:text></a></h3>
		<div>
			<ul class="ztree" id="treeid-monitor"></ul>
		</div>
		<h3><a href="workflow-definition-monitor-task-list.htm" id="process_monitor"><s:text name="wf.menu.extendedTaskMonitoring"></s:text></a></h3>
		<div>
			<ul class="ztree" id="treeid-taskMonitor"></ul>
		</div>
		<h3><a href="data-dictionary.htm" id="data_dict"><s:text name="wf.menu.dataDictionary"></s:text></a></h3>
		<div>
			<ul class="ztree" id="data-dict-tree"></ul>
		</div>
		<h3><a href="workflow-type.htm" id="process_type"><s:text name="wf.menu.flowType"></s:text></a></h3>
		<div>
			<div class="demo" id="process-type-tree" style="margin-top: 10px;"></div>
		</div>
		<h3><a href="office-template.htm" id="office-template"><s:text name="wf.menu.textTemplate"></s:text></a></h3>
		<div>
			<ul class="ztree" id="office-template-tree"></ul>
		</div>
		<h3><a href="workflow-definition-template-list.htm" id="workflow-definition-template-list"><s:text name="wf.menu.flowTemplate"></s:text></a></h3>
		<div>
			<ul class="ztree" id="wfd-template-tree"></ul>
		</div>
	</div>

<script type="text/javascript">
$(document).ready(function() {
	$("#accordion").accordion({fillSpace:true, change:accordionChange});
});
function accordionChange(event,ui){
	var url=ui.newHeader.children("a").attr("href");
	if(url == 'workflow-definition-data.htm'||url == 'workflow-definition.htm'){//流程定义
		$("#treeid-process").html("");
			treeDefinition();
	}else if(url == 'workflow-definition-monitorDefintion.htm'){//流程监控
		$("#treeid-monitor").html("");
			treeMonitor();
	}else if(url == 'data-dictionary.htm'){//数据字典
		$("#data-dict-tree").html("");
			treeDictionary();
	}else if(url == 'office-template.htm'){//正文模板
		 $("#data-dict-tree").html("");
			treeTemplate();
	}else if(url == 'workflow-definition-template-list.htm'){//流程定义模板
		 $("#wfd-template-tree").html("");
		 treeWorkflowDefinitionTemplate();
	}else if(url == 'workflow-definition-monitor-task-list.htm'){//超期任务监控
		url = 'workflow-definition-monitor-task-list.htm?position=taskActive';
		treeTaskMonitor();
	}
	$("#myIFrame").attr("src",url);
}
function getIndex(id){
	var subs = $(id).children("h3");
	for(var i = 0; i < subs.length; i++){
		var hs0 = $($(subs[i]).children('a')[0]).attr('id');
			if(thirdMenu==hs0){
				return i;
			}
	}
	return 0;
}
</script>

