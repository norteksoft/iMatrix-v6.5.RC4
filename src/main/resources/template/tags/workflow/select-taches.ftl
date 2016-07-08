<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html >
	<head>
		<title></title>
		<script type="text/javascript" src="${resourcesCtx}/js/jquery-all-1.0.js"></script>
		
		<link   type="text/css" rel="stylesheet" href="${resourcesCtx}/css/${theme}/jquery-ui-1.8.16.custom.css" id="_style"/>
		
		<script type="text/javascript" src="${resourcesCtx}/js/aa.js"></script>
		<script type="text/javascript" src="${resourcesCtx}/js/public.js" ></script>
		<script type="text/javascript">
			function submitForm(){
				var taskId = $("#taskId").attr("value");
				var code = getTache();
				if(code==""||code==null){
					alert("${selectNextTache}");
				}else{
					$.get("${ctx}/portal/select-activity.action?time="+new Date(), 
							{taskId: taskId,transitionName:code},
							function(taches){
								window.parent.$.colorbox.close();
								window.parent.parent.close();
						});
				}
			}
	
			function getTache(){
				var rds = $("input[name='selectTacheCode']");
				var tacheCode = "";
				for(var i = 0; i < rds.length; i++){
					if($(rds[i]).attr("checked")){
						return $(rds[i]).attr("value");
					}
				}
				return "";
			}
		</script>
	</head>
	
	<body onload="getContentHeight();">
	<div class="ui-layout-center">
		<div class="opt-body">
					<div class="opt-btn">
							<button class='btn' onclick="submitForm();"><span><span>${ftlSubmit}</span></span></button>
					</div>
					<div style="display: none;" id="message"><s:actionmessage theme="mytheme" /></div>
					<div id="opt-content" class="form-bg">
						<form  id="expenseLeaderForm" name="expenseLeaderForm" method="post" action="">
							<input type="hidden"  id="taskId" value="#{taskId }"></input>
							<input type="hidden"   id="tacheCode"  ></input>
							<table class="form-table-border-left">
								<tr>
									<td>${nextTache}</td>
								</tr>
								<#list tacheList?if_exists as item>
									<tr>
										<td>
											<input type="radio" name="selectTacheCode" value="${item[0]}" />${item[1]}	 
										</td>
									</tr>
								</#list>
							</table>
						</form>
					</div>
			</div>
		</div>
	</body>
</html>