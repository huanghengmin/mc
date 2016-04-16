Ext.onReady(function(){
    Ext.BLANK_IMAGE_URL = '../../js3/ext/resources/images/default/s.gif';
    Ext.QuickTips.init();
    Ext.form.Field.prototype.msgTarget = 'side';
    var systemRestartPanel = new Ext.Panel({
		id:'sysRestart.info',
		html:"<a href='javascript:;' onclick='systemRestart();' onmouseover='systemRestartMouseOver(event);' onmouseout='systemRestartMouseOut();' ><img src='../../images/icon/systemRestart.png' /></a>"
	});
	var equipmentRestartPanel = new Ext.Panel({
		id:'equiRestart.info',
		html:"<a href='javascript:;' onclick='equipmentRestart();' onmouseover='equipmentRestartMouseOver(event);' onmouseout='equipmentRestartMouseOut();'><img src='../../images/icon/equipmentRestart.png' /></a>"
	});
	var equipmentShutdownPanel = new Ext.Panel({
		id:'equiShutdown.info',
		html:"<a href='javascript:;' onclick='equipmentShutdown();' onmouseover='equipmentShutMouseOver(event);' onmouseout='equipmentShutMouseOut();'><img src='../../images/icon/equipmentShutdown.png' /></a>"
	});
	new Ext.Viewport({
        renderTo:Ext.getBody(),
        layout:'fit',
        items:[
        	{
        		layout:'form',
        		frame:true,
        		autoScroll:true,
        		items:[
        			{plain:true,height:50},
		        	{
			        	layout:'column',
			        	plain:true,
			        	items:[
			        		{items:[systemRestartPanel],columnWidth:.33},
			        		{items:[equipmentRestartPanel],columnWidth:.33},
			        		{items:[equipmentShutdownPanel],columnWidth:.33}

			        	]
		        	}
        		]
        	}
        ]
    });
});
function mouseCoords(ev){
	 if(ev.pageX || ev.pageY){
	   return {x:ev.pageX, y:ev.pageY};
	 }
	 return {
	     x:ev.clientX + document.body.scrollLeft - document.body.clientLeft,
	     y:ev.clientY + document.body.scrollTop - document.body.clientTop
	 };
}
	var winAlert = new Ext.Window({
		id:'mouseWin.sys.info',
		title:'重启系统',
		closable:false,
		width:200,
		height:80,
		html:"重新启动中监控平台!"
	});
function systemRestartMouseOver(ev){
	ev= ev || window.event;
	Ext.getCmp('mouseWin.sys.info').x=mouseCoords(ev).x;
	Ext.getCmp('mouseWin.sys.info').y=mouseCoords(ev).y;
	Ext.getCmp('mouseWin.sys.info').show();
}
function systemRestartMouseOut(){
	Ext.getCmp('mouseWin.sys.info').hide();
}
	var winAlert = new Ext.Window({
		id:'mouseWin.equiRestart.info',
		title:'重启设备',
		closable:false,
		width:200,
		height:80,
		html:"重新启动当前运行的设备!"
	});

function equipmentRestartMouseOver(ev){
	ev= ev || window.event;
	Ext.getCmp('mouseWin.equiRestart.info').x=mouseCoords(ev).x;
	Ext.getCmp('mouseWin.equiRestart.info').y=mouseCoords(ev).y;
	Ext.getCmp('mouseWin.equiRestart.info').show();
}
function equipmentRestartMouseOut(){
	Ext.getCmp('mouseWin.equiRestart.info').hide();
}
	var winAlert = new Ext.Window({
		id:'mouseWin.equiShut.info',
		title:'关闭设备',
		closable:false,
		width:200,
		height:80,
		html:"关闭当前运行的设备。"
	});
function equipmentShutMouseOver(ev){
	ev= ev || window.event;
	Ext.getCmp('mouseWin.equiShut.info').x=mouseCoords(ev).x;
	Ext.getCmp('mouseWin.equiShut.info').y=mouseCoords(ev).y;
	Ext.getCmp('mouseWin.equiShut.info').show();
}
function equipmentShutMouseOut(){
	Ext.getCmp('mouseWin.equiShut.info').hide();
}

function systemRestart(){
    Ext.MessageBox.show({
	    title:"信息",
        width:250,
		msg:"确定要重启系统吗?",
		animEl:'sysRestart.info',
		icon:Ext.MessageBox.WARNING,
		buttons:Ext.MessageBox.YESNO,
		buttons:{'ok':'确定','no':'取消'},
		fn:function(e){
			if(e=='ok'){
				var myMask = new Ext.LoadMask(Ext.getBody(), {
					msg: '正在重启系统,请稍后...',
					removeMask: true
				});
				myMask.show();
				Ext.Ajax.request({
					url:'../../PlatformAction_sysRestart.action',
					method:'POST',
					success:function(r,o){
                        myMask.hide();
                        var respText = Ext.util.JSON.decode(r.responseText);
                        var msg = respText.msg;
                        Ext.MessageBox.show({
                            title:"信息",
                            msg:msg,
                            animEl:'sysRestart.info',
                            icon:Ext.MessageBox.INFO,
                            buttons:Ext.MessageBox.OK,
                            buttons:{'ok':'确定'}
                        });
                    }
				});
			}
		}
	});
}
function equipmentRestart(){
	Ext.MessageBox.show({
		title:"信息",
        width:250,
		msg:"确定要重启设备吗?",
		animEl:'equipRestart.info',
		icon:Ext.MessageBox.WARNING,
		buttons:Ext.MessageBox.YESNO,
		buttons:{'ok':'确定','no':'取消'},
		fn : function(e){
			if(e=='ok'){
				var myMask = new Ext.LoadMask(Ext.getBody(), {
					msg: '正在重启设备,请稍后...',
					removeMask: true //完成后移除
				});
				myMask.show();
				Ext.Ajax.request({
					url:'../../PlatformAction_equipRestart.action',
					method:'POST',
					success:function(r,o){
						myMask.hide();
                        var respText = Ext.util.JSON.decode(r.responseText);
                        var msg = respText.msg;
                        Ext.MessageBox.show({
                            title:"信息",
                            msg:msg,
                            animEl:'equipRestart.info',
                            icon:Ext.MessageBox.INFO,
                            buttons:Ext.MessageBox.OK,
                            buttons:{'ok':'确定'}
                        });
                    }
				});
			}
		}
	});
}

function equipmentShutdown(){
	Ext.MessageBox.show({
		title:"信息",
        width:250,
		msg:"确定要关闭设备吗?",
		animEl:'equipShutdown.info',
		icon:Ext.MessageBox.WARNING,
		buttons:Ext.MessageBox.YESNO,
		buttons:{'ok':'确定','no':'取消'},
		fn:function(e){
			if(e=='ok'){
				var myMask = new Ext.LoadMask(Ext.getBody(), {
					msg: '正在关闭设备,请稍后...',
					removeMask: true //完成后移除
				});
				myMask.show();
				Ext.Ajax.request({
					url:'../../PlatformAction_equipShutdown.action',
					method:'POST',
					success:function(r,o){
						myMask.hide();
                        var respText = Ext.util.JSON.decode(r.responseText);
                        var msg = respText.msg;
                        Ext.MessageBox.show({
                            title:"信息",
                            msg:msg,
                            animEl:'equipShutdown.info',
                            icon:Ext.MessageBox.INFO,
                            buttons:Ext.MessageBox.OK,
                            buttons:{'ok':'确定'}
                        });
                    }
				});
			}
		}
	});
}