package com.inetec.ichange.mc.service.http.client;

import org.apache.commons.httpclient.methods.GetMethod;

/**
 * Created with IntelliJ IDEA.
 * User: bluesky
 * Date: 12-8-8
 * Time: 下午5:25
 * To change this template use File | Settings | File Templates.
 */
public class UTF8GetMetod extends GetMethod {
    public UTF8GetMetod(String uri) {
        super(uri);
    }

    public String getRequestCharSet() {
        return "GBK";
    }
}
