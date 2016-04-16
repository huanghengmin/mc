package com.fartec.ichange.plugin.filechange.source.plugin.webdav;

import com.fartec.ichange.plugin.filechange.source.SourceOperation;
import com.fartec.ichange.plugin.filechange.utils.FileList;
import com.googlecode.sardine.Sardine;
import com.googlecode.sardine.SardineFactory;
import com.inetec.common.config.nodes.SourceFile;
import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: hhm
 * Date: 12-4-17
 * Time: ����11:12
 * To change this template use File | Settings | File Templates.
 */
public class WebDavSourceListTask implements  Runnable{
    //��Ҫ���͵��б����
    private FileList sendFileList;
    //Դ�������ļ�
    private SourceFile sourceFile;
    //�����ļ���Ŀ��˵Ĳ�������
    private SourceOperation sourceOperation;
    //��ӡ��־����
    private Logger logger=Logger.getLogger(WebDavSourceListTask.class);
    //�����ļ�������
    private WebDavSourceSendUtils webDavSourceSendUtils=new WebDavSourceSendUtils();

    public WebDavSourceListTask(FileList sendFileList, SourceFile sourceFile, SourceOperation sourceOperation){
        //Դ�������ļ�
        this.sourceFile=sourceFile;
        //���͵�Ŀ��˵Ĳ�������
        this.sourceOperation=sourceOperation;
        //���͵��ļ��б����
        this.sendFileList=sendFileList;
    }

    @Override
    public void run() {
        //Դ�˲�������
        Sardine sardine= SardineFactory.begin(sourceFile.getUserName(),sourceFile.getPassword());
        //�����ļ�
        webDavSourceSendUtils.processList(sourceFile, sendFileList,sardine,sourceOperation);
        //��ӡ�߳̽�����ʶ
        logger.info("*****�߳�����*****"+Thread.currentThread().getName()+"*****����*****");
    }
}
