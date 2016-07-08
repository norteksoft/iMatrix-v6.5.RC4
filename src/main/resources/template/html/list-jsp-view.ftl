<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/mms-taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
	<head>
		<title></title>
		<%@ include file="/common/mms-iframe-meta.jsp"%>
		<script src="${resourcesCtx}/widgets/validation/validate-all-1.0.js" type="text/javascript"></script>
		<script src="${resourcesCtx}/widgets/validation/dynamic.validate.js" type="text/javascript"></script>
		<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
				
	</head>
	
	<body onload="getContentHeight();"  onunload="destroyUploadControl();">
		<div class="ui-layout-center">
		<div class="opt-body">
					<div class="opt-btn">
					
						<#if "popup"?contains(inputShowType)  >
							<button class='btn' onclick='window.parent.$.colorbox.close();'><span><span>返回</span></span></button>
						<#else>
							<button class='btn' onclick='setPageState();ajaxSubmit("defaultForm","${ctx}/${fileName}/${entityAttribute}-list.htm","main");'><span><span>返回</span></span></button>
						</#if>
					</div>
				<aa:zone name="main">
					<div id="opt-content" class="form-bg">
					<div id="message" style="display:none;"><s:actionmessage theme="mytheme" /></div>
						<form  id="inputForm" name="inputForm" method="post" action="">
							<input type="hidden" name="id" id="id" value="${id }"/>
							
							
							<table rules=all   style="border: #87CEEB 1px solid;" >
								<tbody id="appendNeed" >
									
										<#if valueList?exists>
											<#list valueList as value0>
													<tr>
														<#list value0 as value00>
															 ${value00} 
														</#list>
													</tr>
											</#list>
										</#if>
									
								</tbody>
							</table>
							
						</form>
					</div>
				</aa:zone>
			</div>
			</div>
	</body>
</html>