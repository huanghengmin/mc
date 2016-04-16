package com.hzih.mc.web.thread;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 13-1-17
 * Time: 下午4:50
 * To change this template use File | Settings | File Templates.
 */
public class SnmpCorrect extends Thread {

    public static Map snmpCorrectMap = new HashMap();
    private static boolean isRun = false;

    public void init(){

    }



    public boolean isRun() {
        return isRun;
    }

    @Override
    public void run() {
        isRun = true;
       while (isRun){
           try {
               snmpCorrectMap = new HashMap();
               Thread.sleep(5*60*1000);
           } catch (InterruptedException e) {
           }
       }
    }
}
