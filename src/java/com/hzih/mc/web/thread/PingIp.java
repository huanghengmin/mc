package com.hzih.mc.web.thread;

import com.hzih.mc.web.action.InterfaceManagerAction;
import com.inetec.common.exception.Ex;
import com.inetec.common.net.Ping;
import org.apache.log4j.Logger;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 12-11-12
 * Time: ����10:06
 * To change this template use File | Settings | File Templates.
 */
public class PingIp implements Runnable{

    private static final Logger logger = Logger.getLogger(PingIp.class);


    private String startIp;
    private String relativeip;
    private int num;
    private String str;
    private String msg="";
    private Map ipmap;
    private Map threadmap;


    public void init(String startIp,int num,String relativeip){
        this.startIp = startIp;
        this.num = num;
        this.relativeip = relativeip;
    }


    @Override
    public void run() {
       threadmap = InterfaceManagerAction.getThreadmap();
       ipmap = InterfaceManagerAction.getIpmap();
       logger.info("IP---------------------------"+startIp);
       String ips[] = startIp.split("\\.");
       int ip = Integer.parseInt(ips[3]);

       for(int i = ip;i>ip-num;i--){
           try {
               logger.info("ping"+relativeip+i);
               str = Ping.exec(relativeip+i, 1);
               if(str.indexOf("ttl")>-1|str.indexOf("TTL")>-1){
                   ipmap.put(relativeip+i,"true");
               }
           } catch (Ex ex) {
               logger.info(ex);
           }
       }

       threadmap.put(Thread.currentThread().getName()," ");
    }
}
