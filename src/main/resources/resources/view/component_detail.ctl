<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html lang="en">
	<head>
		<title>Jwebap Console</title>
		<link rel="stylesheet" type="text/css" href="../../../../resources/css/ext-all.css" />
		<link rel="stylesheet" type="text/css" href="../../../../resources/css/deploy.css" />
		<!-- GC -->
		<style type="text/css">
	
		</style>
	</head>
	<body id="docs">
		<!-- include everything after the loading indicator -->
		<script type="text/javascript" src="../../../../resources/js/ext-base.js"></script>
		<script type="text/javascript" src="../../../../resources/js/ext-all.js"></script>
		<div class="block">
			<div id="top">
				<div id="note" class="content">
				<p class="content">
				This page allows you to define the general configuration of this Jwebap component. 
				</p>
				</div>
			</div> 
			<div id="center" >
				<div class='form'  >
					<form action='save' method='POST'  >
						<input type="hidden" name="componentName"  value='${name}'/>
						<input type="hidden" name="pluginName"  value='${pluginName}'/>
						<div class="x-form-item">
	                        <label class="x-form-label">Name:</label>
	                        <div class="x-form-element">
	                        ${name}
	                        </div>
	                    </div>
	                    <p class="content">
						The name of this jwebap component
						</p>
	                    <div class="x-form-item">
	                        <label class="x-form-label">Class:</label>
	                        <div class="x-form-element">
	                        ${class}
	                        </div>
	                    </div>
	                    <p class="content">
						The class of this jwebap component
						</p>
						$for{param:params}
						<div class="x-form-item">
	                        <label class="x-form-label">${param.name}:</label>
	                        <div class="x-form-element">
	                        $if{param.style=="longtext"}
	                        	<textarea rows="3" cols="50" name="${param.name}" >${param.value}</textarea>
	                        $else
	                           <input type="text" size="30" name="${param.name}"  value='${param.value}'/>
	                        $end
	                        </div>
	                    </div>
	                    <p class="content">
						${param.description}
						</p>
						$end
						
						
						<div class="x-form-button">
							<input type="button" class='buttons' value=" Back " onclick="javascript:window.location='../../plugins';">&nbsp;
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