
/*
* 信息列表布局类，支持通过子窗口显示更详细的列表信息
* 
* @param grid 	InfoGridLayout代理的Grid
* @param config	InfoGridLayout的配置:
				grid		:	表格对象
				layout		:	布局对象，如果为空，那么默认构造layout
				paging 		: 	true/false 是否分页，默认为false
				pageSize 	: 	分页大小，默认为20
				displayMsg	: 	分页显示信息
				emptyMsg	: 	分页空数据信息
				
* @see Ext.grid.Grid
*/
InfoGridLayout = function (config) {
    /**
	* 表格对象
	* @see Ext.grid.Grid
	*/
    this.grid = config.grid;
    
    /**
	* layout
	*/
    this.layout = config.layout;
    
    /**
	* InfoGridLayout配置对象
	*/
    this.config = config;
	
	/**
	* 列表工具栏
	*/
    this.toolBar = null;
    
	/**
	* 详细信息布局默认title
	*/
	this.defaultDetailTitle='More Infomation';	
	
	//初始化
	this.init = function (config){
		if(this.layout==null){
			this.layout = new Ext.BorderLayout(document.body, {
										center: {
											resizeTabs: true,
											autoScroll: true										
										}
									});
		}

	};
	
	//展现布局
    this.render = function () {
    	this.layout.beginUpdate();
    	
    	this.layout.add('center', new Ext.GridPanel(this.grid));
    	var ds=this.grid.getDataSource();
        this.grid.render();
        
        if(ds!==null){
        	ds.load({params:{start:0, limit:20}});
        }
        //显示分页栏
        var paging=config.paging;
		
		if(paging){
			this.setToolBar(null,paging);				
		}
		
        this.grid.getSelectionModel().selectFirstRow();
        this.layout.endUpdate();
    };
	
	//返回ToolBar
    this.getToolBar = function (isCreate) {
    	if(this.toolBar==null && isCreate){
    		this.setToolBar();
    	}
    	return this.toolBar;
    	
    };
	
	/**
	* 增加详细信息窗口
	* @param el Ext.ContentPanel的container
	* @param config 同Ext.ContentPanel的config,多出bindColumn属性，代表详细窗口邦定的数据字段
	*/
    this.addDetailPanel = function (el,config) {
    	this.layout.addRegion('south',{
										title: this.defaultDetailTitle,
										split:true,
										initialSize: 200,
										titlebar: true,
										collapsible: true,
										autoScroll: true,
										animate: true,
										minSize: 100,
										maxSize: 400,
										resizeTabs: true
									});
    
    	var panel= new Ext.ContentPanel(el, config);
    	this.layout.add('south',panel);
    	if(config.bindColumn){
    		this.bindDetailInfo(panel,config.bindColumn);
    	}
    	
    };
    
    this.bindDetailInfo = function (panel,mapping){
    	this.grid.on("rowclick",function(grid,rowIndex,e){
							var stoge=grid.getDataSource();
							var record=stoge.getAt(rowIndex);
							panel.setContent(record.get(mapping));
							
						});
    };
    
    //设置工具栏
    this.setToolBar = function(container,paging) {
    	if(container==null){
    		container = this.grid.getView().getFooterPanel(true);
    	}
    	if(paging){
    		var ds=this.grid.getDataSource();
    		this.toolBar = new Ext.PagingToolbar(container, ds, {
							pageSize: config.pageSize==null?20:config.pageSize,
							displayInfo: true,
							displayMsg: config.displayMsg,
							emptyMsg: config.emptyMsg
						});
    		
    	}else{
    		this.toolBar=new Ext.Toolbar(container);
		}
    };
    
    
	this.init(config);
};

