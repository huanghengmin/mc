Ext.onReady(function() {
    //-----------------------TAB-----------
    var tabs = new Ext.TabPanel('configTab');
    //tabs.addTab('initTab', "初始化配置");
    tabs.addTab('auditConfigTab', "审计入库配置");
    //tabs.addTab('dbConfigTab', "数据库配置");
    tabs.addTab('autoDataTab', "自动迁移策略配置");
    tabs.addTab('manualDataTab', "手动迁移");
    tabs.activate('auditConfigTab');

    /*
   var initForm = new Ext.form.Form({
       labelAlign: 'right',
       buttonAlign:'left',
       labelWidth: 110
   });

   initForm.fieldset(
   {legend:'初始化属性'},
           new Ext.form.Checkbox({
               fieldLabel: '是否覆盖现有表',
               name:'isCover',
               width:190
           }));

   initForm.addButton("执行数据库初始化", function() {
       if (initForm.isValid())
           Ext.Msg.confirm("\u786e\u8ba4?", "\u60a8\u786e\u5b9a\u8981\u4fdd\u5b58?", SaveInitConfig, Ext.MessageBox.buttonText.yes = "\u786e\u8ba4", Ext.MessageBox.buttonText.no = "\u53d6\u6d88");
   });

   function SaveInitConfig(btn) {
       if (btn == 'yes')
           initForm.submit({url:'AuditService?action=initAction',method:'POST',params:initForm.getValues(true),clientValidation:false});
   }


   initForm.render("init");
    */


    var configForm = new Ext.form.Form({
        labelAlign: 'right',
        buttonAlign:'left',
        labelWidth: 90,
        reader: new Ext.data.JsonReader({
            root: 'config',
            totalRecords: 'totalCount'
        }, [
        {name: 'auditon'},
        {name: 'serverip'},
        {name: 'auditPort'},
        {name: 'auditInterval'}])
    });

    var auditon_ds = [
            ['false','关闭'],
            ['true','开启']
            ];

    var auditon = new Ext.form.ComboBox({
        fieldLabel: '审计入库状态',
        hiddenName:'auditon',
        valueField:'value',
        store: new Ext.data.SimpleStore({
            fields: ['value', 'text'],
            data : auditon_ds
        }),
        displayField:'text',
        typeAhead: true,
        mode: 'local',
        triggerAction: 'all',
        emptyText:'请选择审计状态',
        selectOnFocus:true,
        editable:false,
        allowBlank:false,
        resizable:false,
        width:190
    });

    configForm.fieldset(
    {legend:'审计配置信息'},
            auditon,

            new Ext.form.TextField({
                fieldLabel: '审计服务器',
                name: 'serverip',
                allowBlank:false,
                width:190
            }),

            new Ext.form.TextField({
                fieldLabel: '服务端口',
                name: 'auditPort',
                allowBlank:false,
                width:190
            }),
            new Ext.form.TextField({
                fieldLabel: '入库频率(毫秒)',
                name: 'auditInterval',
                allowBlank:false,
                width:190
            }));

    configForm.addButton("保存", function() {
        if (configForm.isValid())
            Ext.Msg.confirm("\u786e\u8ba4?", "\u60a8\u786e\u5b9a\u8981\u4fdd\u5b58?", SaveConfig, Ext.MessageBox.buttonText.yes = "\u786e\u8ba4", Ext.MessageBox.buttonText.no = "\u53d6\u6d88");
    });

    function SaveConfig(btn) {
        if (btn == 'yes')
            configForm.submit({url:'AuditService?action=editCfgAction',method:'POST',params:configForm.getValues(true),clientValidation:false});
    }


    configForm.load({url:'AuditService?action=listCfgAction', waitMsg:'正在加载配置数据...'});

    configForm.render('auditConfig');

    var month_day_ds = [
            ['1','1日'],
            ['2','2日'],
            ['3','3日'],
            ['4','4日'],
            ['5','5日'],
            ['6','6日'],
            ['7','7日'],
            ['8','8日'],
            ['9','9日'],
            ['10','10日'],
            ['11','11日'],
            ['12','12日'],
            ['13','13日'],
            ['14','14日'],
            ['15','15日'],
            ['16','16日'],
            ['17','17日'],
            ['18','18日'],
            ['19','19日'],
            ['20','20日'],
            ['21','21日'],
            ['22','22日'],
            ['23','23日'],
            ['24','24日'],
            ['25','25日'],
            ['26','26日'],
            ['27','27日'],
            ['28','28日'],
            ['29','29日'],
            ['30','30日'],
            ['31','31日']
            ];

    var week_day_ds = [
            ['1','周一'],
            ['2','周二'],
            ['3','周三'],
            ['4','周四'],
            ['5','周五'],
            ['6','周六'],
            ['7','周日']
            ];

    var day_time_ds = [
            ['0','0时'],
            ['1','1时'],
            ['2','2时'],
            ['3','3时'],
            ['4','4时'],
            ['5','5时'],
            ['6','6时'],
            ['7','7时'],
            ['8','8时'],
            ['9','9时'],
            ['10','10时'],
            ['11','11时'],
            ['12','12时'],
            ['13','13时'],
            ['14','14时'],
            ['15','15时'],
            ['16','16时'],
            ['17','17时'],
            ['18','18时'],
            ['19','19时'],
            ['20','20时'],
            ['21','21时'],
            ['22','22时'],
            ['23','23时']
            ];

    //---------------------autoDataPolicy
    var autoPolicyForm = new Ext.form.Form({
        labelAlign: 'right',
        buttonAlign:'left'

    });

    var cBox = new Ext.form.Checkbox({
        fieldLabel: '',
        name: 'hasCondition',
        allowBlank:true
    });

    cBox.on('check', mClick);

    function mClick(box, checked) {
        if (checked) {
            migrationCount.enable();
            leftCount.enable();
        } else {
            migrationCount.disable();
            leftCount.disable();
        }
    }

    autoPolicyForm.fieldset({legend:'自动迁移参数'});
    autoPolicyForm.column(
    {width:80,labelSeparator:'',labelWidth: 50},
            new Ext.form.TextField({
                inputType:'radio',
                fieldLabel: '每日',
                name: 'repeatModel',
                allowBlank:false,
                width:15,
                value:'every_day'
            }),
            new Ext.form.TextField({
                inputType:'radio',
                fieldLabel: '每周',
                name: 'repeatModel',
                allowBlank:false,
                width:15,
                value:'every_week'
            }),
            new Ext.form.TextField({
                inputType:'radio',
                fieldLabel: '每月',
                name: 'repeatModel',
                allowBlank:false,
                width:15,
                value:'every_month'
            })
//            new Ext.form.TextField({
//                hidden:true
//            })
//            ,cBox
            );


//    var migrationCount = new Ext.form.TextField({
//        fieldLabel: '迁移上限条件(条)',
//        name: 'migrationAmount',
//        allowBlank:false,
//        disabled:true,
//        width:190
//    });
//    var leftCount = new Ext.form.TextField({
//        fieldLabel: '迁移剩余数量(条)',
//        name: 'leftCount',
//        allowBlank:false,
//        disabled:true,
//        width:190
//    });

    autoPolicyForm.column(
    {width:450,labelSeparator:'',lableWidth:'120'},
            new Ext.form.TextField({
                hidden:true
            }),
            new Ext.form.ComboBox({
                fieldLabel: '',
                hiddenName:'weekDay',
                valueField:'value',
                store: new Ext.data.SimpleStore({
                    fields: ['value', 'text'],
                    data : week_day_ds
                }),
                displayField:'text',
                typeAhead: true,
                mode: 'local',
                triggerAction: 'all',
                emptyText:'请选择日期',
                selectOnFocus:true,
                editable:false,
                allowBlank:true,
                resizable:true,
                width:190
            }),

            new Ext.form.ComboBox({
                fieldLabel: '',
                hiddenName:'monthDay',
                valueField:'value',
                store: new Ext.data.SimpleStore({
                    fields: ['value', 'text'],
                    data : month_day_ds
                }),
                displayField:'text',
                typeAhead: true,
                mode: 'local',
                triggerAction: 'all',
                emptyText:'请选择日期',
                selectOnFocus:true,
                editable:false,
                allowBlank:true,
                resizable:true,
                width:190
            }),
            new Ext.form.ComboBox({
                fieldLabel: '',
                hiddenName:'time',
                valueField:'value',
                store: new Ext.data.SimpleStore({
                    fields: ['value', 'text'],
                    data : day_time_ds
                }),
                displayField:'text',
                typeAhead: true,
                mode: 'local',
                triggerAction: 'all',
                emptyText:'请选择时间',
                selectOnFocus:true,
                editable:false,
                allowBlank:false,
                resizable:true,
                width:190
            })
//            ,migrationCount,
//            leftCount
            );
    autoPolicyForm.end();

    autoPolicyForm.addButton("保存", function() {
        if (autoPolicyForm.isValid())
            Ext.Msg.confirm("\u786e\u8ba4?", "\u60a8\u786e\u5b9a\u8981\u4fdd\u5b58?", SaveAutoPolicy, Ext.MessageBox.buttonText.yes = "\u786e\u8ba4", Ext.MessageBox.buttonText.no = "\u53d6\u6d88");
    });

    function SaveAutoPolicy(btn) {
        if (btn == 'yes')
            autoPolicyForm.submit({url:'AuditService?action=autoMigrationAction',method:'POST',params:autoPolicyForm.getValues(true),clientValidation:false});
    }

    autoPolicyForm.render('autoMigrationPolicy');

    //手动迁移配置
    var manualPolicyForm = new Ext.form.Form({
        labelAlign: 'right',
        buttonAlign:'left'

    });
    var allR = new Ext.form.Radio({
        fieldLabel: '全部记录',
        name: 'condition',
        allowBlank:false,
        width:100
    });

    var dateR = new Ext.form.Radio({
        fieldLabel: '日期(之前)',
        name: 'condition',
        allowBlank:false,
        width:100
    })

    allR.on("check", allRChecked, allR);
    dateR.on("check", dateRChecked, dateR);

    manualPolicyForm.fieldset({legend:'手动迁移参数'});
    manualPolicyForm.column(
    {width:200,labelSeparator:'',labelWidth: 80},
            new Ext.form.Checkbox({
                fieldLabel: '正常记录',
                name: 'normal',
                allowBlank:true,
                width:100,
                value:'normal_audit'
            }),
            allR,
            dateR
            );

    var dateF = new Ext.form.DateField({
        fieldLabel: '开始日期',
        name: 'date',
        width:110,
        format:'ymd',
        editable:false,
    //emptyText:'请选择开始日期',
        allowBlank:false
    })

    var move_type = new Ext.form.TextField({
        inputType:'hidden',
        name:'moveType'
    })
    manualPolicyForm.column(
    {width:200,labelSeparator:'',labelWidth: 80},
            new Ext.form.Checkbox({
                fieldLabel: '错误记录',
                name: 'error',
                allowBlank:true,
                width:110,
                value:'error_audit'
            }),
            move_type
            ,
            dateF
            );

    manualPolicyForm.end();

    function allRChecked(radio, checked) {
        if (checked) {
            dateF.disable();
            dateR.checked = false;
            move_type.setValue("A");
        }
    }

    function dateRChecked(radio, checked) {
        if (checked) {
            dateF.enable();
            allR.checked = false;
            move_type.setValue("D");
        }
    }

    manualPolicyForm.addButton("执行", function() {
        if (manualPolicyForm.isValid())
            Ext.Msg.confirm("\u786e\u8ba4?", "确认执行此迁移吗?", executePolicy, Ext.MessageBox.buttonText.yes = "\u786e\u8ba4", Ext.MessageBox.buttonText.no = "\u53d6\u6d88");
    });

    function executePolicy(btn) {
        if (btn == 'yes') {
            var querycallback = {success:responseSuccessInfo, failure:responseFailureInfo};
            YAHOO.util.Connect.asyncRequest("POST", "AuditService?action=manualMigrationAction", querycallback, manualPolicyForm.getValues(true));
        }
    }

    var responseSuccessInfo = function (o) {
        Ext.MessageBox.alert("\u6210\u529f", o.responseText);
    };
    var responseFailureInfo = function (o) {
        Ext.MessageBox.alert("\u5931\u8d25", o.responseText);
    };

    manualPolicyForm.render('manualMigrationPolicy');
});