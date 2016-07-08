<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/mms-taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
	<head>
		<title></title>
		<%@ include file="/common/mms-iframe-meta.jsp"%>
		<script src="${resourcesCtx}/widgets/validation/validate-all-1.0.js" type="text/javascript"></script>
		<script src="${resourcesCtx}/widgets/validation/dynamic.validate.js" type="text/javascript"></script>
		<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
		<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
		<script type="text/javascript">
				
				function save${fileName}(url){
					buttonSign="";
					$("#inputForm").attr("action",url);
					$("#inputForm").submit();
				}
				//验证
				function validate${fileName}(){
					$("#inputForm").validate({
						submitHandler: function() {
							var cansave = iMatrix.getSubTableDatas('inputForm');
							if(cansave){
								$(".opt_btn").find("button.btn").attr("disabled","disabled");
								__parseCustomDateTypeValue();//日期格式化保存处理
								ajaxSubmit('inputForm','','main',submitCallback);
							}
						},
						errorPlacement:function(error,element){
							error.appendTo(element.parent().children("span:contains('*')"));
						},
						rules: {
							
						},
						messages: {
							
						}
					});
				}
				function submitCallback(){
					
				}
			</script>
	</head>
	
	<body onload="getContentHeight();"  onunload="destroyUploadControl();">
		<div class="ui-layout-center">
		<div class="opt-body">
			<div class="opt-btn">
				<button class='btn' onclick="save${fileName}('${ctx}/${fileName}/${entityAttribute}-save.htm')"><span><span>保存</span></span></button>
			
				<#if  "popup"?contains(inputShowType)  >
					<button class='btn' onclick='window.parent.$.colorbox.close();'><span><span>返回</span></span></button>
				<#else>
					<button class='btn' onclick='setPageState();ajaxSubmit("defaultForm","${ctx}/${fileName}/${entityAttribute}-list.htm","main");'><span><span>返回</span></span></button>
				</#if>
			</div>
			<aa:zone name="main">
				<div id="opt-content" class="form-bg">
				<div id="message" style="display:none;"><s:actionmessage theme="mytheme" /></div>
					<form  id="inputForm" name="inputForm" method="post" action="">
						<input type="hidden" name="id" id="id" value="${id }"/>
						
						
						<table rules=all   style="border: #87CEEB 1px solid;" >
							<tbody>
								
									<#if valueList?exists>
										<#list valueList as value0>
												<tr>
													<#list value0 as value00>
														${value00} 
													</#list>
												</tr>
										</#list>
									</#if>
								
							</tbody>
						</table>
						
					</form>
				</div>
				<script type="text/javascript">
				$(document).ready(function(){
					//验证表单字段
					addFormValidate('${fieldPermission}','inputForm');
					iMatrix.autoFillOpinion('${autoFillOpinionInfo}');
					validate${fileName}();
					
					<#list dateList as dateEve>
						$('#${dateEve}').datepicker({
							"dateFormat":'yy-mm-dd',
							changeMonth:true,
							changeYear:true,
							showButtonPanel:"true"
						});
					</#list>
				});
			</script>
			</aa:zone>
		</div>
		</div>
	</body>
</html>