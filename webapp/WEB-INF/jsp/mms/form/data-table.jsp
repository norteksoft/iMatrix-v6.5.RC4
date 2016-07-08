<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/mms-taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<title><s:text name="mms.formManager"/></title>
	<%@ include file="/common/mms-iframe-meta.jsp"%>
	
	<script type="text/javascript" src="${mmsCtx}/js/dataTable.js"></script>
	<script type="text/javascript" src="${resourcesCtx}/js/custom.tree.js" ></script>
	
	<script type="text/javascript">
		$(document).ready(function() {
			$("#men_id").attr("value",$("#menuId").val());
		});
		function exportDataTable(){
			$("#defaultForm").find("input[name='tableIds']").remove();
			var ids = jQuery("#dataTables").getGridParam('selarrrow');
			if(ids==''){
				iMatrix.confirm({
					message:iMatrixMessage["formManager.exportTableInfo"],
					confirmCallback:exportDataTableOk
				});
			}else{
				$.each(ids, function(i){
					$("#defaultForm").append(createHiddenInput("tableIds", ids[i]));
				});
				$("#defaultForm").attr("action",webRoot+"/form/export-data-table.htm");
				$("#defaultForm").submit();
			}
		}
		
		function exportDataTableOk(){
			$("#defaultForm").attr("action",webRoot+"/form/export-data-table.htm");
			$("#defaultForm").submit();
		}
		function importDataTable(){
			$.colorbox({href:'${mmsCtx}/form/show-import-data-table.htm',
				iframe:true, innerWidth:400, innerHeight:200,overlayClose:false,title:iMatrixMessage["formManager.importDateSheet"]});
		}

		function dataTableList(){
			ajaxSubmit("defaultForm",  webRoot+"/form/data-table-list-data.htm","dataTablelist");
		}
		function deleteEnableTableInfo(){
			var ids = jQuery("#dataTables").getGridParam('selarrrow');
			if(ids==''){
				showMessage("message", "<font color=\"red\">"+iMatrixMessage.selectOneInfo+"</font>");
			}else{
				iMatrix.confirm({
					message:iMatrixMessage["formManager.delelteAssociateAll"],
					confirmCallback:deleteEnableTableInfoOk,
					parameters:ids
				});
			}
		}
		function deleteEnableTableInfoOk(ids){
			$.each(ids, function(i){
				$("#contentFrom").append(createHiddenInput("tableIds", ids[i]));
			});
			$("#contentFrom").append(createHiddenInput("deleteEnable", true));
			ajaxSubmit("contentFrom", webRoot+"/form/data-table-delete.htm", "dataTablelist");
			$("#contentFrom").find("input[name='tableIds']").remove();
			$("#contentFrom").find("input[name='deleteEnable']").remove();
		}
		function generateCode(){
			var ids = jQuery("#dataTables").getGridParam('selarrrow');
			if(ids==""){
				showMessage("message", "<font color=\"red\">请选择数据</font>");
			}else if(ids.length >= 1){
				$.ajax({
					   type: "POST",
					   url: webRoot+"/form/validate-generate-code.htm",
					   data: "ids="+ids,
					   success: function(msg){
						   if(msg=="ok"){
							   $("#contentFrom").attr("action",webRoot+"/form/generate-code.htm?ids="+ids);
								$("#contentFrom").submit();
						   }else{
							   iMatrix.alert(msg);
						   }
					   }
				});
			}
		}
	</script>
	<style type="text/css">
	.form-table-without-border td input{
		width:250px;
	}
	</style>
</head>
<body>
<div class="ui-layout-center">
<form action="" method="post" name="defaultForm" id="defaultForm"  method="post" target="_blank">
<input type="hidden" id="menu_Id"  name="menuId" value="${menuId }"/>
</form>
<form id="defaultDataTableForm" name="defaultDataTableForm" action="">
	<input type="hidden" id="menuId"  name="menuId" value="${menuId }"/>
	<input id="page_id" type="hidden" name="pageId"></input>
	<input id="pageIds" name="pageIds" type="hidden"></input>
</form>
<div class="opt-body">
<aa:zone name="form_main">
	<aa:zone name="dataTableContent">
		<s:if test="menuId!=null ">
			<form id="contentFrom" name="contentFrom" method="post">
				<input type="hidden" name="states" value="all"/>
				<input type="hidden" id="men_id"  name="menuId" value="${menuId }"/>
			</form>
			<div class="opt-btn">
				<button class="btn" onclick="iMatrix.showSearchDIV(this);"><span><span><s:text name="formManager.search"></s:text></span></span></button>
				<button class="btn" onclick="createNewTable('${mmsCtx}/form/data-table-input.htm', createCallBack);"><span><span ><s:text name="menuManager.new"></s:text></span></span></button>
				<button class="btn" onclick="changeTableInfo('${mmsCtx}/form/data-table-input.htm', createCallBack);"><span><span ><s:text name="menuManager.update"></s:text></span></span></button>
				<button class="btn" onclick="changeTableStates();"><span><span ><s:text name="formManager.startOrForbidden"></s:text></span></span></button>
				<button class="btn" onclick="deleteTableInfo('${mmsCtx}/form/data-table-delete.htm');"><span><span ><s:text name="menuManager.delete"></s:text></span></span></button>
				<button class="btn" onclick="exportDataTable();"><span><span ><s:text name="menuManager.export"></s:text></span></span></button>
				<s:if test='#versionType=="online"'>
					<button class="btn" onclick="demonstrateOperate();"><span><span ><s:text name="menuManager.export"></s:text></span></span></button>
					<button class="btn" onclick="demonstrateOperate();"><span><span ><s:text name="formManager.deleteStarated"></s:text></span></span></button>
				</s:if><s:else>
					<button class="btn" onclick="importDataTable();"><span><span ><s:text name="menuManager.import"></s:text></span></span></button>
					<button class="btn" onclick="deleteEnableTableInfo();"><span><span ><s:text name="formManager.deleteStarated"></s:text></span></span></button>
				</s:else>
				<!--<button class="btn" onclick="generateCode();"><span><span >代码生成</span></span></button>
			--></div>
			<span id="searchContent"></span>
			<div id="opt-content">
				<aa:zone name="dataTablelist">
					<div id="message" style="display:none;"><s:actionmessage theme="mytheme" /></div>
					<form action="" name="pageForm" id="pageForm" method="post">
						<view:jqGrid url="${mmsCtx}/form/data-table-list-data.htm?menuId=${menuId }" code="MMS_DATA_TABLE" gridId="dataTables" pageName="dataTables"></view:jqGrid>
					</form>
				</aa:zone>
			</div>
		</s:if>
	</aa:zone>
</aa:zone>
</div>
</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script src="${resourcesCtx}/widgets/timepicker/timepicker_<%=com.norteksoft.product.util.ContextUtils.getCurrentLanguage()%>.js" type="text/javascript"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>