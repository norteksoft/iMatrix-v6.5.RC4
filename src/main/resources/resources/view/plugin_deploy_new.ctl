<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html lang="en">
	<head>
		<title>Jwebap Console</title>
		<link rel="stylesheet" type="text/css" href="../../../resources/css/ext-all.css" />
		<link rel="stylesheet" type="text/css" href="../../../resources/css/deploy.css" />
		<!-- GC -->
		<style type="text/css">
	
		</style>
	</head>
	<body id="docs">
		<!-- include everything after the loading indicator -->
		<script type="text/javascript" src="../../../resources/js/ext-base.js"></script>
		<script type="text/javascript" src="../../../resources/js/ext-all.js"></script>
		<script type="text/javascript" src="../../../resources/js/PluginNew.js"></script>
		<div class="block">
			<div id="top">
				$if{errMsg}
					<div  class="content">
					<p class="err">
					error: ${errMsg}
					</p>
					</div>
				$end
				<div id="note" class="content">
				<p class="content">
				This page allow you create and deploy a new plugin. Fill the plugin jar file path and plugin name in the blank. The path can use absolute path,
				or use relative path which start with '&amp;{ABSOLUTE_PATH}',like '&amp;{ABSOLUTE_PATH}/tracer.jar'.Jwebap will find the plugin jar file in the classpath.
				</p>
				</div>
			</div> 
			<div id="center" >
				<div class='form'  >
					<form id='pluginAddForm' action='add' method='POST'  target='_self' >
						<div class="x-form-item">
	                        <label class="x-form-label">Plugin Name:</label>
	                        <div class="x-form-element">
	                            <input type="text" size="20" name="pluginName"  value='${pluginName}' />
	                        </div>
	                    </div>
	                    <p class="content">
						The name of this jwebap plugin.
						</p>
	                    <div class="x-form-item">
	                        <label class="x-form-label">Path:</label>
	                        <div class="x-form-element">
	                            <input type="text" size="60" name="path"  value='${path}'/>
	                        </div>
	                    </div>
	                    <p class="content">
						The path of this plugin.The path can use absolute path,or use relative path which start with '&amp;{ABSOLUTE_PATH}',like '${ABSOLUTE_PATH}/tracer.jar'.Jwebap will find the plugin jar file in the classpath.
						</p>
						<div class="x-form-button">
							<input type="button" class='buttons' value=" Back " onclick="javascript:window.location='../plugins';">&nbsp;
							<input type="submit" class='buttons' value=" Save ">
						</div>
					</form>
					
				</div>
				<div>
				
				</div>
			</div>
		</div>
	</body>
</html>