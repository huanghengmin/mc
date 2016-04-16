Ext.onReady(function() {
    var currentRow = null;

    function optOperation(value, p, record) {
        if (value == '0') {
            return '否';
        } else {
            return '是';
        }
    }

    function optSnmpVer(value, p, record) {
        if (value == 'v1') {
            return 'SNMPV1';
        }
        if (value == 'v2') {
            return 'SNMPV2';
        }
        if (value == 'v3') {
            return 'SNMPV3';
        }
        if (value == 'trap') {
            return 'SNMPTrapV1';
        }
    }

    function optAuth(value, p, record) {
        if (value == 'md5') {
            return 'AuthMD5';
        }
        if (value == 'sha') {
            return 'AuthSHA';
        }
    }

    function optCommon(value, p, record) {
        if (value == 'des') {
            return 'DES';
        }
        if (value == '3des') {
            return '3DES';
        }
        if (value == 'aes128') {
            return 'AES128';
        }
        if (value == 'aes192') {
            return 'AES192';
        }
        if (value == 'aes256') {
            return 'AES256';
        }
    }

    var resourceRecord = Ext.data.Record.create(
        [
            {name: 'id'},
            {name: 'name'},
            {name: 'deviceip'},
            {name: 'deviceport'},
            {name: 'devicesnmppwd'},
            {name: 'available'},
            {name: 'devicemode'},
            {name: 'auth'},
            {name: 'authpassword'},
            {name: 'common'},
            {name: 'commonpassword'},
            {name: 'snmpver'}
        ]);

    // create the Data Store

    var resourceDS = new Ext.data.Store({
        proxy: new Ext.data.HttpProxy({url:'IPlatManager?action=listDeviceAction',method:'POST'}),

        reader: new Ext.data.JsonReader({
            root: 'devices',
            totalRecords: 'totalCount',
            totalProperty: 'totalCount'
        }, resourceRecord)
    });


    var resourceCm = new Ext.grid.ColumnModel([
        {header: "设备编号", width: 100, dataIndex: 'id'},
        {header: "设备名称", width: 200, dataIndex: 'name'},
        {header: "设备IP地址", width: 160, dataIndex: 'deviceip'},
        {header: "设备SNMP服务端口", width: 100, dataIndex: 'deviceport'},
        {header: "设备SNMP服务密码", width: 100, dataIndex: 'devicesnmppwd'},
        {header: "启用", width: 40, dataIndex: 'available',renderer:optOperation},
        {header: "设备型号", width: 60, dataIndex: 'devicemode'},
        {header: "SNMP版本", width: 60, dataIndex: 'snmpver',renderer:optSnmpVer},
        {header: "认证加密算法", width: 120, dataIndex: 'auth',renderer:optAuth},
        {header: "认证加密密钥", width: 120, dataIndex: 'authpassword'},
        {header: "通讯加密算法", width: 120, dataIndex: 'common',renderer:optCommon},
        {header: "通讯加密密钥", width: 120, dataIndex: 'commonpassword'}
    ]);

    resourceCm.defaultSortable = true;

    // create the grid
    var resourceGrid = new Ext.grid.Grid('resourceManage', {
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
        var id = row.data['id'];
        var name = row.data['name'];
        var deviceip = row.data['deviceip'];
        var deviceport = row.data['deviceport'];
        var devicesnmppwd = row.data['devicesnmppwd'];

        var available = row.data['available'];

        var devicemode = row.data['devicemode'];
        var snmpver = row.data['snmpver'];
        var auth = row.data['auth'];
        var authpassword = row.data['authpassword'];
        var common = row.data['common'];
        var commonpassword = row.data['commonpassword'];

        //restoreWindow.init();
        resourceManager.showDialog("edit", id, name, deviceip, deviceport, devicesnmppwd, available, devicemode, snmpver, auth, authpassword, common, commonpassword);

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
        text: '新增设备',
        toggleHandler: addresource
    });

    queryPaging.add('-', {
        pressed: false,
        enableToggle:true,
        text: '编辑设备',
        toggleHandler: editresource
    });
    queryPaging.add('-', {
        pressed: false,
        enableToggle:true,
        text: '删除设备',
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
            var id = row.data['id'];
            var name = row.data['name'];
            var deviceip = row.data['deviceip'];
            var deviceport = row.data['deviceport'];
            var devicesnmppwd = row.data['devicesnmppwd'];

            var available = row.data['available'];

            var devicemode = row.data['devicemode'];
            var snmpver = row.data['snmpver'];
            var auth = row.data['auth'];
            var authpassword = row.data['authpassword'];
            var common = row.data['common'];
            var commonpassword = row.data['commonpassword'];

            //restoreWindow.init();
            resourceManager.showDialog("edit", id, name, deviceip, deviceport, devicesnmppwd, available, devicemode, snmpver, auth, authpassword, common, commonpassword);
        }
    }

    function delresource() {
        if (currentRow === null) {
            Ext.MessageBox.alert('提示', '请选中你要删除的行！.');
        } else {
            var row = resourceDS.data.items[currentRow];
            var id = row.data['id'];

            Ext.Msg.confirm('警告', '确定要删除这些记录吗？', function(btn) {
                if (btn == 'yes') {
                    var ids = "";


                    Ext.Ajax.request({
                        url : 'IPlatManager?action=delDeviceAction',
                        params : {
                            id : id
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
            showDialog : function(option, id, name, deviceip, deviceport, devicesnmppwd, available, devicemode, snmpver, auth, authpassword, common, commonpassword) {

                if (!dialog) { // lazy initialize the dialog and only create it once
                    dialog = new Ext.LayoutDialog("dlgErt", {
                        modal: true,
                        width:500,
                        height:420,
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
                            YAHOO.util.Connect.asyncRequest('POST', 'IPlatManager?action=saveDeviceAction', querycallback, myForm.getValues(true));
                        }
                    }

                    var responseSuccessInfo = function(o) {
                        //Ext.MessageBox.alert('成功', '操作完成.');
                        dialog.hide();
                        //列表数据
                        resourceDS.load({params:{start:0, limit:queryPageSize}});
                    };

                    var responseFailureInfo = function(o) {
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

                    // create the Data Store

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
                    //--- build a form
                    fs = new Ext.form.Form({
                        labelAlign: 'left',
                        labelWidth: 120
                    });
                    var idField = new Ext.form.TextField({
                        fieldLabel: '设备编号',
                        name: 'id',
                        width:190,
                        allowBlank:true
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
                        width:190,
                        allowBlank:false
                    });
                    var deviceipField = new Ext.form.TextField({
                        fieldLabel: '设备IP',
                        name: 'deviceip',
                        width:190,
                        grow: false

                    });
                    var deviceportField = new Ext.form.TextField({
                        fieldLabel: '设备SNMP服务端口',
                        name: 'deviceport',
                        allowBlank:false,
                        width:80,
                        grow: false,
                        blankText:'161',
                        emptyText:'161',
                        value:'161'
                    });
                    var devicesnmppwdField = new Ext.form.TextField({
                        fieldLabel: '设备SNMP服务密码',
                        name: 'devicesnmppwd',
                        allowBlank:false,
                        width:80,
                        grow: false,
                        blankText:'public',
                        emptyText:'public',
                        value:'public'
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
                        readOnly:true,
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
                        readOnly:true,
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

                    fs.fieldset(
                        {legend:'设备信息'}, idField
                        ,
                        nameField
                        ,
                        devicemodeField
                        ,
                        deviceipField
                        ,
                        deviceportField
                        ,
                        devicesnmppwdField
                        ,
                        availableField
                        ,
                        snmpverFiled
                        ,
                        authField
                        ,
                        authpasswordField
                        ,
                        commonField
                        , commonpasswordField);
                }


                dialog.show();
                fs.render('formErt');
                fs.findField('id').setValue(id);
                fs.findField('devicemode').setValue(devicemode);
                fs.findField('name').setValue(name);
                fs.findField('available').setValue(available);
                fs.findField('deviceip').setValue(deviceip);
                fs.findField('deviceport').setValue(deviceport);
                fs.findField('devicesnmppwd').setValue(devicesnmppwd);
                fs.findField('snmpver').setValue(snmpver);
                fs.findField('auth').setValue(auth);
                fs.findField('authpassword').setValue(authpassword);
                fs.findField('common').setValue(common);
                fs.findField('commonpassword').setValue(commonpassword);

                myForm = fs;
            }
        };
    }();
});