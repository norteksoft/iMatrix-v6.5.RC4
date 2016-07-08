<table id="${gridId}"></table>
<div id="${gridId}_pager"></div>
<input id="_login_name" type="hidden" value="${loginName?if_exists}"/>
<input id="_user_name" type="hidden" value="${userName?if_exists}"/>
<input id="_exportable_sign" type="hidden" value="${export}"/>
<input id="_main_grid_id" type="hidden" value="${gridId}"/>
<input id="current_language" type="hidden" value="${currentLanguage?if_exists}"/>
<#if subGrid?if_exists !="">
	<input id="_have_sub_grid" type="hidden" value="${subGrid}"/>
<#else>
	<input id="_have_sub_grid" type="hidden" value=""/>
</#if>

<script type="text/javascript">
;if(_gridTagParams==undefined){
	var _gridTagParams={};
}
_gridTagParams=$.extend(_gridTagParams,
	{
		gridId:"${gridId}",
		url:"${url}",
		pageName:"${pageName}",
		subGrid:"${subGrid}",
		mergerCell:"${mergerCell}",
		listColumns:${columns},
		dynamicColumns:${dynamicColumn},
		frozenColumn:${frozenColumn},
		rowNumbers:"${rowNumbers}",
		customProperty:${customProperty},
		editurl:"${editurl}",
		ctx:"${ctx}",
		rowNum:"${rowNum}",
		rowList:${rowList},
		multiselect:${multiselect},
		multiboxSelectOnly:${multiboxSelectOnly},
		sortname:"${sortname}",
		sortorder:"${sortorder}",
		pagination:"${pagination}",
		total:"${total}",
		listCode:"${_list_code}",
		dynamicColumnsPostData:'${dynamicColumns}',
		deleteUrl:"${deleteUrl}",
		dragRowUrl:"${dragRowUrl}",
		groupHeaderSign:"${groupHeaderSign}",
		groupHeader:${groupHeader},
		startQuerySign:'${startQuerySign}',
		currentLanguage:'${currentLanguage}',
		resourceCtx:'${resourceCtx}'
	}
);
$(function(){
	if(iMatrix.validateObjectProperty(_gridTagParams)){
		iMatrix.createJqGrid();
		if("INSIDE_QUERY"==_gridTagParams.startQuerySign || "CUSTOM_QUERY"==_gridTagParams.startQuerySign){
			var currentLanguage = _gridTagParams.currentLanguage;
			if(typeof(currentLanguage) =="undefined" || "zh_CN"==currentLanguage){
				$.getScript(_gridTagParams.resourceCtx+"/templateJs/searchTag.js");
			}else{
				$.getScript(_gridTagParams.resourceCtx+"/templateJs/searchTag_en.js");
			}
		}
	}
});
</script>

