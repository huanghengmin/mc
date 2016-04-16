/**
 * 平台说明
 */
Ext.onReady(function() {

    Ext.BLANK_IMAGE_URL = '../../js3/ext/resources/images/default/s.gif';
	Ext.QuickTips.init();
	Ext.form.Field.prototype.msgTarget = 'side';
	var record = new Ext.data.Record.create([
	  {name:'product',mapping:'product'},
	  {name:'version',mapping:'version'},
	  {name:'os',mapping:'os'}
	]);
	var proxy = new Ext.data.HttpProxy({
		url:"../../LicenseAction_about.action"
	});
	var reader = new Ext.data.JsonReader({
		totalProperty:"total",
		root:"rows"
	},record);
	var store = new Ext.data.Store({
		proxy : proxy,
		reader : reader
	});
	store.load();
	store.on('load',function(){
		var product = store.getAt(0).get('product');
		var center = store.getAt(0).get('version');
		var os = store.getAt(0).get('os');
		Ext.getCmp('product.info').setValue(product);
		Ext.getCmp('version.info').setValue(center);
		Ext.getCmp('os.info').setValue(os);
	});
	var panel = new Ext.form.FormPanel({
		id:'about.info',
        labelWidth:100,
        frame:true,
        loadMask : { msg : '正在加载数据，请稍后.....' },
        autoScroll:true,
        labelAlign:'right',
        items:[{
        	id:'product.info',
        	xtype:'displayfield',
        	fieldLabel : '产品标识'
        },{
        	id:'version.info',
        	xtype:'displayfield',
        	fieldLabel : '版本'
        },{
        	id:'os.info',
        	xtype:'displayfield',
        	fieldLabel : '操作系统'
        },{
        	xtype:'displayfield',
        	fieldLabel : '',
        	value:''
        },{
        	xtype:'displayfield',
        	fieldLabel : '',
        	value:''
        }]
	});
	
    new Ext.Viewport({
    	layout :'fit',
    	renderTo:Ext.getBody(),
    	items:[panel]
    });
            
	
}); // / Ext onReady end!


