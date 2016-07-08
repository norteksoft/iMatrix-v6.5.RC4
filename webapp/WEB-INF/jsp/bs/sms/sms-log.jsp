<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/setting-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<%@ include file="/common/setting-iframe-meta.jsp"%>
	<title>短信日志</title>
	<script type="text/javascript"></script>
</head>

<body>
	<div class="ui-layout-center">
		<div class="opt-body">
			<form action="" name="defaultForm" id="defaultForm" method="post">
				<input name="ids" id="ids" type="hidden"></input>
				<input name="logType" id="logType" type="hidden" value="<s:property value="logType" />"></input>
			</form>
			<aa:zone name="defaultZone">
				<div class="opt-btn">
					<button class="btn" onclick="iMatrix.showSearchDIV(this);"><span><span><s:text name="formManager.search"></s:text></span></span></button>
				</div>
				<div id="opt-content" >
						<div id="message" style="display: none;"><s:actionmessage theme="mytheme" /></div>
						<form action="" name="pageForm" id="pageForm" method="post">
							<s:if test="logType  == 'send'">
								<view:jqGrid url="${settingCtx}/sms/sms-log.htm?logType=${logType }" code="BS_SMS_LOG_SEND" gridId="page" pageName="page"></view:jqGrid>
							</s:if>
							<s:elseif  test="logType == 'receive'">
								<view:jqGrid url="${settingCtx}/sms/sms-log.htm?logType=${logType }" code="BS_SMS_LOG_RECEIVE" gridId="page" pageName="page"></view:jqGrid>
							</s:elseif>
						</form>
				</div>
			</aa:zone>
		</div>	
	</div>
</body>

</html>