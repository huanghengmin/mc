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
 *  实现文件共享的同步,
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
        logger.info("SMB文件同步停止!");
    }

    public boolean isRun() {
        return isRun;
    }

    /**
     * 传流到源端端处理
     * @param in   流对象
     * @param sourceBean  目标端端文件信息
     * @return  返回true则继续,false则结束
     */
    public boolean process(InputStream in, FileBean fileBean) {
        boolean isOk = SambaUtil.writeFile(in, fileBean,config);       // 把输入流写入目标文件
        if(isOk && fileBean.getSyncflag().equals(FileContext.Str_SyncFileEnd)){                     // 比较当前目标文件和源文件的大小,相等时返回true,表示该文件同步完成
            //TODO 同步完成 去掉目标端文件的标志后缀
            String targetFullName = getTargetFileFullName(fileBean);
            isOk = SambaUtil.reNameTarget(targetFullName,config);
        }
        return isOk;
    }

    public boolean process(byte[] data, FileBean fileBean) {
        boolean isOk = SambaUtil.writeFile(data, fileBean,config);       // 把输入流写入目标文件
        if(isOk && fileBean.getSyncflag().equals(FileContext.Str_SyncFileEnd)){                      // 比较当前目标文件和源文件的大小,相等时返回true,表示该文件同步完成
            //TODO 同步完成 去掉目标端文件的标志后缀
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
     * 1.获取源端所有文件名集合
     * 2.分批获取源端文件列表
     * 3.多线程处理
     * 4.是否需要删除
     */
    public void run() {
        //TODO run()
        isRun = true;
        while (isRun()){
            logger.info("第"+ (interval) +"个Smb"+(config.isIstwoway()?"[源端-->目标端]文件同步":"文件同步")+"周期开始!");
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
                            logger.debug("第"+ (interval) +"个Smb"+(config.isIstwoway()?"[源端-->目标端]文件同步":"文件同步")+"周期没有结束,等待10秒..");
                            try{
                                Thread.sleep(1000*10);
                                isTerminated = executor.isTerminated();
                            } catch (InterruptedException e) {
                                logger.debug(e.getMessage());
                            }
                        }
                        if(!config.isIstwoway()&&!config.isDeletefile()){ //判断是否双向同步,是则不用处理删除操作
                            removeTargetFile();
                        }
                    }
                }
            }
            logger.info("第"+ (interval) +"个Smb"+(config.isIstwoway()?"[源端-->目标端]文件同步":"文件同步")+"周期结束!等待"+config.getInterval()/1000+"秒..");
            try {
                Thread.sleep(config.getInterval());
            } catch (InterruptedException e) {
                logger.debug(e.getMessage());
            }
            interval ++;
        }
    }

    /**
     * 发送到目标端删除目标端文的标记
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
            logger.info("[SMB同步]网络异常,删除目标端文件失败!");
            return ;
        }
    }

    private boolean sendSuccess(SmbFile smbFile, SourceFile config) {
        boolean isSmbFile = sendSpecialSourceSmbFiles(smbFile, config);
        isSmbFile = sendSourceSmbFiles(smbFile,config);
        return isSmbFile;
    }

    /**
     * 获取需要断点续传的文件列表
     * @param smbFile   SmbFile对象
     * @param config   源端配置信息
     * @return      需要断点续传
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
     * 获取SmbFile列表
     * @param smbFile    SmbFile对象
     * @param config    源端配置信息
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
     * 递归获取源端子目录下需要断点续传的文件列表,
     * @param isDirectory    是否需要进入子目录 true进入
     * @param smbFile        SmbFile对象
     * @param list           需要返回的列表
     * @param flag           源文件改名后的后缀
     * @param flagEnd        目标文件改名后的后缀
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
                                    logger.info("[SMB]源端临时文件对应的原文件已修改,删除临时文件"+name+"!");
                                }
                            }
                        }else{
                            sf = SambaUtil.getConnectSmbFile(_url);
                            if(sf!=null){
                                sf.delete();
                                logger.info("[SMB]源端临时文件对应的原文件不存在,删除临时文件"+name+"!");
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
     * 递归获取源端子目录下的文件列表,
     * @param isDirectory    是否需要进入子目录 true进入
     * @param smbFile        SmbFile对象
     * @param list           需要返回的列表
     * @param flag           源端不需要读取的文件后缀标记
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
                logger.info("网络异常,下一周期继续!");
                return null;
            }
        }
        return list;
    }

    /**
     * 发送一批文件
     * @param list
     * @return
     */
    private List<SmbFile> sendFile(List<SmbFile> list){
        FileList sourceFileList = SambaUtil.readFileList(config, list);            //当前批次的同步文件
        sourceFileList.setTime(time);
        sourceFileList.setSyncFileListType(FileContext.Str_SyncFileListType_Normal);
        sourceFileList.setSyncFileListFlag(FileContext.Str_SyncFileListStart);
        sourceFileList.setSyncFileListDD(false);   //断点续传

        // TODO 加入线程,进行多线程处理
        Runnable command = new MySourceRunnable(sourceFileList);
        executor.execute(command);
        list = new ArrayList<SmbFile>();
        return list;

    }

    private List<SmbFile> sendDDFile(List<SmbFile> list) {
        FileList sourceFileList = SambaUtil.readFileList(config, list);            //当前批次的同步文件
        sourceFileList.setTime(time);
        sourceFileList.setSyncFileListType(FileContext.Str_SyncFileListType_Normal);
        sourceFileList.setSyncFileListFlag(FileContext.Str_SyncFileListStart);
        sourceFileList.setSyncFileListDD(true);   //断点续传

        // TODO 加入线程,进行多线程处理
        Runnable command = new MySourceRunnable(sourceFileList);
        executor.execute(command);
        list = new ArrayList<SmbFile>();
        return list;
    }
    /**
     * 1.过滤源端列表
     * 2.比较列表中的文件是否存在于目标端,返回需要同步的文件列表
     * 3.文件同步开始
     * 4.文件同步结束处理
     * @param sourceFileList
     */
    private void copySourceFile( FileList sourceFileList) {
        // TODO 过滤已经存在的文件
        FileList compareFileList = null;              // 获取比较后的文件列表
        try{
            compareFileList = source.procesFileList(sourceFileList);
        } catch (Exception e){
            logger.info("[SMB同步]"+e.getMessage()+",结束该线程!");
            return ;
        }
        // TODO 同步
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
                    //TODO 创建临时标记文件
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
                    //TODO 传送文件流或分MB床送字节
                    boolean  isRead = process(sf,fileBean);
                    if(!isRead){
                        logger.info("[SMB同步]网络异常,结束该线程!");
                        return;
                    }
                    //TODO 删除临时标记文件、删除同步完成的文件
                    boolean  isReName = endSource(_url,sf,fileBean);
                    logger.info("[SMB同步]"+fileBean.getFullname()+"同步完成!");
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
     *  传输流到目标端,完成同步,返回 false表示完成了当前文件的同步
     *  1.判断是否存在目标端全名  不为空继续
     *
     * @param sf    源端流
     * @param sourceFileBean    源端文件信息
     * @return  true表示完成
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
                logger.info("[SMB同步]读取源端文件失败,下周期继续!");
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
     *   结束一个文件同步,改回源端的名字,判断是否需要删除源端的文件
     * @param isRead  结束标记,false表示结束
     * @param url
     * @param sf   当前url对应的SmbFile对象
     * @param config  源端配置信息
     * @param fileBean
     */
    private boolean endSource(String url, SmbFile sf, FileBean fileBean) {
        String fileFullName = fileBean.getFullname();
        String _fileFullName = fileFullName.substring(0,fileFullName.lastIndexOf("/")+1)
                + FileContext.Str_Sync + fileBean.getName() + "_" + fileBean.getTime() + FileContext.Str_Lnk;
        //TODO 删除源端同步完成的文件
        if(!config.isIstwoway() && config.isDeletefile()){
            try{
                sf.delete();
            } catch (SmbException e) {
                logger.error("[SMB]源端同步完成后,删除源端同步完成的文件"+fileFullName+"失败,下一周期处理!");
                return false;
            }
            logger.info("[SMB同步]源端同步完成后,删除源端同步完成的文件"+fileFullName+"成功!");
        }
        //TODO 删除临时标记文件
        try{
            SmbFile _sf = SambaUtil.getConnectSmbFile(url);
            if(_sf == null){
                return false;
            }
            _sf.delete();
            logger.info("[SMB]源端同步完成后,删除临时标记文件"+_fileFullName+"成功!");
        } catch (SmbException e) {
            logger.error("[SMB]源端同步完成后,删除临时标记文件"+_fileFullName+"失败,下一周期处理!");
            return false;
        }
        return true;
    }
}
