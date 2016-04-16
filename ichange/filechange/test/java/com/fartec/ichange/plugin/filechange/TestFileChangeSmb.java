package com.fartec.ichange.plugin.filechange;

import com.fartec.ichange.plugin.filechange.utils.SambaUtil;
import com.inetec.common.config.ConfigParser;
import com.inetec.common.config.nodes.IChange;
import com.inetec.common.config.nodes.SourceFile;
import com.inetec.common.config.nodes.TargetFile;
import com.inetec.common.config.nodes.Type;
import com.inetec.common.exception.Ex;
import com.inetec.ichange.api.DataAttributes;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;
import junit.framework.TestCase;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;


/**
 * Created by IntelliJ IDEA.
 * User: bluesky
 * Date: 2010-9-16
 * Time: 23:29:56
 * To change this template use File | Settings | File Templates.
 */
public class TestFileChangeSmb extends TestCase {
    ChangeMainImp main = new ChangeMainImp();
    FileChangeSource source = new FileChangeSource();
    FileChangeTarget target = new FileChangeTarget();
    ChangeTypeImp type;
    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

//    public void testPutFileToSourceServer(){
//        String typeName = "filePutToSourceServer";
//        SourceFile sourceFile = getSourceFile(typeName);
//        TargetFile targetFile = getTartFile(typeName);
//        int i = 1;
//        while (true){
//            System.out.println("开始复制第--"+(i++)+"--次!");
//            init(sourceFile,targetFile);
//        }
//    }

    public void testFileChangeSmb() throws Exception {
//
        init("filesmb");
        ConfigParser config;
        IChange changenode = null;
        try{
            config = new ConfigParser("D:\\fartec\\ichange\\filechange910\\test\\resource\\config_smb.xml");
            changenode = config.getRoot();
        } catch (Exception e){
            e.printStackTrace();
        }
        //ConfigParser config = new ConfigParser("/media/sda5/inetec/ichange/sipchange/utest/resources/config.xml");
        //\Plugin plugin = type.getPlugin();
        System.setProperty("privatenetwork", "false");
        main.setTargetPlugin(target);
        //main.setTargetPlugin(target1);
        target.init(main, type, source);
        source.init(main, type, target);
        target.config(changenode);
        source.config(changenode);
        source.start(new DataAttributes());
        while (true){
            Thread.sleep(1000*5);
        }
    }

    private void init(String typeName){
        type = new ChangeTypeImp(typeName);
    }
    private void _init(String typeName){
        type = new ChangeTypeImp(typeName);
        SourceFile sourceFile = getSourceFile(typeName);
        TargetFile targetFile = getTartFile(typeName);
        init(sourceFile,targetFile);
    }
    private void init(SourceFile config, TargetFile configTarget) {
        String url = SambaUtil.makeSmbUrl(config);
        String urlTarget = SambaUtil.makeSmbUrl(configTarget);
        try {
            SmbFile smbFile = new SmbFile(url);
//            SmbFile[] smbFiles = smbFile.listFiles();
//            for(SmbFile s : smbFiles){
//                s.delete();
//            }
            copy(smbFile);
            smbFile = new SmbFile(urlTarget);
//            smbFiles = smbFile.listFiles();
//            for(SmbFile s : smbFiles){
//                s.delete();
//            }
            if(configTarget.isIstwoway()){
                copy(smbFile);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
//        } catch (SmbException e) {
//            e.printStackTrace();
        }
    }
    private void copy(SmbFile smbFile) {
        try {
            String path = "D:\\fartec\\ichange\\filechange910\\test\\resource\\file";
            File[] fileNames = new File(path).listFiles();
            String canonicalPath = smbFile.getCanonicalPath();
            URL url = smbFile.getURL();
            String query = url.getQuery();
            for (File f : fileNames){
                String smbFileUrl = canonicalPath+f.getName()+"-"+System.currentTimeMillis()+"/?"+query;
                smbFile = new SmbFile(smbFileUrl);
                if(f.isFile()){
                    sendFile(f,smbFile);
                }else if(f.isDirectory()){
                    smbFile.mkdir();
                    smbFile.canWrite();
                    includesFile(f, smbFile);
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (SmbException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private void includesFile(File file, SmbFile smbFile) {
        try{
            File[] fileNames = file.listFiles();
            String canonicalPath = smbFile.getCanonicalPath();
            URL url = smbFile.getURL();
            String query = url.getQuery();
            for (File f : fileNames){
                smbFile = new SmbFile(canonicalPath+f.getName()+"/?"+query);
                if(f.isFile()){
                    sendFile(f,smbFile);
                }else if(f.isDirectory()){
                    smbFile.mkdir();
                    smbFile.canWrite();
                    includesFile(f, smbFile);
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (SmbException e) {
            e.printStackTrace();
        }
    }

    private void sendFile(File file,SmbFile smbFile) {
        InputStream in = null;
        try {
            in = new FileInputStream(file) ;
            int len = 0;
            byte[] buf = new byte[1024*1024*2];
            int size = in.available();
            if(size < 1024*1024*2){
                write(smbFile,in,false);
            }else{
                while((len = in.read(buf))!=-1){
                    if(len < 1024*1024*2){
                        in.close();
                        in = new FileInputStream(file) ;
                        in.skip(size-len);
                        write(smbFile,in,true);
                    }else {
                        write(smbFile,buf);
                    }
                }
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void write(SmbFile smbFile, byte[] buf) {
        try {
            OutputStream out = new SmbFileOutputStream(smbFile,true);
            out.write(buf);
            out.flush();
            out.close();
        } catch (SmbException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (MalformedURLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (UnknownHostException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private void write(SmbFile smbFile, InputStream in, boolean append) {

        OutputStream out = null;
        try{
            smbFile.connect();
            if(append){
                out = new SmbFileOutputStream(smbFile,true);
            }else {
                out = new SmbFileOutputStream(smbFile);
            }
            int len = 0 ;
            byte[] buf = new byte[1024];
            while ((len = in.read(buf))!=-1){
                out.write(buf,0,len);
            }
            out.flush();
            out.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (UnknownHostException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (SmbException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public static TargetFile getTartFile(String typeName) {
        String path = "D:\\fartec\\ichange\\filechange910\\test\\resource\\config_smb.xml";
        ConfigParser configParser = null;
        TargetFile config = null;
        try {
            configParser = new ConfigParser(path);
            Type type = configParser.getRoot().getType(typeName);
            config = type.getPlugin().getTargetFile();
            System.out.println(config.getCharset());
        } catch (Ex ex) {
            ex.printStackTrace();
        }
        return config;
    }

    public static SourceFile getSourceFile(String typeName) {
        String path = "D:\\fartec\\ichange\\filechange910\\test\\resource\\config_smb.xml";
        ConfigParser configParser = null;
        SourceFile config = null;
        try {
            configParser = new ConfigParser(path);
            Type type = configParser.getRoot().getType(typeName);
            config = type.getPlugin().getSourceFile();
        } catch (Ex ex) {
            ex.printStackTrace();
        }
        return config;
    }

}
