package com.inetec.ichange.mc.service.monitor.snmp;

import com.inetec.ichange.mc.pojo.DeviceBean;
import com.inetec.ichange.mc.pojo.SnmpOIDBean;

public class SnmpProcessV1TrapImp implements ISnmpProcess{
	private boolean isRun=false;
	@Override
	public void init(DeviceBean bean, SnmpOIDBean snmpoidbean) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isRun() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	public void close() {
		// TODO Auto-generated method stub
		isRun=false;
	}

}
