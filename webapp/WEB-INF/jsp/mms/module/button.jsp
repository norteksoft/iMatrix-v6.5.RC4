<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/mms-taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<title><s:text name="mms.pageManager"></s:text></title>
	<%@ include file="/common/mms-iframe-meta.jsp"%>
	<script src="${mmsCtx}/js/module-page.js" type="text/javascript" charset="UTF-8"></script>
</head>
<body>
<div class="ui-layout-center">
<aa:zone name="pageTable">
	<aa:zone name="btnZone">
		<div class="opt-btn">
			<button class="btn" onclick="successSave();"><span><span ><s:text name="menuManager.save"></s:text></span></span></button>
			<button class="btn" onclick="previewButton();"><span><span ><s:text name="pageManager.preview"></s:text></span></span></button>
			<button class="btn" onclick="setPageState();returnPageList();"><span><span ><s:text name="menuManager.back"></s:text></span></span></button>
		</div>
	</aa:zone>
	<aa:zone name="contentZone">
	<div id="message" style="display:none;"><s:actionmessage theme="mytheme" /></div>
	   <form id="buttonSaveForm" name="buttonSaveForm" action="" method="post">
			<input name="pageId" value="${pageId }" id="pageId"  type="hidden"></input>
			<input type="hidden" name="menuId" id="menu_Id" value="${menuId }"/>
			<view:formGrid gridId="buttonGrid" code="MMS_BUTTON" entity="${modulePage}" attributeName="buttons"></view:formGrid>
		</form>
		<table id="mata" style="display: none;">
			<tbody>
				<tr>
					<td class="checkbox"><input type="radio" name="columnIdRadio"><input name="columnId" type="hidden"></td>
					<td><input  name="code" maxlength="64"></input></td>
					<td><input name="name" maxlength="64"></input></td>
					<td><input name="displayOrder" size="4" readonly="readonly"></input></td>
					<td><input name="toPageCode" maxlength="64" readonly/>
						<input name="toPageId" maxlength="64" type="hidden"/>
						<a href="#" ><s:text name="menuManager.select"></s:text></a>
					</td>
					<td>
						<textArea cols="60" rows="10" name="event" style="display: none;"></textArea>
						<a href="#"><s:text name="menuManager.edit"></s:text></a>
					</td>
				</tr>
			</tbody>
		</table>
    </aa:zone>
</aa:zone>
</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script src="${resourcesCtx}/widgets/timepicker/timepicker_<%=com.norteksoft.product.util.ContextUtils.getCurrentLanguage()%>.js" type="text/javascript"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
<script src="${resourcesCtx}/widgets/validation/dynamic.validate.js" type="text/javascript"></script>
</html>
