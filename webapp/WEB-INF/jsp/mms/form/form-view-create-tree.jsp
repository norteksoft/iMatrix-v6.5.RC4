<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/mms-taglibs.jsp"%>
<%@ page import="org.ajaxanywhere.AAUtils"%> 
<%     
  response.setHeader("Pragma","No-cache");     
  response.setHeader("Cache-Control","no-cache");     
  response.setDateHeader("Expires",   0);     
  %>
 <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
	<title><s:text name="mms.formManager"/></title>
	
	<%@ include file="/common/mms-iframe-meta.jsp"%>	
	<script type="text/javascript" src="${resourcesCtx}/widgets/jstree/jquery.jstree.js"></script>
	
	<script type="text/javascript">
		function okClick(){
			var value="";
			if("${multiple}"=="true"){
				value=getInfo();
				if(value==""){
					iMatrix.alert(iMatrixMessage["dataAuth.selectPerson"]+"!");
					return;
				}else{
					window.parent.addUsers("${treeType}","${multiple}",value,"${resultId}","${hiddenResultId}","${inputType}");
					window.parent.$("input[type=text]").focus();
					//window.parent.$("#${hiddenResultId}").focus();
					window.parent.$.colorbox.close();
				}
			}else if("${multiple}"=="false"){//user_11408=薛乾生-xgs
				if(("${treeType}"=="COMPANY" || "${treeType}".substring(0,"${treeType}".indexOf("_"))=="MAN")){
					var info=getInfo("user");
					if(info==""){
						iMatrix.alert(iMatrixMessage["dataAuth.selectPerson"]+"!");
						return;
					}else{
							window.parent.addUsers("${treeType}","${multiple}",info,"${resultId}","${hiddenResultId}","${inputType}");
							window.parent.$("input[type=text]").focus();
							//window.parent.$("#${hiddenResultId}").focus();
							window.parent.$.colorbox.close();
							return;
					}
				}
				if("${treeType}".substring(0,"${treeType}".indexOf("_"))=="DEPARTMENT"){
					var info=getInfo("department");
					if(info==""){
						iMatrix.alert(iMatrixMessage["acs.pleaseSelectDepartment"]);
						return;
					}else{
						window.parent.addUsers("${treeType}","${multiple}",value,"${resultId}","${hiddenResultId}","${inputType}");
						window.parent.$("input[type=text]").focus();
						//window.parent.$("#${hiddenResultId}").focus();
						window.parent.$.colorbox.close();
						return;
					}
				}
				if("${treeType}".substring(0,"${treeType}".indexOf("_"))=="GROUP"){
					var info=getInfo("workGroup");
					if(info==""){
						iMatrix.alert(iMatrixMessage["common.select.group"]);
						return;
					}else{
							window.parent.addUsers("${treeType}","${multiple}",value,"${resultId}","${hiddenResultId}","${inputType}");
							window.parent.$("input[type=text]").focus();
							//window.parent.$("#${hiddenResultId}").focus();
							window.parent.$.colorbox.close();
							return;
					}
				}
			}
		}
	</script>
</head>
<body>
<div class="ui-layout-center">
<div class="opt-body">
	<div class="opt-btn">
		<button class="btn" onclick="okClick();"><span><span><s:text name="menuManager.confirm"></s:text></span></span></button>
	</div>
	<div id="opt-content">
		<acsTags:tree defaultable="true" treeId="userTree" treeType="${treeType}" multiple="${multiple}"></acsTags:tree>
	</div>
	</div>
</div>
</body>
</html>