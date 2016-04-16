package com.inetec.ichange.mc.service.monitor.databean;

/**
 * ????
 * Created by IntelliJ IDEA.
 * User: bluesky
 * Date: 2010-9-5
 * Time: 10:12:49
 * To change this template use File | Settings | File Templates.
 */
public class BaseDataBean {
    /**
     * ??
     */
    public final static int I_Status_OK = 200;
    /**
     * ?��
     */
    public final static int I_Status_Alert = 501;
    /**
     * ??????
     */
    public final static int I_Status_Error = 503;

    public int status = I_Status_OK;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String toJsonString() {
        return null;
    }
}
