<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/mms-taglibs.jsp"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
	<head>
	<title>数据选择控件设定</title>
	<%@ include file="/common/mms-iframe-meta.jsp"%>
	<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
	<script src="${imatrixCtx}/widgets/formeditor/kindeditor.js" type="text/javascript"></script>
	<script src="${imatrixCtx}/widgets/formeditor/lang/zh_CN.js" type="text/javascript"></script>
	<script src="${imatrixCtx}/widgets/formeditor/formeditor.js" type="text/javascript"></script>
	<link href="${imatrixCtx}/widgets/formeditor/formeditor.css" rel="stylesheet" type="text/css" />
	
	<script language="javascript" type="text/javascript" src="${imatrixCtx}/widgets/formeditor/dataControl.js"></script>
	<script type="text/javascript"> 
	function generateHtml(){
		var data_control="";var data_fld_name="";var data_field="";
		var rows=$('#map_tbl').find("tr");
		if(rows.length<=1){
			iMatrix.alert(iMatrixMessage["formManager.addSetValidate"]);
		}else{
			for(var i=1;i<rows.length;i++)
		  	{
				  var tds=$(rows[i]).find("td");
				  data_field+=$(tds[0]).text()+",";
				  data_fld_name+=$(tds[1]).text()+",";
				  data_control+=$(tds[2]).text()+",";
			}
			parent.dataAcquisitionHtml($("#controlType").attr("value")
						,$("#name").attr("value")
						,$("#controlId").attr("value")
						,$("#dataSrc").attr("value")
						,$("#dataSrcName").attr("value")
						,data_fld_name
						,data_field
						,data_control
						,$("#referenceControl").attr("value")
						,$("#queryProperty").attr("value")
			);
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
		<div class="opt-btn">
			<button class="btn" onclick="$('#dataForm').submit();"><span><span><s:text name="menuManager.confirm"></s:text></span></span></button>
			<button class="btn" onclick='parent.$.colorbox.close();'><span><span ><s:text name="cancel"></s:text></span></span></button>
		</div>
		<div id="opt-content">
			<aa:zone name="controlContent">
					<form name="dataForm" id="dataForm" action="${mmsCtx }/form/form-view-text.htm">
						<s:hidden name="id"></s:hidden>
						<s:hidden id="formId" name="formId"></s:hidden>
						<input id="code" type="hidden" name="code" value="${code }"/>
						<input id="version" type="hidden" name="version" value="${version}"/>
						<table class="form-table-without-border">
							<tr>
								<td class="content-title"><s:text name="formManager.controlType"></s:text>：</td>
								<td>
									<s:text name="%{formControl.controlType.code}"></s:text>
									<s:hidden theme="simple" id="controlType" name="formControl.controlType" ></s:hidden>
								</td>
								<td><span id="controlTypeTip"></span></td>	
							</tr>	
							<tr>
								<td class="content-title"><s:text name="formManager.controlName"></s:text>：</td>
								<td>
									<s:textfield theme="simple" id="name" name="formControl.name" cssClass="{required:true,messages: {required:'必填'}}"></s:textfield>
								</td>
								<td>
									<span id="nameTip"></span>
								</td>	
							</tr>
							<tr>
								<td class="content-title"><s:text name="form.inputstandard.field.fieldid"></s:text>：</td>
								<td>
									<s:textfield theme="simple" id="controlId" name="formControl.controlId" cssClass="{required:true,messages: {required:'必填'}}"></s:textfield>
								</td>
								<td><span id="controlIdTip"></span></td>	
							</tr>
						  <tr>
						    <td class="content-title"><s:text name="formManager.dataSource"></s:text>：</td>
						    <td>
						    	<select  id="dataSrc" name="formControl.dataSrc" onchange="getData('DATA_ACQUISITION');">
						    	<option value="0"><s:text name="formManager.choose"></s:text></option>
						    	<s:iterator value="dataTableList">
						    	<option value="${name}" <s:if test="formControl.dataSrc==name">selected="selected"</s:if>>${alias}</option>
						    	</s:iterator>
						    	</select>
						    	<s:hidden theme="simple"  name="formControl.dataSrcName" id="dataSrcName" ></s:hidden>
						    </td>
						  </tr>
						  <tr>
						  	<td class="content-title"><s:text name="formManager.searchFieldName"></s:text>：</td>
						  	<td>
							  	 <select name="formControl.queryProperty" id="queryProperty" style="width: 100px;">
								 	<option value=""><s:text name="formManager.selectField"></s:text></option>
							 	 <s:iterator value="columns" >
							 	 	<s:if test="dbColumnName.contains('dt_')">
										<option value="${dbColumnName }" <s:if test="dbColumnName==formControl.queryProperty">selected="selected"</s:if>>${alias }</option>
							 	 	</s:if>
								</s:iterator>
								</select>
						    </td>
						    <td><s:text name="formManager.sureSingle"></s:text></td>
						  </tr>
						  <tr>
						  	<td class="content-title"><s:text name="formManager.singleControlFieldName"></s:text>：</td>
						  	<td>
						  		<s:textfield theme="simple" id="referenceControl" name="formControl.referenceControl" ></s:textfield>
						    </td>
						  </tr>
						</table> 
						<table style="width: 550px;">  
						 <tr style="width: 550px;">
						  	<td style="width: 60px;"><s:text name="formManager.fieldName"></s:text>：</td>
						  	<td style="width: 100px;">
						  		 <select name="dataField" id="dataField" style="width: 100px;">
							  <option value=""><s:text name="formManager.selectField"></s:text></option>
						 	 <s:iterator value="columns">
						 	 	<s:if test="dbColumnName.contains('dt_')">
									<option value="${dbColumnName }">${alias }</option>
						 	 	</s:if>
							</s:iterator>
							</select>
							</td>
							<td style="width: 108px;"><s:text name="formManager.singleTextControlName"></s:text>：</td><td style="width: 80px;"><input type="text" name="itemTitle" id="itemTitle"/></td><td style="width: 100px;"> <input type="checkbox" name="isQuery" id="isQuery" title='<s:text name="isQueryField"></s:text>' style="display: none;"/>
							<input onclick="add();" type="button"  value="<s:text name="form.inputstandard.field.addrow"></s:text> "/>
						    </td>
						  </tr>
						</table>
						  <table id="map_tbl" class="form-table-border-left" >  
							  <thead >
								  <tr><th><s:text name="formManager.field11"></s:text></th>
								      <th><s:text name="formManager.fieldName"></s:text></th>
								      <th ><s:text name="formManager.singleControlFieldName"></s:text></th>
								      <th ><s:text name="form.inputstandard.field.operate"></s:text></th>
							      </tr>
							  </thead>
							  <tbody>
								  <s:iterator value="dataSelectFields[0]" status="stat">
									  <tr>
										  <td>${dataSelectFields[0][stat.index] }</td>
										  <td>${dataSelectFields[1][stat.index] }</td>
										  <td>${dataSelectFields[2][stat.index] }</td>
										  <td><a href="#" onclick="del(this);"><s:text name="menuManager.delete"></s:text></a></td>
									  </tr>
								  </s:iterator>
							  </tbody>
						</table>
					</form>
					<script type="text/javascript">
					function validateText(){
						$("#dataForm").validate({
							submitHandler: function() {
								generateHtml();
							}
						});
					}
					validateText();
					</script>
			</aa:zone>
		</div>
	</div>
	</div>
	</body>
<script src="${resourcesCtx}/widgets/timepicker/timepicker_<%=com.norteksoft.product.util.ContextUtils.getCurrentLanguage()%>.js" type="text/javascript"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
</html>
