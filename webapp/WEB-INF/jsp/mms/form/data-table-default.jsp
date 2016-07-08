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
		function deleteEnableTableInfo(){
			var ids = jQuery("#dataTables").getGridParam('selarrrow');
			$("input[name='tableIds']").remove();
			if(ids==''){
				showMessage("message", "<font color=\"red\">"+iMatrixMessage.selectOneDataInfo+"</font>");
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
			ajaxSubmit("contentFrom", webRoot+"/form/data-table-deleteCustom.htm", "dataTablelist");
		}
		//删除一个数据表信息
		function deleteTableInfo(url){
			var ids = jQuery("#dataTables").getGridParam('selarrrow');
			if(ids==''){
				showMessage("message", "<font color=\"red\">"+iMatrixMessage.selectOneDataInfo+"</font>");
			}else{
				var canPost = true;
				$.each(ids, function(i){
					var id = ids[i];
					var state=jQuery("#dataTables").jqGrid("getCell",id,"tableState");
					if(state!= "DRAFT"){
						showMessage("message", "<font color=\"red\">"+iMatrixMessage["formManager.delelteStartForbiddenInfo"]+"</font>");
						canPost = false;
					}
				});
				if(canPost){
					iMatrix.confirm({
						message:iMatrixMessage.deleteInfo,
						confirmCallback:deleteTableInfoOk,
						parameters:{ids:ids,url:url}
					});
				}
			}
		}
		function deleteTableInfoOk(obj){
			$.each(obj.ids, function(i){
				$("#contentFrom").append(createHiddenInput("tableIds", obj.ids[i]));
			});
			setPageState();
			ajaxSubmit("contentFrom", obj.url, "dataTablelist");
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
				<!-- <button class="btn" onclick="deleteTableInfo('${mmsCtx}/form/data-table-deleteCustom.htm');"><span><span >删除</span></span></button>
				 -->
				 <s:if test='#versionType=="online"'>
				<button class="btn" onclick="demonstrateOperate();"><span><span ><s:text name="formManager.deleteStarated"></s:text></span></span></button>
				 </s:if><s:else>
				<button class="btn" onclick="deleteEnableTableInfo();"><span><span ><s:text name="formManager.deleteStarated"></s:text></span></span></button>
				 </s:else>
			</div>
			<span id="searchContent"></span>
			<div id="opt-content">
				<aa:zone name="dataTablelist">
					<div id="message" style="display:none;"><s:actionmessage theme="mytheme" /></div>
					<form action="" name="pageForm" id="pageForm" method="post">
						<view:jqGrid url="${mmsCtx}/form/data-table-defaultDataTableList.htm?menuId=${menuId }" code="MMS_CUSTOM_DATA_TABLE" gridId="dataTables" pageName="dataTables"></view:jqGrid>
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