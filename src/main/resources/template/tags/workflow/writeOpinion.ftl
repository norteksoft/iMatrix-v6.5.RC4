<table>
<#if options?if_exists != ''&&position == "top">
<tr><td colspan='4'>${options?if_exists}</td></tr>
</#if>
<tr>
<td colspan='4'>
<input id='_opinion_control' type='hidden' name='_opinion_control'/>
<textarea id="${controlId}~~opinion" name="_iMatrix_opinion_${controlId}" onblur="_iMatrix_promptlyOpinionSignAndDate('${controlId}',this,'${userName}');" style="width:${width}px;height:${height}px;">${opinion?if_exists}</textarea>
<script type="text/javascript" >
	$().ready(function() {
		var opiCons = $("input[name='_opinion_control']");
		if(opiCons.length>1){
			for(var i=1;i<opiCons.length;i++){
				$(opiCons[i]).remove();
			}			
		}
		if($("#_opinion_control").attr("id")=="_opinion_control"){//表示文本框已存在
			var controlExist = false;
			var opinionControls = $("#_opinion_control").val();
			var controlId = "${controlId}";
			if(opinionControls!=""&&typeof(opinionControls)!='undefined'){
				for(var i=0;i<opinionControls.length;i++){
					<#if controlType?if_exists != ''>
						if(opinionControls[i]=='${controlId}@@${controlType}'){
							controlExist = true;
							break;
						}
					<#else>
						if(opinionControls[i]=='${controlId}'){
							controlExist = true;
							break;
						}
					</#if>
				}
				if(!controlExist){
				<#if controlType?if_exists != ''>
					controlId = '${controlId}@@${controlType}';
				</#if>
					opinionControls=opinionControls+","+controlId;
				}
			}else{
				<#if controlType?if_exists != ''>
					controlId = '${controlId}@@${controlType}';
				</#if>
				opinionControls=controlId;
			}
			$("#_opinion_control").attr("value",opinionControls);
		}
	});

</script>
</td>
</tr>
<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
<td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
<td align='right'>${signRename}：<input id='_iMatrix_opinionSign_${controlId}' name='_iMatrix_opinionSign_${controlId}' value='${opinionSign?if_exists}' readonly='readonly' style='background:none;border:none;width:80px;'/></td>
<td align='right'>${dateRename}：<input id='_iMatrix_opinionDate_${controlId}' name='_iMatrix_opinionDate_${controlId}' value='${opinionDate?if_exists}' readonly='readonly' style='background:none;border:none;'/></td>
</tr>
<#if options?if_exists != ''&&position == "bottom">
<tr><td colspan='4'>${options?if_exists}</td></tr>
</#if>
</table>