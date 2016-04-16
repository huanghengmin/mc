package com.fartec.ichange.plugin.filechange.target.plugin.webdav;

import com.fartec.ichange.plugin.filechange.utils.FileBean;
import com.fartec.ichange.plugin.filechange.utils.FileContext;
import com.fartec.ichange.plugin.filechange.utils.FileList;
import com.googlecode.sardine.DavResource;
import com.googlecode.sardine.Sardine;
import com.googlecode.sardine.SardineFactory;
import com.inetec.common.config.nodes.TargetFile;
import org.apache.log4j.Logger;
import java.io.*;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: hhm
 * Date: 12-5-9
 * Time: ����6:32
 * To change this template use File | Settings | File Templates.
 */
public class WebdavTargetReceiveUtils {

    private WebDavUtils webDavUtils=new WebDavUtils();
    private Logger logger = Logger.getLogger(WebdavTargetReceiveUtils.class);
    private WebdavTargetTempFileUtils webdavTargetTempFileUtils = new WebdavTargetTempFileUtils();

    /**
     * ���������ļ���Ŀ���
     * @param fileBean   �ļ�����
     */
    public void putTempFileToTarget(FileBean fileBean,TargetFile targetFile){
        //����Ŀ��˲�������
        Sardine sardine= SardineFactory.begin(targetFile.getUserName(),targetFile.getPassword());
        //Ŀ����ļ�ȫ��
        String path= webDavUtils.getTargetHostAndName(targetFile);
        if(fileBean.getFullname().startsWith("/")){
            path += webDavUtils.urlEncoder(webDavUtils.judgeWorkDir(targetFile.getDir()) + fileBean.getFullname(),targetFile).replaceAll("\\+","%20");
        }else {
            path += webDavUtils.urlEncoder(webDavUtils.judgeWorkDir(targetFile.getDir()) + "/"+fileBean.getFullname(),targetFile).replaceAll("\\+","%20");
        }
        File file = webdavTargetTempFileUtils.getTempFile(fileBean);         //�õ���ʱ�ļ�
        if(file!=null){
            try{
                createTargetDirectory(sardine,fileBean, targetFile);//�ּ�����Ŀ¼
                try {
                    sardine.put(path, new FileInputStream(file));
                } catch (FileNotFoundException e) {
                    logger.info("***��ȡ������ʱ�ļ����ϴ�������������!!!!***");
                }
                //�ж��Ƿ񱣴�ɹ�
                if(existsTargetFile(targetFile,fileBean)){
                    file.delete();              //ɾ����ʱ�ļ�
                    webdavTargetTempFileUtils.deleteTempFileFolder(file.getAbsolutePath());
                    logger.info(Thread.currentThread().getName()+"*****�ļ�*****"+webDavUtils.getTargetHostAndName(targetFile) +webDavUtils.judgeWorkDir(targetFile.getDir())+fileBean.getFullname()+"::ͬ����ɣ�");
                }
            }catch (Exception e){
                logger.info(e.getMessage()+"***Ŀ��˷��������ܴ������Ӵ���!!!***");
            }
        }
    }

    /**
     *  �ϴ������ļ�����Ŀ��ˣ�
     * @param fileBean    �ļ�����
     * @param in            ������
     */
    public boolean putFile(FileBean fileBean,InputStream inputStream,TargetFile targetFile){
        boolean flag = false;
        if(fileBean.getFilepostlocation()==-2) {
            webdavTargetTempFileUtils.createNewNormalTempFile(fileBean);
        }else {
            if(fileBean.getFilepostlocation()==0){
                webdavTargetTempFileUtils.createNewNormalTempFile(fileBean);
            }
        }
        boolean exists=false;
        File tempFile= webdavTargetTempFileUtils.getTempFile(fileBean);    //�õ���ʱ�ļ�
        if(fileBean.getSyncflag().equals(FileContext.Str_SyncFileStart)){
            if(tempFile!=null)
                flag = webdavTargetTempFileUtils.createToTempFile(fileBean,inputStream) ;
            else
                return false;
            exists = webdavTargetTempFileUtils.existsTempFile(fileBean);  //�����Ƿ������ʱ�ļ�
            if(exists)
                logger.info(fileBean+"***��ʱ�ļ�����ɹ�!!!!!***");
            try {
                inputStream.close();
            } catch (IOException e) {
                logger.info("***�ر�����������!!!!***"+fileBean.getFullname());
            }
        }else {
            if(fileBean.getSyncflag().equals(FileContext.Str_SyncFileEnd))
                if(tempFile!=null)
                    flag = webdavTargetTempFileUtils.createToTempFile(fileBean,inputStream);
                else
                    return false;
            try {
                inputStream.close();
            } catch (IOException e) {
                logger.info("***�ر�����������!!!!***"+fileBean.getFullname()+e.getMessage());
            }
            exists = webdavTargetTempFileUtils.existsTempFile(fileBean);
            if(exists)
                logger.info(fileBean.getFullname()+"***��ʱ�ļ�����ɹ�!!!***");
        }
        if(exists){
            putTempFileToTarget(fileBean,targetFile);     //������ʱ�ļ�����Ŀ���
        }
        return flag;
    }

    /**
     * �������ɹ��ϴ���ʱ�ļ� !!!
     * @param fileBean       �ļ�����
     * @param targetFile         Ŀ��������ļ�
     */
    public void putSyncIngFileToTarget(FileBean fileBean,TargetFile targetFile){
        //Ŀ����ļ�ȫ��
        String path=webDavUtils.getFilePath(fileBean,targetFile);
        Sardine sardine= SardineFactory.begin(targetFile.getUserName(), targetFile.getPassword());
        File file = webdavTargetTempFileUtils.getTempFile(fileBean);
        if(file!=null){
            //�����ļ���
            createTargetDirectory(sardine,fileBean,targetFile);
            //���浽Ŀ���
            try {
                sardine.put(path, new FileInputStream(file));
            } catch (IOException e) {
                logger.info("***"+fileBean.getFullname()+"���浽Ŀ��˳���!!!****");
            }
            //��ѯĿ����ļ��Ƿ񱣴�ɹ�
            if(judgeNotFinishedFileBeanSaveSuccess(fileBean,targetFile)) {     //����δ����ļ��Ƿ����
                file.delete(); //ɾ����ʱ�ļ�
                webdavTargetTempFileUtils.deleteTempFileFolder(file.getAbsolutePath());             //ɾ����ʱ�ļ�Ŀ¼
                logger.info(Thread.currentThread().getName()+"*****�ļ�*****"+webDavUtils.getTargetHostAndName(targetFile) + webDavUtils.judgeWorkDir(targetFile.getDir())+fileBean.getFullname()+"::ͬ����ɣ�");
            }
        }
    }

    /**
     *   ����δ��ɵ��ļ�
     * @param fileBean  �ļ�����
     * @param targetFile    Ŀ�������ļ�
     * @param sourceFile     Դ�������ļ�
     */
    public boolean putSyncIngFile(InputStream inputStream,FileBean fileBean,TargetFile targetFile){
        boolean flag=false;
        //���浽Ŀ���
        try{
            boolean bool = false;  //������ʱ�ļ��Ƿ񱣴�ɹ�
            if(fileBean.getFilepostlocation() == 0)
                webdavTargetTempFileUtils.createNewNotFinishedFileTempFile(fileBean);        //����δ����ļ���ʱ�ļ�
            if(fileBean.getSyncflag().equals(FileContext.Str_SyncFileStart)){
                if(webdavTargetTempFileUtils.getTempFile(fileBean)!=null)
                    flag = webdavTargetTempFileUtils.createToTempFile(fileBean,inputStream) ;
                else
                    return false;
                bool = webdavTargetTempFileUtils.existsTempFile(fileBean);
                if(bool)
                    logger.info(fileBean+"��ʱ�ļ�����ɹ�");
                inputStream.close();
            }else {
                if(fileBean.getSyncflag().equals(FileContext.Str_SyncFileEnd))
                    if(webdavTargetTempFileUtils.getTempFile(fileBean)!=null)
                        flag = webdavTargetTempFileUtils.createToTempFile(fileBean,inputStream);
                    else
                        return false;
                inputStream.close();
                bool = webdavTargetTempFileUtils.existsTempFile(fileBean);
                if(bool)
                    logger.info(fileBean+"��ʱ�ļ�����ɹ�");
            }
            if(bool){
                putSyncIngFileToTarget(fileBean,targetFile);   //���浽Ŀ��˲�ɾ����ʱ�ļ�
            }
        }catch (Exception e){
            logger.info(e.getMessage()+"������ʱ�ļ�������");
        }  finally {
            try {
                if(inputStream!=null){
                    inputStream.close();
                }
            } catch (IOException e) {
                logger.info(e.getMessage()+"��ȡ��"+fileBean.getFullname()+"���������ļ����޸ģ�����");
            }
        }
        return flag;
    }

    /**
     * ת����ȥĿ��˲��� !
     * @param fileBean   �ļ�����
     * @return            ����δ����ļ��Ƿ񱣴�ɹ�
     */
    public boolean judgeNotFinishedFileBeanSaveSuccess(FileBean fileBean,TargetFile targetFile){
        String path = webDavUtils.getFilePath(fileBean,targetFile);
        //���ļ�ȫ����ת������ɺ���ļ���
        fileBean.setFullname(path.replace(webDavUtils.getTargetHostAndName(targetFile)+
                webDavUtils.urlDecoder(webDavUtils.judgeWorkDir(targetFile.getDir()),targetFile),""));
        //�����Ƿ񱣴�ɹ��ı�ʶ
        return  existsTargetFile(targetFile,fileBean);
    }

    /**
     * ��ѯĿ��˵��ļ��Ƿ����  �����ļ�������
     * @param targetFile           Ŀ��������ļ�
     * @param fileBean                 Դ��fileBean����
     * @return                              �ж�Ŀ����Ƿ���ڴ��ļ������ڷ���true�����ڷ���false
     */
    public boolean existsTargetFile(TargetFile targetFile,FileBean fileBean){
        boolean  flag=false;
        //ת�����ļ�ȫ��
        String targetFileName=null;
        if(fileBean.getFullname().startsWith("/")){
            targetFileName =  webDavUtils.getTargetHostAndName(targetFile) +
                    webDavUtils.urlEncoder(webDavUtils.judgeWorkDir(targetFile.getDir())+
                            fileBean.getFullname(),targetFile).replaceAll("\\+","%20");
        }else {
            targetFileName =  webDavUtils.getTargetHostAndName(targetFile) +
                    webDavUtils.urlEncoder(webDavUtils.judgeWorkDir(targetFile.getDir())+"/"+
                            fileBean.getFullname(),targetFile).replaceAll("\\+","%20");
        }
        Sardine sardine= SardineFactory.begin(targetFile.getUserName(), targetFile.getPassword());   //��������
        try {
            if(sardine.exists(targetFileName)){
                //�г��ļ�
                List<DavResource> davResources=sardine.list(targetFileName);
                Iterator<DavResource> iterator=davResources.iterator();
                DavResource resource=iterator.next();
                String href= URLDecoder.decode(resource.getHref().toString().replaceAll("\\+","%2B"),targetFile.getCharset());
                String fileFullName=null;
                if(!href.contains("http://"))  {
                    fileFullName=href.replaceFirst(webDavUtils.judgeWorkDir(targetFile.getDir()),"");
                } else {
                    fileFullName=href.replaceFirst(webDavUtils.getTargetHostAndName(targetFile)+webDavUtils.judgeWorkDir(targetFile.getDir()),"");
                }
                if((resource.getContentLength()==fileBean.getFilesize())&& (fileFullName.equals(fileBean.getFullname()))){
                    flag=true;
                }
            }
        }catch (IOException e) {
            //����������ļ����򷵻�false
            return flag;
        }
        return flag;
    }

    /**
     * �õ�Ŀ����ϴ���õ��ļ�  !
     * @param fileBean    �ļ�����
     * @return            ����Ŀ����Ƿ���ڴ��ļ�
     */
    public boolean existsIspfNormalFile(FileBean fileBean,TargetFile targetFile){
        //�ļ�����
        FileBean newFileBean=new FileBean();
        //ispf�ļ���׺�滻
        newFileBean.setFullname(webDavUtils.replaceEnd(fileBean.getFullname(),FileContext.Str_SyncFileSourceProcess_Flag));
        //�ļ���С����
        newFileBean.setFilesize(fileBean.getFilesize());
        //�ļ�������
        newFileBean.setName(fileBean.getName());
        //����Ŀ����Ƿ���ڴ��ļ�
        boolean  flag= existsTargetFile(targetFile,newFileBean);
        //�����Ƿ���ڱ�ʶ
        return flag;
    }

    /**
     * �õ�Ŀ���ipse�ļ�   ������ڷ���true
     * @param fileBean        �ļ�����
     * @return                 �ļ��Ƿ����
     */
    public boolean existsIspfIspeFile(FileBean fileBean,TargetFile targetFile){
        //�ļ�����
        FileBean newFileBean=new FileBean();
        //ispf�ļ���׺�滻Ϊispe
        newFileBean.setFullname(
                webDavUtils.replaceOtherSeparator(fileBean.getFullname(),FileContext.Str_SyncFileSourceProcess_Flag,
                        FileContext.Str_SyncFileSourceProcess_End_Flag)) ;
        //�ļ���С����
        newFileBean.setFilesize(fileBean.getFilesize());
        //�ļ�������
        newFileBean.setName(fileBean.getName());
        //����Ŀ����Ƿ���ڴ��ļ�
        boolean  flag= existsTargetFile(targetFile,newFileBean);
        //�����Ƿ���ڱ�ʶ
        return flag;
    }

    /**
     * ����Ŀ����Ѵ��ڵ�ispe�ļ�Ϊ�����ļ�
     * @param fileBean          Դ�˴�������ispf�ļ�
     * @param targetFile           Ŀ��������ļ�
     */
    public void renameIspfIspeFileToNormal(FileBean fileBean,TargetFile targetFile){
        String path= webDavUtils.getTargetHostAndName(targetFile);
        if(fileBean.getFullname().startsWith("/")){
            path += webDavUtils.urlEncoder((webDavUtils.judgeWorkDir(targetFile.getDir())+
                    webDavUtils.replaceOtherSeparator(fileBean.getFullname(),FileContext.Str_SyncFileSourceProcess_Flag,
                            FileContext.Str_SyncFileSourceProcess_End_Flag ))
                    ,targetFile).replaceAll("\\+","%20");
        } else {
            path += webDavUtils.urlEncoder((webDavUtils.judgeWorkDir(targetFile.getDir())+
                    "/"+ webDavUtils.replaceOtherSeparator(fileBean.getFullname(),FileContext.Str_SyncFileSourceProcess_Flag,
                    FileContext.Str_SyncFileSourceProcess_End_Flag )),targetFile).replaceAll("\\+","%20");
        }
        Sardine sardine=SardineFactory.begin(targetFile.getUserName(),targetFile.getPassword());
        try {
            sardine.move(path,webDavUtils.replaceEnd(path,FileContext.Str_SyncFileSourceProcess_End_Flag));
        } catch (IOException e) {
            logger.info("�ƶ�Ŀ����ļ�"+fileBean.getFullname()+"��������");
        }
    }

    /**
     * ���Ŀ���Ϊֻ����ʱ����
     * @param fileBean   �ļ�����
     * @param  targetFile Ŀ��������ļ�
     */
    public void onlyTargetAdd(FileBean fileBean,FileList resultFileList, TargetFile targetFile){
        String requestUrl= webDavUtils.getTargetHostAndName(targetFile)+
                webDavUtils.urlEncoder(webDavUtils.judgeWorkDir(targetFile.getDir())+fileBean.getFullname(),targetFile).replaceAll("\\+","%20");
        Sardine sardine=SardineFactory.begin(targetFile.getUserName(),targetFile.getPassword());
        boolean  flag = false;
        try {
            if(sardine.exists(requestUrl)){
                flag = true;
            }
        } catch (IOException e) {
        }
        if(!flag){
            resultFileList.addFileBean(fileBean);
        }
    }

    /**
     * ԭ����ֻ����ģʽ
     * @param fileBean �ļ�����
     * @param targetFile   Ŀ��������ļ�
     */
    public void originalTargetOnlyAdd(FileBean fileBean,TargetFile targetFile){
        String requestUrl= webDavUtils.getTargetHostAndName(targetFile)+
                webDavUtils.urlEncoder(webDavUtils.judgeWorkDir(targetFile.getDir())+fileBean.getFullname(),targetFile).replaceAll("\\+","%20");
        Date newDate=new Date();
        SimpleDateFormat sDateFormat=new SimpleDateFormat("yyyy_MM_dd HH_mm_ss"); //����ʱ��
        String newDateFormat=sDateFormat.format(newDate);
        Sardine sardine=SardineFactory.begin(targetFile.getUserName(),targetFile.getPassword());
        try {
            if(sardine.exists(requestUrl)){
                try {
                    sardine.move(requestUrl, webDavUtils.getTargetHostAndName(targetFile)+
                            webDavUtils.urlEncoder(webDavUtils.judgeWorkDir(targetFile.getDir())+
                                    webDavUtils.judgeWorkDir(webDavUtils.getFileDir(fileBean))+"/"+newDateFormat+fileBean.getName(),targetFile).replaceAll("\\+","%20"));
                }catch (IOException e) {
                    logger.info("ֻ����ģʽ��Ŀ����ļ�������");
                }
            }
        } catch (IOException e) {
        }
    }

    /**
     * ����Ŀ����ļ���
     * @param sardine   Ŀ��˲�������
     * @param fileBean    �ļ�����
     * @param targetFile    Ŀ��������ļ�
     */
    public  void createTargetDirectory(Sardine sardine,FileBean fileBean, TargetFile targetFile){
        //Դ�ļ���ȥ�����Ͷ˿ڹ����ռ���·��
        String targetDir= webDavUtils.getFileDir(fileBean);
        //�ֱ𴴽���Ŀ¼
        String dirMin=null;
        if(!targetDir.equals("")&&targetDir!=null) {
            if(targetDir.startsWith("/")){
                dirMin= webDavUtils.judgeWorkDir(targetFile.getDir())+targetDir;
            }else {
                dirMin= webDavUtils.judgeWorkDir(targetFile.getDir())+"/"+targetDir;
            }
        }else{
            dirMin= webDavUtils.judgeWorkDir(targetFile.getDir())+"";
        }
        String[] dir=dirMin.split("/");
        String requestDir= webDavUtils.getTargetHostAndName(targetFile)+"/";
        for(String d:dir){
            if(!d.equals("")&&d!=null){
                try {
                    requestDir+= webDavUtils.urlEncoder(d,targetFile)+"/";
                    //���༶Ŀ¼�����Զ�����
                    if(!sardine.exists(requestDir.replaceAll("\\+","%20"))) {
                        sardine.createDirectory(requestDir.replaceAll("\\+","%20"));
                    }
                } catch (IOException e) {
                    logger.info("**********Ŀ��˴���Ŀ¼���ɹ�!!!!**********");
                }
            }
        }
    }
}
