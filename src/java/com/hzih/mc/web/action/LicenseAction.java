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
 * User: 钱晓盼
 * Date: 12-6-11
 * Time: 下午1:57
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
            String product = "MC";//产品标识;
            String version = ManifestUtil.getValue(manifest,"Manifest-Version");        //版本
            String os = System.getProperty("os.name")+" "+System.getProperty("os.version");//操作系统
            json = "{success:true,total:1,rows:[{os:'"+os+"',product:'"+product+"',version:'"+version+"'}]}";
            logger.info("平台说明, 用户获取平台说明信息成功");
        } catch (Exception e) {
            logger.error("平台说明", e);
            logger.error("平台说明, 用户获取平台说明信息失败");
        }
        base.actionEnd(response, json ,result);
        return null;
    }

}
