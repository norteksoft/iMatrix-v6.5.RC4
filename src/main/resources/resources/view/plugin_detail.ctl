<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html lang="en">
	<head>
		<title>Jwebap Console</title>
		<link rel="stylesheet" type="text/css" href="../../../resources/css/ext-all.css" />
		<link rel="stylesheet" type="text/css" href="../../../resources/css/deploy.css" />
		<!-- GC -->
		<style type="text/css">
		DIV.components {
			PADDING-LEFT: 20pt;
			MARGIN: 4pt;
			WIDTH: 700px;
		}
		
		</style>
	</head>
	<body  id="docs">
		<!-- include everything after the loading indicator -->
		<script type="text/javascript" src="../../../resources/js/ext-base.js"></script>
		<script type="text/javascript" src="../../../resources/js/ext-all.js"></script>
		<script type="text/javascript" src="../../../resources/js/plugin_detail.js"></script>
		<div class="block">
		<div id="top">
			<div id="note" class="content">
			<p class="content">
			The page show all the components of this plugin .You can define the component configuration of these component in following table .
			</p>
			
			<p class="h1">
			Deployment for Components:
			</p>
			</div>
			<input type="hidden" id='pluginName' value='${pluginName}' />
		</div>
		<div id="center" >
			<div id="components" class="components" ></div>
			<div class="x-form-button">
				<input type="button" class='buttons' value=" Back " onclick="javascript:window.location='../plugins';">&nbsp;&nbsp;
			</div>
		</div>
		</div>
	</body>
</html>