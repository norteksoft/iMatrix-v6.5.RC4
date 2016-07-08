<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
	<head>
		<title></title>
		<%@include file="/common/meta.jsp" %>
		<#if popupable?if_exists>
		<script src="${resourcesCtx}/widgets/validation/validate-all-1.0.js" type="text/javascript"></script>
		<script src="${resourcesCtx}/widgets/validation/dynamic.validate.js" type="text/javascript"></script>
		<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
		<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
		<#if containWorkflow?if_exists>
		<script type="text/javascript" src="${resourcesCtx}/js/workflowTag.js"></script>
		<script type="text/javascript" src="${resourcesCtx}/js/opinion.js"></script>
		</#if>
			<script type="text/javascript">
				<#if containWorkflow?if_exists>
				var buttonSign="";
				workflowButtonGroup.btnStartWorkflow.click = function(taskId){
					save${entityName}('${ctx}/${namespace}/${entityAttribute}-save.htm');
				};
				workflowButtonGroup.btnSubmitWorkflow.click = function(taskId){
					submit${entityName}('${ctx}/${namespace}/${entityAttribute}-submitProcess.htm');
				};
				
				function submit${entityName}(url){
					buttonSign="firstSubmit";
					$('#taskTransact').val("SUBMIT");
					$("#inputForm").attr("action",url);
					$("#inputForm").submit();
				}
				function submitCallback(){
					if(buttonSign!="firstSubmit"){//第一环节提交不在此处显示“提交成功”信息，在下面的dealResult中显示
						showMsg();
					}
					getContentHeight();
					if(buttonSign=="firstSubmit"){
						dealResult($("#submitResult").val());
					}
				}
				
				//提交后回调事件
				function dealResult(id){
				if(id!=""&&typeof id!='undefined'&&id!=null){
					var ids=id.split(";");
					$("#id").attr("value",ids[0]);
					if(ids[1]!=undefined&&ids[1]!=null&&ids[1]!=''){
						//根据后台返回id判断执行操作，这里代码根据需求写
						if(ids[1]=="OPINION"){//默认值：意见框
							iMatrix.commonOpinion({
								taskId:$("#taskId").val(),
								callbackFun:"submitCommonOpinionForm",
								controlId:"_common_opinion",
								title:"意见"
								});
						}else if(ids[1]=="SELECT_TRANSACTOR"){//默认值：选择办理人
							iMatrix.selectTransactor(selectTransactorCallback,false);
						}
					}else{
						showMsg();
						window.parent.$.colorbox.close();
					}
				}
			}

				function submitCommonOpinionForm(){
					$("#inputForm").attr("action","${ctx}/${namespace}/${entityAttribute}-completeInteractiveTask.htm");
					$("#inputForm").ajaxSubmit(function (id){
						window.parent.$.colorbox.close();
					});
				}
				function selectTransactorCallback(){
					var taskId = $("#taskId").attr("value");
					var userId = ztree.getId();
					$.ajax({
						data:{transactorIdStr:userId,taskId:taskId},
						cache:false,
						type:"post",
						url:"${ctx}/${namespace}/${entityAttribute}-completeInteractiveTask.htm",
						success:function(data, textStatus){
							window.parent.$.colorbox.close();
						}
					});
				}
				
				</#if>
				
				function save${entityName}(url){
					buttonSign="";
					$("#inputForm").attr("action",url);
					$("#inputForm").submit();
				}
				//验证
				function validate${entityName}(){
					$("#inputForm").validate({
						submitHandler: function() {
							var cansave = iMatrix.getSubTableDatas('inputForm');
							if(cansave){
								$(".opt_btn").find("button.btn").attr("disabled","disabled");
								__parseCustomDateTypeValue();//日期格式化保存处理
								ajaxSubmit('inputForm','','main',submitCallback);
							}
						},
						errorPlacement:function(error,element){
							error.appendTo(element.parent().children("span:contains('*')"));
						},
						rules: {
							
						},
						messages: {
							
						}
					});
				}
				
				<#if !containWorkflow?if_exists>
					function submitCallback(){
						showMsg();
						getContentHeight();
						if(buttonSign=="firstSubmit"){
							window.parent.$.colorbox.close();
						}
					}
				</#if>
			</script>
		</#if>
	</head>
	
	<body onload="getContentHeight();"  onunload="destroyUploadControl();">
		<div class="ui-layout-center">
		<div class="opt-body">
					<div class="opt-btn">
						<#if containWorkflow?if_exists>
							<s:if test="taskId==null || (workflowInfo!=null && workflowInfo.processState.code=='process.unsubmit')">
								<wf:workflowButtonGroup taskId="${taskId }"></wf:workflowButtonGroup>
							</s:if>
						<#else>
						<button class='btn' onclick="save${entityName}('${ctx}/${namespace}/${entityAttribute}-save.htm')"><span><span>保存</span></span></button>
						</#if>
						<#if popupable?if_exists>
							<button class='btn' onclick='window.parent.$.colorbox.close();'><span><span>返回</span></span></button>
						<#else>
							<button class='btn' onclick='setPageState();ajaxSubmit("defaultForm","${ctx}/${namespace}/${entityAttribute}-list.htm","main");'><span><span>返回</span></span></button>
						</#if>
					</div>
				<aa:zone name="main">
					<div id="opt-content" class="form-bg">
					<div id="message" style="display:none;"><s:actionmessage theme="mytheme" /></div>
						<form  id="inputForm" name="inputForm" method="post" action="">
							<input type="hidden" name="id" id="id" value="${id }"/>
							<input type="hidden" name="taskId" id="taskId" value="${taskId}" />
							<input type="hidden" name="submitResult" id="submitResult" value="${submitResult}"/>
							<input  type="hidden"  name="opinion" id="_common_opinion" />
							<input type="hidden" name="taskTransact" id="taskTransact" value="${taskTransact }"/>
							<#if containWorkflow?if_exists>
								<grid:formView code="${formCode}" entity="${entityObject}" taskId="${taskId}"></grid:formView>
							<#else>
								<grid:formView code="${formCode}" entity="${entityObject}"></grid:formView>
							
							</#if>
						</form>
					</div>
					<script type="text/javascript">
					$(document).ready(function(){
						//验证表单字段
						addFormValidate('${fieldPermission}','inputForm');
						iMatrix.autoFillOpinion('${autoFillOpinionInfo}');
						<#if popupable?if_exists>
						validate${entityName}();
						</#if>
					});
				</script>
				</aa:zone>
			</div>
			</div>
	</body>
</html>