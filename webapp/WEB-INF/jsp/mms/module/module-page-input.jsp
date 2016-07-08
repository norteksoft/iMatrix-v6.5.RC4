<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/mms-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>列表管理</title>
	<%@ include file="/common/mms-iframe-meta.jsp"%>
	<script src="${mmsCtx}/js/module-page.js" type="text/javascript" charset="UTF-8"></script>
</head>
<body>
<div class="ui-layout-center">
	<aa:zone name="pageTable">
     <script type="text/javascript">
		$(document).ready(function() {
			$( "#tabs" ).tabs({select:function(event,ui){
			}});
		});
	 </script>
		<form id="defaultForm1" name="defaultForm1"action="">
			<input id="page_id1" type="hidden" name="pageId" value="${pageId }"></input>
			<input type="hidden" id="menu_id"  name="menuId" value="${menuId }"/>
		</form>
		<aa:zone name="btnZone">
		<div class="opt-btn">
			<button class="btn" onclick="savePage();"><span><span><s:text name="menuManager.save"></s:text></span></span></button>
			<button class="btn" onclick="previewPage();"><span><span ><s:text name="pageManager.preview"></s:text></span></span></button>
			<button class="btn" onclick="setPageState();returnPageList();"><span><span ><s:text name="menuManager.back"></s:text></span></span></button>
		</div>
		</aa:zone>
		<div id="opt-content">
		<div id="tabs">
			<ul>
				<li ><a href="#tabs-1" onclick="changePageLeaf('basic');"><s:text name="pageManager.baseInfo"></s:text></a></li>
				<li ><a href="#tabs-1" onclick="changePageLeaf('button')"><s:text name="pageManager.buttonSet"></s:text></a></li>
			</ul>
			<div id="tabs-1">
			<aa:zone name="contentZone">
			    <div id="message" style="display:none;"><s:actionmessage theme="mytheme" /></div>
			    <input type="hidden" name="menuHasView" id="menuHasView" value="${menuHasView }"/>
				<form action="" name="viewSaveForm" id="viewSaveForm" method="post"> 
					<input type="hidden" name="pageId" value="${pageId}" id="pageId"/>
					<input type="hidden" name="oldViewType" value="${oldViewType}" id="oldViewType"/>
					<input type="hidden" name="menuId" value="${menuId}" id="menuId"/>
					<table>
						<tr>
							<td><s:text name="menuManager.code"></s:text>：</td>
				  			<td><s:textfield  theme="simple" id="code" name="code" maxlength="64"></s:textfield><span class="required">*</span></td>
				  			<td></td>
						</tr>
						<tr>
							<td><s:text name="menuManager.name"></s:text>：</td>
				  			<td><s:textfield  theme="simple" id="name" name="name" maxlength="64"></s:textfield><span class="required">*</span></td>
				  			<td></td>
						</tr>
						<tr>
							<td><s:text name="pageManager.pageType"></s:text>：</td>
							<td> <s:select theme="simple" list="@com.norteksoft.mms.module.enumeration.ViewType@values()" listValue="getText(code)" name="viewType" id="viewType" onchange="changeViewType();"></s:select> </td>
							<td></td>
						</tr>
						<tr>
							<td><s:text name="pageManager.selectPage"></s:text>：</td>
							<td>
							<s:if test="viewType.code=='view.type.list'">
								<input id="viewName" value="${listView.name }" readonly="readonly" onclick="selectView();" name="viewName"/><input name="viewId" id="__viewId" type="hidden" value="${viewId }"/><span class="required">*</span>&nbsp;&nbsp;<a href="#" onclick="changeViewType();" title='<s:text name="menuManager.clear"></s:text>'  class="small-btn" id="selectBtn"><span><span><s:text name="menuManager.clear"></s:text></span></span></a>
							</s:if><s:else>
								<input id="viewName" value="${formView.name }(${formView.version})" readonly="readonly" onclick="selectView();" name="viewName"/><input name="viewId" id="__viewId" type="hidden" value="${viewId }"/><span class="required">*</span>&nbsp;&nbsp;<a href="#" onclick="changeViewType();" title='<s:text name="menuManager.clear"></s:text>'  class="small-btn" id="selectBtn"><span><span><s:text name="menuManager.clear"></s:text></span></span></a>
							</s:else>
							</td>
							<td></td>
						</tr>
					</table>
				</form>	
			</aa:zone>
			</div>
		</div>
	</div>
</aa:zone>
</div>
</body>
</html>
