package com.inetec.ichange.mc.service.http.client;

import org.apache.log4j.Logger;

import com.inetec.ichange.mc.service.monitor.databean.SysterminalinfDataBean;
import com.inetec.ichange.mc.service.monitor.utils.TerminalInfoCache;

public class TerminalinfService extends Thread {
    private static final Logger log = Logger.getLogger(TerminalinfService.class);
    private boolean isRun = false;
    private String host;
    public static final int I_SleepTime = 1 * 60 * 1000;
    public static TerminalInfoCache terminalCache = new TerminalInfoCache();

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        log.info("TerminalinfService host is : " + host);
        this.host = host;
    }

    /**
     *
     */
    public boolean isRun() {
        return isRun;
    }

    public void setRun(boolean isRun) {
        this.isRun = isRun;
    }

    public void run() {
        isRun = true;
        while (isRun) {
            if (host != null) {
                processTerminalInfo();
            }
            try {
                Thread.sleep(I_SleepTime);
            } catch (InterruptedException e) {
                // okay
            }
        }
        isRun = false;
    }

    private boolean processTerminalInfo() {
        boolean result = false;
        log.info("TerminalInfo process is start");
        JbpgHttpclient client = new JbpgHttpclient();
        client.init(host);
        try {
            String temp = client.vpnAll("0", "0", 5000);
            terminalCache.init(SysterminalinfDataBean.stringToBeans(temp));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            log.warn("TerminalInfo process is start", e);
        }
        result = true;
        return result;

    }

    public void close() {
        isRun = false;
    }

    public static void main(String arg[]) throws Exception {
        TerminalinfService server = new TerminalinfService();
        server.start();
        Thread.sleep(1 * 1000);
        System.out.println("allvpn data:" + TerminalinfService.terminalCache.getAllList("0", "00", 500));
    }

}
