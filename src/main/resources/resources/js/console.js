var Docs = function(){
    var layout, center, tabs,win;
    
    var classClicked = function(e, target){
        win.openWin(target.href,target.outerText);
    };
    
    return {
        init : function(){
            // initialize state manager, we will use cookies
            Ext.state.Manager.setProvider(new Ext.state.CookieProvider());
            
            // create the main layout
            layout = new Ext.BorderLayout(document.body, {
                north: {
                    split:false,
                    initialSize: 32,
                    titlebar: false
                },
                west: {
                    split:true,
                    initialSize: 200,
                    minSize: 175,
                    maxSize: 400,
                    titlebar: true,
                    collapsible: true,
                    animate: true,
                    useShim:true,
                    cmargins: {top:2,bottom:2,right:2,left:2}
                },
                center: {
                    autoScroll:true,
                    tabPosition: 'top',
                    closeOnTab: true,
                    //alwaysShowTabs: true,
                    resizeTabs: true
                }
            });
            // tell the layout not to perform layouts until we're done adding everything
            layout.beginUpdate();
            layout.add('north', new Ext.ContentPanel('header'));
            
            layout.add('west', new Ext.ContentPanel('classes', {title: 'Jwebap Navigation', fitToFrame:true}));
            
            //tabs =new Ext.TabPanel('tabs');
            
            center = layout.getRegion('center');
            win=new TagFrame(center);
            //center.add(new Ext.ContentPanel('main', {fitToFrame:true}));
            

            layout.restoreState();
            layout.endUpdate();
            
            var classes = Ext.get('classes');
			if(classes){
            	 classes.on('click', classClicked, null, {delegate: 'a', stopEvent:true});
		         classes.select('h3').each(function(el){
		             var c = new NavNode(el.dom);
		             /**默认全展开
		             if(!/^\s*.*Component.*\s*$/.test(el.dom.innerHTML)){
		                 c.collapse();
		             }
		             **/
		         });
		    }
            var defaultEl =Ext.get('default');
            if(defaultEl){
               classClicked(null,defaultEl.dom);
            }
            
            // safari and opera have iframe sizing issue, relayout fixes it
			if(Ext.isSafari || Ext.isOpera){
				layout.layout();
			}
			
			var loading = Ext.get('loading');
			var mask = Ext.get('loading-mask');
			mask.setOpacity(.8);
			mask.shift({
				xy:loading.getXY(),
				width:loading.getWidth(),
				height:loading.getHeight(), 
				remove:true,
				duration:1,
				opacity:.3,
				easing:'bounceOut',
				callback : function(){
					loading.fadeOut({duration:.2,remove:true});
				}
			});
        }
    };
}();
Ext.onReady(Docs.init, Docs, true);

/**
 * Simple tree node class based on Collapser and predetermined markup.
 */
var NavNode = function(clickEl, collapseEl){
    this.clickEl = Ext.get(clickEl);
    if(!collapseEl){
        collapseEl = this.clickEl.dom.nextSibling;
        while(collapseEl.nodeType != 1){
            collapseEl = collapseEl.nextSibling;
        }
    }
    this.collapseEl = Ext.get(collapseEl);
    this.clickEl.addClass('collapser-expanded');
    this.clickEl.mon('click', function(){
        this.collapsed === true ? 
            this.expand() : this.collapse();
    }, this, true);
};

NavNode.prototype = {
    collapse : function(){
        this.collapsed = true;
        this.collapseEl.setDisplayed(false);
        this.clickEl.replaceClass('collapser-expanded','collapser-collapsed');
    },
    
    expand : function(){
        this.collapseEl.setDisplayed(true);
        this.collapsed = false;
        this.collapseEl.setStyle('height', '');   
        this.clickEl.replaceClass('collapser-collapsed','collapser-expanded');
    }
};