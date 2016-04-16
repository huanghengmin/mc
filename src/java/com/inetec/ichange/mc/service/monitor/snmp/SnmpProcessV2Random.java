package com.inetec.ichange.mc.service.monitor.snmp;

import com.inetec.ichange.mc.pojo.DeviceBean;
import com.inetec.ichange.mc.pojo.SnmpOIDBean;
import com.inetec.ichange.mc.service.monitor.databean.DeviceDataBean;
import org.apache.log4j.Logger;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.SimpleOIDTextFormat;

import java.io.IOException;
import java.text.ParseException;

/**
 * Created with IntelliJ IDEA.
 * User: bluesky
 * Date: 12-7-25
 * Time: ÏÂÎç4:19
 * To change this template use File | Settings | File Templates.
 */
public class SnmpProcessV2Random implements ISnmpProcess {

    private static final Logger log = Logger.getLogger(SnmpProcessV2Random.class);
    private boolean isRun = false;
    private int deviceStatus = DeviceDataBean.I_Status_OK;
    private int deviceid;
    private String devicename;
    private boolean isError = false;
    CommunityTarget target;
    PDU pdu;
    /**
     * ³õÊ¼»¯
     */
    Address targetAddress;
    TransportMapping transport;
    Snmp snmp = new Snmp(transport);
    OID cpu = new OID();
    OID memtotal = new OID();
    OID memfree = new OID();
    OID curconnect = new OID();
    OID disktotal = new OID();
    OID diskfree = new OID();

    @Override
    public void init(DeviceBean bean, SnmpOIDBean snmpoidbean) {
        // TODO Auto-generated method stub
        if (snmpoidbean == null) {
            return;
        }
        try {
            transport = new DefaultUdpTransportMapping();
            snmp = new Snmp(transport);
            // deviceid=Integer.parseInt(bean.getId());

            snmp.listen();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        targetAddress = GenericAddress.parse("udp:" + bean.getDeviceip() + "/"
                + bean.getDeviceport());

        // deviceid=Integer.parseInt(bean.getId());
        devicename = bean.getId();

        cpu = initOID(cpu, snmpoidbean.getCpuuse());
        memtotal = initOID(memtotal, snmpoidbean.getMemtotal());
        memfree = initOID(memfree, snmpoidbean.getMemuse());
        curconnect = initOID(curconnect, snmpoidbean.getCurconn());
        disktotal = initOID(disktotal, snmpoidbean.getDisktotal());
        diskfree = initOID(diskfree, snmpoidbean.getDiskuse());

        target = new CommunityTarget();
        target.setCommunity(new OctetString(bean.getDevicesnmppwd()));
        target.setAddress(targetAddress);
        target.setRetries(2);
        target.setTimeout(1500);
        target.setVersion(SnmpConstants.version2c);
        // creating PDU
        pdu = new PDU();
        // cpu
        if (cpu.isValid()) {
            pdu.add(new VariableBinding(cpu));
        }
        if (memtotal.isValid()) {
            // memtotal
            pdu.add(new VariableBinding(memtotal));
        }
        // memfree
        if (memfree.isValid())
            pdu.add(new VariableBinding(memfree));
        // curconnect
        if (curconnect.isValid())
            pdu.add(new VariableBinding(curconnect));
        // disktotal
        if (disktotal.isValid())
            pdu.add(new VariableBinding(disktotal));

        // disk free
        if (diskfree.isValid())
            pdu.add(new VariableBinding(diskfree));

        pdu.setType(PDU.GET);

    }

    @Override
    public boolean isRun() {
        // TODO Auto-generated method stub
        return isRun;
    }

    @Override
    public void run() {
        if (isError) {
            log.error("Device name is:" + devicename
                    + " not found snmpoid data row.");
        }
        isRun = true;
        while (isRun) {
            try {
                monitorDevice();
                Thread.sleep(2 * 1000);
            } catch (Exception e) {
                deviceStatus = DeviceDataBean.I_Status_Error;
                DeviceDataBean bean = SnmpMonitorService.dataset
                        .getDeviceDataBeanByID(devicename);
                bean.setStatus(deviceStatus);
                SnmpMonitorService.dataset.returnDeviceDataBean(devicename,
                        bean);
                log.warn("Monitor Device error:(Device ip:"
                        + targetAddress.toString() + ")", e);
                try {
                    Thread.sleep(I_SleepTime);
                } catch (InterruptedException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        }

    }

    public void monitorDevice() throws Exception {
        ResponseEvent event = snmp.send(pdu, target);
        DeviceDataBean bean = SnmpMonitorService.dataset
                .getDeviceDataBeanByID(devicename);
        deviceStatus = DeviceDataBean.I_Status_OK;
        bean.setCpu(Math.round((int) Math.random() * (100 - 0) + 0));
        bean.setMem_total((float) 1);
        bean.setMem(Math.round(Math.random() * (0.9 - 0.2) + 0.2));
        bean.setCurrentcon(Math.round((int) Math.random() * (600 - 10) + 0));
        bean.setStatus(deviceStatus);
        SnmpMonitorService.dataset.returnDeviceDataBean(devicename, bean);

        log.info(event.getResponse());
        log.info(bean.toJsonString());
        System.out.println(bean.toJsonString());


    }

    private OID initOID(OID oid, String soid) {
        if (soid.startsWith(".")) {
            soid = soid.substring(1);
        }
        if (soid.length() <= 2) {
            return oid;
        }
        try {
            oid.setValue(new SimpleOIDTextFormat().parse(soid));
        } catch (ParseException e) {
            log.warn("OID Format error.", e);
        }
        return oid;
    }

    public void close() {
        // TODO Auto-generated method stub
        isRun = false;
        try {
            snmp.close();
        } catch (IOException e) {
            log.warn("snmp close error:", e);
        }
    }
}
