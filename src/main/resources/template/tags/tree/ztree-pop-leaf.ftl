<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>选择办理人</title>
	<meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
        <meta http-equiv="Cache-Control" content="no-store"/>
        <meta http-equiv="Pragma" content="no-cache"/>
        <meta http-equiv="Expires" content="0"/>
   
	<script type="text/javascript" src="${resourcesCtx}/js/jquery-all-1.0.js"></script>
	<script src="${resourcesCtx}/js/form-layout.js" type="text/javascript"></script>
	<link   type="text/css" rel="stylesheet" href="${resourcesCtx}/css/${theme}/jquery-ui-1.8.16.custom.css" id="_style"/>
	<script src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js" type="text/javascript"></script>
	<script type="text/javascript" src="${resourcesCtx}/js/aa.js"></script>
	<script type="text/javascript" src="${resourcesCtx}/js/public_${language}.js"></script>
	<script src="${resourcesCtx}/js/search.js" type="text/javascript"></script>
	<script type="text/javascript" src="${resourcesCtx}/js/public.js"></script>
	<link rel="stylesheet" href="${resourcesCtx}/widgets/ztree/css/zTreeStyle/zTreeStyle.css" type="text/css"/>
	<script type="text/javascript" src="${resourcesCtx}/widgets/ztree/js/jquery.ztree.core-3.5.js"></script>
	<script type="text/javascript" src="${resourcesCtx}/widgets/ztree/js/jquery.ztree.excheck-3.5.js"></script>
	<script type="text/javascript" src="${resourcesCtx}/templateJs/ztree-pop.js"></script>
</head>
<script type="text/javascript">
var nodeList = [], parentNodeList = [],checkNodeList = [] ;
var checkUserNodeList = [] , checkDeparmentNodeList = [], checkWorkgroupNodeList = [];
var currentCheckedNode, currentClickNode ,currentClickParentNode;
var isCheckAll=false;
var companyName ;
var openNode = {},closeNode = {};
var expandNodes;
var searchMsgs;
var actionUrl="${actionUrl}";
var searchUrl="${searchUrl}";
var userWithoutDeptVisible="${userWithoutDeptVisible}";
var showBranch="${showBranch}";
var webapp="${webapp}";
var showThing="${showThing}";
var hiddenThing="${hiddenThing}";
var showInput="${showInput}";
var hiddenInput="${hiddenInput}";
eval("leafsMsg=${leafsMsg}");
var staticVar=[];
var isInit={};//是否已经初始化
var setting = {
	async: {
		enable: true,
		url: getUrlOfLeaf
	},
	check: ""
	,
	data: {
		simpleData: {
			enable: true
		},
		treeType:0
	},
	view: {
		expandSpeed: "",
		fontCss: getFontCss
	},
	callback: {
		beforeExpand: beforeExpand,
		beforeAsync: beforeAsync,
		onAsyncSuccess: onAsyncSuccess,
		onAsyncError: onAsyncError,
		onExpand: onExpand,
		onClick: onClick,
		onCheck: onCheck
	}
};
function getChkStyleByTreeId(treeId){
	for(var i=0,l=leafsMsg.length;i<l;i++){
		if(leafsMsg[i].treeId==treeId){
			return leafsMsg[i].chkStyle;
		}
	}
}
function getUrlOfLeaf(treeId, treeNode) {
		var tempUrl=actionUrl+"?treeType="+getTreeTypeByTreeId(treeId);
		var param = "";
		if(typeof(treeNode)!="undefined"&&treeNode!=null){
			param = "currentId="+treeNode.id;
        }else{
        	var param = "currentId=0";
        }
		return tempUrl+"&" + param;
}
function getTreeTypeByTreeId(treeId){
	for(var i=0,l=leafsMsg.length;i<l;i++){
		if(leafsMsg[i].treeId==treeId){
			return leafsMsg[i].treeType;
		}
	}
}
function getCheck(treeId){
	var chkStyle=getChkStyleByTreeId(treeId);
	var obj={};
	if(chkStyle&&chkStyle!=""){
		var treeType=getTreeTypeByTreeId(treeId)
		if(treeType=='DEPARTMENT_TREE'||treeType=="DEPARTMENT_WORKGROUP_TREE"){
			chkboxType="{'Y' : 's', 'N' : 's' }";
		}else{
			chkboxType="{'Y' : 'ps', 'N' : 'ps' }";
		}
		if(chkboxType&&chkboxType!=""){
			eval("c="+chkboxType);
			obj.chkboxType=c;
		}
		obj.enable=true;
		obj.chkStyle=chkStyle;
	}
	return obj;
}
$(function(){
	initLeaf(leafsMsg);
	$( "#tabs" ).tabs();
	for(var i=0,l=leafsMsg.length;i<l;i++){
		if(i==0){
			setting.check=getCheck(leafsMsg[i].treeId);
			setting.data.treeType=getLeafTreeType(leafsMsg[i].treeType)
			$.fn.zTree.init($("#"+leafsMsg[i].treeId), setting);
			isInit[leafsMsg[i].treeId]=true;
		}else{
			isInit[leafsMsg[i].treeId]=false;
		}
	}
});
//点击页签后初始化
function initByClick(id){
	if(!isInit[id]){
		setting.check=getCheck(id);
		setting.data.treeType=getLeafTreeType(getTreeTypeByTreeId(id));
		$.fn.zTree.init($("#"+id), setting);
		isInit[id]=true;
	}
}
//拼接页签
function initLeaf(leafsMsg){
	for(var i=0,l=leafsMsg.length;i<l;i++){
		$("#leafTitle").append("<li><a onclick='initByClick(\""+leafsMsg[i].treeId+"\")' href='#tabs-"+i+"'>"+leafsMsg[i].leaftitle+"</a></li>");
		$("#leafTitle").after("<div id='tabs-"+i+"'><table class='form-table-without-border'><table><tr><td ><input id='input"+leafsMsg[i].treeId+"' /><td ><a class='search-btn' href='#' onclick='ajaxSearch(\""+leafsMsg[i].treeId+"\",\""+leafsMsg[i].treeType+"\")' ><b class='ui-icon ui-icon-search'></b></a></td></tr></table><tr><td class='content-title'><ul id='"+leafsMsg[i].treeId+"' class='ztree'></ul></td></tr></table></div>");
	}
}

function getLeafTreeType(treeType){
		if(treeType=='COMPANY'){
	      	return 0;
		}else if(treeType=='MAN_DEPARTMENT_TREE'){
			return 1;
		}else if(treeType=='MAN_GROUP_TREE'){
			return 2;
		}else if(treeType=='DEPARTMENT_TREE'){
			return 3;
		}else if(treeType=='GROUP_TREE'){
			return 4;
		}else if(treeType=='DEPARTMENT_WORKGROUP_TREE'){
			return 5;
		}else{
			alert('出错了!!!');
		}
	}
</script>
<body onload="getContentHeight_ColorIframe();">
<div class="ui-layout-center">
		<style type="text/css">
			#tabs,.ui-tabs .ui-tabs-nav li,.ui-jqgrid,.ui-jqgrid .ui-jqgrid-htable th div,.ui-jqgrid .ui-jqgrid-view,.ui-jqgrid .ui-jqgrid-hdiv,.ui-jqgrid .ui-jqgrid-bdiv{ position: static; }
		</style>
		<div id="body1" class="opt-body">
			<div class="cbox-btn">
				<table>
				<tr>
				<td style="width:80px;">
					<button type='button' class='btn' hidefocus="true" onclick="_ok_ztree_leaf()"><span><span>确认</span></span></button>
				</td>
				</tr>
				</table>
			</div>
			<div id="message"></div>
			<div id="opt-content" >
					<div id="tabs">
						<ul id="leafTitle">
						</ul>
					</div>
			</div>
		</div>	
		</div>
</body>
</html>