package com.hzih.mc.entity;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 12-11-12
 * Time: 下午7:23
 * To change this template use File | Settings | File Templates.
 */
public class EquipmentInfo {
    private String ip ;
    private boolean snmp;
    private boolean syslog;
    private boolean ipPing;

    public EquipmentInfo(String ip){
        this.ip = ip;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public boolean isSnmp() {
        return snmp;
    }

    public void setSnmp(boolean snmp) {
        this.snmp = snmp;
    }

    public boolean isSyslog() {
        return syslog;
    }

    public void setSyslog(boolean syslog) {
        this.syslog = syslog;
    }

    public boolean isIpPing() {
        return ipPing;
    }

    public void setIpPing(boolean ipPing) {
        this.ipPing = ipPing;
    }
}
