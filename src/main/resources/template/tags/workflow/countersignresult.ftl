<#if view>
<table class="Table changeTR" >
	<thead>
		<tr>
			<th>${transactor}</th>
			<th>${transactDate}</th>
			<th>${transactOpinion}</th>
		</tr>
	</thead>
	<tbody>
		<#if temps?exists>
			<#list temps as being>
				<tr style="height: 22px;">
					<th colspan="2"> ${being.name} </th>
					<th>(${agree}：#{being.yesNum}&nbsp;&nbsp;&nbsp;&nbsp;${disagree}：#{being.noNum} &nbsp;&nbsp;&nbsp;&nbsp;${total}：#{being.yesNum+being.noNum})</th>
				</tr>
				<#if isInstanceComplete>
					<#list being.getHistoryTask() as been>
						<tr>
							<td width="200">${been.transactorName}</td>
							<td width="200">${been.transactDate?string("yyyy-MM-dd HH:mm")}</td>
							<td >
								<#if been.result='approve'>
									${agree}
								<#else>
									${disagree}
								</#if>
							</td>
						</tr>
					</#list>
				<#else>
					<#list being.getTask() as been>
						<tr>
							<td width="200">${been.transactorName}</td>
							<td width="200">${been.transactDate?string("yyyy-MM-dd HH:mm")}</td>
							<td >
								<#if been.result='approve'>
									${agree}
								<#else>
									${disagree}
								</#if>
							</td>
						</tr>
					</#list>
				</#if>
			</#list>
		</#if>
	</tbody>
</table>
<#else>
	${message}
</#if>