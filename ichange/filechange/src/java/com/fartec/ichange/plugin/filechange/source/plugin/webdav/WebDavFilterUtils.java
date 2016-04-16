package com.fartec.ichange.plugin.filechange.source.plugin.webdav;

import com.fartec.ichange.plugin.filechange.utils.FileType;
import com.inetec.common.config.nodes.SourceFile;
import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: hhm
 * Date: 12-4-7
 * Time: ����8:41
 *�����ļ�����
 */

public class WebDavFilterUtils {

    private Logger logger = Logger.getLogger(WebDavFilterUtils.class);
    private WebDavUtils webDavUtils = new WebDavUtils();

    /**
     * �ļ����˷���
     * @param sourceFile    Դ�������ļ�
     * @param resource      Դ�˷�������Դ
     * @return      ͨ���˵Ĺ����ɵ�fileBean����
     */
    public boolean fileBeanFilter(String fileName,SourceFile sourceFile,byte[] data){
        if(fileName.endsWith("rar") || fileName.endsWith("tar") || fileName.endsWith("zip") || fileName.endsWith("gz") || fileName.endsWith("7z")){
            return false;
        }
        fileName = FileType.getFileType(fileName,data);
        //�����ʶλ
        boolean flag=false;
        //�����ܹ��˵������ַ���
        String canFilterFileTypes=sourceFile.getFiltertypes();
        //���ղ��ܹ��������ַ���
        String notCanFilterFileTypes=sourceFile.getNotfiltertypes();
        //�ܹ����˵�����
        String[] canFilterTypes=null;
        //���ܹ����˵�����
        String[] notCanFilterTypes=null;
        //�ɹ����ļ�����
        if(canFilterFileTypes!=null){
            canFilterTypes=canFilterFileTypes.split(",");
        }
        //���ɹ����ļ�����
        if(notCanFilterFileTypes!=null){
            notCanFilterTypes=notCanFilterFileTypes.split(",");
        }
        //��������ļ�û������������
        if(canFilterFileTypes.equals("")&&notCanFilterFileTypes.equals("")){
            logger.info("***û�����ù����ļ�����!�����ù������ͺ�����!***");
            return  false;
        } else if(canFilterFileTypes.equals("")){
            if(!notCanFilterFileTypes.equals("")) {
                flag=true;
                if(notCanFilterTypes!=null){
                    for (int j=0;j<notCanFilterTypes.length;j++){
                        if(!notCanFilterTypes[j].equals("")) {
                            if(webDavUtils.getFileName(notCanFilterTypes[j]).equals("*")){
                                if(webDavUtils.getFileType(notCanFilterTypes[j]).equals("*")) {
                                    return false;

                                }else {
                                    String  type= webDavUtils.getFileType(notCanFilterTypes[j]);
                                    String fileType= webDavUtils.getFileType(fileName);
                                    if(fileType.equals(type)) {
                                        return false;
                                    }
                                }
                            }else{
                                String  filename= webDavUtils.getFileName(notCanFilterTypes[j]);
                                String  fileBeanName= webDavUtils.getFileName(fileName);
                                if(filename.equals(fileBeanName)){
                                    if(webDavUtils.getFileType(notCanFilterTypes[j]).equals("*")){
                                        return false;
                                    }else{
                                        String  filenameType= webDavUtils.getFileType(notCanFilterTypes[j]);
                                        String  fileBeanNameType= webDavUtils.getFileType(fileName);
                                        if(filenameType.equals(fileBeanNameType)){
                                            return false;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

            }
        }else {
            //��ʾ�и����ַ�����
            if(canFilterTypes!=null){
                for (int i=0;i<canFilterTypes.length;i++){
                    if(!canFilterTypes[i].equals("")) {
                        if(webDavUtils.getFileName(canFilterTypes[i]).equals("*")){
                            if(webDavUtils.getFileType(canFilterTypes[i]).equals("*")) {
                                flag=true;
                            }else{
                                String type= webDavUtils.getFileType(canFilterTypes[i]);
                                String fileType= webDavUtils.getFileType(fileName);
                                if(type.equals(fileType)){
                                    flag=true;
                                }
                            }
                        }else{
                            String  filename= webDavUtils.getFileName(canFilterTypes[i]) ;
                            String  fileBeanName= webDavUtils.getFileName(fileName);
                            if(filename.equals(fileBeanName)){
                                if(webDavUtils.getFileType(canFilterTypes[i]).equals("*")) {
                                    flag=true;
                                }else{
                                    String  filenameType= webDavUtils.getFileType(canFilterTypes[i]) ;
                                    String  fileBeanNameType= webDavUtils.getFileType(fileName);
                                    if(filenameType.equals(fileBeanNameType)){
                                        flag=true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if(notCanFilterTypes!=null){
                for (int j=0;j<notCanFilterTypes.length;j++){
                    if(!notCanFilterTypes[j].equals("")) {
                        if(webDavUtils.getFileName(notCanFilterTypes[j]).equals("*")){
                            if(webDavUtils.getFileType(notCanFilterTypes[j]).equals("*")) {
                                return false;
                            }else {
                                String  type= webDavUtils.getFileType(notCanFilterTypes[j]);
                                String fileType= webDavUtils.getFileType(fileName);
                                if(fileType.equals(type)) {
                                    return false;
                                }
                            }
                        }else{
                            String  filename= webDavUtils.getFileName(notCanFilterTypes[j]);
                            String  fileBeanName= webDavUtils.getFileName(fileName);
                            if(filename.equals(fileBeanName)){
                                if(webDavUtils.getFileType(notCanFilterTypes[j]).equals("*")){
                                    return false;
                                }else{
                                    String  filenameType= webDavUtils.getFileType(notCanFilterTypes[j]);
                                    String  fileBeanNameType= webDavUtils.getFileType(fileName);
                                    if(filenameType.equals(fileBeanNameType)){
                                        return false;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return flag;
    }
}
