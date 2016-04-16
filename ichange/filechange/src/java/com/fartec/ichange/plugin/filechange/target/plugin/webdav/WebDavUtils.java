package com.fartec.ichange.plugin.filechange.target.plugin.webdav;

import com.fartec.ichange.plugin.filechange.utils.FileBean;
import com.fartec.ichange.plugin.filechange.utils.FileContext;
import com.inetec.common.config.nodes.TargetFile;
import org.apache.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

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
     * �õ�Ŀ���δ����ļ�·��
     * @param fileBean        �ļ�����
     * @param targetFile              Ŀ��������ļ�
     * @return
     */
    public String getFilePath(FileBean fileBean,TargetFile targetFile){
        String fileName =  fileBean.getName().replaceFirst(FileContext.Str_Sync,"");
        fileName = fileName.substring(0,fileName.lastIndexOf("_"));
        return getTargetHostAndName(targetFile)+urlEncoder(judgeWorkDir(targetFile.getDir()),targetFile)+urlEncoder(getFileDir(fileBean),targetFile)+"/"+urlEncoder(fileName,targetFile);
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
     * �滻���һ����ʶ�ַ���Ϊ������ַ���
     * @param source                Դ�ַ���
     * @param sourceSeparator            Դ�ַ������
     * @param targetSeparator                    Ŀ���ַ������
     * @return
     */
    public String replaceOtherSeparator(String source,String sourceSeparator,String targetSeparator){
        StringBuilder sb=new StringBuilder("");
        int last = 0;
        if(source.contains(sourceSeparator)){
            last = source.lastIndexOf(sourceSeparator);
        }
        sb.append(source.substring(0,last)).append(targetSeparator).append(source.substring(last+sourceSeparator.length(),source.length()));
        return sb.toString();
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
     * ����URL
     * @param dir   ��Ҫ�����·��
     * @param targetFile       Ŀ��������ļ�
     * @return
     */
    public String urlDecoder(String dir,TargetFile targetFile){
        String url=null;
        try {
            url = URLDecoder.decode(dir,targetFile.getCharset());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     * ��·������URLת�����
     * @param dir   ��Ҫת����ļ�·��
     * @return       ����URLת�����ļ�·��
     */
    public String urlEncoder(String dir,TargetFile targetFile){
        String encodeUrl="/";
        if(!dir.equals("")&&dir!=null){
            if(dir.contains("/")){
                String [] strings=dir.split("/");
                if(strings!=null){
                    for (int i=0;i<strings.length;i++){
                        if(!strings[i].equals("")&&strings[i]!=null) {
                            if(i!=(strings.length-1)){
                                try {
                                    encodeUrl+= URLEncoder.encode(strings[i],targetFile.getCharset())+"/";
                                } catch (UnsupportedEncodingException e) {
                                    logger.error(e.getMessage()+"urlת�뱨��");
                                }
                            }else{
                                if(dir!=null){
                                    try {
                                        encodeUrl+=URLEncoder.encode(strings[i],targetFile.getCharset());
                                    }catch (UnsupportedEncodingException e) {
                                        logger.error(e.getMessage()+"urlת�뱨��");
                                    }
                                }
                            }
                        }
                    }
                }
            }else{
                try {
                    encodeUrl=URLEncoder.encode(dir,targetFile.getCharset());
                } catch (UnsupportedEncodingException e) {
                    logger.error(e.getMessage()+"urlת�뱨��");
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
     * �õ�Ŀ��˵��������͵�ַ
     * @param targetFile  Ŀ��������ļ�
     * @return        ����Ŀ��������·��
     */
    public String getTargetHostAndName(TargetFile targetFile){
        return  "http://"+targetFile.getServerAddress()+":"+targetFile.getPort();
    }

}
