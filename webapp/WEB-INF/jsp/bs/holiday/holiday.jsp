<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/setting-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<%@ include file="/common/setting-iframe-meta.jsp"%>
	<title>节假日设置</title>
	
	<script language="javascript" type="text/javascript" src="${imatrixCtx}/widgets/calendar/WdatePicker.js"></script>
	<script type="text/javascript" src="${resourcesCtx}/widgets/multiselect/jquery.multiselect.min.js"></script>
	<link   type="text/css" rel="stylesheet" href="${resourcesCtx}/widgets/multiselect/jquery.multiselect.css" />
	
	<script type="text/javascript">
		
		function createHoliday(){
			ajaxSubmit('holidayForm', '${settingCtx}/holiday/holiday-input.htm', 'holiday_zone');
		}
		function saveHoliday(){
			ajaxSubmit('holidayInputForm', '${settingCtx}/holiday/holiday-save.htm?branchId='+$("#branchId").val(), 'holiday_zone');
		}
		function returnHoliday(){
			ajaxSubmit('holidayForm', '${settingCtx}/holiday/holiday.htm', 'holiday_zone');
		}
		function ajaxSubmit(form, url, zoons, ajaxCallback){
			var formId = "#"+form;
			if(url != ""){ $(formId).attr("action", url); }
			ajaxAnywhere.formName = form;
			ajaxAnywhere.getZonesToReload = function() { return zoons; };
			ajaxAnywhere.onAfterResponseProcessing = function () {
				if(typeof(ajaxCallback) == "function"){ ajaxCallback(); }
			};
			ajaxAnywhere.submitAJAX();
		}
		function calChange(t, adt){
			var y = Number($('#_year').attr('value'));
			var m = Number($('#_month').attr('value'));
			if(t=='M'){ m+=Number(adt); }else if(t=='Y'){ y+=Number(adt); }
			if(m<1){ y-=1; m=12; }else if(m>12){ y+=1; m=1; }
			if(m<10) m='0'+m;
			$('#targetDate').attr('value', y+'-'+m+'-01');
			ajaxSubmit('calendarForm', '${settingCtx}/holiday/holiday.htm?branchId='+$("#branchId").val(), 'holiday_zone');
		}
		function initCalendar(){
			var cal = eval($('#calendarDays').val())[0];
			$('#_year').attr('value', cal.year);
			$('#_month').attr('value', cal.month+1);
			var calStr = "<tr>"; var i=1;
			for(;i<cal.firstWeekday;i++){ calStr+="<td></td>"; }
			i=8-i; var j = 0;
			for(;j<i;j++){
				if(cal.days[j].isHoliday){ calStr+="<td style='color:#FF2F2F;'>"+cal.days[j].day+"</td>";
				}else{ calStr+="<td>"+cal.days[j].day+"</td>"; }
			}
			var x = 0;
			for(;j<cal.days.length;j++){
				if(x%7==0){ calStr+="</tr><tr>"; } x++;
				if(cal.days[j].isHoliday){ calStr+="<td style='color:#FF2F2F;'>"+cal.days[j].day+"</td>";
				}else{ calStr+="<td>"+cal.days[j].day+"</td>"; }
			}
			calStr+="</tr>";
			$('#calendarBody').html(calStr);
		}
		function showMessage(id, msg){
			if(msg != ""){
				$("#"+id).html(msg);
			}
			$("#"+id).show("show");
			setTimeout('$("#'+id+'").hide("show");',3000);
		}

		/**
		 * 日期初始化
		 * @param id
		 * @return
		 */
		function timeFormat(id,initVal){
			$('#'+id).timepicker({
				timeOnlyTitle: '时间',
				beforeShow:function(input, inst){
					if($("#"+id).attr("value")==""||typeof ($("#"+id).attr("value"))=='undefined'){
						$("#"+id).attr("value",initVal);
					}
				}
			});
		}
		function initDate(id){
			jQuery("#"+id).datepicker({
		       	"dateFormat":'yy-mm-dd',
			      changeMonth:true,
			      changeYear:true,
			      showButtonPanel:"true"
		       });
		}
	</script>
	<style type="text/css">
		.calendardate{border:1px solid #C5D9E8; }
		table.calendar { clear: both; border-collapse: collapse; color: #000;font: normal sans-serif 12px;font-weight: 400;}
		table.calendar caption{ line-height:24px; padding: 2px 4px; border:1px solid #C5D9E8; }
		table.calendar caption input{ text-align: center; border: 0px #fff; background: #fff; width: 30px;}
		table.calendar thead tr, table.calendar tbody tr { height: 22px; }
		table.calendar thead tr th, table.calendar tbody tr td{text-align: center;width: 24px; line-height: 22px;}
		table.calendar thead tr th {background-color : #BDEBEE;}
		table.calendar caption a.navImg{ cursor:pointer;display:block;height:16px;width:16px; margin-top: 4px;}
		table.calendar caption a.navImgl,table.calendar caption a.navImgll {float:left;}
		table.calendar caption a.navImgr,table.calendar caption a.navImgrr {float:right;}
		table.calendar caption a.navImgl{background:url("${imatrixCtx}/widgets/calendar/skin/default/img.gif") no-repeat scroll -16px 0 transparent;}
		table.calendar caption a.navImgll{background:url("${imatrixCtx}/widgets/calendar/skin/default/img.gif") no-repeat scroll 0px 0 transparent;}
		table.calendar caption a.navImgr{background:url("${imatrixCtx}/widgets/calendar/skin/default/img.gif") no-repeat scroll -48px 0 transparent;}
		table.calendar caption a.navImgrr{background:url("${imatrixCtx}/widgets/calendar/skin/default/img.gif") no-repeat scroll -32px 0 transparent;}
	</style>
</head>
	
<body>
<div class="ui-layout-center">
	<div class="opt-body">
	<s:if test="'DEPARTMENT' != nodeType">
		<input type="hidden" id="branchId" name="branchId" value="${branchId}">
		<aa:zone name="holiday_zone">
			<div class="opt-btn">
				<button class="btn" onclick="saveHoliday();"><span><span><s:text name="menuManager.save"></s:text> </span></span></button>
			</div>
			<div id="msg" style="padding-left: 6px;color: red;"></div>
			<form name="calendarForm" id="calendarForm"><input type="hidden" id="targetDate" name="targetDate" value=""> </form>
			<form name="holidayInputForm" id="holidayInputForm" method="post">
			<input type="hidden" name="id" value="${id}">
			<p style="padding: 6px 10px;font-size: 15px;color: red;"><s:text name="basicSetting.tip"></s:text></p>
			<table class="input" style="margin: 5px;">
				<tr>
					<td rowspan="9" style="padding-right:6px;">
						<aa:zone name="holiday_calendar">
						<input value="${specialDates}" name="calendarDays" id="calendarDays" type="hidden"/>
						<input value="${workStartTime}" name="workStartTime1"  type="hidden"/>
						<input value="${workEndTime}" name="workEndTime1" type="hidden"/>
						<div class="calendardate">
						<table class="calendar" >
							<caption> 
								<a class="navImg navImgll" onclick="calChange('Y', -1);"></a><a class="navImg navImgl" onclick="calChange('M', -1);"></a>
								<a class="navImg navImgr" onclick="calChange('Y', 1);"></a><a class="navImg navImgrr" onclick="calChange('M', 1);"></a>
								<input id="_year" readonly="readonly">&nbsp;<s:text name="basicSetting.year"></s:text><input id="_month" readonly="readonly"><s:text name="basicSetting.month"></s:text> 
							</caption>
							<thead>
							<tr><th><s:text name="basicSetting.day"></s:text></th><th><s:text name="basicSetting.one"></s:text></th><th><s:text name="basicSetting.two"></s:text></th><th><s:text name="basicSetting.three"></s:text></th><th><s:text name="basicSetting.four"></s:text></th><th><s:text name="basicSetting.five"></s:text></th><th><s:text name="basicSetting.six"></s:text></th></tr>
							</thead>
							<tbody id="calendarBody">
							</tbody>
						</table>
						</div>
						<script type="text/javascript">
							$().ready(function(){
								initCalendar();
								timeFormat("workStartTime","09:00");
								timeFormat("workEndTime","18:00");
							});
						</script>
						</aa:zone>
					</td>
				</tr>
				<tr>
					<td rowspan="2"><s:text name="basicSetting.commonSet"></s:text>：</td>
				</tr>
				<tr>
					<td><s:text name="basicSetting.startDate"></s:text>：<input readonly="readonly" name="startDate" id="_startDate" value=""> <span style="color: red;padding: 4px;"></span> 
					<s:text name="basicSetting.endDate"></s:text>：<input readonly="readonly" id="_endDate" name="endDate">
					<script type="text/javascript">
						$().ready(function(){
							initDate("_startDate");
							initDate("_endDate");
						});
					</script>
					</td>
				</tr>
				<tr>
					<td rowspan="2"><s:text name="basicSetting.quickSet"></s:text>：</td>
				</tr>
				<tr>
					<td>
						<input type="checkbox" name="dayOfWeek" value="7"/><s:text name="basicSetting.sunday"></s:text>
						<input type="checkbox" name="dayOfWeek" value="1"/><s:text name="basicSetting.monday"></s:text>
						<input type="checkbox" name="dayOfWeek" value="2"/><s:text name="basicSetting.tuesday"></s:text>
						<input type="checkbox" name="dayOfWeek" value="3"/><s:text name="basicSetting.wednesday"></s:text>
						<input type="checkbox" name="dayOfWeek" value="4"/><s:text name="basicSetting.thursday"></s:text>
						<input type="checkbox" name="dayOfWeek" value="5"/><s:text name="basicSetting.friday"></s:text>
						<input type="checkbox" name="dayOfWeek" value="6"/><s:text name="basicSetting.saturday"></s:text>
					</td>
				</tr>
				<tr>
					<td rowspan="2"><s:text name="basicSetting.workTime"></s:text>：</td>
				</tr>
				<tr>
					<td><input id="workStartTime" name="workStartTime"  readonly="readonly" value="${workStartTime }"/>&nbsp;&nbsp;-&nbsp;&nbsp;
						<input readonly="readonly" name="workEndTime" id="workEndTime" value="${workEndTime }"/> <span style="color: red;"></span> 
					</td>
				</tr>
				<tr>
					<td rowspan="2"><s:text name="basicSetting.setTo"></s:text>：</td>
				</tr>
				<tr>
					<td><input type="radio" name="dateType" checked="checked" value="HOLIDAY"  /><s:text name="basicSetting.holiday"></s:text>
						<input type="radio" name="dateType" value="WORKING_DAY" id="work"  /><s:text name="basicSetting.weekDay"></s:text>
					</td>
				</tr>
			</table>
			</form>
			<!-- 
			<script type="text/javascript">
			 $(function(){
				 $("#holidayInputForm").validate({
					submitHandler: function() {
						ajaxSubmit('holidayInputForm', '${settingCtx}/holiday/holiday-save.htm', 'holiday_zone');
					},
					rules: {
						startDate: "required"
					},
					messages: {
						startDate: "必填"
					}
				 });
			});
			</script>
			 -->
		</aa:zone>
	</s:if><s:else>
		<div style="height: 80px;"></div>
	    <div style="font-size: 20px;text-align: center;letter-spacing: 4px;">
	  		 <s:text name="basicSetting.chooseBranch"></s:text>
	    </div>
	</s:else>
	</div>
</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
<script src="${resourcesCtx}/widgets/timepicker/timepicker_<%=com.norteksoft.product.util.ContextUtils.getCurrentLanguage()%>.js" type="text/javascript"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
</html>