<style>
 .ui-jqgrid tr.jqgrow td {
   white-space: normal !important;
   height:auto;
   vertical-align:text-top;
   padding-top:2px;
  }
</style>
<div class="_custom_table">
<table id="${tableId}" newId='1' ></table>
</div>
<input id="_login_name" type="hidden" value="${loginName}"/>
<input id="_user_name" type="hidden" value="${userName}"/>
<script type="text/javascript">
var customGrid;
	$(document).ready(function(){
		<#if listView.deleteUrl?if_exists !="">
			<#if isSubSystem?if_exists=='true'>
				deleteUrl='${listView.deleteUrl}';
			<#else>
				deleteUrl='${ctx}${listView.deleteUrl}';
			</#if>			
		<#else>
			deleteUrl='';
		</#if>
		customGrid={
			datatype:'local',
			mtype:'POST',
			colNames:${colNames},
			colModel:${colModel},
			rowNum:300,
			editurl:'clientArray',
			onSelectRow: function(id,status){
				if(hasEdit){
					if(id && id!=lastsel){
						if(!isHasEdit('${tableId}')){//如果表格有验证，且验证失败时，则不进入该判断
							saveRowWhenAdd(lastsel,'${tableId}');
							lastsel=0;//当新增一条记录后，当前选中的纪录设为非任何纪录
						}
					}
				}
			}
			<#if attributeName?if_exists!="">
				,myattrName:"${attributeName}"
			<#else>
				,myattrName:"customTable"
			</#if>
			<#if listView.multiSelect?if_exists>
				,multiselect: true
				<#if listView.multiboxSelectOnly?if_exists>
				,multiboxonly:true
				<#else>
				,multiboxonly:false
				</#if>
			<#else>
				,multiselect: false
			</#if>
			<#if jsonData?if_exists!="">
				,data:${jsonData}
			</#if>
			<#if listView.rowNumbers?if_exists>
				,rownumbers:true
			</#if>
			<#if footerDatas?if_exists!="">
				,footerrow : true
				,userDataOnFooter : true
				,altRows : true
			</#if>
			<#if listView.customProperty?if_exists!="">
				,${listView.customProperty}
			</#if>
			<#if listView.orderFieldName?if_exists!="">
				<#if editable?if_exists!="false">
					,indexname:'${listView.orderFieldName}'
				<#else>
					,indexname:'false'
				</#if>				
			<#else>
				,indexname:'false'
			</#if>
			<#if listView.editable?if_exists>
				,gridComplete: function(){
					newId='0';lastsel='0';hasEdit=false;
					var ids =jQuery("#${tableId}").jqGrid('getDataIDs');
					for(var i=0;i < ids.length;i++){
						var cl = ids[i];
						<#if webCtx?if_exists!="">
							<#assign _imatrix="${webCtx}">
						<#else>
							<#assign _imatrix="">
						</#if>
						<#if isSubSystem?if_exists!="">
							<#assign _isSubSystem="${isSubSystem}">
						<#else>
							<#assign _isSubSystem="">
						</#if>
						ae = "<a href='#pos' class='small-button-bg' acttype='add' onclick='myAddRow(\"1\",\""+cl+"\",\"${tableId}\",\"${_isSubSystem}\",\"${_imatrix}\");'><span title='${buttonAdd}' class='ui-icon ui-icon-plusthick'></span></a>";
						if(deleteUrl!=''){
							de = "<a href='#pos' class='small-button-bg' acttype='del'  onclick='deleteFormTableData(\"${tableId}\",\""+cl+"\",\""+deleteUrl+"\",\"${_isSubSystem}\",\"${_imatrix}\");'><span title='${buttonDelete}' class='ui-icon  ui-icon-minusthick'></span></a>";
						}else{
							de = "<a href='#pos' class='small-button-bg' acttype='del' onclick='$deleteFormTableData(\"${tableId}\",\""+cl+"\",\"${_isSubSystem}\",\"${_imatrix}\");'><span title='${buttonDelete}' class='ui-icon  ui-icon-minusthick'></span></a>";
						}
						edit = "<a href='#pos' class='small-button-bg' acttype='edit' onclick=\"editClick('"+cl+"','${tableId}');\"><span title='${buttonUpdate}' class='ui-icon  ui-icon-pencil'></span></a>";
						jQuery("#${tableId}").jqGrid('setRowData',ids[i],{act:ae+' '+de+' '+edit+_getCustomeButtons('${tableId}',ids[i])});
					}
					$gridComplete('${tableId}');
				}
			</#if>
			,onCellSelect:$onCellClick
			,ondblClickRow: function(id){
				$ondblClick(id);
			}
			,onRightClickRow: function(id){
				$onRightClick(id);
			}
		};
		jQuery("#${tableId}").jqGrid(customGrid);
		<#if listView.orderFieldName?if_exists!="">
			<#if editable?if_exists!="false">
				  //可拖动的选择权限
				  var _rowId;
				var sortableOptions = {
					items : '.jqgrow:not(.unsortable)',
					start : function(event, ui) {
						_rowId = ui.item.attr('id');
						var originalIndex = jQuery("#${tableId}").jqGrid("getInd", _rowId);
						$sortableRowsStart(_rowId,originalIndex,'${tableId}');
					},
					stop : function(event, ui) {
						var newIndex = jQuery("#${tableId}").jqGrid("getInd", _rowId);
						$sortableRowsStop(_rowId,newIndex,'${tableId}');
					}
				};
				jQuery("#${tableId}").jqGrid('sortableRows',sortableOptions);
			</#if>
		</#if>
		
		<#if jsonData?if_exists!="">
			var mydata=${jsonData};
			for(var i=0;i<mydata.length;i++){
				if(mydata[i].id=='${tableId}_new_0'){
					_controlGridEditColumns('${tableId}');//动态控制某些列不能编辑
					jQuery('#${tableId}').jqGrid('editRow','${tableId}_new_0',true,function(){var id='${tableId}_new_0';${editFunScript}},function(){},'',{},
									function(){
										hasEdit=false;
										$editRowSave('${tableId}_new_0','${tableId}');
										lastsel=0;//当新增一条记录后，当前选中的纪录设为非任何纪录										
									},
									function(){},
									function(){
										hasEdit=false;
										$editRowRestore('${tableId}_new_0','${tableId}');
										lastsel=0;//当新增一条记录后，当前选中的纪录设为非任何纪录	
										
										jQuery("#${tableId}").jqGrid('delRowData','${tableId}_new_0');
										_add_row('${tableId}');
									}
								);
				}
			}
		</#if>
		<#if footerDatas?if_exists!="">
			jQuery("#${tableId}").jqGrid('footerData','set',${footerDatas});
		</#if>		
	});
	
	function editClick(id,tableId){
		hasEdit = isHasEdit(tableId);
		if(!hasEdit){
			if(id){
				 _controlGridEditColumns(tableId);//动态控制某些列不能编辑
				customGridEditRow(id,tableId);
				lastsel=id;
				$editClickCallback(id,tableId);
			}
		}
	}
	${editFunForJs}
</script>