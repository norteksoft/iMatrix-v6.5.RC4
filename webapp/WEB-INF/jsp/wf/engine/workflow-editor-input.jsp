<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/wf-taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
<title>流程管理</title>
</head>
<body >
<div id="layoutCenterId" >
<aa:zone name="wfd_main">

	<form id="wfdForm" name="wfdForm">
		<input type="hidden" name="type" value="${type }"/>
		<input id="system_id" name="sysId" type="hidden" value="${sysId }"/>
		<input id="vertion_type" name="vertionType" type="hidden" value="${vertionType }"/>
	</form>

	<div id="wfeditorDiv" class="opt-body">
	
		<div class="opt-btn">
		<s:if test="option=='view'">
			<div class="opt-btn">
				<button  class='btn' onclick="setPageState();goBackWfd('wfdForm','${wfCtx}/engine/workflow-definition-data.htm','wfd_main','wfdPage');" hidefocus="true"><span><span>返回</span></span></button>
			</div>
		</s:if>
		</div>
		
		<div id="opt-content" style="padding: 0;" ></div>
		<textarea id="xmlContent" name="xml" style="display: hidden;">${xml }</textarea>
	</div>
	
	<script type="text/javascript"> 
    var obj={
    		resourceRoot:resourceRoot+"/widgets",//静态资源
    		iMatrixRoot:imatrixRoot,//iMatrix平台的
    		toFlexPageFunctionName:"createWorkflowDefinition",//iMatrix平台的
    		returnFunctionName:"returnWorkflowDefinitionList"//返回按钮所调用的方法名（需要用户自己来写）
    	};
    $(function(){
    	 if("${option}"=="view"){
         	obj.workflowDefinitionId=parseInt("${wfdId}");
         	obj.pageSign="view";
         }
    	 if("${option}"=="update"){
         	obj.workflowDefinitionId=parseInt("${wfdId}");
         }
    	 if("${option}"=="add"&&"${wfdId}"!=undefined && "${wfdId}"!=""){
         	obj.workflowDefinitionId=parseInt("${wfdId}");
         }
    	 if($('#xmlContent').text() != ''){
         	obj.xml = $('#xmlContent').text();
    	 }
	     $("#wfeditorDiv").wfeditor(obj);
	     contentResize();
   	});
    </script>
    
    
			
</aa:zone>
</div>
</body>
</html>
