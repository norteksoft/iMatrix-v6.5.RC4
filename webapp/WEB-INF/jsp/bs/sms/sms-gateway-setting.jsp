<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/setting-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<%@ include file="/common/setting-iframe-meta.jsp"%>
	<title>短信网关设置</title>
	<script type="text/javascript">
	function showInterface(cellvalue, options, rowObject){
		var v="<a  href=\"#\" hidefocus=\"true\" onclick=\"viewInterface("+rowObject.id+");\">" + cellvalue + "</a>";
		return v;
	}
	function viewInterface(id){
		init_colorbox("${settingCtx}/options/interface-setting-view.htm?id="+id,iMatrixMessage["interfaceManager.ordinaryInferace"],500,500);
	}
	//新建
	function createSmsGatewaySet(){
		var url = "${settingCtx}/sms/sms-gateway-setting-input.htm";
		init_colorbox(url,iMatrixMessage["interfaceManager.smsgatewayNew"],500,450,true,refreshListData);
	}
	//回调
	function refreshListData(){
		jQuery("#page").trigger("reloadGrid");
	}

	//删除
	function deleteSmsGatewaySet(){
		var boxes = jQuery("#page").jqGrid("getGridParam",'selarrrow');
		if(boxes==null || boxes==""){
			iMatrix.alert(iMatrixMessage.selectInfo);
		}else{
			iMatrix.confirm({
				message:iMatrixMessage.deleteInfo,
				confirmCallback:deleteSmsGatewaySetOk,
				parameters:boxes
			});
		}
	}
	function deleteSmsGatewaySetOk(boxes){
		$("#ids").attr("value",boxes.join(","));
		ajaxSubmit("defaultForm",webRoot+"/sms/sms-gateway-setting-delete.htm", "defaultZone",deleteCallback);
	}
	//删除回调
	function deleteCallback(){
		showMsg();
		refreshListData();
	}
	//修改
	function updateSmsGatewaySet(){
		var boxes = jQuery("#page").jqGrid("getGridParam",'selarrrow');
		if(boxes==null || boxes==""){
			iMatrix.alert(iMatrixMessage.selectInfo);
		}else if(boxes.length > 1){
			iMatrix.alert(iMatrixMessage.selectOnlyOneInfo);
		}else{
			var url = "${settingCtx}/sms/sms-gateway-setting-input.htm?id="+boxes[0];
			init_colorbox(url,iMatrixMessage["interfaceManager.smsgatewayUpdate"],500,450,true,refreshListData);
		}
	}
	//启用禁用网关
	function changeGatewayState(){
		var ids = jQuery("#page").getGridParam('selarrrow');
		if(ids=='' || ids==null){
			iMatrix.alert(iMatrixMessage.selectInfo);
			return;
		}
		iMatrix.confirm({
			message:iMatrixMessage["interfaceManager.confrimStartOrForbidden"],
			confirmCallback:changeGatewayStateOk,
			parameters:ids
		});
	}
	function changeGatewayStateOk(ids){
		$.post(webRoot+"/sms/sms-gateway-setting-changeGatewayState.htm?ids="+ids.join(","), "", function(data) {
        	$("#message").html("<font class=\"onSuccess\"><nobr>"+data+"</nobr></font>");
			showMsg("message");
			jQuery("#page").jqGrid().trigger("reloadGrid");
		});
	}
	function updateconfig(ts1,cellval,opts,rwdat,_act){
		var v="<a  href=\"#\" hidefocus=\"true\" onclick=\"xiangxi('"+opts["id"]+"','"+opts["gatewayName"]+"');\">" + iMatrixMessage["interfaceManager.configure"] + "</a>";
		return v;
	}
	/**
	 *详细 
	 */
	function xiangxi(id,gatewayName){
		var url=webRoot+"/sms/sms-gateway-setting-config.htm?id="+id+"&gatewayName="+gatewayName;
		init_colorbox(url,iMatrixMessage["interfaceManager.configure"],"","",true,refreshListData);
	}
	</script>
</head>

<body>
	<div class="ui-layout-center">
		<div class="opt-body">
			<form action="" name="defaultForm" id="defaultForm" method="post">
				<input name="ids" id="ids" type="hidden"></input>
			</form>
			<aa:zone name="defaultZone">
				<div class="opt-btn">
					<button class="btn" onclick="iMatrix.showSearchDIV(this);"><span><span><s:text name="formManager.search"></s:text></span></span></button>
						<button class="btn" onclick='createSmsGatewaySet();' ><span><span ><s:text name="menuManager.new"></s:text></span></span></button>
						<button class="btn" onclick="updateSmsGatewaySet();"><span><span ><s:text name="menuManager.update"></s:text></span></span></button>
						<button class='btn' onclick="changeGatewayState();" ><span><span><s:text name="messagePlatform.geteControl"></s:text></span></span></button>
						<button class="btn" onclick="deleteSmsGatewaySet();"><span><span ><s:text name="basicSetting.delete"></s:text></span></span></button>
				</div>
				<div id="opt-content" >
						<div id="message" style="display: none;"><s:actionmessage theme="mytheme" /></div>
						<form action="${settingCtx}/sms/sms-gateway-setting.htm" name="pageForm" id="pageForm" method="post">
								<view:jqGrid url="${settingCtx}/sms/sms-gateway-setting.htm" code="BS_SMS_GATEWAY_SETTING" gridId="page" pageName="page"></view:jqGrid>
						</form>
				</div>
			</aa:zone>
		</div>	
	</div>
</body>

</html>