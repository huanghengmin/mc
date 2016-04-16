package com.inetec.ichange.mc.service.monitor.snmp;

import com.inetec.ichange.mc.pojo.DeviceBean;
import com.inetec.ichange.mc.pojo.SnmpOIDBean;
import com.inetec.ichange.mc.service.monitor.databean.DeviceDataBean;
import com.inetec.ichange.mc.service.monitor.utils.StringNumberUtils;
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

public class SnmpProcessV3Imp implements ISnmpProcess {
    private static final Logger log = Logger.getLogger(SnmpProcessV2Imp.class);
    private boolean isRun = false;
    private int deviceStatus = DeviceDataBean.I_Status_OK;
    private int deviceid;
    private String devicename;
    private boolean isError = false;
    CommunityTarget target;


    PDU pdu;
    /**
     * ≥ı ºªØ
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
        targetAddress = GenericAddress.parse("udp:" + bean.getDeviceip()
                + "/161");

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
        target.setVersion(SnmpConstants.version3);
        // creating PDU
        pdu = new PDU();
        // cpu
        pdu.add(new VariableBinding(cpu));
        // memtotal
        pdu.add(new VariableBinding(memtotal));
        // memfree
        pdu.add(new VariableBinding(memfree));
        // curconnect
        pdu.add(new VariableBinding(curconnect));
        // disktotal
        pdu.add(new VariableBinding(disktotal));

        // disk free
        pdu.add(new VariableBinding(diskfree));

        pdu.setType(PDU.GETNEXT);

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
                Thread.sleep(I_SleepTime);
            } catch (Exception e) {
                deviceStatus = DeviceDataBean.I_Status_Error;
                DeviceDataBean bean = SnmpMonitorService.dataset
                        .getDeviceDataBeanByID(devicename);

                bean.setStatus(deviceStatus);
                SnmpMonitorService.dataset.returnDeviceDataBean(devicename,
                        bean);

                log.warn("Monitor Device error:(Device ip:"
                        + targetAddress.toString() + ")", e);
            }
        }

    }

    public void monitorDevice() throws Exception {
        // creating PDU

        // sending request

        ResponseEvent event = snmp.send(pdu, target);
        if (event != null && event.getResponse() != null) {
            // event.getResponse());
            DeviceDataBean bean = SnmpMonitorService.dataset
                    .getDeviceDataBeanByID(devicename);
            if (StringNumberUtils.isNumeric(event.getResponse().get(0)
                    .getVariable().toString()))
                bean.setCpu(Integer.parseInt(event.getResponse().get(0)
                        .getVariable().toString()));
            else
                log.warn(event.getResponse().get(0).getOid().toString() + ":"
                        + event.getResponse().get(0).getVariable().toString()
                        + " is error.");
            if (StringNumberUtils.isNumeric(event.getResponse().get(1)
                    .getVariable().toString()))
                bean.setMem_total(Integer.parseInt(event.getResponse().get(1)
                        .getVariable().toString()));
            else
                log.warn(event.getResponse().get(1).getOid().toString() + ":"
                        + event.getResponse().get(1).getVariable().toString()
                        + " is error.");
            if (StringNumberUtils.isNumeric(event.getResponse().get(2)
                    .getVariable().toString()))
                bean.setMem(Integer.parseInt(event.getResponse().get(2)
                        .getVariable().toString()));
            else
                log.warn(event.getResponse().get(2).getOid().toString() + ":"
                        + event.getResponse().get(2).getVariable().toString()
                        + " is error.");
            if (StringNumberUtils.isNumeric(event.getResponse().get(3)
                    .getVariable().toString()))
                bean.setCurrentcon(Integer.parseInt(event.getResponse().get(3)
                        .getVariable().toString()));
            else
                log.warn(event.getResponse().get(3).getOid().toString() + ":"
                        + event.getResponse().get(3).getVariable().toString()
                        + " is error.");
            if (StringNumberUtils.isNumeric(event.getResponse().get(4)
                    .getVariable().toString()))
                bean.setDisk_total(Integer.parseInt(event.getResponse().get(4)
                        .getVariable().toString()));
            else
                log.warn(event.getResponse().get(4).getOid().toString() + ":"
                        + event.getResponse().get(4).getVariable().toString()
                        + " is error.");
            if (StringNumberUtils.isNumeric(event.getResponse().get(5)
                    .getVariable().toString()))
                bean.setDisk(Integer.parseInt(event.getResponse().get(5)
                        .getVariable().toString()));
            else
                log.warn(event.getResponse().get(5).getOid().toString() + ":"
                        + event.getResponse().get(5).getVariable().toString()
                        + " is error.");

            bean.setStatus(deviceStatus);
            SnmpMonitorService.dataset.returnDeviceDataBean(devicename, bean);
            System.out.println(event.getResponse());
            log.info(event.getResponse());
        } else {
            System.out.println("Snmp()"+devicename+" Device response is null.");
            log.warn("Snmp()"+devicename+" Device response is null.");

        }
    }

    private OID initOID(OID oid, String soid) {
        if (soid.startsWith(".")) {
            soid = soid.substring(1);
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


