<script type="text/javascript" src="${resourcesCtx}/js/public_${language}.js"></script>
<script type="text/javascript" src="${resourcesCtx}/templateJs/ztree-tag.js"></script>

<style type="text/css">
	#tabs,.ui-tabs .ui-tabs-nav li,.ui-jqgrid,.ui-jqgrid .ui-jqgrid-htable th div,.ui-jqgrid .ui-jqgrid-view,.ui-jqgrid .ui-jqgrid-hdiv,.ui-jqgrid .ui-jqgrid-bdiv{ position: static; }
</style>
<div id="message"></div>
<div id="tabs">
	<ul id="leafTitle">
	</ul>
</div>
<script type="text/javascript">
var nodeList = [], parentNodeList = [],checkNodeList = [] ;
var actionUrl="${actionUrl}";
var searchUrl="${searchUrl}";
var userWithoutDeptVisible="${userWithoutDeptVisible}";
var showBranch="${showBranch}";
var webapp="${webapp}";
eval("leafsMsg=${leafsMsg}");
var isInit={};//是否已经初始化
//拼接页签

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
		return -1;
	}
}
function getChkStyleByTreeId(treeId){
	for(var i=0,l=leafsMsg.length;i<l;i++){
		if(leafsMsg[i].treeId==treeId){
			return leafsMsg[i].chkStyle;
		}
	}
}

function getTreeTypeByTreeId(treeId){
	for(var i=0,l=leafsMsg.length;i<l;i++){
		if(leafsMsg[i].treeId==treeId){
			return leafsMsg[i].treeType;
		}
	}
}
function getTreeObjByTreeId(treeId){
	for(var i=0,l=leafsMsg.length;i<l;i++){
		if(leafsMsg[i].treeId==treeId){
			return leafsMsg[i];
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
		$("#tabs"+i).attr("style","height:"+(window.innerHeight-60)+"px");
		if(leafsMsg[i].treeType!='CUSTOM'){
			if(i==0){
				setting.check=getCheck(leafsMsg[i].treeId);
				setting.data.treeType=getLeafTreeType(leafsMsg[i].treeType)
				$.fn.zTree.init($("#"+leafsMsg[i].treeId), setting);
				isInit[leafsMsg[i].treeId]=true;
			}else{
				isInit[leafsMsg[i].treeId]=false;
			}
		}
	}
});
//点击页签后初始化
function initByClick(id){
	var treeType=getTreeTypeByTreeId(id);
	if(!isInit[id]){
		setting.check=getCheck(id);
		if(treeType=='CUSTOM'){
			setting.data.treeType=getLeafTreeType('COMPANY');
			var obj=getTreeObjByTreeId(id);
			setting.async.url=obj['url'];
		}else{
			setting.data.treeType=getLeafTreeType(treeType);
			setting.async.url=getUrlOfLeaf
		}
		$.fn.zTree.init($("#"+id), setting);
		isInit[id]=true;
	}
}
function initLeaf(leafsMsg){
	for(var i=0,l=leafsMsg.length;i<l;i++){
		if(getLeafTreeType(leafsMsg[i].treeType)==-1&&leafsMsg[i].treeType!="CUSTOM"){
			leafsMsg[i].treeType="COMPANY";
		}
		var a="<div id='tabs-"+i+"' ><table class='form-table-without-border'><table><tr><td ><input id='input"+leafsMsg[i].treeId+"' /><td ><a id='a"+leafsMsg[i].treeId+"' class='search-btn' href='#' onclick='ajaxSearch(\""+leafsMsg[i].treeId+"\",\""+leafsMsg[i].treeType+"\")' ><b class='ui-icon ui-icon-search'></b></a></td></tr></table><tr><td class='content-title'><ul id='"+leafsMsg[i].treeId+"' class='ztree'></ul></td></tr></table></div>"
		$("#leafTitle").append("<li><a onclick='initByClick(\""+leafsMsg[i].treeId+"\")' href='#tabs-"+i+"'>"+leafsMsg[i].leaftitle+"</a></li>");
		$("#leafTitle").after(a);
	}
}
</script>

