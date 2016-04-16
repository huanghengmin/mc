package com.hzih.mc.web.action;

import com.avdheshyadav.p4j.common.DAOException;
import com.avdheshyadav.p4j.jdbc.service.GenericDAO;
import com.avdheshyadav.p4j.jdbc.service.GenericDaoImpl;
import com.hzih.mc.entity.NetInfo;
import com.hzih.mc.utils.NetworkUtil;
import com.hzih.mc.web.thread.PingIp;
import com.hzih.mc.web.thread.SnmpCorrect;
import com.inetec.common.exception.Ex;
import com.inetec.common.net.Ping;
import com.inetec.common.net.Telnet;
import com.inetec.common.util.OSInfo;
import com.inetec.ichange.common.syslog.SyslogServer;
import com.inetec.ichange.mc.pojo.DeviceBean;
import com.inetec.ichange.mc.pojo.DeviceDao;
import com.inetec.ichange.mc.service.IPlatManager;
import com.inetec.ichange.mc.service.monitor.databean.DeviceDataBean;
import com.inetec.ichange.mc.service.monitor.ping.PingMonitorService;
import com.inetec.ichange.mc.service.monitor.snmp.SnmpMonitorService;
import com.inetec.ichange.mc.service.monitor.utils.DeviceDataBeanSet;
import com.inetec.ichange.mc.utils.DaoService;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

/**
 * �������
 *
 */
public class InterfaceManagerAction extends ActionSupport {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(InterfaceManagerAction.class);
    private static Map ipmap = new HashMap();
    private static Map threadmap = new HashMap();
    private static int  threadnum;
    public static Map snmpMap = new HashMap();
    public static Boolean isConfigOk=true;
    public static String error="";
    private static  SnmpCorrect snmpCorrect = IPlatManager.snmpCorrect;



    public static Boolean getConfigOk() {
        return isConfigOk;
    }

    public static void setConfigOk(Boolean configOk) {
        isConfigOk = configOk;
    }

    public static Map getSnmpMap() {
        return snmpMap;
    }

    public static void setSnmpMap(Map snmpMap) {
        InterfaceManagerAction.snmpMap = snmpMap;
    }

    public static Map getThreadmap() {
        return threadmap;
    }

    public static void setThreadmap(Map threadmap) {
        InterfaceManagerAction.threadmap = threadmap;
    }

    public static Map getIpmap() {
        return ipmap;
    }

    public static void setIpmap(Map ipmap) {
        InterfaceManagerAction.ipmap = ipmap;
    }

    /**
     *
     *
     */


    public String ping() throws IOException {
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String result =	actionBase.actionBegin(request);
        String json = null;
        String msg = null;
        try {
            String ip = ServletRequestUtils.getStringParameter(request,"ip");
            int count = ServletRequestUtils.getIntParameter(request,"count");
            String pingStr = Ping.exec(ip, count);
            msg = getResult(pingStr);
            logger.info("��ͨ�����û�ping�ɹ�");
        } catch (Exception e) {
            logger.error("��ͨ����", e);
            logger.error("��ͨ����, �û�ping���ɹ� ");
            return null;
        }
        json = "{success:true,msg:'"+msg+"'}";
        actionBase.actionEnd(response,json,result);
        return "ping";
    }

    private String getResult(String pingStr) {
        String result = "";
        OSInfo osInfo = OSInfo.getOSInfo();
        if(osInfo.isLinux()){
            String[] pings = pingStr.split("\n");
            for (int i = 0; i < pings.length; i++) {
                if(i<pings.length - 1){
                    result += pings[i].trim()+"<br>";
                }else{
                    result += pings[i].trim();
                }
            }
        }else if(osInfo.isWin()){
            String[] pings = pingStr.split("\r\n");
            for (int i = 0; i < pings.length; i++) {
                if(i<pings.length - 1){
                    result += pings[i].trim()+"<br>";
                }else{
                    result += pings[i].trim();
                }
            }
        }
        return result;
    }

    /**
     * �˿ڲ���
     *
     */
    public String telnet() throws Exception{
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String result =	actionBase.actionBegin(request);
        String json = null;
        String msg = null;
        try {
            String ip = ServletRequestUtils.getStringParameter(request,"ip");
            int port = ServletRequestUtils.getIntParameter(request,"port");
            boolean isTelnet = Telnet.exec(ip, port);
            if(isTelnet){
                logger.info("IP"+ip+"�ϵĶ˿�"+port+"�Ǵ򿪵�!");
                msg = "<font color=\"green\">�˿��Ǵ򿪵�!</font>";
            }else{
                logger.info("IP"+ip+"�ϵĶ˿�"+port+"�ǹرյ�!");
                msg = "<font color=\"red\">�˿��ǹرյ�!</font>";
            }
            logger.info("�˿ڲ���, �û�telnet�ɹ� ");
        } catch (Exception e) {
            logger.error("�˿ڲ���", e);
            logger.error("�˿ڲ���, �û�telnet���ɹ� ");
            msg = "telnetʧ��";
        }
        json = "{success:true,msg:'"+msg+"'}";
        actionBase.actionEnd(response,json,result);
        return null;
    }

    /**
     * ��ȡ·����Ϣ
     *
     */
    public String readRouter() throws Exception {
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String result =	actionBase.actionBegin(request);
        String json = "{success:true,total:0,rows:[]}";
        try {
            String startStr = ServletRequestUtils.getStringParameter(request,"start");
            String limitStr = ServletRequestUtils.getStringParameter(request,"limit");
            Integer start = Integer.decode(startStr);
            Integer limit = Integer.decode(limitStr);
            NetworkUtil networkUtil = new NetworkUtil();
            json = networkUtil.readListRouter(start,limit);
            logger.info("·�ɹ���, �û���ȡ·����Ϣ�ɹ� ");
        } catch (Exception e) {
            logger.error("·�ɹ���", e);
            logger.error("·�ɹ���, �û���ȡ·����Ϣ���ɹ� ");
        }
        actionBase.actionEnd(response, json, result);
        return null;
    }

    /**
     * ��ȡ���нӿ���
     *
     */
    public String readInterfaceName() throws Exception {
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String result =	actionBase.actionBegin(request);
        String json =  "{'success':true,'total':0,'rows':[]}";
        String model = "·�ɹ���/�ӿڹ���";
        String t = ServletRequestUtils.getStringParameter(request,"t");
        if(t.equals("router")){
            model = "·�ɹ���";
        } else if (t.endsWith("interface")){
            model = "�ӿڹ���";
        }
        try {
            NetworkUtil networkUtil = new NetworkUtil();
            json = networkUtil.readListNetInfoName();
            logger.info(model + ", �û���ȡ���нӿ����ɹ� ");
        } catch (Exception e) {
            logger.error(model, e);
            logger.error(model + ", �û���ȡ���нӿ������ɹ� ");
        }
        actionBase.actionEnd(response, json, result);
        return null;
    }
    /**
     * ����һ��·����Ϣ
     *
     */
    public String saveRouter() throws Exception {
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String result =	actionBase.actionBegin(request);
        String json = null;
        String msg = null;
        try {
            String interfaceName = ServletRequestUtils.getStringParameter(request,"interfaceName");
            String subnetMask = ServletRequestUtils.getStringParameter(request,"subnetMask");
            String gateway = ServletRequestUtils.getStringParameter(request,"gateway");
            String destination  = ServletRequestUtils.getStringParameter(request,"destination");
            NetInfo netInfo = new NetInfo(interfaceName,subnetMask,gateway,destination);
            NetworkUtil networkUtil = new NetworkUtil();
            msg = networkUtil.saveRouter(netInfo);
            logger.info("·�ɹ���, �û�����һ��·����Ϣ�ɹ� ");

        } catch (Exception e) {
            logger.error("·�ɹ���", e);
            logger.error("·�ɹ���, �û�����һ��·����Ϣ���ɹ� ");
            msg = "����һ��·����Ϣʧ��";
        }
        json = "{success:true,msg:'"+msg+"'}";
        actionBase.actionEnd(response, json, result);
        return null;
    }

    /**
     * ɾ��һ��·����Ϣ
     *
     */
    public String deleteRouter() throws Exception {
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String result =	actionBase.actionBegin(request);
        String json = null;
        String msg = null;
        try {
            String[] destinationArray = ServletRequestUtils.getStringParameters(request,"destinationArray");
            String[] gatewayArray = ServletRequestUtils.getStringParameters(request,"gatewayArray");
            String[] subnetMaskArray = ServletRequestUtils.getStringParameters(request,"subnetMaskArray");
            String[] interfaceNameArray = ServletRequestUtils.getStringParameters(request,"interfaceNameArray");
            NetworkUtil networkUtil = new NetworkUtil();
            List<NetInfo> netInfos = new ArrayList<NetInfo>();
            for (int i = 0; i < destinationArray.length; i++) {
                NetInfo netInfo = new NetInfo();
                netInfo.setDestination(destinationArray[i]);
                netInfo.setInterfaceName(interfaceNameArray[i]);
                netInfo.setSubnetMask(subnetMaskArray[i]);
                netInfo.setGateway(gatewayArray[i]);
                netInfos.add(netInfo);
            }
            msg = networkUtil.deleteRouter(netInfos);
            logger.info("·�ɹ���, �û�ɾ��·����Ϣ�ɹ� ");
        } catch (Exception e) {
            logger.error("·�ɹ���", e);
            logger.error("·�ɹ���, �û�ɾ��·����Ϣ���ɹ� ");
            msg = "ɾ��·����Ϣʧ��";
        }
        json = "{success:true,msg:'"+msg+"'}";
        actionBase.actionEnd(response, json, result);
        return null;
    }

    /**
     * ��ȡ�ӿ���Ϣ
     *
     */
    public String readInterface() throws Exception {
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String result =	actionBase.actionBegin(request);
        String json = "{success:true,total:0,rows:[]}";
        try {
            String startStr = ServletRequestUtils.getStringParameter(request,"start");
            String limitStr = ServletRequestUtils.getStringParameter(request,"limit");
            Integer start = Integer.decode(startStr);
            Integer limit = Integer.decode(limitStr);
            NetworkUtil networkUtil = new NetworkUtil();
            json = networkUtil.readListNetInfo(start,limit);
            logger.info("�ӿڹ���,  �û���ȡ�ӿ���Ϣ�ɹ� ");
        } catch (Exception e) {
            logger.error("�ӿڹ���", e);
            logger.error("�ӿڹ���,  �û���ȡ�ӿ���Ϣ���ɹ� ");
        }
        actionBase.actionEnd(response, json, result);
        return null;
    }

    /**
     *  ����ӿ�
     *
     */
    public String ifInterfaceUp() throws Exception {
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String result =	actionBase.actionBegin(request);
        String json = null;
        String msg = null;
        try {
            String interfaceName = ServletRequestUtils.getStringParameter(request,"interfaceName");
            NetworkUtil networkUtil = new NetworkUtil();
            msg = networkUtil.ifUp(interfaceName);
            logger.info("�ӿڹ���,  �û�����ӿڳɹ� ");
        } catch (Exception e) {
            logger.error("�ӿڹ���", e);
            logger.error("�ӿڹ���,  �û�����ӿڲ��ɹ� ");
            msg = "����ӿ�ʧ��";
        }
        json = "{success:true,msg:'"+msg+"'}";
        actionBase.actionEnd(response,json,result);
        return null;
    }

    /**
     *  ע���ӿ�
     *
     */
    public String ifInterfaceDown() throws Exception {
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String result =	actionBase.actionBegin(request);
        String json = null;
        String msg = null;
        try {
            String interfaceName = ServletRequestUtils.getStringParameter(request,"interfaceName");
            NetworkUtil networkUtil = new NetworkUtil();
            msg = networkUtil.ifDown(interfaceName);
            logger.info("�ӿڹ���,  �û�ע���ӿڳɹ� ");
        } catch (Exception e) {
            logger.error("�ӿڹ���", e);
            logger.error("�ӿڹ���,  �û�ע���ӿڲ��ɹ� ");
            msg = "ע���ӿ�ʧ��";
        }
        json = "{success:true,msg:'"+msg+"'}";
        actionBase.actionEnd(response, json, result);
        return null;
    }

    /**
     * ��������ӿ�
     *
     */
    public String saveInterface() throws Exception {
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String result =	actionBase.actionBegin(request);
        String json = null;
        String msg = null;
        try {
            String interfaceName = ServletRequestUtils.getStringParameter(request,"interfaceName");
            String ip = ServletRequestUtils.getStringParameter(request,"ip");
            Boolean up = ServletRequestUtils.getBooleanParameter(request,"isUp");
            String subnetMask = ServletRequestUtils.getStringParameter(request,"subnetMask");
            String broadCast = ServletRequestUtils.getStringParameter(request,"broadCast");
            NetInfo netInfo = new NetInfo(interfaceName,ip,up, subnetMask,broadCast);
            NetworkUtil networkUtil = new NetworkUtil();
            msg = networkUtil.saveInterface(netInfo);
            logger.info("�ӿڹ���,  �û���������ӿ���Ϣ�ɹ� ");
        } catch (Exception e) {
            logger.error("�ӿڹ���", e);
            logger.error("�ӿڹ���,  �û���������ӿ���Ϣ���ɹ� ");
            msg = "��������ӿ���Ϣʧ��";
        }
        json = "{success:true,msg:'"+msg+"'}";
        actionBase.actionEnd(response, json, result);
        return null;
    }

    /**
     *  ɾ������ӿ�
     *
     */
    public String deleteInterface() throws Exception {
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String result =	actionBase.actionBegin(request);
        String json = null;
        String msg = null;
        try {
            String interfaceName = ServletRequestUtils.getStringParameter(request,"interfaceName");
            NetworkUtil networkUtil = new NetworkUtil();
            msg = networkUtil.deleteInterface(interfaceName);
            logger.info("�ӿڹ���,  �û�ɾ������ӿڳɹ� ");
        } catch (Exception e) {
            logger.error("�ӿڹ���", e);
            logger.error("�ӿڹ���,  �û�ɾ������ӿڲ��ɹ� ");
            msg = "ɾ������ӿ�ʧ��";
        }
        json = "{success:true,msg:'"+msg+"'}";
        actionBase.actionEnd(response, json, result);
        return null;
    }
    /**
     *  �޸Ľӿ���Ϣ
     *
     */
    public String updateInterface() throws Exception {
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String result =	actionBase.actionBegin(request);
        String json = null;
        String msg = null;
        try {
            String interfaceName = ServletRequestUtils.getStringParameter(request,"interfaceName");
            String encap = ServletRequestUtils.getStringParameter(request,"encap");
            String ip = ServletRequestUtils.getStringParameter(request,"ip");
            Boolean isUp = ServletRequestUtils.getBooleanParameter(request,"isUp");
            Boolean isUpOlder = ServletRequestUtils.getBooleanParameter(request,"isUpOlder");
            String gateway = ServletRequestUtils.getStringParameter(request,"gateway");
            String subnetMask = ServletRequestUtils.getStringParameter(request,"subnetMask");
            String broadCast = ServletRequestUtils.getStringParameter(request,"broadCast");
            NetInfo netInfo = new NetInfo(interfaceName,encap,ip,isUp,gateway,subnetMask,broadCast);
            NetworkUtil networkUtil = new NetworkUtil();
            msg = networkUtil.updateInterface(netInfo,isUpOlder);
            logger.info("�ӿڹ���,  �û��޸Ľӿ���Ϣ�ɹ� ");
        } catch (Exception e) {
            logger.error("�ӿڹ���", e);
            logger.error("�ӿڹ���,  �û��޸Ľӿ���Ϣ���ɹ� ");
            msg = "�޸Ľӿ���Ϣʧ��";
        }
        json = "{success:true,msg:'"+msg+"'}";
        actionBase.actionEnd(response, json, result);
        return null;
    }
    /**
     *  �޸�����ӿ���Ϣ
     *
     */
    public String updateXNInterface() throws Exception {
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String result =	actionBase.actionBegin(request);
        String json = null;
        String msg = null;
        try {
            String interfaceName = ServletRequestUtils.getStringParameter(request,"interfaceName");
            String encap = ServletRequestUtils.getStringParameter(request,"encap");
            String ip = ServletRequestUtils.getStringParameter(request,"ip");
            Boolean isUp = ServletRequestUtils.getBooleanParameter(request,"isUp");
            Boolean isUpOlder = ServletRequestUtils.getBooleanParameter(request,"isUpOlder");
            String gateway = ServletRequestUtils.getStringParameter(request,"gateway");
            String subnetMask = ServletRequestUtils.getStringParameter(request,"subnetMask");
            String broadCast = ServletRequestUtils.getStringParameter(request,"broadCast");
            NetInfo netInfo = new NetInfo(interfaceName,encap,ip,isUp,gateway,subnetMask,broadCast);
            NetworkUtil networkUtil = new NetworkUtil();
            msg = networkUtil.updateInterface(netInfo,isUpOlder);
            logger.info("�ӿڹ���,  �û��޸�����ӿ���Ϣ�ɹ� ");
        } catch (Exception e) {
            logger.error("�ӿڹ���", e);
            logger.error("�ӿڹ���,  �û��޸�����ӿ���Ϣ���ɹ� ");
            msg = "�޸�����ӿ���Ϣʧ��";
        }
        json = "{success:true,msg:'"+msg+"'}";
        actionBase.actionEnd(response, json, result);
        return null;
    }

    /**
     *  �޸�DNS��Ϣ
     *
     */
    public String updateDNS() throws Exception {
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String result =	actionBase.actionBegin(request);
        String json = null;
        String msg = null;
        try {
            String interfaceName = ServletRequestUtils.getStringParameter(request,"interfaceName");
            String dns_1 = ServletRequestUtils.getStringParameter(request,"dns_1");
            String dns_2 = ServletRequestUtils.getStringParameter(request,"dns_2");
            NetInfo netInfo = new NetInfo(interfaceName,dns_1,dns_2);
            NetworkUtil networkUtil = new NetworkUtil();
            msg = networkUtil.updateDNS(netInfo);
            logger.info("�ӿڹ���,  �û��޸�DNS��Ϣ�ɹ� ");
        } catch (Exception e) {
            logger.error("�ӿڹ���", e);
            logger.error("�ӿڹ���,  �û��޸�DNS��Ϣ���ɹ� ");
            msg = "�޸�DNS��Ϣʧ��";
        }
        json = "{success:true,msg:'"+msg+"'}";
        actionBase.actionEnd(response, json, result);
        return null;
    }
    /**
     * ping��ַ�β���
     *
     */



    public String addressPing() throws IOException {
        threadmap = new HashMap();
        ipmap = new HashMap();
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String result = actionBase.actionBegin(request);
        String json = null;
        String msg = null;
        String[] end;
        String[] start;
        String ipstart;
        String ipend;
        String relativeip;
        try {
            logger.info("���뷽��--------------------------------------------");
            ipstart = ServletRequestUtils.getStringParameter(request, "ipstart");
            ipend = ServletRequestUtils.getStringParameter(request, "ipend");
            logger.info("Ip�ֱ�Ϊ" + ipstart + ipend + "-------------------------------");
            start = ipstart.split("\\.");
            end = ipend.split("\\.");
            relativeip = start[0] + "." + start[1] + "." + start[2] + ".";
            int packagenum = 0;
            threadnum=0;
            //����һ���̶߳���       ��СΪ10
            BlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(10);
            //�����̳߳� �����߳�Ϊ 3�� ��������߳���Ϊ 10(9+1) 5S��������̲߳�ִ����ر���   �ȴ�����Ϊ10��
            ThreadPoolExecutor executor = new ThreadPoolExecutor(3, 9, 5, TimeUnit.SECONDS, queue, new ThreadPoolExecutor.CallerRunsPolicy());
            for (int i = Integer.parseInt(start[3]); i <= Integer.parseInt(end[3]); i++) {
                packagenum++;
                if (packagenum == 5) {
                    threadnum++;
                    PingIp pingIp = new PingIp();
                    pingIp.init(relativeip +i,5, relativeip);
                    Thread pingthread = new Thread(pingIp,threadnum+"�߳�");
                    executor.execute(pingthread);
                    packagenum = 0;
                } else if (i == Integer.parseInt(end[3])) {
                    threadnum++;
                    logger.info("�������һ��ping�ĵ�ַ");
                    PingIp pingIp = new PingIp();
                    pingIp.init(relativeip +i, packagenum,relativeip);
                    Thread pingthread = new Thread(pingIp,threadnum+"�߳�");
                    executor.execute(pingthread);
                }
            }
            //�ر��̳߳�
            executor.shutdown();
            logger.info("��ͨ�ɹ�");

        } catch (Exception e) {
            logger.error("��ͨ����", e);
            logger.error("��ͨ����, �û�ping���ɹ� ");
            return null;
        }
        json = "{success:true,msg:'"+msg+"'}";
        actionBase.actionEnd(response, json, result);

        return null;
    }

    public String findPingSuccessIp() throws IOException {
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String result =	actionBase.actionBegin(request);
        String str ="";
        String json = "";
        try {
            int start =Integer.parseInt(ServletRequestUtils.getStringParameter(request,"start")) ;
            int limit =Integer.parseInt(ServletRequestUtils.getStringParameter(request,"limit")) ;
            logger.info(start);
            int num =0;
            str = "{totalProperty:" +ipmap.size() + ",root:[";
            Set<Map.Entry> set = ipmap.entrySet();
            for (Iterator<Map.Entry> it = set.iterator(); it.hasNext();) {
                Map.Entry entry =  it.next();
                if(entry.getValue().equals("true")){
                    num++;
                    if (start < num && num <= (start + limit)) {
                        DeviceDao dao = new DeviceDao();
                        boolean isExist = dao.findDeviceByIp((String)entry.getKey());
                        str+=  "{ip:'"+entry.getKey()+"',flag:'"+isExist+"'},";

                    }
                }

            }
            str +="]}";
            logger.info(str);
            json = str.toString();

        } catch (ServletRequestBindingException e) {
            logger.info(e);
            json = "";
        }
        logger.info("�����豸����JsonΪ"+json);
        actionBase.actionEnd(response,json,result);
        return null;
    }


    public String selectDeviceByIp() throws IOException {
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String result =	actionBase.actionBegin(request);
        String str = "";
        try {
            String ip  = ServletRequestUtils.getStringParameter(request,"ip");
            GenericDAO<DeviceBean> genericDAO = new GenericDaoImpl<DeviceBean>(
                    DaoService.getDaoService().getDataProvider()
                            .getDataFetcher());
            List<DeviceBean> list = genericDAO.findAll(DeviceBean.class,1,10000000);
            for (DeviceBean bean :list){
                if(bean.getDeviceip().equals(ip)){
                    str= "{success:true,id:'"+bean.getId()+"',name:'"+bean.getName()+"',deviceport:'"+bean.getDeviceport()
                            +"',devicesnmppwd:'"+bean.getDevicesnmppwd()+"',auth:'"+bean.getAuth()+"',authpassword:'"+bean.getAuthpassword()+"',common:'"+bean.getCommon()+"',commonpassword:'"+bean.getCommonpassword()+"',devicemode:'"+bean.getDevicemode()+"',available:'"+bean.getAvailable()+"',snmpver:'"+bean.getSnmpver()+"'}";
                }
            }

        } catch (ServletRequestBindingException e) {
            logger.info(e);
        } catch (DAOException e) {
            logger.info(e);
        }
        logger.info(str);
        actionBase.actionEnd(response,str,result);
        return null;
    }




    public boolean isMapFlag() throws IOException {
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String result =	actionBase.actionBegin(request);
        String json;
        String msg ="";
        logger.info(threadnum+"   �߳�����-------------------------------------------------");
        logger.info(threadmap.size()+"   Map����--------------------------------------------");
        if(threadnum==threadmap.size()){
            msg="true";
        }   else{
            msg="false";
        }
        json = msg;
        actionBase.actionEnd(response,json,result);
        return  true;
    }

    //�豸����״̬��Ϣ

    public  String deviceWrokingInfo() throws Exception {
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String result =	actionBase.actionBegin(request);
        String ip="";
        String json="";
//        int num =0;
        Map syslogMap = SyslogServer.syslogMap;
        Map syslogMapTime =  SyslogServer.syslogMapTime;
        Map ipMap = PingMonitorService.ipMap;
        DeviceDataBeanSet dataset = SnmpMonitorService.dataset;

        ConcurrentHashMap beanset = dataset.beanset;

        ConcurrentHashMap ipbeanset = dataset.ipbeanset;


        Set<Map.Entry> ipset = ipbeanset.entrySet();

        String str = "{totalProperty:" +ipbeanset.size() + ",root:[";
        int start =Integer.parseInt(ServletRequestUtils.getStringParameter(request,"start")) ;
        int limit =Integer.parseInt(ServletRequestUtils.getStringParameter(request,"limit")) ;

        Iterator<Map.Entry> it = ipset.iterator();
        List<Map.Entry>  ipKeyList = new ArrayList<Map.Entry>();

        while ( it.hasNext()) {
            Map.Entry ipentry =  it.next();
            ipKeyList.add(ipentry);
        }

        str+=getReturnData(start,limit,ipKeyList,beanset,ipMap,syslogMap,syslogMapTime);

        str +="]}";
        logger.info(str);
        json = str.toString();
        actionBase.actionEnd(response,json,result);
        return "";
    }

    //��������Ƿ�����
    public boolean deviceWorkingInfoping(String ip){
        String pingStr = null;
        String msg = "";
        try {
            pingStr = Ping.exec(ip, 2);
            msg = getResult(pingStr);
            if(msg.indexOf("Reply from")>-1){
                return  true;
            }
            else {
                return false;
            }
        } catch (Ex ex) {
            logger.info(ex);
        }
        return  false;
    }


    public StringBuffer getReturnData(Integer first, Integer limitInt, List<Map.Entry> list, ConcurrentHashMap beanset,Map ipMap ,Map syslogMap,Map syslogMapTime) {
        StringBuffer showData=new StringBuffer();
        int end=first+limitInt;
        int index = end>list.size()?list.size():end;
        for(int i=first;i<index;i++){
            //�ж�̽���Ƿ�������
            if(isConfigOk==false){
                Map.Entry ipentry = list.get(i);
                String ip = (String) ipentry.getKey();
                String   deviceId = (String) ipentry.getValue();
                DeviceDataBean deviceDataBean = (DeviceDataBean) beanset.get(deviceId);
                if("true".equalsIgnoreCase((String) syslogMap.get(ip))){
                    showData.append( "{ip:'"+ip+"',snmp:'"+error+"',ipping:'"+ipMap.get(ip)+"',syslog:'"+syslogMap.get(ip)+"',snmpoid:'"+deviceDataBean.getEqu_name()+"',equname:'"+deviceId+"'},");
                    //��ȡ�����syslogMap��ֵ
                    SyslogServer.syslogMap.remove(ip);
                } else{
                    showData.append( "{ip:'"+ip+"',snmp:'"+error+"',ipping:'"+ipMap.get(ip)+"',syslog:'"+syslogMapTime.get(ip)+"',snmpoid:'"+deviceDataBean.getEqu_name()+"',equname:'"+deviceId+"'},");
                }

            }else{
                Map.Entry ipentry = list.get(i);
                String ip = (String) ipentry.getKey();
                String   deviceId = (String) ipentry.getValue();
                DeviceDataBean deviceDataBean = (DeviceDataBean) beanset.get(deviceId);
                if("true".equalsIgnoreCase((String) syslogMap.get(ip))){
                    if(deviceDataBean.getMaxcon()!=0||deviceDataBean.getCurrentcon()!=0||deviceDataBean.getCpu()!=0||deviceDataBean.getMem()!=0||deviceDataBean.getMem_total()!=0||deviceDataBean.getDisk()!=0||deviceDataBean.getDisk_total()!=0){
                        showData.append( "{ip:'"+ip+"',snmp:'true',ipping:'"+ipMap.get(ip)+"',syslog:'"+syslogMap.get(ip)+"',snmpoid:'"+deviceDataBean.getEqu_name()+"',equname:'"+deviceId+"'},");
                        SnmpCorrect.snmpCorrectMap.put(ip,"true");
                    }else{
                        if("true".equalsIgnoreCase((String) SnmpCorrect.snmpCorrectMap.get(ip))){
                            showData.append( "{ip:'"+ip+"',snmp:'true',ipping:'"+ipMap.get(ip)+"',syslog:'"+syslogMap.get(ip)+"',snmpoid:'"+deviceDataBean.getEqu_name()+"',equname:'"+deviceId+"'},");
                        }  else{
                            showData.append( "{ip:'"+ip+"',snmp:'"+snmpMap.get(ip)+"',ipping:'"+ipMap.get(ip)+"',syslog:'"+syslogMap.get(ip)+"',snmpoid:'"+deviceDataBean.getEqu_name()+"',equname:'"+deviceId+"'},");
                        }
                    }
                    //��ȡ�����syslogMap��ֵ
                    SyslogServer.syslogMap.remove(ip);
                }else{
                    if(deviceDataBean.getMaxcon()!=0||deviceDataBean.getCurrentcon()!=0||deviceDataBean.getCpu()!=0||deviceDataBean.getMem()!=0||deviceDataBean.getMem_total()!=0||deviceDataBean.getDisk()!=0||deviceDataBean.getDisk_total()!=0){
                        showData.append( "{ip:'"+ip+"',snmp:'true',ipping:'"+ipMap.get(ip)+"',syslog:'"+syslogMapTime.get(ip)+"',snmpoid:'"+deviceDataBean.getEqu_name()+"',equname:'"+deviceId+"'},");
                        SnmpCorrect.snmpCorrectMap.put(ip,"true");
                    }else{
                        if("true".equalsIgnoreCase((String) SnmpCorrect.snmpCorrectMap.get(ip))){
                            showData.append( "{ip:'"+ip+"',snmp:'true',ipping:'"+ipMap.get(ip)+"',syslog:'"+syslogMapTime.get(ip)+"',snmpoid:'"+deviceDataBean.getEqu_name()+"',equname:'"+deviceId+"'},");
                        }else{
                            showData.append( "{ip:'"+ip+"',snmp:'"+snmpMap.get(ip)+"',ipping:'"+ipMap.get(ip)+"',syslog:'"+syslogMapTime.get(ip)+"',snmpoid:'"+deviceDataBean.getEqu_name()+"',equname:'"+deviceId+"'},");
                        }
                    }
                }

            }
        }
        return showData;

    }

}


