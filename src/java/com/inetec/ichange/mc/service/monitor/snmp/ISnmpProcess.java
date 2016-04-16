package com.inetec.ichange.mc.service.monitor.snmp;

import com.inetec.ichange.mc.pojo.DeviceBean;
import com.inetec.ichange.mc.pojo.SnmpOIDBean;

/**
 * Created by IntelliJ IDEA.
 * User: bluesky
 * Date: 2010-10-31
 * Time: 10:05:47
 * To change this template use File | Settings | File Templates.
 */
public interface ISnmpProcess extends Runnable {
	public static final int I_SleepTime=10*1000;

	 public void init(DeviceBean bean,SnmpOIDBean snmpoidbean);
	 public boolean isRun();
	 public void close();

}
