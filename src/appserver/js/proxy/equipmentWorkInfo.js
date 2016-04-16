
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
        {header : '设备编号', dataIndex : 'equname', align : 'center',sortable:true},
        {header : '设备SNMPOID', dataIndex : 'snmpoid', align : 'center',sortable:true},
        {header : '设备ip地址', dataIndex : 'ip', align : 'center',sortable:true},
        {header : 'SNMP服务',dataIndex :'snmp',width:50 ,align : 'center',sortable:true,renderer : function(value) {
                if(value=='true'){
                    return "<img src='./img/icon/ok.png'/> ";
                }else if(value=="null"){
                    return "<img src='./img/icon/off.gif'/> ";
                } else if(value=="没有启用snmp检测"){
                    return "<img src='./img/icon/warning.png'  title='没有启用snmp检测' />  ";
                }  else if(value=="设备没有相对应的snmpoid"){
                    return "<img src='./img/icon/warning.png'  title='设备没有相对应的snmpoid' />  ";
                }
            }
        },
        {header : 'syslog服务',dataIndex :'syslog',width:50, align : 'center',sortable:true,renderer : function(value) {
            if(value=='true'){
                return "<img src='./img/icon/ok.png'/> ";
            }else if(value=="null"){
                return "<img src='./img/icon/off.gif'/> ";
            }else{
                return "<img src='./img/icon/warning.png' title='"+value+"'/> ";
            }
        }},
        {header : '网络状态',dataIndex :'ipping',width:50, align : 'center',sortable:true,renderer : function(value) {
              if(value=='true'){
                          return "<img src='./img/icon/ok.png'/> ";
              }else{
                          return "<img src='./img/icon/off.gif'/> ";
              }
            }
        }
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