package com.fartec.ichange.plugin.filechange.source.plugin;


import com.fartec.ichange.plugin.filechange.FileChangeSource;
import com.fartec.ichange.plugin.filechange.source.SourceOperation;
import com.fartec.ichange.plugin.filechange.target.plugin.ITargetProcess;
import com.fartec.ichange.plugin.filechange.utils.*;
import com.fartec.ichange.plugin.filechange.utils.FileFilter;
import com.inetec.common.config.nodes.SourceFile;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.SocketException;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;


/**
 * Created by IntelliJ IDEA.
 * User: bluesky
 * Date: 11-11-26
 * Time: ����3:19
 * To change this template use File | Settings | File Templates.
 */
public class SourceProcessFtp implements ISourceProcess {
    private static final Logger logger=Logger.getLogger(SourceProcessFtp.class);
    private  ITargetProcess iTargetProcess;
    private  SourceFile sourceFile;
    private SourceOperation sourceOperation;
    private int size=0;
    private FTPClient ftpClient;
    private ExecutorService pool = null;
    private int filelistsize=0;
    private int MB= (1024*1024);
    private boolean isRun=false;
    int i=1;
    public boolean isvirusscan = false;
    public boolean isfilter = false;
    public  SourceProcessFtp(SourceFile sourceFile){
        this.sourceFile=sourceFile;
    }

    public SourceProcessFtp(){

    }

    /*
     *����Դ���б�    ʵ�ַ�������
     * @param ftpClient
     * @param dir        Դ��·��
     * @param fileListAll      ���ҵ������ļ��������filelist   ����Ŀ����Ƿ�ɾ�� Դ���Ƿ�ɾ����Ϊtrueʱʹ��
     * @return
     * @throws Ex
 * @throws IOException
     */

    public FileList addSourceFile(String dir,FileList fileListAll)  {
        FileList fileList=new FileList();    //�ļ����б�
        FileList sourcefilelist=new FileList();          // Դ���ļ��б�
        FileList fileListFirst=new FileList();            //  ��ִ���ж��ļ��б�
        FTPFile[] files= new FTPFile[0];         //��ȡ·�����������ݣ��ļ����ļ��У�
        if(connectServer()){
            try {
                files = ftpClient.listFiles();
            } catch (IOException e) {
                logger.error("[FTP]Դ���ļ���û���ҵ�"+dir,e);
                closeConnect();
                return null;
            }
        }
        int length=files.length;
        logger.info("[FTP]Files.length:=:" + length);
        for (FTPFile file: files){       //�ж���û���ж��ļ�  �м���filelistfrist
            if(file.isFile()){
                if(!FileFilter.existSubString(file.getName(),FileContext.Str_SyncFileTargetProcess_End_Flag)){          //�ļ����Ͳ��ǡ�itpe��ִ����������
                    FileBean fileBean =FtpFileCompare._FTPFileToBean(file,dir,sourceFile);   //�齨filebean
                    if(fileBean!=null){
                        fileListAll.addFileBean(fileBean);
                    }
                    FileBean f=  InterceptedFileName(fileBean,dir,ftpClient,sourceFile);
                    if(f!=null){                                                 //�ж��Ƿ�����ж��ļ�
                        f.setFullname(f.getFullname()+FileContext.Str_SyncFileSourceProcess_Flag);
                        fileListFirst.addFileBean(f); //����ж��ļ�
                    }

//                    if(FileFilter.existSubString(file.getName(),FileContext.Str_SyncFileSourceProcess_Flag)){       //�ж��Ƿ�����ж��ļ�
//                        if(fileBean!=null){
//                            fileListFirst.addFileBean(fileBean); //����ж��ļ�
//                        }
//                    }
                }
            }
        }
        if(fileListFirst!=null&&fileListFirst.size()!=0){       //fileListFirst   �����ļ�
            logger.info("[FTP]�����ж��ļ�����");
            if(!executeListInterrupt(fileListFirst, sourceOperation)){
                return null;
            }
        }
        for (FTPFile file: files){
            if(file.isFile()){
                if(!FileFilter.existSubString(file.getName(),FileContext.Str_SyncFileTargetProcess_End_Flag)){          //�ļ����Ͳ��ǡ�itpe��ִ����������
                    FileBean fileBean =FtpFileCompare._FTPFileToBean(file,dir,sourceFile);   //�齨filebean
                    String strings= FileContext.Str_Sync+fileBean.getFullname()+"_"+fileBean.getTime()+FileContext.Str_Lnk;
                    if(file.getName().equals(strings)){             //�ж��Ƿ�����ж��ļ�
                        continue;
                    }else {
                        if(fileBean!=null&&fileBean.getFilesize()!=0){
                            sourcefilelist.addFileBean(fileBean);
                            if(sourcefilelist.size()==filelistsize){
                                logger.info("[FTP]�ļ�����");
                                FileList list=   sourceOperation. procesFileList(sourcefilelist);      //�ж�Ŀ����Ƿ���� ��������ڼ��뵽�ϴ��б� list
                                if(list!=null&&list.size()!=0){
                                    logger.info("[FTP]�����ļ�����");
                                    if(!executeList(list,sourceOperation)){
                                        return null;
                                    }
                                }
                                sourcefilelist.clear();
                            }
                        }
                    }
                }
            } else {         //���ļ��е�
                if(file.getName().equals(".")||file.getName().equals("..")||file.getName()==null){
                    continue;
                } else {
                    FileBean fileBean =FtpFileCompare._FTPFileToBean(file,dir,sourceFile);   //�齨filebean
                    fileBean.setFile(false);
                    fileList.addFileBean(fileBean);
                }
            }
        }
//        if(fileList.size()!=0&&fileList!=null){
//            sourceOperation.procesFileList(fileList);
//        }

        //����ʣ��Ĳ���
        FileList list =null;
        if(sourcefilelist.size()!=0&&sourcefilelist!=null){
            list = sourceOperation.procesFileList(sourcefilelist) ;
        }
        if(list!=null&&list.size()!=0){
            logger.info("[FTP]�������ʣ��Ĳ����ļ�����");
            executeList(list,sourceOperation);
        }
        //��ɵ�ǰĿ¼������һ��Ŀ¼
        for (Iterator i=fileList.iterable();i.hasNext();){
            FileBean file= (FileBean) i.next();
            if(file!=null){
                if(sourceFile.isIsincludesubdir()){     //�Ƿ�����¼�Ŀ¼
                    String strdir=null;
                    if(dir.endsWith("/")){
                        strdir=(dir+file.getName()).trim();
                    } else {
                        strdir=(dir+"/"+file.getName()).trim();
                    }
                    addSourceFile( strdir,fileListAll);
                }
            }
        }
        return  fileListAll;
    }
    //��ȡ�ļ���
    public FileBean InterceptedFileName(FileBean fileBean,String dir,FTPClient ftpClient,SourceFile sourceFile){
        String s=FileFilter.getFirstNewSubString(fileBean.getName(),FileContext.Str_Lnk);
        if(s==""||s==null){
        }else {
//            SimpleDateFormat sdf = new SimpleDateFormat("_yyyy-MM-dd HH:mm:ss:SSS");//
//            String time = sdf.format(fileBean.getTime());
            String ss=FileFilter.getSubString(s,FileContext.Str_Sync);
            if("".equals(ss)||ss==null){
                return null;
            }
            String string2= FileFilter.getFirstNewSubString(ss,"_");
            String s2=FileFilter.getEndSubString(ss,"_");
            long  l= Long.parseLong(s2);

            String url=null;
            try {
                if(dir.endsWith("/")){
                    url= dir+string2;
                } else {
                    url= dir+"/"+string2;
                }
                if(url!=null){
                    if(ftpClient == null){
                        return null;
                    }
                    FTPFile[] ftpFile= ftpClient.listFiles(url);
                    if(ftpFile.length!=0){
                        if(ftpFile[0].getTimestamp().getTimeInMillis()!=l&&ftpFile[0].getSize()==0) {
                            boolean  bool= false; //ɾ���ļ�
                            try {
                                bool = ftpClient.deleteFile(dir+fileBean.getFullname());
                            } catch (IOException e) {
                                logger.error(e);
                            }
                            logger.info("[FTP]ɾ���ļ�"+dir+fileBean.getFullname()+"  �Ƿ�ɹ�   "+bool);
                            return null;
                        }
                        FileBean fileBeans =FtpFileCompare._FTPFileToBean(ftpFile[0],dir,sourceFile);   //�齨filebean
                        return fileBeans;
                    }
                }
            } catch (IOException e) {
                logger.error("[FTP]Դ���ļ���û���ҵ�"+string2+"   "+e.getMessage());
                closeConnect();
                return null;
            }
        }
        return null;
    }


    public  boolean  ss(String dir) {
        String dirs=FileFilter.getEnd_NewSubString(dir,FileContext.Str_SyncFileSourceProcess_Flag) ;            //ex:xxx.txt
        FTPFile ftpFile= null;
        FTPFile ftpFiles=null;
        try {
            ftpFile = ftpClient.mlistFile(dir);
            ftpFiles= ftpClient.mlistFile(dirs);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(ftpFiles!=null&&ftpFile!=null){    //ex: xxx.txt  or xxx.txt.ispf ������
            return false;
        }else {
            return true;
        }
    }
    public void run() {
        isRun=true;
        while (isRun){
            logger.info("\n\n\n");
            logger.info("===============================ͬ����"+ i +"��=====================================");
            if(connectServerRun()){         //����Դ�˷�����
                String dir = sourceFile.getDir();                               //Դ��·��
                FileList fileListAll=new FileList();//Դ�������ļ��б�
                logger.info("[FTP]��ȡԴ���ļ��б�");
                addSourceFile(dir,fileListAll);      //��ȡԴ���ļ��б�
                if (ftpClient!=null){
                    if(ftpClient.isConnected()){
                        closeConnect();
                        logger.info("[FTP]�ر�run");
                    }
                }
            }
            try {
                logger.info("[FTP]ͬ����"+ i++ +"��");
                logger.info("[FTP]��Ϣ��=��"+sourceFile.getInterval());
                Thread.sleep(sourceFile.getInterval());
            } catch (InterruptedException e) {
                logger.error(e);
            }
        }
    }

    public boolean process(InputStream in, FileBean bean) {
        return false;
    }

    public boolean process(byte[] data, FileBean bean) {
        return true;
    }

    public void init(SourceOperation source,SourceFile config) {
        this.sourceOperation=source;
        this.sourceFile=config;
        isfilter = source.getType().isFilter();
        isvirusscan = source.getType().isVirusScan();
        this.size=config.getThreads();
        this.filelistsize=config.getFilelistsize();

        FileChangeSource fileChangeSource = new FileChangeSource();
    }


    public void init(ITargetProcess target, SourceFile config) {
        this.iTargetProcess=target;
        this.sourceFile=config;
        this.size=config.getThreads();
        this.filelistsize=config.getFilelistsize();
    }

    public void stop()  {
        isRun=false;
    }
    /**
     *  ɾ��Դ�˿��ļ���
     * @param dir
     * @return
     * @throws IOException
     */
    public boolean deleteSourceDir(String dir) {
        if(connectServer()){
            try {
                FTPFile[] files  = ftpClient.listFiles(dir);
                for (FTPFile file: files){
                    if(file.getName()==null||".".equals(file.getName())||"..".equals(file.getName())){
                    }else {
                        if (file.isDirectory()) {
                            deleteSourceDir(dir + "/" + file.getName());
                        }
                    }
                }
                ftpClient.removeDirectory(dir);
            } catch (IOException e) {
                logger.error(e);
            }
            return true;
        }else {
            return false;
        }
    }


    public boolean isRun() {
        return isRun;
    }

    @Override
    public FileList procesFileList(FileList list) {
        return null;
    }

    /*
     * ���ݲ�������
     * @return
     */
    public  boolean connectServer() {
        boolean flag = true;
        if (ftpClient == null) {
            int reply;
            try {
                ftpClient = new FTPClient();
                ftpClient.setControlEncoding(sourceFile.getCharset());
                ftpClient.setDefaultPort(sourceFile.getPort());
                ftpClient.setDataTimeout(60000);       //���ô��䳬ʱʱ��Ϊ60��
                ftpClient.setConnectTimeout(60000);       //���ӳ�ʱΪ60��
                try{
                    ftpClient.connect(sourceFile.getServerAddress());
                }catch (IOException e){
                    flag=false;
                    logger.error("Դ�������쳣"+"   "+e.getMessage());
                }
                int i=0;
                while (!ftpClient.isConnected()&&i<5){
                    i++;
                    try {
                        Thread.sleep(1000*60);
                    } catch (InterruptedException ee) {
                        logger.error(ee);
                    }
                    ftpClient=null;
                    connectServer();
                }
                ftpClient.setKeepAlive(true);
                ftpClient.setRemoteVerificationEnabled(true);
                ftpClient.login(sourceFile.getUserName(), sourceFile.getPassword());
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
//            if(sourceFile.getDir()!=null){
//                try {
//                    ftpClient.changeWorkingDirectory(sourceFile.getDir());
//                } catch (IOException e) {
//                    logger.error(e);
//                }
//            }else {
//                logger.warn("Դ�˲����ڸ�Ŀ¼");
//                return false;
//            }
//                ���ñ���ģʽ
                ftpClient.enterLocalPassiveMode();
                reply = ftpClient.getReplyCode();
                if (!FTPReply.isPositiveCompletion(reply)) {
                    ftpClient.disconnect();
                    logger.error("[FTP]����Դ��FTP������" + sourceFile.getServerAddress() + "�ܾ�.");
                    flag = false;
                }


            } catch (SocketException e) {
                flag = false;
                logger.error("[FTP]��¼Դ��ftp������"+sourceFile.getServerAddress()+"ʧ��,���ӳ�ʱ��",e);
            } catch (IOException e) {
                flag = false;
                logger.error("[FTP]��¼Դ��ftp������"+sourceFile.getServerAddress()+"ʧ�ܣ�FTP�������޷��򿪣�",e);
            }
        }
        return flag;
    }
    public  boolean connectServer(FTPClient ftpClient) {
        boolean flag = true;

        int reply;
        try {
            ftpClient.setControlEncoding(sourceFile.getCharset());
            ftpClient.setDefaultPort(sourceFile.getPort());
            ftpClient.setDataTimeout(60000);       //���ô��䳬ʱʱ��Ϊ60��
            ftpClient.setConnectTimeout(60000);       //���ӳ�ʱΪ60��
            try{
                ftpClient.connect(sourceFile.getServerAddress());
            }catch (IOException e){
                flag=false;
                logger.error("Դ�������쳣"+"   "+e.getMessage());
            }
            int i=0;
            while (!ftpClient.isConnected()&&i<5){
                i++;
                try {
                    Thread.sleep(1000*10);
                } catch (InterruptedException ee) {
                    logger.error(ee);
                }
                ftpClient=null;
                connectServer(ftpClient);

            }
            ftpClient.setKeepAlive(true);
            ftpClient.setRemoteVerificationEnabled(true);
            ftpClient.login(sourceFile.getUserName(), sourceFile.getPassword());
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
//            if(sourceFile.getDir()!=null){
//                try {
//                    ftpClient.changeWorkingDirectory(sourceFile.getDir());
//                } catch (IOException e) {
//                    logger.error(e);
//                }
//            }else {
//                logger.warn("Դ�˲����ڸ�Ŀ¼");
//                return false;
//            }
//                ���ñ���ģʽ
            ftpClient.enterLocalPassiveMode();
            reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
                logger.error("[FTP]����Դ��FTP������" + sourceFile.getServerAddress() + "�ܾ�.");
                flag = false;
            }


        } catch (SocketException e) {
            flag = false;
            logger.error("[FTP]��¼Դ��ftp������"+sourceFile.getServerAddress()+"ʧ��,���ӳ�ʱ��",e);
        } catch (IOException e) {
            flag = false;
            logger.error("[FTP]��¼Դ��ftp������"+sourceFile.getServerAddress()+"ʧ�ܣ�FTP�������޷��򿪣�",e);
        }
        return flag;
    }


    /**
     * ��½����
     * @return
     */
    public  boolean connectServerRun() {
        boolean flag = true;
        if (ftpClient == null) {
            int reply;
            try {
                ftpClient = new FTPClient();
                ftpClient.setControlEncoding(sourceFile.getCharset());
                ftpClient.setDefaultPort(sourceFile.getPort());
                ftpClient.setDataTimeout(60000);       //���ô��䳬ʱʱ��Ϊ60��
                ftpClient.setConnectTimeout(60000);       //���ӳ�ʱΪ60��
                try{
                    ftpClient.connect(sourceFile.getServerAddress());
                }catch (SocketException e){
                    flag=false;
                    logger.info("[FTP]Դ�������쳣",e);
                }
                ftpClient.setKeepAlive(true);
                ftpClient.setRemoteVerificationEnabled(true);
                ftpClient.login(sourceFile.getUserName(), sourceFile.getPassword());
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                if(sourceFile.getDir()!=null){
                    try {
                        ftpClient.changeWorkingDirectory(sourceFile.getDir());
                    } catch (IOException e) {
                        logger.error(e);
                    }
                }else {
                    logger.warn("[FTP]Դ�˲����ڸ�Ŀ¼");
                    return false;
                }
//                ���ñ���ģʽ
                ftpClient.enterLocalPassiveMode();
                reply = ftpClient.getReplyCode();
                if (!FTPReply.isPositiveCompletion(reply)) {
                    ftpClient.disconnect();
                    logger.error("[FTP]����Դ��FTP������" + sourceFile.getServerAddress() + "�ܾ�.");
                    flag = false;
                }
            } catch (SocketException e) {
                flag = false;
                logger.error("[FTP]��¼Դ��ftp������"+sourceFile.getServerAddress()+"ʧ��,���ӳ�ʱ��",e);
            } catch (IOException e) {
                flag = false;
                logger.error("[FTP]��¼Դ��ftp������"+sourceFile.getServerAddress()+"ʧ�ܣ�FTP�������޷��򿪣�",e);
            }
        }
        return flag;
    }
    /*
    �ر�����
     */
    public  void closeConnect() {
        try {
            if (ftpClient != null) {
                ftpClient.logout();
//                logger.info("�ر�ftp");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ftpClient != null) {
                if(ftpClient.isConnected()){
                    try {
                        ftpClient.disconnect();
//                logger.info("�Ͽ�ftp����");
                    } catch (IOException e) {
                        logger.error(e);
                    }
                }
                ftpClient=null;
            }
        }
    }
    /*
     *  �����ļ�
     *  1.���������ļ��б�
     *  2.�ȴ����׺����FileContext.Str_SyncFileSourceProcess_Flag   ��û�л�ֱ��ִ����һ����
     *  3.���������Ǹ������ļ���+��׺��
     *  4.��д�������ϴ�
     *  5.��ɺ�Ѻ�׺��ȥ���Ļ�ԭ������
     *  6.���Դ��������ɾ��ִ��ɾ��
     * @param list �����ļ��б�
     * @return      boolean
     */

    public boolean executeList(FileList list,SourceOperation sourceOperation)  {
        logger.info("[FTP]����executeList()����");
        for (Iterator i=list.iterable();i.hasNext();){
            if(!connectServer()){
                return false;
            }else {
                String str="";
                if(sourceFile.getDir().equals("/")){
                    str="";
                }else if(sourceFile.getDir().endsWith("/")){
                    str=sourceFile.getDir();
                }else {
                    str=sourceFile.getDir()+"/";
                }
                FileBean f= (FileBean) i.next();
                if (!f.isFile()) {  //is dir ����
                    continue;
                }
                if(f.getFilesize()==0){      //�ļ���СΪ0 bit  ����
                    continue;
                }
                String stringss=FileContext.Str_Sync+f.getName()+"_"+f.getTime()+FileContext.Str_Lnk;
                logger.info("[FTP]ͬ���ļ�"+str+f.getFullname()+"��ʼ");
                String s=FileFilter.getFirstNewSubString(str+f.getFullname(),"/");
//                logger.info(s+"/"+stringss);
                String strings=s+"/"+stringss;
                InputStream ftpIns= null;
                try {
                    CreateFileLnk(strings);

//                    ftpClient.getReply();
                    ftpIns = ftpClient.retrieveFileStream(f.getFullname());
                    if(ftpIns == null){
                        ftpIns = ftpClient.retrieveFileStream(f.getFullname());
                    }
                    logger.info("[FTP]ftpIn:=:"+ftpIns+"         sourflag.size="+f.getFilesize());


                    boolean flag = sprocess(ftpIns, f,sourceOperation);

//                    if(!flag){
//
//                        continue;
//                    }
                    if(ftpIns != null){
                        ftpIns.close();
                        ftpClient.completePendingCommand();
                    }
                    if(flag){
//                        ftpClient.getReply();
                        ftpClient.noop();
                        if(sourceFile.isDeletefile()){
                            boolean  bool=ftpClient.deleteFile(str+f.getFullname()); //ɾ���ļ�
                            logger.info("[FTP]ɾ���ļ�"+str+f.getFullname()+"  �Ƿ�ɹ�   "+bool);
                        }
                    }
                    boolean  bools=ftpClient.deleteFile(strings); //ɾ���ļ�
                    logger.info("[FTP]ɾ����ʱ�ļ�"+strings+"  �Ƿ�ɹ�  "+bools);
                    logger.info("[FTP]ͬ���ļ�"+str+f.getFullname()+"����");

                }catch (IOException e) {
                    logger.error(e);
                    closeConnect();
                    try {
                        if(ftpClient != null){
                            ftpClient.completePendingCommand();
                        }
                    } catch (IOException e1) {
                        logger.error("completePendingCommand����"+"   "+e1.getMessage());   //To change body of catch statement use File | Settings | File Templates.
                    }
                    continue;
                }
            }
        }
        return true;
    }

    //����lnk�ļ�
    public  void CreateFileLnk(String strings) throws IOException {
        OutputStream outputStream= null;
        outputStream = ftpClient.storeFileStream(strings);
        if(outputStream!=null){
            outputStream.close();
            ftpClient.completePendingCommand();
        }
    }

    //�ж��ļ�����
    public boolean executeListInterrupt(FileList list,SourceOperation sourceOperation)  {
        logger.info("[FTP]executeListInterrupt()����");
        try{
            for (Iterator i=list.iterable();i.hasNext();){
                if(!connectServer()){
                    return false;
                }else {
                    String str="";
                    if(sourceFile.getDir().equals("/")){
                        str="";
                    }else if(sourceFile.getDir().endsWith("/")){
                        str=sourceFile.getDir();
                    }else {
                        str=sourceFile.getDir()+"/";
                    }
                    FileBean f= (FileBean) i.next();
                    if (!f.isFile()) {  //is dir ����
                        continue;
                    }
                    FileList lists=new FileList();
                    lists.addFileBean(f);
                    FileList list1=sourceOperation.procesFileList(lists);
                    String url= FileFilter.getFirstNewSubString(f.getFullname(),FileContext.Str_SyncFileSourceProcess_Flag) ;
                    f.setFullname(url);
                    String stringss=FileContext.Str_Sync+f.getName()+"_"+f.getTime()+FileContext.Str_Lnk;
//                    logger.info("[FTP]ͬ���ļ�"+str+f.getFullname()+"��ʼ");
                    String s=FileFilter.getFirstNewSubString(str+f.getFullname(),"/");
//                    logger.info(s+"/"+stringss);
                    String strings=s+"/"+stringss;
//                    String strings= FileContext.Str_Sync+url+"_"+f.getTime()+FileContext.Str_Lnk;
                    if(list1==null||list1.size()==0){   //
//                        boolean  bools=ftpClient.rename(str+f.getFullname(),str+f.getFullname().split(FileContext.Str_SyncFileSourceProcess_Flag)[0]);
//                        if(bools==false){
//                            logger.info("Դ������ϴ������ʧ��");
//                        }
                        boolean  bools=ftpClient.deleteFile(strings); //ɾ���ļ�
                        logger.info("[FTP]ɾ���ļ�"+str+url+"    "+bools);
                        if(sourceFile.isDeletefile()){
                            boolean  bool=ftpClient.deleteFile(str+url); //ɾ���ļ�
                            logger.info("[FTP]ɾ���ļ�"+str+f.getFullname()+"    "+bool);
                        }
                    }else {
                        for (Iterator j=list1.iterable();j.hasNext();){
                            FileBean fb= (FileBean) j.next();
                            if(fb.getFilesize()==f.getFilesize()){
                                InputStream ftpIn = ftpClient.retrieveFileStream(str+url);
                                logger.info("[FTP]ͬ���ļ�"+str+url+"��ʼ");
                                logger.info("[FTP]ftpIn:=:"+ftpIn+"         sourflag.size="+f.getFilesize());
                                boolean flag = sprocess(ftpIn,f,sourceOperation);
//                                if(!flag ){
//                                    continue;
//                                }
                                ftpIn.close();
                                ftpClient.completePendingCommand();
                                if(flag){
//                                    ftpClient.getReply();
                                    if(sourceFile.isDeletefile()){
                                        boolean  bool=ftpClient.deleteFile(str+url); //ɾ���ļ�
                                        logger.info("[FTP]ɾ���ļ�"+str+url+"    "+bool);
                                    }
                                }
                                boolean  bools=ftpClient.deleteFile(strings); //ɾ���ļ�
                                logger.info("[FTP]ɾ���ļ�"+str+url+"    "+bools);
                                logger.info("[FTP]ͬ���ļ�"+str+f.getFullname()+"����");
                            }
                            if(fb.getFilesize()<f.getFilesize()){
                                ftpClient.setRestartOffset(fb.getFilesize());
                                InputStream ftpIn = ftpClient.retrieveFileStream(str+url);
                                logger.info("[FTP]ͬ���ļ�"+str+url+"��ʼ");
                                logger.info("[FTP]ftpIn:=:"+ftpIn+"         sourflag.size="+f.getFilesize());
                                boolean flag = sprocess(ftpIn,f,sourceOperation);
//                                if(!flag){
//                                    continue;
//                                }
                                ftpIn.close();
                                ftpClient.completePendingCommand();
                                if(flag){
//                                    ftpClient.getReply();
                                    if(sourceFile.isDeletefile()){
                                        boolean  bool=ftpClient.deleteFile(str+url); //ɾ���ļ�
                                        logger.info("[FTP]ɾ���ļ�"+str+url+"    "+bool);
                                    }
                                }
                                boolean  bools=ftpClient.deleteFile(strings); //ɾ���ļ�
                                logger.info("[FTP]ɾ���ļ�"+str+url+"    "+bools);
                                logger.info("[FTP]ͬ���ļ�"+str+f.getFullname()+"����");
                            }
                        }
                    }
                }
            }
            return  true;
        }catch (IOException e) {
            logger.error(e);
            closeConnect();
            try {
                if(ftpClient != null){
                    ftpClient.completePendingCommand();
                }
            } catch (IOException e1) {
                logger.error("completePendingCommand����"+"   "+e1.getMessage());  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        return false;
    }

    /**
     * �ְ�����
     * @param in
     * @param fileBean
     * @return
     */
    public boolean  sprocess(InputStream in,FileBean fileBean,SourceOperation sourceOperation){
        logger.debug("[FTP]sourceOperation  == "+sourceOperation);
        if(in==null){
            return false;
        }
        BufferedInputStream bufferedInputStream = new BufferedInputStream(in);
        byte[] head = new byte[10];
        String realname = null;
        try {
            bufferedInputStream.mark(0);
            bufferedInputStream.read(head);
            realname = FileType.getFileType(fileBean.getName(),head);
            bufferedInputStream.reset();
        } catch (IOException e) {
           logger.error("��ȡ�ļ�ͷʧ��"+"   "+e.getMessage());  //To change body of catch statement use File | Settings | File Templates.
            try {
                bufferedInputStream.close();
            } catch (IOException e1) {
               logger.error(e1.getMessage());  //To change body of catch statement use File | Settings | File Templates.
            }
            return false;
        }
        if(FileType.REAL_NAME_MAP.get(fileBean.getName()) == null){
            fileBean.setRealname(fileBean.getName());
        }
        else {
            fileBean.setRealname(FileType.REAL_NAME_MAP.get(fileBean.getName()));
        }
        boolean flag=false;
        if(filter(realname)){
            byte [] bytes=new byte[sourceFile.getPacketsize()*MB];
            ByteArrayOutputStream out=new ByteArrayOutputStream();
            int post=0;
            long fileSize = 0;
            int len=-1;
            fileBean.setFile_flag(FileContext.Str_SourceFile);
            if( isfilter && (fileBean.getRealname().endsWith(".rtf") || fileBean.getRealname().endsWith(".txt"))){
                try {
                    while ((len=bufferedInputStream.read(bytes))!=-1){
                        if(post>=sourceFile.getPacketsize()*MB){
                            fileBean.setSyncflag(FileContext.Str_SyncFileStart);
                            fileSize+=post;
                            byte[] data = null ;
                            try {
                                data = KeywordsFilterFactory.getKeywordsFilterUtil(fileBean.getRealname()).filter(out.toByteArray(),Basic.getKeywords(),sourceFile);
                            } catch (Exception e) {
                                bufferedInputStream.close();
                                logger.error("�ļ��ַ������벻ƥ��"+e.getMessage()); //To change body of catch statement use File | Settings | File Templates.
                                return false;
                            }
                            flag =  sourceOperation.process(data,fileBean);
                            if (flag==false){
                                post=-1;
                                break;
                            }else {
                                post=0;
                                out.reset();
                                out.write(bytes,0,len);
                                post+=len;
                            }
                        } else {
                            out.write(bytes, 0, len);
                            post+=len;
                        }
                    }
                    if(post!=-1){
                        fileBean.setSyncflag(FileContext.Str_SyncFileEnd);
                        logger.debug(" [FTP] out.toByteArray().length ==   "+out.toByteArray().length);
                        byte[] data = null ;
                        try {
                            data = KeywordsFilterFactory.getKeywordsFilterUtil(fileBean.getRealname()).filter(out.toByteArray(),Basic.getKeywords(),sourceFile);
                        } catch (Exception e) {
                            bufferedInputStream.close();
                            logger.error("�ļ��ַ������벻ƥ��"+e.getMessage());  //To change body of catch statement use File | Settings | File Templates.
                            return false;
                        }
                        flag=sourceOperation.process(data,fileBean);
                    }

                } catch (IOException e) {
                    logger.error(e);
                    return flag=false;
                }
                finally {
                    try {
                        bufferedInputStream.close();
                        out.flush();
                        out.close();
                    } catch (IOException e) {
                        logger.error(e.getMessage());  //To change body of catch statement use File | Settings | File Templates.
                    }
                }
            }
            else {
                try {
                    while ((len=bufferedInputStream.read(bytes))!=-1){
                        if(post>=sourceFile.getPacketsize()*MB){
                            fileBean.setSyncflag(FileContext.Str_SyncFileStart);
                            fileSize+=post;
                            flag =  sourceOperation.process(out.toByteArray(),fileBean);
                            if (flag==false){
                                post=-1;
                                break;
                            }else {
                                post=0;
                                out.reset();
                                out.write(bytes,0,len);
                                post+=len;
                            }
                        } else {
                            out.write(bytes, 0, len);
                            post+=len;
                        }
                    }
                    if(post!=-1){
                        fileBean.setSyncflag(FileContext.Str_SyncFileEnd);
                        logger.debug(" [FTP] out.toByteArray().length ==   "+out.toByteArray().length);
                        flag=sourceOperation.process(out.toByteArray(),fileBean);
                    }

                } catch (IOException e) {
                    logger.error(e.getMessage());
                    return flag=false;
                }
                finally {
                    try {
                        bufferedInputStream.close();
                        out.flush();
                        out.close();
                    } catch (IOException e) {
                        logger.error(e.getMessage());  //To change body of catch statement use File | Settings | File Templates.
                    }
                }
            }
        }
        if(bufferedInputStream != null){
            try {
                bufferedInputStream.close();
                in.close();
            } catch (IOException e) {
                logger.info("�ر���"+bufferedInputStream+"ʧ�ܣ�");  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        return flag;
    }
    /*  public static  void main(String[] arg){
 FTPClient ftpClient=null;
 try {
     ftpClient = new FTPClient();
     ftpClient.setControlEncoding("gbk");
     ftpClient.setDefaultPort(21);
     ftpClient.setDataTimeout(60000);       //���ô��䳬ʱʱ��Ϊ60��
     ftpClient.setConnectTimeout(60000);       //���ӳ�ʱΪ60��
     try{
         ftpClient.connect("127.0.0.1");
     }catch (IOException e){
         logger.info("Դ�������쳣", e);
     }
     int i=0;
    *//* while (!ftpClient.isConnected()&&i<5){
                 i++;
                 try {
                     Thread.sleep(1000*60);
                 } catch (InterruptedException ee) {
                     logger.error(ee);
                 }
                 ftpClient=null;
                 connectServer();

             }*//*
             ftpClient.setKeepAlive(true);
             ftpClient.setRemoteVerificationEnabled(true);
             ftpClient.login("root", "root");
             ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

//                ���ñ���ģʽ
             ftpClient.enterLocalPassiveMode();



         } catch (SocketException e) {
             logger.error("��¼Դ��ftp������127.0.0.1ʧ��,���ӳ�ʱ��",e);
         } catch (IOException e) {
             logger.error("��¼Դ��ftp������127.0.0.1ʧ�ܣ�FTP�������޷��򿪣�",e);
         }
         try {
             InputStream inputStream=  ftpClient.retrieveFileStream("Ubuntu");
            String[] ftpClients=  ftpClient.listNames();
             FTPFile[] ftpFiles=ftpClient.listFiles();
             System.out.println("inputStream  ==  "+ inputStream);
         } catch (IOException e) {
             e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
         }
     }*/
     public boolean filter(String filename){
         if(filename.endsWith("rar") || filename.endsWith("tar") || filename.endsWith("zip") || filename.endsWith("gz") || filename.endsWith("7z")){
             return false;
         }
         if(filename.endsWith("null")){
             logger.warn(filename+"ͬ���ļ�����δ֪��Դ��չ�����޸�");
             return false;
         }
         String fileType = sourceFile.getFiltertypes();
         String notFileType = sourceFile.getNotfiltertypes();
         if((fileType == null || fileType.equals("")) && (notFileType == null || notFileType.equals(""))){  //�������Ͷ�Ϊ����
             logger.warn("�����ù������ͻ��߲���������");
             return false;
         }
         fileType = fileType.replace("*","");
         notFileType = notFileType.replace("*","");
         if(fileType.equals("") || fileType == null){  //δ���ù�������

         }
         else{
             boolean flag = false;
             if(fileType.contains(",")){ //�������������˶������
                 String[] fileTypes = fileType.split(",");
                 for(int  i = 0 ;i < fileTypes.length ; i++){
                     if(!fileTypes[i].equals(".")){//�������Ͳ�Ϊ*.*
                         if(filename.endsWith(fileTypes[i])){ //�ļ���չ����Ϊ�������͵���չ������false
                             flag = true;
                         }
                     }
                 }
                 if(!flag){
                     return false;
                 }
             }
             else {  //��������������һ������
                 if(!fileType.equals(".")){
                     if(!filename.endsWith(fileType)){
                         return false;
                     }
                 }
             }
         }
         if(notFileType == null || notFileType.equals("")){ //δ���ò���������
             return true;
         }
         else {
             if(notFileType.contains(",")){  //�����˶������������
                 String[] notFileTypes = notFileType.split(",");
                 for(int  i = 0 ;i < notFileTypes.length ; i++){
                     if(!notFileTypes[i].equals(".")){ //���������Ͳ�Ϊ*.*
                         if(filename.endsWith(notFileTypes[i])){  //�ļ���չ���Ͳ���������һ��return false
                             return  false;
                         }
                     }
                     else {  //����������Ϊ*.*  return false
                         return false;
                     }
                 }
             }
             else {
                 if(!notFileType.equals(".")){
                     if(filename.endsWith(notFileType)){//�ļ���չ���Ͳ���������һ��return false
                         return false;
                     }
                 }
                 else { //����������Ϊ*.*  return false
                     return false;
                 }
             }
         }
         return true;
     }
}

