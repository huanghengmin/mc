package com.inetec.ichange.mc.service.monitor.snmp;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.*;

import org.apache.log4j.Logger;

import org.snmp4j.util.MultiThreadedMessageDispatcher;
import org.snmp4j.util.ThreadPool;
import org.snmp4j.util.WorkerPool;
import org.snmp4j.*;
import org.snmp4j.mp.MPv1;
import org.snmp4j.mp.MPv2c;
import org.snmp4j.smi.*;

import com.inetec.ichange.mc.service.monitor.databean.DeviceDataBean;

//???????????????
public class SnmpAgentService extends Thread {
	private boolean isRun = false;
	private static final Logger log = Logger.getLogger(SnmpAgentService.class);
	private String host;
	private int port;
	public static String deviceid = "1.3.6.1.4.1.3000.2.1";
	public static String cpuuse = "1.3.6.1.4.1.3000.2.2";
	public static String memtotal = "1.3.6.1.4.1.3000.2.3";
	public static String memfree = "1.3.6.1.4.1.3000.2.4";
	public static String curconnet = "1.3.6.1.4.1.3000.2.5";
	public static String disktotal = ".1.3.6.1.4.1.3000.2.9";
	public static String diskfree = ".1.3.6.1.4.1.3000.2.10";
	

	public  class Handler implements org.snmp4j.CommandResponder {

		protected java.lang.String mAddress = null;
		protected int mPort = 0;
		protected java.lang.String mMyCommunityName = null;
		protected org.snmp4j.TransportMapping mServerSocket = null;
		protected org.snmp4j.TransportMapping mTcpServerSocket=null;
		
		
		protected org.snmp4j.Snmp mSNMP = null;
		protected org.snmp4j.Snmp mTcpSnmp=null;
		private MultiThreadedMessageDispatcher dispatcher;     
	    private WorkerPool threadPool;

		public Handler() {
		}

		public void configure(String host, int port) {
			mAddress = host;
			mPort = port;
			mMyCommunityName = "public";
		}

		public void start() {
            log.info("SnmpAgentService start begin 1");
			try {
				mServerSocket = new org.snmp4j.transport.DefaultUdpTransportMapping(
						new org.snmp4j.smi.UdpAddress(
								mPort));
				mTcpServerSocket=  new org.snmp4j.transport.DefaultTcpTransportMapping(
						new org.snmp4j.smi.TcpAddress(
								mPort));
                log.info("SnmpAgentService start begin2");
				/*threadPool = ThreadPool.create("Trap",20);
		        dispatcher = new MultiThreadedMessageDispatcher(threadPool,   
		                new MessageDispatcherImpl()); */
				mTcpSnmp=new org.snmp4j.Snmp(mTcpServerSocket);
				mSNMP = new org.snmp4j.Snmp(mServerSocket);
				mSNMP.getMessageDispatcher().addMessageProcessingModel(new MPv2c());
				mTcpSnmp.getMessageDispatcher().addMessageProcessingModel(new MPv2c());
				mSNMP.addCommandResponder(this);
				mTcpSnmp.addCommandResponder(this);
                log.info("SnmpAgentService start udp listen");
				mServerSocket.listen();
                log.info("SnmpAgentService start tcp listen");
				mTcpServerSocket.listen();
				mTcpSnmp.listen();
				mSNMP.listen();
			} catch (java.net.UnknownHostException vException) {
				System.out.println(vException);
				log.warn("SnmpAgentService start error:",vException);
			} catch (java.io.IOException vException) {
				System.out.println(vException);
				log.warn("SnmpAgentService start error:",vException);
			}
			log.info("SnmpAgentService start okay");
		}

		public    void processPdu(
				org.snmp4j.CommandResponderEvent aEvent) {
			
			java.lang.String vCommunityName = new java.lang.String(aEvent
					.getSecurityName());
			System.out.println("Community name " + vCommunityName);
			log.info("Community name " + vCommunityName);
			org.snmp4j.PDU vPDU = aEvent.getPDU();
			if (vPDU == null) {
				System.out.println("Null pdu");
				log.info("Null pdu");
			} else {
				System.out.println("(rcv) " + vPDU.toString());
				log.info("(rcv) " + vPDU.toString());
				switch (vPDU.getType()) {
				case org.snmp4j.PDU.GET:
				case org.snmp4j.PDU.GETNEXT:
					break;
				}
				org.snmp4j.mp.StatusInformation statusInformation = new org.snmp4j.mp.StatusInformation();
				org.snmp4j.mp.StateReference ref = aEvent.getStateReference();
				try {
					System.out.println("Sending response");
					vPDU.setType(PDU.RESPONSE);
					/*vPDU.set(0, new VariableBinding(vPDU.get(0).getOid(),
							new OctetString("Test")));*/
					 String id=vPDU.get(0).getVariable().toString();
	                   
	                    vPDU.set(0, new VariableBinding(vPDU.get(0).getOid(),
	                            new OctetString(id)));
	                   DeviceDataBean bean=SnmpMonitorService.dataset.getDeviceDataBeanByID(id);
	                    if(bean!=null&&bean.status==DeviceDataBean.I_Status_OK){
	                    	vPDU.add(new VariableBinding(new OID(new int[]{1, 3, 6, 1, 4, 1, 3000, 2, 2}),new OctetString(String.valueOf(bean.getCpu()))));
	                        //memtotal
	                    	vPDU.add(new VariableBinding(new OID(new int[]{1, 3, 6, 1, 4, 1, 3000, 2, 3}),new OctetString(String.valueOf((float)(Math.round(bean.getMem_total()*100))/100))));
	                        //memfree
	                    	vPDU.add(new VariableBinding(new OID(new int[]{1, 3, 6, 1, 4, 1, 3000, 2, 4}),new OctetString(String.valueOf((float)(Math.round(bean.getMem()*100))/100))));
	                        //curconnect
	                    	vPDU.add(new VariableBinding(new OID(new int[]{1, 3, 6, 1, 4, 1, 3000, 2, 5}),new OctetString(String.valueOf(bean.getCurrentcon()))));
	                         //disk total
	                    	vPDU.add(new VariableBinding(new OID(new int[]{1, 3, 6, 1, 4, 1, 3000, 2, 9}),new OctetString(String.valueOf((float)(Math.round(bean.getDisk_total()*100))/100))));

	                         //disk free
	                    	vPDU.add(new VariableBinding(new OID(new int[]{1, 3, 6, 1, 4, 1, 3000, 2, 10}),new OctetString(String.valueOf((float)(Math.round(bean.getDisk()*100))/100))));
	                    }else{
	                    	log.info("Device Name:"+id+"send response Device is Null.");
	                    	vPDU.add(new VariableBinding(new OID(new int[]{1, 3, 6, 1, 4, 1, 3000, 2, 2}),new OctetString("0")));
	                        //memtotal
	                    	vPDU.add(new VariableBinding(new OID(new int[]{1, 3, 6, 1, 4, 1, 3000, 2, 3}),new OctetString("0")));
	                        //memfree
	                    	vPDU.add(new VariableBinding(new OID(new int[]{1, 3, 6, 1, 4, 1, 3000, 2, 4}),new OctetString("0")));
	                        //curconnect
	                    	vPDU.add(new VariableBinding(new OID(new int[]{1, 3, 6, 1, 4, 1, 3000, 2, 5}),new OctetString("0")));
	                         //disk total
	                    	vPDU.add(new VariableBinding(new OID(new int[]{1, 3, 6, 1, 4, 1, 3000, 2, 9}),new OctetString("0")));

	                         //disk free
	                    	vPDU.add(new VariableBinding(new OID(new int[]{1, 3, 6, 1, 4, 1, 3000, 2, 10}),new OctetString("0")));
	                    }
					int result=aEvent.getMessageDispatcher().returnResponsePdu(
							aEvent.getMessageProcessingModel(),

							aEvent.getSecurityModel(),
							aEvent.getSecurityName(),

							aEvent.getSecurityLevel(), vPDU,
							aEvent.getMaxSizeResponsePDU(), ref,

							statusInformation);
					aEvent.setProcessed(true);
					
				} catch (org.snmp4j.MessageException e) {
					System.out.println(e);
					log.error("SnmpAgentService process request or response error.",e);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println(e);
					log	.error("SnmpAgentService process request or response error.",e);
				}
			}
		}
	}

	public boolean isRun() {
		return isRun;
	}

	public void init(String host, int port, String charset) {
		this.host = host;
		this.port = port;

	}

	public void run() {
		isRun = true;
		Handler h = new Handler();
		h.configure(host, port);
		h.start();
		while (isRun) {
			try {
				Thread.sleep(2*1000);
			} catch (InterruptedException e) {
				// okay
			}
		}
	}

	/**
     *
     */
	public void close() {
		isRun = false;
	}

	public static void main(String argv[]) {
	
		SnmpAgentService h = new SnmpAgentService();
		h.init("0.0.0.0", 1161,"utf-8");
		h.start();
		while (true) {
			synchronized (SnmpAgentService.class) {
				try {
					SnmpAgentService.class.wait();
				} catch (Exception e) {
				}
			}
		}
	}
}
