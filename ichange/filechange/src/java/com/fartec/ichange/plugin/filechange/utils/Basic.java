package com.fartec.ichange.plugin.filechange.utils;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.sql.*;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 12-8-14
 * Time: 上午9:12
 * To change this template use File | Settings | File Templates.
 */
public class Basic {
    private static final Logger logger = Logger.getLogger(Basic.class);
    private static String ip = "127.0.0.1";
    private static String dbtype = "mysql";
    private static String username = "root";
    private static String password = "123456";
    private static String dbname = "jksys";
    private static String table = "contentfilter";
    private static String column = "filter";
    private static Map<String, String> map = new HashMap<String, String>();
    private static Map<String, String> hashMap = new HashMap<String, String>();
    private static String keyword = null;

    static {
        map.put("mysql", "com.mysql.jdbc.Driver");
        SAXReader saxReader = new SAXReader();
//        StringBuilder stringBuilder = new StringBuilder();
        Document document = null;
        try {
            document = saxReader.read(Basic.class.getResourceAsStream("/config_db.xml"));
        } catch (DocumentException e) {
            logger.info(e.getMessage());  //To change body of catch statement use File | Settings | File Templates.
        }
        Element root = document.getRootElement();
        Iterator list = root.elementIterator();
        while (list.hasNext()) {
            Element element = (Element) list.next();
            hashMap.put(element.getName(), element.getStringValue());
        }
        ip = hashMap.get("ip");
        dbtype = hashMap.get("dbtype");
        username = hashMap.get("username");
        password = hashMap.get("password");
        dbname = hashMap.get("dbname");
        table = hashMap.get("table");
        column = hashMap.get("column");
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                getKeyword();
            }
        },1000,12*1000*60*60);
    }

    public static Connection getCon() {
        try {
            Class.forName(map.get(dbtype));
        } catch (ClassNotFoundException e) {
            logger.info(e.getMessage());  //To change body of catch statement use File | Settings | File Templates.
        }
        String url = "jdbc:" + dbtype + "://" + ip + ":3306/" + dbname;
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            logger.info(e.getMessage());  //To change body of catch statement use File | Settings | File Templates.
        }
        return connection;
    }

    private static void getKeyword() {
        Connection connection = getCon();
        String sql = "select " + column + " from " + table;
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(sql);
        } catch (SQLException e) {
            logger.info(e.getMessage());  //To change body of catch statement use File | Settings | File Templates.
        }
        ResultSet resultSet = null;
        try {
            resultSet = preparedStatement.executeQuery();
        } catch (SQLException e) {
            logger.info(e.getMessage());  //To change body of catch statement use File | Settings | File Templates.
        }
        StringBuilder stringBuilder = new StringBuilder();
        try {
            while (resultSet.next()) {
                stringBuilder.append(resultSet.getString(column)).append(",");
            }
        } catch (SQLException e) {
            logger.info(e.getMessage());  //To change body of catch statement use File | Settings | File Templates.
        }
        finally {
            try{
                resultSet.close();
                connection.close();
                preparedStatement.close();
            }
            catch (Exception e){
                logger.error("关闭数据库连接出错");
            }
        }
        keyword = stringBuilder.toString();
//        return data.substring(0, data.lastIndexOf(","));
    }
    public static String getKeywords(){
        if(keyword == null) {
            getKeyword();
        }
        return keyword;
    }
//    public static void main(String[] arg0){
//        System.out.println(Basic.getKeywords());
//    }
}
