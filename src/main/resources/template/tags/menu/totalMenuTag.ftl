<!--一级菜单-->
<div id="header" class="ui-north">
	<div id="topnav" role="contentinfo" style="position:absolute top:0px; z-index:999">
		<a href="#" class="top-nav-first">
			<span></span>
		<#list menus?if_exists as item>
			<#if item_index lt showNum>
			</a><a 
					<#if item.openWay.code=="firstMenu.open.way.currentPageOpen">
						href="${item.menuUrl?if_exists}" 
					<#elseif item.openWay.code=="firstMenu.open.way.newPageOpen">
						target="_blank" href="${item.menuUrl?if_exists}" 
					<#else>
						href="#" onclick="${item.event?if_exists}" 
					</#if>
				<#if item_index==showNum-1>id="lastSys"</#if> 
				    menuInfo="${item.layer?if_exists}_${item.code?if_exists}">
			    <!--如果是标准系统，用系统自带图片，如果是其他系统，则使用自定义上传的图片(如果未上传， 有一个默认的图片)-->
			 	<#if item.type.code=="menu.type.standard">
					<span><span class="sys_${item.code?if_exists}" ></span>${item.name?if_exists}</span>
				<#else>
				 	<#if item.imageUrl?if_exists!="">
				 		<span><span class="sys_${item.code?if_exists}" style="background: url('${imatrixUrl}/icons/${item.imageUrl}') no-repeat;width:0.5cm;height:0.5cm;padding-top:5px;"></span>${item.name?if_exists}</span>
				 	<#else>
						<span><span class="sys_custom" ></span>${item.name?if_exists}</span>
				 	</#if>
			 	</#if>
			</#if>
		</#list>
		</a><#if showNum lt menuSize><a id="selectNumen" class="top-nav-last">
					<span id="moreSystemSpan">${moreSystem}</span></a></#if>
	</div>
	<div id="header-logo">
	</div>
	<div id="honorific">
		<span title="${honorificTitle}<#if helloVisible?if_exists>, ${hello}</#if>"><span class="man">&nbsp;</span>${honorificTitle}<#if helloVisible?if_exists>, ${hello}</#if></span>
		<#if dateVisible?if_exists>
			<span title="${currentTime}"><span class="day">&nbsp;</span>${currentTime}</span>
		</#if>
		<#if passwordVisible?if_exists>
			<span title="${changePassword}" onclick="updatePassword();"><a href="#"><span class="password">&nbsp;</span>${changePassword}</a></span>  
		</#if>
		<#if themeChagable?if_exists>
			<span title="${changeSkin}" onclick="changeStyle(event, this);"><a href="#"><span class="theme">&nbsp;</span>${changeSkin}</a></span> 
		</#if>
		<#if languageVisible?if_exists>
			<#if  "zh_CN"?contains(lanague)  >
				<span title="English" onclick="changeLanague('en_US');"><a href="#"><span class="lanagueEn">&nbsp;</span>English</a></span>
			<#else>
				<span title="chineseName" onclick="changeLanague('zh_CN');"><a href="#"><span class="lanagueCn">&nbsp;</span>chineseName</a></span>
		 	</#if>
	 	</#if>
		<#if existable?if_exists>
			<span title="${exit}"><a href="${imatrixUrl}/j_spring_security_logout"><span class="exit">&nbsp;</span>${exit}</a></span> 
		</#if>
	</div>
</div>

<!--二级菜单-->
<#if showSecMenu?if_exists=="true">
<div id="secNav">
		<a class="scroll-left-btn" onclick="_scrollLeft();">&lt;&lt;</a>
		<div class="fix-menu">
		<ul class="scroll-menu">
			<#list secMenus?if_exists as item>
					<li id="${item.code?if_exists}" menuInfo="${item.layer?if_exists}_${item.code?if_exists}">
						<span>
							<span>
								<a 
								<#if item.event?has_content>
									href="#" onclick="${item.event?if_exists}('${item.menuUrl?if_exists}','${item.name?if_exists}');" 
								<#else>
									href="${item.menuUrl?if_exists}"
									<#if item.externalable?if_exists>
										target="_blank"
									<#else>
										<#if item.iframable?if_exists>
											target="${iframeName}"
											onclick="__setSelectClass('2','${item.code?if_exists}');"
										</#if>
									</#if>
								</#if>
								>${item.name?if_exists}</a>
							</span>
						</span>
					</li>
			</#list>
		</ul>
		</div>
		<a class="scroll-right-btn" onclick="_scrollRight();">&gt;&gt;</a>
		<div class="hid-header" onclick="headerChange(this);" title="隐藏"></div>
</div>

<!--左侧菜单-->
	<div class="ui-layout-west" id="__iMatrix_left_menu_div_id" style="display:none;">
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
											<div id="__fourmenuTree" class="ztree" style="background: none;display:none;">
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
														<div class="four-menu" style="display:none;" iMatrixType="fourMenu" menuInfo="${being.layer?if_exists}_${being.code?if_exists}">
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
														<div class="four-menu" style="display:none;" iMatrixType="fourMenu" menuInfo="${being.layer?if_exists}_${being.code?if_exists}">
															<a href="#this" onclick="${being.event?if_exists}('${being.menuUrl?if_exists}','${being.name?if_exists}')">${being.name?if_exists}</a>
														</div>
													</#if>
												</#if>
											</#list>
										<#else>
											<div class="demo" id="${item.code?if_exists}_content" iMatrixType="demo" style="margin-top: 10px;display:none;"></div>
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
	</div>
</#if>
<script type="text/javascript">
	var __versionType = "${versionType?if_exists}";
	var __imatrixCtx = "${imatrixUrl?if_exists}";
	var __showSecMenu = "${showSecMenu?if_exists}";
	var __selectMenuInfo = ${selectMenuInfo};
	var __firstMenus = ${firstMenus};
	var __showNum = "${showNum?if_exists}";
	var __fourMenuTreeDatas = ${fourMenuTreeDatas};
	var __showZtree = "${showZtree?if_exists}";
	var __menuId="${menuId?if_exists?string('###0')}";
	var __changeType="${changeType}";
	var __showLeftMenu="${showLeftMenu?if_exists}";
	var __fourMenusLength=${fourMenus?if_exists?size};
	var __iframeName = "${iframeName?if_exists}";
	<#if showLeftMenu?if_exists=="true"&&isAccordion?if_exists=="false">
	$().ready(function(){
		if("${lanague}"=="zh_CN"){
			$("body").css("font-family","宋体,Arial, Helvetica, sans-serif");
			$(".form-table-without-border td.header-title").css("font-family","'宋体',Arial,Helvetica,sans-serif");
			$(".form-table-border td.header-title").css("font-family","'宋体',Arial,Helvetica,sans-serif");
			$(".form-table-border-left td.header-title").css("font-family","'宋体',Arial,Helvetica,sans-serif");
		}else{
			$("body").css("font-family","Arial");
			$(".form-table-without-border td.header-title").css("font-family","Arial");
			$(".form-table-border td.header-title").css("font-family","Arial");
			$(".form-table-border-left td.header-title").css("font-family","Arial");
		}
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
	<#if showSecMenu?if_exists=="true">
	$().ready(function(){
		__iMatrixDynamicThirdMenuCover();
	});
	</#if>
</script>
<script src="${resourcesCtx}/templateJs/totalMenuTag.js" type="text/javascript"></script>
<script src="${resourcesCtx}/templateJs/leftMenuTag.js" type="text/javascript"></script>