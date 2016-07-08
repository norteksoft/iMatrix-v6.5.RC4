<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/acs-taglibs.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
	<title><s:text name="company.companyManager"/></title>
	 <%@ include file="/common/acs-iframe-meta.jsp"%>
	<script type="text/javascript">
	var split_one = "|*";
	var split_two = "==";
	var split_three = "**";
	$(function () {
		$.ajaxSetup({cache:false});
		//创建动态树 
		var setting = {
			async : {
				enable : true,
				url : getAsyncDeptUrl
			},
			edit : {
				drag : {
					autoExpandTrigger : true
				},
				enable : true,
				showRemoveBtn : false,
				showRenameBtn : false
			},
			data : {
				simpleData : {
					enable : true
				}
			},
			callback : {
				onClick: expandDeptNode
			}
		};
		$.fn.zTree.init($("#_department_tree"), setting);
	});
	function getAsyncDeptUrl(treeId, treeNode) {
		var param = "";
		if (typeof (treeNode) != "undefined" && treeNode != null) {
			param = "currentId=" + treeNode.id;
		} else {
			param = "currentId=INITIALIZED";
		}
		return "${acsCtx}/organization/load-tree-loadDepartment.action?"+param;
	}
	
	function expandDeptNode(event,treeId, treeNode){
		var zTree = $.fn.zTree.getZTreeObj(treeId);
		zTree.expandNode(treeNode,true,false,true,true);//展开树节点
	}
    function selectedOK(){
    	var node=getSelectNodeId();
		if(typeof(node)=='undefined'||node==''
					||node.indexOf('DEPARTMENTS')>=0
					||node.indexOf('USERSBYBRANCH')>=0){
			 $("#msg").html('<span><s:text name="acs.pleaseSelectDepartment"></s:text></span>');
			 $("#msg").children("span").fadeOut(3500);
			return;
		}else{
			var uIds = window.parent.jQuery("#main_table").getGridParam('selarrrow');
			var deptId = node.split(split_two)[0].split(split_one)[1];
			var userBranchIdBefore = window.parent.$("#brcheId").val();
			$.ajax({
				data:{ids:uIds.join(","),deptId:deptId,branchId:userBranchIdBefore},
				type:"post",
				url:"${acsCtx}/organization/user-validateDeletedUserAdd.action",
				beforeSend:function(XMLHttpRequest){},
				success:function(data, textStatus){
					if(data!=""){
						iMatrix.alert(iMatrixMessage["acs.user"] + data + iMatrixMessage["acs.alreadyExists"]);
					}
						parent.deleteUserToDept(deptId);
				},
				complete:function(XMLHttpRequest, textStatus){},
		        error:function(){

				}
			});
			
		}
		parent.$.colorbox.close();
    }
    function selectChangeMainDepartment(){
    	var node=getSelectNodeId();
    	if(typeof(node)=='undefined'||node==''){
    		 $("#msg").html('<span style="color: red;"><s:text name="acs.pleaseSelectDepartment"></s:text></span>');
			 $("#msg").children("span").fadeOut(3500);
			return;
        }
    	rootNode=node.substring(0,node.indexOf(split_one));
		if(node=='allDepartment_allDepartment=所有部门-部门'||node=='company_company=全公司-全公司'||rootNode=='DEPARTMENTS'||rootNode=='USERSBYBRANCH'){
			 $("#msg").html('<span style="color: red;"><s:text name="acs.pleaseSelectDepartment"></s:text></span>');
			 $("#msg").children("span").fadeOut(3500);
			return;
		}
		var deptId = node.split(split_two)[0].split(split_one)[1];
		var subCompanyId=node.split(split_three)[1];
		var uIds = window.parent.jQuery("#main_table").getGridParam('selarrrow');
		var obj={};
		if(subCompanyId=='null'){
			obj={ids:uIds.join(","),deptId:deptId};
		}else{
			obj={ids:uIds.join(","),deptId:deptId,branchId:subCompanyId};
		}
		$.ajax({
			data:obj,
			type:"post",
			url:webRoot+"/organization/user-changeMainDepartment.action",
			beforeSend:function(XMLHttpRequest){},
			success:function(data, textStatus){
				if("ok"==data){
					parent.changeBatchUserMainDepartment(deptId);
					parent.$.colorbox.close();
				}else if("no"==data){
					iMatrix.confirm({
						message:iMatrixMessage["acs.areReplacingThe"],
						confirmCallback:selectChangeMainDepartmentOk,
						parameters:deptId
					});
				}else{
					iMatrix.alert(data);
				}
			},
			complete:function(XMLHttpRequest, textStatus){},
	        error:function(){

			}
		});
    }
    
    function selectChangeMainDepartmentOk(deptId){
    	parent.changeBatchUserMainDepartment(deptId);
		parent.$.colorbox.close();
    }
    function getSelectNodeId(){
    	var zTree = $.fn.zTree.getZTreeObj("_department_tree");
		var selectNodes = zTree.getSelectedNodes();
		var node="";
		if(selectNodes.length>0){
			node = selectNodes[0].id;
		}
		return node;
    }
	</script>
</head>
<body onload="getContentHeight();">
<div class="ui-layout-center">
	<div class="opt-body">
		<div class="opt-btn">
			<s:if test="fromChangeMainDepartment=='true'">
			<button  class='btn' onclick="selectChangeMainDepartment();"><span><span><s:text name="menuManager.confirm"></s:text></span></span></button>
			</s:if>
			<s:else>
			<button  class='btn' onclick="selectedOK();"><span><span><s:text name="menuManager.confirm"></s:text></span></span></button>
			</s:else>
		</div>
		<div id="opt-content">
			<div id='msg' style="padding-top: 4px;"></div>
			<div style="padding: 5px 10px;">
				<ul class="ztree" id="_department_tree"></ul>
			</div>
		</div>
	</div>
</div>
</body>
</html>
