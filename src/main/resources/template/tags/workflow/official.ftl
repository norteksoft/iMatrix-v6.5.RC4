<form id="officeForm1" name="officeForm1" action="" method="post">
	<input type="hidden" id="workflowId" name="workflowId" value="${workflowId}">
	<input type="hidden" id="taskId" name="taskId" value="#{taskId}">
</form>
<table class="Table" >
	<thead>
		<tr>
			<th width="20%">${fileName}</th><th>${fileSize}</th><th >${fileType}</th><th >${uploadDate}</th><th>${uplaodPerson}</th><th>${uploadTache}</th><th >${uploadOperation}</th>
		</tr>
	</thead>
	<tbody>
	<#if offices?exists>
      <#list offices as being>
         <tr id="${being.id}">
           <td  align="center">
           		<a href="#" onclick="openDocument('${being.fileType }','${being.workflowId}','#{taskId}','#{(being.id) }')">${being.fileName}</a>
           </td>
           <td align="center">${being.fileSize/1000}</td>
           <td align="center">${being.fileType}</td>
           <td align="center">${being.createdTime?string("yyyy-MM-dd HH:mm")}</td>
           <td align="center">${being.creatorName }</td>
           	<td>${being.taskName}</td>
           <td  align="center">
           <#if deleteRight>
           	<a name="#officeList#{being.id}" href="#officeList#{being.id}" onclick="deleteText(#{(being.id)})">${uploadDelete}</a>
           </#if>
           </td>
         </tr>
      </#list>
  </#if>
</tbody>
</table>
<#if createRight>
	<input type="button" value="${createWord}" onclick="openDocument('.doc','${workflowId}','#{taskId}')"/>
	<input type="button" value="${createExcel}" onclick="openDocument('.xls','${workflowId}','#{taskId}');"/>
	<input type="button" value="${createWpstext}" onclick="openDocument('.wps','${workflowId}','#{taskId}')"/>
	<input type="button" value="${createWpsform}" onclick="openDocument('.et','${workflowId}','#{taskId}')"/>
	<input type="button" value="${createPdf}" onclick="openDocument('pdf','${workflowId}','#{taskId}');"/>
	
	<input type="button" value="${uploadFile}" onclick='openUploadDocument(#{taskId},"${workflowId}","openUpload");' id="openUpload"/>
</#if>
