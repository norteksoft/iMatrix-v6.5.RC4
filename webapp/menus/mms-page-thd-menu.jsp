<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/mms-taglibs.jsp"%>

<div id="accordion" >
	<h3><a href="module-page-list.htm" id="page_manage"><s:text name="mms.pageManager"></s:text></a></h3>
	<div>
		<ul class="ztree" id="page_manage_content"></ul>
	</div>
</div>

<script type="text/javascript">
	$(function () {
		$("#accordion").accordion({fillSpace:true, change:accordionChange});
	});
	function accordionChange(event,ui){
		$("#myIFrame").attr("src",ui.newHeader.children("a").attr("href"));
	}
</script>