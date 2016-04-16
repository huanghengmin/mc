package com.hzih.mc.web.action;

import com.hzih.mc.utils.ManifestUtil;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.jar.Manifest;

/**
 * Created by IntelliJ IDEA.
 * User: Ǯ����
 * Date: 12-6-11
 * Time: ����1:57
 * To change this template use File | Settings | File Templates.
 */
public class LicenseAction extends ActionSupport{
    private static final Logger logger = Logger.getLogger(LicenseAction.class);

    public String about() throws Exception {
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase base = new ActionBase();
        String result =	base.actionBegin(request);
        String json = null;
        try {
            Manifest manifest = ManifestUtil.getManifest(ServletContext.class);
            String product = "MC";//��Ʒ��ʶ;
            String version = ManifestUtil.getValue(manifest,"Manifest-Version");        //�汾
            String os = System.getProperty("os.name")+" "+System.getProperty("os.version");//����ϵͳ
            json = "{success:true,total:1,rows:[{os:'"+os+"',product:'"+product+"',version:'"+version+"'}]}";
            logger.info("ƽ̨˵��, �û���ȡƽ̨˵����Ϣ�ɹ�");
        } catch (Exception e) {
            logger.error("ƽ̨˵��", e);
            logger.error("ƽ̨˵��, �û���ȡƽ̨˵����Ϣʧ��");
        }
        base.actionEnd(response, json ,result);
        return null;
    }

}
