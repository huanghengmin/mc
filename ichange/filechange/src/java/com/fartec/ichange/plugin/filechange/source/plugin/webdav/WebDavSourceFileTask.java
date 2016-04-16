package com.fartec.ichange.plugin.filechange.source.plugin.webdav;

import com.fartec.ichange.plugin.filechange.source.SourceOperation;
import com.fartec.ichange.plugin.filechange.utils.FileBean;
import com.googlecode.sardine.Sardine;
import com.googlecode.sardine.SardineFactory;
import com.inetec.common.config.nodes.SourceFile;
import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Daly
 * Date: 12-4-25
 * Time: ����2:16
 * To change this template use File | Settings | File Templates.
 */
public class WebDavSourceFileTask implements Runnable{
    //Ҫ���͵��ļ�����
    private FileBean sendFileBean;
    //Դ�������ļ�
    private SourceFile sourceFile;
    //���͵�Ŀ��˵Ĳ�������
    private SourceOperation sourceOperation;
    //��ӡ��־����
    private Logger logger = Logger.getLogger(WebDavSourceFileTask.class);
    //�����ļ�������
    private WebDavSourceSendUtils webDavSourceSendUtils = new WebDavSourceSendUtils();

    public WebDavSourceFileTask(FileBean sendFileBean, SourceFile sourceFile, SourceOperation sourceOperation){
        //Դ������ �ļ�
        this.sourceFile = sourceFile;
        //���͵�Ŀ��˲�������
        this.sourceOperation = sourceOperation;
        //Ҫ���͵ĵ����ļ�����
        this.sendFileBean = sendFileBean;
    }

    @Override
    public void run() {
        //Դ�˲�������
        Sardine sardine= SardineFactory.begin(sourceFile.getUserName(),sourceFile.getPassword());
        //�����ļ�
        webDavSourceSendUtils.processFile(sendFileBean,sourceOperation,sardine,sourceFile);
        //��ӡ�߳�������ɱ�ʶ
        logger.info("**"+"�߳�����**" + Thread.currentThread().getName() + "**����!!!**");
    }
}
