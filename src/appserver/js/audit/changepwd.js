Ext.onReady(function () {

    function checkNewPwd(newPwd) {
        return newPwd == pwdForm.findField("repeatPwd").getValue();
    }

    function checkRepPwd(repeatPwd) {
        return repeatPwd == pwdForm.findField("newPwd").getValue();
    }
    var pwdForm = new Ext.form.Form({labelAlign:"right", buttonAlign:"center", labelWidth:80});
    pwdForm.fieldset({legend:"\u4fee\u6539\u5bc6\u7801"}, new Ext.form.TextField({fieldLabel:"\u539f\u5bc6\u7801", name:"oldPwd", inputType:"password", allowBlank:true, width:150}), new Ext.form.TextField({fieldLabel:"\u65b0\u5bc6\u7801", name:"newPwd",validator:checkNewPwd, inputType:"password", allowBlank:true, width:150}), new Ext.form.TextField({fieldLabel:"\u91cd\u590d\u5bc6\u7801", name:"repeatPwd", inputType:"password", validator:checkRepPwd, allowBlank:true, width:150}));
    pwdForm.addButton("\u4fdd\u5b58", function () {
        if (pwdForm.isValid()) {
            var querycallback = {success:responseSuccessInfo, failure:responseFailureInfo};
            YAHOO.util.Connect.asyncRequest("POST", "IPlatManager?action=changePwdAction", querycallback, pwdForm.getValues(true));
        }
    });
    var responseSuccessInfo = function (o) {
        alert(o.responseText);
    };
    var responseFailureInfo = function (o) {
        alert(o.responseText);
    };
    pwdForm.render("pwdForm");
});