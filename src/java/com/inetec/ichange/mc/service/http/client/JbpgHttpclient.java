package com.inetec.ichange.mc.service.http.client;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.log4j.Logger;

/**
 * VPNµ÷ÓÃ Created by IntelliJ IDEA. User: bluesky Date: 11-8-8 Time: ÏÂÎç3:02 To
 * change this template use File | Settings | File Templates.
 */
public class JbpgHttpclient {
    public static Logger logger = Logger.getLogger(JbpgHttpclient.class);
    protected HttpClient client;
    protected String host;
    public static String Str_urlHttp = "http://";
    public static String noblockurl = "/DoTermStatus";
    public static String blockurl = "/DoTermStatus";
    public static String pnourl = "&policeno=";
    public static String allUrl = "/DoTermStatusAll";

    protected GetMethod getMethod;

    /*
      * public String vpnOnline(String beginno, String endno, int pagesize)
      * throws Exception { String result = null;
      * getMethod.setRequestHeader("command", "onlinevpn");
      * getMethod.setRequestHeader("beginno", beginno);
      * getMethod.setRequestHeader("endno", endno);
      * getMethod.setRequestHeader("pagesize", String.valueOf(pagesize));
      * getMethod.setHttp11(true); int code = client.executeMethod(getMethod); if
      * (code == 200 && getMethod.getResponseContentLength() > 0) {
      *
      * result = new String(getMethod.getResponseBody(), "gbk");
      * logger.info("vpnOnline request command:onlinevpn,beginno:" + beginno +
      * ",endno:" + endno + ",pagesize:" + pagesize + "response body:" + result);
      * } else { result = new String(getMethod.getResponseBody(), "gbk");
      * logger.warn("vpnOnline request command:onlinevpn,beginno:" + beginno +
      * ",endno:" + endno + ",pagesize:" + pagesize + "response body:" + result);
      * } return result; }
      */

    /*
      * block ÖÕ¶Ë×è¶Ï
      */
    public boolean vpnblock(String pno, String cn, String ip) throws Exception {
        boolean result = false;
        getMethod.setURI(new URI(Str_urlHttp + host + blockurl));
        NameValuePair[] names = new NameValuePair[4];
        names[0] = new NameValuePair("opername", "yw");
        names[1] = new NameValuePair("command", "block");
        names[2] = new NameValuePair("cn", cn);
        names[3] = new NameValuePair("policeno", cn + " " + pno);
        getMethod.addRequestHeader("cn", cn);
        getMethod.setQueryString(names);
        int code = client.executeMethod(getMethod);
        if (code == 200) {

            result = true;

            logger.info("vpnOnline request command:block,cn:" + cn + ",ip:"
                    + ip + "response code:" + code + " response body:"
                    + getMethod.getResponseBodyAsString());

        } else {
            logger.info("vpnOnline request command:block,cn:" + cn + ",ip:"
                    + ip + "response body code:" + code + " response body:"
                    + getMethod.getResponseBodyAsString());
        }
        return result;
    }

    /*
      * ÖÕ¶Ë×è¶Ï»Ö¸´
      */
    public boolean vpnnoblock(String pno, String cn, String ip) throws Exception {
        boolean result = false;

        getMethod.setURI(new URI(Str_urlHttp + host + noblockurl));
        // getMethod.setRequestHeader("policeno", cn + " " + pno);
        NameValuePair[] names = new NameValuePair[4];
        names[0] = new NameValuePair("opername", "yw");
        names[1] = new NameValuePair("command", "noblock");
        names[2] = new NameValuePair("cn", cn);
        names[3] = new NameValuePair("policeno", cn + " " + pno);
        getMethod.addRequestHeader("cn", cn);
        getMethod.setQueryString(names);
        getMethod.setHttp11(true);
        int code = client.executeMethod(getMethod);
        if (code == 200) {

            result = true;

            logger.info("vpnOnline request command:noblock,cn:" + cn + ",ip:"
                    + ip + "response body:"
                    + getMethod.getResponseBodyAsString());

        } else {
            logger.info("vpnOnline request command:noblock,cn:" + cn + ",ip:"
                    + ip + "response body:"
                    + getMethod.getResponseBodyAsString());
        }
        return result;
    }


    /**
     * @param beginno
     * @param endno
     * @param pagesize
     * @return
     * @throws Exception
     */
    public String vpnAll(String beginno, String endno, int pagesize)
            throws Exception {
        String result = null;
        getMethod = new GetMethod(Str_urlHttp + host + allUrl);
        getMethod.addRequestHeader("command", "allvpn");
        getMethod.addRequestHeader("beginno", beginno);
        getMethod.addRequestHeader("endno", endno);
        getMethod.addRequestHeader("pagesize", String.valueOf(pagesize));
        getMethod.setHttp11(true);
        int code = client.executeMethod(getMethod);
        if (code == 200 && getMethod.getResponseContentLength() > 0) {

            result = getMethod.getResponseBodyAsString();
            logger.info("vpnOnline request command:allvpn,beginno:" + beginno
                    + ",endno:" + endno + ",pagesize:" + pagesize
                    + "response body:" + result);
        } else {
            result = new String(getMethod.getResponseBody(), "gbk");
            logger.warn("vpnOnline request command:allvpn,beginno:" + beginno
                    + ",endno:" + endno + ",pagesize:" + pagesize
                    + "response body:" + result);
        }
        return result;

    }

    /*
      * public String vpnNew(String beginno, String endno, int pagesize) throws
      * Exception { String result = null; getMethod.setRequestHeader("command",
      * "newvpn"); getMethod.setRequestHeader("beginno", beginno);
      * getMethod.setRequestHeader("endno", endno);
      * getMethod.setRequestHeader("pagesize", String.valueOf(pagesize));
      * getMethod.setHttp11(true); int code = client.executeMethod(getMethod); if
      * (code == 200 && getMethod.getResponseContentLength() > 0) {
      *
      * result = new String(getMethod.getResponseBody(), "gbk");
      * logger.info("vpnOnline request command:onlinevpn,beginno:" + beginno +
      * ",endno:" + endno + ",pagesize:" + pagesize + "response body:" + result);
      * } else { result = new String(getMethod.getResponseBody(), "gbk");
      * logger.warn("vpnOnline request command:onlinevpn,beginno:" + beginno +
      * ",endno:" + endno + ",pagesize:" + pagesize + "response body:" + result);
      * } return result;
      *
      * }
      */

    public void init(String host) {
        HttpClientParams clientparams = new HttpClientParams();
        clientparams.setConnectionManagerTimeout(5 * 60 * 1000);
        //clientparams.setContentCharset("GBK");
        //clientparams.setHttpElementCharset("GBK");
        client = new HttpClient(clientparams);
        getMethod = new GetMethod();
        this.host = host;

    }

    public void close() {
        getMethod.releaseConnection();
    }

    public static void main(String arg[]) throws Exception {
        JbpgHttpclient vpn = new JbpgHttpclient();
        vpn.init("192.168.20.22");
        System.out.println(vpn.vpnAll("0", "0", 500));
        // //vpn.vpnOnline("1", "40", 40);
        if (vpn.vpnblock("111111111111111111", "Ð¤", "9.9.0.15")) {
            logger.info("vpn block result: ³É¹¦");
        }

        /*if (!vpn.vpnnoblock("123456789123456789", "sxl 123456789123456789", "171.168.1.1")) {
          logger.info("vpn noblock result: ³É¹¦");
      }  */
        // vpn.vpnNew("00001", "00040", 40);
    }
}
