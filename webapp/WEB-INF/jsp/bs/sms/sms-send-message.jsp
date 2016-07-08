<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/setting-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<%@ include file="/common/setting-iframe-meta.jsp"%>
	<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
	<title>通用短信发送</title>
	<script type="text/javascript">
	$(document).ready(function(){
		validateSave();
	});
	
	function validateSave(){
		$("#smsSettingForm").validate({
			submitHandler: function() {
				ajaxSubmit("smsSettingForm",webRoot+"/sms/sms-send-message-save.htm", "smsSettingZone",saveCallback);
			},
			rules: {
				content: "required",
				receiver: "required"
			},
			messages: {
				content: iMatrixMessage["menuManager.required"],
				receiver: iMatrixMessage["menuManager.required"]
			}
		});
	}
	
	function saveCallback(){
		showMsg();
		validateSave();
	}
	//保存	
	function submitSetting(){
		var content = $("#receiver").attr("value");//所有人员/将格式标准化
		
		var reg2 = new RegExp("(,)","g"); 
		content = content.replace(reg2,";");
		var reg3 = new RegExp("(，)","g"); 
		content = content.replace(reg3,";");
		var reg4 = new RegExp("(;)","g"); 
		content = content.replace(reg4,";");
		var reg5 = new RegExp("(；)","g"); 
		content = content.replace(reg5,";");
		var reg6 = new RegExp("( )","g"); 
		content = content.replace(reg6,";");

		var reg8 = new RegExp("(。)","g"); 
		content = content.replace(reg8,";");
		
		$("#receiver").attr("value",content);
	    $("#smsSettingForm").submit();
	}


	function opt(){
		$("#message").html("");
		roleAddUser();
	}
	function roleAddUser(){
		var zTreeSetting={
			leaf: {
				enable: false
			},
			type: {
				treeType: "MAN_DEPARTMENT_TREE",
				noDeparmentUser:true,
				onlineVisible:false
			},
			data: {
				chkStyle:"checkbox",
				chkboxType:"{'Y' : 'ps', 'N' : 'ps' }",
				branchIds:"${manageBranchesIds}"
			},
			view: {
				title: iMatrixMessage["user.selectStaff"],
				width: 300,
				height:400,
				url:imatrixRoot,
				showBranch:true
			},
			feedback:{
				enable: true,
               showThing:"fk_user_id",
               append:false
			},
			callback: {
				onClose:function(){
					$("#ids").attr("value",ztree.getIds());
					var receiver = ztree.getNames();
					var internationReceiver = receiver;
					if("所有人员"==receiver){
						receiver = "\\${common.allPersonnel}";
						internationReceiver = iMatrixMessage["common.allPersonnel"];
					}
					$("#receiver").attr("value",receiver);
					$("#internationReceiver").attr("value",internationReceiver);
				}
			}			
			};
		    popZtree(zTreeSetting);
	}

	
	</script>
</head>
<body onload="">
<div class="ui-layout-center">
<div class="opt-body">
	<div class="opt-btn">
		<a class="btn" href="#" onclick="submitSetting();"><span><span><s:text name="messagePlatform.send"></s:text></span></span></a>
	</div>
	<div id="opt-content" >
		<aa:zone name="smsSettingZone">
			<div id="message" style="display: none;"><s:actionmessage theme="mytheme" /></div>
			
			<form action="" name="smsSettingForm" id="smsSettingForm" method="post">
				<input type="hidden" value="" name="ids" id="ids"/>	
				<table class="form-table-without-border">
				
					<tr>
						<td class="content-title" style="width: 130px;"><s:text name="messagePlatform.receiver"></s:text>：</td>
						<td> 
							<input name="internationReceiver" style="width: 400px;" id="internationReceiver" maxlength="500" value="${internationReceiver }"/> 
							<input type="hidden" name="receiver" style="width: 400px;" id="receiver" maxlength="500" value="${receiver }"/> 
							<a href="#"  class='btn' onclick="opt()"><span><span><s:text name="messagePlatform.addPerson"></s:text></span></span></a>
							<span class="required">*</span>
							
						</td>
					</tr>
					
					<tr>
						<td class="content-title" style="width: 130px;"><s:text name="messagePlatform.smsContent"></s:text>：</td>
						<td> 
							<textarea name="content" cols="5" rows="15" id="content" style="border: 1 solid #888888;LINE-HEIGHT:18px;padding: 3px;width:400px;">${content }</textarea>
					 		<span class="required">*</span>
						</td>
					</tr>
					
				</table>
			</form>
		</aa:zone>
	</div>
</div>
</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script src="${resourcesCtx}/widgets/timepicker/timepicker_<%=com.norteksoft.product.util.ContextUtils.getCurrentLanguage()%>.js" type="text/javascript"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
</html>