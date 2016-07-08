
HttpTracer = {
	            init : function(){
					var ds =new Ext.data.JsonStore({
							url: 'tracer/datas',
							root: 'traces',
							totalProperty: "totalCount",
    						fields:  [
    							{name: 'isClosed',mapping: 'isClosed'},
								{name: 'ip',mapping: 'ip'},
								{name: 'uri', mapping: 'uri'},
								{name: 'createTime', mapping: 'createTime'},
								{name: 'destoryTime', mapping: 'destoryTime'},
								{name: 'jdbcOpened', mapping: 'jdbcOpened'},
								{name: 'cost', mapping: 'cost',type: 'int'},
								{name: 'detail', mapping: 'detail'}
							],
							remoteSort: true
						});

						//ColumnModels
						var colModel =  new Ext.grid.ColumnModel([{ 
						   header: "Ip",
						   dataIndex: 'ip',
						   width: 70,   
						   renderer: renderRed,
						   css: 'white-space:normal;'
						},{
						   header: "URI",
						   dataIndex: 'uri',
						   renderer: renderRed,
						   width: 300
						},{
						   header: "Create",
						   dataIndex: 'createTime',
						   renderer: renderRed,
						   sortable: true,
						   width: 115 
						},{
						   header: "Destory",
						   dataIndex: 'destoryTime',
						   renderer: renderRed,
						   width: 115 
						},{
						   header: "Jdbc-Opened",
						   dataIndex: 'jdbcOpened',
						   renderer: renderRed,
						   width: 80
						},{
						   header: "State",
						   dataIndex: 'isClosed',
						   renderer: renderRed,
						   sortable: true,
						   width: 60
						},{
						   header: "Cost(ms)",
						   dataIndex: 'cost',
						   renderer: renderRed,
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
									
						var toolBar=layout.getToolBar(true);
						toolBar.add('-', {
					        pressed: true,
					        enableToggle:true,
					        text: 'Clear Trace',
					        cls: 'x-btn button',
					        toggleHandler: clearDatas
					    });	
					    		
						layout.addDetailPanel('detail', {bindColumn:'detail',fitToFrame:true});
						
						function clearDatas(btn, pressed){
							var conn=new Ext.data.Connection({
							        method:'POST',
							        timeout:10000,
							        url:'tracer/clear'
							     });
							     
							conn.request({
							        callback:function(){
							        	ds.reload();
							        }
							        
							    });
				           
						}
	           }
	};

Ext.onReady(HttpTracer.init, HttpTracer, true);

function renderRed(value, cell, record,rowindex){
		var isClosed=record.get('isClosed');
		if(isClosed=='opened'){
			cell.attr='style="background:#ffcccc"';
		}
		return value;
}

