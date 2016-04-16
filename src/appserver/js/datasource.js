Ext.onReady(function() {
    var fs = new Ext.form.Form({
        labelAlign: 'right',
        buttonAlign:'left',
        labelWidth: 110,

    // configure how to read the XML Data
        reader: new Ext.data.JsonReader({
            root: 'ds',
            totalRecords: 'totalCount'
        }, [
        {name: 'driver'},
        {name: 'driverUrl'},
        {name: 'user'},
        {name: 'password'},
        {name: 'maximumConnectionCount'},
        {name: 'minimumConnectionCount'},
        {name: 'maximumConnectionLifetime'},
        {name: 'maximumActiveTime'},
        {name: 'houseKeepingTestSql'}
                ])
    });

    fs.fieldset(
    {legend:'连接池配置信息'},
            new Ext.form.TextField({
                fieldLabel: '数据库驱动',
                name: 'driver',
                width:190
            }),
            new Ext.form.TextField({
                fieldLabel: '数据库URL',
                name: 'driverUrl',
                width:190
            }),
            new Ext.form.TextField({
                fieldLabel: '用户账户',
                name: 'user',
                width:190
            }),
            new Ext.form.TextField({
                fieldLabel: '用户口令',
                name: 'password',
                inputType:'password',
                width:190
            }),
            new Ext.form.TextField({
                fieldLabel: '最大连接数',
                name: 'maximumConnectionCount',
                width:190
            }),
            new Ext.form.TextField({
                fieldLabel: '最小连接数',
                name: 'minimumConnectionCount',
                width:190
            }),
            new Ext.form.TextField({
                fieldLabel: '连接最大生命周期',
                name: 'maximumConnectionLifetime',
                width:190
            }),
            new Ext.form.TextField({
                fieldLabel: '最大活动时间',
                name: 'maximumActiveTime',
                width:190
            }),
            new Ext.form.TextArea({
                fieldLabel: '连接测试语句',
                name: 'houseKeepingTestSql',
                width:190,
                grow: false,
                preventScrollbars:false
            }));

    fs.addButton("连接测试", function() {
        if (fs.isValid()) {
            var querycallback = {success:testSuccessInfo, failure:testFailureInfo,timeout:10000};
            YAHOO.util.Connect.asyncRequest("POST", "AuditService?action=testConnAction&" + fs.getValues(true), querycallback);
        }
    });

    var testSuccessInfo = function (o) {
        Ext.MessageBox.alert("成功", o.responseText);
					//Ext.MessageBox.hide();
    };
    var testFailureInfo = function (o) {
        Ext.MessageBox.alert("错误", o.responseText);
    };

    fs.addButton("更新配置", function() {
        if (fs.isValid())
            Ext.Msg.confirm("\u786e\u8ba4?", "\u60a8\u786e\u5b9a\u8981\u4fdd\u5b58?", SaveDS, Ext.MessageBox.buttonText.yes = "\u786e\u8ba4", Ext.MessageBox.buttonText.no = "\u53d6\u6d88");
    });

    function SaveDS(btn) {
        if (btn == 'yes')
            fs.submit({url:'AuditService?action=editDsCfgAction',method:'POST',params:fs.getValues(true),clientValidation:false});
    }

   // fs.addButton("执行数据库初始化", function() {
   //     Ext.Msg.confirm("\u786e\u8ba4?", "您确定要创建审计数据库吗?", SaveInitConfig, Ext.MessageBox.buttonText.yes = "\u786e\u8ba4", Ext.MessageBox.buttonText.no = "\u53d6\u6d88");
    //});

    function SaveInitConfig(btn) {
        if (btn == "yes") {
            var querycallback = {success:initSuccessInfo, failure:initFailureInfo,timeout:10000};
            YAHOO.util.Connect.asyncRequest("POST", "AuditService?action=initAction", querycallback);
            Ext.MessageBox.progress("进度信息","正在创建数据库...");
        }
    }
    var initSuccessInfo = function (o) {
        Ext.MessageBox.hide();
        Ext.MessageBox.alert("成功", "系统初始化成功.");
					//Ext.MessageBox.hide();
    };
    var initFailureInfo = function (o) {
        Ext.MessageBox.hide();
        Ext.MessageBox.alert("错误", o.responseText);
    };

    fs.load({url:'AuditService?action=listDsCfgAction', waitMsg:'正在加载系统配置...'});

    fs.render('datasource');
})
        ;