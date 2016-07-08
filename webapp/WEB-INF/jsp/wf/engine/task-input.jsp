<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/wf-taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	
	<title><s:text name="wf.prcoess.management"/></title>
	<%@ include file="/common/wf-iframe-meta.jsp"%>
	
	
	<link href="${imatrixCtx}/widgets/workflow-swfupload/default.css" rel="stylesheet" type="text/css" />
	<script type="text/javascript" src="${imatrixCtx}/widgets/workflow-swfupload/workflow-attachment-handlers.js"></script>
	<script type="text/javascript" src="${imatrixCtx}/widgets/workflow-swfupload/swfupload.js"></script>
	
	<script type="text/javascript" src="${wfCtx}/js/swfobject.js"></script>
	
	<script src="${wfCtx}/js/util.js" type="text/javascript"></script>
	<script src="${wfCtx}/js/task.js" type="text/javascript"></script>
	<script src="${wfCtx}/js/workflow-instance.js" type="text/javascript"></script>
	
	<script src="${wfCtx}/js/workflow.js"></script>
	<script src="${wfCtx}/js/opinion.js"></script>
	<script type="text/javascript">
	$(document).ready(function() {
		$("#tabs").tabs();
	});
	function loadUserTree(){
	    $("#user_tree").tree({
	        data:{
		        type:"json",
		        url:"${wfCtx}/engine/tree-load.htm",
		        async:true,
		        async_data:function (NODE){ return {currentId:$(NODE).attr("id") || "INITIALIZED"}}
	      	},
	      	selected    : "array",        // FALSE or STRING or ARRAY
			rules   : {        
				multiple    : "on"
			},
			ui : {
				theme_name : "checkbox",
				context : []
			},
			callback : {
				onchange : function (NODE, TREE_OBJ) {
					treeSetting(NODE, TREE_OBJ);
					}
				}
	    });
	}
	function treeSetting(NODE, TREE_OBJ){
		if(TREE_OBJ.settings.ui.theme_name == "checkbox") {
			var $this = $(NODE).is("li") ? $(NODE) : $(NODE).parent();
			if($this.children("a.unchecked").size() == 0) {
				TREE_OBJ.container.find("a").addClass("unchecked");
			}
			$this.children("a").removeClass("clicked");
			if($this.children("a").hasClass("checked")) {
				$this.find("li").andSelf().children("a").removeClass("checked").removeClass("undetermined").addClass("unchecked");
				var state = 0;
			}
			else {
				$this.find("li").andSelf().children("a").removeClass("unchecked").removeClass("undetermined").addClass("checked");
				var state = 1;
			}
			$this.parents("li").each(function () { 
				if(state == 1) {
					if($(this).find("a.unchecked, a.undetermined").size() - 1 > 0) {
						$(this).parents("li").andSelf().children("a").removeClass("unchecked").removeClass("checked").addClass("undetermined");
						return false;
					}
					else $(this).children("a").removeClass("unchecked").removeClass("undetermined").addClass("checked");
				}
				else {
					if($(this).find("a.checked, a.undetermined").size() - 1 > 0) {
						$(this).parents("li").andSelf().children("a").removeClass("unchecked").removeClass("checked").addClass("undetermined");
						return false;
					}
					else $(this).children("a").removeClass("checked").removeClass("undetermined").addClass("unchecked");
				}
			});
		}
	}
	//分发
	function distribute(){
		ajax("inputForm", webRoot + "/engine/task-save.htm", "wf_task");
	}

	//领取
	function drawTask(){
		ajax("inputForm", webRoot + "/engine/task-receive.htm", "wf_task");
	}
	function readed(){
		submitForm("READED","default");
	}	
	

	//同意
	function approve(){
		if($("#mustoption").attr("value")=='true'&&$("#editedoption").attr("value")=='false'){
			iMatrix.alert(iMatrixMessage["wf.text.mustFillIn"]);
			return ;
		}
		$("#wf_task_transact").attr("value", "APPROVE");
		ajax("inputForm", webRoot + "/engine/task-save.htm", "wf_task");
	}

	//拒绝
	function refuse(){
		if($("#mustoption").attr("value")=='true'&&$("#editedoption").attr("value")=='false'){
			iMatrix.alert(iMatrixMessage["wf.text.mustFillIn"]);
			return ;
		}
		$("#wf_task_transact").attr("value", "REFUSE");
		ajax("inputForm", webRoot + "/engine/task-save.htm", "wf_task");
	}
	function ajax(fromId, url, zone, callback){
		$("#"+fromId).attr("action", url);
		ajaxAnywhere.formName = fromId;
		ajaxAnywhere.getZonesToReload = function() {
			return zone;
		};
		ajaxAnywhere.onAfterResponseProcessing = function () {
			if(typeof callback == "function"){
				callback();
			}
			showMsg("successMessage");
		};
		ajaxAnywhere.submitAJAX();
	}

	function back(){
		parent.window.close();
	}

	</script>
</head>
<body >
<div class="ui-layout-center">
<div class="opt-body">
	<div style="text-align: left;" >
		<form id="defaultForm" action="" name="defaultForm" method="post"></form>
		<form id="wf_form" name="wf_form" action="${wfCtx}/engine/task-save.htm" method="post">
			<input type="hidden" id="task_id" name="taskId" value="${taskId }" />
			<input type="hidden" id="task_transact" name="transact" value="" >
		</form>
		<aa:zone name="wf_task">
			<p  class="buttonP">
				<span id="taskbutton">
					<s:if test='task.active == 0 && task.processingMode.condition!="阅"'>
						<a href="#" id="saveFormButton"   onclick="saveInputForm('${wfCtx}/engine/task-saveForm.htm')" class="btnStyle"><s:text name="menuManager.save"></s:text></a>
					</s:if>
					<s:if test="task.active == 1">
						<a href="#" class="btnStyle" onclick="assignTransactor()"><s:text name="instance.history.submit"></s:text></a>
					</s:if>
						<s:if test='task.active == 2 && task.processingMode.condition != "阅" && task.processingMode.condition !=  "会签式" && task.processingMode.condition !=  "投票式" &&task.processingMode.condition !=  "分发" '>
							<a href="#" onclick="getBack()" class="btnStyle "><s:text name="wf.text.getBack"></s:text></a>&nbsp;&nbsp;
						</s:if>
					<s:if test='task.active == 0&&task.processingMode.condition == "编辑式"'>
							<a href="#" id="submitFormButton"  onclick="submitEdit()" class="btnStyle"><s:text name="instance.history.submit"></s:text></a>
					</s:if>
					<s:if test='task.active == 0 && (task.processingMode.condition == "审批式" || task.processingMode.condition == "会签式")'>
							<a href="#" class="btnStyle" onclick="approve()"><s:text name="instance.history.agree"></s:text></a>
							<a href="#" class="btnStyle" onclick="refuse()"><s:text name="instance.history.disagree"></s:text></a>
						<s:if test="task.processingMode.condition == '会签式'">
							<a href="#" onclick='init_tb("${wfCtx}/engine/task-addCountersign.htm?taskId=${taskId }TB_iframe=true&width=300&height=400","加签")' class="btnStyle "><s:text name="instance.history.addsign"></s:text></a>
							<a href="#" onclick='init_tb("${wfCtx}/engine/task-deleteCountersign.htm?taskId=${taskId }TB_iframe=true&width=300&height=400","减签")' class="btnStyle "><s:text name="instance.history.removesign"></s:text></a>
						</s:if>
					</s:if>
					<s:if test="task.active == 0&&task.processingMode.condition == '签收式'">
							<a href="#" class="btnStyle" onclick="signoff()"><s:text name="wf.text.signFor"></s:text></a>
					</s:if>
					<s:if test="task.active == 0&&task.processingMode.condition == '投票式'">
							<a href="#" class="btnStyle" onclick="agreement()"><s:text name="wf.text.agree"></s:text></a>
							<a href="#" class="btnStyle" onclick="oppose()"><s:text name="wf.text.oppose"></s:text></a>
							<a href="#" class="btnStyle" onclick="kiken()"><s:text name="wf.text.waiver"></s:text></a>
					</s:if>
					<s:if test='task.active == 0&&task.processingMode.condition == "交办式"'>
							<a href="#"  onclick="assign();" class="btnStyle "><s:text name="wf.text.assigned"></s:text></a>
					</s:if>
					<s:if test='task.active == 0&&task.processingMode.condition == "分发"'>
							<a href="#"  onclick="distribute();" class="btnStyle "><s:text name="wf.text.distribute"></s:text></a>
					</s:if>
					<s:if test='task.active == 0 && "阅" == task.processingMode.condition  '>
						<a href="#"  onclick="readed();" class="btnStyle "><s:text name="wf.text.seen"></s:text></a>
					</s:if>	
					<s:if test="task.active==4">
						<a href="#" class="btnStyle " onclick="drawTask();"><s:text name="wf.text.receive"></s:text></a>
					</s:if>
					<a href="#" onclick="parent.window.close();" class="btnStyle"><s:text name="wf.button.close"></s:text></a>
				</span>
				<span id="closeBtn" style="display: none;"><a href="#" onclick="parent.window.close();" class="btnStyle"><s:text name="wf.button.close"></s:text></a></span>
			</p>
			<div id="successMessage"><s:actionmessage theme="mytheme" /></div>
			<div id="tabs">
				<ul>
					<li><a href="#tabs-1" onclick="ajaxAnyWhere_workflow('${wfCtx}/engine/task-input.htm?workflowId=${workflowId }&taskId=${taskId }','viewProcess',this);"><s:text name="wf.table.formInformation"></s:text></a></li>
					<li><a href="#tabs-1" onclick="ajaxAnyWhere_workflow('${wfCtx}/engine/task-flowHistory.htm?workflowId=${workflowId}&taskId=${taskId }','viewProcess',this);"><s:text name="wf.table.circulationHistory"></s:text></a></li>
					<li><a href="#tabs-1" onclick="ajaxAnyWhere_workflow('${wfCtx}/engine/task-countersign.htm?workflowId=${workflowId }&taskId=${taskId }','viewProcess',this);"><s:text name="wf.table.theSign"></s:text></a></li>
					<li><a href="#tabs-1" onclick="ajaxAnyWhere_workflow('${wfCtx}/engine/task-vote.htm?workflowId=${workflowId }&taskId=${taskId }','viewProcess',this);"><s:text name="wf.table.pollResults"></s:text></a></li>
				</ul>
			</div>
			<aa:zone name="viewProcess">
				<div id="tabs-1">
					<input id="taskName"  type="hidden" name="taskName" value="${task.name}"/>
					<%@ include file="task-fragment.jsp" %>
				</div>
			</aa:zone>
		</aa:zone>
		<div style="display: none;" id="custom"></div>
	</div>
</div>
</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script src="${resourcesCtx}/widgets/timepicker/timepicker_<%=com.norteksoft.product.util.ContextUtils.getCurrentLanguage()%>.js" type="text/javascript"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>

