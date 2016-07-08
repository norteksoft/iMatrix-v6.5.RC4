<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/acs-taglibs.jsp"%>
<%@ page import="org.ajaxanywhere.AAUtils"%> 
<%@ page import="com.norteksoft.product.util.WebContextUtils"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
	  <%@ include file="/common/acs-iframe-meta.jsp"%>
	  <title><s:text name="company.companyManager"/></title>
</head>
<body onload="getContentHeight();">
  <div class="ui-layout-center">
  	<div class="opt-body">
		<div class="opt-btn" style="margin-bottom: 6px;">
			  <button class='btn' onclick="selectDepartment('_department_tree');"><span><span><s:text name="common.submit"/></span></span></button>
		</div>
		<div id="opt-content">
			<ul class="ztree" id="_department_tree"></ul>
			<script type="text/javascript">
			var split_one = "|*";
			var split_two = "==";
			var split_three = "**";
			//treeType:3 表示部门树，控制父子选中样式问题
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
					check : {
						chkStyle:"checkbox",
						enable : true,
						chkboxType:{"Y": "s", "N": "s"}
					},
					data : {
						simpleData : {
							enable : true
						}
					},
					callback : {
						onClick: expandDeptNode,
						onAsyncSuccess: selectChildrenNode
						
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
				if(treeNode){
					var zTree = $.fn.zTree.getZTreeObj(treeId);
					zTree.checkNode(treeNode, null, true);
			 		if(treeNode.checked){
			 			zTree.expandNode(treeNode,true,false,true,true);
			 		}
				}
			}
			
			function selectChildrenNode(event, treeId, treeNode){
				if(treeNode && treeNode.checked){
					var zTree = $.fn.zTree.getZTreeObj(treeId);
					var nodes = treeNode.children;
					for(var i=0;i<nodes.length;i++){
						zTree.checkNode(nodes[i], null, true);
					}
				}
			}
			
			function selectDepartment(treeId){
				var zTree = $.fn.zTree.getZTreeObj("_department_tree");
				var arr = zTree.getCheckedNodes();
				
				var mainDepartmentName = window.parent.$("#mainDepartmentName").val();
				var mainDepartSubCompanyId = window.parent.$("#departSubCompanyId").val();
				var mainDepartmentId = window.parent.$("#oneDid").val();
				var userLoginName = window.parent.$("#loginName").val();
				var userId = window.parent.$("#uid").val();
					
				if(arr.length <= 0){
					 //请选择部门
					 iMatrix.alert('<s:text name="role.selectDepartment"/>！');
					return;
				}
				var departId="";
				var departName="";
				var departSubCompanyId="";
				var flag=false;
				for(var i=0; i<arr.length; i++){ //USERSBYDEPARTMENT-3978=火箭总师办
					var nodeId = arr[i].id;
					var type = nodeId.split(split_one)[0]; 
					if(type=="USERSBYDEPARTMENT"){
						var name = nodeId.split(split_one)[1].split(split_two)[1].split(split_three)[0];
						var id = nodeId.split(split_one)[1].split(split_two)[0];
						if(mainDepartmentId!=id){
							departId += nodeId.split(split_one)[1].split(split_two)[0]+"=";
							departSubCompanyId += nodeId.split(split_three)[1]+",";
						    departName+=name+",";
						}else{
							flag=true;
	                        //已被选为正职部门
	                        iMatrix.alert(mainDepartmentName+"<s:text name='user.hasSelectDep'/>!");
						}
					}
				}
				var departIds=departId.substring(0,departId.length-1);
				var departNames=departName.substring(0,departName.length-1);
				if(departIds==''&&!flag){
					//只能选择部门，请选择部门
					iMatrix.alert('<s:text name="user.onlySelectDep"/>！');
					return;
				}else if(departIds==''&&flag){
                    return; 
				}
				departSubCompanyId = departSubCompanyId.substring(0,departSubCompanyId.length-1);
				if(!isInSameBranch(mainDepartSubCompanyId,departSubCompanyId)){
                    //您选择的兼职部门和正职部门不属同一分支机构，请重新选择
                    iMatrix.alert("<s:text name='user.parttimeDepNotInOneBranch'/>！");
					return;
				}else if(mainDepartSubCompanyId==''&&departSubCompanyId!=''&&!isInSameBranchWhenOnlySub(departSubCompanyId)){
					 //您选择的兼职部门不属同一分支机构，请重新选择
					 iMatrix.alert("<s:text name='user.parttime2DepNotInOneBranch'/>！");
					 return;
				}else{
					$.ajax( {
	    				data : {
						  userLoginName : userLoginName,chooseDepartmentIds:departIds,uusId:userId
	    				},
	    				type : "post",
	    				url : "${acsCtx}/organization/user-validateLoginNameRepeatByDepartIds.action",
	    				beforeSend : function(XMLHttpRequest) {
	    				},
	    				success : function(data, textStatus) {
		    				if(data=='true'){
		    				    window.parent.$("#dids").attr("value",departIds);
		    				    window.parent.$("#departmentName").attr("value",departNames);
		    				    window.parent.$("#secondDepartSubCompanyId").attr("value",departSubCompanyId);
		    				    window.parent.$.colorbox.close();
		    				}else{
                                //所在的分支机构中存在同名用户，请重新选择
                                iMatrix.alert("<s:text name='permission.item.type.department'/>："+data+"<s:text name='user.hasRepeatUsername'/>！");
			    			}
	    				},
	    				error : function(XMLHttpRequest, textStatus) {
	    					iMatrix.alert(textStatus);
	    				}
	    			});
				
				}
				
			}
			//验证兼职是否在同一分支机构(不选择正职部门时)
			//12,null,22,null....
			function isInSameBranchWhenOnlySub(departSubCompanyId){
				var strArr = departSubCompanyId.split(",");
				var first = strArr[0];
				for(i=0;i<strArr.length;i++){
                    if(first!=strArr[i]){
                      return false;
                    }
				}
                return true;
			}
			
			//验证兼职和主职部门是否在同一分支机构
			function isInSameBranch(mainSubId,subIdStr){
				if(mainSubId==''){
                    return true;
				}else{
					if(mainSubId=='null'&&isUserIdNull(subIdStr)){
	                    return true;
					}else if(mainSubId!='null'&&!isUserIdNull(subIdStr)){
						var strArr = subIdStr.split(",");
						for(i=0;i<strArr.length;i++){
                            if(mainSubId!=strArr[i]){
                              return false;
                            }
						}
						return true;
					}else if(mainSubId!='null'&&isUserIdNull(subIdStr)){
	                    return false;
					}else if(mainSubId=='null'&&!isUserIdNull(subIdStr)){
	                    return false;
					}
				}
			}
			function isUserIdNull(ids){
				 var idStr = ids.split(",");
				 var result = true;
				  for(var i=0;i<idStr.length;i++){
				      if(idStr[i]!='null'){
				        result = false;
				        break;
				      }
				  }
				  return result;
		    }
			</script>
			</div>
		</div>
	</div>
</body>
</html>
