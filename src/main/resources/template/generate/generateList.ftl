<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
	<title></title>
	<%@include file="/common/meta.jsp" %>
	<script src="${resourcesCtx}/widgets/validation/validate-all-1.0.js" type="text/javascript"></script>
	<script src="${resourcesCtx}/widgets/validation/dynamic.validate.js" type="text/javascript"></script>
	<#if containWorkflow?if_exists>
	<script type="text/javascript" src="${ctx}/widgets/workflowEditor/swfobject.js"></script>
	<script type="text/javascript" src="${resourcesCtx}/js/workflowTag.js"></script>
	<script type="text/javascript" src="${resourcesCtx}/js/opinion.js"></script>
	</#if>
</head>

<body onclick="$('#sysTableDiv').hide(); $('#styleList').hide();" >
	<%@ include file="/menus/header.jsp" %>
	<div class="ui-layout-center">
		<div class="opt-body">
			<form id="defaultForm" name="defaultForm" method="post"  action=""></form>
			<aa:zone name="mainZone">
				<script type="text/javascript">
				<#if popupable?if_exists>
					//新建
					function create${entityName}(url){
						init_colorbox(url,"新建${entityAlias}","","","",colorboxCloseCallback);
					}
					//修改
					function update${entityName}(url){
						var ids = jQuery("#${entityAttribute}GridId").getGridParam('selarrrow');
						if(ids==""){
							alert("请选择一条数据");
						}else if(ids.length > 1){
							alert("只能选择一条数据");
						}else if(ids.length == 1){
							init_colorbox(url+"?id="+ids[0],"修改${entityAlias}","","","",colorboxCloseCallback);
						}
					}
					function view${entityName}(url){
						var ids = jQuery("#${entityAttribute}GridId").getGridParam('selarrrow');
						if(ids==""){
							alert("请选择一条数据");
						}else if(ids.length > 1){
							alert("只能选择一条数据");
						}else if(ids.length == 1){
							init_colorbox(url+"?id="+ids[0],"查看${entityAlias}","","","",null);
						}
						
					}
					function colorboxCloseCallback(){
						jQuery("#${entityAttribute}GridId").trigger("reloadGrid");
					}
				<#else>
					function view${entityName}(url){
						var ids = jQuery("#${entityAttribute}GridId").getGridParam('selarrrow');
						if(ids==""){
							alert("请选择一条数据");
						}else if(ids.length > 1){
							alert("只能选择一条数据");
						}else if(ids.length == 1){
							ajaxSubmit("defaultForm",url+"?id="+ids[0],"main",null);
						}
						
					}
					//新建
					function create${entityName}(url){
						ajaxSubmit("defaultForm",url,"main",create${entityName}Callback);
					}
					function create${entityName}Callback(){
						validate${entityName}();
						getContentHeight();
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
							rules: {
								
							},
							messages: {
								
							}
						});
					}
					
					//修改
					function update${entityName}(url){
						var ids = jQuery("#${entityAttribute}GridId").getGridParam('selarrrow');
						if(ids==""){
							alert("请选择一条数据");
						}else if(ids.length > 1){
							alert("只能选择一条数据");
						}else if(ids.length == 1){
							ajaxSubmit("defaultForm",url+"?id="+ids[0],"main",create${entityName}Callback);
						}
					}
					<#if containWorkflow?if_exists>
					function changeViewSet(opt){
						if(opt=="basic"){
							ajaxSubmit("defaultForm1", "${ctx}/${namespace}/${entityAttribute}-view.htm", 'viewZone');
						}else if(opt=="history"){
							ajaxSubmit("defaultForm1", "${ctx}/${namespace}/${entityAttribute}-showHistory.htm", 'viewZone');
						}
					}
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
						validate${entityName}();
						getContentHeight();
						if(buttonSign=="firstSubmit"){
							buttonSign="";
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
							setPageState();
							ajaxSubmit("defaultForm","${ctx}/${namespace}/${entityAttribute}-list.htm","main");
						}
					}
				}
			
					function submitCommonOpinionForm(){
						$("#inputForm").attr("action","${ctx}/${namespace}/${entityAttribute}-completeInteractiveTask.htm");
						$("#inputForm").ajaxSubmit(function (id){
							//刷新区域
							setPageState();
							ajaxSubmit("defaultForm","${ctx}/${namespace}/${entityAttribute}-list.htm","main");
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
								//刷新区域
								setPageState();
								ajaxSubmit("defaultForm","${ctx}/${namespace}/${entityAttribute}-list.htm","main");
							}
						});
					}
					
					<#else>
					function submitCallback(){
						showMsg();
						validate${entityName}();
						getContentHeight();
					}
					</#if>
					function save${entityName}(url){
						$("#inputForm").attr("action",url);
						$("#inputForm").submit();
					}
				</#if>
					
					//删除
					function delete${entityName}(url){
						var ids = jQuery("#${entityAttribute}GridId").getGridParam('selarrrow');
						if(ids.length<=0){
							alert("请选择数据");
						}else {
							ajaxSubmit('defaultForm', url+'?ids='+ids.join(','), 'main',showMsg);
						}
					}
					
				</script>
				<aa:zone name="main">
					<div class="opt-btn">
						<button  class='btn' onclick="iMatrix.showSearchDIV(this);"><span><span>查询</span></span></button>
						<button class="btn" onclick="create${entityName}('${ctx}/${namespace}/${entityAttribute}-input.htm');"><span><span>新建</span></span></button>
						<button class="btn" onclick="update${entityName}('${ctx}/${namespace}/${entityAttribute}-input.htm');"><span><span>修改</span></span></button>
						<button class="btn" onclick="delete${entityName}('${ctx}/${namespace}/${entityAttribute}-delete.htm');"><span><span >删除</span></span></button>
						<button class="btn" onclick="view${entityName}('${ctx}/${namespace}/${entityAttribute}-view.htm');"><span><span >查看</span></span></button>
					</div>
					<div id="message"><s:actionmessage theme="mytheme" /></div>	
					<div id="opt-content" >
						<form id="contentForm" name="contentForm" method="post"  action="">
							<grid:jqGrid gridId="${entityAttribute}GridId" url="${ctx}/${namespace}/${entityAttribute}-listDatas.htm" submitForm="defaultForm" code="${listCode}" ></grid:jqGrid>
						</form>
					</div>
				</aa:zone>
			</aa:zone>
		</div>
	</div>
	
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
</html>