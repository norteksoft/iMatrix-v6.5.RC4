<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html lang="en">
	<head>
		<title>Jwebap Console</title>
		<link rel="stylesheet" type="text/css" href="../../resources/css/ext-all.css" />
		<link rel="stylesheet" type="text/css" href="../../resources/css/deploy.css" />
		<!-- GC -->
		<style type="text/css">
		DIV.plugins {
			PADDING-RIGHT: 4pt;
			PADDING-LEFT: 4pt;
			PADDING-BOTTOM: 4pt;
			PADDING-TOP: 4pt;
			MARGIN: 4pt;
			WIDTH: 700px;
		}
		
		</style>
	</head>
	<body  id="docs">
		<!-- include everything after the loading indicator -->
		<script type="text/javascript" src="../../resources/js/ext-base.js"></script>
		<script type="text/javascript" src="../../resources/js/ext-all.js"></script>
		<script type="text/javascript" src="../../resources/js/plugin_list.js"></script>
		<div class="block">
		<div id="top">
			<div id="note" class="content">
			<p class="content">
			A Jwebap plugin  is a JAR file with plugin.xml in /META-INF. A Jwebap plugin includes some usesful functions about profiling, debug, tesing etc. And  
			provide some page view in Jwebap Web Console to show statistic data.
			</p>
			
			<p class="content">
			You can deploy one or more plugins in Jwebap.Once Jwebap startup, all plugins will initialize.This page displays key information about these plugins .
			To deployed A plugin ,click the Deploy a new plugin...
			</p>
			<p class="note">
			Note : after one or more plugins are configured , please reboot you application. 
			</p>
			<p class="link">
			<a href="plugins/new" ><img src="../../resources/images/deploy/create.gif" />Deploy a new plugin...</a>
			</p>
			</div>
			
		</div>
		<div id="center" >
			<div class="plugins" >
				<div id="plugins"></div>
			</div>
		</div>
		</div>
	</body>
</html>