<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/wf-taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<title>退回</title>
		<%@ include file="/common/wf-colorbox-meta.jsp"%>
			
		<script type="text/javascript">

		function goback(workflowIds, form){
			var taskName =getTaskName();
			if(taskName != ""){
				$("#okBtn").attr("disabled","disabled");
				$("#cancelBtn").attr("disabled","disabled");
				$.post("${wfCtx }/engine/task-volumeBack.htm", "workflowIds=" + workflowIds + "&backto=" + taskName+"&wfdId=${wfdId}", postSuccess);
			}
		}
		function postSuccess(msg){
			msg=msg+"";
			if(msg=="OK"){
				$("#back-msg").html("<s:text name='wf.taskJumpOk'></s:text>");
				$("#back-msg").show("show");
				setTimeout('$("#back-msg").hide("show");',2000);
				window.parent.backViewClose($("#wfdId").attr("value"),"${position}");
				window.parent.$.colorbox.close();
			}else if(msg=="RETURN_URL"){
				window.parent.taskJumpAssignTransactorVolume("${workflowIds }",getTaskName(),$("#wfdId").attr("value"),"${position}");
			}else{
				$("#okBtn").removeAttr("disabled");
				$("#cancelBtn").removeAttr("disabled");
				$("#back-msg").html(msg);
				$("#back-msg").show("show");
				setTimeout('$("#back-msg").hide("show");',3000);
			}
		}

		function getTaskName(){
			var rds = $("input[name='backto']");
			var taskName = "";
			for(var i = 0; i < rds.length; i++){
				if($(rds[i]).attr("checked")){
					taskName = $(rds[i]).attr("value");
				}
			}
			return taskName;
		}
		</script>
	</head>
	
	<body onload="getContentHeight();">
	 <div class="ui-layout-center">
		 <div class="opt-body">
				<div class="opt-btn">
					<s:if test="canBackTo.size() != 0">
						<security:authorize ifAnyGranted="wf_task_go_back">
							<button class='btn' onclick="goback('${workflowIds }');" hidefocus="true" id="okBtn"><span><span><s:text name="menuManager.confirm"></s:text></span></span></button>
						</security:authorize>
					</s:if>
					<button class='btn' onclick="window.parent.$.colorbox.close();" hidefocus="true" id="cancelBtn"><span><span><s:text name="menuManager.back"></s:text></span></span></button>
				</div>
				<div id="back-msg" style="margin: 5px 10px;color: red;" class="tabDiv"></div>
				<div id="opt-content">
					<div id="form-info" style="margin: 16px 10px;" class="tabDiv">
						<form id="goBackform"></form>
						<input id="wfdId" name="wfdId" value="${wfdId }" type="hidden"/>
						<input id="workflowIds" type="hidden" name="workflowIds" value="${workflowIds }"/>
						<table>
						<s:if test="canBackTo && canBackTo.size() == 0">
							<tr>
								<td>
								<s:text name="wf.taskJumpError"></s:text>
								</td>
							</tr>
						</s:if><s:else>
							<s:iterator value="canBackTo" id="toTask">
								<tr>
									<td>
										<input value="${toTask}" type="radio" name="backto"/> ${toTask}
									</td>
								</tr>
							</s:iterator>
						</s:else>
						</table>
					</div>
				</div>
			</div>
	   </div>
	</body>
</html>