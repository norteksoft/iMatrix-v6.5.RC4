<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/mm-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html >
<head>
	<title><s:text name="mm.performance.monitoring.parameter.is.set"/></title>
	<%@ include file="/common/mm-meta.jsp"%>
	<script type="text/javascript">
	function updateParameter(){
		var boxes = jQuery("#monitorParmeterList").jqGrid("getGridParam",'selarrrow');
		if(boxes==null||boxes==""){
			iMatrix.alert(iMatrixMessage["selectInfo"]);
		}else if(boxes.length>1){
			iMatrix.alert(iMatrixMessage["selectOneInfo"]);
		}else{
			ajaxAnyWhereSubmit('defaultForm','${mmCtx}/mm/monitor-parmeter-input.htm?id='+boxes[0],'form_main');
		}
	}
	</script>
</head>
<body>
	<script type="text/javascript">
		var secMenu="mm_monitor_infor";
		var thirdMenu="_mm_infor";
	</script>
	
	<%@ include file="/menus/header.jsp"%>
	
	<div class="ui-layout-west">
		<%@ include file="/menus/mm-thd-menu.jsp"%>
	</div>
	<form action="" id="defaultForm" name="defaultForm" method="post"></form>
	<div class="ui-layout-center">
	<aa:zone name="form_main">
			<div class="opt-body">
				<div id="opt-content" >
					<div id="message"><s:actionmessage theme="mytheme" /></div>
					<script type="text/javascript">setTimeout('$("#message").hide("show");',3000);</script>
					<s:if test="jwebType=='http'">
						<table class="leadTable">
				     		<thead>
				     			<tr>
				     				<th><s:text name="mm.request.uri"></s:text></th>
				     				<th><s:text name="mm.create.time"/></th>
				     				<th><s:text name="mm.inertia.time"/></th>
				     				<th><s:text name="mm.used.time"/></th>
				     				<th>Ip</th>
				     				<th><s:text name="mm.detailed.trajectory"/></th>
				     			</tr>
				     		</thead>
				     		<tbody>
				     		<s:iterator value="monitorInfors">
				     			<tr>
				     				<td>${uri}</td>
				     				<td>${createdDate}</td>
				     				<td>${inActiveTime}</td>
				     				<td>${cost}</td>
				     				<td>${ip}</td>
				     				<td>${detail}</td>
				     			</tr>
				     		</s:iterator>
				     		</tbody>
			     		</table>
		     		</s:if>
		     		<s:if test="jwebType=='meth'">
					<table class="leadTable">
		     		<thead>
		     			<tr>
		     				<th><s:text name="mm.execute.class"/></th>
		     				<th><s:text name="mm.create.time"></s:text></th>
		     				<th><s:text name="mm.inertia.time"/></th>
		     				<th><s:text name="mm.used.time"/></th>
		     				<th><s:text name="mm.detailed.trajectory"/></th>
		     			</tr>
		     		</thead>
		     		<tbody>
		     		<s:iterator value="monitorInfors">
		     			<tr>
		     				<td>${meth}</td>
		     				<td>${createdDate}</td>
		     				<td>${inActiveTime}</td>
		     				<td>${cost}</td>
		     				<td>${detail}</td>
		     			</tr>
		     		</s:iterator>
		     		</tbody>
		     	</table>
		     	</s:if>
		     	<s:if test="jwebType=='jdbc'">
					<table class="leadTable">
		     		<thead>
		     			<tr>
		     				
		     				<th><s:text name="mm.create.time"></s:text></th>
		     				<th><s:text name="mm.inertia.time"/></th>
		     				<th><s:text name="mm.used.time"/></th>
		     				<th><s:text name="mm.execute.jdbc"></s:text></th>
		     				<th><s:text name="mm.detailed.trajectory"/></th>
		     			</tr>
		     		</thead>
		     		<tbody>
		     		<s:iterator value="monitorInfors">
		     			<tr>
		     				<td>${createdDate}</td>
		     				<td>${inActiveTime}</td>
		     				<td>${cost}</td>
		     				<td>${sqlList}</td>
		     				<td>${detail}</td>
		     			</tr>
		     		</s:iterator>
		     		</tbody>
		     	</table>
		     	</s:if>
				</div>
			</div>
	</aa:zone>
	</div>
</body>
</html>