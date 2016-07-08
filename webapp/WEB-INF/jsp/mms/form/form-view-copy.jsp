<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/mms-taglibs.jsp"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>表单复制</title>
	<%@ include file="/common/mms-iframe-meta.jsp"%>
	

	<script src="${imatrixCtx}/widgets/formeditor/kindeditor.js" type="text/javascript"></script>
	<script src="${imatrixCtx}/widgets/formeditor/lang/zh_CN.js" type="text/javascript"></script>
	<script src="${imatrixCtx}/widgets/formeditor/formeditor.js" type="text/javascript"></script>
	<link href="${imatrixCtx}/widgets/formeditor/formeditor.css" rel="stylesheet" type="text/css" />
	
	<script src="${mmsCtx}/js/form-view.js" type="text/javascript" charset="UTF-8"></script>		
	<script type="text/javascript">
	$(document).ready(function(){
		validateCopy();
	});
	function validateCopy(){
		$("#copyForm").validate({
			submitHandler: function() {
				copySub();
			},
			rules: {
				code:"required",
				name: "required"
			},
			messages: {
				code:iMatrixMessage["menuManager.required"],
				name: iMatrixMessage["menuManager.required"]
			}
		});
		validateCopyFormViewCode();
	}
	function okCopy(){
		$("#copyForm").submit();
	}
	function copySub(){
		ajaxAnyWhereSubmit("copyForm", "", "form_main",backFunction);
	}
	function backFunction(){
		parent.$.colorbox.close();
		window.parent.back();
	}
	//验证编号唯一
	function validateCopyFormViewCode(){
		$("#codeCopy").blur(function(){
			$.ajax({
				   type: "POST",
				   url: "${mmsCtx }/form/form-view-validateFormCode.htm",
				   data: "soleCode="+$("#codeCopy").attr("value"),
				   success: function(data){
				   		if(data=="true"){
				   			iMatrix.alert(iMatrixMessage.code+$("#codeCopy").attr("value")+iMatrixMessage.exist);
			   				$("#codeCopy").attr("value","");
			   				$("#codeCopy").focus();
				   		}
				   }
				}); 
		});
	}
	</script>
</head>
<body style="padding: 5px;">
<div class="ui-layout-center">
<aa:zone name="form_main">
	<div class="opt-btn">
		<button class="btn" onclick="okCopy();"><span><span><s:text name="menuManager.confirm"></s:text></span></span></button>
		<button class="btn" onclick='parent.$.colorbox.close();' id="create"><span><span ><s:text name="menuManager.back"></s:text></span></span></button>
	</div>
	<form id="copyForm" name="copyForm" action="${mmsCtx }/form/form-view-savecopy.htm" method="post">
				<input type="hidden" id="menee_id"  name="menuId" value="${menuId }"/>
				<input type="hidden" id="formViewId" name="formId" value="${formView.id}" ></input> 
			<table class="form-table-without-border">
				<tr>
					<td class="content-title"><s:text name="menuManager.code"></s:text> ：</td>
					<td> <input id="codeCopy" name="code" maxlength="100"></input><span class="required">*</span> </td>
					<td> <span id="codeCopyTip"></span> </td>
				</tr>	
				<tr>
					<td class="content-title"><s:text name="menuManager.name"></s:text>：</td>
					<td> <input id="nameCopy" name="name"  maxlength="100"></input><span class="required">*</span> </td>
					<td> <span id="nameCopyTip"></span> </td>
				</tr>
			</table>
		</form>		
</aa:zone>
</div>
</body>
<script src="${resourcesCtx}/widgets/timepicker/timepicker_<%=com.norteksoft.product.util.ContextUtils.getCurrentLanguage()%>.js" type="text/javascript"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>
