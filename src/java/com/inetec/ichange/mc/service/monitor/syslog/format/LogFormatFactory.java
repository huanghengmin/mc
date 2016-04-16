package com.inetec.ichange.mc.service.monitor.syslog.format;

public class LogFormatFactory {
    public static String S_LogType_KoalVpn = "KoalVpn";
    public static String S_LogType_KoalTbsg = "KoalTbsg";
    public static String S_LogType_NetChinaFirewall = "netchinafirewall";
    public static String S_LogType_NetChinaGap = "netchinagap";

    public static boolean checkFormat(String log) {
        if (new KoalTbsgLog().validate(log)) {
            return true;
        }
        if (new KoalVpnLog().validate(log)) {
            return true;
        }
        return false;
    }

    public static ILogFormat getLogFormat(String log,String leve) {
        ILogFormat result = null;
        if (new KoalTbsgLog().validate(log)) {
            result = new KoalTbsgLog();
            result.process(log,leve);
        }
        if(new KoalVpnLog().validate(log)){
        	result=new KoalVpnLog();
        	result.process(log, leve);
        }
        
        
        return result;
    }


}
