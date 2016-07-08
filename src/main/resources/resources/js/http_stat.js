
HttpStat = {
	            init : function(){
					var ds =new Ext.data.JsonStore({
							url: 'stat/datas',
							root: 'stat',
							totalProperty: "totalCount",
    						fields:  [
    							{name: 'uri',mapping: 'key'},
    							{name: 'total',mapping: 'frequency'},
								{name: 'createTime',mapping: 'createTime'},
								{name: 'last', mapping: 'last'},
								{name: 'min', mapping: 'min'},
								{name: 'max', mapping: 'max'},
								{name: 'average', mapping: 'average'}
							],
							remoteSort: true
						});

						//ColumnModels
						var colModel =  new Ext.grid.ColumnModel([{
						   header: "URI",
						   dataIndex: 'uri',
						   width: 300
						},{
						   header: "Create",
						   dataIndex: 'createTime',
						   width: 115 
						},{
						   header: "Last",
						   dataIndex: 'last',
						   width: 115 
						},{
						   header: "Min(ms)",
						   dataIndex: 'min',
						   sortable: true,
						   width: 80
						},{
						   header: "Max(ms)",
						   dataIndex: 'max',
						   sortable: true,
						   width: 80
						},{
						   header: "Average(ms)",
						   dataIndex: 'average',
						   sortable: true,
						   width: 80
						},{
						   header: "Total",
						   dataIndex: 'total',
						   sortable: true,
						   width: 60
						}
						
						]);

						var grid = new Ext.grid.Grid('grid', {
							ds: ds,
							cm: colModel,
							loadMask: true
						});
						
						var layout=new InfoGridLayout({
										grid : grid,
										paging : true,
										pageSize: 20,
										displayMsg:'Displaying {0} - {1} of {2}',
										emptyMsg: "No datas to display"
									});
						layout.render();
									
						
	           }
	};

Ext.onReady(HttpStat.init, HttpStat, true);


