package com.inetec.ichange.mc.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.inetec.common.exception.Ex;
import com.inetec.ichange.mc.pojo.SnmpOIDBean;

public class DelSnmpOIDAction extends IAction {
	static Logger logger = Logger.getLogger(DelSnmpOIDAction.class);

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response)
			throws Ex {
		String name = request.getParameter("name");
	
		
		SnmpOIDBean bean = new SnmpOIDBean();
		bean.setName(name);
	


		snmpoidDAO.delSnmpOID(bean);
	}
}
