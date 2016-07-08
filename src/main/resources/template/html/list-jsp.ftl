<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
	<title></title>
	<%@include file="/common/meta.jsp" %>
	<script src="${resourcesCtx}/widgets/validation/validate-all-1.0.js" type="text/javascript"></script>
	<script src="${resourcesCtx}/widgets/validation/dynamic.validate.js" type="text/javascript"></script>
</head>

<body onclick="$('#sysTableDiv').hide(); $('#styleList').hide();" >
	<%@ include file="/menus/header.jsp" %>
	<div class="ui-layout-center">
		<div class="opt-body">
			<form id="defaultForm" name="defaultForm" method="post"  action=""></form>
			<aa:zone name="mainZone">
				<script type="text/javascript">
				
				<#if  "popup"?contains(inputShowType)  >
					//新建
					function create${fileName}(url){
						init_colorbox(url,"新建${fileName}","","","",colorboxCloseCallback);
					}
					//修改
					function update${fileName}(url){
						var ids = jQuery("#${fileName}GridId").getGridParam('selarrrow');
						if(ids==""){
							alert("请选择一条数据");
						}else if(ids.length > 1){
							alert("只能选择一条数据");
						}else if(ids.length == 1){
							init_colorbox(url+"?id="+ids[0],"修改${fileName}","","","",colorboxCloseCallback);
						}
					}
					
					//查看
					function view${fileName}(url){
						var ids = jQuery("#${fileName}GridId").getGridParam('selarrrow');
						if(ids==""){
							alert("请选择一条数据");
						}else if(ids.length > 1){
							alert("只能选择一条数据");
						}else if(ids.length == 1){
							init_colorbox(url+"?id="+ids[0],"查看${fileName}","","","",null);
						}
						
					}
					function colorboxCloseCallback(){
						jQuery("#${fileName}GridId").trigger("reloadGrid");
					}
				<#else>
					function view${fileName}(url){
						var ids = jQuery("#${fileName}GridId").getGridParam('selarrrow');
						if(ids==""){
							alert("请选择一条数据");
						}else if(ids.length > 1){
							alert("只能选择一条数据");
						}else if(ids.length == 1){
							ajaxSubmit("defaultForm",url+"?id="+ids[0],"main",null);
						}
						
					}
					//新建
					function create${fileName}(url){
						ajaxSubmit("defaultForm",url,"main",create${fileName}Callback);
					}
					function create${fileName}Callback(){
						validate${fileName}();
						getContentHeight();
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
							rules: {
								
							},
							messages: {
								
							}
						});
					}
					
					//修改
					function update${fileName}(url){
						var ids = jQuery("#${fileName}GridId").getGridParam('selarrrow');
						if(ids==""){
							alert("请选择一条数据");
						}else if(ids.length > 1){
							alert("只能选择一条数据");
						}else if(ids.length == 1){
							ajaxSubmit("defaultForm",url+"?id="+ids[0],"main",create${fileName}Callback);
						}
					}
					function submitCallback(){
						showMsg();
						validate${fileName}();
						getContentHeight();
					}
				</#if>
				
					function save${fileName}(url){
						$("#inputForm").attr("action",url);
						$("#inputForm").submit();
					}
					
					//删除
					function delete${fileName}(url){
						var ids = jQuery("#${fileName}GridId").getGridParam('selarrrow');
						if(ids.length<=0){
							alert("请选择数据");
						}else {
							ajaxSubmit('defaultForm', url+'?ids='+ids.join(','), 'main',showMsg);
						}
					}
					
				</script>
				<aa:zone name="main">
					<div class="opt-btn">
						<button  class='btn' onclick="iMatrix.showSearchDIV(this);"><span><span>查询</span></span></button>
						<button class="btn" onclick="create${fileName}('${ctx}/${fileName}/${fileName}-input.htm');"><span><span>新建</span></span></button>
						<button class="btn" onclick="update${fileName}('${ctx}/${fileName}/${fileName}-input.htm');"><span><span>修改</span></span></button>
						<button class="btn" onclick="delete${fileName}('${ctx}/${fileName}/${fileName}-delete.htm');"><span><span >删除</span></span></button>
						<button class="btn" onclick="view${fileName}('${ctx}/${fileName}/${fileName}-view.htm');"><span><span >查看</span></span></button>
					</div>
					<div id="message"><s:actionmessage theme="mytheme" /></div>	
					<div id="opt-content" >
						<form id="contentForm" name="contentForm" method="post"  action="">
							<grid:jqGrid gridId="${fileName}GridId" url="${ctx}/${fileName}/${fileName}-listDatas.htm" submitForm="defaultForm" code="${fileName}" ></grid:jqGrid>
						</form>
					</div>
				</aa:zone>
			</aa:zone>
		</div>
	</div>
	
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
</html>