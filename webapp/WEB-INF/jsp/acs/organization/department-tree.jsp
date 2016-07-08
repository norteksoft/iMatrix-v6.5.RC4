<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/acs-taglibs.jsp"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<%@ include file="/common/acs-iframe-meta.jsp"%>
	<title><s:text name="company.companyManager"/></title>
</head>
<body onload="getContentHeight();">
<div class="ui-layout-center">
	<div class="opt-body">
		<div id="opt-content">
			<ul class="ztree" id="_department_tree"></ul>
		</div>
	</div>
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
			onClick: selectDepartment
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

function getSelectNodeId(){
	var zTree = $.fn.zTree.getZTreeObj("_department_tree");
	var selectNodes = zTree.getSelectedNodes();
	var node="";
	if(selectNodes.length>0){
		node = selectNodes[0].id;
	}
	return node;
}
function selectDepartment(){
	var currentDeptId = parent.$("#id").attr("value");
	var isBranch = parent.$("#branchFlag").attr("value");
	node = getSelectNodeId();
	var currentDeptSubCompanyId = parent.$("#subCompanyId").attr("value");
	if(node!='undefined'&&node!=''&&(node.indexOf('USERSBYDEPARTMENT')>=0||node.indexOf('USERSBYBRANCH')>=0)){
		var deptId = node.split(split_one)[1].split(split_two)[0];
		var type = node.split(split_one)[0];
		var deptSubCompanyId = node.split(split_three)[1];
		if(currentDeptId==deptId){
			iMatrix.alert(iMatrixMessage["acs.theFatherCannot"]);
			return;
		}
		if(type=='USERSBYDEPARTMENT'||type=='USERSBYBRANCH'){
			if(isBranch=="false"){
	            if(!isInSameBranch(currentDeptId,currentDeptSubCompanyId,deptSubCompanyId)){
	               iMatrix.alert(iMatrixMessage["acs.canNotCross"]);
	               return;
	            }
			}
		}
		
		$.ajax({
			data:{currentDeptId: currentDeptId,deptId: deptId},
			type:"post",
			url: "department-isSubDepartment.action",
			beforeSend:function(XMLHttpRequest){},
			success:function(data, textStatus){
				if(data=="false"){
					var name = node.split(split_two)[1].split(split_three)[0];
					parent.setParentDeptInfo(deptId, name);
					window.parent.$.colorbox.close();
				}else if(data=="no"){
					iMatrix.alert(iMatrixMessage["acs.pleaseDeleteThe"]);
				}else{
					iMatrix.alert(iMatrixMessage["acs.cannotSetThe"]);
				}
			},
			complete:function(XMLHttpRequest, textStatus){},
	        error:function(){

			}
		});
	}else if(node.indexOf('DEPARTMENTS')>=0){//表示公司名称，树的根节点
		if(currentDeptId!=''){//修改
			if(isBranch=="true"){//如果是分支机构
				var name = node.split(split_two)[1];
				parent.setParentDeptName(name);
				window.parent.$.colorbox.close();
			}else{
				if(currentDeptSubCompanyId!=''){//分支机构下的部门
	               iMatrix.alert(iMatrixMessage["acs.youHaveCross"]);
	               return;
				}else{//如果是公司下的部门
					var name = node.split(split_two)[1];
					parent.setParentDeptName(name);
					window.parent.$.colorbox.close();
	
				}
			}
		}else{//新建
			var name = node.split(split_two)[1];
			parent.setParentDeptName(deptId, name);
			window.parent.$.colorbox.close();
		}
	}else{
		iMatrix.alert(iMatrixMessage["acs.pleaseSelectDepartment"]);
	}
}

function isInSameBranch(currentDeptId,currentDeptSubCompanyId,deptSubCompanyId){
  if(currentDeptId!=''){//修改
	  if(currentDeptSubCompanyId==''&&deptSubCompanyId!='null'){
            return false;
		}else if(currentDeptSubCompanyId!=''&&deptSubCompanyId=='null'){
			return false;
		}else if(currentDeptSubCompanyId!=''&&deptSubCompanyId!='null'){
			if(currentDeptSubCompanyId==deptSubCompanyId){
               return true;
			}else{
               return false;
			}
		}else{
              return true;
		} 
  }else{
	    return true;
  }
	
}
</script>	
</div>
</body>
</html>
