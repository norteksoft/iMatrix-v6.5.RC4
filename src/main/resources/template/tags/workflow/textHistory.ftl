<div class="demo" id="textContent" style="display: block; height: auto;"  >
<table class="form-table-border-left">
	<thead>
		<tr>
		<th style="width: 5%">${sequence}</th>
		<th style="width: 15%">${name}</th>
		<th style="width: 35%">${history}</th>
		<th style="width: 45%">${opinion}</th>
		</tr>
	</thead>
	<tbody>
		<#list instanceHistory?if_exists as item>
		<tr>
			<td>
				${item_index+1}
				<#assign _currentSequence="${item_index+1}">
			</td>
			<td>
				<#if item.taskName?if_exists=="" > 
					<#if item.type?if_exists==3>
						${end}
					<#else>
						${start}
					</#if>   
			  	<#else>
			  		<#if item.subTaskId?if_exists!=0 > 
				  	<a href="#" onclick="showSubWorkflowHistory('${item.subTaskId?if_exists}','${item.taskName?if_exists }');">${item.taskName?if_exists }</a>
				  	<#else>
				  		${item.taskName?if_exists }  
				  	</#if>
				</#if> 
			</td>
			<td>
				${item.internationalizationResult?if_exists }
			</td>
			<td>
				${item.transactorOpinion?if_exists }
			</td>
		</tr>
		</#list>
		<#list historyInstanceHistory?if_exists as item>
		<tr>
			<td>
				${item_index+1}
			</td>
			<td>
				<#if item.taskName?if_exists=="" > 
					<#if item.type?if_exists==3>
						${end}
					<#else>
						${start}
					</#if>   
			  	<#else>
			  		<#if item.subTaskId?if_exists!=0 > 
				  	<a href="#" onclick="showSubWorkflowHistory('${item.subTaskId?if_exists}','${item.taskName?if_exists }');">${item.taskName?if_exists }</a>
				  	<#else>
				  		${item.taskName?if_exists }  
				  	</#if>
				</#if> 
			</td>
			<td>
				${item.internationalizationResult?if_exists }
			</td>
			<td>
				${item.transactorOpinion?if_exists }
			</td>
		</tr>
		</#list>
		<#list currentTasks?if_exists as item>
				<tr>
				<td>
				 ${_currentSequence?number+item_index+1}
				</td>
				<td>
					<#if item[2]?if_exists=='subWorkflow' > 
						<a href="#" onclick="showSubWorkflowHistory('${item[3]}','${item[0]}');">${item[0]}</a>
					<#else>
				  		${item[0]}
					</#if>
				</td>
				<td>
					<#if item[2]?if_exists=='subWorkflow' > 
						${createPerson}：${item[1]}
					<#else>
				  		 ${handlePerson}：${item[1]}
					</#if>
				</td>
				<td>
				 
				</td>
				</tr>
				</#list>
	</tbody>
</table>
</div>         