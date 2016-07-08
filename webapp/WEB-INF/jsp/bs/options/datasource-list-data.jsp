<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/setting-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<%@ include file="/common/setting-iframe-meta.jsp"%>
	<title><s:text name="bs.interface.management"/></title>
	<script type="text/javascript">
	function showDatasource(cellvalue, options, rowObject){
		var v="<a  href=\"#\" hidefocus=\"true\" onclick=\"viewDatasource("+rowObject.id+");\">" + cellvalue + "</a>";
		return v;
	}
	function viewDatasource(id){
		init_colorbox("${settingCtx}/options/datasource-setting-view.htm?id="+id,iMatrixMessage["basicSetting.dataSourceConfigure"]);
	}

	function createDatasource(id){
		var url = "${settingCtx}/options/datasource-setting-input.htm";
		if(typeof(id)!="undefined"&&id!=""){
			url=url+"?id="+id;
		}
		init_colorbox(url,iMatrixMessage["basicSetting.dataSourceConfigure"],"","",true,refreshListData);
	}

	function refreshListData(){
		jQuery("#page").trigger("reloadGrid");
	}

	function deleteCallback(){
		showMsg();
		refreshListData();
	}

	function interfaceUseDatasource(option){
		var url = webRoot+"/options/datasource-setting-use-validate.htm";
		var ids = jQuery("#page").jqGrid("getGridParam",'selarrrow');
		var param = "ids="+ids.join(",");
		if(ids==null||ids==""){
			iMatrix.alert(iMatrixMessage.selectInfo);
			return;
		}
		if(option=="update"){
			param = "ids="+ids[0];
			if(ids.length>1){
				iMatrix.alert(iMatrixMessage.selectOneInfo);
				return;
			}
		}
		$.ajax({
			   type: "POST",
			   url: url,
			   data: param,
			   success: function(msg){
			   		var message = msg;
			   		if(message!=""){
			   			if(option=="delete"){
				   			iMatrix.confirm({
				   				message:message+iMatrixMessage.deleteInfo,
				   				confirmCallback:deleteDatasourceOk,
				   				parameters:ids
				   			});
			   			}else{
				   			iMatrix.confirm({
				   				message:message+iMatrixMessage.updateInfo,
				   				confirmCallback:createDatasourceOk,
				   				parameters:ids
				   			});
			   			}
			   		}else{
						if(option=="delete"){
							iMatrix.confirm({
								message:iMatrixMessage.deleteInfo,
								confirmCallback:deleteDatasourceOk,
								parameters:ids
							});
						}else{
			   				createDatasource(ids[0]);
						}
			   		}
			   }
		});
	}
	
	function createDatasourceOk(ids){
		createDatasource(ids[0]);
	}
	function deleteDatasourceOk(ids){
		deleteDatasource(ids);
	}
	function deleteDatasource(ids){
		$("#ids").attr("value",ids.join(","));
		ajaxSubmit("defaultForm",webRoot+"/options/datasource-setting-delete.htm", "datasource-zones",deleteCallback);
	}
	</script>
</head>

<body>
	<div class="ui-layout-center">
		<div class="opt-body">
		<form action="" name="defaultForm" id="defaultForm" method="post">
			<input name="ids" id="ids" type="hidden"></input>
		</form>
			<aa:zone name="datasource-zones">
				<div class="opt-btn">
					<button class="btn" onclick="iMatrix.showSearchDIV(this);"><span><span><s:text name="formManager.search"></s:text></span></span></button>
						<button class="btn" onclick='createDatasource();' ><span><span ><s:text name="menuManager.new"></s:text></span></span></button>
						<button class="btn" onclick="interfaceUseDatasource('update');"><span><span ><s:text name="menuManager.update"></s:text></span></span></button>
						<button class="btn" onclick="interfaceUseDatasource('delete');"><span><span ><s:text name="menuManager.delete"></s:text></span></span></button>
				</div>
				<div id="opt-content" >
						<div id="message" style="display: none;"><s:actionmessage theme="mytheme" /></div>
						<form action="${settingCtx}/options/datasource-setting-list-data.htm" name="pageForm" id="pageForm" method="post">
								<view:jqGrid url="${settingCtx}/options/datasource-setting-list-data.htm" code="BS_DATASOURCE_SETTING" gridId="page" pageName="page"></view:jqGrid>
						</form>
				</div>
			</aa:zone>
		</div>	
	</div>
</body>

</html>