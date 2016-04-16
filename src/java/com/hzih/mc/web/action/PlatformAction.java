package com.hzih.mc.web.action;


import com.inetec.common.util.OSReBoot;
import com.inetec.ichange.mc.utils.ReStartSystem;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by IntelliJ IDEA.
 * User: 钱晓盼
 * Date: 12-6-11
 * Time: 上午11:10
 * To change this template use File | Settings | File Templates.
 */
public class PlatformAction extends ActionSupport {

    private static final Logger logger = Logger.getLogger(PlatformAction.class);

    public String sysRestart() throws Exception {
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String result =	actionBase.actionBegin(request);
        String msg = null;
        try {
            ReStartSystem.restart();
//            Proc proc;
//            OSInfo osinfo = OSInfo.getOSInfo();
//            if (osinfo.isWin()) {
//                proc = new Proc();
//                proc.exec("nircmd service restart cms");
//            }
//            if (osinfo.isLinux()) {
//              proc = new Proc();
//              proc.exec("service cms restart");
//            }
            msg = "重启系统成功";
            logger.info("平台管理, 用户重启系统成功 ");
        } catch (Exception e) {
            logger.error("平台管理", e);
            logger.error("平台管理, 用户重启系统失败 ");
            msg = "重启系统失败";

        }
        String json = "{success:true,msg:'"+msg+"'}";
        actionBase.actionEnd(response,json,result);
        return null;
    }

    public String equipRestart() throws Exception {
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String result =	actionBase.actionBegin(request);
        String msg = null;
        try {
            OSReBoot.exec();
            msg = "重启设备成功";
            logger.info("平台管理, 用户重启设备成功 ");
        } catch (Exception e) {
            logger.error("平台管理", e);
            logger.error("平台管理, 用户重启设备失败 ");
            msg = "重启设备失败";
        }
        String json = "{success:true,msg:'"+msg+"'}";
        actionBase.actionEnd(response,json,result);
        return null;
    }

    public String equipShutdown() throws Exception {
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String result =	actionBase.actionBegin(request);
        String msg = null;
        try {
            OSReBoot.exec();
            msg = "关闭设备成功";
            logger.info("平台管理, 用户关闭设备成功 ");
        } catch (Exception e) {
            logger.error("平台管理", e);
            logger.error("平台管理, 用户关闭设备失败 ");
            msg = "关闭设备失败";
        }
        String json = "{success:true,msg:'"+msg+"'}";
        actionBase.actionEnd(response,json,result);
        return null;
    }

}
