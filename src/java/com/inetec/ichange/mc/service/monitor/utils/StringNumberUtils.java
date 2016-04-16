package com.inetec.ichange.mc.service.monitor.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringNumberUtils {
	public static boolean isNumeric(String str)
	{
	Pattern pattern = Pattern.compile("[0-9]*");
	Matcher isNum = pattern.matcher(str);
	if( !isNum.matches() )
	{
	return false;
	}
	return true;
	} 
	public static boolean isEndWith(String srcstr,String endflag){
		return srcstr.endsWith(endflag);
	}
	 


	
}
