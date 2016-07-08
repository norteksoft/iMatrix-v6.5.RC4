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
	
	<script type="text/javascript">
		function new01(){// popup或refresh
			<#if  "refresh"?contains(inputShowType)  >
				window.location = "../${fileName}/${fileName}-input.html";
			</#if>
			
			<#if  inputShowType?contains("popup")>
				_open("../${fileName}/${fileName}-input.html", 500, 600, "${fileName}");
			</#if>
			
		}
		
		function see01(){
			<#if  "refresh"?contains(inputShowType)  >
				window.location = "../${fileName}/${fileName}-see.html";
			</#if>
			
			<#if  inputShowType?contains("popup")>
				_open("../${fileName}/${fileName}-see.html", 500, 600, "${fileName}");
			</#if>
		}
	</script>
</head>
<body>
	<div class="ui-layout-center">
		<div class="opt-body" style="overflow:auto;"  >
			<div class="opt-btn">
				<button class='btn' onclick="new01();"><span><span>新建</span></span></button>
				<button class='btn' onclick="new01();"><span><span>修改</span></span></button>	
				<button class='btn' onclick="alert('删除')"><span><span>删除</span></span></button>	
				<button class='btn' onclick="see01();"><span><span>查看</span></span></button>	
			</div>
				
			<div class="widget-content">
				<div class="contentleadTable" >
					<table class="leadTable" style="width: 800px ; " >
						<thead>
							<tr>
								<th><input onclick="checkAll();" id="check1" type="checkbox"/></th>
						
								<#if nameList?exists>
									<#list nameList as name0>
										${name0}
									</#list>
								</#if>
								
							</tr>
						</thead>
						
						<tbody>
							<#if valueList?exists>
								<#list valueList as value0>
										<tr>
											<td><input onclick="checkAll();" id="check1" type="checkbox"/></td>
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