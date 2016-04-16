package com.inetec.ichange.mc.service.monitor.cms;

import java.io.File;
import java.io.FilenameFilter;

public class FileType implements FilenameFilter{

	@Override
	public boolean accept(File dir, String name) {
		
		if(name.endsWith(".tar.gz")||name.endsWith(".zip")){
			return true;
		}
		return false;
	}

}
