<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/acs-taglibs.jsp"%>

<script type="text/javascript">
	
	$(function () {
		$("#accordion1").accordion({fillSpace:true,change:accordionChange});
	});
	function accordionChange(event,ui){
		var url=ui.newHeader.children("a").attr("href");
		if(url=="${acsCtx}/syssetting/security-set-integration.htm"){//集成参数设置
			url = "${acsCtx}/syssetting/security-set-integration.htm?type=ldap";
	       	initUserTree();
		}
		$("#myIFrame").attr("src",url);
	}
	/**
	 * 初始化用户树
	 */
	function initUserTree(){
		$.ajaxSetup({cache:false});
		tree.initTree({treeId:"company_user",
			data:[ 
		         	{ id:'_security_setldap', pId:0, name:'<s:text name="menu.ldap"/>',iconSkin:"folder"},//LDAP集成
		         	{ id:'_security_setiMatrix', pId:0, name:'<s:text name="menu.imatrix"/>',iconSkin:"folder"},//平台自集成
		         	{ id:'_security_setrtx', pId:0, name:'<s:text name="menu.rtx"/>',iconSkin:"folder"},//RTX集成
		         	{ id:'_security_setother', pId:0, name:'<s:text name="menu.other"/>',iconSkin:"folder"}//其他方式集成
				],
			type:"ztree",
			initiallySelectFirstChild:false,//是否选择第一个子节点
			initiallySelectFirst:true,//是否选择第一个
			callback:{//回调函数
					onClick:selectUserNode
				}});   
	}
	//回调函数
	function selectUserNode(event, treeId, treeNode){ 
		var url="";
		if(treeNode==null||treeNode.id=='_security_setldap'){
			url="${acsCtx}/syssetting/security-set-integration.htm?type=ldap";
		}else if(treeNode.id=='_security_setiMatrix'){
			url="${acsCtx}/syssetting/security-set-integration.htm?type=iMatrix";
		}else if(treeNode.id=='_security_setrtx'){
			url="${acsCtx}/syssetting/security-set-integration.htm?type=rtx";
		}else {
			url="${acsCtx}/syssetting/security-set-integration.htm?type=other";
		}
		$("#myIFrame").attr("src",url);
	}
</script>
<div id="accordion1" class="basic">
	
	<h3><a href="${acsCtx}/syssetting/security-set.action" id="_security_set" ><s:text name="menu.safeParameters"/></a></h3><!-- 安全参数设置 -->
	<div>
		<div  id="security_set_tree" class="demo"></div>
	</div>
	<h3><a href="${acsCtx}/syssetting/security-set-integration.htm" id="_security_setldap" ><s:text name="menu.integratedParameters"/></a></h3><!-- 集成参数设置 -->
		<div>
			<ul class="ztree" id="company_user"></ul>
		</div>
</div>




