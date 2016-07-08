<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/setting-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<%@ include file="/common/setting-iframe-meta.jsp"%>
	<script type="text/javascript" src="${resourcesCtx}/js/custom.tree.js"> </script>
	<!-- 树 -->
<script type="text/javascript" src="${resourcesCtx}/js/staff-tree.js"></script>
<script type="text/javascript" src="${ctx}/js/item.js"></script>
	
	<title>定时设置</title>
	<script type="text/javascript">
	$(document).ready(function(){
		validateInter();
	});

	//提交
	function submitJobInfo(){
		if($("#code").attr("value")==""){
			$("#interFrom").attr("action",'${settingCtx}/options/internation-save.htm');
			$("#interFrom").submit();
		}else{
			checkCode();
		}
	}

	function validateInter(){
		$("#interFrom").validate({
			submitHandler: function() {
				var cansave=iMatrix.getFormGridDatas("interFrom","interOptionsId");
				if(cansave){
					ajaxSubmit('interFrom','${settingCtx}/options/internation-save.htm','inter_zoon',saveCallback);
				}
			},
			rules: {
				code: "required"
			},
			messages: {
				code: iMatrixMessage["menuManager.required"]
			}
		});
	}

	function saveCallback(){
		parent.backPage();
		parent.$.colorbox.close();
	}
	/* function categoryNameClick(obj){
		custom_ztree({url:webRoot+'/options/internation-select-category.htm',
			onsuccess:function(){closeFun(obj);},
			width:300,
			height:300,
			title:iMatrixMessage["basicSetting.selectLanguage"],
			webRoot:imatrixRoot,
			nodeInfo:['id']
		});
	} */
	function viewNameClick(obj){
		custom_ztree({url:webRoot+'/options/internation-select-category.htm',
			onsuccess:function(){closeFun(obj);},
			width:300,
			height:300,
			title:iMatrixMessage["basicSetting.selectLanguage"],
			webRoot:imatrixRoot,
			nodeInfo:['id']
		});
	}

	function closeFun(obj){
		var name = getSelectNodeTitle();
		var categoryName = name;
		if(name=="中文" || name=="Chinese"){
			categoryName = "\\${bs.languageCategory.zh}";
		}else if(name=="英文" || name=="English"){
			categoryName = "\\${bs.languageCategory.en}";
		}
		$("#"+obj.rowid+"_category").attr("value",getSelectValue("id"));
		$("#"+obj.rowid+"_categoryName").attr("value",categoryName);
		$("#"+obj.currentInputId).attr("value",getSelectNodeTitle());
	}

	function checkCode(){
		$.ajax({
			type : "post",
			dataType : "json",
			data : "interCode="+$("#code").val()+"&id="+$("#id").val()+"&type="+$("#type").val(),
			url : webRoot + "/options/internation-check-code.htm",
			success : function(data) {
				if(data){
				  iMatrix.alert(iMatrixMessage["basicSetting.optionCodeExist"]);
			   }else{
				   $("#interFrom").submit();
			   }
			},
			error : function() {
				iMatrix.alert(iMatrixMessage.serverInfo);
			}
		});
	}
	function countInternationWidthHeight(){
		jQuery("#interOptionsId").jqGrid('setGridHeight',280);
		jQuery("#interOptionsId").jqGrid('setGridWidth',580);
	}
	</script>
</head>
<body onload="countInternationWidthHeight();">
<div class="ui-layout-center">
<div class="opt-body">
	<div class="opt-btn">
		<a class="btn" href="#" onclick="submitJobInfo();"><span><span><s:text name="basicSetting.submit"></s:text></span></span></a>
	</div>
	<div id="opt-content" >
		<aa:zone name="inter_zoon">
			<div id="message" style="display:none;"><s:actionmessage theme="mytheme" /></div>
			<form action="" name="interFrom" id="interFrom" method="post">
				<input type="hidden" name="id" id="id" value="${id}"/>
				<input type="hidden" name="interCode" id="interCode" value="${code}"/>
				<input type="hidden" name="type" id="type" value="${type}"/>
				<table>
					<tbody>
						<tr>
							<td style="width:90px;">
								<s:text name="basicSetting.code"></s:text>：
							</td>
							<td>
								<input type="text" id="code" name="code" value="${code }"/><span class="required">*</span>
							</td>
						</tr>
						<tr>
							<td>
								<s:text name="basicSetting.remark"></s:text>：
							</td>
							<td>
							</td>
						</tr>
						<tr>
							<td></td>
							<td>
								<s:textarea  cols="50" rows="3" id="remark" name="remark"></s:textarea>
							</td>
						</tr>
					</tbody>
				</table>
				<view:formGrid gridId="interOptionsId" code="BS_INTERNATION_OPTION_EDIT" entity="${internation}" attributeName="internationOptions"></view:formGrid>
			</form>
		</aa:zone>
	</div>
</div>
</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script src="${resourcesCtx}/widgets/timepicker/timepicker_<%=com.norteksoft.product.util.ContextUtils.getCurrentLanguage()%>.js" type="text/javascript"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>