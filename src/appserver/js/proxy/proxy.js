var ComboBoxPageSize = 5;

//统一定义分页长度
var queryPageSize = 20;


Ext.BLANK_IMAGE_URL = 'images/default/s.gif';

//在字段旁边提示错误信息
Ext.form.Field.prototype.msgTarget = 'side';
//自定义数组操作函数
Array.prototype.indexOf = function (p_var) {
	for (var i = 0; i < this.length; i++) {
		if (this[i] == p_var) {
			return (i);
		}
	}
	return (-1);
};
Array.prototype.remove = function (p_var) {
	var dx = this.indexOf(p_var);
	if (isNaN(dx) || dx < 0) {
		return false;
	} else {
		for (var i = 0, n = 0; i < this.length; i++) {
			if (this[i] != this[dx]) {
				this[n++] = this[i];
			}
		}
		this.length -= 1;
	}
};

