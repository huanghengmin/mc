package com.fartec.ichange.plugin.filechange.source.plugin;

import com.fartec.ichange.plugin.filechange.source.SourceOperation;
import com.fartec.ichange.plugin.filechange.target.plugin.ITargetProcess;
import com.fartec.ichange.plugin.filechange.utils.*;
import com.inetec.common.config.nodes.SourceFile;
import com.inetec.common.io.IOUtils;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileFilter;
import jcifs.smb.SmbFilenameFilter;
import org.apache.log4j.Logger;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by IntelliJ IDEA.
 * User: qxp
 * Date: 2012-04-27
 * Time: pm3:00
 *  ʵ���ļ������ͬ��,
 */
public class SourceProcessSmb implements ISourceProcess {
    private Logger logger = Logger.getLogger(SourceProcessSmb.class);
    private ITargetProcess targetProcess;   // targetProcess
    private SourceOperation source;
    private SourceFile config;
    private boolean isRun = false;
    private static long interval = 1;
    private static int MB = 1024*1024;
    private long time;
    private ExecutorService executor;
    private boolean isfilter;
    private boolean isvirusscan;
    private class MySourceRunnable implements Runnable {
        private  FileList sourceFileList;
        public MySourceRunnable( FileList sourceFileList) {
            this.sourceFileList = sourceFileList;
        }
        public void run() {
            copySourceFile(sourceFileList);
        }
    }

    public SourceProcessSmb() {
    }

    public void init(SourceOperation source, SourceFile config) {
        this.source = source;
        this.config = config;
        isfilter = source.getType().isFilter();
        isvirusscan = source.getType().isVirusScan();
    }

    public void init(ITargetProcess targetProcess ,SourceFile config) {
        this.targetProcess = targetProcess;
        this.config = config;
    }

    public void stop() {
        isRun =  false;
        interval = 0;
        logger.info("SMB�ļ�ͬ��ֹͣ!");
    }

    public boolean isRun() {
        return isRun;
    }

    /**
     * ������Դ�˶˴���
     * @param in   ������
     * @param sourceBean  Ŀ��˶��ļ���Ϣ
     * @return  ����true�����,false�����
     */
    public boolean process(InputStream in, FileBean fileBean) {
        boolean isOk = SambaUtil.writeFile(in, fileBean,config);       // ��������д��Ŀ���ļ�
        if(isOk && fileBean.getSyncflag().equals(FileContext.Str_SyncFileEnd)){                     // �Ƚϵ�ǰĿ���ļ���Դ�ļ��Ĵ�С,���ʱ����true,��ʾ���ļ�ͬ�����
            //TODO ͬ����� ȥ��Ŀ����ļ��ı�־��׺
            String targetFullName = getTargetFileFullName(fileBean);
            isOk = SambaUtil.reNameTarget(targetFullName,config);
        }
        return isOk;
    }

    public boolean process(byte[] data, FileBean fileBean) {
        boolean isOk = SambaUtil.writeFile(data, fileBean,config);       // ��������д��Ŀ���ļ�
        if(isOk && fileBean.getSyncflag().equals(FileContext.Str_SyncFileEnd)){                      // �Ƚϵ�ǰĿ���ļ���Դ�ļ��Ĵ�С,���ʱ����true,��ʾ���ļ�ͬ�����
            //TODO ͬ����� ȥ��Ŀ����ļ��ı�־��׺
            String targetFullName = getTargetFileFullName(fileBean);
            isOk = SambaUtil.reNameTarget(targetFullName,config);
        }
        return isOk;
    }

    private String getTargetFileFullName(FileBean sourceBean) {
        String targetFullName = sourceBean.getFullname();
        if(sourceBean.getFullname().endsWith(FileContext.Str_SyncFileTargetProcess_Flag)){
            targetFullName = sourceBean.getFullname().split(FileContext.Str_SyncFileTargetProcess_Flag)[0]
                    + FileContext.Str_SyncFileTargetProcess_End_Flag;
        }else{
            targetFullName = targetFullName + FileContext.Str_SyncFileTargetProcess_End_Flag;
        }
        return targetFullName;
    }

    private FileBean getTargetFileBean(FileBean sourceBean) {
        String targetFullName = sourceBean.getFullname();
        if(sourceBean.getFullname().endsWith(FileContext.Str_SyncFileTargetProcess_Flag)){
            targetFullName = sourceBean.getFullname().split(FileContext.Str_SyncFileTargetProcess_Flag)[0]
                    +FileContext.Str_SyncFileTargetProcess_End_Flag;
        }
        return SambaUtil.readFileBean(config,targetFullName);
    }

    public FileList procesFileList(FileList list) {
        FileList fileList = new FileList();
        Iterator<FileBean> sourceIte = list.iterable();
        while(sourceIte.hasNext()){
            FileBean sourceFileBean = sourceIte.next();
            FileBean targetFileBean = getTargetFileBean(sourceFileBean);
            if(sourceFileBean.getFullname().endsWith(FileContext.Str_SyncFileTargetProcess_Flag)){
                if (targetFileBean.getFilesize()>sourceFileBean.getFilesize()){
                    sourceFileBean.setFilepostlocation(0);
                }else {
                    sourceFileBean.setFilepostlocation(targetFileBean.getFilesize());
                }
                fileList.addFileBean(sourceFileBean);
            }
            if(targetFileBean.getTime() == 0){
                sourceFileBean.setFilepostlocation(0);
                fileList.addFileBean(sourceFileBean);
            }
        }
        return fileList;
    }

    /**
     * 1.��ȡԴ�������ļ�������
     * 2.������ȡԴ���ļ��б�
     * 3.���̴߳���
     * 4.�Ƿ���Ҫɾ��
     */
    public void run() {
        //TODO run()
        isRun = true;
        while (isRun()){
            logger.info("��"+ (interval) +"��Smb"+(config.isIstwoway()?"[Դ��-->Ŀ���]�ļ�ͬ��":"�ļ�ͬ��")+"���ڿ�ʼ!");
            if(config.getNotfiltertypes()!=null
                    &&config.getNotfiltertypes().equals("*.*")){
            }else{
                this.executor = Executors.newFixedThreadPool(config.getThreads());
                this.time = System.currentTimeMillis();
                SmbFile smbFile = SambaUtil.readSmbFile(config);
                if(smbFile != null){
                    boolean isSendSuccess = sendSuccess(smbFile,config);
                    executor.shutdown();
                    if(isSendSuccess){
                        boolean isTerminated = executor.isTerminated();
                        while (!isTerminated){
                            logger.debug("��"+ (interval) +"��Smb"+(config.isIstwoway()?"[Դ��-->Ŀ���]�ļ�ͬ��":"�ļ�ͬ��")+"����û�н���,�ȴ�10��..");
                            try{
                                Thread.sleep(1000*10);
                                isTerminated = executor.isTerminated();
                            } catch (InterruptedException e) {
                                logger.debug(e.getMessage());
                            }
                        }
                        if(!config.isIstwoway()&&!config.isDeletefile()){ //�ж��Ƿ�˫��ͬ��,�����ô���ɾ������
                            removeTargetFile();
                        }
                    }
                }
            }
            logger.info("��"+ (interval) +"��Smb"+(config.isIstwoway()?"[Դ��-->Ŀ���]�ļ�ͬ��":"�ļ�ͬ��")+"���ڽ���!�ȴ�"+config.getInterval()/1000+"��..");
            try {
                Thread.sleep(config.getInterval());
            } catch (InterruptedException e) {
                logger.debug(e.getMessage());
            }
            interval ++;
        }
    }

    /**
     * ���͵�Ŀ���ɾ��Ŀ����ĵı��
     */
    private void removeTargetFile() {
        FileList removeFileList = new FileList();
        removeFileList.setTime(time);
        removeFileList.setSyncFileListType(FileContext.Str_SyncFileListType_Delete);
        if(config.isDeletefile()){
            removeFileList.setSyncFileListFlag(FileContext.Str_SyncFileListType_Delete);
        }
        try{
            source.procesFileList(removeFileList);
        } catch (Exception e){
            logger.info("[SMBͬ��]�����쳣,ɾ��Ŀ����ļ�ʧ��!");
            return ;
        }
    }

    private boolean sendSuccess(SmbFile smbFile, SourceFile config) {
        boolean isSmbFile = sendSpecialSourceSmbFiles(smbFile, config);
        isSmbFile = sendSourceSmbFiles(smbFile,config);
        return isSmbFile;
    }

    /**
     * ��ȡ��Ҫ�ϵ��������ļ��б�
     * @param smbFile   SmbFile����
     * @param config   Դ��������Ϣ
     * @return      ��Ҫ�ϵ�����
     */
    public boolean sendSpecialSourceSmbFiles(SmbFile smbFile,SourceFile config){
        List<SmbFile> list = new ArrayList<SmbFile>();
        try{
            boolean isDirectory = smbFile.isDirectory();
            if (isDirectory) {
                list = getIncludeSpecialFileNames(isDirectory,smbFile,list,config);
                if(list == null){
                    return false;
                } else if(list.size() > 0){
                    sendDDFile(list);
                }
            }
        }catch (Exception e){
            logger.debug("sendSpecialSourceSmbFiles() " + e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * ��ȡSmbFile�б�
     * @param smbFile    SmbFile����
     * @param config    Դ��������Ϣ
     * @return
     */
    public boolean sendSourceSmbFiles(SmbFile smbFile,SourceFile config){
        List<SmbFile> list = new ArrayList<SmbFile>();
        try{
            boolean isDirectory = smbFile.isDirectory();
            if (isDirectory) {
                list = getIncludeFileNames(isDirectory, smbFile, list,config);
                if(list == null){
                    return false;
                } else  if(list.size() > 0){
                    sendFile(list);
                }
            }
        }catch (Exception e){
            logger.debug("sendSourceSmbFiles()" + e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * �ݹ��ȡԴ����Ŀ¼����Ҫ�ϵ��������ļ��б�,
     * @param isDirectory    �Ƿ���Ҫ������Ŀ¼ true����
     * @param smbFile        SmbFile����
     * @param list           ��Ҫ���ص��б�
     * @param flag           Դ�ļ�������ĺ�׺
     * @param flagEnd        Ŀ���ļ�������ĺ�׺
     */
    private List<SmbFile> getIncludeSpecialFileNames(boolean isDirectory, SmbFile smbFile, List<SmbFile> list,SourceFile config) {
        if (isDirectory){
            try{
                SmbFilenameFilter fileNameFilter = new SambaFilterSpecialName(config);
                SmbFile[] smbFileNames = smbFile.listFiles(fileNameFilter);
                for (int i = 0;i<smbFileNames.length;i++){

                    if(smbFileNames[i].isFile()){
                        String _url = smbFileNames[i].getCanonicalPath() + "?iocharset=" + config.getCharset();
                        String canonicalPath = smbFileNames[i].getCanonicalPath();
                        String name = smbFileNames[i].getName();
                        String fileName = name.substring(5,name.lastIndexOf("_"));
                        long time = Long.parseLong(name.substring(name.lastIndexOf("_")+1,name.lastIndexOf(FileContext.Str_Lnk)));
                        String url = canonicalPath.substring(0,canonicalPath.lastIndexOf("/")+1) + fileName +"?iocharset=" + config.getCharset();
                        SmbFile sf = SambaUtil.getConnectSmbFile(url);
                        if(sf != null){
                            long lastModified = sf.getLastModified();
                            if(lastModified == time){
                                list.add(sf);
                                if(list.size() == config.getFilelistsize()){
                                    list = sendDDFile(list);
                                }
                            }else {
                                sf = SambaUtil.getConnectSmbFile(_url);
                                if(sf!=null){
                                    sf.delete();
                                    logger.info("[SMB]Դ����ʱ�ļ���Ӧ��ԭ�ļ����޸�,ɾ����ʱ�ļ�"+name+"!");
                                }
                            }
                        }else{
                            sf = SambaUtil.getConnectSmbFile(_url);
                            if(sf!=null){
                                sf.delete();
                                logger.info("[SMB]Դ����ʱ�ļ���Ӧ��ԭ�ļ�������,ɾ����ʱ�ļ�"+name+"!");
                            }
                            return null;
                        }
                    }
                }
                if(config.isIsincludesubdir()){
                    SmbFileFilter fileFilter = new SambaFilterFile();
                    SmbFile[] smbFiles = smbFile.listFiles(fileFilter);
                    for (int i = 0;i<smbFiles.length;i++){
                        if(smbFiles[i].isDirectory()){
                            list = getIncludeSpecialFileNames(isDirectory,smbFiles[i],list,config);
                        }
                    }
                }
            } catch (Exception e){
                logger.debug("getIncludeSpecialFileNames()" + e.getMessage());
                logger.error( e.getMessage());
                return null;
            }
        }
        return list;
    }

    /**
     * �ݹ��ȡԴ����Ŀ¼�µ��ļ��б�,
     * @param isDirectory    �Ƿ���Ҫ������Ŀ¼ true����
     * @param smbFile        SmbFile����
     * @param list           ��Ҫ���ص��б�
     * @param flag           Դ�˲���Ҫ��ȡ���ļ���׺���
     * @param config
     */
    private List<SmbFile> getIncludeFileNames(boolean isDirectory, SmbFile smbFile, List<SmbFile> list, SourceFile config) {
        if (isDirectory){
            try{
                SmbFilenameFilter fileNameFilter = new SambaFilterName(config,smbFile.getPath());
                SmbFile[] smbFileNames = smbFile.listFiles(fileNameFilter);
                for (int i = 0;i<smbFileNames.length;i++){
                    if(smbFileNames[i].isFile()){
                        String url = smbFileNames[i].getCanonicalPath() +"?iocharset=" + config.getCharset();
                        SmbFile sf = SambaUtil.getConnectSmbFile(url);
                        if(sf != null){
                            list.add(sf);
                            if(list.size() == config.getFilelistsize()){
                                list = sendFile(list);
                            }
                        }else{
                            return null;
                        }
                    }
                }
                if(config.isIsincludesubdir()){
                    SmbFileFilter fileFilter = new SambaFilterFile();
                    SmbFile[] smbFiles = smbFile.listFiles(fileFilter);
                    for (int i = 0;i<smbFiles.length;i++){
                        if(smbFiles[i].isDirectory()){
                            list = getIncludeFileNames(isDirectory,smbFiles[i],list,config);
                        }
                    }
                }
            } catch (Exception e){
                logger.debug("getIncludeFileNames()" + e.getMessage());
                logger.info("�����쳣,��һ���ڼ���!");
                return null;
            }
        }
        return list;
    }

    /**
     * ����һ���ļ�
     * @param list
     * @return
     */
    private List<SmbFile> sendFile(List<SmbFile> list){
        FileList sourceFileList = SambaUtil.readFileList(config, list);            //��ǰ���ε�ͬ���ļ�
        sourceFileList.setTime(time);
        sourceFileList.setSyncFileListType(FileContext.Str_SyncFileListType_Normal);
        sourceFileList.setSyncFileListFlag(FileContext.Str_SyncFileListStart);
        sourceFileList.setSyncFileListDD(false);   //�ϵ�����

        // TODO �����߳�,���ж��̴߳���
        Runnable command = new MySourceRunnable(sourceFileList);
        executor.execute(command);
        list = new ArrayList<SmbFile>();
        return list;

    }

    private List<SmbFile> sendDDFile(List<SmbFile> list) {
        FileList sourceFileList = SambaUtil.readFileList(config, list);            //��ǰ���ε�ͬ���ļ�
        sourceFileList.setTime(time);
        sourceFileList.setSyncFileListType(FileContext.Str_SyncFileListType_Normal);
        sourceFileList.setSyncFileListFlag(FileContext.Str_SyncFileListStart);
        sourceFileList.setSyncFileListDD(true);   //�ϵ�����

        // TODO �����߳�,���ж��̴߳���
        Runnable command = new MySourceRunnable(sourceFileList);
        executor.execute(command);
        list = new ArrayList<SmbFile>();
        return list;
    }
    /**
     * 1.����Դ���б�
     * 2.�Ƚ��б��е��ļ��Ƿ������Ŀ���,������Ҫͬ�����ļ��б�
     * 3.�ļ�ͬ����ʼ
     * 4.�ļ�ͬ����������
     * @param sourceFileList
     */
    private void copySourceFile( FileList sourceFileList) {
        // TODO �����Ѿ����ڵ��ļ�
        FileList compareFileList = null;              // ��ȡ�ȽϺ���ļ��б�
        try{
            compareFileList = source.procesFileList(sourceFileList);
        } catch (Exception e){
            logger.info("[SMBͬ��]"+e.getMessage()+",�������߳�!");
            return ;
        }
        // TODO ͬ��
        if(compareFileList.size()>0){
            Iterator<FileBean> iterator = compareFileList.iterable();
            while (iterator.hasNext()){
                FileBean fileBean = iterator.next();
                String url = SambaUtil.makeSmbUrlAddFullName(config, fileBean.getFullname());
                try {
                    SmbFile sf = SambaUtil.getConnectSmbFile(url);
                    if(sf == null){
                        return;
                    }
                    //TODO ������ʱ����ļ�
                    String canonicalPath = sf.getCanonicalPath();
                    String name = sf.getName();
                    String fileName = FileContext.Str_Sync + name + "_" + fileBean.getTime() + FileContext.Str_Lnk;
                    String _url = canonicalPath.substring(0,canonicalPath.lastIndexOf("/")+1) + fileName +"?iocharset=" + config.getCharset();
                    SmbFile _sf =  new SmbFile(_url);
                    if(_sf == null){
                        return;
                    }
                    if(!_sf.exists()){
                        _sf.createNewFile();
                    }
                    //TODO �����ļ������MB�����ֽ�
                    boolean  isRead = process(sf,fileBean);
                    if(!isRead){
                        logger.info("[SMBͬ��]�����쳣,�������߳�!");
                        return;
                    }
                    //TODO ɾ����ʱ����ļ���ɾ��ͬ����ɵ��ļ�
                    boolean  isReName = endSource(_url,sf,fileBean);
                    logger.info("[SMBͬ��]"+fileBean.getFullname()+"ͬ�����!");
                    if(!isReName){
                        return;
                    }
                } catch (Exception e) {
                    logger.debug("copyFile()"+e.getMessage());
                    return;
                }
            }
        }
    }

    /**
     *  ��������Ŀ���,���ͬ��,���� false��ʾ����˵�ǰ�ļ���ͬ��
     *  1.�ж��Ƿ����Ŀ���ȫ��  ��Ϊ�ռ���
     *
     * @param sf    Դ����
     * @param sourceFileBean    Դ���ļ���Ϣ
     * @return  true��ʾ���
     */
    private boolean process(SmbFile sf,FileBean fileBean) {
        if(FileType.REAL_NAME_MAP.get(fileBean.getName()) == null){
            fileBean.setRealname(fileBean.getName());
        }else {
            fileBean.setRealname(FileType.REAL_NAME_MAP.get(fileBean.getName()));
        }
        boolean isWrite = true;
        InputStream in = null;
        try {
            in = SambaUtil.getConnectSmbFileInputStream(sf);
            if(in == null){
                logger.info("[SMBͬ��]��ȡԴ���ļ�ʧ��,�����ڼ���!");
                return true;
            }
            in.skip(fileBean.getFilepostlocation());
            int len = 0;
            byte[] buf = new byte[config.getPacketsize()*MB];
            fileBean.setFile_flag(FileContext.Str_SourceFile);
            if( isfilter && (fileBean.getRealname().endsWith(".txt") || fileBean.getRealname().endsWith(".rtf"))){
                while((len = in.read(buf))!=-1){
                    if(len < config.getPacketsize()*MB){
                        fileBean.setSyncflag(FileContext.Str_SyncFileEnd);
                        buf = IOUtils.copyArray(buf,len);
                        try{
                            byte[] data = KeywordsFilterFactory.getKeywordsFilterUtil(fileBean.getRealname()).filter(buf,Basic.getKeywords(),config);
                            isWrite = source.process(data, fileBean);
                        } catch (Exception e){
                            return false;
                        }
                    }else{
                        fileBean.setSyncflag(FileContext.Str_SyncFileIng);
                        try{
                            byte[] data = KeywordsFilterFactory.getKeywordsFilterUtil(fileBean.getRealname()).filter(buf,Basic.getKeywords(),config);
                            isWrite = source.process(data, fileBean);
                        } catch (Exception e){
                            return false;
                        }
                    }
                    if(isWrite == false){
                        return false;
                    }
                }
                in.close();
            }
            else {
                while((len = in.read(buf))!=-1){
                    if(len < config.getPacketsize()*MB){
                        fileBean.setSyncflag(FileContext.Str_SyncFileEnd);
                        buf = IOUtils.copyArray(buf,len);
                        try{
                            isWrite = source.process(buf, fileBean);
                        } catch (Exception e){
                            return false;
                        }
                    }else{
                        fileBean.setSyncflag(FileContext.Str_SyncFileIng);

                        try{
                            isWrite = source.process(buf, fileBean);
                        } catch (Exception e){
                            return false;
                        }
                    }
                    if(isWrite == false){
                        return false;
                    }
                }
                in.close();
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            return false;
        }
        return isWrite;
    }

    /**
     *   ����һ���ļ�ͬ��,�Ļ�Դ�˵�����,�ж��Ƿ���Ҫɾ��Դ�˵��ļ�
     * @param isRead  �������,false��ʾ����
     * @param url
     * @param sf   ��ǰurl��Ӧ��SmbFile����
     * @param config  Դ��������Ϣ
     * @param fileBean
     */
    private boolean endSource(String url, SmbFile sf, FileBean fileBean) {
        String fileFullName = fileBean.getFullname();
        String _fileFullName = fileFullName.substring(0,fileFullName.lastIndexOf("/")+1)
                + FileContext.Str_Sync + fileBean.getName() + "_" + fileBean.getTime() + FileContext.Str_Lnk;
        //TODO ɾ��Դ��ͬ����ɵ��ļ�
        if(!config.isIstwoway() && config.isDeletefile()){
            try{
                sf.delete();
            } catch (SmbException e) {
                logger.error("[SMB]Դ��ͬ����ɺ�,ɾ��Դ��ͬ����ɵ��ļ�"+fileFullName+"ʧ��,��һ���ڴ���!");
                return false;
            }
            logger.info("[SMBͬ��]Դ��ͬ����ɺ�,ɾ��Դ��ͬ����ɵ��ļ�"+fileFullName+"�ɹ�!");
        }
        //TODO ɾ����ʱ����ļ�
        try{
            SmbFile _sf = SambaUtil.getConnectSmbFile(url);
            if(_sf == null){
                return false;
            }
            _sf.delete();
            logger.info("[SMB]Դ��ͬ����ɺ�,ɾ����ʱ����ļ�"+_fileFullName+"�ɹ�!");
        } catch (SmbException e) {
            logger.error("[SMB]Դ��ͬ����ɺ�,ɾ����ʱ����ļ�"+_fileFullName+"ʧ��,��һ���ڴ���!");
            return false;
        }
        return true;
    }
}
