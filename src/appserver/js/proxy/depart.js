Ext.onReady(function() {    
	var currentRow = null;
    var departRecord = Ext.data.Record.create(
    [{name: 'id'},
    {name: 'departname'},
    {name: 'description'}
    ]);

    // create the Data Store
    
    var departDS = new Ext.data.Store({
   	 	proxy: new Ext.data.HttpProxy({url:'IPlatManager?action=listDepartAction',method:'POST'}),
    
        reader: new Ext.data.JsonReader({
            root: 'departs',
            totalRecords: 'totalCount',
            totalProperty: 'totalCount'
        }, departRecord)
    });
	
    var departCm = new Ext.grid.ColumnModel([
    {header: "部门ID", width: 100, dataIndex: 'id',type:'int'},
    {header: "部门名称", width: 150, dataIndex: 'departname'},
    {header: "部门描述", width: 300, dataIndex: 'description'}
    ]);
    
    departCm.defaultSortable = true;

    // create the grid
    var departGrid = new Ext.grid.Grid('departManage', {
        ds: departDS,
        cm: departCm
    });
	
	departGrid.addListener('rowdblclick',launchDetail);
	departGrid.addListener("rowclick", rowClick);
    departGrid.render();
    //departDS.load();
    function launchDetail(grid,rowIndex,columnIndex,e){
    	//var pudID=this.ds.getAt(rowIndex).data["pudID"];
    	//alert("apptype--"+departDS.getAt(rowIndex).data["username"]);
      var row = departDS.data.items[rowIndex];
      var id = row.data['id'];
      var departname = row.data['departname'];
      var description = row.data['description'];
      //restoreWindow.init();
      departManager.showDialog("edit",id,departname,description);
    	
    }
    
    function rowClick(grid, rowIndex, columnIndex, e) {
		currentRow = rowIndex;
	}
    
    var departGridHeader = departGrid.getView().getHeaderPanel(true);

    var queryPaging = new Ext.PagingToolbar(departGridHeader, departDS, {
        pageSize: queryPageSize,
        displayInfo: true,
        displayMsg: '显示记录 {0} - {1} of {2}',
        emptyMsg: "没有记录"
    });
    
    queryPaging.add('-', {
        pressed: false,
        enableToggle:true,
        text: '新增部门',
        toggleHandler: addDepart
    });
    
    queryPaging.add("-", {pressed:false, enableToggle:true, text:"编辑部门", toggleHandler:editresource});
    
    departDS.load({params:{start:0, limit:queryPageSize}});
    
    function addDepart(){
    	departManager.showDialog("new");
    }
    
    function editresource() {
		if (currentRow === null) {
			Ext.MessageBox.alert("\u63d0\u793a", "\u8bf7\u5148\u9009\u62e9\u4e00\u884c\u6570\u636e.");
		} else {
      		var row = departDS.data.items[currentRow];
      		var id = row.data['id'];
      		var departname = row.data['departname'];
      		var description = row.data['description'];
      //restoreWindow.init();
      		departManager.showDialog("edit",id,departname,description);    	
		}
	}
    
var departManager = function() {
  // define some private variables
  var dialog, showLink, myForm, fs;
  var id,departname,description;  		
        
  // return a public interface
  return {
    init: function() {   
		myForm = Ext.get("formErt");
    },
    showDialog : function(option,id,departname,desc){

      if(!dialog){ // lazy initialize the dialog and only create it once
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
		/*
        function submitForm() {
          if( myForm.isValid() ) {
            myForm.submit({
              params:{url:'IPlatServlet?action=listUser',action:'submit', rm : 'save'},
              waitMsg:'正在保存数据...',
              scope: restoreWindow,
              failure: function(myForm, action) {
                Ext.MessageBox.alert('错误', '提交信息不正确');
              },
              success: function(myForm, action) {
                Ext.MessageBox.alert('成功', '修改完成');
                dialog.hide();
              }
            } );
           //dialog.hide();
          }
          else {
            Ext.Msg.alert('错误', '请填写完整相关信息.');
          }
        }
        */
      	
        function SaveConfig(btn) {
   			if(btn == "yes"){
        		var querycallback = {
            		success : responseSuccessInfo,
            		failure : responseFailureInfo
        		};
        		YAHOO.util.Connect.asyncRequest('POST', 'IPlatManager?action=saveDepartAction', querycallback, myForm.getValues(true));
    		}
    	}
    	var responseSuccessInfo = function(o)
    	{
      	  	//Ext.MessageBox.alert('成功', '修改完成.');
        	dialog.hide();
        	//列表数据
        	departDS.load({params:{start:0, limit:queryPageSize}});
    	};

    	var responseFailureInfo = function(o)
    	{
        	Ext.MessageBox.alert('失败', '修改失败.');
    	};

        dialog.addButton('保存', function() {
        if (myForm.isValid()) { 
		 		Ext.Msg.confirm('确认?','您确定要保存?',SaveConfig, Ext.MessageBox.buttonText.yes="确认",Ext.MessageBox.buttonText.no="取消");            
        	}
    	},dialog);
    

        dialog.addButton('关闭', dialog.hide, dialog);

        //--- setup dialog layout
        var layout = dialog.getLayout();
        layout.beginUpdate();
        layout.add('center', new Ext.ContentPanel('panelErt', {title: 'Edit Ert'}));
        layout.endUpdate();	
        
        //--- build a form
  		fs = new Ext.form.Form( {
          labelAlign: 'left',
          labelWidth: 80          
        });
        
        fs.fieldset(
          {legend:'部门信息'},    
          
          new Ext.form.TextField({
              fieldLabel: 'ID',
              name: 'id',
              width:190,
              readOnly:true,
              allowBlank:true
          }),
                
          new Ext.form.TextField({
              fieldLabel: '部门名称',
              name: 'departname',
              width:190,
              allowBlank:false
          }),
          
          new Ext.form.TextArea({
              fieldLabel: '部门描述',
              name: 'description',
              width:190,
              grow: false,
              preventScrollbars:false
          }));      	
      } 

      dialog.show();
      fs.render('formErt');      
      
      fs.findField('id').setValue(id);
      fs.findField('departname').setValue(departname);
      fs.findField('description').setValue(desc);  
	  /*
	  if(option=="edit"){
      	fs.findField('departname').el.dom.readOnly =true;
      }else if(option=="new"){
      	fs.findField('departname').el.dom.readOnly=false;
      }
      */
      myForm = fs;
    }
  };
}();
});