package com.fartec.ichange.plugin.filechange.utils;

import com.inetec.common.config.nodes.SourceFile;
import com.inetec.common.config.nodes.TargetFile;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import jcifs.smb.SmbFilenameFilter;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;

/**
 * Created by IntelliJ IDEA.
 * User: ?????
 * Date: 12-5-10
 * Time: ????9:14
 * To change this template use File | Settings | File Templates.
 */
public class SambaFilterName implements SmbFilenameFilter{
    private Logger logger = Logger.getLogger(SambaFilterName.class);
    private SourceFile sourceFile;
    private TargetFile targetFile;
    private String parent ;
    public SambaFilterName(SourceFile sourceFile , String parent) {
        this.sourceFile = sourceFile;
        this.parent = parent ;
    }

    public SambaFilterName(TargetFile targetFile) {
        this.targetFile = targetFile;
    }

    public boolean accept(SmbFile smbFile, String fileName) throws SmbException {

        if( fileName.endsWith(FileContext.Str_SyncFileTargetProcess_End_Flag)){
            return false;
        }
        if(sourceFile != null && targetFile == null){
            return fixFilter(fileName);
        }else if(sourceFile == null && targetFile != null){
            return true;
        }
        return false;
    }

    private boolean fixFilter(String fileName) {
        if(fileName.endsWith("rar") || fileName.endsWith("tar") || fileName.endsWith("zip") || fileName.endsWith("gz") || fileName.endsWith("7z")){
            return false;
        }
        String url = parent.substring(0,parent.lastIndexOf("?"));
        String charset = parent.substring(parent.lastIndexOf("?"),parent.length());
        String fullname = url + fileName + charset ;
        byte[] data = new byte[10] ;
        try {
            SmbFile smbFile = new SmbFile(fullname);
            SmbFileInputStream smbFileInputStream = new SmbFileInputStream(smbFile);
            smbFileInputStream.read(data);
            smbFileInputStream.close();
        } catch (MalformedURLException e) {
            logger.error("smb过滤器中创建smbfile失败",e);  //To change body of catch statement use File | Settings | File Templates.
        } catch (UnknownHostException e) {
            logger.error("smb过滤器中创建smbFileInputStream失败",e);  //To change body of catch statement use File | Settings | File Templates.
        } catch (SmbException e) {
            logger.error("smb过滤器中创建smbFileInputStream失败",e); //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            logger.error("smb过滤器中读smbFileInputStream失败",e); //To change body of catch statement use File | Settings | File Templates.
        }
        fileName = FileType.getFileType(fileName,data);
        String filterType = null;
        boolean isFilterTypes = false;
        boolean isFilterTypesAll = false;
        if(sourceFile.getFiltertypes()!=null&&(sourceFile.getNotfiltertypes()==null||sourceFile.getNotfiltertypes().equals(""))){
            filterType = sourceFile.getFiltertypes();
            isFilterTypesAll = filterType.equals("*.*");
            if(isFilterTypesAll){
                return true;
            } else {
                String[] filterTypes = filterType.split(",");
                if(filterTypes.length>1){
                    for(int i = 0 ; i < filterTypes.length;i++){
                        isFilterTypes = fileName.endsWith(filterTypes[i].substring(filterTypes[i].lastIndexOf(".")));
                        if(isFilterTypes){
                            return true;
                        }
                    }
                }else if(filterTypes.length == 1){
                    isFilterTypes = fileName.endsWith(filterType.substring(filterType.lastIndexOf(".")));
                    if(isFilterTypes){
                        return true;
                    }
                }
            }
        }
        if(sourceFile.getNotfiltertypes()!=null&& (sourceFile.getFiltertypes()==null||sourceFile.getFiltertypes().equals(""))){
            filterType = sourceFile.getNotfiltertypes();
            isFilterTypesAll = filterType.equals("*.*");
            if(isFilterTypesAll){
                return false;
            } else {
                String[] filterTypes = filterType.split(",");
                if(filterTypes.length>1){
                    int flag = 0;
                    for(int i = 0 ; i < filterTypes.length;i++){
                        isFilterTypes = fileName.endsWith(filterTypes[i].substring(filterTypes[i].lastIndexOf(".")));
                        if(isFilterTypes){
                            flag ++;
                        }
                    }
                    if (flag == 0){
                        return true;
                    }
                }else if(filterTypes.length == 1){
                    isFilterTypes = fileName.endsWith(filterType.substring(filterType.lastIndexOf(".")));
                    if(!isFilterTypes){
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
