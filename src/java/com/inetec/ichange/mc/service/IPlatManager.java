package com.inetec.ichange.mc.service;

import com.hzih.mc.web.action.InterfaceManagerAction;
import com.hzih.mc.web.thread.SnmpCorrect;
import com.inetec.common.exception.Ex;
import com.inetec.ichange.common.ApplicationContextUtil;
import com.inetec.ichange.common.syslog.SyslogServer;
import com.inetec.ichange.mc.IPlatConstant;
import com.inetec.ichange.mc.Pagination;
import com.inetec.ichange.mc.action.IAction;
import com.inetec.ichange.mc.pojo.ConfigBean;
import com.inetec.ichange.mc.pojo.ConfigDao;
import com.inetec.ichange.mc.pojo.DeviceDao;
import com.inetec.ichange.mc.pojo.SnmpOIDDao;
import com.inetec.ichange.mc.service.http.client.TerminalinfService;
import com.inetec.ichange.mc.service.monitor.cms.CmsDeviceConfigService;
import com.inetec.ichange.mc.service.monitor.ping.PingMonitorService;
import com.inetec.ichange.mc.service.monitor.snmp.SnmpMonitorService;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class IPlatManager extends HttpServlet {

    private static final long serialVersionUID = -3161681620670465963L;
    public static SyslogServer syslogMonitorService;
    // private static SysLogMonitorService syslogMonitorService;
    public static SnmpMonitorService snmpService;
    private static CmsDeviceConfigService cmsDeviceConfig;
    private static PingMonitorService pingMonitorService;
    public static TerminalinfService terminalinfServcie;
    public static SnmpCorrect snmpCorrect;
    private static boolean syslogIsRun = false;
    public static boolean snmpIsRun = false;
    private static boolean termianlIsRun = false;
    private static boolean ippingIsRun = false;
    private static boolean snmpCorrectIsRun = false;
    private static Logger logger = Logger.getLogger(IPlatManager.class);

    /*
      * Constructor of the object.
      */
    public IPlatManager() {
        super();
        processSyslog();
        processSnmp();
        processIp();
        processSnmpCorrect();

    }



    /**
     * Destruction of the servlet. <br>
     */
    public void destroy() {
        super.destroy(); // Just puts "destroy" string in log
        // Put your code here
    }

    /**
     * The doGet method of the servlet. <br>
     * <p/>
     * This method is called when a form has its tag value method equals to get.
     *
     * @param request  the request send by the client to the server
     * @param response the response send by the server to the client
     * @throws ServletException if an error occurred
     * @throws IOException      if an error occurred
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter(IPlatConstant.SERVICE_ACTION);
        try {
//			if (action.equals(IPlatConstant.SAVE_USER_ACTION)) {
//				getAction(action).execute(request, response);
//			} else if (action.equals(IPlatConstant.LIST_USER_ACTION)) {
//				getAction(action).execute(request, response);
//			} else if (action.equals(IPlatConstant.SAVE_DEPART_ACTION)) {
//				getAction(action).execute(request, response);
//			} else if (action.equals(IPlatConstant.LIST_DEPART_ACTION)) {
//				getAction(action).execute(request, response);
//			}else if (action.equals(IPlatConstant.SAVE_RESOURCE_ACTION)) {
//				getAction(action).execute(request, response);
//			} else if (action.equals(IPlatConstant.LIST_RESOURCE_ACTION)) {
//				getAction(action).execute(request, response);
//			}else if (action.equals(IPlatConstant.LIST_RESTYPE_ACTION)) {
//				getAction(action).execute(request, response);
//			}else if (action.equals(IPlatConstant.LIST_ROLE_ACTION)) {
//				getAction(action).execute(request, response);
//			}else if (action.equals(IPlatConstant.SAVE_ROLE_ACTION)) {
//				getAction(action).execute(request, response);
//			}else if (action.equals(IPlatConstant.LIST_ROLERES_ACTION)) {
//				getAction(action).execute(request, response);
//			}else if (action.equals(IPlatConstant.LIST_USERROLE_ACTION)) {
//				getAction(action).execute(request, response);
//			}else if (action.equals(IPlatConstant.LOGON_ACTION)) {
//				getAction(action).execute(request, response);
//			}
            getAction(action).execute(request, response);
        } catch (Ex x) {
            response.getWriter().write(x.getMsg().toString());
        }
    }

    /**
     * The doPost method of the servlet. <br>
     * <p/>
     * This method is called when a form has its tag value method equals to
     * post.
     *
     * @param request  the request send by the client to the server
     * @param response the response send by the server to the client
     * @throws ServletException if an error occurred
     * @throws IOException      if an error occurred
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter(IPlatConstant.SERVICE_ACTION);
        try {
//			if (action.equals(IPlatConstant.SAVE_USER_ACTION)) {
//				getAction(action).execute(request, response);
//			} else if (action.equals(IPlatConstant.LIST_USER_ACTION)) {
//				getAction(action).execute(request, response);
//			} else if (action.equals(IPlatConstant.SAVE_DEPART_ACTION)) {
//				getAction(action).execute(request, response);
//			} else if (action.equals(IPlatConstant.LIST_DEPART_ACTION)) {
//				getAction(action).execute(request, response);
//			}else if (action.equals(IPlatConstant.SAVE_RESOURCE_ACTION)) {
//				getAction(action).execute(request, response);
//			} else if (action.equals(IPlatConstant.LIST_RESOURCE_ACTION)) {
//				getAction(action).execute(request, response);
//			}else if (action.equals(IPlatConstant.LIST_RESTYPE_ACTION)) {
//				getAction(action).execute(request, response);
//			}else if (action.equals(IPlatConstant.LIST_ROLE_ACTION)) {
//				getAction(action).execute(request, response);
//			}else if (action.equals(IPlatConstant.SAVE_ROLE_ACTION)) {
//				getAction(action).execute(request, response);
//			}else if (action.equals(IPlatConstant.LIST_ROLERES_ACTION)) {
//				getAction(action).execute(request, response);
//			}else if (action.equals(IPlatConstant.LIST_USERROLE_ACTION)) {
//				getAction(action).execute(request, response);
//			}else if (action.equals(IPlatConstant.LOGON_ACTION)) {
//				getAction(action).execute(request, response);
//			}
            getAction(action).execute(request, response);
        } catch (Ex x) {
            response.getWriter().write(x.getMsg().toString());
        }
    }

    /**
     * Initialization of the servlet. <br>
     *
     * @throws ServletException if an error occurs
     */
    public void init() throws ServletException {


    }

    private IAction getAction(String actionName) {

        return (IAction) ApplicationContextUtil.getBean(actionName);
    }

    public void closeSyslog() {
        IPlatManager.syslogIsRun = false;
        syslogMonitorService.close();
        syslogMonitorService = null;
        termianlIsRun = false;
        terminalinfServcie.close();

    }

    public void closeSnmp() {
        IPlatManager.snmpIsRun = false;
    }

    public void processSyslog() {
        if (IPlatManager.syslogIsRun) {
            return;
        }
        if (IPlatManager.termianlIsRun) {
            return;
        }
        if (terminalinfServcie == null) {
            terminalinfServcie = new TerminalinfService();
        }
        if (syslogMonitorService == null) {
            syslogMonitorService = new SyslogServer();
        }
        if (cmsDeviceConfig == null) {
            cmsDeviceConfig = new CmsDeviceConfigService();
        }
        if (syslogMonitorService.isRun()) {
            IPlatManager.syslogIsRun = true;
            return;
        }
        ConfigDao config = (ConfigDao) ApplicationContextUtil.getBean("configDAO");
        if (config == null) {
            return;
        }
        Pagination<ConfigBean> paginatin = config.getConfig();
        if (paginatin == null) {
            logger.warn("configDAO reader Config is null.");
            return;
        }
        if (paginatin.getItems() != null && paginatin.getTotalCount() > 0) {
            ConfigBean configBean = (ConfigBean) paginatin.getItems().get(0);
            int port = Integer.parseInt(configBean.getSysport().trim());
            int cmsPort = Integer.parseInt(configBean.getCmssysport().trim());
            syslogMonitorService.config("0.0.0.0", port, "GBK");
            syslogMonitorService.setRemtoeServer(configBean.getCmsip(), cmsPort);
            cmsDeviceConfig.setHost(configBean.getCmsip());
            cmsDeviceConfig.start();
            terminalinfServcie.start();
            syslogMonitorService.start();
        }

        IPlatManager.syslogIsRun = true;
        IPlatManager.termianlIsRun = true;


    }

    public static void processSnmp() {
        if (IPlatManager.snmpIsRun) {
            return;
        }

        if (snmpService == null) {
            snmpService = new SnmpMonitorService();
        }
        if (snmpService.isRun()) {
            IPlatManager.snmpIsRun = true;
            return;
        }
        ConfigDao config = (ConfigDao) ApplicationContextUtil.getBean("configDAO");
        Pagination<ConfigBean> paginatin = config.getConfig();
        if (paginatin == null) {
            logger.warn("configDAO reader Config is null.");
            InterfaceManagerAction.isConfigOk=false;
            InterfaceManagerAction.error="��ȡ̽������Ϊ��";
            return;
        }
        if (paginatin.getItems() != null && paginatin.getTotalCount() > 0) {
            ConfigBean configBean = (ConfigBean) paginatin.getItems().get(0);
            int port = Integer.parseInt(configBean.getPort().trim());
            DeviceDao device = (DeviceDao) ApplicationContextUtil.getBean("deviceDAO");
            SnmpOIDDao snmpoidDAO = (SnmpOIDDao) ApplicationContextUtil.getBean("snmpoidDAO");
            snmpService.init(device.listDevice(200, 1).getItems(), snmpoidDAO.listSnmpOID(200, 1).getItems(), port);
            new Thread(snmpService).start();
        }
        IPlatManager.snmpIsRun = true;
    }

    private void processIp() {
        if(IPlatManager.ippingIsRun){
             return;
        }
        if(pingMonitorService == null){
            pingMonitorService = new PingMonitorService();
        }
        if(pingMonitorService.isRun()){
            IPlatManager.ippingIsRun = true;
            return;
        }
        new Thread(pingMonitorService).start();
        IPlatManager.ippingIsRun=true;
    }

    private void processSnmpCorrect() {
        if(IPlatManager.snmpCorrectIsRun){
            return;
        }
        if(snmpCorrect == null){
            snmpCorrect = new SnmpCorrect();
        }
        if(snmpCorrect.isRun()){
            IPlatManager.ippingIsRun = true;
            return;
        }
        new Thread(snmpCorrect).start();
        IPlatManager.ippingIsRun=true;
    }


}
