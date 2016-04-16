package com.inetec.ichange.mc.service.http.client;

import com.inetec.ichange.main.api.DataAttributes;
import com.inetec.ichange.main.api.Status;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import java.io.File;


/**
 * To change this template use File | Settings | File Templates.
 */
public class TSRSHttpclient {
    Logger logger = Logger.getLogger(TSRSHttpclient.class);
    private HttpClient client;
    ;
    public static String Str_urlHttp = "http://";
    public static String url = ":7500/";
    private String host = "";
    private GetMethod getMethod;
    public static String Str_OSUrl = "/tsrs/asset/statisticOfOS.asp";
    public static String Str_NetUrl = "/tsrs/network/networkQueryFrame.asp";
    public static String ProcessUrl = "/tsrs/process/processList.asp";

    public DataAttributes viewvpn(String ip, String policeno) throws Exception {
        DataAttributes buff = new DataAttributes();
        getMethod.setURI(new URI(Str_urlHttp + host + url));
        getMethod.setRequestHeader("command", "viewvpn");
        getMethod.setRequestHeader("ip", ip);
        getMethod.setRequestHeader("policeno", policeno);
        getMethod.setRequestHeader("sizescale", "1");
        getMethod.setRequestHeader("compressscale", "80");
        //getMethod.(names);
        getMethod.setHttp11(true);
        int code = client.executeMethod(getMethod);
        if (code == 200 && getMethod.getResponseContentLength() > 0) {
            logger.info("TSRS View vpn is code:200");
            String imgname = getMethod.getResponseHeader("imgno").getValue();
            String imgtype = getMethod.getResponseHeader("imgtype").getValue();
            buff.setProperty("imgno", imgname);
            buff.setProperty("imgtype", imgtype);
            buff.setResultData(IOUtils.toByteArray(getMethod
                    .getResponseBodyAsStream()));
            buff.setStatus(Status.S_Success);

        } else {
            buff.setStatus(Status.S_Faild);
            logger.info("TSRS View vpn is code:" + code);
            System.out.print(new String(getMethod.getResponseBody(), "gbk"));
        }
        return buff;
    }

    /**
     * 操作系统查看
     *
     * @param ip
     * @param policeno
     * @return
     * @throws Exception
     */
    public DataAttributes osquery(String ip, String policeno) throws Exception {
        DataAttributes buff = new DataAttributes();
        getMethod.setURI(new URI(Str_urlHttp + host + Str_OSUrl));
        getMethod.setHttp11(true);
        int code = client.executeMethod(getMethod);
        if (code == 200 && getMethod.getResponseContentLength() > 0) {

            buff.setResultData(getMethod.getResponseBody());
            buff.setStatus(Status.S_Success);
        } else {
            buff.setStatus(Status.S_Faild);
            getMethod.getResponseBodyAsString();
        }
        return buff;
    }

    /**
     * 终端网络信息
     *
     * @param ip
     * @param policeno
     * @return
     * @throws Exception
     */
    public DataAttributes netquery(String ip, String policeno) throws Exception {
        DataAttributes buff = new DataAttributes();
        getMethod.setURI(new URI(Str_urlHttp + host + Str_NetUrl));
        getMethod.setRequestHeader("command", "osquery");
        getMethod.setRequestHeader("ip", ip);
        getMethod.setRequestHeader("policeno", policeno);
        getMethod.setRequestHeader("sizescale", "1");
        getMethod.setRequestHeader("compressscale", "80");

        getMethod.setHttp11(true);
        int code = client.executeMethod(getMethod);
        if (code == 200 && getMethod.getResponseContentLength() > 0) {
            String imgname = getMethod.getResponseHeader("imgno").getValue();
            String imgtype = getMethod.getResponseHeader("imgtype").getValue();
            buff.setProperty("imgno", imgname);
            buff.setProperty("imgtype", imgtype);

            buff.setResultData(getMethod.getResponseBody());
            buff.setStatus(Status.S_Success);
        } else {
            buff.setStatus(Status.S_Faild);
            getMethod.getResponseBodyAsString();
        }
        return buff;
    }

    /*
      * 终端进程信息
      */
    public DataAttributes processquery(String ip, String policeno)
            throws Exception {
        DataAttributes buff = new DataAttributes();

        getMethod.setHttp11(true);
        int code = client.executeMethod(getMethod);
        if (code == 200 && getMethod.getResponseContentLength() > 0) {

            buff.setResultData(getMethod.getResponseBody());
            buff.setStatus(Status.S_Success);
        } else {
            buff.setStatus(Status.S_Faild);
            System.out.print(getMethod.getResponseBodyAsString());
        }
        return buff;
    }

    public void init(String host) throws URIException {
        HttpClientParams clientparams = new HttpClientParams();
        clientparams.setConnectionManagerTimeout(5 * 60 * 1000);
        client = new HttpClient(clientparams);
        getMethod = new GetMethod();

        this.host = host;

    }

    public void close() {
        getMethod.releaseConnection();
    }

    public static void main(String arg[]) throws Exception {
        TSRSHttpclient tsrs = new TSRSHttpclient();
        tsrs.init("192.168.20.23");
        DataAttributes da = tsrs.viewvpn("9.9.0.15", "123456123456123456");
        if (da.isResultData()) {
            File jpeg = new File("C:\\tsrsviewvpn\\ttsrsmc"
                    + System.currentTimeMillis() + ".jpg");
            jpeg.createNewFile();
            FileUtils.writeByteArrayToFile(jpeg, IOUtils.toByteArray(da
                    .getResultData()));
        }

    }
}
