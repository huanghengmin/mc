/*
package com.inetec.test;


import com.inetec.common.config.nodes.Jdbc;
import com.inetec.common.config.nodes.Field;
import com.inetec.common.exception.Ex;
import com.inetec.ichange.console.config.Constant;
import com.inetec.ichange.console.config.database.DBFactory;
import com.inetec.ichange.console.config.database.DBUtil;
import com.inetec.ichange.console.config.database.IDataBase;
import com.inetec.unitest.UniTestCase;
import com.inetec.ichange.console.config.utils.TriggerBean;
import com.inetec.ichange.console.client.util.DbInitBean;

import java.util.List;
import java.util.Iterator;

*/
/**
 * �๦������.
 *
 * @author <a href="mailto:ljr@inetec.com.cn">������</a>
 * @version 1.0
 * @since 2009-6-21
 *//*

public class TestDBUtil extends UniTestCase {
    public TestDBUtil(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    */
/*  public void testGetTables() throws Ex {
        Jdbc jdbc = new Jdbc();
        jdbc.setDbUrl("jdbc:mysql://localhost:3306/iflow");
        jdbc.setDriverClass("com.mysql.jdbc.Driver");
        jdbc.setDbUser("root");
        jdbc.setPassword("");

       String[] names = DBUtil(jdbc);

        for (int i = 0; i < names.length; i++) {
            System.out.println(names[i]);
        }

        // String[] fields = DBUtil.getTableNames(jdbc);
        for (int i = 0; i < fields.length; i++) {
            System.out.println(fields[i]);
        }

        List fs = DBUtil.getFields(jdbc, "processes");
        for (Iterator it = fs.iterator(); it.hasNext(); ) {
            Field fd = (Field) it.next();
            System.out.println(fd.getColumnSize());
            System.out.println(fd.getFieldName());
            System.out.println(fd.getJdbcType());
            System.out.println(fd.isNull());
            System.out.println(fd.isPk());
            System.out.println("++++++++++++++++++++++++++++==");
            \
            <dbtype>oracle</dbtype>
          <dbvender>ORACLE</dbvender>
          <dbcatalog/>
          <dburl>jdbc:oracle:thin:@192.168.1.104:1521:test1</dburl>
          <dbowner>test1</dbowner>
          <dbhost>192.168.1.104</dbhost>
          <driverclass>oracle.jdbc.driver.OracleDriver</driverclass>
          <dbuser>test1</dbuser>
          <password>ipKDNSw3Ft0=</password>
          <encoding>utf-8</encoding>
        }
    }*//*

    public void testGetTables() throws Ex {
        Jdbc jdbc = new Jdbc();
        jdbc.setDbType("oracle");
        jdbc.setDbVender("ORACLE");
        jdbc.setDbUrl("jdbc:oracle:thin:@192.168.1.104:1521:test1");
        jdbc.setDbHost("192.168.1.104");
        jdbc.setDbOwner("test1");
        jdbc.setDriverClass("oracle.jdbc.driver.OracleDriver");
        jdbc.setDbUser("test1");
        jdbc.setPassword("ipKDNSw3Ft0=");
        jdbc.setEncoding("utf-8");

        IDataBase databese = DBFactory.getDataBase(jdbc, Constant.DB_INTERNAL);
        String[] names = databese.getTableNames();

        for (int i = 0; i < names.length; i++) {
            System.out.println(names[i]);
        }

        String[] fields = databese.getFieldNames("test");
        for (int i = 0; i < fields.length; i++) {
            System.out.println(fields[i]);
        }

        List fs = databese.getFields("test");
        for (Iterator it = fs.iterator(); it.hasNext(); ) {
            Field fd = (Field) it.next();
            System.out.println(fd.getColumnSize());
            System.out.println(fd.getFieldName());
            System.out.println(fd.getJdbcType());
            System.out.println(fd.isNull());
            System.out.println(fd.isPk());
            System.out.println("++++++++++++++++++++++++++++==");
        }
    }

    public void testDbIniBean() throws Ex {


    }
}*/
