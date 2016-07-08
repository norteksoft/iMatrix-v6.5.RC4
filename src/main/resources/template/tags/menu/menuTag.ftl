<div id="topnav" role="contentinfo">
	<a href="#" class="top-nav-first">
		<span></span>
	<#list menus?if_exists as item>
		<#if item_index<showNum>
		</a><a 
				<#if item.openWay.code=="firstMenu.open.way.currentPageOpen">
					href="${item.menuUrl?if_exists}" 
				<#elseif item.openWay.code=="firstMenu.open.way.newPageOpen">
					target="_blank" href="${item.menuUrl?if_exists}" 
				<#else>
					href="#" onclick="${item.event?if_exists}" 
				</#if>
			<#if item_index==showNum-1>id="lastSys"</#if> 
			class="<#if item.id?if_exists==firstMenuId?if_exists>top-selected</#if>">

				<span><span class="sys_${item.code?if_exists}" ></span>${item.name?if_exists}</span>
			 
		</#if>
	</#list>
	</a><#if showNum<menuSize><a id="selectNumen" class="top-nav-last">
				<span>${moreSystem}</span></a></#if>
</div>
<script type="text/javascript">
	function selectSystems(id){
		$('#styleList').hide();
		if($('#sysTableDivNew').attr('id')=='sysTableDivNew' ){//表示存在该div
			$('#sysTableDivNew').hide();
		}
		if($('#sysTableDiv').attr('id')!='sysTableDiv'){
			var table = "<div id='sysTableDiv'><table id='systemTable'><tbody>";
			table = table+appendFirstMenus();
			table = table+"</tbody></table></div>";
			$('body').append(table);
		}
		$('#sysTableDiv').show();
		var position = $("#"+id).position();
		$('#sysTableDiv').css('top', (position.top+36)+'px');
		$('#sysTableDiv').css('right', '0px');
	}
	
	function selectNumenClickNew(id){
		$('#styleList').hide();
		if($('#sysTableDiv').attr('id')=='sysTableDiv' ){//表示存在该div
			$('#sysTableDiv').hide();
		}
		if($('#sysTableDivNew').attr('id')!='sysTableDivNew'){
			var table = "<div id='sysTableDivNew' ><table id='systemTable'><tbody>";
			table = table+appendFirstMenus();
			table = table+"</tbody></table></div>";
			$('body').append(table);
		}
		$('#sysTableDivNew').show();
		var position = $("#"+id).position();
		var offset = $("#"+id).offset(); 
		$('#sysTableDivNew').css('top', (position.top+36)+'px');
		var widtha =  parseInt($('#sysTableDivNew').css('width'))-56+5 ;//56表示“更多”按钮的宽度,+5表示不紧贴右边
		$('#sysTableDivNew').css('left', offset.left-widtha+'px'); 
	}
	
	function appendFirstMenus(){
		var tableInfo = "";
		<#list menus?if_exists as item>
			<#if (item_index>=showNum)>
					tableInfo = tableInfo+"<tr><td>"+
					"<a "+
					<#if item.openWay.code=="firstMenu.open.way.currentPageOpen">
						"href=\"${item.menuUrl?if_exists}\" "+
					<#elseif item.openWay.code=="firstMenu.open.way.newPageOpen">
						"target=\"_blank\" href=\"${item.menuUrl?if_exists}\" "+
					<#else>
						"href=\"#\" onclick=\"${item.event?if_exists}\" "+
					</#if>
					"<#if item_index==showNum-1>id=\"lastSys\"</#if> "+
					"class=\"<#if item.id?if_exists==firstMenuId?if_exists>top-selected</#if>\">"+
					<#if item.type.code?if_exists=='menu.type.standard'>
						"<span><span></span>${item.name?if_exists}</span>"+
					<#else>
						"<span><span class=\"custom\"></span>${item.name?if_exists}</span>"+
					</#if>
				"</a>"+
				"</td></tr>";
				
			</#if>
		</#list>
		return tableInfo;
	}
	//当是iframe显示页面时，设置菜单的选中样式
	function __setSelectClass(menuLayer,menuCode){
		if(menuLayer=="2"){
			$("li[id='"+menuCode+"']").attr("class","sec-selected");//
		}else if(menuLayer=="3"){
			$("div.west-notree-selected").removeClass("west-notree-selected");//3
			$("#"+menuCode).parent().addClass("west-notree-selected");//3
		}else if(menuLayer=="4"){
			$("div[menuInfo^='4_']").removeClass("four-menu-selected");//4
			$("div[menuInfo^='4_']").addClass("four-menu");//4
			$("div[menuInfo='4_"+menuCode+"']").attr("class","four-menu-selected");//4
		}
	}
</script>