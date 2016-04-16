Ext.onReady(function() {
	var fs = new Ext.form.Form( {
		labelAlign : 'left',
		buttonAlign : 'left',
		labelWidth : 200,

		// configure how to read the XML Data
		reader : new Ext.data.JsonReader( {
			root : 'configs',
			totalRecords : 'totalCount'
		}, [  {
			name : 'descr'
		}, {
			name : 'ip'
		}, {
			name : 'port'
		}, {
			name : 'cmsip'
		}, {
			name : 'cmssysport'
		},
		{
			name : 'sysport'
		}, 
		{
			name : 'cmsport'
		} ])
	});

	fs.fieldset( {
		legend : '探针配置信息'
	}, new Ext.form.TextField( {
		fieldLabel : '探针标识',
		name : 'descr',
		width : 190
	}), new Ext.form.TextField( {
		fieldLabel : '探针IP地址',
		name : 'ip',
		width : 190
	}), new Ext.form.TextField( {
		fieldLabel : '探针SNMP端口',
		name : 'port',
		width : 190
	}),
	new Ext.form.TextField( {
		fieldLabel : '探针SYSLOG端口',
		name : 'sysport',
		width : 190
	}), new Ext.form.TextField( {
		fieldLabel : '集控系统IP地址',
		name : 'cmsip',
		width : 190
	}),new Ext.form.TextField( {
			fieldLabel : '集控系统SYSLOG端口',
			name : 'cmssysport',
			width : 190
		}),
	  new Ext.form.TextField( {
		fieldLabel : '集控系统SNMP端口',
		name : 'cmsport',
		width : 190
	}));

	fs.addButton("连接测试", function() {
		if (fs.isValid()) {
			var querycallback = {
				success : testSuccessInfo,
				failure : testFailureInfo,
				timeout : 10000
			};
			YAHOO.util.Connect.asyncRequest("POST",
					"IPlatManager?action=testConnAction&" + fs.getValues(true),
					querycallback);
		}
	});

	var testSuccessInfo = function(o) {
		Ext.MessageBox.alert("成功", o.responseText);
		// Ext.MessageBox.hide();
	};
	var testFailureInfo = function(o) {
		Ext.MessageBox.alert("错误", o.responseText);
	};

	fs.addButton("更新配置", function() {
		if (fs.isValid())
			Ext.Msg.confirm("\u786e\u8ba4?",
					"\u60a8\u786e\u5b9a\u8981\u4fdd\u5b58?", SaveDS,
					Ext.MessageBox.buttonText.yes = "\u786e\u8ba4",
					Ext.MessageBox.buttonText.no = "\u53d6\u6d88");
	});

	function SaveDS(btn) {
		if (btn == 'yes')
			fs.submit( {
				url : 'IPlatManager?action=saveConfigAction',
				method : 'POST',
				params : fs.getValues(true),
				clientValidation : false
			});
	}

	

	function SaveInitConfig(btn) {
		if (btn == "yes") {
			var querycallback = {
				success : initSuccessInfo,
				failure : initFailureInfo,
				timeout : 10000
			};
			/*YAHOO.util.Connect.asyncRequest("POST",
					"AuditService?action=initAction", querycallback);
			Ext.MessageBox.progress("进度信息", "正在创建数据库...");*/
		}
	}
	var initSuccessInfo = function(o) {
		Ext.MessageBox.hide();
		Ext.MessageBox.alert("成功", "系统初始化成功.");
		// Ext.MessageBox.hide();
	};
	var initFailureInfo = function(o) {
		Ext.MessageBox.hide();
		Ext.MessageBox.alert("错误", o.responseText);
	};

	fs.load( {
		url : 'IPlatManager?action=loadConfigAction',
		waitMsg : '正在加载系统配置...'
	});

	fs.render('proteConfig');
});
