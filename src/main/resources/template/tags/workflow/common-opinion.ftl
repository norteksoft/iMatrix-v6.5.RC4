<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
	<head>
		<title></title>
		<meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
	    <meta http-equiv="Cache-Control" content="no-store"/>
	    <meta http-equiv="Pragma" content="no-cache"/>
	    <meta http-equiv="Expires" content="0"/>
	    
		<script type="text/javascript" src="${resourcesCtx}/js/jquery-all-1.0.js"></script>
		<script type="text/javascript" src="${resourcesCtx}/js/form-layout.js"></script>	
		
		<script type="text/javascript" src="${resourcesCtx}/js/aa.js"></script>
		<script type="text/javascript" src="${resourcesCtx}/js/form.js"></script>
		<script type="text/javascript" src="${resourcesCtx}/js/public.js" ></script>
		<script type="text/javascript">
			function _iMatrix_submitCommonOpinion(){
				if("${opinionRequired}"=="true"){
					if($("#opinion").val()==""){
						alert("${inputOpinion}");
						return;
					}
				}
				var controlId="${controlId}";
				if(parent.$("#"+controlId).length>0){
					parent.$("#"+controlId).attr("value",$("#opinion").val());
					parent.${callbackFun}();
					window.parent.$.colorbox.close();
				}else{
					alert("${opinionControl}");
				}
			}
		</script>
			
		</head>
	
	<body>
	<div class="ui-layout-center">
		<div class="opt-body" style="margin-left:10px;">
			<div style="height: 10px;"></div>
			<div class="opt-btn">
				<button class='btn' onclick="_iMatrix_submitCommonOpinion();"><span><span>${ftlSubmit}</span></span></button>
			</div>
			<div style="height: 10px;"></div>
			<div id="opt-content" class="form-bg">
				<form  id="commonOpinionForm" name="commonOpinionForm" method="post" action="">
					<input type="hidden" name="controlId" id="controlId" value="${controlId}"></input>
					<table class="form-table-without-border" >
						<tr>
							<td>
							${opinion}：
							</td>
						</tr>
						<tr>
							<td>
								<textarea name="opinion" id="opinion" rows="8" cols="50" style="width: 600px;overflow: auto;" onkeyup="if(this.value.length>500)this.value=this.value.substring(0,500);"></textarea>
							</td>
						</tr>
					</table>
				</form>
			</div>
		</div>
	</div>
	</body>
</html>