package com.inetec.ichange.mc.service.monitor.utils;

public class IpAddressUtils {
	public static String getHost(String socketaddress){
		String temp=socketaddress.split("/")[1];
		if(temp.split(":").length==2){
			temp=temp.split(":")[0];
			socketaddress=temp;
		}
		return socketaddress;
		
	}
	
    
}
