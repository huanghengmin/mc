package com.inetec.ichange.mc.service.monitor.vpn;

/**
 * ������Ӧ
 *
 * @author collin.code@gmail.com
 *
 */
public class VpnServiceResponse {
    /**
     * ��Ӧ���룺20 0 400 500
     */
    int code;

    /**
     * json��Ӧ����
     */
    String data;

    public VpnServiceResponse(int code, String data) {
        super();
        this.code = code;
        this.data = data;
    }

    public VpnServiceResponse() {

    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
