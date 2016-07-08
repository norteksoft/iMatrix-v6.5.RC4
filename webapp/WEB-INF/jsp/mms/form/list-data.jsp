<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/mms-taglibs.jsp"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title><s:text name="mms.formManager"/></title>
	<%@ include file="/common/mms-iframe-meta.jsp"%>
	
	<script src="${resourcesCtx}/widgets/timepicker/timepicker_<%=com.norteksoft.product.util.ContextUtils.getCurrentLanguage()%>.js" type="text/javascript"></script>
	<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
	<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
	
	<link href="${imatrixCtx}/widgets/formeditor/themes/default/default.css" rel="stylesheet" type="text/css" />
	<script src="${imatrixCtx}/widgets/formeditor/kindeditor.js" type="text/javascript"></script>
	<script src="${imatrixCtx}/widgets/formeditor/lang/zh_CN.js" type="text/javascript"></script>
	<script src="${imatrixCtx}/widgets/formeditor/formeditor.js" type="text/javascript"></script>
	<link href="${imatrixCtx}/widgets/formeditor/formeditor.css" rel="stylesheet" type="text/css" />
	
	<script src="${mmsCtx}/js/dataTable.js" type="text/javascript"></script>
	<script src="${mmsCtx}/js/form-view.js" type="text/javascript" charset="UTF-8"></script>
	
	<script type="text/javascript">
		//返回调用,保持页数。
		function goBackForm(form,url,zone,jemesaId){
		//	getPageStateAttr(jemesaId, form);
			ajaxAnyWhereSubmit("backForm", "", "form_main",goBackCallback);
		}
		function goBackCallback(){
			KindEditor.remove("#content");
			parent.showWestAndNorth();
			validateNext();
		}
		function next(){
			$.colorbox.close();
			ajaxAnyWhereSubmit("inputForm", "", "form_main",inputCallBack);
			
		}
		
		function updateFormStyle(){
			var ids = jQuery("#page").getGridParam('selarrrow');
			if(ids==""){
				showMessage("message", "<font color=\"red\">"+iMatrixMessage.selectOneInfo+"</font>");
				return;
			}else if(ids.length > 1){
				showMessage("message", "<font color=\"red\">"+iMatrixMessage.selectOnlyOneInfo+"</font>");
				return;
			}
			var html= '<div id="subPageTwo" align="left" class="opt-body">'
				+ '                        <div class="opt-btn">'
				+ '                            <button class="btn" onclick="saveColumnAmount();"><span><span><s:text name="menuManager.confirm"></s:text></span></span></button>'
				+ '                            <button class="btn" onclick="cancelColumnAmount();"><span><span ><s:text name="menuManager.back"></s:text></span></span></button>'
				+ '                        </div>'
				+ '                        <div class="opt-content">'
				+ '                            <form id="updateStyleForm" name="updateStyleForm" action="" method="post">'
				+ '                            <div style="height: 20px;"></div>'
				+ '                                    <table class="form-table-without-border" style="width: 300px;">'
				+ '                                        <s:hidden id="menuId" name="menuId" theme="simple"></s:hidden>'
				+ '                                        <tr>'
				+ '                                            <td><img src="${mmsCtx}/images/twoColumn.jpg" /></td>'
				+ '                                            <td><img src="${mmsCtx}/images/fourColumn.jpg" /></td>'
				+ '                                            <td><img src="${mmsCtx}/images/sixColumn.jpg" /></td>'
				+ '                                        </tr>    '
				+ '                                        <tr>'
				+ '                                            <td><input name="columnAmount" value="2" type="radio"/><s:text name="template.two.column"></s:text></td>'
				+ '                                            <td><input name="columnAmount" value="4" type="radio"/><s:text name="template.four.column"></s:text></td>'
				+ '                                            <td><input name="columnAmount" value="6" type="radio"/><s:text name="template.six.column"></s:text></td>'
				+ '                                            '
				+ '                                        </tr>    '
				+ '                                    </table>'
				+ '                                </form>    '
				+ '                        </div>    '
				+ '                    </div>';
			$(html).dialog({
				  width: 400,
			      height: 200,
			      modal: true,
			      resizable:false,// 是否可以调整对话框的大小，默认为 true
			      title:iMatrixMessage["formManager.updateTemplate"], 
			      draggable: false,//是否拖拽
			      open: function(){
						 refreshForm();
				  },close: function() {
					  $(this).dialog("destroy").remove();
			      }
		    });
			//$.colorbox({href:"#subPageTwo",inline:true, innerWidth:400, innerHeight:200,overlayClose:false,onLoad:function(){refreshForm();},title:iMatrixMessage["formManager.updateTemplate"]});
		}
		function saveColumnAmount(){
			var selectedLength=$("input[name=columnAmount]:checked").length;
			if(selectedLength==1){
				var ids = jQuery("#page").getGridParam('selarrrow');
				$("#updateStyleForm").attr("action","${mmsCtx }/form/form-view-updateColumnAmount.htm?formId="+ids);
				ajaxAnyWhereSubmit("updateStyleForm", "", "form_main",updateStyleCallBack);
			}else{
				iMatrix.alert(iMatrixMessage.selectInfo);
			}
		}
		function cancelColumnAmount(){
			$("#subPageTwo").dialog("destroy").remove();;
		}
		function updateStyleCallBack(){
			//$.colorbox.close();
			$("#subPageTwo").dialog("destroy").remove();;
			inputCallBack();
		}
		function update(id){
			//隐藏查询框
			HideSearchBox();
			if(id){
				$("#updateForm > input[name='formId']").attr("value",id);
			}else{
				var ids = jQuery("#page").getGridParam('selarrrow');
				if(ids==""){
					showMessage("message", "<font color=\"red\">"+iMatrixMessage.selectOneInfo+"</font>");
					return;
				}else if(ids.length > 1){
					showMessage("message", "<font color=\"red\">"+iMatrixMessage.selectOnlyOneInfo+"</font>");
					return;
				}else if(ids.length == 1){
					$("#updateForm > input[name='formId']").attr("value",ids[0]);
				}
			}
			ajaxAnyWhereSubmit("updateForm", "", "form_main",inputCallBack);
		}
		function copy(){
			var ids = jQuery("#page").getGridParam('selarrrow');
			if(ids==""){
				showMessage("message", "<font color=\"red\">"+iMatrixMessage.selectOneInfo+"</font>");
			}else if(ids.length > 1){
				showMessage("message", "<font color=\"red\">"+iMatrixMessage.selectOnlyOneInfo+"</font>");
			}else if(ids.length == 1){
				var meId = $("#menuId").val();
				$.colorbox({href:"${mmsCtx}/form/form-view-copy.htm?menuId="+meId+"&formId="+ids[0],iframe:true, innerWidth:400, innerHeight:300,overlayClose:false,title:iMatrixMessage["formManager.copyForm"]});
			}
		}
		$(document).ready(function(){
			validateNext();
		});
		function validateNext(){
			$("#inputForm").validate({
				submitHandler: function() {
				   var code = $("#code").val();
					if(validateFormCode(code)){
						next();
					}else{
						iMatrix.alert(iMatrixMessage.formCodeIncludeInfo);
					}
				},
				rules: {
					code:"required",
					name: "required",
					remark:{
						maxlength:500
					}
				},
				messages: {
					code:iMatrixMessage["menuManager.required"],
					name: iMatrixMessage["menuManager.required"],
					remark:{
						maxlength:iMatrixMessage["formManager.validateMaxLength"]
					}
				}
			});
			validateFormViewCode();
		}

		//验证表单编号是否符合规则，表单编号只能包含"_",否则返回false;
		function validateFormCode(code){
			return code.indexOf("-")<0&&code.indexOf("~")<0
			&&code.indexOf("!")<0&&code.indexOf("@")<0
			&&code.indexOf("#")<0&&code.indexOf("$")<0
			&&code.indexOf("%")<0&&code.indexOf("%")<0
			&&code.indexOf("^")<0&&code.indexOf("&")<0
			&&code.indexOf("*")<0&&code.indexOf("(")<0
			&&code.indexOf(")")<0&&code.indexOf("+")<0
			&&code.indexOf("=")<0&&code.indexOf("{")<0
			&&code.indexOf("}")<0&&code.indexOf("|")<0
			&&code.indexOf("\\")<0&&code.indexOf("[")<0
			&&code.indexOf("]")<0&&code.indexOf(";")<0
			&&code.indexOf("'")<0&&code.indexOf(":")<0
			&&code.indexOf("\"")<0&&code.indexOf("<")<0
			&&code.indexOf(">")<0&&code.indexOf("?")<0
			&&code.indexOf(",")<0&&code.indexOf(".")<0
			&&code.indexOf("/")<0&&code.indexOf("￥")<0
			&&code.indexOf("（")<0&&code.indexOf("）")<0
			&&code.indexOf("——")<0&&code.indexOf("【")<0
			&&code.indexOf("】")<0&&code.indexOf("、")<0
			&&code.indexOf("；")<0&&code.indexOf("‘")<0
			&&code.indexOf("：")<0&&code.indexOf("“")<0
			&&code.indexOf("《")<0&&code.indexOf("》")<0
			&&code.indexOf("，")<0&&code.indexOf("。")<0
			&&code.indexOf("？")<0&&code.indexOf(" ")<0
			&&code.indexOf("`")<0&&code.indexOf("·")<0;
		}
		
		function ok(){
			$("#inputForm").submit();
		}
	
		var editor;
		function inputCallBack(){
			//国际化kindEditor
			window.KindEditor.options.langType=iMatrixMessage["kindEditor.options.langType"];
			var hh = $(window).height();
			var ww = $(window).width()-20;
			parent.hideWestAndNorth();
               editor = KindEditor.create('#content', {
               	width:ww,
   				height:hh+50,
   				themeType : 'default',
   				filterMode:false,
   				resizeType: 0 ,
   				items : ['source','undo','redo', 'print', 'cut', 'copy', 'paste','plainpaste', 'wordpaste','|',
   							'justifyleft','justifycenter','justifyright','justifyfull','insertorderedlist', 'insertunorderedlist',
   							'indent', 'outdent', 'subscript','superscript', '|','selectall', '-',
   							'fontname', 'fontsize', '|','forecolor', 'hilitecolor','bold', 'italic', 'underline', 'strikethrough', 'removeformat','|',
   							 'table','hr']
   			
               });
		}
				
		function saveNewVersion(){
			$("#operation").attr("value","addVersion");
			ajaxSave();
		}
		function editorSave(editor){
				$("#html").attr("value",editor.html());
			if($("#inputForm > input[name='formId']").attr("value")==""||$("#formStates").val()=='DRAFT'||$("#formStates").val()==''){
				//直接保存
				ajaxSave();
			}else{
				//弹出保存选项 
				$.colorbox({href:"#saveChoice",inline:true, innerWidth:380, innerHeight:100,overlayClose:false,title:iMatrixMessage["formManager.saveItem"]});
			}
		}

		function ajaxSave(){
			$("html").attr("value",editor.html());
			$("#inputForm").ajaxSubmit(function (data){
				if(data.indexOf("id:")>=0){//表示保存成功
					var results = data.split(";");
					var resultId = results[0].split(":");
					if(resultId[0]=="id"){
						//表单id
						$("#formId").attr("value",resultId[1]);
						//表单版本
						var resultVersion = results[1].split(":");
						$("#version").attr("value",resultVersion[1]);
						successTip(iMatrixMessage.saveSuccess);
					}
				}else{
					errorTip(iMatrixMessage["department.error"] + ":"+data.replace("ms:",""));
				}
			});
		}

		function updateVersion(){
			if(versionType=="online"){
				iMatrix.alert(iMatrixMessage.onlineVesionInfo);
			}else{
				$("html").attr("value",editor.html());
				$("#inputForm").ajaxSubmit(function (data){
					var result = data.split(":");
					if(result[0]=="id"){
						var results = data.split(";");
						var resultId = results[0].split(":");
						if(resultId[0]=="id"){
							//表单id
							$("#formId").attr("value",resultId[1]);
							//表单版本
							var resultVersion = results[1].split(":");
							$("#version").attr("value",resultVersion[1]);
							successTip(iMatrixMessage.saveSuccess);
						}
					}else if(result[0]=="ms"){
						errorTip("错误:"+result[1]);
					}
				});
			}
		}
		
		function deleteForm(){
			var ids = jQuery("#page").getGridParam('selarrrow');
			if(ids==''){
				showMessage("message", "<font color=\"red\">"+iMatrixMessage.selectOneInfo+"</font>");
			}else{
				iMatrix.confirm({
					message:iMatrixMessage.deleteInfo,
					confirmCallback:deleteFormOk,
					parameters:ids
				});
			}
		}
		function deleteFormOk(ids){
			$.each(ids, function(i){
				$("#pageForm").append(createHiddenInput("formViewDeleteIds", ids[i]));
			});
			setPageState();
			ajaxSubmit("pageForm", webRoot+"/form/form-view-delete.htm?menuId=${menuId}", "viewTable", deleteCallBack);
		}
		//验证编号唯一
		function validateFormViewCode(){
			$("#code").blur(function(){
				$.ajax({
					   type: "POST",
					   url: webRoot + "/form/form-view-validateFormCode.htm",
					   data: "soleCode="+$("#code").attr("value")+"&formId="+$("#formId").attr("value"),
					   success: function(data){
					   		if(data=="true"){
					   			iMatrix.alert(iMatrixMessage.thisCode+$("#code").attr("value")+iMatrixMessage.exist);
				   				$("#code").attr("value","");
				   				$("#code").focus();
					   		}
					   }
					}); 
			});
		}
		//改变表单状态
		function changeFormStates(){
			var ids = jQuery("#page").getGridParam('selarrrow');
			if(ids==""){
				showMessage("message", "<font color=\"red\">"+iMatrixMessage.selectOneInfo+"</font>");
			}else if(ids.length >= 1){
				if(versionType=="online"){
					var state=$("#page").jqGrid('getCell',ids,"formState");
					if(state=='ENABLE'||state=='DISABLE'){
						iMatrix.alert(iMatrixMessage.shieldStartOrStopInfo);
					}else{
						$.each(ids, function(i){
							$("#updateForm").append(createHiddenFormInput("formViewIds", ids[i]));
						});
						ajaxSubmit("updateForm", webRoot+"/form/form-view-state.htm", "form_main", changeCallBack);
					}
				}else{
					$.each(ids, function(i){
						$("#updateForm").append(createHiddenFormInput("formViewIds", ids[i]));
					});
					ajaxSubmit("updateForm", webRoot+"/form/form-view-state.htm", "form_main", changeCallBack);
				}
			}
		}
		function createHiddenFormInput(name, value){
			return "<input type=\"hidden\" name=\""+name+"\" value=\""+value+"\"/>";
		}
		function changeCallBack(){
			showMsg("message",5000);
			validateNext();
		}
		function deleteCallBack(){
			showMsg("message",2000);
		}
		function back(){
			ajaxSubmit("defaultForm",webRoot+"/form/list-data.htm", "form_main",validateNext);
		}
		//创建一个隐藏域
		function createHiddenInput(name, value){
			return "<input type=\"hidden\" name=\""+name+"\" value=\""+value+"\"/>";
		}
		function createForm(){
			$.colorbox({href:"#subPage",inline:true, innerWidth:600, innerHeight:300,overlayClose:false,onLoad:function(){refreshForm();},title:iMatrixMessage["formManager.createForm"]});
		}

		function refreshForm(){
             $("#code").attr("value","");
             $("#name").attr("value","");
             $("#remark").attr("value","");
		}
		
		function exportFormView(){
			$("#exportForm").find("input[name='formViewIds']").remove();
			var ids = jQuery("#page").getGridParam('selarrrow');
			if(ids==''){
				iMatrix.confirm({
					message:iMatrixMessage.exportSystemAllFormInfo,
					confirmCallback:exportFormViewOk
				});
			}else{
				$.each(ids, function(i){
					$("#exportForm").append(createHiddenInput("formViewIds", ids[i]));
				});
				$("#exportForm").attr("action",webRoot+"/form/export-form-view.htm");
				$("#exportForm").submit();
			}
		}
		
		function exportFormViewOk(){
			$("#exportForm").attr("action",webRoot+"/form/export-form-view.htm");
			$("#exportForm").submit();
		}
		function importFormView(){
			$.colorbox({href:'${mmsCtx}/form/show-import-form-view.htm',
				iframe:true, innerWidth:400, innerHeight:200,overlayClose:false,title:iMatrixMessage["formManager.importFormViewColorboxTitle"]});
		}

		function listViewList(){
			ajaxSubmit("exportForm",  webRoot+"/form/list-data.htm","viewTable");
		}

		function rebackFormView(){
			var ids = jQuery("#page").getGridParam('selarrrow');
			if(ids==''){
				showMessage("message", "<font color=\"red\">"+iMatrixMessage.selectOneInfo+"</font>");
			}else{
				$.each(ids, function(i){
					$("#pageForm").append(createHiddenInput("formViewRebackIds", ids[i]));
				});
				setPageState();
				ajaxSubmit("pageForm", webRoot+"/form/form-view-reback.htm", "viewTable", deleteCallBack);
			}
		}
		function deleteCompleteForm(){
			var ids = jQuery("#page").getGridParam('selarrrow');
			if(ids==''){
				showMessage("message", "<font color=\"red\">"+iMatrixMessage.selectOneInfo+"</font>");
			}else{
				iMatrix.confirm({
					message:iMatrixMessage.deleteCompleteFormInfo,
					confirmCallback:deleteCompleteFormOk,
					parameters:ids
				});
			}
		}
		function deleteCompleteFormOk(ids){
			$.each(ids, function(i){
				$("#pageForm").append(createHiddenInput("formViewDeleteCompleteIds", ids[i]));
			});
			setPageState();
			ajaxSubmit("pageForm", webRoot+"/form/form-view-delete-complete.htm", "viewTable", deleteCallBack);
		}
		function cancelClick(){
			goBackForm("backForm",webRoot+ "/form/list-data.htm","form_main","page");
			//KindEditor.remove("#content");
		}
	</script>
	<style type="text/css">
	.form-table-without-border td input{
		width:200px;
	}
	</style>
</head>
<body >
<div class="ui-layout-center">
<form id="exportForm" name="exportForm"action="" method="post" >
	<input type="hidden" id="menu_Id"  name="menuId" value="${menuId }"/>
</form>
<form id="defaultForm" name="defaultForm"action="" method="post" >
	<input type="hidden" id="menuId"  name="menuId" value="${menuId }"/>
	<input id="formTypeId"  name="formTypeId" type="hidden"></input>
</form>
<div class="opt-body">
	<aa:zone name="form_main">
		<s:if test="menuId!=null">
			<div class="opt-btn">
				<s:if test='deletedFormViewFolder=="deleted_form_view"'>
				    <s:if test='#versionType=="online"'>
						<button class="btn" onclick="demonstrateOperate();"><span><span ><s:text name="formManager.deleteThoroughly"></s:text> </span></span></button>
					</s:if><s:else>
				        <button class="btn" onclick="deleteCompleteForm();"><span><span ><s:text name="formManager.deleteThoroughly"></s:text> </span></span></button>
				    </s:else>
				    <button class="btn" onclick="rebackFormView();"><span><span ><s:text name="formManager.restore"></s:text></span></span></button>
				</s:if><s:else>
					<button class="btn" onclick="iMatrix.showSearchDIV(this);"><span><span><s:text name="formManager.search"></s:text></span></span></button>
					<button class="btn" onclick='createForm();' id="create"><span><span ><s:text name="menuManager.new"></s:text></span></span></button>
					<button class="btn" onclick="copy();"><span><span ><s:text name="formManager.copy"></s:text></span></span></button>
					<button class="btn" onclick="update();"><span><span ><s:text name="menuManager.update"></s:text> </span></span></button>
					<button class="btn" onclick="changeFormStates();"><span><span ><s:text name="formManager.startOrForbidden"></s:text></span></span></button>
					<button class="btn" onclick="deleteForm();"><span><span ><s:text name="menuManager.delete"></s:text> </span></span></button>
					<button class="btn" onclick="exportFormView();"><span><span ><s:text name="menuManager.export"></s:text></span></span></button>
					<s:if test='#versionType=="online"'>
						<button class="btn" onclick="demonstrateOperate();"><span><span ><s:text name="menuManager.import"></s:text></span></span></button>
					</s:if><s:else>
						<button class="btn" onclick="importFormView();"><span><span ><s:text name="menuManager.import"></s:text></span></span></button>
					</s:else>
					<button class="btn" onclick="updateFormStyle();"><span><span ><s:text name="formManager.updateTemplate"></s:text></span></span></button>
				</s:else>
			</div>
			<aa:zone name="viewTable">
				<div id="opt-content">
					<div id="message" style="display:none;"><s:actionmessage theme="mytheme" /></div>
					<form name="updateForm" id="updateForm" action="${mmsCtx }/form/form-view-next.htm">
						<s:hidden name="formId"></s:hidden>
						<s:hidden name="dataTableId"></s:hidden>
						<input type="hidden" id="menuId"  name="menuId" value="${menuId }"/>
						<input type="hidden" name="states" value="all"/>
					</form>
					<form action="${mmsCtx}/form/list-data.htm" name="pageForm" id="pageForm" method="post">
						<view:jqGrid url="${mmsCtx}/form/list-data.htm?menuId=${menuId }&deletedFormViewFolder=${deletedFormViewFolder}" code="MMS_FORM_VIEW" gridId="page" pageName="page"></view:jqGrid>
					</form>
				</div>
			</aa:zone>
			<div style="display: none;">
					<div id="subPage" align="left" class="opt-body">
						<div class="opt-btn">
							<button class="btn" onclick="ok();"><span><span><s:text name="menuManager.confirm"></s:text></span></span></button>
							<button class="btn" onclick="$.colorbox.close();"><span><span ><s:text name="menuManager.back"></s:text></span></span></button>
						</div>
						
						<div class="opt-content">
							<form id="inputForm" name="inputForm" action="${mmsCtx }/form/form-view-next.htm" method="post">
									<table class="form-table-without-border">
										<s:hidden id="menuId" name="menuId" theme="simple"></s:hidden>
										<tr>
											<td class="content-title"><s:text name="menuManager.code"></s:text>：</td>
											<td> <s:textfield id="code" name="code" theme="simple" maxlength="26"></s:textfield><span class="required">*</span> </td>
											<td> <span id="codeTip"></span> </td>
										</tr>	
										<tr>
											<td class="content-title"><s:text name="menuManager.name"></s:text>：</td>
											<td> <s:textfield id="name" name="name" theme="simple"></s:textfield><span class="required">*</span>  </td>
											<td> <span id="nameTip"></span> </td>
										</tr>
										
										<tr>
											<td class="content-title"><s:text name="menuManager.remark"></s:text>：</td>
											<td colspan="2">  <span id="remarkTip"></span>
												<s:textarea theme="simple"  name="remark" id="remark"  cols="55" rows="5" ></s:textarea>
											</td>
										</tr>
									</table>
								</form>	
						</div>								
					</div>
				</div>
		</s:if>
	</aa:zone>
</div>
</div>
</body>
</html>
