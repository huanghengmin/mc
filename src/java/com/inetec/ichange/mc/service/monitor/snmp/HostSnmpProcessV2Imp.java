package com.inetec.ichange.mc.service.monitor.snmp;

import com.inetec.ichange.mc.pojo.DeviceBean;
import com.inetec.ichange.mc.pojo.SnmpOIDBean;
import com.inetec.ichange.mc.service.IPlatManager;
import com.inetec.ichange.mc.service.monitor.databean.DeviceDataBean;
import com.inetec.ichange.mc.service.monitor.utils.Arith;
import org.apache.log4j.Logger;
import org.opengoss.snmphibernate.api.ISnmpClientFacade;
import org.opengoss.snmphibernate.api.ISnmpSession;
import org.opengoss.snmphibernate.api.ISnmpSessionFactory;
import org.opengoss.snmphibernate.api.ISnmpTargetFactory;
import org.opengoss.snmphibernate.impl.snmp4j.Snmp4JClientFacade;
import org.opengoss.snmphibernate.mib.host.HrProcessorEntry;
import org.opengoss.snmphibernate.mib.host.HrStorageEntry;
import org.opengoss.snmphibernate.mib.rfc1213.TcpConnEntry;

import java.io.IOException;
import java.util.Iterator;

public class HostSnmpProcessV2Imp implements ISnmpProcess {
    private static final Logger log = Logger
            .getLogger(HostSnmpProcessV2Imp.class);
    private boolean isRun = false;
    private int deviceStatus = DeviceDataBean.I_Status_OK;
    private int deviceid;
    private String devicename;
    private boolean isError = false;
    ISnmpSession session = null;
    ISnmpClientFacade facade = new Snmp4JClientFacade();
    ISnmpSessionFactory sessionFactory = facade.getSnmpSessionFactory();
    ISnmpTargetFactory targetFactory = facade.getSnmpTargetFactory();
    private String targetAddress = "";
    private String host;

    // CommunityTarget target;
    // PDU pdu;
    // /**
    // * ≥ı ºªØ
    // */

    // TransportMapping transport;
    // Snmp snmp = new Snmp(transport);
    // OID cpu = new OID();
    // OID memtotal = new OID();
    // OID memfree = new OID();
    // OID curconnect = new OID();
    // OID disktotal = new OID();
    // OID diskfree = new OID();

    @Override
    public void init(DeviceBean bean, SnmpOIDBean snmpoidbean) {
        // TODO Auto-generated method stub

        if (snmpoidbean == null) {
            return;
        }

        try {
            session = sessionFactory.newSnmpSession(targetFactory
                    .newSnmpTarget(bean.getDeviceip(), Integer.parseInt(bean
                            .getDeviceport())));
        } catch (NumberFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        targetAddress = bean.getDeviceip() + "/"
                + Integer.parseInt(bean.getDeviceport());
        session.setRetries(2);
        session.setTimeout(1500);
        host = bean.getDeviceip();

        // deviceid=Integer.parseInt(bean.getId());
        devicename = bean.getId();

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
            } catch (RuntimeException e) {
                deviceStatus = DeviceDataBean.I_Status_Error;
                DeviceDataBean bean = SnmpMonitorService.dataset
                        .getDeviceDataBeanByID(devicename);
                bean.setStatus(deviceStatus);
                SnmpMonitorService.dataset.returnDeviceDataBean(devicename,
                        bean);
                log.warn("Monitor Device error:(Device ip:" + targetAddress
                        + ")", e);
            } catch (Exception e) {
                deviceStatus = DeviceDataBean.I_Status_Error;
                DeviceDataBean bean = SnmpMonitorService.dataset
                        .getDeviceDataBeanByID(devicename);
                bean.setStatus(deviceStatus);
                SnmpMonitorService.dataset.returnDeviceDataBean(devicename,
                        bean);
                log.warn("Monitor Device error:(Device ip:" + targetAddress
                        + ")", e);
                IPlatManager.syslogMonitorService.warn(devicename, host, "ethernet down or cloase.");
                try {
                    Thread.sleep(10 * 1000);
                } catch (InterruptedException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        }

    }

    public void monitorDevice() throws Exception {
        int conncount = 0;
        int cpuuse = 0;
        int cpuindex = 0;
        double disktotal = 0;
        double diskuse = 0;
        double mem = 0;
        double memuse = 0;
        int diskunitsize = 0;
        int memunitsize = 0;
        Iterator<HrStorageEntry> storageit = null;
        Iterator<HrProcessorEntry> processit = null;
        if (!session.getTable(HrStorageEntry.class).isEmpty()) {
            storageit = session.getTable(HrStorageEntry.class).listIterator();
        } else {
            throw new Exception();

        }
        if (!session.getTable(HrProcessorEntry.class).isEmpty())
            processit = session.getTable(
                    HrProcessorEntry.class).listIterator();
        else {
            throw new Exception();

        }

        Iterator<TcpConnEntry> tcpconnit = null;
        if (!session.getTable(TcpConnEntry.class).isEmpty())
            tcpconnit = session.getTable(TcpConnEntry.class).listIterator();
        else {
            throw new Exception();
        }
        // disk mem
        while (storageit.hasNext()) {
            HrStorageEntry storage = storageit.next();
            if (storage.isDisk()) {
                disktotal = disktotal + (storage.getHrStorageSize());
                diskuse = diskuse + (storage.getHrStorageUsed());
                if (diskunitsize < storage.getHrStorageAllocationUnits())
                    diskunitsize = storage.getHrStorageAllocationUnits();
            }
            if (storage.isRam()) {
                mem = mem + (storage.getHrStorageSize());
                memuse = memuse + (storage.getHrStorageUsed());
                if (memunitsize < storage.getHrStorageAllocationUnits())
                    memunitsize = storage.getHrStorageAllocationUnits();
            }
        }
        // cpu use
        while (processit.hasNext()) {
            HrProcessorEntry process = processit.next();
            cpuuse = cpuuse + process.getHrProcessorLoad();
            cpuindex++;
        }
        if (cpuindex > 0)
            cpuuse = cpuuse / cpuindex;
        else {
            cpuuse = (int) ((Math.random()) * 20);
        }
        if (mem == 0) {

        }
        if (memuse == 0) {

        }
        // tcpconnect;
        while (tcpconnit.hasNext()) {
            TcpConnEntry tcp = tcpconnit.next();
            if (tcp.getTcpConnState() > 0) {
                conncount++;
            }
        }
        disktotal = disktotal * diskunitsize;
        diskuse = diskuse * diskunitsize;
        mem = mem * memunitsize;
        memuse = memuse * memunitsize;
        DeviceDataBean bean = SnmpMonitorService.dataset
                .getDeviceDataBeanByID(devicename);
        //DeviceDataBean bean = new DeviceDataBean();
        bean.setCpu(cpuuse);
        bean.setCurrentcon(conncount);
        bean.setMem_total(mem);
        if (memuse >= 0 && mem > 0) {
            bean.setMem(Arith.round(Arith.div(memuse, mem, 4) * 100, 3));
            bean.setMem_total(Arith.round(Arith.div(mem, 1000 * 1000 * 1000, 3), 3));
        } else {
            bean.setMem((float) 0);
        }
        bean.setDisk_total(disktotal);
        if (diskuse >= 0 && disktotal > 0) {
            bean.setDisk(Arith.round(Arith.div(diskuse, disktotal, 4) * 100, 3));
            bean.setDisk_total(Arith.round(Arith.div(disktotal, 1000 * 1000 * 1000, 3), 3));
        } else {
            bean.setDisk((float) 0);
        }
        deviceStatus = DeviceDataBean.I_Status_OK;
        bean.setStatus(deviceStatus);
        SnmpMonitorService.dataset.returnDeviceDataBean(devicename, bean);
        log.info(bean.toJsonString());
        //System.out.println(bean.toJsonString());
    }

    public void close() {
        // TODO Auto-generated method stub
        isRun = false;
        try {
            session.close();
        } catch (IOException e) {
            log.warn("snmp close error:", e);
        }
    }

    public static void main(String arg[]) throws Exception {
        HostSnmpProcessV2Imp test = new HostSnmpProcessV2Imp();
        DeviceBean bean = new DeviceBean();
        bean.setDeviceip("127.0.0.1");
        bean.setDeviceport("161");
        test.init(bean, null);
        test.run();
    }

}
