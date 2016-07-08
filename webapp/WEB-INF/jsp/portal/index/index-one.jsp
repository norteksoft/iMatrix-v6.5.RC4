<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/portal-taglibs.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html >
<head>
	<title>${webpage.i18Name}</title>
	<%@ include file="/common/portal-meta.jsp"%>
	<script src="${resourcesCtx}/js/jquery.timers-1.2.js" type="text/javascript"></script>
	<script src="${portalCtx}/js/portal_<%=com.norteksoft.product.util.ContextUtils.getCurrentLanguage()%>.js" type="text/javascript"></script>
	<script src="${portalCtx}/js/index.js" type="text/javascript"></script>
	<script src="${portalCtx}/js/layout.js" type="text/javascript"></script>
	<script src="${resourcesCtx}/js/myMessage.js" type="text/javascript"></script>
	<style type="text/css">
		#header-resizer img.addpage{ float: left;  margin: 4px 6px;cursor: pointer;}
		#header-resizer img.addpage:hover{ background-color: transparent; border: none;}
		#header-resizer img.editpage{ float: left; margin: 10px 2px 2px 6px; cursor: pointer; display: inline;}
		#header-resizer img.editpage:hover{ background-color: transparent; border: none;}
		#header-resizer ul li span span a{ float: left; }
		.palace-widget{ width: 100%; }
		.leadTable th,.leadTable td{white-space:nowrap;}
		.ui-layout-center{overflow:auto;*padding-right:16px;overflow-x:hidden;}
		
		
		.ui-dialog-titlebar a.ui-dialog-titlebar-close span.ui-icon-closethick{float: right;*margin-top: -20px;}
		.remassage-title{width: 75%;padding-left:20px;background: url("../../images/reply-mail.png")  no-repeat left 50%;}
		.remassage-title-p{width: 75%;padding-left:20px;background: url("../../images/reply-pmail.png")  no-repeat left 50%;}
		.remassage-name {width: 25%;text-align:center;min-width: 110px;}
		
		.div-tb{float:left;width:120px;height:20px;margin:0 5px;text-align: center;}
	</style>
	
	<script type="text/javascript">
	$(document).ready(function () {
		$.fn.EasyWidgets({
			callbacks : {
				onChangePositions : function(positions){
					if(positions!=$('#widgetPosition').val()){
						$.post(webRoot+"/index/index-savePositions.htm", { webpageId: "${webpage.id}", positions: positions },
							function(data){
								var posStrs = positions.split("|");
								var newPos = positions;
								if(posStrs.length==1){//1栏
									newPos = newPos+"|widget-place-center=|widget-place-right=";
								}else if(posStrs.length==2){//2栏
									newPos = posStrs[0]+"|widget-place-center=|"+posStrs[1];
								}
								$('#widgetPosition').val(newPos);
							});
					}
				},
				onClose : function (link, widget){
					var id=widget.attr('id').split('-')[1];
					var positions = deletePostion(id);
					$.post(webRoot+"/index/index-delete.htm", { webpageId: "${webpage.id}", widgetId: id, positions: positions },
							function(data){
								$('#widgetPosition').val(positions);
							});
				},
				onRefreshPositions : function(){
					return $('#widgetPosition').val();
				},
				onAdd : function(widget, placeId){  },
				onEdit : function(link, widget){
					var webpageId = $("#webpageId").val();
					_widget_parameter_set(widget,webpageId);
					//widget.find('.widget-editbox').html('<input type="button" value="Close" class="widget-close-editbox">');
				},
				onCancelEdit : function(link, widget){
					widget.find('.widget-editbox').html('');
				}
			},
			i18n : {
			      editText : '<span class="edit"> </span>',
			      closeText : '<span class="close"> </span>',
			      extendText : '<span class="extend"> </span>',
			      collapseText : '<span class="collapse"> </span>',
			      cancelEditText : '<span class="cancel"> </span>',
			      editTitle : iMatrixMessage['edit.widget'],
			      closeTitle : iMatrixMessage['close.widget'],
			      confirmMsg : iMatrixMessage['determine.the.close.small.window'],
			      cancelEditTitle : iMatrixMessage['cancel.edit.widget'],
			      extendTitle : iMatrixMessage['extend.widget'],
			      collapseTitle : iMatrixMessage['collapse.widget']
			    }
		});
		loadWidgetContents();
	});


	function loadWidgetContents(){
		//获得widgetDiv中以identifierwidget-开头的div集合
		var __widgetIdInputs = $("#widgetDiv").find("div[id^='identifierwidget-']");
		var _widgetIds = "";
		for(var i=0;i<__widgetIdInputs.length;i++){
			var divId=$(__widgetIdInputs[i]).attr("id");
			var _widgetId = divId.split("-")[1];
			var contentDivId = "widget-content-"+_widgetId;
			var iframeable = $("#"+contentDivId).attr("iframeable");
			if(iframeable!="true"){
				if(_widgetIds==""){
					_widgetIds = _widgetId;
				}else{
					_widgetIds = _widgetIds+","+_widgetId;
				}
			}
		}
		if(_widgetIds!=""){
			loadWidgetContent(_widgetIds, "",1);
		}
	}
	
	function loadWidgetContent(widgetIds, contentDivId,pageNo){
		loadWidgetDivContent(widgetIds,pageNo);
	}
	function loadWidgetDivContent(widgetIds,pageNo){
		var webpageId=$("#_webpageId").attr("value");
		if(webpageId==""||typeof webpageId=='undefined'){
			webpageId = $("#webpageId").attr("value");
		}
		$.ajax({
			data:{widgetIdStrs:widgetIds, webpageId:webpageId,pageNo:pageNo},
			type:"post",
			async: true,
			url:webRoot+"/index/index-input.htm",
			success:function(data, textStatus){
				//data:{widgetId1:html}@#$%{widgetId2:html}@#$%....};
				var results= data.split("}@#$%");
				for(var i=0;i<results.length;i++){
					if(results[i]!=""&&typeof(results[i])!='undefined'){
						var widgetId = results[i].substring(results[i].indexOf("{")+1,results[i].indexOf(":"));
						var html = results[i].substring(results[i].indexOf(":")+1);
						//alert("data="+data);
						if(html=="error"){
							$("#widget-content-"+widgetId).html('<p style="text-align:center;margin:0;"><span style="color:red;font-size:20px;"><img src="../../images/loaderror1.png"><b>'+iMatrixMessage["portal.sorry.widget.erro"]+'</b></span></p>');
						}else{
							//alert("contentDivId="+contentDivId);
							$("#widget-content-"+widgetId).html('<div class="contentleadTable" >'+html+'</div>');
						}
					}
				}
					
			},
			error:function(data){
				//data:widgetId1,widgetId2,....小窗体id的集合
				//获得widgetDiv中以identifierwidget-开头的div集合
				var __widgetIdInputs = $("#widgetDiv").find("div[id^='identifierwidget-']");
				for(var i=0;i<__widgetIdInputs.length;i++){
					var divId=$(__widgetIdInputs[i]).attr("id");
					var _widgetId = divId.split("-")[1];
					var contentDivId = "widget-content-"+_widgetId;
					$("#"+contentDivId).html('<p style="text-align:center;margin:0;"><span style="color:red;font-size:20px;"><img src="../../images/loaderror1.png"><b>'+iMatrixMessage["portal.sorry.widget.erro"]+'</b></span></p>');
				}
			}
			});
	}
	</script>
</head>
<body onclick="bodyClick();$('#sysTableDiv').hide();$('#styleList').hide();">
	<form id="webpageForm" > <input type="hidden" id="deleteWebpageId" name="webpageId" value=""/> </form>
	<form id="taskChangeLeafForm" ><input type="hidden" id="task_detail_leaf" value=""/>  </form>
	<%@ include file="/menus/header.jsp"%>
	<div id="tabsDiv" class="flag">
    	<ul><li><a href="#"onClick="alterWebpage(this);bodyClick();"><s:text name="ModifyTab" /></a></li></ul>
        <ul><li><a href="#" onClick="delPage(this);"><s:text name="DeleteTab" /></a></li></ul>
    </div>
    <div id="tabsDivDefault" class="flag">
    	<ul><li><a href="#" onClick="alterWebpage(this);bodyClick();"><s:text name="ModifyTab" /></a></li></ul>
    </div>
	
	<div id="secNav">
		<ul>
			<s:iterator value="webPages" id="wp" status="st">
				<li id="webpage_${id}" class='<s:if test="#st.Last">last</s:if> <s:if test="id==webpage.id">sec-selected</s:if>' >
					<s:if test="!#wp.acquiescent">
						<span><span><a href="${portalCtx}${url}?webpageId=${id}">${i18Name}</a>
						<a style="margin-top: 4px" class="ui-icon  ui-icon-gear editpage" onclick="tabsClick(this,'${id}');stopBubble(event);"></a>
						</span></span>
					</s:if><s:else>
						<span><span><a href="${portalCtx}${url}?webpageId=${id}">${i18Name}</a>
						<a style="margin-top: 4px" class="ui-icon  ui-icon-gear editpage" onclick="tabsDefaultClick(this,'${id}');stopBubble(event);"></a>
						</span></span>
					</s:else>
				</li>
			</s:iterator>
		</ul>
		<img onclick="addPage();" class="addpage" alt="添加页签" src="../../images/add.png">
		<div class="sec-forms">
			<b class="pemessage"></b><a href="#"  onclick="openMessage();" ><s:text name="selfmessage"/></a>
			<b class="public-setting"></b><a href="#"  onclick="baseSetting();" ><s:text name="selfsetting"/></a>
			<security:authorize ifAnyGranted="show-register-widget">
			<b class="loginforms"></b><a href="#" onclick="registerWidget();"><s:text name="registerForm"/></a>
			</security:authorize>
			<b class="addforms"></b><a href="#"  onclick="addWidget();"><s:text name="addForm"/></a>
			<security:authorize ifAnyGranted="portal_index_add_theme">
			<b class="addforms addwg"></b><a class="addwg" href="#"  onclick="addTheme();"><s:text name="addTheme"/></a>
			</security:authorize>
		</div>
		<div title="隐藏" onclick="headerChange(this);" class="hid-header"></div>
	</div>
	
	
	<div class="ui-layout-center">
		<aa:zone name="webpage_zone">
		<div id="widgetDiv">
			<form id="widgetForm" action="${portalCtx}/index/index-saveWidgetToPortal.htm" method="post">
				<input type="hidden" id="webpageId" name="webpageId" value="${webpage.id}"/>
				<input type="hidden" id="widgetPosition" name="positions" value="${webpage.widgetPosition}"/>
				<input type="hidden" id="_widgetCode" name="widgetCode" value=""/>
				<input type="hidden" id="_position" name="position" value=""/>
				<input type="hidden" id="currentlanguage" value="<%=com.norteksoft.product.util.ContextUtils.getCurrentLanguage()%>"/>
			</form>
			<div id="widget-place-left" class="widget-place palace-widget">
				<s:iterator value="webpage.leftWidgets" status="status" var="leftPos">
					<s:if test="borderVisible">
						<div class="widget movable collapsable removable  closeconfirm editable" id="identifierwidget-${id}">
							<div class="widget-header">
								<h3>${nameil8}</h3>
							</div>
							<div class="widget-editbox"></div>
							<div class="widget-content" id="widget-content-${id}" iframeable="${iframeable }" <s:if test="widgetHeight!=null">style="height:${widgetHeight}px;"</s:if>>
								<s:if test="iframeable">
									<iframe  id="contentIFrame-${id}"  src="${tempUrl }"  style="height:100%;" frameborder="0" allowtransparency="no" >
									</iframe>
									<s:if test="widgetHeight==null">
									<script>
									$(document).ready(function () {
										$("#widget-content-${id}").css("height","500px");
									});
									</script>
									</s:if>
								</s:if><s:else>
									<table style="width: 100%;"><tr><td align="center"><img alt="" src="../../images/loading.gif"/>&emsp;<s:text name="index.load.widget.info"></s:text></td></tr></table>
								</s:else>
							</div>
							<b class="xbottom"><b class="xb5"></b><b class="xb4"></b><b class="xb3"></b><b class="xb2"></b><b class="xb1"></b></b>
						</div>
					</s:if><s:else>
						<div id="identifierwidget-${id}" style="margin-top: 5px;">
							<div id="widget-content-${id}" iframeable="${iframeable }" <s:if test="widgetHeight!=null">style="height:${widgetHeight}px;"</s:if>>
								<s:if test="iframeable">
									<iframe  id="contentIFrame-${id}"  src="${tempUrl }"  style="height:100%;" frameborder="0" allowtransparency="no" >
									</iframe>
									<s:if test="widgetHeight==null">
									<script>
									$(document).ready(function () {
										$("#widget-content-${id}").css("height","500px");
									});
									</script>
									</s:if>
								</s:if><s:else>
									<table style="width: 100%;" id="loadTb-${id}"><tr><td align="center"><img alt="" src="../../images/loading.gif"/>&emsp;<s:text name="index.load.widget.info"></s:text></td></tr></table>
								</s:else>
							</div>
						</div>
					</s:else>
				</s:iterator>
			</div>  
		</div>
		 </aa:zone>
	</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script src="${resourcesCtx}/widgets/timepicker/timepicker_<%=com.norteksoft.product.util.ContextUtils.getCurrentLanguage()%>.js" type="text/javascript"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>