package com.hzih.mc.web.action;

import com.hzih.mc.utils.FileUtil;
import com.hzih.mc.utils.StringContext;
import com.inetec.common.client.ECommonUtilFactory;
import com.inetec.common.client.IECommonUtil;
import com.inetec.common.client.util.LogBean;
import com.inetec.common.client.util.XChange;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.OutputStream;
import java.util.StringTokenizer;

/**
 * Created by IntelliJ IDEA.
 * User: Ǯ����
 * Date: 12-6-11
 * Time: ����1:04
 * To change this template use File | Settings | File Templates.
 */
public class DownLoadAction extends ActionSupport{
    private static final Logger logger = Logger.getLogger(DownLoadAction.class);
    private String type;
    private String logName;

    public String readLocalLogName() throws Exception{
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String result =	actionBase.actionBegin(request);
        String json = null;
        try {
            json = FileUtil.readFileNames(StringContext.localLogPath);
            logger.info("��־����, �û���ȡ���б�����־���ơ���С��Ϣ�ɹ�");
        } catch (Exception e) {
            logger.error("��־����", e);
            logger.error("��־����, �û���ȡ���б�����־���ơ���С��Ϣʧ��");
        }
        actionBase.actionEnd(response,json,result);
        return null;
    }

    public String readRemoteLogName() throws Exception{
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String result =	actionBase.actionBegin(request);
        String json = null;
        try {
            json = FileUtil.readRemoteFileNames();
            logger.info("��־����, �û���ȡ����Զ����־���ơ���С��Ϣ�ɹ�");
        } catch (Exception e) {
            logger.error("��־����", e);
            logger.error("��־����, �û���ȡ����Զ����־���ơ���С��Ϣʧ��");
        }
        actionBase.actionEnd(response,json,result);
        return null;
    }

    public String download() throws Exception {
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String result =	actionBase.actionBegin(request);
        String json = "{success:false}";
        try {
            if(logName!=null){
                logger.info("����:" + logName+"��ʼ");
            }
            String Agent = request.getHeader("User-Agent");
            StringTokenizer st = new StringTokenizer(Agent,";");
            st.nextToken();
            //�õ��û����������  MSIE  Firefox
            String userBrowser = st.nextToken();
            String path = null;
            if ("internal_log".equals(type)) {
                path = StringContext.localLogPath +"/" + logName.split("\\(")[0];
                File source = new File(path);
                String name = source.getName();
                FileUtil.downType(response,name,userBrowser);
                response = FileUtil.copy(source, response);
                logger.info("����" + logName.split("\\(")[0] + "�ɹ�!");
            } else if ("external_log".equals(type)) {
                IECommonUtil ecu = ECommonUtilFactory.createECommonUtil();
                LogBean bean = new LogBean();
                bean.setLogFileName(logName.split("\\(")[0]);
                bean.setLogFileLocation(0);
                int loglen = 0;
                boolean isLogEnd = false;
                OutputStream out=response.getOutputStream();
                FileUtil.downType(response,logName.split("\\(")[0],userBrowser);
                while (!isLogEnd) {
                    try {
                        bean.setLogFileLocation(loglen);
                        byte[] files = ecu.getLogFile(bean);
                        logger.info("�ļ�����: "+files.length);
                        if (files.length > 0) {
                            loglen += files.length;
                            out.write(files);
                        } else {
                            isLogEnd = true;
                            out.flush();
                        }
                    } catch (XChange e) {
                        logger.warn(e.getMessage());
                    }
                }
                logger.info("����" + logName.split("\\(")[0] + "�ɹ�!");
            }
            logger.info("��־����, �û�������־�ɹ�");
            json = "{success:true}";
        } catch (Exception e) {
            logger.error("��־����", e);
            logger.error("��־����, �û�������־ʧ��");
        }
        actionBase.actionEnd(response,json,result);
        return null;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLogName() {
        return logName;
    }

    public void setLogName(String logName) {
        this.logName = logName;
    }
}
