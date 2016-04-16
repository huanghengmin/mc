package com.inetec.ichange.mc.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.inetec.common.exception.Ex;
import com.inetec.ichange.mc.IPlatConstant;
import com.inetec.ichange.mc.pojo.*;

public abstract class IAction {
	DeviceDao deviceDAO;
    ConfigDao configDAO;
    SnmpOIDDao snmpoidDAO;
  

    

    public ConfigDao getConfigDAO() {
		return configDAO;
	}

	public void setConfigDAO(ConfigDao configDAO) {
		this.configDAO = configDAO;
	}

	public DeviceDao getDeviceDAO() {
		return deviceDAO;
	}

	public void setDeviceDAO(DeviceDao deviceDAO) {
		this.deviceDAO = deviceDAO;
	}
	
	public SnmpOIDDao getSnmpoidDAO() {
		return snmpoidDAO;
	}

	public void setSnmpoidDAO(SnmpOIDDao snmpoidDAO) {
		this.snmpoidDAO = snmpoidDAO;
	}

	public void returnStatus(HttpServletResponse response, boolean isSuccess) {
        if (isSuccess) {
            response.setHeader(IPlatConstant.PROXY_BUS_STATUSCODE, "1");
            response.setHeader(IPlatConstant.PROXY_BUS_STATUSMSG, "success");
        } else {
            response.setHeader(IPlatConstant.PROXY_BUS_STATUSCODE, "-1");
            response.setHeader(IPlatConstant.PROXY_BUS_STATUSMSG, "failure");
        }
    }

    public abstract void execute(HttpServletRequest request,
                                 HttpServletResponse response) throws Ex;
}
