<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/wf-taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title><s:text name="wf.prcoess.management"/></title>
<%@ include file="/common/wf-iframe-meta.jsp"%>
</head>

<body>
<div class="ui-layout-center" style="top:0;left:0;border: 0px;">
	<div class="opt-body">
	<aa:zone name="wfd_main">
		<div class="opt-btn">
			<button class='btn' onclick="doDeleteTasks();" id="okbtn"><span><span><s:text name="menuManager.confirm"></s:text></span></span></button>
			<button class='btn' onclick="cancelDeleteTasks();" ><span><span><s:text name="process.cancel"></s:text></span></span></button>
		</div>
		<div id="opt-content" >
			<span id="message" style="display:none;margin-left: 10px;" ><s:actionmessage theme="mytheme" /></span>
			<s:if test='chooseTasks.get("JUST_ONE")!=null'>
				<div style="padding: 5px;"><s:text name="wf.workflowDefinitionChooseTask.useHandlePeople"/></div>
				<table style="width: 99%;border: solid 1px;" class="tasks">
					<tr>
						<td><s:text name="wf.table.taskName"></s:text></td>
						<td><s:text name="wf.table.linkName"></s:text></td>
						<td><s:text name="wf.table.transactor"/></td>
						<td><s:text name="wf.table.createdTime"/></td>
						<td><s:text name="wf.table.originator"/></td>
						<td><s:text name="wf.table.processName"></s:text></td>
					</tr>
					<s:iterator value='chooseTasks.get("JUST_ONE")' id="task">
					    <tr>
							<td>${title}</td>
							<td>${name}</td>
							<td>${transactorName}</td>
							<td><s:date name="createdTime" format="yyyy-MM-dd HH:mm"/> </td>
							<td>${creatorName}</td>
							<td>${groupName}</td>
					    </tr>
					</s:iterator>
				</table>
			</s:if>
			<s:if test='chooseTasks.get("JUST_ONE")==null&&chooseTasks.size()>0||chooseTasks.size()>1'>
				<div style="padding: 5px;"><s:text name="wf.workflowDefinitionChooseTask.leastOneTask"/></div>
				<table style="width: 99%;border: solid 1px;" class="tasks">
					<tr>
						<td style="width: 30px;"></td>
						<td><s:text name="wf.table.taskName"></s:text></td>
						<td><s:text name="wf.table.linkName"></s:text></td>
						<td><s:text name="wf.table.transactor"/></td>
						<td><s:text name="wf.table.createdTime"/></td>
						<td><s:text name="wf.table.originator"/></td>
					</tr>
					<s:iterator value="chooseTasks.keySet()" id="instanceTs">
						<s:if test="#instanceTs!='JUST_ONE'">
						<tbody class="selected">
						<tr> 
							<td colspan="6"><s:text name="wf.workflowDefinitionChooseTask.instance"></s:text><s:set name="groupName" 
								value="chooseTasks.get(#instanceTs).get(0).groupName"></s:set> ${groupName}</td>
						</tr>
					    <s:iterator value="chooseTasks.get(#instanceTs)" id="task">
						    <tr>
						    	<td><input type="checkbox" name="taskIds" value="${id}"> </td>
								<td>${title}</td>
								<td>${name}</td>
								<td>${transactorName}</td>
								<td><s:date name="createdTime" format="yyyy-MM-dd HH:mm"/> </td>
								<td>${creatorName}</td>
						    </tr>
					    </s:iterator>
					    </tbody>
					    </s:if>
					</s:iterator>
				</table>
			</s:if>
			<s:else>
				<script type="text/javascript"> $().ready(function(){ $("#okbtn").remove(); }); </script>
			</s:else>
		</div>
	</aa:zone>
	</div>
	</div>
</body>
</html>
