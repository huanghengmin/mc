package com.inetec.ichange.mc.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.inetec.common.exception.Ex;
import com.inetec.ichange.mc.pojo.DeviceBean;

public class DelDeviceAction extends IAction {
	static Logger logger = Logger.getLogger(DelDeviceAction.class);

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response)
			throws Ex {
		String id = request.getParameter("id");
		

		DeviceBean bean = new DeviceBean();
		bean.setId(id);
		

		

		deviceDAO.delDevice(bean);
	}
}
