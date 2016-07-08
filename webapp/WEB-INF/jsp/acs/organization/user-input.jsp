<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/acs-taglibs.jsp"%>
<%@ page import="org.ajaxanywhere.AAUtils"%> 

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>列表管理</title>
	<%@ include file="/common/acs-iframe-meta.jsp"%>
</head>
<body>
<div class="ui-layout-center">
	<aa:zone name="acs_content">
	<div class="opt-btn">
		<security:authorize ifAnyGranted="saveEditUser">
			<s:if test="looked=='LOOK'&&edited!='NEW'&&edited!='NEW'"></s:if>
			<s:else>
			<button  class='btn' onclick="submitForm();"><span><span><s:text name="common.submit"/></span></span></button>
			</s:else>
		</security:authorize>
		    <s:if test="fromWorkgroup=='fromWorkgroup'">
			<button  class='btn'  onclick="setPageState();cancel();"><span><span><s:text name="common.cancel"/></span></span></button>
			<form id="cancelForm"  name="cancelForm" action="${acsCtx}/organization/work-group-getUserByWorkGroup.action" method="post">
				<input type="hidden"  name="workGroupId"  value="${workGroupId }" />
			</form>
			</s:if>
			<s:else>
			<button  class='btn'  onclick="setPageState();cancel();"><span><span><s:text name="common.cancel"/></span></span></button>
			<form id="cancelForm"  name="cancelForm" action="${acsCtx}/organization/user.action" method="post">
				<input type="hidden" id="departmId"  name="departmId"  value="" />
				<input type="hidden" id="departmType"  name="departmType"  value="" />
				<input type="hidden" id="branchId"  name="branchId"  value="" />
			</form>
			</s:else>
	</div>
	<div style="display: none;" id="message"><font class="onSuccess"><nobr><s:text name="common.saved"></s:text></nobr></font></div>
	<div id="opt-content">
		<form id="inputForm"  name="inputForm" action="${acsCtx}/organization/user-save.action" method="post">
			<input type="hidden" id="id" name="id"  value="${id}" />
			<input type="hidden" id="uid"  value="${user.id}" />
			<input type="hidden" id="dids"  name="dids"  value="${deId}" />
			<input type="hidden" id="deId"  name="deId"  value="${deId}" />
			<input type="hidden" id="oldDid"  name="oldDid"  value="" />
			<input type="hidden" id="oldType"  name="oldType"  value="" />
			<input type="hidden" id="oneDid"  name="oneDid"  value="${user.mainDepartmentId }" />
			<input type="hidden" id="subCompanyId"  value="${user.subCompanyId}" />
			<input type="hidden" id="departSubCompanyId"  value="${mainDepartmentSubCompanyId}" />
			<input type="hidden" id="secondDepartSubCompanyId"  value="" />
			<input type="hidden" id="passWordChange"  name="passWordChange"  value="" />
			<input type="hidden" id="bId"  name="branId"  value="${branId}" />
			<table class="form-table-without-border" >
			 <tr>
				<td class="content-title"><s:text name="user.loginName"/></td>
				<td>
					<s:if test="id==null">
						<input type="hidden" name="historyUserName" value="${requestScope.user.name}"/>
						<input type="text" name="user.loginName"  class="required" id="loginName"  maxlength="30" value="${requestScope.user.loginName}"  style="color: black;"/>
						<font color="red">*</font>
					</s:if><s:else>
						<input type="text" disabled="disabled" value="${requestScope.user.loginName}" id="loginName"/>
					</s:else>
				</td>
				<td class="content-title"> <s:text name="user.trueName"/></td>
				<td>
					<s:if test='"LOOK".equals(looked)'>
						<input type="text" name="user.name" disabled="disabled"  maxlength="30" value="${requestScope.user.name}"/>
					</s:if><s:else>
						<input type="text"  name="user.name" maxlength="30" class="required" value="${requestScope.user.name}" id="trueName1" style="color: black;"/> 
						<font color="red">*</font>
					</s:else>
				</td>
			</tr>
			<s:if test="id==null">
				<tr>
					<td class="content-title"><s:text name="user.passWord"/></td>
					<td><input  type="password"  name="user.password"   class="required" id ="password" value="${requestScope.user.password}" onchange="checkLoginPassword(this)" style="color: black;"/>(<s:text name='user.passwordQuote'/>)
						<font color="red">*</font></td>
					<td class="content-title"><s:text name="user.passwordConfirm"/></td>
					<td><input  type="password" name="passwordConfirm"   id="passwordConfirm" value="${user.password}" />(<s:text name='user.passwordQuote'/>)
						<font color="red">*</font> </td>
				</tr>
			</s:if>
			<s:else>
	             <s:if test="looked=='LOOK'&&edited!='NEW'"></s:if>
	             <s:else>
	             <tr>
		             <td class="content-title">
		                <security:authorize ifAnyGranted="modifyPassWord">
		                   <a href='javascript: modifyPassWord(${id},"${acsCtx}/organization/user-modifyPassWord.action")' id="modify_password" name="modify_password">
		                      <s:text name="userInfo.updatePassword"/>
		                      <input  type="hidden" name="user.password"  id ="password" value="${requestScope.user.password}"/>
		                   </a>
		                 </security:authorize>  
		             </td>							            
		             <td></td>
		             <td></td>
		             <td></td>
		         </tr>
		         </s:else>
			</s:else>
			 <tr> 
		         <td class="content-title"><s:text name='user.principalDep'/></td><!-- 正职部门 -->
			     <td>
			     <input type="text" id="mainDepartmentName" value="${mainDepartmentName}" disabled="disabled"></input>
			      <s:if test="looked=='LOOK'&&edited!='NEW'"></s:if>
			     <s:else>
			     <a href="#" onclick="Dtree2('single'); " class="small-btn" ><span><span id="choose_user"><s:text name='user.choose'/></span></span></a>
	             </s:else>
			     </td>
		         <td class="content-title"><s:text name='user.PartTimeDepartment'/></td><!-- 兼职部门 -->
			     <td>
			     <input type="text" id="departmentName" value="${departmentName}" disabled="disabled"></input>
			     <s:if test="looked=='LOOK'&&edited!='NEW'"></s:if>
			     <s:else>
			      <a href="#" onclick="Dtree2('multiple'); " class="small-btn" ><span><span id="choose_user"><s:text name='user.choose'/></span></span></a>&nbsp;<a href="#" onclick="clearInput('departmentName');clearInput('dids'); " class="small-btn" ><span><span ><s:text name="user.empty"></s:text></span></span></a>
	             </s:else>
			     </td>
		     </tr>
			<tr>
	             <td class="content-title"> <s:text name="user.email"/></td>
	             <td>
		             <s:if test="looked=='LOOK'&&edited!='NEW'">
		             <input  type="text" name="user.email"  disabled="disabled"  value="${requestScope.user.email}"  />
		             </s:if>
		             <s:else>
		             <input  type="text"  name="user.email"  id="email" class="required email" value="${requestScope.user.email}" style="color: black;"/>
		             </s:else>
	             </td>
	             
	             <td class="content-title"><s:text name="user.ResearchersClassified"/></td><!-- 人员密级 -->
	             <s:if test="looked=='LOOK'&&edited!='NEW'">
	             <td><select name="user.secretGrade" disabled="disabled">
	             		<option value="COMMON" <s:if test="user.secretGrade==@com.norteksoft.acs.base.enumeration.SecretGrade@COMMON">selected="selected"</s:if>> <s:text name='user.general'/> </option><!-- 一般 -->
	             		<option value="MAJOR" <s:if test="user.secretGrade==@com.norteksoft.acs.base.enumeration.SecretGrade@MAJOR">selected="selected"</s:if>><s:text name='user.important'/></option><!-- 重要 -->
	             		<option value="CENTRE" <s:if test="user.secretGrade==@com.norteksoft.acs.base.enumeration.SecretGrade@CENTRE">selected="selected"</s:if>><s:text name='user.theCore'/></option><!-- 核心 -->
	             	</select></td>
	             </s:if>
	             <s:else>
	             <td>
	             	<select name="user.secretGrade">
	             		<option value="COMMON" <s:if test="user.secretGrade==@com.norteksoft.acs.base.enumeration.SecretGrade@COMMON">selected="selected"</s:if>><s:text name='user.general'/> </option><!-- 一般 -->
	             		<option value="MAJOR" <s:if test="user.secretGrade==@com.norteksoft.acs.base.enumeration.SecretGrade@MAJOR">selected="selected"</s:if>><s:text name='user.important'/></option><!-- 重要 -->
	             		<option value="CENTRE" <s:if test="user.secretGrade==@com.norteksoft.acs.base.enumeration.SecretGrade@CENTRE">selected="selected"</s:if>><s:text name='user.theCore'/></option><!-- 核心 -->
	             	</select></td>
	             </s:else>
	         </tr>	
	         <tr>
	             <td class="content-title"> <s:text name='user.emailConfiguration'/></td><!-- 邮箱配置 -->
	             <td>
		             <s:if test="looked=='LOOK'&&edited!='NEW'">
		             	<select id="mailboxDeploy" name="user.mailboxDeploy" disabled="disabled">
		             		<option value=''><s:text name='user.pleaseChoose'/></option>
							<s:iterator value="@com.norteksoft.acs.base.enumeration.MailboxDeploy@values()" var="mailboxDeployVar">
								<option <s:if test="#mailboxDeployVar==user.mailboxDeploy">selected="selected"</s:if> value="${mailboxDeployVar}"><s:text name="%{code}"></s:text></option>
							</s:iterator>
						</select>
		             </s:if>
		             <s:else>
		             	<select id="mailboxDeploy" name="user.mailboxDeploy">
		             		<option value=''><s:text name='user.pleaseChoose'/></option>
							<s:iterator value="@com.norteksoft.acs.base.enumeration.MailboxDeploy@values()" var="mailboxDeployVar">
								<option <s:if test="#mailboxDeployVar==user.mailboxDeploy">selected="selected"</s:if> value="${mailboxDeployVar}"><s:text name="%{code}"></s:text></option>
							</s:iterator>
						</select>
		             </s:else>
		             <font color="red">*</font>
	             </td>
	              <s:if test="looked=='LOOK'&&edited!='NEW'">
	              <td class="content-title"> <s:text name='user.branch'/></td><!-- 分支机构 -->
	              <td>
	              <input type="text" value="${requestScope.user.subCompanyName }" disabled="disabled" />
	              </td>
	              </s:if>
	         </tr>		 
	         <tr>
	             <td class="content-title"> <s:text name="validat.input.weighing"/></td>
	             <s:if test="looked=='LOOK'&&edited!='NEW'">
		             <s:if test="user.weight!=null">
		             <td><input  type="text" name="user.weight" maxlength="8" disabled="disabled" id="weighingId" value="${user.weight}" /></td>
		             </s:if>
		             <s:else>
		             <td><input  type="text" name="user.weight" maxlength="8" disabled="disabled" id="weighingId" value="1" /></td>
		             </s:else>
	             </s:if>
	             <s:else>
		             <s:if test="user.weight!=null">
		             <td><input  type="text"  name="user.weight" class="required digits" maxlength="8" id="weighingId" value="${user.weight}" style="color: black;" /></td>
		             </s:if>
		             <s:else>
		             <td><input  type="text" name="user.weight"  maxlength="8" id="weighingId" value="1" /></td>
		             </s:else>
	             </s:else>
	             <td class="content-title"> <s:text name='user.respectfulName'/></td><!-- 尊称 -->
	             <s:if test="looked=='LOOK'&&edited!='NEW'">
	             <td> <input name="user.honorificName" maxlength="15" disabled="disabled" value="${user.honorificName}"/></td>
	             </s:if>
	             <s:else>
	             <td> <input name="user.honorificName" maxlength="15" value="${user.honorificName}"/></td>
	             </s:else>
	         </tr>
	          <tr>
	             <td class="content-title"><s:text name='user.emailSize'/>(M)</td><!-- 邮件大小 -->
	             <td>
	             <s:if test="looked=='LOOK'&&edited!='NEW'">
	              <input  type="text"  name="user.mailSize"  maxlength="8" disabled="disabled" id="mailSizeId" value="${user.mailSize}" style="color: black;" />
	             </s:if>
	             <s:else>
	              <input  type="text"  name="user.mailSize"  maxlength="8" class="required number" id="mailSizeId" value="${user.mailSize}" style="color: black;" /><font color="red">*</font>
	             </s:else>
	             </td>
	             <td class="content-title"> <s:text name="user.mobileTelephone"/></td>
	             <s:if test="looked=='LOOK'&&edited!='NEW'">
	             <td><input  type="text" name="user.mobileTelephone" maxlength="20"  disabled="disabled"  value="${user.mobileTelephone}" id="mobileTelephone"/></td>
	             </s:if>
	             <s:else>
	             <td><input  type="text"  name="user.mobileTelephone" maxlength="20"  value="${user.mobileTelephone}" id="mobileTelephone" onkeyup="value=this.value.replace(/[^-,0-9]/g,'');"/></td>
	             </s:else>
	         </tr>	 	          
	         <tr>
	         	 <td class="content-title"> <s:text name="userInfo.telephone"/></td>
	             <s:if test="looked=='LOOK'&&edited!='NEW'">
	             <td><input  type="text" name="telephone" maxlength="30"  disabled="disabled"  value="${telephone}" id="Tel_number"/></td>
	             </s:if>
	             <s:else>
	             <td><input  type="text"  name="telephone" maxlength="30"  value="${telephone}" id="telephone" onkeyup="value=this.value.replace(/[^-,0-9]/g,'');"/></td>
	             </s:else>
	             <td class="content-title"> <s:text name="user.sex"/></td>
	             <td>
	              <s:if test="looked=='LOOK'&&edited!='NEW'">
	                 <SELECT NAME="user.sex" disabled="disabled">  
	                      <s:if test="user.sex==true">
							    <OPTION VALUE="true" selected="selected"><s:text name="userInfo.male"/></OPTION>
							    <OPTION VALUE="false"><s:text name="userInfo.woman"/></OPTION>
	                      </s:if>
	                      <s:else>
							    <OPTION VALUE="true" ><s:text name="userInfo.male"/></OPTION>
							    <OPTION VALUE="false" selected="selected"><s:text name="userInfo.woman"/></OPTION>
	                      </s:else>	
						</SELECT>  
	             </s:if>
	             <s:else>
	                    <SELECT NAME="user.sex">  
	                      <s:if test="user.sex==true">
							    <OPTION VALUE="true" selected="selected"><s:text name="userInfo.male"/></OPTION>
							    <OPTION VALUE="false"><s:text name="userInfo.woman"/></OPTION>
	                      </s:if>
	                      <s:else>
							    <OPTION VALUE="true" ><s:text name="userInfo.male"/></OPTION>
							    <OPTION VALUE="false" selected="selected"><s:text name="userInfo.woman"/></OPTION>
	                      </s:else>	
						</SELECT>  
				 </s:else>		
	             </td>
	         </tr>
	          <tr>
	             <td class="content-title"><s:text name='user.cardNumber'/></td><!-- 卡号 -->
	             <td>
	             <s:if test="looked=='LOOK'&&edited!='NEW'">
	                 <input type="text" disabled="disabled" name="user.cardNumber" id="user.cardNo"  value="${user.cardNumber}" />
	             </s:if>
	             <s:else>
	                 <input type="text" name="user.cardNumber" id="user.cardNo"  value="${user.cardNumber}" />
	             </s:else>
	             </td>
	             <td class="content-title"><s:text name="user.nativePlace"/></td>
	             <td>
	             <s:if test="looked=='LOOK'&&edited!='NEW'">
	             <input type="text" name="nativePlace"  disabled="disabled" maxlength="15" value="${nativePlace}" id="nativePlace"/>
	             </s:if>
	             <s:else>
	             <input type="text"  name="nativePlace"   maxlength="15" value="${nativePlace}" id="nativePlace"/>
	             </s:else>
	             </td>
	         </tr>
	          <s:if test="looked=='LOOK'&&edited!='NEW'">
	          <tr>
	             <td class="content-title"> <s:text name="user.race"/></td>
	             <td><input type="text" name="nation"  disabled="disabled" maxlength="15" value="${nation}" id="race"/></td>
	             <td class="content-title"> <s:text name="userInfo.polityVisage"/></td>
	             <td><input type="text" name="politicalStatus" disabled="disabled" maxlength="15" value="${politicalStatus}" id="polityVisage"/></td>
	         </tr> 
	         <tr>
				<td class="content-title"><s:text name="userInfo.weight"/>(kg) </td>
				<td><input  type="text"  disabled="disabled" name="bodyWeight" maxlength="15" value="${bodyWeight}" id="weight"/></td>
	             <td class="content-title"><s:text name="userInfo.high"/>(cm) </td>
	             <td><input  type="text"  disabled="disabled" name="height" maxlength="15" value="${height}" id="high"/></td>
	         </tr>  
	         <tr>
	             <td class="content-title"><s:text name="user.birthday"/></td>
	             <td>
	                 <input type="text" disabled="disabled" name="birthday" id="birthday"  value="${birthday}"  readonly="readonly" />
	             </td>
	             <td class="content-title"><s:text name="userInfo.iDcard"/></td>
	             <td><input type="text"  disabled="disabled" name="idCardNumber" maxlength="20" value="${idCardNumber}" id="IDcard"/></td>
	         </tr> 
	          <tr>
	             <td class="content-title"><s:text name="userInfo.hireDate"/></td>
	             <td>
	                 <input type="text" disabled="disabled" name="hireDate" id="hireDate"  value="${hireDate}" readonly="readonly" />
	             </td>
	             <td class="content-title"><s:text name="userInfo.treatment"/> </td>
	             <td><input type="text" disabled="disabled" name="treatment"  maxlength="20" value="${treatment}" id="treatment"/></td>
	         </tr>
	         <tr>
	             <td class="content-title"> <s:text name="userInfo.marriageStates"/></td>
	             <td><input type="text" disabled="disabled" name="maritalStatus"  maxlength="10" value="${maritalStatus}" id="marriageStates"/></td>
	             <td class="content-title"><s:text name="userInfo.schoolRecord"/></td>
	             <td><input type="text" disabled="disabled" name="educationGrade"  maxlength="20" value="${educationGrade}" id="schoolRecord"/></td>
	         </tr>
	         <tr>
	             <td class="content-title"><s:text name="userInfo.school"/></td>
	             <td><input type="text" disabled="disabled" name="graduatedSchool"  maxlength="20" value="${graduatedSchool}" id="school"/></td>
	             <td class="content-title"><s:text name="userInfo.speciality"/></td>
	             <td><input type="text" disabled="disabled" name="major"  maxlength="15" value="${major}" id="speciality"/></td>
	         </tr>
	         <tr>
	             <td class="content-title"> <s:text name="userInfo.degree"/></td>
	             <td><input type="text" disabled="disabled" name="degree"  maxlength="15" value="${degree}" id="degree"/></td>
	             <td class="content-title"><s:text name="userInfo.graduateDate"/></td>
	             <td>
	                 <input type="text" disabled="disabled" name="graduatedDate"  id="graduateDate"  value="${graduatedDate}"  readonly="readonly" />
	             </td>
	         </tr>      
	         <tr>
	             <td class="content-title"><s:text name="userInfo.firstLanguage"/></td>
	             <td><input type="text" disabled="disabled" name="firstForeignLanguage"  maxlength="10" value="${firstForeignLanguage}" id="FirstLanguage"/></td>
	             <td class="content-title"><s:text name="userInfo.skilldegree"/></td>
	             <td><input type="text" disabled="disabled" name="skilledDegree"  maxlength="15" value="${skilledDegree}" id="skilldegree"/></td>
	         </tr>
	         <tr>
	             <td class="content-title"><s:text name="userInfo.secondLange"/></td>
	             <td><input type="text" disabled="disabled" name="secondForeignLanguage" maxlength="10"  value="${secondForeignLanguage}" id="SecondLange"/></td>
	             <td class="content-title"><s:text name="userInfo.bloodType"/></td>
	             <td><input type="text" disabled="disabled" name="bloodGroup"  maxlength="6" value="${bloodGroup}" id="bloodType"/></td>
	         </tr>     
	         <tr>
	             <td class="content-title"> <s:text name="userInfo.homeAddress"/></td>
	             <td><input type="text" disabled="disabled" name="homeAddress" maxlength="15"  value="${homeAddress}" id="homeAddress"/></td>
	             <td class="content-title"><s:text name="userInfo.homePostCode"/></td>
	             <td><input  type="text"  disabled="disabled" name="homePostCode"  maxlength="10" value="${homePostCode}" id="homePostCode"/></td>
	         </tr>
	         <tr>
	             <td class="content-title"> <s:text name="userInfo.cityArea"/></td>
	             <td><input type="text" disabled="disabled" name="cityArea"  maxlength="15" value="${cityArea}" /></td>
	             <td class="content-title"><s:text name="userInfo.interest"/></td>
	             <td><input type="text" disabled="disabled" name="interest"  maxlength="15" value="${interest}" id="interest"/></td>
	         </tr>
	         <tr>
	             <td class="content-title"><s:text name="userInfo.marriageDate"/></td>
	             <td>
	                 <input type="text" disabled="disabled" name="marriageDate" id="marriageDate"  value="${marriageDate}"  readonly="readonly" />
	             </td>
	             <td class="content-title"><s:text name="userInfo.mateName"/></td>
	             <td><input type="text" disabled="disabled" name="mateName" maxlength="10"  value="${mateName}" id="mateName"/></td>
	         </tr>
	         <tr>
	             <td class="content-title"><s:text name="userInfo.mateBirthday"/></td>
	             <td>
	                 <input type="text" disabled="disabled" name="mateBirthday" id="mateBirthday"  value="${mateBirthday}"  readonly="readonly"   />
	             </td>
	             <td class="content-title"><s:text name="userInfo.mateRace"/></td>
	             <td><input type="text" disabled="disabled" name="mateNation"  value="${mateNation}" maxlength="15" id="mateNation"/></td>
	         </tr>						         
	         <tr>
	             <td class="content-title"><s:text name="userInfo.mateWorkPlace"/></td>
	             <td><input type="text" disabled="disabled" name="mateWorkPlace"  value="${mateWorkPlace}" maxlength="15" id="mateWorkPlace"/></td>
	             <td class="content-title"><s:text name="userInfo.mateAddress"/></td>
	             <td><input type="text" disabled="disabled" name="mateAddress"  value="${mateAddress}" maxlength="15" id="mateAddress"/></td>
	         </tr>
	         <tr>
	             <td class="content-title"> <s:text name="userInfo.matePostCode"/></td>
	             <td><input type="text" disabled="disabled" name="matePostCode"  value="${matePostCode}" maxlength="15" id="matePostCode"/></td>
	             <td class="content-title"><s:text name="userInfo.mateTelephone"/></td>
	             <td><input type="text" disabled="disabled" name="mateTelephone"  value="${mateTelephone}" maxlength="15" id="mateTelephone"/></td>
	         </tr>   
	         <tr>
	             <td class="content-title"><s:text name="userInfo.fatherName"/></td>
	             <td><input type="text" disabled="disabled" name="fatherName"  value="${fatherName}" maxlength="10" id="fatherName"/></td>
	             <td class="content-title"><s:text name="userInfo.motherName"/></td>
	             <td><input type="text" disabled="disabled" name="motherName"  value="${motherName}" maxlength="10" id="motherName"/></td>
	         </tr>						         
	         <tr>
	             <td class="content-title"><s:text name="userInfo.FMAddress"/></td>
	             <td><input type="text" disabled="disabled" name="parentAddress"  value="${parentAddress}" maxlength="15" id="FMAddress"/></td>
	            <td class="content-title"><s:text name="userInfo.FMPostCode"/></td>
	            <td><input  type="text" disabled="disabled" name=parentPostCode  value="${parentPostCode}" maxlength="10" id="FMPostCode"/></td>
	         </tr> 
             </s:if>
             <s:else>
	         <tr>
	             <td class="content-title"> <s:text name="user.race"/></td>
	             <td><input type="text"  name="nation"  maxlength="15" value="${nation}" id="race"/></td>
	             <td class="content-title"> <s:text name="userInfo.polityVisage"/></td>
	             <td><input type="text"  name="politicalStatus" maxlength="15" value="${politicalStatus}" id="polityVisage"/></td>
	         </tr> 
	         <tr>
	             <td class="content-title"><s:text name="userInfo.weight"/>(kg) </td>
	             <td><input  type="text"  name="bodyWeight" maxlength="15" value="${bodyWeight}" id="weight"/></td>
	             <td class="content-title"><s:text name="userInfo.high"/>(cm) </td>
	             <td><input  type="text"   name="height" maxlength="15" value="${height}" id="high"/></td>
	         </tr>  
	         <tr>
	             <td class="content-title"><s:text name="user.birthday"/></td>
	             <td>
	                 <input type="text"  name="birthday" id="birthday"  value="${birthday}"  readonly="readonly"   />
	             </td>
	             <td class="content-title"><s:text name="userInfo.iDcard"/></td>
	             <td><input type="text"   name="idCardNumber" maxlength="20" value="${idCardNumber}" id="IDcard"/></td>
	         </tr> 
	          <tr>
	             <td class="content-title"><s:text name="userInfo.hireDate"/></td>
	             <td>
	                 <input type="text"  name="hireDate" id="hireDate"  value="${hireDate}" readonly="readonly"   />
	             </td>
	             <td class="content-title"><s:text name="userInfo.treatment"/> </td>
	             <td><input type="text"  name="treatment"  maxlength="20" value="${treatment}" id="treatment"/></td>
	         </tr>
	         <tr>
	             <td class="content-title"> <s:text name="userInfo.marriageStates"/></td>
	             <td><input type="text"  name="maritalStatus"  maxlength="10" value="${maritalStatus}" id="marriageStates"/></td>
	             <td class="content-title"><s:text name="userInfo.schoolRecord"/></td>
	             <td><input type="text"  name="educationGrade"  maxlength="20" value="${educationGrade}" id="schoolRecord"/></td>
	         </tr>
	         <tr>
	             <td class="content-title"><s:text name="userInfo.school"/></td>
	             <td><input type="text"  name="graduatedSchool"  maxlength="20" value="${graduatedSchool}" id="school"/></td>
	             <td class="content-title"><s:text name="userInfo.speciality"/></td>
	             <td><input type="text"  name="major"  maxlength="15" value="${major}" id="speciality"/></td>
	         </tr>
	         <tr>
	             <td class="content-title"> <s:text name="userInfo.degree"/></td>
	             <td><input type="text"  name="degree"  maxlength="15" value="${degree}" id="degree"/></td>
	             <td class="content-title"><s:text name="userInfo.graduateDate"/></td>
	             <td>
	                 <input type="text"  name="graduatedDate"  id="graduateDate"  value="${graduatedDate}"  readonly="readonly"   />
	             </td>
	         </tr>      
	         <tr>
	             <td class="content-title"><s:text name="userInfo.firstLanguage"/></td>
	             <td><input type="text"  name="firstForeignLanguage"  maxlength="10" value="${firstForeignLanguage}" id="FirstLanguage"/></td>
	             <td class="content-title"><s:text name="userInfo.skilldegree"/></td>
	             <td><input type="text"  name="skilledDegree"  maxlength="15" value="${skilledDegree}" id="skilldegree"/></td>
	         </tr>
	         <tr>
	             <td class="content-title"><s:text name="userInfo.secondLange"/></td>
	             <td><input type="text"  name="secondForeignLanguage" maxlength="10"  value="${secondForeignLanguage}" id="SecondLange"/></td>
	             <td class="content-title"><s:text name="userInfo.bloodType"/></td>
	             <td><input type="text" name="bloodGroup"  maxlength="6" value="${bloodGroup}" id="bloodType"/></td>
	         </tr>     
	         <tr>
	             <td class="content-title"> <s:text name="userInfo.homeAddress"/></td>
	             <td><input type="text" name="homeAddress" maxlength="15"  value="${homeAddress}" id="homeAddress"/></td>
	             <td class="content-title"><s:text name="userInfo.homePostCode"/></td>
	             <td><input  type="text"  name="homePostCode"  maxlength="10" value="${homePostCode}" id="homePostCode"/></td>
	         </tr>
	         <tr>
	             <td class="content-title"> <s:text name="userInfo.cityArea"/></td>
	             <td><input type="text" name="cityArea"  maxlength="15" value="${cityArea}" /></td>
	             <td class="content-title"><s:text name="userInfo.interest"/></td>
	             <td><input type="text" name="interest"  maxlength="15" value="${interest}" id="interest"/></td>
	         </tr>
	         <tr>
	             <td class="content-title"><s:text name="userInfo.marriageDate"/></td>
	             <td>
	                 <input type="text" name="marriageDate" id="marriageDate"  value="${marriageDate}"  readonly="readonly"   />
	             </td>
	             <td class="content-title"><s:text name="userInfo.mateName"/></td>
	             <td><input type="text" name="mateName" maxlength="10"  value="${mateName}" id="mateName"/></td>
	         </tr>
	         <tr>
	             <td class="content-title"><s:text name="userInfo.mateBirthday"/></td>
	             <td>
	                 <input type="text" name="mateBirthday" id="mateBirthday"  value="${mateBirthday}"  readonly="readonly"   />
	             </td>
	             <td class="content-title"><s:text name="userInfo.mateRace"/></td>
	             <td><input type="text" name="mateNation"  value="${mateNation}" maxlength="15" id="mateRace"/></td>
	         </tr>						         
	         <tr>
	             <td class="content-title"><s:text name="userInfo.mateWorkPlace"/></td>
	             <td><input type="text" name="mateWorkPlace"  value="${mateWorkPlace}" maxlength="15" id="mateWorkPlace"/></td>
	             <td class="content-title"><s:text name="userInfo.mateAddress"/></td>
	             <td><input type="text" name="mateAddress"  value="${mateAddress}" maxlength="15" id="mateAddress"/></td>
	         </tr>
	         <tr>
	             <td class="content-title"> <s:text name="userInfo.matePostCode"/></td>
	             <td><input type="text" name="matePostCode"  value="${matePostCode}" maxlength="15" id="matePostCode"/></td>
	             <td class="content-title"><s:text name="userInfo.mateTelephone"/></td>
	             <td><input type="text" name="mateTelephone"  value="${mateTelephone}" maxlength="15" id="mateTelephone" onkeyup="value=this.value.replace(/[^-,0-9]/g,'');"/></td>
	         </tr>   
	         <tr>
	             <td class="content-title"><s:text name="userInfo.fatherName"/></td>
	             <td><input type="text" name="fatherName"  value="${fatherName}" maxlength="10" id="fatherName"/></td>
	             <td class="content-title"><s:text name="userInfo.motherName"/></td>
	             <td><input type="text" name="motherName"  value="${motherName}" maxlength="10" id="motherName"/></td>
	         </tr>						         
	         <tr>
	             <td class="content-title"><s:text name="userInfo.FMAddress"/></td>
	             <td><input type="text" name="parentAddress"  value="${parentAddress}" maxlength="15" id="FMAddress"/></td>
	            <td class="content-title"><s:text name="userInfo.FMPostCode"/></td>
	            <td><input  type="text" name="parentPostCode"  value="${parentPostCode}" maxlength="10" id="FMPostCode"/></td>
	         </tr> 
	         </s:else>
	         </table>
		</form>
	</div>
	<script type="text/javascript">
		$().ready(function(){
			initDate("birthday");
			initDate("hireDate");
			initDate("graduateDate");
			initDate("marriageDate");
			initDate("mateBirthday");
		});
	</script>
</aa:zone>
</div>
</body>
</html>
