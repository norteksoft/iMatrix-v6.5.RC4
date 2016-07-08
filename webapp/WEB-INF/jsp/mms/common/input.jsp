<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/mms-taglibs.jsp"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>${modulePage.name }</title>
	<%@ include file="/common/mms-meta.jsp"%>
	
	<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
	
	<!--上传js-->
	<script type="text/javascript" src="${imatrixCtx}/widgets/swfupload/swfupload.js"></script>
	<script type="text/javascript" src="${imatrixCtx}/widgets/swfupload/handlers.js"></script>
	<script type="text/javascript" src="${imatrixCtx}/widgets/swfupload/otherHandlers.js"></script>
	<script type="text/javascript" src="${imatrixCtx}/wf/js/util.js"></script>
	<script type="text/javascript" src="${imatrixCtx}/wf/js/text.js"></script>
	<script type="text/javascript" src="${imatrixCtx}/wf/js/opinion.js"></script>
	
	
	<script src="${resourcesCtx}/js/workflowTag.js"></script>
	<script src="${mmsCtx}/js/form-view.js"></script>
	<script src="${mmsCtx}/js/mmsapi.js" type="text/javascript"></script>
	<script src="${resourcesCtx}/js/form.js" type="text/javascript"></script>
	<script src="${resourcesCtx}/js/opinion.js" type="text/javascript"></script>
	<script src="${mmsCtx}/js/mms-workflow.js" type="text/javascript"></script>
	<script type="text/javascript" src="${resourcesCtx}/js/staff-tree.js" ></script>
	<view:JC code="${formCode}" version="${formVersion}"></view:JC>
	
</head>
<body>
<script type="text/javascript">
		var thirdMenu = "mmm_t_th";
	</script>
  	<%@ include file="/menus/header.jsp"%>
	<div class="ui-layout-center">
		<div class="opt-body">
			<aa:zone name="default_refresh_zone">
			<style type="text/css">
				#tabs,.ui-tabs .ui-tabs-nav li,.ui-jqgrid,.ui-jqgrid .ui-jqgrid-htable th div,.ui-jqgrid .ui-jqgrid-view,.ui-jqgrid .ui-jqgrid-hdiv,.ui-jqgrid .ui-jqgrid-bdiv{ position: static; }
			</style>
			<s:if test="modulePage==null">
				<div class="opt-btn">
					<button href="#" class="btn" type="button" onclick="window.location.reload(true);"><span><span><s:text name="form.back"></s:text></span></span></button> 
				</div>
				<div id="opt-content">
					<p><s:text name="common.no.form.page"></s:text></p>
					<p>&nbsp;</p>
					<p><s:text name="common.set.page"></s:text></p>
				</div>
			</s:if><s:else>
				<input type="hidden" id="selectWorkflowUrl" value="${workflowUrl}" />
				<form id="default_refresh_form" name="default_refresh_form" method="post">
					<input type='hidden' name="pageId" value="${pageId}" id="_pageId">
				</form>
				<s:if test="(workflowUrl!=null&&workflowUrl!='') || (processId != null&&processId!='')">
						<aa:zone name="button_zone">
							<div class="opt-btn">
								<s:if test = "canSubmitTask">
									<wf:workflowButtonGroup taskId="${taskId}"></wf:workflowButtonGroup>
								</s:if>
								<button href="#" class="btn" type="button" onclick="buttonExecute(this, {execute: toListPage});" pageid="${toPageId }"><span><span><s:text name="form.back"></s:text></span></span></button> 
							</div>
						</aa:zone>
				</s:if><s:else>
					<button:button code="${modulePage.code}"></button:button>
				</s:else> 
				<div id="opt-content">
					<div id="tabs">
						<ul >
							<li><a href="#tabs-1"  onclick="ajaxAnyWhereSubmit('defaultForm1','${mmsCtx}/common/input.htm','button_zone,content_zone',_back_to_form);"><s:text name="wf.table.formInformation"></s:text></a></li>
							<s:if test="data.instance_id!=null&&data.instance_id!=''">
								<li><a href="#tabs-1"  onclick="ajaxAnyWhereSubmit('defaultForm1','${mmsCtx}/common/history.htm','button_zone,content_zone');"><s:text name="wf.table.circulationHistory"/></a></li>
							</s:if>
							<s:if test="permission.countersignResultVisible">
								<li><a href="#tabs-1"  onclick="ajaxAnyWhereSubmit('defaultForm1','${mmsCtx}/common/countersign.htm','button_zone,content_zone');"><s:text name="wf.table.theSign"/></a></li>
							</s:if>
							<s:if test="permission.voteResultVisible">
								<li><a href="#tabs-1"  onclick="ajaxAnyWhereSubmit('defaultForm1','${mmsCtx}/common/vote.htm','button_zone,content_zone');"><s:text name="wf.table.pollResults"/></a></li>
							</s:if>
						</ul>
						<form id="defaultForm1" name="defaultForm1"action="">
							<input type="hidden" name="dataId" id="dataId" value="${data.id }"  />
							<input type="hidden" name="taskId" id="taskId" value="${taskId }" />
							<input type='hidden' name="pageId" value="${pageId}" id="pageId">
						</form>
						<div id="tabs-1">
						<aa:zone name="content_zone">
							<div id="opt_message" style="display:none;"><s:actionmessage theme="mytheme" /></div>
							<input type='hidden' value="" id="_is_validate_ok"/>
							<form id="default_submit_form" name="default_submit_form" method="post">
								<input type='hidden' value='${validateString}' id="_validate_string" name="validateString" />
								<input type='hidden' value='${richString}' id="_rich_string" name="richString" />
								<input type='hidden' id="_wfDefId" name="processId" value="${processId }" />
								<input type='hidden' id="taskId" name="taskId" value="${taskId}">
								<input type='hidden' name="pageId" value="${pageId}" id="pageId">
								<input type='hidden' id="_choose_url" value="${chooseUrl}">
								<input type='hidden' id="assignee" name="assignee" />
								<input type='hidden' id="__transact" name="transact" value="">
								<input type='hidden' id="dataId" name="dataId" value="${data.id}">
								<input type="hidden" id="opinionRight"  value="${opinionRight}"/>
								<input type="hidden" id="opinionSize"  value="${opinionSize}"/>
								<input type="hidden" id="doneOpinion"  value=""/>
								<view:formView code="${formCode}" entity="${data}" version="${formVersion}"  taskId="${taskId}"></view:formView>
							</form>
							<s:if test="(workflowUrl!=null&&workflowUrl!='') || (processId != null&&processId!='')">
								<p id="opinionPos"><s:text name="common.transact.opinion"></s:text>
									<a href="#opinionPos" onclick="_view_opinion(this,'${taskId}','true');" state="1"><img id="viewImg" src="${imatrixCtx}/mms/images/x1.png"></a> 
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
							</s:if>
						</aa:zone>
						</div>
					</div>
				</div>
				<script type="text/javascript">
				//启用流程
				workflowButtonGroup.btnStartWorkflow.click= function(){
					var url = $('#selectWorkflowUrl').val();
					if("${onlyTable}"=="onlyTable"&&url == '/common/select-workflow.htm'){
						iMatrix.alert('<s:text name="common.form.one.version"></s:text>');
						return;
					}
					$("#default_submit_form").submit();
					if($('#_is_validate_ok').attr('value')=="TRUE"){
						iMatrix.customControlValidate();//自定义控件验证：日期、富文本、自定义列表
						ajaxAnyWhereSubmit("default_submit_form", webRoot+"/common/start.htm", "default_refresh_zone",__startCallback);
					}
				};
				//提交流程
				workflowButtonGroup.btnSubmitWorkflow.click = function(taskId){
					var url = $('#selectWorkflowUrl').val();
					if("${onlyTable}"=="onlyTable"&&url == '/common/select-workflow.htm'){
						iMatrix.alert('<s:text name="common.form.one.version"></s:text>');
						return;
					}
					var opinionRight = $("#opinionRight").val();
					var opinionSize = $("#opinionSize").val();
					var doneOpinion = $("#doneOpinion").val(); 
					if((opinionRight.indexOf('must')!=-1)&&(opinionSize==0)&&(doneOpinion=='')){
		                  iMatrix.alert('<s:text name="common.opinion.fill"></s:text>');
		                  return;
					}else{
						$("#default_submit_form").submit();
						if($('#_is_validate_ok').attr('value')=="TRUE"){
							iMatrix.customControlValidate();//自定义控件验证：日期、富文本、自定义列表
							ajaxAnyWhereSubmit("default_submit_form", webRoot+"/common/submit.htm", "default_refresh_zone", __submitOK);
						}
					}
				};
				//取回后保存按钮
				workflowButtonGroup.btnSaveForm.click= function(){
					$("#default_submit_form").submit();
					if($('#_is_validate_ok').attr('value')=="TRUE"){
						iMatrix.customControlValidate();//自定义控件验证：日期、富文本、自定义列表
						ajaxAnyWhereSubmit("default_submit_form", webRoot+"/common/save-task.htm", "default_refresh_zone", __startCallback);
					}
				};
				//取回后提交按钮
				workflowButtonGroup.btnSubmitTask.click= function(){
					var opinionRight = $("#opinionRight").val();
					var opinionSize = $("#opinionSize").val();
					var doneOpinion = $("#doneOpinion").val(); 
					if((opinionRight.indexOf('must')!=-1)&&(opinionSize==0)&&(doneOpinion=='')){
		                  iMatrix.alert('<s:text name="common.opinion.fill"></s:text>');
		                  return;
					}else{
						$("#default_submit_form").submit();
						if($('#_is_validate_ok').attr('value')=="TRUE"){
							$('#__transact').attr('value', 'SUBMIT');
							iMatrix.customControlValidate();//自定义控件验证：日期、富文本、自定义列表
							ajaxAnyWhereSubmit("default_submit_form", webRoot+"/common/submit-task.htm", "default_refresh_zone", __submitOK);
						}
					}
				};
				//取回后抄送
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
					
				//取回后指派
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
							data: {
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
									btnAssignCallBack();
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
						window.location.reload(true);
					});
				}
				//取回
				workflowButtonGroup.btnGetBackTask.click = function(taskId){
					$.ajax({
						data:{taskId: taskId},
						type:"post",
						url:webRoot + "/common/get-back.htm",
						beforeSend:function(XMLHttpRequest){},
						success:function(data, textStatus){
							if(data=="任务已取回"||data =="The task has been retrieved"){
								intoInput();
							}else{
								iMatrix.alert(data);
							}
						},
						complete:function(XMLHttpRequest, textStatus){},
				        error:function(){
			
						}
					});
				};

				//选择办理人
				workflowButtonGroup.btnAssignTransactor.click = function(taskId){
					workflowButtonGroup.btnSubmitWorkflow.click(taskId);
				};
				
				//选择环节
				workflowButtonGroup.btnChoiceTache.click = function(taskId){
					workflowButtonGroup.btnSubmitWorkflow.click(taskId);
				};
				function __submitOK(){
					var url = $('#_choose_url').val();
					if('choose_user'==url){
						popbox("select_transactor", 300, 400, iMatrixMessage["wf.engine.choosePeople"]);
						return;
					}
					if(url != ''){
						if(url=="OPINION"){//默认值：意见框
							//alert("默认值意见框");
							iMatrix.commonOpinion({
								taskId:$("#taskId").val(),
								callbackFun:"submitCommonOpinionForm",
								controlId:"approveOpinion"
								});
						}else if(url=="SELECT_TRANSACTOR"){//默认值：选择办理人
							iMatrix.selectTransactor(___selectTransactorFirstCallback,true,imatrixRoot);
						}else{
							url = url+'&closeFlag=false';
							$.colorbox({href:webRoot+url,iframe:true, innerWidth:700, innerHeight:500,overlayClose:false,title:iMatrixMessage["common.choose.to.deal.with.people"],onClosed:function(){intoInput();}});
						}
					}else{
						window.location.reload(true);
					}
				}

				function submitCommonOpinionForm(){
					$("#officeForm").attr("action",webRoot+"/common/save-common-opinion.htm");
					$("#officeForm").ajaxSubmit(function (id){
							window.parent.location.reload(true);
					});
				}
				
				function __startCallback(){
					__show_message('opt_message',iMatrixMessage["saveSuccess"],'onSuccess');
					_formValidate();
					getContentHeight();
				}

				$(document).ready(function(){
						$( "#tabs" ).tabs();
						_formValidate();
						addFormValidate('${validateString}','default_submit_form');
						iMatrix.autoFillOpinion('${autoFillOpinionInfo}');
						$("#workflowButtonGroup_btnStartWorkflow").html('<span><span>'+workflowButtonGroup.btnStartWorkflow.name+'</span></span>');
						$("#workflowButtonGroup_btnSubmitWorkflow").html('<span><span>'+workflowButtonGroup.btnSubmitWorkflow.name+'</span></span>');
				});
                function _back_to_form(){
                	_formValidate();
					addFormValidate('${validateString}','default_submit_form');
                }
				function intoInput(){
					ajaxAnyWhereSubmit("default_submit_form", webRoot+"/common/input.htm", "default_refresh_zone",getContentHeight);
				}
				function __choose_transctor_call_back(){
					window.location.reload(true);
				}
				</script>
				<div id="select_transactor" style="display: none;">
					<s:if test="choiceTransactor.size()>0">
					<div class="opt-btn">
						<button class="btn" type="button" onclick="selectTransactorOk();"><span><span><s:text name="menuManager.confirm"></s:text></span></span></button> 
					</div>
					<form action="#" id="select_transactor_form" name="select_transactor_form" method="post">
						<s:iterator value="choiceTransactor" id="user">
							<div style="padding: 4px 8px;"> <input type="radio" value="<s:property value="#user.key"/>" name="transactor"/><s:property value="value.name"/> </div>
						</s:iterator>
						<input name="taskId" id="transact_task_id" type="hidden" value="${taskId}"/>
					</form>
					<script type="text/javascript">
						function selectTransactorOk(){
							if($("input[name='transactor']:checked").length==1){
								ajaxAnyWhereSubmit("select_transactor_form", webRoot+"/common/submit-task.htm", "default_refresh_zone", __choose_transctor_call_back);
							}else{
								iMatrix.alert(iMatrixMessage["wf.engine.choosePeople"]);
							}
						}
					</script>
					</s:if>
				</div>
			</s:else>
			</aa:zone>
		</div>
	</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script src="${resourcesCtx}/widgets/timepicker/timepicker_<%=com.norteksoft.product.util.ContextUtils.getCurrentLanguage()%>.js" type="text/javascript"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
<script src="${resourcesCtx}/widgets/validation/dynamic.validate.js" type="text/javascript"></script>
</html>
