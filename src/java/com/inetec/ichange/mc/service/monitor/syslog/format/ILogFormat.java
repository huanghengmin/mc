package com.inetec.ichange.mc.service.monitor.syslog.format;




/*
*�豸��־��ʽ����ӿ�
*/
public interface ILogFormat {
    public static final String S_Deviceid = "deviceid";
    public static final String S_ip = "ip";

    /*
    *�����ַ�Ϊ��־��ʽ��������ӦֵΪ������??
    */
    public void process(String log,String level);

    /**
     * ��֤��ʽ��ȷ
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
