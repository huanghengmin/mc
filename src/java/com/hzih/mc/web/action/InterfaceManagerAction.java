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
 * 网络管理
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
            logger.info("连通测试用户ping成功");
        } catch (Exception e) {
            logger.error("连通测试", e);
            logger.error("连通测试, 用户ping不成功 ");
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
     * 端口测试
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
                logger.info("IP"+ip+"上的端口"+port+"是打开的!");
                msg = "<font color=\"green\">端口是打开的!</font>";
            }else{
                logger.info("IP"+ip+"上的端口"+port+"是关闭的!");
                msg = "<font color=\"red\">端口是关闭的!</font>";
            }
            logger.info("端口测试, 用户telnet成功 ");
        } catch (Exception e) {
            logger.error("端口测试", e);
            logger.error("端口测试, 用户telnet不成功 ");
            msg = "telnet失败";
        }
        json = "{success:true,msg:'"+msg+"'}";
        actionBase.actionEnd(response,json,result);
        return null;
    }

    /**
     * 读取路由信息
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
            logger.info("路由管理, 用户读取路由信息成功 ");
        } catch (Exception e) {
            logger.error("路由管理", e);
            logger.error("路由管理, 用户读取路由信息不成功 ");
        }
        actionBase.actionEnd(response, json, result);
        return null;
    }

    /**
     * 读取所有接口名
     *
     */
    public String readInterfaceName() throws Exception {
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String result =	actionBase.actionBegin(request);
        String json =  "{'success':true,'total':0,'rows':[]}";
        String model = "路由管理/接口管理";
        String t = ServletRequestUtils.getStringParameter(request,"t");
        if(t.equals("router")){
            model = "路由管理";
        } else if (t.endsWith("interface")){
            model = "接口管理";
        }
        try {
            NetworkUtil networkUtil = new NetworkUtil();
            json = networkUtil.readListNetInfoName();
            logger.info(model + ", 用户读取所有接口名成功 ");
        } catch (Exception e) {
            logger.error(model, e);
            logger.error(model + ", 用户读取所有接口名不成功 ");
        }
        actionBase.actionEnd(response, json, result);
        return null;
    }
    /**
     * 新增一个路由信息
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
            logger.info("路由管理, 用户新增一个路由信息成功 ");

        } catch (Exception e) {
            logger.error("路由管理", e);
            logger.error("路由管理, 用户新增一个路由信息不成功 ");
            msg = "新增一个路由信息失败";
        }
        json = "{success:true,msg:'"+msg+"'}";
        actionBase.actionEnd(response, json, result);
        return null;
    }

    /**
     * 删除一个路由信息
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
            logger.info("路由管理, 用户删除路由信息成功 ");
        } catch (Exception e) {
            logger.error("路由管理", e);
            logger.error("路由管理, 用户删除路由信息不成功 ");
            msg = "删除路由信息失败";
        }
        json = "{success:true,msg:'"+msg+"'}";
        actionBase.actionEnd(response, json, result);
        return null;
    }

    /**
     * 读取接口信息
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
            logger.info("接口管理,  用户读取接口信息成功 ");
        } catch (Exception e) {
            logger.error("接口管理", e);
            logger.error("接口管理,  用户读取接口信息不成功 ");
        }
        actionBase.actionEnd(response, json, result);
        return null;
    }

    /**
     *  激活接口
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
            logger.info("接口管理,  用户激活接口成功 ");
        } catch (Exception e) {
            logger.error("接口管理", e);
            logger.error("接口管理,  用户激活接口不成功 ");
            msg = "激活接口失败";
        }
        json = "{success:true,msg:'"+msg+"'}";
        actionBase.actionEnd(response,json,result);
        return null;
    }

    /**
     *  注销接口
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
            logger.info("接口管理,  用户注销接口成功 ");
        } catch (Exception e) {
            logger.error("接口管理", e);
            logger.error("接口管理,  用户注销接口不成功 ");
            msg = "注销接口失败";
        }
        json = "{success:true,msg:'"+msg+"'}";
        actionBase.actionEnd(response, json, result);
        return null;
    }

    /**
     * 新增虚拟接口
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
            logger.info("接口管理,  用户新增虚拟接口信息成功 ");
        } catch (Exception e) {
            logger.error("接口管理", e);
            logger.error("接口管理,  用户新增虚拟接口信息不成功 ");
            msg = "新增虚拟接口信息失败";
        }
        json = "{success:true,msg:'"+msg+"'}";
        actionBase.actionEnd(response, json, result);
        return null;
    }

    /**
     *  删除虚拟接口
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
            logger.info("接口管理,  用户删除虚拟接口成功 ");
        } catch (Exception e) {
            logger.error("接口管理", e);
            logger.error("接口管理,  用户删除虚拟接口不成功 ");
            msg = "删除虚拟接口失败";
        }
        json = "{success:true,msg:'"+msg+"'}";
        actionBase.actionEnd(response, json, result);
        return null;
    }
    /**
     *  修改接口信息
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
            logger.info("接口管理,  用户修改接口信息成功 ");
        } catch (Exception e) {
            logger.error("接口管理", e);
            logger.error("接口管理,  用户修改接口信息不成功 ");
            msg = "修改接口信息失败";
        }
        json = "{success:true,msg:'"+msg+"'}";
        actionBase.actionEnd(response, json, result);
        return null;
    }
    /**
     *  修改虚拟接口信息
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
            logger.info("接口管理,  用户修改虚拟接口信息成功 ");
        } catch (Exception e) {
            logger.error("接口管理", e);
            logger.error("接口管理,  用户修改虚拟接口信息不成功 ");
            msg = "修改虚拟接口信息失败";
        }
        json = "{success:true,msg:'"+msg+"'}";
        actionBase.actionEnd(response, json, result);
        return null;
    }

    /**
     *  修改DNS信息
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
            logger.info("接口管理,  用户修改DNS信息成功 ");
        } catch (Exception e) {
            logger.error("接口管理", e);
            logger.error("接口管理,  用户修改DNS信息不成功 ");
            msg = "修改DNS信息失败";
        }
        json = "{success:true,msg:'"+msg+"'}";
        actionBase.actionEnd(response, json, result);
        return null;
    }
    /**
     * ping地址段测试
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
            logger.info("进入方法--------------------------------------------");
            ipstart = ServletRequestUtils.getStringParameter(request, "ipstart");
            ipend = ServletRequestUtils.getStringParameter(request, "ipend");
            logger.info("Ip分别为" + ipstart + ipend + "-------------------------------");
            start = ipstart.split("\\.");
            end = ipend.split("\\.");
            relativeip = start[0] + "." + start[1] + "." + start[2] + ".";
            int packagenum = 0;
            threadnum=0;
            //设置一个线程队列       大小为10
            BlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(10);
            //创建线程池 初试线程为 3个 最大运行线程数为 10(9+1) 5S过后池内线程不执行则关闭线   等待队列为10个
            ThreadPoolExecutor executor = new ThreadPoolExecutor(3, 9, 5, TimeUnit.SECONDS, queue, new ThreadPoolExecutor.CallerRunsPolicy());
            for (int i = Integer.parseInt(start[3]); i <= Integer.parseInt(end[3]); i++) {
                packagenum++;
                if (packagenum == 5) {
                    threadnum++;
                    PingIp pingIp = new PingIp();
                    pingIp.init(relativeip +i,5, relativeip);
                    Thread pingthread = new Thread(pingIp,threadnum+"线程");
                    executor.execute(pingthread);
                    packagenum = 0;
                } else if (i == Integer.parseInt(end[3])) {
                    threadnum++;
                    logger.info("进入最后一批ping的地址");
                    PingIp pingIp = new PingIp();
                    pingIp.init(relativeip +i, packagenum,relativeip);
                    Thread pingthread = new Thread(pingIp,threadnum+"线程");
                    executor.execute(pingthread);
                }
            }
            //关闭线程池
            executor.shutdown();
            logger.info("联通成功");

        } catch (Exception e) {
            logger.error("连通测试", e);
            logger.error("连通测试, 用户ping不成功 ");
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
        logger.info("查找设备返回Json为"+json);
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
        logger.info(threadnum+"   线程数量-------------------------------------------------");
        logger.info(threadmap.size()+"   Map数量--------------------------------------------");
        if(threadnum==threadmap.size()){
            msg="true";
        }   else{
            msg="false";
        }
        json = msg;
        actionBase.actionEnd(response,json,result);
        return  true;
    }

    //设备运行状态信息

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

    //监测网络是否正常
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
            //判断探针是否有配置
            if(isConfigOk==false){
                Map.Entry ipentry = list.get(i);
                String ip = (String) ipentry.getKey();
                String   deviceId = (String) ipentry.getValue();
                DeviceDataBean deviceDataBean = (DeviceDataBean) beanset.get(deviceId);
                if("true".equalsIgnoreCase((String) syslogMap.get(ip))){
                    showData.append( "{ip:'"+ip+"',snmp:'"+error+"',ipping:'"+ipMap.get(ip)+"',syslog:'"+syslogMap.get(ip)+"',snmpoid:'"+deviceDataBean.getEqu_name()+"',equname:'"+deviceId+"'},");
                    //读取完清空syslogMap的值
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
                    //读取完清空syslogMap的值
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


