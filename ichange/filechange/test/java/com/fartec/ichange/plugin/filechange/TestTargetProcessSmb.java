package com.fartec.ichange.plugin.filechange;

import com.fartec.ichange.plugin.filechange.target.plugin.TargetProcessSmb;
import com.fartec.ichange.plugin.filechange.utils.FileList;
import com.inetec.common.config.nodes.TargetFile;
import junit.framework.TestCase;

/**
 * Created by IntelliJ IDEA.
 * User: Ǯ����
 * Date: 12-3-16
 * Time: ����3:33
 * To change this template use File | Settings | File Templates.
 */
public class TestTargetProcessSmb extends TestCase {
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
        TargetProcessSmb smb = new TargetProcessSmb();
//        TargetOperation target = new TargetOperation();
        TargetFile config = new TargetFile();
//        smb.init(target , config);
        FileList sourceFileList = new FileList();
        smb.procesFileList(sourceFileList);
    }

}
