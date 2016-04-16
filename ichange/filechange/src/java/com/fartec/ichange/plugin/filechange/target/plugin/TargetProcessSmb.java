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
    private static ExecutorService pool;                //写临时配置文件池
    private static ExecutorService executor;           //发送文件同步池
    protected static int extendsFile = 0;             //不是断点续传
    private static long interval = 1;
    private long time;                                  //周期开始时间
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
     * 1.把输入流写入目标文件
     * 2.获取当前目标文件的大小
     * 3.比较当前目标文件和源文件的大小,相等时返回true,表示该文件同步完成
     * @param in   流对象
     * @param sourceBean 源文件
     * @return    返回true,表示该文件同步完成,false表示继续
     */
    public boolean process(InputStream in, FileBean sourceBean) {
//        logger.info("Smb文件同步开始输入内容到目标文件!");
        boolean isOk = SambaUtil.writeFile(in, sourceBean, config);       // 把输入流写入目标文件
        if(isOk && sourceBean.getSyncflag().equals(FileContext.Str_SyncFileEnd)){                      // 比较当前目标文件和源文件的大小,相等时返回true,表示该文件同步完成
            //TODO 同步完成 去掉目标端文件的标志后缀
            String targetFullname = getTargetFileFullName(sourceBean);
            isOk = SambaUtil.reNameTarget(targetFullname,config);
        }
        return isOk;
    }

    /**
     *
     * @param data 二进制对象
     * @param sourceBean
     * @return
     */
    public boolean process(byte[] data, FileBean sourceBean) {
        boolean isOk = SambaUtil.writeFile(data, sourceBean,config);       // 把输入流写入目标文件
        if(isOk && sourceBean.getSyncflag().equals(FileContext.Str_SyncFileEnd)){                      // 比较当前目标文件和源文件的大小,相等时返回true,表示该文件同步完成
            //TODO 同步完成 去掉目标端文件的标志后缀
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
     * 1. 源文件列表 取值
     * 2. 目标文件 取值  为空
     * 3. 比较 源和目标 文件  不相等 执行4,5 相等执行1,2
     * 4. 如果目标端配置信息deleteFile为true,则改变该文件名(加上时间)
     * 5. 组成新列表
     * @param sourceFileList   传入源端服务器文件过滤后的列表
     * @return  返回比较结果
     */
    public FileList procesFileList(FileList sourceFileList) { //传入源端服务器文件过滤后的列表
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
                        logger.debug("[SMB] 断点续传比较列表");
                        extendsFile = 1;
                        FileBean targetFileBean = getTargetFileBean(sourceFileBean);
                        if(targetFileBean.getFilesize()>sourceFileBean.getFilesize()){
                            sourceFileBean.setFilepostlocation(0);
                        }else {
                            sourceFileBean.setFilepostlocation(targetFileBean.getFilesize());     //断点续传
                        }
                        fileList.addFileBean(sourceFileBean);
                        fileList.setSyncFileListType(FileContext.Str_SyncFileListType_Normal);
                    } else {
                        logger.debug("[SMB] 比较列表");
                        extendsFile = 0;
                        if(!config.isIstwoway() && !config.isOnlyadd() && config.isDeletefile()){
                            fileBeans.add(sourceFileBean);
                        }
                        FileBean targetFileBean = getTargetFileBean(sourceFileBean);
                        if(targetFileBean.getTime() != 0 &&
                                targetFileBean.getFilesize() != sourceFileBean.getFilesize()){
                            if(config.isOnlyadd() == true){   //目标端只增加
                                continue;
                            }
                            sourceFileBean.setFilepostlocation(0);                   //不用断点续传  ,游标为0
                            fileList.addFileBean(sourceFileBean);      // 源端符合同步要求的所有文件
                            fileList.setSyncFileListType(FileContext.Str_SyncFileListType_Normal);
                        } else if(targetFileBean.getTime() == 0){
                            sourceFileBean.setFilepostlocation(0);                   //不用断点续传  ,游标为0
                            fileList.addFileBean(sourceFileBean);
                            fileList.setSyncFileListType(FileContext.Str_SyncFileListType_Normal);
                        }
                    }
                }
            } catch (Exception e){
                logger.debug(e.getMessage());
            }
            //TODO 缓存删除对比列表
            if(!config.isIstwoway() && !config.isOnlyadd()
                    && config.isDeletefile() && extendsFile == 0){
                logger.debug("[cacheFileBeans]缓存删除对比列表");
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
     *缓存源端同步列表作为目标端删除的列表参考
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
                logger.info("第"+ (interval) +"个Smb文件双向同步[目标端-->源端]周期开始!");
                this.executor = Executors.newFixedThreadPool(config.getThreads());
                List<SmbFile> smbFiles = getListSmbFile();
                if(smbFiles != null){
                    int fileSize = smbFiles.size();                       //周期内源端文件记录数(过滤前)
                    if( fileSize > 0 ){
                        sendFile(smbFiles);
                    }
                    executor.shutdown();
                    boolean isTerminated = executor.isTerminated();
                    while (!isTerminated){
                         logger.info("第"+ (interval) +"个Smb文件双向同步[目标端-->源端]周期没有结束!等待10秒..");
                        try{
                            Thread.sleep(1000*10);
                            isTerminated = executor.isTerminated();
                        } catch (InterruptedException e) {
                            logger.info(e.getMessage());
                        }
                    }
                }
                logger.info("第"+ (interval) +"个Smb文件双向同步[目标端-->源端]周期结束!等待"+2+"秒..");
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
        List<SmbFile> smbFiles = new ArrayList<SmbFile>();             // 获取源端文件名列表
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
     * 需要断点续传的文件列表
     * @param smbFile
     * @param config
     * @return
     */
    private List<SmbFile> readSpecialTargetSmbFiles(SmbFile smbFile, TargetFile config) {
        List<SmbFile> list = new ArrayList<SmbFile>();
        String flag = FileContext.Str_SyncFileTargetProcess_Flag;            //表示目标端传送到源端的改名后文件
        String flagEnd = FileContext.Str_SyncFileSourceProcess_End_Flag;    //表示源端传送到目标端的改名后文件
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
            logger.info("网络异常,下一周期继续!");
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
                logger.info("网络异常,下一周期继续!");
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
            logger.info("网络异常,下一周期继续!");
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
                logger.info("网络异常,下一周期继续!");
                return null;
            }
        }
        return list;
    }

    /**
     * 一批文件的处理
     * @param list
     * @return
     */
    private List<SmbFile> sendFile(List<SmbFile> list){
        List<FileBean> fileBeans = SambaUtil.readFileBeanList(config, list);            //当前批次的同步文件
        FileList sourceFileList =  new FileList();
        for(FileBean  fileBean : fileBeans){
            sourceFileList.addFileBean(fileBean);                     //源端需要同步的fileList
        }
        sourceFileList.setTime(time);
        sourceFileList.setSyncFileListType(FileContext.Str_SyncFileListType_Normal);
        sourceFileList.setSyncFileListFlag(FileContext.Str_SyncFileListStart);
        // TODO 加入线程,进行多线程处理
        Runnable command = new MyTargetRunnable(sourceFileList);
        executor.execute(command);
        list = new ArrayList<SmbFile>();
        return list;
    }

    /**
     * 不满分批数的文件处理
     * @param list
     * @param fileSize
     */
    private void sendEndFile(List<SmbFile> list, int fileSize) {
        try{
            int flag = 0;                                         //文件记录数分割标记
            if(fileSize <= config.getThreads()){
                flag = 0;
            } else if ( fileSize > config.getThreads() && fileSize <= config.getFilelistsize() ){
                flag = 1;
            }
            int tempTotal = 0;                                    //已经处理文件总数
            List<SmbFile> removeSmbFiles = new ArrayList<SmbFile>();   //目标端不需要删除的文件列表
            while ( tempTotal < fileSize ){
                int size = config.getFilelistsize();             //当前需要的同步文件个数
                if(flag == 0){
                    size = 1;
                } else if (flag == 1){
                    size = fileSize/config.getThreads();
                    int last = fileSize - tempTotal;                 //剩余需要的同步文件个数
                    if( last <= size){
                        size = fileSize - tempTotal;
                    }
                }
                tempTotal += size;
                List<SmbFile> tempSmbFiles = new ArrayList<SmbFile>();   //获取 当前批次需要的同步文件个数列表
                for (int i = 0; i < size ; i ++){
                    tempSmbFiles.add(list.get(i));
                }
                List<FileBean> fileBeans = SambaUtil.readFileBeanList(config,tempSmbFiles);            //当前批次的同步文件
                FileList sourceFileList =  new FileList();
                for(FileBean  fileBean : fileBeans){
                    sourceFileList.addFileBean(fileBean);                     //源端需要同步的fileList
                }
                sourceFileList.setTime(time);
                sourceFileList.setSyncFileListType(FileContext.Str_SyncFileListType_Normal);
                sourceFileList.setSyncFileListFlag(FileContext.Str_SyncFileListStart);
                // TODO 加入线程,进行多线程处理
                Runnable command = new MyTargetRunnable(sourceFileList);
                executor.execute(command);
                // TODO 删除小列表中同步完成的列表
                list.removeAll(tempSmbFiles);                                                     //去掉已经比较完了的文件
            }
        } catch (Exception e){
            logger.debug(e.getMessage());
        }
    }

    private void copyTargetFile( FileList sourceFileList) {
        // TODO 过滤已经存在的文件
        FileList compareFileList = null;              // 获取比较后的文件列表
        try{
            compareFileList = target.procesFileList(sourceFileList);
        } catch (Exception e){
            logger.info("[SMB同步]"+e.getMessage() + ",结束该线程!");
            return ;
        }
        // TODO 同步
        Iterator<FileBean> iterator = compareFileList.iterable();
        while (iterator.hasNext()){
            FileBean fileBean = iterator.next();
            String url = SambaUtil.makeSmbUrlAddFullName(config, fileBean.getFullname());
            try {
                //TODO 新开始一个文件同步,改变原来的名字(源端文件最后加上 FileContext.Str_SyncFileTargetProcess_Flag),并且同时改变fileBean的全名
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
                //TODO 传送文件流或分MB床送字节
                boolean  isRead = process(sf,fileBean);
                if(!isRead){
                    logger.info("[SMB同步]网络异常,结束该线程!");
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
                logger.info("[SMB同步]双向同步读取目标端文件失败,下周期继续!");
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
                //TODO 一个文件同步完成,改回原来的名字
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
                    logger.error("[SMB]双向同步时目标端同步完成后,改名失败,下一周期处理!");
                    return false;
                }
                String fileFullName = fileBean.getFullname();
                if(fileFullName.endsWith(FileContext.Str_SyncFileTargetProcess_Flag)){
                    fileFullName = fileFullName.substring(0,fileFullName.indexOf(FileContext.Str_SyncFileTargetProcess_Flag));
                }
                logger.info("[SMB同步]"+fileFullName+"同步完成!");
            }
        } catch (Exception e) {
            logger.debug("endTarget() "+e.getMessage());
            return false;
        }
        return true;
    }
}
