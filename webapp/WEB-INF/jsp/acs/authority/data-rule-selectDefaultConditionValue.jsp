<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/acs-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>数据表</title>
	<%@ include file="/common/acs-iframe-meta.jsp"%>
	<script type="text/javascript">
		function enteringValue(){
			var conditionValues = "";
			var conditionNames = "";
			var rowId = $("#rowId").val();
			if("entering"==$("#pageSign").val() || ""==$("#pageSign").val()){
				if("valuesSign"==$("#valuesSign").val()){
					var fieldValue = $("input[name='fieldValue']:checked");
					if(fieldValue.length<=0){
						//请选择一个条件值！
						iMatrix.alert(iMatrixMessage["acs.data.rule.pleaseSelectAConditionValue"]);
						return;
					}else{
						conditionNames = $(fieldValue).attr("title");
						conditionValues = $(fieldValue).attr("value");
					}
				}else{
					conditionNames = $("#fieldValue").val();
					if(conditionNames != ""){
						conditionValues = conditionNames;
					}else{
						//请输入条件值！
						iMatrix.alert(iMatrixMessage["acs.data.rule.pleaseEnterAConditionValue"]);
						return;
					}
				}
			}else if("default"==$("#pageSign").val()){
				var fieldNames = $("input[name='fieldName']:checked");
				if(fieldNames.length<=0){
					//请选择一个标准值！
					iMatrix.alert(iMatrixMessage["acs.data.rule.pleaseSelectAStandardValue"]);
					return;
				}else{
					if("LONG"==$("#dataType").val()){
						for(var i=0;i<fieldNames.length;i++){
							if(conditionValues!=""){
								conditionValues+=",";
								conditionNames+=",";
							}
							conditionNames += fieldNames[i].title;
							conditionValues += fieldNames[i].value;
						}
					}else{
						conditionNames = fieldNames[0].title;
						conditionValues = fieldNames[0].value;
					}
				}
			}else if("beanName"==$("#pageSign").val()){
				conditionNames = $("#springBeanName").val();
				if(conditionNames != ""){
					conditionValues = "beanName:"+conditionNames;
				}else{
					//请填写beanName！
					iMatrix.alert(iMatrixMessage["acs.data.rule.pleaseFillInBeanName"]);
					return;
				}
			}
			window.parent.$("#"+rowId+"_conditionName").attr("value",conditionNames);
			window.parent.$("#"+rowId+"_conditionValue").attr("value",conditionValues);
			window.parent.$.colorbox.close();
		}

		function changeConditionValueType(obj){
			changeViewSet($(obj).val());
		}

		function changeViewSet(position){
			if(position=="entering"){//输入
				$("#tabs-1").css("display","block");
				$("#tabs-2").css("display","none");
				$("#tabs-3").css("display","none");
				$("#pageSign").attr("value","entering");
				$("#selectConditionValueType").attr("value","entering");
			}else if(position=="default"){//默认值 
				$("#tabs-2").css("display","block");
				$("#tabs-1").css("display","none");
				$("#tabs-3").css("display","none");
				$("#pageSign").attr("value","default");
				$("#selectConditionValueType").attr("value","default");
			}else if(position=="beanName"){//beanName
				$("#tabs-3").css("display","block");
				$("#tabs-1").css("display","none");
				$("#tabs-2").css("display","none");
				$("#pageSign").attr("value","beanName");
				$("#selectConditionValueType").attr("value","beanName");
			}
		}

		function validateField(obj,rowId,dataType){
			if(dataType == 'TEXT' || dataType == 'BOOLEAN'){
				window.parent.validateFieldString(obj,rowId);
			}else if(dataType == 'LONG' || dataType == 'INTEGER'){
				var value=obj.value.replace(/[^0-9]/g,'');
				$(obj).attr("value",value);		
			}else if(dataType == 'NUMBER' || dataType == 'AMOUNT' || dataType == 'DOUBLE' || dataType == 'FLOAT'){
				var value=obj.value.replace(/[^0-9\.]/g,'');
				$(obj).attr("value",value);	
			}
		}
	</script>
	<style type="text/css">
	.form-table-without-border td input{
		width:200px;
	}
	</style>
</head>
<body onload="getContentHeight();">
<div class="ui-layout-center">
<div class="opt-body">
	<form id="defaultForm" name="defaultForm"action="" method="post" >
		<input type="hidden" id="rowId" value="${rowId}">
		<input type="hidden" id="dataType" value="${dataType}">
		<input type="hidden" id="pageSign">
	</form>
	<aa:zone name="main_zone">
	
		<div class="opt-btn">
			<!-- 
			<button class="btn" onclick="<s:if test="standardField=='~~roleId'||standardField=='~~subCompanyId'">select();</s:if><s:else>selectMans();</s:else>"><span><span >确定</span></span></button>
			 -->
			<button class="btn" onclick="enteringValue();"><span><span ><s:text name="menuManager.confirm"></s:text></span></span></button>
		</div>
		<div id="opt-content">
			<div id="tabs-0" style="margin-left: 5px">
				<s:text name="authorization.conditionType"></s:text>：<select id="selectConditionValueType" onchange="changeConditionValueType(this);" style="width: 150px;">
					<option value="entering"><s:text name="authorization.handSelect"></s:text></option>
					<s:if test="dataType=='TEXT' || dataType=='LONG'">
					<option value="default"><s:text name="authorization.standardValue"></s:text></option>
					</s:if>
					<option value="beanName"><s:text name="authorization.DynamicCalculate"></s:text></option>
				</select>
			</div>
			<div id="tabs-1" style="margin-left: 5px;margin-top: 10px;">
				<s:if test="values.size()>0">
					<input type="hidden" id="valuesSign" value="valuesSign">
					<s:text name="authorization.conditionValue"></s:text>：<table class="form-table-border-left" style="width: 240px;margin-left: 5px;margin-top: 10px;">
					<s:iterator value="values" var="bean">
						<tr>
							<td style="width: 20%;"><input name="fieldValue" type="radio" value="${bean[0] }" title="${bean[1] }"/></td>
							<td>${bean[1] }</td>
						</tr>
					</s:iterator>
				</table>
				</s:if><s:else>
					<s:text name="authorization.conditionValue"></s:text>：
					<input id="fieldValue" name="fieldValue" onkeyup='validateField(this,"${rowId}","${dataType}");' style="width: 175px;"/>
					<s:if test="dataType=='DATE'">
						<script type="text/javascript">
							$(function(){
								addDatepickerEvent();
							});
							function addDatepickerEvent(){
								$("#fieldValue").attr("readonly","readonly");
								$("#fieldValue").datepicker({
								    changeMonth:true,
								    changeYear:true,
									showButtonPanel:"true"
					        	});
							}
						</script>
					</s:if><s:elseif test="TIME">
						<script type="text/javascript">
						$(function(){
							addDatetimepickerEvent();
						});
						function addDatetimepickerEvent(){
							$("#fieldValue").attr("readonly","readonly");
							$("#fieldValue").datetimepicker({
						    	"dateFormat":"yy-mm-dd",
							    changeMonth:true,
							    changeYear:true,
							    showSecond: false,
								showMillisec: false,
								"timeFormat": "hh:mm"
				        	});
						}
						</script>
					</s:elseif>
				</s:else>
			</div>
			<div id="tabs-2" style="display: none;">
				<s:if test="dataType=='LONG'">
					<table class="form-table-border-left" style="width: 340px;margin-left: 5px;margin-top: 10px;">
						<s:iterator value="@com.norteksoft.acs.base.enumeration.ConditionType@values()" var="conditionVar">
							<tr>
								<td style="width: 15px;"><input name="fieldName" type="checkbox" value="${conditionVar}" title="<s:text name="%{code}"></s:text>"/></td>
								<td><s:text name="%{code}"></s:text></td>
							</tr>
						</s:iterator>
					</table>
				</s:if><s:else>
					<table class="form-table-border-left" style="width: 240px;margin-left: 5px;margin-top: 10px;">
						<tr>
							<td style="width: 15px;"><input name="fieldName" type="radio" value="~~currentUser" title="<s:text name="data.rule.default.value.currentUserName"></s:text>"/></td>
							<td><s:text name="data.rule.default.value.currentUserName"></s:text></td>
						</tr>
						<tr>
							<td style="width: 15px;"><input name="fieldName" type="radio" value="~~currentLoginName" title="<s:text name="data.rule.default.value.currentLoginName"></s:text>"/></td>
							<td><s:text name="data.rule.default.value.currentLoginName"></s:text></td>
						</tr>
						<tr>
							<td style="width: 15px;"><input name="fieldName" type="radio" value="~~currentUserDepartment" title="<s:text name="data.rule.default.value.currentUserMainDepartmentId"></s:text>"/></td>
							<td><s:text name="data.rule.default.value.currentUserMainDepartmentId"></s:text></td>
						</tr>
						<tr>
							<td style="width: 15px;"><input name="fieldName" type="radio" value="~~currentUserMainDepartmentName" title="<s:text name="data.rule.default.value.currentUserMainDepartmentName"></s:text>"/></td>
							<td><s:text name="data.rule.default.value.currentUserMainDepartmentName"></s:text></td>
						</tr>
						<tr>
							<td style="width: 15px;"><input name="fieldName" type="radio" value="~~currentUserRole" title="<s:text name="data.rule.default.value.currentUserRoleId"></s:text>"/></td>
							<td><s:text name="data.rule.default.value.currentUserRoleId"></s:text></td>
						</tr>
						<tr>
							<td style="width: 15px;"><input name="fieldName" type="radio" value="~~currentUserRoleName" title="<s:text name="data.rule.default.value.currentUserRoleName"></s:text>"/></td>
							<td><s:text name="data.rule.default.value.currentUserRoleName"></s:text></td>
						</tr>
					</table>
				</s:else>
			</div>
			<div id="tabs-3" style="display: none;margin-left: 5px;margin-top: 10px;">
				beanName：<input id="springBeanName" name="springBeanName"  style="width: 175px;"/><s:text name="authorization.springInfo"></s:text>
			</div>
		</div>
		<script type="text/javascript">
			$(document).ready(function(){
				settingDefaultConditionValue();
			});
			function settingDefaultConditionValue(){
				var rowId = $("#rowId").val();
				var conditionValue = window.parent.$("#"+rowId+"_conditionValue").val();
				if(conditionValue != ""){
					if(conditionValue.indexOf("~~")==0){
						changeViewSet('default');
						$('input[name="fieldName"][value="'+conditionValue+'"]').attr("checked",true);
					}else if(conditionValue.indexOf("beanName:")==0){
						changeViewSet('beanName');
						$("#springBeanName").val(conditionValue.replace("beanName:",""));
					}else{
						if("LONG"==$("#dataType").val()){
							if("valuesSign"==$("#valuesSign").val()){
								changeViewSet('entering');
								$('input[name="fieldValue"][value="'+conditionValue+'"]').attr("checked",true);
							}else{
								if(conditionValue!=""){
									var values = conditionValue.split(",");
									var sign = false;
									$("input[name='fieldName']").each(function(){
										if(this.value==$.trim(values[0])){
											sign = true;
											return false;
										}
									});
									if(sign){
										changeViewSet('default');
										$.each(values,function(i){
											$('input[name="fieldName"][value="'+$.trim(values[i])+'"]').attr("checked",true);
										});
									}else{
										changeViewSet('entering');
										$("#fieldValue").val(conditionValue);
									}
								}
							}
						}else{
							changeViewSet('entering');
							if("valuesSign"==$("#valuesSign").val()){
								$('input[name="fieldValue"][value="'+conditionValue+'"]').attr("checked",true);
							}else{
								$("#fieldValue").val(conditionValue);
							}
						}
					}
				}
			}
		</script>
	</aa:zone>
</div>
</div>
</body>
<script src="${resourcesCtx}/widgets/timepicker/timepicker_<%=com.norteksoft.product.util.ContextUtils.getCurrentLanguage()%>.js" type="text/javascript"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
</html>
