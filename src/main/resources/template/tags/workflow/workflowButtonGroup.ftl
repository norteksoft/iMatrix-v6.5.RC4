	<script type="text/javascript">
		WorkflowButtonGroup.webRoot='${webRoot}'
	</script>
	<#if taskId==0||workflow.processState.code=='process.unsubmit'>
		<#if taskId==0>
			<button href="#" class='btn' id="${workflowButtonGroupName}_btnStartWorkflow" onclick="${workflowButtonGroupName}.btnStartWorkflow.click(#{taskId},'','${submitForm}')" ></button>
			<button href="#" class='btn' id="${workflowButtonGroupName}_btnSubmitWorkflow"  onclick="${workflowButtonGroupName}.btnSubmitWorkflow.click(#{taskId},'','${submitForm}')" ></button>
			<script type="text/javascript">
					$("#${workflowButtonGroupName}_btnStartWorkflow").html('<span><span>'+${workflowButtonGroupName}.btnStartWorkflow.name+'</span></span>');<#--${workflowButtonGroupName}.btnStartWorkflow.name-->
					$("#${workflowButtonGroupName}_btnSubmitWorkflow").html('<span><span>'+${workflowButtonGroupName}.btnSubmitWorkflow.name+'</span></span>');<#--${workflowButtonGroupName}.btnSubmitWorkflow.name-->
			</script>
		<#else>
			<#if task.active == 0 >
				<button href="#" class='btn' id="${workflowButtonGroupName}_btnStartWorkflow" onclick="${workflowButtonGroupName}.btnStartWorkflow.click(#{taskId},'','${submitForm}')" ></button>
				<button href="#" class='btn' id="${workflowButtonGroupName}_btnSubmitWorkflow"  onclick="${workflowButtonGroupName}.btnSubmitWorkflow.click(#{taskId},'','${submitForm}')" ></button>
				<script type="text/javascript">
						$("#${workflowButtonGroupName}_btnStartWorkflow").html('<span><span>'+${workflowButtonGroupName}.btnStartWorkflow.name+'</span></span>');<#--${workflowButtonGroupName}.btnStartWorkflow.name-->
						$("#${workflowButtonGroupName}_btnSubmitWorkflow").html('<span><span>'+${workflowButtonGroupName}.btnSubmitWorkflow.name+'</span></span>');<#--${workflowButtonGroupName}.btnSubmitWorkflow.name-->
				</script>
			</#if>
			<#if task.active == 1>
				<#--转向指定办理人页面按钮-->
				<button href="#"  id="${workflowButtonGroupName}_btnAssignTransactor"  class="btn" onclick="${workflowButtonGroupName}.btnAssignTransactor.click(#{taskId},'','${submitForm}')"></button>
				<script type="text/javascript">
					$("#${workflowButtonGroupName}_btnAssignTransactor").html('<span><span>'+${workflowButtonGroupName}.btnAssignTransactor.name+'</span></span>');<#--${workflowButtonGroupName}.btnAssignTransactor.name-->
				</script>
			</#if>
			<#if task.active == 6>
				<#--转向选择环节页面按钮-->
				<button href="#" class='btn' id="${workflowButtonGroupName}_btnSaveForm"    onclick="${workflowButtonGroupName}.btnSaveForm.click(#{taskId},'','${submitForm}')" ></button>
				<script type="text/javascript">
					$("#${workflowButtonGroupName}_btnSaveForm").html('<span><span>'+${workflowButtonGroupName}.btnSaveForm.name+'</span></span>');<#--${workflowButtonGroupName}.btnSaveForm.name-->
				</script>
				<button href="#" class='btn' id="${workflowButtonGroupName}_btnChoiceTache"   onclick="${workflowButtonGroupName}.btnChoiceTache.click(#{taskId},'','${submitForm}')"></button>
				<script type="text/javascript">
					$("#${workflowButtonGroupName}_btnChoiceTache").html('<span><span>'+${workflowButtonGroupName}.btnChoiceTache.name+'</span></span>');<#--${workflowButtonGroupName}.btnChoiceTache.name-->
				</script>
			</#if>
		</#if>
	<#else>
		<#if task.active == 0  && task.processingMode.condition!="阅">
			<#--保存表单信息按钮-->
			<button href="#" class='btn' id="${workflowButtonGroupName}_btnSaveForm"  <#if !task.showButtonSave>style="display: none;"</#if>  onclick="${workflowButtonGroupName}.btnSaveForm.click(#{taskId},'','${submitForm}')" ></button>
			<script type="text/javascript">
				$("#${workflowButtonGroupName}_btnSaveForm").html('<span><span>'+'${task.saveButton}'+'</span></span>');<#--${workflowButtonGroupName}.btnSaveForm.name-->
			</script>
		</#if>
		<#if task.active == 1>
			<#--转向指定办理人页面按钮-->
			<button href="#" id="${workflowButtonGroupName}_btnAssignTransactor"  class="btn" onclick="${workflowButtonGroupName}.btnAssignTransactor.click(#{taskId},'','${submitForm}')"></button>
			<script type="text/javascript">
				$("#${workflowButtonGroupName}_btnAssignTransactor").html('<span><span>'+'${task.submitButton}'+'</span></span>');<#--${workflowButtonGroupName}.btnAssignTransactor.name-->
			</script>
		</#if>
		<#if task.active == 6>
			<#--转向选择环节页面按钮-->
			<button href="#" class='btn' id="${workflowButtonGroupName}_btnSaveForm"  <#if !task.showButtonSave>style="display: none;"</#if>  onclick="${workflowButtonGroupName}.btnSaveForm.click(#{taskId},'','${submitForm}')" ></button>
			<script type="text/javascript">
				$("#${workflowButtonGroupName}_btnSaveForm").html('<span><span>'+'${task.saveButton}'+'</span></span>');<#--${workflowButtonGroupName}.btnSaveForm.name-->
			</script>
			<button href="#" class='btn' id="${workflowButtonGroupName}_btnChoiceTache"   onclick="${workflowButtonGroupName}.btnChoiceTache.click(#{taskId},'','${submitForm}')"></button>
			<script type="text/javascript">
				$("#${workflowButtonGroupName}_btnChoiceTache").html('<span><span>'+'${task.submitButton}'+'</span></span>');<#--${workflowButtonGroupName}.btnChoiceTache.name-->
			</script>
		</#if>
		
		<#if task.active==4>
			<#--领取任务-->
			<button href="#" <#if !task.showButtonDraw>style="display: none;"</#if> class='btn' id="${workflowButtonGroupName}_btnDrawTask"  class="btnStyle " onclick="${workflowButtonGroupName}.btnDrawTask.click(#{taskId},'','${submitForm}');"></button>
			<script type="text/javascript">
				$("#${workflowButtonGroupName}_btnDrawTask").html('<span><span>'+'${task.drawButton}'+'</span></span>');<#--${workflowButtonGroupName}.btnDrawTask.name-->
			</script>
		</#if>
		
		<#if task['drawTask'] && task.active == 0>
			<button href="#" <#if !task.showButtonAbandon>style="display: none;"</#if> class='btn' id="${workflowButtonGroupName}_btnAbandonTask"   onclick="${workflowButtonGroupName}.btnAbandonTask.click(#{taskId},'','${submitForm}');" ></button>
			<script type="text/javascript">
				$("#${workflowButtonGroupName}_btnAbandonTask").html('<span><span>'+'${task.abandonButton}'+'</span></span>');<#--${workflowButtonGroupName}.btnAbandonTask.name-->
			</script>
		</#if>
		
		<#if task.active == 0 && "阅" == task.processingMode.condition>
			<#--完成阅办任务-->
			<button href="#" class='btn' id="${workflowButtonGroupName}_btnReadTask"  onclick="${workflowButtonGroupName}.btnReadTask.click(#{taskId},'READED','${submitForm}');" class="btnStyle "></button>
			<script type="text/javascript">
				$("#${workflowButtonGroupName}_btnReadTask").html('<span><span>'+'${task.readButton}'+'</span></span>');<#--${workflowButtonGroupName}.btnReadTask.name-->
			</script>
		</#if>	
		
		
		<#if task.active == 2 
				&& task.processingMode.condition != "阅" 
				&& task.processingMode.condition !=  "会签式" 
				&& task.processingMode.condition !=  "投票式" 
				&&task.processingMode.condition !=  "分发" >
			<button href="#" <#if !task.showButtonGetBack>style="display: none;"</#if> class='btn' id="${workflowButtonGroupName}_btnGetBackTask"  onclick="${workflowButtonGroupName}.btnGetBackTask.click(#{taskId},'','${submitForm}')" class="btnStyle "></button>
			<script type="text/javascript">
				$("#${workflowButtonGroupName}_btnGetBackTask").html('<span><span>'+'${task.backButton}'+'</span></span>');<#--${workflowButtonGroupName}.btnGetBackTask.name-->
			</script>
		</#if>
		<#if task.active == 0 &&task.processingMode.condition == "编辑式">
			<button href="#" class='btn'  id="${workflowButtonGroupName}_btnSubmitTask"   onclick="${workflowButtonGroupName}.btnSubmitTask.click(#{taskId},'SUBMIT','${submitForm}')" ></button>
			<script type="text/javascript">
				$("#${workflowButtonGroupName}_btnSubmitTask").html('<span><span>'+'${task.submitButton}'+'</span></span>');<#--${workflowButtonGroupName}.btnSubmitTask.name-->
			</script>
		</#if>
		<#if task.active == 0 
			 && (task.processingMode.condition == "审批式" 
			 || task.processingMode.condition == "会签式")>
				<button href="#" class='btn' id="${workflowButtonGroupName}_btnApproveTask"   onclick="${workflowButtonGroupName}.btnApproveTask.click(#{taskId},'APPROVE','${submitForm}')"></button>
				<button href="#" class='btn' id="${workflowButtonGroupName}_btnRefuseTask"   onclick="${workflowButtonGroupName}.btnRefuseTask.click(#{taskId},'REFUSE','${submitForm}')"></button>
				<script type="text/javascript">
					$("#${workflowButtonGroupName}_btnApproveTask").html('<span><span>'+'${task.agreeButton}'+'</span></span>');<#--${workflowButtonGroupName}.btnApproveTask.name-->
					$("#${workflowButtonGroupName}_btnRefuseTask").html('<span><span>'+'${task.disagreeButton}'+'</span></span>');<#--${workflowButtonGroupName}.btnRefuseTask.name-->
				</script>
			<#if task.processingMode.condition == '会签式'>
				<button href="#" <#if !task.showButtonAddCounter>style="display: none;"</#if> class='btn' id="${workflowButtonGroupName}_btnAddCountersign"  onclick="${workflowButtonGroupName}.btnAddCountersign.click(#{taskId},'','${submitForm}')" class="btnStyle "></button>
				<button href="#" <#if !task.showButtonDelCounter>style="display: none;"</#if> class='btn' id="${workflowButtonGroupName}_btnDeleteCountersign"  onclick="${workflowButtonGroupName}.btnDeleteCountersign.click(#{taskId},'','${submitForm}')" class="btnStyle "></button>
				<script type="text/javascript">
					$("#${workflowButtonGroupName}_btnDeleteCountersign").html('<span><span>'+'${task.removeSignerButton}'+'</span></span>');<#--${workflowButtonGroupName}.btnDeleteCountersign.name-->
					$("#${workflowButtonGroupName}_btnAddCountersign").html('<span><span>'+'${task.addSignerButton}'+'</span></span>');<#--${workflowButtonGroupName}.btnAddCountersign.name-->
				</script>
			</#if>
		</#if>
		<#if task.active == 0 &&task.processingMode.condition == '签收式'>
			<button href="#" class='btn' id="${workflowButtonGroupName}_btnSignoffTask"   onclick="${workflowButtonGroupName}.btnSignoffTask.click(#{taskId},'SIGNOFF','${submitForm}')"></button>
			<script type="text/javascript">
				$("#${workflowButtonGroupName}_btnSignoffTask").html('<span><span>'+'${task.signForButton}'+'</span></span>');<#--${workflowButtonGroupName}.btnSignoffTask.name-->
			</script>
		</#if>
		<#if task.active == 0 &&task.processingMode.condition == '投票式'>
			<button href="#" class='btn' id="${workflowButtonGroupName}_btnVoteAgreement"   onclick="${workflowButtonGroupName}.btnVoteAgreement.click(#{taskId},'AGREEMENT','${submitForm}')"></button>
			<button href="#" class='btn' id="${workflowButtonGroupName}_btnVoteOppose"   onclick="${workflowButtonGroupName}.btnVoteOppose.click(#{taskId},'OPPOSE','${submitForm}')"></button>
			<button href="#" <#if !task.showButtonKiken>style="display: none;"</#if> class='btn' id="${workflowButtonGroupName}_btnVoteKiken"   onclick="${workflowButtonGroupName}.btnVoteKiken.click(#{taskId},'KIKEN','${submitForm}')"></button>
			<script type="text/javascript">
				$("#${workflowButtonGroupName}_btnVoteAgreement").html('<span><span>'+'${task.approveButton}'+'</span></span>');<#--${workflowButtonGroupName}.btnVoteAgreement.name-->
				$("#${workflowButtonGroupName}_btnVoteOppose").html('<span><span>'+'${task.opposeButton}'+'</span></span>');<#--${workflowButtonGroupName}.btnVoteOppose.name-->
				$("#${workflowButtonGroupName}_btnVoteKiken").html('<span><span>'+'${task.abstainButton}'+'</span></span>');<#--${workflowButtonGroupName}.btnVoteKiken.name-->
			</script>
		</#if>
		<#if task.active == 0 &&task.processingMode.condition == "交办式">
			<button href="#" class='btn' id="${workflowButtonGroupName}_btnAssignTask"  onclick="${workflowButtonGroupName}.btnAssignTask.click(#{taskId},'ASSIGN','${submitForm}');" class="btnStyle "></button>
			<script type="text/javascript">
				$("#${workflowButtonGroupName}_btnAssignTask").html('<span><span>'+'${task.assignButton}'+'</span></span>');<#--${workflowButtonGroupName}.btnAssignTask.name-->
			</script>
		</#if>
		<#if task.active == 0 &&task.processingMode.condition == "分发">
			<button href="#" class='btn' id="${workflowButtonGroupName}_btnDistributeTask"   onclick="${workflowButtonGroupName}.btnDistributeTask.click(#{taskId},'DISTRIBUTE','${submitForm}');" ></button>
			<script type="text/javascript">
				$("#${workflowButtonGroupName}_btnDistributeTask").html('<span><span>'+'${task.submitButton}'+'</span></span>');<#--${workflowButtonGroupName}.btnDistributeTask.name-->
			</script>
		</#if>
		<#if showOtherButton>
			<#if task.active == 0 || task.active == 1||task.active == 6>
				<button href="#" <#if !task.showButtonCopy>style="display: none;"</#if> class='btn' id="${workflowButtonGroupName}_btnCopyTache"   onclick="${workflowButtonGroupName}.btnCopyTache.click(#{taskId},'','${submitForm}');" ></button>
				<script type="text/javascript">
					$("#${workflowButtonGroupName}_btnCopyTache").html('<span><span>'+'${task.copyButton}'+'</span></span>');<#--${workflowButtonGroupName}.btnCopyTache.name-->
				</script>
			</#if>
			<#if (task.active == 0||task.active == 4)&& task.processingMode.condition !="阅">
				<button href="#" <#if !task.showButtonAppoint>style="display: none;"</#if> class='btn' id="${workflowButtonGroupName}_btnAssign"   onclick="${workflowButtonGroupName}.btnAssign.click(#{taskId},'ASSIGN_TASK','${submitForm}');" ></button>
				<script type="text/javascript">
					$("#${workflowButtonGroupName}_btnAssign").html('<span><span>'+'${task.appointButton}'+'</span></span>');<#--${workflowButtonGroupName}.btnAssign.name-->
				</script>
			</#if>
		</#if>
	</#if>
	<#if hiddenButton != "">
		<script type="text/javascript">
		<#list hiddenButton?split(',') as btn>
			<#if btn == "saveForm">
				$("#${workflowButtonGroupName}_btnSaveForm").css("display","none");
			</#if>
			<#if btn == "startWorkflow">
				$("#${workflowButtonGroupName}_btnStartWorkflow").css("display","none");
			</#if>
			<#if btn == "submitWorkflow">
				$("#${workflowButtonGroupName}_btnSubmitWorkflow").css("display","none");
			</#if>
			<#if btn == "assignTransactor">
				$("#${workflowButtonGroupName}_btnAssignTransactor").css("display","none");
			</#if>
			<#if btn == "choiceTache">
				$("#${workflowButtonGroupName}_btnChoiceTache").css("display","none");
			</#if>
			<#if btn == "drawTask">
				$("#${workflowButtonGroupName}_btnDrawTask").css("display","none");
			</#if>
			<#if btn == "readTask">
				$("#${workflowButtonGroupName}_btnReadTask").css("display","none");
			</#if>
			<#if btn == "distributeTask">
				$("#${workflowButtonGroupName}_btnDistributeTask").css("display","none");
			</#if>
			<#if btn == "assignTask">
				$("#${workflowButtonGroupName}_btnAssignTask").css("display","none");
			</#if>
			<#if btn == "voteKiken">
				$("#${workflowButtonGroupName}_btnVoteKiken").css("display","none");
			</#if>
			<#if btn == "voteOppose">
				$("#${workflowButtonGroupName}_btnVoteOppose").css("display","none");
			</#if>
			<#if btn == "voteAgreement">
				$("#${workflowButtonGroupName}_btnVoteAgreement").css("display","none");
			</#if>
			<#if btn == "signoffTask">
				$("#${workflowButtonGroupName}_btnSignoffTask").css("display","none");
			</#if>
			<#if btn == "addCountersign">
				$("#${workflowButtonGroupName}_btnAddCountersign").css("display","none");
			</#if>
			<#if btn == "deleteCountersign">
				$("#${workflowButtonGroupName}_btnDeleteCountersign").css("display","none");
			</#if>
			<#if btn == "approveTask">
				$("#${workflowButtonGroupName}_btnApproveTask").css("display","none");
			</#if>
			<#if btn == "refuseTask">
				$("#${workflowButtonGroupName}_btnRefuseTask").css("display","none");
			</#if>
			<#if btn == "submitTask">
				$("#${workflowButtonGroupName}_btnSubmitTask").css("display","none");
			</#if>
			<#if btn == "getBackTask">
				$("#${workflowButtonGroupName}_btnGetBackTask").css("display","none");
			</#if>
			<#if btn == "copyTache">
				$("#${workflowButtonGroupName}_btnCopyTache").css("display","none");
			</#if>
			<#if btn == "abandonTask">
				$("#${workflowButtonGroupName}_btnAbandonTask").css("display","none");
			</#if>
			<#if btn == "assign">
				$("#${workflowButtonGroupName}_btnAssign").css("display","none");
			</#if>
	 	</#list>
	 	</script>
	</#if>
