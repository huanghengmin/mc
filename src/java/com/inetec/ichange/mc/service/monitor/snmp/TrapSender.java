package com.inetec.ichange.mc.service.monitor.snmp;

/**
 * Copyright 2010 TechDive.in
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * <a href="http://www.apache.org/licenses/LICENSE-2.0" title="http://www.apache.org/licenses/LICENSE-2.0">http://www.apache.org/licenses/LICENSE-2.0</a>
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

/**
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR AND CONTRIBUTORS ``AS IS''
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE AUTHOR OR CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

import java.util.Date;

import org.snmp4j.*;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.event.ResponseListener;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.MessageProcessingModel;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.SecurityLevel;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;

public class TrapSender {
    public static final String community = "public";

    //  Sending Trap for sysLocation of RFC1213
    public static final String trapOid = ".1.3.6.1.4.1.3000.2.2";

    public static final String ipAddress = "192.168.111.2";

    public static final int port = 161;

    public TrapSender() {
    }

    public static void main(String[] args) {
        TrapSender snmp4JTrap = new TrapSender();

        /* Sending V1 Trap */
        //snmp4JTrap.sendSnmpV1Trap();

        /* Sending V2 Trap */
        snmp4JTrap.sendSnmpV2Trap();

        // snmp4JTrap.sendSnmpV3Trap();


    }

    /**
     * This methods sends the V1 trap to the Localhost in port 163
     */
    public void sendSnmpV1Trap() {
        try {
            //Create Transport Mapping
            TransportMapping transport = new DefaultUdpTransportMapping();
            transport.listen();

            //Create Target
            CommunityTarget comtarget = new CommunityTarget();
            comtarget.setCommunity(new OctetString(community));
            comtarget.setVersion(SnmpConstants.version1);
            comtarget.setAddress(new UdpAddress(ipAddress + "/" + port));
            comtarget.setRetries(2);
            comtarget.setTimeout(5000);

            //Create PDU for V1
            PDUv1 pdu = new PDUv1();
            pdu.setType(PDU.V1TRAP);
            pdu.setEnterprise(new OID(trapOid));
            pdu.setGenericTrap(PDUv1.ENTERPRISE_SPECIFIC);
            pdu.setSpecificTrap(1);
            pdu.setAgentAddress(new IpAddress(ipAddress));

            //Send the PDU
            Snmp snmp = new Snmp(transport);
            System.out.println("Sending V1 Trap to " + ipAddress + " on Port " + port);
            snmp.send(pdu, comtarget);
            ResponseListener listener = new ResponseListener() {
                public void onResponse(ResponseEvent event) {
                    // Always cancel async request when response has been received
                    // otherwise a memory leak is created! Not canceling a request
                    // immediately can be useful when sending a request to a broadcast
                    // address.
                    ((Snmp) event.getSource()).cancel(event.getRequest(), this);
                    PDU response = event.getResponse();
                    PDU request = event.getRequest();
                    if (response == null) {
                        System.out.println("Request " + request + " timed out");
                    } else {
                        System.out.println("Received response " + response + " on request " +
                                request);
                    }
                }
            };

            snmp.send(pdu, comtarget,null,listener);
            snmp.listen();
            Thread.sleep(60 * 1000);
        }
        catch (Exception e) {
            System.err.println("Error in Sending V1 Trap to " + ipAddress + " on Port " + port);
            System.err.println("Exception Message = " + e.getMessage());
        }
    }


    /**
     * This methods sends the V2 trap to the Localhost in port 163
     */
    public void sendSnmpV2Trap() {
        try {
            //Create Transport Mapping
            TransportMapping transport = new DefaultUdpTransportMapping();
            transport.listen();

            //Create Target
            CommunityTarget comtarget = new CommunityTarget();
            comtarget.setCommunity(new OctetString(community));
            comtarget.setVersion(SnmpConstants.version2c);
            comtarget.setAddress(new UdpAddress("192.168.111.2/161"));
            comtarget.setRetries(2);
            comtarget.setTimeout(5000);

            //Create PDU for V2
            PDU pdu = new PDU();

            // need to specify the system up time
            pdu.add(new VariableBinding(SnmpConstants.sysUpTime, new OctetString(new Date().toString())));
            pdu.add(new VariableBinding(SnmpConstants.snmpTrapOID, new OID(trapOid)));
            pdu.add(new VariableBinding(SnmpConstants.snmpTrapAddress, new IpAddress(ipAddress)));

            // variable binding for Enterprise Specific objects, Severity (should be defined in MIB file)
            pdu.add(new VariableBinding(new OID(trapOid), new OctetString("")));
            pdu.setType(PDU.NOTIFICATION);

            //Send the PDU
            Snmp snmp = new Snmp();
            //snmp.listen();

            snmp.send(pdu, comtarget);
               Thread.sleep(60 * 1000);



           // Thread.sleep(60 * 1000);
        }
        catch (Exception e) {
            System.err.println("Error in Sending V2 Trap to " + ipAddress + " on Port " + port);
            System.err.println("Exception Message = " + e.getMessage());
        }
    }

    /**
     * This methods sends the V3 trap to the Localhost in port 163
     */
    public void sendSnmpV3Trap() {
        try {
            //Create Transport Mapping
            TransportMapping transport = new DefaultUdpTransportMapping();
            transport.listen();

            //Create Target
            CommunityTarget comtarget = new CommunityTarget();
            comtarget.setCommunity(new OctetString(community));
            comtarget.setVersion(SnmpConstants.version3);
            comtarget.setAddress(new UdpAddress(ipAddress + "/" + port));
            comtarget.setRetries(2);
            comtarget.setTimeout(5000);

            UserTarget target = new UserTarget();
            target.setAddress(new UdpAddress(ipAddress + "/" + port));
            target.setRetries(1);
// set timeout to 500 milliseconds -> 2*500ms = 1s total timeout
            target.setTimeout(500);
            target.setVersion(SnmpConstants.version3);
            target.setSecurityLevel(SecurityLevel.AUTH_PRIV);
            target.setSecurityName(new OctetString("AuthSHA"));


            //Create PDU for V3

            ScopedPDU pdu = new ScopedPDU();

            pdu.setType(PDU.INFORM);
            pdu.add(new VariableBinding(SnmpConstants.sysUpTime, new TimeTicks(10)));
            pdu.add(new VariableBinding(SnmpConstants.snmpTrapOID, SnmpConstants.linkDown));


            // variable binding for Enterprise Specific objects, Severity (should be defined in MIB file)
            pdu.add(new VariableBinding(new OID(trapOid), new OctetString("Major")));
            pdu.setType(PDU.NOTIFICATION);

            //Send the PDU
            Snmp snmp = new Snmp(transport);
            System.out.println("Sending V3 Trap to " + ipAddress + " on Port " + port);
            ResponseListener listener = new ResponseListener() {
                public void onResponse(ResponseEvent event) {
                    // Always cancel async request when response has been received
                    // otherwise a memory leak is created! Not canceling a request
                    // immediately can be useful when sending a request to a broadcast
                    // address.
                    ((Snmp) event.getSource()).cancel(event.getRequest(), this);
                    PDU response = event.getResponse();
                    PDU request = event.getRequest();
                    if (response == null) {
                        System.out.println("Request " + request + " timed out");
                    } else {
                        System.out.println("Received response " + response + " on request " +
                                request);
                    }
                }
            };

            snmp.send(pdu, target);
            snmp.listen();
            Thread.sleep(60 * 1000);
        }
        catch (Exception e) {
            System.err.println("Error in Sending V3 Trap to " + ipAddress + " on Port " + port);
            System.err.println("Exception Message = " + e.getMessage());
        }
    }
}
