<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/setting-taglibs.jsp"%>
<%@ include file="/common/mms-taglibs.jsp"%>
<div id="accordion" class="basic">
	<h3><a href="${settingCtx}/sms/sms-gateway-setting.htm" id="sms_manager"><s:text name="messagePlatform.messagePlatform"></s:text></a></h3>
	<div>
		<ul class="ztree" id="sms_tree" ></ul>
	</div>
</div>
<script type="text/javascript">
	$().ready(function () {
		$("#accordion").accordion({fillSpace:true, change:accordionChange});
	});
	function accordionChange(event,ui){
		var url=ui.newHeader.children("a").attr("href");
		if(url=="${settingCtx}/sms/sms-gateway-setting.htm"){
			createjobInfoTree("sms_tree","${settingCtx}/sms/sms-tree.htm",false);//短信平台树
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
	 	if(treeId=="sms_tree"){//短信平台树
			if(currentId=="_smsGatewaySetting"){
				$("#myIFrame").attr("src","${settingCtx}/sms/sms-gateway-setting.htm");
			}else if(currentId=="_templateSetting"){
				$("#myIFrame").attr("src","${settingCtx}/sms/sms-template-setting.htm");
			}else if(currentId=="_SmsWaitTosend"){
				$("#myIFrame").attr("src","${settingCtx}/sms/sms-wait-tosend.htm");
			}else if(currentId=="_SmsLog_Send"){
				$("#myIFrame").attr("src","${settingCtx}/sms/sms-log.htm?logType=send");
			}else if(currentId=="_SmsLog_Receive"){
				$("#myIFrame").attr("src","${settingCtx}/sms/sms-log.htm?logType=receive");
			}else if(currentId=="_SmsSendMessage"){
				$("#myIFrame").attr("src","${settingCtx}/sms/sms-send-message.htm");
				//$("#myIFrame").attr("src","${mmsCtx}/form/createhtml-editor.htm");
			}else{
				if(currentId!="_SmsAuthoritySetting"){
					$("#myIFrame").attr("src","${settingCtx}/sms/sms-authority-setting.htm?systemId="+currentId);
				}
			}
		}
	}
</script>
