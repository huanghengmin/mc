var getLogsTime;
var getSystemInfoTime;
var getPlatformInfoTime;
var logs_row_count = 30;
var getSystemInterval = 1000 * 20;
var getLogsInterval = 1000 * 5;
var getSystemInfoInterval = 1000 * 30
Ext.onReady(function() {
    //========================preference Form=============
    /*
     var configForm = new Ext.form.Form({
         labelAlign: 'right',
         labelWidth: 90,
         id:'configForm',
         url:'TestServlet',
         method:'POST'
     });

     configForm.fieldset(
     {legend:'监控信息配置'},
             new Ext.form.TextField({
                 fieldLabel: '监控服务地址',
                 name: 'server_adderess',
                 width:190
             }),

             new Ext.form.TextField({
                 fieldLabel: '系统监控端口',
                 name: 'system_monitor_port',
                 vtype:'alphanum',
                 width:190
             }),

             new Ext.form.TextField({
                 fieldLabel: '审计监控端口',
                 name: 'audit_monitor_port',
                 vtype:'alphanum',
                 width:190
             }),

             new Ext.form.TextField({
                 fieldLabel: '系统监控频率',
                 name: 'interval',
             //vtype:'email',
                 vtype:'alphanum',
                 width:190
             })
             );



     function SaveConfig(btn) {
            if(btn == "yes"){
             var querycallback = {
                 success : responseSuccessInfo,
                 failure : responseFailureInfo
             };

             YAHOO.util.Connect.asyncRequest('POST', "LogService?action=saveConfig", querycallback, configForm.getValues(true));
         }
     }
     var responseSuccessInfo = function(o)
     {
         alert(o.responseText);
     }

     var responseFailureInfo = function(o)
     {
         alert(o.statusText);
     }

     configForm.addButton('保存', function() {
         if (configForm.isValid()) {
          Ext.Msg.confirm('确认?','您确定要保存?',SaveConfig, Ext.MessageBox.buttonText.yes="确认",Ext.MessageBox.buttonText.no="取消");
         }
     });
     configForm.addButton('重置', function() {
         if (configForm.isValid()) {
             configForm.reset();
         }
     });

     configForm.render('preference-form');
     */
    //=======================tabs==================

    var tabs = new Ext.TabPanel('monitor');
    tabs.addTab('platform', "平台监控");
    tabs.addTab('system', "系统资源监控");
    tabs.addTab('logs', "审计信息");
    //tabs.addTab('preference', "监控设置");
    tabs.activate('platform');

    //===========================logs Data====================
    var logRecord = Ext.data.Record.create(
            [{name: 'appName'},
            {name: 'appType'},
            {name: 'network'},
            {name: 'auditLevel'},
            {name: 'ip'},
            {name: 'date'},
            {name: 'sourceDest'},
            {name: 'databaseName'},
            {name: 'tableName'},
            {name: 'statusCode'},
            {name: 'recordcount'},
            {name: 'appInfo'},
            {name: 'destUrl'},
            {name: 'userName'},
            {name: 'operType'},
            {name: 'fileName'},
            {name: 'pkId'}
                    ]);

    // create the Data Store
    var logsDs = new Ext.data.Store({
        reader: new Ext.data.JsonReader({
            root: 'logs',
            totalRecords: '@total'
        }, logRecord)
    });

    function optNetwork(value, p, record) {
        //alert(value);
        var returnString;
        if (value == 't')
            returnString = '内网';
        else if (value == 'f')
            returnString = '外网';
        return returnString;
    }

    function optSourceDest(value, p, record) {
        var returnString;
        if (value == 's')
            returnString = '源端';
        else if (value == 'd')
            returnString = '目标端';
        return returnString;
    }

    function optApptype(value, p, record) {
        var returnString;
        if (value == 'db')
            returnString = '数据同步';
        else if (value == 'proxy')
            returnString = '通用代理';
        else if (value == 'aproxy')
            returnString = '认证代理';
        else if (value == 'file')
            returnString = '文件传输';
        return returnString;
    }

    function optStatusCode(value, p, record) {
        var returnString;
        if (value == '0')
            returnString = '正常';
        else if (value == '－1')
            returnString = '不可知错误';
        else if (value == '-6')
            returnString = '没有实现的方法或接口';
        else if (value == '10011')
            returnString = '字符串为空';
        else if (value == '10012')
            returnString = '字符串索引越界';
        else if (value == '10035')
            returnString = '重复配置';
        else if (value == '10036')
            returnString = '配置不成功';
        else if (value == '10037')
            returnString = '配置变量无效';
        else if (value == '10038')
            returnString = '配置数据为空';
        else if (value == '10039')
            returnString = '没有实现配置接口';
        else if (value == '10040')
            returnString = '没有配置进行配置操作';
        else if (value == '-10012')
            returnString = '网络异常';
        else if (value == '-10013')
            returnString = '目标端处理错误';
        else if (value == '-10014')
            returnString = '数据是空';
        else if (value == '-10015')
            returnString = '代理端已关闭';
        else if (value == '-10016')
            returnString = '操作关键字已经取消';
        return String.format("{0}  <b>{1}</b>", returnString, value);
    }
    /*
    var logsCm = new Ext.grid.ColumnModel([
    {header: "应用名称", width: 80, dataIndex: 'appname',locked: true},
    {header: "应用类型", width: 100, dataIndex: 'apptype' ,renderer:optApptype},
    {header: "审计级别", width: 80, dataIndex: 'level'},
    {header: "日期", width: 150, dataIndex: 'date'},
    {header: "内网/外网", width: 80, dataIndex: 'network',renderer:optNetwork},
    {header: "源/目标", width: 80, dataIndex: 'source_dest',renderer:optSourceDest},
    {header: "数据库", width: 80, dataIndex: 'database'},
    {header: "表", width: 80, dataIndex: 'table'},
    {header: "状态码", width: 120, dataIndex: 'status_code',renderer:optStatusCode},
    {header: "交换记录数", width: 80, dataIndex: 'recordCount'},
    {header: "日志内容", width: 500, dataIndex: 'app_info'},
    {header: "审计服务器", width: 80, dataIndex: 'ip'}
            ]);
    */

    var logsCm = new Ext.grid.ColumnModel([
    {header: "应用名称", width: 100,locked:true, dataIndex: 'appName'},
    {header: "应用类型", width: 100, dataIndex: 'appType',renderer:optApptype},
    {header: "网络位置", width: 100, dataIndex: 'network',renderer:optNetwork},
    {header: "日志等级", width: 100, dataIndex: 'auditLevel'},
    {header: "IP地址", width: 100, dataIndex: 'ip'},
    {header: "目标地址", width: 100, dataIndex: 'destUrl'},
    {header: "登录帐号", width: 100, dataIndex: 'userName'},
    {header: "操作行为", width: 100, dataIndex: 'operType'},
    {header: "入库时间", width: 100, dataIndex: 'date'},
    {header: "源/目标", width: 100, dataIndex: 'sourceDest',renderer:optSourceDest},
    {header: "数据库名", width: 100, dataIndex: 'databaseName'},
    {header: "数据表名", width: 100, dataIndex: 'tableName'},
    {header: "状态代码", width: 100, dataIndex: 'statusCode',renderer:optStatusCode},
    {header: "记录条数", width: 100, dataIndex: 'recordcount'},
    {header: "临时文件名称", width: 100, dataIndex: 'fileName'},
    {header: "出错记录编号", width: 100, dataIndex: 'pkId'},
    {header: "业务信息", width: 100, dataIndex: 'appInfo'}
            ]);
    logsCm.defaultSortable = true;

    // create the grid
    var logsGrid = new Ext.grid.Grid('logs-info', {
        ds: logsDs,
        cm: logsCm
    });

    logsGrid.render();

    var getLogsSuccess = function(o)
    {
        var logs = o.responseText;

        if (logs != null && logs != "") {
            if (logsDs.getTotalCount() >= logs_row_count) {
                logsDs.removeAll();
            }
            var logsData = eval("(" + logs + ")");

            logsDs.loadData(logsData, true);
        }
    };

    var getLogsFailure = function(o) {
        //alert(o.responseText);
    };

    function loadLogs() {
        var logsCallback = {
            success : getLogsSuccess,
            failure : getLogsFailure,
            timeout:3000
        };

        YAHOO.util.Connect.asyncRequest("POST", "AuditService?action=getLogsAction", logsCallback);
        window.clearTimeout(getLogsTime);
        getLogsTime = window.setTimeout(function() {
            loadLogs()
        }, getLogsInterval);
    }

    loadLogs();

    //===========================Query Data====================
    /*
    var debug_level = [
            ['debug','debug'],
            ['info','info'],
            ['warn','warn'],
            ['error','error'],
            ['fatal','fatal'],
            ['all','全部']
            ];

    var appDs = new Ext.data.Store({
        proxy: new Ext.data.HttpProxy({url:'LogService?action=getAppNames',method:'POST'}),

        reader: new Ext.data.JsonReader({
            root: 'apps',
            totalProperty: 'totalCount',
            id:'appid'
        }, [
        {name: 'appname'},
        {name: 'desc'}
                ])
    });

    var dbDs = new Ext.data.Store({
        proxy: new Ext.data.HttpProxy({url:'LogService?action=getDbNames',method:'POST'}),

        reader: new Ext.data.JsonReader({
            root: 'dbs',
            totalProperty: 'totalCount',
            id:'dbid'
        }, [
        {name: 'dbname'},
        {name: 'desc'}
                ])
    });

    var appResultTpl = new Ext.Template(
            '<div class="search-item">',
            '<font color=336699><b>{appname}</b></font> - {desc}',
            '</div>'
            );

    var dbResultTpl = new Ext.Template(
            '<div class="search-item">',
            '<font color=336699><b>{dbname}</b></font> - {desc}',
            '</div>'
            );


    var queryDs = new Ext.data.Store({
    //proxy: new Ext.data.HttpProxy({url:'LogService?action=queryData&'+params,method:'POST'}),

    // create reader that reads the Topic records
        reader: new Ext.data.JsonReader({
            root: 'rows',
            totalProperty: 'totalCount'
        }, logRecord),

    // turn on remote sorting
        remoteSort: true
    });
    
    var queryGrid = new Ext.grid.Grid('query-data', {
        ds: queryDs,
        cm: logsCm,
        selModel: new Ext.grid.RowSelectionModel({singleSelect:true}),
        enableColLock:false,
        loadMask: true
    });

    queryGrid.render();

    var queryGridFoot = queryGrid.getView().getFooterPanel(true);

    var queryPaging = new Ext.PagingToolbar(queryGridFoot, queryDs, {
        pageSize: queryPageSize,
        displayInfo: true,
        displayMsg: '显示审计记录 {0} - {1} of {2}',
        emptyMsg: "没有找到给定条件的审计记录"
    });
    function fetchData(params) {
        queryDs.proxy = new Ext.data.HttpProxy({url:'LogQuery?' + params,method:'POST'})
        queryDs.load({params:{start:0, limit:queryPageSize}});
    }

    var queryForm = new Ext.form.Form({
        labelAlign: 'left'
    });

    queryForm.column(
    {width:300}, // precise column sizes or percentages or straight CSS
            new Ext.form.DateField({
                fieldLabel: '开始日期',
                name: 'startdate',
                width:190,
                format:'y/m/d',
                allowBlank:false
            }),

            new Ext.form.ComboBox({
                fieldLabel: '应用名称',
                hiddenName:'appname',
                store: appDs,
                displayField:'appname',
                typeAhead: false,
                triggerAction: 'all',
                emptyText:'请选择应用名称',
                loadingText: '正在获取应用名称...',
                selectOnFocus:true,
                tpl: appResultTpl,
            //pageSize:10,
                width:190
            }),

            new Ext.form.ComboBox({
                fieldLabel: '审计级别',
                hiddenName:'level',
                store: new Ext.data.SimpleStore({
                    fields: ['value', 'text'],
                    data : debug_level // from states.js
                }),
                displayField:'text',
                typeAhead: true,
                mode: 'local',
                triggerAction: 'all',
                emptyText:'请选择审计级别',
                selectOnFocus:true,
                width:190
            })
            );

    queryForm.column(
    {width:300, style:'margin-left:10px', clear:true}, // apply custom css, clear:true means it is the last column
            new Ext.form.DateField({
                fieldLabel: '结束日期',
                name: 'enddate',
                width:190,
                format:'y/m/d',
                allowBlank:false
            }),

            new Ext.form.ComboBox({
                fieldLabel: '数据库名称',
                hiddenName:'dbname',
                store: dbDs,
                displayField:'dbname',
                typeAhead: true,
                triggerAction: 'all',
                emptyText:'请选择数据库名称',
                loadingText: '正在获取数据库名称...',
                selectOnFocus:true,
            //pageSize:10,
                tpl:dbResultTpl,
                width:190
            })
            );

    queryForm.addButton('查询', function() {
        if (queryForm.isValid()) {
            fetchData(queryForm.getValues(true));
        }
    });
    queryForm.render('query-form');
    */
    //============================Platform==========================

    var platformDs = new Ext.data.Store({
        proxy: new Ext.data.HttpProxy({url:'AuditService?action=getPlatformLogAction',method:'POST',timeout:3000}),
    //proxy: new Ext.data.HttpProxy({url: 'test.xml'}),
        reader: new Ext.data.XmlReader({
            record: 'type'
        //totalProperty: '@total'
        }, [
        {name:'appname',mapping:'@value'},
        {name:'external_source_db',mapping:'external > source > source_db@value'},
        {name:'external_source_status',mapping:'external > source > source_status'},
        {name:'internal_target_db',mapping:'internal > targets > target > target_db@value'},
        {name:'internal_target_status',mapping:'internal > targets > target > target_status'},
        {name:'external_target_db',mapping:'external > targets > target > target_db@value'},
        {name:'external_target_status',mapping:'external > targets > target > target_status'},
        {name:'internal_source_db',mapping:'internal > source > source_db@value'},
        {name:'internal_source_status',mapping:'internal > source > source_status'} ,
        {name:'count',mapping:'count@value'}
                ])
    });


    //简单数据模型
    /*
       var apptype_ds = [
   			['',''],
            ['db','数据库交换'],
            ['file','文件交换'],
            ['proxy','通用代理'],
            ['aproxy','认证代理']
            ];
       */
    // var apptype_ds = platformDs;


    function platformStatusCode(value, p, record) {
        var returnString;
        if (value == '0')
            returnString = '正常';
        else if (value == '-1')
            returnString = '不可知错误';
        else if (value == '2')
            returnString = '配置成功';
        else if (value == '-2')
            returnString = '数据库错误';
        else if (value == '-3')
            returnString = '数据库连接错误';
        else if (value == '-10011')
            returnString = '数据格式错误';
        else if (value == '-10012')
            returnString = '网络异常';
        else if (value == '-10013')
            returnString = '目标端处理失败';
        else if (value == '-10014')
            returnString = '数据交换源端处理失败';
        else if (value == '-10015')
            returnString = '临时表数据无效';
        else if (value == '-10016')
            returnString = '数据交换导出数据错误';
        else if (value == '-10017')
            returnString = '数据交换组装数据错误';
        else if (value == '-10018')
            returnString = '对源表数据处理失败';
        else if (value == '10016')
            returnString = '无效数据';
        else if (value == '-10019')
            returnString = '无效的目标字段';
        else if (value == '-10035')
            returnString = '重复配置错误';
        else if (value == '-10036')
            returnString = '配置失败';
        else if (value == '-10037')
            returnString = '配置变量没有找到错误';
        else if (value == '-10038')
            returnString = '配置数据为空';
        else if (value == '-10040')
            returnString = '没有配置';
        else if (value == '-10041')
            returnString = '没有运行';
        return String.format("{0}  <b>{1}</b>", returnString, value);
    }
    /*
    var reader = new Ext.data.XmlReader({
        xmlData:platformDs.reader.xmlData,
        record: 'types>type>external > targets > target'
    //totalProperty: '@total'
    }, [
    {name:'external_target_db',mapping:'target_db@value'}
            ]);
    */
    //var Ed = Ext.grid.GridEditor;
    var platformCm = new Ext.grid.ColumnModel([
    {header: "应用名称", width: 80, dataIndex: 'appname'},
    {header: "外网源端库名", width: 100, dataIndex: 'external_source_db'},
    {header: "外网源端状态", width: 100, dataIndex: 'external_source_status',renderer:platformStatusCode},
    {header: "内网目标端库名", width: 100, dataIndex: 'internal_target_db'},
    {header: "内网目标端状态", width: 100, dataIndex: 'internal_target_status',renderer:platformStatusCode},
    {header: "内网源端库名", width: 100, dataIndex: 'internal_source_db'},
    {header: "内网源端状态", width: 100, dataIndex: 'internal_source_status',renderer:platformStatusCode},
    {header: "外网目标端库名", width: 100, dataIndex: 'external_target_db'/*,editor:new Ed(new Ext.form.ComboBox({
        fieldLabel: '查询类型',
        hiddenName:'queryType',
        valueField:'external_target_db',
        store: new Ext.data.SimpleStore({
            fields: ['value', 'text'],
            reader : reader// from states.js
        }),
        displayField:'external_target_db',
        typeAhead: true,
        mode: 'remote',
        triggerAction: 'all',
        emptyText:'请选择查询类型',
        selectOnFocus:true,
        editable:false,
        allowBlank:false,
        resizable:true,
        width:190
    }))*/},
    {header: "外网目标端状态", width: 100, dataIndex: 'external_target_status',renderer:platformStatusCode},
    {header: "交换数据量", width: 100, dataIndex: 'count'}
            ]);
    platformCm.defaultSortable = true;
    // create the grid
    //var rsm = new Ext.grid.RowSelectionModel({singleSelect:true});
    var platformGrid = new Ext.grid.EditorGrid('platform-info', {
        ds: platformDs,
        cm: platformCm,
    //selModel: rsm,
        enableColLock:false
    });
    platformGrid.render();

    function loadPlatformInfo() {
        platformDs.load();
        window.clearTimeout(getPlatformInfoTime);
        getPlatformInfoTime = window.setTimeout(function() {
            loadPlatformInfo()
        }, getSystemInfoInterval);
    }

    loadPlatformInfo();


     //============================System==========================

    var systemDs = new Ext.data.Store({
        proxy: new Ext.data.HttpProxy({url:'AuditService?action=getSysLogAction',method:'POST',timeout:3000}),
        reader: new Ext.data.JsonReader({
            root: 'system'
        //totalRecords: 'totalCount'
        }, [
        {name:'network'},
        {name:'cpu'},
        {name:'totalmem'},
        {name:'usemem'},
        {name:'freemem'}
                ])
    });

    function optCpu(value, p, record) {
        return "<b>" + value + "%</b>";
    }

    function optM(value, p, record) {
        return value + "(<b>M</b>)";
    }

    var systemCm = new Ext.grid.ColumnModel([
    {header: "位置", width: 100, dataIndex: 'network',renderer:optNetwork},
    {header: "CPU占用率", width: 100, dataIndex: 'cpu',renderer:optCpu},
    {header: "内存总数", width: 100, dataIndex: 'totalmem',renderer:optM},
    {header: "已用内存", width: 100, dataIndex: 'usemem',renderer:optM},
    {header: "空闲内存", width: 100, dataIndex: 'freemem',renderer:optM}
            ]);
    systemCm.defaultSortable = true;

    var systemGrid = new Ext.grid.EditorGrid('system-info', {
        ds: systemDs,
        cm: systemCm,
        enableColLock:false
    });

    systemGrid.render();

    function loadSystemInfo() {
        systemDs.load();
        window.clearTimeout(getSystemInfoTime);
        getSystemInfoTime = window.setTimeout(function() {
            loadSystemInfo()
        }, getSystemInterval);
    }

    loadSystemInfo();
});

