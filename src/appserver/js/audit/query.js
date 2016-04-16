Ext.onReady(function() {
	//-----------------------TABL-----------
	var tabs = new Ext.TabPanel('queryTab');
    tabs.addTab('dbQueryTab', "数据库同步查询");
    tabs.addTab('proxyQueryTab', "通用代理查询");
    tabs.addTab('aProxyQueryTab', "认证代理查询");
    tabs.activate('dbQueryTab');
    //---------------------------FORMS----------------------      
	//简单数据模型
    /*
       var apptype_ds = [
   			['',''],
            ['db','数据库同步'],
            //['file','文件交换'],
            ['proxy','通用代理'],
            ['aproxy','认证代理']
            ];
    
    var appname_ds = [
    		['',''],
            ['app1','app1'],
            ['app2','app1'],
            ['app3','app1'],
            ['app4','app1']
            ]; 
     */
    var normal_error_ds = [
            ['normal','正常记录'],
            ['error','错误记录']
            ]; 
            
    var network_ds = [
    		['',''],
            ['t','内网'],
            ['f','外网']
            ];  
            
	var dbForm = new Ext.form.Form({
        labelAlign: 'left',
        buttonAlign:'left'
    });       
   
    dbForm.fieldset({legend:"查询信息"}, 
    	new Ext.form.ComboBox({
                fieldLabel: '查询类型',
                hiddenName:'queryType',
                valueField:'value',
                store: new Ext.data.SimpleStore({
                    fields: ['value', 'text'],
                    data : normal_error_ds // from states.js
                }),
                displayField:'text',
                typeAhead: true,
                mode: 'local',
                triggerAction: 'all',
                emptyText:'请选择查询类型',
                selectOnFocus:true,
                editable:false,
                allowBlank:false,
                resizable:true,
                width:190
            }),
                       
            new Ext.form.TextField({
              fieldLabel: '应用名称',
              name:'appName',
              width:190,
              allowBlank:true
          	}),

            new Ext.form.ComboBox({
                fieldLabel: '网络位置',
                hiddenName:'network',
                valueField:'value',
                store: new Ext.data.SimpleStore({
                    fields: ['value', 'text'],
                    data : network_ds
                }),
                displayField:'text',
                typeAhead: true,
                mode: 'local',
                triggerAction: 'all',
                emptyText:'请选择网络位置',
                selectOnFocus:true,
                allowBlank:true,
                editable:false,
                resizable:true,
                width:190
            }),
            /*
            new Ext.form.ComboBox({
                fieldLabel: '数据库名',
                hiddenName:'databaseName',
                valueField:'value',
                store: new Ext.data.SimpleStore({
                    fields: ['value', 'text'],
                    data : appname_ds
                }),
                displayField:'text',
                typeAhead: true,
                mode: 'local',
                triggerAction: 'all',
                emptyText:'请选择数据库名',
                selectOnFocus:true,
                allowBlank:true,
                resizable:true,
                width:190
            }),
            */
            new Ext.form.TextField({
              fieldLabel: '数据库名',
              name:'databaseName',
              width:190,
              allowBlank:true
          	}),
            /*
            new Ext.form.ComboBox({
                fieldLabel: '数据表名',
                hiddenName:'tableName',
                valueField:'value',
                store: new Ext.data.SimpleStore({
                    fields: ['value', 'text'],
                    data : appname_ds
                }),
                displayField:'text',
                typeAhead: true,
                mode: 'local',
                triggerAction: 'all',
                emptyText:'请选择数据表名',
                selectOnFocus:true,
                allowBlank:true,
                resizable:true,
                width:190
            }),
            */
            new Ext.form.TextField({
              fieldLabel: '数据表名',
              name:'tableName',
              width:190,
              allowBlank:true
          	}),
          	
            new Ext.form.DateField({
                fieldLabel: '开始日期',
                name: 'startdate',
                width:190,
                format:'y-m-d',
                editable:false,
                //emptyText:'请选择开始日期',
                allowBlank:true
            }),    	

        	new Ext.form.DateField({
                fieldLabel: '结束日期',
                name: 'enddate',
                width:190,
                format:'y-m-d',
                editable:false,
                //emptyText:'请选择结束日期',
                allowBlank:true
            }));
    dbForm.addButton('查询',function(){
    	if(dbForm.isValid()){
    		dbDs.proxy=new Ext.data.HttpProxy({url:"AuditService?action=auditQueryAction&appType=db&"+dbForm.getValues(true), method:"POST"});
    		dbDs.load({params:{start:0, limit:queryPageSize}});
    	}
    },this);
    dbForm.render('dbQuery');
    
        
    var proxyForm = new Ext.form.Form({
        labelAlign: 'left',
        buttonAlign:'left'
    });
    
    var proxy_query_type = new Ext.form.ComboBox({
                fieldLabel: '查询类型',
                hiddenName:'queryType',
                store: new Ext.data.SimpleStore({
                    fields: ['queryType', 'text'],
                    data : normal_error_ds
                }),
                displayField:'text',
                valueField:'queryType',
                typeAhead: true,
                mode: 'local',
                triggerAction: 'all',
                emptyText:'请选择查询类型',
                selectOnFocus:true,
                allowBlank:false,
                editable:false,
                resizable:true,
                width:190
            });

    proxy_query_type.on('select',proxyQueryType,this);
    
    proxyForm.fieldset({legend:"查询信息"},
    		proxy_query_type,
            /*
            new Ext.form.ComboBox({
                fieldLabel: '应用名称',
                hiddenName:'appName',
                store: new Ext.data.SimpleStore({
                    fields: ['value', 'text'],
                    data : appname_ds // from states.js
                }),
                valueField:'value',
                displayField:'text',
                typeAhead: true,
                mode: 'local',
                triggerAction: 'all',
                emptyText:'请选择应用名称',
                selectOnFocus:true,
                allowBlank:true,
                editable:false,
                resizable:true,
                width:190
            }),
            */
            new Ext.form.TextField({
              fieldLabel: '应用名称',
              name:'appName',
              width:190,
              allowBlank:true
          	}),
            new Ext.form.TextField({
              fieldLabel: '请求端IP',
              name:'ip',
              width:190,
              allowBlank:true
          	}),
          	
          	new Ext.form.TextField({
              fieldLabel: '目标端URL',
              name:'destUrl',
              width:190,
              vtype:'url',
              allowBlank:true
          	}),
          	 /*
			new Ext.form.ComboBox({
                fieldLabel: '目标段URL',
                hiddenName:'destUrl',
                valueField:'value',
                store: new Ext.data.SimpleStore({
                    fields: ['value', 'text'],
                    data : appname_ds // from states.js
                }),
                displayField:'text',
                typeAhead: true,
                mode: 'local',
                triggerAction: 'all',
                emptyText:'请选择目标段URL',
                selectOnFocus:true,
                allowBlank:true,
                resizable:true,
                width:190
            }),
            */
            new Ext.form.DateField({
                fieldLabel: '开始日期',
                name: 'startdate',
                width:190,
                format:'y-m-d',
                editable:false,
                //emptyText:'请选择开始日期',
                allowBlank:true
            }),    	

        	new Ext.form.DateField({
                fieldLabel: '结束日期',
                name: 'enddate',
                width:190,
                format:'y-m-d',
                editable:false,
                //emptyText:'请选择结束日期',
                allowBlank:true
            }));
    proxyForm.addButton('查询',function(){
    	if(proxyForm.isValid()){
    		proxyDs.proxy = new Ext.data.HttpProxy({url:"AuditService?action=auditQueryAction&appType=proxy&"+proxyForm.getValues(true), method:"POST"});
    		proxyDs.load({params:{start:0,limit:queryPageSize}});
    	}
    });
    proxyForm.render('proxyQuery');

    function proxyQueryType(box,record,index){
        var qType = proxyForm.findField('queryType').getValue();
    	if(qType == 'normal'){
    		proxyForm.findField('destUrl').enable();
    	}else if (qType == 'error'){
    		proxyForm.findField('destUrl').disable();
    	}
    }

    
    var aProxyForm = new Ext.form.Form({
        labelAlign: 'left',
        buttonAlign:'left'
    });
    
    aProxyForm.fieldset({legend:"查询信息"},
            /*
            new Ext.form.ComboBox({
                fieldLabel: '应用名称',
                hiddenName:'appName',
                valueField:'value',
                store: new Ext.data.SimpleStore({
                    fields: ['value', 'text'],
                    data : appname_ds // from states.js
                }),
                displayField:'text',
                typeAhead: true,
                mode: 'local',
                triggerAction: 'all',
                emptyText:'请选择应用名称',
                selectOnFocus:true,
                allowBlank:true,
                resizable:true,
                editable:false,
                width:190
            }),
            */
            new Ext.form.TextField({
              fieldLabel: '应用名称',
              name:'appName',
              width:190,
              allowBlank:true
          	}),
            
            new Ext.form.TextField({
              fieldLabel: '用户',
              name:'userName',
              width:190,
              allowBlank:true
          	}),
          	
            new Ext.form.TextField({
              fieldLabel: '请求端IP',
              name:'ip',
              width:190,
              allowBlank:true
          	}),
          	/*
          	new Ext.form.ComboBox({
                fieldLabel: '部门',
                hiddenName:'depart',
                valueField:'value',
                store: new Ext.data.SimpleStore({
                    fields: ['value', 'text'],
                    data : appname_ds // from states.js
                }),
                displayField:'text',
                typeAhead: true,
                mode: 'local',
                triggerAction: 'all',
                emptyText:'请选择部门',
                selectOnFocus:true,
                allowBlank:true,
                resizable:true,
                width:190
            }),

			new Ext.form.ComboBox({
                fieldLabel: '用户',
                hiddenName:'userName',
                valueField:'value',
                store: new Ext.data.SimpleStore({
                    fields: ['value', 'text'],
                    data : appname_ds // from states.js
                }),
                displayField:'text',
                typeAhead: true,
                mode: 'local',
                triggerAction: 'all',
                emptyText:'请选择用户',
                selectOnFocus:true,
                allowBlank:true,
                resizable:true,
                width:190
            }),
            */
            
            new Ext.form.TextField({
              fieldLabel: '目标端URL',
              name:'destUrl',
              width:190,
              vtype:'url',
              allowBlank:true
          	}),
            
            new Ext.form.DateField({
                fieldLabel: '开始日期',
                name: 'startdate',
                width:190,
                format:'y-m-d',
                editable:false,
                //emptyText:'请选择开始日期',
                allowBlank:true
            }),    	

        	new Ext.form.DateField({
                fieldLabel: '结束日期',
                name: 'enddate',
                width:190,
                editable:false,
                format:'y-m-d',
                //emptyText:'请选择结束日期',
                allowBlank:true
            }));
            
    aProxyForm.addButton('查询',function(){
    	if(aProxyForm.isValid()){
    		aProxyDs.proxy = new Ext.data.HttpProxy({url:"AuditService?action=auditQueryAction&appType=aproxy&"+aProxyForm.getValues(true), method:"POST"});
    		aProxyDs.load({params:{start:0, limit:queryPageSize}});
    	}
    });
    aProxyForm.render('aProxyQuery');    
    aProxyForm.el.createChild({tag:"input", type:"hidden", name:"queryType",value:"normal"});
    
    //---------------------Grids-------------------------
    
    function optNetwork(value, p, record){
        var returnString;
        if(value == 't')
            returnString =  '内网';
        else if(value == 'f')
            returnString =  '外网';
        return returnString;
    }

    function optSourceDest(value, p, record){
      var returnString;
        if(value == 's')
            returnString =  '源端';
        else if(value == 'd')
            returnString =  '目标端';
        return returnString;
    }

    function optApptype(value, p, record){
      var returnString;
        if(value == 'db')
            returnString =  '数据同步';
        else if(value == 'proxy')
            returnString =  '通用代理';
        else if(value == 'aproxy')
            returnString =  '认证代理';
         else if(value == 'file')
            returnString =  '文件传输';
        return returnString;
    }

    function optStatusCode(value, p, record){
      var returnString;
        if(value == '0')
            returnString =  '正常';
        else if(value == '－1')
            returnString =  '不可知错误';
        else if(value == '-6')
            returnString =  '没有实现的方法或接口';
        else if(value == '10011')
            returnString =  '字符串为空';
        else if(value == '10012')
            returnString =  '字符串索引越界';
        else if(value == '10035')
            returnString =  '重复配置';
        else if(value == '10036')
            returnString =  '配置不成功';
        else if(value == '10037')
            returnString =  '配置变量无效';
        else if(value == '10038')
            returnString =  '配置数据为空';
        else if(value == '10039')
            returnString =  '没有实现配置接口';
        else if(value == '10040')
            returnString =  '没有配置进行配置操作';
        else if(value == '-10012')
            returnString =  '网络异常';
        else if(value == '-10013')
            returnString =  '目标端处理错误';
        else if(value == '-10014')
            returnString =  '数据是空';
        else if(value == '-10015')
            returnString =  '代理端已关闭';
        else if(value == '-10016')
            returnString =  '操作关键字已经取消';
        return String.format("{0}  <b>{1}</b>",returnString,value);
    }
        
    //公共数据模型
    var dbRecord = Ext.data.Record.create(
    [{name: 'appName'},
    {name: 'appType'},
    {name: 'network'},
    {name: 'auditLevel'},
    {name: 'date'},
    {name: 'sourceDest'},
    {name: 'databaseName'},
    {name: 'tableName'},
    {name: 'statusCode'},
    {name: 'recordcount'},
    {name: 'fileName'},
    {name: 'pkId'},
    {name: 'appInfo'},
    ]);
    
    var proxyRecord = Ext.data.Record.create(
    [{name: 'appName'},
    {name: 'appType'},
    {name: 'auditLevel'},
    {name: 'ip'},
    {name: 'date'},
    {name: 'appInfo'},
    {name: 'destUrl'},
    //{name: 'fileName'},
    //{name: 'pkId'}
    ]);
    
    var aProxyRecord = Ext.data.Record.create(
    [{name: 'appName'},
    {name: 'appType'},
    {name: 'auditLevel'},
    {name: 'ip'},
    {name: 'date'},
    {name: 'appInfo'},
    {name: 'destUrl'},
    {name: 'userName'},
    {name: 'operType'}
    ]);
    
    
    
    var dbDs = new Ext.data.Store({
        reader: new Ext.data.JsonReader({
            root: 'audits',
            totalRecords: 'totalCount',
            totalProperty: 'totalCount'
        }, dbRecord)
    });
    
    var proxyDs = new Ext.data.Store({
        reader: new Ext.data.JsonReader({
            root: 'audits',
            totalRecords: 'totalCount',
            totalProperty: 'totalCount'
        }, proxyRecord)
    });
    
    var aProxyDs = new Ext.data.Store({		
        reader: new Ext.data.JsonReader({
            root: 'audits',
            totalRecords: 'totalCount',
            totalProperty: 'totalCount'
        }, aProxyRecord)
    });
	
    var dbAuditCm = new Ext.grid.ColumnModel([
    {header: "应用名称", width: 100,locked:true, dataIndex: 'appName'},
    {header: "应用类型", width: 100, dataIndex: 'appType',renderer:optApptype},
    {header: "网络位置", width: 100, dataIndex: 'network',renderer:optNetwork},
    {header: "日志等级", width: 100, dataIndex: 'auditLevel'},
    {header: "入库时间", width: 120, dataIndex: 'date'},
    {header: "源/目标", width: 100, dataIndex: 'sourceDest',renderer:optSourceDest},
    {header: "数据库名", width: 100, dataIndex: 'databaseName'},
    {header: "数据表名", width: 100, dataIndex: 'tableName'},
    {header: "状态代码", width: 100, dataIndex: 'statusCode',renderer:optStatusCode},
    {header: "记录条数", width: 100, dataIndex: 'recordcount'},
    {header: "临时文件名称", width: 100, dataIndex: 'fileName'},
    {header: "出错记录编号", width: 100, dataIndex: 'pkId'},
    {header: "业务信息", width: 100, dataIndex: 'appInfo'}
    ]);
    
    dbAuditCm.defaultSortable = true;

    // create the grid
    var dbAuditGrid = new Ext.grid.Grid('dbGrid', {
        ds: dbDs,
        cm: dbAuditCm
    });
    
    dbAuditGrid.render();  
    
    var proxyAuditCm = new Ext.grid.ColumnModel([
    {header: "应用名称", width: 100,locked:true, dataIndex: 'appName'},
    {header: "应用类型", width: 100, dataIndex: 'appType',renderer:optApptype},
    {header: "日志等级", width: 100, dataIndex: 'auditLevel'},
    {header: "请求端IP", width: 100, dataIndex: 'ip'},
    {header: "目标地址", width: 100, dataIndex: 'destUrl'},
    {header: "入库时间", width: 120, dataIndex: 'date'},
    //{header: "临时文件名称", width: 100, dataIndex: 'fileName'},
    //{header: "出错记录编号", width: 100, dataIndex: 'pkId'},
    {header: "业务信息", width: 100, dataIndex: 'appInfo'}
    ]);
    
    proxyAuditCm.defaultSortable = true;

    // create the grid
    var proxyAuditGrid = new Ext.grid.Grid('proxyGrid', {
        ds: proxyDs,
        cm: proxyAuditCm
    });
    
    proxyAuditGrid.render(); 
    
    var aProxyAuditCm = new Ext.grid.ColumnModel([
    {header: "应用名称", width: 100,locked:true, dataIndex: 'appName'},
    {header: "应用类型", width: 100, dataIndex: 'appType',renderer:optApptype},
    {header: "日志等级", width: 100, dataIndex: 'auditLevel'},
    {header: "登录帐号", width: 100, dataIndex: 'userName'},
    {header: "请求端IP", width: 100, dataIndex: 'ip'},
    {header: "目标地址", width: 100, dataIndex: 'destUrl'},
    {header: "操作行为", width: 100, dataIndex: 'operType'},
    {header: "入库时间", width: 120, dataIndex: 'date'},
    {header: "业务信息", width: 100, dataIndex: 'appInfo'}
    ]);
    
    aProxyAuditCm.defaultSortable = true;

    // create the grid
    var aProxyAuditGrid = new Ext.grid.Grid('aProxyGrid', {
        ds: aProxyDs,
        cm: aProxyAuditCm
    });
    
    aProxyAuditGrid.render(); 
    
    
    
    var dbGridHeader = dbAuditGrid.getView().getHeaderPanel(true);
    new Ext.PagingToolbar(dbGridHeader, dbDs, {pageSize:queryPageSize, displayInfo:true, displayMsg:"\u663e\u793a\u8bb0\u5f55 {0} - {1} of {2}", emptyMsg:"\u6ca1\u6709\u8bb0\u5f55"});

	var proxyGridHeader = proxyAuditGrid.getView().getHeaderPanel(true);
    new Ext.PagingToolbar(proxyGridHeader, proxyDs, {pageSize:queryPageSize, displayInfo:true, displayMsg:"\u663e\u793a\u8bb0\u5f55 {0} - {1} of {2}", emptyMsg:"\u6ca1\u6709\u8bb0\u5f55"});
    
    var aProxyGridHeader = aProxyAuditGrid.getView().getHeaderPanel(true);
    new Ext.PagingToolbar(aProxyGridHeader, aProxyDs, {pageSize:queryPageSize, displayInfo:true, displayMsg:"\u663e\u793a\u8bb0\u5f55 {0} - {1} of {2}", emptyMsg:"\u6ca1\u6709\u8bb0\u5f55"});
	  
});