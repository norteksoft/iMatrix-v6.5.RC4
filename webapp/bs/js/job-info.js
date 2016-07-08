
	function checkBoxSelect(id){
		$("#"+id).multiselect({
		 	multiple: true,
		  /*header: "Select an option",
		   noneSelectedText: "Select an Option",*/
		   header: true,
		   selectedList: 1
		});
	}

	/**
	 * 日期初始化
	 * @param id
	 * @return
	 */
	function timeFormat(id){
		$('#'+id).timepicker({
			timeOnlyTitle: '时间',
			beforeShow:function(input, inst){
				if($("#"+id).attr("value")==""||typeof ($("#"+id).attr("value"))=='undefined'){
					$("#"+id).attr("value","00:00");
				}
			}
		});
	}


	/**
	 * 日期初始化
	 * @param id
	 * @return
	 */
	function dateFormat(id){
		$('#'+id).datetimepicker({
			"dateFormat":'yy-mm-dd',
			changeMonth:true,
			changeYear:true
		});
	}

	function typeChange(value){
		clearForm();
		if(value=="everyDate"){
			$("#tr_everyDate").show();
			$("#tr_everyWeek").hide();
			$("#tr_everyMonth").hide();
			$("#tr_appointTime").hide();
			$("#tr_appointSet").hide();
			$("#tr_intervalTime_type").hide();
			$("#tr_intervalTime_hour").hide();
			$("#tr_intervalTime_second").hide();
			$("#tr_appointSet_discription").hide();
		}else if(value=="everyWeek"){
			$("#tr_everyDate").show();
			$("#tr_everyWeek").show();
			$("#tr_everyMonth").hide();
			$("#tr_appointTime").hide();
			$("#tr_appointSet").hide();
			$("#tr_intervalTime_type").hide();
			$("#tr_intervalTime_hour").hide();
			$("#tr_intervalTime_second").hide();
			$("#tr_appointSet_discription").hide();
		}else if(value=="everyMonth"){
			$("#tr_everyDate").show();
			$("#tr_everyWeek").hide();
			$("#tr_everyMonth").show();
			$("#tr_appointTime").hide();
			$("#tr_appointSet").hide();
			$("#tr_appointSet_discription").hide();
			$("#tr_intervalTime_type").hide();
			$("#tr_intervalTime_hour").hide();
			$("#tr_intervalTime_second").hide();
		}else if(value=="appointTime"){
			$("#tr_everyDate").hide();
			$("#tr_everyWeek").hide();
			$("#tr_everyMonth").hide();
			$("#tr_appointTime").show();
			$("#tr_appointSet").hide();
			$("#tr_intervalTime_type").hide();
			$("#tr_intervalTime_hour").hide();
			$("#tr_intervalTime_second").hide();
			$("#tr_appointSet_discription").hide();
		}else if(value=="appointSet"){
			$("#tr_everyDate").hide();
			$("#tr_everyWeek").hide();
			$("#tr_everyMonth").hide();
			$("#tr_appointTime").hide();
			$("#tr_intervalTime_type").hide();
			$("#tr_intervalTime_hour").hide();
			$("#tr_intervalTime_second").hide();
			$("#tr_appointSet").show();
			$("#tr_appointSet_discription").show();
		}else if(value=="intervalTime"){//时间间隔
			$("#tr_everyDate").hide();
			$("#tr_everyWeek").hide();
			$("#tr_everyMonth").hide();
			$("#tr_appointTime").hide();
			$("#tr_appointSet").hide();
			$("#tr_appointSet_discription").hide();
			$("#tr_intervalTime_type").show();
		}
	}

	function clearForm(){
     $("#everyDate").attr("value","");
     $("#appointTime").attr("value","");
     $("#appointSet").attr("value","");
     $("#everySecond").attr("value","");
     $("#everyHour").attr("value","");
     $("#everyWeek").attr("value","");
     $("#everyMonth").attr("value","");
    }

	function intervalTypeChange(type){
          if(type=='secondType'){
        	$("#tr_intervalTime_second").show();
  			$("#tr_intervalTime_hour").hide();
          }else if(type=='hourType'){
        	  $("#tr_intervalTime_second").hide();
    		  $("#tr_intervalTime_hour").show();
          }
    }

	//提交
	function submitJobInfo(){
		$("#jobInfoFrom").attr("action",webRoot+'/options/job-info-save.htm');
		$("#jobInfoFrom").submit();
	}

		$.validator.addMethod("customRequired", function(value, element) {
			var $element = $(element);
			if($element.val()!=null&&$element.val()!=''&&typeof ($element.val())!='undefined'){
				return true;
			}
			
			if($("#typeEnum").val()=='everyDate'){
				if($("#everyDate").val()!=null&&$("#everyDate").val()!=''&&typeof ($("#everyDate").val())!='undefined'){
					return true;
				}
			}
			if($("#typeEnum").val()=='everyMonth'){
				if(($("#everyMonth").val()!=null&&$("#everyMonth").val()!=''&&typeof ($("#everyMonth").val())!='undefined')
						&&($("#everyDate").val()!=null&&$("#everyDate").val()!=''&&typeof ($("#everyDate").val())!='undefined')){
					return true;
				}
			}
			if($("#typeEnum").val()=='everyWeek'){
				if($("#everyDate").val()!=null&&$("#everyDate").val()!=''&&typeof ($("#everyDate").val())!='undefined'){
					return true;
				}
			}
			if($("#typeEnum").val()=='appointTime'){
				if($("#appointTime").val()!=null&&$("#appointTime").val()!=''&&typeof ($("#appointTime").val())!='undefined'){
					return true;
				}
			}
			if($("#typeEnum").val()=='appointSet'){
				if($("#appointSet").val()!=null&&$("#appointSet").val()!=''&&typeof ($("#appointSet").val())!='undefined'){
					return true;
				}
			}
			if($("#typeEnum").val()=='intervalTime'){
				if(($("#everySecond").val()!=null&&$("#everySecond").val()!=''&&typeof ($("#everySecond").val())!='undefined')
						||($("#everyHour").val()!=null&&$("#everyHour").val()!=''&&typeof ($("#everyHour").val())!='undefined')){
					return true;
				}
			}
		}, iMatrixMessage["common.required"]);

		$.validator.addMethod("intervalTimeValidate", function(value, element) {
			if($("#typeEnum").val()=='intervalTime'){
				if(($("#everySecond").val()!=null&&$("#everySecond").val()!=''&&typeof ($("#everySecond").val())!='undefined'&&$("#everySecond").val().indexOf(0)!=0)
						||($("#everyHour").val()!=null&&$("#everyHour").val()!=''&&typeof ($("#everyHour").val())!='undefined'&&$("#everyHour").val().indexOf(0)!=0)){
					return true;
				}
			}else{
                    return true; 
			}
		}, iMatrixMessage["bs.jobInfo.youEnteredIsNotValid"]);
		
	function validateTimer(){
		$("#jobInfoFrom").validate({
			submitHandler: function() {
				var emails = $("#emails").attr("value");
				if(typeof(emails)!='undefined'&&emails.length>1000){
					//字段邮件提醒人只能输入1000个字符,当前长度为
					iMatrix.alert(iMatrixMessage["bs.jobInfo.fieldEmailReminder"]+emails.length);
					return;
				}
				var rtxAccounts = $("#rtxAccounts").attr("value");
				if(typeof(rtxAccounts)!='undefined'&&rtxAccounts.length>1000){
					//字段RTX提醒人只能输入1000个字符,当前长度为
					iMatrix.alert(iMatrixMessage["bs.jobInfo.fieldRtxReminder"]+rtxAccounts.length);
					return;
				}
				var phoneReminderNums = $("#phoneReminderNums").attr("value");
				if(typeof(phoneReminderNums)!='undefined'&&phoneReminderNums.length>1000){
					//字段短信提醒人只能输入1000个字符,当前长度为
					iMatrix.alert(iMatrixMessage["bs.jobInfo.fieldMessageReminder"]+phoneReminderNums.length);
					return;
				}
				var officeHelperReminderNames = $("#officeHelperReminderNames").attr("value");
				if(typeof(officeHelperReminderNames)!='undefined'&&officeHelperReminderNames.length>490){
					//字段办公助手提醒人只能输入490个字符,当前长度为
					iMatrix.alert(iMatrixMessage["bs.jobInfo.fieldOfficerReminder"]+officeHelperReminderNames.length);
					return;
				}
				
				if($("#typeEnum").val()=='everyWeek'){//每周时
					if($("#everyWeek").val()==null||$("#everyWeek").val()==''||typeof ($("#everyWeek").val())=='undefined'){
						$("#tr_everyWeek td").append('<label  class="error">'+iMatrixMessage["common.required"]+'</label>');
					}else{
						$("#tr_everyWeek td label").remove();
						$("#jobInfoFrom").ajaxSubmit(function (id){
							$("#id").attr("value",id);
							$("#message").show();
							setTimeout('$("#message").hide("show");',3000);
							parent.backPage();
							parent.$.colorbox.close();
						});
					}
				}else{//非每周
					$("#jobInfoFrom").ajaxSubmit(function (id){
						$("#id").attr("value",id);
						$("#message").show();
						setTimeout('$("#message").hide("show");',3000);
						parent.backPage();
						parent.$.colorbox.close();
					});
				}
			
			},
			rules: {
				runAsUser: "required",
				code: "required",
				url: "required",
				typeEnum:"required",
			    everySecond: {
				    digits: true,
				    maxlength:7,
				    min:1
			    },
			    everyHour: {
			    	digits: true,
				    maxlength:7,
				    min:1
			    }
			},
			messages: {
				runAsUser: iMatrixMessage["common.required"],
				code: iMatrixMessage["common.required"],
				url: iMatrixMessage["common.required"],
				typeEnum: iMatrixMessage["common.required"],
				everySecond:{
				   digits : iMatrixMessage["bs.jobInfo.pleaseEnterAnIntegerBelowSevenBits"],//请输入7位以下的整数
				   maxlength:iMatrixMessage["bs.jobInfo.pleaseEnterAnIntegerBelowSevenBits"],//请输入7位以下的整数
				   min:iMatrixMessage["bs.jobInfo.pleaseEnterAnIntegerGreaterThanOne"]//请输入大于1的整数
			    },
			    everyHour:{
			    	digits : iMatrixMessage["bs.jobInfo.pleaseEnterAnIntegerBelowSevenBits"],//请输入7位以下的整数
				   maxlength:iMatrixMessage["bs.jobInfo.pleaseEnterAnIntegerBelowSevenBits"],//请输入7位以下的整数
				   min:iMatrixMessage["bs.jobInfo.pleaseEnterAnIntegerGreaterThanOne"]//请输入大于1的整数
				}
			}
		});
	}

	/*---------------------------------------------------------
	函数名称:selectPrincipal
	参    数:id
	功    能：负责人树
	------------------------------------------------------------*/
	function selectPrincipal(name,id){
		var zTreeSetting={
				leaf: {
					enable: false
				},
				type: {
					treeType: "MAN_DEPARTMENT_TREE",
					noDeparmentUser:false,
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
			                //showInput:"point_user",
			                //hiddenInput:"point_user_value",
			                append:false
				},
				callback: {
					onClose:function(){
						getUserInformation(name,id);
					}
				}			
				};
			    popZtree(zTreeSetting);
	}

	function getUserInformation(name,id){
		$("#"+name).attr("value",ztree.getName());
		$("#"+id).attr("value",ztree.getId());
		$("#runAsUser").attr("value",ztree.getLoginName());
	}

	function showDiscription(){
		$("#discription").show();
		 $.colorbox({href:"#discription",inline:true, innerWidth:550, innerHeight:450,overlayClose:false,title:iMatrixMessage["bs.jobCornSettingDeclare.explain"],onClosed:function(){$("#discription").hide();}});
	}
	function secondOrHour(obj){
        if(obj.id=='everySecond'){
            $("#everyHour").attr("value","");
         }else{
        	$("#everySecond").attr("value","");
         }
	}