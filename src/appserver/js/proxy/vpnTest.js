
Ext.onReady(function() {

    var task = {
        run : function() {
            ds.reload();
        },
        interval : 10*1000 //10秒
    }
    Ext.TaskMgr.start(task);


    // 表头
    var sm = new Ext.grid.CheckboxSelectionModel();
    var cm = new Ext.grid.ColumnModel([
        sm,
        {header : 'vpn提供的用户信息', dataIndex : 'equname', align : 'center',sortable:true},
        {header : '信息内容', dataIndex : 'snmpoid', align : 'center',sortable:true}
    ]);

    //数据存储
    var ds = new Ext.data.Store({
        proxy : new Ext.data.HttpProxy({
            url :'InterfaceManagerAction_deviceWrokingInfo.action'
        }),
        reader : new Ext.data.JsonReader({
            totalProperty : 'totalProperty',
            root : 'root'
        }, [{
            name : 'ip'
        },{
            name :'snmp'
        },{
            name :'syslog'
        },{
            name :'ipping'
        },{
            name :'snmpoid'
        },{
            name :'equname'
        }
        ])
    });

    //表格
    var grid = new Ext.grid.GridPanel({
        renderTo : "grid", // 渲染到哪里
        stripeRows : true, // 斑马线效果
        columnLines : true, // 控制中间是否有线相隔
        store : ds,
        height : setHeight(),
        width : setWidth(),
        cm : cm, // 表头
        selModel : sm, // 为Grid提供选区模型
        viewConfig : {
            forceFit : true
        },
        bbar : new Ext.PagingToolbar({
            pageSize : 15,
            store : ds,
            displayInfo : true,
            displayMsg : '显示第{0}条到{1}条记录,一共{2}条',
            emptyMsg : "没有记录"
        })
    });

    ds.load({
        params : {
            start : 0,
            limit : 15
        }
    });
    grid.render();

    var port = new Ext.Viewport({
        layout:'fit',
        renderTo:Ext.getBody(),
        items:[grid]
    });

});

function setHeight(){
    var h = document.body.clientHeight-8;
    return h;
}

function setWidth(){
    return document.body.clientWidth-8;
}