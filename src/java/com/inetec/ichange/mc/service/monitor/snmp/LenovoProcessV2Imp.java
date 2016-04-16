package com.inetec.ichange.mc.service.monitor.snmp;

import com.inetec.ichange.mc.pojo.DeviceBean;
import com.inetec.ichange.mc.pojo.SnmpOIDBean;
import com.inetec.ichange.mc.service.IPlatManager;
import com.inetec.ichange.mc.service.monitor.databean.DeviceDataBean;
import com.inetec.ichange.mc.service.monitor.utils.Arith;
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
import java.util.Iterator;

public class LenovoProcessV2Imp implements ISnmpProcess {
    private static final Logger log = Logger
            .getLogger(LenovoProcessV2Imp.class);
    private boolean isRun = false;
    private int deviceStatus = DeviceDataBean.I_Status_OK;
    private int deviceid;
    private String devicename;
    private boolean isError = false;
    private String host;
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
        targetAddress = GenericAddress.parse("udp:" + bean.getDeviceip() + "/"
                + bean.getDeviceport());

        // deviceid=Integer.parseInt(bean.getId());
        host = bean.getDeviceip();
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
                IPlatManager.syslogMonitorService.warn(devicename, host, "ethernet down or cloase.");
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
        // creating PDU

        // sending request

        ResponseEvent event = snmp.send(pdu, target);
        DeviceDataBean bean = SnmpMonitorService.dataset
                .getDeviceDataBeanByID(devicename);
        if (event != null && event.getResponse() != null) {
            // event.getResponse());

            Iterator<VariableBinding> respIt = (Iterator<VariableBinding>) event.getResponse().getVariableBindings().iterator();

            while (respIt.hasNext()) {
                VariableBinding res = respIt.next();
                if (!StringNumberUtils.isNumeric(res.getVariable().toString())) {
                    log.warn("devicename:" + devicename + " "
                            + res.getOid().toString() + ":"
                            + res.getVariable().toString() + " is error.");
                    continue;
                }
                if (res.getOid().equals(cpu))
                    bean.setCpu(res.getVariable().toInt());

                if (res.getOid().equals(memtotal))
                    bean.setMem_total(res.getVariable().toLong());

                if (res.getOid().equals(memfree)) {
                    bean.setMem(res.getVariable().toLong());

                }

                if (res.getOid().equals(curconnect))
                    bean.setCurrentcon(res.getVariable().toInt());

                if (res.getOid().equals(disktotal))
                    bean.setDisk_total(res.getVariable().toLong());

                if (res.getOid().equals(diskfree)) {
                    bean.setDisk(res.getVariable().toLong());
                }

            }
            deviceStatus = DeviceDataBean.I_Status_OK;
            bean.setStatus(deviceStatus);
            if (bean.getCpu() > 0) {
                bean.setCpu(100 - bean.getCpu());
            }
            if (bean.getMem() > 0) {
                bean.setMem(100 - Arith.div(bean.getMem(), bean.getMem_total()) * 100);
            }
            if (bean.getMem_total() > 0) {
                bean.setMem_total(Arith.round(Arith.div(bean.getMem_total(), 1000 * 1000, 3),3));
            }
            if (bean.getDisk() > 0) {

                bean.setDisk(100 - Arith.round(Arith.div(bean.getDisk(), bean.getDisk_total(), 4) * 100,3));
            }
            if (bean.getDisk_total() > 0) {
                bean.setDisk_total(Arith.round(Arith.div(bean.getDisk_total(), (1000 * 1000), 3),3));
            }
            SnmpMonitorService.dataset.returnDeviceDataBean(devicename, bean);
            System.out.println(event.getResponse());
            log.info(event.getResponse());
            log.info(bean.toJsonString());
            System.out.println(bean.toJsonString());
        } else {
            System.out.println("Snmp()" + devicename
                    + " Device response is null.");
            log.warn("Snmp()" + devicename + " Device response is null.");
            deviceStatus = DeviceDataBean.I_Status_Error;
            bean.setStatus(deviceStatus);
            IPlatManager.syslogMonitorService.warn(devicename, host, "ethernet down or cloase.");


        }

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
