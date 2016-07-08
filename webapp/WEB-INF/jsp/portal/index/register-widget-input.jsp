<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/common/portal-taglibs.jsp"%>
<aa:zone name="widget-zones">
	<div class="opt-btn">
		<button class="btn" onclick='saveWidget();' id="create"><span><span ><s:text name='portal.save'/></span></span></button>
		<button class="btn" onclick="setPageState();backWidget();"><span><span ><s:text name='portal.return'/></span></span></button>
	</div>
	<div id="opt-content">
	<form id="backForm" name="backForm" action="" method="post">
		<input type="hidden" id="system_id"  name="systemId" value="${systemId}"/>
		<input type="hidden" name="widgetId" id="widgetId" value="${id}"/>
	</form>
	<div id="message" style="display:none;"><s:actionmessage theme="mytheme" /></div>
		<form id="inputForm" name="inputForm" action="" method="post">
			<input type="hidden" id="system_id"  name="systemId" value="${systemId}"/>
			<input type="hidden" name="widgetId" id="widgetId" value="${id}"></input>
			<div style=" height:520px; overflow-y:scroll;">
			<table class="form-table-without-border">
				<tr>
					<td style="width: 20%"><s:text name='Window_coding'/>：</td>
					<td style="width: 80%"> <input id="code" name="code" value="${code}"  maxlength="255" ></input><span class="required">*</span></td>
				</tr>	
				<tr>
					<td ><s:text name='Window_name'/>：</td>
					<td> <input id="name" name="name" value="${name}"  maxlength="255"></input><span class="required">*</span></td>
				</tr>	
				<tr>
					<td><s:text name='widget.viewName'/>：</td>
					<td><input id="viewName" name="viewName" value="${viewName}" readonly="readonly" style="background-color:#C0C0C0" maxlength="255" /></td>
				</tr>	
				<tr>
					<td ><s:text name='UrlFor_ContentOf_Window'/>：</td>
					<td> <input id="url" name="url" value="${url}"  maxlength="255" ></input><span class="required">*</span></td>
				</tr>	
				<tr>
					<td  ><s:text name='Whether_default_display'/>：</td>
					<td> <input name="acquiescent" id="defaulted" type="hidden" value="${acquiescent }"></input><input id="ifDefaulted" <s:if test="acquiescent">checked="checked"</s:if> type="checkbox" onclick="defaulteChecked(this);"></input></td>
				</tr>	
				<tr>
					<td  ><s:text name='Whether_display_paging'/>：</td>
					<td> 
						<input name="pageVisible" id="pageVisible" type="hidden" value="${pageVisible }"></input><input id="ifPageVisible" <s:if test="pageVisible">checked="checked"</s:if> type="checkbox" onclick="changePageVisible(this);"></input>
						<span style="color: red;">*（<s:text name="configure_the_parameters" />）</span>
					</td>
				</tr>	
				<tr>
					<td  ><s:text name='small_window_display_frame'/>：</td>
					<td> 
						<input name="borderVisible" id="borderVisible" type="hidden" value="${borderVisible }"></input><input <s:if test="borderVisible">checked="checked"</s:if> type="checkbox" onclick="changeBorderVisible(this);"></input>
						<span style="color: red;">（<s:text name="Applies_only_column_signed" />）</span>
					</td>
				</tr>	
				<tr>
					<td  ><s:text name='Smallform_content_isin_iframe'/>：</td>
					<td> 
						<input name="iframeable" id="iframeable" type="hidden" value="${iframeable }"></input><input <s:if test="iframeable">checked="checked"</s:if> type="checkbox" onclick="changeIframeable(this);"></input>
					</td>
				</tr>
				<tr id="autoLoginTr" <s:if test="!iframeable">style="display:none;"</s:if>>
					<td  ><s:text name='window.auto.login'/>：</td>
					<td> 
						<input name="autoLoginable" id="autoLoginable" type="hidden" value="${autoLoginable }"></input><input <s:if test="autoLoginable">checked="checked"</s:if> type="checkbox"  onclick="changeAutoLoginable(this);"></input>
					</td>
				</tr>		
				<tr>
					<td  ><s:text name='Small_window_height'/>：</td>
					<td> 
						<input name="widgetHeight" id="widgetHeight"   <s:if test="iframeable&&widgetHeight==null">value="500"</s:if><s:else>value="${widgetHeight}"</s:else>></input>
					</td>
				</tr>	
				<tr>
					<td ><s:text name='The_default_location'/>：</td>
					<td> 
						<input type="radio" name="position" value="0"  <s:if test="position==0">checked="checked"</s:if> /><s:text name="LeftColumn"/>
						<input type="radio" name="position" value="1"  <s:if test="position==1">checked="checked"</s:if> /><s:text name="middleColumn"/>
						<input type="radio" name="position" value="2"  <s:if test="position==2">checked="checked"</s:if> /><s:text name="rightColumn"/>
					</td>
				</tr>
				<tr>
					<td ><s:text name='permissions'/>：</td>
					<td> <input id="roleNames" name="roleNames" value="${roleNames}"  readonly="readonly"></input><input id="roleIds" name="roleIds" value="${roleIds}"  type="hidden"></input>&nbsp;&nbsp;<a href="#" onclick="addRole();" class="small-btn"><span><span><s:text name="portal.append"/> </span></span></a>&nbsp;<a href="#" onclick="clearRole();" class="small-btn"><span><span><s:text name="portal.empty"/></span></span></a></td>
				</tr>	
				<tr>
					<td ><s:text name='Window_parameter'/>：</td>
					<td> </td>
				</tr>	
			</table>
			<grid:formGrid gridId="parameterGrid" code="PORTAL_WIDGET_PARAMETER" attributeName="parameters" entity="${widget}"></grid:formGrid>
			</div>
		</form>
	</div>
</aa:zone>
