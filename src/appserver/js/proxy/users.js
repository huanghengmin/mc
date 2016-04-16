var roleArray = new Array();
//处理资源选择
function roleCheck(id, isSelect) {
    if (isSelect) {
        roleArray.push(id);
    } else {
        roleArray.remove(id);
    }
    document.all["roleids"].value = roleArray;
}

Ext.onReady(function() {
    var currentRow = null;
    function optOperation(value, p, record) {
        if (value == '0') {
            return '否';
        } else {
            return '是';
        }
    }

    var userRecord = Ext.data.Record.create(
            [{name: 'id'},
            {name: 'userid'},
            {name: 'username'},
            {name: 'password'},
            {name: 'available'},
            {name: 'description'},
            {name: 'departid'},
            {name: 'departname'}
                    ]);

    var departRecord = Ext.data.Record.create(
            [{name: 'departid',mapping:'id'},
            {name: 'departname'},
            {name: 'description'}
                    ]);

    // create the Data Store

    var departDs = new Ext.data.Store({
        proxy: new Ext.data.HttpProxy({url:'IPlatManager?action=listDepartAction',method:'POST'}),

        reader: new Ext.data.JsonReader({
            root: 'departs',
            totalRecords: 'totalCount',
            totalProperty: 'totalCount'
        }, departRecord)
    });

    var usersDs = new Ext.data.Store({
        proxy: new Ext.data.HttpProxy({url:'IPlatManager?action=listUserAction',method:'POST'}),

        reader: new Ext.data.JsonReader({
            root: 'users',
            totalRecords: 'totalCount',
            totalProperty: 'totalCount'
        }, userRecord),
        baseParams: {limit:queryPageSize}
    });

    var usersCm = new Ext.grid.ColumnModel([
    {header: "用户ID", width: 100, dataIndex: 'id',type:'int'},
    {header: "登陆账户", width: 100, dataIndex: 'userid'},
    {header: "用户名称", width: 100, dataIndex: 'username'},
    {header: "是否可用", width: 100, dataIndex: 'available',renderer:optOperation},
    {header: "用户描述", width: 300, dataIndex: 'description'}
            ]);

    usersCm.defaultSortable = true;

    // create the grid
    var usersGrid = new Ext.grid.Grid('userManage', {
        ds: usersDs,
        cm: usersCm
    });

    usersGrid.addListener('rowdblclick', launchDetail);
    usersGrid.addListener("rowclick", rowClick);
    usersGrid.render();
    //usersDs.load();
    function launchDetail(grid, rowIndex, columnIndex, e) {
        //var pudID=this.ds.getAt(rowIndex).data["pudID"];
        //alert("apptype--"+usersDs.getAt(rowIndex).data["username"]);
        var row = usersDs.data.items[rowIndex];
        var id = row.data['id'];
        var userid = row.data['userid'];
        var username = row.data['username'];
        var available = row.data['available'];
        var description = row.data['description'];
        var password = row.data['password'];
        var departid = row.data['departid'];
        var departname = row.data['departname'];
      //restoreWindow.init();
        userManager.showEditDialog(id, userid, username, password, available, description, departid, departname);

    }

    function rowClick(grid, rowIndex, columnIndex, e) {
        currentRow = rowIndex;
    }

    var usersGridHeader = usersGrid.getView().getHeaderPanel(true);

    var tb = new Ext.Toolbar('search-tb', [
            '搜索用户: ', ' ',
            new Ext.app.SearchField({
                store: usersDs,
                width:200
            })
            ]);

    var queryPaging = new Ext.PagingToolbar(usersGridHeader, usersDs, {
        pageSize: queryPageSize,
        displayInfo: true,
        displayMsg: '显示记录 {0} - {1} of {2}',
        emptyMsg: "没有记录"
    });

    queryPaging.add('-', {
        pressed: false,
        enableToggle:true,
        text: '新增用户',
        toggleHandler: addUser
    });

    queryPaging.add("-", {pressed:false, enableToggle:true, text:"编辑用户", toggleHandler:editresource});

    usersDs.load({params:{start:0, limit:queryPageSize}});

    function addUser() {
        userManager.showNewDialog();
    }

    function editresource() {
        if (currentRow === null) {
            Ext.MessageBox.alert("\u63d0\u793a", "\u8bf7\u5148\u9009\u62e9\u4e00\u884c\u6570\u636e.");
        } else {
            var row = usersDs.data.items[currentRow];
            var id = row.data['id'];
            var userid = row.data['userid'];
            var username = row.data['username'];
            var available = row.data['available'];
            var description = row.data['description'];
            var password = row.data['password'];
            var departid = row.data['departid'];
            var departname = row.data['departname'];
      //restoreWindow.init();
            userManager.showEditDialog(id, userid, username, password, available, description, departid, departname);

        }
    }

    var departResultTpl = new Ext.Template(
            '<div>',
            '<font color=336699 size=2><b>{departid}</b> - {departname}</font> ',
            '</div>'
            );


    var userManager = function () {
        var newDialog, editDialog, myForm, editfs, newfs, roleDS;
        function checkPwd(pwd) {
            return pwd == newfs.findField('password').getValue();
        }

        function checkEditPwd(pwd) {
            return pwd == editfs.findField('password').getValue();
        }

        function makeCheckBox(value, p, record) {
            var checkBox;
            if (value) {
                roleCheck(record.data["id"], value)
                checkBox = "<input type='checkbox' name='check_" + record.data["id"] + "' onClick=\"roleCheck('" + record.data["id"] + "',this.checked)\" checked>";
            } else {
                checkBox = "<input type='checkbox' name='check_" + record.data["id"] + "' onClick=\"roleCheck('" + record.data["id"] + "',this.checked)\">";
            }
            return checkBox;
        }
        return {init:function () {
            myForm = Ext.get("formErt");
        },

            showEditDialog:function (id, uId, uName, pwd, used, desc, departid, departname) {
                if (!editDialog) { // lazy initialize the dialog and only create it once
                    editDialog = new Ext.LayoutDialog("editUserPanel", {title:"编辑用户", modal:true, width:500, height:400, shadow:true, minWidth:300, resizable:false, minHeight:300, proxyDrag:true, autoScroll:true, center:{margins:{left:3, top:3, right:3, bottom:3}, autoScroll:true}});
                    editDialog.addKeyListener(27, editDialog.hide, editDialog);
                    function SaveConfig(btn) {
                        if (btn == "yes") {
                            var querycallback = {success:responseSuccessInfo, failure:responseFailureInfo};
                            YAHOO.util.Connect.asyncRequest("POST", "IPlatManager?action=saveUserAction", querycallback, myForm.getValues(true));
                        }
                    }
                    var responseSuccessInfo = function (o) {
                        Ext.MessageBox.alert("\u6210\u529f", "\u4fee\u6539\u5b8c\u6210.");
					//editDialog.hide();
                        //列表数据
                        usersDs.load({params:{start:0, limit:queryPageSize}});
                    };
                    var responseFailureInfo = function (o) {
                        Ext.MessageBox.alert("\u5931\u8d25", "\u4fee\u6539\u5931\u8d25.");
                    };
                    editDialog.addButton("\u4fdd\u5b58", function () {
                        if (myForm.isValid()) {
                            Ext.Msg.confirm("\u786e\u8ba4?", "\u60a8\u786e\u5b9a\u8981\u4fdd\u5b58?", SaveConfig, Ext.MessageBox.buttonText.yes = "\u786e\u8ba4", Ext.MessageBox.buttonText.no = "\u53d6\u6d88");
                        }
                    }, editDialog);
                    editDialog.addButton("\u5173\u95ed", editDialog.hide, editDialog);
                    var roleRecord = Ext.data.Record.create([{name:"id"}, {name:"roleid"}, {name:"rolename"}, {name:"description"},{name:"isselect"}]);
                    var roleCm = new Ext.grid.ColumnModel([{header:"\u662f\u5426\u9009\u62e9", width:100, dataIndex:"isselect", type:"boolean", renderer:makeCheckBox}, {header:"角色ID", width:70, dataIndex:"id", type:"int"},{header:"角色", width:100, dataIndex:"roleid"}, {header:"角色别名", width:100, dataIndex:"rolename"}, {header:"\u89d2\u8272\u63cf\u8ff0", width:100, dataIndex:"description"}]);
                    roleDS = new Ext.data.Store({reader:new Ext.data.JsonReader({root:"roles", totalRecords:"totalCount", totalProperty:"totalCount"}, roleRecord)});
                    var roleGrid = new Ext.grid.Grid("userRoleList", {ds:roleDS, cm:roleCm});
                    roleGrid.render();
                    /*
                     var gridHead = resGrid.getView().getHeaderPanel(true);
                     new Ext.Toolbar(gridHead, [{text:"u6dfbu52a0u8d44u6e90", handler:function () {
                         ResourceListManage.showResDialog();
                     }}, {text:"u5220u9664u8d44u6e90", handler:function () {
                         alert("u5220u9664u8d44u6e90");
                     }}]);
                     */
                    //var dd = new Ext.Toolbar(gridHead, []);
                    //var resGridHeader = resGrid.getView().getHeaderPanel(true);
                    //var queryPaging = new Ext.PagingToolbar(resGridHeader, roleDS, {pageSize:queryPageSize, displayInfo:true, displayMsg:"u663eu793au8bb0u5f55 {0} - {1} of {2}", emptyMsg:"u6ca1u6709u8bb0u5f55"});

                    //--- setup dialog layout
                    var layout = editDialog.getLayout();
                    layout.beginUpdate();
                    layout.add("center", new Ext.ContentPanel("editUser", {title:"用户属性"}));
                    layout.add("center", new Ext.GridPanel(roleGrid, {title:"隶属角色"}));
                    layout.endUpdate();
        
        
        
        //--- build a form
                    editfs = new Ext.form.Form({labelAlign:"left", labelWidth:80});
                    editfs.fieldset({legend:"用户信息"}, new Ext.form.TextField({
                        fieldLabel: "用户ID",
                        name: 'id',
                        width:190,
                        readOnly:true,
                        allowBlank:true
                    }),

                            new Ext.form.TextField({
                                fieldLabel: '登录账户',
                                name: 'userid',
                                width:190,
                                allowBlank:true
                            }),

                            new Ext.form.TextField({
                                fieldLabel: '登录口令',
                                name: 'password',
                                width:190,
                                allowBlank:false,
                                inputType:'password'
                            }),

                            new Ext.form.TextField({
                                fieldLabel:'重复口令',
                                name: 'repassword',
                                width:190,
                                allowBlank:false,
                                validator:checkEditPwd,
                                inputType:'password'
                            }),

                            new Ext.form.TextField({
                                fieldLabel: '用户名',
                                name:'username',
                                width:190,
                                allowBlank:false
                            }),

                            new Ext.form.Checkbox({
                                fieldLabel: '是否启用',
                                name: 'available',
                                width:190,
                                allowBlank:false
                            }),

                            new Ext.form.ComboBox({
                                fieldLabel: '所属部门',
                                hiddenName:'departid',
                                valueField:'departid',
                                store: departDs,
                                displayField:'departname',
                                typeAhead: false,
                                editable:false,
                                triggerAction: 'all',
                            //emptyText:'请选择应用名称',
                                loadingText: '正在部门列表...',
                                selectOnFocus:true,
                                tpl: departResultTpl,
                                pageSize:ComboBoxPageSize,
                                allowBlank:false,
                                width:190
                            }),

                            new Ext.form.TextArea({
                                fieldLabel: '用户描述',
                                name: 'description',
                                width:190,
                                grow: false,
                                preventScrollbars:false
                            }));
                    editfs.render("editForm");
                    editfs.el.createChild({tag:'input', type:'hidden', name:'roleids'});
                }
			//重置数组
                roleArray = new Array();

                editfs.findField('id').setValue(id);
                editfs.findField('userid').setValue(uId);
                editfs.findField('username').setValue(uName);
                editfs.findField('password').setValue(pwd);
                editfs.findField('repassword').setValue(pwd);
                editfs.findField('available').setValue(used);
                editfs.findField('departid').setValue(departid);
                editfs.findField('description').setValue(desc);
                editfs.findField('userid').el.dom.readOnly = true;
                roleDS.proxy = new Ext.data.HttpProxy({url:"IPlatManager?action=listUserRoleAction&userid=" + id, method:"POST"});
                roleDS.load();
                myForm = editfs;
                editDialog.show();
            },


            showNewDialog:function () {
                if (!newDialog) { // lazy initialize the dialog and only create it once
                    newDialog = new Ext.LayoutDialog("newUserPanel", {title:"新增用户", modal:true, width:500, height:375, shadow:true, minWidth:300, resizable:false, minHeight:300, proxyDrag:true, autoScroll:true, center:{margins:{left:3, top:3, right:3, bottom:3}, autoScroll:true}});
                    newDialog.addKeyListener(27, newDialog.hide, newDialog);
                    function SaveConfig(btn) {
                        if (btn == "yes") {
                            var querycallback = {success:responseSuccessInfo, failure:responseFailureInfo};
                            YAHOO.util.Connect.asyncRequest("POST", "IPlatManager?action=saveUserAction", querycallback, myForm.getValues(true));
                        }
                    }
                    var responseSuccessInfo = function (o) {
                        //Ext.MessageBox.alert("\u6210\u529f", "\u4fee\u6539\u5b8c\u6210.");
                        newDialog.hide();
        	//列表数据
                        usersDs.load({params:{start:0, limit:queryPageSize}});
                    };
                    var responseFailureInfo = function (o) {
                        Ext.MessageBox.alert(o);
                    };
                    newDialog.addButton("\u4fdd\u5b58", function () {
                        if (myForm.isValid()) {
                            Ext.Msg.confirm("\u786e\u8ba4?", "\u60a8\u786e\u5b9a\u8981\u4fdd\u5b58?", SaveConfig, Ext.MessageBox.buttonText.yes = "\u786e\u8ba4", Ext.MessageBox.buttonText.no = "\u53d6\u6d88");
                        }
                    }, newDialog);
                    newDialog.addButton("\u5173\u95ed", newDialog.hide, newDialog);
				
        //--- setup dialog layout
                    var layout = newDialog.getLayout();
                    layout.beginUpdate();
                    layout.add("center", new Ext.ContentPanel("newUser", {title:"\u89d2\u8272\u5c5e\u6027"}));
                    layout.endUpdate();
        
        //--- build a form
                    newfs = new Ext.form.Form({labelAlign:"left", labelWidth:80});
                    newfs.fieldset({legend:"\u89d2\u8272\u4fe1\u606f"},

                            new Ext.form.TextField({
                                fieldLabel: "用户ID",
                                name: 'id',
                                width:190,
                                readOnly:true,
                                allowBlank:true
                            }),

                            new Ext.form.TextField({
                                fieldLabel: '登录账户',
                                name: 'userid',
                                width:190,
                                allowBlank:false
                            }),

                            new Ext.form.TextField({
                                fieldLabel: '登录口令',
                                name: 'password',
                                width:190,
                                allowBlank:false,
                                inputType:'password'
                            }),

                            new Ext.form.TextField({
                                fieldLabel: '重复口令',
                                name: 'repassword',
                                width:190,
                                allowBlank:false,
                                validator:checkPwd,
                                inputType:'password'
                            }),

                            new Ext.form.TextField({
                                fieldLabel: '用户名',
                                name:'username',
                                width:190,
                                allowBlank:false
                            }),

                            new Ext.form.Checkbox({
                                fieldLabel: '是否启用',
                                name: 'available',
                                width:190,
                                allowBlank:false
                            }),

                            new Ext.form.ComboBox({
                                fieldLabel: '所属部门',
                                hiddenName:'departid',
                                valueField:'departid',
                                store: departDs,
                                displayField:'departname',
                                typeAhead: false,
                                editable:false,
                                triggerAction: 'all',
                            //emptyText:'请选择应用名称',
                                loadingText: '正在获取部门列表...',
                                selectOnFocus:true,
                                tpl: departResultTpl,
                                pageSize:ComboBoxPageSize,
                                allowBlank:false,
                                width:190
                            }),

                            new Ext.form.TextArea({
                                fieldLabel: '用户描述',
                                name: 'description',
                                width:190,
                                grow: false,
                                preventScrollbars:false
                            }));
                    newfs.render("newForm");
                }

                newfs.findField('id').setValue("");
                newfs.findField('userid').setValue("");
                newfs.findField('username').setValue("");
                newfs.findField('password').setValue("");
                newfs.findField('repassword').setValue("");
                newfs.findField('available').setValue("");
                newfs.findField('departid').setValue("");
                newfs.findField('description').setValue("");

                myForm = newfs;
                newDialog.show();
            }};
    }();
});

Ext.app.SearchField = Ext.extend(Ext.form.TwinTriggerField, {
    initComponent : function() {
        Ext.app.SearchField.superclass.initComponent.call(this);
        this.on('specialkey', function(f, e) {
            if (e.getKey() == e.ENTER) {
                this.onTrigger2Click();
            }
        }, this);
    },

    validationEvent:false,
    validateOnBlur:false,
    trigger1Class:'x-form-clear-trigger',
    trigger2Class:'x-form-search-trigger',
    hideTrigger1:true,
    width:180,
    hasSearch : false,
    paramName : 'query_text',

    onTrigger1Click : function() {
        if (this.hasSearch) {
            var o = {start: 0};
            o[this.paramName] = '';
            this.store.reload({params:o});
            this.el.dom.value = '';
            this.triggers[0].hide();
            this.hasSearch = false;
        }
    },

    onTrigger2Click : function() {
        var v = this.getRawValue();
        if (v.length < 1) {
            this.onTrigger1Click();
            return;
        }
        var o = {start: 0};
        o[this.paramName] = v;
        this.store.reload({params:o});
        this.hasSearch = true;
        this.triggers[0].show();
    }
});