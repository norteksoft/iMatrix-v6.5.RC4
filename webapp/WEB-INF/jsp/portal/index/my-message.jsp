<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/common/portal-taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<title><s:text name='selfMsgManager'/></title>
	<%@ include file="/common/portal-meta.jsp"%>
	<link   type="text/css" rel="stylesheet" href="${resourcesCtx}/widgets/jqgrid/ui.jqgrid.css" />
	<script src="${resourcesCtx}/widgets/jqgrid/jqgrid_<%=com.norteksoft.product.util.ContextUtils.getCurrentLanguage()%>.js" type="text/javascript"></script>
	<script type="text/javascript" src="${resourcesCtx}/widgets/jqgrid/jqgrid-all-1.0.js"></script>
	<script type="text/javascript" src="${resourcesCtx}/widgets/jqgrid/jqGrid.custom.js"></script>
	<script type="text/javascript" src="${resourcesCtx}/js/public.js"></script>
	<script type="text/javascript" src="${resourcesCtx}/js/custom.tree.js" ></script>
	
	<script type="text/javascript">
	function deleteMessages(formName,url,zone){
		var boxes = jQuery("#messagesTableId").jqGrid("getGridParam",'selarrrow');
		if(boxes!=''){
			url=url+"&ids="+boxes;
			setPageState();
			ajaxSubmit(formName,url,zone);
		}else{
			$("#message").show();
			$("#message").html('<font class="onError"><nobr><s:text name="selectOneMsg"/></nobr></font>');
			return;
		}
	}
	
	function setMessagesState(formName,url,zone,bl){
		var boxes = jQuery("#messagesTableId").jqGrid("getGridParam",'selarrrow');
		if(boxes!=''){
			url=url+"&ids="+boxes+"&bl="+bl;
			ajaxSubmit(formName,url,zone);
		}else{
			$("#message").show();
			$("#message").html('<font class="onError"><nobr><s:text name="selectOneMsg"/></nobr></font>');
			return;
		}
	}
	
	function deleteAllMessages(formName,url,zone){
			ajaxSubmit(formName,url,zone);
	}

	function openMessage(messageType){
		$.colorbox({href:webRoot+"/index/my-message-input.htm?messageType="+messageType,iframe:true, innerWidth:450, innerHeight:500,overlayClose:false,title:"<s:text name='sendMessage'/>"});
	}

	function messageTitleStrong(cellvalue, options, rowObject){
		if(rowObject.bl=="true"){
			return "<strong>"+cellvalue+"</strong>";
		}else{
			return cellvalue;
		}
		
	}

	function changeMessageType(type,id){
		$("div[id^='bs_']").removeClass('west-notree-selected');
		$('#bs_'+id).addClass('west-notree-selected');
		ajaxSubmit("defaultForm","${portalCtx}/index/my-message.htm?messageType="+type, "messageList");
	}

	function viewMessageState(ts1,cellval,opts){
		//var state = $('#_docTypeIcon').attr('value');
		//var iconArr = icons.split(',');
		//for(var i=0;i<iconArr.length;i++){
		//	if(iconArr[i].indexOf(opts.suffix+":")>=0){
		//		var haveImg=(webRoot+iconArr[i].split(':')[1]).replace(webRoot+'/iconfolder/','');
		//		if(haveImg==''){
		//			return "&nbsp;";
		//		}else{
		//			return '<img src="'+webRoot+iconArr[i].split(':')[1]+'"></img>';
		//		}
		//	}
		//}
		if(opts.visible=='true' || opts.visible=='1'){//未读
			return '<img src="'+imatrixRoot+'/images/unread.png"></img>';
		}else{//已读
			return '<img src="'+imatrixRoot+'/images/reply-mail.png"></img>';
		}
	}

	function messageInfor(ts1,cellval,opts){
		var v = ts1;
		if(opts.messageType=='online.message'){
			v = "<a href='#' onclick='getMessageInfor("+opts.id+");'>"+ts1+"</a>";
		}
		return v;
	}
	//查看在线消息
	function getMessageInfor(id){
		$.colorbox({href:webRoot+'/index/my-message-view.htm?isOpen=true&id='+id,iframe:true, innerWidth:800, innerHeight:400,
			overlayClose:false,
			onClosed:function(){
				ajaxSubmit("defaultForm","${portalCtx}/index/my-message.htm?messageType=ONLINE_MESSAGE", "messageList");
			},
			title:"<s:text name='View_the_online_news' />"
			});
	}
	</script>
</head>
<body>
<form action="" name="defaultForm" id="defaultForm" method="post"></form>
	<div class="ui-layout-west">
		<div style="display: block; height: 10px;"></div>
		<div id="bs_system" class="west-notree" onclick='changeMessageType("SYSTEM_MESSAGE","system");'><a href="#"><s:text name="system_message"/></a></div>
		<div id="bs_online" class="west-notree" onclick='changeMessageType("ONLINE_MESSAGE","online");'><a href="#"><s:text name="online_message"/></a></div>
	</div>
	<div class="ui-layout-center">
		<div class="opt-body">
			<aa:zone name="messageList">
				<div class="opt-btn">
					<a class="btn" href="#" onclick="deleteMessages('defaultForm','${portalCtx}/index/my-message-delete.htm?messageType=${messageType}','messageList');"><span><span ><s:text name="portal.delete"/></span></span></a>
					<a class="btn" href="#" onclick="deleteAllMessages('defaultForm','${portalCtx}/index/my-message-deleteAll.htm?messageType=${messageType}','messageList');"><span><span ><s:text name="portal.empty"/></span></span></a>
						<a class="btn" href="#" onclick="setMessagesState('defaultForm','${portalCtx}/index/my-message-stateAll.htm?messageType=${messageType}','messageList',false);"><span><span ><s:text name="mark_as_read"/></span></span></a>
						<a class="btn" href="#" onclick="setMessagesState('defaultForm','${portalCtx}/index/my-message-stateAll.htm?messageType=${messageType}','messageList',true);"><span><span ><s:text name="mark_as_unread"/></span></span></a>
					<s:if test="messageType=='ONLINE_MESSAGE'">
					<a class="btn" href="#" onclick="openMessage('ONLINE_MESSAGE');"><span><span ><s:text name="launched_online_news"/></span></span></a>
					</s:if><s:else>
					<security:authorize ifAnyGranted="portal_send_system_message">
						<a class="btn" href="#" onclick="openMessage('SYSTEM_MESSAGE');"><span><span ><s:text name="launched_system_news"/></span></span></a>
					</security:authorize>
					</s:else>
				</div>
				<div id="opt-content">
					<script type="text/javascript">setTimeout('$("#message").hide("show");',3000);</script>
					<div id="message"><s:actionmessage theme="mytheme" /></div>	
					<grid:jqGrid url="${portalCtx}/index/my-message.htm?messageType=${messageType}" code="PORTAL_MESSAGES" gridId="messagesTableId" pageName="pages" ></grid:jqGrid>
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