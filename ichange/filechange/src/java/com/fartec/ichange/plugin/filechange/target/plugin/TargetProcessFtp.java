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
 * Time: 下午6:32
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
     * 创建文件夹
     * @param ftpClientTag
     * @param dir      需要创建的文件夹路径
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
                        bool=ftpClientTag.makeDirectory(dirss);//创建文件夹
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
     * 处理源端文件列表。
     * @param list 文件列表对象
     * @return 返回要同步的文件列表。
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
                    if(fileBean.getFullname().endsWith(FileContext.Str_SyncFileSourceProcess_Flag)){   //中断文件处理
                        FileBean fb=  interruptFile(fileBean);
                        if(fb!=null){
                            fileList.addFileBean(fb);
                        }
                    }
                    if(!fileBean.getFullname().endsWith(FileContext.Str_SyncFileSourceProcess_Flag)){   // //普通文件处理
                        FileBean fb=  ordinaryFile(fileBean);
                        if(fb!=null){
                            fileList.addFileBean(fb);
                        }
                    }
                }
                if(targetFile.isDeletefile()){
                    FileFtp.ftpJdomFile(list);   //目标端建立源端同步文件的xml文件
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
     * 中断文件处理
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
//                        logger.info("文件已经传完，只需改名");
            ftpClientTag.rename(str,strd);
            return null;
        }
        if(file!=null&&file.getSize()<fileBean.getFilesize()){
//                        logger.info("文件没有传完，断点续传，改名");
            FileBean fb=new FileBean();
            fb.setFilesize(file.getSize());
            fb.setFullname(fileBean.getFullname());
            fb.setName(file.getName());
//            fileList.addFileBean(fb);
            return  fb;
        }
        if(file==null){
            if(files==null){
//              logger.info("目标端没有文件");
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
     * 普通文件处理
     */
    public FileBean ordinaryFile (FileBean fileBean) {
        FTPFile ftpfile = null;     //是否目标端存在文件
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
            if(ss!=fileBean.getFilesize()){ //文件存在但是大小不同
                if(targetFile.isOnlyadd()){     //是否是只增加
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
     * 连接服务器
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
                ftpClientTag.setDataTimeout(60000);       //设置传输超时时间为60秒
                ftpClientTag.setConnectTimeout(60000);       //连接超时为60秒
                try{
                    ftpClientTag.connect(targetFile.getServerAddress());
                }catch (IOException e){
                    flag=false;
                    logger.error("[FTP]目标端连接异常"+targetFile.getServerAddress()+"   "+e.getMessage());
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
//                设置被动模式
                ftpClientTag.enterLocalPassiveMode();
                reply = ftpClientTag.getReplyCode();
                if (!FTPReply.isPositiveCompletion(reply)) {
                    ftpClientTag.disconnect();
                    logger.error("[FTP]连接目标端FTP服务器"+targetFile.getServerAddress()+"拒绝.");
                    flag = false;
                }
                if(targetFile.getDir()!=null){
                    //todo 创建   targetFile.getDir()文件夹
                    makeNewDir(ftpClientTag,targetFile.getDir());
                }else {
                    logger.warn("[FTP]目标端不存在根目录，以‘/’为根目录继续进行");
                    try {
                        ftpClientTag.changeWorkingDirectory("/");
                    } catch (IOException e) {
                        logger.error(e);
                    }
                }
            } catch (SocketException e) {
                flag = false;
                logger.error("[FTP]登录目标端ftp服务器"+targetFile.getServerAddress()+"失败,连接超时！",e);
            } catch (IOException e) {
                flag = false;
                logger.error("[FTP]登录目标端ftp服务器"+targetFile.getServerAddress()+"失败，FTP服务器无法打开！",e);
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
            logger.info("[FTP]目标端生成文件路径dir == "+str);
            try {
                ftpOut  =  ftpClientTag.appendFileStream(str);//   目标端输出
                if (ftpOut==null){
                    FTPFile ftpFile =ftpClientTag.mlistFile(str);
                    if (ftpFile!=null){
                        ftpOut  =  ftpClientTag.appendFileStream(FileFilter.getFirstNewSubString(str,FileContext.Str_SyncFileSourceProcess_End_Flag));//   目标端输出
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
                    logger.error("completePendingCommand出错"+"   "+e1.getMessage());  //To change body of catch statement use File | Settings | File Templates.
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
                    logger.error("[FTP]OutputStream 为空",e);
                    return false;
                }
            }
            if(bean.getSyncflag().equals(FileContext.Str_SyncFileEnd)){
                //改名
                try {
                    FTPFile ftpFile= ftpClientTag.mlistFile(str.substring(0, str.length()-FileContext.Str_SyncFileSourceProcess_End_Flag.length()));

                    if (ftpFile!=null){
                        ftpClientTag.deleteFile(str.substring(0, str.length()-FileContext.Str_SyncFileSourceProcess_End_Flag.length()));
                        boolean  targbool = ftpClientTag.rename(str,str.substring(0, str.length()-FileContext.Str_SyncFileSourceProcess_End_Flag.length()));
                        if(!targbool){
                            logger.info("[FTP]"+str+"改名成"+str.substring(0, str.length()-FileContext.Str_SyncFileSourceProcess_End_Flag.length())+"失败");
                        }else {
                            logger.info("[FTP]"+str+"改名成"+str.substring(0, str.length()-FileContext.Str_SyncFileSourceProcess_End_Flag.length())+"成功");
                        }
                    }else {
                        boolean  targbool = ftpClientTag.rename(str,str.substring(0, str.length()-FileContext.Str_SyncFileSourceProcess_End_Flag.length()));
                        if(!targbool){
                            logger.info("[FTP]"+str+"改名成"+str.substring(0, str.length()-FileContext.Str_SyncFileSourceProcess_End_Flag.length())+"失败");
                        }else {
                            logger.info("[FTP]"+str+"改名成"+str.substring(0, str.length()-FileContext.Str_SyncFileSourceProcess_End_Flag.length())+"成功");
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
     *关闭连接
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
