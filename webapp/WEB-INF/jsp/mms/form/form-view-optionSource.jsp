<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/mms-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>数据表</title>
	<%@ include file="/common/mms-iframe-meta.jsp"%>
	<script src="${resourcesCtx}/js/tree.js" type="text/javascript"></script>
	<script type="text/javascript">
		$(document).ready(function(){
			create_tree();
		});
		function create_tree(){
			$.ajaxSetup({cache:false});
			tree.initTree({treeId:"treeDiv",
				url: webRoot+"/form/option-source.htm",
				type:"ztree",
				initiallySelect:"",
				callback:{
						//onClick:select
				}});
		}

		function select(){
			var data = tree.getSelectNodeId();
			var node=tree.getSelectNode();
			if(typeof(data)!='undefined'&& node!=""&&node.pId!=null){
				window.parent.$("#optionGroupCode").attr("value",node.name);
				window.parent.$("#optionGroupId").attr("value",data);
				window.parent.$.colorbox.close();
			}else{
				iMatrix.alert(iMatrixMessage["formManager.optionInfo"]);
			}
		}
	</script>
	<style type="text/css">
	.form-table-without-border td input{
		width:200px;
	}
	</style>
</head>
<body onload="getContentHeight();">
<div class="ui-layout-center">
<div class="opt-body">
	<form id="defaultForm" name="defaultForm"action="" method="post" ></form>
	<aa:zone name="main_zone">
		<div class="opt-btn">
			<button class="btn" onclick="select();"><span><span ><s:text name="menuManager.confirm"></s:text></span></span></button>
		</div>
		<div id="opt-content">
			<table>
				<tr>
				<td >
					<input id="searchroleInput" /></td><td ><a class="search-btn" href="#" onclick="tree.searchNodes($('#searchroleInput').val());" ><b class="ui-icon ui-icon-search"></b></a>
				</td>
				</tr>
			</table>
			<ul id="treeDiv" class="ztree"></ul>
		</div>
	</aa:zone>
</div>
</div>
</body>
</html>
