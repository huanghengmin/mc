package com.fartec.ichange.plugin.filechange.utils;

import com.inetec.common.config.nodes.SourceFile;
import org.apache.commons.net.ftp.FTPFile;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 12-3-27
 * Time: ����12:43
 * To change this template use File | Settings | File Templates.
 */
public class FtpFileCompare {
    /**
     * ftptile ת����filebean  ����
     * @param file      ftp�ļ�����
     * @param workdir    ����·��
     * @param sourceFile   Դ���ļ�����
     * @return
     */

    public static FileBean _FTPFileToBean(FTPFile file,String workdir,SourceFile sourceFile){
        FileBean result = new FileBean();
//        String[] st=workdir.split("/");
        String strdir=null;
        if(workdir.equalsIgnoreCase(sourceFile.getDir())){     //�������·������Դ��ƥ��ĸ�Ŀ¼��
            workdir="";
            strdir=(workdir+file.getName()).trim();                          //����Ŀ¼ȥ��Դ�˸�Ŀ¼
        }else {
            if(sourceFile.getDir().equals("/")){
                strdir=workdir+"/"+file.getName();
            }
            else if(sourceFile.getDir().endsWith("/")){
                strdir=FileFilter.getSubString(workdir,sourceFile.getDir())+"/"+file.getName();
            }else {
                strdir=FileFilter.getSubString(workdir,sourceFile.getDir()+"/")+"/"+file.getName();
            }
        }
        result.setFullname(strdir);
        result.setName(file.getName());
        result.setTime(file.getTimestamp().getTimeInMillis());
        result.setFilesize(file.getSize());
        return result;
    }
}

