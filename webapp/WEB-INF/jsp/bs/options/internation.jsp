<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/setting-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>国际化设置</title>
	<%@ include file="/common/setting-iframe-meta.jsp"%>
	<script type="text/javascript" src="${resourcesCtx}/widgets/multiselect/jquery.multiselect.min.js"></script>
	<link   type="text/css" rel="stylesheet" href="${resourcesCtx}/widgets/multiselect/jquery.multiselect.css" />
	<script type="text/javascript" src="${resourcesCtx}/js/staff-tree.js"></script>
	
	<script type="text/javascript">
	//弹出页面
	function openPage(url,titles,opt){
		if(opt=="create"){
			$.colorbox({href:url,iframe:true, innerWidth:600, innerHeight:500,overlayClose:false,title:titles});
		}else if(opt=="update"){
			var boxes = jQuery("#InterInfoId").jqGrid("getGridParam",'selarrrow');
			if(boxes.length<=0){
				iMatrix.alert(iMatrixMessage.selectInfo);
				return;
			}else if(boxes.length>1){
				iMatrix.alert(iMatrixMessage.selectOneDataInfo);
				return;
			}else{
				$.colorbox({href:url+"&id="+boxes[0],iframe:true, innerWidth:600, innerHeight:500,overlayClose:false,title:titles});
			}
		}
	}

	//删除
	function deleteInfo(){
		var boxes = jQuery("#InterInfoId").jqGrid("getGridParam",'selarrrow');
		if(boxes.length<=0){
			iMatrix.alert(iMatrixMessage.selectInfo);
			return;
		}else{
			iMatrix.confirm({
				message:iMatrixMessage.deleteInfo,
				confirmCallback:deleteInfoOk,
				parameters:boxes
			});
		}
	}
	function deleteInfoOk(boxes){
		setPageState();
		ajaxSubmit('pageForm','${settingCtx}/options/internation-delete.htm?ids='+boxes.join(','),'message_zone',deleteCallback);
	}
	function deleteCallback(){
		showMsg();
		ajaxSubmit('pageForm','${settingCtx}/options/internation.htm?type=${type }','groups_main');
	}
	function backPage(){
		setPageState();
		ajaxSubmit('pageForm','${settingCtx}/options/internation.htm?type=${type }','groups_main');
	}

	function updateCache(){
		ajaxSubmit('pageForm','${settingCtx}/options/internation-update-cache.htm','message_zone',showMsg);
	}
	</script>
</head>
<body>
	<div class="ui-layout-center">
	<div class="opt-body">
		<aa:zone name="groups_main">
			<div class="opt-btn">
				<a class="btn" href="#" onclick="iMatrix.showSearchDIV(this);"><span><span><s:text name="formManager.search"></s:text></span></span></a>
				<a class="btn" href="#" onclick="openPage('${settingCtx}/options/internation-input.htm?type=${type }','<s:text name="menuManager.new"></s:text>','create');"><span><span><s:text name="menuManager.new"></s:text></span></span></a>
				<a class="btn" href="#" onclick="openPage('${settingCtx}/options/internation-input.htm?type=${type }','<s:text name="menuManager.update"></s:text>','update');"><span><span><s:text name="menuManager.update"></s:text> </span></span></a>
				<a class="btn" href="#" onclick="updateCache();"><span><span><s:text name="basicSetting.updateCache"></s:text></span></span></a>
				<a class="btn" href="#" onclick="deleteInfo();"><span><span ><s:text name="menuManager.delete"></s:text></span></span></a>
			</div>
			<div id="opt-content" >
				<script type="text/javascript">setTimeout('$("#message").hide("show");',3000);</script>
				<aa:zone name="message_zone">
				<div id="message"><s:actionmessage theme="mytheme" /></div>
				</aa:zone>
				<form action="" name="pageForm" id="pageForm" method="post">
					<view:jqGrid url="${settingCtx}/options/internation.htm?type=${type }" subGrid="childId" code="BS_INTERNATION" pageName="pages" gridId="InterInfoId"></view:jqGrid>
					<div style="height: 8px;"></div>
					<view:subGrid gridId="childId" url="${settingCtx}/options/internation-chiledList.htm" code="BS_INTERNATION_OPTION" pageName="interOptions"></view:subGrid>
				</form>
			</div>
		</aa:zone>
	</div>
</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script src="${resourcesCtx}/widgets/timepicker/timepicker_<%=com.norteksoft.product.util.ContextUtils.getCurrentLanguage()%>.js" type="text/javascript"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>