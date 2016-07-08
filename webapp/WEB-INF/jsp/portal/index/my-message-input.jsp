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
	
	<script type="text/javascript" src="${resourcesCtx}/js/staff-tree.js"></script>
	<script type="text/javascript">
		isUsingComonLayout=false;
		function selectPerson(){
			var acsSystemUrl = "${portalCtx}";
//			popTree({ title :'选择人员',
//				innerWidth:'300',
//				treeType:'MAN_DEPARTMENT_TREE',
//				defaultTreeValue:'loginName',
//				leafPage:'false',
//				multiple:'true',
//				hiddenInputId:"loginNames",
//				showInputId:"userNames",
//				onlineVisible:true,
//				callBack:function(){customCallbackFun();}});
			var zTreeSetting={
					leaf: {
						enable: false
					},
					type: {
						treeType: "MAN_DEPARTMENT_TREE",
						noDeparmentUser:true,
						onlineVisible:true
					},
					data: {
						chkStyle:"checkbox",
						chkboxType:"{'Y' : 'ps', 'N' : 'ps' }"
					},
					view: {
						title: "<s:text name='select_person'/>",
						width: 300,
						height:400,
						url:imatrixRoot
					},
					feedback:{
						enable: true,
				        //showInput:"userNames",
				        append:false
					},
					callback: {
						onClose:function(){
							customCallbackFun();
						}
					}			
					};
				    popZtree(zTreeSetting);
		}

		function customCallbackFun(){
			if($("#loginNames").val()!='ALLCOMPANYID'){
				$("#loginNames").attr("value",ztree.getLoginNames());
				$("#ids").attr("value",ztree.getIds());
				var tempUserNames = ztree.getNames();
				if("所有人员" == tempUserNames){
					tempUserNames = iMatrixMessage["common.allPersonnel"];
				}
				$("#userNames").attr("value",tempUserNames);
			}
		}

		function sumbitMessages(formId,url){
			$("#"+formId).attr("action",url);
			$("#"+formId).ajaxSubmit(function (id){				
				if(id.split("-")[1]=='ONLINE_MESSAGE'){					
					parent.ajaxSubmit("defaultForm","${portalCtx}/index/my-message.htm?messageType=ONLINE_MESSAGE", "messageList");					
				}else{
					parent.ajaxSubmit("defaultForm","${portalCtx}/index/my-message.htm?messageType=SYSTEM_MESSAGE", "messageList");				
				}
				parent.$.colorbox.close();
			});
		}
	</script>
</head>
<body>
<div class="opt-body">
	<div class="opt-btn">
		<a class="btn" href="#" onclick="sumbitMessages('defaultForm','${portalCtx}/index/my-message-save.htm');"><span><span ><s:text name='portal.submit'/></span></span></a>
	</div>
	<div id="opt-content">
		<form action="" name="defaultForm" id="defaultForm" method="post">
			<input id="messageType" name="messageType" type="hidden" value="${messageType}">
			<table class="form-table-without-border">
				<tr>
					<td><s:text name='receiver'/>:<input readonly="readonly" name="userNames" id="userNames" value="${userNames}" />
								<input type="hidden" name="loginNames" id="loginNames" value="${loginNames}"/>
								<input type="hidden" name="ids" id="ids" value="${ids}"/>
								<a href="#" onclick="selectPerson();" class="small-btn"><span><span><s:text name='protal_select'/></span></span></a>
					</td>
				</tr>
				<tr>
					<td><s:text name='protal_message'/>:<textarea name="content" id="content" style="height: 350px;" onkeyup="if(this.value.length>500)this.value=this.value.substring(0,500);">${content}</textarea></td>
				</tr>
			</table>	
		</form>
	</div>
</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>