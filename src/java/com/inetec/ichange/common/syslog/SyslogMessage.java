package com.inetec.ichange.common.syslog;


import java.text.ParseException;
import java.util.Hashtable;

public class SyslogMessage
        implements Cloneable {

    // SyslogLvl    
    //    
    public static final int LOG_EMERG = 0; /* system is unusable */
    public static final int LOG_ALERT = 1; /* action must be taken immediately */
    public static final int LOG_CRIT = 2; /* critical conditions */
    public static final int LOG_ERR = 3; /* error conditions */
    public static final int LOG_WARNING = 4; /* warning conditions */
    public static final int LOG_NOTICE = 5; /* normal but significant condition */
    public static final int LOG_INFO = 6; /* informational */
    public static final int LOG_DEBUG = 7; /* debug-level messages */
    public static final int LOG_ALL = 8; /* '*' in config, all levels */

    //    
    // SyslogFac    
    //    
    public static final int LOG_KERN = 0; /* kernel messages */
    public static final int LOG_USER = 1; /* random user-level messages */
    public static final int LOG_MAIL = 2; /* mail system */
    public static final int LOG_DAEMON = 3; /* system daemons */
    public static final int LOG_AUTH = 4; /* security/authorization messages */
    public static final int LOG_SYSLOG = 5; /* messages generated internally by syslogd */
    public static final int LOG_LPR = 6; /* line printer subsystem */
    public static final int LOG_NEWS = 7; /* network news subsystem */
    public static final int LOG_UUCP = 8; /* UUCP subsystem */
    public static final int LOG_CRON = 9; /* clock daemon */

    /* other codes through 15 reserved for system use */

    public static final int LOG_LOCAL0 = 16; /* reserved for local use */
    public static final int LOG_LOCAL1 = 17; /* reserved for local use */
    public static final int LOG_LOCAL2 = 18; /* reserved for local use */
    public static final int LOG_LOCAL3 = 19; /* reserved for local use */
    public static final int LOG_LOCAL4 = 20; /* reserved for local use */
    public static final int LOG_LOCAL5 = 21; /* reserved for local use */
    public static final int LOG_LOCAL6 = 22; /* reserved for local use */
    public static final int LOG_LOCAL7 = 23; /* reserved for local use */

    public static final int LOG_NFACILITIES = 24;   /* current number of facilities */

    public static final int LOG_PRIMASK = 0x07;     /* mask to extract priority part (internal) */
    public static final int LOG_FACMASK = 0x03F8;   /* mask to extract facility part */
    public static final int INTERNAL_NOPRI = 0x10;  /* the "no priority" priority */

    public static final int LOG_PID = 0x01; /* log the pid with each message */
    public static final int LOG_CONS = 0x02; /* log on the console if errors in sending */
    public static final int LOG_ODELAY = 0x04; /* delay open until first syslog() (default) */
    public static final int LOG_NDELAY = 0x08; /* don't delay open */
    public static final int LOG_NOWAIT = 0x10; /* don't wait for console forks: DEPRECATED */
    public static final int LOG_PERROR = 0x20; /* log to stderr as well */


    static private Hashtable facHash;
    static private Hashtable priHash;


    static {
        facHash = new Hashtable(20);

        facHash.put("KERN", new Integer(SyslogMessage.LOG_KERN));
        facHash.put("KERNEL", new Integer(SyslogMessage.LOG_KERN));
        facHash.put("USER", new Integer(SyslogMessage.LOG_USER));
        facHash.put("MAIL", new Integer(SyslogMessage.LOG_MAIL));
        facHash.put("DAEMON", new Integer(SyslogMessage.LOG_DAEMON));
        facHash.put("AUTH", new Integer(SyslogMessage.LOG_AUTH));
        facHash.put("SYSLOG", new Integer(SyslogMessage.LOG_SYSLOG));
        facHash.put("LPR", new Integer(SyslogMessage.LOG_LPR));
        facHash.put("NEWS", new Integer(SyslogMessage.LOG_NEWS));
        facHash.put("UUCP", new Integer(SyslogMessage.LOG_UUCP));
        facHash.put("CRON", new Integer(SyslogMessage.LOG_CRON));
        facHash.put("LOCAL0", new Integer(SyslogMessage.LOG_LOCAL0));
        facHash.put("LOCAL1", new Integer(SyslogMessage.LOG_LOCAL1));
        facHash.put("LOCAL2", new Integer(SyslogMessage.LOG_LOCAL2));
        facHash.put("LOCAL3", new Integer(SyslogMessage.LOG_LOCAL3));
        facHash.put("LOCAL4", new Integer(SyslogMessage.LOG_LOCAL4));
        facHash.put("LOCAL5", new Integer(SyslogMessage.LOG_LOCAL5));
        facHash.put("LOCAL6", new Integer(SyslogMessage.LOG_LOCAL6));
        facHash.put("LOCAL7", new Integer(SyslogMessage.LOG_LOCAL7));

        priHash = new Hashtable(20);

        priHash.put("EMERG", new Integer(SyslogMessage.LOG_EMERG));
        priHash.put("EMERGENCY", new Integer(SyslogMessage.LOG_EMERG));
        priHash.put("LOG_EMERG", new Integer(SyslogMessage.LOG_EMERG));
        priHash.put("ALERT", new Integer(SyslogMessage.LOG_ALERT));
        priHash.put("LOG_ALERT", new Integer(SyslogMessage.LOG_ALERT));
        priHash.put("CRIT", new Integer(SyslogMessage.LOG_CRIT));
        priHash.put("CRITICAL", new Integer(SyslogMessage.LOG_CRIT));
        priHash.put("LOG_CRIT", new Integer(SyslogMessage.LOG_CRIT));
        priHash.put("ERR", new Integer(SyslogMessage.LOG_ERR));
        priHash.put("ERROR", new Integer(SyslogMessage.LOG_ERR));
        priHash.put("LOG_ERR", new Integer(SyslogMessage.LOG_ERR));
        priHash.put("WARNING", new Integer(SyslogMessage.LOG_WARNING));
        priHash.put("LOG_WARNING", new Integer(SyslogMessage.LOG_WARNING));
        priHash.put("NOTICE", new Integer(SyslogMessage.LOG_NOTICE));
        priHash.put("LOG_NOTICE", new Integer(SyslogMessage.LOG_NOTICE));
        priHash.put("INFO", new Integer(SyslogMessage.LOG_INFO));
        priHash.put("LOG_INFO", new Integer(SyslogMessage.LOG_INFO));
        priHash.put("DEBUG", new Integer(SyslogMessage.LOG_DEBUG));
        priHash.put("LOG_DEBUG", new Integer(SyslogMessage.LOG_DEBUG));
    }


    public boolean isRepeat;
    public int facility;
    public int serverty;
    public String timestamp;
    public String hostName;
    public String processName;
    public int processId;
    public String message;
    public String[] matchVars;
    private int length;

    public void setFacility(int facility) {
        this.facility = facility;
    }

    static public int
    computeCode(int facility, int priority) {
        return ((facility << 3) | priority);
    }

    static public String
    getPriorityName(int level) {
        switch (level) {
            case SyslogMessage.LOG_EMERG:
                return "panic";
            case SyslogMessage.LOG_ALERT:
                return "alert";
            case SyslogMessage.LOG_CRIT:
                return "critical";
            case SyslogMessage.LOG_ERR:
                return "error";
            case SyslogMessage.LOG_WARNING:
                return "warning";
            case SyslogMessage.LOG_NOTICE:
                return "notice";
            case SyslogMessage.LOG_INFO:
                return "info";
            case SyslogMessage.LOG_DEBUG:
                return "debug";
        }

        return "unknown level='" + level + "'";
    }

    static public String
    getFacilityName(int facility) {
        switch (facility) {
            case SyslogMessage.LOG_KERN:
                return "kernel";
            case SyslogMessage.LOG_USER:
                return "user";
            case SyslogMessage.LOG_MAIL:
                return "mail";
            case SyslogMessage.LOG_DAEMON:
                return "daemon";
            case SyslogMessage.LOG_AUTH:
                return "auth";
            case SyslogMessage.LOG_SYSLOG:
                return "syslog";
            case SyslogMessage.LOG_LPR:
                return "lpr";
            case SyslogMessage.LOG_NEWS:
                return "news";
            case SyslogMessage.LOG_UUCP:
                return "uucp";
            case SyslogMessage.LOG_CRON:
                return "cron";

            case SyslogMessage.LOG_LOCAL0:
                return "local0";
            case SyslogMessage.LOG_LOCAL1:
                return "local1";
            case SyslogMessage.LOG_LOCAL2:
                return "local2";
            case SyslogMessage.LOG_LOCAL3:
                return "local3";
            case SyslogMessage.LOG_LOCAL4:
                return "local4";
            case SyslogMessage.LOG_LOCAL5:
                return "local5";
            case SyslogMessage.LOG_LOCAL6:
                return "local6";
            case SyslogMessage.LOG_LOCAL7:
                return "local7";
        }

        return "unknown facility='" + facility + "'";
    }


    static public int
    getPriority(String priority)
            throws ParseException {
        String priKey = priority.toUpperCase();
        Integer result = (Integer)
                SyslogMessage.priHash.get(priKey);

        if (result == null) {
            throw new ParseException
                    ("unknown priority '" + priority + "'", 0);
        }

        return result.intValue();
    }

    static public int
    serverty(int priority, int facility)
            throws ParseException {
        return priority - facility * 8;
    }

    public int getFacility() {
        return facility;
    }

    static public int
    extractFacility(int code) {
        return ((code & SyslogMessage.LOG_FACMASK) >> 3);
    }


    static public int
    extractPriority(int code) {
        return (code & SyslogMessage.LOG_PRIMASK);
    }


    static public int
    getFacility(String facility)
            throws ParseException {
        String facKey = facility.toUpperCase();
        Integer result = (Integer)
                SyslogMessage.facHash.get(facKey);

        if (result == null) {
            throw new ParseException
                    ("unknown facility '" + facility + "'", 0);
        }

        return result.intValue();
    }

    public int getServerty() {
        return serverty;
    }

    public void setServerty(int serverty) {
        this.serverty = serverty;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public int getProcessId() {
        return processId;
    }

    public void setProcessId(int processId) {
        this.processId = processId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String[] getMatchVars() {
        return matchVars;
    }

    public void setMatchVars(String[] matchVars) {
        this.matchVars = matchVars;
    }
}







