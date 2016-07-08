<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/wf-taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
	<title>选择办理人</title>
	<%@ include file="/common/wf-colorbox-meta.jsp"%>
	
	<script src="${wfCtx }/js/wf.js" type="text/javascript"></script>
	<script>
	$(function () {
		userTree();
	});
	function getTreeUrl(treeId, treeNode) {
		var actionUrl = "${wfCtx}/engine/task-delTransactorTree.htm";
		var param = "";
		if(typeof(treeNode)!="undefined"&&treeNode!=null){
			param = "currentId="+treeNode.id;
        }else{
        	var param = "currentId=0";
        }
        param=param+"&workflowId=${workflowId}";
		return actionUrl+"?" + param;
	}
	function userTree(){  
		$.ajaxSetup({cache:false});
		tree.initTree({treeId:"userTree",
			url:getTreeUrl,
			type:"ztree",
			multiple:true,
			callback:{
					//onClick:selectNode
				}});
	}


	function getUserJsonValue(arr){
		var info="[";
     	for(var i=0; i<arr.length; i++){
	     	var type=arr[i].substring(0,arr[i].lastIndexOf("_"));
	     	if(type!="company"){
			  info+="{type:"+"\"user\""
			     +",loginName:"+"\""+arr[i].substring(arr[i].indexOf("-")+1,arr[i].indexOf("~"))+"\""
			     +",transactorId:"+"\""+arr[i].substring(arr[i].indexOf("~")+1,arr[i].indexOf("="))+"\""
			     +",name:"+"\""+arr[i].substring(arr[i].indexOf("_")+1,arr[i].indexOf("-"))+"\""
			     +",taskId:"+"\""+arr[i].substring(arr[i].indexOf("=")+1,arr[i].length)+"\""+"},";
			}
	    }
	    if(info.indexOf(",")>=0){
	    	info=info.substring(0,info.length-1);
	    }
       info+="]";
       return info;
	}

	function getSelectUsers(){
    	var lists = tree.getSelectNodes();
		var v="" ;
		for(var i=0; i<lists.length; i++){
			v+=lists[i].id;
			if(i!=lists.length-1)
				v+=";";
		}
		if(v!=""){
			var arr=v.split(";");
			return arr;
		}else{
			return "";
		}
	}
	

	//用户树多选 ；
	function OK(){
		var info=getUserJsonValue(getSelectUsers());
		var arr=eval(info);
		if(info!=""&&info!="[]"){
			if(arr.length=="${transactorNum}"){
				iMatrix.alert(iMatrixMessage["wf.text.CannottGetRid"]);
			}else{
				$("#transactorForm").append('<input type="hidden" name="workflowId" value="${workflowId}"/>');
				for(var i=0; i<arr.length; i++){
					if(arr[i].transactorId==""){
						$("#transactorForm").append('<input type="hidden" name="transactorLoginNames" value="'+arr[i].loginName+"-="+arr[i].taskId+'"/>');
					}else{
						$("#transactorForm").append('<input type="hidden" name="transactorIdStrs" value="'+arr[i].transactorId+"-="+arr[i].taskId+'"/>');
					}
				}
				ajaxSubmit('transactorForm','${wfCtx}/engine/task-delTransactorSave.htm','wf_task',postSuccess);
			}
		}else if(arr.length==0){
			window.parent.$.colorbox.close();
		}
	}
	function postSuccess(){
		window.parent.$.colorbox.close();
	}
	function closeBtn(){
		window.parent.$.colorbox.close();
	}
	</script>
								
</head>
<body onload="getContentHeight();">
	<div class="ui-layout-center">
		<div class="opt-body">
		<form action="" id="pageForm" method="post"></form>
		<aa:zone name="wf_task">
			<s:if test="hasActivitySubProcess">
				<div class="opt-btn">
					<button class='btn' onclick="closeBtn();" hidefocus="true"><span><span><s:text name="wf.button.close"></s:text></span></span></button>
				</div>
				<div id="opt-content">
					<font color="red"><s:text name="wf.text.theCurrentTaskReduced"></s:text></font>
				</div>
			</s:if>
			<s:elseif test="transactorNum==0">
				<div class="opt-btn">
					<button class='btn' onclick="closeBtn();" hidefocus="true"><span><span><s:text name="wf.button.close"></s:text></span></span></button>
				</div>
				<div id="opt-content">
					<div style="margin-top: 5px ;margin-left: 5px;">
						<font color="red"><s:text name="wf.text.theCurrentProcess"></s:text></font>
					</div>
					
				</div>
			</s:elseif>
			<s:elseif test="transactorNum==1">
				<div class="opt-btn">
					<button class='btn' onclick="closeBtn();" hidefocus="true"><span><span><s:text name="wf.button.close"></s:text></span></span></button>
				</div>
				<div id="opt-content">
					<div style="margin-top: 5px ;margin-left: 5px;">
						<font color="red"><s:text name="wf.text.theCurrentTaskIs"></s:text></font><br /><br />
						<s:text name="wf.text.transactorIs"></s:text>：${transactor }
					</div>
				</div>
			</s:elseif>
			<s:else>
				<div style="width:auto; padding: 0; margin: 0;text-align: left;" id="leafTree">
					<div class="opt-btn">
						<button class='btn' onclick="OK();" hidefocus="true"><span><span><s:text name="menuManager.confirm"></s:text></span></span></button>
					</div>
					<div id="opt-content">
						<div id="userTree" class="ztree"></div>
					</div>
				</div>
			</s:else>
			<form id="transactorForm" action="post" name="transactorForm">
			</form>
			</aa:zone>
		</div>
	</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>
