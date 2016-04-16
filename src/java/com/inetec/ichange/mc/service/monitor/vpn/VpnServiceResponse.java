package com.inetec.ichange.mc.service.monitor.vpn;

/**
 * 服务响应
 *
 * @author collin.code@gmail.com
 *
 */
public class VpnServiceResponse {
    /**
     * 响应代码：20 0 400 500
     */
    int code;

    /**
     * json响应数据
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
