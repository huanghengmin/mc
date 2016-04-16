package com.inetec.ichange.common.syslog.code;

import com.inetec.common.config.nodes.IpMac;
import com.inetec.ichange.common.syslog.SyslogMessage;
import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.AttributeKey;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import java.nio.charset.Charset;

public class SyslogProtocolDecoder implements ProtocolDecoder {
    final static Logger logger = Logger.getLogger(SyslogProtocolDecoder.class);
    private static final AttributeKey BUF_BYTE = new AttributeKey(
            SyslogProtocolDecoder.class, "syslog");
    private String charset = "GBK";

    public void decode(IoSession ioSession, IoBuffer ioBuffer,
                       ProtocolDecoderOutput protocolDecoderOutput) throws
            Exception {
        try {
            IoBuffer bufTmp = ioBuffer;

            while (bufTmp.remaining() >= 4) { // 循环处理数据包
                //System.out.println("循环处理数据包");

                String data = bufTmp.getString(Charset.forName(charset).newDecoder());
                int n = data.indexOf(">");
                SyslogMessage syslogMessage = new SyslogMessage();
                if (n > 0) {
                    int facility = SyslogMessage.extractFacility(Integer.parseInt(data.substring(1, n)));
                    int priority = SyslogMessage.extractPriority(Integer.parseInt(data.substring(1, n)));
                    int serverty = SyslogMessage.serverty(priority, facility);
                    syslogMessage.setServerty(priority);
                    syslogMessage.setFacility(facility);

                    int msgn = data.indexOf("]:", n) + 2;
                    if (!data.matches("]:"))
                        msgn = n + 1;

                    syslogMessage.setHostName(IpMac.getMinaRemoteIp(ioSession.getRemoteAddress().toString()));
                    syslogMessage.setMessage(data.substring(msgn));


                } else {
                    logger.warn("Syslog format is error:" + data);
                }
                /* ProtobufResponse pak = new ProtobufResponse();
               pak.setResByteLen(dataLen);
               pak.readFrom(b, 4);*/
                //System.out.println("往下传递");
                protocolDecoderOutput.write(syslogMessage);
            }

            bufTmp.free();
        } catch (Exception ex) {
            logger.warn("Tcp Server decode data Exception:", ex);
        }
    }

    public void dispose(IoSession session) throws Exception {
        // System.out.println("dispose");
    }

    public void finishDecode(IoSession session, ProtocolDecoderOutput out) throws
            Exception {
        //System.out.println("finishDecode");
    }

}