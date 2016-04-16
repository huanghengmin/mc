package com.hzih.mc.utils;

import java.util.ArrayList;
import java.util.List;

public class LicenseUtils {
    /**
     *  权限控制
     * @param isExistLicense    是否存在 usb-key
     * @return
     */
	public List<String> getNeedsLicenses(boolean isExistLicense) {
		String qxManager = "TOP_QXGL:SECOND_YHGL:SECOND_JSGL:SECOND_AQCL:";                   //权限管理
		String wlManager = "TOP_WLGL:SECOND_JKGL:SECOND_LTCS:SECOND_DKCS:SECOND_LYGL:";     //网络管理
		String xtManager = "TOP_XTGL:SECOND_PTGL:SECOND_PZGL:";                                 //系统管理
//		String license = License.getModules();//许可证允许的权限
		String license = "";
		String permission = null;
        permission = qxManager + wlManager + xtManager ;
		if(isExistLicense){
			permission += license;
		}
		String[] permissions = permission.split(":");
		List<String> lps = new ArrayList<String>();
		for (int i = 0; i < permissions.length; i++) {
			lps.add(permissions[i]);
		}
		return lps;
	}
}
