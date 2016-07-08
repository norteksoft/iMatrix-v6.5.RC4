<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/mm-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html >
<head>
	<title><s:text name="mm.performance.monitoring.parameter.is.set"/></title>
</head>
<body>
	<aa:zone name="form_main">
			<div class="opt-body">
				<div class="opt-btn">
					<a href="#" class='btn' onclick="mm_saveMonitorParmeter('saveParmeterForm','${mmCtx}/mm/monitor-parmeter-save.htm');"><span><span><s:text name="mm.save"/></span></span></a>
					<a href="#" class='btn' onclick="ajaxAnyWhereSubmit('defaultForm','${mmCtx}/mm/monitor-parmeter.htm','form_main');"><span><span><s:text name="mm.back"></s:text></span></span></a>
				</div>
				<div id="opt-content" >
				<form action="" id="saveParmeterForm" name="saveParmeterForm" method="post">	
					<span id="message" style="display: none;"><font class="onSuccess"><nobr><s:text name="mm.save.sucess"/></nobr></font></span>
					<input type="hidden" id="id" name="id" value="${id}"/>
						<s:if test="systemCode!=null&&systemCode!=''">
							<table class="form-table-without-border" style="width: auto;">
								<tr>
									<td class="content-title"><s:text name="mm.current.system"/></td>
									<td>
										${systemName}
										<input type="hidden" id="systemCode" name="systemCode" value="${systemCode}"/>
									</td>
								</tr>
							</table>
						</s:if><s:else>
							<table class="form-table-without-border" style="width: auto;">
								<tr>
									<td class="content-title"><s:text name="mm.select.system"/></td>
									<td>
										<select name="code" id="code">
											<s:if test="systemCode!=null&&systemCode!=''">
												<option value="${systemCode}">${systemName}</option>
											</s:if>
											<s:iterator value="businessSystems" var="bu">
												<option value="${bu.code}">${bu.name}</option>
											</s:iterator>
										</select>
									</td>
								</tr>
							</table>
						</s:else>
						<br/>
						<s:text name="mm.http.to.access.parameters.configuration"></s:text>
						<table width="100%" class="form-table-border-left" style="margin-top: 10px;">
						  <tr> 
						    <td  ><s:text name="mm.maximum.used.time"/></td>
						    <td ><input name="trace_max_size_http" id="trace_max_size_http" value="${trace_max_size_http}"/></td>
						    <td ><s:text name="mm.maximal.stock"></s:text></td>
						    <td ><input name="trace_filter_active_time_http" id="trace_filter_active_time_http" value="${trace_filter_active_time_http}"/></td>
						  </tr>
						</table>
						
						<br/>
						<s:text name="mm.jdbc.to.access.parameters.configuration"/>
						<table width="100%" class="form-table-border-left" style="margin-top: 10px;">
						  <tr> 
						    <td  ><s:text name="mm.maximum.used.time"/></td>
						    <td ><input name="trace_max_size_jdbc" id="trace_max_size_jdbc" value="${trace_max_size_jdbc}"/></td>
						    <td ><s:text name="mm.maximal.stock"/></td>
						    <td ><input name="trace_filter_active_time_jdbc" id="trace_filter_active_time_jdbc" value="${trace_filter_active_time_jdbc}"/></td>
						  </tr>
						  <tr> 
						    <td  ><s:text name="mm.database.driver"/></td>
						    <td colspan="2">
						    	<textarea rows="4" name="driver_clazzs" id="driver_clazzs" >${driver_clazzs}</textarea>
						    </td>
						  </tr>
						</table>
						
						<br/>
						<s:text name="mm.class.operation.parameters.configuration"/>
						<table width="100%" class="form-table-border-left" style="margin-top: 10px;">
						  <tr> 
						    <td  ><s:text name="mm.maximum.used.time"/></td>
						    <td ><input name="trace_max_size_meth" id="trace_max_size_meth" value="${trace_max_size_meth}"/></td>
						    <td ><s:text name="mm.maximal.stock"/></td>
						    <td ><input name="trace_filter_active_time_meth" id="trace_filter_active_time_http" value="${trace_filter_active_time_meth}"/></td>
						  </tr>
						  <tr>
						  	<td  ><s:text name="mm.class"/></td>
						  	<td colspan="2">
						    	<textarea rows="4" name="detect_clazzs" id="detect_clazzs" >${detect_clazzs}</textarea>
						    </td>
						  </tr>
						</table>
						
						
					</form>
				</div>
			</div>
	</aa:zone>
</body>
</html>