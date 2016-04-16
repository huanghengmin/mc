package com.inetec.ichange.mc.service.monitor.snmp;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.event.ResponseListener;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;

/**
 * Created by IntelliJ IDEA.
 * User: bluesky
 * Date: 2010-9-19
 * Time: 16:27:45
 * To change this template use File | Settings | File Templates.
 */
public class ComSwitchSnmpProcess {


    public void testRequest() throws Exception {
        Address targetAddress = GenericAddress.parse("udp:192.168.111.2/161");
        TransportMapping transport = new DefaultUdpTransportMapping();
        Snmp snmp = new Snmp(transport);

        CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString("public"));
        target.setAddress(targetAddress);
        target.setRetries(2);
        target.setTimeout(1500);
        target.setVersion(SnmpConstants.version2c);
        // creating PDU
        PDU pdu = new PDU();
        //cpu
        pdu.add(new VariableBinding(new OID(new int[]{1, 3, 6, 1, 4, 1, 3000, 2, 2})));
        //memtotal
        pdu.add(new VariableBinding(new OID(new int[]{1, 3, 6, 1, 4, 1, 3000, 2, 3})));
        //memfree
        pdu.add(new VariableBinding(new OID(new int[]{1, 3, 6, 1, 4, 1, 3000, 2, 4})));
        //curconnect
        pdu.add(new VariableBinding(new OID(new int[]{1, 3, 6, 1, 4, 1, 3000, 2, 5})));
         //disk total
        pdu.add(new VariableBinding(new OID(new int[]{1, 3, 6, 1, 4, 1, 3000, 2, 9})));

         //disk free
        pdu.add(new VariableBinding(new OID(new int[]{1, 3, 6, 1, 4, 1, 3000, 2, 10})));


        pdu.setType(PDU.GETNEXT);
        // sending request
        ResponseListener listener = new ResponseListener() {
            public void onResponse(ResponseEvent event) {
//                // Always cancel async request when response has been received
//                // otherwise a memory leak is created! Not canceling a request
//                // immediately can be useful when sending a request to a broadcast
//                // address.
               ((Snmp) event.getSource()).cancel(event.getRequest(), this);
                System.out.println("Received response PDU is: " + event.getResponse());

            }
        };


        snmp.send(pdu, target, null, listener);
        snmp.listen();



        Thread.sleep(60 * 1000);

    }

    public static void main(String arg[]) throws Exception {
        ComSwitchSnmpProcess process = new ComSwitchSnmpProcess();
        process.testRequest();
    }
}
