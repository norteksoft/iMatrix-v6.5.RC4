<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/acs-taglibs.jsp"%>
<%@ page import="org.ajaxanywhere.AAUtils"%> 

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/acs-iframe-meta.jsp"%>
<title><s:text name="user.addUser"/></title>
	
<link rel="stylesheet" type="text/css" href="${resourcesCtx}/widgets/validation/cmxform.css"/>
<title>Insert title here</title>
<script type="text/javascript">
	var setting = {
			check: {
				enable: true
			},
			data: {
				simpleData: {
					enable: true
				}
			}
		};
		function back(){
			window.parent.$.colorbox.close();
		}
	$(document).ready(function() {
		$( "#tabs" ).tabs();
		
	});
	</script>
</head>
<body>
	<div class="ui-layout-center">
		<div class="opt-body">
			<div class="opt-btn">
				<button class='btn' hidefocus="true" onclick="tijiao()"><span><span>保存</span></span></button>
				<button class='btn' hidefocus="true" onclick="quxiao()"><span><span>取消</span></span></button>
			</div>
			<div id="opt-content" >
				<div id="tabs">
					<ul>
						<li><a href="#tabs-1" >人员</a></li>
						<li><a href="#tabs-2" >部门</a></li>
						<li><a href="#tabs-3" >工作组</a></li>
					</ul>
					<div id="tabs-1">
						<table class="form-table-without-border">
							<tr>
								<td class="content-title"><ztree:ztree
									treeType="MAN_DEPARTMENT_TREE"
									treeId="tree1" 
									userWithoutDeptVisible="true"  
									showBranch="true"
									chkboxType="{'Y':'ps','N':'ps'}"
									chkStyle="checkbox"
									>
						</ztree:ztree></td>
							</tr>
						</table>
					</div>
					<div id="tabs-2">
						<table class="form-table-without-border">
							<tr>
								<td class="content-title"><ztree:ztree
									treeType="DEPARTMENT_TREE"
									treeId="tree2" 
									userWithoutDeptVisible="true"  
									showBranch="true"
									chkboxType="{'Y':'s','N':'s'}"
									chkStyle="checkbox"
									>
						</ztree:ztree></td>
							</tr>
						</table>
					</div>
						<div id="tabs-3">
						<table class="form-table-without-border">
							<tr>
								<td class="content-title"></td>
							</tr>
							
						</table>
					</div>
				</div>
			</div>
		</div>	
	</div>
</body>