<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/wf-taglibs.jsp"%>

<html>
<div class="ui-layout-center">
	<aa:zone name="wfd_main">
		<div class="opt-btn">
			<button  class='btn' onclick="submitBasic();" hidefocus="true"><span><span><s:text name="menuManager.save"></s:text></span></span></button>
			<button class='btn' onclick="basicGoBack();" hidefocus="true"><span><span><s:text name="menuManager.back"></s:text></span></span></button>
		</div>
		<div id="opt-content">
			<div id="message" style="display:none;"><s:actionmessage theme="mytheme" /></div>
			<form id="inputForm" name="inputForm" action="" method="post">
				<input type="hidden" name="type" value="${type }"/>
				<input type="hidden" name="wfdId" value="${wfdId }"/>
				<input id="system_id" name="sysId" type="hidden" value="${sysId }"/>
				<input id="vertion_type" name="vertionType" type="hidden" value="${vertionType }"/>
				<table class="form-table-without-border">
					<tr>
						<td class="content-title"><s:text name="wf.table.code"></s:text>：</td>
						<td> ${code } </td>
						<td></td>
					</tr>
					<tr>
						<td class="content-title" style="width: 90px;"><s:text name="wf.table.name"></s:text>：</td>
						<td> <input name="name" value="${name}" ></input><span class="required">*</span></td>
						<td></td>
					</tr>	
					<tr>
						<td class="content-title" style="width: 90px;"><s:text name="wf.table.customCategories"></s:text>：</td>
						<td> <input name="customType" value="${customType}" ></input></td>
						<td></td>
					</tr>	
					
					<tr>
						<td class="content-title"><s:text name="wf.table.administrator"></s:text>：</td>
						<td><input name="adminName" value="${adminName}" id="adminName" readonly="readonly" />
							<input name="adminLoginName" value="${adminLoginName}" type="hidden" id="adminLoginName" />
							<input name="adminId" value="${adminId}" type="hidden" id="adminId" /><span class="required">*</span>
							<a href="#" onclick='selectPerson();' title='<s:text name="wf.engine.choose"></s:text>' 
									class="small-btn" id="selectBtn"><span><span><s:text name="wf.engine.choose"></s:text></span></span></a>
						</td>
						<td></td>
					</tr>
				
					<tr>
						<td class="content-title"><s:text name="wf.table.type"></s:text>：</td>
						<td>
							<select name="typeId" id="typeId" class="typeRequired">
								<option value=""><s:text name="wf.table.pleaseSelectType"></s:text></option>
								<s:iterator value="typeList">
									<s:if test="typeId==id">
										<option value="${id }" selected="selected">${name }</option>
									</s:if><s:else>
										<option value="${id }">${name }</option>
									</s:else>
								</s:iterator>
							</select> 
							<span class="required">*</span>
						</td>
						<td></td>
					</tr>
					<tr>
						<td class="content-title"><s:text name="wf.table.subordinateSystem"></s:text>：</td>
						<td>
							<select name="systemId" id="systemId" class="systemRequired">
								<option value=""><s:text name="wf.table.pleaseSelectTheSystem"></s:text></option>
								<s:iterator value="menus">
									<s:if test="workflowDefinition.systemId==systemId">
										<option value="${systemId }" selected="selected">${name }</option>
									</s:if><s:else>
										<option value="${systemId }">${name }</option>
									</s:else>
								</s:iterator>
							</select> 
							<span class="required">*</span>
							 </td>
						<td></td>
					</tr>
					 <s:if test="hasBranch">
						<tr>
							<td class="content-title"><s:text name="wf.table.subordinateBranch"></s:text>：</td>
							<td>
								<select name="subCompanyCode" id="subCompanyCode" class="branchRequired">
									<option value=""><s:text name="wf.table.pleaseSelectBranch"></s:text></option>
									<s:if test="subCompanyCode==companyCode">
										<option value="${companyCode }" selected="selected">${companyName }</option>
									</s:if><s:else>
										<option value="${companyCode }">${companyName }</option>
									</s:else>
									<s:iterator value="branches">
										<s:if test="subCompanyCode==code">
											<option value="${code }" selected="selected">${name }</option>
										</s:if><s:else>
											<option value="${code }">${name }</option>
										</s:else>
									</s:iterator>
								</select> 
								<span class="required">*</span>
								 </td>
							<td></td>
						</tr>
					 </s:if>
				</table>
			</form>	
	</div>	
	</aa:zone>
</div>	
</html>
