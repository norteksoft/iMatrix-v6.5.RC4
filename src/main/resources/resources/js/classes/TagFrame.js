/*
* Tag窗口类，在一个布局区域内，动态的增加新的tag和关闭tag,
* 每个tag包含一个iframe对象，用于显示不同的页面
* 
* @param region: 布局区域
* @see Ext.LayoutRegion
*/
var TagFrame = function (region) {

this.wins = [];   //已经打开的窗口
this.maxWins = 8;

/**
* 打开窗口，如果窗口已经存在则激活
*/
this.openWin=function (url, name) {
		var win=this.container(url, name);
		
        if(win!==null){
        	var panel=win.panel;
        	if(panel.getEl()){
        		this.activateWin(win);
        		return;
        	}else{
        		this.removeWin(win);
        	}
        	
        }
        
        this.addWin(url, name);
        
     };
/**
* 新增窗口
*/    
this.addWin=function (url, name) {
		if(this.wins.length>=this.maxWins){
			
			this.removeWin(this.wins[0]);
		}

        var id = Ext.id();
        var iframe = document.createElement("iframe");
        iframe.id = id;
        iframe.src = url;
        
        iframe.setAttribute("frameborder", "no");
        iframe.setAttribute("scrolling", "auto");
        
        document.body.appendChild(iframe);
        
        
        var panel=new Ext.ContentPanel(id, {title:name, fitToFrame:true, closable:true});
        region.add(panel);
    	
    	this.wins[this.wins.length]={id:id,url:url,name:name,panel:panel};
    };
/**
* 激活窗口
*/
this.activateWin = function (win) {
        var panel=win.panel;
        if(panel.getEl()){
        	region.showPanel(panel.getId());
        }
        
    };
    
/**
* 移除窗口
*/    
this.removeWin =function (win)        //remove window
{
		
		if(win == null)return;
		var temparr = [];

		for(var i=0;i<this.wins.length;i++)
		{
			if(this.wins[i] != win){
				temparr[temparr.length] = this.wins[i];
			}
		}
		this.wins = temparr;
		
		var panel=win.panel;
		if(panel.getEl()){
			region.remove(panel,true);
		}
	};
	    
this.container = function (url,name){
		for(var i=0;i<this.wins.length;i+=1)
		{
			if(this.wins[i].name == name && this.wins[i].url == url)
			{
				return this.wins[i];
			}
		}
		return null;
	};
    
};

