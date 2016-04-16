package com.fartec.ichange.plugin.filechange.source.plugin.webdav;

import com.fartec.ichange.plugin.filechange.utils.FileBean;
import com.fartec.ichange.plugin.filechange.utils.FileContext;
import com.googlecode.sardine.DavResource;
import com.googlecode.sardine.Sardine;
import com.inetec.common.config.nodes.SourceFile;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created by Int IDEA.
 * User: hhm
 * Date: 12-4-19
 * Time: ����8:35
 * To change this template use File | Settings | File Templates.
 */
public class WebDavUtils {

    private static Logger logger=Logger.getLogger(WebDavUtils.class);

    /**
     * �õ�link�ļ�·��
     * @param fileBean  �ļ�����
     * @param sourceFile        Դ�������ļ�
     * @return
     */
    public String getLinkFilePath(FileBean fileBean,SourceFile sourceFile){
        String path =  getSourceHostAndName(sourceFile)+judgeWorkDir(sourceFile.getDir()) + getFileDir(fileBean);
        if(path.endsWith("/")){
            try {
                path = path  + FileContext.Str_Sync+URLEncoder.encode(getFileBeanName(fileBean),sourceFile.getCharset())+"_"+fileBean.getTime()+FileContext.Str_Lnk;
            } catch (UnsupportedEncodingException e) {
                logger.info("��������֧��urlת�����");
            }
        }else{
            try {
                path =   path  +"/"+ FileContext.Str_Sync+URLEncoder.encode(getFileBeanName(fileBean),sourceFile.getCharset())+"_"+fileBean.getTime()+FileContext.Str_Lnk;
            } catch (UnsupportedEncodingException e) {
                logger.info("��������֧��urlת�����");
            }
        }
        return  path.replaceAll("\\+","%20");
    }

    /**
     * ����link�ļ�
     * @param fileBean �ļ�����
     * @param sardine          Դ�˲�������
     * @param sourceFile                 Դ�������ļ�
     */
    public void createLinkFile(FileBean fileBean,Sardine sardine,SourceFile sourceFile){
        String newPath = getLinkFilePath(fileBean,sourceFile);
        try {
            sardine.put(newPath,new String("").getBytes());
        } catch (IOException e) {
            logger.info("�����ձ�ʶ�ļ�����!"+fileBean.getFullname());
        }
    }

    /**
     * ɾ���ձ�ʶ�ļ�
     * @param fileBean  �ļ�����
     * @param sardine    Դ�˲�������
     * @param sourceFile   Դ�������ļ�
     */
    public void deleteLinkFile(FileBean fileBean,Sardine sardine,SourceFile sourceFile) {
        String newPath = getLinkFilePath(fileBean,sourceFile);
        try {
            sardine.delete(newPath);
        } catch (IOException e) {
            logger.info("ɾ���ձ�ʶ�ļ�����!"+fileBean.getFullname());
        }
    }

    /**
     * �õ�syncFile�е�ʱ��
     * @param fileBean    �ļ�����
     * @return �����ļ�����޸ĵ�ʱ��
     */
    public long getSyncIngFileTime(String  fileName){
        if(fileName.startsWith(FileContext.Str_Sync)){
            if(fileName.endsWith(FileContext.Str_Lnk)){
                String path =  fileName.substring(0,fileName.lastIndexOf("."));
                String time =  path.substring(path.lastIndexOf("_")+1,path.length());
                return Long.parseLong(time);
            }
        }
        return -1;
    }

    /**
     * �õ�fileBean���ļ���
     * @param fileBean   �ļ�����
     * @return
     */
    public String getFileBeanName(FileBean fileBean){
        String name = fileBean.getFullname();
        if(name.contains("/")){
            name = name.substring(name.lastIndexOf("/")+1,name.length()) ;
        }
        return name;
    }

    /**
     * �õ�����·��
     * @param resource  ��������Դ����
     * @return    ���ط�������Ӧ·��
     */
    public String getCorrespondingLinkFilePath(DavResource resource,SourceFile sourceFile){
        String path = null;
        String href = null;      //���ĵ�ת����href�����ܺ����������Ͷ˿ں�,����û��
        try {
            href = URLDecoder.decode(resource.getHref().toString().replaceAll("\\+","%2B"),sourceFile.getCharset());
        } catch (UnsupportedEncodingException e) {
            logger.info("******��������֧��urlת�����*****");
        }
        if(!href.contains("http://"))  {
            path = getSourceHostAndName(sourceFile) + href;
        } else {
            path = href;
        }
        return path;
    }

    /**
     * ת����url
     * @param resource Դ����Դ�ļ�
     * @param sourceFile       Դ�������ļ�
     * @return         ����ת�����ļ�·��
     */
    public String encodeCorrespondingLinkFilePath(DavResource resource,SourceFile sourceFile){
        String path = getCorrespondingLinkFilePath(resource,sourceFile);
        return  getSourceHostAndName(sourceFile)+urlEncoder(path.replaceFirst(getSourceHostAndName(sourceFile),""),sourceFile) ;
    }

    /**
     * �õ���Ӧ�ļ�·��
     * @param resource ��Դ����
     * @param sourceFile     Դ�������ļ�
     * @return
     */
    public String  getCorrespondingFilePath(DavResource resource,SourceFile sourceFile){
        String sourceFileName = getDavResourceName(resource,sourceFile) ;
        String path = getCorrespondingLinkFilePath(resource,sourceFile);
        if(path.contains("/")){
            path = path.substring(0,path.lastIndexOf("/"));
        }
        if(path.endsWith("/")){
            path =  path + sourceFileName;
        }  else {
            path = path+"/"+sourceFileName;
        }
        return path;
    }

    /**
     * �õ�ͬ���ļ���
     * @param resource  ��Դ�ļ�
     * @param sourceFile      Դ�������ļ�
     * @return
     */
    public String  getSyncIngFileName (DavResource resource,SourceFile sourceFile){
        String sourceFileName = getDavResourceName(resource,sourceFile)  ;
        sourceFileName = sourceFileName.substring(0,sourceFileName.lastIndexOf("_"));
        return  sourceFileName.replaceFirst(FileContext.Str_Sync,"");
    }

    /**
     * �����ļ���fileBean
     * @param resource
     * @param sourceFile
     * @return
     */
    public FileBean buildSyncIngFile(DavResource resource,SourceFile sourceFile,Sardine sardine){
        String name = getSyncIngFileName(resource,sourceFile);
        String path = getCorrespondingLinkFilePath(resource,sourceFile);
        if(path.contains("/")){
            path = path.substring(0,path.lastIndexOf("/"));
        }
        if(path.endsWith("/")){
            path =  path + name;
        }  else {
            path = path+"/"+name;
        }
        try {
            List<DavResource> resources =  sardine.list(path);
            if(resources.size()>0){
                DavResource resource1 = resources.iterator().next();
                FileBean fileBean =new FileBean();
                fileBean.setName(name);
                fileBean.setFullname(path.replace( getSourceHostAndName(sourceFile) + judgeWorkDir(sourceFile.getDir()),""));
                fileBean.setFilesize(resource1.getContentLength());
                fileBean.setTime(resource1.getModified().getTime());
                return fileBean;
            }
        } catch (IOException e) {
            logger.info("�����ڴ��ļ�");
        }
        return  null;
    }


    public String getSyncName(String fileName,SourceFile sourceFile){
        String name =  fileName.substring(0,fileName.lastIndexOf("_"));
        return name.replaceFirst(FileContext.Str_Sync,"");
    }

    /**
     * ����ļ��Ƿ� �޸Ĺ�������޸Ĺ�ֱ��ɾ����ʶ�ļ�
     * @param fileBean  ��ʶ�ļ�����
     * @param sourceFile  Դ�������ļ�
     * @param sardine  Դ�˲�������
     * @param time   �޸� ʱ��
     * @return   �����ļ��Ƿ��޸ĵı�ʶ
     */
    public boolean judeFileModify(DavResource resource,SourceFile sourceFile,Sardine sardine){
        boolean  flag =false;
        String path = getCorrespondingFilePath(resource,sourceFile);
        String resourceName = getDavResourceName(resource,sourceFile);
        long time = getSyncIngFileTime(resourceName);
        if(path.contains("/")){
            path = path.substring(0,path.lastIndexOf("/")+1);
        }
        path = path+getSyncName(resourceName,sourceFile);
        try {
            if(sardine.exists(path)){
                List<DavResource> resources = sardine.list(path);
                if(resources.size()>0){
                    DavResource resource1 = resources.iterator().next();
                    long  rTime =   resource1.getModified().getTime() ;
                    if(time ==rTime){
                        flag=true;
                    }
                }
            }
        } catch (IOException e) {
            logger.info("Դ�˲����ڴ��ļ�");
            return false;
        }
        return flag;
    }

    /**
     * �滻���һ��separator
     * @param source    Դ�ַ���
     * @param separator    �滻���ַ���
     * @return
     */
    public String replaceEnd(String source,String separator) {
        StringBuilder sb=new StringBuilder("");
        int last = 0;
        if(source.contains(separator)){
            last = source.lastIndexOf(separator);
        }
        sb.append(source.substring(0,last)).append(source.substring(last+separator.length(),source.length()));
        return sb.toString();
    }

    /**
     * �õ�����������Դ�ļ���
     * @param resource   ��Դ����
     * @param sourceFile    Դ�������ļ�
     */
    public String getDavResourceName(DavResource resource, SourceFile sourceFile){
        String href = urlDecoder(resource.getHref().toString(),sourceFile);
        String fileName=null;
        if(href.contains("/")){
            fileName = href.substring(href.lastIndexOf("/")+1,href.length());
        }  else {
            fileName = href;
        }
        return   fileName;
    }

    public String getDavResourcePath(DavResource resource, SourceFile sourceFile){
        String href = urlDecoder(resource.getHref().toString(),sourceFile);
        if(!href.contains("http://"))  {
           return  href.replaceFirst(judgeWorkDir(sourceFile.getDir()),"");
        } else {
            return  href.replaceFirst(getSourceHostAndName(sourceFile)+judgeWorkDir(sourceFile.getDir()),"");
        }
    }


    /**
     * �õ��������ļ����͵��ļ�����
     * @param fileName  �ļ�ȫ��
     * @return   �����ļ����Ʋ������ļ�����
     */
    public String getFileName(String fileName){
        String fName=null;
        if(fileName.contains(".")){
            fName=fileName.substring(0,fileName.lastIndexOf("."));
        }else{
            fName=fileName;
        }
        return fName;
    }

    /**
     * ����URL
     * @param dir  ��Ҫ�����url·��
     * @param sourceFile      Դ�������ļ�
     * @return         ������url
     */
    public String urlDecoder(String dir,SourceFile sourceFile){
        String url=null;
        try {
            url = URLDecoder.decode(dir,sourceFile.getCharset());
        } catch (UnsupportedEncodingException e) {
            logger.info(e.getMessage());
        }
        return url;
    }

    /**
     * �õ��ļ���Ŀ¼
     * @param fileBean  �ļ�����
     * @return          �ļ�Ŀ¼
     */
    public String getFileDir(FileBean fileBean){
        String dir=null;
        if(fileBean.getFullname().contains("/")){
            dir=fileBean.getFullname().substring(0,fileBean.getFullname().lastIndexOf("/"));
        }else{
            dir="";
        }
        return dir;
    }

    /**
     * �õ��������ļ����ļ�����
     * @param fileName  �ļ�ȫ��
     * @return          �������ļ����͵��ļ���
     */
    public String getFileType(String fileName){
        String fType=null;
        if(fileName.contains(".")){
            fType=fileName.substring(fileName.lastIndexOf(".")+1,fileName.length());
        }else{
            fType="";
        }
        return  fType;
    }

    /**
     * ��·������URLת�����
     * @param dir   ��Ҫת����ļ�·��
     * @return       ����URLת�����ļ�·��
     */
    public String urlEncoder(String dir,SourceFile sourceFile){
        String encodeUrl="/";
        if(!dir.equals("")&&dir!=null){
            if(dir.contains("/")){
                String [] strings=dir.split("/");
                if(strings!=null){
                    for (int i=0;i<strings.length;i++){
                        if(!strings[i].equals("")&&strings[i]!=null) {
                            if(i!=(strings.length-1)){
                                try {
                                    encodeUrl+= URLEncoder.encode(strings[i],sourceFile.getCharset())+"/";
                                } catch (UnsupportedEncodingException e) {
                                    logger.error(e.getMessage()+"urlת�뱨��!!!");
                                }
                            }else{
                                if(dir!=null){
                                    try {
                                        encodeUrl+=URLEncoder.encode(strings[i],sourceFile.getCharset());
                                    }catch (UnsupportedEncodingException e) {
                                        logger.error(e.getMessage()+"urlת�뱨��!!!");
                                    }
                                }
                            }
                        }
                    }
                }
            }else{
                try {
                    encodeUrl=URLEncoder.encode(dir,sourceFile.getCharset());
                } catch (UnsupportedEncodingException e) {
                    logger.error(e.getMessage()+"urlת�뱨��!!!");
                }
            }
        }
        return  encodeUrl;
    }

    /**
     * ��ʽ�������ռ�
     * @param workDir  ����Ĺ����ռ�
     * @return      ���ظ�ʽ����Ĺ����ռ�
     */
    public String judgeWorkDir(String workDir){
        if(workDir.startsWith("/")){
            if(workDir.endsWith("/")){
                return  workDir.substring(0,workDir.length()-1);
            }else{
                return  workDir;
            }
        }else {
            if(workDir.endsWith("/")){
                return  "/"+workDir.substring(0,workDir.length()-1);
            }else{
                return "/"+workDir;
            }
        }
    }

    /**
     * �õ�Դ�˵��������͵�ַ
     * @param sourceFile Դ�������ļ�
     * @return       ����Դ�������·��
     */
    public String getSourceHostAndName(SourceFile sourceFile){
        return  "http://"+sourceFile.getServerAddress()+":"+sourceFile.getPort();
    }
}
