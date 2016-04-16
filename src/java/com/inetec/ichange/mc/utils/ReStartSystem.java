package com.inetec.ichange.mc.utils;

import com.inetec.common.util.OSInfo;
import com.inetec.common.util.Proc;

public class ReStartSystem {
	public static boolean restart() {
		OSInfo osinfo = OSInfo.getOSInfo();
		if (osinfo.isWin()) {
			Proc proc = new Proc();
			proc.exec("nircmd service restart mc");
		}
		if (osinfo.isLinux()) {
			Proc proc = new Proc();
			proc.exec("service mc restart");
		}
		return true;
	}
}
