<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/wf-taglibs.jsp"%>
<html>
<head>
	<title>委托管理</title>
	<%@ include file="/common/wf-iframe-meta.jsp"%>
</head>
<body>
<div class="ui-layout-center">
<aa:zone name="delegatemainlist">
<form action="" name="defaultForm" id="defaultForm" method="post"></form>
	<div class="opt-btn">
		<button class="btn" onclick="setPageState();ajaxSubmit('defaultForm','${wfCtx}/engine/delegate-main.htm','delegatemainlist');"><span><span><s:text name="menuManager.back"></s:text></span></span></button>
	</div>
	<div id="opt-content">
	<table class="Table">
	 		<tr style="height: 30px;">
				<td><s:text name="wf.table.principal"></s:text>：</td>
				<td>${trustorName }</td>
		  	</tr>
		  	<tr style="height: 30px;">
				<td><s:text name="wf.table.bailee"></s:text>：</td>
				<td>${trusteeName }</td>
		  	</tr>
	 		<tr style="height: 30px;">
				<td><s:text name="wf.table.effectiveDate"></s:text>：</td>
				<td><s:date name="beginTime"  format="yyyy-MM-dd" /> </td>
		  	</tr>
	 		<tr style="height: 30px;">
				<td><s:text name="wf.table.closingDate"></s:text>：</td>
				<td><s:date name="endTime"  format="yyyy-MM-dd" /> </td>
		  	</tr>
			<s:if test="style==1">
		 		<tr style="height: 30px;">
					<td><s:text name="wf.table.commissionedForm"></s:text>：</td>
					<td><s:text name="wf.engine.delegate.specifyProcess"></s:text></td>
			  	</tr>
			  	<tr style="height: 30px;">
					<td><s:text name="wf.table.commissionedProcess"></s:text>：</td>
					<td>${name}(${workflowVersion})</td>
			  	</tr>
		 		<tr style="height: 30px;">
					<td><s:text name="wf.table.commissionLink"></s:text>：</td>
					<td>${activityName}
					 </td>
			  	</tr>
		  	</s:if>
			<s:elseif test="style==2">
			     <tr style="height: 30px;">
					<td><s:text name="wf.table.commissionedForm"></s:text>：</td>
					<td><s:text name="wf.engine.delegate.allProcess"></s:text></td>
			  	</tr>        
			</s:elseif>
			<s:elseif test="style==3">
				<tr style="height: 30px;">
					<td><s:text name="wf.table.commissionedForm"></s:text>：</td>
					<td><s:text name="wf.engine.delegate.scopeOfAuthority"></s:text></td>
			  	</tr> 
			  	<tr style="height: 30px;">
					<td><s:text name="wf.engine.delegate.scopeOfAuthority"></s:text>：</td>
					<td>${selectedRoleNames }</td>
			  	</tr>   
			</s:elseif>
	 		
	 		<tr style="height: 60px;">
	 			  <td><s:text name="wf.engine.delegate.illustrate"></s:text>：</td>
				<td width="380"><textarea name="remark" cols="50" rows="5" readonly="readonly">${remark}</textarea></td>
		  	</tr>
		</table>
		</div>
</aa:zone>
</div>
</body>
</html>
