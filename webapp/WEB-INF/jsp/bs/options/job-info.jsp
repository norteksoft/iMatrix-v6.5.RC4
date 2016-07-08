<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/setting-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>定时设置</title>
	<%@ include file="/common/setting-iframe-meta.jsp"%>
	<script type="text/javascript" src="${resourcesCtx}/widgets/multiselect/jquery.multiselect.min.js"></script>
	<link   type="text/css" rel="stylesheet" href="${resourcesCtx}/widgets/multiselect/jquery.multiselect.css" />
	<script type="text/javascript" src="${resourcesCtx}/js/staff-tree.js"></script>
	
	<script type="text/javascript" src="${settingCtx}/js/bs.js"></script>
	
	<script type="text/javascript">
		//通用ajaxAnywhere提交
		function ajaxSubmit(form, url, zoons, ajaxCallback){
			var formId = "#"+form;
			if(url != ""){
				$(formId).attr("action", url);
			}
			ajaxAnywhere.formName = form;
			ajaxAnywhere.getZonesToReload = function() {
				return zoons;
			};
			ajaxAnywhere.onAfterResponseProcessing = function () {
				if(typeof(ajaxCallback) == "function"){
					ajaxCallback();
				}
			};
			ajaxAnywhere.submitAJAX();
		}
		
		//子表点击事件
		//列表管理/字段信息/格式设置列编辑时的文本框的onclick事件
		//obj:{rowid:id,currentInputId:id_formatSetting}
		function cornClick(obj){
			$('#'+obj.currentInputId).timepicker({
				timeOnlyTitle: '时间'
			});
		}

		//子表下拉框改变事件
		function typeEnumChange(obj){
			//$("#"+obj.rowid+"_dateTime").attr("disabled","disabled");
		}

		

		//修改
		function updateJobInfo(){
			var boxes = jQuery("#jobInfoId").jqGrid("getGridParam",'selarrrow');
			if(boxes==null||boxes==""||boxes.length>1){
				iMatrix.alert(iMatrixMessage["selectOneInfo"]);
			}else{
				ajaxSubmit('defaultForm','${settingCtx}/options/job-info-input.htm?id='+boxes,'groups_main');
			}
			
		}
		
		//删除
		function deleteJobInfo(){
			var boxes = jQuery("#jobInfoId").jqGrid("getGridParam",'selarrrow');
			if(boxes==null||boxes==""){
				iMatrix.alert(iMatrixMessage["selectOneInfo"]);
			}else{
				var canPost = true;
				if(versionType=="online"){
					$.each(boxes, function(i){
						var id = boxes[i];
						var state=jQuery("#jobInfoId").jqGrid("getCell",id,"dataState");
						if(state!= "DRAFT"){
							iMatrix.alert(iMatrixMessage["basicSetting.enableInfo"]);
							canPost = false;
						}
					});
				}
				if(canPost){
					iMatrix.confirm({
						message:iMatrixMessage["deleteInfo"],
						confirmCallback:deleteJobInfoOk,
						parameters:boxes
					});
				}
			}
			
		}
		
		function deleteJobInfoOk(boxes){
			setPageState();
			ajaxSubmit('defaultForm','${settingCtx}/options/job-info-delete.htm?ids='+boxes,'groups_main');
		}
		//查看页面
		function viewJobInfo(ts1,cellval,opts,rwdat,_act){
			var v="<a  href=\"#\" hidefocus=\"true\" onclick=\"viewJobInfoInput("+opts.id+");\">" + ts1 + "</a>";
			return v;
		}

		//查看页面
		function viewJobInfoInput(id){
			ajaxSubmit('defaultForm','${settingCtx}/options/job-info-view.htm?id='+id,'groups_main');
		}

		//弹出页面
		function openPage(url){
			$.colorbox({href:url,iframe:true, innerWidth:700, innerHeight:500,overlayClose:false,title:iMatrixMessage["bs.newTimingSettings"]});
		}
		//修改定时
		function updateJob(url){
			var boxes = jQuery("#jobInfoId").jqGrid("getGridParam",'selarrrow');
			if(boxes==null||boxes==""||boxes.length>1){
				iMatrix.alert(iMatrixMessage["selectOneInfo"]);
			}else{
				$.colorbox({href:url+"&id="+boxes,iframe:true, innerWidth:700, innerHeight:500,overlayClose:false,title:iMatrixMessage["bs.modifyTimingSettings"]});
			}
		}

		function backPage(){
			setPageState();
			ajaxSubmit('defaultForm','${settingCtx}/options/job-info.htm','groups_main');
		}

		//增加定时
		function addCornInfo(url){
			var boxes = jQuery("#jobInfoId").jqGrid("getGridParam",'selarrrow');
			if(boxes==null||boxes==""||boxes.length>1){
				iMatrix.alert(iMatrixMessage["selectOneInfo"]);
			}else{
				$.colorbox({href:url+"&id="+boxes,iframe:true, innerWidth:700, innerHeight:500,overlayClose:false,title:iMatrixMessage["bs.addTiming"]});
			}
			
		}

		//删除定时
		function deleteCornInfo(){
			var boxes = jQuery("#childId").jqGrid("getGridParam",'selarrrow');
			if(boxes==null||boxes==""){
				iMatrix.alert(iMatrixMessage["selectOneInfo"]);
			}else{
				if(versionType=="online"){
					var mainTableId = jQuery("#jobInfoId").jqGrid("getGridParam",'selarrrow');
					var state=jQuery("#jobInfoId").jqGrid("getCell",mainTableId,"dataState");
					if(state!= "DRAFT"){
						iMatrix.alert(iMatrixMessage["basicSetting.enableInfo"]);
						return;
					}
				}
				iMatrix.confirm({
					message:iMatrixMessage["deleteInfo"],
					confirmCallback:deleteCornInfoOk,
					parameters:boxes
				});
			}
			
		}
		function deleteCornInfoOk(boxes){
			setPageState();
			ajaxSubmit('defaultForm','${settingCtx}/options/job-info-deleteCornInfo.htm?ids='+boxes,'groups_main');
		}
		// 设置状态
		function setStateCornInfo(state){
			var boxes = jQuery("#jobInfoId").jqGrid("getGridParam",'selarrrow');
			if(boxes==null||boxes==""){
				iMatrix.alert(iMatrixMessage["selectOneInfo"]);
			}else{
				setPageState();
				ajaxSubmit('defaultForm','${settingCtx}/options/job-info-setState.htm?ids='+boxes+'&dataState='+state,'groups_main');
			}
			
		}
		//obj{rowid:id,currentInputId:rowid_runAsUserName}
		function runAsUserNameClick(obj){
			var zTreeSetting={
					leaf: {
						enable: false
					},
					type: {
						treeType: "MAN_DEPARTMENT_TREE",
						noDeparmentUser:true,
						onlineVisible:false
					},
					data: {
					},
					view: {
						title: iMatrixMessage["user.selectStaff"],
						width: 300,
						height:400,
						url:imatrixRoot,
						showBranch:true
					},
					feedback:{
						enable: true,
		                append:false
					},
					callback: {
						onClose:function(){
							runAsUserNameClickCallback();
						}
					}			
					};
				    popZtree(zTreeSetting);
		}

		function runAsUserNameClickCallback(obj){
			$("#"+obj.rowid+"_runAsUser").attr("value",ztree.getLoginName());
			$("#"+obj.rowid+"_runAsUserId").attr("value",ztree.getId());
		}
	</script>
</head>
<body>
	<div class="ui-layout-center">
	<div class="opt-body">
		<form name="defaultForm" id="defaultForm">
			<input type="hidden" name="systemId" value="${systemId}"/>
		</form>
		<aa:zone name="groups_main">
			<div class="opt-btn">
				<a class="btn" href="#" onclick="openPage('${settingCtx}/options/job-info-timeTaskInput.htm?systemId=${systemId}');"><span><span><s:text name="menuManager.new"></s:text></span></span></a>
				<a class="btn" href="#" onclick="updateJob('${settingCtx}/options/job-info-timeTaskInput.htm?systemId=${systemId}');"><span><span><s:text name="menuManager.update"></s:text></span></span></a>
				<a class="btn" href="#" onclick="deleteJobInfo();"><span><span ><s:text name="menuManager.delete"></s:text></span></span></a>
				<a class="btn" href="#" onclick="addCornInfo('${settingCtx}/options/job-info-input.htm?systemId=${systemId}');"><span><span><s:text name="addTimer"></s:text></span></span></a>
				<a class="btn" href="#" onclick="deleteCornInfo();"><span><span ><s:text name="deleteTimer"></s:text></span></span></a>
				<a class="btn" href="#" onclick="setStateCornInfo('ENABLE');"><span><span ><s:text name="job.enable"></s:text></span></span></a>
				<s:if test='#versionType=="online"'>
					<a class="btn" href="#" onclick="demonstrateOperate();"><span><span ><s:text name="job.disalbe"></s:text></span></span></a>
				</s:if><s:else>
					<a class="btn" href="#" onclick="setStateCornInfo('DISABLE');"><span><span ><s:text name="job.disalbe"></s:text></span></span></a>
				</s:else>
			</div>
			<div id="opt-content" >
				<script type="text/javascript">setTimeout('$("#message").hide("show");',3000);</script>
				<div id="message"><s:actionmessage theme="mytheme" /></div>
				<view:jqGrid url="${settingCtx}/options/job-info.htm?systemId=${systemId}" subGrid="childId" code="BS_JOBINFO" pageName="pages" gridId="jobInfoId"></view:jqGrid>
				<div style="height: 8px;"></div>
				<view:subGrid gridId="childId" url="${settingCtx}/options/job-info-chiledList.htm" code="BS_CORNINFO" pageName="cornInfos"></view:subGrid>
			</div>
		</aa:zone>
	</div>
</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script src="${resourcesCtx}/widgets/timepicker/timepicker_<%=com.norteksoft.product.util.ContextUtils.getCurrentLanguage()%>.js" type="text/javascript"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>