Ext.onReady(function() {
    var currentRow = null;
    function optOperation(value, p, record) {
        if (value == '0') {
            return '否';
        } else {
            return '是';
        }
    }

    function optProxytype(value, p, record) {
        if (value == 'v1') {
            return 'SNMPV1';
        }
        if (value == 'v2') {
            return 'SNMPV2';
        }
        if (value == 'v3') {
            return 'SNMPV3';
        }
        if (value == 'trapv1') {
            return 'SNMPTrapV1';
        }
        if (value == 'trapv2') {
            return 'SNMPTrapV2';
        }
        if (value == 'trapv3') {
            return 'SNMPTrapV3';
        }
    }

    var resourceRecord = Ext.data.Record.create(
            [{name: 'cpuuse'},
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
    
    // create the Data Store

    var resourceDS = new Ext.data.Store({
        proxy: new Ext.data.HttpProxy({url:'IPlatManager?action=listSnmpOIDAction',method:'POST'}),

        reader: new Ext.data.JsonReader({
            root: 'snmpoids',
            totalRecords: 'totalCount',
            totalProperty: 'totalCount'
        }, resourceRecord)
    });
    
    
    var resourceCm = new Ext.grid.ColumnModel([
    {header: "名称", width: 100, dataIndex: 'name'},
    {header: "设备厂商", width: 100, dataIndex: 'company'},
    {header: "设备类型", width: 100, dataIndex: 'type'},
    {header: "CPU使用率", width: 100, dataIndex: 'cpuuse'},
    {header: "磁盘总量", width: 100, dataIndex: 'disktotal'},
    {header: "磁盘用量", width: 100, dataIndex: 'diskuse'},
    {header: "内存总量", width: 100, dataIndex: 'memtotal'},
    {header: "内存用量", width: 100, dataIndex: 'memuse'},
    {header: "当前连接数", width: 100, dataIndex: 'curconn'},
    {header: "SNMP版本", width: 150, dataIndex: 'snmpver',renderer:optProxytype}
            ]);

    resourceCm.defaultSortable = true;

    // create the grid
    var resourceGrid = new Ext.grid.Grid('snmpoidManage', {
        ds: resourceDS,
        cm: resourceCm
    });

    resourceGrid.addListener('rowdblclick', launchDetail);
    resourceGrid.addListener('rowclick', rowClick);
    resourceGrid.render();

    function rowClick(grid, rowIndex, columnIndex, e) {
        currentRow = rowIndex;
    }
    //resourceDS.load();
    function launchDetail(grid, rowIndex, columnIndex, e) {
        //var pudID=this.ds.getAt(rowIndex).data["pudID"];
        //alert("apptype--"+resourceDS.getAt(rowIndex).data["username"]);
        //alert(currentRow);
        var row = resourceDS.data.items[rowIndex];
        var name = row.data['name'];
        var company = row.data['company'];
        var type = row.data['type'];
        var cpuuse = row.data['cpuuse'];
        var disktotal = row.data['disktotal'];
        var diskuse = row.data['diskuse'];
        var memtotal = row.data['memtotal'];
        var memuse = row.data['memuse'];
        var curconn = row.data['curconn'];
        var snmpver = row.data['snmpver'];
      //restoreWindow.init();
        resourceManager.showDialog("edit",name,company, type,cpuuse, disktotal,diskuse, memtotal, memuse,curconn, snmpver);

    }

    var resourceGridHeader = resourceGrid.getView().getHeaderPanel(true);

    var queryPaging = new Ext.PagingToolbar(resourceGridHeader, resourceDS, {
        pageSize: queryPageSize,
        displayInfo: true,
        displayMsg: '显示记录 {0} - {1} of {2}',
        emptyMsg: "没有记录"
    });

    queryPaging.add('-', {
        pressed: false,
        enableToggle:true,
        text: '新增SNMPOID',
        toggleHandler: addresource
    });

    queryPaging.add('-', {
        pressed: false,
        enableToggle:true,
        text: '编辑SNMPOID',
        toggleHandler: editresource
    });
    queryPaging.add('-', {
        pressed: false,
        enableToggle:true,
        text: '删除SNMPOID',
        toggleHandler: delresource
    });

    resourceDS.load({params:{start:0, limit:queryPageSize}});

    function addresource() {
        resourceManager.showDialog("new");
    }

    function editresource() {
        if (currentRow === null) {
            Ext.MessageBox.alert('提示', '请先选择一行数据.');
        } else {
            var row = resourceDS.data.items[currentRow];
            var name = row.data['name'];
            var company = row.data['company'];
            var type = row.data['type'];
            var cpuuse = row.data['cpuuse'];
            var disktotal = row.data['disktotal'];
            var diskuse = row.data['diskuse'];
            var memtotal = row.data['memtotal'];
            var memuse = row.data['memuse'];
            var curconn = row.data['curconn'];
            var snmpver = row.data['snmpver'];
      //restoreWindow.init();
            resourceManager.showDialog("edit",name,company,type, cpuuse, disktotal,diskuse, memtotal, memuse,curconn, snmpver);
        }
    }
    function delresource() {
        if (currentRow === null) {
            Ext.MessageBox.alert('提示', '请先选择一行数据.');
        } else {
        	var row = resourceDS.data.items[currentRow];        
        	var name = row.data['name'];
              		Ext.Msg.confirm('警告', '确定要删除这些记录吗？', function(btn) {
        						if (btn == 'yes') {
        							Ext.Ajax.request({
        								url : 'IPlatManager?action=delSnmpOIDAction',
        								params : {
        									name : name
        								},
        								success : function(response, options) {
        									resourceDS.load({params:{start:0, limit:queryPageSize}});
        								}
        							});
        						} else {
        							return false;
        						}
        					});
                }
            }
        
   
    var resourceManager = function() {
        // define some private variables
        var dialog, showLink, myForm, fs;
        var id,resvalue,description;
        
  // return a public interface
        return {
            init: function() {
                myForm = Ext.get("formErt");
            },
            showDialog : function(option, name,company,type, cpuuse, disktotal,diskuse, memtotal, memuse,curconn, snmpver) {

                if (!dialog) { // lazy initialize the dialog and only create it once
                    dialog = new Ext.LayoutDialog("dlgErt", {
                        modal: true,
                        width:500,
                        height:385,
                        shadow:true,
                        minWidth:300,
                        resizable:false,
                        minHeight:300,
                        proxyDrag: true,
                        center: {
                            margins:{left:3,top:3,right:3,bottom:3},
                            autoScroll:false
                        }
                    });

                    dialog.addKeyListener(27, dialog.hide, dialog);

                    function SaveConfig(btn) {
                        if (btn == "yes") {
                            var querycallback = {
                                success : responseSuccessInfo,
                                failure : responseFailureInfo
                            };
                            YAHOO.util.Connect.asyncRequest('POST', 'IPlatManager?action=saveSnmpOIDAction', querycallback, myForm.getValues(true));
                        }
                    }
                    var responseSuccessInfo = function(o)
                    {
                        //Ext.MessageBox.alert('成功', '操作完成.');
                        dialog.hide();
        	//列表数据
                        resourceDS.load({params:{start:0, limit:queryPageSize}});
                    };

                    var responseFailureInfo = function(o)
                    {
                        Ext.MessageBox.alert('失败', '操作失败.');
                    };

                    dialog.addButton('保存', function() {
                        //if (myForm.isValid()) {
                            Ext.Msg.confirm('确认?', '您确定要保存?', SaveConfig, Ext.MessageBox.buttonText.yes = "确认", Ext.MessageBox.buttonText.no = "取消");
                        //}
                    }, dialog);


                    dialog.addButton('关闭', dialog.hide, dialog);

        //--- setup dialog layout
                    var layout = dialog.getLayout();
                    layout.beginUpdate();
                    layout.add('center', new Ext.ContentPanel('panelErt', {title: 'Edit Ert'}));
                    layout.endUpdate();


                    var typeResultTpl = new Ext.Template(
                            '<div>',
                            '<font color=336699 size=2><b>{typeid}</b> - {typename}</font> ',
                            '</div>'
                            );

                    var charsetDs = [
                            ['Cisco','思科'],
                            ['huawei','华为'],
                            ['3com','3com'],
                            ['h3c','华三'],
                            ['bdcom','博达'],
                            ['topsec','天融信'],
                            ['leadsec','联想网御'],
                            ['venustech','启明星辰'],
                            ['pcserver','服务器'],
                            ['radware','radware负载均衡'],
                            ['koal','格尔'],
                            ['zhyu','中宇万通'],
                            ['hawksec','云鹰合创'],
                            ['netchina','中网'],
                             ['legendsec','网御神州']
                            ];
                   
                    var proxytypeDs = [
                            ['v1','v1'],
                            ['v2','v2'],
                            ['v3','v3'],
                             ['trapv1','trapv1'],
                             ['trapv2','trapv2'],
                             ['trapv3','trapv3']
                            ];
        //--- build a form
                    fs = new Ext.form.Form({
                        labelAlign: 'left',
                        labelWidth: 80
                    });

                    fs.fieldset(
                    {legend:'SNMPOID信息'},
                            new Ext.form.TextField({
                                fieldLabel: '名称',
                                name: 'name',
                                width:190,
                                allowBlank:false
                            }),
                            new Ext.form.TextField({
                                fieldLabel: '设备类型',
                                name: 'type',
                                width:190,
                                allowBlank:false
                            }),
                            new Ext.form.ComboBox({
                                fieldLabel: '设备厂商',
                                hiddenName:'company',
                                store: new Ext.data.SimpleStore({
                                    fields: ['value', 'text'],
                                    data : charsetDs
                                }),
                                displayField:'text',
                                valueField:'value',
                                typeAhead: true,
                                editable:true,
                                allowBlank:false,
                                mode: 'local',
                                triggerAction: 'all',
                                emptyText:'请选择厂商',
                                selectOnFocus:true,
                                width:190
                            }),
                            new Ext.form.TextField({
                                fieldLabel: 'CPU使用率',
                                name: 'cpuuse',
                                width:190,
                                allowBlank:true
                            }),

                            new Ext.form.TextField({
                                fieldLabel: '磁盘总量',
                                name: 'disktotal',
                                width:190,
                                allowBlank:false
                            }),

                            new Ext.form.TextField({
                                fieldLabel: '磁盘用量',
                                name: 'diskuse',
                                width:190,
                                grow: false
                            }),
                            new Ext.form.TextField({
                                fieldLabel: '内存总量',
                                name: 'memtotal',
                                width:190,
                                allowBlank:false
                            }),

                            new Ext.form.TextField({
                                fieldLabel: '内存用量',
                                name: 'memuse',
                                width:190,
                                grow: false
                            }),
                            new Ext.form.TextField({
                                fieldLabel: '当前连接数',
                                name: 'curconn',
                                width:190,
                                grow: false
                            }),
                            new Ext.form.ComboBox({
                                fieldLabel: 'SNMP版本',
                                hiddenName:'snmpver',
                                store: new Ext.data.SimpleStore({
                                    fields: ['value', 'text'],
                                    data : proxytypeDs
                                }),
                                displayField:'text',
                                valueField:'value',
                                typeAhead: true,
                                editable:true,
                                mode: 'local',
                                triggerAction: 'all',
                                emptyText:'请选择SNMP版本',
                                selectOnFocus:true,
                                width:190
                            }));
                }

                dialog.show();
                fs.render('formErt');
                fs.findField('name').setValue(name);
                fs.findField('company').setValue(company);
                fs.findField('cpuuse').setValue(cpuuse);
                fs.findField('type').setValue(type);
                fs.findField('disktotal').setValue(disktotal);
                fs.findField('diskuse').setValue(diskuse);
                fs.findField('snmpver').setValue(snmpver);
                fs.findField('memtotal').setValue(memtotal);
                fs.findField('memuse').setValue(memuse);
                fs.findField('curconn').setValue(curconn);
                myForm = fs;
            }
        };
    }();
});