package com.inetec.ichange.mc.service.monitor.syslog.format;


import org.apache.commons.lang.StringUtils;

/**
 * @author bluesky
 */
public class KoalTbsgLog implements ILogFormat {
    private static final String S_Logflag = "logflag=\"TBSGS\"";
    private static final String S_ServiceID = "serviceid=\"1\"";
    private static final String S_Separator_Keys = " ";
    private static final String S_Separator_KeyValue = "=";
    /**
     * id
     */
    public String equid;
    /*
     *ip��ַ
     */
    public String ip;
    public String log;
    private String logflag;
    private String userip;
    private String accessurl;
    private String orgcode;
    private String username;
    private String identity;
    private String accessreturn;
    private String reason;
    private String tbsgip;
    private String proxycn;
    private String terminalid;
    private String time;
    private String bytes;
    private String upbytes;
    private String serviceid;
    private String level;

    public String getLogflag() {
        return logflag;
    }

    public void setLogflag(String logflag) {
        this.logflag = logflag;
    }

    public String getUserip() {
        return userip;
    }

    public void setUserip(String userip) {
        this.userip = userip;
    }

    public String getAccessurl() {
        return accessurl;
    }

    public void setAccessurl(String accessurl) {
        this.accessurl = accessurl;
    }

    public String getOrgcode() {
        return orgcode;
    }

    public void setOrgcode(String orgcode) {
        this.orgcode = orgcode;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getAccessreturn() {
        return accessreturn;
    }

    public void setAccessreturn(String accessreturn) {
        this.accessreturn = accessreturn;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getTbsgip() {
        return tbsgip;
    }

    public void setTbsgip(String tbsgip) {
        this.tbsgip = tbsgip;
    }

    public String getProxycn() {
        return proxycn;
    }

    public void setProxycn(String proxycn) {
        this.proxycn = proxycn;
    }

    public String getTerminalid() {
        return terminalid;
    }

    public void setTerminalid(String terminalid) {
        this.terminalid = terminalid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getBytes() {
        return bytes;
    }

    public void setBytes(String bytes) {
        this.bytes = bytes;
    }

    public String getUpbytes() {
        return upbytes;
    }

    public void setUpbytes(String upbytes) {
        this.upbytes = upbytes;
    }

    public String getServiceid() {
        return serviceid;
    }

    public void setServiceid(String serviceid) {
        this.serviceid = serviceid;
    }

    public void process(String log) {
        // TODO Auto-generated method stub
        this.log = log;
        if (validate(log)) {
            String[] keyvalues = log.split(S_Separator_Keys);
            for (int i = 0; i < keyvalues.length; i++) {
                if (keyvalues[i].startsWith("deviceid=")) {
                    equid = keyvalues[i].substring("deviceid=".length());
                }
                if (keyvalues[i].startsWith("ip=")) {
                    ip = keyvalues[i].substring("ip=".length());
                }
                if (keyvalues[i].startsWith("logflag=")) {
                    logflag = keyvalues[i].substring("logflag=".length());
                }
                if (keyvalues[i].startsWith("userip=")) {
                    userip = keyvalues[i].substring("userip=".length());
                }
                if (keyvalues[i].startsWith("accessurl=")) {
                    accessurl = keyvalues[i].substring("accessurl=".length());
                }
                if (keyvalues[i].startsWith("orgcode=")) {
                    orgcode = keyvalues[i].substring("orgcode".length());
                }
                if (keyvalues[i].startsWith("username=")) {
                    username = keyvalues[i].substring("username=".length());
                }
                if (keyvalues[i].startsWith("identity=")) {
                    identity = keyvalues[i].substring("identity=".length());
                }
                if (keyvalues[i].startsWith("accessreturn=")) {
                    accessreturn = keyvalues[i].substring("accessreturn="
                            .length());
                }
                if (keyvalues[i].startsWith("reason=")) {
                    reason = keyvalues[i].substring("reason=".length());
                }
                if (keyvalues[i].startsWith("tbsgip=")) {
                    tbsgip = keyvalues[i].substring("tbsgip=".length());
                }
                if (keyvalues[i].startsWith("proxycn=")) {
                    proxycn = keyvalues[i].substring("proxycn=".length());
                }
                if (keyvalues[i].startsWith("terminalid=")) {
                    terminalid = keyvalues[i].substring("terminalid=".length());
                }
                if (keyvalues[i].startsWith("time=")) {

                    time = (keyvalues[i].substring("time=".length()) + " " + keyvalues[i + 1]);
                    i++;
                }
                if (keyvalues[i].startsWith("bytes=")) {
                    bytes = keyvalues[i].substring("bytes="
                            .length());

                }
                if (keyvalues[i].startsWith("upbytes=")) {
                    upbytes = keyvalues[i].substring("upbytes="
                            .length());
                }
                if (keyvalues[i].startsWith("serviceid=")) {
                    serviceid = keyvalues[i].substring("serviceid=".length());
                }

            }
        }

    }

    @Override
    public void process(String log, String level) {
        this.level = level;
        process(log);
    }

    @Override
    public boolean validate(String log) {
        boolean result = false;


        if (StringUtils.containsIgnoreCase(log, S_Logflag)
                && StringUtils.containsIgnoreCase(log, S_ServiceID))
            result = true;
        return result;
    }

 

    public long getIn_Flux() {
        if (bytes != null && bytes.equalsIgnoreCase(""))
            return Long.parseLong(bytes);
        return 0;
    }

    public long getOut_Flux() {
        if (bytes != null && bytes.equalsIgnoreCase(""))
            return Long.parseLong(bytes);
        return 0;
    }



    public static void main(String arg[]) throws Exception {
        String logs = "logflag=\"TBSGS\" userip=\"191.168.191.7\" accessurl=\"http://192.168.1.8/\" orgcode=\"-\" username=\"-\" identity=\"-\" accessreturn=\"Y\" reason=\"成功\" tbsgip=\"192.168.190.7\" proxycn=\"-\" terminalid=\"-\" time=\"2012-03-19 03:33:36\" bytes=\"8560\" upbytes=\"0\" serviceid=\"3\"";
       /*// KoalVpnLog log = new KoalVpnLog();
        log.process(logs);

        System.out.println("logflag=" + log.getLogflag() + " time="
                + log.getTime());*/

    }


}
