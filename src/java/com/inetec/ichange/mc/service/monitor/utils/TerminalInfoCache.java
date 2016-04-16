package com.inetec.ichange.mc.service.monitor.utils;

import com.inetec.ichange.mc.service.monitor.databean.SysterminalinfDataBean;
import org.apache.log4j.Logger;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 内存存储对象
 *
 * @author bluesky
 *
 */
public class TerminalInfoCache {
    private static final Logger logger = Logger
            .getLogger(TerminalInfoCache.class);
    public static final String Str_JsonHeader = "[";
    public static final String Str_ObjectSpareChar = ",";
    public static final String Str_EndHeader = "]";
    public static String allList;
    public static String onlineList;

    public ConcurrentHashMap beanset = new ConcurrentHashMap();
    public ConcurrentHashMap userbeanset = new ConcurrentHashMap();

    public String getOnlineList(String beginno, String endno, int pagesize) {
        StringBuffer buff = new StringBuffer();
        logger.info("getOnline List begin");
        buff.append(Str_JsonHeader);
        int count = 0;
        Iterator<SysterminalinfDataBean> it = userbeanset.values().iterator();
        while (it.hasNext()) {
            SysterminalinfDataBean bean = it.next();
            if (bean.isOnline()||bean.getStatus()==1) {
                buff.append(bean.toJsonString());
                buff.append(Str_ObjectSpareChar);
                count++;
            }
        }
        logger.info("getOnline List end.");
        buff.append(totalJson(count, beginno, endno, pagesize));
        buff.append(Str_EndHeader);
        return buff.toString();
    }

    public String getAllList(String beginno, String endno, int pagesize) {
        logger.info("getALL List begin");
        StringBuffer buff = new StringBuffer();
        buff.append(Str_JsonHeader);
        Iterator<String> it = userbeanset.keySet().iterator();
        while (it.hasNext()) {
            SysterminalinfDataBean bean2 = (SysterminalinfDataBean) userbeanset
                    .get(it.next());
            // logger.info("SysterminalinfDatabean is:" + bean2.toJsonString());
            buff.append(bean2.toJsonString());
            buff.append(Str_ObjectSpareChar);
        }
        logger.info("getALL List end.");
        buff.append(totalJson(userbeanset.size(), beginno, endno, pagesize));
        buff.append(Str_EndHeader);
        return buff.toString();
    }

    public void init(List<SysterminalinfDataBean> list) {

        for (int i = 0; i < list.size(); i++) {
            if (!userbeanset.containsKey(list.get(i).getUserId())) {
                beanset.put(list.get(i).getIp(), list.get(i));
                userbeanset.put(list.get(i).getUserId(), list.get(i));
            }
        }

    }

    /**
     * 更新在线状态
     *
     * @param ip
     * @param userid
     */
    public void updateOnlineStatus(String ip, String userid, long influx,
                                   long outflux) {
        if (userbeanset.containsKey(userid)) {
            SysterminalinfDataBean bean1 = (SysterminalinfDataBean) userbeanset
                    .get(userid);
            bean1.setLastDate(System.currentTimeMillis());
            bean1.setStatus(1);
            bean1.setIn_flux(bean1.getIn_flux() + influx);
            bean1.setOut_flux(bean1.getOut_flux() + outflux);
            bean1.setIp(ip);
            userbeanset.replace(userid, bean1);
            if (beanset.containsKey(ip) && !ip.equalsIgnoreCase("0.0.0.0")) {
                beanset.replace(ip, bean1);
            } else {
                beanset.put(ip, bean1);
            }
            logger.info("userid update onlinestatus:" + userid + " ip:" + ip
                    + " influx:" + influx + " outflux:" + outflux);

        }
    }

    public String getCnByUserId(String id) {
        if (userbeanset.containsKey(id)) {
            SysterminalinfDataBean bean1 = (SysterminalinfDataBean) userbeanset
                    .get(id);
            return bean1.getCn();

        }
        return null;
    }

    /**
     * 临时阻断当前用户
     *
     * @param ip
     * @param userid
     */
    public void tempblock(String ip, String userid) {
        if (userbeanset.containsKey(userid)) {
            SysterminalinfDataBean bean1 = (SysterminalinfDataBean) userbeanset
                    .get(userid);
            bean1.setLastDate(System.currentTimeMillis());
            bean1.setBlock(true);
            bean1.setStatus(1);
            bean1.setIp(ip);
            userbeanset.replace(userid, bean1);
            if (beanset.containsKey(ip)) {
                beanset.replace(ip, bean1);
            } else {
                beanset.put(ip, bean1);
            }
            logger.info("userid update tempblock:" + userid + " ip:" + ip);

        }
    }

    /**
     * 恢复用户
     *
     * @param ip
     * @param userid
     */
    public void noblock(String ip, String userid) {
        if (userbeanset.containsKey(userid)) {
            SysterminalinfDataBean bean1 = (SysterminalinfDataBean) userbeanset
                    .get(userid);
            bean1.setLastDate(System.currentTimeMillis());
            bean1.setBlock(false);
            bean1.setStatus(1);
            bean1.setIp(ip);
            userbeanset.replace(userid, bean1);
            if (beanset.containsKey(ip)) {
                beanset.replace(ip, bean1);
            } else {
                beanset.put(ip, bean1);
            }
            logger.info("userid update noblock:" + userid + " ip:" + ip);

        }
    }

    /**
     * 阻断当前用户
     *
     * @param ip
     * @param userid
     */
    public void block(String ip, String userid) {
        if (userbeanset.containsKey(userid)) {
            SysterminalinfDataBean bean1 = (SysterminalinfDataBean) userbeanset
                    .get(userid);
            bean1.setLastDate(System.currentTimeMillis());
            bean1.setBlock(true);
            bean1.setStatus(1);
            bean1.setIp(ip);
            userbeanset.replace(userid, bean1);
            if (beanset.containsKey(ip)) {
                beanset.replace(ip, bean1);
            } else {
                beanset.put(ip, bean1);
            }
            logger.info("userid update  block:" + userid + " ip:" + ip);

        }
    }

    public String totalJson(int total, String beginno, String endno,
                            int pagesize) {
        StringBuffer json = new StringBuffer();
        json.append("{total:" + total);
        json.append(",beginno:'" + beginno + "'");
        json.append(",endno:'" + endno + "'");
        json.append(",pagesize:" + pagesize + "}");
        return json.toString();
    }

}
