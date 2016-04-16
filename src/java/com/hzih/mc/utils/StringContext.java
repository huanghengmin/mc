package com.hzih.mc.utils;

/**
 * Created by IntelliJ IDEA.
 * User: Ç®ÏþÅÎ
 * Date: 12-6-7
 * Time: ÉÏÎç10:56
 * To change this template use File | Settings | File Templates.
 */
public class StringContext {
    public final static String systemPath = System.getProperty("mc.home");
    public final static String INTERFACE = "/etc/network/interfaces";
    public final static String IFSTATE = "/etc/network/run/ifstate";
    public final static String localLogPath = systemPath + "/logs";
    public final static String tomcatPathServer = systemPath +"/tomcat/conf/server.xml";
    public final static String webPath = StringContext.systemPath+"/tomcat/webapps";
}
