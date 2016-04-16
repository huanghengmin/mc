package com.hzih.mc.web.action;

import com.hzih.mc.utils.Configuration;
import com.hzih.mc.utils.StringContext;
import com.inetec.common.exception.Ex;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.springframework.web.bind.ServletRequestUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Ǯ����
 * Date: 12-5-18
 * Time: ����3:54
 * ���ù���
 */
public class ConfigManagerAction  extends ActionSupport {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(ConfigManagerAction.class);

//    private static final String path = "E:/server.xml";

    /**
     * ��ȡ������񡢼��زɼ����ݽӿ��趨IP��ַ����json1
     *
     */
    public String readIps() throws Exception {
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String result = actionBase.actionBegin(request);
        String json = "{'success':true,'total':0,'rows':[]}";
        try {
            json = read(StringContext.tomcatPathServer);
            logger.info( "���ù��� ,�û���ȡ������񡢼��زɼ����ݽӿ��趨IP��ַ�ɹ� ");
        } catch (Exception e) {
            logger.error("���ù���", e);
            logger.error("���ù��� ,�û���ȡ������񡢼��زɼ����ݽӿ��趨IP��ַ���ɹ� ");
        }
        actionBase.actionEnd(response, json, result);
        return null;
    }

    /**
     * ��ȡ����ͻ�����ַ����json
     *
     */
    public String select() throws Exception {
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String result =	actionBase.actionBegin(request);
        String json = "{'success':true,'total':0,'ip1':'','ip2':'','rows':[]}";
        try {
            String startStr = ServletRequestUtils.getStringParameter(request,"start");
            String limitStr = ServletRequestUtils.getStringParameter(request,"limit");
            Integer start = Integer.decode(startStr);
            Integer limit = Integer.decode(limitStr);
            json = read(StringContext.tomcatPathServer,start,limit);
            logger.info( "���ù��� ,�û���ȡ����ͻ�����ַ�ɹ� ");
        } catch (Exception e) {
            logger.error("���ù���", e);
            logger.error("���ù��� ,�û���ȡ����ͻ�����ַ���ɹ� ");
        }
        actionBase.actionEnd(response, json, result);
        return null;
    }



    /**
     * ɾ�� ����ͻ�����ַ��ע�����Զ��Ϊ�ͻ�����ַ��
     *
     */
    public String delete() throws Exception {
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String result =	actionBase.actionBegin(request);
        String json = null;
        String msg = null;
        try {
            String[] ip = ServletRequestUtils.getStringParameters(request, "ipArray");
            msg = delete(StringContext.tomcatPathServer, ip);
            logger.info( "���ù��� ,�û�ɾ������ͻ�����ַ�ɹ� ");
        } catch (Exception e) {
            logger.error("���ù���", e);
            logger.error( "���ù���,�û�ɾ������ͻ�����ַ���ɹ� ");
            msg = "ɾ������ͻ�����ַʧ��";
        }
        json = "{success:true,msg:'"+msg+"'}";
        actionBase.actionEnd(response, json, result);
        return null;
    }
    /**
     * ���� ����ͻ�����ַ��ע�����Զ��Ϊ�ͻ�����ַ��
     *
     */
    public String insert() throws Exception {
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String result =	actionBase.actionBegin(request);
        String json = null;
        String msg = null;
        try {
            String[] ip = ServletRequestUtils.getStringParameters(request, "ipArray");
            msg = insert(StringContext.tomcatPathServer, ip);
            logger.info( "���ù��� ,�û���������ͻ�����ַ�ɹ� ");
        } catch (Exception e) {
            logger.error("���ù���", e);
            logger.error("���ù��� ,�û���������ͻ�����ַ���ɹ� ");
            msg = "��������ͻ�����ַʧ��";
        }
        json = "{success:true,msg:'"+msg+"'}";
        actionBase.actionEnd(response, json, result);
        return null;
    }

    /**
     * ���¶˿���8443�ķ���ip--�������ӿ��趨IP��ַ
     *
     */
    public String update8443() throws Exception {
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String result =	actionBase.actionBegin(request);
        String json = null;
        String msg = null;
        try {
            String ip = ServletRequestUtils.getStringParameter(request, "ip");
            msg = update(StringContext.tomcatPathServer, ip,8443);
            logger.info( "���ù��� ,�û����¹������ӿ��趨IP��ַ�ɹ� ");
        } catch (Exception e) {
            logger.error("���ù���", e);
            logger.error( "���ù���,�û����¹������ӿ��趨IP��ַ���ɹ� ");
            msg = "���¹������ӿ��趨IP��ַʧ��";
        }
        json = "{success:true,msg:'"+msg+"'}";
        actionBase.actionEnd(response, json, result);
        return null;
    }

    /**
     * ���¶˿���8000�ķ���ip--���زɼ����ݽӿ��趨IP��ַ
     *
     */
    public String update8000() throws Exception {
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String result =	actionBase.actionBegin(request);
        String json = null;
        String msg = null;
        try {
            String ip = ServletRequestUtils.getStringParameter(request, "ip");
            msg = update(StringContext.tomcatPathServer,ip, 8000);
            logger.info( "���ù��� ,�û����¼��زɼ����ݽӿ��趨IP��ַ�ɹ� ");
        } catch (Exception e) {
            logger.error("���ù���", e);
            logger.error("���ù���,�û����¼��زɼ����ݽӿ��趨IP��ַ���ɹ� ");
            msg = "���¼��زɼ����ݽӿ��趨IP��ַʧ��";
        }
        json = "{success:true,msg:'"+msg+"'}";
        actionBase.actionEnd(response, json, result);
        return null;
    }

    private String update(String path, String ip, int port) {
        String result = null;
        try {
            Configuration config = new Configuration(path);
            result = config.editConnectorIp(ip,""+port);
        } catch (Ex ex) {
            result = ex.getMessage();
        }
        return result;
    }

    private String insert(String path, String[] ips) {
        String result = null;
        try {
            Configuration config = new Configuration(path);
            List<String> list = config.getAllowIPs();
            String ip = "|";
            for (int i = 0 ; i < ips.length ; i ++){
                boolean isExist = check(ips[i],list);
                if(!isExist){
                    ip += ips[i];
                }
            }
            result = config.editAllowIp( ip);
        } catch (Ex ex) {
            result = ex.getMessage();
        }
        return result;
    }

    private String delete(String path, String[] ips) {
        String result = null;
        try {
            Configuration config = new Configuration(path);
            List<String> temp = new ArrayList<String>();
            List<String> list = config.getAllowIPs();
            for (int i = 0 ; i < ips.length ; i ++){
                boolean isExist = check(ips[i],list);
                if(isExist){
                    temp.add(ips[i]);
                }else {
                    logger.warn(ips[i] + "�Ѿ�ɾ���򲻴���");
                }
            }
            list.remove(temp);
            String ip = list.get(0);
            for (int i = 1 ; i < list.size() ; i ++){
                ip += "|"+list.get(i);
            }
            result = config.deleteAllowIp( ip);
        } catch (Ex ex) {
            result = ex.getMessage();
        }
        return result;
    }

    private boolean check(String ip, List<String> list) {
        for (String str : list){
            if(str.equals(ip)){
                return true;
            }
        }
        return false;
    }

    private String read(String path, Integer start, Integer limit) {
        String json = "{success:true,total:0,rows:[]}";
        try {
            Configuration config = new Configuration(path);
            List<String> list = config.getAllowIPs();
            json = "{success:true,total:"+(list.size())+",rows:[";
            int index = 0;
            int count = 0;
            for (String str : list){
                if(index == start && count < limit){
                    json +="{ip:'"+str+"'},";
                    count ++;
                    start ++;
                }
                index ++;
            }
            json += "]}";
        } catch (Ex ex) {
            logger.error(ex.getMessage());
        }
        return json;
    }

    private String read(String path) {
        String json = "{'success':true,'total':0,'rows':[]}";
        try {
            Configuration config = new Configuration(path);
            String ip1 = config.getConnectorIp(""+8443);
            String ip2 = config.getConnectorIp(""+8000);
            json = "{'success':true,'total':1,'rows':[{'ip1':'"+ip1+"','ip2':'"+ip2+"'}]}";
        } catch (Ex ex) {
            logger.error(ex.getMessage());
        }
        return json;
    }
}
