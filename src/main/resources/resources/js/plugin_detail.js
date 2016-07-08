
ComponentListView = {
	            init : function(){
						ds =new Ext.data.JsonStore({
							url: 'components/list',
							root: 'components',
    						fields:  [
    							{name: 'name',mapping: 'name'},
    							{name: 'type',mapping: 'type'}
							],
							remoteSort: true
						});
						this.components=ds;
						var pluginName =document.getElementById('pluginName').value;
						ds.load({
							params :{pluginName:pluginName}
						});
						
						//ColumnModels
						var colModel =  new Ext.grid.ColumnModel([{
						   header: "name",
						   dataIndex: 'name',
						   renderer: function(value){
						   		var html="<a href=\"components/detail?pluginName="+pluginName+"&componentName="+value+"\" >"+value+"</a>";
								return html;
						   },
						   width: 150
						},{
						   header: "class",
						   dataIndex: 'type',
						   width: 500 
						},{
						   header: "",
						   dataIndex: '',
						   width: 50 
						}
						
						]);

						var grid = new Ext.grid.Grid('components', {
							ds: ds,
							cm: colModel,
							loadMask: true
						});
						
						grid.render();	
	           }
	           
	};

Ext.onReady(ComponentListView.init, ComponentListView, true);

//显示component列表名称栏
function showNameColumn(value){
	
}
