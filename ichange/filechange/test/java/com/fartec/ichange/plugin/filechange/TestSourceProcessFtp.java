package com.fartec.ichange.plugin.filechange;

import com.fartec.ichange.plugin.filechange.source.plugin.SourceProcessFtp;
import com.fartec.ichange.plugin.filechange.target.plugin.ITargetProcess;
import com.fartec.ichange.plugin.filechange.target.plugin.TargetProcessFtp;
import com.inetec.common.config.nodes.SourceFile;
import com.inetec.common.security.FileMd5;
import com.inetec.unitest.UniTestCase;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: bluesky
 * Date: 12-3-12
 * Time: ����4:19
 * To change this template use File | Settings | File Templates.
 */
public class TestSourceProcessFtp extends UniTestCase {
    public TestSourceProcessFtp(String name) {
        super(name);
    }
    public void testFtpProcess() {
        SourceProcessFtp ftp = new SourceProcessFtp();
        ITargetProcess tftp = new TargetProcessFtp();
        ftp.init(tftp, new SourceFile());
        new Thread(ftp).start();
        while (ftp.isRun()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                //okay
            }
        }


    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testSourceProcessFileMd5() {
        try {
            System.out.println("md5 value:" + FileMd5.getFileMD5String("d:\\test.txt"));
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
