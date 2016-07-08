<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/wf-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>委托管理</title>
</head>
<body>
<div class="ui-layout-center">
<aa:zone name="delegatemainlist">
	<script type="text/javascript">
	
	$(function(){
		getContentHeight();
		$('#beginTime').datepicker({
			showButtonPanel:"true",
			"dateFormat":'yy-mm-dd',
		      changeMonth:true,
		      changeYear:true,
		      onSelect: function(dateText, inst){
				var beginDate=formatStrToDate(dateText);
				var endStr=$('#endTime').attr("value");
				if(endStr!=""&&typeof(endStr)!="undefined"){
			      	var endDate=formatStrToDate(endStr);
				      if(beginDate.getTime()>=endDate.getTime()){
				    	  iMatrix.alert(iMatrixMessage["wf.theEffectiveDate"]);
				    	  $('#beginTime').attr("value","");
				    	  return;
				      }
			      }

				var cDate=new Date();
				var currentDate=formatStrToDate(cDate.getFullYear()+"-"+(cDate.getMonth()+1)+"-"+cDate.getDate());
			    if(beginDate.getTime()<currentDate.getTime()){
					iMatrix.alert(iMatrixMessage["wf.effectiveDateShould"]);
					$('#beginTime').attr("value","");
				}
				
				
			  }
		});
		$('#endTime').datepicker({
			showButtonPanel:"true",
			"dateFormat":'yy-mm-dd',
		      changeMonth:true,
		      changeYear:true,
		      onSelect: function(dateText, inst){
				var endDate=formatStrToDate(dateText);
				
		      	var beginStr=$('#beginTime').attr("value");
			     if(beginStr!=""&&typeof(beginStr)!="undefined"){
				      var beginDate=formatStrToDate(beginStr);
				      if(beginDate.getTime()>=endDate.getTime()){
				    	  iMatrix.alert(iMatrixMessage["wf.closingDateShould"]);
				    	  $('#endTime').attr("value","");
				    	  return;
				      }
			      }
			      
			     var cDate=new Date();
			        currentDate=formatStrToDate(cDate.getFullYear()+"-"+(cDate.getMonth()+1)+"-"+cDate.getDate());
					if(endDate.getTime()<currentDate.getTime()){
						iMatrix.alert(iMatrixMessage["wf.closingDateCurrent"]);
						$('#endTime').attr("value","");
					}
			  }
		});
	});


	function formatStrToDate(dateStr){
		var year=parseInt(dateStr.split("-")[0]);
		var monStr=dateStr.split("-")[1];
		if(monStr.indexOf("0")==0){//如果是一位数应加0，否则getTime()获得的值不一致
			monStr=monStr.substring(1);
		}
      	var month=parseInt(monStr);
      	var dayStr=dateStr.split("-")[2];
      	if(dayStr.indexOf("0")==0){
      		dayStr=dayStr.substring(1);
      	}
     	var day=parseInt(dayStr);
      	return new Date(year,month-1,day);
      	
	}

	</script>
	<div class="opt-btn">
		<s:if test="state==null||state.code=='delegate.main.states.new.creating'">
			<security:authorize ifAnyGranted="wf_delegateMain_save">
				<button class="btn" onclick="validateForEntrust();submitForm();"><span><span><s:text name="form.save"></s:text></span></span></button>
			</security:authorize>
			<security:authorize ifAnyGranted="wf_delegateMain_save">
				<button class="btn" onclick="submitAndStartForm();"><span><span><s:text name="form.saveAndEnable"></s:text></span></span></button>
			</security:authorize>
		</s:if>
		<security:authorize ifAnyGranted="wf_delegateMain">
			<button class="btn" onclick="setPageState();ajaxSubmit('defaultForm','${wfCtx}/engine/delegate-main.htm','delegatemainlist');"><span><span ><s:text name="form.back"></s:text></span></span></button>
		</security:authorize>
	</div>
	<div id="opt-content">
		<div id="message" style="display: none" ><s:actionmessage theme="mytheme" /></div>
		<form action="${wfCtx}/engine/delegate-main-save.htm" name="delegateSaveForm" id="delegateSaveForm" method="post">
			<input type="hidden" id="id" name="id" value="${id}"/> 
			<input type="hidden" id="needStart" name="needStart" value="false"/>
			<table class="Table" >
		 		<tr style="height: 30px;">
					<td style="width: 80px;"><s:text name="wf.engine.delegate.bailee"></s:text>：</td>
					<td  style="width: 380px;">
						<input id="trusteeName" type="text" name="trusteeName" value="${trusteeName}" readonly="readonly"/>
						<input id="trustee" type="hidden" name="trustee" value="${trustee}" />
						<input id="trusteeId" type="hidden" name="trusteeId" value="${trusteeId}" />
						  <a href="#" onclick='selectUser("selectBtn")' title='<s:text name="wf.engine.choose"></s:text>'  class="small-btn" id="selectBtn"><span><span><s:text name="wf.engine.choose"></s:text></span></span></a> 	<span class="required">*</span>
						 <!-- <a href="#" onclick='selectUser("selectBtn")' title="追加"  class="small-btn" id="selectBtn"><span><span>追加</span></span></a> 	<span class="required">*</span>-->
						 <!-- <a href="#" onclick='removeOption()' title="移除"  class="small-btn" id="selectBtn"><span><span>移除</span></span></a> 	<span class="required">*</span> -->
					</td>
					<td> </td>
			  	</tr>
		 		<tr style="height: 30px;">
					<td ><s:text name="wf.engine.delegate.effectiveDate"></s:text>：</td>
					<td  >
						<input value="<s:date name="beginTime"  format="yyyy-MM-dd" />" id="beginTime" name="beginTime" readonly="readonly" /><span class="required">*</span>
					</td>
					<td> </td>
			  	</tr>
		 		<tr style="height: 30px;">
					<td ><s:text name="wf.engine.delegate.closingDate"></s:text>：</td>
					<td  >
						<input value="<s:date name="endTime"  format="yyyy-MM-dd" />" id="endTime" name="endTime" readonly="readonly"/><span class="required">*</span>
					</td>
					<td> </td>
			  	</tr>
		 		<tr style="height: 30px;">
					<td ><s:text name="wf.engine.delegate.commissionedForm"></s:text>：</td>
					<td  >	
					<select name="style" id="styleSelect" onchange="change(this.value);" class="styleRequired">
								<option value="0"><s:text name="wf.engine.delegate.pleaseSelect"></s:text></option>
								<s:if test="style==1">
										<option value="1"  selected="selected"><s:text name="wf.engine.delegate.specifyProcess"></s:text></option>
								</s:if>
								<s:else>
									<option value="1"><s:text name="wf.engine.delegate.specifyProcess"></s:text></option>
								</s:else>
								<s:if test="style==2">
										<option value="2"  selected="selected"><s:text name="wf.engine.delegate.allProcess"></s:text></option>
								</s:if>
								<s:else>
									<option value="2"><s:text name="wf.engine.delegate.allProcess"></s:text></option>
								</s:else>
								<s:if test="style==3">
										<option value="3"  selected="selected"><s:text name="wf.engine.delegate.scopeOfAuthority"></s:text></option>
								</s:if>
								<s:else>
									<option value="3"><s:text name="wf.engine.delegate.scopeOfAuthority"></s:text></option>
								</s:else>
						</select>
						<input id="inputForValidate1" name="inputForValidate1" type="hidden"/><span class="required">*</span>	
					</td>
					<td> </td>
			  	</tr>
		 		<tr id="chooseP" style="height: 30px;display :none;">
					<td ><s:text name="wf.engine.delegate.selectionProcess"></s:text>：</td>
					<td  >
					<select id="processId" name="processId" id="flowIdSelect" onchange="changeFlow(this.value);" class="flowRequired">
								<option value="0"><s:text name="wf.engine.delegate.pleaseSelectProcess"></s:text></option>
								<s:iterator value="workflowDefinitions" var="wdf">
									<s:if test="processDefinitionId==#wdf.processId">
										<option value="${wdf.processId }" selected="selected">${name}(${version })</option>
									</s:if>
									<s:else>
										<option value="${wdf.processId }">${name }(${version })</option>
									</s:else>
								</s:iterator>
							</select>
							
							<input id="inputForValidate2" name="inputForValidate2" type="hidden"/><span class="required">*</span>
					</td>
					<td> </td>
			  	</tr>
			 		<tr id="chooseT" style="height: 30px;display :none;">
						<td style="vertical-align: top;"><s:text name="wf.engine.delegate.selectionlink"></s:text>：</td>
						<td>
						<aa:zone name="taskNamesSelect">
							<a href="#" onclick='selectTache();' title='<s:text name="wf.engine.choose"></s:text>'  class="small-btn" id="selectBtn"><span><span><s:text name="wf.engine.choose"></s:text></span></span></a>
							<a href="#" onclick="$('#activityName').attr('value','');" title='<s:text name="wf.engine.empty"></s:text>' class="small-btn" id="selectBtn"><span><span><s:text name="wf.engine.empty"></s:text></span></span></a>  
							<br/><s:text name="wf.engine.delegate.beforeTheFirstLink"></s:text> <br/><s:text name="wf.engine.delegate.subProcessLink"></s:text><span class="required">*</span><br/>
							<textarea id="activityName" name="activityName" cols="20" rows="5"  readonly="readonly" class="tacheRequired">${activityName}</textarea>
						 	<input id="inputForValidate3" name="inputForValidate3" type="hidden"/>
						</aa:zone>
						</td>
						<td> </td>
				  	</tr>
			  	<tr style="display: none;">
			  		<td ><s:text name="wf.engine.delegate.ownAuthority"></s:text>：</td>
			  	  <td id="rolesId"  >
			  	  </td>
			  	  <td><span class="required">*</span></td>
			  	</tr>
		 		<tr style="height: 30px;">
					<td  ><s:text name="wf.engine.delegate.illustrate"></s:text>：</td>
					<td  >
		 			  <textarea id="contextArea" name="remark" cols="50" rows="5" style="overflow: auto;" >${remark}</textarea>
		 			</td>
		 			<td> </td>
			  	</tr>
			</table>
		</form>
		<form name="defaultForm" id="defaultForm" action="" method="post"></form>
	</div>
</aa:zone>
</div>
</body>
</html>
