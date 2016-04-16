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
 * User: 钱晓盼
 * Date: 12-6-11
 * Time: 下午1:04
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
            logger.info("日志下载, 用户获取所有本地日志名称、大小信息成功");
        } catch (Exception e) {
            logger.error("日志下载", e);
            logger.error("日志下载, 用户获取所有本地日志名称、大小信息失败");
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
            logger.info("日志下载, 用户获取所有远程日志名称、大小信息成功");
        } catch (Exception e) {
            logger.error("日志下载", e);
            logger.error("日志下载, 用户获取所有远程日志名称、大小信息失败");
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
                logger.info("下载:" + logName+"开始");
            }
            String Agent = request.getHeader("User-Agent");
            StringTokenizer st = new StringTokenizer(Agent,";");
            st.nextToken();
            //得到用户的浏览器名  MSIE  Firefox
            String userBrowser = st.nextToken();
            String path = null;
            if ("internal_log".equals(type)) {
                path = StringContext.localLogPath +"/" + logName.split("\\(")[0];
                File source = new File(path);
                String name = source.getName();
                FileUtil.downType(response,name,userBrowser);
                response = FileUtil.copy(source, response);
                logger.info("下载" + logName.split("\\(")[0] + "成功!");
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
                        logger.info("文件长度: "+files.length);
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
                logger.info("下载" + logName.split("\\(")[0] + "成功!");
            }
            logger.info("日志下载, 用户下载日志成功");
            json = "{success:true}";
        } catch (Exception e) {
            logger.error("日志下载", e);
            logger.error("日志下载, 用户下载日志失败");
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
