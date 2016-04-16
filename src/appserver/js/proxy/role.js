var resArray = new Array();
//处理资源选择
function resCheck(id, isSelect) {
    if (isSelect) {
        resArray.push(id);
    } else {
        resArray.remove(id);
    }
    document.all["resids"].value = resArray;
}
var userArray = new Array();
//处理资源选择
function userCheck(id, isSelect) {
    if (isSelect) {
        userArray.push(id);
    } else {
        userArray.remove(id);
    }
    document.all["userids"].value = userArray;
}
Ext.onReady(function () {
    var currentRow = null;
    var roleRecord = Ext.data.Record.create([{name:"id"}, {name:"roleid"}, {name:"rolename"}, {name:"description"}]);

    // create the Data Store
    var roleDS = new Ext.data.Store({proxy:new Ext.data.HttpProxy({url:"IPlatManager?action=listRoleAction", method:"POST"}), reader:new Ext.data.JsonReader({root:"roles", totalRecords:"totalCount", totalProperty:"totalCount"}, roleRecord)});
    var roleCm = new Ext.grid.ColumnModel([{header:"\u89d2\u8272ID", width:100, dataIndex:"id", type:"int"}, {header:"\u89d2\u8272", width:120, dataIndex:"roleid"}, {header:"\u89d2\u8272\u522b\u540d", width:120, dataIndex:"rolename"}, {header:"\u89d2\u8272\u63cf\u8ff0", width:300, dataIndex:"description"}]);
    roleCm.defaultSortable = true;

    // create the grid
    var roleGrid = new Ext.grid.Grid("roleManage", {ds:roleDS, cm:roleCm});
    roleGrid.addListener("rowdblclick", launchDetail);
    roleGrid.addListener("rowclick", rowClick);
    roleGrid.render();
    function rowClick(grid, rowIndex, columnIndex, e) {
        currentRow = rowIndex;
    }
    //roleDS.load();
    function launchDetail(grid, rowIndex, columnIndex, e) {
        //var pudID=this.ds.getAt(rowIndex).data["pudID"];
        //alert("apptype--"+roleDS.getAt(rowIndex).data["username"]);
        var row = roleDS.data.items[rowIndex];
        var id = row.data["id"];
        var roleid = row.data["roleid"];
        var rolename = row.data["rolename"];
        var description = row.data["description"];
        roleManager.showEditDialog(id, roleid, rolename, description);
    }
    var roleGridHeader = roleGrid.getView().getHeaderPanel(true);
    var queryPaging = new Ext.PagingToolbar(roleGridHeader, roleDS, {pageSize:queryPageSize, displayInfo:true, displayMsg:"\u663e\u793a\u8bb0\u5f55 {0} - {1} of {2}", emptyMsg:"\u6ca1\u6709\u8bb0\u5f55"});
    queryPaging.add("-", {pressed:false, enableToggle:true, text:"\u65b0\u589e\u89d2\u8272", toggleHandler:addRole});
    queryPaging.add("-", {pressed:false, enableToggle:true, text:"\u7f16\u8f91\u89d2\u8272", toggleHandler:editresource});
    roleDS.load({params:{start:0, limit:queryPageSize}});
    function addRole() {
        roleManager.showNewDialog();
    }
    function editresource() {
        if (currentRow === null) {
            Ext.MessageBox.alert("\u63d0\u793a", "\u8bf7\u5148\u9009\u62e9\u4e00\u884c\u6570\u636e.");
        } else {
            var row = roleDS.data.items[currentRow];
            var id = row.data["id"];
            var roleid = row.data["roleid"];
            var rolename = row.data["rolename"];
            var description = row.data["description"];
            roleManager.showEditDialog(id, roleid, rolename, description);
        }
    }
    var roleManager = function () {
        var newDialog, editDialog, myForm, editfs, newfs, resDS, usersDs;
        function makeResCheckBox(value, p, record) {
            var checkBox;
            if (value) {
                resCheck(record.data["id"], value);
                checkBox = "<input type='checkbox' name='resCheck_" + record.data["id"] + "' onClick=\"resCheck('" + record.data["id"] + "',this.checked)\" checked>";
            } else {
                checkBox = "<input type='checkbox' name='resCheck_" + record.data["id"] + "' onClick=\"resCheck('" + record.data["id"] + "',this.checked)\">";
            }
            return checkBox;
        }
        function makeUserCheckBox(value, p, record) {
            var checkBox;
            if (value) {
                userCheck(record.data["id"], value);
                checkBox = "<input type='checkbox' name='userCheck_" + record.data["id"] + "' onClick=\"userCheck('" + record.data["id"] + "',this.checked)\" checked>";
            } else {
                checkBox = "<input type='checkbox' name='userCheck_" + record.data["id"] + "' onClick=\"userCheck('" + record.data["id"] + "',this.checked)\">";
            }
            return checkBox;
        }
        return {init:function () {
            myForm = Ext.get("formErt");
        }, showEditDialog:function (id, roleid, rolename, desc) {
            if (!editDialog) { // lazy initialize the dialog and only create it once
                function optProxytype(value, p, record) {
                    if (value == '1') {
                        return '内网资源';
                    } else {
                        return '外网资源';
                    }
                }

                editDialog = new Ext.LayoutDialog("editRolePanel", {title:"\u7f16\u8f91\u89d2\u8272", modal:true, width:500, height:385, shadow:true, minWidth:300, resizable:false, minHeight:300, proxyDrag:true, autoScroll:true, center:{margins:{left:3, top:3, right:3, bottom:3}, autoScroll:true}});
                editDialog.addKeyListener(27, editDialog.hide, editDialog);

                function SaveConfig(btn) {
                    if (btn == "yes") {
                        var querycallback = {success:responseSuccessInfo, failure:responseFailureInfo};
                        YAHOO.util.Connect.asyncRequest("POST", "IPlatManager?action=saveRoleAction", querycallback, myForm.getValues(true));
                    }
                }
                var responseSuccessInfo = function (o) {
                    Ext.MessageBox.alert("\u6210\u529f", "\u4fee\u6539\u5b8c\u6210.");
					//editDialog.hide();
                    //列表数据
                    roleDS.load({params:{start:0, limit:queryPageSize}});
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
                var resRecord = Ext.data.Record.create([{name:"id"}, {name:"resvalue"}, {name:"resname"}, {name:"proxytype"}, {name:"description"}, {name:"isselect"}]);
                var resCm = new Ext.grid.ColumnModel([{header:"\u662f\u5426\u9009\u62e9", width:100, dataIndex:"isselect", type:"boolean", renderer:makeResCheckBox}, {header:"\u8d44\u6e90ID", width:100, dataIndex:"id", type:"int"}, {header:"\u8d44\u6e90\u540d\u79f0", width:100, dataIndex:"resname"}, {header:"\u8d44\u6e90\u5185\u5bb9", width:200, dataIndex:"resvalue"},{header:"资源位置", width:100, dataIndex:"proxytype",renderer:optProxytype}, {header:"\u8d44\u6e90\u63cf\u8ff0", width:100, dataIndex:"description"}]);
                resDS = new Ext.data.Store({reader:new Ext.data.JsonReader({root:"reses", totalRecords:"totalCount", totalProperty:"totalCount"}, resRecord)});
                var resGrid = new Ext.grid.Grid("userResourceList", {ds:resDS, cm:resCm});
                resGrid.render();
                var userRecord = Ext.data.Record.create([{name:"id"}, {name:"userid"}, {name:"username"}, {name:"description"}, {name:"isselect"}]);
                usersDs = new Ext.data.Store({reader:new Ext.data.JsonReader({root:"users", totalRecords:"totalCount", totalProperty:"totalCount"}, userRecord)});
                var usersCm = new Ext.grid.ColumnModel([{header:"\u662f\u5426\u9009\u62e9", width:100, dataIndex:"isselect", type:"boolean", renderer:makeUserCheckBox}, {header:"\u7528\u6237ID", width:100, dataIndex:"id", type:"int"}, {header:"\u767b\u9646\u8d26\u6237", width:100, dataIndex:"userid"}, {header:"\u7528\u6237\u540d\u79f0", width:100, dataIndex:"username"}, {header:"\u7528\u6237\u63cf\u8ff0", width:300, dataIndex:"description"}]);
                var usersGrid = new Ext.grid.Grid("userList", {ds:usersDs, cm:usersCm});
                usersGrid.render();
        //--- setup dialog layout
                var layout = editDialog.getLayout();
                layout.beginUpdate();
                layout.add("center", new Ext.ContentPanel("editRole", {title:"\u89d2\u8272\u5c5e\u6027"}));
                layout.add("center", new Ext.GridPanel(resGrid, {title:"\u5173\u8054\u8d44\u6e90"}));
                layout.add("center", new Ext.GridPanel(usersGrid, {title:"\u7528\u6237\u6620\u5c04"}));
                layout.endUpdate();
        
        //--- build a form
                editfs = new Ext.form.Form({labelAlign:"left", labelWidth:80});
                editfs.fieldset({legend:"\u89d2\u8272\u4fe1\u606f"}, new Ext.form.TextField({fieldLabel:"ID", name:"id", width:190, readOnly:true, allowBlank:true}), new Ext.form.TextField({fieldLabel:"\u89d2\u8272", name:"roleid", width:190, allowBlank:false}), new Ext.form.TextField({fieldLabel:"\u89d2\u8272\u522b\u540d", name:"rolename", width:190, allowBlank:false}), new Ext.form.TextArea({fieldLabel:"\u89d2\u8272\u63cf\u8ff0", name:"description", width:190, grow:false, preventScrollbars:false}));
                editfs.render("editForm");
                editfs.el.createChild({tag:"input", type:"hidden", name:"resids"});
                editfs.el.createChild({tag:"input", type:"hidden", name:"userids"});
            }
			//重置数组
            resArray = new Array();
            editfs.findField("id").setValue(id);
            editfs.findField("roleid").setValue(roleid);
            editfs.findField("rolename").setValue(rolename);
            editfs.findField("description").setValue(desc);
			//editfs.findField("resids").setValue("");
            editfs.findField("roleid").el.dom.readOnly = true;
            resDS.proxy = new Ext.data.HttpProxy({url:"IPlatManager?action=listRoleResAction&roleid=" + id, method:"POST"});
            resDS.load();
            usersDs.proxy = new Ext.data.HttpProxy({url:"IPlatManager?action=listRoleUserAction&roleid=" + id, method:"POST"});
            usersDs.load();
            myForm = editfs;
            editDialog.show();
        }, showNewDialog:function () {
            if (!newDialog) { // lazy initialize the dialog and only create it once
                newDialog = new Ext.LayoutDialog("newRolePanel", {title:"\u65b0\u589e\u89d2\u8272", modal:true, width:500, height:385, shadow:true, minWidth:300, resizable:false, minHeight:300, proxyDrag:true, autoScroll:true, center:{margins:{left:3, top:3, right:3, bottom:3}, autoScroll:true}});
                newDialog.addKeyListener(27, newDialog.hide, newDialog);
                function SaveConfig(btn) {
                    if (btn == "yes") {
                        var querycallback = {success:responseSuccessInfo, failure:responseFailureInfo};
                        YAHOO.util.Connect.asyncRequest("POST", "IPlatManager?action=saveRoleAction", querycallback, myForm.getValues(true));
                    }
                }
                var responseSuccessInfo = function (o) {
                    //Ext.MessageBox.alert("\u6210\u529f", "\u4fee\u6539\u5b8c\u6210.");
                    newDialog.hide();
        	//列表数据
                    roleDS.load({params:{start:0, limit:queryPageSize}});
                };
                var responseFailureInfo = function (o) {
                    //Ext.MessageBox.alert("u5931u8d25", "u4feeu6539u5931u8d25.");
                    Ext.MessageBox.alert(o.responseText);
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
                layout.add("center", new Ext.ContentPanel("newRole", {title:"\u89d2\u8272\u5c5e\u6027"}));
                layout.endUpdate();
        
        //--- build a form
                newfs = new Ext.form.Form({labelAlign:"left", labelWidth:80});
                newfs.fieldset({legend:"\u89d2\u8272\u4fe1\u606f"}, new Ext.form.TextField({fieldLabel:"ID", name:"id", width:190, readOnly:true, allowBlank:true}), new Ext.form.TextField({fieldLabel:"\u89d2\u8272", name:"roleid", width:190, allowBlank:false}), new Ext.form.TextField({fieldLabel:"\u89d2\u8272\u522b\u540d", name:"rolename", width:190, allowBlank:false}), new Ext.form.TextArea({fieldLabel:"\u89d2\u8272\u63cf\u8ff0", name:"description", width:190, grow:false, preventScrollbars:false}));
            }
            newfs.render("newForm");

            newfs.findField("id").setValue("");
            newfs.findField("roleid").setValue("");
            newfs.findField("rolename").setValue("");
            newfs.findField("description").setValue("");
            myForm = null;
            myForm = newfs;
            newDialog.show();
        }};
    }();
});

