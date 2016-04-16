package com.fartec.ichange.plugin.filechange.utils;

import com.inetec.common.config.nodes.SourceFile;
import com.inetec.common.config.nodes.TargetFile;
import com.inetec.common.exception.Ex;
import jcifs.smb.*;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SambaUtil {
    private static Logger logger = Logger.getLogger(SambaUtil.class);
    private static final String fileListPath = System.getProperty("ichange.home")+"/temp/filelist_smb.xml";
    private static final String fileListHistoryPath = System.getProperty("ichange.home")+"/temp/history/";
    private static long time;


    /**
     * ȷ��filelist_smb.xmlΪ<?xml version="1.0" encoding="utf-8"?><root><filelists/></></root>
     * @param fileListPath
     */
    public static void initFileListSmb() {
        try {
            File fileListSmb = new File(SambaUtil.fileListPath);
            if(fileListSmb.exists()){
                InputStream in = new FileInputStream(fileListSmb);
                SimpleDateFormat sdf = new SimpleDateFormat("'['yyyy-MM-dd_HH-mm-ss']'");
                String history = "filelist_smb" + sdf.format(new Date()) + ".xml";
                String historyPath = fileListHistoryPath + history;
//                File file = new File(historyPath) ;
//                if(!file.exists()){
//                    file.mkdirs();
//                }
                OutputStream out = new FileOutputStream(new File(historyPath));
                byte[] buf = new byte[2 * 1024];
                int len = 0;
                while ((len = in.read(buf))!=-1){
                    out.write(buf,0,len);
                }
                out.flush();
                out.close();
                fileListSmb.delete();
            }
            String str = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<root>\n<filelists> \n</filelists>\n</root>\n ";
            OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(fileListSmb));
            out.write(str);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            logger.info("filelist_smb.xml������");
        } catch (IOException e) {
            logger.debug(e.getMessage());
        }
    }

    /**
     *  ͨ��smbЭ��д���ļ�������
     *  1. ȡ����ȷurl
     *  2. �ж�smbFile�Ƿ��ļ���,��������ļ��� �������óɿɶ�д
     *  3. �ж��Ƿ�����ļ�(����ǰ),������ 4,5,6;���� 7.
     *  4. ������(���Ϻ�׺)
     *  5. �жϼ��Ϻ�׺���url�ļ��Ƿ����,���������ж��Ƿ����ļ��в�����,������,�ٷֳɶ��2M�İ�����
     *  6. �жϼ��Ϻ�׺���url�ļ��Ƿ����,�����жϸ��ļ��Ƿ����Դ���ļ�,�����򸲸�,С��׷��,������ֱ�ӷ���
     *  7. �жϸ��ļ��Ƿ����Դ���ļ�,�����򸲸�,С��׷��,������ֱ�ӷ���
     * @param in      ������
     * @param config   Ŀ���ļ���Ϣ
     */
    public static boolean writeFile(InputStream in,FileBean sourceBean,TargetFile config) {
        String smbUrl = makeSmbUrlEndNotExist(config);
        SmbFile smbFile = null;
        SmbFile reSmbFile = null;
        SmbFileOutputStream out = null;
        String fileFullName = sourceBean.getFullname();
        try {
            reSmbFile = getConnectTargetSmbFile(makeSmbUrlAddFullName(config, fileFullName + FileContext.Str_SyncFileSourceProcess_End_Flag));
            if (reSmbFile == null){
                return false;
            }
            if( !reSmbFile.exists() ){
                String[] files = reSmbFile.getURL().getPath().split("/");
                String url = makeUrlNotWithDir(config);
                for( int i = 1 ; i < files.length - 1; i ++){
                    url += "/"+files[i];
                    SmbFile sFile = getConnectTargetSmbFile(url + "/?" + smbUrl.split("\\?")[1]);
                    if (sFile == null){
                        return false;
                    }
                    if(!sFile.exists()){
                        sFile.mkdir();
                        logger.warn("�������ļ���"+files[i]);
                        sFile.canWrite();
                    }
                }
                reSmbFile.createNewFile();
                reSmbFile.canWrite();
                out = getConnectSmbFileOutputStream(reSmbFile);
                if (out == null){
                    return false;
                }
                return write(in,out);
            } else {

                if((long)reSmbFile.length() < sourceBean.getFilesize()) {
                    out = getConnectSmbFileOutputStream(reSmbFile,true);
                    if (out == null){
                        return false;
                    }
                    return write(in,out);
                } else {
                    out = getConnectSmbFileOutputStream(reSmbFile);
                    if (out == null){
                        return false;
                    }
                    return write(in,out);
                }
            }
        } catch (Exception e) {
            logger.debug("д�ļ�("+sourceBean.getFullname()+"):" + e.getMessage());
            return false;
        }
    }

    private static boolean write(InputStream in, SmbFileOutputStream out) {
        try {
            /*int len = 0;
            byte[] buf = new byte[1024*1024*2];
            while (len != -1){
                len = in.read(buf);
                out.write(buf,0,len);
            }
            out.flush();
            out.close();*/
            IOUtils.copy(in, out);
            in.close();
            out.close();
        } catch (IOException e) {
            logger.debug("д�ļ� " + e.getMessage());
            return false;
        }
        return true;
    }


    /**
     *
     * @param data     ����2MB�İ�
     * @param sourceBean   Դ�ļ���Ϣ
     * @param config
     */
    public static boolean writeFile(byte[] data, FileBean sourceBean, TargetFile config) {
        String smbUrl = makeSmbUrlEndNotExist(config);
        SmbFile smbFile = null;
        SmbFile reSmbFile = null;
        SmbFileOutputStream out = null;
        String fileFullName = sourceBean.getFullname();
        try {
            reSmbFile = getConnectTargetSmbFile(makeSmbUrlAddFullName(config, fileFullName + FileContext.Str_SyncFileSourceProcess_End_Flag));
            if (reSmbFile == null){
                return false;
            }
            if(!reSmbFile.exists() ){       //��������ļ�������
                String[] files = reSmbFile.getURL().getPath().split("/");
                String url = makeUrlNotWithDir(config);
                for( int i = 1 ; i < files.length - 1; i ++){
                    url += "/"+files[i];
                    SmbFile sFile = getConnectTargetSmbFile(url + "/?" + smbUrl.split("\\?")[1]);
                    if (sFile == null){
                        return false;
                    }
                    if(!sFile.exists()){
                        sFile.mkdir();
                        logger.warn("�������ļ���"+files[i]);
                        sFile.canWrite();
                    }
                }
                reSmbFile.createNewFile();
                reSmbFile.canWrite();
                out = getConnectSmbFileOutputStream(reSmbFile);
                if (out == null){
                    return false;
                }
                out.write(data);
                out.flush();
                out.close();
            } else {   //��������ļ�����
                if((long)reSmbFile.length() < sourceBean.getFilesize()) {
                    out = getConnectSmbFileOutputStream(reSmbFile,true);
                    if (out == null){
                        return false;
                    }
                    out.write(data);
                } else {
                    out = getConnectSmbFileOutputStream(reSmbFile);
                    if (out == null){
                        return false;
                    }
                    out.write(data);
                }
                out.flush();
                out.close();
            }
        } catch (Exception e) {
            logger.debug("��MBд�ļ�("+sourceBean.getFullname()+"):" + e.getMessage());
            return false;
        }
        return true;
    }

    public static boolean writeFile(InputStream in, FileBean fileBean, SourceFile config) {
        String smbUrl = makeSmbUrlEndNotExist(config);
        SmbFile smbFile = null;
        SmbFile reSmbFile = null;
        SmbFileOutputStream out = null;
        String fileFullName = fileBean.getFullname();
        try {
            reSmbFile = getConnectSmbFile( makeSmbUrlAddFullName(config,fileFullName + FileContext.Str_SyncFileTargetProcess_End_Flag) );
            if (reSmbFile == null){
                return false;
            }
            if( !reSmbFile.exists() ){
                String[] files = reSmbFile.getURL().getPath().split("/");
                String url = makeUrlNotWithDir(config);
                for( int i = 1 ; i < files.length - 1; i ++){
                    url += "/"+files[i];
                    SmbFile sFile = getConnectSmbFile(url+"/?"+smbUrl.split("\\?")[1]);
                    if (sFile == null){
                        return false;
                    }
                    if(!sFile.exists()){
                        sFile.mkdir();
                        logger.warn("�������ļ���"+files[i]);
                        sFile.canWrite();
                    }
                }
                reSmbFile.createNewFile();
                reSmbFile.canWrite();
                out = getConnectSmbFileOutputStream(reSmbFile);
                if (out == null){
                    return false;
                }
                return write(in,out);
            } else {
                if((long)reSmbFile.length() < fileBean.getFilesize()) {
                    out = getConnectSmbFileOutputStream(reSmbFile,true);
                    if (out == null){
                        return false;
                    }
                    return write(in,out);
                } else {
                    out = getConnectSmbFileOutputStream(reSmbFile);
                    if (out == null){
                        return false;
                    }
                    return write(in,out);
                }
            }
        } catch (Exception e) {
            logger.debug("д�ļ�("+fileBean.getFullname()+"):" + e.getMessage());
            return false;
        }
    }


    public static boolean writeFile(byte[] data, FileBean fileBean, SourceFile config) {
        String smbUrl = makeSmbUrlEndNotExist(config);
        SmbFile smbFile = null;
        SmbFile reSmbFile = null;
        SmbFileOutputStream out = null;
        String fileFullName = fileBean.getFullname();
        try {
            reSmbFile = getConnectSmbFile( makeSmbUrlAddFullName(config,fileFullName+ FileContext.Str_SyncFileTargetProcess_End_Flag));
            if (reSmbFile == null){
                return false;
            }
            if(!reSmbFile.exists() ){       //��������ļ�������
                String[] files = reSmbFile.getURL().getPath().split("/");
                String url = makeUrlNotWithDir(config);
                for( int i = 1 ; i < files.length - 1; i ++){
                    url += "/"+files[i];
                    SmbFile sFile = getConnectSmbFile(url+"/?"+smbUrl.split("\\?")[1]);
                    if (sFile == null){
                        return false;
                    }
                    if(!sFile.exists()){
                        sFile.mkdir();
                        logger.warn("�������ļ���"+files[i]);
                        sFile.canWrite();
                    }
                }
                reSmbFile.createNewFile();
                reSmbFile.canWrite();
                out = getConnectSmbFileOutputStream(reSmbFile);
                if (out == null){
                    return false;
                }
                out.write(data);
                out.flush();
                out.close();
            } else {   //��������ļ�����
                if((long)reSmbFile.length() < fileBean.getFilesize()) {
                    out = getConnectSmbFileOutputStream(reSmbFile,true);
                    if (out == null){
                        return false;
                    }
                    out.write(data);
                } else {
                    out = getConnectSmbFileOutputStream(reSmbFile);
                    if (out == null){
                        return false;
                    }
                    out.write(data);
                }
                out.flush();
                out.close();
            }
        } catch (Exception e) {
            logger.debug("��MBд�ļ�("+fileBean.getFullname()+"):" + e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * �ж�Ŀ¼�Ƿ���"/"Ϊ��β,���Ǹ�Ŀ¼׷��"/",����ֱ�ӷ���
     * @param dir
     * @return
     */
    public static String checkDir(String dir) {
        if(dir.endsWith("/")){
            return dir;
        } else {
            return dir + "/";
        }
    }

    private static String makeUrlNotWithDir(SourceFile config) {
        String server = config.getServerAddress() + ":" + config.getPort();
        String userName = config.getUserName();
        String password = config.getPassword();
        String smbUrl = "smb://" + userName + ":" + password + "@" + server;
        return smbUrl;
    }

    private static String makeUrlNotWithDir(TargetFile config) {
        String server = config.getServerAddress() + ":" + config.getPort();
        String userName = config.getUserName();
        String password = config.getPassword();
        String smbUrl = "smb://" + userName + ":" + password + "@" + server;
        return smbUrl;
    }

    /**
     * ͨ��Դ��������Ϣ��֯url ���׷��"/"
     * @param config
     * @return
     */
    public static String makeSmbUrl(SourceFile config){
        String filePath = config.getServerAddress() + ":" + config.getPort() + config.getDir();
        String userName = config.getUserName();
        String password = config.getPassword();
        String smbUrl = "smb://" + userName + ":" + password + "@" + filePath + "/?iocharset="+config.getCharset();
        return smbUrl;
    }

    /**
     * ͨ��Ŀ��������Ϣ��֯url,���׷��"/"
     * @param config
     * @return
     */
    public static String makeSmbUrl(TargetFile config){
        String filePath = config.getServerAddress() + ":" + config.getPort() + config.getDir();
        String userName = config.getUserName();
        String password = config.getPassword();
        String smbUrl = "smb://" + userName + ":" + password + "@" + filePath + "/?iocharset="+config.getCharset();
        return smbUrl;
    }

    /**
     * ͨ��Դ��������Ϣ��֯url ���׷��"/"
     * @param config
     * @return
     */
    public static String makeSmbUrlEndNotExist(SourceFile config){
        String filePath = config.getServerAddress() + ":" + config.getPort() + config.getDir();
        String userName = config.getUserName();
        String password = config.getPassword();
        String smbUrl = "smb://" + userName + ":" + password + "@" + filePath + "?iocharset="+config.getCharset();
        return smbUrl;
    }

    /**
     * ͨ��Ŀ��������Ϣ��֯url ���׷��"/"
     * @param config
     * @return
     */
    public static String makeSmbUrlEndNotExist(TargetFile config){
        String filePath = config.getServerAddress() + ":" + config.getPort() + config.getDir();
        String userName = config.getUserName();
        String password = config.getPassword();
        String smbUrl = "smb://" + userName + ":" + password + "@" + filePath + "?iocharset="+config.getCharset();
        return smbUrl;
    }

    public static String makeSmbUrlAddFullName(TargetFile config, String fullName) {
        String url = makeSmbUrlEndNotExist(config);
        return url.split("\\?")[0] + fullName + "?" +url.split("\\?")[1];
    }

    public static String makeSmbUrlAddFullName(SourceFile config, String fullName) {
        String url = makeSmbUrlEndNotExist(config);
        return url.split("\\?")[0] + fullName + "?" +url.split("\\?")[1];
    }

    /**
     * ͨ��smbЭ���ȡԴ�˸�Ŀ¼�µ��ļ��б�(�ļ���,��С,Э��·��,�ļ�Md5)
     * @param config   Դ�ļ���Ϣ
     * @param smbFile  ��Ҫ��ȡ���ļ��б���ļ���
     * @return
     */
    public static FileList readFileList(SourceFile config,List<SmbFile> smbFiles) {

        FileList sourceFileList =  new FileList();
        try{
            for (SmbFile smbFile : smbFiles){              //
                String fileName = smbFile.getName();
                boolean isDirectory = smbFile.isDirectory();
                if(smbFile.isFile()) {                        // 4 �õ��ļ��� �ж��Ƿ��ļ�  5 �� ֱ�Ӷ�
                    FileBean fileBean = new FileBean();
                    long length = smbFile.length();
                    if(length > 0){                           //���ļ�����
                        String path = smbFile.getURL().getPath();
                        String fullName = path.split(config.getDir())[1];
                        long lastModified = smbFile.getLastModified();
                        fileBean.setName(fileName);
                        fileBean.setFilesize(length);
                        fileBean.setFullname(fullName);
                        fileBean.setTime(lastModified);
                        fileBean.setMd5(FileBean.Str_FileMd5);
                        sourceFileList.addFileBean(fileBean);
                    }
                }
            }
        } catch (Exception e){
            logger.debug("readFileBeanList()" + e.getMessage());
        }
        return sourceFileList;
    }

    public static List<FileBean> readFileBeanList(TargetFile config,List<SmbFile> smbFiles) {
        List<FileBean> fileBeans = new ArrayList<FileBean>();
        try{
            for (SmbFile smbFile : smbFiles){              //
                String fileName = smbFile.getName();
                boolean isFile = smbFile.isFile();
                if(isFile) {                        // 4 �õ��ļ��� �ж��Ƿ��ļ�  5 �� ֱ�Ӷ�
                    FileBean fileBean = new FileBean();
                    long length = smbFile.length();
                    String path = smbFile.getURL().getPath();
                    String fullName = path.split(config.getDir())[1];
                    long lastModified = smbFile.getLastModified();
                    fileBean.setName(fileName);
                    fileBean.setFilesize(length);
                    fileBean.setFullname(fullName);
                    fileBean.setTime(lastModified);
                    fileBean.setMd5(FileBean.Str_FileMd5);
                    fileBeans.add(fileBean);
                }
            }
        } catch (Exception e){
            logger.debug("readFileBeanList()" + e.getMessage());
        }
        return fileBeans;
    }

    /**
     * �����ļ�ȫ���õ��ļ�
     * @param config
     * @param fullName
     * @return
     */
    public static FileBean readFileBean(TargetFile config, String fullName) {
        String smbUrl = makeSmbUrlAddFullName(config,fullName);
        FileBean fileBean = new FileBean();
        try {
            SmbFile smbFile = getConnectTargetSmbFile(smbUrl);
            if (smbFile == null){
                return null;
            }
            String fileName = smbFile.getName();
            long length = 0;
            try{
                length = smbFile.length();
            } catch (Exception e){

            }
            long lastModified = smbFile.getLastModified();
            fileBean.setName(fileName);
            fileBean.setFilesize(length);
            fileBean.setFullname(fullName);
            fileBean.setTime(lastModified);
            fileBean.setMd5(FileBean.Str_FileMd5);
            return fileBean;
        } catch (Exception e) {
            logger.debug("readFileBean()" + e.getMessage());
        }
        return null;
    }

    public static FileBean readFileBean(SourceFile config, String fullName) {
        String smbUrl = makeSmbUrlAddFullName(config,fullName);
        FileBean fileBean = new FileBean();
        try {
            SmbFile smbFile = getConnectSmbFile(smbUrl);
            if (smbFile == null){
                return null;
            }
            String fileName = smbFile.getName();
            long length = smbFile.length();
            long lastModified = smbFile.getLastModified();
            fileBean.setName(fileName);
            fileBean.setFilesize(length);
            fileBean.setFullname(fullName);
            fileBean.setTime(lastModified);
            fileBean.setMd5(FileBean.Str_FileMd5);
        } catch (Exception e) {
            logger.debug("readFileBean()" + e.getMessage());
        }
        return fileBean;
    }

    /**
     *  ��ȡSmbFile����
     * @param config  Դ�������ļ���Ϣ
     * @return
     */
    public static SmbFile readSmbFile(SourceFile config) {
        String dir = makeSmbUrl(config);
        return getConnectSmbFile(dir);
    }

    public static SmbFile readSmbFile(TargetFile config) {
        String dir = makeSmbUrl(config);
        return getConnectTargetSmbFile(dir);
    }

    /**
     * ����Ҫ����ļ�
     * @param smbFile
     * @param config
     * @return
     */
    public static boolean fixFilter(SmbFile smbFile, SourceFile config) {
        String filterType = null;
        boolean isFilterTypes = false;
        boolean isFilterTypesAll = false;
        if(config.getFiltertypes()!=null&&(config.getNotfiltertypes()==null||config.getNotfiltertypes().equals(""))){
            filterType = config.getFiltertypes();
            isFilterTypesAll = filterType.equals("*.*");
            if(isFilterTypesAll){
                return true;
            } else {
                String[] filterTypes = filterType.split(",");
                if(filterTypes.length>1){
                    for(int i = 0 ; i < filterTypes.length;i++){
                        isFilterTypes = smbFile.getName().endsWith(filterTypes[i].substring(filterTypes[i].lastIndexOf(".")));
                        if(isFilterTypes){
                            return true;
                        }
                    }
                }else if(filterTypes.length == 1){
                    isFilterTypes = smbFile.getName().endsWith(filterType.substring(filterType.lastIndexOf(".")));
                    if(isFilterTypes){
                        return true;
                    }
                }
            }
        }
        if(config.getNotfiltertypes()!=null&& (config.getFiltertypes()==null||config.getFiltertypes().equals(""))){
            filterType = config.getNotfiltertypes();
            isFilterTypesAll = filterType.equals("*.*");
            if(isFilterTypesAll){
                return false;
            } else {
                String[] filterTypes = filterType.split(",");
                if(filterTypes.length>1){
                    int flag = 0;
                    for(int i = 0 ; i < filterTypes.length;i++){
                        isFilterTypes = smbFile.getName().endsWith(filterTypes[i].substring(filterTypes[i].lastIndexOf(".")));
                        if(isFilterTypes){
                            flag ++;
                        }
                    }
                    if (flag == 0){
                        return true;
                    }
                }else if(filterTypes.length == 1){
                    isFilterTypes = smbFile.getName().endsWith(filterType.substring(filterType.lastIndexOf(".")));
                    if(!isFilterTypes){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean fixFilter(SmbFile smbFile, TargetFile config) {
        return true;
    }

    public static List<SmbFile> readSpecialSourceSmbFiles(SmbFile smbFile,TargetFile config){
        List<SmbFile> list = new ArrayList<SmbFile>();
        String flag = FileContext.Str_SyncFileTargetProcess_Flag;
        String flagEnd = FileContext.Str_SyncFileSourceProcess_End_Flag;
        try{
            boolean isDirectory = smbFile.isDirectory();
            if (isDirectory) {
                SmbFile[] smbFiles = smbFile.listFiles();
                for (int i = 0;i<smbFiles.length;i++){
                    if(smbFiles[i].isFile()){
                        if(smbFiles[i].getName().endsWith(flag)){
                            String url = smbFiles[i].getCanonicalPath() +"?" + smbFile.getURL().getQuery();
                            SmbFile sf = getConnectTargetSmbFile(url);
                            if (smbFile != null){
                                list.add(sf);
                            }
                        }else if(smbFiles[i].getName().endsWith(flagEnd)){
                            continue;
                        }
                    }else if( smbFiles[i].isDirectory() ){    //�Ƿ���Ҫ������Ŀ¼
                        smbFile = getConnectTargetSmbFile(smbFiles[i].getCanonicalPath() + "?" + smbFile.getURL().getQuery());
                        if(smbFile != null){
                            isDirectory = smbFile.isDirectory();
                            getIncludeSpecialFileNames(isDirectory, smbFile, list,flag,flagEnd );
                        }
                    }
                }
            }
        }catch (Exception e){
            logger.debug("readSpecialSourceSmbFiles() " + e.getMessage());
        }
        return list;
    }

    /**
     * �ݹ��ȡԴ����Ŀ¼����Ҫ�ϵ��������ļ��б�,
     * @param isDirectory    �Ƿ���Ҫ������Ŀ¼ true����
     * @param smbFile        SmbFile����
     * @param list           ��Ҫ���ص��б�
     * @param flag           Դ�ļ�������ĺ�׺
     * @param flagEnd        Ŀ���ļ�������ĺ�׺
     */
    private static void getIncludeSpecialFileNames(boolean isDirectory, SmbFile smbFile, List<SmbFile> list,String flag,String flagEnd) {
        if (isDirectory){
            try{
                SmbFile[] smbFiles = smbFile.listFiles();
                for (int i = 0;i<smbFiles.length;i++){
                    if(smbFiles[i].isFile()){
                        if(smbFiles[i].getName().endsWith(flag)){
                            String url = smbFiles[i].getCanonicalPath() +"?" + smbFile.getURL().getQuery();
                            SmbFile sf = getConnectSmbFile(url);
                            if (smbFile != null){
                                list.add(sf);
                            }
                        } else if(smbFiles[i].getName().endsWith(flagEnd)){
                            continue;
                        }
                    }else if(smbFiles[i].isDirectory()){
                        smbFile = getConnectSmbFile(smbFiles[i].getCanonicalPath() + "?" + smbFile.getURL().getQuery());
                        if(smbFile != null){
                            isDirectory = smbFile.isDirectory();
                            getIncludeSpecialFileNames(isDirectory, smbFile, list,flag,flagEnd );
                        }
                    }
                }
            } catch (Exception e){
                logger.debug("getIncludeSpecialFileNames() " + e.getMessage());
            }
        }
    }

    /**
     * Ŀ���ļ�ͬ����ɺ� �Ļ�ԭ��
     * @param fileFullName    Ŀ����
     * @param config     Ŀ��������Ϣ
     */
    public static boolean reNameTarget(String fileFullName, TargetFile config) {
        String smbUrl = makeSmbUrlEndNotExist(config);
        SmbFile smbFile = null;
        SmbFile reSmbFile = null;

        if(fileFullName.endsWith(FileContext.Str_SyncFileSourceProcess_End_Flag)){
            try {
                reSmbFile = getConnectTargetSmbFile(makeSmbUrlAddFullName(config, fileFullName.substring(0, fileFullName.lastIndexOf(FileContext.Str_SyncFileSourceProcess_End_Flag))));
                if (reSmbFile == null){
                    return false;
                }
                if(reSmbFile.exists()){
                    smbFile.delete();
                }
                smbFile = getConnectTargetSmbFile(makeSmbUrlAddFullName(config, fileFullName));
                if ((smbFile == null)){
                    return false;
                }
                smbFile.renameTo(reSmbFile);
            } catch (SmbException e) {
            } catch (IOException e) {
                logger.debug("reNameTarget("+fileFullName+") " +e.getMessage());
                return false;
            }
        }
        return true;
    }

    public static boolean reNameTarget(String fileFullName, SourceFile config) {
        String smbUrl = makeSmbUrlEndNotExist(config);
        SmbFile smbFile = null;
        SmbFile reSmbFile = null;
        try {
            smbFile = getConnectSmbFile(makeSmbUrlAddFullName(config,fileFullName));
            if (smbFile == null){
                return false;
            }
            reSmbFile = getConnectSmbFile(makeSmbUrlAddFullName(config,fileFullName.substring(0,fileFullName.indexOf(FileContext.Str_SyncFileTargetProcess_End_Flag))));
            if (reSmbFile == null){
                return false;
            }
            smbFile.renameTo(reSmbFile);
        } catch (SmbException e) {
        } catch (IOException e) {
            logger.debug("reNameTarget("+fileFullName+") " +e.getMessage());
            return false;
        }
        try{
            smbFile = getConnectSmbFile(makeSmbUrlAddFullName(config,fileFullName.substring(0,fileFullName.indexOf(FileContext.Str_SyncFileTargetProcess_End_Flag))));
            if (smbFile == null){
                return false;
            }
            reSmbFile = getConnectSmbFile(makeSmbUrlAddFullName(config,fileFullName));
            if (reSmbFile == null){
                return false;
            }
            if(smbFile.exists() && reSmbFile.exists()){
                reSmbFile.delete();
            }
        } catch (Exception e){
            return false;
        }
        return true;
    }

    /**
     * Ŀ���ֻ���Ӹ���
     * @param fileFullName  Ŀ���ļ�ԭ��
     * @param config    Ŀ��������Ϣ
     */
    public static void reNameTargetOnlyAdd(String fileFullName, TargetFile config) {
        SmbFile smbFile = null;
        SmbFile reSmbFile = null;
        String newFileFullName = getNewFileFullName(fileFullName);
        try {
            smbFile = getConnectSmbFile(makeSmbUrlAddFullName(config, fileFullName));
            if (smbFile == null){
                return;
            }
            reSmbFile = getConnectSmbFile(makeSmbUrlAddFullName(config,newFileFullName));
            if (reSmbFile == null){
                return;
            }
            smbFile.renameTo(reSmbFile);
        } catch (SmbException e) {
        } catch (IOException e) {
            logger.debug("[SMBͬ��]�����쳣!");
        }
    }

    private static String getNewFileFullName(String fileFullName) {
        String[] strs = fileFullName.split("\\.");
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("_yyyy-MM-dd_HH-mm-ss-SSS");//
        String time = sdf.format(date);
        String newFileFullName = strs[0] + time;
        for (int i = 1;i < strs.length; i++){
            newFileFullName += "." + strs[i] ;
        }
        return newFileFullName;
    }



    /**
     * ɾ���Ȼ����ж���ļ�
     * @param config
     * @param sourceFileList
     * @param extendsFile
     */
    public static void deleteFile(TargetFile config, FileList sourceFileList, int extendsFile) {
        time = sourceFileList.getTime();
        if ( extendsFile == 0 ) {
            List<SmbFile> targetSmbFiles = SambaUtil.readTargetSmbFiles(config);       //С��200���ļ�
            if( targetSmbFiles.size() > 0){
                deleteFile(targetSmbFiles,config);
            }
        }
        initFileListSmb();
    }

    public static void deleteFileList(long time){
        Configuration configuration;
        try {
            configuration = new Configuration(fileListPath);
            configuration.deleteFileList(time);            //ɾ�������е�filelist
            configuration.save();
        } catch (Ex ex) {
            logger.debug(ex.getMessage());
        }
    }

    /**
     * ��ȡĿ��˵������ļ���
     *
     * @param config     Ŀ���������Ϣ
     * @param flag
     * @return
     */
    private static List<SmbFile> readTargetSmbFiles(TargetFile config) {
        List<SmbFile> list = new ArrayList<SmbFile>();
        SmbFile smbFile = null;
        try{
            String url = makeSmbUrl(config);
            smbFile  = getConnectTargetSmbFile(url);
            if (smbFile !=null){
                boolean isDirectory = smbFile.isDirectory();
                list = getIncludeTargetFileNames(isDirectory, smbFile, list,config,false);
                if(list.size()>0){
                    list = deleteFile(list,config);
                }
            }
        }catch (Exception e){
            logger.debug("readTargetSmbFiles()" + e.getMessage());
            return null;
        }
        return list;
    }

    /**
     *
     * @param isDirectory
     * @param smbFile
     * @param list
     * @param config
     * @param isIncludes    true������Ŀ¼
     * @return
     */
    private static List<SmbFile> getIncludeTargetFileNames(boolean isDirectory, SmbFile smbFile, List<SmbFile> list, TargetFile config,boolean isIncludes) {
        if (isDirectory){
            try{
                SmbFile[] smbFiles = smbFile.listFiles(new SmbFilenameFilter() {
                    public boolean accept(SmbFile smbFile, String s) throws SmbException {
                        return true;
                    }
                });
                if(smbFiles.length == 0 && isIncludes){
                    list.add(smbFile);
                }
                for (int i = 0;i<smbFiles.length;i++){
                    if(smbFiles[i].isFile()){
                        String url = smbFiles[i].getCanonicalPath() +"?iocharset=" + config.getCharset();
                        SmbFile sf = getConnectTargetSmbFile(url);
                        if(sf != null){
                            list.add(sf);
                            if(list.size() == config.getFilelistsize()){
                                list = deleteFile(list,config);
                            }
                        }
                    }
                }
                SmbFile[] smbFileDs = smbFile.listFiles(new SmbFileFilter() {
                    public boolean accept(SmbFile smbFile) throws SmbException {
                        if(smbFile.isDirectory()){
                            return true;
                        }
                        return false;
                    }
                });
                for (int i = 0;i<smbFileDs.length;i++){
                    if(smbFileDs[i].isDirectory()){
                        list = getIncludeTargetFileNames(isDirectory, smbFileDs[i], list, config,true);
                    }
                }
            } catch (Exception e){
                logger.debug("getIncludeSpecialFileNames() " + e.getMessage());
            }
        }
        return list;
    }

    /**
     * ɾ�������ļ��в����ڵ��ļ�
     *
     * @param list
     * @param flag
     * @return
     */
    private static List<SmbFile> deleteFile(List<SmbFile> list,TargetFile config){
        for(SmbFile smbFile : list){
            String path = smbFile.getURL().getPath();
            String targetFullName = path.split(config.getDir())[1];
            Configuration configuration = null;
            try {
                configuration = new Configuration(fileListPath);
            } catch (Ex ex) {
                ex.printStackTrace();
            }
            boolean isExist = configuration.isExist(targetFullName,time);    //�����д���
            if(!isExist){
                try {
                    smbFile.delete();
                    logger.info("[SMBͬ��]ɾ��Ŀ��˶����ļ� "+targetFullName+ " �ɹ�!");
                } catch (SmbException e) {
                    logger.error("[SMBͬ��]ɾ��Ŀ��˶����ļ� "+targetFullName+ " ʧ��,��һ���ڼ���!"+e.getMessage());
                }
            }
        }
        list = new ArrayList<SmbFile>();
        return list;
    }

    public static SmbFile getSmbFile(String url){
        SmbFile smbFile = null;
        try {
            smbFile = new SmbFile(url);
            smbFile.connect();
            return smbFile;
        } catch (MalformedURLException e) {
        } catch (IOException e) {
            logger.error("[SMBͬ��]"+e.getMessage());
        }
        return null;
    }
    public static SmbFile getConnectSmbFile(String url){

        SmbFile smbFile = getSmbFile(url);
        int index = 1;
        while (smbFile == null) {
            if(index > 5){
                logger.info("[SMBͬ��]�������ӳ�ʱ");
                return null;
            }
            logger.info("[SMBͬ��]��������ʧ��,�ȴ�60��..");
            try {
                Thread.sleep(1000*60);
                index ++;
            } catch (InterruptedException e) {
            }
            smbFile = getSmbFile(url);
        }
        boolean isExist = false;
        boolean isD = false;
        try {
            isExist = smbFile.exists();
            isD = smbFile.isDirectory();
        } catch (SmbException e) {
            logger.error("[SMBͬ��]"+e.getMessage());
        }
        if(isExist){
            return smbFile;
        }else if(isD) {
            logger.error("[SMBͬ��]��Ŀ¼������!");
            return null;
        }
        return null;
    }

    public static SmbFile getConnectTargetSmbFile(String url){

        SmbFile smbFile = getSmbFile(url);
        int index = 1;
        while (smbFile == null) {
            if(index > 5){
                logger.info("[SMBͬ��]�������ӳ�ʱ");
                return null;
            }
            logger.info("[SMBͬ��]��������ʧ��,�ȴ�60��..");
            try {
                Thread.sleep(1000*60);
                index ++;
            } catch (InterruptedException e) {
            }
            smbFile = getSmbFile(url);
        }
//        String[] files = smbFile.getURL().getPath().split("/");
//        String u = smbFile.getCanonicalPath().split(smbFile.getURL().getPath())[0];
//        for( int i = 1 ; i < files.length - 1; i ++){
//            u += "/"+files[i];
//            try{
//                SmbFile sFile = new SmbFile(u+"/?"+url.split("\\?")[1]);
//                if(!sFile.exists()){
//                    sFile.mkdir();
//                    logger.warn("�������ļ���"+files[i]);
//                    sFile.canWrite();
//                }
//            } catch (Exception e) {
//
//            }
//
//        }
        return smbFile;
    }


    public static InputStream getConnectSmbFileInputStream(SmbFile smbFile) {
        InputStream in = null;
        try {
            if(smbFile.exists()){
                in = getSmbFileInputStream(smbFile);
            }else{
                return null;
            }
        } catch (SmbException e) {
            logger.info(e.getMessage());
        }
        int index = 1;
        while (in == null){
            if(index > 5){
                logger.info("[SMBͬ��]�������ӳ�ʱ");
                return null;
            }
            logger.info("[SMBͬ��]��������ʧ��,�ȴ�60��..");
            try {
                Thread.sleep(1000*60);
                index ++;
            } catch (InterruptedException e) {
            }
            try {
                if(smbFile.exists()){
                    in = getSmbFileInputStream(smbFile);
                }else{
                    return null;
                }
            } catch (SmbException e) {
                logger.info("[SMBͬ��]"+e.getMessage());
            }
        }
        return in;
    }

    private static InputStream getSmbFileInputStream(SmbFile smbFile) {
        InputStream in = null;
        try {
            return in = new SmbFileInputStream(smbFile);
        } catch (SmbException e) {
            logger.info("[SMBͬ��]"+e.getMessage());
        } catch (MalformedURLException e) {
            logger.info("[SMBͬ��]"+e.getMessage());
        } catch (UnknownHostException e) {
            logger.info("[SMBͬ��]"+e.getMessage());
        }
        return null;
    }


    private static SmbFileOutputStream getConnectSmbFileOutputStream(SmbFile smbFile) {
        SmbFileOutputStream out = getSmbFileOutputStream(smbFile);
        int index = 1;
        while (smbFile == null){
            if(index > 5){
                logger.info("[SMBͬ��]�������ӳ�ʱ");
                return null;
            }
            logger.info("[SMBͬ��]��������ʧ��,�ȴ�60��..");
            try {
                Thread.sleep(1000*60);
                index ++;
            } catch (InterruptedException e) {
            }
            out = getSmbFileOutputStream(smbFile);
        }
        return out;
    }

    private static SmbFileOutputStream getSmbFileOutputStream(SmbFile smbFile) {
        SmbFileOutputStream out = null;
        try {
            return out = new SmbFileOutputStream(smbFile);
        } catch (SmbException e) {
            logger.info("[SMBͬ��]"+e.getMessage());
        } catch (MalformedURLException e) {
            logger.info("[SMBͬ��]"+e.getMessage());
        } catch (UnknownHostException e) {
            logger.info("[SMBͬ��]"+e.getMessage());
        }
        return null;
    }

    private static SmbFileOutputStream getConnectSmbFileOutputStream(SmbFile smbFile,boolean append) {
        SmbFileOutputStream out = getSmbFileOutputStream(smbFile,append);
        int index = 1;
        while (out == null){
            if(index > 5){
                return null;
            }
            logger.info("[SMBͬ��]��������ʧ��,�ȴ�60��..");
            try {
                Thread.sleep(1000*60);
                index ++;
            } catch (InterruptedException e) {
            }
            out = getSmbFileOutputStream(smbFile,append);
        }
        return out;
    }

    private static SmbFileOutputStream getSmbFileOutputStream(SmbFile smbFile,boolean append) {
        SmbFileOutputStream out = null;
        try {
            return out = new SmbFileOutputStream(smbFile,append);
        } catch (SmbException e) {
            logger.info("[SMBͬ��]"+e.getMessage());
        } catch (MalformedURLException e) {
            logger.info("[SMBͬ��]"+e.getMessage());
        } catch (UnknownHostException e) {
            logger.info("[SMBͬ��]"+e.getMessage());
        }
        return null;
    }
}