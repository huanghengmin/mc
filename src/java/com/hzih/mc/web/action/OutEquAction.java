package com.hzih.mc.web.action;

import com.hzih.mc.web.thread.SnmpCorrect;
import com.inetec.common.exception.E;
import com.inetec.common.exception.Ex;
import com.inetec.common.i18n.Message;
import com.inetec.ichange.main.api.DataAttributes;
import com.inetec.ichange.mc.service.IPlatManager;
import com.inetec.ichange.mc.service.monitor.databean.DeviceDataBean;
import com.inetec.ichange.mc.service.monitor.ping.PingMonitorService;
import com.inetec.ichange.mc.service.monitor.snmp.SnmpMonitorService;
import org.apache.log4j.Category;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: wxh
 * Date: 2005-8-15
 * Time: 20:17:53
 * To change this template use File | Settings | File Templates.
 */
public class OutEquAction extends HttpServlet {
    public static Category s_log = null;
    private  static Map ipMap = PingMonitorService.ipMap;
    private static SnmpCorrect snmpCorrect = IPlatManager.snmpCorrect;

    public OutEquAction() {
        s_log = Category.getInstance(OutEquAction.class);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        System.out.println("doget����");
        response.setContentType("text/html");
        PrintWriter writer = response.getWriter();

        writer.println("<html>");
        writer.println("<head>");
        writer.println("<title>vpn  Service Page</title>");
        writer.println("</head>");
        writer.println("<body bgcolor=white>");
        writer.println("<table border=\"0\">");
        writer.println("<tr>");
        writer.println("<td>");
        writer.println("<h1>Hzih Vpn main Status Page</h1>");
        writer.println("<P>vpn  service is running.<P><BR>");
        writer.println("</td>");
        writer.println("</tr>");
        writer.println("</table>");
        writer.println("</body>");
        writer.println("</html>");
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            byte[] data = null;
            String result="";
            int status = 200;
            String ip = request.getParameter("deviceip");
            String command = request.getParameter("Command");
            if(command.equalsIgnoreCase("ipping")){
                result = (String)ipMap.get(ip);
                if(result==null||"null".equalsIgnoreCase(result)){
                       result = "false";
                }
                status=200;
            }
            else {
                String id = SnmpMonitorService.dataset.getDeviceIDByIP(ip);
                DeviceDataBean deviceDataBean = SnmpMonitorService.dataset.getDeviceDataBeanByID(id);
                if(deviceDataBean.getMaxcon()!=0||deviceDataBean.getCurrentcon()!=0||deviceDataBean.getCpu()!=0||deviceDataBean.getMem()!=0||deviceDataBean.getMem_total()!=0||deviceDataBean.getDisk()!=0||deviceDataBean.getDisk_total()!=0){
                    result = deviceDataBean.toJsonString();
                    result = result.substring(0,result.length()-1);
                    result = result+",'ipPing':"+ipMap.get(ip)+"}";
                    status=200;
                    snmpCorrect.snmpCorrectMap.put(ip,"true");
                }else{
                    result = deviceDataBean.toJsonString();
                    result = result.substring(0,result.length()-1);
                    result = result+",'ipPing':"+ipMap.get(ip)+"}";
                    if("true".equalsIgnoreCase((String) SnmpCorrect.snmpCorrectMap.get(ip))){
                        status=200;
                    }else {
                        status=503;
                    }

                }
            }
            response.setStatus(status);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(result.getBytes());
            data = DataAttributes.readInputStream(byteArrayInputStream);
            response.setContentLength(data.length);
            response.getOutputStream().write(data);
    }

    private DataAttributes reciveServiceControl(HttpServletRequest req) throws Ex {
        DataAttributes result = new DataAttributes();
        String common = req.getParameter("Command");
        if (common == null || common == "") {
            common = req.getHeader("Command");
        }
        if (common == null || common == "") {
            throw new Ex().set(E.E_ObjectNotFound, new Message("Commond is null or empty."));
        }

        Enumeration enumeration = req.getParameterNames();
        Enumeration reqheader = req.getHeaderNames();

        while (enumeration.hasMoreElements()) {
            String hdr = (String) enumeration.nextElement();
            result.putValue(hdr.toLowerCase(), req.getParameter(hdr));
            //s_log.info("request parameter:" + hdr + " value:" + req.getParameter(hdr));
        }
        while (reqheader.hasMoreElements()) {
            String hdr = (String) reqheader.nextElement();
            result.putValue(hdr.toLowerCase(), req.getHeader(hdr));
            //s_log.info("request hdr:" + hdr + " value:" + req.getHeader(hdr));
        }

        try {
            if (req.getContentLength() > 0)
                result.setResultData(DataAttributes.readInputStream(req.getInputStream()));
        } catch (IOException e) {
            throw new Ex().set(E.E_IOException, new Message("Request get Stream error."));
        }
        return result;


    }


    public void init(String path, String fileName) throws ServletException {


    }


}
