function remindUser(type){
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
				chkStyle:"checkbox",
				chkboxType:"{'Y' : 'ps', 'N' : 'ps' }"
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
					remindUserCallback(type);
				}
			}			
		};
	    popZtree(zTreeSetting);
}

function remindUserCallback(type){
	if(type=="email"){
		var mailInfo = ztree.getEmails();
		if(mailInfo!=""){
			var result = "";
			var nums = mailInfo.split(",");
			for(var i=0;i<nums.length;i++){
				if(nums[i]!=""){
					result=result+nums[i]+",";
				}
			}
			if(result.indexOf(",")>0){
				result = result.substring(0,result.lastIndexOf(","));
			}
			$("#emails").attr("value",result);
		}
		
	}else if(type=="RTX"){
		var userNames = ztree.getNames();
		if(userNames=="所有人员"){
			iMatrix.alert(iMatrixMessage["interfaceManager.specificPerson"]);
			return;
		}
		var loginNames = ztree.getLoginNames();
		$("#rtxAccounts").attr("value",loginNames);
	}else if(type=="phone"){
		var phoneNums = ztree.getTelephones();
		if(phoneNums!=""){
			var result = "";
			var nums = phoneNums.split(",");
			for(var i=0;i<nums.length;i++){
				if(nums[i]!=""&&nums[i]!="null"){
					result=result+nums[i]+",";
				}
			}
			if(result.indexOf(",")>0){
				result = result.substring(0,result.lastIndexOf(","));
			}
			$("#phoneReminderNums").attr("value",result);
		}
	}else if(type=="swing"){
		var userNames = ztree.getNames();
		if(userNames=="所有人员"){
			iMatrix.alert(iMatrixMessage["interfaceManager.specificPerson"]);
			return;
		}
		$("#officeHelperReminderNames").attr("value",userNames);
		var userIds = ztree.getIds();
		$("#officeHelperReminderIds").attr("value",userIds);
	}
}

function clearValue(inputId1,inputId2){
	$("#"+inputId1).attr("value","");
	if($("#"+inputId2).attr("id")==inputId2){//判断input是否存在
		$("#"+inputId2).attr("value","");
	}
}