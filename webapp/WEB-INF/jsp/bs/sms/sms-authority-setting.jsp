<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/setting-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<%@ include file="/common/setting-iframe-meta.jsp"%>
	<title>收发接口设置</title>
	<script type="text/javascript">
	//新建
	function createSmsAuthoritySet(){
		var url = "${settingCtx}/sms/sms-authority-setting-input.htm?systemId="+$("#systemId").val();
		init_colorbox(url,iMatrixMessage["messagePlatform.sendReceiveSetNew"],600,400,true,refreshListData);
	}
	//回调
	function refreshListData(){
		jQuery("#page").trigger("reloadGrid");
	}

	//删除
	function deleteSmsAuthoritySet(){
		var boxes = jQuery("#page").jqGrid("getGridParam",'selarrrow');
		if(boxes==null || boxes==""){
			iMatrix.alert(iMatrixMessage.selectInfo);
		}else{
			iMatrix.confirm({
				message:iMatrixMessage.deleteInfo,
				confirmCallback:deleteSmsAuthoritySetOk,
				parameters:boxes
			});
		}
	}
	function deleteSmsAuthoritySetOk(boxes){
		$("#ids").attr("value",boxes.join(","));
		ajaxSubmit("defaultForm",webRoot+"/sms/sms-authority-setting-delete.htm", "defaultZone",deleteCallback);
	}
	//启用禁用
	function changeStatus(){
		var boxes = jQuery("#page").jqGrid("getGridParam",'selarrrow');
		if(boxes==null || boxes==""){
			iMatrix.alert(iMatrixMessage.selectInfo);
		}else{
			iMatrix.confirm({
				message:iMatrixMessage["pageManager.confirmStartOrForbidden"],
				confirmCallback:changeStatusOk,
				parameters:boxes
			});
		}
	}
	function changeStatusOk(boxes){
		$("#ids").attr("value",boxes.join(","));
		ajaxSubmit("defaultForm",webRoot+"/sms/sms-authority-setting-changeStatus.htm", "defaultZone",deleteCallback);
	}
	//删除回调
	function deleteCallback(){
		showMsg();
		refreshListData();
	}
	//修改
	function updateSmsAuthoritySet(){
		var boxes = jQuery("#page").jqGrid("getGridParam",'selarrrow');
		if(boxes==null || boxes==""){
			iMatrix.alert(iMatrixMessage.selectInfo);
		}else if(boxes.length > 1){
			iMatrix.alert(iMatrixMessage.selectOneInfo);
		}else{
			var url = "${settingCtx}/sms/sms-authority-setting-input.htm?id="+boxes[0];
			init_colorbox(url,iMatrixMessage["messagePlatform.sendReceiveSetUpdate"],600,400,true,refreshListData);
		}
	}
	</script>
</head>

<body>
	<div class="ui-layout-center">
		<div class="opt-body">
			<form action="" name="defaultForm" id="defaultForm" method="post">
				<input name="ids" id="ids" type="hidden"></input>
				<input name="systemId" id="systemId" type="hidden" value="${systemId }"></input>
			</form>
			<aa:zone name="defaultZone">
				<div class="opt-btn">
					<button class="btn" onclick="iMatrix.showSearchDIV(this);"><span><span><s:text name="formManager.search"></s:text></span></span></button>
						<button class="btn" onclick='createSmsAuthoritySet();' ><span><span ><s:text name="menuManager.new"></s:text></span></span></button>
						<button class="btn" onclick="updateSmsAuthoritySet();"><span><span ><s:text name="menuManager.update"></s:text></span></span></button>
						<button class="btn" onclick="deleteSmsAuthoritySet();"><span><span ><s:text name="menuManager.delete"></s:text></span></span></button>
						<button class="btn" onclick="changeStatus();"><span><span ><s:text name="formManager.startOrForbidden"></s:text></span></span></button>
				</div>
				<div id="opt-content" >
						<div id="message" style="display: none;"><s:actionmessage theme="mytheme" /></div>
						<form action="" name="pageForm" id="pageForm" method="post">
								<view:jqGrid url="${settingCtx}/sms/sms-authority-setting.htm?systemId=${systemId }" code="BS_SMS_AUTHORITY_SETTING" gridId="page" pageName="page"></view:jqGrid>
						</form>
				</div>
			</aa:zone>
		</div>	
	</div>
</body>

</html>