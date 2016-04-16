package com.inetec.ichange.common.syslog;


import com.inetec.ichange.common.syslog.code.SyslogCodecFactory;
import com.inetec.ichange.mc.service.IPlatManager;
import com.inetec.ichange.mc.service.monitor.snmp.SnmpMonitorService;
import com.inetec.ichange.mc.service.monitor.syslog.format.KoalTbsgLog;
import com.inetec.ichange.mc.service.monitor.syslog.format.KoalVpnLog;
import com.inetec.ichange.mc.service.monitor.syslog.format.LogFormatFactory;
import org.apache.log4j.Logger;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.executor.OrderedThreadPoolExecutor;
import org.apache.mina.transport.socket.DatagramSessionConfig;
import org.apache.mina.transport.socket.nio.NioDatagramAcceptor;
import org.productivity.java.syslog4j.Syslog;
import org.productivity.java.syslog4j.SyslogIF;

import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * Created by IntelliJ IDEA.
 * User: bluesky
 * Date: 2009-11-7
 * Time: 23:39:55
 * To change this template use File | Settings | File Templates.
 */
public class SyslogServer extends Thread {
    public static final Logger logger = Logger.getLogger(SyslogServer.class);
    public static Map syslogMap = new HashMap();
    public static Map syslogMapTime = new HashMap();
    private int timeout = 1;
    private boolean m_runing = false;
    private NioDatagramAcceptor acceptor;

    private SyslogHandler handler;
    private String charset;
    private ExecutorService filterExecutor;
    private ExecutorService acceptExecutor;
    private String sysloghost;
    private int syslogport;
    private String cmsip;
    private int cmsPort;
    private SyslogIF syslogClient;

    public boolean isRun() {
        return m_runing;
    }

    public void setRemtoeServer(String cmsip, int cmsPort) {
        this.cmsip = cmsip;
        this.cmsPort = cmsPort;
        syslogClient = Syslog.getInstance("udp");
        syslogClient.getConfig().setHost(cmsip);
        syslogClient.getConfig().setPort(cmsPort);
        syslogClient.getConfig().setCharSet("GBK");

    }

    public void info(String deviceid, String ip, String message) {
        if (syslogClient != null) {
            syslogClient.info("deviceid=" + deviceid + " ip=" + ip + " ," + message);
        }
        logger.info("sent syslog to cms syslog:" + "deviceid=" + deviceid + "," + message);

    }

    public void warn(String deviceid, String ip, String message) {
        if (syslogClient != null) {
            syslogClient.warn("deviceid=" + deviceid + " ip=" + ip + " , " + message);
        }
        logger.warn("sent syslog to cms syslog:" + "deviceid=" + deviceid + " ip=" + ip + " ," + message);
    }

    public void error(String deviceid, String ip, String message) {
        if (syslogClient != null) {
            syslogClient.error("deviceid=" + deviceid + "," + message);
        }
        logger.error("sent syslog to cms syslog:" + "deviceid=" + deviceid + " ip=" + ip + " ," + message);
    }

    public void processSyslog(SyslogMessage syslog) {


        String host = syslog.getHostName();
        logger.info("recv syslog host:" + host);

        String deviceid = SnmpMonitorService.dataset
                .getDeviceIDByIP(host);
        logger.info("deviceid="
                + deviceid
                + " ip=" + host + " " + syslog.getMessage());


        if (SyslogMessage.getPriorityName(syslog.getServerty()).equalsIgnoreCase("warn")) {
            syslogClient
                    .warn("deviceid="
                            + deviceid + " ip="
                            + host + " "
                            + syslog.getMessage());
            syslogClient.flush();
        }
        if (SyslogMessage.getPriorityName(syslog.getServerty()).equalsIgnoreCase("info")) {
            if (LogFormatFactory.checkFormat(syslog.getMessage())) {
//
                if(syslog.getMessage().contains("serviceid=\"1\"")){
                    KoalTbsgLog log = new KoalTbsgLog();
                    log.process(syslog.getMessage(), "info");
                    IPlatManager.terminalinfServcie.terminalCache.updateOnlineStatus(log.getUserip(), log.getIdentity(), log.getIn_Flux(), log.getOut_Flux());
                }else {
                    KoalVpnLog log = new KoalVpnLog();
                    log.process(syslog.getMessage(), "info");
                    IPlatManager.terminalinfServcie.terminalCache.updateOnlineStatus(log.getUserip(), log.getIdentity(), log.getIn_Flux(), log.getOut_Flux());
                }



            }
            if(syslog.getMessage().contains("logFlag")){
                syslogClient
                        .info(syslog.getMessage());
                logger.info(syslog.getMessage());
            }else{
                syslogClient
                        .info("deviceid="
                                + deviceid + " ip="
                                + host + " "
                                + syslog.getMessage());

            }
            syslogClient.flush();

        }
        if (SyslogMessage.getPriorityName(syslog.getServerty()).equalsIgnoreCase("error")) {
            syslogClient
                    .error("deviceid="
                            + deviceid + " ip="
                            + host + " "
                            + syslog.getMessage());
            syslogClient.flush();
        }

        syslogMap.put(host,"true");
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        syslogMapTime.put(host,"最后一次收到时间"+df.format(new Date()));
    }

    public void close() {
        m_runing = false;
        if (acceptor != null) {
            if (sysloghost != null)
                acceptor.unbind(new InetSocketAddress(sysloghost, syslogport));
            else {
                acceptor.unbind(new InetSocketAddress(syslogport));
            }

            acceptor.dispose();
            if (filterExecutor != null)
                filterExecutor.shutdown();
            if (acceptExecutor != null) {
                acceptExecutor.shutdown();

            }
        }

        logger.info("Syslog service Run stop.port:" + syslogport);
    }


    public void config(String host, int port, String charset) {


        sysloghost = host;

        syslogport = port;
        this.filterExecutor = new OrderedThreadPoolExecutor(Runtime.getRuntime().availableProcessors() + 1);
        this.acceptExecutor = new OrderedThreadPoolExecutor(Runtime.getRuntime().availableProcessors() + 1);


    }


    public void run() {
        m_runing = true;
        try {
            startSyslog();
        } catch (Exception e) {
            logger.warn("Syslog  service Run error.port:" + syslogport, e);
        }
    }

    private void startSyslog() throws Exception {
        // Create UDP/IP acceptor.
        acceptor = new NioDatagramAcceptor();
        DatagramSessionConfig dcfg = acceptor.getSessionConfig();
        dcfg.setReuseAddress(true);
        acceptor.getFilterChain().addLast("threadPool", new ExecutorFilter(filterExecutor));//
        acceptor.getFilterChain().addLast("codec",
                new ProtocolCodecFilter(new SyslogCodecFactory()));
        //acceptor.getSessionConfig().setKeepAlive(true);
        handler = new SyslogHandler(this);
        acceptor.setHandler(handler);
        acceptor.getSessionConfig().setBothIdleTime(timeout * 60);

        //acceptor.getSessionConfig().setSoLinger(0);

        //acceptor.setReuseAddress(true);


        if (sysloghost != null)
            acceptor.bind(new InetSocketAddress(sysloghost, syslogport));
        else
            acceptor.bind(new InetSocketAddress(syslogport));
    }


    public static void main(String arg[]) throws Exception {
//        SyslogServer syslog = new SyslogServer();
//        syslog.config(null, 514, "GBK");
//        syslog.setRemtoeServer("172.16.2.6", 1514);
//        syslog.start();
//        while (true) {
//            Thread.sleep(60 * 1000);
//        }


    }


}
