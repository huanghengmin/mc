package com.fartec.ichange.plugin.filechange.source.plugin;

import com.fartec.ichange.plugin.filechange.source.SourceOperation;
import com.fartec.ichange.plugin.filechange.target.plugin.ITargetProcess;
import com.fartec.ichange.plugin.filechange.utils.FileBean;
import com.fartec.ichange.plugin.filechange.utils.FileList;
import com.inetec.common.config.nodes.SourceFile;

import java.io.InputStream;

/**
 * Created by IntelliJ IDEA.
 * User: bluesky
 * Date: 11-11-26
 * Time: ����3:21
 * To change this template use File | Settings | File Templates.
 */
public interface ISourceProcess extends Runnable {
    /**
     * �������ݱ��浽���ļ�
     *
     * @param in   ������
     * @param bean �ļ�����
     * @return ����ɹ�����true ,��֮����false;
     */
    public boolean process(InputStream in, FileBean bean);

    /**
     * �������ݱ��浽���ļ�
     *
     * @param data �����ƶ���
     * @param bean �ļ�����
     * @return ����ɹ�����true ,��֮����false;
     */

    public boolean process(byte[] data, FileBean bean);

    /**
     * ���Դ�˴������ĳ�ʼ��
     *
     * @param source ԭ�˲�������
     * @param config Դ��������Ϣ
     */

    public void init(SourceOperation source, SourceFile config);
    /**
     * ���Դ�˴������ĳ�ʼ��
     *
     * @param target Ŀ��˲�������
     * @param config Դ��������Ϣ
     */

    public void init(ITargetProcess target, SourceFile config);

    /**
     * ֹͣ����
     */

    public void stop();

    /**
     * �б�ò���Ƿ�������
     *
     * @return �����з���true ,��֮����false;
     */

    public boolean isRun();

    /**
     * ����Ŀ����ļ��б�
     *
     * @param list �ļ��б����
     * @return ����Ҫͬ�����ļ��б�
     */

    public FileList procesFileList(FileList list);

}
