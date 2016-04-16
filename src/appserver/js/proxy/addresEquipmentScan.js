

Ext.onReady(function() {

    //定时器 在扫描线程没结束前定时刷新datastore
    var task = {
        run : function() {
            ds.load({
                params : {
                    start : 0,
                    limit : 15
                }
            });
            isMapSelect();
        },
        interval : 5000 // 5秒
    }

    var tbar = new Ext.Toolbar({
        autoWidth :true,
        autoHeight:true,
        items: [
        'ip开始地址'
        ,{
            id:'internal.ip.start',
            emptyText : "ip开始地址",
            xtype:'textfield',
            name:'ipstart',
//            value:'172.16.2.2',
            regex:/^(((25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9][0-9]|[0-9])\.){3}(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9][0-9]|[0-9]))$/,
            regexText:'这个不是Ip'
        },{
            xtype : 'tbseparator',
            width : 10
        },
          'ip结束地址'
        ,{
            id:'internal.ip.stop',
            emptyText : "ip结束地址",
            xtype:'textfield',
            name:'ipstart',
//            value:'172.16.2.4',
            regex:/^(((25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9][0-9]|[0-9])\.){3}(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9][0-9]|[0-9]))$/,
            regexText:'这个不是Ip'
        } ,{
            pressed : false,
            text : '<font color="blue">开始搜索</font>',
            handler : function() {
                select();
            }
        }
        ]
    });



    // 表头
    var sm = new Ext.grid.CheckboxSelectionModel();
    var cm = new Ext.grid.ColumnModel([
        sm,
        {header : 'IP地址', dataIndex : 'ip', align : 'center',sortable:true},
        { header : '操作标记',dataIndex :'flag', align : 'center',renderer : function(value) {
                if(value=='false'){
                    return "<a  style='text-decoration: underline;color:blue;'onclick='insert()'>增加设备</a>&nbsp<a style='text-decoration: underline;color:gray;'>查看设备</a>";
                } else{
                    return "<a  style='text-decoration: underline;color:gray;'>增加设备</a>&nbsp<a style='text-decoration: underline;color:blue;'onclick='lookup()'>查看设备</a>";
                }
            }
        }
    ]);



    //数据存储
    var ds = new Ext.data.Store({
        proxy : new Ext.data.HttpProxy({
            url :'InterfaceManagerAction_findPingSuccessIp.action'
        }),
        reader : new Ext.data.JsonReader({
            totalProperty : 'totalProperty',
            root : 'root'
             }, [{
                  name : 'ip'
             },{
                  name :'flag'
             }
        ])
    });

   //表格
    var grid = new Ext.grid.GridPanel({
        renderTo : "grid", // 渲染到哪里
        stripeRows : true, // 斑马线效果
        columnLines : true, // 控制中间是否有线相隔
        store : ds,
        height : 400,
        width : 1000,
        cm : cm, // 表头
        selModel : sm, // 为Grid提供选区模型
        viewConfig : {
            forceFit : true
        },
        tbar:tbar,
        bbar : new Ext.PagingToolbar({
            pageSize : 15,
            store : ds,
            displayInfo : true,
            displayMsg : '显示第{0}条到{1}条记录,一共{2}条',
            emptyMsg : "没有记录"
        })
    });
    grid.render();
    ds.load({
        params : {
            start : 0,
            limit : 15
        }
    });

    var port = new Ext.Viewport({
        layout:'fit',
        renderTo:Ext.getBody(),
        items:[grid]
    });



    // 点击获取行
    var row;
    sm.on("rowselect", function(sm, rowIndex) {
        row = rowIndex;
    });

    //Ping的IP数量
    var num = 0;

    //搜寻地址段方法
    var select = function (){
       var sum = 0;     //pingIP数量的大小
//       alert(ips.length);
        num = 0;     //存放IP数组的大小
        var ipstart = Ext.getCmp('internal.ip.start').getValue();
        var ipstop = Ext.getCmp('internal.ip.stop').getValue();
        var ipkaishi = ipstart.split(".");
        var ipjieshu = ipstop.split(".");
        if(ipkaishi[0]==ipjieshu[0]&&ipkaishi[1]==ipjieshu[1]&&ipkaishi[2]==ipjieshu[2]){
            var kaishi = parseInt(ipkaishi[3]);
            var jieshu = parseInt(ipjieshu[3]);
//                    var myMask = new Ext.LoadMask(Ext.getBody(),{
//                        msg:'正在扫描地址段'+ipkaishi+"到"+ipjieshu+',请稍后...',
//                        removeMask:true
//                    });
//                    myMask.show();
                   Ext.Ajax.request({
                       url : 'InterfaceManagerAction_addressPing.action',
                       params:{
                           ipstart:ipstart,
                           ipend:ipstop
                       },
                       method : 'POST',
                       success:function(result,request) {
                                grid.render();

                           Ext.TaskMgr.start(task);

                       },
                       failure:function(){
                               grid.render();

                           Ext.TaskMgr.start(task);

                       }
                    });
       }else{
           Ext.Msg.alert('提示', "请确保开始IP与结束IP在一个地址段内！");
       }
    }

   var isMapSelect =function(){
       Ext.Ajax.request({
           url : 'InterfaceManagerAction_isMapFlag.action',
           method : 'POST',
           success:function(result,request) {
               if(result.responseText=="true"){
                   ds.load({
                       params : {
                           start : 0,
                           limit : 15
                       }
                   });
                   Ext.TaskMgr.stop(task);
               }
           },
           failure:function(){
               ds.load({
                   params : {
                       start : 0,
                       limit : 15
                   }
               });
               Ext.TaskMgr.stop(task);
           }
       });
   }


   //增加设备方法
   var insert = function(ipValue){
//       alert(ipValue);
       Ext.QuickTips.init();// 用来提示信息
       Ext.form.Field.prototype.msgTarget = 'side';// 统一指定错误提示方式

       var snmpoidRecord = Ext.data.Record.create(
           [
               {name: 'cpuuse'},
               {name: 'name'},
               {name: 'disktotal'},
               {name: 'type'},
               {name: 'company'},
               {name: 'diskuse'},
               {name: 'memtotal'},
               {name: 'curconn'},
               {name: 'memuse'},
               {name: 'snmpver'}
           ]);

       //建立数据

       var snmpoidDS = new Ext.data.Store({
           proxy: new Ext.data.HttpProxy({url:'IPlatManager?action=listSnmpOIDAction',method:'POST'}),

           reader: new Ext.data.JsonReader({
               root: 'snmpoids',
               totalRecords: 'totalCount',
               totalProperty: 'totalCount'
           }, snmpoidRecord)
       });
       snmpoidDS.load();

       var snmpverDs = [
           ['v1','SNMP v1'],
           ['v2','SNMP v2'],
           ['v3','SNMP v3']
       ];

       var authDs = [
           ['md5','AuthMD5'],
           ['sha','AuthSHA']
       ];

       var commonDs = [
           ['des','DES'],
           ['3des','3DES'],
           ['aes128','AES128'],
           ['aes192','AES192'],
           ['aes256','AES256']
       ];

       //选择框 输入框
       var idField = new Ext.form.TextField({
           fieldLabel: '设备编号',
           name: 'id',
           width:190,
           allowBlank : false,
           blankText : "请输入设备编号，设备编号需要与CMS中设备Id对应",
           regex : /^.{1,30}$/,
           regexText : '请输入设备编号，设备编号需要与CMS中设备Id对应',
           anchor : '90%'
       });

       var nameField = new Ext.form.ComboBox({
           hiddenName:'name',
           store:snmpoidDS,
           displayField:'name',
           valueField:'name',
           fieldLabel: '设备SNMOID',
           typeAhead: true,
           editable:true,
           allowBlank:false,
           mode: 'local',
           triggerAction: 'all',
           emptyText:'请选择SNMOID',
           selectOnFocus:true,
           width:190
       });

       var devicemodeField = new Ext.form.TextField({
           fieldLabel: '设备型号',
           name: 'devicemode',
           emptyText:'请输入设备型号',
           width:190,
           allowBlank:false
       });

       var deviceipField = new Ext.form.TextField({
           fieldLabel: '设备IP',
           name: 'deviceip',
           width:190,
           grow: false,
           value:ipValue ,
           emptyText:ipValue,
           blankText:ipValue

       });

       var deviceportField = new Ext.form.TextField({
           fieldLabel: '设备SNMP服务端口',
           name: 'deviceport',
           allowBlank:false,
           width:80,
           grow: false,
           value:'161',
           blankText:'161'
       });

       var devicesnmppwdField = new Ext.form.TextField({
           fieldLabel: '设备SNMP服务密码',
           name: 'devicesnmppwd',
           allowBlank:false,
           width:80,
           grow: false,
           value:'public',
           blankText:'public'
       });

       var availableField = new Ext.form.Checkbox({
           fieldLabel: '启用',
           name: 'available',
           width:190,
           allowBlank:false
       });

       var authField = new Ext.form.ComboBox({
           hiddenName:'auth',
           store:new Ext.data.SimpleStore({
               fields: ['value', 'text'],
               data : authDs
           }),
           displayField:'text',
           valueField:'value',
           fieldLabel: '认证加密算法',
           typeAhead: true,
           editable:false,
           allowBlank:true,
           mode: 'local',
           triggerAction: 'all',
           emptyText:'请选择认证加密算法',
           selectOnFocus:true,
           width:190
       });

       var authpasswordField = new Ext.form.TextField({
           fieldLabel: '认证加密密钥',
           name: 'authpassword',
           allowBlank:true,
           width:80,
           readOnly:true,
           grow: false,
           editable:false,
           blankText:'',
           emptyText:'',
           value:''
       });

       var commonField = new Ext.form.ComboBox({
           hiddenName:'common',
           store:new Ext.data.SimpleStore({
               fields: ['value', 'text'],
               data : commonDs
           }),
           displayField:'text',
           valueField:'value',
           fieldLabel: '通讯加密算法',
           typeAhead: true,
           editable:false,
           allowBlank:true,
           mode: 'local',
           triggerAction: 'all',
           emptyText:'请选择通讯加密算法',
           selectOnFocus:true,
           width:190
       });

       var commonpasswordField = new Ext.form.TextField({
           fieldLabel: '通讯加密密钥',
           name: 'commonpassword',
           allowBlank:true,
           width:80,
           readOnly:true,
           grow: false,
           blankText:'',
           editable:false,
           emptyText:'',
           value:''
       });

       var snmpverFiled = new Ext.form.ComboBox({
           hiddenName:'snmpver',
           store:new Ext.data.SimpleStore({
               fields: ['value', 'text'],
               data : snmpverDs
           }),
           displayField:'text',
           valueField:'value',
           fieldLabel: 'SNMP版本',
           typeAhead: true,
           editable: true,
           allowBlank:false,
           mode: 'local',
           triggerAction: 'all',
           emptyText:'请选择SNMP版本',
           selectOnFocus:true,
           width:190,
           listeners: {
               'select': function(snmpver) {
                   if (snmpver.getValue() == 'v3') {
                       authField.el.dom.readOnly = false;
                       authpasswordField.el.dom.readOnly = false;
                       commonField.el.dom.readOnly = false;
                       commonpasswordField.el.dom.readOnly = false;
                   } else {
                       authField.setValue('');
                       authpasswordField.setValue('');
                       commonField.setValue('');
                       commonpasswordField.setValue('');
                       authField.el.dom.readOnly = true;
                       authpasswordField.el.dom.readOnly = true;
                       commonField.el.dom.readOnly = true;
                       commonpasswordField.el.dom.readOnly = true;
                   }
               }
           }
       });

       //表单
       var myForm = new Ext.form.FormPanel({
           labelAlign : 'right',
           labelWidth : 150,
           frame : true,
           items : [{
               xtype : 'fieldset',
               title : '设备信息',
               autoHeight : true,
               items : [{// 第1行
                   layout : 'column',
                   items : [{
                       columnWidth : .8,
                       layout : 'form',
                       items : idField
                   },{
                       columnWidth : .8,
                       layout : 'form',
                       items : nameField
                   },{
                       columnWidth : .8,
                       layout : 'form',
                       items : devicemodeField
                   },{
                       columnWidth : .8,
                       layout : 'form',
                       items : deviceipField
                   },{
                       columnWidth : .8,
                       layout : 'form',
                       items : deviceportField
                   },{
                       columnWidth : .8,
                       layout : 'form',
                       items : devicesnmppwdField
                   },{
                       columnWidth : .8,
                       layout : 'form',
                       items : availableField
                   },{
                       columnWidth : .8,
                       layout : 'form',
                       items : snmpverFiled
                   },{
                       columnWidth : .8,
                       layout : 'form',
                       items : authField
                   },{
                       columnWidth : .8,
                       layout : 'form',
                       items : authpasswordField
                   } ,{
                       columnWidth : .8,
                       layout : 'form',
                       items : commonField
                   },{
                       columnWidth : .8,
                       layout : 'form',
                       items : commonpasswordField
                   }
                   ]
               }]
           }]
       });

       //增加消息框
       var insert_Win = new Ext.Window({
           plain : true,
           layout : 'form',
           resizable : true, // 改变大小
           draggable : true, // 不允许拖动
           closeAction : 'close',// 可被关闭 close or hide
           modal : true, // 模态窗口
           width : 600,
           autoHeight : true,
           title : '增加设备信息',
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
                               msg:'是否确定要新增设备么?',
                               buttons:Ext.MessageBox.YESNO,
                               buttons:{'ok':'确定','no':'取消'},
                               icon:Ext.MessageBox.QUESTION,
                               closable:false,
                               fn:function(e){
                                   if(e=='ok'){
                                       myForm.getForm().submit({
                                           url :'IPlatManager?action=saveDeviceAction',
                                           method :'POST',
                                           waitTitle :'系统提示',
                                           waitMsg :'正在保存,请稍后...',
                                           success : function(form,action) {
                                               insert_Win.close();
                                               Ext.MessageBox.alert("恭喜", "提交成功!");
                                               grid.reload();
                                           },
                                           failure : function() {
                                               Ext.MessageBox.show({
                                                   title:'系统提示',
                                                   width:200,
                                                   msg:'新增设备失败，请与管理员联系!',
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
                   text : '重置',
                   handler : function() {
                       myForm.form.reset();
                   }
               })
           ]
       }).show();
   };

    //查看设备方法

    var lookup = function(ipValue){
        //数据框
        var idField = new Ext.form.DisplayField({
            fieldLabel: '设备编号',
            name: 'info.id',
            width:190
        });


        var nameField = new Ext.form.DisplayField({// 不能为空 文本类型
            fieldLabel: '设备SNMOID',
            name : 'name',
            width:190
        });

        var devicemodeField = new Ext.form.DisplayField({
            fieldLabel: '设备型号',
            name: 'devicemode',
            width:190
        });

        var deviceipField = new Ext.form.DisplayField({
            fieldLabel: '设备IP',
            name: 'info.deviceip',
            width:190
        });

        var deviceportField = new Ext.form.DisplayField({
            fieldLabel: '设备SNMP服务端口',
            name: 'deviceport',
            width:80
        });

        var devicesnmppwdField = new Ext.form.DisplayField({
            fieldLabel: '设备SNMP服务密码',
            name: 'devicesnmppwd',
            width:80
        });
        var availableField = new Ext.form.DisplayField({
            fieldLabel: '启用',
            name: 'available',
            width:190
        });

        var authField = new Ext.form.DisplayField({
            name:'auth',
            fieldLabel: '认证加密算法',
            width:190
        });

        var authpasswordField = new Ext.form.DisplayField({
            fieldLabel: '认证加密密钥',
            name: 'authpassword',
            width:80
        });

        var commonField = new Ext.form.DisplayField({
            name:'common',
            fieldLabel: '通讯加密算法',
            width:190
        });

        var commonpasswordField = new Ext.form.DisplayField({
            fieldLabel: '通讯加密密钥',
            name: 'commonpassword',
            width:80
        });

        var snmpverFiled = new Ext.form.DisplayField({
            fieldLabel: 'SNMP版本',
            name: 'snmpver',
            width:80
        });




        deviceipField.setValue(ipValue);




        //消息框
        var selectMyForm = new Ext.form.FormPanel({
            labelAlign : 'right',
            labelWidth : 150,
            frame : true,
            reader : new Ext.data.JsonReader({
                root : "root"
            }),
            items : [{
                xtype : 'fieldset',
                title : '查看信息',
                autoHeight : true,
                items : [{// 第1行
                    layout : 'column',
                    items : [{
                        columnWidth : .8,
                        layout : 'form',
                        items : idField
                    },{
                        columnWidth : .8,
                        layout : 'form',
                        items : nameField
                    },{
                        columnWidth : .8,
                        layout : 'form',
                        items :   devicemodeField
                    },{
                        columnWidth : .8,
                        layout : 'form',
                        items :   deviceipField
                    },{
                        columnWidth : .8,
                        layout : 'form',
                        items :   deviceportField
                    },{
                        columnWidth : .8,
                        layout : 'form',
                        items :   devicesnmppwdField
                    },{
                        columnWidth : .8,
                        layout : 'form',
                        items :   availableField
                    },{
                        columnWidth : .8,
                        layout : 'form',
                        items :   snmpverFiled
                    },{
                        columnWidth : .8,
                        layout : 'form',
                        items :   authField
                    },{
                        columnWidth : .8,
                        layout : 'form',
                        items :   authpasswordField
                    },{
                        columnWidth : .8,
                        layout : 'form',
                        items :   commonField
                    },{
                        columnWidth : .8,
                        layout : 'form',
                        items :   commonpasswordField
                    }]
                }]
            }]

        });


        if (selectMyForm) {
            var myMask = new Ext.LoadMask(Ext.getBody(),{
                msg : '正在加载数据,请稍后...',
                removeMask:true
            });
            myMask.show();
            Ext.Ajax.request({
                url : 'InterfaceManagerAction_selectDeviceByIp.action',
                params : {
                    dc : new Date()  ,
                    ip:ipValue
                },
                success : function(response, opts) {
                    var data = Ext.util.JSON.decode(response.responseText);
                    idField.setValue(data.id);
                    nameField.setValue(data.name);
                    deviceportField.setValue(data.deviceport);
                    devicesnmppwdField.setValue(data.devicesnmppwd);
                    if(data.auth=='null'){
                        authField.setValue("");
                    }else{
                        authField.setValue(data.auth);
                    }
                    if(data.authpassword=='null'){
                        authpasswordField.setValue("");
                    }else{
                        authpasswordField.setValue(data.authpassword);
                    }
                    if(data.common=='null'){
                        commonField.setValue("");
                    }else{
                        commonField.setValue(data.common);
                    }
                    if(data.commonpassword=='null'){
                        commonpasswordField.setValue("");
                    }else{
                        commonpasswordField.setValue(data.commonpassword);
                    }
                    devicemodeField.setValue(data.devicemode);
                    if(data.available=='on'){
                        availableField.setValue("是");
                    }else{
                        availableField.setValue("否");
                    }
                    snmpverFiled.setValue(data.snmpver);
                    myMask.hide();
                },
                failure : function(response, opts) {
                    myMask.hide();
                    Ext.Msg.alert('', "加载配置数据失败，请重试！");
                }
            });
        }


        var select_Win = new Ext.Window({
            plain : true,
            // layout : 'form',
            resizable : true, // 改变大小
            draggable : true, // 不允许拖动
            closeAction : 'close',// 可被关闭 close or hide
            modal : true, // 模态窗口
            width : 600,
            autoHeight : true,
            title : '查看信息',
            items : [selectMyForm],// 嵌入数据
            buttonAlign : 'center',
            loadMask : true,
            bbar: [
                new Ext.Toolbar.Fill(),
                new Ext.Button ({
                    allowDepress : false,
                    text : '关闭',
                    handler : function() {
                        select_Win.close();
                    }
                })
            ]
        });

        select_Win.show();
    }

    Method.insert = function() {
        var record = grid.getSelectionModel().getSelected();// 点击选择行
        var ipValue = record.get("ip");
        insert(ipValue);
    };
    Method.lookup = function() {
        var record = grid.getSelectionModel().getSelected();// 点击选择行
        var ipValue = record.get("ip");
        lookup(ipValue);
    };

});


var Method= new Object;

function insert() {
    Method.insert();
}

function lookup() {
    Method.lookup();
}





//        //进度条 关闭  效果
//        var pro = function(v) {
//            return function() {
//                if(v == 100) {
//                    Ext.Msg.hide();
//                    Ext.Msg.alert('消息提示', 'ping '+ipstart+'到 '+ipstop+'完成');
//                } else {
//                    var i = v/99;
//                    Ext.Msg.updateProgress(i, Math.round(100*i)+'%');
//                }
//            };
//        };
//
//        for(var i = 1; i < 101; i++) {
//            setTimeout(pro(i), i*30);
//        };


