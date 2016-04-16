package com.fartec.ichange.plugin.filechange;

import com.fartec.ichange.plugin.filechange.source.plugin.SourceProcessSmb;
import com.fartec.ichange.plugin.filechange.target.plugin.ITargetProcess;
import com.fartec.ichange.plugin.filechange.target.plugin.TargetProcessSmb;
import com.inetec.common.config.nodes.SourceFile;
import com.inetec.common.config.nodes.TargetFile;
import junit.framework.TestCase;

/**
 * Created by IntelliJ IDEA.
 * User: Ǯ����
 * Date: 12-3-14
 * Time: ����7:10
 * To change this template use File | Settings | File Templates.
 */
public class TestSourceProcessSmb extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testDummy() {
        assertTrue(true);
    }

    public void testProcess(){

        SourceProcessSmb smb = new SourceProcessSmb();
        ITargetProcess iSmb = new TargetProcessSmb();
//        TargetOperation targetOperation = new TargetOperation(smb,is);

        TargetFile configTarget = new TargetFile();
        SourceFile config = new SourceFile();
//        iSmb.init( targetOperation, configTarget);
        smb.init(iSmb, config);
        new Thread(smb).start();

        while (smb.isRun()) {
            smb.run();
            smb.stop();
        }
    }

}
