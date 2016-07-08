<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/mms-taglibs.jsp"%>
<html>
<head>
<title>流转历史</title>
	<%@ include file="/common/mms-iframe-meta.jsp"%>
</head>
<body>
<div class="ui-layout-center">
	<aa:zone name="button_zone"></aa:zone>
	<aa:zone name="content_zone">
	<s:if test="view">
		<table class="form-table-border-left" >
				<thead>
					<tr>
						<th><s:text name="wf.button.transactor"></s:text></th>
						<th><s:text name="wf.table.managementDate"></s:text></th>
						<th><s:text name="wf.table.managementAdvice"></s:text></th>
					</tr>
				</thead>
				<tbody>
					<s:iterator value="temps"> 
						<tr style="height: 22px;"><th colspan="2"> ${name} </th><th>(<s:text name="instance.history.agree"></s:text>：${yesNum}&nbsp;&nbsp;&nbsp;&nbsp;<s:text name="instance.history.disagree"></s:text>：${noNum} &nbsp;&nbsp;&nbsp;&nbsp;合计：${yesNum+noNum})</th></tr>
						<s:if test="instanceInHistory">
							<s:iterator value="historyTask">
								<tr>
									<td width="200">${transactorName}</td>
									<td width="200"><s:date name="transactDate"  format="yyyy-MM-dd HH:mm" /></td>
									<td >
										<s:if test="taskProcessingResult.name=='transition.approval.result.agree'"><s:text name="instance.history.agree"></s:text></s:if><s:else><s:text name="instance.history.disagree"></s:text></s:else>
									</td>
								</tr>
							</s:iterator>
						</s:if><s:else>
							<s:iterator value="task">
								<tr>
									<td width="200">${transactorName}</td>
									<td width="200"><s:date name="transactDate"  format="yyyy-MM-dd HH:mm" /></td>
									<td >
										<s:if test="taskProcessingResult.name=='transition.approval.result.agree'"><s:text name="instance.history.agree"></s:text></s:if><s:else><s:text name="instance.history.disagree"></s:text></s:else>
									</td>
								</tr>
							</s:iterator>
						</s:else>
					</s:iterator>
				</tbody>
			</table>
	</s:if><s:else>${message }</s:else>
	</aa:zone>
</div>
</body>
</html>
