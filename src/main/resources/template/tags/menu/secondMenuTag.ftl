<a class="scroll-left-btn" onclick="_scrollLeft();">&lt;&lt;</a>
<div class="fix-menu">
<ul class="scroll-menu">
	<#list secMenus?if_exists as item>
		<#if item.id?if_exists?string==secondMenuId?if_exists?string>
			<li id="${item.code?if_exists}" class="sec-selected">
			
		<#else>
			<li id="${item.code?if_exists}">
		</#if>   
				<span>
					<span>
						<a 
							<#if item.event?has_content>
								href="#" onclick="${item.event?if_exists}" 
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
