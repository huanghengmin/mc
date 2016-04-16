package com.inetec;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

/**
 * 调用服务接口封装
 *
 * @author collin.code@gmail.com
 *
 */
public class ServiceUtil {
    /**
     * Logger for this class
     */
//    private static final Logger logger = Logger.getLogger(ServiceUtil.class);

    public static String serviceUrl = "http://172.16.2.4:8080/sslvpn/McReturn.action";

    public static ServiceResponse callService(String[][] params) {
//        String serviceUrl = "http://127.0.0.1:8080/sslservice/Service";
        HttpClient client = new HttpClient();

        client.getHttpConnectionManager().getParams().setConnectionTimeout(
                5 * 1000);
        client.getHttpConnectionManager().getParams().setSoTimeout(5 * 1000);

        PostMethod post = new PostMethod(serviceUrl);
        post.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 5 * 1000);
        post.addRequestHeader("Content-Type",
                "application/x-www-form-urlencoded;charset=UTF-8");

        for (String[] param : params) {
            post.addParameter(param[0], param[1]);
        }

        ServiceResponse response = new ServiceResponse();

        int statusCode = 0;
        try {
            statusCode = client.executeMethod(post);
            //logger.info("statusCode:" + statusCode);
            System.out.println("statusCode=="+statusCode);
            response.setCode(statusCode);
            if (statusCode == 200) {
                String data = post.getResponseBodyAsString();
                System.out.println("data=="+data);
                //logger.info("data:" + data);
                response.setData(data);
            }
        } catch (Exception e) {
            System.err.println("访问接口失败"+ e);
        }

        return response;
    }

    public static void main(String[] arg){
        String[][] params = new String[][] {
                {"Command","stop"},
                { "ip", "192.168.2.173" },
//             { "port", "8443" },
//             { "CAAddress", "D:\\user4.pfx" } ,
//             {"password","123456"  }
        };
        ServiceResponse response = ServiceUtil.callService(params);

    }
}
