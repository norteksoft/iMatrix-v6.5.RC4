<table class="form-table-border-left" style="width:630px;word-wrap:break-word;word-break:break-all;" >
	<thead>
		<tr>
			<th style="width: 20%">${caterer}</th>
			<th style="width: 35%">${content}</th>
			<th style="width: 25%">${date}</th>
			<th style="width: 20%">${tache}</th>
		</tr>
	</thead>
	<tbody>
	<#if opinions?exists>
		<#if view>
			<#if instanceInHistory>
				<#list histOpinions as being>
					<tr>
						<td style="width: 20%">${being.transactorName}</td>
						<td style="width: 35%">${being.opinion}</td>
						<td style="width: 25%">${being.createdTime?string("yyyy-MM-dd HH:mm")}</td>
						<td style="width: 20%">${being.taskName}</td>
					</tr>
				</#list>
			<#else>
				<#list opinions as being>
					<tr>
						<td style="width: 20%">${being.transactorName}</td>
						<td style="width: 35%">${being.opinion}</td>
						<td style="width: 25%">${being.createdTime?string("yyyy-MM-dd HH:mm")}</td>
						<td style="width: 20%">${being.taskName}</td>
					</tr>
				</#list>
			</#if>
		</#if>
	</#if>
	</tbody>
</table>
