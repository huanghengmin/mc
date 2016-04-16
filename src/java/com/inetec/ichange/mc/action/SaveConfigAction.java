package com.inetec.ichange.mc.action;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.inetec.common.exception.Ex;
import com.inetec.ichange.mc.pojo.ConfigBean;
public class SaveConfigAction extends IAction {
	static Logger logger = Logger.getLogger(SaveConfigAction.class);

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response)
			throws Ex {
		//String proteId = request.getParameter("proteId");
		String descr = request.getParameter("descr");
		String ip = request.getParameter("ip");
		String port = request.getParameter("port");
		String cmsip = request.getParameter("cmsip");
		String cmsport = request.getParameter("cmsport");
		String cmssysport = request.getParameter("cmssysport");
		String sysport = request.getParameter("sysport");
		

		ConfigBean bean = new ConfigBean();
		//bean.setProteId(proteId);
		bean.setDescr(descr);
		bean.setIp(ip);
		bean.setPort(port);
		bean.setCmsip(cmsip);
		bean.setCmsport(cmsport);
		bean.setSysport(sysport);
		bean.setCmssysport(cmssysport);
		configDAO.saveDevice(bean);
	}

}
