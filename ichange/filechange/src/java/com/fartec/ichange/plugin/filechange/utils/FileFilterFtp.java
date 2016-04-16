package com.fartec.ichange.plugin.filechange.utils;

import com.inetec.common.config.nodes.SourceFile;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;
import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 12-5-10
 * Time: 上午11:14
 * To change this template use File | Settings | File Templates.
 */
public class FileFilterFtp implements FTPFileFilter {
    private static final Logger logger=Logger.getLogger(FileFilterFtp.class);
    private String filtertypes;
    private String notfiltertypes;
    private SourceFile sourceFile;
    private String url;
    public FileFilterFtp(SourceFile sourceFile ,String url){
        this.sourceFile = sourceFile ;
        this.filtertypes=sourceFile.getFiltertypes();
        this.notfiltertypes=sourceFile.getNotfiltertypes();
        this.url = url ;
    }
    @Override
    public boolean accept(FTPFile file) {
        if(file.isDirectory()){
            return true;
        }
//        byte[] data = new byte[10];
//        String dir = "";
//        FTPClient ftpClient = new FTPClient();
//        connectServer(ftpClient);
//        try {
//            if(url.endsWith("/")){
//                dir=(url+file.getName()).trim();
//            } else {
//                dir=(url+"/"+file.getName()).trim();
//            }
//            InputStream inputStream = ftpClient.retrieveFileStream(dir) ;
//            inputStream.read(data);
//            inputStream.close();
//            closeConnect(ftpClient);
//        } catch (IOException e) {
//            logger.error("FileFilterFtp中读流失败",e);  //To change body of catch statement use File | Settings | File Templates.
//            closeConnect(ftpClient);
//        }
//        String realname = FileType.getFileType(file.getName(),data) ;
//        String filename=FileFilter.getEndSubString(realname,".");
        String filename = FileFilter.getEndSubString(file.getName(),".");
        if(filename.endsWith("tar") || filename.endsWith("zip") || filename.endsWith("gz") || filename.endsWith("rar")){
            return false ;
        }
        String[] nfts=null;
        if(notfiltertypes!=null&&!notfiltertypes.equals("")) {
            nfts=notfiltertypes.split(",");
        }
        String[] fts=null;
        if(filtertypes!=null&&!filtertypes.equals("")){
            fts=filtertypes.split(",");
        }
        if(fts==null&&nfts!=null){   //过滤类型没有 不过滤类型存在
            boolean  flag=false;
            for(String nft:nfts){
                String nftname= FileFilter.getEndSubString(nft,".");
                if(!nftname.equals("*")){
                    if(!flag){
                        if(!filename.equals(nftname)){                   //如果文件类型等于过滤类型   返回false 否则 true
                            flag=false;
                        }else {
                            flag=true;
                        }
                    }
                } else {
                    return false;
                }
            }
            if(!flag){
                return true;
            }
        }
        if(fts!=null&&nfts==null){  //过滤类型存在 不过滤类型不存在
            for (String ft:fts){
                String ftname=FileFilter.getEndSubString(ft,".");
                if(ftname.equals("*")){
                    return true;
                }else if(filename.equals(ftname)){       //如果文件类型等于过滤类型   返回true
                    return true;
                }
            }
        }
        if(fts==null&&nfts==null){       //过滤类型不存在 不过滤类型不存在  全部不通过
            logger.error("配置错误过滤类型与不过滤类型为空");
            return false;
        }
        if(fts!=null&&nfts!=null){       //过滤 不过滤类型都存在  先判断过滤的类型 获取出来再跟不过滤的对比
            boolean  flag=false;
            for(String nft:nfts){
                String nftname= FileFilter.getEndSubString(nft,".");
                if(nftname.equals("*")){
                    return false;
                }
                if(!flag){
                    if(!filename.equals(nftname)){                   //如果文件类型等于过滤类型   返回false 否则 true
                        flag=false;
                    }else {
                        flag=true;
                    }
                }
            }
            if(!flag){
                for (String ft:fts){
                    String ftname=FileFilter.getEndSubString(ft,".");
                    if(filename.equals(ftname)){       //如果文件类型等于过滤类型   返回true 否则 false
                        return true;
                    }
                }
            }
        }
        return  false;
    }
//    public  boolean connectServer(FTPClient ftpClient) {
//        boolean flag = true;
//
//        int reply;
//        try {
//            ftpClient.setControlEncoding(sourceFile.getCharset());
//            ftpClient.setDefaultPort(sourceFile.getPort());
//            ftpClient.setDataTimeout(60000);       //设置传输超时时间为60秒
//            ftpClient.setConnectTimeout(60000);       //连接超时为60秒
//            try{
//                ftpClient.connect(sourceFile.getServerAddress());
//            }catch (IOException e){
//                flag=false;
//                logger.info("源端连接异常", e);
//            }
//            int i=0;
//            while (!ftpClient.isConnected()&&i<5){
//                i++;
//                try {
//                    Thread.sleep(1000*60);
//                } catch (InterruptedException ee) {
//                    logger.error(ee);
//                }
//                ftpClient=null;
//                connectServer(ftpClient);
//
//            }
//            ftpClient.setKeepAlive(true);
//            ftpClient.setRemoteVerificationEnabled(true);
//            ftpClient.login(sourceFile.getUserName(), sourceFile.getPassword());
//            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
////            if(sourceFile.getDir()!=null){
////                try {
////                    ftpClient.changeWorkingDirectory(sourceFile.getDir());
////                } catch (IOException e) {
////                    logger.error(e);
////                }
////            }else {
////                logger.warn("源端不存在根目录");
////                return false;
////            }
////                设置被动模式
//            ftpClient.enterLocalPassiveMode();
//            reply = ftpClient.getReplyCode();
//            if (!FTPReply.isPositiveCompletion(reply)) {
//                ftpClient.disconnect();
//                logger.error("[FTP]连接源端FTP服务器" + sourceFile.getServerAddress() + "拒绝.");
//                flag = false;
//            }
//
//
//        } catch (SocketException e) {
//            flag = false;
//            logger.error("[FTP]登录源端ftp服务器"+sourceFile.getServerAddress()+"失败,连接超时！",e);
//        } catch (IOException e) {
//            flag = false;
//            logger.error("[FTP]登录源端ftp服务器"+sourceFile.getServerAddress()+"失败，FTP服务器无法打开！",e);
//        }
//        return flag;
//    }
//    public  void closeConnect(FTPClient ftpClient) {
//        try {
//            if (ftpClient != null) {
//                ftpClient.logout();
////                logger.info("关闭ftp");
//            }
//        } catch (Exception e) {
//            logger.warn(e.getMessage());
//        } finally {
//            if (ftpClient != null) {
//                if(ftpClient.isConnected()){
//                    try {
//                        ftpClient.disconnect();
////                logger.info("断开ftp连接");
//                    } catch (IOException e) {
//                        logger.warn(e.getMessage());
//                    }
//                }
//                ftpClient=null;
//            }
//        }
//    }
}
