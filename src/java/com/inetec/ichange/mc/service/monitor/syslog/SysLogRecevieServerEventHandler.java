package com.inetec.ichange.mc.service.monitor.syslog;

import org.apache.log4j.Logger;
import org.productivity.java.syslog4j.Syslog;
import org.productivity.java.syslog4j.SyslogIF;
import org.productivity.java.syslog4j.server.SyslogServerEventHandlerIF;
import org.productivity.java.syslog4j.server.SyslogServerEventIF;
import org.productivity.java.syslog4j.server.SyslogServerIF;
import org.productivity.java.syslog4j.util.SyslogUtility;



import com.inetec.ichange.mc.service.IPlatManager;
import com.inetec.ichange.mc.service.monitor.snmp.SnmpMonitorService;
import com.inetec.ichange.mc.service.monitor.syslog.format.KoalTbsgLog;
import com.inetec.ichange.mc.service.monitor.syslog.format.KoalVpnLog;
import com.inetec.ichange.mc.service.monitor.syslog.format.LogFormatFactory;
import com.inetec.ichange.mc.service.monitor.utils.IpAddressUtils;

import java.net.SocketAddress;
import java.util.Date;


public  class SysLogRecevieServerEventHandler implements SyslogServerEventHandlerIF {
	private static final long serialVersionUID = 6036415838696050746L;
	private static final Logger logger = Logger
			.getLogger(SysLogRecevieServerEventHandler.class);
	private SyslogIF syslog;
	private String host;
	private int port;
	protected boolean isprivate = false;

	public SysLogRecevieServerEventHandler() {

	}

	public void init(String host, int port) {
		this.host = host;
		this.port = port;
		syslog = Syslog.getInstance("udp");
		syslog.getConfig().setHost(host);
		syslog.getConfig().setPort(port);
		syslog.getConfig().setCharSet("GBK");
	}

	public Object sessionOpened(SyslogServerIF syslogServerIF,
			SocketAddress socketAddress) {
		return null;

	}

	public void event(Object paramObject, SyslogServerIF paramSyslogServerIF, SocketAddress paramSocketAddress, SyslogServerEventIF paramSyslogServerEventIF) {
		String str1 = (paramSyslogServerEventIF.getDate() == null ? new Date()
				: paramSyslogServerEventIF.getDate()).toString();
		String str2 = SyslogUtility.getFacilityString(paramSyslogServerEventIF
				.getFacility());
		String str3 = SyslogUtility.getLevelString(paramSyslogServerEventIF
				.getLevel());
		String message = "";
		// if
		// (LogFormatFactory.checkFormat(paramSyslogServerEventIF.getMessage()))
		// {
		//try {
		String oldmessage=paramSyslogServerEventIF.getMessage();
			message = new String(paramSyslogServerEventIF.getRaw());
			if(message.lastIndexOf(oldmessage)>=0)
			message=message.substring(message.lastIndexOf(oldmessage));
			
		
		
		String host=IpAddressUtils.getHost(paramSocketAddress.toString());
		logger.info("recv syslog host:"+host);
		
		String deviceid=SnmpMonitorService.dataset
		.getDeviceIDByIP(host);
		logger.info("deviceid="
				+ deviceid
				+ " ip=" + host + " " + str1
				+ " " + str2 + " " + str3 + " " + message);
		if(deviceid==null){
            logger.warn( " ip=" + host + " " + str1
                    + " " + str2 + " " + str3 + " " + message +" is error log");
			return ;
		}
		if (str3.equalsIgnoreCase("warn")) {
			syslog
					.warn("deviceid="
							+ deviceid + " ip="
							+ host+ " "
							+ message);
			syslog.flush();
		}
		if (str3.equalsIgnoreCase("info")) {
			if(LogFormatFactory.checkFormat(message)){
				KoalVpnLog log=new KoalVpnLog();
				log.process(message, "info");
				IPlatManager.terminalinfServcie.terminalCache.updateOnlineStatus(log.getUserip(), log.getIdentity(),log.getIn_Flux(),log.getOut_Flux());

			}
			syslog
					.info("deviceid="
							+ deviceid+ " ip="
							+ host+ " "
							+ message);
			syslog.flush();
		}
		if (str3.equalsIgnoreCase("error")) {
			syslog
					.error("deviceid="
							+ deviceid + " ip="
							+host+ " "
							+ message);
			syslog.flush();
		}

		/*
		 * } else logger.info(" not process log:" +
		 * paramSyslogServerEventIF.getMessage());
		 */return;
		// EquipmentLog device = new EquipmentLog();

	}

	public void exception(Object o, SyslogServerIF syslogServerIF,
			SocketAddress socketAddress, Exception e) {
		// To change body of implemented methods use File | Settings | File
		// Templates.
		System.out.println("Session exception:" + e.toString());
	}

	public void sessionClosed(Object o, SyslogServerIF syslogServerIF,
			SocketAddress socketAddress) {
		// To change body of implemented methods use File | Settings | File
		// Templates.
		System.out.println("session Closed.");
	}


	public void sessionClosed(Object session, SyslogServerIF syslogServer,
			SocketAddress socketAddress, boolean timeout) {
		// TODO Auto-generated method stub
		
	}


	public void destroy(SyslogServerIF syslogServer) {
		// TODO Auto-generated method stub
		
	}


	public void initialize(SyslogServerIF syslogServer) {
		// TODO Auto-generated method stub
		
	}

	

	

	
}
