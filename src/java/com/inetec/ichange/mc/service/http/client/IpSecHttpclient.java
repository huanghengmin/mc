package com.inetec.ichange.mc.service.http.client;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;

import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.log4j.Logger;


/**
 * VPN���� Created by IntelliJ IDEA. User: bluesky Date: 11-8-8 Time: ����3:02 To
 * change this template use File | Settings | File Templates.
 */
public class IpSecHttpclient extends JbpgHttpclient {
    public static Logger logger = Logger.getLogger(IpSecHttpclient.class);

    public static String Str_urlHttp = "http://";
    public static String noblockurl = "/DoTermStatus";
    public static String blockurl = "/DoTermStatus?opername=yw&command=block&cn=";
    public static String pnourl = "&policeno=";
    public static String allUrl = "/DoTermStatusAll";


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
      * block �ն����
      */
    public boolean vpnblock(String pno, String cn, String ip) throws Exception {
        boolean result = false;

        getMethod = new UTF8GetMetod(Str_urlHttp + host + noblockurl);
        //getMethod = new GetMethod();
        // getMethod.setURI(new URI(Str_urlHttp + host + noblockurl + cn + pnourl + pno));
        NameValuePair[] names = new NameValuePair[4];
        names[0] = new NameValuePair("opername", "yw");
        names[1] = new NameValuePair("command", "block");
        names[2] = new NameValuePair("cn", cn);
        names[3] = new NameValuePair("policeno", pno);
        getMethod.addRequestHeader("cn", cn);
        //getMethod.setQueryString();
        getMethod.setQueryString(names);
        //logger.info("URL:");
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
      * �ն���ϻָ�
      */
    public boolean vpnnoblock(String pno, String cn, String ip) throws Exception {
        boolean result = false;

        getMethod = new UTF8GetMetod(Str_urlHttp + host + blockurl);
        // getMethod.setRequestHeader("policeno", cn + " " + pno);
        NameValuePair[] names = new NameValuePair[4];
        names[0] = new NameValuePair("opername", "yw");
        names[1] = new NameValuePair("command", "noblock");
        names[2] = new NameValuePair("cn", cn);
        names[3] = new NameValuePair("policeno", pno);
        getMethod.addRequestHeader("cn", cn);
        getMethod.setQueryString(names);
        logger.info("URL:" + getMethod.getURI().getURI());
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
        clientparams.setContentCharset("GBK");
        clientparams.setUriCharset("UTF-8");
        clientparams.setCredentialCharset("GBK");
        clientparams.setHttpElementCharset("GBK");
        client = new HttpClient(clientparams);

        this.host = host;

    }

    public void close() {
        getMethod.releaseConnection();
    }

    public static void main(String arg[]) throws Exception {
        IpSecHttpclient vpn = new IpSecHttpclient();
        vpn.init("192.168.20.22");

        //System.out.println("Ф utf-8:" + vpn.transfer("Ф", "utf-8"));
        System.out.println(vpn.vpnAll("0", "500", 500));
        // //vpn.vpnOnline("1", "40", 40);
        /* if (vpn.vpnblock("111111111111111111", "Ф", "9.9.0.15")) {
            logger.info("vpn block result: �ɹ�");
        }*/
        if (vpn.vpnnoblock("111111111111111111", "Ф", "9.9.0.15")) {
            logger.info("vpn noblock result: �ɹ�");
        }
        vpn.close();
        // vpn.vpnNew("00001", "00040", 40);
    }


}
