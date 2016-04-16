package com.fartec.ichange.plugin.filechange.target.plugin;


import com.fartec.ichange.plugin.filechange.target.TargetOperation;
import com.fartec.ichange.plugin.filechange.utils.*;
import com.inetec.common.config.nodes.TargetFile;
import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;

//import com.inetec.common.io.IOUtils;

/**
 * Created by IntelliJ IDEA.
 * User: bluesky
 * Date: 11-11-27
 * Time: ����6:32
 * To change this template use File | Settings | File Templates.
 */
public class TargetProcessFtp implements ITargetProcess {
    private static final Logger logger=Logger.getLogger(TargetProcessFtp.class);
    private TargetOperation targetOperation;
    private TargetFile targetFile;
    private int size=0;
    private FTPClient ftpClientTag;
    private OutputStream ftpOut=null;
    private int MB= (1024*1024);
    ExecutorService pools = null;

    /**
     * �����ļ���
     * @param ftpClientTag
     * @param dir      ��Ҫ�������ļ���·��
     * @return
     */

    public boolean makeNewDir(FTPClient ftpClientTag,String dir){
        String dirss="";
        boolean bool=false;
        String [] strs=dir.split("/");
        if (strs.length!=0){
            for(String str :strs){
                if("".equals(str)||str==null){
                    continue;
                }else {
                    dirss=dirss+"/"+str;
                    try {
                        bool=ftpClientTag.makeDirectory(dirss);//�����ļ���
                    } catch (IOException e) {
                        logger.error(e);
                        closeConnectTag();
                    }
                }
            }
        }
        return bool;
    }

    /**
     * ����Դ���ļ��б�
     * @param list �ļ��б����
     * @return ����Ҫͬ�����ļ��б�
     */
    public  FileList procesFileList(FileList list)  {
        ftpClientTag=null;
        FileList fileList=new FileList();
        if(connectServerTag()){
            try {
                for (Iterator i=list.iterable();i.hasNext();) {
                    FileBean fileBean= (FileBean) i.next();
                    if (FileFilter.existSubString(fileBean.getFullname(), "/")) {
                        String str = FileFilter.getFirstNewSubString(fileBean.getFullname(),fileBean.getName());
                        String strs="";
                        if (targetFile.getDir().endsWith("/")){
                            strs=targetFile.getDir()+str;
                        }else {
                            strs=targetFile.getDir()+"/"+str;
                        }
                        makeNewDir(ftpClientTag,strs);
                    }
                    if(fileBean.getFullname().endsWith(FileContext.Str_SyncFileSourceProcess_Flag)){   //�ж��ļ�����
                        FileBean fb=  interruptFile(fileBean);
                        if(fb!=null){
                            fileList.addFileBean(fb);
                        }
                    }
                    if(!fileBean.getFullname().endsWith(FileContext.Str_SyncFileSourceProcess_Flag)){   // //��ͨ�ļ�����
                        FileBean fb=  ordinaryFile(fileBean);
                        if(fb!=null){
                            fileList.addFileBean(fb);
                        }
                    }
                }
                if(targetFile.isDeletefile()){
                    FileFtp.ftpJdomFile(list);   //Ŀ��˽���Դ��ͬ���ļ���xml�ļ�
                }
            } catch (IOException e) {
                logger.error(e);
                closeConnectTag();
            }
            return fileList;
        }
        return fileList;
    }
    /**
     * �ж��ļ�����
     */
    public FileBean interruptFile(FileBean fileBean) throws IOException {
        String dir="";
        if (targetFile.getDir().endsWith("/")){
            dir=targetFile.getDir();
        }else {
            dir=targetFile.getDir()+"/";
        }
        String str = fileBean.getFullname().replace(FileContext.Str_SyncFileSourceProcess_Flag,FileContext.Str_SyncFileSourceProcess_End_Flag);
        String strd=str.replace(FileContext.Str_SyncFileSourceProcess_End_Flag,"").trim();
        FTPFile file=ftpClientTag.mlistFile(dir+str);
        FTPFile files=ftpClientTag.mlistFile(dir+strd);
        if(file!=null&&file.getSize()==fileBean.getFilesize()){
//                        logger.info("�ļ��Ѿ����ֻ꣬�����");
            ftpClientTag.rename(str,strd);
            return null;
        }
        if(file!=null&&file.getSize()<fileBean.getFilesize()){
//                        logger.info("�ļ�û�д��꣬�ϵ�����������");
            FileBean fb=new FileBean();
            fb.setFilesize(file.getSize());
            fb.setFullname(fileBean.getFullname());
            fb.setName(file.getName());
//            fileList.addFileBean(fb);
            return  fb;
        }
        if(file==null){
            if(files==null){
//              logger.info("Ŀ���û���ļ�");
//                fileList.addFileBean(fileBean);
                return  fileBean;
            }else {
                if(files.getSize()==fileBean.getFilesize()){
                    ftpClientTag.rename(str,strd);
                    return null;
                }
            }
        }
        return null;
    }
    /**
     * ��ͨ�ļ�����
     */
    public FileBean ordinaryFile (FileBean fileBean) {
        FTPFile ftpfile = null;     //�Ƿ�Ŀ��˴����ļ�
        String strs="";
        if (targetFile.getDir().endsWith("/")){
            strs=targetFile.getDir()+fileBean.getFullname();
        }else {
            strs=targetFile.getDir()+"/"+fileBean.getFullname();
        }
        try {
//            logger.info("targetFile.getDir()+/+fileBean.getFullname()== "+strs);
            logger.debug("targetFile.getDir()+/+fileBean.getFullname()+FileContext.Str_SyncFileSourceProcess_End_Flag== "+strs+FileContext.Str_SyncFileSourceProcess_End_Flag);
            ftpfile = ftpClientTag.mlistFile(strs);
        } catch (IOException e) {
            logger.error("ftpfile:=:"+ftpfile ,e);
        }
        if(ftpfile!= null){
            long ss=ftpfile.getSize();
            if(ss!=fileBean.getFilesize()){ //�ļ����ڵ��Ǵ�С��ͬ
                if(targetFile.isOnlyadd()){     //�Ƿ���ֻ����
                    return null;
                }else {
                    return fileBean;
                }
            }
        }else {
            return fileBean;
        }
        return null;
    }

    public void run() {
    }


    /**
     * ���ӷ�����
     * @return   flag
     */
    public  boolean connectServerTag() {
        boolean flag = true;
        if (ftpClientTag == null) {
            int reply;
            try {
                ftpClientTag = new FTPClient();
                ftpClientTag.setControlEncoding(targetFile.getCharset());
                ftpClientTag.setDefaultPort(targetFile.getPort());
                ftpClientTag.setDataTimeout(60000);       //���ô��䳬ʱʱ��Ϊ60��
                ftpClientTag.setConnectTimeout(60000);       //���ӳ�ʱΪ60��
                try{
                    ftpClientTag.connect(targetFile.getServerAddress());
                }catch (IOException e){
                    flag=false;
                    logger.error("[FTP]Ŀ��������쳣"+targetFile.getServerAddress()+"   "+e.getMessage());
                }
                int i=0;
                while (!ftpClientTag.isConnected()&&i<5){
                    i++;
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        logger.error(e.getMessage());
                    }
                    ftpClientTag=null;
                    connectServerTag();

                }
                ftpClientTag.setKeepAlive(true);
                ftpClientTag.setRemoteVerificationEnabled(true);
                ftpClientTag.login(targetFile.getUserName(), targetFile.getPassword());
                ftpClientTag.setFileType(FTP.BINARY_FILE_TYPE);
//                ���ñ���ģʽ
                ftpClientTag.enterLocalPassiveMode();
                reply = ftpClientTag.getReplyCode();
                if (!FTPReply.isPositiveCompletion(reply)) {
                    ftpClientTag.disconnect();
                    logger.error("[FTP]����Ŀ���FTP������"+targetFile.getServerAddress()+"�ܾ�.");
                    flag = false;
                }
                if(targetFile.getDir()!=null){
                    //todo ����   targetFile.getDir()�ļ���
                    makeNewDir(ftpClientTag,targetFile.getDir());
                }else {
                    logger.warn("[FTP]Ŀ��˲����ڸ�Ŀ¼���ԡ�/��Ϊ��Ŀ¼��������");
                    try {
                        ftpClientTag.changeWorkingDirectory("/");
                    } catch (IOException e) {
                        logger.error(e);
                    }
                }
            } catch (SocketException e) {
                flag = false;
                logger.error("[FTP]��¼Ŀ���ftp������"+targetFile.getServerAddress()+"ʧ��,���ӳ�ʱ��",e);
            } catch (IOException e) {
                flag = false;
                logger.error("[FTP]��¼Ŀ���ftp������"+targetFile.getServerAddress()+"ʧ�ܣ�FTP�������޷��򿪣�",e);
            }
        }
        return flag;
    }
    public boolean process(InputStream in, FileBean bean){
        if(connectServerTag()){
            String dirName="";

//            if(!bean.getFullname().endsWith(FileContext.Str_SyncFileSourceProcess_Flag)){
////                if(!FileFilter.existSubString(bean.getFullname(),FileContext.Str_SyncFileSourceProcess_Flag)){   //
//                dirName = bean.getFullname()+FileContext.Str_SyncFileSourceProcess_End_Flag;
//            }else {
//                dirName=bean.getFullname().substring(0, bean.getFullname().length()-FileContext.Str_SyncFileSourceProcess_Flag.length())+FileContext.Str_SyncFileSourceProcess_End_Flag;
//            }
            dirName = bean.getFullname()+FileContext.Str_SyncFileSourceProcess_End_Flag;
            String str ="";
            if (dirName.startsWith("/")&&targetFile.getDir().endsWith("/")){
                str=  targetFile.getDir()+dirName.substring(1,dirName.length());
            } else if (dirName.startsWith("/")&&!targetFile.getDir().endsWith("/")||!dirName.startsWith("/")&&targetFile.getDir().endsWith("/")) {
                str=  targetFile.getDir()+dirName;
            }else if (!dirName.startsWith("/")&&!targetFile.getDir().endsWith("/")){
                str=  targetFile.getDir()+"/"+dirName;
            }
            logger.info("[FTP]Ŀ��������ļ�·��dir == "+str);
            try {
                ftpOut  =  ftpClientTag.appendFileStream(str);//   Ŀ������
                if (ftpOut==null){
                    FTPFile ftpFile =ftpClientTag.mlistFile(str);
                    if (ftpFile!=null){
                        ftpOut  =  ftpClientTag.appendFileStream(FileFilter.getFirstNewSubString(str,FileContext.Str_SyncFileSourceProcess_End_Flag));//   Ŀ������
                    }
                }

            } catch (IOException e) {
                closeConnectTag();
                try {
                    in.close();
                } catch (IOException e1) {
                   logger.error(e1.getMessage()); //To change body of catch statement use File | Settings | File Templates.
                }
                logger.error("[FTP]dirName :=:"+dirName+"    "+e.getMessage());
                try {
                    ftpClientTag.completePendingCommand();
                } catch (IOException e1) {
                    try {
                        in.close();
                    } catch (IOException e2) {
                       logger.error(e2.getMessage());  //To change body of catch statement use File | Settings | File Templates.
                    }
                    logger.error("completePendingCommand����"+"   "+e1.getMessage());  //To change body of catch statement use File | Settings | File Templates.
                }
                return false;
            }
//            logger.info("dirName"+dirName+"   filebean="+bean.getFullname());
            if (ftpOut!=null){
                try {
                    IOUtils.copy(in,ftpOut);
                    ftpOut.flush();
                    ftpOut.close();
                    in.close();
                    ftpClientTag.completePendingCommand();
                } catch (IOException e) {
                    closeConnectTag();
                    try {
                        in.close();
                    } catch (IOException e1) {
                        logger.error(e1.getMessage());  //To change body of catch statement use File | Settings | File Templates.
                    }
                    logger.error("[FTP]OutputStream Ϊ��",e);
                    return false;
                }
            }
            if(bean.getSyncflag().equals(FileContext.Str_SyncFileEnd)){
                //����
                try {
                    FTPFile ftpFile= ftpClientTag.mlistFile(str.substring(0, str.length()-FileContext.Str_SyncFileSourceProcess_End_Flag.length()));

                    if (ftpFile!=null){
                        ftpClientTag.deleteFile(str.substring(0, str.length()-FileContext.Str_SyncFileSourceProcess_End_Flag.length()));
                        boolean  targbool = ftpClientTag.rename(str,str.substring(0, str.length()-FileContext.Str_SyncFileSourceProcess_End_Flag.length()));
                        if(!targbool){
                            logger.info("[FTP]"+str+"������"+str.substring(0, str.length()-FileContext.Str_SyncFileSourceProcess_End_Flag.length())+"ʧ��");
                        }else {
                            logger.info("[FTP]"+str+"������"+str.substring(0, str.length()-FileContext.Str_SyncFileSourceProcess_End_Flag.length())+"�ɹ�");
                        }
                    }else {
                        boolean  targbool = ftpClientTag.rename(str,str.substring(0, str.length()-FileContext.Str_SyncFileSourceProcess_End_Flag.length()));
                        if(!targbool){
                            logger.info("[FTP]"+str+"������"+str.substring(0, str.length()-FileContext.Str_SyncFileSourceProcess_End_Flag.length())+"ʧ��");
                        }else {
                            logger.info("[FTP]"+str+"������"+str.substring(0, str.length()-FileContext.Str_SyncFileSourceProcess_End_Flag.length())+"�ɹ�");
                        }
                    }
                } catch (IOException e) {
                    closeConnectTag();
                    logger.error(e);
                    return false;
                }
            }
            return true;
        }
        return false;
    }


    @Override
    public boolean process(byte[] data, FileBean bean) {

        return true;
    }

    public void init(TargetOperation target, TargetFile config) {
        this.targetFile=config;
        this.targetOperation=target;
        this.size=config.getThreads();
    }

    @Override
    public void stop() {
    }
    @Override
    public boolean isRun() {
        return true;
    }
    /**
     *�ر�����
     */
    public  void closeConnectTag() {

        try {
            if (ftpClientTag != null) {
                ftpClientTag.logout();
            }
        } catch (Exception e) {
            logger.info(e);
        } finally {
            if(ftpClientTag!=null){
                if(ftpClientTag.isConnected()){
                    try {
                        ftpClientTag.disconnect();
                    } catch (IOException e) {
                        logger.info(e);
                    }
                }
                ftpClientTag=null;
            }
        }
    }

}
