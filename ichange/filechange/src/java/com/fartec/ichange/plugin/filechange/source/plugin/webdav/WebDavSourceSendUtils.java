package com.fartec.ichange.plugin.filechange.source.plugin.webdav;

import com.fartec.ichange.plugin.filechange.source.SourceOperation;
import com.fartec.ichange.plugin.filechange.utils.*;
import com.googlecode.sardine.DavResource;
import com.googlecode.sardine.Sardine;
import com.googlecode.sardine.SardineFactory;
import com.inetec.common.config.nodes.SourceFile;
import org.apache.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Created by IntelliJ IDEA.
 * User: hhm
 * Date: 12-5-8
 * Time: ����2:49
 * To change this template use File | Settings | File Templates.
 */
public class WebDavSourceSendUtils {
    private final int MB = 1024*1024;
    private Logger logger = Logger.getLogger(WebDavSourceSendUtils.class);
    private WebDavUtils webDavUtils = new WebDavUtils();
    private WebDavFilterUtils webDavFilterUtils = new WebDavFilterUtils();

    /**
     * ���ͱ�ʶ�ļ��б�
     * @param fileList �ļ��б����
     * @param sourceFile   Դ�������ļ�
     * @param sourceOperation     ���͵�Ŀ��˲�������
     */
    public void syncIngFileList(FileList fileList,SourceFile sourceFile,SourceOperation sourceOperation){
        if(fileList.size()>0){
            Iterator<FileBean> iterator =  fileList.iterable();
            while (iterator.hasNext()){
                FileBean fileBean = iterator.next();
                processSyncIngFile(fileBean,sourceFile,sourceOperation);
            }
        }
    }

    /**
     * ����δ����ļ���Ŀ���
     * @param fileBean �ļ�����
     * @param sardine �ļ���������
     * @param path �ļ�����·��
     * @param sourceFile Դ�������ļ�
     * @param sourceOperation Ŀ��˲�������
     */
    public void processSyncIngFile(FileBean fileBean,SourceFile sourceFile,SourceOperation sourceOperation){
        //ת�����ļ�ȫ��
        String path = webDavUtils.getSourceHostAndName(sourceFile);
        if(fileBean.getFullname().startsWith("/")){
            path += webDavUtils.urlEncoder((webDavUtils.judgeWorkDir(sourceFile.getDir()) + fileBean.getFullname()),sourceFile).replaceAll("\\+", "%20");
        }else {
            path += webDavUtils.urlEncoder((webDavUtils.judgeWorkDir(sourceFile.getDir()) + "/" + fileBean.getFullname()),sourceFile).replaceAll("\\+", "%20");
        }
        Sardine sardine = SardineFactory.begin(sourceFile.getUserName(),sourceFile.getPassword());
        InputStream inputStream = null;
        if(fileBean.getFilepostlocation()==-2){
            try {
                inputStream = sardine.get(path);
            } catch (IOException e) {
                logger.info("***��ȡ"+fileBean.getFullname()+"�ļ������ɹ�,�ļ����ܱ��ƶ�!!!!***");
            }
            if(inputStream != null){
                boolean  flag = processSyncIngBytes(inputStream,fileBean,sourceFile,sourceOperation);
                if(flag){
                    try{
                        webDavUtils.deleteLinkFile(fileBean,sardine,sourceFile);//ɾ����ʶ�ļ�
                        if(sourceFile.isDeletefile()){
                            //���Դ��ɾ����ֱ��ɾ��
                            sardine.delete(path);
                            //ɾ��Դ���ļ��ļ���
                            deleteSourceDirectory(fileBean, sardine, sourceFile);
                        }
                    }catch (IOException e){
                        logger.error(fileBean.getFullname()+"***�Ļ�ԭ�����ɹ�!!!�ļ����ܱ��޸Ļ��ƶ����޸�!!!***"+e.getMessage());
                        return;
                    }
                }
            }else {
                logger.info("***��ȡ�ļ���"+fileBean.getFullname()+"����!!!�����ļ����޸�!!!***");
                return;
            }

        } else {
            try {
                inputStream = sardine.get(path);
            } catch (IOException e) {
                logger.info("***��ȡ"+fileBean.getFullname()+"�ļ������ɹ�,�ļ����ܱ��ƶ�!!!!***");
            }
            if(inputStream != null){
                try {
                    inputStream.skip(fileBean.getFilepostlocation());
                } catch (IOException e) {
                    logger.info("����Ŀ����ļ����ȱ���!!");
                }
                boolean  flag = processSyncIngBytes(inputStream,fileBean,sourceFile,sourceOperation);
                if(flag){
                    try{
                        webDavUtils.deleteLinkFile(fileBean,sardine,sourceFile);//ɾ����ʶ�ļ�
                        if(sourceFile.isDeletefile()){
                            //���Դ��ɾ����ֱ��ɾ��
                            sardine.delete(path);
                            //ɾ��Դ���ļ��ļ���
                            deleteSourceDirectory(fileBean, sardine, sourceFile);
                        }
                    }catch (IOException e){
                        logger.error(fileBean.getFullname()+"***�Ļ�ԭ�����ɹ�!!!�ļ����ܱ��޸Ļ��ƶ����޸�!!!***"+e.getMessage());
                        return;
                    }
                }
            }else {
                logger.info("***��ȡ�ļ���"+fileBean.getFullname()+"����!!!�����ļ����޸�!!!***");
                return;
            }
        }
    }

    /**
     * �����ļ�����
     * @param resource  ��Դ����
     * @param sourceFile     Դ�������ļ�
     * @return                     ���ع����õ��ļ�����
     */
    public FileBean buildFileBean(DavResource resource,SourceFile sourceFile){
        FileBean fileBean=new FileBean();
        fileBean.setFilesize(resource.getContentLength());
        try{
            //���ĵ�ת����href�����ܺ����������Ͷ˿ں�,����û��

            String href= URLDecoder.decode(resource.getHref().toString().replaceAll("\\+","%2B"),sourceFile.getCharset());
            if(!href.contains("http://"))  {
                fileBean.setName(href.substring(href.lastIndexOf("/")+1,href.length()));
                fileBean.setFullname(href.replaceFirst(webDavUtils.judgeWorkDir(sourceFile.getDir()),""));
            } else {
                fileBean.setName(href.substring(href.lastIndexOf("/") + 1, href.length()));
                fileBean.setFullname(href.replaceFirst(webDavUtils.getSourceHostAndName(sourceFile) + webDavUtils.judgeWorkDir(sourceFile.getDir()), ""));
            }
        }catch (UnsupportedEncodingException e) {
            logger.info("****��������֧��URLת�����!!!!!****");
        }
        fileBean.setTime(resource.getModified().getTime());
        return  fileBean;
    }

    /**
     *
     * @param sourceFile
     * @return
     */
    public List<DavResource> ergodicSourceSyncIng(SourceFile sourceFile){
        //����Դ��ͬ�����������ļ�
        List<DavResource>  syncIngFileList =new ArrayList<DavResource>();
        //�����Ŀ¼�б�
        List<DavResource> folder = new ArrayList<DavResource>();
        //��ʼ����·��
        String sourceRequestUrl = webDavUtils.getSourceHostAndName(sourceFile)+ webDavUtils.urlEncoder(webDavUtils.judgeWorkDir(sourceFile.getDir()),sourceFile);
        //������������
        Sardine sardine = SardineFactory.begin(sourceFile.getUserName(), sourceFile.getPassword());
        //webDAV��Դ
        List<DavResource> sourceDavResource=null;
        try {
            if(sardine.exists(sourceRequestUrl)){
                sourceDavResource = sardine.list(sourceRequestUrl);
            }
        }catch (IOException e){
            logger.info(e.getMessage()+"***����������Ŀ¼��������ʧ�ܻ������Ŀ¼���ó���!!!***");
            return null;
        }
        if(!sourceDavResource.isEmpty()){
            Iterator<DavResource> sourceIterator = sourceDavResource.iterator();      //����
            sourceIterator.next();   //������һ��ָ��Ŀ¼����Դ
            while (sourceIterator.hasNext()){
                DavResource resource = sourceIterator.next();
                if(resource.isDirectory()){
                    folder.add(resource);
                }else{
                    String fileName = webDavUtils.getDavResourceName(resource,sourceFile); //�õ���Դ����
                    if(fileName.startsWith(FileContext.Str_Sync)&&fileName.endsWith(FileContext.Str_Lnk)){
                        String name = webDavUtils.getSyncIngFileName(resource,sourceFile);
                        String fullname =webDavUtils.getSourceHostAndName(sourceFile)+webDavUtils.judgeWorkDir(sourceFile.getDir());
                        boolean  flag = webDavFilterUtils.fileBeanFilter(name,sourceFile,getHeadData(sardine,fullname,fileName,sourceFile));
                        if(flag){
                            syncIngFileList.add(resource);  //��ӵ�ͬ���ļ��б���
                        }
                    }
                }
            }
        }
        if(!folder.isEmpty()){
            ergodicSourceFolderSyncIng(sourceFile,folder,sardine,syncIngFileList);      //�����²�Ŀ¼
        }
        return syncIngFileList;
    }

    /**
     *
     * @param sourceFile
     * @param sourceFolder
     * @param sardine
     * @param syncIngFileList
     */
    public void  ergodicSourceFolderSyncIng(SourceFile sourceFile,List<DavResource> sourceFolder,Sardine sardine,List<DavResource> syncIngFileList){
        if(sourceFile.isIsincludesubdir()){
            //�ļ����б�
            List<DavResource> folder = new ArrayList<DavResource>();
            //�������������ļ��м���
            if(!sourceFolder.isEmpty()){
                Iterator<DavResource> soIterator = sourceFolder.iterator();
                while (soIterator.hasNext()){
                    DavResource davResource = soIterator.next();
                    String requestUrl = null;
                    if(davResource.getHref().toString().contains("http://")) {
                        String decoder = webDavUtils.urlDecoder(davResource.getHref().toString(),sourceFile).replaceFirst(webDavUtils.getSourceHostAndName(sourceFile),"");
                        requestUrl =  webDavUtils.getSourceHostAndName(sourceFile)+
                                webDavUtils.urlEncoder(webDavUtils.urlDecoder(decoder,sourceFile),sourceFile).replaceAll("\\+", "%20");
                    }else {
                        requestUrl = webDavUtils.getSourceHostAndName(sourceFile) +
                                webDavUtils.urlEncoder(webDavUtils.urlDecoder(davResource.getHref().toString(),sourceFile),sourceFile).replaceAll("\\+", "%20");
                    }
                    //�õ���Դ
                    List<DavResource> davResources = null;
                    try {
                        if(sardine.exists(requestUrl)){
                            davResources = sardine.list(requestUrl);
                        }
                    } catch (IOException e) {
                        logger.info(e.getMessage()+"***Error!!!�ļ��п����ƶ����޸�!!!***");
                        return;
                    }
                    if(davResources!=null){
                        if(davResources.size()>0){
                            Iterator<DavResource> iterator = davResources.iterator();
                            //������Ŀ¼
                            iterator.next();
                            while (iterator.hasNext()){
                                DavResource resource = iterator.next();
                                if(resource.isDirectory()){
                                    folder.add(resource); //�²��ļ��м���Щ����
                                }else{
                                    String fileName = webDavUtils.getDavResourceName(resource,sourceFile);   //�õ���Դ����
                                    if(fileName.startsWith(FileContext.Str_Sync)&&fileName.endsWith(FileContext.Str_Lnk)){
                                        String name = webDavUtils.getSyncIngFileName(resource,sourceFile);

                                       String fullname =webDavUtils.getSourceHostAndName(sourceFile)+webDavUtils.judgeWorkDir(sourceFile.getDir());
                                        boolean  flag = webDavFilterUtils.fileBeanFilter(name,sourceFile,getHeadData(sardine,fullname,fileName,sourceFile));
                                        if(flag){
                                            syncIngFileList.add(resource);  //��ӵ�ͬ���ļ��б���
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if(folder.size() > 0 ){
                ergodicSourceFolderSyncIng(sourceFile,folder,sardine,syncIngFileList); //�²����
            }
        }
    }

    /**
     * ����Bytes���鵽Ŀ���
     * @param in ����������
     * @param fileBean �ļ�����
     * @param sourceFile Դ�������ļ�
     * @param sourceOperation Ŀ��˲�������
     * @return
     */
    public boolean processSyncIngBytes(InputStream inputStream,FileBean fileBean,SourceFile sourceFile,SourceOperation sourceOperation){
        boolean flag = false;
        int length = -1;
        long readLength = 0;
        byte[] bytes = new byte[sourceFile.getPacketsize()*MB];
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            while ((length = inputStream.read(bytes)) != -1){
                if(readLength >= sourceFile.getPacketsize()*MB){
                    fileBean.setSyncflag(FileContext.Str_SyncFileStart);
                    flag  =  sourceOperation.process(out.toByteArray(),fileBean);
                    fileBean.setFilepostlocation(3);
                    if(flag == false) {
                        readLength = -1;
                        return false;
                    }
                    readLength = 0;
                    out.reset();
                    out.write(bytes, 0, length);
                    readLength += length;
                } else {
                    out.write(bytes, 0, length);
                    readLength += length;
                }
            }
            if(readLength >= 0){
                fileBean.setSyncflag(FileContext.Str_SyncFileEnd);
                flag = sourceOperation.process(out.toByteArray(),fileBean);
                fileBean.setFilepostlocation(3);
                out.reset();
                readLength = 0;
            }
        } catch (IOException e) {
            logger.info("***Ŀ��˷����ļ�"+fileBean.getFullname()+"���ݱ���!!!***");
            return false;
        }finally {
            try {
                inputStream.close();
                out.close();
            } catch (IOException e) {
                logger.info("***�ر���"+fileBean.getFullname()+"����!!!***");
                return false;
            }
        }
        return flag;
    }

    /**
     * ������ͨ�ļ�
     * @param inputStream
     * @param fileBean
     * @param sourceFile
     * @param sourceOperation
     * @return
     */
    public boolean processBytes(InputStream inputStream,FileBean fileBean,SourceFile sourceFile,SourceOperation sourceOperation ){
        fileBean.setRealname(FileType.REAL_NAME_MAP.get(fileBean.getName()));
        boolean flag = false;
        int length = -1;
        long readLength = 0;
        byte[] bytes = new byte[sourceFile.getPacketsize()*MB];
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            int pointer = 0;
            if(fileBean.getRealname().endsWith(".txt") || fileBean.getRealname().endsWith(".rtf")){
                while ((length = inputStream.read(bytes)) != -1){
                    if(readLength >= sourceFile.getPacketsize()*MB){
                        fileBean.setFilepostlocation(pointer);
                        fileBean.setSyncflag(FileContext.Str_SyncFileStart);
                        byte[] data = KeywordsFilterFactory.getKeywordsFilterUtil(fileBean.getRealname()).filter(out.toByteArray(),Basic.getKeywords(),sourceFile) ;
                        flag  =  sourceOperation.process(data,fileBean);
                        if(flag == false) {
                            readLength = -1;
                            return false;
                        }
                        pointer ++;
                        readLength = 0;
                        out.reset();
                        out.write(bytes, 0, length);
                        readLength += length;
                    } else {
                        out.write(bytes, 0, length);
                        readLength += length;
                    }
                }
                if(readLength >= 0){
                    fileBean.setSyncflag(FileContext.Str_SyncFileEnd);
                    fileBean.setFilepostlocation(pointer);
                    byte[] data = KeywordsFilterFactory.getKeywordsFilterUtil(fileBean.getRealname()).filter(out.toByteArray(),Basic.getKeywords(),sourceFile) ;
                    flag  =  sourceOperation.process(data,fileBean);
                    pointer ++;
                    out.reset();
                    readLength = 0;
                }
            }
            else {
                while ((length = inputStream.read(bytes)) != -1){
                    if(readLength >= sourceFile.getPacketsize()*MB){
                        fileBean.setFilepostlocation(pointer);
                        fileBean.setSyncflag(FileContext.Str_SyncFileStart);
                        flag  =  sourceOperation.process(out.toByteArray(),fileBean);
                        if(flag == false) {
                            readLength = -1;
                            return false;
                        }
                        pointer ++;
                        readLength = 0;
                        out.reset();
                        out.write(bytes, 0, length);
                        readLength += length;
                    } else {
                        out.write(bytes, 0, length);
                        readLength += length;
                    }
                }
                if(readLength >= 0){
                    fileBean.setSyncflag(FileContext.Str_SyncFileEnd);
                    fileBean.setFilepostlocation(pointer);
                    flag = sourceOperation.process(out.toByteArray(),fileBean);
                    pointer ++;
                    out.reset();
                    readLength = 0;
                }
            }
        } catch (IOException e) {
            logger.info("***Ŀ��˷����ļ�"+fileBean.getFullname()+"���ݱ���!!!***");
            return false;
        } catch (Exception e) {
            logger.info("***Ŀ��˷����ļ�"+fileBean.getFullname()+"���˹ؼ��ʱ���!!!***");
        } finally {
            try {
                inputStream.close();
                out.close();
            } catch (IOException e) {
                logger.info("***�ر���"+fileBean.getFullname()+"����!!!***");
                return false;
            }
        }
        return flag;
    }
    /**
     * �����ļ���Ŀ��˵ķ���
     * @param fileBean      Դ���ļ�����
     * @param path            Դ���ļ�·��
     * @param sardine         Դ�˲�������
     * @param iTargetProcess   Ŀ��˲�������
     * @param sourceFile         Դ�������ļ�
     */
    public void processFile(FileBean fileBean,SourceOperation sourceOperation,Sardine sardine,SourceFile sourceFile){
        //ת�����ļ�ȫ��
        String path = webDavUtils.getSourceHostAndName(sourceFile);
        if(fileBean.getFullname().startsWith("/")){
            path += webDavUtils.urlEncoder((webDavUtils.judgeWorkDir(sourceFile.getDir()) + fileBean.getFullname()),sourceFile).replaceAll("\\+", "%20");
        }else {
            path += webDavUtils.urlEncoder((webDavUtils.judgeWorkDir(sourceFile.getDir()) + "/" + fileBean.getFullname()),sourceFile).replaceAll("\\+", "%20");
        }
        InputStream inputStream = null;
        webDavUtils.createLinkFile(fileBean,sardine,sourceFile);//������ʶ�ļ�
        try {
            inputStream = sardine.get(path);
        } catch (IOException e) {
            logger.info("***��ȡ"+fileBean.getFullname()+"�ļ������ɹ�,�ļ����ܱ��ƶ�!!!!***");
        }
        if(inputStream != null){
            boolean  flag = processBytes(inputStream,fileBean,sourceFile,sourceOperation);
            if(flag){
                try{
                    webDavUtils.deleteLinkFile(fileBean,sardine,sourceFile);//ɾ����ʶ�ļ�
                    if(sourceFile.isDeletefile()){
                        //���Դ��ɾ����ֱ��ɾ��
                        sardine.delete(path);
                        //ɾ��Դ���ļ��ļ���
                        deleteSourceDirectory(fileBean, sardine, sourceFile);
                    }
                }catch (IOException e){
                    logger.error(fileBean.getFullname()+"***�Ļ�ԭ�����ɹ�!!!�ļ����ܱ��޸Ļ��ƶ����޸�!!!***"+e.getMessage());
                    return;
                }
            }
        }else {
            logger.info("***��ȡ�ļ���"+fileBean.getFullname()+"����!!!�����ļ����޸�!!!***");
            return;
        }
    }

    /**
     * �ϴ��з����������ļ�
     * @param sourceFile     Դ�����ļ�
     * @param fileList         �б�
     * @param sardine           Դ��������
     * @param iTargetProcess   Ŀ��˲�������
     */
    public void processList(SourceFile sourceFile,FileList fileList,Sardine sardine,SourceOperation sourceOperation) {
        if(fileList.size() > 0 && fileList != null){
            //ȡ���ļ��б�
            Iterator<FileBean> it = fileList.iterable();
            while (it.hasNext()){
                FileBean fileBean = it.next();
                //�����ļ�����
                processFile(fileBean, sourceOperation, sardine, sourceFile);
            }
            fileList.clear();
        }
    }

    /**
     *  ѭ��ɾ��Դ���ļ���
     * @param sardine       Դ�˲�������
     * @param requestUrl        ����·��
     * @param sourceFile            Դ�������ļ�
     */
    public void deleteSourceFolder(Sardine sardine,String requestUrl,SourceFile sourceFile){
        try {
            if(sardine.exists(requestUrl)){
                List<DavResource> davResourceList = sardine.list(requestUrl);
                Iterator<DavResource> iterator = davResourceList.iterator();
                iterator.next();
                if(!iterator.hasNext()) {
                    if(requestUrl.endsWith("/")){
                        sardine.delete(requestUrl);
                    }else {
                        requestUrl += "/";
                        sardine.delete(requestUrl);
                    }
                    requestUrl = requestUrl.substring(0,requestUrl.lastIndexOf("/"));
                    if(requestUrl.contains("/")){
                        requestUrl = requestUrl.substring(0,requestUrl.lastIndexOf("/"));
                        if(!requestUrl.equals(webDavUtils.getSourceHostAndName(sourceFile)+webDavUtils.urlEncoder(webDavUtils.judgeWorkDir(sourceFile.getDir()),sourceFile))){
                            deleteSourceFolder(sardine,requestUrl,sourceFile);
                        }
                    }
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage()+"***ɾ��Դ���ļ���"+webDavUtils.urlDecoder(requestUrl,sourceFile)+"���ɹ�!!!***");
        }
    }

    /**
     * ɾ��Դ���ļ���
     * @param fileBean   �ļ�����
     * @param sardine         �ļ���������
     * @param sourceFile           Դ�������ļ�
     */
    public void deleteSourceDirectory(FileBean fileBean,Sardine sardine,SourceFile sourceFile){
        //�õ�fileBean��·�����������ļ������������Ͷ˿�
        String targetDir = webDavUtils.getFileDir(fileBean);
        //�õ�fileBean��·�����������ļ���
        String requestUrl = webDavUtils.getSourceHostAndName(sourceFile)+ webDavUtils.urlEncoder(webDavUtils.judgeWorkDir(sourceFile.getDir())+targetDir,sourceFile);
        //ѭ���ж�ɾ��·��
        deleteSourceFolder(sardine,requestUrl,sourceFile);
    }

    /**
     * ����Դ�˸�Ŀ¼
     * @param sourceOperation     Ŀ��˲�������
     * @param sourceFile               Դ�������ļ�
     * @param uploadCount               �����ϴ�����
     * @param pool                         �̳߳�
     */
    public void ergodicSource(SourceOperation sourceOperation,SourceFile sourceFile,long uploadCount,ExecutorService pool){
        //����Դ��ͬ�����������ļ�
        FileList sendFileList = new FileList();
        //�����Ŀ¼�б�
        List<DavResource> folder = new ArrayList<DavResource>();
        //��ʼ����·��
        String sourceRequestUrl = webDavUtils.getSourceHostAndName(sourceFile)+ webDavUtils.urlEncoder(webDavUtils.judgeWorkDir(sourceFile.getDir()),sourceFile);
        //������������
        Sardine sardine = SardineFactory.begin(sourceFile.getUserName(), sourceFile.getPassword());
        //webDAV��Դ
        List<DavResource> sourceDavResource=null;
        try {
            if(sardine.exists(sourceRequestUrl)){
                sourceDavResource = sardine.list(sourceRequestUrl);
            }
        }catch (IOException e){
            logger.info(e.getMessage()+"***����������Ŀ¼��������ʧ�ܻ������Ŀ¼���ó���!!!***");
            return;
        }
        if(!sourceDavResource.isEmpty()){
            //����
            Iterator<DavResource> sourceIterator = sourceDavResource.iterator();
            //������һ��ָ��Ŀ¼����Դ
            sourceIterator.next();
            //ѭ��������Ŀ¼
            while (sourceIterator.hasNext()){
                DavResource resource = sourceIterator.next();
                if(resource.isDirectory()){
                    folder.add(resource);
                }else{
                    if(resource.getContentLength()>0){
                        String fullname =webDavUtils.getSourceHostAndName(sourceFile)+webDavUtils.judgeWorkDir(sourceFile.getDir());
                        String fileName = webDavUtils.getDavResourceName(resource,sourceFile); //�õ���Դ����
                        boolean flag = webDavFilterUtils.fileBeanFilter(fileName,sourceFile,getHeadData(sardine,fullname,fileName,sourceFile));
                        if(flag) {
                            FileBean fileBean =buildFileBean(resource,sourceFile);
                            if(fileBean!=null)
                                sendFileList.addFileBean(fileBean);
                            if(sendFileList.size() == (uploadCount*(sourceFile.getThreads()))){
                                poolFileList(sendFileList,sourceFile,uploadCount,pool,sourceOperation);
                            }
                        }
                    }
                }
            }
        }
        if(sendFileList.size() > 0){
            poolFileList(sendFileList,sourceFile,uploadCount,pool,sourceOperation);
        }
        if(!folder.isEmpty()){
            ergodicSourceFolder(sourceFile,folder,sardine,uploadCount,sourceOperation,pool);      //�����²�Ŀ¼
        }
    }

    /**
     * �����²�Ŀ¼����
     * @param sourceFile  Դ�������ļ�
     * @param sourceFolder    Դ���ϲ��ļ���
     * @param sardine          Դ�˲�������
     * @param uploadCount     ��������
     * @param iTargetProcess     Ŀ��˲�������
     * @param pool                 Դ���̳߳�
     */
    public void ergodicSourceFolder(SourceFile sourceFile,List<DavResource> sourceFolder,Sardine sardine,long uploadCount,SourceOperation sourceOperation,ExecutorService pool){
        if(sourceFile.isIsincludesubdir()){
            FileList sendFileList=new FileList();
            //�ļ����б�
            List<DavResource> folder = new ArrayList<DavResource>();
            //�������������ļ��м���
            if(!sourceFolder.isEmpty()){
                Iterator<DavResource> soIterator = sourceFolder.iterator();
                while (soIterator.hasNext()){
                    DavResource davResource = soIterator.next();
                    String requestUrl = null;
                    if(davResource.getHref().toString().contains("http://")) {
                        String decoder = webDavUtils.urlDecoder(davResource.getHref().toString(),sourceFile).replaceFirst(webDavUtils.getSourceHostAndName(sourceFile),"");
                        requestUrl =  webDavUtils.getSourceHostAndName(sourceFile)+
                                webDavUtils.urlEncoder(webDavUtils.urlDecoder(decoder,sourceFile),sourceFile).replaceAll("\\+", "%20");
                    }else {
                        requestUrl = webDavUtils.getSourceHostAndName(sourceFile) +
                                webDavUtils.urlEncoder(webDavUtils.urlDecoder(davResource.getHref().toString(),sourceFile),sourceFile).replaceAll("\\+", "%20");
                    }
                    //�õ���Դ
                    List<DavResource> davResources = null;
                    try {
                        if(sardine.exists(requestUrl)){
                            davResources = sardine.list(requestUrl);
                        }
                    } catch (IOException e) {
                        logger.info(e.getMessage()+"***Error!!!�ļ��п����ƶ����޸�!!!***");
                        return;
                    }
                    if(davResources!=null){
                        if(davResources.size()>0){
                            Iterator<DavResource> iterator = davResources.iterator();
                            //������Ŀ¼
                            iterator.next();
                            while (iterator.hasNext()){
                                DavResource resource = iterator.next();
                                if(resource.isDirectory()){
                                    folder.add(resource); //�²��ļ��м���Щ����
                                }else{
                                    if(resource.getContentLength()>0){
//                                        String path = webDavUtils.getDavResourcePath(resource,sourceFile);
                                        String fullname =webDavUtils.getSourceHostAndName(sourceFile)+webDavUtils.judgeWorkDir(sourceFile.getDir());
                                        String fileName = webDavUtils.getDavResourceName(resource,sourceFile);   //�õ���Դ����
                                        boolean flag = webDavFilterUtils.fileBeanFilter(fileName,sourceFile,getHeadData(sardine,fullname,fileName,sourceFile));
                                        if(flag) {
                                            FileBean fileBean =buildFileBean(resource,sourceFile);
                                            if(fileBean!=null)
                                                sendFileList.addFileBean(fileBean);
                                            if(sendFileList.size() == (uploadCount*(sourceFile.getThreads()))){
                                                poolFileList(sendFileList,sourceFile,uploadCount,pool,sourceOperation);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if(sendFileList.size() > 0){
                poolFileList(sendFileList,sourceFile,uploadCount,pool,sourceOperation);
            }
            //�����²�Ŀ¼
            if(folder.size() > 0 ){
                ergodicSourceFolder(sourceFile,folder,sardine,uploadCount,sourceOperation,pool); //�²����
            }
        }
    }

    /**
     * ���̷߳����ļ�
     * @param poolList ��Ҫ���͵��ļ��б�
     * @param sourceFile    Դ�������ļ�
     * @param uploadCount        �����ϴ�����
     * @param pool                    �̳߳�
     * @param sourceOperation          Ŀ��˲�������
     */
    public void poolFileList(FileList poolList,SourceFile sourceFile,long  uploadCount,ExecutorService pool,SourceOperation sourceOperation){
        FileList sFileList = alterFileList(poolList) ;
        FileList sendFileList = sourceOperation.procesFileList(sFileList);
        if(sendFileList!=null){
            if(sendFileList.size()>0){
                //����ļ��б��С���ڣ��߳������Է�����С
                if(sendFileList.size() == (sourceFile.getThreads()*uploadCount)){    //���б���������߳������Է�������ʱ�Ϳ��跢��
                    Iterator<FileBean> its = sendFileList.iterable();
                    FileList poolFileList = new FileList();
                    while (its.hasNext()){
                        poolFileList.addFileBean(its.next());
                        if(poolFileList.size() == uploadCount){
                            poolList(sourceOperation,poolFileList,pool,sourceFile);       //�����ļ��б�����
                        }
                    }
                    if(poolFileList.size() > 0&&poolFileList.size() < sourceFile.getThreads()){
                        poolFile(sourceOperation,poolFileList,pool,sourceFile);   //���͵����ļ�����
                    }else {
                        poolList(sourceOperation,poolFileList,pool,sourceFile);        //�����ļ��б�����
                    }
                    sendFileList.clear();
                    //����ļ��б��С�������߳�����С���߳������Է�������
                }else if((sourceFile.getThreads() < sendFileList.size())&&(sendFileList.size() < (sourceFile.getThreads()*uploadCount))){
                    long count = sendFileList.size()/sourceFile.getThreads()+1;
                    Iterator<FileBean> its = sendFileList.iterable();
                    FileList poolFileList = new FileList();
                    while (its.hasNext()){
                        poolFileList.addFileBean(its.next());
                        if(poolFileList.size() == count){
                            poolList(sourceOperation,poolFileList,pool,sourceFile);    //�����ļ��б�����
                        }
                    }
                    if(poolFileList.size() > 0&&poolFileList.size() < sourceFile.getThreads()){    //�����ļ��б�����
                        //�ļ���С���߳���ʱ��Ĵ���ʽ
                        poolFile(sourceOperation,poolFileList,pool,sourceFile);    //���͵����ļ�����
                    }else {
                        poolList(sourceOperation,poolFileList,pool,sourceFile);                     //�����ļ��б�����
                    }
                    sendFileList.clear();
                }else { //�ļ���С���߳���ʱ��Ĵ���ʽ
                    poolFile(sourceOperation,sendFileList,pool,sourceFile);   //���͵����ļ�����
                    sendFileList.clear();
                }
            }
        }
    }

    /**
     * �����ļ��б�Ŀ���
     * @param sourceOperation  Ŀ��˲�������
     * @param poolFileList     ��Ҫ���͵��ļ��б�
     * @param pool               ���͵��̳߳ض���
     * @param sourceFile        Դ�������ļ�
     */
    public void poolList(SourceOperation sourceOperation,FileList poolFileList,ExecutorService pool,SourceFile sourceFile){
        if(poolFileList.size() > 0){
            pool.execute(new WebDavSourceListTask(poolFileList,sourceFile,sourceOperation));
        }
    }

    /**
     *   ���������ļ�����
     * @param sourceOperation    Ŀ��˲�������
     * @param poolFileList      ��Ҫ���͵��б�
     * @param pool                �̳߳�
     * @param sourceFile        Դ�������ļ�
     */
    public void poolFile(SourceOperation sourceOperation,FileList poolFileList,ExecutorService pool,SourceFile sourceFile){
        if(poolFileList.size() > 0){
            Iterator<FileBean> iterator = poolFileList.iterable();
            while (iterator.hasNext()){
                FileBean fileBean = iterator.next();
                pool.execute(new WebDavSourceFileTask(fileBean,sourceFile,sourceOperation));
            }
        }

    }

    /**
     * ת��Ҫͬ�����ļ��б�
     * @param poolFileList ͬ���ļ��б�
     * @return  Ҫ�ϴ����ļ��б�
     */
    public FileList alterFileList(FileList poolFileList){
        FileList sendFileList = new FileList();
        if(poolFileList.size()>0&&poolFileList!=null){
            Iterator<FileBean> fileBeanIterator = poolFileList.iterable();
            while (fileBeanIterator.hasNext())
                sendFileList.addFileBean(fileBeanIterator.next());
        }
        poolFileList.clear();
        return sendFileList;
    }
    public byte[] getHeadData(Sardine sardine , String path,String filename ,SourceFile sourceFile){
        String encod = webDavUtils.urlEncoder(filename,sourceFile).replaceAll("\\+","%20");
        byte[] data = new byte[10] ;
        try {
            InputStream inputStream = sardine.get(path+"/"+encod);
            inputStream.read(data);
            inputStream.close();
        } catch (IOException e) {
            logger.error("getHeadData()��ͨ��sardine��ȡinputStreamʧ��",e);  //To change body of catch statement use File | Settings | File Templates.
        }
        return data ;
    }
}
