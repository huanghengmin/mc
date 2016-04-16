package com.inetec.ichange.mc.action;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.inetec.common.exception.Ex;

import com.inetec.ichange.mc.utils.ReStartSystem;

public class SystemRestartAction extends IAction {
	static Logger logger = Logger.getLogger(SystemRestartAction.class);

	public void execute(HttpServletRequest request, HttpServletResponse response)
			throws Ex {
		    logger.info("begin restart mc service");
		    //response.
			ReStartSystem.restart();
			logger.info("end restart mc service");
	}
}
