package com.inetec.ichange.mc.service.monitor.syslog.format;




/*
*设备日志格式处理接口
*/
public interface ILogFormat {
    public static final String S_Deviceid = "deviceid";
    public static final String S_ip = "ip";

    /*
    *处理字符为日志格式，解析对应值为对象属??
    */
    public void process(String log,String level);

    /**
     * 验证格式正确
     *
     * @param log
     * @return
     */
    public boolean validate(String log);


    /**
     * ??????
     * @return
     */
    public long getIn_Flux();
    /**
     * ??????
     * @return
     */
    public long getOut_Flux();




}
