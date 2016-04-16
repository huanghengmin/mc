package com.inetec;

import com.inetec.ichange.mc.service.monitor.vpn.VpnServiceResponse;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 12-12-14
 * Time: ÏÂÎç5:47
 * To change this template use File | Settings | File Templates.
 */
public class aaa {
    private static final Logger logger = Logger.getLogger(aaa.class);

    public static void main(String[] args){
        Cut c = new Cut("172.16.9.2","publicUser0350");
        Thread c1 = new Thread(c);
        c1.start();
//
        Cut a = new Cut("172.16.9.2","publicUser0258");
        Thread a1 = new Thread(a);
        a1.start();
    }

    static class Cut extends Thread{
        private String ip;
        private String name;

        public Cut (String ip,String name){
            this.ip =ip;
            this.name = name;
        }

        public void run(){

            int n=1;
            while(true){
                logger.info("µÚ"+n+"´Î½ØÍ¼");
                String[][] params = new String[][] {
                        { "ip", ip },
                        { "name", name },
                        {"commond", "viewvpn"}
                };
                callService(params,"http://"+"192.168.1.233"+":"+8000+"/sslvpn/UserSnapshotOrLocationAction_Snapshot.action");
                n++;
                try {
                    Thread.sleep(1000*10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }



    public static String callService(String[][] params,String methodurl) {
        String data="";
        HttpClient client = new HttpClient();
        client.getHttpConnectionManager().getParams().setConnectionTimeout(
                5 * 1000);
        client.getHttpConnectionManager().getParams().setSoTimeout(5 * 1000);
        PostMethod post = new PostMethod(methodurl);

        post.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 5 * 1000);
        post.addRequestHeader("Content-Type",
                "application/x-www-form-urlencoded;charset=UTF-8");
        for (String[] param : params) {
            post.addParameter(param[0], param[1]);
        }
        VpnServiceResponse responseVpn = new VpnServiceResponse();

        int statusCode = 0;
        try {
            statusCode = client.executeMethod(post);
            responseVpn.setCode(statusCode);
            if (statusCode == 200) {
                data = post.getResponseBodyAsString();
                responseVpn.setData(data);
            }
        } catch (Exception e) {
            logger.info("callService"+ e);
        }
        return data;
    }

}
