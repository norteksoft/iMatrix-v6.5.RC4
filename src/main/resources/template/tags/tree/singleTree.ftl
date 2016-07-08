<table id="searchTb"><tr><td >
	<input id="searchInput" value=""/></td><td ><a class="search-btn" href="#" onclick="search_fun('${treeId}','searchInput');" ><b class="ui-icon ui-icon-search"></b></a>
</td></tr></table>
<div id="${treeId}" class="demo">
</div>
<script type="text/javascript">
//<---------------解析树节点的分隔符-------------->
var split_one = "~~";
var split_two = "==";
var split_three = "*#";
var split_four = "|#";
var split_five = "+#";
var split_six = "~#";
var split_seven = "**";
var split_eight = "=#";
var split_nine = "~*";
var split_ten = "~+";
var $$p={
	treeId:'${treeId}',
	actionUrl:'${actionUrl}',
	searchUrl:'${searchUrl}',
	resourceCtx:'${resourceCtx}'
};
</script>
<script src="${resourceCtx}/templateJs/jstree-tag-single.js" type="text/javascript"></script>
