package com.inetec.ichange.mc.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.inetec.common.exception.Ex;
import com.inetec.ichange.mc.pojo.DeviceBean;

public class SaveDeviceAction extends IAction {
	static Logger logger = Logger.getLogger(SaveDeviceAction.class);

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response)
			throws Ex {
		String id = request.getParameter("id");
		String name = request.getParameter("name");
		String deviceip = request.getParameter("deviceip");
		String deviceport = request.getParameter("deviceport");
		String devicesnmppwd = request.getParameter("devicesnmppwd");
		String devicetype = request.getParameter("devicetype");
		String devicecompany = request.getParameter("devicecompany");
		String available = request.getParameter("available");
		String devicemode = request.getParameter("devicemode");
		String snmpver = request.getParameter("snmpver");
		String auth=request.getParameter("auth");
		String authpassword=request.getParameter("authpassword");
		String common=request.getParameter("common");
		String commonPassword=request.getParameter("commonpassword");

		DeviceBean bean = new DeviceBean();
		bean.setId(id);
		bean.setName(name);
		// res.setRestype(type);
		bean.setDeviceip(deviceip);
		bean.setDeviceport(deviceport);
		bean.setDevicesnmppwd(devicesnmppwd);
		bean.setDevicetype(devicetype);
		bean.setDevicecompany(devicecompany);
		bean.setDevicemode(devicemode);
		bean.setAvailable(available);
		bean.setSnmpver(snmpver);
		bean.setAuth(auth);
		bean.setAuthpassword(authpassword);
		bean.setCommon(common);
		bean.setCommonpassword(commonPassword);

		

		deviceDAO.saveDevice(bean);
	}
}
