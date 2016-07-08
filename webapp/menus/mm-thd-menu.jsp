<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/mms-taglibs.jsp"%>

<div id="accordion" >
	<h3><a href="${mmCtx}/mm/monitor-infor.htm" id="_mm_infor"><s:text name="mm.information.monitoring"/></a></h3>
	<div>
		<ul class="ztree" id="dynamic-menu"></ul>
	</div>
	<h3><a href="${mmCtx}/mm/monitor-parmeter.htm" id="_mm_parmeter"><s:text name="mm.parameter.setting"/></a></h3>
	<div>
	</div>
</div>
<link rel="stylesheet" href="${resourcesCtx}/widgets/ztree/css/zTreeStyle/zTreeStyle.css" type="text/css">
<script type="text/javascript" src="${resourcesCtx}/widgets/ztree/js/jquery.ztree.core-3.5.js"></script>
<script type="text/javascript" src="${resourcesCtx}/js/tree.js"></script>
<script type="text/javascript">
	$(function () {
		var url=window.location.href;
		if(url.indexOf("/mm/monitor-infor.htm")>=0){
			mm_folderTreeMenu();
			$("#accordion").accordion({fillSpace:true, change:accordionChange,active: 0});
		}else if(url.indexOf("/mm/monitor-parmeter.htm")>=0){
			$("#accordion").accordion({fillSpace:true, change:accordionChange,active: 1});
		}
	});
	function accordionChange(event,ui){
		var url=ui.newHeader.children("a").attr("href");
		if(url.indexOf("/mm/monitor-infor.htm")>=0){
			mm_folderTreeMenu();
		}else if(url.indexOf("/mm/monitor-parmeter.htm")>=0){
			ajaxAnyWhereSubmit('defaultForm',url,'form_main',getContentHeight);
		}
	}

	/**
	 * 加载动态树
	 */
	function mm_folderTreeMenu(){
		$.ajaxSetup({cache:false});
		//treeId:,url:,data(静态树才需要该参数):,multiple:,callback:
		tree.initTree({treeId:"dynamic-menu",
			url:"${mmCtx}/mm/monitor-infor-tree.htm",
			type:"ztree",
			initiallySelectFirstChild:true,
			initiallySelectFirst:true,
			callback:{
					onClick:mm_treeback
				}});
	    }
		
	function mm_treeback(){
		var id = tree.getSelectNodeId();
		if(typeof(id)!='undefined'&&id.indexOf("root_")<0){
			var ids=id.split("_");
			ajaxAnyWhereSubmit('defaultForm',webRoot+'/mm/monitor-infor.htm?systemCode='+ids[0]+"&jwebType="+ids[1],'form_main',getContentHeight);
		}
	}
</script>

