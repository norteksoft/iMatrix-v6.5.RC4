<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html lang="en">
	<head>
		<title>Jwebap Console</title>
		<link rel="stylesheet" type="text/css" href="../resources/css/ext-all.css" />
		<link rel="stylesheet" type="text/css" href="../resources/css/collapser.css"></link>
		<link rel="stylesheet" type="text/css" href="../resources/css/docs.css"></link>
		
		<!-- GC -->
		<style type="text/css">
		html, body {
	        margin:0;
	        padding:0;
	        border:0 none;
	        overflow:hidden;
	        height:100%;
	    }
		</style>
	</head>
	<body scroll="no" id="docs">
		<div id="loading-mask" style="width:100%;height:100%;background:#c3daf9;position:absolute;z-index:20000;left:0;top:0;">
			&#160;
		</div>
		<div id="loading">
			<div class="loading-indicator">
				<img src="../resources/images/console/loading.gif" style="width:16px;height:16px;" align="absmiddle">
				&#160;Loading...
			</div>
		</div>
		<!-- include everything after the loading indicator -->
		<script type="text/javascript" src="../resources/js/ext-base.js"></script>
		<script type="text/javascript" src="../resources/js/ext-all.js"></script>
		<script type="text/javascript" src="../resources/js/classes/TagFrame.js"></script>
		<script type="text/javascript" src="../resources/js/console.js"></script>

		<div id="header">
			<div style="padding-top:3px;">
				Jwebap Console
			</div>
		</div>

		<div id="classes">
			<!-- BEGIN TREE -->
			<a id="welcome-link" href="">Jwebap Home</a>
			<div class="pkg"><h3>Component View</h3>
				<div class="pkg-body">
					<div class="pkg"><h3>Jdbc Component</h3>
						<div class="pkg-body">
							<a id='default' href="jdbc/tracer">Jdbc Traces</a>
						</div>
					</div>
					<div class="pkg"><h3>Http Component</h3>
						<div class="pkg-body">
							<a href="http/tracer">Http Traces</a>
							<a href="http/stat">Http Stat</a>	
						</div>
					</div>
					<div class="pkg"><h3>Method Component</h3>
						<div class="pkg-body">
							<a href="method/tracer">Method Traces</a>
							<a href="method/stat">Method Stat</a>	
						</div>
					</div>
				</div>
			</div>	
			<div class="pkg"><h3>Deploy</h3>
				<div class="pkg-body">
					<a href="deploy/plugins">Plugins</a>
				</div>
			</div>
			<a id="help-forums" href="http://sourceforge.net/forum/?group_id=210919">Help Forums</a>

			<!-- END TREE -->
		</div>

	</body>
</html>
