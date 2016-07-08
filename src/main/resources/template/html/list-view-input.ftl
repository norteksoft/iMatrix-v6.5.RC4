<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<title>${fileName}</title>
	<script type="text/javascript" src="../../js/jquery-latest.js"></script>
	<script type="text/javascript" src="../../js/jquery.layout-latest.js"></script>	
	<script type="text/javascript" src="../../js/jquery-ui-latest.js"></script>
	<script type="text/javascript" src="../../js/jquery.layout.resizePaneAccordions-1.0.js"></script>
	<script type="text/javascript" src="../../js/common-layout.js"></script>	
	<script src="../../widgets/colorbox/jquery.colorbox.js" type="text/javascript"></script>
	<script src="../../widgets/jqgrid/grid.locale-en.js" type="text/javascript"></script>
	<script src="../../widgets/jqgrid/jquery.jqGrid.src.js" type="text/javascript"></script>
	<script type="text/javascript" src="../../widgets/jstree/jquery.jstree.js"></script>
	<link type="text/css" rel="stylesheet" href="../../widgets/jqgrid/ui.jqgrid.css" />
	<link type="text/css" rel="stylesheet" href="../../css/sky-blue/jquery-ui-1.8.16.custom.css" id="_style"/>
	<script type="text/javascript" src="../../js/common.js"></script>
	<script type="text/javascript" src="../../widgets/timepicker/timepicker-all-1.0.js"></script>
	
	<script type="text/javascript">
		$(document).ready(function(){
			<#list dateList as dateEve>
				$('#${dateEve}').datepicker({
					"dateFormat":'yy-mm-dd',
					changeMonth:true,
					changeYear:true,
					showButtonPanel:"true"
				});
			</#list>
		});
		function fanhui(){
			<#if  "refresh"?contains(inputShowType)  >
				window.location = "../${fileName}/${fileName}.html";
			</#if>
			
			<#if  inputShowType?contains("popup")>
				window.parent.$.colorbox.close();
			</#if>
		}

		
		
		
	</script>
</head>
<body>
	<div class="ui-layout-center"  style="background:#F0FFFF">
		<div class="opt-body"  style="background:#F0FFFF;overflow:auto;">
			<div class="opt-btn">
				<button class='btn' onclick="alert('保存')"><span><span>保存</span></span></button>
				<button class='btn' onclick="fanhui();"><span><span>返回</span></span></button>	
				 
			</div>
				
			<div class="widget-content"  style="background:#F0FFFF">
				<div class="contentleadTable"  style="background:#F0FFFF" >

					<table    rules=all  <#if "refresh"?contains(inputShowType)> style="border: #87CEEB 1px solid;" </#if>  <#if  "popup"?contains(inputShowType)  > style="border: #87CEEB 1px solid;"  </#if>   >
						<tbody id="appendNeed" >
							
								<#if valueList?exists>
									<#list valueList as value0>
										<tr>
											<#list value0 as value00>
												 ${value00} 
											</#list>
										</tr>
									</#list>
								</#if>
							
						</tbody>
					</table>
					
				</div>
				<br/>
				<br/>
				<br/>
				<br/>
				<br/>
				<div>
					<span style="color: red">
	 					注释：${fileName}
					</span>
				</div>
				<div style="height: 10px;"></div>
				<div>
					<span style="color: blue">
					</span>
				</div>
			</div>
			</div>
		</div>
	</div>
</body>
</html>