function show_flag(value,p,r){
    if(r.get("state")==2){
        if(r.get("IsObtainLocation")=="true"){
            return String.format('<font color="gray">阻断</font>' + '&nbsp;&nbsp;&nbsp;'
                + '<a id="renameFbq.info" href="javascript:void(0);" onclick="certrecover();">恢复</a>'+ '&nbsp;&nbsp;&nbsp;'
                +'<a href="javascript:void(0);" onclick="cutScreen();">截屏</a>' + '&nbsp;&nbsp;&nbsp;'+ '<a href="javascript:void(0);" onclick="uploadLocation()">停止上传位置</a>'
            );
        } else{
            return String.format('<font color="gray">阻断</font>' + '&nbsp;&nbsp;&nbsp;'
                + '<a id="renameFbq.info" href="javascript:void(0);" onclick="certrecover();">恢复</a>'+ '&nbsp;&nbsp;&nbsp;'
                +'<a href="javascript:void(0);" onclick="cutScreen();">截屏</a>'  + '&nbsp;&nbsp;&nbsp;'+ '<a href="javascript:void(0);" onclick="uploadLocation();">停止上传位置</a>'
            );
        }

    } else{
        if(r.get("IsObtainLocation")=="null"){
            return String.format('<a href="javascript:void(0);" onclick="certblock();">阻断</a>' + '&nbsp;&nbsp;&nbsp;'
                + '<font color="gray">恢复</font>'+ '&nbsp;&nbsp;&nbsp;'+'<a href="javascript:void(0);" onclick="cutScreen();">截屏</a>' + '&nbsp;&nbsp;&nbsp;'+ '<a href="javascript:void(0);" onclick="uploadLocation();">上传地理位置</a>'
            );
         }else{
            return String.format('<a href="javascript:void(0);" onclick="certblock();">阻断</a>' + '&nbsp;&nbsp;&nbsp;'
                + '<font color="gray">恢复</font>'+ '&nbsp;&nbsp;&nbsp;'+'<a href="javascript:void(0);" onclick="cutScreen();">截屏</a>' + '&nbsp;&nbsp;&nbsp;'+ '<a href="javascript:void(0);" onclick="uploadLocation();">上传地理位置</a>'
            );
        }

    }

}

function show_info(value) {
    if(value=='null'){
        return "";
    } else{
        return value;
    }
}

function show_type(value){
    if(value==0){
         return "移动终端";
    }else if(value==1){
        return "PC机";
    }else{
        return value;
    }
}

Ext.onReady(function() {
    Ext.BLANK_IMAGE_URL = '../../js/ext/resources/images/default/s.gif';

    Ext.QuickTips.init();
    Ext.form.Field.prototype.msgTarget = 'side';

    var start = 0;
    var pageSize = 15;
    var record = new Ext.data.Record.create([
        {name:'name',mapping:'Name'},
        {name:'dept',mapping:'Dept'},
//        {name:'crtnumber',mapping:'crtnumber'},
        {name:'sourceIp',	mapping:'SourceIp'},
        {name:'targetIp',	mapping:'TargetIp'},
//        {name:'duration',	mapping:'duration'},
        {name:'loginTime',	mapping:'LoginTime'},
        {name:'inflow',	mapping:'Inflow'},
        {name:'outflow',	mapping:'Outflow'},
        {name:'type',	mapping:'Type'},
        {name:'state',	mapping:'State'},
        {name:'location',	mapping:'Location'}
    ]);
    var proxy = new Ext.data.HttpProxy({
        url:"VpnAction_selectInfo.action"
    });
    var reader = new Ext.data.JsonReader({
        totalProperty:"total",
        root:"rows",
        id:'id'
    },record);
    var store = new Ext.data.GroupingStore({
        id:"store.info",
        proxy : proxy,
        reader : reader
    });
    store.load({
        params:{
            start:start,limit:pageSize
        }
    });
    var boxM = new Ext.grid.CheckboxSelectionModel();   //复选框
    var rowNumber = new Ext.grid.RowNumberer();         //自动 编号
    var colM = new Ext.grid.ColumnModel([
        boxM,
        rowNumber,
//        {header:'证书序号',	    dataIndex:'crtnumber',  align:'center',sortable:true,menuDisabled:true},
        {header:'用户姓名',		dataIndex:'name',        align:'center',sortable:true,menuDisabled:true},
        {header:'部门地址',		dataIndex:'dept',        align:'center',sortable:true,menuDisabled:true},
        {header:'远程地址',		dataIndex:'sourceIp',   align:'center',sortable:true,menuDisabled:true},
        {header:'资源地址',		dataIndex:'targetIp',   align:'center',sortable:true,menuDisabled:true,renderer:show_info},
        {header:'登录时间',		dataIndex:'loginTime',  align:'center',sortable:true,menuDisabled:true},
//        {header:'登录时长',		dataIndex:'duration',   align:'center',sortable:true,menuDisabled:true},
        {header:'输出流量',	    dataIndex:'inflow',     align:'center',sortable:true,menuDisabled:true},
        {header:'接收流量',	    dataIndex:'outflow',    align:'center',sortable:true,menuDisabled:true},
        {header:'终端类型',		dataIndex:'type',        align:'center',sortable:true,menuDisabled:true,renderer:show_type},
        {header:'地理位置',	    dataIndex:'location',   align:'center',sortable:true,menuDisabled:true,renderer:show_info},
        {header:'操作标记',	    dataIndex:'flag',   align:'center',sortable:true,menuDisabled:true,renderer:show_flag,width:200}

    ]);

    var tbar = new Ext.Toolbar({
        autoWidth :true,
        autoHeight:true,
        items: [{
            pressed : false,
            text : '<font size="2" style="font-weight: bold;">vpn配置</font>',
            handler : function() {
                vpnConfig();
            }
        }
        ]
    });



    /*for(var i=6;i<14;i++){
     colM.setHidden(i,!colM.isHidden(i));                // 加载后 不显示 该项
     }
     colM.defaultSortable = true;*/
    var task = {
        run: function(){
            store.reload();
            grid_panel.reload;
        },
        interval: 1000*10 //10 second
    }
    var runner = new Ext.util.TaskRunner();
    runner.start(task);
    var page_toolbar = new Ext.PagingToolbar({
        pageSize : pageSize,
        store:store,
        displayInfo:true,
        displayMsg:"显示第{0}条记录到第{1}条记录，在线人数一共{2}个",
        emptyMsg:"没有记录,在线人数0人",
        beforePageText:"当前页",
        afterPageText:"共{0}页"
    });
    var grid_panel = new Ext.grid.GridPanel({
        id:'grid.info',
        plain:true,
        height:setHeight(),
        width:setWidth(),
        animCollapse:true,
//        loadMask:{msg:'正在加载数据，请稍后...'},
        border:false,
        collapsible:false,
        cm:colM,
        sm:boxM,
        store:store,
        stripeRows:true,
        autoExpandColumn:'Position',
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
        },
//        tbar:tbar,
        view:new Ext.grid.GroupingView({
            forceFit:true,
            groupingTextTpl:'{text}({[values.rs.length]}条记录)'
        }),
        bbar:page_toolbar
    });
    var port = new Ext.Viewport({
        layout:'fit',
        renderTo: Ext.getBody(),
        items:[grid_panel]
    });

    //vpn服务配置
    var vpnConfig = function(){

        //选择框 输入框
        var ipField = new Ext.form.TextField({
            fieldLabel: 'vpn服务器Ip',
            name: 'ip',
            width:100,
            allowBlank : false,
            blankText : "不能为空，请正确填写",
            regex : /^.{1,30}$/,
            regexText : '请输入vpn服务器Ip',
            emptyText : '请输入vpn服务器Ip',
            anchor : '90%'
        });


        var portField = new Ext.form.TextField({
            fieldLabel: 'vpn服务器port',
            name: 'port',
            width:100,
            allowBlank : false,
            blankText : "不能为空，请正确填写",
            regex : /^.{1,30}$/,
            regexText : '请输入vpn服务器port',
            emptyText : '请输入vpn服务器port',
            anchor : '90%'
        });

        Ext.Ajax.request({
            url : 'VpnAction_vpnConfigInfo.action',
            success : function(response, opts) {
                var data = Ext.util.JSON.decode(response.responseText);
                ipField.setValue(data.vpnip);
                portField.setValue(data.vpnport);
            },
            failure : function(response, opts) {
                Ext.Msg.alert('', "加载配置数据失败，请重试！");
            }
        });

        //表单
        var myForm = new Ext.form.FormPanel({
            labelAlign : 'right',
            labelWidth : 120,
            frame : true,
            items : [{
                xtype : 'fieldset',
                title : 'vpn监控配置信息',
                autoHeight : true,
                items : [
                   {
                        columnWidth : .8,
                        layout : 'form',
                        items : ipField
                    },{
                        columnWidth : .8,
                        layout : 'form',
                        items : portField
                    }
                ]
            }]
        });

        //增加消息框
        var update_Win = new Ext.Window({
            plain : true,
            layout : 'form',
            resizable : true, // 改变大小
            draggable : true, // 不允许拖动
            closeAction : 'close',// 可被关闭 close or hide
            modal : true, // 模态窗口
            width : 400,
            autoHeight : true,
            title : 'vpn监控配置信息',
            items : [myForm],// 嵌入数据
            buttonAlign : 'center',
            loadMask : true,
            bbar:[
                new Ext.Toolbar.Fill(),
                new Ext.Button ({
                    id:'interface.update.win.info',
                    text : '保存',
                    allowDepress : false,
                    handler : function() {
                        if (myForm.form.isValid()) {
                            Ext.MessageBox.show({
                                title:'信息',
                                width:200,
                                msg:'是否确定要配置信息?',
                                buttons:Ext.MessageBox.YESNO,
                                buttons:{'ok':'确定','no':'取消'},
                                icon:Ext.MessageBox.QUESTION,
                                closable:false,
                                fn:function(e){
                                    if(e=='ok'){
                                        myForm.getForm().submit({
                                            url :'VpnAction_updatevpnConfig.action',
                                            params:{
                                                vpnip:ipField.value,
                                                vpnport:portField.value
                                            },
                                            method :'POST',
                                            waitTitle :'系统提示',
                                            waitMsg :'正在保存,请稍后...',
                                            success : function(form,action) {
                                                insert_Win.close();
                                                Ext.MessageBox.alert("恭喜", "提交成功!");
                                            },
                                            failure : function() {
                                                Ext.MessageBox.show({
                                                    title:'系统提示',
                                                    width:200,
                                                    msg:'更新配置信息失败，请与管理员联系!',
                                                    animEl:'interface.update.win.info',
                                                    buttons:Ext.MessageBox.OK,
                                                    buttons:{'ok':'确定'},
                                                    icon:Ext.MessageBox.ERROR,
                                                    closable:false
                                                });
                                            }
                                        });
                                    }
                                }
                            });

                        } else {
                            Ext.MessageBox.show({
                                title:'信息',
                                width:200,
                                msg:'请填写完成再提交!',
                                animEl:'interface.update.win.info',
                                buttons:Ext.MessageBox.OK,
                                buttons:{'ok':'确定'},
                                icon:Ext.MessageBox.ERROR,
                                closable:false
                            });
                        }
                    }
                }),
                new Ext.Button ({
                    allowDepress : false,
                    text : '取消',
                    handler : function() {
                        update_Win.close();
                    }
                })
            ]
        }).show();



    };
});
function setHeight(){
    var h = document.body.clientHeight-8;
    return h;
}

function setWidth(){
    return document.body.clientWidth-8;
}
//恢复此证书在此ip上使用
function certblock(){
    var grid = Ext.getCmp('grid.info');
    var store = grid.getStore();
    var ip=grid.getSelectionModel().getSelected().get("sourceIp");
    var name=grid.getSelectionModel().getSelected().get("name");
    Ext.MessageBox.show({
        title:'信息',
        msg:'<font color="green">确定要阻断？</font>',
//        animEl:value+'.recover.info',
        width:260,
        buttons:Ext.Msg.YESNO,
        buttons:{'ok':'确定','no':'取消'},
        icon:Ext.MessageBox.INFO,
        closable:false,
        fn:function(e){
            if(e == 'ok'){
                Ext.Ajax.request({
                    url : 'VpnAction_block.action',
                    params:{ip:ip,name:name},
                    method:'POST',
                    success : function(){
                        Ext.MessageBox.show({
                            title:'信息',
                            width:250,
                            msg:'操作成功,点击返回列表!',
                            buttons:Ext.MessageBox.OK,
                            buttons:{'ok':'确定'},
                            icon:Ext.MessageBox.INFO,
                            closable:false,
                            fn:function(e){
                                if(e=='ok'){
                                    grid.render();
                                    store.reload();
                                }
                            }
                        });
                    },
                    failure : function(){
                        Ext.MessageBox.show({
                            title:'信息',
                            width:250,
                            msg:'请与后台服务人员联系!',
                            buttons:Ext.MessageBox.OK,
                            buttons:{'ok':'确定'},
                            icon:Ext.MessageBox.ERROR,
                            closable:false
                        });
                    }
                });
            }
        }
    });
}

function certrecover(){
    var grid = Ext.getCmp('grid.info');
    var store = grid.getStore();
    var ip=grid.getSelectionModel().getSelected().get("sourceIp");
    var name=grid.getSelectionModel().getSelected().get("name");
    Ext.MessageBox.show({
        title:'信息',
        msg:'<font color="green">确定要恢复？</font>',
//        animEl:value+'.recover.info',
        width:260,
        buttons:Ext.Msg.YESNO,
        buttons:{'ok':'确定','no':'取消'},
        icon:Ext.MessageBox.INFO,
        closable:false,
        fn:function(e){
            if(e == 'ok'){
                Ext.Ajax.request({
                    url : 'VpnAction_recover.action',
                    params:{ip:ip,name:name},
                    method:'POST',
                    success : function(){
                        Ext.MessageBox.show({
                            title:'信息',
                            width:250,
                            msg:'操作成功,点击返回列表!',
                            buttons:Ext.MessageBox.OK,
                            buttons:{'ok':'确定'},
                            icon:Ext.MessageBox.INFO,
                            closable:false,
                            fn:function(e){
                                if(e=='ok'){
                                    grid.render();
                                    store.reload();
                                }
                            }
                        });
                    },
                    failure : function(){
                        Ext.MessageBox.show({
                            title:'信息',
                            width:250,
                            msg:'请与后台服务人员联系!',
                            animEl:value+'.delete.info',
                            buttons:Ext.MessageBox.OK,
                            buttons:{'ok':'确定'},
                            icon:Ext.MessageBox.ERROR,
                            closable:false
                        });
                    }
                });
            }
        }
    });
}

function cutScreen(){
    var grid = Ext.getCmp('grid.info');
    var store = grid.getStore();
    var ip=grid.getSelectionModel().getSelected().get("sourceIp");
    var name=grid.getSelectionModel().getSelected().get("name");
    Ext.MessageBox.show({
        title:'信息',
        msg:'<font color="green">确定要截屏？</font>',
//        animEl:value+'.recover.info',
        width:260,
        buttons:Ext.Msg.YESNO,
        buttons:{'ok':'确定','no':'取消'},
        icon:Ext.MessageBox.INFO,
        closable:false,
        fn:function(e){
            if(e == 'ok'){
                Ext.Ajax.request({
                    url : 'VpnAction_cutScreen.action',
                    params:{ip:ip,name:name},
                    method:'POST',
                    success : function(){
                        Ext.MessageBox.show({
                            title:'信息',
                            width:250,
                            msg:'操作成功,点击返回列表!',
                            buttons:Ext.MessageBox.OK,
                            buttons:{'ok':'确定'},
                            icon:Ext.MessageBox.INFO,
                            closable:false,
                            fn:function(e){
                                if(e=='ok'){
                                    grid.render();
                                    store.reload();
                                }
                            }
                        });
                    },
                    failure : function(){
                        Ext.MessageBox.show({
                            title:'信息',
                            width:250,
                            msg:'失败,请与后台服务人员联系!',
                            buttons:Ext.MessageBox.OK,
                            buttons:{'ok':'确定'},
                            icon:Ext.MessageBox.ERROR,
                            closable:false
                        });
                    }
                });
            }
        }
    });
}

function uploadLocation(){
        var grid = Ext.getCmp('grid.info');
        var store = grid.getStore();
        var ip=grid.getSelectionModel().getSelected().get("sourceIp");
        var name=grid.getSelectionModel().getSelected().get("name");
        Ext.MessageBox.show({
            title:'信息',
            msg:'<font color="green">确定要上传地理位置？</font>',
//        animEl:value+'.recover.info',
            width:260,
            buttons:Ext.Msg.YESNO,
            buttons:{'ok':'确定','no':'取消'},
            icon:Ext.MessageBox.INFO,
            closable:false,
            fn:function(e){
                if(e == 'ok'){
                    Ext.Ajax.request({
                        url : 'VpnAction_uploadLocation.action',
                        params:{ip:ip,name:name},
                        method:'POST',
                        success : function(){
                            Ext.MessageBox.show({
                                title:'信息',
                                width:250,
                                msg:'操作成功,点击返回列表!',
                                buttons:Ext.MessageBox.OK,
                                buttons:{'ok':'确定'},
                                icon:Ext.MessageBox.INFO,
                                closable:false,
                                fn:function(e){
                                    if(e=='ok'){
                                        grid.render();
                                        store.reload();
                                    }
                                }
                            });
                        },
                        failure : function(){
                            Ext.MessageBox.show({
                                title:'信息',
                                width:250,
                                msg:'失败,请与后台服务人员联系!',
                                buttons:Ext.MessageBox.OK,
                                buttons:{'ok':'确定'},
                                icon:Ext.MessageBox.ERROR,
                                closable:false
                            });
                        }
                    });
                }
            }
        });
}
