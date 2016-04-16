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
 * User: Ǯ����
 * Date: 12-6-11
 * Time: ����11:10
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
            msg = "����ϵͳ�ɹ�";
            logger.info("ƽ̨����, �û�����ϵͳ�ɹ� ");
        } catch (Exception e) {
            logger.error("ƽ̨����", e);
            logger.error("ƽ̨����, �û�����ϵͳʧ�� ");
            msg = "����ϵͳʧ��";

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
            msg = "�����豸�ɹ�";
            logger.info("ƽ̨����, �û������豸�ɹ� ");
        } catch (Exception e) {
            logger.error("ƽ̨����", e);
            logger.error("ƽ̨����, �û������豸ʧ�� ");
            msg = "�����豸ʧ��";
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
            msg = "�ر��豸�ɹ�";
            logger.info("ƽ̨����, �û��ر��豸�ɹ� ");
        } catch (Exception e) {
            logger.error("ƽ̨����", e);
            logger.error("ƽ̨����, �û��ر��豸ʧ�� ");
            msg = "�ر��豸ʧ��";
        }
        String json = "{success:true,msg:'"+msg+"'}";
        actionBase.actionEnd(response,json,result);
        return null;
    }

}
