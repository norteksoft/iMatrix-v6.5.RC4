<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/setting-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<%@ include file="/common/setting-iframe-meta.jsp"%>
	<title><s:text name="bs.interface.management"/></title>
	<script type="text/javascript">
	function showInterface(cellvalue, options, rowObject){
		var v="<a  href=\"#\" hidefocus=\"true\" onclick=\"viewInterface("+rowObject.id+");\">" + cellvalue + "</a>";
		return v;
	}
	function viewInterface(id){
		init_colorbox("${settingCtx}/options/interface-setting-view.htm?id="+id,iMatrixMessage["interfaceManager.ordinaryInferace"],500,500);
	}

	function createInterface(id){
		var url = "${settingCtx}/options/interface-setting-input.htm";
		if(typeof(id)!="undefined"&&id!=""){
			url=url+"?id="+id;
		}
		init_colorbox(url,iMatrixMessage["interfaceManager.ordinaryInferace"],"","",true,refreshListData);
	}

	function refreshListData(){
		jQuery("#page").trigger("reloadGrid");
	}
	function deleteInterface(){
		var boxes = jQuery("#page").jqGrid("getGridParam",'selarrrow');
		if(boxes==null||boxes==""){
			iMatrix.alert(iMatrixMessage.selectInfo);
		}else{
			iMatrix.confirm({
				message:iMatrixMessage.deleteInfo,
				confirmCallback:deleteInterfaceOk,
				parameters:boxes
			});
		}
	}
	function deleteInterfaceOk(boxes){
		$("#ids").attr("value",boxes.join(","));
		ajaxSubmit("defaultForm",webRoot+"/options/interface-setting-delete.htm", "interface-zones",deleteCallback);
	}
	function deleteCallback(){
		showMsg();
		refreshListData();
	}

	function updateInterface(){
		var boxes = jQuery("#page").jqGrid("getGridParam",'selarrrow');
		if(boxes==null||boxes==""){
			iMatrix.alert(iMatrixMessage.selectInfo);
		}else if(boxes.length>1){
			iMatrix.alert(iMatrixMessage.selectOneInfo);
		}else{
			createInterface(boxes[0]);
		}
	}
	function changeInterfaceState(){
		var ids = jQuery("#page").getGridParam('selarrrow');
		if(ids==''||ids==null){
			iMatrix.alert(iMatrixMessage.selectInfo);
			return;
		}
		iMatrix.confirm({
			message:iMatrixMessage["interfaceManager.confrimStartOrForbidden"],
			confirmCallback:changeInterfaceStateOk,
			parameters:ids
		});
	}
	function changeInterfaceStateOk(ids){
		$.post(webRoot+"/options/interface-setting-deploy.htm?ids="+ids.join(","), "", function(data) {
        	$("#message").html("<font class=\"onSuccess\"><nobr>"+data+"</nobr></font>");
			showMsg("message");
			jQuery("#page").jqGrid().trigger("reloadGrid");
		});
	}
	</script>
</head>

<body>
	<div class="ui-layout-center">
		<div class="opt-body">
			<form action="" name="defaultForm" id="defaultForm" method="post">
				<input name="ids" id="ids" type="hidden"></input>
			</form>
			<aa:zone name="interface-zones">
				<div class="opt-btn">
					<button class="btn" onclick="iMatrix.showSearchDIV(this);"><span><span><s:text name="formManager.search"></s:text></span></span></button>
						<button class="btn" onclick='createInterface();' ><span><span ><s:text name="menuManager.new"></s:text></span></span></button>
						<button class="btn" onclick="updateInterface();"><span><span ><s:text name="menuManager.update"></s:text></span></span></button>
						<button class='btn' onclick="changeInterfaceState();" ><span><span><s:text name="formManager.startOrForbidden"></s:text></span></span></button>
						<button class="btn" onclick="deleteInterface();"><span><span ><s:text name="menuManager.delete"></s:text></span></span></button>
				</div>
				<div id="opt-content" >
						<div id="message" style="display: none;"><s:actionmessage theme="mytheme" /></div>
						<form action="${settingCtx}/options/interface-setting-list-data.htm" name="pageForm" id="pageForm" method="post">
								<view:jqGrid url="${settingCtx}/options/interface-setting-list-data.htm" code="BS_INTERFACE_SETTING" gridId="page" pageName="page"></view:jqGrid>
						</form>
				</div>
			</aa:zone>
		</div>	
	</div>
</body>

</html>