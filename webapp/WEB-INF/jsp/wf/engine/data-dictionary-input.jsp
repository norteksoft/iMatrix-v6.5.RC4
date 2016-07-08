<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/wf-taglibs.jsp"%>

<html>
<div class="ui-layout-center">
	<aa:zone name="dict_zone">
		<div class="opt-btn">
			<button  class='btn' onclick="saveDict();" hidefocus="true"><span><span><s:text name="menuManager.save"></s:text></span></span></button>
			<button class='btn' onclick="setPageState();dataDicBack('returnForm','${wfCtx}/engine/data-dictionary.htm','dict_zone','dictPage');" hidefocus="true"><span><span><s:text name="menuManager.back"></s:text></span></span></button>
		</div>
		<div id="opt-content">
		<div id="backMsg" style="margin-left: 12px;color: green;"><s:actionmessage theme="mytheme"/></div>
		
		<form id="dictForm" name="dictForm" action="">
			<input type="hidden" name="id" value="${id}"/>
			<input type="hidden" name="operate" value="${operate}"/>
			
			<table class="Table" >
			<tr style="height: 30px;">
				<td style="width: 90px;"><s:text name="wf.dictionary.category"></s:text>：</td>
				<td><input id="dict_tid" name="typeId" type="hidden" value="${typeId}">
					<input id="dict_noid" name="typeNo" type="hidden" value="${typeNo}">
						<select id="data_dict_type" name="typeName" style="width: 12em;" onchange="setDictTypeId(this);">
							<option typeId="0" typeNo=""></option>
							<s:iterator value="typeList">
								<s:if test="typeName==name">
									<option selected="selected" value="${name}"  typeId="${id }" typeNo="${no }"><s:property value="name"/></option>
								</s:if><s:else>
									<option value="${name}"  typeId="${id }" typeNo="${no }"><s:property value="name"/></option>
								</s:else>
							</s:iterator>
						</select>
				</td>
			</tr>
		 		<tr style="height: 30px;">
					<td style="width: 90px;"><s:text name="wf.dictionary.information"></s:text>：
					</td>
					<td>
						<input id="info" name="info" value="${info}" style="width: 12em;" maxlength="40"/><span class="required">*</span>
					</td>
				</tr>
				<tr style="height: 30px;">
					<td id="processTD" style="width: 90px;"><s:text name="wf.dictionary.processName"></s:text>：
					</td>
					<td>
					<s:iterator value="@com.norteksoft.wf.base.enumeration.DataDictProcessType@values()">
						<s:if test="processType!=null">
							<s:if test="processType==code">
								<input name="processType" checked="checked" type="radio" value="${code}" onclick="setProcessType(${code});" id="processType${code }"/><s:text name="%{name}"></s:text>
							</s:if><s:else>
								<input name="processType" type="radio" value="${code}" onclick="setProcessType(${code});" id="processType${code }"/><s:text name="%{name}"></s:text>
							</s:else>
							<s:if test="code==1">
								<s:if test="processType==1">
								<span  id="addProcess" style="display: inline"><a class="small-btn" onclick="addProcesses('${id }','addProcess');" ><span><span><s:text name="wf.engine.choose"></s:text></span></span></a></span>
								</s:if><s:else>
								<span  id="addProcess" style="display: none"><a class="small-btn" onclick="addProcesses('${id }','addProcess');" ><span><span><s:text name="wf.engine.choose"></s:text></span></span></a></span>
								</s:else>
							</s:if>
						</s:if><s:else>
							<s:if test="code==0">
								<input  name="processType" type="radio" checked="checked" value="${code}" onclick="setProcessType(${code});" id="processType${code }"/><s:text name="%{name}"></s:text>
							</s:if><s:else>
								<input  name="processType" type="radio" value="${code}" onclick="setProcessType(${code});" id="processType${code }"/><s:text name="%{name}"></s:text>
								<span  id="addProcess" style="display: none"><a class="small-btn" onclick="addProcesses('${id }','addProcess');" ><span><span><s:text name="wf.engine.choose"></s:text></span></span></a></span>
							</s:else>
						</s:else>
					</s:iterator> 
					<ul class="noListStyle" id="processRange" style="margin: 10px 0 0 68px;display:none;">
						<s:iterator value="processTaches" id="processTache">
							<li><input name="processes" type="text" value="${processTache.processDefinitionId };${processTache.processDefinitionName}[${processTache.tacheName}]" /></li>
						</s:iterator>
						<s:iterator value="processPros" id="process">
							<li><input name="processes" type="text" value="${process.processDefinitionId };${process.processDefinitionName}" /></li>
						</s:iterator>
					</ul>
					</td>
			</tr>
			<tr>
				<td style="width: 90px;">
				</td>
				<td >
					<div id="processDiv" >
					<s:if test="processType==1">
						<table id="processTb" class="leadTable" style="width:440px;">
							<thead>
								<tr>
								<th style="width:300px;"><s:text name="wf.dictionary.process"></s:text></th>
								<th style="width:140px;"><s:text name="wf.dictionary.operation"></s:text></th>
								</tr>
							</thead>
							<tbody id="processViewTb">
								<s:iterator value="processesView" id="processView">
									<tr >
										<td>${processView[0]}</td>
										<td><a href="#" onclick="deleteDictProcess(this,'${processView[1]}','${processView[0]}');"><s:text name="menuManager.delete"></s:text></a></td>
									</tr>
								</s:iterator>
							</tbody>
						</table>
					</s:if>
				</div>
				</td>
			</tr>
				<tr style="height: 30px;">
					<td style="width: 90px;"><s:text name="wf.dictionary.use"></s:text>：
					</td>
					<td>
						<select id="dict_useType" name="type"style="width: 12em;" onchange="changeType()">
						<s:iterator value="@com.norteksoft.wf.base.enumeration.DataDictUseType@values()">
							<s:if test="type == code">
								<option selected="selected" value="${code}"><s:text name="%{name}"></s:text></option>
							</s:if><s:else>
								<option value="${code}"><s:text name="%{name}"></s:text></option>
							</s:else>
						</s:iterator>
						</select></td></tr>
								
	 		<tr style="height: 34px;">
					<td style="width: 90px;"><s:text name="wf.dictionary.operation"></s:text>：</td>
					<td>
						<div id="dict_operation"  >
							<s:if test="id != null && type == 0">
								<s:iterator value="@com.norteksoft.wf.base.enumeration.TransactorPermission@values()" status="ind">
									<s:if test="operation!=null && operation.contains(name+',')">
										<div style="display: inline;float: left;">
											<input id="operations${ind.index}" name="operations" checked="checked" type="checkbox" value="${name}" /><s:text name="%{name}"></s:text>
										&nbsp;</div>
									</s:if><s:else>
										<div style="display: inline;float: left;">
											<input id="operations${ind.index}" name="operations" type="checkbox" value="${name}" /><s:text name="%{name}"></s:text> 
										&nbsp;</div>
									</s:else>
								</s:iterator>
							</s:if>
							<s:elseif test="id != null && type == 1">
								<s:iterator value="@com.norteksoft.wf.base.enumeration.TextPerimssion@values()" status="ind">
									<s:if test="operation!=null && operation.contains(name+',')">
										<div style="display: inline;float: left;">
											<input id="operations${ind.index}" name="operations" checked="checked" type="checkbox" value="${name}" /><s:text name="%{name}"></s:text> 
										&nbsp;</div>
									</s:if><s:else>
										<div style="display: inline;float: left;">
											<input id="operations${ind.index}" name="operations" type="checkbox" value="${name}" /><s:text name="%{name}"></s:text> 
										&nbsp;</div>
									</s:else>
								</s:iterator>
							</s:elseif>
							<s:else>
								<s:iterator value="@com.norteksoft.wf.base.enumeration.TransactorPermission@values()" status="ind">
									<div style="display: inline;float: left;">
										<input id="operations${ind.index}" name="operations" type="checkbox" value="${name}" /><s:text name="%{name}"></s:text> 
									&nbsp;</div>
								</s:iterator>
							</s:else>
						</div>
						<span id="operationsTip"  style="width:250px"></span>
					</td>
				</tr>
		 		<tr style="height: 30px;">
					<td style="width: 90px;"><s:text name="wf.dictionary.displayOrder"></s:text>：</td>
					<td>
						<input name="displayIndex" value="${displayIndex}"style="width: 12em;" id="displayIndex" onkeyup="value=value.replace(/[^\d]/g,'')" maxlength="9"/><span class="required">*</span>
					</td>
				</tr>
		 		<tr style="height: 34px;">
					<td style="width: 90px;" ><s:text name="wf.dictionary.remarks"></s:text>：
					</td>
					<td>
						<textarea style="height: 50px;width:440px;" name="remark" id="remark" onfocus="ss=setInterval(getFormContent,600)" onblur="clearInterval(ss)">${remark}</textarea>
					</td>
				</tr>
				<tr style="min-height: 30px;width:150px;">
					<td style="width: 90px;"><s:text name="wf.dictionary.managementPersonnel"></s:text>：</td>
					<td>
					<a href="#" onclick="addUsers(${id});" class="small-btn" style="margin-bottom:5px;" id="addBtn"><span><span><s:text name="wf.dictionary.add"></s:text></span></span></a>
					<a href="#" onclick="$('#slcMan').html('');$('#userNamesView').html('');" class="small-btn" style="margin-bottom:5px;"><span><span><s:text name="menuManager.clear"></s:text></span></span></a>
					<!-- 
					<a href="#" onclick="clearAway();" class="small-btn" style="margin-bottom:5px;"><span><span>移除</span></span></a>
					 -->
					<div  class="noListStyle" id="viewMan" >
						<div style="width:80%" id="userNamesView">${userNamesView}</div>
					</div>
					<div id="slcMan" style="margin: 10px 0 0 68px;display:none;">
						<s:iterator value="dduUsers" id="ddu">
						<s:if test="loginName == 'all_users'">
						<input name="userNames" type="text" value="${ddu.loginName}" /><br/>
						</s:if><s:else>
						<input name="userNames" type="text" value="0;${ddu.infoName}[${ddu.loginName}:${ddu.infoId}]" /><br/>
						</s:else>
						</s:iterator>
						<s:iterator value="depts" id="dept">
							<input name="deptNames" type="text" value="1;${dept.infoName}[${dept.infoId}]" /><br/>
						</s:iterator>
						<s:iterator value="workGroups" id="group">
							<input name="groupNames" type="text" value="2;${group.infoName}[${group.infoId}]" /><br/>
						</s:iterator>
						<s:iterator value="ranks" id="rank">
							<input name="rankNames" type="text" value="3;${rank.infoName}[${rank.infoId}]" /><br/>
						</s:iterator>
					</div>
					</td>
				</tr>
			  </table>
		</form>
		<div id="transactor_permission" style="display: none;">
				<s:iterator value="@com.norteksoft.wf.base.enumeration.TransactorPermission@values()">
				    <div style="display: inline;float: left;">
					<input name="operations" value="${name}" type="checkbox"><s:text name="%{name}"></s:text>
					&nbsp;</div>
				</s:iterator>
		</div>
		<div id="text_perimssion" style="display: none;">
				<s:iterator value="@com.norteksoft.wf.base.enumeration.TextPerimssion@values()">
				    <div style="display: inline;float: left;">
					 <input name="operations" value="${name}" type="checkbox"><s:text name="%{name}"></s:text>
					&nbsp;</div>
				</s:iterator>
		</div>
	</div>	
	</aa:zone>
</div>	
</html>
