package com.hzih.mc.web.action;

import com.hzih.mc.entity.JarBean;
import com.hzih.mc.utils.FileUtil;
import com.hzih.mc.utils.StringContext;
import com.inetec.common.util.OSInfo;
import com.inetec.common.util.Proc;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.springframework.web.bind.ServletRequestUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by IntelliJ IDEA.
 * User: Ǯ����
 * Date: 12-6-11
 * Time: ����1:57
 * To change this template use File | Settings | File Templates.
 */
public class UpgradeAction extends ActionSupport{
    private static final Logger logger = Logger.getLogger(UpgradeAction.class);
    private File uploadFile;
    private String uploadFileFileName;
    private String uploadFileContentType;
    private String upgradeTime;
    /**
     * ��ȡ ������������б�
     * */
    public String selectWar() throws IOException {
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String result =	actionBase.actionBegin(request);
        String json = "{success:true,total:0,rows:[]}";
        try {
            int start = ServletRequestUtils.getIntParameter(request, "start");
            int limit = ServletRequestUtils.getIntParameter(request, "limit");
            json = selectWars(StringContext.webPath,start,limit);
            logger.info("�汾����,�û���ȡ������������б�ɹ� ");
        } catch (Exception e) {
            logger.error("�汾����", e);
            logger.error("�汾����,�û���ȡ������������б��ɹ� ");
        }
        actionBase.actionEnd(response, json, result);
        return null;
    }

    /**
     *
     * @param path     war����tomcat�µ�·��
     * @param start    ��ҳ��ʼҳ
     * @param limit    ��ҳ��С
     * @return
     * @throws Exception
     */
    private String selectWars(String path, int start, int limit) throws Exception {
        File webapps = new File(path);
        File[] wars = webapps.listFiles(new FilenameFilter() {
            public boolean accept(File file, String s) {
                if (s.endsWith(".war")) {
                    return true;
                }
                return false;
            }
        });
        int total = wars.length;
        String json = "{'success':true,'total':"+total+",'rows':[";
        if(total==0){
            json+=",";
        }if(total > 0){
            int index = 0;
            for (int i = 0;i<total;i ++) {
                if(i == start && i < limit){
                    start ++;
                    index ++;
                    File f = wars[i];
                    long modifiedTime = f.lastModified();
                    Date date=new Date(modifiedTime);
                    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String upgradeTime=sdf.format(date);
                    ZipFile file = new ZipFile(f);
                    ZipEntry entry = file.getEntry("META-INF/version.properties");
                    String warName = f.getName().substring(0,f.getName().lastIndexOf('.'));
                    boolean isExistOld = isExistOld(webapps,warName);
                    String version = null;
                    String buildDate = null;
                    String newVersion = null;
                    if(entry!=null){
                        InputStream in = file.getInputStream(entry);                       //"/META-INF/version.properties"
                        Properties config = new Properties();
                        config.load(in);
                        version = config.getProperty("version");
                        buildDate = config.getProperty("builddate");
                        in.close();
                    }else {
                        version = "�Ͱ汾,û�а汾˵��";
                        buildDate = "�Ͱ汾,û�а汾˵��";
                    }
                    newVersion = getNewVersion(path,f.getName());
                    file.close();
                    json += "{warName:'"+warName+"',upgradeTime:'"+upgradeTime+
                            "',warVersion:'"+version+"',buildDate:'"+buildDate+
                            "',newVersion:'"+newVersion+"',flag:"+isExistOld+"},";
                }
            }
        }
        json += "]}";
        return json;
    }

    private String getNewVersion(String path, String name) throws IOException {
        File f = new File(path + "/" + name + "_tmp");
        String version = null;
        if(f.exists()){
            ZipFile file = new ZipFile(f);
            ZipEntry entry = file.getEntry("META-INF/version.properties");
            if(entry!=null){
                InputStream in = file.getInputStream(entry);                       //"/META-INF/version.properties"
                Properties config = new Properties();
                config.load(in);
                version = config.getProperty("version");
                in.close();
            }else {
                version = "�Ͱ汾,û�а汾˵��";
            }
        }else {
            version = "û���ϴ������汾";
        }
        return version;
    }

    private boolean isExistOld(File webapps, String warName) {
        File[] warOlds = webapps.listFiles(new FilenameFilter() {
            public boolean accept(File file, String s) {
                if (s.endsWith(".war_old")) {
                    return true;
                }
                return false;
            }
        });
        if(warOlds.length>0){
            for (int i = 0;i<warOlds.length;i++){
                if(warOlds[i].getName().equals(warName+".war_old")){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * ��ȡ ����Jar������б���
     * */
    public String selectJar() throws IOException {
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String result =	actionBase.actionBegin(request);
        String json = "{success:true,total:0,rows:[]}";
        try {
            int start = ServletRequestUtils.getIntParameter(request, "start");
            int limit = ServletRequestUtils.getIntParameter(request, "limit");
            json = selectJars(StringContext.webPath, start, limit);
            logger.info("�汾����,�û���ȡ����Jar������б����ɹ� ");
        } catch (Exception e) {
            logger.error("�汾����", e);
            logger.error("�汾����,�û���ȡ����Jar��������б��ɹ� ");
        }
        actionBase.actionEnd(response, json, result);
        return null;
    }

    private String selectJars(String path, int start, int limit) throws IOException {
        File webapps = new File(path);
        File[] wars = webapps.listFiles(new FilenameFilter() {
            public boolean accept(File file, String s) {
                if (s.endsWith(".war")) {
                    return true;
                }
                return false;
            }
        });
        List<JarBean> jarBeans = new ArrayList<JarBean>();
        for (int i = 0;i<wars.length;i ++) {
            File f = wars[i];
            ZipFile file = new ZipFile(f);
            ZipEntry entry = file.getEntry("META-INF/version.properties");
            String jar = null;
            if(entry!=null){
                InputStream in = file.getInputStream(entry);                       //"/META-INF/version.properties"
                Properties config = new Properties();
                config.load(in);
                jar = config.getProperty("jar");
                in.close();
                jarBeans = toJarBeanList(wars[i].getName(),jar,jarBeans);
            }
            file.close();
        }
        int total = jarBeans.size();
        String json = "{'success':true,'total':"+total+",'rows':[";
        if(total==0){
            json+=",";
        }if(total > 0){
            int index = 0;
            for (int i = 0;i<total;i ++) {
                if(i == start && i < limit){
                    start ++;
                    index ++;
                    json += "{jarName:'" + jarBeans.get(i).getJarName() +
                            "',warName:'" + jarBeans.get(i).getWarName() +
                            "',jarVersion:'" + jarBeans.get(i).getJarVersion() + "'},";
                }
            }
        }
        json +="]}";
        return json;
    }

    private List<JarBean> toJarBeanList(String warName, String jar, List<JarBean> jarBeans) {
        String[] jars = jar.split(",");
        for (int i = 0;i<jars.length;i ++){
            JarBean jarBean = new JarBean();
            jarBean.setWarName(warName);
            jarBean.setJarName(jars[i].split("-")[0]);
            jarBean.setJarVersion(jars[i].split("-")[1]);
            jarBeans.add(jarBean);
        }
        return jarBeans;
    }

    /**
     * �ϴ��ļ�*.war
     */
    public String uploadWar() throws IOException {

        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase base = new ActionBase();
        String result =	base.actionBegin(request);
        String msg = null;
        try {
            if(uploadFileFileName.endsWith(".war")){
                FileUtil.upload(StringContext.webPath, uploadFile, uploadFileFileName+"_tmp");
                msg = "�ϴ��ɹ�";
            }else{
                msg = "�ϴ����ļ�����[*.war]�ļ�";
            }
        } catch (Exception e) {
            logger.error("�汾����", e);
            logger.error("�汾����,�û��ϴ���Ҫ���µ�.WAR�ļ����ɹ� ");
            msg = "�ϴ�ʧ��"+e.getMessage();
        }
        String json = "{success:true,msg:'"+msg+"'}";

        base.actionEnd(response, json, result);
        return null;
    }

    /**
     * ����
     */
    public String upgrade() throws IOException {
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String result =	actionBase.actionBegin(request);
        String msg = null;
        try {
            File dir = new File(StringContext.webPath);
            boolean isOk = checkTime(dir); //true,��������
            if(isOk){
                File[] files = dir.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File file, String s) {
                        if (s.endsWith(".war_tmp")) {
                            return true;
                        }
                        return false;
                    }
                });
                if(files.length>0){
                    renameWarFiles(StringContext.webPath,files);
                    renameWarTmpFiles(files);
                    upgrade("mc");
                    if(msg==null){
                        logger.info("�汾����,�û������ɹ� ");
                        msg = "�����ɹ�";
                    }
                }else {
                    logger.info("�汾����,�û�����ʧ��,û����Ҫ�����ķ���");
                    msg = "û����Ҫ�����ķ���";
                }
            } else {
                logger.info("�汾����,�û�����ʧ��,��������Ƶ��:10�����ڶ������");
                msg = "��������Ƶ��,����"+upgradeTime+"������!";
            }
        } catch (Exception e) {
            logger.error("�汾����", e);
            logger.error("�汾����,�û��������ɹ� ");
            msg = "����ʧ��"+e.getMessage();
        }
        String json = "{success:true,msg:'"+msg+"'}";

        actionBase.actionEnd(response, json, result);
        return null;
    }

    private boolean checkTime(File dir) {
        File[] files = dir.listFiles(new FilenameFilter() {
            public boolean accept(File file, String s) {
                if(s.endsWith(".war")){
                    return true;
                }
                return false;
            }
        });
        long[] times = new long[files.length];
        for (int i=0;i<files.length;i++ ){
            long time = files[i].lastModified();
            times[i] = System.currentTimeMillis()-time;
        }
        Arrays.sort(times);
        long time = times[0];
        if(time < 10 * 60 * 1000){
            long m = time/1000/60;
            long s = time/1000 - m*60;
            upgradeTime = ( 10 - m ) +"��"+ ( 60 - s ) + "��";
            return false;
        }
        return true;
    }
    /**
     * ��*.war��Ϊ*.war_old;
     * @param path
     */
    private void renameWarFiles(String path,File[] files) {
        for(int i=0;i<files.length;i++){
            File tmpFile = files[i];
            File newFile = new File(path+"/" + tmpFile.getName().split("_tmp")[0] + "_old");
            // �ı�*.war ��Ϊ*.war_old
            if(newFile.exists()){
                newFile.delete();
            }
            File oldFile = new File(path+"/" + tmpFile.getName().split("_tmp")[0]);
            oldFile.renameTo(newFile);
        }
    }

    /**
     * ��*.war_tmpΪ*.war
     * @param files  *.war_tmp
     */
    private void renameWarTmpFiles(File[] files) {
        for(int i=0;i<files.length;i++){
            File tmpFile = files[i];
            File newFile = new File(StringContext.webPath+"/"+tmpFile.getName().split("_tmp")[0]);//*.war
            // �ı�*.war_tmp ��Ϊ*.war
            if(newFile.exists()){
                newFile.delete();
            }
            tmpFile.renameTo(newFile);
        }
    }

    private void upgrade(String service) {
        Proc proc;
        OSInfo osinfo = OSInfo.getOSInfo();
        if (osinfo.isWin()) {
            proc = new Proc();
            proc.exec("nircmd service upgrade "+service);
        }
        if (osinfo.isLinux()) {
            proc = new Proc();
            proc.exec("service "+service+" upgrade");
        }
    }

    /**
     * �汾����
     */
    public String backup() throws IOException {
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String result =	actionBase.actionBegin(request);
        String msg = null;
        try {
            String warName = ServletRequestUtils.getStringParameter(request,"warName");
            File oldFile = new File(StringContext.webPath+"/"+warName+"_old");
            File tmpFile = new File(StringContext.webPath+"/"+warName+"_tmp");
            if(tmpFile.exists()){
                tmpFile.delete();
            }
            oldFile.renameTo(tmpFile);
            if(msg==null){
                logger.info("�汾����,�û�������һ���汾�ɹ� ");
                msg = "������һ���汾�ɹ�";
            }
        } catch (Exception e) {
            logger.error("�汾����", e);
            logger.error("�汾����,�û�������һ���汾���ɹ� ");
            msg = "������һ���汾ʧ��"+e.getMessage();
        }
        String json = "{success:true,msg:'"+msg+"'}";

        actionBase.actionEnd(response, json, result);
        return null;
    }

    public File getUploadFile() {
        return uploadFile;
    }

    public void setUploadFile(File uploadFile) {
        this.uploadFile = uploadFile;
    }

    public String getUploadFileFileName() {
        return uploadFileFileName;
    }

    public void setUploadFileFileName(String uploadFileFileName) {
        this.uploadFileFileName = uploadFileFileName;
    }

    public String getUploadFileContentType() {
        return uploadFileContentType;
    }

    public void setUploadFileContentType(String uploadFileContentType) {
        this.uploadFileContentType = uploadFileContentType;
    }
}
