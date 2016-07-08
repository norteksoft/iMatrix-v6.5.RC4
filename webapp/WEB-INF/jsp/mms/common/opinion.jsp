<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/mms-taglibs.jsp"%>

<form name="parameterform" id="parameterform" method="post">

</form>

<aa:zone name="default_opinion_zone">
<div id="___opinion_zone_content">
	
	<table class="form-table-border-left" style="width:700px;" >
		<thead>
			<tr>
				<th style="width: 20%"><s:text name="transactor"></s:text></th>
				<th style="width: 35%"><s:text name="transactOpinion"></s:text></th>
				<th style="width: 25%"><s:text name="transactDate"></s:text></th>
				<th style="width: 20%"><s:text name="history.tag.taskName"></s:text></th>
			</tr>
		</thead>
		<tbody>
		<s:if test="opinionRight.contains('view')">
			<s:iterator value="opinions">
				<tr>
					<td>${transactorName}</td>
					<td>${opinion}</td>
					<td><s:date name="createdTime" format="yyyy-MM-dd HH:mm:ss"/></td>
					<td>${taskName}</td>
				</tr>
			</s:iterator>
		</s:if>
		</tbody>
	</table>
	<div style="margin-top: 10px; ">
		<s:if test="opinionRight.contains('edit')||opinionRight.contains('must')">
			<button onclick="addOpinion();" type="button" class="btn"><span><span><s:text name="common.opinion.add"></s:text></span></span></button>
		</s:if>
	</div>
	
	
	<div id="editOpinion" style="margin: 10px 0 0 8px;display: none;height: 260;width: 480;" >
		<p  class="buttonP">
			<button href="#" onclick="saveOpinion();" type="button" class="btn"><span><span><s:text name="menuManager.confirm"></s:text></span></span></button>&nbsp;&nbsp;<button href="#" onclick="removeOpinion();" type="button" class="btn"><span><span><s:text name="cancel"></s:text></span></span></button>
		</p>
		<p><s:text name="ftl.text.suggestion"></s:text>：</p>
			<form id="approvalViews" name="approvalViews" action="">
				<input type="hidden" name="workflowId" value="${workflowId}"/>
				<input type="hidden" name="taskId" value="${taskId}"/>
				<input type="hidden" name="companyId" value="${companyId}"/>
				<textarea id="opinions" name="opinions" cols="50" rows="8" style="width: 700px;" ></textarea>
			</form>
			
	</div>
</div>
</aa:zone>