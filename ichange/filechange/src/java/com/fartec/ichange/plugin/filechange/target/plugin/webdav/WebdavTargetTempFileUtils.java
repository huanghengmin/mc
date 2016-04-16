package com.fartec.ichange.plugin.filechange.target.plugin.webdav;

import com.fartec.ichange.plugin.filechange.utils.FileBean;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: hhm
 * Date: 12-5-6
 * Time: ����4:23
 * To change this template use File | Settings | File Templates.
 */
public class WebdavTargetTempFileUtils {

    private Logger logger=Logger.getLogger(WebdavTargetTempFileUtils.class);
    private static final String tempFilePath = System.getProperty("ichange.home")+"/temp/webdav";

    /**
     * ����Ŀ¼
     * @param destDirName Ŀ��Ŀ¼��
     * @return Ŀ¼�����ɹ�����true�����򷵻�false
     */
    public void createTempFileDir(String destDirName) {
        File dir = new File(destDirName);
        if(!dir.exists()) {
            if(!destDirName.endsWith("/"))
                destDirName = destDirName + "/";
            // ��������Ŀ¼
            if(dir.mkdirs()) {
                System.out.println("**����Ŀ¼" + destDirName + "�ɹ�!!!**");
            } else {
                System.out.println("**����Ŀ¼" + destDirName + "ʧ��!!!**");
            }
        }
    }

    /**
     * ɾ����ʱ�ļ��ļ���
     * @param file �ļ�����
     */
    public  void deleteTempFileFolder(String path){
        if(path.contains(File.separator)){
            path = path.replace(File.separator,"\\/");
            path = path.substring(0,path.lastIndexOf("\\/"));
            if(!path.equals(tempFilePath)){
                File file=new File(path);
                if(file.listFiles().length<=0){
                    file.delete();
                    deleteTempFileFolder(file.getAbsolutePath());
                }
            }

        }
    }

    /**
     * ������ʱ �ļ�
     * @param fileBean �ļ�����
     * @return    �����ļ��Ƿ����
     */
    public boolean existsTempFile(FileBean fileBean){
        File file = getTempFile(fileBean);
        if(file!=null){
            if(file.exists()){
                if(fileBean.getFilesize() == file.length()){
                    return  true;
                }  else {
                    return false;
                }
            }
        }
        return false;
    }

    public boolean existsSyncIngTempFile(FileBean fileBean){
        File file = getTempFile(fileBean);
        if(file!=null){
            if(file.exists()){
                if(file.length()>fileBean.getFilesize()){
                    file.delete();
                    deleteTempFileFolder(file.getAbsolutePath());
                }
                return  true;
            }  else {
                return false;
            }
        }
        return false;
    }

    /**
     * ��������ʱ�ļ�
     * @param fileBean �ļ�����
     * @param in            �ļ���
     * @return
     */
    public boolean createToTempFile(FileBean fileBean,InputStream in) {
        logger.info("Ŀ���������ʱ�ļ�·��"+tempFilePath);
        File file  =  getTempFile(fileBean);
        if(file!=null){
            if(file.exists()){
                FileOutputStream fileOutputStream=null;
                try {
                    fileOutputStream = new FileOutputStream(file,true);
                    try {
                        fileOutputStream.write(IOUtils.toByteArray(in));
                    } catch (IOException e) {
                        logger.info(e.getMessage());
                    }
                } catch (FileNotFoundException e) {
                    logger.info(e.getMessage());
                } finally {
                    try {
                        if(in!=null)
                            in.close();
                    } catch (IOException e) {
                        logger.info(e.getMessage());
                        return false;
                    }
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        logger.info(e.getMessage());
                        return false;
                    }
                }
            }
        }
        if(file.length()>fileBean.getFilesize()){
            file.delete();
            deleteTempFileFolder(file.getAbsolutePath());
            return false;
        }
        return true;
    }

    /**
     *  �õ���ʱ�ļ�
     * @param fileBean �ļ�����
     * @return        ��ʱ�ļ�
     */
    public File getTempFile(FileBean fileBean){
        String path = getTempFilePath(fileBean);
        File file = new File(path);
        if(file.exists()){
            return file;
        }
        return null;
    }

    /**
     * �õ�δ����ļ���С
     * @param fileBean       �ļ�����
     * @return    Ŀ�����ʱ�ļ��Ĵ�С
     */
    public long getTempFileContentLength(FileBean fileBean){
        long  contentLength = 0;
        String path = getTempFilePath(fileBean);
        File file = new File(path);
        if(file.exists()){
            long  fileLength = file.length();
            if(fileLength>fileBean.getFilesize()){
                file.delete();
                deleteTempFileFolder(file.getAbsolutePath());
                return  0;
            } else {
                contentLength=file.length();
            }
        }
        return  contentLength;
    }

    /**
     * �õ��ļ�·��
     * @param fileBean  �ļ�����
     * @return
     */
    public String getTempFilePath(FileBean fileBean){
        String path =null;
        logger.info(fileBean.getFullname()+"�ļ�ȫ��");
        String fileFullName=null;
        if(fileBean.getFullname().contains("/"))
            fileFullName = fileBean.getFullname().replace("/",File.separator);
        else
            fileFullName = fileBean.getFullname();

        if(fileFullName.startsWith(File.separator))
            path = tempFilePath+fileFullName;
        else
            path = tempFilePath + File.separator + fileFullName;
        path =  path.replace(File.separator,"/");
        return  path;
    }

    /**
     *  ����ispfδ����ļ�
     * @param fileBean       ����ispfδ����ļ�
     * @return                    �����ɹ�����true���򷵻�false
     */
    public boolean createNewNotFinishedFileTempFile(FileBean fileBean){
        boolean  flag = false;
        String path = getTempFilePath(fileBean);
        File file = new File(path);
        if(!file.exists()){
            try {
                String dir=null;
                if(path.contains("/")){
                    dir=path.substring(0,path.lastIndexOf("/"));
                } else {
                    dir=path;
                }
                createTempFileDir(dir);   //����Ŀ¼
                flag = file.createNewFile();
            } catch (IOException e) {
                logger.info("�������ļ�����������");
            }
        }
        return flag;
    }

    /**
     *    ������ʱ�ļ�
     * @param fileBean �ļ�����
     * @return    ���ع����ļ��Ƿ�ɹ�
     */
    public boolean createNewNormalTempFile(FileBean fileBean){
        boolean  flag=false;
        String path = getTempFilePath(fileBean);
        File file=new File(path);
        if(file.exists()){
            file.delete();
        } else {
            try {
                String dir=null;
                if(path.contains("/")){
                    dir = path.substring(0,path.lastIndexOf("/"));
                } else {
                    dir = path;
                }
                createTempFileDir(dir);     //����Ŀ¼
                flag = file.createNewFile();
            } catch (IOException e) {
                logger.info("**�������ļ�����!!!!!**");
            }
        }
        return flag;
    }
}
