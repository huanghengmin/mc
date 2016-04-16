package com.fartec.ichange.plugin.filechange.source.plugin.webdav;

import com.fartec.ichange.plugin.filechange.utils.FileBean;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: hhm
 * Date: 12-5-6
 * Time: ����4:36
 * To change this template use File | Settings | File Templates.
 */
public class WebdavSourceTempFileUtils {
    private static final String tempFilePath = System.getProperty("ichange.home")+"/temp/webdav/";
    private Logger logger = Logger.getLogger(WebdavSourceTempFileUtils.class);
    private static WebdavSourceTempFileUtils webdavSourceTempFileUtils = null;
    private WebdavSourceTempFileUtils(){}
    public static WebdavSourceTempFileUtils getSingleWebdavSourceTempFile(){
        if(webdavSourceTempFileUtils == null){
            webdavSourceTempFileUtils = new WebdavSourceTempFileUtils();
        }
        return webdavSourceTempFileUtils;
    }
    /**
     * ����Ŀ¼
     * @param destDirName Ŀ��Ŀ¼��
     * @return Ŀ¼�����ɹ�����true�����򷵻�false
     */
    public void createDir(String destDirName) {
        File dir = new File(destDirName);
        if(!dir.exists()) {
            if(!destDirName.endsWith(File.separator))
                destDirName = destDirName + File.separator;
            // ��������Ŀ¼
            if(dir.mkdirs()) {
                System.out.println("����Ŀ¼" + destDirName + "�ɹ���");
            } else {
                System.out.println("����Ŀ¼" + destDirName + "�ɹ���");
            }
        }
    }

    /**
     * �õ���ʱ�ļ�
     * @param fileBean �ļ�����
     * @return    �õ�����ʱ�ļ�
     */
    public File getFile(FileBean fileBean){
        logger.info(fileBean.getFullname()+"�ļ�ȫ��");
        String fileFullName = null;
        if(fileBean.getFullname().contains("/"))
            fileFullName =fileBean.getFullname().replace("/",File.separator);
        else
            fileFullName = fileBean.getFullname();
        String path = null;
        if(fileFullName.startsWith(File.separator))
            path = tempFilePath+fileFullName;
        else
            path = tempFilePath+File.separator+fileFullName;
        File file = new File(path);
        if(!file.exists()){
            String dir = null;
            if(path.contains(File.separator)){
                dir = path.substring(0,path.lastIndexOf(File.separator));
            } else {
                dir = path;
            }
            createDir(dir);
            try {
                file.createNewFile();
            } catch (IOException e) {
               logger.info(e.getMessage());  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        return  file;
    }
    /**
     * ������ʱ�ļ�
     * @param prefix ��ʱ�ļ���ǰ׺
     * @param suffix ��ʱ�ļ��ĺ�׺
     * @param dirName ��ʱ�ļ����ڵ�Ŀ¼���������null�������û����ĵ�Ŀ¼�´�����ʱ�ļ�
     * @return ��ʱ�ļ������ɹ����س���·�����Ĺ淶·�����ַ��������򷵻�null
     */
    public void createToTempFile(FileBean fileBean,InputStream in) {
        File file  =  getFile(fileBean);
        if(file.exists()){
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(file,true);
                try {
                    fileOutputStream.write(IOUtils.toByteArray(in));
                } catch (IOException e) {
                  logger.info(e.getMessage());  //To change body of catch statement use File | Settings | File Templates.
                }
            } catch (FileNotFoundException e) {
                logger.info(e.getMessage()); //To change body of catch statement use File | Settings | File Templates.
            } finally {
                try {
                    if(in != null)
                        in.close();
                } catch (IOException e) {
                 logger.info(e.getMessage());  //To change body of catch statement use File | Settings | File Templates.
                }
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                 logger.info(e.getMessage());  //To change body of catch statement use File | Settings | File Templates.
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
        File file = getFile(fileBean);
        if(file.exists()){
            if(fileBean.getFilesize() == file.length()){
                logger.info("��ʱ�ļ�����ɹ�");
                return  true;
            }
        }
        return false;
    }
}
