<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/mms-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>${modulePage.name }</title>
	<%@ include file="/common/mms-iframe-meta.jsp"%>
	<!--上传js-->
	<script type="text/javascript" src="${imatrixCtx}/widgets/swfupload/swfupload.js"></script>
	<link rel="stylesheet" href="${resourcesCtx}/widgets/wfeditor/css/wf-html5.css" type="text/css" />
    <script type="text/javascript" src="${resourcesCtx}/js/raphael-2.1.2.js"></script>
    <script type="text/javascript" src="${resourcesCtx}/widgets/wfeditor/wfeditor.js"></script>
	<script type="text/javascript" src="${imatrixCtx}/widgets/swfupload/handlers.js"></script>
	<script type="text/javascript" src="${imatrixCtx}/widgets/swfupload/otherHandlers.js"></script>
	
	<script src="${imatrixCtx}/widgets/formeditor/kindeditor.js" type="text/javascript"></script>
	
	<script src="${resourcesCtx}/widgets/timepicker/timepicker_<%=com.norteksoft.product.util.ContextUtils.getCurrentLanguage()%>.js" type="text/javascript"></script>
	<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
	<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
	
	<script type="text/javascript" src="${imatrixCtx}/widgets/workflowEditor/swfobject.js"></script>
	<script type="text/javascript" src="${imatrixCtx}/wf/js/util.js"></script>
	<script type="text/javascript" src="${imatrixCtx}/wf/js/text.js"></script>
	<script type="text/javascript" src="${imatrixCtx}/wf/js/opinion.js"></script>
	
	<script type="text/javascript" src="${resourcesCtx}/js/staff-tree.js"></script>
	<script type="text/javascript" src="${resourcesCtx}/js/custom.tree.js" ></script>
	
	<script type="text/javascript" src="${resourcesCtx}/js/workflowTag.js"></script>
	<script src="${imatrixCtx}/mms/js/mmsapi.js" type="text/javascript"></script>
	<script src="${imatrixCtx}/mms/js/mms-workflow.js" type="text/javascript"></script>
	<script src="${resourcesCtx}/js/opinion.js" type="text/javascript"></script>
	<view:JC code="${formCode}" version="${formVersion}"></view:JC>
	
	<script type="text/javascript">
	  isUsingFormLayout=false;
		$().ready(function() {
			$( "#tabs" ).tabs();
			format_Validate();
			iMatrix.autoFillOpinion('${autoFillOpinionInfo}');
		});

		function format_Validate(){
			addFormValidate($("#_validate_string").attr("value"),'default_submit_form');
			_formValidate();
		}
		
		//启用流程
		workflowButtonGroup.btnStartWorkflow.click= function(){
			$("#default_submit_form").submit();
			if($('#_is_validate_ok').attr('value')=="TRUE"){
				iMatrix.customControlValidate();//自定义控件验证：日期、富文本、自定义列表
				ajaxAnyWhereSubmit("default_submit_form", webRoot+"/common/save-task.htm", "default_refresh_zone", needChoose);
			}
		};
		//提交流程
		workflowButtonGroup.btnSubmitWorkflow.click = function(taskId){
			var opinionRight = $("#opinionRight").val();
			var opinionSize = $("#opinionSize").val();
			var doneOpinion = $("#doneOpinion").val(); 
			if((opinionRight.indexOf('must')!=-1)&&(opinionSize==0)&&(doneOpinion=='')){
                  iMatrix.alert(iMatrixMessage["common.opinion.fill"]);
			}else{
				$("#default_submit_form").submit();
				if($('#_is_validate_ok').attr('value')=="TRUE"){
					$('#__transact').attr('value', 'SUBMIT');
					iMatrix.customControlValidate();//自定义控件验证：日期、富文本、自定义列表
					ajaxAnyWhereSubmit("default_submit_form", webRoot+"/common/submit-task.htm", "default_refresh_zone", needChoose);
				}
			}
		};
		
		//保存按钮
		workflowButtonGroup.btnSaveForm.click= function(){
			$("#default_submit_form").submit();
			if($('#_is_validate_ok').attr('value')=="TRUE"){
				iMatrix.customControlValidate();//自定义控件验证：日期、富文本、自定义列表
				ajaxAnyWhereSubmit("default_submit_form", webRoot+"/common/save-task.htm", "default_refresh_zone", needChoose);
			}
		};
		//提交按钮
		workflowButtonGroup.btnSubmitTask.click= function(){
			var opinionRight = $("#opinionRight").val();
			var opinionSize = $("#opinionSize").val();
			var doneOpinion = $("#doneOpinion").val(); 
			if((opinionRight.indexOf('must')!=-1)&&(opinionSize==0)&&(doneOpinion=='')){
                  iMatrix.alert(iMatrixMessage["common.opinion.fill"]);
			}else{
				$("#default_submit_form").submit();
				if($('#_is_validate_ok').attr('value')=="TRUE"){
					$('#__transact').attr('value', 'SUBMIT');
					iMatrix.customControlValidate();//自定义控件验证：日期、富文本、自定义列表
					ajaxAnyWhereSubmit("default_submit_form", webRoot+"/common/submit-task.htm", "default_refresh_zone", needChoose);
				}
			}
		};
		//同意按钮
		workflowButtonGroup.btnApproveTask.click = function(taskId){
			var opinionRight = $("#opinionRight").val();
			var opinionSize = $("#opinionSize").val();
			var doneOpinion = $("#doneOpinion").val(); 
			if((opinionRight.indexOf('must')!=-1)&&(opinionSize==0)&&(doneOpinion=='')){
                  iMatrix.alert(iMatrixMessage["common.opinion.fill"]);
			}else{
				$("#default_submit_form").submit();
				if($('#_is_validate_ok').attr('value')=="TRUE"){
					$('#__transact').attr('value', 'APPROVE');
					iMatrix.customControlValidate();//自定义控件验证：日期、富文本、自定义列表
					ajaxAnyWhereSubmit("default_submit_form", webRoot+"/common/submit-task.htm", "default_refresh_zone", needChoose);
				}
			}
		};
		//不同意按钮
		workflowButtonGroup.btnRefuseTask.click = function(taskId){
			$("#default_submit_form").submit();
			if($('#_is_validate_ok').attr('value')=="TRUE"){
				$('#__transact').attr('value', 'REFUSE');
				iMatrix.customControlValidate();//自定义控件验证：日期、富文本、自定义列表
				ajaxAnyWhereSubmit("default_submit_form", webRoot+"/common/submit-task.htm", "default_refresh_zone", needChoose);
			}
		};

		//抄送
		workflowButtonGroup.btnCopyTache.click = function(taskId){
			var acsSystemUrl = "${mmsCtx}";
			var zTreeSetting={
					leaf: {
						enable: false
					},
					type: {
						treeType: "MAN_DEPARTMENT_TREE",
						noDeparmentUser:false,
						onlineVisible:false
					},
					data: {
						chkStyle:"checkbox",
						chkboxType:"{'Y' : 'ps', 'N' : 'ps' }"
					},
					view: {
						title: iMatrixMessage["common.cc.personnel"],
						width: 300,
						height:400,
						url:imatrixRoot,
						showBranch:true
					},
					feedback:{
						enable: true,
				                showInput:"assignee",
				                hiddenInput:"assignee",
				                append:false
					},
					callback: {
						onClose:function(){
							copyPersonCallBack();
						}
					}			
					};
				    popZtree(zTreeSetting);
			};

			function copyPersonCallBack(){
				$('#assignee').attr("value",ztree.getIds());
				$("#default_submit_form").attr("action","${mmsCtx}/common/copy-tache.htm");
				$("#default_submit_form").ajaxSubmit(function (id){
					iMatrix.alert(id);
				});
			}
			
		//指派
		workflowButtonGroup.btnAssign.click = function(taskId){
			var acsSystemUrl = "${mmsCtx}";
			var zTreeSetting={
					leaf: {
						enable: false
					},
					type: {
						treeType: "MAN_DEPARTMENT_TREE",
						noDeparmentUser:false,
						onlineVisible:false
					},
					view: {
						title: iMatrixMessage["common.task.assignments"],
						width: 300,
						height:400,
						url:imatrixRoot,
						showBranch:true
					},
					feedback:{
						enable: true,
				                showInput:"assignee",
				                hiddenInput:"assignee",
				                append:false
					},
					callback: {
						onClose:function(){
							btnAssignCallBack();;
						}
					}			
					};
				    popZtree(zTreeSetting);
			};

		function btnAssignCallBack(){
			$('#assignee').attr("value",ztree.getId());
			$("#default_submit_form").attr("action","${mmsCtx}/common/assign-tree.htm");
			$("#default_submit_form").ajaxSubmit(function (id){
				iMatrix.alert(id);
				window.parent.close();
			});
		}

		//加签
		workflowButtonGroup.btnAddCountersign.click = function(taskId){
			var acsSystemUrl = "${mmsCtx}";
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
						chkboxType:"{'Y' : 'ps', 'N' : 'ps' }"
					},
					view: {
						title: iMatrixMessage["common.select.add.counter.personnel"],
						width: 300,
						height:400,
						url:imatrixRoot,
						showBranch:true
					},
					feedback:{
						enable: true,
				                showInput:"assignee",
				                hiddenInput:"assignee",
				                append:false
					},
					callback: {
						onClose:function(){
							addCountersignCallBack();
						}
					}			
					};
				    popZtree(zTreeSetting);
			};

		function addCountersignCallBack(){
			var userIds = ztree.getIds();
			if(userIds==""||typeof(userIds)=='undefined'){
				$('#assignee').attr("value","all_user");
			}else{
				$('#assignee').attr("value",userIds);
			}
			$("#default_submit_form").attr("action","${mmsCtx}/common/add-assign.htm");
			$("#default_submit_form").ajaxSubmit(function (id){
				iMatrix.alert(id);
			});
		}
		
		//减签
		workflowButtonGroup.btnDeleteCountersign.click = function(taskId){
				custom_ztree({url:webRoot+'/common/remove-assign-tree.htm',
					onsuccess:function(){removeCountersignCallBack();},
					width:400,
					height:500,
					title:iMatrixMessage["common.select.delete.counter.personnel"],
					postData:{taskId:$("#taskId").attr("value")},
					nodeInfo:['type','name','loginName','transactorId','taskId'],
					multiple:true,
					webRoot:imatrixRoot
				});
			};

		function removeCountersignCallBack(){
			var transactorIds=getSelectValue("taskId");
			var resultNames="";
			for(var i=0;i<transactorIds.length;i++){
				if(transactorIds[i]!="company"){
					resultNames=resultNames+transactorIds[i]+",";
				}
			}
			if(resultNames.indexOf(",")>=0){
				resultNames=resultNames.substring(0,resultNames.lastIndexOf(","));
			}
			if(resultNames==""){
				iMatrix.alert(iMatrixMessage["task.thereIsOnlyOneManagerNoNeedToSign"]);
			}else{
				$('#assignee').attr("value",resultNames);
				$("#default_submit_form").attr("action","${mmsCtx}/common/remove-assign.htm");
				$("#default_submit_form").ajaxSubmit(function (id){
					iMatrix.alert(id);
				});
			}
		}
			
		function needChoose(){
			closePopbox();
			var url = $('#_choose_url').val();
			if('choose_user'==url){
				$( "#tabs" ).tabs();
				popbox("select_transactor", 300, 400, iMatrixMessage["wf.engine.choosePeople"]);
				return;
			}
			if(typeof(url)!='undefined'){
				if(url.length > 0){
					$( "#tabs" ).tabs();
					format_Validate();
					if(url=="OPINION"){//默认值：意见框
						//alert("默认值意见框");
						iMatrix.commonOpinion({
							taskId:$("#taskId").val(),
							callbackFun:"submitCommonOpinionForm",
							controlId:"approveOpinion",
							title:"意见"
							});
					}else if(url=="SELECT_TRANSACTOR"){//默认值：选择办理人
						iMatrix.selectTransactor(___selectTransactorTaskCallback,true,imatrixRoot);
					}else{
						$.colorbox({href:webRoot+$('#_choose_url').val(),iframe:true, innerWidth:700, innerHeight:500,overlayClose:false,title:iMatrixMessage["authorization.select"],onClosed:function(){format_Validate();}});
					}
				}else{
					$( "#tabs" ).tabs();
					format_Validate();
					__show_message('opt_message',iMatrixMessage['common.successful.operation'],'onSuccess');
				}
			}
		}

		function submitCommonOpinionForm(){
			$("#officeForm").attr("action",webRoot+"/common/save-common-opinion.htm");
			$("#officeForm").ajaxSubmit(function (id){
					window.parent.close();
					window.parent.location.reload(true);
			});
		}

		function ___show(){
			__show_message('opt_message');
		}
		//阅读按钮
		workflowButtonGroup.btnReadTask.click = function(){
			$("#default_submit_form").submit();
			if($('#_is_validate_ok').attr('value')=="TRUE"){
				$('#__transact').attr('value', 'READED');
				iMatrix.customControlValidate();//自定义控件验证：日期、富文本、自定义列表
				ajaxAnyWhereSubmit("default_submit_form", webRoot+"/common/submit-task.htm", "default_refresh_zone",needChoose);
			}
		};
		
		//选择办理人
		workflowButtonGroup.btnAssignTransactor.click = function(){
			format_Validate();
			$("#default_submit_form").submit();
			if($('#_is_validate_ok').attr('value')=="TRUE"){
				$('#__transact').attr('value', 'SUBMIT');
				iMatrix.customControlValidate();//自定义控件验证：日期、富文本、自定义列表
				ajaxAnyWhereSubmit("default_submit_form", webRoot+"/common/submit-task.htm", "default_refresh_zone",assignTransactorCallback);
			}
		};

		function assignTransactorCallback(){
			needChoose();
			format_Validate();
		}
		
		//选择环节
		workflowButtonGroup.btnChoiceTache.click = function(){
			$("#default_submit_form").submit();
			if($('#_is_validate_ok').attr('value')=="TRUE"){
				$('#__transact').attr('value', 'SUBMIT');
				iMatrix.customControlValidate();//自定义控件验证：日期、富文本、自定义列表
				ajaxAnyWhereSubmit("default_submit_form", webRoot+"/common/submit-task.htm", "default_refresh_zone",needChoose);
			}
		};
		
		//赞成票按钮
		workflowButtonGroup.btnVoteAgreement.click = function(){ // 投票
			$("#default_submit_form").submit();
			if($('#_is_validate_ok').attr('value')=="TRUE"){
				$('#__transact').attr('value', 'AGREEMENT');
				iMatrix.customControlValidate();//自定义控件验证：日期、富文本、自定义列表
				ajaxAnyWhereSubmit("default_submit_form", webRoot+"/common/submit-task.htm", "default_refresh_zone", needChoose);
			}
		};
		
		//反对票按钮
		workflowButtonGroup.btnVoteOppose.click = function(){
			$("#default_submit_form").submit();
			if($('#_is_validate_ok').attr('value')=="TRUE"){
				$('#__transact').attr('value', 'OPPOSE');
				iMatrix.customControlValidate();//自定义控件验证：日期、富文本、自定义列表
				ajaxAnyWhereSubmit("default_submit_form", webRoot+"/common/submit-task.htm", "default_refresh_zone", needChoose);
			}
		};
		
		//弃权按钮
		workflowButtonGroup.btnVoteKiken.click = function(){
			$("#default_submit_form").submit();
			if($('#_is_validate_ok').attr('value')=="TRUE"){
				$('#__transact').attr('value', 'KIKEN');
				iMatrix.customControlValidate();//自定义控件验证：日期、富文本、自定义列表
				ajaxAnyWhereSubmit("default_submit_form", webRoot+"/common/submit-task.htm", "default_refresh_zone", needChoose);
			}
		};
		
		// 分发按钮
		workflowButtonGroup.btnDistributeTask.click = function(){ // 分发
			$("#default_submit_form").submit();
			if($('#_is_validate_ok').attr('value')=="TRUE"){
				$('#__transact').attr('value', 'DISTRIBUTE');
				iMatrix.customControlValidate();//自定义控件验证：日期、富文本、自定义列表
				ajaxAnyWhereSubmit("default_submit_form", webRoot+"/common/submit-task.htm", "default_refresh_zone", needChoose);
			}
		};
		
		//交办指派按钮
		workflowButtonGroup.btnAssignTask.click = function(){//交办
			$("#default_submit_form").submit();
			if($('#_is_validate_ok').attr('value')=="TRUE"){
				$('#__transact').attr('value', 'ASSIGN');
				iMatrix.customControlValidate();//自定义控件验证：日期、富文本、自定义列表
				ajaxAnyWhereSubmit("default_submit_form", webRoot+"/common/submit-task.htm", "default_refresh_zone", needChoose);
			}
		};
		
		//签收任务按钮
		workflowButtonGroup.btnSignoffTask.click = function(){
			$("#default_submit_form").submit();
			if($('#_is_validate_ok').attr('value')=="TRUE"){
				$('#__transact').attr('value', 'SIGNOFF');
				iMatrix.customControlValidate();//自定义控件验证：日期、富文本、自定义列表
				ajaxAnyWhereSubmit("default_submit_form", webRoot+"/common/submit-task.htm", "default_refresh_zone", needChoose);
			}
		};

		/*
		 * 领取任务
		 */
		workflowButtonGroup.btnDrawTask.click = function(taskId){
			$("#default_submit_form").attr("action","${mmsCtx}/common/drawTask.htm");
			$("#default_submit_form").ajaxSubmit(function (id){
				if(id=='task.not.need.receive'){
					//window.location.reload(true);
					$("#drawTask_message").html("<font class=\"onError\"><nobr>不需要领取,可能已被他人领取</nobr></font>");
					$("#drawTask_message").show("show");
					setTimeout('$("#drawTask_message").hide("show");',3000);
					setTimeout('window.location.reload(false);',2000);
				}else if(id=='task.receive.success'){
					window.location.reload(false);
					//alert("领取成功");
				}else{
					//领取出错
					iMatrix.alert(iMatrixMessage["task.receiveAnError"]);
				}
			});
		};

		/*
		 * 放弃领取的任务
		 */
		workflowButtonGroup.btnAbandonTask.click = function(taskId){
			$("#default_submit_form").attr("action","${mmsCtx}/common/abandonReceive.htm");
			$("#default_submit_form").ajaxSubmit(function (id){
				if(id=="task.abandon.receive.success"){
					window.location.reload(false);
				}else{
					//放弃领取出错
					iMatrix.alert(iMatrixMessage["task.toGiveUpToGetAnError"]);
				}
			});
		};
		
		function getTaskId(){
			return $("#taskId").attr("value");
		}


	</script>
</head>
<body>
<div class="ui-layout-center">
	<div class="opt-body">
		<aa:zone name="default_refresh_zone">
			<style type="text/css">
				#tabs,.ui-tabs .ui-tabs-nav li,.ui-jqgrid,.ui-jqgrid .ui-jqgrid-htable th div,.ui-jqgrid .ui-jqgrid-view,.ui-jqgrid .ui-jqgrid-hdiv,.ui-jqgrid .ui-jqgrid-bdiv{ position: static; }
			</style>
			<aa:zone name="button_zone">
				<s:if test="!viewFlag">
				<div class="opt-btn">
					<wf:workflowButtonGroup taskId="${taskId}"></wf:workflowButtonGroup>
				</div>
				</s:if>
			</aa:zone>
			<div id="opt-content">
				<div id="opt_message" style="display:none;"><s:actionmessage theme="mytheme" /></div>
				<div id="drawTask_message" style="display:none;"></div>
				<form id="defaultForm1" name="defaultForm1"action="">
					<input type="hidden" name="id" id="id" value="${data.id }"  />
					<input type="hidden" name="taskId" id="taskId" value="${taskId }" />
					<input type='hidden' value="" id="_is_validate_ok"/>
				</form>
				<div id="tabs">
					<ul >
						<li><a href="#tabs-1"  onclick="ajaxAnyWhereSubmit('defaultForm1','${mmsCtx}/common/task.htm','button_zone,content_zone',format_Validate);"><s:text name="wf.table.formInformation"/></a></li>
						<li><a href="#tabs-1"  onclick="ajaxAnyWhereSubmit('defaultForm1','${mmsCtx}/common/history.htm','button_zone,content_zone');"><s:text name="wf.table.circulationHistory"/></a></li>
						<s:if test="permission.countersignResultVisible">
							<li><a href="#tabs-1"  onclick="ajaxAnyWhereSubmit('defaultForm1','${mmsCtx}/common/countersign.htm','button_zone,content_zone');"><s:text name="wf.table.theSign"/></a></li>
						</s:if>
						<s:if test="permission.voteResultVisible">
							<li><a href="#tabs-1"  onclick="ajaxAnyWhereSubmit('defaultForm1','${mmsCtx}/common/vote.htm','button_zone,content_zone');"><s:text name="wf.table.pollResults"/></a></li>
						</s:if>
					</ul>
					<div id="tabs-1">
						<aa:zone name="content_zone">
							<form action="" id="default_submit_form" name="default_submit_form" method="post">
								<input type="hidden" id="_choose_url" value="${chooseUrl}">
								<input type='hidden' id="__transact" name="transact" value="">
								<input type="hidden"  id="taskId" name="taskId" value="${taskId}">
								<input type='hidden' value='${validateString}' id="_validate_string" />
								<input type='hidden' value='${richString}' id="_rich_string" name="richString" />
								<input type='hidden' id="id"  value="${data.id}"/>
								<input type="hidden"  name="assignee" id="assignee" ></input>
								<input type="hidden" id="opinionRight"  value="${opinionRight}"/>
								<input type="hidden" id="opinionSize"  value="${opinionSize}"/>
								<input type="hidden" id="doneOpinion"  value=""/>
								<aa:zone name="history_refresh_zone">
									<view:formView code="${formCode}" entity="${data}" version="${formVersion}" taskId="${taskId}"></view:formView>
								</aa:zone>
								</form>
									<p id="opinionPos"><s:text name="common.transact.opinion"></s:text>
										<a href="#opinionPos" onclick="_view_opinion(this,'${taskId}',true);" state="1"><img id="viewImg" src="${imatrixCtx}/mms/images/x1.png"></a> 
									</p>
									<aa:zone name="default_opinion_zone"></aa:zone>
									
									<p id="textPos"><s:text name="common.input.text"></s:text>
									   <a href="#textPos" onclick="_view_text(this,'${taskId }');" state="1"><img id="textImg" src="${mmsCtx}/images/x1.png"></a> 
									</p>
									<aa:zone name="default_text_zone"></aa:zone>
									
									<p id="viewPos"><s:text name="common.input.attache"></s:text>
										<a href="#viewPos" onclick="_view_accessory(this,'${taskId }');" state="1"><img id="accImg" src="${mmsCtx}/images/x1.png"></a> 
									</p>
									<aa:zone name="default_accessory_zone"></aa:zone>
								<form action="" name="workflow_attachments_form" id="workflow_attachments_form" method="post">
									<input type="hidden" name="taskId" value="${taskId }"/>
									<input type="hidden" id="companyId" value="${data.company_id }"/>
								</form>
								
								<form id="officeForm1" name="officeForm1" action="" method="post">
									<input type="hidden" id="workflowId" name="workflowId" value="${workflowId}">
									<input type="hidden" id="taskId" name="taskId" value="${taskId}">
						        </form>
						        <form action="" id="opinion_form" name="opinion_form" method="post">
						        	<input type="hidden" name="taskId" value="${taskId }"/>
						        </form>
								<form action="" name="officeForm" id="officeForm" method="post">
									<input type="hidden" name="workflowId" value="${workflowId}"/>
									<input type="hidden" name="taskId" value="${taskId}"/>
									<input type="hidden" name="opinion" id="approveOpinion"/>
								</form>
						</aa:zone>
					</div>
				</div>
			</div>
			<div id="select_transactor" style="display: none;">
					<s:if test="choiceTransactor.size()>0">
					<div class="opt-btn">
						<button class="btn" type="button" onclick="selectTransactorOk();"><span><span><s:text name="menuManager.confirm"></s:text></span></span></button> 
					</div>
					<form action="#" id="select_transactor_form" name="select_transactor_form" method="post">
						<s:iterator value="choiceTransactor" id="user">
							<div style="padding: 4px 8px;"> <input type="radio" value="<s:property value="#user.key"/>" name="transactor"/><s:property value="value.name"/> </div>
						</s:iterator>
						<input name="taskId" id="transact_task_id" type="hidden" value="${taskId }"/>
					</form>
					<script type="text/javascript">
						function selectTransactorOk(){
							if($("input[name='transactor']:checked").length==1){
								$('#transact_task_id').attr('value', $('#taskId').val());
								ajaxAnyWhereSubmit("select_transactor_form", webRoot+"/common/submit-task.htm", "default_refresh_zone", closeSelectTransactor);
							}else{
								iMatrix.alert(iMatrixMessage["wf.engine.choosePeople"]);
							}
						}

						function closeSelectTransactor(){
							window.parent.close();
						}
					</script>
					</s:if>
				</div>
		</aa:zone>
	</div>
</div>
</body>

</html>
