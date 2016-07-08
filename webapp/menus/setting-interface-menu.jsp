<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/setting-taglibs.jsp"%>
<div id="accordion" class="basic">
	<h3><a href="${settingCtx}/options/datasource-setting-list-data.htm" id="rank-manager"><s:text name="interfaceManager.dataSourceConfigure"></s:text></a></h3>
	<div></div>
	<h3><a href="${settingCtx}/options/interface-setting-list-data.htm" id="option-group"><s:text name="interfaceManager.timerInterfaceConfigure"></s:text></a></h3>
	<div>
		<ul class="ztree" id="interface_content"></ul>
	</div>
</div>
<link rel="stylesheet" href="${resourcesCtx}/widgets/ztree/css/zTreeStyle/zTreeStyle.css" type="text/css">
<script type="text/javascript" src="${resourcesCtx}/widgets/ztree/js/jquery.ztree.core-3.5.js"></script>
<script type="text/javascript" src="${resourcesCtx}/js/tree.js"></script>
<script type="text/javascript">
	$().ready(function () {
		$("#accordion").accordion({fillSpace:true, change:accordionChange});
	});
	function accordionChange(event,ui){
		var url=ui.newHeader.children("a").attr("href");
		if(url=="${settingCtx}/options/interface-setting-list-data.htm"){
			//$("#_interface").attr("class","four-menu-selected");
			createInterfaceTree();
		}
		$("#myIFrame").attr("src",url);
	}

	//创建页面树菜单
	function createInterfaceTree(){
		$.ajaxSetup({cache:false});
		//treeId:,url:,data(静态树才需要该参数):,multiple:,callback:
		tree.initTree({treeId:"interface_content",
			url:"${settingCtx}/options/interface-setting-tree.htm",
			type:"ztree",
			initiallySelectFirstChild:true,
			initiallySelectFirst:true,
			callback:{
					onClick:selectNode
				}});
	}

	function selectNode(){
		var currentId = tree.getSelectNodeId();
		if(currentId=="_interface"){
			$("#myIFrame").attr("src","${settingCtx}/options/interface-setting-list-data.htm");
		}else{
			if(currentId!="_job_time"){
				$("#myIFrame").attr("src","${settingCtx}/options/job-info.htm?systemId="+currentId);
			}
		}
	}
</script>
