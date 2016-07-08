<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/setting-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<%@ include file="/common/setting-iframe-meta.jsp"%>
	<title>定时设置</title>
</head>
<body onload="">
<div class="ui-layout-center">
<div class="opt-body">
	<div id="opt-content" >
		<aa:zone name="datasource-zones">
			<div id="message" style="display: none;"><s:actionmessage theme="mytheme" /></div>
			<form action="" name="jobInfoFrom" id="jobInfoFrom" method="post">
				<input  type="hidden" name="id" id="id" value="${id}"/>
				<table class="form-table-without-border">
						<tr>
							<td class="content-title"><s:text name="interfaceManager.dataSourceCode"></s:text>：</td>
							<td> ${code }</td>
						</tr>
						<tr>
							<td class="content-title" style="width: 90px;"><s:text name="interfaceManager.dataSourceDriver"></s:text>：</td>
							<td> ${driveName }</td>
						</tr>	
						<tr>
							<td class="content-title"><s:text name="interfaceManager.dataSourceAddress"></s:text>：</td>
							<td>
								${dataBaseUrl }
							</td>
						</tr>
						<tr>
							<td class="content-title"><s:text name="interfaceManager.userName"></s:text>：</td>
							<td>
								${userName }
							 </td>
							<td></td>
						</tr>
						<tr>
							<td class="content-title"><s:text name="interfaceManager.creator"></s:text>：</td>
							<td>
								${creatorName }
							 </td>
							<td></td>
						</tr>
						<tr>
							<td class="content-title"><s:text name="interfaceManager.createTime"></s:text>：</td>
							<td>
							<s:date name="createdTime"  format="yyyy-MM-dd HH:mm" />
							 </td>
						</tr>
						<tr>
							<td class="content-title" style="width: 90px;"><s:text name="interfaceManager.instruction"></s:text>：</td>
							<td>${remark }</td>
						</tr>	
					</table>
			</form>
		</aa:zone>
	</div>
</div>
</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
</html>