package com.fartec.ichange.plugin.filechange.utils;

import com.inetec.common.config.nodes.SourceFile;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;
import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 12-5-10
 * Time: ����11:14
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
//            logger.error("FileFilterFtp�ж���ʧ��",e);  //To change body of catch statement use File | Settings | File Templates.
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
        if(fts==null&&nfts!=null){   //��������û�� ���������ʹ���
            boolean  flag=false;
            for(String nft:nfts){
                String nftname= FileFilter.getEndSubString(nft,".");
                if(!nftname.equals("*")){
                    if(!flag){
                        if(!filename.equals(nftname)){                   //����ļ����͵��ڹ�������   ����false ���� true
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
        if(fts!=null&&nfts==null){  //�������ʹ��� ���������Ͳ�����
            for (String ft:fts){
                String ftname=FileFilter.getEndSubString(ft,".");
                if(ftname.equals("*")){
                    return true;
                }else if(filename.equals(ftname)){       //����ļ����͵��ڹ�������   ����true
                    return true;
                }
            }
        }
        if(fts==null&&nfts==null){       //�������Ͳ����� ���������Ͳ�����  ȫ����ͨ��
            logger.error("���ô�����������벻��������Ϊ��");
            return false;
        }
        if(fts!=null&&nfts!=null){       //���� ���������Ͷ�����  ���жϹ��˵����� ��ȡ�����ٸ������˵ĶԱ�
            boolean  flag=false;
            for(String nft:nfts){
                String nftname= FileFilter.getEndSubString(nft,".");
                if(nftname.equals("*")){
                    return false;
                }
                if(!flag){
                    if(!filename.equals(nftname)){                   //����ļ����͵��ڹ�������   ����false ���� true
                        flag=false;
                    }else {
                        flag=true;
                    }
                }
            }
            if(!flag){
                for (String ft:fts){
                    String ftname=FileFilter.getEndSubString(ft,".");
                    if(filename.equals(ftname)){       //����ļ����͵��ڹ�������   ����true ���� false
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
//            ftpClient.setDataTimeout(60000);       //���ô��䳬ʱʱ��Ϊ60��
//            ftpClient.setConnectTimeout(60000);       //���ӳ�ʱΪ60��
//            try{
//                ftpClient.connect(sourceFile.getServerAddress());
//            }catch (IOException e){
//                flag=false;
//                logger.info("Դ�������쳣", e);
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
////                logger.warn("Դ�˲����ڸ�Ŀ¼");
////                return false;
////            }
////                ���ñ���ģʽ
//            ftpClient.enterLocalPassiveMode();
//            reply = ftpClient.getReplyCode();
//            if (!FTPReply.isPositiveCompletion(reply)) {
//                ftpClient.disconnect();
//                logger.error("[FTP]����Դ��FTP������" + sourceFile.getServerAddress() + "�ܾ�.");
//                flag = false;
//            }
//
//
//        } catch (SocketException e) {
//            flag = false;
//            logger.error("[FTP]��¼Դ��ftp������"+sourceFile.getServerAddress()+"ʧ��,���ӳ�ʱ��",e);
//        } catch (IOException e) {
//            flag = false;
//            logger.error("[FTP]��¼Դ��ftp������"+sourceFile.getServerAddress()+"ʧ�ܣ�FTP�������޷��򿪣�",e);
//        }
//        return flag;
//    }
//    public  void closeConnect(FTPClient ftpClient) {
//        try {
//            if (ftpClient != null) {
//                ftpClient.logout();
////                logger.info("�ر�ftp");
//            }
//        } catch (Exception e) {
//            logger.warn(e.getMessage());
//        } finally {
//            if (ftpClient != null) {
//                if(ftpClient.isConnected()){
//                    try {
//                        ftpClient.disconnect();
////                logger.info("�Ͽ�ftp����");
//                    } catch (IOException e) {
//                        logger.warn(e.getMessage());
//                    }
//                }
//                ftpClient=null;
//            }
//        }
//    }
}
