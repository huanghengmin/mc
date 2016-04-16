/*
package com.inetec.test;

import com.inetec.common.config.ConfigParser;
import com.inetec.common.config.metadata.MetaDB;
import com.inetec.common.config.nodes.IChange;
import com.inetec.common.exception.Ex;

import com.inetec.ichange.console.config.utils.TriggerBean;

import com.inetec.ichange.console.client.util.*;
import com.inetec.ichange.console.client.IServiceCommand;
import com.inetec.ichange.console.client.ServiceCommandFactory;
import com.inetec.unitest.UniTestCase;

import java.util.List;
import java.io.IOException;

*/
/**
 * Created by IntelliJ IDEA.
 * User: wxh
 * Date: 2009-8-31
 * Time: 22:26:28
 * To change this template use File | Settings | File Templates.
 *//*

public class TestServiceCommand extends UniTestCase {
    IServiceCommand serviceCommand = null;
     IChange ichange =null;
    public TestServiceCommand(String name) {
        super(name);
        System.setProperty("ichange.home","D:\\inetec\\ichange\\dist\\dist\\");

    }

    protected void setUp() throws Exception {
        ChannelInfo channelInfo = new ChannelInfo();
        ConfigParser parser = new ConfigParser("D:\\config.xml");
        ichange = parser.getRoot();
        com.inetec.common.config.nodes.Channel channel = ichange.getChannel();
        channelInfo.set(channel, ichange.getPrivateKey(), ichange.getPrivatePassword());
        serviceCommand = ServiceCommandFactory.createServiceCommand(channelInfo);
        //super.setUp();
    }

    protected void tearDown() throws Exception {
        //super.tearDown();
    }

public void testDBMetaData()
        throws Ex
    {

        DataAttributes da = new DataAttributes();
        JdbcUtil jdbcUtil = new JdbcUtil();
        jdbcUtil.setJdbc(ichange.getJdbc("in_sql"));
        da.setObject(jdbcUtil);
        da = serviceCommand.execCommand(Command.C_DBMetaData, da);
        if(da.getStatus().isSuccess())
        {
            MetaDB metadb = (MetaDB)da.getObject();
            System.out.println("sucess.");
        } else
        {
            System.out.println("faild.");
        }
    }

    public void testDBCreateFlag()
        throws Ex
    {

        DbInitBean dbInitBean = new DbInitBean();
        String tableNames[] = {
            "testflag"
        };
        dbInitBean.setTableNames(tableNames);
        DataAttributes da = new DataAttributes();
        JdbcUtil jdbcUtil = new JdbcUtil();
        jdbcUtil.setJdbc(ichange.getJdbc("out_oracle"));
        jdbcUtil.setDbInit(dbInitBean);
        da.setObject(jdbcUtil);
        da = serviceCommand.execCommand(Command.C_DBCreateFlag, da);
        if(da.getStatus().isSuccess())
            System.out.println("sucess.");
        else
            System.out.println("faild.");
    }

    public void testDBDeleteFlag()
        throws Ex
    {

        DataAttributes da = new DataAttributes();
        DbInitBean dbInitBean = new DbInitBean();
        String tableNames[] = {
            "testflag".toUpperCase()
        };
        dbInitBean.setTableNames(tableNames);
        JdbcUtil jdbcUtil = new JdbcUtil();
        jdbcUtil.setJdbc(ichange.getJdbc("out_oracle"));
        jdbcUtil.setDbInit(dbInitBean);
        da.setObject(jdbcUtil);
        da = serviceCommand.execCommand(Command.C_DBDeleteFlag, da);
        if(da.getStatus().isSuccess())
            System.out.println("sucess.");
        else
            System.out.println("faild.");
    }

    public void testDBDeleteTrigger()
        throws Ex
    {

        DbInitBean dbInitBean = new DbInitBean();
        String tableNames[] = {
            "testflag".toUpperCase()
        };
        dbInitBean.setTableNames(tableNames);
        TriggerBean bean = new TriggerBean();
        TriggerBean beans[] = {
            bean
        };
        bean.setTableName("testflag".toUpperCase());
        bean.setMonitorDelete(true);
        bean.setMonitorInsert(true);
        bean.setMonitorUpdate(true);
        dbInitBean.setTriggerBeans(beans);
        dbInitBean.setTempTable("temptable".toUpperCase());
        DataAttributes da = new DataAttributes();
        JdbcUtil jdbcUtil = new JdbcUtil();
        jdbcUtil.setJdbc(ichange.getJdbc("out_oracle"));
        jdbcUtil.setDbInit(dbInitBean);
        da.setObject(jdbcUtil);
        da = serviceCommand.execCommand(Command.C_DBDeleteTrigger, da);
        if(da.getStatus().isSuccess())
            System.out.println("sucess.");
        else
            System.out.println("faild.");
    }

    public void testDBCreateTrigger()
        throws Ex
    {

        DbInitBean dbInitBean = new DbInitBean();
        String tableNames[] = {
            "testflag".toUpperCase()
        };
        dbInitBean.setTempTable("temptable".toUpperCase());
        TriggerBean bean = new TriggerBean();
        TriggerBean beans[] = {
            bean
        };
        bean.setTableName("testflag".toUpperCase());
        bean.setMonitorDelete(true);
        bean.setMonitorInsert(true);
        bean.setMonitorUpdate(true);
        //bean.setPkFields();
        dbInitBean.setTriggerBeans(beans);
        dbInitBean.setTableNames(tableNames);
        DataAttributes da = new DataAttributes();
        JdbcUtil jdbcUtil = new JdbcUtil();
        jdbcUtil.setJdbc(ichange.getJdbc("out_oracle"));
        jdbcUtil.setDbInit(dbInitBean);
        da.setObject(jdbcUtil);
        da = serviceCommand.execCommand(Command.C_DBCreateTrigger, da);
        if(da.getStatus().isSuccess())
            System.out.println("sucess.");
        else
            System.out.println("faild.");
    }

    public void testDBCreateSeq()
        throws Ex
    {

        DbInitBean dbInitBean = new DbInitBean();
        String tableNames[] = {
            "testflag"
        };
        dbInitBean.setTableNames(tableNames);
        DataAttributes da = new DataAttributes();
        JdbcUtil jdbcUtil = new JdbcUtil();
        jdbcUtil.setJdbc(ichange.getJdbc("out_oracle"));
        jdbcUtil.setDbInit(dbInitBean);
        da.setObject(jdbcUtil);
        da = serviceCommand.execCommand(Command.C_DbCreateSequence, da);
        if(da.getStatus().isSuccess())
            System.out.println("sucess.");
        else
            System.out.println("faild.");
    }

    public void testDBDeleteSeq()
        throws Ex
    {

        DbInitBean dbInitBean = new DbInitBean();
        String tableNames[] = {
            "testflag"
        };
        dbInitBean.setTableNames(tableNames);
        DataAttributes da = new DataAttributes();
        JdbcUtil jdbcUtil = new JdbcUtil();
        jdbcUtil.setJdbc(ichange.getJdbc("out_oracle"));
        jdbcUtil.setDbInit(dbInitBean);
        da.setObject(jdbcUtil);
        da = serviceCommand.execCommand(Command.C_DbDeleteSequence, da);
        if(da.getStatus().isSuccess())
            System.out.println("sucess.");
        else
            System.out.println("faild.");
    }

    public void testDBDeleteTempTable()
        throws Ex
    {

        DbInitBean dbInitBean = new DbInitBean();
        dbInitBean.setTempTable("temptable");
        DataAttributes da = new DataAttributes();
        JdbcUtil jdbcUtil = new JdbcUtil();
        jdbcUtil.setJdbc(ichange.getJdbc("out_oracle"));
        jdbcUtil.setDbInit(dbInitBean);
        da.setObject(jdbcUtil);
        da = serviceCommand.execCommand(Command.C_DbDeleteTempTable, da);
        if(da.getStatus().isSuccess())
            System.out.println("sucess.");
        else
            System.out.println("faild.");
    }

        public void testDBCreateTempTable()
            throws Ex
        {

            DbInitBean dbInitBean = new DbInitBean();
            dbInitBean.setTempTable("temptable");
            DataAttributes da = new DataAttributes();
            JdbcUtil jdbcUtil = new JdbcUtil();
            jdbcUtil.setJdbc(ichange.getJdbc("out_oracle"));
            jdbcUtil.setDbInit(dbInitBean);
            da.setObject(jdbcUtil);
            da = serviceCommand.execCommand(Command.C_DbCreateTempTable, da);
            if(da.getStatus().isSuccess())
                System.out.println("sucess.");
            else
                System.out.println("faild.");
        }

    public void testDBTestConnect()
            throws Ex
        {


            DataAttributes da = new DataAttributes();
            JdbcUtil jdbcUtil = new JdbcUtil();
            jdbcUtil.setJdbc(ichange.getJdbc("in_sybase"));

            da.setObject(jdbcUtil);
            da = serviceCommand.execCommand(Command.C_DBTestConnect, da);
            if(da.getStatus().isSuccess())
                System.out.println("sucess.");
            else
                System.out.println("faild.");
        }

    */
/*public void testProcdure() throws Ex {
        ConfigParser parser = new ConfigParser("d:/ichange/repository/config.xml");
        IChange ichange = parser.getRoot();
        Jdbc jdbc = ichange.getJdbc("oracle");

        Table[] tables = ichange.getType("app1").getPlugin().getDataBase().getAllTables();
        System.out.println(tables.length);
        TriggerBean[] beans = new TriggerBean[tables.length];
        for (int i = 0; i < tables.length; i++) {
            beans[i] = new TriggerBean();
            beans[i].setTableName(tables[i].getTableName());
            beans[i].setMonitorDelete(tables[i].isMonitorDelete());
            beans[i].setMonitorInsert(tables[i].isMonitorInsert());
            beans[i].setMonitorUpdate(tables[i].isMonitorUpdate());
        }

        IDataBase database = DBFactory.getDataBase(jdbc, Constant.DB_INTERNAL);
        database.openConnection();
        database.createTrigger(beans,"");
        database.closeConnection();
    }*//*


//    public void testFlag() throws Ex {
//        ConfigParser parser = new ConfigParser("d:/ichange/repository/config.xml");
//        IChange ichange = parser.getRoot();
//        IDataBase database = DBFactory.getDataBase(ichange.getJdbc("oracle"), Constant.DB_INTERNAL);
//        database.openConnection();
//        database.createFlag("ACTIVITIES");
//        database.closeConnection();
//    }         s
    public void testTypeStatus()throws Ex, IOException {
        DataAttributes da = new DataAttributes();
       // da = serviceCommand.execCommand(Command.C_SystemMonitor, da);
        System.out.println(new String(DataAttributes.readInputStream(da.getResultData())));

    }
}
*/
