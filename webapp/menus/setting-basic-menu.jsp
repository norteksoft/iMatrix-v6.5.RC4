<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/setting-taglibs.jsp"%>
<div id="accordion" class="basic">
	<h3><a href="${settingCtx}/rank/list-data.htm" id="rank-manager"><s:text name="basicSetting.rankManager"></s:text></a></h3>
	<div></div>
	<h3><a href="${settingCtx}/options/option-group.htm" id="option-group"><s:text name="basicSetting.optionGroup"></s:text></a></h3>
	<div>
		<ul class="ztree" id="option_content"></ul>
	</div>
	<h3><a href="${settingCtx}/holiday/holiday.htm" id="mms-holiday"><s:text name="basicSetting.mmsHoliday"></s:text></a></h3>
	<div>
		<ul class="ztree" id="holiday_tree" ></ul>
	</div>
	<h3><a href="${settingCtx}/options/import-definition.htm" id="import-manager"><s:text name="basicSetting.importManager"></s:text></a></h3>
	<div></div>
	 
	<h3><a href="${settingCtx}/options/internation.htm?type=internation.type.menu.resource" id="internation-manager"><s:text name="basicSetting.internationManager"></s:text></a></h3>
	<div>
		<s:iterator value="@com.norteksoft.bs.options.enumeration.InternationType@values()" var="internationType">
			<div <s:if test="#internationType.code=='internation.type.menu.resource'">class="four-menu-selected"</s:if><s:else>class="four-menu"</s:else>>
				<a class="leftCol" id="" onclick="interationOnclick('${settingCtx}/options/internation.htm?type=${code} ',this);" style="margin-top: 10px;"><s:text name="%{code}"></s:text></a>
			</div>
		</s:iterator>
	</div>
	<h3><a href="${settingCtx}/signature/signature.htm" id="signature-manager"><s:text name="basicSetting.signatureManager"></s:text></a></h3>
	<div></div>
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
		if(url=="${settingCtx}/options/option-group.htm"){
			createjobInfoTree("option_content","${settingCtx}/options/system-tree.htm");
		}else if(url=="${settingCtx}/holiday/holiday.htm"){
			createjobInfoTree("holiday_tree","${settingCtx}/holiday/holiday-tree.htm",false);
		}
		$("#myIFrame").attr("src",ui.newHeader.children("a").attr("href"));
	}

	//创建页面树菜单
	function createjobInfoTree(treeId,url,initiallySelectFirstChild){
		$.ajaxSetup({cache:false});
		//treeId:,url:,data(静态树才需要该参数):,multiple:,callback:
		tree.initTree({treeId:treeId,
			url:url,
			type:"ztree",
			initiallySelectFirstChild:initiallySelectFirstChild,
			initiallySelectFirst:true,
			callback:{
					onClick:selectNode
				}});
	}

	function selectNode(){
		var currentId = tree.getSelectNodeId();
		var treeId = tree.treeId;
		if(treeId=="option_content"){
			if(currentId!="all_system"){
				$("#myIFrame").attr("src","${settingCtx}/options/option-group.htm?sysId="+currentId);
			}else{
				$("#myIFrame").attr("src","${settingCtx}/options/option-group.htm");
			}
		}else if(treeId=="holiday_tree"){
			var branchId=currentId.split("_");
			if(branchId[0]=="COMPANY"){
				$("#myIFrame").attr("src","${settingCtx}/holiday/holiday.htm?nodeType="+branchId[0]);
			}else if(branchId[0]=="BRANCHES"){
				$("#myIFrame").attr("src","${settingCtx}/holiday/holiday.htm?nodeType="+branchId[0]+"&branchId="+branchId[1]);
			}else{
				$("#myIFrame").attr("src","${settingCtx}/holiday/holiday.htm?nodeType="+branchId[0]);
			}
		}
	}
	
	function interationOnclick(url,obj){
		$(obj).parent().parent().children(".four-menu-selected").addClass("four-menu");
		$(obj).parent().parent().children(".four-menu-selected").removeClass("four-menu-selected");
		$(obj).parent().removeClass("four-menu");
		$(obj).parent().addClass("four-menu-selected");
		$("#myIFrame").attr("src",url);
	}
</script>
