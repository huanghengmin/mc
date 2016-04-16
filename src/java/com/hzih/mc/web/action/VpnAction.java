package com.hzih.mc.web.action;

import com.inetec.ichange.mc.service.monitor.vpn.VpnServiceResponse;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 12-11-15
 * Time: ????10:44
 * To change this template use File | Settings | File Templates.
 */
public class VpnAction  {

    private static final Logger logger = Logger.getLogger(VpnAction.class);
    private String vpnip ="";
    private String vpnport="";
    private static  Properties prop= new Properties();

    public VpnAction() throws IOException {
        prop.load(VpnAction.class.getResourceAsStream("/monitor.properties"));
        vpnip = prop.getProperty("vpnip");
        vpnport = prop.getProperty("vpnport");
    }


    public void selectInfo(){
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String result =	actionBase.actionBegin(request);
        String json ="";

        try {
            String[][] a = {};
            json =callService(a,"http://"+vpnip+":"+vpnport+"/sslvpn/McReturn.action");
            actionBase.actionEnd(response,json,result);
        } catch (Exception e) {
            logger.info("vpn���Ҵ���" + e);
        }
    }

    public void block(){
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String result =	actionBase.actionBegin(request);
        String json = "";

        try {
            String ip = ServletRequestUtils.getStringParameter(request,"ip");
            String name = ServletRequestUtils.getStringParameter(request,"name");
            String[][] params = new String[][] {
                    { "ip", ip },
                    { "name", name }
            };

            json = callService(params,"http://"+vpnip+":"+vpnport+"/sslvpn/UserRecoverOrBlockAction_Block.action");

            actionBase.actionEnd(response,json,result);

        } catch (ServletRequestBindingException e) {
            logger.info(e);
        } catch (HttpException e) {
            logger.info(e);
        } catch (IOException e) {
            logger.info(e);
        }
    }

    public void recover(){
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String result =	actionBase.actionBegin(request);
        String json = "";
        try {
            String ip = ServletRequestUtils.getStringParameter(request,"ip");
            String name = ServletRequestUtils.getStringParameter(request,"name");
            String[][] params = new String[][] {
                    { "ip", ip },
                    { "name", name }
            };

            json = callService(params,"http://"+vpnip+":"+vpnport+"/sslvpn/UserRecoverOrBlockAction_Recover.action");

            actionBase.actionEnd(response,json,result);

        } catch (ServletRequestBindingException e) {
            logger.info(e);
        } catch (HttpException e) {
            logger.info(e);
        } catch (IOException e) {
            logger.info(e);
        }
    }


    public void cutScreen(){
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String result =	actionBase.actionBegin(request);
        String json = "";

        try {
            String ip = ServletRequestUtils.getStringParameter(request,"ip");
            String name = ServletRequestUtils.getStringParameter(request,"name");
            String commond = "viewvpn";
            String[][] params = new String[][] {
                    { "ip", ip },
                    { "name", name },
                    {"commond",commond}
            };

            json = callService(params,"http://"+vpnip+":"+vpnport+"/sslvpn/UserSnapshotOrLocationAction_Snapshot.action");

            actionBase.actionEnd(response,json,result);

        } catch (ServletRequestBindingException e) {
            logger.info(e);
        } catch (HttpException e) {
            logger.info(e);
        } catch (IOException e) {
            logger.info(e);
        }
    }


    public void uploadLocation(){
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String result =	actionBase.actionBegin(request);
        String json = "";

        try {
            String ip = ServletRequestUtils.getStringParameter(request,"ip");
            String name = ServletRequestUtils.getStringParameter(request,"name");
            String commond = "location";
            String[][] params = new String[][] {
                    { "ip", ip },
                    { "name", name },
                    {"commond",commond}
            };

            json = callService(params,"http://"+vpnip+":"+vpnport+"/sslvpn/UserSnapshotOrLocationAction_Snapshot.action");

            actionBase.actionEnd(response,json,result);

        } catch (ServletRequestBindingException e) {
            logger.info(e);
        } catch (HttpException e) {
            logger.info(e);
        } catch (IOException e) {
            logger.info(e);
        }
    }


    public String vpnConfigInfo() throws IOException {
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String result =	actionBase.actionBegin(request);
        String json = "{success:true,vpnip:'"+vpnip+"',vpnport:'"+vpnport+"'}";
        actionBase.actionEnd(response,json,result);

        return "";
    }

    public void updatevpnConfig() throws ServletRequestBindingException, IOException {
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String result =	actionBase.actionBegin(request);
        String vpnip = ServletRequestUtils.getStringParameter(request,"ip");
        String vpnport = ServletRequestUtils.getStringParameter(request,"port");

        prop.setProperty("vpnip", vpnip);
        prop.setProperty("vpnport",vpnport);
        String json = "{success:true,vpnip:'"+vpnip+"',vpnport:'"+vpnport+"'}";
        actionBase.actionEnd(response,json,result);
    }



    public static String callService(String[][] params,String methodurl) {
        logger.info("????callService????");
        String data="";
        HttpClient client = new HttpClient();
        client.getHttpConnectionManager().getParams().setConnectionTimeout(
                5 * 1000);
        client.getHttpConnectionManager().getParams().setSoTimeout(5 * 1000);
//        GetMethod get =new GetMethod(methodurl);
//        get.addRequestHeader("","");
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
            logger.info("statusCode:" + statusCode);
            System.out.println("statusCode=="+statusCode);
            responseVpn.setCode(statusCode);
            if (statusCode == 200) {
                data = post.getResponseBodyAsString();
                logger.info("DATA????:"+data);
                responseVpn.setData(data);
            }
        } catch (Exception e) {
            logger.info("?????????"+ e);
        }
        return data;
    }

}
