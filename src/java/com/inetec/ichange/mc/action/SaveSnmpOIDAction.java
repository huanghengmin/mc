package com.inetec.ichange.mc.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.inetec.common.exception.Ex;
import com.inetec.ichange.mc.pojo.SnmpOIDBean;

public class SaveSnmpOIDAction extends IAction {
	static Logger logger = Logger.getLogger(SaveSnmpOIDAction.class);

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response)
			throws Ex {
		String name = request.getParameter("name");
		String company = request.getParameter("company");
		String type = request.getParameter("type");
		String snmpver = request.getParameter("snmpver");
		String cpuuse = request.getParameter("cpuuse");
		String disktotal = request.getParameter("disktotal");
		String diskuse = request.getParameter("diskuse");
		String memtotal = request.getParameter("memtotal");
		String memuse = request.getParameter("memuse");
		String curconn = request.getParameter("curconn");
		
		SnmpOIDBean bean = new SnmpOIDBean();
		bean.setName(name);
		bean.setCompany(company);
		bean.setType(type);
		bean.setCpuuse(cpuuse);
		bean.setCurconn(curconn);
		bean.setDisktotal(disktotal);
		bean.setDiskuse(diskuse);
		bean.setMemtotal(memtotal);
		bean.setMemuse(memuse);
		bean.setSnmpver(snmpver);


		snmpoidDAO.saveDevice(bean);
	}
}
