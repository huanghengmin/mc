package com.inetec.ichange.mc.service.monitor.databean;

/**
 * ?��?????
 * Created by IntelliJ IDEA.
 * User: bluesky
 * Date: 2010-9-5
 * Time: 10:09:33
 * To change this template use File | Settings | File Templates.
 */
public class DeviceDataBean extends BaseDataBean {
    /**
     * ?��??
     */
    public String equ_name;
    /**
     * ?��ID
     */
    public int equ_id;
    /**
     * ?????????
     */
    public int maxcon;
    /**
     * ?????????
     */

    public int currentcon;
    /**
     * cpu???????????
     */
    public int cpu;
    /**
     * ??????????????
     */
    public double mem;
    /**
     * ???????
     */
    public double mem_total;
    /**
     * ??????????
     */
    public double disk_total;
    /**
     * ????????? ???????
     */
    public double disk;
    /**
     * vpn??????
     */
    public int vpn;

    public int getVpn() {
        return vpn;
    }

    public void setVpn(int vpn) {
        this.vpn = vpn;
    }

    public String getEqu_name() {
        return equ_name;
    }

    public void setEqu_name(String equ_name) {
        this.equ_name = equ_name;
    }

    public int getEqu_id() {
        return equ_id;
    }

    public void setEqu_id(int equ_id) {
        this.equ_id = equ_id;
    }

    public int getMaxcon() {
        return maxcon;
    }

    public void setMaxcon(int maxcon) {
        this.maxcon = maxcon;
    }

    public int getCurrentcon() {
        return currentcon;
    }

    public void setCurrentcon(int currentcon) {
        this.currentcon = currentcon;
    }

    public int getCpu() {
        return cpu;
    }

    public void setCpu(int cpu) {
        this.cpu = cpu;
    }

    public double getMem() {
        return mem;
    }

    public void setMem(double mem) {
        this.mem = mem;
    }

    public double getMem_total() {
        return mem_total;
    }

    public void setMem_total(double mem_total) {
        this.mem_total = mem_total;
    }

    public double getDisk_total() {
        return disk_total;
    }

    public void setDisk_total(double disk_total) {
        this.disk_total = disk_total;
    }

    public double getDisk() {
        return disk;
    }

    public void setDisk(double disk) {
        this.disk = disk;
    }

    public String toJsonString() {
        StringBuffer buff = new StringBuffer();
        buff.append("{'equ_name':");
        buff.append(equ_name);
        buff.append(",'maxcon':");
        buff.append(maxcon);
        buff.append(",'currentcon':");
        buff.append(currentcon);
        buff.append(",'cpu':");
        buff.append(cpu);
        buff.append(",'mem':");
        buff.append(mem);
        buff.append(",'mem_total':");
        buff.append(mem_total);

        buff.append(",'disk':");
        buff.append(disk);
        buff.append(",'disk_total':");
        buff.append(disk_total);
        buff.append(",'vpn':");
        buff.append(vpn);

        buff.append("}");
        return buff.toString();
    }

    public static void main(String args[]) throws Exception {
        DeviceDataBean bean = new DeviceDataBean();
        bean.setCpu(30);
        bean.setEqu_name("id22");
        bean.setEqu_id(2);
        bean.setCurrentcon(300);
        bean.setMaxcon(1000);
        bean.setDisk(30);
        bean.setDisk_total(300);
        bean.setMem(16);
        bean.setMem_total(8);
        bean.setVpn(0);
        System.out.println(bean.toJsonString());
    }
}
