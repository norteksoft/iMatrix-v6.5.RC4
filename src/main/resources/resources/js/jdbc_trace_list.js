
HttpTracer = {
	            init : function(){
					var ds =new Ext.data.JsonStore({
							url: 'tracer/datas',
							root: 'traces',
							totalProperty: "totalCount",
    						fields:  [
    							{name: 'thread',mapping: 'thread'},
    							{name: 'isClosed',mapping: 'isClosed'},
								{name: 'createTime', mapping: 'createTime'},
								{name: 'destoryTime', mapping: 'destoryTime'},
								{name: 'cost', mapping: 'cost',type: 'int'},
								{name: 'detail', mapping: 'detail'},
								{name: 'sqls', mapping: 'sqls'}
							],
							remoteSort: true
						});

						//ColumnModels
						var colModel =  new Ext.grid.ColumnModel([{ 
						   header: "Thread",
						   dataIndex: 'thread',
						   width: 200,   
						   renderer: renderRed,
						   css: 'white-space:normal;'
						},{
						   header: "Create",
						   dataIndex: 'createTime',
						   renderer: renderRed,
						   sortable: true,
						   width: 140 
						},{
						   header: "Destory",
						   dataIndex: 'destoryTime',
						   renderer: renderRed,
						   width: 140 
						},{
						   header: "State",
						   dataIndex: 'isClosed',
						   renderer: renderRed,
						   sortable: true,
						   width: 100
						},{
						   header: "Cost(ms)",
						   dataIndex: 'cost',
						   renderer: renderRed,
						   sortable: true,
						   width: 100
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
					    		
						layout.addDetailPanel('detail', {title:'Thread Stack',bindColumn:'detail',fitToFrame:true});
						layout.addDetailPanel('sqls', {title:'SQLs',bindColumn:'sqls',fitToFrame:true});
						
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

