package com.inetec.ichange.mc;

import java.util.HashMap;
import java.util.Map;

public class PassPort {

    private static PassPort _passPort = new PassPort();
    private Map<String,String> s_passport = new HashMap<String,String>();
    private long serverid;
    private PassPort(){

    }

    public static PassPort getInstatnce(){
        return _passPort;
    }

    public void addPassPort(String userId,String passPort){
        s_passport.put(userId,passPort);
    }

    public boolean hasPassPort(String passPort){
        return s_passport.values().contains(passPort);
    }

    public String getPassPort(String userId){
        return s_passport.get(userId);
    }

    public boolean hasKey(String userId){
        return s_passport.keySet().contains(userId);
    }

    public void removePassPort(String userId){
        s_passport.remove(userId);
    }

    public long getServerid() {
        return serverid;
    }

    public void setServerid(long serverid) {
        this.serverid = serverid;
    }
}
