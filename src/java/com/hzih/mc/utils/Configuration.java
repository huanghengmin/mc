package com.hzih.mc.utils;

import com.inetec.common.exception.Ex;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Configuration {
    static Logger logger = Logger.getLogger(Configuration.class);

    private Document document;
    public String confPath;

    public Configuration(Document doc) {
        this.document = doc;
    }

    public Configuration(String path) throws Ex {
        this.confPath = path;
        SAXReader saxReader = new SAXReader();
        try {
            document = saxReader.read(path);
        } catch (DocumentException e) {
            e.printStackTrace();
            logger.info(e.getMessage());
        }
    }

    public Configuration(InputStream is, String path) throws Ex {
        this.confPath = path;
        SAXReader saxReader = new SAXReader();
        try {
            document = saxReader.read(is);
        } catch (DocumentException e) {
            logger.info(e.getMessage());
        }
    }

    public String editConnectorIp(String ip, String port) {
        try{
            Element connector = (Element) document.selectSingleNode("/Server/Service/Connector[@port=" + port + "]");
            if(connector != null){
                connector.attribute("address").setText(ip);
                String result = save();
                if(result.equals("保存成功")){
                    if(port.equals(""+8443)){
                        return "更新管理服务接口设定IP地址成功";
                    }else if(port.equals(""+8000)){
                        return "更新集控采集数据接口设定IP地址成功";
                    }else{
                        return "更新成功,端口是"+port;
                    }
                }else{
                    return result;
                }
            }
        } catch (Exception e){
            logger.info(e.getMessage());
        }
        return "更新出错";
    }

    public String getConnectorIp(String port) {
        String ip = "";
        try{
            Element connector = (Element) document.selectSingleNode("/Server/Service/Connector[@port=" + port + "]");
            if(connector != null){
                ip = connector.attribute("address").getText();
            }
        } catch (Exception e){
            logger.info(e.getMessage());
        }
        return ip;
    }

    public List<String> getAllowIPs(){
        List<String> allowIps = new ArrayList<String>();
        try{
            Element valve = (Element) document.selectSingleNode("/Server/Service/Engine/Valve");
            if(valve!=null){
                String ip = valve.attribute("allow").getText();
                String[] ips = ip.split("\\|");
                if(ips.length>1){
                    for (int i = 0; i < ips.length; i ++){
                        allowIps.add(ips[i]);
                    }
                }else{
                    allowIps.add(ip);
                }
            }
        } catch (Exception e){
            logger.info(e.getMessage());
        }
        return allowIps;
    }

    public String editAllowIp(String ip) {
        try{
            Element value = (Element) document.selectSingleNode("/Server/Service/Engine/Valve");
            if(value!=null){
                ip = value.attribute("allow").getText() + ip;
                value.attribute("allow").setText(ip);
                String result = save();
                if(result.equals("保存成功")){
                    return "更新管理客户机地址成功";
                }else{
                    return result;
                }
            }
        } catch (Exception e){
            logger.info(e.getMessage());
        }
        return "更新出错";
    }
    public String deleteAllowIp(String ip) {
        try{
            Element value = (Element) document.selectSingleNode("/Server/Service/Engine/Valve");
            if(value!=null){
                value.attribute("allow").setText(ip);
                String result = save();
                if(result.equals("保存成功")){
                    return "删除管理客户机地址成功";
                }else{
                    return result;
                }
            }
        } catch (Exception e){
            logger.info(e.getMessage());
        }
        return "删除出错";
    }

    public String save() {
        String result = null;
        XMLWriter output = null;
        try {
            File file = new File(confPath);
            FileInputStream fin = new FileInputStream(file);
            byte[] bytes = new byte[fin.available()];
            while (fin.read(bytes) < 0) fin.close();
            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setEncoding("UTF-8");
            output = new XMLWriter(new FileOutputStream(file),format);
            if(document != null){
                output.write(document);
                return result = "保存成功";
            }else{
                result = "dom4j处理出错";
            }
        } catch (FileNotFoundException e) {
            result = e.getMessage();
        } catch (IOException e) {
            result = e.getMessage();
        } finally {
            try {
                if (output != null)
                    output.close();
            } catch (IOException e) {
                result = e.getMessage();
            }
        }
        return "保存失败,"+result;
    }


}