package com.fartec.ichange.plugin.filechange.target.plugin;

import com.fartec.ichange.plugin.filechange.target.TargetOperation;
import com.fartec.ichange.plugin.filechange.utils.*;
import com.inetec.common.config.nodes.TargetFile;
import com.inetec.common.exception.Ex;
import com.inetec.common.io.IOUtils;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileFilter;
import jcifs.smb.SmbFilenameFilter;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by IntelliJ IDEA.
 * User: bluesky
 * Date: 11-11-27
 * Time: pm 6:32
 * To change this template use File | Settings | File Templates.
 */
public class TargetProcessSmb implements ITargetProcess {
    private Logger logger = Logger.getLogger(TargetProcessSmb.class);
    private boolean isRun = false;
    private TargetFile config;
    private TargetOperation target;
    private static ExecutorService pool;                //д��ʱ�����ļ���
    private static ExecutorService executor;           //�����ļ�ͬ����
    protected static int extendsFile = 0;             //���Ƕϵ�����
    private static long interval = 1;
    private long time;                                  //���ڿ�ʼʱ��
    private static int MB = 1024*1024;
    private static final String fileListPath = System.getProperty("ichange.home")+"/temp/filelist_smb.xml";
    private class TargetCacheFileRunnable implements Runnable {
        private List<FileBean> fileBeans;
        private long time;
        public TargetCacheFileRunnable( List<FileBean> fileBeans,long time) {
            this.fileBeans = fileBeans;
            this.time = time;
        }
        public void run() {
            try {
                int i = 1000;
                Thread.sleep(i);
            } catch (InterruptedException e) {
            }
            cacheRemoveFileList(fileBeans, time);
        }
    }

    private class MyTargetRunnable implements Runnable {
        private  FileList targetFileList;
        public MyTargetRunnable( FileList targetFileList) {
            this.targetFileList = targetFileList;
        }
        public void run() {
            copyTargetFile(targetFileList);
        }
    }

    public TargetProcessSmb() {
    }

    /**
     * 1.��������д��Ŀ���ļ�
     * 2.��ȡ��ǰĿ���ļ��Ĵ�С
     * 3.�Ƚϵ�ǰĿ���ļ���Դ�ļ��Ĵ�С,���ʱ����true,��ʾ���ļ�ͬ�����
     * @param in   ������
     * @param sourceBean Դ�ļ�
     * @return    ����true,��ʾ���ļ�ͬ�����,false��ʾ����
     */
    public boolean process(InputStream in, FileBean sourceBean) {
//        logger.info("Smb�ļ�ͬ����ʼ�������ݵ�Ŀ���ļ�!");
        boolean isOk = SambaUtil.writeFile(in, sourceBean, config);       // ��������д��Ŀ���ļ�
        if(isOk && sourceBean.getSyncflag().equals(FileContext.Str_SyncFileEnd)){                      // �Ƚϵ�ǰĿ���ļ���Դ�ļ��Ĵ�С,���ʱ����true,��ʾ���ļ�ͬ�����
            //TODO ͬ����� ȥ��Ŀ����ļ��ı�־��׺
            String targetFullname = getTargetFileFullName(sourceBean);
            isOk = SambaUtil.reNameTarget(targetFullname,config);
        }
        return isOk;
    }

    /**
     *
     * @param data �����ƶ���
     * @param sourceBean
     * @return
     */
    public boolean process(byte[] data, FileBean sourceBean) {
        boolean isOk = SambaUtil.writeFile(data, sourceBean,config);       // ��������д��Ŀ���ļ�
        if(isOk && sourceBean.getSyncflag().equals(FileContext.Str_SyncFileEnd)){                      // �Ƚϵ�ǰĿ���ļ���Դ�ļ��Ĵ�С,���ʱ����true,��ʾ���ļ�ͬ�����
            //TODO ͬ����� ȥ��Ŀ����ļ��ı�־��׺
            String targetFullname = getTargetFileFullName(sourceBean);
            isOk = SambaUtil.reNameTarget(targetFullname,config);
        }
        return isOk;
    }

    private String getTargetFileFullName(FileBean sourceBean) {
        FileBean targetFileBean = getTargetFileBean(sourceBean);
        return targetFileBean.getFullname();
    }

    /**
     * 1. Դ�ļ��б� ȡֵ
     * 2. Ŀ���ļ� ȡֵ  Ϊ��
     * 3. �Ƚ� Դ��Ŀ�� �ļ�  ����� ִ��4,5 ���ִ��1,2
     * 4. ���Ŀ���������ϢdeleteFileΪtrue,��ı���ļ���(����ʱ��)
     * 5. ������б�
     * @param sourceFileList   ����Դ�˷������ļ����˺���б�
     * @return  ���رȽϽ��
     */
    public FileList procesFileList(FileList sourceFileList) { //����Դ�˷������ļ����˺���б�
        FileList fileList = new FileList();
        long time = sourceFileList.getTime();

        fileList.setTime(time);
        String flag = sourceFileList.getSyncFileListFlag();
        if(sourceFileList.getSyncFileListType().equals(FileContext.Str_SyncFileListType_Normal)){
            logger.debug("[SMB] target processFileList begin,fileList's fileListType is: "+sourceFileList.getSyncFileListType());
            List<FileBean> fileBeans = new ArrayList<FileBean>();
            try{
                Iterator<FileBean> sourceIte = sourceFileList.iterable();
                while(sourceIte.hasNext()){
                    FileBean sourceFileBean = sourceIte.next();
                    if(sourceFileList.getSyncFileListDD()){
                        logger.debug("[SMB] �ϵ������Ƚ��б�");
                        extendsFile = 1;
                        FileBean targetFileBean = getTargetFileBean(sourceFileBean);
                        if(targetFileBean.getFilesize()>sourceFileBean.getFilesize()){
                            sourceFileBean.setFilepostlocation(0);
                        }else {
                            sourceFileBean.setFilepostlocation(targetFileBean.getFilesize());     //�ϵ�����
                        }
                        fileList.addFileBean(sourceFileBean);
                        fileList.setSyncFileListType(FileContext.Str_SyncFileListType_Normal);
                    } else {
                        logger.debug("[SMB] �Ƚ��б�");
                        extendsFile = 0;
                        if(!config.isIstwoway() && !config.isOnlyadd() && config.isDeletefile()){
                            fileBeans.add(sourceFileBean);
                        }
                        FileBean targetFileBean = getTargetFileBean(sourceFileBean);
                        if(targetFileBean.getTime() != 0 &&
                                targetFileBean.getFilesize() != sourceFileBean.getFilesize()){
                            if(config.isOnlyadd() == true){   //Ŀ���ֻ����
                                continue;
                            }
                            sourceFileBean.setFilepostlocation(0);                   //���öϵ�����  ,�α�Ϊ0
                            fileList.addFileBean(sourceFileBean);      // Դ�˷���ͬ��Ҫ��������ļ�
                            fileList.setSyncFileListType(FileContext.Str_SyncFileListType_Normal);
                        } else if(targetFileBean.getTime() == 0){
                            sourceFileBean.setFilepostlocation(0);                   //���öϵ�����  ,�α�Ϊ0
                            fileList.addFileBean(sourceFileBean);
                            fileList.setSyncFileListType(FileContext.Str_SyncFileListType_Normal);
                        }
                    }
                }
            } catch (Exception e){
                logger.debug(e.getMessage());
            }
            //TODO ����ɾ���Ա��б�
            if(!config.isIstwoway() && !config.isOnlyadd()
                    && config.isDeletefile() && extendsFile == 0){
                logger.debug("[cacheFileBeans]����ɾ���Ա��б�");
                cacheFileBeans(fileBeans, time);
            }
        } else if(sourceFileList.getSyncFileListType().equals(FileContext.Str_SyncFileListType_Delete)){
            logger.debug("[delete] target processFileList begin,fileList's fileListType is: "+sourceFileList.getSyncFileListType());
            if(!config.isIstwoway()
                    && !config.isOnlyadd()
                    && config.isDeletefile()){
                pool.shutdown();
                while (!pool.isTerminated()){
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        logger.debug(e.getMessage());
                    }
                }
                SambaUtil.deleteFile(config,sourceFileList,extendsFile);
                pool = Executors.newFixedThreadPool(config.getThreads());
            }
            logger.debug("[the end of delete]target processFileList end!");
        }
        logger.debug("[the end ]target processFileList end!");
        return fileList;
    }

    private FileBean getTargetFileBean(FileBean sourceFileBean) {
        String targetFullName = sourceFileBean.getFullname();
        FileBean fileBean = SambaUtil.readFileBean(config,targetFullName);
        if(fileBean.getTime()==0){
            String targetFullNameEnd = sourceFileBean.getFullname() + FileContext.Str_SyncFileSourceProcess_End_Flag;
            fileBean = SambaUtil.readFileBean(config,targetFullNameEnd);
        }
        return fileBean;
    }

    private List<FileBean> fileListToBeans(FileList fileList) {
        List<FileBean> fileBeans = new ArrayList<FileBean>();
        Iterator<FileBean> iterator = fileList.iterable();
        while(iterator.hasNext()){
            FileBean fileBean = iterator.next();
            fileBeans.add(fileBean);
        }
        return fileBeans;
    }

    private void cacheFileBeans(List<FileBean> fileBeans, long time) {
        if(fileBeans.size()>0){
            Runnable command = new TargetCacheFileRunnable(fileBeans,time);
            pool.execute(command);
        }
    }

    /**
     *����Դ��ͬ���б���ΪĿ���ɾ�����б�ο�
     * @param flag
     */
    public synchronized void cacheRemoveFileList(List<FileBean> fileBeans, long time) {
        Configuration configuration;
        try {
            configuration = new Configuration(fileListPath);
            configuration.addFileList(fileBeans,time);
        } catch (Ex ex) {
            logger.debug(ex.getMessage());
        }
    }

    public void init(TargetOperation target, TargetFile config){
        this.target = target;
        this.config = config;
        if(!config.isIstwoway()
                && !config.isOnlyadd()
                && config.isDeletefile()){
            SambaUtil.initFileListSmb();
            this.pool = Executors.newFixedThreadPool(config.getThreads());
        }
    }

    public void stop() {
        isRun = false;
    }

    public boolean isRun() {
        return isRun;
    }

    public void run() {
        this.time = System.currentTimeMillis();
        isRun = true;
        /*if(config.isIstwoway()){
            while (isRun()){
                logger.info("��"+ (interval) +"��Smb�ļ�˫��ͬ��[Ŀ���-->Դ��]���ڿ�ʼ!");
                this.executor = Executors.newFixedThreadPool(config.getThreads());
                List<SmbFile> smbFiles = getListSmbFile();
                if(smbFiles != null){
                    int fileSize = smbFiles.size();                       //������Դ���ļ���¼��(����ǰ)
                    if( fileSize > 0 ){
                        sendFile(smbFiles);
                    }
                    executor.shutdown();
                    boolean isTerminated = executor.isTerminated();
                    while (!isTerminated){
                         logger.info("��"+ (interval) +"��Smb�ļ�˫��ͬ��[Ŀ���-->Դ��]����û�н���!�ȴ�10��..");
                        try{
                            Thread.sleep(1000*10);
                            isTerminated = executor.isTerminated();
                        } catch (InterruptedException e) {
                            logger.info(e.getMessage());
                        }
                    }
                }
                logger.info("��"+ (interval) +"��Smb�ļ�˫��ͬ��[Ŀ���-->Դ��]���ڽ���!�ȴ�"+2+"��..");
                try {
                    Thread.sleep(1000*2);
                } catch (InterruptedException e) {
                    logger.debug(e.getMessage());
                }
                interval ++;
            }
        }*/
    }

    private List<SmbFile> getListSmbFile() {
        List<SmbFile> smbFiles = new ArrayList<SmbFile>();             // ��ȡԴ���ļ����б�
        SmbFile smbFile = SambaUtil.readSmbFile(config);
        if(smbFile == null){
            return null;
        }
        List<SmbFile> smbSpecialFiles = readSpecialTargetSmbFiles(smbFile, config);
        if(smbSpecialFiles == null ){
            return null;
        }
        if(smbSpecialFiles.size() > 0) {
            for (SmbFile smbSpecialFile : smbSpecialFiles){
                smbFiles.add(smbSpecialFile);
            }
        } else {
            smbFiles = readTargetSmbFiles(smbFile, config);
        }
        return smbFiles;
    }

    /**
     * ��Ҫ�ϵ��������ļ��б�
     * @param smbFile
     * @param config
     * @return
     */
    private List<SmbFile> readSpecialTargetSmbFiles(SmbFile smbFile, TargetFile config) {
        List<SmbFile> list = new ArrayList<SmbFile>();
        String flag = FileContext.Str_SyncFileTargetProcess_Flag;            //��ʾĿ��˴��͵�Դ�˵ĸ������ļ�
        String flagEnd = FileContext.Str_SyncFileSourceProcess_End_Flag;    //��ʾԴ�˴��͵�Ŀ��˵ĸ������ļ�
        try{
            boolean isDirectory = smbFile.isDirectory();
            if (isDirectory) {
                list = getIncludeSpecialFileNames(isDirectory, smbFile, list,config );
                if(list.size() == config.getFilelistsize()){
                    sendFile(list);
                }
            }
        }catch (Exception e){
            logger.debug("readSpecialSourceSmbFiles() " + e.getMessage());
            logger.info("�����쳣,��һ���ڼ���!");
            return null;
        }
        return list;
    }

    private List<SmbFile> getIncludeSpecialFileNames(boolean isDirectory, SmbFile smbFile, List<SmbFile> list, TargetFile config) {
        if (isDirectory){
            try{
                SmbFilenameFilter fileNameFilter = new SambaFilterSpecialName(config);
                SmbFile[] smbFileNames = smbFile.listFiles(fileNameFilter);
                for (int i = 0;i<smbFileNames.length;i++){
                    if(smbFileNames[i].isFile()){
                        String url = smbFileNames[i].getCanonicalPath() +"?iocharset=" + config.getCharset();
                        SmbFile sf = SambaUtil.getConnectTargetSmbFile(url);
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
                SmbFileFilter fileFilter = new SambaFilterFile();
                SmbFile[] smbFiles = smbFile.listFiles(fileFilter);
                for (int i = 0;i<smbFiles.length;i++){
                    if(smbFiles[i].isDirectory()){
                        list = getIncludeSpecialFileNames(isDirectory,smbFiles[i],list,config);
                        if(list.size() == config.getFilelistsize()){
                            sendFile(list);
                        }
                    }
                }
            } catch (Exception e){
                logger.debug("getIncludeSpecialFileNames() " + e.getMessage());
                logger.info("�����쳣,��һ���ڼ���!");
                return null;
            }
        }
        return list;
    }

    private List<SmbFile> readTargetSmbFiles(SmbFile smbFile, TargetFile config) {
        List<SmbFile> list = new ArrayList<SmbFile>();
        String flag = FileContext.Str_SyncFileSourceProcess_End_Flag;
        try{
            boolean isDirectory = smbFile.isDirectory();
            if (isDirectory) {
                list = getIncludeFileNames(isDirectory, smbFile, list,config);
                if(list.size() == config.getFilelistsize()){
                    sendFile(list);
                }
            }
        }catch (Exception e){
            logger.debug("readTargetSmbFiles()" + e.getMessage());
            logger.info("�����쳣,��һ���ڼ���!");
            return null;
        }
        return list;
    }
    private List<SmbFile> getIncludeFileNames(boolean isDirectory, SmbFile smbFile, List<SmbFile> list, TargetFile config) {
        if (isDirectory){
            try{
                SmbFilenameFilter fileNameFilter = new SambaFilterName(config);
                SmbFile[] smbFileNames = smbFile.listFiles(fileNameFilter);
                for (int i = 0;i<smbFileNames.length;i++){
                    if(smbFileNames[i].isFile()){
                        String url = smbFileNames[i].getCanonicalPath() +"?iocharset=" + config.getCharset();
                        SmbFile sf = SambaUtil.getConnectTargetSmbFile(url);
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
                SmbFileFilter fileFilter = new SambaFilterFile();
                SmbFile[] smbFiles = smbFile.listFiles(fileFilter);
                for (int i = 0;i<smbFiles.length;i++){
                    if(smbFiles[i].isDirectory()){
                        list = getIncludeFileNames(isDirectory,smbFiles[i],list,config);
                        if(list.size() == config.getFilelistsize()){
                            sendFile(list);
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
     * һ���ļ��Ĵ���
     * @param list
     * @return
     */
    private List<SmbFile> sendFile(List<SmbFile> list){
        List<FileBean> fileBeans = SambaUtil.readFileBeanList(config, list);            //��ǰ���ε�ͬ���ļ�
        FileList sourceFileList =  new FileList();
        for(FileBean  fileBean : fileBeans){
            sourceFileList.addFileBean(fileBean);                     //Դ����Ҫͬ����fileList
        }
        sourceFileList.setTime(time);
        sourceFileList.setSyncFileListType(FileContext.Str_SyncFileListType_Normal);
        sourceFileList.setSyncFileListFlag(FileContext.Str_SyncFileListStart);
        // TODO �����߳�,���ж��̴߳���
        Runnable command = new MyTargetRunnable(sourceFileList);
        executor.execute(command);
        list = new ArrayList<SmbFile>();
        return list;
    }

    /**
     * �������������ļ�����
     * @param list
     * @param fileSize
     */
    private void sendEndFile(List<SmbFile> list, int fileSize) {
        try{
            int flag = 0;                                         //�ļ���¼���ָ���
            if(fileSize <= config.getThreads()){
                flag = 0;
            } else if ( fileSize > config.getThreads() && fileSize <= config.getFilelistsize() ){
                flag = 1;
            }
            int tempTotal = 0;                                    //�Ѿ������ļ�����
            List<SmbFile> removeSmbFiles = new ArrayList<SmbFile>();   //Ŀ��˲���Ҫɾ�����ļ��б�
            while ( tempTotal < fileSize ){
                int size = config.getFilelistsize();             //��ǰ��Ҫ��ͬ���ļ�����
                if(flag == 0){
                    size = 1;
                } else if (flag == 1){
                    size = fileSize/config.getThreads();
                    int last = fileSize - tempTotal;                 //ʣ����Ҫ��ͬ���ļ�����
                    if( last <= size){
                        size = fileSize - tempTotal;
                    }
                }
                tempTotal += size;
                List<SmbFile> tempSmbFiles = new ArrayList<SmbFile>();   //��ȡ ��ǰ������Ҫ��ͬ���ļ������б�
                for (int i = 0; i < size ; i ++){
                    tempSmbFiles.add(list.get(i));
                }
                List<FileBean> fileBeans = SambaUtil.readFileBeanList(config,tempSmbFiles);            //��ǰ���ε�ͬ���ļ�
                FileList sourceFileList =  new FileList();
                for(FileBean  fileBean : fileBeans){
                    sourceFileList.addFileBean(fileBean);                     //Դ����Ҫͬ����fileList
                }
                sourceFileList.setTime(time);
                sourceFileList.setSyncFileListType(FileContext.Str_SyncFileListType_Normal);
                sourceFileList.setSyncFileListFlag(FileContext.Str_SyncFileListStart);
                // TODO �����߳�,���ж��̴߳���
                Runnable command = new MyTargetRunnable(sourceFileList);
                executor.execute(command);
                // TODO ɾ��С�б���ͬ����ɵ��б�
                list.removeAll(tempSmbFiles);                                                     //ȥ���Ѿ��Ƚ����˵��ļ�
            }
        } catch (Exception e){
            logger.debug(e.getMessage());
        }
    }

    private void copyTargetFile( FileList sourceFileList) {
        // TODO �����Ѿ����ڵ��ļ�
        FileList compareFileList = null;              // ��ȡ�ȽϺ���ļ��б�
        try{
            compareFileList = target.procesFileList(sourceFileList);
        } catch (Exception e){
            logger.info("[SMBͬ��]"+e.getMessage() + ",�������߳�!");
            return ;
        }
        // TODO ͬ��
        Iterator<FileBean> iterator = compareFileList.iterable();
        while (iterator.hasNext()){
            FileBean fileBean = iterator.next();
            String url = SambaUtil.makeSmbUrlAddFullName(config, fileBean.getFullname());
            try {
                //TODO �¿�ʼһ���ļ�ͬ��,�ı�ԭ��������(Դ���ļ������� FileContext.Str_SyncFileTargetProcess_Flag),����ͬʱ�ı�fileBean��ȫ��
                SmbFile sf = SambaUtil.getConnectTargetSmbFile(url);
                if(sf == null){
                    return;
                }
                if( !sf.getURL().getPath().endsWith( FileContext.Str_SyncFileTargetProcess_Flag ) ){
                    url = url.split("\\?")[0] + FileContext.Str_SyncFileTargetProcess_Flag + "?" + url.split("\\?")[1];
                    try{
                        SmbFile rsf = SambaUtil.getConnectTargetSmbFile(url);
                        if(rsf == null){
                            return;
                        }
                        sf.renameTo(rsf);
                    } catch (SmbException e) {
                    }
                    sf = SambaUtil.getConnectTargetSmbFile(url);
                    if(sf == null){
                        return;
                    }
                    fileBean.setFullname(fileBean.getFullname() + FileContext.Str_SyncFileTargetProcess_Flag);
                }
                //TODO �����ļ������MB�����ֽ�
                boolean  isRead = process(sf,fileBean);
                if(!isRead){
                    logger.info("[SMBͬ��]�����쳣,�������߳�!");
                    return;
                }
                boolean isReName = endTarget(isRead,url,sf,config,fileBean);
                if(!isReName){
                    return ;
                }
            } catch (Exception e) {
                logger.debug("copyTargetFile()"+e.getMessage());
                return;
            }
        }
    }

    private boolean process(SmbFile sf,FileBean fileBean) {
        boolean isWrite = true;
        InputStream in = null;
        try {
            in = SambaUtil.getConnectSmbFileInputStream(sf) ;
            if(in == null){
                logger.info("[SMBͬ��]˫��ͬ����ȡĿ����ļ�ʧ��,�����ڼ���!");
                return false;
            }
            in.skip(fileBean.getFilepostlocation());
            int len = 0;
            byte[] buf = new byte[config.getPacketsize()*MB];
            fileBean.setFile_flag(FileContext.Str_TargetFile);
            while((len = in.read(buf))!=-1){
                if(len < config.getPacketsize()*MB){
                    fileBean.setSyncflag(FileContext.Str_SyncFileEnd);
                    buf = IOUtils.copyArray(buf, len);
                    try{
                        isWrite = target.process(buf, fileBean);
                    }catch (Exception e){
                        return false;
                    }
                }else{
                    fileBean.setSyncflag(FileContext.Str_SyncFileIng);
                    try{
                        isWrite = target.process(buf, fileBean);
                    }catch (Exception e){
                        return false;
                    }
                }
                if(isWrite == false){
                    return false;
                }
            }
            in.close();
        } catch (Exception e) {
            logger.debug(e.getMessage());
        }
        return isWrite;
    }

    private boolean endTarget(boolean isRead, String url, SmbFile sf, TargetFile config, FileBean fileBean) {
        try{
            if(isRead) {
                //TODO һ���ļ�ͬ�����,�Ļ�ԭ��������
                try{
                    if(url.split("\\?")[0].endsWith(FileContext.Str_SyncFileTargetProcess_Flag)){
                        SmbFile _sf = SambaUtil.getConnectTargetSmbFile(url.split("\\?")[0].split(FileContext.Str_SyncFileTargetProcess_Flag)[0] + "?" +url.split("\\?")[1]);
                        if(_sf == null){
                            return false;
                        }
                        sf.renameTo(_sf);
                    }else {
                        sf.renameTo(sf);
                    }
                } catch (SmbException e) {
                    logger.error("[SMB]˫��ͬ��ʱĿ���ͬ����ɺ�,����ʧ��,��һ���ڴ���!");
                    return false;
                }
                String fileFullName = fileBean.getFullname();
                if(fileFullName.endsWith(FileContext.Str_SyncFileTargetProcess_Flag)){
                    fileFullName = fileFullName.substring(0,fileFullName.indexOf(FileContext.Str_SyncFileTargetProcess_Flag));
                }
                logger.info("[SMBͬ��]"+fileFullName+"ͬ�����!");
            }
        } catch (Exception e) {
            logger.debug("endTarget() "+e.getMessage());
            return false;
        }
        return true;
    }
}
