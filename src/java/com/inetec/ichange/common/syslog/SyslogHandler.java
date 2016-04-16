package com.inetec.ichange.common.syslog;

import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

public class SyslogHandler extends IoHandlerAdapter {

    private final Logger log = Logger.getLogger(SyslogHandler.class);
    private SyslogServer syslog;

    public SyslogHandler(SyslogServer syslog) {
        this.syslog = syslog;
    }

    public void exceptionCaught(IoSession session, Throwable cause)
            throws Exception {
        session.close(true);
        log.error("session occured exception, so close it.");
    }

    public void messageReceived(IoSession session, Object message)
            throws Exception {
        if (message instanceof SyslogMessage) {
                SyslogMessage msg = (SyslogMessage) message;
                syslog.processSyslog(msg);


        }

    }

    public void messageSent(IoSession session, Object message) throws Exception {
        super.messageSent(session, message);
    }

    public void sessionCreated(IoSession session) throws Exception {
        log.info("remote client [" + session.getRemoteAddress().toString() + "] connected.");
    }
}