Ext.onReady(function() {
var apptype_ds = [
            ['db','数据库交换'],
            ['proxy','通用代理'],
            ['aproxy','认证代理']
            ];
var bbtype_ds = [
            ['1','柱状图'],
            ['2','饼图']
            ];
var bbway_ds = [
            ['y','按年'],
           	['m','按月'],
            ['w','按周'],
            ['d','按日']
            ];
var vyear_ds = [
            ['2007','2007年'],
           	['2008','2008年'],
            ['2009','2009年'],
            ['2010','2010年']
            ];
            
var vmonth_ds = [
            ['01','一月'],
           	['02','二月'],
            ['03','三月'],
            ['04','四月'],
            ['05','五月'],
            ['06','六月'],
            ['07','七月'],
            ['08','八月'],
            ['09','九月'],
            ['10','十月'],
            ['11','十一月'],
            ['12','十二月']
            ];
var vweek_ds = [
            ['1','第一周'],
           	['2','第二周'],
            ['3','第三周'],
            ['4','第四周']
            ];
var vday_ds = [
            ['01','1日'],
           	['02','2日'],
            ['03','3日'],
            ['04','4日'],
            ['05','5日'],
            ['06','6日'],
            ['07','7日'],
            ['08','8日'],
            ['09','9日'],
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

var reportForm = new Ext.form.Form({
        labelAlign: 'left'
        //url:'AuditService?action=reportAction',
       // method:'POST',
        //params:reportForm.getValues(true),
        //clientValidation:true
    });       
    
    var bb_wayField = new Ext.form.ComboBox({
                fieldLabel: '统计方式',
                hiddenName:'bb_way',
                store: new Ext.data.SimpleStore({
                    fields: ['value', 'text'],
                    data : bbway_ds
                }),
                displayField:'text',
                valueField:'value',
                typeAhead: true,
                mode: 'local',
                triggerAction: 'all',
                emptyText:'请选择统计方式',
                selectOnFocus:true,
                allowBlank:false,
                editable:false,
                resizable:true,
                width:190
            });
            
    bb_wayField.on('select',bbWaySelect,this);
    
    
   
    reportForm.fieldset({legend:"查询信息"},            
        new Ext.form.ComboBox({
                fieldLabel: '应用类型',
                hiddenName:'app_type',
                store: new Ext.data.SimpleStore({
                    fields: ['value', 'text'],
                    data : apptype_ds // from states.js
                }),
                displayField:'text',
                valueField:'value',
                typeAhead: true,
                mode: 'local',
                triggerAction: 'all',
                emptyText:'请选择应用类型',
                selectOnFocus:true,
                allowBlank:false,
                editable:false,
                resizable:true,
                width:190
            }),

            new Ext.form.TextField({
              fieldLabel: '应用名称',
              name:'app_name',
              width:190
          	}),
          	
            new Ext.form.ComboBox({
                fieldLabel: '报表类型',
                hiddenName:'bb_type',
                store: new Ext.data.SimpleStore({
                    fields: ['value', 'text'],
                    data : bbtype_ds
                }),
                displayField:'text',
                valueField:'value',
                typeAhead: true,
                editable:true,
                mode: 'local',
                triggerAction: 'all',
                emptyText:'请选择报表类型',
                selectOnFocus:true,
                resizable:true,
                editable:false,
                allowBlank:false,
                width:190
            }),
            
            bb_wayField,
            
            new Ext.form.ComboBox({
                fieldLabel: '选择年份',
                hiddenName:'v_year',
                store: new Ext.data.SimpleStore({
                    fields: ['value', 'text'],
                    data : vyear_ds // from states.js
                }),
                displayField:'text',
                valueField:'value',
                typeAhead: true,
                mode: 'local',
                triggerAction: 'all',
                emptyText:'请选择年份',
                selectOnFocus:true,
                allowBlank:false,
                editable:false,
                resizable:true,
                width:190
            }),
            
            new Ext.form.ComboBox({
                fieldLabel: '选择月份',
                hiddenName:'v_month',
                store: new Ext.data.SimpleStore({
                    fields: ['value', 'text'],
                    data : vmonth_ds // from states.js
                }),
                displayField:'text',
                valueField:'value',
                typeAhead: true,
                mode: 'local',
                triggerAction: 'all',
                emptyText:'请选择选择月份',
                selectOnFocus:true,
                editable:false,
                allowBlank:false,
                disabled:true,
                resizable:true,
                width:190
            }),
            
            new Ext.form.ComboBox({
                fieldLabel: '选择周',
                hiddenName:'v_week',
                store: new Ext.data.SimpleStore({
                    fields: ['value', 'text'],
                    data : vweek_ds // from states.js
                }),
                displayField:'text',
                valueField:'value',
                typeAhead: true,
                mode: 'local',
                triggerAction: 'all',
                emptyText:'请选择周',
                selectOnFocus:false,
                editable:false,
                allowBlank:false,
                disabled:true,
                resizable:true,
                width:190
            }),
            
            new Ext.form.ComboBox({
                fieldLabel: '选择日期',
                hiddenName:'v_day',
                store: new Ext.data.SimpleStore({
                    fields: ['value', 'text'],
                    data : vday_ds // from states.js
                }),
                displayField:'text',
                valueField:'value',
                typeAhead: true,
                mode: 'local',
                triggerAction: 'all',
                emptyText:'请选择日期',
                selectOnFocus:true,
                editable:false,
                allowBlank:false,
                resizable:true,
                disabled:true,
                width:190
            }));
            
            
    reportForm.addButton('提交',function(){
   	 	//alert(reportForm.getValues(true));
   		var querycallback = {success:responseSuccessInfo, failure:responseFailureInfo};
    	if(reportForm.isValid()){
    		YAHOO.util.Connect.asyncRequest("POST", "AuditService?action=reportAction", querycallback, reportForm.getValues(true));
    		Ext.MessageBox.wait('正在生成报表...','请稍等');
    	}
    });    
    
    reportForm.render('reportQuery');
    
     var responseSuccessInfo = function (o) {  
     				Ext.MessageBox.hide();
           			//reportDialog.showDialog(o.responseText);   
           			var op = window.open("reportShow.html","","width=1020,height=760,toolbar=no,menubar=no,location=no,status=no,resizable=yes,scrollbars=yes");
           			op.document.write(o.responseText);
           			
           			//reportForm.findField("description").setValue(o.responseText);       		
            	
					//Ext.MessageBox.alert("\u6210\u529f", "\u4fee\u6539\u5b8c\u6210.");
				};
				var responseFailureInfo = function (o) {
					Ext.MessageBox.hide();
					Ext.MessageBox.alert("出现内部错误");
				};
    
    reportForm.el.createChild({tag:"input", type:"hidden", name:"report_file"}); 
    //reportForm.findField('v_day').disable(); 
   // reportForm.findField('v_day').setVisible(false); 
   
   	function bbWaySelect(box,record,index){
   		//首先设置报表路径
   		var bb_way = reportForm.findField('bb_way').getValue();
   		if(bb_way=='y'){
   			document.all['report_file'].value ='year';
   			reportForm.findField('v_month').disable();
   			reportForm.findField('v_week').disable();
   			reportForm.findField('v_day').disable();
   		}else if(bb_way == 'm'){
   			document.all['report_file'].value ='month';
   			reportForm.findField('v_month').enable();
   			reportForm.findField('v_week').disable();
   			reportForm.findField('v_day').disable();
   		}else if(bb_way == 'w'){
   			document.all['report_file'].value ='week';
   			reportForm.findField('v_month').enable();
   			reportForm.findField('v_week').enable();
   			reportForm.findField('v_day').disable();
   		}else if(bb_way == 'd'){
   			document.all['report_file'].value ='day';
   			reportForm.findField('v_month').enable();
   			reportForm.findField('v_week').disable();
   			reportForm.findField('v_day').enable();
   		}  		
   	}
    
/*
var reportDialog = function() {
  // define some private variables
  var dialog, reportPanel;  		
        
  // return a public interface
  return {
    init: function() {   
		
    },
    showDialog : function(reportContent){

      if(!dialog){ // lazy initialize the dialog and only create it once
        dialog = new Ext.LayoutDialog("reportLayout", {
                        modal: true,
                        width:800,
                        height:600,
                        shadow:true,
                        minWidth:300,
                        resizable:true,
                        minHeight:300,
                        proxyDrag: true,
                        title:'审计报表',
                        center: {
                          margins:{left:3,top:3,right:3,bottom:3},
                          autoScroll:true
                        }
        });
        
        dialog.addKeyListener(27, dialog.hide, dialog);
        
      	        //--- setup dialog layout
        reportPanel = new Ext.ContentPanel('reportPanel', {title: '审计报表'});
        var layout = dialog.getLayout();
        layout.beginUpdate();
        layout.add('center', reportPanel);
        layout.endUpdate();	         
      }
     
      reportPanel.setContent(reportContent,true); 
      dialog.show();
    }
  };
}();
*/
 });