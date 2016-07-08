<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/acs-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>查看条件</title>
	<%@ include file="/common/acs-iframe-meta.jsp"%>
	<script src="${acsCtx}/js/authority.js" type="text/javascript"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			getConditionValue();
		});
		function getConditionValue(){
			var conditionValue="";
			var rows = window.parent.jQuery("#childGridId").jqGrid('getRowData');
			for(var i=0;i<rows.length;i++){
				var row=rows[i];
				var leftBracket=row['leftBracket'];//左括号
				var itemType=row['itemType'];//条件类型
				var operator=row['operator'];//操作符
				var conditionName=row['conditionName'];//条件
				if("\\${common.allPersonnel}"==conditionName){
					conditionName=iMatrixMessage["common.allPersonnel"];
				}else if("\\${common.allDepartment}"==conditionName){
					conditionName=iMatrixMessage["common.allDepartment"];
				}else if("\\${common.allWorkgroup}"==conditionName){
					conditionName=iMatrixMessage["common.allWorkgroup"];
				}
				var rightBracket=row['rightBracket'];//右括号
				var joinType=row['joinType'];//连接符
				if(itemType!='' && operator!='' && conditionName!=''){
					if(leftBracket != ''){
						if(leftBracket=='LEFTSINGLE'){//单层括号
							conditionValue+='(';
						}else if(leftBracket=='LEFTDOUBLE'){//双层括号
							conditionValue+='((';
						}
					}
					if(itemType=='USER'){//人员
						conditionValue+=iMatrixMessage["permission.item.type.user"]+'&nbsp;';
					}else if(itemType=='DEPARTMENT'){//部门
						conditionValue+=iMatrixMessage["permission.item.type.department"]+'&nbsp;';
					}else if(itemType=='ROLE'){//角色
						conditionValue+=iMatrixMessage["permission.item.type.role"]+'&nbsp;';
					}else if(itemType=='WORKGROUP'){//工作组
						conditionValue+=iMatrixMessage["permission.item.type.workgroup"]+'&nbsp;';
					}else if(itemType=='CURRENT_USER_SUPERIOR_DEPARTMENT'){//当前用户上级部门
						conditionValue+=iMatrixMessage["permission.item.type.currentUserSuperiorDepartment"]+'&nbsp;';
					}else if(itemType=='CURRENT_USER_TOP_DEPARTMENT'){//当前用户顶级部门
						conditionValue+=iMatrixMessage["permission.item.type.currentUserTopDepartment"]+'&nbsp;';
					}else if(itemType=='CURRENT_USER_DIRECT_SUPERIOR_ID'){//直属上级
						conditionValue+=iMatrixMessage["permission.item.type.currentUserDirectSuperiorId"]+'&nbsp;';
					}else if(itemType=='CURRENT_USER_DIRECT_SUPERIOR_DEPARTMENT'){//直属上级的部门
						conditionValue+=iMatrixMessage["permission.item.type.currentUserDirectSuperiorDepartment"]+'&nbsp;';
					}else if(itemType=='CURRENT_USER_DIRECT_SUPERIOR_ROLE'){//直属上级的角色
						conditionValue+=iMatrixMessage["permission.item.type.currentUserDirectSuperiorRole"]+'&nbsp;';
					}else if(itemType=='CURRENT_USER_DIRECT_SUPERIOR_WORKGROUP'){//直属上级的工作组
						conditionValue+=iMatrixMessage["permission.item.type.currentUserDirectSuperiorWorkgroup"]+'&nbsp;';
					}else if(itemType=='ALL_USER'){//所有人
						conditionValue+=iMatrixMessage["permission.item.type.all.user"]+'&nbsp;';
					}
					if(operator=='ET'){//等于
						conditionValue+=iMatrixMessage["operator.text.et"]+'&nbsp;';
					}else if(operator=='NET'){//不等于
						conditionValue+=iMatrixMessage["operator.text.et.not"]+'&nbsp;';
					}
					conditionValue+=conditionName+'&nbsp;';
					if(rightBracket != ''){
						if(rightBracket=='RIGHTSINGLE'){//单层括号
							conditionValue+=')';
						}else if(rightBracket=='RIGHTDOUBLE'){//双层括号
							conditionValue+='))';
						}
					}
					if(joinType != '' && i<rows.length-1){
						if(joinType=='AND'){//并且
							conditionValue+='&nbsp;'+iMatrixMessage["condition.operator.and"]+'&nbsp;';
						}else if(joinType=='OR'){//或者
							conditionValue+='&nbsp;'+iMatrixMessage["condition.operator.or"]+'&nbsp;';
						}
					}
					$("#opt-content").html(conditionValue);
				}
			}
		}
	</script>
</head>
<body>
	<div class="ui-layout-center">
		<div class="opt-body">
			<div id="opt-content">
			</div>
		</div>
	</div>
</body>
</html>
