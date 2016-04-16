var pbar3;// 定义一个变量用于进度条对象
var btn3;// 定义一个变量用于进按钮对象
Ext.onReady(function() {
	pbar3 = new Ext.ProgressBar( { // 实例化进度条
				renderTo : "resourceManage", // 进度条呈现的一个DIV
				width : 300, // 进度条的宽度
				text : "单击按钮开始重启服务..." // 在进度条里的初始文本
			});
	btn3 = Ext.get("btn3"); // 按钮对象
		btn3.on("click", function() { // 按钮单击事件
					btn3.dom.disabled = true; // 把按钮设置为不可用状态
				pbar3.updateText("正在重启..."); // 更新进度条的文本信息
				pbar3.wait( { // 开始执行进度条
							interval : 100, // 每次进度的时间间隔
							duration : 5000, // 进度条跑动的持续时间
							increment : 50, // 进度条的增量，这个值设的越大，进度条跑的越慢，不能小于1，如果小于1，进度条会跑出范围
							fn : function() { // 进度条完成时执行的函数，也可设为nulll
								pbar3.updateText("重启完成");// 更新进度条的文本信息
							}
						});
			});
	});