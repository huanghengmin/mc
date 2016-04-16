package com.inetec.ichange.mc.service.monitor.snmp;

import com.inetec.ichange.mc.pojo.DeviceBean;
import com.inetec.ichange.mc.pojo.SnmpOIDBean;
import com.inetec.ichange.mc.service.monitor.utils.DeviceDataBeanSet;
import com.inetec.ichange.mc.service.monitor.utils.SnmpProcessFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Snmp  Created by IntelliJ IDEA. User: bluesky Date: 2010-9-7 Time:
 * 14:43:58 To change this template use File | Settings | File Templates.
 */
public class SnmpMonitorService extends Thread {

    private boolean isRun = false;
    private boolean isInit = false;
    public static DeviceDataBeanSet dataset = new DeviceDataBeanSet();
    public static SnmpAgentService snmpAgent = new SnmpAgentService();
    public List<ISnmpProcess> snmps;
    private List<SnmpOIDBean> snmpOIDS;

    public SnmpMonitorService() {

    }

    /**
     * ≥ı ºªØ
     *
     * @param beans
     */
    public void init(List<DeviceBean> beans, List<SnmpOIDBean> snmpoid, int port) {
        if (beans == null) {
            isInit = false;
            return;

        }
        dataset.init(beans);
        snmpAgent.init("0.0.0.0", port, "utf-8");
        snmpOIDS = snmpoid;
        snmps = new ArrayList();

        for (int i = 0; i < beans.size(); i++) {
            try {

                if (beans.get(i).getAvailable().equalsIgnoreCase("on")) {
                    SnmpOIDBean bean=getSnmpOIDByName(
                            beans.get(i).getName());
                    ISnmpProcess process = SnmpProcessFactory
                            .getSnmpProcessByVer(bean.getSnmpver(),bean.getCompany(),bean.getType());
                    process.init(beans.get(i), bean);
                    snmps.add(process);
                }
            } catch (RuntimeException e) {
                System.out.print(e);
            } catch (Exception e) {
                System.out.print(e);
            }

        }
        isInit = true;

    }

    public boolean isRun() {
        return isRun;
    }

    public void run() {
        isRun = true;
        if (isInit) {
            snmpAgent.start();
            snmpProcessRun();
            while (isRun) {
                try {
                    Thread.sleep(10*60 * 1000);
                } catch (InterruptedException e) {
                    // okays
                }
            }
        }
    }

    public void close() {
        isRun = false;
        snmpAgent.close();
        snmpProcessClose();

    }

    public SnmpOIDBean getSnmpOIDByName(String name) {
        SnmpOIDBean result = null;
        for (int i = 0; i < snmpOIDS.size(); i++) {
            if (snmpOIDS.get(i).getName().equalsIgnoreCase(name)) {
                result = snmpOIDS.get(i);
            }
        }
        return result;
    }

    private void snmpProcessRun() {
        for (int i = 0; i < snmps.size(); i++) {
            new Thread(snmps.get(i)).start();
        }
    }

    private void snmpProcessClose() {
        for (int i = 0; i < snmps.size(); i++) {
            snmps.get(i).close();
        }
    }

}
