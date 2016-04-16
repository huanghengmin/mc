package com.inetec.ichange.mc.service.http.client;

import java.io.File;
import java.io.FileInputStream;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.FileRequestEntity;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

/**
 * 调用 cms上传 Created by IntelliJ IDEA. User: bluesky Date: 11-8-8 Time: 下午3:02 To
 * change this template use File | Settings | File Templates.
 */
public class CmsHttpClient {
    Logger logger = Logger.getLogger(CmsHttpClient.class);
    private HttpClient client;
    private String host;
    public static String Str_urlHttp = "http://";
    public static String url = "/monitorservice/monitorService";

    private PostMethod postMethod;

    public boolean deviceConfig(String filefullname, String filename)
            throws Exception {
        boolean result = false;
        postMethod.setURI(new URI(Str_urlHttp + host + url));
        //postMethod.addParameter(paramName, paramValue)
        postMethod.addRequestHeader("SERVICEREQUESTTYPE", "SERVICECONTROLPOST");
        postMethod.addRequestHeader("Command", "deviceconfig");
        postMethod.addRequestHeader("filename", filename);
        //postMethod.setHttp11(true);
        File file = new File(filefullname);
        if (file.exists()) {
            logger.info(" request command :deviceconfig,file:"
                    + "set response body.");
            RequestEntity request = new ByteArrayRequestEntity(FileUtils.readFileToByteArray(file));
            postMethod.setRequestEntity(request);


        }
        int code = client.executeMethod(postMethod);
        if (code == 200 && postMethod.getResponseContentLength() > 0) {
            String resulta = postMethod.getResponseBodyAsString();
            logger.info(" mc request command:deviceconfig,file:"
                    + "response body:" + resulta);

            result = true;
        } else {

            logger.info("mc request command:deviceconfig,file:"
                    + filename + "response code:" + code);

        }
        return result;
    }

    public void init(String host) {
        client = new HttpClient();
        postMethod = new PostMethod();
        this.host = host;

    }

    public void close() {
        postMethod.releaseConnection();
    }

    public static void main(String arg[]) throws Exception {
        CmsHttpClient vpn = new CmsHttpClient();
        vpn.init("192.168.20.22");
        // vpn.vpnOnline("1", "40", 40);
        // vpn.vpnAll("0", "1000", 1000);
        // vpn.vpnNew("00001", "00040", 40);
    }
}
