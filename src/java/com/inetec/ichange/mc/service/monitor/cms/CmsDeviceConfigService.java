package com.inetec.ichange.mc.service.monitor.cms;

import com.inetec.ichange.mc.service.http.client.CmsHttpClient;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;


public class CmsDeviceConfigService extends Thread {
	private static final Logger log = Logger.getLogger(CmsDeviceConfigService.class);
	private boolean isRun = false;
	private String host;
	private int port=8000;
	public static final int I_SleepTime = 60 * 60 * 1000;
	public String path="/usr/app/mc/data";

	public CmsDeviceConfigService() {

	}

	public boolean isRun() {
		return isRun;
	}

	public void setRun(boolean isRun) {
		this.isRun = isRun;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void run() {
		isRun = true;
		while (isRun) {
			processDeviceConfig();
			try {
				Thread.sleep(I_SleepTime);
			} catch (InterruptedException e) {
				// okay
			}
		}
		isRun = false;
	}

	private boolean processDeviceConfig() {
		boolean result = false;
		log.info("Device Config is start");
		File dir = new File(path);
		File[] files = dir.listFiles(new FileType());
        if(files!=null){
		    log.info("Device Config filessize:"+files.length);
        }

        if(files!=null){
            for (int i = 0; i < files.length; i++) {
                try {
                    CmsHttpClient cmsclient = new CmsHttpClient();
                    cmsclient.init(host + ":" + port);
                    result=cmsclient.deviceConfig(files[i].getAbsolutePath(), files[i]
                            .getName());
                    if(result){
                        FileUtils.forceDelete(files[i]);
                    }
                    cmsclient.close();
                    log.info("upload to cms device config okay:"+files[i].getAbsolutePath());
                } catch (Exception e) {
                    log.warn("upload to cms device config faild:"+files[i].getAbsolutePath(),e);
                }
            }
            log.info("Device Config is end:"+files.length);
        }
		result = true;
		return result;

	}

	public void close() {
		isRun = false;
	}
	
	public static void main(String arg[])throws Exception{
		CmsDeviceConfigService ss=new CmsDeviceConfigService();
		ss.setHost("192.168.20.72");
		ss.path="c:/testdir";
		ss.start();
		while(true){
			Thread.sleep(100);
		}
		
	}

}
