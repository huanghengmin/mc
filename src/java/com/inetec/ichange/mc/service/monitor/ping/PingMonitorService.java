package com.inetec.ichange.mc.service.monitor.ping;

import com.inetec.common.exception.Ex;
import com.inetec.common.net.Ping;
import com.inetec.ichange.mc.service.IPlatManager;
import com.inetec.ichange.mc.service.monitor.snmp.SnmpMonitorService;
import com.inetec.ichange.mc.service.monitor.utils.DeviceDataBeanSet;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 12-11-13
 * Time: ????2:35
 * To change this template use File | Settings | File Templates.
 */
public class PingMonitorService extends Thread{
    private static final Logger logger = Logger.getLogger(PingMonitorService.class);
    public static Map ipMap = new HashMap();
    private static SnmpMonitorService snmpService;
    private static boolean isRun = false;
    private String str;


    public PingMonitorService() {

    }

    public void init(){

    }

    public boolean isRun() {
        return isRun;
    }

    public void run()  {
        isRun = true;


        while (isRun){
            try {
                logger.info("联通状态 ping检测 开始");
                DeviceDataBeanSet dataset = SnmpMonitorService.dataset;
                ConcurrentHashMap ipbeanset = dataset.ipbeanset;
                Set<Map.Entry> ipset = ipbeanset.entrySet();
                for (Iterator<Map.Entry> it = ipset.iterator(); it.hasNext();) {
                    Map.Entry ipentry =  it.next();
                    String ip = (String) ipentry.getKey();
                    String name = (String) ipentry.getValue();
                    try {
                        str = Ping.exec(ip, 1);
                        if(str.indexOf("ttl")>-1|str.indexOf("TTL")>-1){
                            ipMap.put(ip,"true");
                        }else {
                            ipMap.put(ip,"false");
                            IPlatManager.syslogMonitorService.warn(name, ip, "device ping is error");
                        }
                    } catch (Ex ex) {
                        logger.info(ex+"\n"+"ping"+ip+"失败");
                    }
                }
                Thread.sleep(2*60*1000);
            } catch (InterruptedException e) {

            }
        }
    }

}
