
PluginDeploy = {
				plugins	: null,
	            init : function(){
						ds =new Ext.data.JsonStore({
							url: 'plugins/list',
							root: 'plugins',
    						fields:  [
    							{name: 'name',mapping: 'name'},
    							{name: 'path',mapping: 'path'}
							],
							remoteSort: true
						});
						this.plugins=ds;
						ds.load();
						
						//ColumnModels
						var colModel =  new Ext.grid.ColumnModel([{
						   header: "name",
						   dataIndex: 'name',
						   renderer: showNameColumn,
						   width: 150
						},{
						   header: "path",
						   dataIndex: 'path',
						   width: 500 
						},{
						   header: "",
						   dataIndex: '',
						   renderer: showOperColumn,
						   width: 50 
						}
						
						]);

						var grid = new Ext.grid.Grid('plugins', {
							ds: ds,
							cm: colModel,
							loadMask: true
						});
						
						grid.render();	
	           },
	           //删除插件
				removePlugin : function(pluginName){
					var conn=new Ext.data.Connection({
					        method:'POST',
					        timeout:10000,
					        url:'plugins/remove'
					     });
					var ds=PluginDeploy.plugins;
					     
					conn.request({
							params :{pluginName:pluginName},
					        callback:function(){
					        	ds.reload();
					        }
					        
					    });
								           
				}
	           
	};

Ext.onReady(PluginDeploy.init, PluginDeploy, true);
//显示plugin列表操作栏
function showOperColumn(value,metadata,record){
	var pluginName=record.get("name");
	var html="<a href=\"javascript:PluginDeploy.removePlugin('"+pluginName+"');\" ><img src=\"../../resources/images/deploy/delete.gif\" /></a>";
	return html;
}

//显示plugin列表名称栏
function showNameColumn(value){
	var html="<a href=\"plugins/detail?pluginName="+value+"\" >"+value+"</a>";
	return html;
}
