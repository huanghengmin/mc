/**
 * 日志下载
 */
Ext.onReady(function(){
    Ext.BLANK_IMAGE_URL = '../../js3/ext/resources/images/default/s.gif';
    Ext.QuickTips.init();
    Ext.form.Field.prototype.msgTarget ='side';

//    var pageStart = 0;
//    var pageSize = 15;
	var internal_record = new Ext.data.Record.create([
        {name:'fileName',mapping:'fileName'}
    ]);
    var internal_proxy = new Ext.data.HttpProxy({
        url:"../../DownLoadAction_readLocalLogName.action"
    });
    var internal_reader = new Ext.data.JsonReader({
        totalProperty:"total",
        root:"rows"
    },internal_record);
    var internal_store = new Ext.data.Store({
        proxy : internal_proxy,
        reader : internal_reader
    });
    internal_store.load();
	
	var internal_logBoxM = new Ext.grid.CheckboxSelectionModel();   //复选框
    var internal_logRowNumber = new Ext.grid.RowNumberer();         //自动 编号
    var internal_logColM = new Ext.grid.ColumnModel([
        internal_logBoxM,
        internal_logRowNumber,
        {header:"下载本地日志文件名",dataIndex:"fileName",align:'center',renderer : internal_logDownloadShowUrl}
    ]);

    var internal_logGrid = new Ext.grid.GridPanel({
        plain:true,
        animCollapse:true,
        height:300,
        loadMask:{msg:'正在加载数据，请稍后...'},
        border:false,
        collapsible:false,
        cm:internal_logColM,
        sm:internal_logBoxM,
        store:internal_store,
        stripeRows:true,
        autoExpandColumn:2,
        disableSelection:true,
        bodyStyle:'width:100%',
        enableDragDrop: true, 
        selModel:new Ext.grid.RowSelectionModel({singleSelect:true}),
        viewConfig:{
            forceFit:true,
            enableRowBody:true,
            getRowClass:function(record,rowIndex,p,store){
                return 'x-grid3-row-collapsed';
            }
        }
    });
    
    /*var external_record = new Ext.data.Record.create([
        {name:'externalLog',mapping:'externalLog'}
    ]);
    var external_proxy = new Ext.data.HttpProxy({
    	url:"../../DownLoadAction_readRemoteLogName.action"
    });
    var external_reader = new Ext.data.JsonReader({
    	totalProperty:"total",
    	root:"rows"
    },external_record);
    var external_store = new Ext.data.Store({
    	proxy : external_proxy,
    	reader : external_reader
    });
    external_store.load();
                                              	
    var external_logBoxM = new Ext.grid.CheckboxSelectionModel();   //复选框
    var external_logRowNumber = new Ext.grid.RowNumberer();         //自动 编号
    var external_logColM = new Ext.grid.ColumnModel([
    	external_logBoxM,
    	external_logRowNumber,
        {header:"下载远程日志文件名",dataIndex:"fileName",align:'center',renderer : external_logDownloadShowUrl}
    ]);

    var external_logGrid = new Ext.grid.GridPanel({
    	plain:true,
    	animCollapse:true,
    	height:300,
    	loadMask:{msg:'正在加载数据，请稍后...'},
    	border:false,
    	collapsible:false,
    	cm:external_logColM,
    	sm:external_logBoxM,
    	store:external_store,
    	stripeRows:true,
    	autoExpandColumn:2,
    	disableSelection:true,
    	bodyStyle:'width:100%',
    	enableDragDrop: true, 
    	selModel:new Ext.grid.RowSelectionModel({singleSelect:true}),
    	viewConfig:{
    		forceFit:true,
    		enableRowBody:true,
    		getRowClass:function(record,rowIndex,p,store){
    			return 'x-grid3-row-collapsed';
    		}
    	}
    });*/
    new Ext.Viewport({
    	border:false,
    	renderTo:Ext.getBody(),
        layout:'fit',
        items:[internal_logGrid]
        /*[{
        	layout:'column',
        	items:[
        		{height:setHeight(),border:false,items:[internal_logGrid],columnWidth:.5},
        		{height:setHeight(),border:false,items:[external_logGrid],columnWidth:.5}
        	]
    	}]*/
    });
});

function setHeight(){
	var h = document.body.clientHeight-8;
	return h;
}

function internal_logDownloadShowUrl(value){
	var type = 'internal_log';
	return "<a href='javascript:;' onclick='download_log(\""+value+"\",\""+type+"\");'>"+value+"</a>";
}
function external_logDownloadShowUrl(value){
	var type = 'external_log';
	return "<a href='javascript:;' onclick='download_log(\""+value+"\",\""+type+"\");'>"+value+"</a>";
}
function download_log(logName,type){
    if (!Ext.fly('test')) {
        var frm = document.createElement('form');
        frm.id = 'test';
        frm.name = id;
        frm.style.display = 'none';
        document.body.appendChild(frm);
    }
    Ext.Ajax.request({
        url: '../../DownLoadAction_download.action',
        params:{type:type,logName:logName },
        form: Ext.fly('test'),
        method: 'POST',
        isUpload: true
    });
}
