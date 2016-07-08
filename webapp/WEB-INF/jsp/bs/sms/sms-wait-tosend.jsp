<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/setting-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<%@ include file="/common/setting-iframe-meta.jsp"%>
	<title>待发送列表</title>
	<script type="text/javascript">
	//删除
	function deleteSms(){
		var boxes = jQuery("#page").jqGrid("getGridParam",'selarrrow');
		if(boxes==null || boxes==""){
			iMatrix.alert(iMatrixMessage.selectInfo);
		}else{
			iMatrix.confirm({
				message:iMatrixMessage.deleteInfo,
				confirmCallback:deleteSmsOk,
				parameters:boxes
			});
		}
	}
	function deleteSmsOk(boxes){
		$("#ids").attr("value",boxes.join(","));
		ajaxSubmit("defaultForm",webRoot+"/sms/sms-wait-tosend-delete.htm", "defaultZone",deleteCallback);
	}
	//删除回调
	function deleteCallback(){
		showMsg();
		refreshListData();
	}
	//回调
	function refreshListData(){
		jQuery("#page").trigger("reloadGrid");
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
						<button class="btn" onclick="deleteSms();"><span><span ><s:text name="basicSetting.delete"></s:text></span></span></button>
				</div>
				<div id="opt-content" >
						<div id="message" style="display: none;"><s:actionmessage theme="mytheme" /></div>
						<form action="${settingCtx}/sms/sms-wait-tosend.htm" name="pageForm" id="pageForm" method="post">
								<view:jqGrid url="${settingCtx}/sms/sms-wait-tosend.htm" code="BS_SMS_WAIT_TOSEND" gridId="page" pageName="page"></view:jqGrid>
						</form>
				</div>
			</aa:zone>
		</div>	
	</div>
</body>

</html>