package com.inetec.ichange.mc.service.http;

import com.inetec.common.exception.Ex;
import com.inetec.ichange.main.api.DataAttributes;
import com.inetec.ichange.main.api.Status;
import com.inetec.ichange.mc.service.IPlatManager;
import com.inetec.ichange.mc.service.http.client.IpSecHttpclient;
import com.inetec.ichange.mc.service.http.client.JbpgHttpclient;
import com.inetec.ichange.mc.service.http.client.TSRSHttpclient;
import com.inetec.ichange.mc.service.monitor.utils.ServiceUtils;
import org.apache.log4j.Logger;

import java.io.InputStream;

/**
 * 北京讯安网络设备管理Http协议实现
 *
 * @author bluesky
 */

public class ZdCmsHttpProcess implements IServiceCommondProcess {
    private static final Logger m_log = Logger
            .getLogger(ZdCmsHttpProcess.class);

    @Override
    public int getProcessgetCapabilitie() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public DataAttributes process(InputStream input,
                                  DataAttributes dataAttributes) throws Ex {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataAttributes process(InputStream input) throws Ex {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataAttributes process(String fileName, DataAttributes dataAttributes)
            throws Ex {
        String deviceip = dataAttributes.getProperty("deviceip");
        String command = dataAttributes.getValue("command");
        String ip = dataAttributes.getValue("ip");
        String policeno = dataAttributes.getValue("policeno");
        String result = null;
        /**
         * allvpn
         */
        m_log.info("zdcms call command:" + command);
        m_log.info("zdcms call comand deviceip:" + deviceip);
        String code = "500";
        if (command.equalsIgnoreCase("allvpn")) {
            IPlatManager.terminalinfServcie.setHost(deviceip);
            String beginno = dataAttributes
                    .getProperty(ServiceUtils.Str_Monitor_BeginNo); // beginno
            String endno = dataAttributes
                    .getProperty(ServiceUtils.Str_Monitor_EndNo); // endno
            String pagesize = dataAttributes
                    .getProperty(ServiceUtils.Str_Monitor_PageSize); // pagesize

            if (pagesize == null || pagesize.equalsIgnoreCase(""))
                pagesize = "200";
            m_log.info("zdcms call pagesize:" + pagesize);

            String data = IPlatManager.terminalinfServcie.terminalCache
                    .getAllList(beginno, endno, Integer.parseInt(pagesize));
            // String data =
            // "[{status:'1',ifcancel:false,ip:'171.168.1.1',ifblock:false,cardtype:'TF卡',cardmodel:'型号',cardver:'3.0',policecate:'1',policeno:'654321',policename:'李四',idno:'222222222222222222',org:'静安区公安局',depart:'交警大队',region:'上海市',logindate:'2012-04-12 08:00:21',onlinetime:'17:33:22',createdate:'2012-04-12 08:00:21'},{status:'1',ifcancel:false,ip:'171.168.1.1',ifblock:false,cardtype:'ukey',cardmodel:'a型号',cardver:'3.0',policecate:'1',policeno:'123654',policename:'sxl',idno:'123456789123465798',org:'徐汇区公安局',depart:'刑警大队',region:'重庆市',logindate:'2012-04-19 03:26:22',onlinetime:'22:7:21',createdate:'2012-04-19 03:26:22'},{status:'0',ifcancel:false,ip:'171.168.1.1',ifblock:false,cardtype:'类型',cardmodel:'a型号',cardver:'版本',policecate:'1',policeno:'123654',policename:'王五',idno:'333333333333333333',org:'静安区公安局',depart:'刑警大队',region:'上海市',logindate:'2012-04-12 08:01:38',onlinetime:'17:32:5',createdate:'2012-04-12 08:01:38'},{status:'0',ifcancel:false,ip:'171.168.1.1',ifblock:false,cardtype:'TF卡',cardmodel:'型号',cardver:'版本',policecate:'1',policeno:'12345678',policename:'测试1',idno:'123456789012345678',org:'徐汇区公安局',depart:'刑警大队',region:'上海市',logindate:'2012-04-11 08:43:16',onlinetime:'16:50:27',createdate:'2012-04-11 08:43:16'},{status:'0',ifcancel:false,ip:'171.168.1.1',ifblock:false,cardtype:'TF卡',cardmodel:'a型号',cardver:'版本',policecate:'1',policeno:'123456',policename:'张三',idno:'111111111111111111',org:'徐汇区公安局',depart:'刑警大队',region:'上海市',logindate:'2012-04-12 07:57:10',onlinetime:'17:36:33',createdate:'2012-04-12 07:57:10'},{total:5,beginno:'0',endno:'00',pagesize:500}]";
            m_log.info("allvpn data:" + data);
            dataAttributes.setResultData(data.getBytes());
            dataAttributes.setStatus(Status.S_Success);
            code = "200";
        }
        if (command.equalsIgnoreCase("onlinevpn")) {
            IPlatManager.terminalinfServcie.setHost(deviceip);
            String beginno = dataAttributes
                    .getProperty(ServiceUtils.Str_Monitor_BeginNo); // beginno
            String endno = dataAttributes
                    .getProperty(ServiceUtils.Str_Monitor_EndNo); // endno
            String pagesize = dataAttributes
                    .getProperty(ServiceUtils.Str_Monitor_PageSize); // pagesize
            if (pagesize == null || pagesize.equalsIgnoreCase(""))
                pagesize = "200";

            code = "200";

            String data = IPlatManager.terminalinfServcie.terminalCache
                    .getOnlineList(beginno, endno, Integer.parseInt(pagesize));
            // String data =
            // "[{status:'1',ifcancel:false,ip:'171.168.1.1',ifblock:false,cardtype:'TF卡',cardmodel:'型号',cardver:'3.0',policecate:'1',policeno:'654321',policename:'李四',idno:'2222222222222222222',org:'静安区公安局',depart:'交警大队',region:'上海市',logindate:'2012-04-12 08:00:21',onlinetime:'17:33:22',createdate:'2012-04-12 08:00:21'},{status:'1',ifcancel:false,ip:'171.168.1.1',ifblock:false,cardtype:'ukey',cardmodel:'a型号',cardver:'3.0',policecate:'1',policeno:'123654',policename:'sxl',idno:'123456789123465798',org:'徐汇区公安局',depart:'刑警大队',region:'重庆市',logindate:'2012-04-19 03:26:22',onlinetime:'22:7:21',createdate:'2012-04-19 03:26:22'},{status:'0',ifcancel:false,ip:'171.168.1.1',ifblock:false,cardtype:'类型',cardmodel:'a型号',cardver:'版本',policecate:'1',policeno:'123654',policename:'王五',idno:'333333333333333333',org:'静安区公安局',depart:'刑警大队',region:'上海市',logindate:'2012-04-12 08:01:38',onlinetime:'17:32:5',createdate:'2012-04-12 08:01:38'},{status:'0',ifcancel:false,ip:'171.168.1.1',ifblock:false,cardtype:'TF卡',cardmodel:'型号',cardver:'版本',policecate:'1',policeno:'12345678',policename:'测试1',idno:'123456789012345678',org:'徐汇区公安局',depart:'刑警大队',region:'上海市',logindate:'2012-04-11 08:43:16',onlinetime:'16:50:27',createdate:'2012-04-11 08:43:16'},{status:'0',ifcancel:false,ip:'171.168.1.1',ifblock:false,cardtype:'TF卡',cardmodel:'a型号',cardver:'版本',policecate:'1',policeno:'123456',policename:'张三',idno:'111111111111111111',org:'徐汇区公安局',depart:'刑警大队',region:'上海市',logindate:'2012-04-12 07:57:10',onlinetime:'17:36:33',createdate:'2012-04-12 07:57:10'},{total:5,beginno:'0',endno:'00',pagesize:500}]";
            m_log.info("onlinevpn data:" + data);
            dataAttributes.setResultData(data.getBytes());
            dataAttributes.setStatus(Status.S_Success);
        }
        /**
         * block
         */
        if (command.equalsIgnoreCase("block")) {
            JbpgHttpclient client;
            /*if (IPlatManager.terminalinfServcie.getHost() != null && IPlatManager.terminalinfServcie.getHost().equalsIgnoreCase(deviceip))
                client = new IpSecHttpclient();
            else*/
            client = new IpSecHttpclient();
            client.init(deviceip);
            try {
                String cn = IPlatManager.terminalinfServcie.terminalCache
                        .getCnByUserId(policeno);

                m_log.warn("vpn block 用户证书CN is :" + cn);
                m_log.warn("vpn block 用户证书policeno is :" + policeno);
                if (cn != null)
                    if (client.vpnblock(policeno, cn, ip)) {
                        code = "200";
                        IPlatManager.terminalinfServcie.terminalCache.block(ip, policeno);
                    } else
                        code = "400";
                else {
                    m_log.warn("vpn block 用户证书CN is null.");
                    code = "400";
                }

                dataAttributes.setStatus(Status.S_Success);
                client.close();
            } catch (Exception e) {
                m_log.warn("Vpn http client call vpnblock failed.", e);
            }
        }
        /**
         * noblock
         */
        if (command.equalsIgnoreCase("noblock")) {
            JbpgHttpclient client = new IpSecHttpclient();
            client.init(deviceip);
            try {
                String cn = IPlatManager.terminalinfServcie.terminalCache
                        .getCnByUserId(policeno);
                m_log.warn("vpn noblock 用户证书CN is :" + cn);
                m_log.warn("vpn noblock 用户证书policeno is :" + policeno);
                if (cn != null)
                    if (client.vpnnoblock(policeno, cn, ip)) {
                        code = "200";
                        IPlatManager.terminalinfServcie.terminalCache.noblock(ip, policeno);
                    } else
                        code = "400";
                else {
                    m_log.warn("vpn noblock 用户证书CN is null.");
                    code = "400";
                }

                dataAttributes.setStatus(Status.S_Success);
                client.close();
            } catch (Exception e) {
                m_log.warn("Vpn http client call noblock failed.", e);
            }
        }
        /**
         * vewvpn
         */
        if (command.equalsIgnoreCase("viewvpn")) {
            TSRSHttpclient client = new TSRSHttpclient();
            try {
                client.init(deviceip);
                dataAttributes = client.viewvpn(ip, policeno);
                code = "200";
                if (dataAttributes.isResultData()) {
                    dataAttributes.setProperty(
                            ServiceUtils.Str_ResponseProcessStatus, code);
                    m_log.info("tsrs viewvpn command is okay!");
                } else {
                    m_log.info("tsrs viewvpn command is not okay!");
                }
                client.close();
            } catch (Exception e) {
                m_log.warn("TSRS http client call vpnblock failed.", e);
            }
        }
        /**
         * osquery
         */
        if (command.equalsIgnoreCase("osquery")) {
            TSRSHttpclient client = new TSRSHttpclient();
            try {
                client.init(deviceip);
                dataAttributes = client.osquery(ip, policeno);
                code = "200";

                client.close();
            } catch (Exception e) {
                m_log.warn("TSRS http client call osquery failed.", e);
            }
        }
        /**
         * netquery
         */
        if (command.equalsIgnoreCase("netquery")) {
            TSRSHttpclient client = new TSRSHttpclient();
            try {
                client.init(deviceip);
                dataAttributes = client.netquery(ip, policeno);
                code = "200";
                client.close();
            } catch (Exception e) {
                m_log.warn("TSRS http client call netquery failed.", e);
            }
        }
        /**
         * processquery
         */
        if (command.equalsIgnoreCase("processquery")) {
            TSRSHttpclient client = new TSRSHttpclient();
            try {
                client.init(deviceip);
                dataAttributes = client.processquery(ip, policeno);
                code = "200";
                client.close();
            } catch (Exception e) {
                m_log.warn("TSRS http client call processquery failed.", e);
            }
        }
        dataAttributes
                .setProperty(ServiceUtils.Str_ResponseProcessStatus, code);
        return dataAttributes;
    }

    @Override
    public DataAttributes process(String fileName) throws Ex {
        // TODO Auto-generated method stub
        return null;
    }

}
