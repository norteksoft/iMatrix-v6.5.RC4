<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/mms-taglibs.jsp"%>
<div id="accordion" >
	<h3><a href="${mmsCtx}/page/createhtml-editor.htm" id="createhtml-manager"><s:text name="mms.formGenerate"></s:text></a></h3>
	<div>
		<div class="demo" id="create_page" style="margin-top: 10px;"></div>
	</div>
</div>


<script type="text/javascript">
	$(function () {
		$("#accordion").accordion({fillSpace:true, change:accordionChange});
	});
	function accordionChange(event,ui){
		var url=ui.newHeader.children("a").attr("href");
		if(url=="${mmsCtx}/page/createhtml-editor.htm"){
			$("#myIFrame").attr("src",ui.newHeader.children("a").attr("href"));
		}
	}
</script>