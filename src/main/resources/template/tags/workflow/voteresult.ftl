<#if view>
<table  class="Table changeTR" >
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
					<th>(${approve}：${being.yesNum}&nbsp;&nbsp;&nbsp;&nbsp;${disApprove}：${being.noNum} &nbsp;&nbsp;&nbsp;&nbsp;${waiver}：${being.invaNum} &nbsp;&nbsp;&nbsp;&nbsp;${total}：${being.yesNum+being.noNum+being.invaNum})</th>
				</tr>
				<#if isInstanceComplete>
					<#list being.getHistoryTask() as been>
						<tr>
							<td width="200">${been.transactorName}</td>
							<td width="200">${been.transactDate?string("yyyy-MM-dd HH:mm")}</td>
							<td>
								<#if been.result='agreement'>
									${approve}
								<#elseif been.result='oppose'>
									${disApprove}
								<#else>
									${waiver}
								</#if>
							</td>
						</tr>
					</#list>
				<#else>
					<#list being.getTask() as been>
						<tr>
							<td width="200">${been.transactorName}</td>
							<td width="200">${been.transactDate?string("yyyy-MM-dd HH:mm")}</td>
							<td>
								<#if been.result='agreement'>
									${approve}
								<#elseif been.result='oppose'>
									${disApprove}
								<#else>
									${waiver}
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