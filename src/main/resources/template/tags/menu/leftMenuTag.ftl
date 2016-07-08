<#if showLeftMenu?if_exists=="true">
	<input id="_iMatrix_company_id" value="${companyId}" type="hidden"/>
		<#if isAccordion?if_exists=="true">
				<div id="__accordion" >
				  	<#list thirdMenus?if_exists as item>
				  		<#if item.type?if_exists=='PLACEHOLDER'>
							<h3 id="__iMatrixPlaceholder_${item.code?if_exists}" style="display:none;"><a href="${item.menuUrl?if_exists}"></a></h3>
							<div class="demo" id="__iMatrixPlaceholderDiv_${item.code?if_exists}_content" style="margin-top: 10px;display:none;"></div>
				  		<#else>
					  		<#if item.iframable?if_exists>
								<h3><a href="${item.menuUrl?if_exists}" id="${item.code?if_exists}" target="${iframeName}">${item.name?if_exists}</a></h3>
					  		<#else>
								<h3><a href="${item.menuUrl?if_exists}" id="${item.code?if_exists}">${item.name?if_exists}</a></h3>
					  		</#if>
								<div id="div_${item.code?if_exists}">
									<#if showZtree?if_exists=="true">
										<#if item.id?if_exists?string('###0')==thirdMenuId?if_exists?string('###0')>
											<div id="__fourmenuTree" class="ztree" style="background: none;">
											</div>
										</#if>
									<#else>
										<#if fourMenus?if_exists?size gt 0>
											<#list fourMenus?if_exists as being>
												<#if being.type?if_exists=='PLACEHOLDER'>
										  			<div class="four-menu" style="display:none;" id="__iMatrixPlaceholder_${being.code?if_exists}" iMatrixType="fourMenu">
										  				<a href="#this"></a>
													</div>
										  		<#else>
													<#if being.event?if_exists=="">
														<div class="four-menu"  iMatrixType="fourMenu" menuInfo="${being.layer?if_exists}_${being.code?if_exists}">
															<a href="${being.menuUrl?if_exists}" 
															<#if being.externalable?if_exists>
																target="_blank"
															<#else>
																<#if being.iframable?if_exists>
																	target="${iframeName}"
																	onclick="__setSelectClass('4','${being.code?if_exists}');"
																</#if>
															</#if>
															>${being.name?if_exists}</a>
														</div>
													<#else>
														<div class="four-menu"  iMatrixType="fourMenu" menuInfo="${being.layer?if_exists}_${being.code?if_exists}">
															<a href="#this" onclick="${being.event?if_exists}('${being.menuUrl?if_exists}','${being.name?if_exists}')">${being.name?if_exists}</a>
														</div>
													</#if>
												</#if>
											</#list>
										<#else>
											<div class="demo" id="${item.code?if_exists}_content" iMatrixType="demo" style="margin-top: 10px;"></div>
										</#if>
									</#if>
								</div>
						</#if>
					</#list>
				</div>
		<#else>
			<#list thirdMenus?if_exists as item>
				<#if item.type?if_exists=='PLACEHOLDER'>
		  			<div class="west-notree" id="__iMatrixPlaceholder_${item.code?if_exists}" style="display:none;"/>
						<a href="#this" id="${item.code?if_exists}"></a>
					</div>
		  		<#else>
					<div class="west-notree">
					<#if item.event?if_exists=="">
						<#if item.iframable?if_exists>
							<a href="${item.menuUrl?if_exists}&aid=${item.code?if_exists}" id="${item.code?if_exists}" target="${iframeName}" onclick="__setSelectClass('3','${item.code?if_exists}');">${item.name?if_exists}</a>
						<#else>
							<a href="${item.menuUrl?if_exists}&aid=${item.code?if_exists}" id="${item.code?if_exists}" target="_parent">${item.name?if_exists}</a>
						</#if>
					<#else>
						<a href="#" id="${item.code?if_exists}" onclick="${item.event?if_exists}('${item.menuUrl?if_exists}','${item.name?if_exists}');" >${item.name?if_exists}</a>
					</#if>
					</div>
				</#if>
			</#list>	
		</#if>	
</#if>
<script type="text/javascript">
	var __imatrixCtx = "${imatrixUrl?if_exists}";
	var __selectMenuInfo = ${selectMenuInfo};
	var __fourMenuTreeDatas = ${fourMenuTreeDatas};
	var __showZtree = "${showZtree?if_exists}";
	var __menuId="${menuId?if_exists?string('###0')}";
	var __showLeftMenu="${showLeftMenu?if_exists}";
	var __fourMenusLength=${fourMenus?if_exists?size};
	var __iframeName = "${iframeName?if_exists}";
	<#if showLeftMenu?if_exists=="true"&&isAccordion?if_exists=="false">
	$().ready(function(){
		var __iMatrixBrowserUrl=document.location.href;
		var __iMatrixAId = __iMatrixBrowserUrl.split("&aid=");
		if(__iMatrixAId.length<2){
			$("div.ui-layout-west div.west-notree").first().addClass('west-notree-selected');
		}else{
			var __iMatrixCurrentA=$("#"+__iMatrixAId[1]);//取得当前被点击a标签
			if(__iMatrixCurrentA != null){
				$('#'+__iMatrixAId[1]).parent().addClass('west-notree-selected');
			}
		}
	});
	</#if>
</script>
<script src="${resourcesCtx}/templateJs/leftMenuTag.js" type="text/javascript"></script>